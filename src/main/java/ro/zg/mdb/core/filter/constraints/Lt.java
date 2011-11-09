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
import ro.zg.mdb.core.meta.data.FieldDataModel;

public class Lt<T extends Comparable<T>> implements Constraint<T>{
    
    private T limit;
    private boolean inclusive;
    
    public Lt(T limit, boolean inclusive) {
	super();
	this.limit = limit;
	this.inclusive=inclusive;
    }
    
    public Lt(T limit) {
	super();
	this.limit = limit;
    }


    /**
     * @return the limit
     */
    public T getLimit() {
        return limit;
    }

    @Override
    public boolean isSatisfied(T value) {
	if(inclusive && limit.equals(value)) {
	    return true;
	}
	return limit.compareTo(value) > 0;
    }

    @Override
    public boolean isPossible(FieldDataModel fieldDataModel) {
	return true;
    }

    @Override
    public boolean process(FieldConstraintContext context) {
	context.addRange(null, limit);
	return true;
    }

    @Override
    public Constraint<T> and(Constraint<T> c) {
	if(c instanceof Eq<?>) {
	    return andEq((Eq<T>)c);
	}
	if(c instanceof Lt<?>) {
	    return andLt((Lt<T>)c);
	}
	if(c instanceof Gt<?>) {
	    return andGt((Gt<T>)c);
	}
	return c.and(this);
    }
    
    private Constraint<T> andEq(Eq<T> eq){
	if(!isSatisfied(eq.getExpected())) {
	    return null;
	}
	return eq;
    }
    
    private Constraint<T> andLt(Lt<T> lt){
	if(isSatisfied(lt.limit)) {
	    return lt;
	}
	return this;
    }
    
    private Constraint<T> andGt(Gt<T> gt){
	if(isSatisfied(gt.getLimit())) {
	    return new Between(gt.getLimit(), limit);
	}
	return null;
    }

    @Override
    public Constraint<T> or(Constraint<T> c) {
	if (c instanceof Eq<?>) {
	    return orEq((Eq<T>) c);
	}
	if (c instanceof Gt<?>) {
	    return orGt((Gt<T>) c);
	}
	if (c instanceof Lt<?>) {
	    return orLt((Lt<T>) c);
	}
	return c.or(this);
    }
    
    @SuppressWarnings("unchecked")
    private Constraint<T> orEq(Eq<T> eq){
	if(isSatisfied(eq.getExpected())) {
	    return this;
	}
	return new Or<T>(this,eq);
    }
    
    @SuppressWarnings("unchecked")
    private Constraint<T> orGt(Gt<T> gt){
	if(gt.isSatisfied(limit)) {
	    return new All<T>();
	}
	return new Or<T>(this,gt);
    }
    
    private Constraint<T> orLt(Lt<T> lt){
	if(lt.isSatisfied(limit)) {
	    return lt;
	}
	return this;
	
    }

    @Override
    public Gt<T> not() {
	return new Gt<T>(limit,true);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (inclusive ? 1231 : 1237);
	result = prime * result + ((limit == null) ? 0 : limit.hashCode());
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
	Lt other = (Lt) obj;
	if (inclusive != other.inclusive)
	    return false;
	if (limit == null) {
	    if (other.limit != null)
		return false;
	} else if (!limit.equals(other.limit))
	    return false;
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Lt [limit=" + limit + ", inclusive=" + inclusive + "]";
    }

}
