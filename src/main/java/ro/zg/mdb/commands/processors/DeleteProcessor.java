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
package ro.zg.mdb.commands.processors;

import java.util.Collection;

import ro.zg.mdb.commands.CommandContext;
import ro.zg.mdb.core.concurrency.PersistableObjectLockManager;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.persistence.PersistenceManager;

public class DeleteProcessor<T> extends FilteredCommandProcessor<T,Long>{

//    public DeleteProcessor(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager) {
//	super(persistenceManager, locksManager);
//    }

    @Override
    protected Long processAll(CommandContext<T> context) throws MdbException {
	return context.getTransactionManager().deleteAll(context.getObjectName(),context.getType(), context.getFilter());
    }

    @Override
    protected Long processAllowed(CommandContext<T> context, Collection<String> allowed) throws MdbException {
	return context.getTransactionManager().deleteObjects(context.getObjectName(),context.getType(),allowed, context.getFilter());
    }

    @Override
    protected Long processRestricted(CommandContext<T> context, Collection<String> restricted) throws MdbException {
	return context.getTransactionManager().deleteAllBut(context.getObjectName(),context.getType(), context.getFilter(),restricted);
    }

    @Override
    protected Long processEmpty(CommandContext<T> context) {
	return 0L;
    }

   


}
