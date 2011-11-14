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
