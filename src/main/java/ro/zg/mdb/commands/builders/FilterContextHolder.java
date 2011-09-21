package ro.zg.mdb.commands.builders;

public class FilterContextHolder<T,R> {
    protected FilterBuilderContext<T,R> filterContext;

    public FilterContextHolder(FilterBuilderContext<T,R> filterContext) {
	super();
	this.filterContext = filterContext;
    }
    
    
}
