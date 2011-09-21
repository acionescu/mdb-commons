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
