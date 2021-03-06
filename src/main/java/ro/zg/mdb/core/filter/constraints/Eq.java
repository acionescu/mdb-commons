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

public class Eq<T> implements Constraint<T> {
    protected T expected;
    
    public Eq(T o) {
	if (o == null) {
	    throw new IllegalArgumentException("Expected value cannot be null");
	}
	expected = o;
    }

    @Override
    public boolean isSatisfied(T value) {
	return expected.equals(value);
    }

    @Override
    public boolean isPossible(PersistentFieldMetadata<?> fieldDataModel) {
	return true;
    }

    /**
     * @return the expected
     */
    public T getExpected() {
	return expected;
    }

    @Override
    public boolean process(FieldConstraintContext<?> context) {
	context.addValue(expected);
	return true;
    }

    @Override
    public Constraint<T> and(Constraint<T> c) {
	if (c instanceof Eq<?>) {
	    return andEq((Eq<T>) c);
	}
	return c.and(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Constraint<T> or(Constraint<T> c) {
	if(c instanceof Eq<?>) {
	    return new Or<T>(this,c);
	}
	return c.or(this);
    }

    private Constraint<T> andEq(Eq<T> eq) {
	if (expected.equals(eq.getExpected())) {
	    return this;
	}
	return null;
    }

    @Override
    public Not<T> not() {
	return new Not<T>(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((expected == null) ? 0 : expected.hashCode());
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
	Eq other = (Eq) obj;
	if (expected == null) {
	    if (other.expected != null)
		return false;
	} else if (!expected.equals(other.expected))
	    return false;
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Eq [expected=" + expected + "]";
    }
   
}
