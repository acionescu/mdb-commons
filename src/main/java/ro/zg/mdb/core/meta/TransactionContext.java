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

import java.util.HashMap;
import java.util.Map;

import ro.zg.util.data.ListMap;

public class TransactionContext {
    private ListMap<String, String> pendingRows=new ListMap<String, String>();
    private Map<String,Object> pendingObjects=new HashMap<String, Object>();
    private Map<String,String> pendingFields=new HashMap<String, String>();
    
    public void addPendingRow(String objectName, String rowId) {
	pendingRows.add(objectName, rowId);
    }
    
    public boolean isRowPending(String objectName, String rowId) {
	return pendingRows.containsValueForKey(objectName, rowId);
    }
    
    public void removePendingRow(String objectName, String rowId) {
	pendingRows.remove(objectName,rowId);
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
    
}
