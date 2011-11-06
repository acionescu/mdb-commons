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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.constants.Constants;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.constants.SpecialPaths;
import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.concurrency.PersistableObjectLockManager;
import ro.zg.mdb.core.concurrency.ResourceLock;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.FieldConstraintContext;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.filter.constraints.Range;
import ro.zg.mdb.core.schema.ObjectContext;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;
import ro.zg.mdb.util.MdbObjectHandler;
import ro.zg.mdb.util.MdbObjectSetHandler;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.ObjectsUtil;
import ro.zg.util.io.file.LineHandler;

public class PersistentObjectDataManager extends PersistentDataManager {
    private PersistableObjectLockManager locksManager;

    public PersistentObjectDataManager(PersistenceManager persistenceManager, PersistableObjectLockManager locksManager) {
	super(persistenceManager);
	this.locksManager = locksManager;
    }

    protected String getPath(String p1, String p2) {
	return persistenceManager.getPath(p1, p2);
    }

    protected String getPath(String... paths) {
	return persistenceManager.getPath(paths);
    }

    protected String getRowsPathForType(String typeName) {
	return getPath(typeName, SpecialPaths.ROWS);
    }

    protected String getRowPath(String typeName, String rowId) {
	return getPath(getRowsPathForType(typeName), rowId);
    }

    protected String getIndexPath(String typeName, String indexName) {
	return getPath(typeName, SpecialPaths.INDEXES, indexName);
    }

    protected String getCompositeIndexPath(String typeName, String indexName) {
	return getPath(typeName, SpecialPaths.COMPOSITE_INDEXES, indexName);
    }

    protected Collection<String> read(String id, final Collection<String> data) throws MdbException {
	try {
	    persistenceManager.read(id, new LineHandler() {

		@Override
		public boolean handle(String line) {
		    data.add(line);
		    return true;
		}
	    });
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
	return data;
    }

    protected Collection<String> read(String id) throws MdbException {
	final Set<String> data = new LinkedHashSet<String>();
	return read(id, data);
    }

    protected <T> Collection<T> readAllObjects(final ObjectDataModel<T> odm, final Filter filter,
	    final Collection<T> dataHolder) throws MdbException {

	MdbObjectSetHandler handler = new MdbObjectSetHandler() {
	    private ResourceLock rowLock;

	    @Override
	    public boolean handle(String line) {
		data = line;
		return false;
	    }

	    @Override
	    public boolean startFile(String name) {
		rowLock = locksManager.getRowLock(name);
		rowLock.acquireReadLock();
		return true;
	    }

	    @Override
	    public boolean endFile(String name) {
		rowLock.releaseReadLock();
		locksManager.releaseRowLock(rowLock);
		T object;
		try {
		    object = odm.getObjectFromString(data, filter);
		} catch (MdbException e) {
		    error = e;
		    return false;
		}
		if (object != null) {
		    dataHolder.add(object);
		}
		return true;
	    }
	};
	String typeName = odm.getTypeName();
	try {
	    persistenceManager.readAllChildren(getRowsPathForType(typeName), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}
	return dataHolder;
    }

    protected <T> Collection<T> readAllObjectsBut(final ObjectDataModel<T> odm, final Filter filter,
	    final Collection<T> dataHolder, final Collection<String> restricted) throws MdbException {

	MdbObjectSetHandler handler = new MdbObjectSetHandler() {
	    private ResourceLock rowLock;

	    @Override
	    public boolean handle(String line) {
		data = line;
		return false;
	    }

	    @Override
	    public boolean startFile(String name) {
		boolean process = !restricted.contains(name);
		if (process) {
		    rowLock = locksManager.getRowLock(name);
		    rowLock.acquireReadLock();
		}
		return process;
	    }

	    @Override
	    public boolean endFile(String name) {
		rowLock.releaseReadLock();
		locksManager.releaseRowLock(rowLock);
		T object;
		try {
		    object = odm.getObjectFromString(data, filter);
		} catch (MdbException e) {
		    error = e;
		    return false;
		}
		if (object != null) {
		    dataHolder.add(object);
		}
		return true;
	    }
	};
	String typeName = odm.getTypeName();
	try {
	    persistenceManager.readAllChildren(getRowsPathForType(typeName), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}

	return dataHolder;
    }

    protected <T> Collection<T> readObjects(Collection<String> ids, final ObjectDataModel<T> odm, final Filter filter,
	    final Collection<T> dataHolder) throws MdbException {

	MdbObjectHandler handler = new MdbObjectHandler() {

	    @Override
	    public boolean handle(String line) {
		data = line;
		/* read only one line */
		return false;
	    }
	};
	String typeName = odm.getTypeName();
	for (String id : ids) {
	    ResourceLock rowLock = locksManager.getRowLock(id);
	    rowLock.acquireReadLock();
	    try {

		persistenceManager.read(getRowPath(typeName, id), handler);

		T object = odm.getObjectFromString(handler.getData(), filter);
		if (object != null) {
		    dataHolder.add(object);
		}
	    } catch (PersistenceException e) {
		throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	    } catch (Exception e) {
		throw new RuntimeException("Read error: " + rowLock, e);
	    } finally {
		rowLock.releaseReadLock();
		locksManager.releaseRowLock(rowLock);
	    }
	}
	if (handler.hasError()) {
	    throw handler.getError();
	}

	return dataHolder;
    }

    protected <T> void deleteUnusedIndexes(String typeName, ObjectContext<T> oc, String rowId) {
	Map<String, String> oldIndexedValues = oc.getOldIndexedValues();
	if (oldIndexedValues.size() > 0) {
	    // System.out.println(rowId + ": deleting unique indexes " + oc.getOldUniqueValues());
	    for (UniqueIndexValue uiv : oc.getOldUniqueValues().values()) {
		deleteUniqueIndex(typeName, uiv, rowId);
		if (!uiv.isComposite()) {
		    oldIndexedValues.remove(uiv.getName());
		}
	    }
	    // System.out.println("deleting indexes " + oldIndexedValues);
	    for (Map.Entry<String, String> e : oldIndexedValues.entrySet()) {
		deleteIndex(typeName, e.getKey(), e.getValue(), rowId);
	    }
	}
    }

    protected <T> boolean delete(String typeName, ObjectContext<T> oc, String rowId) {
	// System.out.println("start delete for row "+rowId);
	deleteUnusedIndexes(typeName, oc, rowId);
	// System.out.println("deleting "+rowId);
	return persistenceManager.delete(getRowPath(typeName, rowId));
    }

    protected <T> long deleteAll(final ObjectDataModel<T> odm, final Filter filter) throws MdbException {
	final String typeName = odm.getTypeName();
	MdbObjectSetHandler handler = new MdbObjectSetHandler() {
	    private ResourceLock rowLock;

	    @Override
	    public boolean handle(String line) {
		data = line;
		return true;
	    }

	    @Override
	    public boolean startFile(String name) {
		rowLock = locksManager.getRowLock(name);
		rowLock.acquireWriteLock();
		return true;
	    }

	    @Override
	    public boolean endFile(String name) {
		ObjectContext<T> oc = null;
		boolean deleted = false;
		try {
		    oc = getObjectContextFromString(odm, data, filter);
		    if (oc != null) {
			deleted = delete(typeName, oc, name);
		    }
		} catch (MdbException e) {
		    error = e;
		    return false;
		} finally {
		    rowLock.releaseWriteLock();
		    locksManager.releaseRowLock(rowLock);
		}

		if (deleted) {
		    rowCount++;
		}

		return true;
	    }
	};

	try {
	    persistenceManager.readAllChildren(getRowsPathForType(typeName), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}
	return handler.getRowCount();
    }

    protected <T> long deleteAllBut(final ObjectDataModel<T> odm, final Filter filter,
	    final Collection<String> restricted) throws MdbException {
	final String typeName = odm.getTypeName();
	MdbObjectSetHandler handler = new MdbObjectSetHandler() {
	    private ResourceLock rowLock;

	    @Override
	    public boolean handle(String line) {
		data = line;
		return true;
	    }

	    @Override
	    public boolean startFile(String name) {
		if (!restricted.contains(name)) {
		    rowLock = locksManager.getRowLock(name);
		    rowLock.acquireWriteLock();
		    return true;
		}
		return false;
	    }

	    @Override
	    public boolean endFile(String name) {
		ObjectContext<T> oc = null;
		boolean deleted = false;
		try {
		    oc = getObjectContextFromString(odm, data, filter);
		    if (oc != null) {
			deleted = delete(typeName, oc, name);
		    }
		} catch (MdbException e) {
		    error = e;
		    return false;
		} finally {
		    rowLock.releaseWriteLock();
		    locksManager.releaseRowLock(rowLock);
		}

		if (deleted) {
		    rowCount++;
		}
		return true;
	    }
	};

	try {
	    persistenceManager.readAllChildren(getRowsPathForType(typeName), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}
	return handler.getRowCount();
    }

    protected <T> long deleteObjects(Collection<String> ids, final ObjectDataModel<T> odm, final Filter filter)
	    throws MdbException {
	final String typeName = odm.getTypeName();

	MdbObjectHandler handler = new MdbObjectHandler() {

	    @Override
	    public boolean handle(String line) {
		data = line;
		return true;
	    }
	};
	long rowsCount = 0;
	for (String id : ids) {
	    ResourceLock rowLock = locksManager.getRowLock(id);
	    rowLock.acquireWriteLock();
	    boolean deleted = false;
	    try {
		persistenceManager.read(getRowPath(typeName, id), handler);

		ObjectContext<T> oc = getObjectContextFromString(odm, handler.getData(), filter);
		if (oc != null) {
		    deleted = delete(typeName, oc, id);
		}
	    } catch (PersistenceException e) {
		throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	    } catch (Exception e) {
		throw new RuntimeException("Delete error: " + rowLock, e);
	    } finally {
		rowLock.releaseWriteLock();
		locksManager.releaseRowLock(rowLock);
	    }
	    if (deleted) {
		rowsCount++;
	    }
	}
	if (handler.hasError()) {
	    throw handler.getError();
	}

	return rowsCount;
    }

    public <F extends Comparable<F>> Collection<String> getRowsForRange(String typeName, FieldConstraintContext<F> fcc,
	    Collection<String> data) throws MdbException {
	String indexPath = getIndexPath(typeName, fcc.getFieldName());
	Range<F> range = fcc.getRange();
	
	Map<String, String> elligiblePaths = new HashMap<String, String>();
	elligiblePaths.put(indexPath, "");
	F minValue = range.getMinValue();
	F maxValue = range.getMaxValue();
	boolean inRange = true;

	while ((elligiblePaths.size() > 0) && inRange) {
	    for (Map.Entry<String, String> pe : elligiblePaths.entrySet()) {
		String currentPath = pe.getKey();
		Map<String, String> nextPaths = new HashMap<String, String>();
		String[] nextVals;
		try {
		    nextVals = persistenceManager.listChildren(currentPath);
		} catch (PersistenceException e) {
		    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
		}
		for (String nextVal : nextVals) {
		    if (nextVal.length() > 1) {
			continue;
		    }
		    String digit = nextVal;
		    if (",".equals(digit)) {
			digit = ".";
		    }
		    String valuePath = pe.getValue() + digit;
		    String nextPath = getPath(currentPath, nextVal);
		    // BigDecimal value = new BigDecimal(valuePath);
		    F value = range.fromString(valuePath);

		    int minComp = 1;
		    int maxComp = -1;
		    inRange = ((minValue == null) || ((minComp = value.compareTo(minValue)) >= 0))
			    && ((maxValue == null) || ((maxComp = value.compareTo(maxValue)) <= 0));
		    /* the value is in range */
		    if (inRange) {
			if (minComp > 0 && maxComp < 0) {
			    nextPaths.put(nextPath, valuePath);
			}

			if (fcc.getConstraint().isSatisfied(value)) {
			    readIndexRows(getIndexRowsPath(nextPath), data);
			}
		    }
		}
		elligiblePaths = nextPaths;
	    }
	   
	}
	return data;
    }

    private Collection<String> readIndexRows(String indexRowsPath, Collection<String> data) throws MdbException {
	if (data == null) {
	    data = new HashSet<String>();
	}
	String[] rows;
	ResourceLock indexLock = locksManager.getIndexLock(indexRowsPath);
	indexLock.acquireReadLock();
	try {
	    rows = persistenceManager.listChildren(indexRowsPath);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	} finally {
	    indexLock.releaseReadLock();
	    locksManager.releaseIndexLock(indexLock);
	}
	data.addAll(Arrays.asList(rows));
	return data;
    }

    protected boolean indexValueExists(String typeName, String indexName, String value) throws MdbException {
	try {
	    return persistenceManager.exists(getPathForIndex(typeName, indexName, value));
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
    }

    protected boolean compositeIndexValueExists(String typeName, String indexName, String value) throws MdbException {
	try {
	    return persistenceManager.exists(getPath(getCompositeIndexPath(typeName, indexName), value));
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
    }

    public Collection<String> getRowsForValues(String typeName, FieldConstraintContext<?> fcc, Set<String> data)
	    throws MdbException {
	String indexName = fcc.getFieldName();
	for (String value : fcc.getValues()) {
	    String path = getPathForIndex(typeName, indexName, value);
	    readIndexRows(path, data);
	}
	return data;
    }
    
    private <T> String getRowIdForUniqueIndex(String typeName, UniqueIndexValue uiv) throws MdbException {
	String indexPath = getIndexValuePath(typeName, uiv);
	Collection<String> rowsIds=getRowsForValue(typeName, indexPath, uiv.getValue());
	int count=rowsIds.size();
	if(count < 1) {
	    return null;
	}
	if(count==1) {
	    return new ArrayList<String>(rowsIds).get(0);
	}
	throw new MdbException(MdbErrorType.DUPLICATE_UNIQUE_VALUE, new GenericNameValue("typeName",typeName),new GenericNameValue("index",uiv.getName()),new GenericNameValue("value",uiv.getValue()));
    }

    protected Collection<String> getRowsForValue(String typeName, String indexName, String value) throws MdbException {
	String path = getPathForIndex(typeName, indexName, value);
	return readIndexRows(path, null);
    }

    private String getPathForIndex(String typeName, String indexName, String value) {
	String path = getIndexPath(typeName, indexName);
	for (int i = 0; i < value.length(); i++) {
	    path = persistenceManager.getPath(path, String.valueOf(value.charAt(i)));
	}
	return getIndexRowsPath(path);
    }

    protected String getIndexRowsPath(String indexValuePath) {
	return getPath(indexValuePath, SpecialPaths.INDEX_ROWS);
    }

    protected void addIndex(String typeName, String indexName, String indexValue, String rowId) throws MdbException {
	String indexPath = getPathForIndex(typeName, indexName, indexValue);
	String indexRowPath = getPath(indexPath, rowId);

	ResourceLock indexLock = locksManager.getIndexLock(indexPath);
	indexLock.acquireWriteLock();
	try {
	    // System.out.println("add index "+indexRowPath);
	    persistenceManager.create(indexRowPath);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	} finally {
	    indexLock.releaseWriteLock();
	    locksManager.releaseIndexLock(indexLock);
	}
    }

    protected boolean addUniqueIndex(String typeName, String indexName, String indexValue, String rowId)
	    throws MdbException {
	String indexPath = getPathForIndex(typeName, indexName, indexValue);
	String indexRowPath = getPath(indexPath, rowId);

	try {
	    // if (persistenceManager.exists(indexPath)) {
	    // System.out.println("tried to create unique index but already existed: " + indexPath);
	    // }
	    // System.out.println("add unique index " + indexRowPath);
	    boolean created = persistenceManager.create(indexRowPath);
	    // if (!persistenceManager.exists(indexPath)) {
	    // System.out.println("created index " + indexPath + " but it's not there");
	    // }
	    return created;
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
    }

    protected boolean deleteIndex(String typeName, String indexName, String indexValue, String rowId) {
	String indexPath = getPathForIndex(typeName, indexName, indexValue);
	String indexRowPath = getPath(indexPath, rowId);
	ResourceLock indexLock = locksManager.getIndexLock(indexPath);
	indexLock.acquireWriteLock();
	boolean deleted = persistenceManager.delete(indexRowPath);
	indexLock.releaseWriteLock();
	locksManager.releaseIndexLock(indexLock);
	return deleted;
    }

    protected void addCompositeIndex(String typeName, String indexName, String indexValue, String rowId)
	    throws MdbException {
	String indexPath = getCompositeIndexPath(typeName, indexName);
	String indexValuePath = getPath(indexPath, indexValue);
	try {
	    persistenceManager.write(indexValuePath, rowId);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
    }

    protected boolean deleteUniqueIndex(String typeName, UniqueIndexValue uiv, String rowId) {
	String indexValuePath = getIndexValuePath(typeName, uiv);

	ResourceLock indexLock = locksManager.getIndexLock(indexValuePath);
	indexLock.acquireWriteLock();
	// try {
	// Collection<String> rows = readIndexRows(indexValuePath, null);
	// if (!rows.contains(rowId) || rows.size() > 1) {
	// System.out.println("tried to delete unique index " + indexValuePath + " for row " + rowId
	// + " but found " + rows);
	// }
	// } catch (MdbException e) {
	// e.printStackTrace();
	// }
	// System.out.println("delete unique " + indexValuePath);
	boolean deleted = persistenceManager.delete(indexValuePath);

	indexLock.releaseWriteLock();
	locksManager.releaseIndexLock(indexLock);
	return deleted;
    }

    public <T> ObjectContext<T> getObjectContext(T target, ObjectDataModel<T> odm, SchemaContext schemaContext)
	    throws MdbException {
	Class<?> type = target.getClass();
	Map<String, Object> valuesMap = odm.getFieldValuesMap(target);
	Map<String, String> indexedValues = new HashMap<String, String>();
	Map<String, UniqueIndexValue> uniqueValues = new HashMap<String, UniqueIndexValue>();
	Map<String, ObjectContext<?>> nestedObjectContexts=new HashMap<String, ObjectContext<?>>();
	String data = "";
	boolean addSeparator = false;

	for (FieldDataModel<?> fdm : odm.getFields().values()) {
	    String fieldName = fdm.getName();
	    Object fieldValue = valuesMap.get(fieldName);
	    /* convert value to string */
	    String fieldData = null;

	    /* see if this is a link */
	    LinkModel linkModel = fdm.getLinkModel();
	    boolean isLink = linkModel != null;

	    if (fieldValue == null) {
		if (schemaContext != null) {
		    String sequenceId = fdm.getSequenceId();
		    if (sequenceId != null) {
			fieldData = "" + schemaContext.getNextValForSequence(sequenceId);
			odm.setFieldValue(target, fieldName, fieldData);
		    }
		}
	    } else {
		if (isLink) {
		    /*
		     * if the linked object already exists, get the rowId, otherwise create an ObjectContext for it
		     */
		    if(fieldValue != null) {
			nestedObjectContexts.put(fieldName, getNestedObjectContext(fieldValue, (ObjectDataModel<Object>)fdm.getDataModel(), schemaContext));
		    }
		    
		    continue;
		}
		fieldData = fieldValue.toString();
	    }

	    /* test if the value is valid for this field */
	    try {
		fdm.testValue(fieldData);
	    } catch (MdbException ode) {
		throw new MdbException(MdbErrorType.REQUIRED, new GenericNameValue("type", type.getName()),
			new GenericNameValue(fieldName, fieldData));
	    }

	    String uniqueIndexName = fdm.getUniqueIndexId();
	    UniqueIndex uniqueIndex = null;
	    if (uniqueIndexName != null) {
		uniqueIndex = odm.getUniqueIndex(uniqueIndexName);
		if (uniqueIndex.isComposite()) {
		    addUniqueIndex(uniqueIndexName, fieldData, uniqueValues);
		} else {
		    addUniqueIndex(fieldName, fieldData, uniqueValues);
		}
	    }

	    /* add reqular indexes, if no unique index exists or exists but it's composite */
	    if (fdm.isIndexed() && (uniqueIndex == null || uniqueIndex.isComposite())) {
		indexedValues.put(fieldName, fieldData);
	    }

	    /* build string representation */
	    if (addSeparator) {
		data += Constants.COLUMN_SEPARATOR;
	    } else {
		addSeparator = true;
	    }

	    if (fieldData != null) {
		data += fieldData;
	    }

	}
	/* build object context */

	// TODO: remove this
	// ObjectContext<T> oc = new ObjectContext<T>();
	// oc.setData(data);
	// oc.setIndexedValues(indexedValues);
	// oc.setUniqueValues(uniqueValues);
	// oc.setObjectDataModel(odm);
	// return oc;

	return new ObjectContext<T>(odm, data, indexedValues, uniqueValues);
    }

    /**
     * {@link ObjectContext} builder for update
     * 
     * @param target
     * @param odm
     * @param filter
     * @param rawOldData
     * @return
     * @throws MdbException
     */
    protected <T> ObjectContext<T> getObjectContext(T target, ObjectDataModel<T> odm, Filter filter, String rawOldData,
	    String rowId) throws MdbException {
	if (rawOldData == null) {
	    return null;
	}
	Class<?> type = target.getClass();
	Map<String, Object> oldValuesMap = new HashMap<String, Object>();
	String[] oldData = rawOldData.split(Constants.COLUMN_SEPARATOR);

	/* first check if the oldData satisfies the filter */
	Set<String> constrainedFields = filter.getConstraintFields();
	/* first get the constrained fields */
	if (constrainedFields != null && !constrainedFields.isEmpty()) {
	    for (String fieldName : constrainedFields) {
		oldValuesMap.put(fieldName, odm.getValueForField(fieldName, oldData));
	    }
	    /* now do a check on the values map */
	    if (!filter.isSatisfied(oldValuesMap)) {
		return null;
	    }
	}
	/* if the filter is satisfied, let's get the data for the updated fields */

	Map<String, String> indexedValues = new HashMap<String, String>();
	Map<String, UniqueIndexValue> uniqueValues = new HashMap<String, UniqueIndexValue>();

	Map<String, String> outdatedIndexValues = new HashMap<String, String>();
	Map<String, UniqueIndexValue> outdatedUniqueValues = new HashMap<String, UniqueIndexValue>();

	Map<String, Object> newValuesMap = odm.getFieldValuesMap(target);

	Set<String> targetFields = filter.getTargetFields();
	Set<String> changedFields = new HashSet<String>();

	String[] newData = new String[oldData.length];
	System.arraycopy(oldData, 0, newData, 0, oldData.length);

	if (targetFields == null || targetFields.size() == 0) {
	    /* if no fields were specified assume all */
	    targetFields = odm.getFields().keySet();
	}

	for (String fieldName : targetFields) {
	    FieldDataModel fdm = odm.getField(fieldName);
	    int position = fdm.getPosition();
	    String fieldData = "";

	    Object fieldValue = newValuesMap.get(fieldName);
	    /* convert value to string */
	    if (fieldValue != null) {
		fieldData = fieldValue.toString();
	    }
	    String oldVal = oldData[position];

	    if (ObjectsUtil.areEqual(fieldData, oldVal)) {
		/* if the value of this field hasn't changed there's nothing more to do */
		continue;
	    }
	    changedFields.add(fieldName);

	    /* test if the value is valid for this field */
	    try {
		fdm.testValue(fieldData);
	    } catch (MdbException ode) {
		throw new MdbException(MdbErrorType.REQUIRED, new GenericNameValue("type", type.getName()),
			new GenericNameValue(fieldName, fieldData));
	    }

	    /* update the new data with this field value */
	    newData[position] = fieldData;

	    /* build reqular indexes */
	    if (fdm.isIndexed()) {
		indexedValues.put(fieldName, fieldData);
		outdatedIndexValues.put(fieldName, oldVal);
	    }
	}

	if (changedFields.size() == 0) {
	    /* nothing changed */
	    return null;
	}

	/* ok now we should see how many unique indexes have changed */
	Collection<UniqueIndex> touchedUniqueIndexes = odm.getTouchedUniqueIndexes(changedFields);

	/*
	 * for each composite index that has changed we need to compute the new value and the old value that is going to
	 * be deleted
	 */

	for (UniqueIndex ui : touchedUniqueIndexes) {
	    for (FieldDataModel fdm : ui.getFields()) {
		String uniqueIndexName = ui.getId();
		int position = fdm.getPosition();
		if (odm.getUniqueIndex(uniqueIndexName).isComposite()) {
		    addUniqueIndex(uniqueIndexName, newData[position], uniqueValues);
		    addUniqueIndex(uniqueIndexName, oldData[position], outdatedUniqueValues);
		} else {
		    String fieldName = fdm.getName();
		    addUniqueIndex(fieldName, newData[position], uniqueValues);
		    addUniqueIndex(fieldName, oldData[position], outdatedUniqueValues);
		    /* remove unique indexes from regular indexes */
		    indexedValues.remove(fieldName);
		    outdatedIndexValues.remove(fieldName);
		}
	    }
	}

	String data = "";
	boolean addSeparator = false;
	for (int i = 0; i < newData.length; i++) {
	    String fieldData = newData[i];
	    if (addSeparator) {
		data += Constants.COLUMN_SEPARATOR;
	    } else {
		addSeparator = true;
	    }

	    if (fieldData != null) {
		data += fieldData;
	    }
	}

	/* build object context */

	// TODO: remove this
	// ObjectContext<T> oc = new ObjectContext<T>();
	// oc.setData(data);
	// oc.setIndexedValues(indexedValues);
	// oc.setUniqueValues(uniqueValues);
	// oc.setObjectDataModel(odm);
	// oc.setOldIndexedValues(outdatedIndexValues);
	// oc.setOldUniqueValues(outdatedUniqueValues);
	// return oc;

	return new ObjectContext<T>(odm, data, indexedValues, uniqueValues, outdatedIndexValues, outdatedUniqueValues,
		rowId);

    }

    public <T> ObjectContext<T> getObjectContextFromString(ObjectDataModel<T> odm, String data, Filter filter)
	    throws MdbException {
	if (data == null) {
	    return null;
	}
	Map<String, Object> valuesMap = new HashMap<String, Object>();
	String[] columns = data.split(Constants.COLUMN_SEPARATOR);
	Set<String> constrainedFields = filter.getConstraintFields();
	/* first get the constrained fields */
	if (constrainedFields != null && !constrainedFields.isEmpty()) {
	    for (String fieldName : constrainedFields) {
		valuesMap.put(fieldName, odm.getValueForField(fieldName, columns));
	    }
	    /* now do a check on the values map */
	    if (!filter.isSatisfied(valuesMap)) {
		// System.out.println(data+" is not satisfying "+filter.getConstraint());
		return null;
	    }
	}
	Map<String, String> indexedValues = new HashMap<String, String>();
	Map<String, UniqueIndexValue> uniqueValues = new HashMap<String, UniqueIndexValue>();

	for (FieldDataModel<?> fdm : odm.getIndexedFields()) {
	    String fieldName = fdm.getName();
	    int position = fdm.getPosition();
	    String fieldData = columns[position];

	    indexedValues.put(fieldName, fieldData);
	    String uniqueIndexName = fdm.getUniqueIndexId();
	    UniqueIndex uniqueIndex = null;
	    if (uniqueIndexName != null) {
		uniqueIndex = odm.getUniqueIndex(uniqueIndexName);
		if (uniqueIndex.isComposite()) {
		    addUniqueIndex(uniqueIndexName, fieldData, uniqueValues);
		} else {
		    addUniqueIndex(fieldName, fieldData, uniqueValues);
		}
	    }
	    /* add reqular indexes, if no unique index exists or exists but it's composite */
	    if (uniqueIndex == null || uniqueIndex.isComposite()) {
		indexedValues.put(fieldName, fieldData);
	    }
	}

	// ObjectContext<T> oc = new ObjectContext<T>();
	// oc.setOldIndexedValues(indexedValues);
	// oc.setOldUniqueValues(uniqueValues);
	// return oc;

	return new ObjectContext<T>(odm, indexedValues, uniqueValues);
    }
    
    private <N> ObjectContext<N> getNestedObjectContext(N target, ObjectDataModel<N> odm, SchemaContext schemaContext) throws MdbException{
	
	/* check if an object with the specified pk already exists */
	UniqueIndexValue pkValue = odm.getUniqueIndexValue(target, odm.getPrimaryKeyId());
	String rowId=getRowIdForUniqueIndex(odm.getTypeName(), pkValue);
	if(rowId != null) {
	    return new ObjectContext<N>(odm, rowId);
	}
	
	return getObjectContext(target, odm, schemaContext);
	
    }

    private void addUniqueIndex(String indexName, String value, Map<String, UniqueIndexValue> indexesValues) {
	UniqueIndexValue uiv = indexesValues.get(indexName);
	if (uiv == null) {
	    uiv = new UniqueIndexValue(indexName);
	    indexesValues.put(indexName, uiv);
	}
	uiv.addValue(value);
    }

    protected void testUniqueIndexes(String typeName, Collection<UniqueIndexValue> uniqueIndexesValues)
	    throws MdbException {

	for (UniqueIndexValue uiv : uniqueIndexesValues) {
	    boolean exists = false;
	    if (uiv.isComposite()) {
		exists = compositeIndexValueExists(typeName, uiv.getName(), uiv.getValue());
	    } else {
		exists = indexValueExists(typeName, uiv.getName(), uiv.getValue());
	    }
	    if (exists) {
		throw new MdbException(MdbErrorType.UNIQUENESS_VIOLATED, new GenericNameValue(uiv.getName(), uiv));
	    }
	}
    }

    protected <T> Long updateAll(final T source, final ObjectDataModel<T> odm, final Filter filter) throws MdbException {
	final String typeName = odm.getTypeName();
	MdbObjectSetHandler handler = new MdbObjectSetHandler() {
	    private ResourceLock rowLock;

	    @Override
	    public boolean endFile(String name) {
		ObjectContext<T> oc = null;
		try {
		    oc = getObjectContext(source, odm, filter, data, name);
		    if (oc != null) {
			// oc.setRowIdProvider(new StaticRowIdProvider(name));
			oc.setRowLock(rowLock);
			boolean updated = update(oc, name);
			if (updated) {
			    rowCount++;
			}
		    } else {
			rowLock.releaseWriteLock();
			locksManager.releaseRowLock(rowLock);
		    }
		} catch (MdbException e) {
		    error = e;
		    return false;
		} finally {
		    if (oc == null) {
			rowLock.releaseWriteLock();
			locksManager.releaseRowLock(rowLock);
		    }
		}
		return true;
	    }

	    @Override
	    public boolean startFile(String name) {
		rowLock = locksManager.getRowLock(name);
		rowLock.acquireWriteLock();
		return true;
	    }

	    @Override
	    public boolean handle(String line) {
		data = line;
		return true;
	    }

	};
	try {
	    persistenceManager.readAllChildren(getRowsPathForType(typeName), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}

	return handler.getRowCount();
    }

    protected <T> Long updateAllBut(final T source, final ObjectDataModel<T> odm, final Filter filter,
	    final Collection<String> restricted) throws MdbException {
	final String typeName = odm.getTypeName();
	MdbObjectSetHandler handler = new MdbObjectSetHandler() {
	    private ResourceLock rowLock;

	    @Override
	    public boolean endFile(String name) {
		ObjectContext<T> oc = null;
		try {
		    oc = getObjectContext(source, odm, filter, data, name);
		    if (oc != null) {
			// oc.setRowIdProvider(new StaticRowIdProvider(name));
			oc.setRowLock(rowLock);
			boolean updated = update(oc, name);
			if (updated) {
			    rowCount++;
			}
		    }
		} catch (MdbException e) {
		    error = e;
		    return false;
		} finally {
		    if (oc == null) {
			rowLock.releaseWriteLock();
			locksManager.releaseRowLock(rowLock);
		    }
		}
		return true;
	    }

	    @Override
	    public boolean startFile(String name) {
		boolean process = !restricted.contains(name);
		if (process) {
		    rowLock = locksManager.getRowLock(name);
		    rowLock.acquireWriteLock();
		}
		return process;
	    }

	    @Override
	    public boolean handle(String line) {
		data = line;
		return true;
	    }

	};
	try {
	    persistenceManager.readAllChildren(getRowsPathForType(typeName), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}

	return handler.getRowCount();
    }

    protected <T> Long updateObjects(Collection<String> ids, T source, ObjectDataModel<T> odm, Filter filter)
	    throws MdbException {
	final String typeName = odm.getTypeName();
	MdbObjectHandler handler = new MdbObjectHandler() {

	    @Override
	    public boolean handle(String line) {
		data = line;
		return true;
	    }
	};
	long rowCount = 0;
	for (String id : ids) {
	    ResourceLock rowLock = locksManager.getRowLock(id);
	    rowLock.acquireWriteLock();
	    ObjectContext<T> oc = null;
	    try {
		persistenceManager.read(getRowPath(typeName, id), handler);
		oc = getObjectContext(source, odm, filter, handler.getData(), id);
		if (oc != null) {
		    // oc.setRowIdProvider(new StaticRowIdProvider(id));
		    oc.setRowLock(rowLock);
		    boolean updated = update(oc, id);
		    if (updated) {
			rowCount++;
		    }
		}

	    } catch (PersistenceException e) {
		throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	    } catch (MdbException e) {
		throw new MdbException(e, MdbErrorType.UPDATE_ERROR, new GenericNameValue("rowId", id),
			new GenericNameValue("oldData", handler.getData()), new GenericNameValue("newData",
				oc.getData()), new GenericNameValue("src", source));
	    } finally {
		if (oc == null) {
		    rowLock.releaseWriteLock();
		    locksManager.releaseRowLock(rowLock);
		}
	    }
	}
	if (handler.hasError()) {
	    throw handler.getError();
	}

	return rowCount;
    }

    protected <T> T create(T target, ObjectDataModel<T> odm, SchemaContext schemaContext) throws MdbException {
	Class<?> type = target.getClass();
	Persistable persistenceInfo = type.getAnnotation(Persistable.class);
	if (persistenceInfo == null) {
	    throw new RuntimeException("Object " + target
		    + " is not persistable. Must be annotaded with 'ro.zg.mdb.core.annotations.Persistable'");
	}

	/* get the object context */
	ObjectContext<T> oc = getObjectContext(target, odm, schemaContext);
	save(oc);

	return target;
    }

    protected <T> boolean update(ObjectContext<T> oc, String rowId) throws MdbException {
	save(oc);
	deleteUnusedIndexes(oc.getObjectDataModel().getTypeName(), oc, rowId);
	return true;
    }

    protected <T> void save(ObjectContext<T> objectContext) throws MdbException {
	ObjectDataModel<T> odm = objectContext.getObjectDataModel();
	String objectName = objectContext.getObjectName();

	/* assure there is no duplication for unique indexes */
	Collection<UniqueIndexValue> uniqueValues = objectContext.getUniqueValues().values();
	Collection<ResourceLock> uniqueValuesLocks = null;

	String rowId = null;
	/* hold the unique values */
	synchronized (odm) {
	    uniqueValuesLocks = lockUniqueValues(objectName, uniqueValues);
	    // rowId = objectContext.getRowIdProvider().provideRowId();
	    rowId = objectContext.getRowInfo().getRowId();
	}

	// System.out.println("start saving "+rowId);
	try {
	    testUniqueIndexes(objectName, uniqueValues);
	    // System.out.println(Thread.currentThread().getId() + " passed testing for " + uniqueValues);
	} catch (MdbException e) {
	    /* in case the test fails release the locks */
	    releaseUniqueValuesLocks(uniqueValuesLocks);
	    throw e;
	}

	ResourceLock rowLock = objectContext.getRowLock();
	if (rowLock == null) {
	    rowLock = locksManager.getRowLock(rowId);
	    rowLock.acquireWriteLock();
	}

	try {
	    /* create the actual data record */

	    try {
		// System.out.println("saving "+objectContext.getData()+" for "+ typeName+" at row "+rowId);
		saveData(objectName, objectContext.getData(), rowId);
	    } catch (PersistenceException e) {
		throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	    } finally {
		rowLock.releaseWriteLock();
		locksManager.releaseRowLock(rowLock);
	    }

	    /* create unique indexes */
	    for (UniqueIndexValue uiv : objectContext.getUniqueValues().values()) {
		if (uiv.isComposite()) {
		    addCompositeIndex(objectName, uiv.getName(), uiv.getValue(), rowId);
		} else {
		    addUniqueIndex(objectName, uiv.getName(), uiv.getValue(), rowId);
		}
	    }
	    // System.out.println(Thread.currentThread().getId() + " created " +
	    // objectContext.getUniqueValues().values());
	    /* create indexes */
	    for (Map.Entry<String, String> e : objectContext.getIndexedValues().entrySet()) {
		addIndex(objectName, e.getKey(), e.getValue(), rowId);
	    }
	} finally {
	    releaseUniqueValuesLocks(uniqueValuesLocks);
	}

    }

    protected Collection<ResourceLock> lockUniqueValues(String typeName, Collection<UniqueIndexValue> uniqueValues) {
	Collection<ResourceLock> locks = new HashSet<ResourceLock>();
	for (UniqueIndexValue uiv : uniqueValues) {
	    locks.add(lockUniqueValue(typeName, uiv));
	}
	return locks;
    }

    protected void releaseUniqueValuesLocks(Collection<ResourceLock> locks) {
	for (ResourceLock lock : locks) {
	    lock.releaseWriteLock();
	    locksManager.releaseIndexLock(lock);
	    // System.out.println(Thread.currentThread().getId() + " released index lock " + lock.getResourceId());
	}
    }

    protected ResourceLock lockUniqueValue(String typeName, UniqueIndexValue uiv) {
	String indexValuePath = getIndexValuePath(typeName, uiv);
	ResourceLock indexLock = locksManager.getIndexLock(indexValuePath);
	indexLock.acquireWriteLock();
	// System.out.println(Thread.currentThread().getId() + " locked index " + indexValuePath);
	return indexLock;
    }
    

    protected void saveData(String typeName, String data, String rowId) throws PersistenceException {
	persistenceManager.write(getRowPath(typeName, rowId), data);
    }

    protected String getIndexValuePath(String typeName, UniqueIndexValue uiv) {
	String indexValuePath = null;
	if (uiv.isComposite()) {
	    String indexPath = getCompositeIndexPath(typeName, uiv.getName());
	    indexValuePath = getPath(indexPath, uiv.getValue());
	} else {
	    indexValuePath = getPathForIndex(typeName, uiv.getName(), uiv.getValue());
	}
	return indexValuePath;
    }

}
