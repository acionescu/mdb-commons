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

import java.util.ArrayList;
import java.util.Collection;

import ro.zg.mdb.commands.CommandContext;
import ro.zg.mdb.core.exceptions.MdbException;

public class FindProcessor<T> extends FilteredCommandProcessor<T, Collection<T>> {

    // public FindProcessor(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager) {
    // super(persistenceManager, locksManager);
    // }

    @Override
    protected Collection<T> processAll(CommandContext<T> context) throws MdbException {
	return context.getTransactionManager().readAllObjects(context.getObjectName(), context.getType(),
		context.getFilter(), new ArrayList<T>());
    }

    @Override
    protected Collection<T> processAllowed(CommandContext<T> context, Collection<String> allowed) throws MdbException {
	return context.getTransactionManager().readObjects(context.getObjectName(), context.getType(), allowed,
		context.getFilter(), new ArrayList<T>());
    }

    @Override
    protected Collection<T> processRestricted(CommandContext<T> context, Collection<String> restricted)
	    throws MdbException {
	return context.getTransactionManager().readAllObjectsBut(context.getObjectName(), context.getType(),
		context.getFilter(), new ArrayList<T>(), restricted);
    }

    @Override
    protected Collection<T> processEmpty(CommandContext<T> context) {
	return new ArrayList<T>();
    }

}
