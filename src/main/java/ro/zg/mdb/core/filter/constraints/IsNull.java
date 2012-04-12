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
package ro.zg.mdb.core.filter.constraints;

import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.meta.persistence.data.PersistentFieldMetadata;

public class IsNull<T> implements Constraint<T>{

    @Override
    public boolean isSatisfied(T value) {
	return value==null;
    }

    @Override
    public boolean isPossible(PersistentFieldMetadata fieldDataModel) {
	if(fieldDataModel.isRequired()) {
	    /*
	     * if IsNull constraint is present and the field is marked as required, then there is now way that
	     * such a row exists
	     */
	    return false;
	}
	return true;
    }

    @Override
    public boolean process(FieldConstraintContext context) {
	context.addValue(null);
	return true;
    }

    @Override
    public Constraint<T> and(Constraint<T> c) {
	return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Constraint<T> or(Constraint<T> c) {
	return new Or<T>(this,c);
    }

    @Override
    public Constraint<T> not() {
	return new Not<T>(this);
    }

}
