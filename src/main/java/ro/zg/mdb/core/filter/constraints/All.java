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

import java.util.Map;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.filter.ObjectConstraint;
import ro.zg.mdb.core.filter.ObjectConstraintContext;
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.ObjectDataModel;

public class All<T> implements Constraint<T>, ObjectConstraint{

    @Override
    public Constraint<T> and(Constraint<T> c) {
	return c;
    }

    @Override
    public boolean isPossible(FieldDataModel fieldDataModel) {
	return true;
    }

    @Override
    public boolean isSatisfied(T value) {
	return true;
    }

    @Override
    public Constraint<T> or(Constraint<T> c) {
	return this;
    }

    @Override
    public boolean process(FieldConstraintContext context) {
	return true;
    }

    @Override
    public Constraint<T> not() {
	return null;
    }

    @Override
    public boolean isPossible(ObjectDataModel objectDataModel) {
	return true;
    }

    @Override
    public boolean isSatisfied(Map<String, Object> values) {
	return true;
    }

    @Override
    public boolean process(ObjectConstraintContext objectContext) throws MdbException {
	return false;
    }

}
