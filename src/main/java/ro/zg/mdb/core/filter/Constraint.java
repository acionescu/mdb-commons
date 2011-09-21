package ro.zg.mdb.core.filter;

import ro.zg.mdb.core.meta.FieldDataModel;


public interface Constraint<T> {
    /**
     * Checks if the specified value satisfies this constraint
     * @param value
     * @return true if the value is allowd, false otherwise
     */
    boolean isSatisfied(T value);
    /**
     * Checks if the specified constraint is logically possible
     * @param context
     * @return true if possible, false otherwise
     */
    boolean isPossible(FieldDataModel fieldDataModel);
    
    /**
     * Processes the possible values that a constraint allowes
     * and does also some checks if such a value is at all possible
     * with the current {@link FieldDataModel}
     * @param context
     * @return - true if there is a posibility for this constrain to be satisfied, false otherwise
     */
    boolean process(FieldConstraintContext context);
    
    Constraint<T> and(Constraint<T> c);
    
    Constraint<T> or(Constraint<T> c);
    
    Constraint<T> not();
    
}
