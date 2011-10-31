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
    
    
    
}
