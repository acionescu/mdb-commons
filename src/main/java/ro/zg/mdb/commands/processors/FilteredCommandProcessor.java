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
import java.util.Set;

import ro.zg.mdb.commands.FilteredCommandContext;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.filter.ObjectConstraintContext;
import ro.zg.mdb.core.meta.data.ObjectDataModel;

public abstract class FilteredCommandProcessor<T, C extends FilteredCommandContext<T, ?, ?, ?>> implements
	CommandProcessor<T, C> {

    // public FilteredCommandProcessor(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager)
    // {
    // super(persistenceManager, locksManager);
    // }

    @Override
    public void process(C context) throws MdbException {
	Filter filter = context.getFilter();
	ObjectDataModel<T> odm = context.getObjectDataModel();

	if (filter.isPossible(odm)) {
	    ObjectConstraintContext<T> occ = new ObjectConstraintContext<T>(context.getObjectName(), context.getType(),
		    context.getTransactionManager());

	    if (filter.process(occ)) {
		/* some indexes were hit */
		occ.resolveNestedObjectContexts();
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
			processEmpty(context);
			return;
		    }
		} else {
		    if (restricted != null) {
			isAllowed = false;
		    } else if (allowed != null) {
			isAllowed = true;
		    } else {
			/* nothing has been found */
			processEmpty(context);
			return;
		    }
		}

		if (isAllowed && !allowed.isEmpty()) {
		    processAllowed(context, allowed);
		    return;
		} else if (!isAllowed) {
		    processRestricted(context, restricted);
		    return;
		}
		processEmpty(context);
		return;
	    } else {
		/* no index was hit, we need full scan */
		processAll(context);
		return;
	    }
	}

	processEmpty(context);
    }

    protected abstract void processAllowed(C context, Collection<String> allowed) throws MdbException;

    protected abstract void processRestricted(C context, Collection<String> restricted) throws MdbException;

    protected abstract void processAll(C context) throws MdbException;

    protected abstract void processEmpty(C context);

}
