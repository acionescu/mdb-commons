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
