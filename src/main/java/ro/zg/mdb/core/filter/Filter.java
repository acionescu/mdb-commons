package ro.zg.mdb.core.filter;

import java.util.Map;
import java.util.Set;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.constraints.All;
import ro.zg.mdb.core.meta.ObjectDataModel;

public class Filter implements ObjectConstraint{
    private ObjectConstraint constraint=new All<Object>();
    private Set<String> targetFields;
    private Set<String> constraintFields;
    

    public Filter() {
	super();
    }

    public Filter(ObjectConstraint constraint, Set<String> constraintFields) {
	super();
	this.constraint = constraint;
	this.constraintFields = constraintFields;
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
    public boolean process(ObjectConstraintContext objectContext) throws MdbException {
	return constraint.process(objectContext);
    }

    @Override
    public boolean isPossible(ObjectDataModel objectDataModel) {
	return constraint.isPossible(objectDataModel);
    }

    /**
     * @param targetFields the targetFields to set
     */
    public void setTargetFields(Set<String> targetFields) {
        this.targetFields = targetFields;
    }
    
}
