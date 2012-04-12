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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.Map;

import ro.zg.mdb.core.meta.persistence.data.LinkValue;
import ro.zg.mdb.core.schema.ObjectContext;
import ro.zg.util.data.ListMap;

public class TransactionContext {
    private ListMap<String, String> pendingRows = new ListMap<String, String>();
    private Map<String, Object> pendingObjects = new Hashtable<String, Object>();
    private Map<String, String> pendingFields = new HashMap<String, String>();
    private Map<String, LinkValue> pendingLinks = new HashMap<String, LinkValue>();
    /**
     * This map keeps track of the objects in the course of writing 
     * <br/>
     * An {@link IdentityHashMap} is used because we want to keep track of objects by reference only
     * <br/>
     * Each time a nested object has a reference to an object already in the course of processing
     * will get a dummy {@link ObjectContext} on which the rowId will be filled after the creation of the pending
     * object's {@link ObjectContext}
     */
    private Map<Object, ObjectContext<?>> pendingObjectsForWrite = new IdentityHashMap<Object, ObjectContext<?>>();

    public void addPendingRow(String objectName, String rowId) {
	pendingRows.add(objectName, rowId);
    }

    public boolean isRowPending(String objectName, String rowId) {
	return pendingRows.containsValueForKey(objectName, rowId);
    }

    public void removePendingRow(String objectName, String rowId) {
	pendingRows.remove(objectName, rowId);
    }

    public void addPendingObject(String rowId, Object object) {
	pendingObjects.put(rowId, object);
    }

    public Object getPendingObject(String rowId) {
	return pendingObjects.get(rowId);
    }

    public void addPendingField(String fullFieldName, String rowId) {
	pendingFields.put(fullFieldName, rowId);
    }

    public String getRowIdForPendingField(String fullFieldName) {
	return pendingFields.get(fullFieldName);
    }

    public void addPendingLink(String fullFieldName, LinkValue linkValue) {
	pendingLinks.put(fullFieldName, linkValue);
    }

    public LinkValue getPendingLink(String fullFieldName) {
	return pendingLinks.get(fullFieldName);
    }

    public void clear() {
	pendingRows = new ListMap<String, String>();
	pendingObjects = new HashMap<String, Object>();
	pendingFields = new HashMap<String, String>();
	pendingLinks = new HashMap<String, LinkValue>();
    }
    
    public void clearPendingRows() {
	pendingRows = new ListMap<String, String>();
    }
    
    public <T> void addPendingObjectForWrite(T o, ObjectContext<T> oc) {
	pendingObjectsForWrite.put(o,oc);
    }

    public <T> ObjectContext<T> getObjectContextForPendingObject(T o) {
	return (ObjectContext<T>)pendingObjectsForWrite.get(o);
    }
    
    public <T> ObjectContext<T> removePendingObjectForWrite(T o) {
	return (ObjectContext<T>)pendingObjectsForWrite.remove(o);
    }
    
    public void clearPendingFields() {
	pendingFields.clear();
    }
    
}
