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

public abstract class ComplexConstraint<T> implements Constraint<T>{
    private Constraint<T> compiledValue;
    boolean isCompiled;
    
    protected abstract Constraint<T> compile();
    
    public abstract void addConstraint(Constraint<T> constraint);

    /**
     * @return the compiledValue
     */
    public Constraint<T> getCompiledValue() {
	if(!isCompiled) {
	    isCompiled=true;
	    compiledValue=compile();
	}
        return compiledValue;
    }
    
}
