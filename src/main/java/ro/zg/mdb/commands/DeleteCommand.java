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
package ro.zg.mdb.commands;

import ro.zg.mdb.commands.builders.CommandBuilderContext;
import ro.zg.mdb.commands.builders.FilteredCommand;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;

public class DeleteCommand<T> extends FilteredCommand<T,Long>{

    public DeleteCommand(CommandBuilderContext<T> commandContext) {
	super(commandContext);
    }

    @Override
    public Long execute() throws MdbException {
	Filter filter =filterContext.getFilter();
	return commandContext.getObjectDataManager().delete(filter);
    }

    

}
