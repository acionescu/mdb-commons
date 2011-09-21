package ro.zg.mdb.core.schema;

import java.util.Map;

import ro.zg.mdb.core.concurrency.ResourceLock;
import ro.zg.mdb.core.meta.ObjectDataModel;
import ro.zg.mdb.core.meta.UniqueIndexValue;
import ro.zg.mdb.util.RowIdProvider;

public class ObjectContext<T> {
    private RowIdProvider rowIdProvider;
    private ResourceLock rowLock;
    private ObjectDataModel<T> objectDataModel;
    private Map<String, String> indexedValues;
    private Map<String,UniqueIndexValue> uniqueValues;
    private Map<String, String> oldIndexedValues;
    private Map<String, UniqueIndexValue> oldUniqueValues;
    private String data;
    /**
     * @return the indexedValues
     */
    public Map<String, String> getIndexedValues() {
        return indexedValues;
    }
    /**
     * @return the data
     */
    public String getData() {
        return data;
    }
    /**
     * @param indexedValues the indexedValues to set
     */
    public void setIndexedValues(Map<String, String> indexedValues) {
        this.indexedValues = indexedValues;
    }
    /**
     * @param data the data to set
     */
    public void setData(String data) {
        this.data = data;
    }
    /**
     * @return the objectDataModel
     */
    public ObjectDataModel<T> getObjectDataModel() {
        return objectDataModel;
    }
    /**
     * @param objectDataModel the objectDataModel to set
     */
    public void setObjectDataModel(ObjectDataModel<T> objectDataModel) {
        this.objectDataModel = objectDataModel;
    }
    /**
     * @return the uniqueValues
     */
    public Map<String, UniqueIndexValue> getUniqueValues() {
        return uniqueValues;
    }
    /**
     * @param uniqueValues the uniqueValues to set
     */
    public void setUniqueValues(Map<String, UniqueIndexValue> uniqueValues) {
        this.uniqueValues = uniqueValues;
    }
    
   
    /**
     * @return the oldIndexedValues
     */
    public Map<String, String> getOldIndexedValues() {
        return oldIndexedValues;
    }
    /**
     * @return the oldUniqueValues
     */
    public Map<String, UniqueIndexValue> getOldUniqueValues() {
        return oldUniqueValues;
    }
    /**
     * @param oldIndexedValues the oldIndexedValues to set
     */
    public void setOldIndexedValues(Map<String, String> oldIndexedValues) {
        this.oldIndexedValues = oldIndexedValues;
    }
    /**
     * @param oldUniqueValues the oldUniqueValues to set
     */
    public void setOldUniqueValues(Map<String, UniqueIndexValue> oldUniqueValues) {
        this.oldUniqueValues = oldUniqueValues;
    }
    /**
     * @return the rowIdProvider
     */
    public RowIdProvider getRowIdProvider() {
        return rowIdProvider;
    }
    /**
     * @return the rowLock
     */
    public ResourceLock getRowLock() {
        return rowLock;
    }
    /**
     * @param rowIdProvider the rowIdProvider to set
     */
    public void setRowIdProvider(RowIdProvider rowIdProvider) {
        this.rowIdProvider = rowIdProvider;
    }
    /**
     * @param rowLock the rowLock to set
     */
    public void setRowLock(ResourceLock rowLock) {
        this.rowLock = rowLock;
    }
    
}
