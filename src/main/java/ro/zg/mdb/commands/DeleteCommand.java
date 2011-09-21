package ro.zg.mdb.commands;

import ro.zg.mdb.commands.builders.CommandBuilderContext;
import ro.zg.mdb.commands.builders.FilteredCommand;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;

public class DeleteCommand<T> extends FilteredCommand<T,Long>{

    public DeleteCommand(CommandBuilderContext<T> commandContext) {
	super(commandContext);
    }

    @Override
    public Long execute() throws MdbException {
	Filter filter =filterContext.getFilter();
	return commandContext.getObjectDataManager().delete(filter);
    }

    

}
