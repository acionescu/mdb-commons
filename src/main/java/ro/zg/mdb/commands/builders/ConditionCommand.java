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
package ro.zg.mdb.commands.builders;

import ro.zg.mdb.commands.Command;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.constraints.And;
import ro.zg.mdb.core.filter.constraints.Or;


public class ConditionCommand<T,R> extends FilterContextHolder<T, R> implements Command<R>{
   
    

    public ConditionCommand(FilterBuilderContext<T, R> filterContext) {
	super(filterContext);
    }

    public FieldFilterAfterConditionBuilder<T,R> and(){
	filterContext.addCondition(new And<Object>());
	return new FieldFilterAfterConditionBuilder<T,R>(filterContext);
    }
    
    public FieldFilterAfterConditionBuilder<T,R> or(){
	filterContext.addCondition(new Or<Object>());
	return new FieldFilterAfterConditionBuilder<T,R>(filterContext);
    }


    @Override
    public R execute() throws MdbException {
	return filterContext.execute();
    }
    
    
}
