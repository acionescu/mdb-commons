package ro.zg.mdb.core.meta;

public class DataModel<T> {
    private Class<T> type;
    private boolean complexType;
    
    
    public DataModel(Class<T> type, boolean complexType) {
	super();
	this.type = type;
	this.complexType = complexType;
    }


    public DataModel(Class<T> type) {
	super();
	this.type = type;
    }


    /**
     * @return the type
     */
    public Class<T> getType() {
        return type;
    }


    /**
     * @return the complexType
     */
    public boolean isComplexType() {
        return complexType;
    }
    
    public String getTypeName() {
	return type.getName();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "DataModel [type=" + type + ", complexType=" + complexType + "]";
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (complexType ? 1231 : 1237);
	result = prime * result + ((type == null) ? 0 : type.hashCode());
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
	DataModel other = (DataModel) obj;
	if (complexType != other.complexType)
	    return false;
	if (type == null) {
	    if (other.type != null)
		return false;
	} else if (!type.equals(other.type))
	    return false;
	return true;
    }
    
    
    
}
