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
package ro.zg.mdb.core.meta.persistence.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.constants.Constants;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.constants.OperationType;
import ro.zg.mdb.core.annotations.PersistentField;
import ro.zg.mdb.core.annotations.Implementation;
import ro.zg.mdb.core.annotations.Link;
import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.metadata.commons.ObjectMetadataImpl;
import ro.zg.metadata.exceptions.MetadataException;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.reflection.ReflectionUtility;

@Persistable
@PersistentField(name = "fields", allowedTypes= {PersistentFieldMetadataImpl.class})
public class PersistentObjectMetadataImpl<T> extends ObjectMetadataImpl<T, PersistentFieldMetadata<?>> implements
	PersistentObjectMetadata<T> {

    @Link(name = "object_type_object_id_field", lazy = false)
    private PersistentFieldMetadata<String> objectIdField;

    @Link(name = "object_type_required_fields", lazy = false)
    private Set<PersistentFieldMetadata<?>> requiredFields = new HashSet<PersistentFieldMetadata<?>>();

    @Link(name = "object_type_unique_indexes", key = "id", lazy = false)
    private Map<String, UniqueIndex> uniqueIndexes = new HashMap<String, UniqueIndex>();

    @Link(name = "object_type_indexed_fields", lazy = false)
    private Set<PersistentFieldMetadata<?>> indexedFields = new LinkedHashSet<PersistentFieldMetadata<?>>();

    // @Implementation(type = LinkedHashMap.class)
    // @Link(name = "object_type_fields", key = "name", lazy = false)
    // private Map<String, PersistentFieldMetadata<?>> fields;

    @Implementation(type = LinkedHashMap.class)
    private Map<String, Integer> fieldsPositions;

    @Implementation(type = LinkedHashSet.class)
    @Link(name = "object_type_linked_fields", lazy = false)
    private Set<PersistentFieldMetadata<?>> linkedFields = new LinkedHashSet<PersistentFieldMetadata<?>>();

    // @Link(name = "object_type_simple_fields", lazy = false)
    // private Set<PersistentFieldMetadata<?>> simpleFields = new LinkedHashSet<PersistentFieldMetadata<?>>();
    @Implementation(type = LinkedHashSet.class)
    private Set<String> simpleFields = new LinkedHashSet<String>();

    @Link(name = "object_type_references", lazy = false)
    private Set<LinkMetadata> references = new HashSet<LinkMetadata>();

    public PersistentObjectMetadataImpl() {
	super();
    }

    public PersistentObjectMetadataImpl(Class<T> type) {
	super(type);
    }

    private void initFields() {
	fieldsPositions = new LinkedHashMap<String, Integer>();
	fields = new LinkedHashMap<String, PersistentFieldMetadata<?>>();
    }

    public synchronized void addFieldMetadata(PersistentFieldMetadata<?> fdm) {
	if (fields == null) {
	    initFields();
	}
	String fieldName = fdm.getName();
	fields.put(fieldName, fdm);

	if (fdm.getLinkMetadata() != null) {
	    linkedFields.add(fdm);
	} else {
	    if (fieldName.equals(getObjectIdFieldName())) {
		return;
	    }
	    simpleFields.add(fieldName);
	    int pos = simpleFields.size() - 1;
	    fieldsPositions.put(fieldName, pos);
	    // fdm.setPosition(pos);
	}
    }

    public void addRequiredField(PersistentFieldMetadata<?> fdm) {
	requiredFields.add(fdm);
    }

    public void addUniqueIndex(UniqueIndex uniqueIndex) {
	uniqueIndexes.put(uniqueIndex.getName(), uniqueIndex);
    }

    public void addIndexedField(PersistentFieldMetadata<?> fdm) {
	indexedFields.add(fdm);
    }

    public void addUniqueField(String uniqueIndexName, PersistentFieldMetadata<?> fdm) {
	UniqueIndex index = uniqueIndexes.get(uniqueIndexName);
	if (index == null) {
	    index = new UniqueIndex();
	    index.setName(uniqueIndexName);
	    addUniqueIndex(index);
	}
	index.addField(fdm);
	requiredFields.add(fdm);
	addIndexedField(fdm);
    }

    public void addReference(LinkMetadata linkModel) {
	references.add(linkModel);
    }

    /**
     * Returns the complex {@link PersistentObjectMetadata} of a field or null if the field is simple or undefined
     * 
     * @param fieldName
     * @return
     */
    public PersistentObjectMetadata<?> getObjectDataModelForField(String fieldName) {
	// PersistentFieldMetadata<?> fdm = getField(fieldName);
	// if (fdm == null) {
	// return null;
	// }
	// Metadata<?> dm = fdm.getValueMetadata();
	// if (dm.isComplexType()) {
	// return (PersistentObjectMetadata<?>) dm;
	// }
	// return null;
	return (PersistentObjectMetadata<?>) super.getObjectDataModelForField(fieldName);
    }

    public void setObjectId(T target, String id) throws MdbException {
	if (objectIdField != null) {
	    try {
		setFieldValue(target, objectIdField.getName(), id);
	    } catch (MetadataException e) {
		throw new MdbException(MdbErrorType.GENERIC_ERROR, e);
	    }
	}
    }

    /**
     * This returns only the simple nested fields, not multivalued
     * 
     * @param filter
     * @param path
     * @return
     */
    public Set<PersistentFieldMetadata<?>> getNestedFieldsForFilter(Filter filter, String path) {
	Set<PersistentFieldMetadata<?>> nestedFields = new HashSet<PersistentFieldMetadata<?>>();

	/*
	 * in the case of an update we need to bring the data for all nested fields if, no target field was specified
	 */
	Set<String> targetFields = filter.getTargetFields();
	if (targetFields == null && OperationType.UPDATE.equals(filter.getOperationType())) {
	    return linkedFields;
	}

	boolean isMainField = "".equals(path);
	for (PersistentFieldMetadata<?> fdm : linkedFields) {
	    if (fdm.getValueMetadata().isMultivalued()) {
		continue;
	    }

	    String fullFieldName = fdm.getName();
	    if (!isMainField) {
		fullFieldName = path + Constants.NESTED_FIELD_SEPARATOR + fullFieldName;
	    }

	    if (filter.isFieldNeeded(fullFieldName) || (targetFields == null && !fdm.getLinkMetadata().isLazy())) {
		nestedFields.add(fdm);
	    }
	}

	return nestedFields;
    }

    public Map<String, PersistentFieldMetadata<?>> getMultivaluedNestedFieldsForFilter(Filter filter, String path) {
	Map<String, PersistentFieldMetadata<?>> nestedFields = new HashMap<String, PersistentFieldMetadata<?>>();
	Set<String> targetFields = filter.getTargetFields();
	boolean targetFieldsEmpty = (targetFields == null || targetFields.isEmpty());
	for (PersistentFieldMetadata<?> fdm : linkedFields) {
	    if (fdm.getValueMetadata().isMultivalued()) {

		boolean add = false;
		if (targetFieldsEmpty) {
		    if (!fdm.getLinkMetadata().isLazy()) {
			add = true;
		    }
		} else {
		    add = targetFields.contains(path + fdm.getName());
		}

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
    public Set<PersistentFieldMetadata<?>> getRequiredFields() {
	return requiredFields;
    }

    /**
     * @return the indexedFields
     */
    public Set<PersistentFieldMetadata<?>> getIndexedFields() {
	return indexedFields;
    }

    /**
     * @return the fields
     */
    public Map<String, PersistentFieldMetadata<?>> getFields() {
	return fields;
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
     * @return the references
     */
    public Set<LinkMetadata> getReferences() {
	return references;
    }

    /**
     * @return the objectIdField
     */
    public PersistentFieldMetadata<String> getObjectIdField() {
	return objectIdField;
    }

    public String getObjectIdFieldName() {
	if (objectIdField != null) {
	    return objectIdField.getName();
	}
	return null;
    }

    /**
     * @param objectIdField
     *            the objectIdField to set
     */
    public void setObjectIdField(PersistentFieldMetadata<String> objectIdField) {
	this.objectIdField = objectIdField;
    }

    // public T getObjectFromString(String data, Filter filter) throws MdbException {
    // if (data == null) {
    // return null;
    // }
    // T restored = null;
    // try {
    // restored = getType().newInstance();
    // } catch (Exception e) {
    // throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR,
    // new GenericNameValue("type", getType()));
    // }
    //
    // Map<String, Object> valuesMap = new HashMap<String, Object>();
    // String[] columns = data.split(Constants.COLUMN_SEPARATOR);
    // Set<String> constrainedFields = filter.getConstraintFields();
    // /* first get the constrained fields */
    // if (constrainedFields != null && !constrainedFields.isEmpty()) {
    // for (String fieldName : constrainedFields) {
    // valuesMap.put(fieldName, getValueForField(fields.get(fieldName), columns));
    // }
    // /* now do a check on the values map */
    // if (!filter.isSatisfied(valuesMap)) {
    // return null;
    // }
    // }
    // Set<String> targetFields = filter.getTargetFields();
    // if (targetFields == null || targetFields.isEmpty()) {
    // targetFields = fields.keySet();
    // }
    // boolean hasConstraints = constrainedFields != null && !constrainedFields.isEmpty();
    // /* populate the object with the target values */
    // for (String fieldName : targetFields) {
    // Object fieldValue = null;
    // PersistentFieldMetadata<?> fdm = fields.get(fieldName);
    // if (hasConstraints && constrainedFields.contains(fieldName)) {
    // fieldValue = valuesMap.get(fieldName);
    // } else {
    // fieldValue = getValueForField(fdm, columns);
    // }
    // if (fieldValue != null) {
    // addValueToObject(restored, fdm, fieldValue);
    // }
    //
    // }
    // return restored;
    // }

    // public T getObjectFromValuesMap(Map<String, Object> valuesMap, Collection<String> targetFields, String path,
    // TransactionContext transactionContext, String rowId) throws MdbException {
    // T restored = null;
    // try {
    // restored = getType().newInstance();
    // } catch (Exception e) {
    // throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR,
    // new GenericNameValue("type", getType()));
    // }
    //
    // transactionContext.addPendingObject(rowId, restored);
    //
    // boolean targetFieldsExist = targetFields != null && !targetFields.isEmpty();
    // /* populate the object with the target values */
    // for (String fieldName : fields.keySet()) {
    // String fullFieldName = path + fieldName;
    //
    // if (targetFieldsExist && !targetFields.contains(fullFieldName)) {
    // continue;
    // }
    // Object fieldValue = null;
    // PersistentFieldMetadata<?> fdm = fields.get(fieldName);
    // if (fdm.getDataModel().isMultivalued()) {
    // continue;
    // }
    //
    // if (fdm.getLinkModel() != null) {
    // String nestedRowId = transactionContext.getRowIdForPendingField(fullFieldName);
    // fieldValue = transactionContext.getPendingObject(nestedRowId);
    // if (fieldValue == null) {
    // fieldValue = getObjectFromValuesMap(valuesMap, targetFields, fullFieldName
    // + Constants.NESTED_FIELD_SEPARATOR, transactionContext, nestedRowId);
    // }
    // } else {
    // fieldValue = valuesMap.get(fullFieldName);
    // }
    //
    // if (fieldValue != null) {
    // addValueToObject(restored, fdm, fieldValue);
    // }
    //
    // }
    // return restored;
    // }

    public Map<String, Object> getDataMapFromString(String data, Map<String, Object> valuesMap, String path)
	    throws MdbException {
	if (data == null) {
	    return null;
	}
	if (valuesMap == null) {
	    valuesMap = new HashMap<String, Object>();
	}
	String[] columns = data.split(Constants.COLUMN_SEPARATOR);
	for (String fieldName : simpleFields) {
	    PersistentFieldMetadata<?> fdm = fields.get(fieldName);
	    if (fdm == null) {
		throw new RuntimeException("No field data model for " + fieldName + " on " + this);
	    }
	    String fullFieldName = path + fieldName;
	    try {
		valuesMap.put(fullFieldName, getValueForField(fdm, columns));
	    } catch (Exception e) {
		throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR, new GenericNameValue("class",
			getType()), new GenericNameValue("fullFieldName", fullFieldName));
	    }
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

    public String getObjectId(Object object) throws MdbException {
	if (objectIdField == null) {
	    return null;
	}
	try {
	    return (String) getValueForField((T) object, objectIdField.getName());
	} catch (MetadataException e) {
	    throw new MdbException(MdbErrorType.GENERIC_ERROR, e);
	}
    }

    public UniqueIndexValue getUniqueIndexValue(T target, String indexId) throws MdbException {
	UniqueIndex ui = getUniqueIndex(indexId);
	if (ui == null) {
	    return null;
	}
	if (ui.isComposite()) {
	    return getCompositeUniqueIndexValue(ui, target);
	}
	return getSimpleUniqueIndexValue(ui, target);
    }

    private UniqueIndexValue getCompositeUniqueIndexValue(UniqueIndex ui, T target) throws MdbException {
	UniqueIndexValue uiv = new UniqueIndexValue(ui.getName());
	for (PersistentFieldMetadata<?> fdm : ui.getFields()) {
	    Object fieldValue;
	    try {
		fieldValue = getValueForField(target, fdm.getName());
	    } catch (MetadataException e) {
		throw new MdbException(MdbErrorType.GENERIC_ERROR, e);
	    }
	    if (fieldValue == null) {
		return null;
	    }
	    uiv.addValue(fieldValue.toString());
	}
	return uiv;
    }

    private UniqueIndexValue getSimpleUniqueIndexValue(UniqueIndex ui, T target) throws MdbException {
	UniqueIndexValue uiv = null;
	for (PersistentFieldMetadata<?> fdm : ui.getFields()) {
	    String fieldName = fdm.getName();
	    Object fieldValue;
	    try {
		fieldValue = getValueForField(target, fieldName);
	    } catch (MetadataException e) {
		throw new MdbException(MdbErrorType.GENERIC_ERROR, e);
	    }
	    if (fieldValue == null) {
		return null;
	    }
	    uiv = new UniqueIndexValue(fdm.getName());
	    uiv.addValue(fieldValue.toString());
	}
	return uiv;
    }

    private Object getValueForField(PersistentFieldMetadata<?> fdm, String[] columns) throws MdbException {
	String fieldName = fdm.getName();
	int colIndex = -1;
	colIndex = fieldsPositions.get(fieldName);

	if (colIndex >= columns.length) {
	    return null;
	}
	String fieldData = columns[colIndex];

	try {
	    // return ReflectionUtility.createObjectByTypeAndValue(fdm.getType(), fieldData);
	    return fdm.createValueFromString(fieldData);
	} catch (Exception e) {
	    throw new MdbException(e, MdbErrorType.OBJECT_MATERIALIZATION_ERROR, new GenericNameValue("type", getType()
		    .getName()), new GenericNameValue("fieldName", fieldName));
	}
    }

    public Object getValueForField(String fieldName, String[] columns) throws MdbException {
	return getValueForField(fields.get(fieldName), columns);
    }

    public boolean addValueToObject(Object target, PersistentFieldMetadata<?> fdm, Object fieldValue)
	    throws MdbException {
	String fieldName = fdm.getName();
	try {
	    ReflectionUtility.setValueToField(target, fieldName, fieldValue);
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
    // for (PersistentFieldMetadata fdm : fields) {
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

    /**
     * @return the simpleFields
     */
    public Set<String> getSimpleFields() {
	return simpleFields;
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

    public Integer getFieldPosition(String fieldName) {
	return fieldsPositions.get(fieldName);
    }

    /**
     * @return the linkedFields
     */
    public Set<PersistentFieldMetadata<?>> getLinkedFields() {
	return linkedFields;
    }

    /**
     * @param requiredFields
     *            the requiredFields to set
     */
    public void setRequiredFields(Set<PersistentFieldMetadata<?>> requiredFields) {
	this.requiredFields = requiredFields;
    }

    /**
     * @param uniqueIndexes
     *            the uniqueIndexes to set
     */
    public void setUniqueIndexes(Map<String, UniqueIndex> uniqueIndexes) {
	this.uniqueIndexes = uniqueIndexes;
    }

    /**
     * @param indexedFields
     *            the indexedFields to set
     */
    public void setIndexedFields(Set<PersistentFieldMetadata<?>> indexedFields) {
	this.indexedFields = indexedFields;
    }

    /**
     * @param fields
     *            the fields to set
     */
    public void setFields(Map<String, PersistentFieldMetadata<?>> fields) {
	this.fields = fields;
	// matchFieldsWithFieldsPositions();
    }

    /**
     * @param fieldsPositions
     *            the fieldsPositions to set
     */
    public void setFieldsPositions(Map<String, Integer> fieldsPositions) {
	this.fieldsPositions = fieldsPositions;
	// matchFieldsWithFieldsPositions();
	matchSimpleFieldsWithFieldsPositions();
    }

    /**
     * @param linkedFields
     *            the linkedFields to set
     */
    public void setLinkedFields(Set<PersistentFieldMetadata<?>> linkedFields) {
	this.linkedFields = linkedFields;
    }

    /**
     * @param simpleFields
     *            the simpleFields to set
     */
    public void setSimpleFields(Set<String> simpleFields) {
	this.simpleFields = simpleFields;
	matchSimpleFieldsWithFieldsPositions();
    }

    /**
     * @param references
     *            the references to set
     */
    public void setReferences(Set<LinkMetadata> references) {
	this.references = references;
    }

    private void matchFieldsWithFieldsPositions() {
	if (fieldsPositions != null && fields != null) {
	    Map<String, PersistentFieldMetadata<?>> orderedFields = new LinkedHashMap<String, PersistentFieldMetadata<?>>();
	    for (String fieldName : fieldsPositions.keySet()) {
		orderedFields.put(fieldName, fields.get(fieldName));
	    }
	    for (PersistentFieldMetadata<?> fdm : fields.values()) {
		if (!fieldsPositions.containsKey(fdm.getName())) {

		}
	    }
	    fields = orderedFields;
	    // System.out.println("set fields to "+orderedFields);
	}
    }

    private void matchSimpleFieldsWithFieldsPositions() {
	// if (fieldsPositions != null && simpleFields != null) {
	// Set<String> orderedSimpleFields = new LinkedHashSet<String>();
	// for (String fieldName : fieldsPositions.keySet()) {
	// if (simpleFields.contains(fieldName)) {
	// orderedSimpleFields.add(fieldName);
	// }
	// }
	// simpleFields = orderedSimpleFields;
	// }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "PersistentObjectMetadata [objectIdField=" + objectIdField + ", requiredFields=" + requiredFields
		+ ", uniqueIndexes=" + uniqueIndexes + ", indexedFields=" + indexedFields + ", fields=" + fields
		+ ", fieldsPositions=" + fieldsPositions + ", linkedFields=" + linkedFields + ", simpleFields="
		+ simpleFields + ", references=" + references + "]";
    }

}
