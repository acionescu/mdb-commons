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

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.MdbConfig;
import ro.zg.mdb.core.meta.data.SchemaConfig;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;

@Persistable
public class MdbInstance {
    private String name;
    PersistenceManager persistanceManager;
    private MdbConfig config=new MdbConfig();
    private Map<String,SchemaManager> schemas = new HashMap<String, SchemaManager>();
    
    public MdbInstance(String name, PersistenceManager persistanceManager, MdbConfig config) {
	this.name=name;
	this.persistanceManager = persistanceManager;
	this.config = config;
    }
    
    public void start() {
	
    }
    
    private void init() {
	
    }
    
    private void loadSchema(String name) {
	
    }
    
    public SchemaManager getSchema(String schemaName) throws MdbException {
	SchemaManager schemaManager = schemas.get(schemaName);
	if(schemaManager == null) {
	    if(config.isAutomaticSchemaCreationOn()) {
		try {
		    SchemaConfig schemaConfig=new SchemaConfig(schemaName);
		    schemaConfig.setMetadataPersistanceAllowed(true);
		    schemaConfig.setObjectReferenceAllowed(true);
		    schemaConfig.setHistoryAllowed(true);
		    schemaConfig.setSequenceUsageAllowed(true);
		    schemaManager = new SchemaManager(persistanceManager.getPersistenceManager(schemaName), schemaConfig);
		} catch (PersistenceException e) {
		    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
		} 
	    }
	    else {
		throw new RuntimeException("Schema '"+schemaName+"' does not exist.");
	    }
	}
	return schemaManager;
    }
    
    
}
