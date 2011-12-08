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

import ro.zg.mdb.commands.builders.CommandBuilderContext;
import ro.zg.mdb.commands.builders.FilteredCommand;
import ro.zg.mdb.constants.OperationType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;

public class DeleteCommand<T> extends FilteredCommand<T,Long>{

    public DeleteCommand(CommandBuilderContext<T> commandContext) {
	super(commandContext);
    }

    @Override
    public Long execute() throws MdbException {
	Filter filter =filterContext.getFilter();
	filter.setOperationType(OperationType.DELETE);
	return commandContext.getObjectDataManager().delete(filter);
    }

    

}
