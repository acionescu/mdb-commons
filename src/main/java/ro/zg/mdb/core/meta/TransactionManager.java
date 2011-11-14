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
	String objectName = objectContext.getObjectName();
	String rowId = objectContext.getRowId();
	if (!transactionContext.isRowPending(objectName, rowId)) {
	    PersistentObjectDataManager<T> odm = schemaContext.getObjectDataManager(objectContext.getObjectDataModel()
		    .getType(), objectContext.getObjectName());
	    odm.save(objectContext, this);

	}
    }

    public long getNextValForSequence(String seqId) throws MdbException {
	return schemaContext.getNextValForSequence(seqId);
    }

    public void saveObjectsLink(ObjectsLink link, String linkName) throws MdbException {
	schemaContext.getMetadataManager().saveLink(linkName, link);
    }

    public <T> T buildObject(String objectName,ObjectDataModel<T> odm, String data, Filter filter, String rowId) throws MdbException {
//	T object = odm.getObjectFromString(data, filter);
//	populateLinkFields(object, odm, filter, rowId);
//	return object;
	
	/* get the data for the main object */
	Map<String,Object> valuesMap=odm.getDataMapFromString(data, null,"");
	
	transactionContext.addPendingRow(objectName, rowId);
	/* get the data for the single nested objects */
	getNestedObjectsData(odm, filter, rowId, valuesMap, "");
	/* check if the filter is satisfied */
	if(!filter.isSatisfied(valuesMap)) {
	    transactionContext.removePendingRow(objectName, rowId);
	    return null;
	}
	
	T object = odm.getObjectFromValuesMap(valuesMap, filter.getTargetFields(), "",transactionContext,rowId);
	populateMultivaluedLinkFields(object, odm, filter, rowId, "");
	
	return object;
    }

    private <T> void getNestedObjectsData(ObjectDataModel<T> odm, Filter filter, String rowId, Map<String,Object> valuesMap, String path) throws MdbException {
	Map<String, FieldDataModel<?>> nestedFields=odm.getNestedFieldsForFilter(filter, path);
	/* iterate over the nested fields and get data if a link exists */
	for(FieldDataModel<?> fdm : nestedFields.values()) {
	    LinkModel lm = fdm.getLinkModel();
	    /* check for a link */
	    Collection<ObjectsLink> links=getObjectLinks(lm, rowId, false);
	    
	    if(links.size() == 1) {
		String fullFieldName=path+fdm.getName();
		/* if link exists get the nested object row id */
		String nestedRowId = new ArrayList<ObjectsLink>(links).get(0).getRowId(!lm.isFirst());
		transactionContext.addPendingField(fullFieldName, nestedRowId);
		/* if this row is already pending skip processing */
		if(transactionContext.isRowPending(odm.getTypeName(), nestedRowId)) {
		    continue;
		}
		transactionContext.addPendingRow(odm.getTypeName(), nestedRowId);
		
		ObjectDataModel<?> fieldDataModel=(ObjectDataModel<?>)fdm.getDataModel();
		/* get the object data for the row id */
		String nestedData=getDataForRowId(fieldDataModel, nestedRowId);
		
		
		String newPath=fullFieldName+Constants.NESTED_FIELD_SEPARATOR;
		/* add object values to the values map */
		fieldDataModel.getDataMapFromString(nestedData, valuesMap,newPath);
		/* do the same for any nested fields that this object might have */
		getNestedObjectsData(fieldDataModel, filter, nestedRowId, valuesMap, newPath);
		
	    }
	    else if(links.size() > 1) {
		throw new MdbException(MdbErrorType.ONE_TO_ONE_VIOLATED, new GenericNameValue("links",links));
	    }
	}
    }
    
    
    private String getDataForRowId(ObjectDataModel<?> odm, String rowId) throws MdbException {
	return getObjectDataManager(odm.getTypeName(), odm.getType()).readData(rowId);
    }
    
    public Collection<ObjectsLink> getObjectLinks(LinkModel linkModel, String rowId, boolean reversed)
	    throws MdbException {
	return schemaContext.getMetadataManager().getObjectLinks(linkModel, rowId, reversed);
    }
    
    private <T> void populateMultivaluedLinkFields( T target, ObjectDataModel<T> odm, Filter filter, String rowId,String path)
	    throws MdbException {
//	SchemaMetadataManager smm = schemaContext.getMetadataManager();

	Map<String,FieldDataModel<?>> nestedFields = odm.getMultivaluedNestedFieldsForFilter(filter, path);
	
	/* iterate over link fields and check to see if any links exists */
	for (FieldDataModel<?> fdm : nestedFields.values()) {
	    String fieldName = fdm.getName();
	    LinkModel lm = fdm.getLinkModel();
	    /* skip the field if it is lazy and not specifically mentioned for retrieval */
	    if (lm.isLazy() && !filter.isTargetFieldPresent(fieldName)) {
		continue;
	    }
	    Collection<ObjectsLink> links = getObjectLinks(lm, rowId,false);
	    Collection<String> linkRowsIds = ObjectsLinkUtil.getRows(links, !lm.isFirst());

//	    Collection<?> linkValues = schemaContext.getObjectDataManager(fdm.getType()).readObjects(linkRowsIds,
//		    new Filter(), this, new ArrayList());
//
//	    odm.populateComplexFields(target, fieldName, linkValues);
	    
	    Collection<Object> values=new HashSet<Object>();
	    
	    ObjectDataModel<?> fieldDataModel=(ObjectDataModel<?>)fdm.getDataModel();
	    for(String nesteRowId : linkRowsIds) {
		String rowData = getDataForRowId(fieldDataModel, nesteRowId);
		values.add(buildObject(fieldDataModel.getTypeName(), fieldDataModel, rowData, filter, nesteRowId));
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

    public <T> ObjectDataModel<T> getObjectDataModel(Class<T> type) {
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
	return odm.deleteAll(filter);
    }

    public <T> long deleteAllBut(String objectName, Class<T> type, final Filter filter,
	    final Collection<String> restricted) throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.deleteAllBut(filter, restricted);
    }

    public <T> long deleteObjects(String objectName, Class<T> type, Collection<String> ids, final Filter filter)
	    throws MdbException {
	PersistentObjectDataManager<T> odm = getObjectDataManager(objectName, type);
	return odm.deleteObjects(ids, filter);
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
}
