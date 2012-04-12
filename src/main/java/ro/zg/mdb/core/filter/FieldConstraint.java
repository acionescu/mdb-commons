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

import java.util.Map;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.constraints.And;
import ro.zg.mdb.core.filter.constraints.Or;
import ro.zg.mdb.core.meta.persistence.data.PersistentFieldMetadata;
import ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadata;

public class FieldConstraint<T> implements Constraint<T>, ObjectConstraint {
    private String fieldName;
    private Constraint<Object> constraint;

    public FieldConstraint(String fieldName, Constraint<Object> constraint) {
	super();
	this.fieldName = fieldName;
	this.constraint = constraint;
    }

    public boolean process(ObjectConstraintContext<?> objectContext) throws MdbException {
	PersistentObjectMetadata<?> odm = objectContext.getObjectDataModel();
	PersistentFieldMetadata<?> fdm = odm.getField(fieldName);
	
	if (!fdm.isIndexed() && !fdm.isObjectId()) {
	    return false;
	}
	FieldConstraintContext<?> fieldContext = new FieldConstraintContext(fdm,constraint);
	process(fieldContext);
	
	objectContext.addFieldConstraintContext(fieldContext,fieldName);
	return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Constraint<T> and(Constraint<T> c) {
	return new And<T>(this, c);
    }

    @Override
    public boolean isPossible(PersistentFieldMetadata fieldDataModel) {
	return constraint.isPossible(fieldDataModel);
    }

    @Override
    public boolean isSatisfied(T value) {
	return constraint.isSatisfied(value);
    }

    @Override
    public Constraint<T> not() {
	return new FieldConstraint<T>(fieldName, constraint.not());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Constraint<T> or(Constraint<T> c) {
	return new Or<T>(this, c);
    }

    @Override
    public boolean process(FieldConstraintContext context) {
	return constraint.process(context);
    }

    @Override
    public boolean isSatisfied(Map<String, Object> values) {
	return constraint.isSatisfied(values.get(fieldName));
    }

    @Override
    public boolean isPossible(PersistentObjectMetadata<?> objectDataModel) {
	return constraint.isPossible(objectDataModel.getField(fieldName));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((constraint == null) ? 0 : constraint.hashCode());
	result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
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
	FieldConstraint other = (FieldConstraint) obj;
	if (constraint == null) {
	    if (other.constraint != null)
		return false;
	} else if (!constraint.equals(other.constraint))
	    return false;
	if (fieldName == null) {
	    if (other.fieldName != null)
		return false;
	} else if (!fieldName.equals(other.fieldName))
	    return false;
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "FieldConstraint [fieldName=" + fieldName + ", constraint=" + constraint + "]";
    }
    
    
}
