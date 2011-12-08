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
package ro.zg.mdb.core.meta.data;

import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.annotations.ObjectId;
import ro.zg.mdb.core.annotations.Unique;
import ro.zg.util.hash.HashUtil;

@Persistable
public class Row {
    @ObjectId
    private String id;
    
    @Unique(id=1)
    private String globalHash;
    
    private long timestamp;
    
    private String currentHash;
    
    private static transient volatile long uniqueIndex;
    
    /**
     * Constructor used when the globalHash is already known, like in the update cases
     * @param globalHash
     */
    public Row(String hash) {
	super();
	this.globalHash = hash;
    }
    /**
     * Constructor used usually when row info is created from data 
     * @param globalHash
     * @param currentHash
     * @param timestamp
     */
    public Row(String rowId, long timestamp) {
	super();
	this.globalHash = rowId;
	this.timestamp = timestamp;
    }

    public static Row buildFromData(String data) {
	long timestamp=System.currentTimeMillis();
	String rowId=HashUtil.digestSHA1(data+timestamp+(++uniqueIndex));
	return new Row(rowId,timestamp);
    }


    /**
     * @return the globalHash
     */
    public String getGlobalHash() {
        return globalHash;
    }
    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

   
    /**
     * @return the currentHash
     */
    public String getCurrentHash() {
        return currentHash;
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    
    
}
