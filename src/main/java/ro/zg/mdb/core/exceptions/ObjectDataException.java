package ro.zg.mdb.core.exceptions;

import ro.zg.mdb.constants.MdbErrorType;

public class ObjectDataException extends Exception{

    /**
     * 
     */
    private static final long serialVersionUID = 3511536683111946661L;
    
    private MdbErrorType errorType;
    private String objectType;
    private String fieldName;
    private Object fieldValue;
    
    public ObjectDataException(String objectType, String fieldName, Object fieldValue) {
	super();
	this.objectType = objectType;
	this.fieldName = fieldName;
	this.fieldValue = fieldValue;
    }

    public ObjectDataException(String objectType, String fieldName, Object fieldValue, Throwable cause) {
	super(cause);
	this.objectType = objectType;
	this.fieldName = fieldName;
	this.fieldValue = fieldValue;
    }

    public ObjectDataException(MdbErrorType errorType, String objectType, String fieldName, Object fieldValue) {
	super();
	this.errorType = errorType;
	this.objectType = objectType;
	this.fieldName = fieldName;
	this.fieldValue = fieldValue;
    }

    public ObjectDataException(MdbErrorType errorType, String fieldName, Object fieldValue) {
	super();
	this.errorType = errorType;
	this.fieldName = fieldName;
	this.fieldValue = fieldValue;
    }

    
    
    /**
     * @return the errorType
     */
    public MdbErrorType getErrorType() {
        return errorType;
    }

    /**
     * @return the objectType
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @return the fieldValue
     */
    public Object getFieldValue() {
        return fieldValue;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return this.getClass().getName()+" [objectType=" + objectType + ", errorType=" + errorType + ", fieldName=" + fieldName
		+ ", fieldValue=" + fieldValue + "]";
    }
    
}
