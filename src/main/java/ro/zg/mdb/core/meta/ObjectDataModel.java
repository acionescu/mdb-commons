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
package ro.zg.mdb.core.meta;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ro.zg.commons.exceptions.ContextAwareException;
import ro.zg.mdb.constants.Constants;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.reflection.ReflectionUtility;

public class ObjectDataModel<T> {
    private Class<T> type;
    private String primaryKeyId = Constants.DEFAULT_PRIMARY_KEY_ID;
    private Set<FieldDataModel> requiredFields = new HashSet<FieldDataModel>();
    private Map<String, UniqueIndex> uniqueIndexes = new HashMap<String, UniqueIndex>();
    private Set<FieldDataModel> indexedFields = new LinkedHashSet<FieldDataModel>();
    /**
     * the fields with the position as a key
     */
    private Map<String, FieldDataModel> fields = new LinkedHashMap<String, FieldDataModel>();
    private Map<String, Integer> fieldsPositions = new HashMap<String, Integer>();

    public ObjectDataModel(Class<T> type) {
	this.type = type;
    }

    public synchronized void addFieldDataModel(FieldDataModel fdm) {
	String fieldName = fdm.getName();
	fields.put(fieldName, fdm);
	int pos = fields.size() - 1;
	fieldsPositions.put(fieldName, pos);
	fdm.setPosition(pos);
    }

    public void addRequiredField(FieldDataModel fdm) {
	requiredFields.add(fdm);
    }

    public void setPrimaryKey(UniqueIndex pk) {
	primaryKeyId = pk.getId();
	uniqueIndexes.put(primaryKeyId, pk);
    }

    public void addUniqueIndex(UniqueIndex uniqueIndex) {
	uniqueIndexes.put(uniqueIndex.getId(), uniqueIndex);
    }

    public void addIndexedField(FieldDataModel fdm) {
	indexedFields.add(fdm);
    }

    public void addPkField(FieldDataModel fdm) {
	addUniqueField(primaryKeyId, fdm);
    }

    public void addUniqueField(String id, FieldDataModel fdm) {
	UniqueIndex index = uniqueIndexes.get(id);
	if (index == null) {
	    index = new UniqueIndex();
	    index.setId(id);
	    addUniqueIndex(index);
	}
	index.addField(fdm);
	requiredFields.add(fdm);
	addIndexedField(fdm);
    }

    /**
     * @return the type
     */
    public Class<T> getType() {
	return type;
    }

    /**
     * @return the requiredFields
     */
    public Set<FieldDataModel> getRequiredFields() {
	return requiredFields;
    }

    /**
     * @return the indexedFields
     */
    public Set<FieldDataModel> getIndexedFields() {
	return indexedFields;
    }

    /**
     * @return the fields
     */
    public Map<String, FieldDataModel> getFields() {
	return fields;
    }

    public FieldDataModel getField(String name) {
	return fields.get(name);
    }

    public UniqueIndex getPrimaryKey() {
	return uniqueIndexes.get(primaryKeyId);
    }

    /**
     * @return the uniqueIndexes
     */
    public Map<String, UniqueIndex> getUniqueIndexes() {
	return uniqueIndexes;
    }

    public UniqueIndex getUniqueIndex(String id) {
	return uniqueIndexes.get(id);
    }

    /**
     * @return the primaryKeyId
     */
    public String getPrimaryKeyId() {
	return primaryKeyId;
    }

    public T getObjectFromString(String data, Filter filter) throws MdbException {
	if (data == null) {
	    return null;
	}
	T restored = null;
	try {
	    restored = type.newInstance();
	} catch (Exception e) {
	    throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR, new GenericNameValue("type", type));
	}

	Map<String, Object> valuesMap = new HashMap<String, Object>();
	String[] columns = data.split(Constants.COLUMN_SEPARATOR);
	Set<String> constrainedFields = filter.getConstraintFields();
	/* first get the constrained fields */
	if (constrainedFields != null && !constrainedFields.isEmpty()) {
	    for (String fieldName : constrainedFields) {
		valuesMap.put(fieldName, getValueForField(fields.get(fieldName), columns));
	    }
	    /* now do a check on the values map */
	    if (!filter.isSatisfied(valuesMap)) {
		return null;
	    }
	}
	Set<String> targetFields = filter.getTargetFields();
	if (targetFields == null || targetFields.isEmpty()) {
	    targetFields = fields.keySet();
	}
	boolean hasConstraints = constrainedFields != null && !constrainedFields.isEmpty();
	/* populate the object with the target values */
	for (String fieldName : targetFields) {
	    Object fieldValue = null;
	    FieldDataModel fdm = fields.get(fieldName);
	    if (hasConstraints && constrainedFields.contains(fieldName)) {
		fieldValue = valuesMap.get(fieldName);
	    } else {
		fieldValue = getValueForField(fdm, columns);
	    }
	    if (fieldValue != null) {
		addValueToObject(restored, fdm, fieldValue);
	    }

	}
	return restored;
    }

    private Object getValueForField(FieldDataModel fdm, String[] columns) throws MdbException {
	String fieldName = fdm.getName();
	String fieldType = fdm.getType().getSimpleName();
	int colIndex = fieldsPositions.get(fieldName);
	String fieldData = columns[colIndex];

	try {
	    return ReflectionUtility.createObjectByTypeAndValue(fieldType, fieldData);
	} catch (ContextAwareException e) {
	    throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR, new GenericNameValue("type", type
		    .getName()), new GenericNameValue("fieldName", fieldName));
	}
    }

    public Object getValueForField(String fieldName, String[] columns) throws MdbException {
	return getValueForField(fields.get(fieldName), columns);
    }

    private boolean addValueToObject(Object target, FieldDataModel fdm, Object fieldValue) throws MdbException {
	String fieldName = fdm.getName();
	try {
	    ReflectionUtility.setValueToField(target, type.getDeclaredField(fieldName), fieldValue);
	} catch (Exception e) {
	    throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR, new GenericNameValue("type", type
		    .getName()), new GenericNameValue("fieldName", fieldName));
	}
	return true;
    }

    // public ObjectContext getObjectContext(Object target) throws ObjectDataException {
    // Map<String, Object> valuesMap = getFieldValuesMap(target);
    //
    // Map<String, String> uniqueIndexesValues = new HashMap<String, String>();
    // Map<String, String> indexedValues = new HashMap<String, String>();
    // String data = "";
    // boolean addSeparator = false;
    //
    // for (FieldDataModel fdm : fields) {
    // String fieldName = fdm.getName();
    // Object fieldValue = valuesMap.get(fieldName);
    // /* convert value to string */
    // String fieldData = (fieldValue != null) ? fieldValue.toString() : null;
    //
    // /* test if the value is valid for this field */
    // try {
    // fdm.testValue(fieldData);
    // } catch (ObjectDataException ode) {
    // throw new ObjectDataException(ode.getErrorType(), type.getName(), fieldName, fieldData);
    // }
    // /* build unique indexes */
    // String uniqueIndexId = null;
    // if (fdm.isPrimaryKey()) {
    // uniqueIndexId = primaryKeyId;
    // } else {
    // uniqueIndexId = fdm.getUniqueIndex();
    // }
    // if (uniqueIndexId != null) {
    // appendValueToIndex(uniqueIndexId, fieldData, uniqueIndexesValues);
    // }
    //
    // /* build string representation */
    // if (addSeparator) {
    // data += Constants.COLUMN_SEPARATOR;
    // } else {
    // addSeparator = true;
    // }
    //
    // if (fieldData != null) {
    // data += fieldData;
    //
    // /* build reqular indexes */
    // if (fdm.isIndexed()) {
    // indexedValues.put(fieldName, fieldData);
    // }
    // }
    // }
    // /* build object context */
    // ObjectContext oc = new ObjectContext();
    // oc.setData(data);
    // oc.setIndexedValues(indexedValues);
    // oc.setUniqueIndexesValues(uniqueIndexesValues);
    //
    // return oc;
    // }
    //
    // private void appendValueToIndex(String indexId, Object value, Map<String, String> uniqueIndexesValues) {
    // String newValue = uniqueIndexesValues.get(indexId);
    // if (newValue == null) {
    // newValue = "";
    // } else {
    // newValue += Constants.INDEX_SEPARATOR;
    // }
    // newValue += value;
    // uniqueIndexesValues.put(indexId, newValue);
    // }

    public Map<String, Object> getFieldValuesMap(Object target) throws MdbException {
	Map<String, Object> valuesMap = new HashMap<String, Object>();
	Class<?> type = target.getClass();
	for (FieldDataModel fdm : fields.values()) {
	    String fieldName = fdm.getName();
	    try {
		valuesMap.put(fieldName, ReflectionUtility.getValueForField(target, fieldName));
	    } catch (Exception e) {
		throw new MdbException(e, MdbErrorType.GET_FIELD_ERROR, new GenericNameValue(type.getName(), fieldName));
	    }
	}
	return valuesMap;
    }

    public Collection<UniqueIndex> getTouchedUniqueIndexes(Collection<String> targetFields) {
	Set<UniqueIndex> hit = new HashSet<UniqueIndex>();
	for (UniqueIndex ui : uniqueIndexes.values()) {
	    for (String targetField : targetFields) {
		if (ui.containsField(targetField)) {
		    hit.add(ui);
		    continue;
		}
	    }
	}
	return hit;
    }

    public String getTypeName() {
	return type.getName();
    }

    /**
     * @return the fieldsPositions
     */
    public Map<String, Integer> getFieldsPositions() {
	return fieldsPositions;
    }

    public void setFieldValue(Object target, String fieldName, String fieldValue) throws MdbException {
	try {
	    ReflectionUtility.setValueToField(target, fieldName, fieldValue);
	} catch (Exception e) {
	    throw new MdbException(e, MdbErrorType.SET_FIELD_ERROR, new GenericNameValue(fieldName, fieldValue));
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return super.toString()+"[type=" + type + ", fields=" + fields + ", requiredFields=" + requiredFields
		+ ", uniqueIndexes=" + uniqueIndexes + ", indexedFields=" + indexedFields + "]";
    }

}
