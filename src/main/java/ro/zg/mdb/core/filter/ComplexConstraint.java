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
