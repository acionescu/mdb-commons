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
package ro.zg.mdb.core.schema;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.core.concurrency.ResourceLock;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.LinkValue;
import ro.zg.mdb.core.meta.data.ObjectDataModel;
import ro.zg.mdb.core.meta.data.Row;
import ro.zg.mdb.core.meta.data.UniqueIndexValue;

public class ObjectContext<T> {
    private T target;
    private ResourceLock rowLock;
    private ObjectDataModel<T> objectDataModel;
    private Map<String, String> indexedValues;
    private Map<String,UniqueIndexValue> uniqueValues;
    private Map<String, String> oldIndexedValues;
    private Map<String, UniqueIndexValue> oldUniqueValues;
    private String data;
    private Row rowInfo;
    private boolean alreadyCreated;
    /**
     * The name under which this object will be stored
     * Usually it's the object's class name, but other name can also be specified
     */
    private String objectName;
    
    
    /**
     * used to handle links
     */
    private Map<String,ObjectContext<?>> nestedObjectContexts=new HashMap<String, ObjectContext<?>>();
    private Map<String, List<ObjectContext<?>>> nestedMultivaluedObjectContexts=new HashMap<String, List<ObjectContext<?>>>(); 
    private Set<LinkValue> linksToAdd=new HashSet<LinkValue>();
    private Set<LinkValue> linksToRemove=new HashSet<LinkValue>();
    
    public ObjectContext(ObjectDataModel<T> objectDataModel, String data, Map<String, String> indexedValues,
	    Map<String, UniqueIndexValue> uniqueValues, Map<String, String> oldIndexedValues,
	    Map<String, UniqueIndexValue> oldUniqueValues, String rowId, Map<String, ObjectContext<?>> nestedContexts, Set<LinkValue> linksToRemove, Set<LinkValue> linksToAdd) {
	super();
	this.objectDataModel = objectDataModel;
	this.data = data;
	this.indexedValues = indexedValues;
	this.uniqueValues = uniqueValues;
	this.oldIndexedValues = oldIndexedValues;
	this.oldUniqueValues = oldUniqueValues;
	this.rowInfo=new Row(rowId);
	this.alreadyCreated=true;
	this.objectName=objectDataModel.getTypeName();
	this.nestedObjectContexts=nestedContexts;
	this.linksToRemove=linksToRemove;
	this.linksToAdd=linksToAdd;
    }


    public ObjectContext(T target, ObjectDataModel<T> objectDataModel, String data, Map<String, String> indexedValues,
	    Map<String, UniqueIndexValue> uniqueValues, Map<String, ObjectContext<?>> nestedContexts, Map<String, List<ObjectContext<?>>> nestedMultivaluedObjectContexts) throws MdbException {
	super();
	this.target=target;
	this.objectDataModel = objectDataModel;
	this.data = data;
	this.indexedValues = indexedValues;
	this.uniqueValues = uniqueValues;
	this.objectName=objectDataModel.getTypeName();
	this.nestedObjectContexts=nestedContexts;
	this.nestedMultivaluedObjectContexts=nestedMultivaluedObjectContexts;
	updateRowInfo();
    }


    public ObjectContext(ObjectDataModel<T> objectDataModel, Map<String, String> oldIndexedValues, Map<String, UniqueIndexValue> oldUniqueValues) {
	super();
	this.oldIndexedValues = oldIndexedValues;
	this.oldUniqueValues = oldUniqueValues;
	this.alreadyCreated=true;
	this.objectDataModel=objectDataModel;
	this.objectName=objectDataModel.getTypeName();
    }
    
    public ObjectContext(ObjectDataModel<T> objectDataModel, String rowId) {
	this.rowInfo=new Row(rowId);
	this.alreadyCreated=true;
	this.objectDataModel=objectDataModel;
	this.objectName=objectDataModel.getTypeName();
    }
    
    public ObjectContext(ObjectDataModel<T> objectDataModel) {
	this.alreadyCreated=true;
	this.objectDataModel=objectDataModel;
	this.objectName=objectDataModel.getTypeName();
    }
    
    private void updateRowInfo() throws MdbException {
	String rowId = objectDataModel.getObjectId(target);
	if(rowId == null) {
	    rowInfo=Row.buildFromData(data);
	    objectDataModel.setObjectId(target, rowInfo.getHash());
	}
	else {
	    rowInfo = new Row(rowId);
	}
    }
    
    public void updateObjectId() throws MdbException {
	objectDataModel.setObjectId(target, getRowId());
    }
    
    
    public void addNestedObjectContext(String fieldName, ObjectContext<?> oc) {
	nestedObjectContexts.put(fieldName, oc);
    }
    
    /**
     * @return the indexedValues
     */
    public Map<String, String> getIndexedValues() {
        return indexedValues;
    }
    /**
     * @return the data
     */
    public String getData() {
        return data;
    }
    /**
     * @param indexedValues the indexedValues to set
     */
    public void setIndexedValues(Map<String, String> indexedValues) {
        this.indexedValues = indexedValues;
    }
    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }
    /**
     * @return the objectDataModel
     */
    public ObjectDataModel<T> getObjectDataModel() {
        return objectDataModel;
    }
    /**
     * @param objectDataModel the objectDataModel to set
     */
    public void setObjectDataModel(ObjectDataModel<T> objectDataModel) {
        this.objectDataModel = objectDataModel;
    }
    /**
     * @return the uniqueValues
     */
    public Map<String, UniqueIndexValue> getUniqueValues() {
        return uniqueValues;
    }
    /**
     * @param uniqueValues the uniqueValues to set
     */
    public void setUniqueValues(Map<String, UniqueIndexValue> uniqueValues) {
        this.uniqueValues = uniqueValues;
    }
    
   
    /**
     * @return the oldIndexedValues
     */
    public Map<String, String> getOldIndexedValues() {
        return oldIndexedValues;
    }
    /**
     * @return the oldUniqueValues
     */
    public Map<String, UniqueIndexValue> getOldUniqueValues() {
        return oldUniqueValues;
    }
    /**
     * @param oldIndexedValues the oldIndexedValues to set
     */
    public void setOldIndexedValues(Map<String, String> oldIndexedValues) {
        this.oldIndexedValues = oldIndexedValues;
    }
    /**
     * @param oldUniqueValues the oldUniqueValues to set
     */
    public void setOldUniqueValues(Map<String, UniqueIndexValue> oldUniqueValues) {
        this.oldUniqueValues = oldUniqueValues;
    }

    /**
     * @return the rowLock
     */
    public ResourceLock getRowLock() {
        return rowLock;
    }

    /**
     * @param rowLock the rowLock to set
     */
    public void setRowLock(ResourceLock rowLock) {
        this.rowLock = rowLock;
    }


    /**
     * @return the rowInfo
     */
    public Row getRowInfo() {
        return rowInfo;
    }


    /**
     * @return the alreadyCreated
     */
    public boolean isAlreadyCreated() {
        return alreadyCreated;
    }


    /**
     * @return the nestedObjectContexts
     */
    public Map<String, ObjectContext<?>> getNestedObjectContexts() {
        return nestedObjectContexts;
    }


    /**
     * @return the objectName
     */
    public String getObjectName() {
        return objectName;
    }


    /**
     * @param objectName the objectName to set
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
    
    public String getRowId() {
	return rowInfo.getHash();
    }
    
    public boolean isUpdated() {
	return data != null;
    }


    /**
     * @return the linksToAdd
     */
    public Set<LinkValue> getLinksToAdd() {
        return linksToAdd;
    }


    /**
     * @return the linksToRemove
     */
    public Set<LinkValue> getLinksToRemove() {
        return linksToRemove;
    }


    /**
     * @return the nestedMultivaluedObjectContexts
     */
    public Map<String, List<ObjectContext<?>>> getNestedMultivaluedObjectContexts() {
        return nestedMultivaluedObjectContexts;
    }
    
    public void setRowId(String rowId) {
	rowInfo=new Row(rowId);
    }
}
