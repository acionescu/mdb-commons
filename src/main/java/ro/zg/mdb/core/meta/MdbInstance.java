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
		    schemaManager = new SchemaManager(persistanceManager.getPersistenceManager(schemaName), new SchemaConfig(schemaName, true));
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
