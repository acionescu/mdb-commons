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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;
import ro.zg.util.data.SetMap;

public class IndexContext {
    private PersistenceManager persistenceManager;
    
    /* remembers if a path exists or not */
    private Map<String,Boolean> paths=new HashMap<String, Boolean>();
    /* remembers the possible next values for a path */
    private SetMap<String,String> nextVals=new SetMap<String, String>();
    
    public IndexContext(PersistenceManager persistenceManager) {
	super();
	this.persistenceManager = persistenceManager;
    }

    public boolean exists(String path) throws MdbException {
	Boolean exists = paths.get(path);
	if(exists==null) {
	    try {
		exists=persistenceManager.exists(path);
	    } catch (PersistenceException e) {
		throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	    }
	    paths.put(path, exists);
	}
	return exists;
    }
    
    public boolean isNextPossible(String path, int next) throws MdbException {
	Set<String> nested = nextVals.get(path);
	if(nested == null) {
	    try {
		nested=new HashSet<String>(Arrays.asList(persistenceManager.listChildren(path)));
	    } catch (PersistenceException e) {
		throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	    }
	    nextVals.put(path, nested);
	}
	return nested.contains(String.valueOf(next));
    }
}
