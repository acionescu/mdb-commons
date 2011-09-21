package ro.zg.mdb.commands.builders;


public abstract class FilteredCommand<T,R> extends AbstractCommand<T,R>{
    protected FilterBuilderContext<T, R> filterContext;
   
    public FilteredCommand(CommandBuilderContext<T> commandContext) {
	super(commandContext);
	filterContext=new FilterBuilderContext<T, R>(this);
    }

    public FilterBuilder<T,R> where() {
	return filterContext.getFilterBuilder();
    }
}
