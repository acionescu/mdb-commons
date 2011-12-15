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
package ro.zg.mdb.commands;

import java.util.Arrays;
import java.util.LinkedHashSet;

import ro.zg.mdb.commands.builders.CommandBuilderContext;
import ro.zg.mdb.commands.builders.FilteredCommand;
import ro.zg.mdb.commands.builders.FindResultBuilderFactory;
import ro.zg.mdb.commands.builders.PersistentCollection;
import ro.zg.mdb.commands.builders.SimpleFindResultBuilderFactory;
import ro.zg.mdb.constants.OperationType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;


public class GetCommand<T,R> extends FilteredCommand<T,PersistentCollection<R>>{
    private FindResultBuilderFactory<T,R> defaultResultBuilderFactory = new SimpleFindResultBuilderFactory<T,R>(); 
    
    public GetCommand(CommandBuilderContext<T> commandContext) {
	super(commandContext);
    }


    public GetCommand(CommandBuilderContext<T> commandContext,
	    FindResultBuilderFactory<T, R> defaultResultBuilderFactory) {
	super(commandContext);
	this.defaultResultBuilderFactory = defaultResultBuilderFactory;
    }




    @Override
    public PersistentCollection<R> execute() throws MdbException {
	Filter filter =filterContext.getFilter();
	filter.setOperationType(OperationType.READ);
	String[] requiredFields=commandContext.getFields();
	
	if(requiredFields != null) {
	    filter.setTargetFields(new LinkedHashSet<String>(Arrays.asList(requiredFields)));
	}
	
	return commandContext.getObjectDataManager().find(filter,defaultResultBuilderFactory.getResultBuilder());
    }

    

}
