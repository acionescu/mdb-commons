package ro.zg.mdb.commands.builders;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import ro.zg.mdb.commands.Command;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.ComplexConstraint;
import ro.zg.mdb.core.filter.Constraint;
import ro.zg.mdb.core.filter.ConstraintSet;
import ro.zg.mdb.core.filter.FieldConstraint;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.filter.ObjectConstraint;

public class FilterBuilderContext<T, R> implements Command<R> {
    private Command<R> command;
    private FilterBuilder<T, R> filterBuilder = new FilterBuilder<T, R>(this);

    private T source;

    private Set<String> fields = new HashSet<String>();
    private int currentFieldConstraints;
    private String currentFieldName;
    private Deque<Constraint<Object>> constraintsStack = new ArrayDeque<Constraint<Object>>();
    private Deque<ComplexConstraint<Object>> pendingConditions = new ArrayDeque<ComplexConstraint<Object>>();
    private Filter filter = new Filter();

    public FilterBuilderContext(Command<R> command) {
	super();
	this.command = command;
    }

    public void addCondition(ComplexConstraint<Object> condition) {
	pendingConditions.push(condition);
    }

    public void addConstraint(Constraint<Object> c) {
	if (pendingConditions.size() == 0) {
	    constraintsStack.push((Constraint<Object>) c);
	    currentFieldConstraints++;
	    return;
	}
	
	/* this should never be empty if an old constraint already exists */
	ComplexConstraint<Object> condition = pendingConditions.peek();

	/* only 'and' and 'or' will satisfy this condition */
	if (condition instanceof ConstraintSet<?>) {
	    if (currentFieldConstraints < 1) {
		constraintsStack.push((Constraint<Object>) c);
		currentFieldConstraints++;
		return;
	    }
	    Constraint<Object> oldConstraint = constraintsStack.pop();
	    condition.addConstraint(oldConstraint);
	    currentFieldConstraints--;
	}
	/* 'not' will only do this */
	condition.addConstraint(c);
	pendingConditions.pop();
	addConstraint(condition);
	
    }

    @SuppressWarnings("unchecked")
    private void createFieldConstraint() {
	if (currentFieldName == null) {
	    return;
	}
	/* we're only interested in the last value in the stack */
	Constraint<Object> fc = new FieldConstraint(currentFieldName, constraintsStack.pop());
	currentFieldConstraints=constraintsStack.size();
	addConstraint(fc);
    }

    private void createFilter() {
	filter = new Filter((ObjectConstraint) constraintsStack.pop(), fields);
    }

    @Override
    public R execute() throws MdbException {
	createFieldConstraint();
	createFilter();
	return command.execute();
    }

    /**
     * @return the filterBuilder
     */
    public FilterBuilder<T, R> getFilterBuilder() {
	return filterBuilder;
    }

    /**
     * @return the source
     */
    public T getSource() {
	return source;
    }

    public FieldFilterBuilder<T, R> setCurrentField(String fieldName) {
	createFieldConstraint();
	currentFieldConstraints = 0;
	FieldFilterBuilder<T, R> ff = new FieldFilterBuilder<T, R>(this);
	currentFieldName = fieldName;
	fields.add(fieldName);
	return ff;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(T source) {
	this.source = source;
    }

    /**
     * @return the filter
     */
    public Filter getFilter() {
	return filter;
    }

}
