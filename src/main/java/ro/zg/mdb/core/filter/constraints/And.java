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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.ComplexConstraint;
import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.ConstraintSet;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.filter.ObjectConstraint;
import ro.zg.mdb.core.filter.ObjectConstraintContext;
import ro.zg.mdb.core.meta.FieldDataModel;
import ro.zg.mdb.core.meta.ObjectDataModel;

public class And<T> extends ConstraintSet<T> implements ObjectConstraint {

    public And() {
	super();
    }

    public And(Set<Constraint<T>> constraints) {
	super(constraints);
    }

    public And(Constraint<T>... contraints) {
	setConstraints(new HashSet<Constraint<T>>(Arrays.asList(contraints)));
    }

    @Override
    public boolean isSatisfied(T value) {
	Constraint<T> result = getCompiledValue();
	if (result == this) {
	    for (Constraint<T> c : constraints) {
		if (!c.isSatisfied(value)) {
		    return false;
		}
	    }
	    return true;
	} else {
	    return result.isSatisfied(value);
	}
    }

    @Override
    public boolean isPossible(FieldDataModel fieldDataModel) {
	Constraint<T> result = getCompiledValue();
	if (result == null) {
	    return false;
	}
	if (result == this) {
	    for (Constraint<?> c : constraints) {
		if (!c.isPossible(fieldDataModel)) {
		    return false;
		}
	    }
	    return true;
	}
	return result.isPossible(fieldDataModel);
    }

    @Override
    public boolean process(FieldConstraintContext context) {
	Constraint<T> result = getCompiledValue();
	if (result != null) {
	    /*
	     * if constraint set couldn't be reduced anymore, we'll have to process each constraint separately
	     */
	    if (result == this) {
		for (Constraint<T> c : constraints) {
		    boolean isPossible = c.process(context);
		    if (!isPossible) {
			return false;
		    }
		}
		return true;
	    }
	    return result.process(context);
	}
	return false;
    }

    @Override
    protected Constraint<T> compile() {
	Constraint<T> result = new All<T>();
	for (Constraint<T> c : constraints) {
	    if (c instanceof ComplexConstraint<?>) {
		result = result.and(((ComplexConstraint<T>) c).getCompiledValue());
	    } else {
		result = result.and(c);
	    }
	    if (result == null) {
		return null;
	    }
	}
	/* if the result couldn't be reduced anymore return this instance */
	if (equals(result)) {
	    return this;
	}
	return result;
    }

    @Override
    public Constraint<T> and(Constraint<T> c) {
	addConstraint(c);
	return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Constraint<T> or(Constraint<T> c) {
	return new Or<T>(this, c);
    }

    @Override
    public Constraint<T> not() {
	Or<T> or = new Or<T>();
	for (Constraint<T> c : constraints) {
	    or.addConstraint(c.not());
	}
	return or;
    }

    @Override
    public boolean process(ObjectConstraintContext objectContext) throws MdbException {
	int processed = 0;
	for (Constraint<T> c : constraints) {
	    ObjectConstraint oc = (ObjectConstraint) c;
	    boolean isProcessed = oc.process(objectContext);
	    if (isProcessed) {
		processed++;
	    }
	    if (processed == 2) {
		objectContext.applyAnd();
		processed--;
	    }
	}
	return (processed > 0);
    }

    @Override
    public boolean isSatisfied(Map<String, Object> values) {
	for (Constraint<T> c : constraints) {
	    ObjectConstraint oc = (ObjectConstraint) c;
	    boolean isSatisfied = oc.isSatisfied(values);
	    if (!isSatisfied) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public boolean isPossible(ObjectDataModel objectDataModel) {
	for (Constraint<T> c : constraints) {
	    ObjectConstraint oc = (ObjectConstraint) c;
	    if (!oc.isPossible(objectDataModel)) {
		return false;
	    }
	}
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "And [constraints=" + constraints + "]";
    }
    
}
