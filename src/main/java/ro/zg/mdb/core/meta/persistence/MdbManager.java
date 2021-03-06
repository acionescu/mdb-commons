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
package ro.zg.mdb.core.meta.persistence;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.persistence.data.MdbConfig;
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
