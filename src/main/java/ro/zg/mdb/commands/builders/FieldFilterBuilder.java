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

import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.constraints.Between;
import ro.zg.mdb.core.filter.constraints.Eq;
import ro.zg.mdb.core.filter.constraints.Gt;
import ro.zg.mdb.core.filter.constraints.IsNull;
import ro.zg.mdb.core.filter.constraints.Lt;
import ro.zg.mdb.core.filter.constraints.Not;
import ro.zg.mdb.core.filter.constraints.Or;


public class FieldFilterBuilder<T,R> extends FilterContextHolder<T, R>{

    public FieldFilterBuilder(FilterBuilderContext<T, R> filterContext) {
	super(filterContext);
    }

    
    public ConditionCommand<T,R> eq(Object expected){
	filterContext.addConstraint(new Eq<Object>(expected));
	return new ConditionCommand<T, R>(filterContext);
    }
    
    public ConditionCommand<T,R> isNull(){
	filterContext.addConstraint(new IsNull<Object>());
	return new ConditionCommand<T, R>(filterContext);
    }
    
    @SuppressWarnings("unchecked")
    public ConditionCommand<T,R> gt(int value){
	filterContext.addConstraint((Constraint)new Gt<Integer>(value));
	return new ConditionCommand<T, R>(filterContext);
    }
    
    @SuppressWarnings("unchecked")
    public ConditionCommand<T,R> gt(long value){
	filterContext.addConstraint((Constraint)new Gt<Long>(value));
	return new ConditionCommand<T, R>(filterContext);
    }
    
    @SuppressWarnings("unchecked")
    public ConditionCommand<T,R> lt(int value){
	filterContext.addConstraint((Constraint)new Lt<Integer>(value));
	return new ConditionCommand<T, R>(filterContext);
    }
    
    @SuppressWarnings("unchecked")
    public ConditionCommand<T,R> lt(long value){
	filterContext.addConstraint((Constraint)new Lt<Long>(value));
	return new ConditionCommand<T, R>(filterContext);
    }
    
    @SuppressWarnings("unchecked")
    public ConditionCommand<T,R> between(int min, int max){
	filterContext.addConstraint((Constraint)new Between<Integer>(min,max));
	return new ConditionCommand<T, R>(filterContext);
    }
    
    @SuppressWarnings("unchecked")
    public ConditionCommand<T,R> between(long min, long max){
	filterContext.addConstraint((Constraint)new Between<Long>(min,max));
	return new ConditionCommand<T, R>(filterContext);
    }
    
    public FieldFilterBuilder<T,R> not(){
	filterContext.addCondition(new Not<Object>());
	return this;
    }
    
    @SuppressWarnings("unchecked")
    public ConditionCommand<T, R> in(Object... values){
	Or<Object> or= new Or<Object>();
	for(Object v: values) {
	    or.addConstraint((Constraint)new Eq<Object>(v));
	}
	filterContext.addConstraint(or);
	return new ConditionCommand<T, R>(filterContext);
    }
} 
