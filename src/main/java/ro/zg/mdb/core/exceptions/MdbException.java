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
