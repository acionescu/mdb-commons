package ro.zg.mdb.commands.processors;

import java.util.ArrayList;
import java.util.Collection;

import ro.zg.mdb.commands.CommandContext;
import ro.zg.mdb.core.concurrency.PersistableObjectLockManager;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.persistence.PersistenceManager;

public class FindProcessor<T> extends FilteredCommandProcessor<T,Collection<T>>{

    public FindProcessor(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager) {
	super(persistenceManager, locksManager);
    }

    @Override
    protected Collection<T> processAll(CommandContext<T> context) throws MdbException {
	return readAllObjects(context.getObjectDataModel(), context.getFilter(), new ArrayList<T>());
    }

    @Override
    protected Collection<T> processAllowed(CommandContext<T> context, Collection<String> allowed) throws MdbException {
	return readObjects(allowed, context.getObjectDataModel(), context.getFilter(), new ArrayList<T>());
    }

    @Override
    protected Collection<T> processRestricted(CommandContext<T> context, Collection<String> restricted) throws MdbException {
	return readAllObjectsBut(context.getObjectDataModel(), context.getFilter(), new ArrayList<T>(), restricted);
    }

    @Override
    protected Collection<T> processEmpty(CommandContext<T> context) {
	return new ArrayList<T>();
    }
    
}
