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
import ro.zg.mdb.core.annotations.ObjectId;
import ro.zg.mdb.core.annotations.Unique;
import ro.zg.util.hash.HashUtil;

@Persistable
public class Row {
    @ObjectId
    private String id;
    
    @Unique(id=1)
    private String hash;
    
    private long timestamp;
    
    private String oldHash;
    
    private static transient volatile long uniqueIndex;
    
    /**
     * Constructor used when the hash is already known, like in the update cases
     * @param hash
     */
    public Row(String rowId) {
	super();
	this.hash = rowId;
    }
    /**
     * Constructor used usually when row info is created from data 
     * @param hash
     * @param currentHash
     * @param timestamp
     */
    public Row(String rowId, long timestamp) {
	super();
	this.hash = rowId;
	this.timestamp = timestamp;
    }

    public static Row buildFromData(String data) {
	long timestamp=System.currentTimeMillis();
	String rowId=HashUtil.digestSHA1(data+timestamp+(uniqueIndex++));
	return new Row(rowId,timestamp);
    }


    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
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
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    
    
}
