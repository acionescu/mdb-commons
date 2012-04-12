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

import java.util.Map;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.filter.ObjectConstraint;
import ro.zg.mdb.core.filter.ObjectConstraintContext;
import ro.zg.mdb.core.meta.persistence.data.PersistentFieldMetadata;
import ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadata;

public class All<T> implements Constraint<T>, ObjectConstraint{

    @Override
    public Constraint<T> and(Constraint<T> c) {
	return c;
    }

    @Override
    public boolean isPossible(PersistentFieldMetadata fieldDataModel) {
	return true;
    }

    @Override
    public boolean isSatisfied(T value) {
	return true;
    }

    @Override
    public Constraint<T> or(Constraint<T> c) {
	return this;
    }

    @Override
    public boolean process(FieldConstraintContext context) {
	return true;
    }

    @Override
    public Constraint<T> not() {
	return null;
    }

    @Override
    public boolean isPossible(PersistentObjectMetadata objectDataModel) {
	return true;
    }

    @Override
    public boolean isSatisfied(Map<String, Object> values) {
	return true;
    }

    @Override
    public boolean process(ObjectConstraintContext objectContext) throws MdbException {
	return false;
    }

}
