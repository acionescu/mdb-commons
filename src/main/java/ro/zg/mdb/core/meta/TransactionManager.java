/*******************************************************************************
 * Copyright (c) 2011 Adrian Cristian Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Adrian Cristian Ionescu - initial API and implementation
 ******************************************************************************/
package ro.zg.mdb.core.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.constants.Constants;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.ConstraintType;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.filter.ObjectConstraintContext;
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.LinkModel;
import ro.zg.mdb.core.meta.data.LinkValue;
import ro.zg.mdb.core.meta.data.MultivaluedDataModel;
import ro.zg.mdb.core.meta.data.ObjectDataModel;
import ro.zg.mdb.core.meta.data.ObjectsLink;
import ro.zg.mdb.core.schema.ObjectContext;
import ro.zg.mdb.util.ObjectsLinkUtil;
import ro.zg.util.data.GenericNameValue;

public class TransactionManager {
    private SchemaContext schemaContext;
    private TransactionContext transactionContext;

    public TransactionManager(SchemaContext schemaContext) {
	super();
	this.schemaContext = schemaContext;
	this.transactionContext = new TransactionContext();
    }

    public <T> void save(ObjectContext<T> objectContext) throws MdbException {
	// String objectName = objectContext.getObjectName();
	// String rowId = objectContext.getRowId();
	// if (!transactionContext.isRowPending(objectName, rowId)) {
	PersistentObjectDataManager<T> odm = schemaContext.getObjectDataManager(objectContext.getObjectDataModel()
		.getType(), objectContext.getObjectName());
	odm.save(objectContext, this);

	// }
    }

    public <T> boolean update(ObjectContext<T> objectContext) throws MdbException {
	PersistentObjectDataManager<T> odm = schemaContext.getObjectDataManager(objectContext.getObjectDataModel()
		.getType(), objectContext.getObjectName());
	return odm.update(objectContext, this);
    }

    public long getNextValForSequence(String seqId) throws MdbException {
	return schemaContext.getNextValForSequence(seqId);
    }

    public void saveLinkValue(LinkValue linkValue) throws MdbException {
	schemaContext.getMetadataManager().saveLink(linkValue.getLinkName(), linkValue.getLink());
    }

    public long deleteLinkValue(LinkValue linkValue) throws MdbException {
	return schemaContext.getMetadataManager().deleteLink(linkValue.getLinkName(), linkValue.getLink());
    }

    public long deleteLinks(LinkModel lm, String rowId) throws MdbException {
	return schemaContext.getMetadataManager().deleteLinks(lm.getName(), rowId, lm.isFirst());
    }

    public <T> Map<String, Object> getAndCheckValuesMap(String objectName, ObjectDataModel<T> odm, String data,
	    Filter filter, String rowId) throws MdbException {

	/* get the data for the main object */
	Map<String, Object> valuesMap = odm.getDataMapFromString(data, null, "");
	if (valuesMap == null) {
	    return null;
	}
	String objectIdFieldName = odm.getObjectIdFieldName();
	if (objectIdFieldName != null) {
	    valuesMap.put(objectIdFieldName, rowId);
	}

	boolean addedPendingRow = false;
	if (!transactionContext.isRowPending(objectName, rowId)) {
	    transactionContext.addPendingRow(objectName, rowId);
	    addedPendingRow = true;
	}

	/* get the data for the single nested objects */
	getNestedObjectsData(odm, filter, rowId, valuesMap, "");

	if (addedPendingRow) {
	    transactionContext.removePendingRow(objectName, rowId);
	}

	/* check if the filter is satisfied */
	if (!filter.isSatisfied(valuesMap)) {

	    return null;
	}
	return valuesMap;
    }

    public <T> Map<String, Object> getValuesMap(String objectName, ObjectDataModel<T> odm, String data, Filter filter,
	    String rowId) throws MdbException {

	/* get the data for the main object */
	Map<String, Object> valuesMap = odm.getDataMapFromString(data, null, "");
	if (valuesMap == null) {
	    return null;
	}
	String objectIdFieldName = odm.getObjectIdFieldName();
	if (objectIdFieldName != null) {
	    valuesMap.put(objectIdFieldName, rowId);
	}

	boolean addedPendingRow = false;
	if (!transactionContext.isRowPending(objectName, rowId)) {
	    transactionContext.addPendingRow(objectName, rowId);
	    addedPendingRow = true;
	}

	/* get the data for the single nested objects */
	getNestedObjectsData(odm, filter, rowId, valuesMap, "");

	if (addedPendingRow) {
	    transactionContext.removePendingRow(objectName, rowId);
	}

	return valuesMap;
    }

    public <T> T buildObject(String objectName, ObjectDataModel<T> odm, String data, Filter filter, String rowId)
	    throws MdbException {

	transactionContext.addPendingRow(objectName, rowId);
	Map<String, Object> valuesMap = getAndCheckValuesMap(objectName, odm, data, filter, rowId);
	if (valuesMap == null) {
	    transactionContext.removePendingRow(objectName, rowId);
	    return null;
	}

	T object = getObjectFromValuesMap(odm.getType(), valuesMap, filter.getTargetFields(), "", rowId);
	populateMultivaluedLinkFields(object, odm, filter, rowId, "");

	transactionContext.removePendingRow(objectName, rowId);

	return object;
    }

    private <T> void getNestedObjectsData(ObjectDataModel<T> odm, Filter filter, String rowId,
	    Map<String, Object> valuesMap, String path) throws MdbException {
	Set<FieldDataModel<?>> nestedFields = odm.getNestedFieldsForFilter(filter, path);
	/* iterate over the nested fields and get data if a link exists */
	for (FieldDataModel<?> fdm : nestedFields) {
	    LinkModel lm = fdm.getLinkModel();
	    /* check for a link */
	    Collection<ObjectsLink> links = getObjectLinks(lm, rowId, false);

	    if (links.size() == 1) {
		String fullFieldName = path + fdm.getName();
		/* if link exists get the nested object row id */
		ObjectsLink ol = new ArrayList<ObjectsLink>(links).get(0);
		String nestedRowId = ol.getRowId(!lm.isFirst());
		transactionContext.addPendingField(fullFieldName, nestedRowId);
		transactionContext.addPendingLink(fullFieldName, ol);
		/* if this row is already pending skip processing */
		if (transactionContext.isRowPending(odm.getTypeName(), nestedRowId)) {
		    continue;
		}
		transactionContext.addPendingRow(odm.getTypeName(), nestedRowId);

		ObjectDataModel<?> fodm = (ObjectDataModel<?>) fdm.getDataModel();
		/* get the object data for the row id */
		String nestedData = getDataForRowId(fodm, nestedRowId);

		String newPath = fullFieldName + Constants.NESTED_FIELD_SEPARATOR;
		/* add object values to the values map */
		fodm.getDataMapFromString(nestedData, valuesMap, newPath);

		/* set the object id */
		String objectIdFieldName = fodm.getObjectIdFieldName();
		if (objectIdFieldName != null) {
		    valuesMap.put(newPath + objectIdFieldName, nestedRowId);
		}

		/* do the same for any nested fields that this object might have */
		getNestedObjectsData(fodm, filter, nestedRowId, valuesMap, newPath);

	    } else if (links.size() > 1) {
		throw new MdbException(MdbErrorType.ONE_TO_ONE_VIOLATED, new GenericNameValue("links", links));
	    }
	}
    }

    public String getDataForRowId(ObjectDataModel<?> odm, String rowId) throws MdbException {
	return getObjectDataManager(odm.getTypeName(), odm.getType()).readData(rowId);
    }

    public Collection<ObjectsLink> getObjectLinks(LinkModel linkModel, String rowId, boolean reversed)
	    throws MdbException {
	return schemaContext.getMetadataManager().getObjectLinks(linkModel, rowId, reversed);
    }

    /**
     * Iterates over all the possible references for this object type and checks for direct references to the object
     * identified by the rowId <br/>
     * If any link is found an {@link MdbErrorType#DIRECT_REFERENCE_VIOLATED} error is thrown
     * 
     * @param odm
     * @param rowId
     * @throws MdbException
     */
    public <T> void checkForDirectReferences(ObjectDataModel<T> odm, String rowId) throws MdbException {
	SchemaMetadataManager smm = schemaContext.getMetadataManager();
	for (LinkModel lm : odm.getReferences()) {
	    Collection<ObjectsLink> links = smm.getObjectLinks(lm, rowId, true);
	    if (links.size() > 0) {
		throw new MdbException(MdbErrorType.DIRECT_REFERENCE_VIOLATED, new GenericNameValue("link",
			new LinkValue(lm.getName(), new ArrayList<ObjectsLink>(links).get(0))));
	    }
	}
    }

    private <T> void populateMultivaluedLinkFields(T target, ObjectDataModel<T> odm, Filter filter, String rowId,
	    String path) throws MdbException {
	// SchemaMetadataManager smm = schemaContext.getMetadataManager();

	Map<String, FieldDataModel<?>> nestedFields = odm.getMultivaluedNestedFieldsForFilter(filter, path);

	/* iterate over link fields and check to see if any links exists */
	for (FieldDataModel<?> fdm : nestedFields.values()) {
	    String fieldName = fdm.getName();
	    LinkModel lm = fdm.getLinkModel();
//	    /* skip the field if it is lazy and not specifically mentioned for retrieval */
//	    if (lm.isLazy() && !filter.isTargetFieldPresent(fieldName)) {
//		continue;
//	    }
	    Collection<ObjectsLink> links = getObjectLinks(lm, rowId, false);
	    Collection<String> linkRowsIds = ObjectsLinkUtil.getRows(links, !lm.isFirst());

	    // Collection<?> linkValues = schemaContext.getObjectDataManager(fdm.getType()).readObjects(linkRowsIds,
	    // new Filter(), this, new ArrayList());
	    //
	    // odm.populateComplexFields(target, fieldName, linkValues);

	    Collection<Object> values = new HashSet<Object>();

	    MultivaluedDataModel<?, ?> multivaluedDataModel = (MultivaluedDataModel<?, ?>) fdm.getDataModel();
	    ObjectDataModel<?> nodm = getObjectDataModel(multivaluedDataModel.getType());
	    for (String nesteRowId : linkRowsIds) {
		Object nestedObject = transactionContext.getPendingObject(nesteRowId);
		if (nestedObject == null) {
		    String rowData = getDataForRowId(nodm, nesteRowId);
		    nestedObject = buildObject(nodm.getTypeName(), nodm, rowData, new Filter(), nesteRowId);
		}

		values.add(nestedObject);
	    }

	    odm.populateComplexFields(target, fieldName, values);
	}
    }

    public <T> ObjectContext<T> getObjectContext(String objectName, T object, boolean create) throws MdbException {
	PersistentObjectDataManager<T> odm = schemaContext.getObjectDataManager((Class<T>) object.getClass(),
		objectName);
	if (odm == null) {
	    throw new MdbException(MdbErrorType.UNKNOWN_OBJECT_TYPE, new GenericNameValue("name", objectName),
		    new GenericNameValue("type", object.getClass()));
	}
	return odm.getObjectContext(object, create, this);
    }

    public <T> ObjectContext<T> getObjectContext(String objectName, T target, Filter filter, String rowId,
	    Map<String, Object> oldValuesMap, String path) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, (Class<T>) target.getClass());
	return odm.getObjectContext(this, target, filter, rowId, oldValuesMap, path);

    }

    public <T> ObjectContext<T> getObjectContext(String objectName, T target, Filter filter, String rawOldData,
	    String rowId, String path) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, (Class<T>) target.getClass());
	return odm.getObjectContext(this, target, filter, rawOldData, rowId, path);
    }

    public <T> List<ObjectContext<T>> getObjectContextsList(String objectName, Collection<T> values, boolean create)
	    throws MdbException {
	List<ObjectContext<T>> objectContexts = new ArrayList<ObjectContext<T>>();

	for (T target : values) {
	    objectContexts.add(getObjectContext(objectName, target, create));
	}

	return objectContexts;
    }

    public <T> T getObjectFromValuesMap(Class<T> type, Map<String, Object> valuesMap, Collection<String> targetFields,
	    String path, String rowId) throws MdbException {
	ObjectDataModel<T> odm = getObjectDataModel(type);

	T restored = null;
	try {
	    restored = odm.getType().newInstance();
	} catch (Exception e) {
	    throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR, new GenericNameValue("type",
		    odm.getType()));
	}

	transactionContext.addPendingObject(rowId, restored);

	boolean targetFieldsExist = targetFields != null && !targetFields.isEmpty();
	/* populate the object with the target values */

	for (FieldDataModel<?> fdm : odm.getFields().values()) {
	    String fieldName = fdm.getName();
	    String fullFieldName = path + fieldName;

	    if (fdm.getDataModel().isMultivalued()) {
		continue;
	    }

	    if (targetFieldsExist && !targetFields.contains(fullFieldName)) {
		continue;
	    }

	    Object fieldValue = null;
	    LinkModel lm = fdm.getLinkModel();
	    if (lm != null) {
		if (!targetFieldsExist && lm.isLazy()) {
		    continue;
		}

		String nestedRowId = transactionContext.getRowIdForPendingField(fullFieldName);
		fieldValue = transactionContext.getPendingObject(nestedRowId);
		if (fieldValue == null) {
		    fieldValue = getObjectFromValuesMap(fdm.getType(), valuesMap, targetFields, fullFieldName
			    + Constants.NESTED_FIELD_SEPARATOR, nestedRowId);
		}
	    } else {
		fieldValue = valuesMap.get(fullFieldName);
	    }

	    if (fieldValue != null) {
		odm.addValueToObject(restored, fdm, fieldValue);
	    }
	}

	odm.setObjectId(restored, rowId);

	return restored;
    }

    public <T> ObjectDataModel<T> getObjectDataModel(Class<T> type) throws MdbException {
	return schemaContext.getObjectDataModel(type);
    }

    private <T> PersistentObjectDataManager<T> getObjectDataManager(String objectName, Class<T> type)
	    throws MdbException {
	PersistentObjectDataManager<T> odm = schemaContext.getObjectDataManager(type, objectName);
	if (odm == null) {
	    throw new MdbException(MdbErrorType.UNKNOWN_OBJECT_TYPE, new GenericNameValue("name", objectName),
		    new GenericNameValue("type", type));
	}
	return odm;
    }

    public <T> Collection<String> getRowsForRange(String objectName, Class<T> type,
	    FieldConstraintContext<?> constraintContext, Collection<String> rows) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.getRowsForRange(constraintContext, rows);
    }

    public <T> Collection<String> getRowsForValues(String objectName, Class<T> type, FieldConstraintContext<?> fcc,
	    Set<String> data) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.getRowsForValues(fcc, data);
    }

    public <T> long deleteAll(String objectName, Class<T> type, final Filter filter) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.deleteAll(filter, this);
    }

    public <T> long deleteAllBut(String objectName, Class<T> type, final Filter filter,
	    final Collection<String> restricted) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.deleteAllBut(filter, restricted, this);
    }

    public <T> long deleteObjects(String objectName, Class<T> type, Collection<String> ids, final Filter filter)
	    throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.deleteObjects(ids, filter, this);
    }

    public <T> Collection<T> readAllObjects(String objectName, Class<T> type, final Filter filter,
	    final Collection<T> dataHolder) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.readAllObjects(filter, this, dataHolder);
    }

    public <T> Collection<T> readAllObjectsBut(String objectName, Class<T> type, final Filter filter,
	    final Collection<T> dataHolder, final Collection<String> restricted) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.readAllObjects(filter, this, dataHolder);
    }

    public <T> Collection<T> readObjects(String objectName, Class<T> type, Collection<String> ids, final Filter filter,
	    final Collection<T> dataHolder) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.readObjects(ids, filter, this, dataHolder);

    }

    public <T> Long updateAll(String objectName, Class<T> type, final T source, final Filter filter)
	    throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.updateAll(source, filter, this);
    }

    public <T> Long updateAllBut(String objectName, Class<T> type, final T source, final Filter filter,
	    final Collection<String> restricted) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.updateAllBut(source, filter, restricted, this);
    }

    public <T> Long updateObjects(String objectName, Class<T> type, Collection<String> ids, T source, Filter filter)
	    throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.updateObjects(ids, source, filter, this);
    }

    public <T> T create(String objectName, Class<T> type, T target) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.create(target, this);
    }

    public boolean processConstraint(ConstraintType constraintType, ObjectConstraintContext<?> occ) throws MdbException {
	return schemaContext.getConstraintProcessor(constraintType).apply(occ);
    }

    public ObjectsLink getPendingLinkForField(String fullFieldName) {
	return transactionContext.getPendingLink(fullFieldName);
    }

    public String getPendingRowIdForField(String fullFieldName) {
	return transactionContext.getRowIdForPendingField(fullFieldName);
    }

    public void addPendingRowId(String objectName, String rowId) {
	transactionContext.addPendingRow(objectName, rowId);
    }

    public boolean isRowPending(String objectName, String rowId) {
	return transactionContext.isRowPending(objectName, rowId);
    }

    public void clearTransactionContext() {
	transactionContext.clear();
    }

    public void clearPendingRows() {
	transactionContext.clearPendingRows();
    }

    public <T> ObjectContext<T> addPendingObjectForWrite(T o, ObjectDataModel<T> odm) {
	ObjectContext<T> pendingContext = new ObjectContext<T>(odm);
	transactionContext.addPendingObjectForWrite(o, pendingContext);
	return pendingContext;
    }

    public <T> ObjectContext<T> getObjectContextForPendingObject(T o) {
	return transactionContext.getObjectContextForPendingObject(o);
    }

    public <T> ObjectContext<T> removePendingObjectForWrite(T o) {
	return transactionContext.removePendingObjectForWrite(o);
    }

}
