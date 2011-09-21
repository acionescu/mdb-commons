package ro.zg.mdb.commands.builders;

import ro.zg.mdb.commands.Command;

public abstract class AbstractCommand<T,R> implements Command<R>{
    protected CommandBuilderContext<T> commandContext;
    
    
    public AbstractCommand(CommandBuilderContext<T> commandContext) {
	super();
	this.commandContext = commandContext;
    }

}
