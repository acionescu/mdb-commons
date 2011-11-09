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

import ro.zg.mdb.core.meta.data.FieldDataModel;


public interface Constraint<T> {
    /**
     * Checks if the specified value satisfies this constraint
     * @param value
     * @return true if the value is allowd, false otherwise
     */
    boolean isSatisfied(T value);
    /**
     * Checks if the specified constraint is logically possible
     * @param context
     * @return true if possible, false otherwise
     */
    boolean isPossible(FieldDataModel fieldDataModel);
    
    /**
     * Processes the possible values that a constraint allowes
     * and does also some checks if such a value is at all possible
     * with the current {@link FieldDataModel}
     * @param context
     * @return - true if there is a posibility for this constrain to be satisfied, false otherwise
     */
    boolean process(FieldConstraintContext context);
    
    Constraint<T> and(Constraint<T> c);
    
    Constraint<T> or(Constraint<T> c);
    
    Constraint<T> not();
    
}
