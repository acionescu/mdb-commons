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

import java.util.Collection;

import ro.zg.mdb.commands.CommandContext;
import ro.zg.mdb.commands.UpdateCommandContext;
import ro.zg.mdb.core.concurrency.PersistableObjectLockManager;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.persistence.PersistenceManager;

public class UpdateProcessor<T> extends FilteredCommandProcessor<T, UpdateCommandContext<T>>{

//    public UpdateProcessor(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager) {
//	super(persistenceManager, locksManager);
//    }

    @Override
    protected void processAll(UpdateCommandContext<T> context) throws MdbException {
	long updated = context.getTransactionManager().updateAll(context.getObjectName(), context.getType(), context.getSource(), context.getFilter());
	context.getResultBuilder().setValue(updated);
    }

    @Override
    protected void processAllowed(UpdateCommandContext<T> context, Collection<String> allowed) throws MdbException {
	long updated = context.getTransactionManager().updateObjects(context.getObjectName(), context.getType(),allowed,context.getSource(), context.getFilter());
	context.getResultBuilder().setValue(updated);
    }

    @Override
    protected void processRestricted(UpdateCommandContext<T> context, Collection<String> restricted) throws MdbException {
	long updated = context.getTransactionManager().updateAllBut(context.getObjectName(), context.getType(),context.getSource(), context.getFilter(),restricted);
	context.getResultBuilder().setValue(updated);
    }

    @Override
    protected void processEmpty(UpdateCommandContext<T> context) {
	context.getResultBuilder().setValue(0L);
    }

}
