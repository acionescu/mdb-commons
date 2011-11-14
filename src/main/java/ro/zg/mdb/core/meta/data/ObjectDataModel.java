/*******************************************************************************
 * Copyright (c) 2011 Adrian Cristian Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Adrian Cristian Ionescu - initial API and implementation
 ******************************************************************************/
package ro.zg.mdb.core.meta.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.NestingKind;

import ro.zg.commons.exceptions.ContextAwareException;
import ro.zg.mdb.constants.Constants;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.meta.TransactionContext;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.reflection.ReflectionUtility;

public class ObjectDataModel<T> extends DataModel<T> {
    private String primaryKeyId = Constants.DEFAULT_PRIMARY_KEY_ID;
    private Set<FieldDataModel<?>> requiredFields = new HashSet<FieldDataModel<?>>();
    private Map<String, UniqueIndex> uniqueIndexes = new HashMap<String, UniqueIndex>();
    private Set<FieldDataModel<?>> indexedFields = new LinkedHashSet<FieldDataModel<?>>();
    /**
     * the fields with the position as a key
     */
    private Map<String, FieldDataModel<?>> fields = new LinkedHashMap<String, FieldDataModel<?>>();
    private Map<String, Integer> fieldsPositions = new HashMap<String, Integer>();
    private Set<FieldDataModel<?>> linkedFields = new LinkedHashSet<FieldDataModel<?>>();

    public ObjectDataModel(Class<T> type) {
	super(type, true);
    }

    public synchronized void addFieldDataModel(FieldDataModel<?> fdm) {
	String fieldName = fdm.getName();
	fields.put(fieldName, fdm);
	int pos = fields.size() - 1;
	fieldsPositions.put(fieldName, pos);
	fdm.setPosition(pos);
	if (fdm.getLinkModel() != null) {
	    linkedFields.add(fdm);
	}
    }

    public void addRequiredField(FieldDataModel<?> fdm) {
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
     * Returns the complex {@link ObjectDataModel} of a field or null if the field is simple or undefined
     * 
     * @param fieldName
     * @return
     */
    public ObjectDataModel<?> getObjectDataModelForField(String fieldName) {
	FieldDataModel<?> fdm = getField(fieldName);
	if (fdm == null) {
	    return null;
	}
	DataModel<?> dm = fdm.getDataModel();
	if (dm.isComplexType()) {
	    return (ObjectDataModel<?>) dm;
	}
	return null;
    }

    /**
     * This returns only the simple nested fields, not multivalued
     * 
     * @param filter
     * @param path
     * @return
     */
    public Map<String, FieldDataModel<?>> getNestedFieldsForFilter(Filter filter, String path) {
	Map<String, FieldDataModel<?>> nestedFields = new HashMap<String, FieldDataModel<?>>();
	Set<String> targetFields = filter.getTargetFields();
	boolean targetFieldsEmpty = (targetFields == null || targetFields.isEmpty());
	for (FieldDataModel<?> fdm : linkedFields) {
	    if (fdm.getDataModel().isMultivalued()) {
		continue;
	    }
	    boolean add = false;
	    if (targetFieldsEmpty) {
		if (!fdm.getLinkModel().isLazy()) {
		    add = true;
		}
	    } else {
		add = targetFields.contains(path + fdm.getName());
	    }

	    if (!add) {
		add = filter.getConstraintFields().contains(path + Constants.NESTED_FIELD_SEPARATOR + fdm.getName());
	    }

	    if (add) {
		nestedFields.put(fdm.getName(), fdm);
	    }
	}

	return nestedFields;
    }

    public Map<String, FieldDataModel<?>> getMultivaluedNestedFieldsForFilter(Filter filter, String path) {
	Map<String, FieldDataModel<?>> nestedFields = new HashMap<String, FieldDataModel<?>>();
	Set<String> targetFields = filter.getTargetFields();
	boolean targetFieldsEmpty = (targetFields == null || targetFields.isEmpty());
	for (FieldDataModel<?> fdm : linkedFields) {
	    if (fdm.getDataModel().isMultivalued()) {

		boolean add = false;
		if (targetFieldsEmpty) {
		    if (!fdm.getLinkModel().isLazy()) {
			add = true;
		    }
		} else {
		    add = targetFields.contains(path + fdm.getName());
		}

		// if (!add) {
		// add = filter.getConstraintFields()
		// .contains(path + Constants.NESTED_FIELD_SEPARATOR + fdm.getName());
		// }

		if (add) {
		    nestedFields.put(fdm.getName(), fdm);
		}
	    }
	}

	return nestedFields;
    }

    /**
     * @return the requiredFields
     */
    public Set<FieldDataModel<?>> getRequiredFields() {
	return requiredFields;
    }

    /**
     * @return the indexedFields
     */
    public Set<FieldDataModel<?>> getIndexedFields() {
	return indexedFields;
    }

    /**
     * @return the fields
     */
    public Map<String, FieldDataModel<?>> getFields() {
	return fields;
    }

    public FieldDataModel<?> getField(String name) {

	int nestedIndex = name.indexOf(Constants.NESTED_FIELD_SEPARATOR);
	if (nestedIndex <= 0 || nestedIndex == (name.length() - 1)) {
	    return fields.get(name);
	}
	String mainField = name.substring(0, nestedIndex);
	FieldDataModel<?> currentField = fields.get(mainField);
	if (currentField != null) {
	    return ((ObjectDataModel<?>) currentField.getDataModel()).getField(name.substring(nestedIndex + 1));
	}
	return null;
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
	    restored = getType().newInstance();
	} catch (Exception e) {
	    throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR,
		    new GenericNameValue("type", getType()));
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

    public T getObjectFromValuesMap(Map<String, Object> valuesMap, Collection<String> targetFields, String path,
	    TransactionContext transactionContext, String rowId) throws MdbException {
	T restored = null;
	try {
	    restored = getType().newInstance();
	} catch (Exception e) {
	    throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR,
		    new GenericNameValue("type", getType()));
	}

	transactionContext.addPendingObject(rowId, restored);

	boolean targetFieldsExist = targetFields != null && !targetFields.isEmpty();
	/* populate the object with the target values */
	for (String fieldName : fields.keySet()) {
	    String fullFieldName = path + fieldName;

	    if (targetFieldsExist && !targetFields.contains(fullFieldName)) {
		continue;
	    }
	    Object fieldValue = null;
	    FieldDataModel<?> fdm = fields.get(fieldName);
	    if (fdm.getDataModel().isMultivalued()) {
		continue;
	    }

	    if (fdm.getLinkModel() != null) {
		String nestedRowId = transactionContext.getRowIdForPendingField(fullFieldName);
		fieldValue = transactionContext.getPendingObject(nestedRowId);
		if (fieldValue == null) {
		    fieldValue = getObjectFromValuesMap(valuesMap, targetFields, fullFieldName
			    + Constants.NESTED_FIELD_SEPARATOR,transactionContext, nestedRowId);
		}
	    } else {
		fieldValue = valuesMap.get(fullFieldName);
	    }

	    if (fieldValue != null) {
		addValueToObject(restored, fdm, fieldValue);
	    }

	}
	return restored;
    }

    public Map<String, Object> getDataMapFromString(String data, Map<String, Object> valuesMap, String path)
	    throws MdbException {
	if (valuesMap == null) {
	    valuesMap = new HashMap<String, Object>();
	}
	String[] columns = data.split(Constants.COLUMN_SEPARATOR);
	for (FieldDataModel<?> fdm : fields.values()) {
	    String fieldName = fdm.getName();
	    valuesMap.put(path + fieldName, getValueForField(fdm, columns));
	}
	return valuesMap;
    }

    public void populateComplexFields(T target, String fieldName, Collection<?> values) throws MdbException {
	Object value = getField(fieldName).createFromValue(values);
	try {
	    ReflectionUtility.setValueToField(target, fieldName, value);
	} catch (Exception e) {
	    throw new MdbException(e, MdbErrorType.GET_FIELD_ERROR, new GenericNameValue("name", fieldName),
		    new GenericNameValue("value", value));
	}
    }

    public UniqueIndexValue getUniqueIndexValue(T target, String indexId) throws MdbException {
	UniqueIndex ui = getUniqueIndex(indexId);
	if (ui == null) {
	    return null;
	}
	UniqueIndexValue uiv = new UniqueIndexValue(indexId);
	for (FieldDataModel<?> fdm : ui.getFields()) {
	    Object fieldValue = getValueForField(target, fdm.getName());
	    if (fieldValue == null) {
		return null;
	    }
	    uiv.addValue(fieldValue.toString());
	}
	return uiv;
    }

    private Object getValueForField(FieldDataModel<?> fdm, String[] columns) throws MdbException {
	String fieldName = fdm.getName();
	String fieldType = fdm.getType().getSimpleName();
	int colIndex = fieldsPositions.get(fieldName);
	String fieldData = columns[colIndex];

	try {
	    return ReflectionUtility.createObjectByTypeAndValue(fieldType, fieldData);
	} catch (ContextAwareException e) {
	    throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR, new GenericNameValue("type", getType()
		    .getName()), new GenericNameValue("fieldName", fieldName));
	}
    }

    public Object getValueForField(T target, String fieldName) throws MdbException {
	try {
	    return ReflectionUtility.getValueForField(target, fieldName);
	} catch (Exception e) {
	    throw new MdbException(e, MdbErrorType.GET_FIELD_ERROR, new GenericNameValue("field", fieldName),
		    new GenericNameValue("target", target));
	}
    }

    public Object getValueForField(String fieldName, String[] columns) throws MdbException {
	return getValueForField(fields.get(fieldName), columns);
    }

    private boolean addValueToObject(Object target, FieldDataModel fdm, Object fieldValue) throws MdbException {
	String fieldName = fdm.getName();
	try {
	    ReflectionUtility.setValueToField(target, getType().getDeclaredField(fieldName), fieldValue);
	} catch (Exception e) {
	    throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR, new GenericNameValue("type", getType()
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

    public Map<String, Object> getFieldValuesMap(T target) throws MdbException {
	Map<String, Object> valuesMap = new HashMap<String, Object>();
	for (FieldDataModel<?> fdm : fields.values()) {
	    String fieldName = fdm.getName();
	    try {
		valuesMap.put(fieldName, getValueForField(target, fieldName));
	    } catch (Exception e) {

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

    /**
     * @return the linkedFields
     */
    public Set<FieldDataModel<?>> getLinkedFields() {
	return linkedFields;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return super.toString() + "[fields=" + fields + ", requiredFields=" + requiredFields + ", uniqueIndexes="
		+ uniqueIndexes + ", indexedFields=" + indexedFields + "]";
    }

}
