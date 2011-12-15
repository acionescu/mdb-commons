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

import ro.zg.mdb.commands.DeleteCommandContext;
import ro.zg.mdb.core.exceptions.MdbException;

public class DeleteProcessor<T> extends FilteredCommandProcessor<T,DeleteCommandContext<T>>{

//    public DeleteProcessor(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager) {
//	super(persistenceManager, locksManager);
//    }

    @Override
    protected void processAll(DeleteCommandContext<T> context) throws MdbException {
	long deleted = context.getTransactionManager().deleteAll(context.getObjectName(),context.getType(), context.getFilter());
	context.getResultBuilder().setValue(deleted);
    }

    @Override
    protected void processAllowed(DeleteCommandContext<T> context, Collection<String> allowed) throws MdbException {
	long deleted = context.getTransactionManager().deleteObjects(context.getObjectName(),context.getType(),allowed, context.getFilter());
	context.getResultBuilder().setValue(deleted);
    }

    @Override
    protected void processRestricted(DeleteCommandContext<T> context, Collection<String> restricted) throws MdbException {
	long deleted = context.getTransactionManager().deleteAllBut(context.getObjectName(),context.getType(), context.getFilter(),restricted);
	context.getResultBuilder().setValue(deleted);
    }

    @Override
    protected void processEmpty(DeleteCommandContext<T> context) {
	context.getResultBuilder().setValue(0L);
    }

   


}
