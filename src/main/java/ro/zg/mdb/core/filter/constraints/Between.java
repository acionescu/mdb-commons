package ro.zg.mdb.core.filter.constraints;

import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.meta.FieldDataModel;

public class Between<T extends Comparable<T>> implements Constraint<T> {
    private Gt<T> min;
    private Lt<T> max;

    public Between(T min, T max) {
	super();
	this.min = new Gt<T>(min);
	this.max = new Lt<T>(max);
    }

    public Between(T min, T max, boolean minInclusive, boolean maxInclusive) {
	super();
	this.min = new Gt<T>(min, minInclusive);
	this.max = new Lt<T>(max, maxInclusive);
    }
    
    public T getMinValue() {
	return min.getLimit();
    }
    
    public T getMaxValue() {
	return max.getLimit();
    }

    @Override
    public boolean isSatisfied(T value) {
	return min.isSatisfied(value) && max.isSatisfied(value);
    }

    @Override
    public boolean isPossible(FieldDataModel fieldDataModel) {
	return min.isPossible(fieldDataModel) && max.isPossible(fieldDataModel);
    }


    @Override
    public boolean process(FieldConstraintContext context) {
	context.addRange(min.getLimit(), max.getLimit());
	return true;
    }

    @Override
    public Constraint<T> and(Constraint<T> c) {
	if(c instanceof Eq<?>) {
	    return andEq((Eq<T>)c);
	}
	if(c instanceof Gt<?>) {
	    return andGt((Gt<T>)c);
	}
	if(c instanceof Lt<?>) {
	    return andLt((Lt<T>)c);
	}
	if(c instanceof Between<?>) {
	    return andRange((Between<T>)c);
	}
	return c.and(this);
    }

    private Constraint<T> andEq(Eq<T> eq) {
	if (isSatisfied(eq.getExpected())) {
	    return eq;
	}
	return null;
    }

    private Constraint<T> andGt(Gt<T> gt) {
	Gt<T> newMin =(Gt<T>)gt.and(min);
	return newMin.and(max);
    }

    private Constraint<T> andLt(Lt<T> lt) {
	Lt<T> newMax=(Lt<T>)lt.and(max);
	return newMax.and(min);
    }

    private Constraint<T> andRange(Between<T> r) {
	Between<T> newRange=(Between<T>)r.and(min);
	if(newRange != null) {
	    return newRange.and(max);
	}
	return null;
    }

    @Override
    public Constraint<T> or(Constraint<T> c) {
	if(c instanceof Eq<?>) {
	    return orEq((Eq<T>)c);
	}
	if(c instanceof Gt<?>) {
	    return orGt((Gt<T>)c);
	}
	if(c instanceof Lt<?>) {
	    return orLt((Lt<T>)c);
	}
	if(c instanceof Between<?>) {
	    return orRange((Between<T>)c);
	}
	return c.or(this);
    }
    
    @SuppressWarnings("unchecked")
    private Constraint<T> orEq(Eq<T> eq) {
	if (isSatisfied(eq.getExpected())) {
	    return this;
	}
	return new Or<T>(this,eq);
    }

    @SuppressWarnings("unchecked")
    private Constraint<T> orGt(Gt<T> gt) {
	Gt<T> newMin =(Gt<T>)gt.or(min);
	if(max.isSatisfied(newMin.getLimit())) {
	    return newMin;
	}
	return new Or<T>(this,gt);
    }

    @SuppressWarnings("unchecked")
    private Constraint<T> orLt(Lt<T> lt) {
	Lt<T> newMax=(Lt<T>)lt.and(max);
	if(newMax.isSatisfied(min.getLimit())) {
	    return newMax;
	}
	return new Or<T>(this,lt);
    }

    @SuppressWarnings("unchecked")
    private Constraint<T> orRange(Between<T> r) {
	if(isSatisfied(r.getMinValue())) {
	    if(isSatisfied(r.getMaxValue())) {
		return this;
	    }
	    return new Between(min.getLimit(),r.getMaxValue());
	}
	else if(r.isSatisfied(max.getLimit())){
	    return r;
	}
	else if(isSatisfied(r.getMaxValue())) {
	    return new Between<T>(r.getMinValue(),max.getLimit());
	}
	return new Or<T>(this,r);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Or<T> not() {
	return new Or<T>(min.not(),max.not());
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((max == null) ? 0 : max.hashCode());
	result = prime * result + ((min == null) ? 0 : min.hashCode());
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
	Between other = (Between) obj;
	if (max == null) {
	    if (other.max != null)
		return false;
	} else if (!max.equals(other.max))
	    return false;
	if (min == null) {
	    if (other.min != null)
		return false;
	} else if (!min.equals(other.min))
	    return false;
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Between [min=" + min + ", max=" + max + "]";
    }
    
    
}
