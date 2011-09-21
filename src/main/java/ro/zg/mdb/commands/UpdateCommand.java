package ro.zg.mdb.commands;

import java.util.Arrays;
import java.util.LinkedHashSet;

import ro.zg.mdb.commands.builders.CommandBuilderContext;
import ro.zg.mdb.commands.builders.FilteredCommand;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;

public class UpdateCommand<T> extends FilteredCommand<T,Long>{

    public UpdateCommand(CommandBuilderContext<T> commandContext) {
	super(commandContext);
    }

    @Override
    public Long execute() throws MdbException {
	T source = commandContext.getSource();
	Filter filter =filterContext.getFilter();
	String[] targetFields=commandContext.getFields();
	
	if(targetFields != null) {
	    filter.setTargetFields(new LinkedHashSet<String>(Arrays.asList(targetFields)));
	}
	return commandContext.getObjectDataManager().update(source, filter);
    }

   

}
