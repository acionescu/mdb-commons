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
import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.ConstraintSet;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.filter.ObjectConstraint;
import ro.zg.mdb.core.filter.ObjectConstraintContext;
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.ObjectDataModel;

public class Or<T> extends ConstraintSet<T> implements ObjectConstraint {

    public Or() {
	super();
	// TODO Auto-generated constructor stub
    }

    public Or(Set<Constraint<T>> constraints) {
	super(constraints);
	// TODO Auto-generated constructor stub
    }

    public Or(Constraint<T>... contraints) {
	setConstraints(new HashSet<Constraint<T>>(Arrays.asList(contraints)));
    }
    

    @Override
    public Constraint<T> compile() {
	Constraint<T> result = null;
	boolean isFirst = true;
	for (Constraint<T> c : constraints) {
	    if (isFirst) {
		result = c;
		isFirst = false;
	    } else {
		result = result.or(c);
		if (result == null) {
		    return null;
		}
	    }
	}
	if (result == null) {
	    return new All<T>();
	}
	if (equals(result)) {
	    return this;
	}
	return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Constraint<T> and(Constraint<T> c) {
	return new And<T>(this, c);
    }

    @Override
    public boolean isPossible(FieldDataModel fieldDataModel) {
	for (Constraint<T> c : constraints) {
	    if (c.isPossible(fieldDataModel)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean isSatisfied(T value) {
	Constraint<T> result = getCompiledValue();
	if (result == this) {
	    for (Constraint<T> c : constraints) {
		if (c.isSatisfied(value)) {
		    return true;
		}
	    }
	    return false;
	} else {
	    return result.isSatisfied(value);
	}
    }

    @Override
    public Constraint<T> not() {
	And<T> and = new And<T>();
	for (Constraint<T> c : constraints) {
	    and.addConstraint(c.not());
	}
	return and;
    }

    @Override
    public Constraint<T> or(Constraint<T> c) {
	addConstraint(c);
	return this;
    }

    @Override
    public boolean process(FieldConstraintContext context) {
	Constraint<T> result = getCompiledValue();
	if (result != null) {
	    /*
	     * if constraint set couldn't be reduced anymore, we'll have to process each constraint separately
	     */
	    if (result == this) {
		boolean isPossible = false;
		for (Constraint<T> c : constraints) {
		    isPossible = c.process(context) || isPossible;
		}
		return isPossible;
	    }
	    return result.process(context);
	}
	return false;
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
		objectContext.applyOr();
		processed--;
	    }

	}
	return (processed > 0);
    }

    @Override
    public boolean isSatisfied(Map<String, Object> values) {
	for (Constraint<T> c : constraints) {
	    ObjectConstraint oc = (ObjectConstraint) c;
	    if (oc.isSatisfied(values)) {
		return true;
	    }
	}
	return false;
    }

    @Override
    public boolean isPossible(ObjectDataModel objectDataModel) {
	for (Constraint<T> c : constraints) {
	    ObjectConstraint oc = (ObjectConstraint) c;
	    if (oc.isPossible(objectDataModel)) {
		return true;
	    }
	}
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Or [constraints=" + constraints + "]";
    }
}
