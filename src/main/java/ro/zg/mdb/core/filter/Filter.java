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
package ro.zg.mdb.core.filter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.constants.Constants;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.constraints.All;
import ro.zg.mdb.core.meta.data.ObjectDataModel;

public class Filter implements ObjectConstraint {
    private ObjectConstraint constraint = new All<Object>();
    private Set<String> targetFields;
    private Set<String> constraintFields;
    private Set<String> neededFields;

    public Filter() {
	super();
	neededFields = new HashSet<String>();
    }

    public Filter(ObjectConstraint constraint, Set<String> constraintFields) {
	super();
	this.constraint = constraint;
	this.constraintFields = constraintFields;
	updateNeededFields();
    }

    private void updateNeededFields() {
	neededFields = new HashSet<String>();
	if (targetFields != null) {
	    extractNeededFields(targetFields);
	}
	if (constraintFields != null) {
	    extractNeededFields(constraintFields);
	}
    }

    private void extractNeededFields(Set<String> fields) {
	for (String field : fields) {
	    neededFields.add(field);
	    String parentField = field;
	    int index = -1;
	    while ((index = parentField.lastIndexOf(Constants.NESTED_FIELD_SEPARATOR)) > 0) {
		parentField = parentField.substring(0, index);
		neededFields.add(parentField);
	    }
	}
    }

    public boolean isFieldNeeded(String fullFieldName) {
	return neededFields.contains(fullFieldName);
    }

    /**
     * @return the constraint
     */
    public ObjectConstraint getConstraint() {
	return constraint;
    }

    /**
     * @return the targetFields
     */
    public Set<String> getTargetFields() {
	return targetFields;
    }

    /**
     * @return the constraintFields
     */
    public Set<String> getConstraintFields() {
	return constraintFields;
    }

    @Override
    public boolean isSatisfied(Map<String, Object> values) {
	return constraint.isSatisfied(values);
    }

    @Override
    public boolean process(ObjectConstraintContext<?> objectContext) throws MdbException {
	return constraint.process(objectContext);
    }

    @Override
    public boolean isPossible(ObjectDataModel<?> objectDataModel) {
	return constraint.isPossible(objectDataModel);
    }

    /**
     * @param targetFields
     *            the targetFields to set
     */
    public void setTargetFields(Set<String> targetFields) {
	if (targetFields != null && targetFields.size() > 0) {
	    this.targetFields = targetFields;
	} else {
	    this.targetFields = null;
	}
	if (targetFields != null) {
	    extractNeededFields(targetFields);
	}
    }

    public boolean isTargetFieldPresent(String fieldName) {
	return targetFields.contains(fieldName);
    }
}
