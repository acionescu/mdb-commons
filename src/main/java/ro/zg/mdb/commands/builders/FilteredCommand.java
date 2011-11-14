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
package ro.zg.mdb.commands.builders;


public abstract class FilteredCommand<T,R> extends AbstractCommand<T,R>{
    protected FilterBuilderContext<T, R> filterContext;
   
    public FilteredCommand(CommandBuilderContext<T> commandContext) {
	super(commandContext);
	filterContext=new FilterBuilderContext<T, R>(this);
    }

    public FilterBuilder<T,R> where() {
	return filterContext.getFilterBuilder();
    }
}
