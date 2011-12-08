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
