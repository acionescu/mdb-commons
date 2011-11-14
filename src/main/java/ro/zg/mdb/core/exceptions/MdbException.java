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
package ro.zg.mdb.core.exceptions;

import java.util.Arrays;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.util.data.GenericNameValue;

public class MdbException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = -2622927659817507562L;

     private MdbErrorType errorType;
    private GenericNameValue[] params;
    
    public MdbException(MdbErrorType errorType, GenericNameValue... params) {
	this.errorType=errorType;
	this.params=params;
    }
    
    public MdbException(Throwable cause, MdbErrorType errorType, GenericNameValue... params) {
	super(cause);
	this.errorType=errorType;
	this.params=params;
    }
    
    

    public MdbException(MdbErrorType errorType, Throwable cause) {
	super(cause);
	this.errorType = errorType;
    }

    /**
     * @return the errorType
     */
    public MdbErrorType getErrorType() {
        return errorType;
    }

    /**
     * @return the params
     */
    public GenericNameValue[] getParams() {
        return params;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return errorType + "[" + Arrays.toString(params) + "]";
    }
    
}
