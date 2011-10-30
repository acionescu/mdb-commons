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
