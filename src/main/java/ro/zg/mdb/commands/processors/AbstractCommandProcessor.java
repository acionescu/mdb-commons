package ro.zg.mdb.commands.processors;

import ro.zg.mdb.core.concurrency.PersistableObjectLockManager;
import ro.zg.mdb.core.meta.PersistentObjectDataManager;
import ro.zg.mdb.persistence.PersistenceManager;


public abstract class AbstractCommandProcessor<T,R> extends PersistentObjectDataManager implements CommandProcessor<T,R>{

    public AbstractCommandProcessor(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager) {
	super(persistenceManager, locksManager);
    }

}
