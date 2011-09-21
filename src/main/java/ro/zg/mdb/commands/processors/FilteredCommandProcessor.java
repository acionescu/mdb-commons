package ro.zg.mdb.commands.processors;

import java.util.Collection;
import java.util.Set;

import ro.zg.mdb.commands.CommandContext;
import ro.zg.mdb.core.concurrency.PersistableObjectLockManager;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.filter.ObjectConstraintContext;
import ro.zg.mdb.core.meta.ObjectDataModel;
import ro.zg.mdb.persistence.PersistenceManager;

public abstract class FilteredCommandProcessor<T,R> extends AbstractCommandProcessor<T,R>{

    public FilteredCommandProcessor(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager) {
	super(persistenceManager, locksManager);
    }

    @Override
    public R process(CommandContext<T> context) throws MdbException {
	Filter filter = context.getFilter();
	ObjectDataModel<T> odm=context.getObjectDataModel();
	
	if (filter.isPossible(odm)) {
	    ObjectConstraintContext<T> occ = new ObjectConstraintContext<T>(odm, this);

	    if (filter.process(occ)) {
		/* some indexes were hit */
		Set<String> allowed = occ.getAllowed();
		Set<String> restricted = occ.getRestricted();
		boolean isAllowed = false;
		if (occ.isSimple() || occ.isIntersection()) {
		    if (allowed != null) {
			isAllowed = true;
		    } else if (restricted != null) {
			isAllowed = false;
		    } else {
			/* nothing has been found */
			return processEmpty(context);
		    }
		} else {
		    if (restricted != null) {
			isAllowed = false;
		    } else if (allowed != null) {
			isAllowed = true;
		    } else {
			/* nothing has been found */
			return processEmpty(context);
		    }
		}

		if (isAllowed && !allowed.isEmpty()) {
		    return processAllowed(context, allowed);
		} else if (!isAllowed) {
		    return processRestricted(context, restricted);
		}
		return processEmpty(context);
	    } else {
		/* no index was hit, we need full scan */
		return processAll(context);
	    }
	}

	return processEmpty(context);
    }
    
    protected abstract R processAllowed(CommandContext<T> context,Collection<String> allowed) throws MdbException;
    
    protected abstract R processRestricted(CommandContext<T> context,Collection<String> restricted) throws MdbException;
    
    protected abstract R processAll(CommandContext<T> context) throws MdbException;
    
    protected abstract R processEmpty(CommandContext<T> context);


}
