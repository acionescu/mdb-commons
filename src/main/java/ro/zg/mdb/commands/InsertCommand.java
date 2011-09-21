package ro.zg.mdb.commands;

import ro.zg.mdb.commands.builders.AbstractCommand;
import ro.zg.mdb.commands.builders.CommandBuilderContext;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.SchemaManager;


public class InsertCommand<T> extends AbstractCommand<T,T>{

    public InsertCommand(CommandBuilderContext<T> commandContext) {
	super(commandContext);
    }

    @Override
    public T execute() throws MdbException {
	T source = commandContext.getSource();
	return commandContext.getObjectDataManager().save(source);
    }

    

}
