package ro.zg.mdb.core.meta.data;

public class MultivaluedDataModel<M,T> extends DataModel<T>{
    protected Class<? extends M> multivaluedType;
    protected boolean collection;
    protected boolean map;

    public MultivaluedDataModel(Class<T> type, Class<? extends M> multivaluedType) {
	super(type, true);
	this.multivalued=true;
	this.multivaluedType = multivaluedType;
    }

    /**
     * @return the collection
     */
    public boolean isCollection() {
        return collection;
    }

    /**
     * @return the map
     */
    public boolean isMap() {
        return map;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    /**
     * @param map the map to set
     */
    public void setMap(boolean map) {
        this.map = map;
    }
    
    
}
