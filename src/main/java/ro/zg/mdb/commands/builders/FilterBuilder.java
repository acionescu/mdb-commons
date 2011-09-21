package ro.zg.mdb.commands.builders;



public class FilterBuilder<T,R> extends FilterContextHolder<T,R>{
    
    public FilterBuilder(FilterBuilderContext<T, R> filterContext) {
	super(filterContext);
    }

    public FieldFilterBuilder<T,R> field(String fieldName) {
	return filterContext.setCurrentField(fieldName);
    }

    
}
