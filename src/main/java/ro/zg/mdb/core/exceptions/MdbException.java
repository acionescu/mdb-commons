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
