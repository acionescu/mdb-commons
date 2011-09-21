package ro.zg.mdb.core.filter.constraints;

import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.meta.FieldDataModel;

public class IsNull<T> implements Constraint<T>{

    @Override
    public boolean isSatisfied(T value) {
	return value==null;
    }

    @Override
    public boolean isPossible(FieldDataModel fieldDataModel) {
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
