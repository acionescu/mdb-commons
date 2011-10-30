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

import ro.zg.mdb.core.filter.ComplexConstraint;
import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.meta.FieldDataModel;

public class Not<T> extends ComplexConstraint<T> {
    private Constraint<T> negated;

    public Not() {
	super();
    }

    public Not(Constraint<T> c) {
	negated = c;
    }

    public void addConstraint(Constraint<T> constraint) {
	this.negated = constraint;
    }

    @Override
    public boolean isSatisfied(T value) {
	Constraint<T> result = getCompiledValue();
	if (result == this) {
	    return !negated.isSatisfied(value);
	} else {
	    return result.isSatisfied(value);
	}
    }

    @Override
    public boolean isPossible(FieldDataModel fieldDataModel) {
	return true;
    }

    @Override
    public boolean process(FieldConstraintContext context) {
	Constraint<T> not = getCompiledValue();
	if (not == null) {
	    return false;
	} else if (this != not) {
	    return not.process(context);
	} else {
	    boolean isPossible = negated.process(context);
	    if (!isPossible) {
		return false;
	    }
	    context.setRestricted(true);
	}
	return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Constraint<T> and(Constraint<T> c) {
	Constraint<T> compiled = getCompiledValue();
	if (compiled == null) {
	    return null;
	}
	if (compiled == this) {
	    return new And<T>(this, c);
	}
	return compiled.and(c);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Constraint<T> or(Constraint<T> c) {
	Constraint<T> compiled = getCompiledValue();
	if (compiled == null) {
	    return null;
	}
	if (compiled == this) {
	    return new Or<T>(this, c);
	}
	return compiled.or(c);
    }

    @Override
    public Constraint<T> compile() {
	if (negated instanceof Eq<?>) {
	    return this;
	}
	if (negated instanceof IsNull<?>) {
	    return this;
	}
	return negated.not();
    }

    @Override
    public Constraint<T> not() {
	return negated;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((negated == null) ? 0 : negated.hashCode());
	return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Not other = (Not) obj;
	if (negated == null) {
	    if (other.negated != null)
		return false;
	} else if (!negated.equals(other.negated))
	    return false;
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Not [negated=" + negated + "]";
    }

}
