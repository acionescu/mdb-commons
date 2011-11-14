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

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;

public class PersistentDataManager {
    protected PersistenceManager persistenceManager;

    public PersistentDataManager(PersistenceManager persistenceManager) {
	super();
	this.persistenceManager = persistenceManager;
    }
    
    protected PersistenceManager getPersistenceManager(String path) throws MdbException {
	try {
	    return persistenceManager.getPersistenceManager(path);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
    }
}
