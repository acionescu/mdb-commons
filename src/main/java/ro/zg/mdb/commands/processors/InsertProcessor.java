package ro.zg.mdb.commands.processors;

import ro.zg.mdb.commands.CommandContext;
import ro.zg.mdb.core.concurrency.PersistableObjectLockManager;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.persistence.PersistenceManager;

public class InsertProcessor<T> extends AbstractCommandProcessor<T,T>{

    public InsertProcessor(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager) {
	super(persistenceManager, locksManager);
    }

    @Override
    public T process(CommandContext<T> context) throws MdbException {
	return create(context.getSource(), context.getObjectDataModel(), context.getSchemaContext());
    }

}
