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
package ro.zg.mdb.core.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.LinkModel;
import ro.zg.mdb.core.meta.data.LinkValue;
import ro.zg.mdb.core.meta.data.ObjectDataModel;
import ro.zg.mdb.core.meta.data.ObjectsLink;
import ro.zg.mdb.core.meta.data.UniqueIndex;
import ro.zg.mdb.core.meta.data.UniqueIndexValue;
import ro.zg.mdb.core.schema.ObjectContext;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;
import ro.zg.mdb.util.MdbObjectHandler;
import ro.zg.mdb.util.MdbObjectSetHandler;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.ObjectsUtil;
import ro.zg.util.io.file.LineHandler;

public class PersistentObjectDataManager<T> extends PersistentDataManager {
    private PersistableObjectLockManager locksManager = new PersistableObjectLockManager();
    private ObjectDataModel<T> objectDataModel;
    private String objectName;

    public PersistentObjectDataManager(PersistenceManager persistenceManager, ObjectDataModel<T> objectDataModel,
	    String objectName) {
	super(persistenceManager);
	this.objectDataModel = objectDataModel;
	this.objectName = objectName;
    }

    protected String getPath(String p1, String p2) {
	return persistenceManager.getPath(p1, p2);
    }

    protected String getPath(String... paths) {
	return persistenceManager.getPath(paths);
    }

    protected String getRowsDirPath() {
	return getPath(SpecialPaths.ROWS);
    }

    protected String getRowPath(String rowId) {
	return getPath(getRowsDirPath(), rowId);
    }

    protected String getIndexPath(String indexName) {
	return getPath(SpecialPaths.INDEXES, indexName);
    }

    protected String getCompositeIndexPath(String indexName) {
	return getPath(SpecialPaths.COMPOSITE_INDEXES, indexName);
    }

    protected boolean rowExists(String rowId) throws MdbException {
	try {
	    return persistenceManager.exists(getRowPath(rowId));
	} catch (PersistenceException e) {
	    throw new MdbException(e, MdbErrorType.PERSISTENCE_ERROR);
	}
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

    public String readData(String id) throws MdbException {
	MdbObjectHandler handler = new MdbObjectHandler() {

	    @Override
	    public boolean handle(String line) {
		data = line;
		/* read only one line */
		return false;
	    }
	};

	ResourceLock rowLock = locksManager.getRowLock(id);
	rowLock.acquireReadLock();
	try {

	    persistenceManager.read(getRowPath(id), handler);

	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	} catch (Exception e) {
	    throw new RuntimeException("Read error: " + rowLock, e);
	} finally {
	    rowLock.releaseReadLock();
	    locksManager.releaseRowLock(rowLock);
	}

	return handler.getData();
    }

    public Collection<T> readAllObjects(final Filter filter, final TransactionManager transactionManager,
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
		    // object = odm.getObjectFromString(data, filter);
		    object = transactionManager.buildObject(objectName, objectDataModel, data, filter, name);
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
	// String typeName = objectDataModel.getTypeName();
	try {
	    persistenceManager.readAllChildren(getRowsDirPath(), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}
	return dataHolder;
    }

    public Collection<T> readAllObjectsBut(final Filter filter, final TransactionManager transactionManager,
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
		    // object = odm.getObjectFromString(data, filter);
		    object = transactionManager.buildObject(objectName, objectDataModel, data, filter, name);
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
	// String typeName = objectDataModel.getTypeName();
	try {
	    persistenceManager.readAllChildren(getRowsDirPath(), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}

	return dataHolder;
    }

    public Collection<T> readObjects(Collection<String> ids, final Filter filter,
	    final TransactionManager transactionManager, final Collection<T> dataHolder) throws MdbException {

	MdbObjectHandler handler = new MdbObjectHandler() {

	    @Override
	    public boolean handle(String line) {
		data = line;
		/* read only one line */
		return false;
	    }
	};
	// String typeName = objectDataModel.getTypeName();
	for (String id : ids) {
	    ResourceLock rowLock = locksManager.getRowLock(id);
	    rowLock.acquireReadLock();
	    try {

		persistenceManager.read(getRowPath(id), handler);

		// T object = odm.getObjectFromString(handler.getData(), filter);
		T object = transactionManager.buildObject(objectName, objectDataModel, handler.getData(), filter, id);
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

    protected void deleteUnusedIndexes(ObjectContext<T> oc, String rowId) {
	Map<String, String> oldIndexedValues = oc.getOldIndexedValues();
	if (oldIndexedValues.size() > 0) {
	    // System.out.println(rowId + ": deleting unique indexes " + oc.getOldUniqueValues());
	    for (UniqueIndexValue uiv : oc.getOldUniqueValues().values()) {
		deleteUniqueIndex(uiv, rowId);
		if (!uiv.isComposite()) {
		    oldIndexedValues.remove(uiv.getName());
		}
	    }
	    // System.out.println("deleting indexes " + oldIndexedValues);
	    for (Map.Entry<String, String> e : oldIndexedValues.entrySet()) {
		deleteIndex(e.getKey(), e.getValue(), rowId);
	    }
	}
    }

    protected boolean delete(ObjectContext<T> oc, String rowId, TransactionManager transactionManager)
	    throws MdbException {

	ResourceLock rowLock = locksManager.getRowLock(rowId);
	rowLock.acquireWriteLock();

	try {

	    // System.out.println("start delete for row "+rowId);
	    deleteUnusedIndexes(oc, rowId);
	    // System.out.println("deleting "+rowId);

	    /* delete links */
	    for (FieldDataModel<?> fdm : objectDataModel.getLinkedFields()) {
		LinkModel lm = fdm.getLinkModel();
		if (lm.isFirst()) {
		    transactionManager.deleteLinks(lm, rowId);
		}
	    }

	    return persistenceManager.delete(getRowPath(rowId));
	} finally {
	    rowLock.releaseWriteLock();
	    locksManager.releaseRowLock(rowLock);
	}
    }

    public long deleteAll(final Filter filter, final TransactionManager transactionManager) throws MdbException {
	// final String typeName = objectDataModel.getTypeName();
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
		    oc = getObjectContextForDelete(data, filter, name, transactionManager);
		    if (oc != null) {
			deleted = delete(oc, name, transactionManager);
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
	    persistenceManager.readAllChildren(getRowsDirPath(), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}
	return handler.getRowCount();
    }

    public long deleteAllBut(final Filter filter, final Collection<String> restricted,
	    final TransactionManager transactionManager) throws MdbException {
	// final String typeName = objectDataModel.getTypeName();
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
		    oc = getObjectContextForDelete(data, filter, name, transactionManager);
		    if (oc != null) {
			deleted = delete(oc, name, transactionManager);
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
	    persistenceManager.readAllChildren(getRowsDirPath(), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}
	return handler.getRowCount();
    }

    public long deleteObjects(Collection<String> ids, final Filter filter, final TransactionManager transactionManager)
	    throws MdbException {
	// final String typeName = objectDataModel.getTypeName();

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
		persistenceManager.read(getRowPath(id), handler);

		ObjectContext<T> oc = getObjectContextForDelete(handler.getData(), filter, id, transactionManager);
		if (oc != null) {
		    deleted = delete(oc, id, transactionManager);
		}
	    } catch (PersistenceException e) {
		throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	    } catch (MdbException e) {
		throw e;
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

    public <F extends Comparable<F>> Collection<String> getRowsForRange(FieldConstraintContext<F> fcc,
	    Collection<String> data) throws MdbException {
	String indexPath = getIndexPath(fcc.getFieldName());
	Range<F> range = fcc.getRange();

	Map<String, String> elligiblePaths = new HashMap<String, String>();
	elligiblePaths.put(indexPath, "");
	F minValue = range.getMinValue();
	F maxValue = range.getMaxValue();
	boolean inRange = true;

	String minValueString = (minValue != null) ? minValue.toString() : null;
	String maxValueString = (maxValue != null) ? maxValue.toString() : null;

	while ((elligiblePaths.size() > 0) && inRange) {
	    Map<String, String> nextPaths = new HashMap<String, String>();

	    for (Map.Entry<String, String> pe : elligiblePaths.entrySet()) {
		String currentPath = pe.getKey();

		String[] nextVals;
		try {
		    nextVals = persistenceManager.listChildren(currentPath);
		} catch (PersistenceException e) {
		    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
		}

		String currentValuePath = pe.getValue();
		int nextValueLength = currentValuePath.length() + 1;
		F minValFragment = null;
		boolean minSatisfied = false;

		if (minValueString != null && (minValueString.length() >= nextValueLength)) {
		    minValFragment = range.fromString(minValueString.substring(0, nextValueLength));
		} else {
		    minSatisfied = true;
		}
		//
		// boolean maxSatisfied=true;
		// if(maxValueString != null && (maxValueString.length() == nextValueLength)) {
		// maxSatisfied=false;
		// }

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
		    inRange = (minSatisfied || ((minComp = value.compareTo(minValFragment)) >= 0))
			    && (maxValue == null || ((maxComp = value.compareTo(maxValue)) <= 0));
		    /* the value is in range */
		    if (inRange) {
			if (minComp >= 0 && maxComp <= 0) {
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

    protected boolean indexValueExists(String indexName, String value) throws MdbException {
	try {
	    return persistenceManager.exists(getPathForIndex(indexName, value));
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
    }

    protected boolean compositeIndexValueExists(String indexName, String value) throws MdbException {
	try {
	    return persistenceManager.exists(getPath(getCompositeIndexPath(indexName), value));
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
    }

    public Collection<String> getRowsForValues(FieldConstraintContext<?> fcc, Set<String> data) throws MdbException {
	String indexName = fcc.getFieldName();
	for (String value : fcc.getValues()) {
	    String path = getPathForIndex(indexName, value);
	    readIndexRows(path, data);
	}
	return data;
    }

    private String getRowIdForUniqueIndex(UniqueIndexValue uiv) throws MdbException {
	if (uiv == null) {
	    return null;
	}
	String indexPath = getIndexValuePath(uiv);
	Collection<String> rowsIds = readIndexRows(indexPath, null);
	int count = rowsIds.size();
	if (count < 1) {
	    return null;
	}
	if (count == 1) {
	    return new ArrayList<String>(rowsIds).get(0);
	}
	throw new MdbException(MdbErrorType.DUPLICATE_UNIQUE_VALUE, new GenericNameValue("index", uiv.getName()),
		new GenericNameValue("value", uiv.getValue()));
    }

    private String getPathForIndex(String indexName, String value) {
	String path = getIndexPath(indexName);
	for (int i = 0; i < value.length(); i++) {
	    path = persistenceManager.getPath(path, String.valueOf(value.charAt(i)));
	}
	return getIndexRowsPath(path);
    }

    protected String getIndexRowsPath(String indexValuePath) {
	return getPath(indexValuePath, SpecialPaths.INDEX_ROWS);
    }

    protected void addIndex(String indexName, String indexValue, String rowId) throws MdbException {
	String indexPath = getPathForIndex(indexName, indexValue);
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

    protected boolean addUniqueIndex(String indexName, String indexValue, String rowId) throws MdbException {
	String indexPath = getPathForIndex(indexName, indexValue);
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

    protected boolean deleteIndex(String indexName, String indexValue, String rowId) {
	String indexPath = getPathForIndex(indexName, indexValue);
	String indexRowPath = getPath(indexPath, rowId);
	ResourceLock indexLock = locksManager.getIndexLock(indexPath);
	indexLock.acquireWriteLock();
	boolean deleted = persistenceManager.delete(indexRowPath);
	indexLock.releaseWriteLock();
	locksManager.releaseIndexLock(indexLock);
	return deleted;
    }

    protected void addCompositeIndex(String indexName, String indexValue, String rowId) throws MdbException {
	String indexPath = getCompositeIndexPath(indexName);
	String indexValuePath = getPath(indexPath, indexValue);
	try {
	    persistenceManager.write(indexValuePath, rowId);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
    }

    protected boolean deleteUniqueIndex(UniqueIndexValue uiv, String rowId) {
	String indexValuePath = getIndexValuePath(uiv);

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

    public ObjectContext<T> getObjectContext(T target, TransactionManager transactionManager) throws MdbException {
	Class<?> type = target.getClass();
	Map<String, Object> valuesMap = objectDataModel.getFieldValuesMap(target);
	Map<String, String> indexedValues = new HashMap<String, String>();
	Map<String, UniqueIndexValue> uniqueValues = new HashMap<String, UniqueIndexValue>();
	Map<String, ObjectContext<?>> nestedObjectContexts = new HashMap<String, ObjectContext<?>>();
	String data = "";
	boolean addSeparator = false;
	String objectIdFieldName = objectDataModel.getObjectIdFieldName();

	for (FieldDataModel<?> fdm : objectDataModel.getFields().values()) {
	    String fieldName = fdm.getName();
	    if (fieldName.equals(objectIdFieldName)) {
		continue;
	    }

	    Object fieldValue = valuesMap.get(fieldName);
	    /* convert value to string */
	    String fieldData = null;

	    if (fieldValue == null) {
		if (transactionManager != null) {
		    String sequenceId = fdm.getSequenceId();
		    if (sequenceId != null) {
			fieldData = "" + transactionManager.getNextValForSequence(sequenceId);
			objectDataModel.setFieldValue(target, fieldName, fieldData);
		    }
		}
	    } else {
		/* see if this is a link */
		LinkModel linkModel = fdm.getLinkModel();
		boolean isLink = linkModel != null;

		if (isLink) {
		    /*
		     * if the linked object already exists, get the rowId, otherwise create an ObjectContext for it
		     */
		    if (fieldValue != null) {
			ObjectContext<?> nestedObjectContext = transactionManager.getObjectContext(fieldValue
				.getClass().getName(), fieldValue, true);
			nestedObjectContexts.put(fieldName, nestedObjectContext);
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
		uniqueIndex = objectDataModel.getUniqueIndex(uniqueIndexName);
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

	return new ObjectContext<T>(target, objectDataModel, data, indexedValues, uniqueValues, nestedObjectContexts);
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
    public ObjectContext<T> getObjectContext(TransactionManager transactionManager, T target, Filter filter,
	    String rawOldData, String rowId) throws MdbException {
	if (rawOldData == null) {
	    return null;
	}

	Map<String, Object> oldValuesMap = transactionManager.getAndCheckValuesMap(objectName, objectDataModel,
		rawOldData, filter, rowId);
	if (oldValuesMap == null) {
	    return null;
	}
	transactionManager.clearPendingRows();
	return getObjectContext(transactionManager, target, filter, rowId, oldValuesMap, "");

    }

    public ObjectContext<T> getObjectContext(TransactionManager transactionManager, T target, Filter filter,
	    String rawOldData, String rowId, String path) throws MdbException {
	if (rawOldData == null) {
	    return null;
	}

	Map<String, Object> oldValuesMap = transactionManager.getValuesMap(objectName, objectDataModel, rawOldData,
		filter, rowId);
	if (oldValuesMap == null) {
	    return null;
	}

	return getObjectContext(transactionManager, target, filter, rowId, oldValuesMap, path);

    }

    public ObjectContext<T> getObjectContext(TransactionManager transactionManager, T target, Filter filter,
	    String rowId, Map<String, Object> oldValuesMap, String path) throws MdbException {

	if (transactionManager.isRowPending(objectName, rowId)) {
	    return null;
	}
	transactionManager.addPendingRowId(objectName, rowId);

	Map<String, String> indexedValues = new HashMap<String, String>();
	Map<String, UniqueIndexValue> uniqueValues = new HashMap<String, UniqueIndexValue>();

	Map<String, String> outdatedIndexValues = new HashMap<String, String>();
	Map<String, UniqueIndexValue> outdatedUniqueValues = new HashMap<String, UniqueIndexValue>();

	Map<String, Object> newValuesMap = objectDataModel.getFieldValuesMap(target);

	Set<String> targetFields = filter.getTargetFields();
	Set<String> changedFields = new HashSet<String>();

	Set<FieldDataModel<?>> simpleFields = objectDataModel.getSimpleFields();
	String[] newData = new String[simpleFields.size()];

	for (FieldDataModel<?> fdm : simpleFields) {
	    String fieldName = fdm.getName();
	    /* the full field name in the nested hierarchy */
	    String fullFieldName = path + fieldName;
	    int position = objectDataModel.getFieldPosition(fieldName);

	    Object oldVal = oldValuesMap.get(fullFieldName);
	    String oldValData = (oldVal != null) ? oldVal.toString() : "";

	    /* if the current field is not targeted for update, keep the old value */
	    if (targetFields != null && !targetFields.contains(fullFieldName)) {
		newData[position] = oldValData;
		continue;
	    }

	    String fieldData = "";

	    Object fieldValue = newValuesMap.get(fieldName);
	    /* convert value to string */
	    if (fieldValue != null) {
		fieldData = fieldValue.toString();
	    }

	    /* update the new data with this field value */
	    newData[position] = fieldData;

	    if (ObjectsUtil.areEqual(fieldValue, oldVal)) {
		/* if the value of this field hasn't changed there's nothing more to do */
		continue;
	    }

	    changedFields.add(fieldName);

	    /* test if the value is valid for this field */
	    try {
		fdm.testValue(fieldData);
	    } catch (MdbException ode) {
		throw new MdbException(MdbErrorType.REQUIRED, new GenericNameValue("type", objectDataModel.getType()
			.getName()), new GenericNameValue(fieldName, fieldData));
	    }

	    /* build reqular indexes */
	    if (fdm.isIndexed()) {
		indexedValues.put(fieldName, fieldData);
		outdatedIndexValues.put(fieldName, oldVal.toString());
	    }
	}

	String data = null;

	if (changedFields.size() > 0) {

	    /* ok, now we should see how many unique indexes have changed */
	    Collection<UniqueIndex> touchedUniqueIndexes = objectDataModel.getTouchedUniqueIndexes(changedFields);

	    /*
	     * for each composite index that has changed we need to compute the new value and the old value that is
	     * going to be deleted
	     */

	    for (UniqueIndex ui : touchedUniqueIndexes) {
		for (FieldDataModel<?> fdm : ui.getFields()) {
		    String uniqueIndexName = ui.getId();
		    String fieldName = fdm.getName();
		    String fullFieldName = path + fieldName;
		    int position = objectDataModel.getFieldPosition(fieldName);
		    if (objectDataModel.getUniqueIndex(uniqueIndexName).isComposite()) {
			addUniqueIndex(uniqueIndexName, newData[position], uniqueValues);
			addUniqueIndex(uniqueIndexName, oldValuesMap.get(fullFieldName).toString(),
				outdatedUniqueValues);
		    } else {

			addUniqueIndex(fieldName, newData[position], uniqueValues);
			addUniqueIndex(fieldName, oldValuesMap.get(fullFieldName).toString(), outdatedUniqueValues);
			/* remove unique indexes from regular indexes */
			indexedValues.remove(fieldName);
			outdatedIndexValues.remove(fieldName);
		    }
		}
	    }

	    data = "";
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
	}

	/* Now we must deal with the pesky nested objects */

	Map<String, ObjectContext<?>> nestedContexts = new HashMap<String, ObjectContext<?>>();
	Set<LinkValue> linksToRemove = new HashSet<LinkValue>();
	Set<LinkValue> linksToAdd = new HashSet<LinkValue>();

	for (FieldDataModel<?> fdm : objectDataModel.getLinkedFields()) {
	    String fieldName = fdm.getName();
	    String fullFieldName = path + fieldName;
	    /* first, check if the field is actually targeted for update */
	    if (targetFields != null && !targetFields.contains(fullFieldName)) {
		continue;
	    }
	    /* now let's see if there is a link for this field */
	    ObjectsLink ol = transactionManager.getPendingLinkForField(fullFieldName);
	    /* get the new value */
	    Object fieldValue = newValuesMap.get(fieldName);

	    /* get the object data model of the field */
	    ObjectDataModel<?> fodm = (ObjectDataModel<?>) fdm.getDataModel();

	    /* let's test different cases */
	    if (ol == null && fieldValue == null) {
		/* nothing to do */
		continue;
	    } else if (ol != null && fieldValue != null) {
		/*
		 * so, there already is a link for this field and a new value was also specified
		 */
		/* nestedRowId should not be null, because the link is not null */
		String nestedRowId = transactionManager.getPendingRowIdForField(fullFieldName);
		String currentValueId = fodm.getObjectId(fieldValue);

		if (currentValueId == null) {
		    /* new object */
		    nestedContexts.put(fieldName,
			    transactionManager.getObjectContext(fodm.getTypeName(), fieldValue, true));
		    /* remove the old link */
		    linksToRemove.add(new LinkValue(fdm.getLinkModel().getName(), ol));
		} else if (nestedRowId.equals(currentValueId)) {
		    /* same object, we need to see if something has updated */
		    nestedContexts.put(fieldName, transactionManager.getObjectContext(fodm.getTypeName(), fieldValue,
			    filter, nestedRowId, oldValuesMap, fullFieldName + Constants.NESTED_FIELD_SEPARATOR));
		} else {
		    /* reference has changed to another object */
		    nestedContexts.put(fieldName, transactionManager.getObjectContext(fodm.getTypeName(), fieldValue,
			    filter, currentValueId, oldValuesMap, fullFieldName + Constants.NESTED_FIELD_SEPARATOR));
		    /* remove the old link */
		    linksToRemove.add(new LinkValue(fdm.getLinkModel().getName(), ol));
		    linksToAdd.add(getLinkValue(fdm, rowId, currentValueId));
		}
	    } else if (ol != null) {
		/* the field value was set to null, we need to remove the old link */
		linksToRemove.add(new LinkValue(fdm.getLinkModel().getName(), ol));
	    } else {
		/*
		 * there is no old link, but a new value was set we need to see if this is a completely new object, or a
		 * reference to an existing one which should be updated if changed
		 */
		String currentValueId = fodm.getObjectId(fieldValue);
		if (currentValueId == null) {
		    nestedContexts.put(fieldName,
			    transactionManager.getObjectContext(fodm.getTypeName(), fieldValue, true));
		} else {
		    /*
		     * the tricky part here is that this object's data is not found in the oldValuesMap so we need to
		     * call the main getObjectContext for update
		     */

		    String nestedData = transactionManager.getDataForRowId(fodm, currentValueId);
		    nestedContexts.put(fieldName, transactionManager.getObjectContext(fodm.getTypeName(), fieldValue,
			    filter, nestedData, currentValueId, fullFieldName + Constants.NESTED_FIELD_SEPARATOR));
		    linksToAdd.add(getLinkValue(fdm, rowId, currentValueId));
		}
	    }
	}

	/* build object context */

	return new ObjectContext<T>(objectDataModel, data, indexedValues, uniqueValues, outdatedIndexValues,
		outdatedUniqueValues, rowId, nestedContexts, linksToRemove, linksToAdd);

    }

    public ObjectContext<T> getObjectContextForDelete(String data, Filter filter, String rowId,
	    TransactionManager transactionManager) throws MdbException {
	if (data == null) {
	    return null;
	}

	Map<String, Object> valuesMap = transactionManager.getAndCheckValuesMap(data, objectDataModel, data, filter,
		rowId);
	if (valuesMap == null) {
	    return null;
	}

	/*
	 * We need to check if any direct references exist for this object, in which case we abort this action by
	 * throwing an appropriate error
	 */
	transactionManager.checkForDirectReferences(objectDataModel, rowId);

	/*
	 * if this object is valid for deletion, proceed on finding out the unique indexes that also need to be deleted
	 */

	Map<String, String> indexedValues = new HashMap<String, String>();
	Map<String, UniqueIndexValue> uniqueValues = new HashMap<String, UniqueIndexValue>();

	for (FieldDataModel<?> fdm : objectDataModel.getIndexedFields()) {
	    String fieldName = fdm.getName();
	    Object fieldValue = valuesMap.get(fieldName);
	    if (fieldValue == null) {
		continue;
	    }
	    String fieldData = fieldValue.toString();

	    indexedValues.put(fieldName, fieldData);
	    String uniqueIndexName = fdm.getUniqueIndexId();
	    UniqueIndex uniqueIndex = null;
	    if (uniqueIndexName != null) {
		uniqueIndex = objectDataModel.getUniqueIndex(uniqueIndexName);
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

	return new ObjectContext<T>(objectDataModel, indexedValues, uniqueValues);
    }

    // private <N> ObjectContext<N> getNestedObjectContext(N target, ObjectDataModel<N> odm,
    // TransactionManager transactionManager) throws MdbException {
    //
    // /* check if an object with the specified pk already exists */
    // UniqueIndexValue pkValue = odm.getUniqueIndexValue(target, odm.getPrimaryKeyId());
    // String rowId = getRowIdForUniqueIndex(pkValue);
    // if (rowId != null) {
    // return new ObjectContext<N>(odm, rowId);
    // }
    //
    // return getObjectContext(target, , transactionManager);
    //
    // }

    public ObjectContext<T> getObjectContext(T target, boolean create, TransactionManager transactionManager)
	    throws MdbException {

	/* check if an object with the specified row id already exists */
	String rowId = objectDataModel.getObjectId(target);
	if (rowId != null) {
	    return new ObjectContext<T>(objectDataModel, rowId);
	} else if (create) {
	    return getObjectContext(target, transactionManager);
	}
	return null;
    }

    private void addUniqueIndex(String indexName, String value, Map<String, UniqueIndexValue> indexesValues) {
	UniqueIndexValue uiv = indexesValues.get(indexName);
	if (uiv == null) {
	    uiv = new UniqueIndexValue(indexName);
	    indexesValues.put(indexName, uiv);
	}
	uiv.addValue(value);
    }

    protected void testUniqueIndexes(Collection<UniqueIndexValue> uniqueIndexesValues) throws MdbException {

	for (UniqueIndexValue uiv : uniqueIndexesValues) {
	    boolean exists = false;
	    if (uiv.isComposite()) {
		exists = compositeIndexValueExists(uiv.getName(), uiv.getValue());
	    } else {
		exists = indexValueExists(uiv.getName(), uiv.getValue());
	    }
	    if (exists) {
		throw new MdbException(MdbErrorType.UNIQUENESS_VIOLATED, new GenericNameValue(uiv.getName(), uiv));
	    }
	}
    }

    public Long updateAll(final T source, final Filter filter, final TransactionManager transactionManager)
	    throws MdbException {
	// final String typeName = odm.getTypeName();
	MdbObjectSetHandler handler = new MdbObjectSetHandler() {
	    private ResourceLock rowLock;

	    @Override
	    public boolean endFile(String name) {
		ObjectContext<T> oc = null;
		try {
		    oc = getObjectContext(transactionManager, source, filter, data, name);
		    if (oc != null) {
			// oc.setRowIdProvider(new StaticRowIdProvider(name));
			oc.setRowLock(rowLock);
			boolean updated = update(oc, transactionManager);
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
	    persistenceManager.readAllChildren(getRowsDirPath(), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}

	return handler.getRowCount();
    }

    public Long updateAllBut(final T source, final Filter filter, final Collection<String> restricted,
	    final TransactionManager transactionManager) throws MdbException {
	// final String typeName = odm.getTypeName();
	MdbObjectSetHandler handler = new MdbObjectSetHandler() {
	    private ResourceLock rowLock;

	    @Override
	    public boolean endFile(String name) {
		ObjectContext<T> oc = null;
		try {
		    oc = getObjectContext(transactionManager, source, filter, data, name);
		    if (oc != null) {
			// oc.setRowIdProvider(new StaticRowIdProvider(name));
			oc.setRowLock(rowLock);
			boolean updated = update(oc, transactionManager);
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
	    persistenceManager.readAllChildren(getRowsDirPath(), handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}

	if (handler.hasError()) {
	    throw handler.getError();
	}

	return handler.getRowCount();
    }

    public Long updateObjects(Collection<String> ids, T source, Filter filter, TransactionManager transactionManager)
	    throws MdbException {
	// final String typeName = odm.getTypeName();
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
		persistenceManager.read(getRowPath(id), handler);
		oc = getObjectContext(transactionManager, source, filter, handler.getData(), id);
		if (oc != null) {
		    // oc.setRowIdProvider(new StaticRowIdProvider(id));
		    oc.setRowLock(rowLock);
		    boolean updated = update(oc, transactionManager);
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

    public T create(T target, TransactionManager transactionManager) throws MdbException {
	Class<?> type = target.getClass();
	Persistable persistenceInfo = type.getAnnotation(Persistable.class);
	if (persistenceInfo == null) {
	    throw new RuntimeException("Object " + target
		    + " is not persistable. Must be annotaded with 'ro.zg.mdb.core.annotations.Persistable'");
	}

	/* get the object context */
	ObjectContext<T> oc = getObjectContext(target, transactionManager);
	save(oc, transactionManager);

	return target;
    }

    public boolean update(ObjectContext<T> oc, TransactionManager transactionManager) throws MdbException {
	save(oc, transactionManager);
	deleteUnusedIndexes(oc, oc.getRowId());
	for (LinkValue lv : oc.getLinksToRemove()) {
	    transactionManager.deleteLinkValue(lv);
	}

	return true;
    }

    public void save(ObjectContext<T> objectContext, TransactionManager transactionManager) throws MdbException {
	ObjectDataModel<T> odm = objectContext.getObjectDataModel();
	// String objectName = objectContext.getObjectName();

	/* assure there is no duplication for unique indexes */
	Collection<UniqueIndexValue> uniqueValues = objectContext.getUniqueValues().values();
	Collection<ResourceLock> uniqueValuesLocks = null;

	String rowId = objectContext.getRowId();
	ResourceLock rowLock = objectContext.getRowLock();
	if (rowLock == null) {
	    rowLock = locksManager.getRowLock(rowId);
	    rowLock.acquireWriteLock();
	}

	if (!objectContext.isAlreadyCreated() && rowExists(rowId)) {
	    rowLock.releaseWriteLock();
	    locksManager.releaseRowLock(rowLock);
	    throw new MdbException(MdbErrorType.UNIQUENESS_VIOLATED, new GenericNameValue("type",
		    objectDataModel.getType()), new GenericNameValue("rowId", rowId));
	}

	/* hold the unique values */
	synchronized (odm) {

	    uniqueValuesLocks = lockUniqueValues(uniqueValues);
	    // rowId = objectContext.getRowIdProvider().provideRowId();

	}

	// System.out.println("start saving "+rowId);
	try {
	    testUniqueIndexes(uniqueValues);
	    // System.out.println(Thread.currentThread().getId() + " passed testing for " + uniqueValues);
	} catch (MdbException e) {
	    /* in case the test fails release the locks */
	    releaseUniqueValuesLocks(uniqueValuesLocks);
	    throw e;
	}

	/* set this row as pending */
	transactionManager.addPendingRowId(objectName, rowId);

	/*
	 * take care of the nested objects
	 */
	for (Map.Entry<String, ObjectContext<?>> e : objectContext.getNestedObjectContexts().entrySet()) {

	    ObjectContext<?> noc = e.getValue();
	    if (noc == null) {
		continue;
	    }

	    boolean createLink = false;

	    if (!noc.isAlreadyCreated()) {
		transactionManager.save(noc);
		createLink = true;

	    } else if (noc.isUpdated()) {
		transactionManager.update(noc);
	    } else {
		createLink = true;
	    }

	    if (createLink) {
		LinkValue lv = getLinkValue(odm.getField(e.getKey()), rowId, noc.getRowId());
		transactionManager.saveLinkValue(lv);
	    }

	}

	try {
	    /* create the actual data record */

	    try {
		// System.out.println("saving "+objectContext.getData()+" for "+ typeName+" at row "+rowId);
		saveData(objectContext.getData(), rowId);
	    } catch (PersistenceException e) {
		throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	    } finally {
		rowLock.releaseWriteLock();
		locksManager.releaseRowLock(rowLock);
	    }

	    /* create unique indexes */
	    for (UniqueIndexValue uiv : objectContext.getUniqueValues().values()) {
		if (uiv.isComposite()) {
		    addCompositeIndex(uiv.getName(), uiv.getValue(), rowId);
		} else {
		    addUniqueIndex(uiv.getName(), uiv.getValue(), rowId);
		}
	    }
	    // System.out.println(Thread.currentThread().getId() + " created " +
	    // objectContext.getUniqueValues().values());
	    /* create indexes */
	    for (Map.Entry<String, String> e : objectContext.getIndexedValues().entrySet()) {
		addIndex(e.getKey(), e.getValue(), rowId);
	    }

	    /* udpate the object id */
	    if (!objectContext.isAlreadyCreated()) {
		objectContext.updateObjectId();
	    }

	    /* create links */
	    for (LinkValue lv : objectContext.getLinksToAdd()) {
		transactionManager.saveLinkValue(lv);
	    }

	} finally {
	    releaseUniqueValuesLocks(uniqueValuesLocks);
	}
    }

    protected LinkValue getLinkValue(FieldDataModel<?> fdm, String mainRowId, String nestedRowId) {
	LinkModel linkModel = fdm.getLinkModel();
	String linkName = linkModel.getName();
	ObjectsLink ol = new ObjectsLink();
	if (linkModel.isFirst()) {
	    ol.setFirstRowId(mainRowId);
	    ol.setSecondRowId(nestedRowId);
	} else {
	    ol.setSecondRowId(mainRowId);
	    ol.setFirstRowId(nestedRowId);
	}
	return new LinkValue(linkName, ol);
    }

    protected Collection<ResourceLock> lockUniqueValues(Collection<UniqueIndexValue> uniqueValues) {
	Collection<ResourceLock> locks = new HashSet<ResourceLock>();
	for (UniqueIndexValue uiv : uniqueValues) {
	    locks.add(lockUniqueValue(uiv));
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

    protected ResourceLock lockUniqueValue(UniqueIndexValue uiv) {
	String indexValuePath = getIndexValuePath(uiv);
	ResourceLock indexLock = locksManager.getIndexLock(indexValuePath);
	indexLock.acquireWriteLock();
	// System.out.println(Thread.currentThread().getId() + " locked index " + indexValuePath);
	return indexLock;
    }

    protected void saveData(String data, String rowId) throws PersistenceException {
	persistenceManager.write(getRowPath(rowId), data);
    }

    protected String getIndexValuePath(UniqueIndexValue uiv) {
	String indexValuePath = null;
	if (uiv.isComposite()) {
	    String indexPath = getCompositeIndexPath(uiv.getName());
	    indexValuePath = getPath(indexPath, uiv.getValue());
	} else {
	    indexValuePath = getPathForIndex(uiv.getName(), uiv.getValue());
	}
	return indexValuePath;
    }

}
