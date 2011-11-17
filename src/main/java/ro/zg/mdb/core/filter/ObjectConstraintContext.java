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
	this.transactionManager=transactionManager;
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

    public boolean applyAnd() throws MdbException {
	return transactionManager.processConstraint(ConstraintType.AND, this);
    }

    public boolean applyOr() throws MdbException {
	return transactionManager.processConstraint(ConstraintType.OR, this);
    }

    public void resolveNestedObjectContexts() throws MdbException {
	for(ObjectConstraintContext<?> nestedContext : nestedObjectContexts.values()) {
	    nestedContext.resolveNestedObjectContexts();
	    extractRowsFromNested(nestedContext);
	}
    }
    

    public boolean extractRowsFromNested(ObjectConstraintContext<?> nestedContext) throws MdbException {
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
    
    public int getPendingObjectContextsSize() {
	return pendingObjectContexts.size();
    }
    
    public ObjectConstraintContext<?> popPendingObjectContext(){
	return pendingObjectContexts.pop();
    }

    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @param intersection the intersection to set
     */
    public void setIntersection(boolean intersection) {
        this.intersection = intersection;
    }

    /**
     * @param simple the simple to set
     */
    public void setSimple(boolean simple) {
        this.simple = simple;
    }
    
    public void pushAllowedRows(Set<String> rows) {
	allowedRowsIds.push(rows);
    }

    public void pushRestrictedRows(Set<String> rows) {
	restrictedRowsIds.push(rows);
    }
    
    public Set<String> popAllowedRows(){
	return allowedRowsIds.pop();
    }
    
    public Set<String> popRestrictedRows(){
	return restrictedRowsIds.pop();
    }

    /**
     * @return the allowedRowsIds
     */
    public Deque<Set<String>> getAllowedRowsIds() {
        return allowedRowsIds;
    }

    /**
     * @return the restrictedRowsIds
     */
    public Deque<Set<String>> getRestrictedRowsIds() {
        return restrictedRowsIds;
    }

    /**
     * @return the parentContext
     */
    public ObjectConstraintContext<?> getParentContext() {
        return parentContext;
    }
    
    
}
