package ro.zg.mdb.core.meta.persistence.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.metadata.commons.ObjectMetadata;

public interface PersistentObjectMetadata<T> extends ObjectMetadata<T, PersistentFieldMetadata<?>> {

    PersistentObjectMetadata<?> getObjectDataModelForField(String fieldName);

    PersistentFieldMetadata<?> getObjectIdField();

    Map<String, UniqueIndex> getUniqueIndexes();

    Set<PersistentFieldMetadata<?>> getIndexedFields();

    Map<String, Integer> getFieldsPositions();

    Set<PersistentFieldMetadata<?>> getLinkedFields();

    Set<String> getSimpleFields();

    Set<LinkMetadata> getReferences();

    String getObjectId(Object object) throws MdbException;

    void setObjectId(T target, String id) throws MdbException;

    String getObjectIdFieldName();

    UniqueIndex getUniqueIndex(String id);

    Integer getFieldPosition(String fieldName);

    Collection<UniqueIndex> getTouchedUniqueIndexes(Collection<String> targetFields);

    Map<String, Object> getDataMapFromString(String data, Map<String, Object> valuesMap, String path)
	    throws MdbException;

    Set<PersistentFieldMetadata<?>> getNestedFieldsForFilter(Filter filter, String path);
    
    Map<String, PersistentFieldMetadata<?>> getMultivaluedNestedFieldsForFilter(Filter filter, String path);

    void populateComplexFields(T target, String fieldName, Collection<?> values) throws MdbException;
    
    boolean addValueToObject(Object target, PersistentFieldMetadata<?> fdm, Object fieldValue) throws MdbException;
}
