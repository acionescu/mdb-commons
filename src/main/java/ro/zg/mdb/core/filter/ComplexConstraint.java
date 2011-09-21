package ro.zg.mdb.core.filter;

public abstract class ComplexConstraint<T> implements Constraint<T>{
    private Constraint<T> compiledValue;
    boolean isCompiled;
    
    protected abstract Constraint<T> compile();
    
    public abstract void addConstraint(Constraint<T> constraint);

    /**
     * @return the compiledValue
     */
    public Constraint<T> getCompiledValue() {
	if(!isCompiled) {
	    isCompiled=true;
	    compiledValue=compile();
	}
        return compiledValue;
    }
    
}
