package ro.zg.mdb.core.meta.data;

import java.util.Map;

public class MapDataModel<K,T> extends MultivaluedDataModel<Map<K,T>, T>{
    private Class<K> keyType;
    
    public MapDataModel(Class<K> keyType, Class<T> valueType, Class<? extends Map<K, T>> multivaluedType) {
	super(valueType, multivaluedType);
	this.keyType=keyType;
	this.map=true;
    }

    /**
     * @return the keyType
     */
    public Class<K> getKeyType() {
        return keyType;
    }
    
    
}
