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

import ro.zg.mdb.commands.builders.AbstractCommand;
import ro.zg.mdb.commands.builders.CommandBuilderContext;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.SchemaManager;


public class InsertCommand<T> extends AbstractCommand<T,T>{

    public InsertCommand(CommandBuilderContext<T> commandContext) {
	super(commandContext);
    }

    @Override
    public T execute() throws MdbException {
	T source = commandContext.getSource();
	return commandContext.getObjectDataManager().create(source);
    }

    

}
