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
package ro.zg.mdb.core.meta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.filter.Filter;
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
	this.transactionContext=new TransactionContext();
    }

    public <T> void save(ObjectContext<T> objectContext) throws MdbException {
	String objectName = objectContext.getObjectName();
	String rowId = objectContext.getRowId();
	if (!transactionContext.isRowPending(objectName, rowId)) {
	    PersistentObjectDataManager<T> odm = schemaContext.getObjectDataManager(objectContext
		    .getObjectDataModel().getType(), objectContext.getObjectName());
	    odm.save(objectContext, this);

	}
    }
    
    public long getNextValForSequence(String seqId) throws MdbException{
	return schemaContext.getNextValForSequence(seqId);
    }
    
    public void saveObjectsLink(ObjectsLink link, String linkName) throws MdbException {
	schemaContext.getMetadataManager().saveLink(linkName, link);
    }
    
    public <T> T buildObject(ObjectDataModel<T> odm, String data, Filter filter, String rowId) throws MdbException {
	T object = odm.getObjectFromString(data, filter);
	populateLinkFields(object, odm, filter, rowId);
	return object;
    }
    
    public <T> void populateLinkFields(T target, ObjectDataModel<T> odm, Filter filter, String rowId) throws MdbException {
	SchemaMetadataManager smm = schemaContext.getMetadataManager();
	
	/* iterate over link fields and check to see if any links exists */
	for(FieldDataModel<?> fdm : odm.getLinkedFields()) {
	    String fieldName = fdm.getName();
	    LinkModel lm = fdm.getLinkModel();
	    /* skip the field if it is lazy and not specifically mentioned for retrieval */
	    if(lm.isLazy() && !filter.isTargetFieldPresent(fieldName)) {
		continue;
	    }
	    Collection<ObjectsLink> links = smm.getObjectLinks(lm, rowId);
	    Collection<String> linkRowsIds = ObjectsLinkUtil.getRows(links, !lm.isFirst());
	    
	    Collection<?> linkValues = schemaContext.getObjectDataManager(fdm.getType()).readObjects(linkRowsIds,new Filter(), this,new ArrayList());
	    
	    odm.populateComplexFields(target, fieldName, linkValues);
	}
    }
    
    public <T> ObjectContext<T> getObjectContext(String objectName, T object, boolean create) throws MdbException{
	PersistentObjectDataManager<T> odm =schemaContext.getObjectDataManager((Class<T>)object.getClass(), objectName);
	if(odm==null) {
	    throw new MdbException(MdbErrorType.UNKNOWN_OBJECT_TYPE, new GenericNameValue("name",objectName), new GenericNameValue("type",object.getClass()));
	}
	return odm.getObjectContext(object, create, this);
    }
    
    public <T> ObjectDataModel<T> getObjectDataModel(Class<T> type){
	return schemaContext.getObjectDataModel(type);
    }
    
    private <T> PersistentObjectDataManager<T> getObjectDataManager(String objectName, Class<T> type) throws MdbException{
	PersistentObjectDataManager<T> odm =schemaContext.getObjectDataManager(type, objectName);
	if(odm==null) {
	    throw new MdbException(MdbErrorType.UNKNOWN_OBJECT_TYPE, new GenericNameValue("name",objectName), new GenericNameValue("type",type));
	}
	return odm;
    }
       
    
    public <T> Collection<String> getRowsForRange(String objectName, Class<T> type, FieldConstraintContext<?> constraintContext, Collection<String> rows) throws MdbException {
	PersistentObjectDataManager<T> odm=getObjectDataManager(objectName, type);
	return odm.getRowsForRange(constraintContext, rows);
    }
    
    public <T> Collection<String> getRowsForValues(String objectName, Class<T> type,FieldConstraintContext<?> fcc, Set<String> data) throws MdbException {
	PersistentObjectDataManager<T> odm=getObjectDataManager(objectName, type);
	return odm.getRowsForValues(fcc, data);
    }
}
