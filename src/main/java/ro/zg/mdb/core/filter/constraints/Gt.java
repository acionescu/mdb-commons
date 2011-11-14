/*******************************************************************************
 * Copyright (c) 2011 Adrian Cristian Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Adrian Cristian Ionescu - initial API and implementation
 ******************************************************************************/
package ro.zg.mdb.core.filter.constraints;

import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.meta.data.FieldDataModel;

public class Gt<T extends Comparable<T>> implements Constraint<T> {
    private T limit;
    private boolean inclusive;
    
    public Gt(T limit, boolean inclusive) {
	super();
	this.limit = limit;
	this.inclusive=inclusive;
    }

    public Gt(T limit) {
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
	return limit.compareTo(value) < 0;
    }

    @Override
    public boolean isPossible(FieldDataModel fieldDataModel) {
	return true;
    }

    @Override
    public boolean process(FieldConstraintContext context) {
	context.addRange(limit, null);
	return true;
    }

    @Override
    public Constraint<T> and(Constraint<T> c) {
	if (c instanceof Eq<?>) {
	    return andEq((Eq<T>) c);
	}
	if (c instanceof Gt<?>) {
	    return andGt((Gt<T>) c);
	}
	if (c instanceof Lt<?>) {
	    return andLt((Lt<T>) c);
	}
	return c.and(this);
    }

    private Constraint<T> andEq(Eq<T> eq) {
	if (!isSatisfied(eq.getExpected())) {
	    return null;
	}
	return eq;
    }

    private Constraint<T> andGt(Gt<T> gt) {
	if (isSatisfied(gt.limit)) {
	    return gt;
	}
	return this;
    }

    private Constraint<T> andLt(Lt<T> lt) {
	if (isSatisfied(lt.getLimit())) {
	    return new Between<T>(limit, lt.getLimit());
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
    
    private Constraint<T> orGt(Gt<T> gt){
	if(gt.isSatisfied(limit)) {
	    return gt;
	}
	return this;
    }
    
    @SuppressWarnings("unchecked")
    private Constraint<T> orLt(Lt lt){
	if(lt.isSatisfied(limit)) {
	    return new All<T>();
	}
	return new Or<T>(this,lt);
    }

    @Override
    public Lt<T> not() {
	return new Lt<T>(limit, true);
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
	Gt other = (Gt) obj;
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
	return "Gt [limit=" + limit + ", inclusive=" + inclusive + "]";
    }

}
