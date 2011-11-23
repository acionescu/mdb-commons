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

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.core.meta.data.ObjectsLink;
import ro.zg.util.data.ListMap;

public class TransactionContext {
    private ListMap<String, String> pendingRows = new ListMap<String, String>();
    private Map<String, Object> pendingObjects = new HashMap<String, Object>();
    private Map<String, String> pendingFields = new HashMap<String, String>();
    private Map<String, ObjectsLink> pendingLinks = new HashMap<String, ObjectsLink>();

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

    public void addPendingLink(String fullFieldName, ObjectsLink ol) {
	pendingLinks.put(fullFieldName, ol);
    }

    public ObjectsLink getPendingLink(String fullFieldName) {
	return pendingLinks.get(fullFieldName);
    }

    public void clear() {
	pendingRows = new ListMap<String, String>();
	pendingObjects = new HashMap<String, Object>();
	pendingFields = new HashMap<String, String>();
	pendingLinks = new HashMap<String, ObjectsLink>();
    }
    
    public void clearPendingRows() {
	pendingRows = new ListMap<String, String>();
    }

}
