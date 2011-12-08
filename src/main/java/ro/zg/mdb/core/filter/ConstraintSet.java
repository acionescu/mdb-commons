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
package ro.zg.mdb.core.filter;

import java.util.HashSet;
import java.util.Set;

public abstract class ConstraintSet<T> extends ComplexConstraint<T>{
    protected Set<Constraint<T>> constraints=new HashSet<Constraint<T>>();
    
    public ConstraintSet() {
	super();
    }
    public ConstraintSet(Set<Constraint<T>> constraints) {
	super();
	this.constraints = constraints;
    }
    /**
     * @return the constraints
     */
    public Set<Constraint<T>> getConstraints() {
        return constraints;
    }
    /**
     * @param constraints the constraints to set
     */
    public void setConstraints(Set<Constraint<T>> constraints) {
        this.constraints = constraints;
    }
    
    public void addConstraint(Constraint<T> constraint) {
	constraints.add(constraint);
    }
    
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((constraints == null) ? 0 : constraints.hashCode());
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
	ConstraintSet other = (ConstraintSet) obj;
	if (constraints == null) {
	    if (other.constraints != null)
		return false;
	} else if (!constraints.equals(other.constraints))
	    return false;
	return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "ConstraintSet [constraints=" + constraints + "]";
    }
   
}
