package ro.zg.mdb.core.meta;

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.exceptions.MdbException;
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
		    schemaManager = new SchemaManager(schemaName,persistanceManager.getPersistenceManager(schemaName));
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
