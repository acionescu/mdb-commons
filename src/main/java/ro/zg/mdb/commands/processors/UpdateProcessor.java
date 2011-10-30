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
import ro.zg.mdb.core.concurrency.PersistableObjectLockManager;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.persistence.PersistenceManager;

public class UpdateProcessor<T> extends FilteredCommandProcessor<T, Long>{

    public UpdateProcessor(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager) {
	super(persistenceManager, locksManager);
    }

    @Override
    protected Long processAll(CommandContext<T> context) throws MdbException {
	return updateAll(context.getSource(), context.getObjectDataModel(), context.getFilter());
    }

    @Override
    protected Long processAllowed(CommandContext<T> context, Collection<String> allowed) throws MdbException {
	return updateObjects(allowed,context.getSource(), context.getObjectDataModel(), context.getFilter());
    }

    @Override
    protected Long processRestricted(CommandContext<T> context, Collection<String> restricted) throws MdbException {
	return updateAllBut(context.getSource(), context.getObjectDataModel(), context.getFilter(),restricted);
    }

    @Override
    protected Long processEmpty(CommandContext<T> context) {
	return 0L;
    }

}
