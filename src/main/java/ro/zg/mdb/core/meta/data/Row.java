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
package ro.zg.mdb.core.meta.data;

import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.annotations.PrimaryKey;
import ro.zg.mdb.core.annotations.Unique;
import ro.zg.util.hash.HashUtil;

@Persistable
public class Row {
    @PrimaryKey
    private String rowId;
    @Unique(id=1)
    private String currentHash;
    
    private long timestamp;
    
    private String oldHash;
    
    /**
     * Constructor used when the rowId is already known, like in the update cases
     * @param rowId
     */
    public Row(String rowId) {
	super();
	this.rowId = rowId;
    }
    /**
     * Constructor used usually when row info is created from data 
     * @param rowId
     * @param currentHash
     * @param timestamp
     */
    public Row(String rowId, String currentHash, long timestamp) {
	super();
	this.rowId = rowId;
	this.currentHash = currentHash;
	this.timestamp = timestamp;
    }

    public static Row buildFromData(String data) {
	long timestamp=System.currentTimeMillis();
	String currentHash=HashUtil.digestSHA1(data);
	String rowId=HashUtil.digestSHA1(currentHash+timestamp);
	return new Row(rowId,currentHash,timestamp);
    }

    /**
     * @return the rowId
     */
    public String getRowId() {
        return rowId;
    }

    /**
     * @return the currentHash
     */
    public String getCurrentHash() {
        return currentHash;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return the oldHash
     */
    public String getOldHash() {
        return oldHash;
    }
    
    
}
