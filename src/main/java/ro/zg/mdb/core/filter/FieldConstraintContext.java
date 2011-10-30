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

import ro.zg.mdb.core.filter.constraints.Range;
import ro.zg.mdb.core.meta.FieldDataModel;

public class FieldConstraintContext<T extends Comparable<T>> {
    private FieldDataModel fieldDataModel;
    private Set<String> values = new HashSet<String>();
    private Range<T> range;
    private boolean restricted;
    private Constraint<T> constraint;

    public FieldConstraintContext(FieldDataModel fdm, Constraint<T> constraint) {
	fieldDataModel = fdm;
	this.constraint=constraint;
    }

    public String getFieldName() {
	return fieldDataModel.getName();
    }

    public void addValue(Object value) {
	if (value != null) {
	    values.add(value.toString());
	} else {
	    values.add(null);
	}
    }

    @SuppressWarnings("unchecked")
    public void addRange(T minValue, T maxValue) {
	if (range == null) {
	    range = new Range<T>(minValue, maxValue);
	} else {
	    boolean change = false;
	    T min = range.getMinValue();
	    T max = range.getMaxValue();
	    
	    if (minValue != null) {
		if (min == null || range.checkValue(minValue) < 0) {
		    min = minValue;
		    change = true;
		}
	    }
	    if (maxValue != null) {
		if (max==null || range.checkValue(maxValue) > 0) {
		    max = maxValue;
		    change = true;
		}
	    }
	    if (change) {
		range = new Range(min, max);
	    }
	}
    }

    /**
     * @return the fieldDataModel
     */
    public FieldDataModel getFieldDataModel() {
	return fieldDataModel;
    }

    /**
     * @param fieldDataModel
     *            the fieldDataModel to set
     */
    public void setFieldDataModel(FieldDataModel fieldDataModel) {
	this.fieldDataModel = fieldDataModel;
    }

    /**
     * @return the values
     */
    public Set<String> getValues() {
	return values;
    }

    /**
     * @return the restricted
     */
    public boolean isRestricted() {
	return restricted;
    }

    /**
     * @param values
     *            the values to set
     */
    public void setValues(Set<String> values) {
	this.values = values;
    }

    /**
     * @return the range
     */
    public Range<T> getRange() {
	return range;
    }

    /**
     * @param restricted
     *            the restricted to set
     */
    public void setRestricted(boolean restricted) {
	this.restricted = restricted;
    }

    /**
     * @return the minMax
     */
    public boolean hasRanges() {
	return range != null;
    }

    /**
     * @return the constraint
     */
    public Constraint<T> getConstraint() {
	return constraint;
    }

    public boolean hasValues() {
	return !values.isEmpty();
    }

}
