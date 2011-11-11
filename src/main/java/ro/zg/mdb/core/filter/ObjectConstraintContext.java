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
package ro.zg.mdb.core.filter;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ro.zg.mdb.constants.Constants;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.TransactionManager;
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.LinkModel;
import ro.zg.mdb.core.meta.data.ObjectDataModel;
import ro.zg.mdb.core.meta.data.ObjectsLink;

public class ObjectConstraintContext<T> {
    private ObjectDataModel<T> objectDataModel;
    private String objectName;
    private Class<T> type;
    private ObjectConstraintContext<?> parentContext;
    private int depth=0;
    private String fieldName;
    private TransactionManager transactionManager;
    private Deque<Set<String>> allowedRowsIds = new ArrayDeque<Set<String>>();
    private Deque<Set<String>> restrictedRowsIds = new ArrayDeque<Set<String>>();
    private Set<String> usedIndexes = new HashSet<String>();
    private boolean intersection = false;
    private boolean simple = true;
    private HashMap<String, ObjectConstraintContext<?>> nestedObjectContexts = new HashMap<String, ObjectConstraintContext<?>>();

    private Deque<ObjectConstraintContext<?>> pendingObjectContexts = new ArrayDeque<ObjectConstraintContext<?>>();

    public ObjectConstraintContext(String objectName, Class<T> type, TransactionManager transactionManager) {
	super();

	this.transactionManager = transactionManager;
	this.objectName = objectName;
	this.type = type;
	this.objectDataModel = transactionManager.getObjectDataModel(type);
    }

    private ObjectConstraintContext(String fieldName, ObjectDataModel<T> objectDataModel,
	    TransactionManager transactionManager, ObjectConstraintContext<?> parentContext) {
	this.objectDataModel = objectDataModel;
	this.objectName = objectDataModel.getTypeName();
	this.type = objectDataModel.getType();
	this.parentContext = parentContext;
	this.fieldName = fieldName;
	this.depth=parentContext.depth+1;
    }

    public ObjectConstraintContext<?> getObjectContraintContextForField(String fieldName) {
	int separatorIndex = fieldName.indexOf(Constants.NESTED_FIELD_SEPARATOR);
	if (separatorIndex <= 0) {
	    return this;
	}
	String currentFieldName = fieldName.substring(0, separatorIndex);
	String nestedFieldName = fieldName.substring(separatorIndex) + 1;
	return getNestedObjectConstraintContext(currentFieldName).getObjectContraintContextForField(nestedFieldName);
    }

    private ObjectConstraintContext<?> getNestedObjectConstraintContext(String fieldName) {
	ObjectConstraintContext<?> occ = nestedObjectContexts.get(fieldName);
	if (occ == null) {
	    occ = new ObjectConstraintContext(fieldName, objectDataModel.getObjectDataModelForField(fieldName),
		    transactionManager, this);
	    nestedObjectContexts.put(fieldName, occ);
	}
	return occ;
    }

    /**
     * @return the objectDataModel
     */
    public ObjectDataModel<T> getObjectDataModel() {
	return objectDataModel;
    }

    private void addFieldConstraintContext(FieldConstraintContext<?> context) throws MdbException {
	// fieldsConstraintContexts.put(context.getFieldName(), context);
	FieldDataModel<?> fdm = context.getFieldDataModel();
	if (fdm.isIndexed()) {
	    usedIndexes.add(fdm.getName());
	    processConstraint(context);
	}
    }

    public void addFieldConstraintContext(FieldConstraintContext<?> context, String fieldName) throws MdbException {
	ObjectConstraintContext<?> currentOcc = getObjectContraintContextForField(fieldName);
	currentOcc.addFieldConstraintContext(context);

	pendingObjectContexts.push(currentOcc);
    }

    public <F extends Comparable<F>> void processConstraint(FieldConstraintContext<F> constraintContext)
	    throws MdbException {
	Set<String> allowedRows = new HashSet<String>();
	Set<String> restrictedRows = new HashSet<String>();
	if (constraintContext.hasRanges()) {
	    transactionManager.getRowsForRange(objectName, type, constraintContext, allowedRows);
	    allowedRowsIds.push(allowedRows);
	}
	if (constraintContext.hasValues()) {
	    if (constraintContext.isRestricted()) {
		transactionManager.getRowsForValues(objectName, type, constraintContext, restrictedRows);
		restrictedRowsIds.push(restrictedRows);
	    } else {
		transactionManager.getRowsForValues(objectName, type, constraintContext, allowedRows);
		allowedRowsIds.push(allowedRows);
	    }
	}

    }

    public Set<String> getIntersection(Deque<Set<String>> stack) {
	Set<String> s1 = null;
	Set<String> s2 = null;

	if (stack.size() > 0) {
	    s1 = stack.pop();
	} else {
	    return new HashSet<String>();
	}
	if (stack.size() > 0) {
	    s2 = stack.pop();
	} else {
	    return s1;
	}

	s1.retainAll(s2);
	return s1;
    }

    public Set<String> getUnion(Deque<Set<String>> stack) {
	Set<String> s1 = null;
	Set<String> s2 = null;

	if (stack.size() > 0) {
	    s1 = stack.pop();
	} else {
	    return new HashSet<String>();
	}
	if (stack.size() > 0) {
	    s2 = stack.pop();
	} else {
	    return s1;
	}

	s1.addAll(s2);
	return s1;
    }

    public boolean applyAnd() {
	if (pendingObjectContexts.size() > 1) {
	    ObjectConstraintContext<?> firstOcc = pendingObjectContexts.pop();
	    ObjectConstraintContext<?> secondOcc = pendingObjectContexts.pop();

	    if (firstOcc == secondOcc) {
		if (firstOcc != this) {
		    return firstOcc.applyAnd();
		}
	    } else {

	    }
	}

	intersection = true;
	simple = false;
	Set<String> allowed = getIntersection(allowedRowsIds);
	Set<String> restricted = getUnion(restrictedRowsIds);
	allowedRowsIds.push(allowed);
	restrictedRowsIds.push(restricted);

	if (!restricted.isEmpty()) {
	    if (!allowed.isEmpty()) {
		allowed.removeAll(restricted);
	    }
	    return true;
	}
	return !allowed.isEmpty();
    }

    public boolean applyOr() {
	intersection = false;
	simple = false;
	Set<String> allowed = getUnion(allowedRowsIds);
	Set<String> restricted = getIntersection(restrictedRowsIds);
	allowedRowsIds.push(allowed);
	restrictedRowsIds.push(restricted);

	if (!restricted.isEmpty()) {
	    restricted.removeAll(allowed);
	}

	return true;
    }

    private void reconciliateAnd(ObjectConstraintContext<?> firstOcc, ObjectConstraintContext<?> secondOcc) throws MdbException {
	
	
	
	if(firstOcc.depth==secondOcc.depth) {
	    
	}
    }

    private void reconciliateAndWithNested(ObjectConstraintContext<?> nestedContext) throws MdbException {
	boolean rowsExtracted = extractRowsFromNested(nestedContext);
	applyAnd();
    }

    private boolean extractRowsFromNested(ObjectConstraintContext<?> nestedContext) throws MdbException {
	LinkModel lm = getLinkModel(nestedContext.fieldName);
	
	Set<String> allowed = getReverseLinkedRows(lm, nestedContext.getAllowed());
	if (allowed != null) {
	    allowedRowsIds.push(allowed);
	    return true;
	} else {
	    Set<String> restricted = getReverseLinkedRows(lm, nestedContext.getRestricted());
	    if (restricted != null) {
		restrictedRowsIds.push(restricted);
		return true;
	    }
	}
	return false;
    }

    private Set<String> getReverseLinkedRows(LinkModel lm, Collection<String> rows) throws MdbException {
	if (rows == null) {
	    return null;
	}
	Set<String> parentRows = new HashSet<String>();

	for (String rowId : rows) {
	    Collection<ObjectsLink> links = transactionManager.getObjectLinks(lm, rowId, true);
	    for (ObjectsLink link : links) {
		if (lm.isFirst()) {
		    parentRows.add(link.getFirstRowId());
		} else {
		    parentRows.add(link.getSecondRowId());
		}
	    }
	}
	return parentRows;
    }

    private LinkModel getLinkModel(String nestedFieldName) {
	FieldDataModel<?> fdm = objectDataModel.getField(nestedFieldName);
	LinkModel lm = fdm.getLinkModel();
	return lm;
    }

    public Set<String> getAllowed() {
	if (allowedRowsIds.isEmpty()) {
	    return null;
	}
	return allowedRowsIds.peek();
    }

    public Set<String> getRestricted() {
	if (restrictedRowsIds.isEmpty()) {
	    return null;
	}
	return restrictedRowsIds.peek();
    }

    public boolean hasUsedIndexes() {
	return !usedIndexes.isEmpty();
    }

    /**
     * @return the intersection
     */
    public boolean isIntersection() {
	return intersection;
    }

    /**
     * @return the simple
     */
    public boolean isSimple() {
	return simple;
    }

}
