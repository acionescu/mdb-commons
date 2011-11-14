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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

import ro.zg.mdb.commands.builders.CommandBuilderContext;
import ro.zg.mdb.commands.builders.FilteredCommand;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;


public class GetCommand<T> extends FilteredCommand<T,Collection<T>>{
    
    public GetCommand(CommandBuilderContext<T> commandContext) {
	super(commandContext);
    }


    @Override
    public Collection<T> execute() throws MdbException {
	Filter filter =filterContext.getFilter();
	String[] requiredFields=commandContext.getFields();
	
	if(requiredFields != null) {
	    filter.setTargetFields(new LinkedHashSet<String>(Arrays.asList(requiredFields)));
	}
	
	return commandContext.getObjectDataManager().find(filter);
    }

    

}
