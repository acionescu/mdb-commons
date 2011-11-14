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
import ro.zg.mdb.core.meta.data.MdbConfig;
import ro.zg.mdb.persistence.FilePersistenceManager;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;

public class MdbManager {
    private String repositoryDir;
    private PersistenceManager persistanceManager;
    
    public MdbManager(String repositoryDir) {
	this.repositoryDir=repositoryDir;
	persistanceManager=new FilePersistenceManager(repositoryDir);
    }
    
    public MdbInstance getMdbInstance(String name) throws MdbException {
	return getMdbInstance(name, new MdbConfig());
    }
    
    public MdbInstance getMdbInstance(String name, MdbConfig config) throws MdbException {
	try {
	    return new MdbInstance(name, persistanceManager.getPersistenceManager(name), config);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
    }
    
  
}
