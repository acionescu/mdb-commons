/*******************************************************************************
 * Copyright 2011 Adrian Cristian Ionescu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ro.zg.mdb.core.meta.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.commands.builders.FindResultBuilder;
import ro.zg.mdb.constants.Constants;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.ConstraintType;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.filter.ObjectConstraintContext;
import ro.zg.mdb.core.meta.persistence.data.LinkMetadata;
import ro.zg.mdb.core.meta.persistence.data.LinkValue;
import ro.zg.mdb.core.meta.persistence.data.ObjectsLink;
import ro.zg.mdb.core.meta.persistence.data.PersistentFieldMetadata;
import ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadata;
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
	schemaContext.getLinksManager().saveLink(linkValue.getLinkName(), linkValue.getLink());
    }

    public long deleteLinkValue(LinkValue linkValue) throws MdbException {
	return schemaContext.getLinksManager().deleteLink(linkValue.getLinkName(), linkValue.getLink());
    }

    public long deleteLinks(LinkMetadata lm, String rowId) throws MdbException {
	return schemaContext.getLinksManager().deleteLinks(lm.getName(), rowId, lm.isFirst());
    }

    public <T> Map<String, Object> getAndCheckValuesMap(String objectName, PersistentObjectMetadata<T> odm, String data,
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

    public <T> Map<String, Object> getValuesMap(String objectName, PersistentObjectMetadata<T> odm, String data, Filter filter,
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

    public <T> T buildObject(String objectName, PersistentObjectMetadata<T> odm, String data, Filter filter, String rowId)
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

    private <T> void getNestedObjectsData(PersistentObjectMetadata<T> odm, Filter filter, String rowId,
	    Map<String, Object> valuesMap, String path) throws MdbException {
	Set<PersistentFieldMetadata<?>> nestedFields = odm.getNestedFieldsForFilter(filter, path);
	/* iterate over the nested fields and get data if a link exists */
	for (PersistentFieldMetadata<?> fdm : nestedFields) {
	    LinkMetadata lm = fdm.getLinkMetadata();
	    /* check for a link */
	    LinksSet linksSet = getObjectLinks(lm, rowId, false);
	    Collection<ObjectsLink> links = null;
	    Class<?> linkNestedType = null;
	    if (lm.isPolymorphic()) {
		PolymorphicLinksSet pls = (PolymorphicLinksSet) linksSet;
		for (Class<?> type : pls.getTypes()) {
		    links = pls.getLinks(type);
		    linkNestedType = type;
		}
	    } else {
		SimpleLinksSet sls = (SimpleLinksSet) linksSet;
		links = sls.getLinks();
	    }
	    if (links == null) {
		return;
	    }

	    if (links.size() == 1) {
		String fullFieldName = path + fdm.getName();
		/* if link exists get the nested object row id */
		ObjectsLink ol = new ArrayList<ObjectsLink>(links).get(0);
		String nestedRowId = ol.getRowId(!lm.isFirst());
		transactionContext.addPendingField(fullFieldName, nestedRowId);
		LinkValue linkValue = new LinkValue(lm.getFullName(linkNestedType), linkNestedType, ol);

		transactionContext.addPendingLink(fullFieldName, linkValue);
		/* if this row is already pending skip processing */
		if (transactionContext.isRowPending(odm.getTypeName(), nestedRowId)) {
		    continue;
		}
		transactionContext.addPendingRow(odm.getTypeName(), nestedRowId);

		PersistentObjectMetadata<?> fodm = (PersistentObjectMetadata<?>) fdm.getValueMetadata();
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

    public String getDataForRowId(PersistentObjectMetadata<?> odm, String rowId) throws MdbException {
	return getObjectDataManager(odm.getTypeName(), odm.getType()).readData(rowId);
    }

    public LinksSet getObjectLinks(LinkMetadata linkModel, String rowId, boolean reversed) throws MdbException {
	return schemaContext.getLinksManager().getObjectLinks(linkModel, rowId, reversed);
    }
    
    public Collection<String> getLinksIds(LinkMetadata linkModel, String rowId, boolean reversed) throws MdbException{
	return schemaContext.getLinksManager().getLinksIds(linkModel, rowId, reversed);
    }
    
    public Collection<LinkValue> getLinkValues(LinkMetadata linkModel, String rowId, boolean reversed) throws MdbException{
	return schemaContext.getLinksManager().getLinksValues(linkModel, rowId, reversed);
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
    public <T> void checkForDirectReferences(PersistentObjectMetadata<T> odm, String rowId) throws MdbException {
	LinksManager smm = schemaContext.getLinksManager();
	for (LinkMetadata lm : odm.getReferences()) {
	    Collection<ObjectsLink> links = smm.getLinks(lm.getFullName(odm.getType()), rowId, !lm.isFirst())
		    .getLinks();
	    if (links.size() > 0) {
		throw new MdbException(MdbErrorType.DIRECT_REFERENCE_VIOLATED, new GenericNameValue("link",
			new LinkValue(lm.getFullName(odm.getType()), new ArrayList<ObjectsLink>(links).get(0))));
	    }
	}
    }

    private <T> void populateMultivaluedLinkFields(T target, PersistentObjectMetadata<T> odm, Filter filter, String rowId,
	    String path) throws MdbException {
	// LinksManager smm = schemaContext.getMetadataManager();

	Map<String, PersistentFieldMetadata<?>> nestedFields = odm.getMultivaluedNestedFieldsForFilter(filter, path);

	/* iterate over link fields and check to see if any links exists */
	for (PersistentFieldMetadata<?> fdm : nestedFields.values()) {
	    String fieldName = fdm.getName();
	    LinkMetadata lm = fdm.getLinkMetadata();
	    boolean isNestedFirst = !lm.isFirst();
	    LinksSet linksSet = getObjectLinks(lm, rowId, false);
	    Collection<Object> values = new LinkedHashSet<Object>();

	    if (lm.isPolymorphic()) {
		PolymorphicLinksSet pls = (PolymorphicLinksSet) linksSet;
		for (Class<?> key : pls.getTypes()) {
		    /* use the type of the object */
		    values.addAll(getValuesFromLinks(key,
			    pls.getLinks(key), isNestedFirst));
		}
	    } else {
		SimpleLinksSet sls = (SimpleLinksSet) linksSet;
		/* use the type of the field */
		values = getValuesFromLinks(fdm.getValueMetadata().getType(), sls.getLinks(),
			isNestedFirst);
	    }
	    if (values.size() > 0) {
//		System.out.println(target.getClass()+", "+fieldName+", "+values);
		odm.populateComplexFields(target, fieldName, values);
	    }
	}
    }

    private Collection<Object> getValuesFromLinks(Class<?> nestedObjectType,
	    Collection<ObjectsLink> links, boolean isNestedFirst) throws MdbException {
	Collection<String> linkRowsIds = ObjectsLinkUtil.getRows(links, isNestedFirst);

	Collection<Object> values = new LinkedHashSet<Object>();

	// MultivaluedDataModel<?, ?> multivaluedDataModel = (MultivaluedDataModel<?, ?>) fdm.getDataModel();
	PersistentObjectMetadata<?> nodm = getObjectDataModel(nestedObjectType);
	for (String nesteRowId : linkRowsIds) {
	    Object nestedObject = transactionContext.getPendingObject(nesteRowId);
	    if (nestedObject == null) {
		String rowData = getDataForRowId(nodm, nesteRowId);
		nestedObject = buildObject(nodm.getTypeName(), nodm, rowData, new Filter(), nesteRowId);
		/* 
		 * we need to clear pending fields after each item was created in order not to
		 * copy certain values of one item onto others
		 */
		transactionContext.clearPendingFields();
	    }

	    values.add(nestedObject);
	}
	return values;
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
	PersistentObjectMetadata<T> odm = getObjectDataModel(type);

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

	for (PersistentFieldMetadata<?> fdm : odm.getFields().values()) {
	    String fieldName = fdm.getName();
	    String fullFieldName = path + fieldName;
	    LinkMetadata lm = fdm.getLinkMetadata();
	    
	    /* skip linked multivalued fields */
	    if (lm != null && fdm.getValueMetadata().isMultivalued()) {
		continue;
	    }

	    if (targetFieldsExist && !targetFields.contains(fullFieldName)) {
		continue;
	    }

	    Object fieldValue = null;
	    
	    if (lm != null) {
		if (!targetFieldsExist && lm.isLazy()) {
		    continue;
		}

		String nestedRowId = transactionContext.getRowIdForPendingField(fullFieldName);
		if(nestedRowId == null) {
		    /* this may happen for collections of complex objects */
		    continue;
		}
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

    public <T> PersistentObjectMetadata<T> getObjectDataModel(Class<T> type) throws MdbException {
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

    public <T> void readAllObjects(String objectName, Class<T> type, final Filter filter,
	    final FindResultBuilder<T, ?> resultBuilder) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	odm.readAllObjects(filter, this, resultBuilder);
    }

    public <T> void readAllObjectsBut(String objectName, Class<T> type, final Filter filter,
	    final FindResultBuilder<T, ?> resultBuilder, final Collection<String> restricted) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	odm.readAllObjectsBut(filter, this, resultBuilder, restricted);
    }

    public <T> void readObjects(String objectName, Class<T> type, Collection<String> ids, final Filter filter,
	    final FindResultBuilder<T, ?> resultBuilder) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	odm.readObjects(ids, filter, this, resultBuilder);

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

    public LinkValue getPendingLinkForField(String fullFieldName) {
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

    public <T> ObjectContext<T> addPendingObjectForWrite(T o, PersistentObjectMetadata<T> odm) {
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
