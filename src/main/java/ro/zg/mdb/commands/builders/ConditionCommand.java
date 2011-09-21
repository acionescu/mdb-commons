package ro.zg.mdb.commands.builders;

import ro.zg.mdb.commands.Command;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.constraints.And;
import ro.zg.mdb.core.filter.constraints.Or;


public class ConditionCommand<T,R> extends FilterContextHolder<T, R> implements Command<R>{
   
    

    public ConditionCommand(FilterBuilderContext<T, R> filterContext) {
	super(filterContext);
    }

    public FieldFilterAfterConditionBuilder<T,R> and(){
	filterContext.addCondition(new And<Object>());
	return new FieldFilterAfterConditionBuilder<T,R>(filterContext);
    }
    
    public FieldFilterAfterConditionBuilder<T,R> or(){
	filterContext.addCondition(new Or<Object>());
	return new FieldFilterAfterConditionBuilder<T,R>(filterContext);
    }


    @Override
    public R execute() throws MdbException {
	return filterContext.execute();
    }
    
    
}
