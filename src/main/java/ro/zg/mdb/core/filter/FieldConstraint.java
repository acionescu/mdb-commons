package ro.zg.mdb.core.filter;

import java.util.Map;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.constraints.And;
import ro.zg.mdb.core.filter.constraints.Or;
import ro.zg.mdb.core.meta.FieldDataModel;
import ro.zg.mdb.core.meta.ObjectDataModel;

public class FieldConstraint<T> implements Constraint<T>, ObjectConstraint {
    private String fieldName;
    private Constraint<Object> constraint;

    public FieldConstraint(String fieldName, Constraint<Object> constraint) {
	super();
	this.fieldName = fieldName;
	this.constraint = constraint;
    }

    public boolean process(ObjectConstraintContext objectContext) throws MdbException {
	ObjectDataModel odm = objectContext.getObjectDataModel();
	FieldDataModel fdm = odm.getField(fieldName);
	if (!fdm.isIndexed()) {
	    return false;
	}
	FieldConstraintContext fieldContext = new FieldConstraintContext(fdm,constraint);
	process(fieldContext);
	objectContext.addFieldConstraintContext(fieldContext);
	return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Constraint<T> and(Constraint<T> c) {
	return new And<T>(this, c);
    }

    @Override
    public boolean isPossible(FieldDataModel fieldDataModel) {
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
    public boolean isPossible(ObjectDataModel objectDataModel) {
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
