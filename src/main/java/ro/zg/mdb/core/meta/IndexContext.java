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
