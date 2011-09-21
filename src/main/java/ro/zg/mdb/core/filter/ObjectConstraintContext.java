package ro.zg.mdb.core.filter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.FieldDataModel;
import ro.zg.mdb.core.meta.ObjectDataModel;
import ro.zg.mdb.core.meta.PersistentObjectDataManager;

public class ObjectConstraintContext<T> {
    private ObjectDataModel<T> objectDataModel;
    private String typeName;
    private Map<String, FieldConstraintContext> fieldsConstraintContexts = new HashMap<String, FieldConstraintContext>();
    private PersistentObjectDataManager indexManager;
    private Deque<Set<String>> allowedRowsIds = new ArrayDeque<Set<String>>();
    private Deque<Set<String>> restrictedRowsIds = new ArrayDeque<Set<String>>();
    private Set<String> usedIndexes = new HashSet<String>();
    private boolean intersection = false;
    private boolean simple = true;

    public ObjectConstraintContext(ObjectDataModel<T> objectDataModel, PersistentObjectDataManager indexManager) {
	super();
	this.objectDataModel = objectDataModel;
	this.indexManager = indexManager;
	this.typeName = objectDataModel.getTypeName();
    }

    /**
     * @return the objectDataModel
     */
    public ObjectDataModel<T> getObjectDataModel() {
	return objectDataModel;
    }

    public void addFieldConstraintContext(FieldConstraintContext context) throws MdbException {
	fieldsConstraintContexts.put(context.getFieldName(), context);
	FieldDataModel fdm = context.getFieldDataModel();
	if (fdm.isIndexed()) {
	    usedIndexes.add(fdm.getName());
	    processConstraint(context);
	}
    }

    /**
     * @return the fieldsConstraintContexts
     */
    public Map<String, FieldConstraintContext> getFieldsConstraintContexts() {
	return fieldsConstraintContexts;
    }

    public <F extends Comparable<F>> void processConstraint(FieldConstraintContext<F> constraintContext) throws MdbException {
	Set<String> allowedRows = new HashSet<String>();
	Set<String> restrictedRows = new HashSet<String>();
	if (constraintContext.hasRanges()) {
	    indexManager.getRowsForRange(typeName, constraintContext, allowedRows);
	    allowedRowsIds.push(allowedRows);
	}
	if (constraintContext.hasValues()) {
	    if (constraintContext.isRestricted()) {
		indexManager.getRowsForValues(typeName, constraintContext, restrictedRows);
		restrictedRowsIds.push(restrictedRows);
	    } else {
		indexManager.getRowsForValues(typeName, constraintContext, allowedRows);
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

	if (!restricted.isEmpty()) {
	    restricted.removeAll(allowed);
	}

	return true;
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