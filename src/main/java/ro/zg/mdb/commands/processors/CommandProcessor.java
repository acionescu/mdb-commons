package ro.zg.mdb.commands.processors;

import ro.zg.mdb.commands.CommandContext;
import ro.zg.mdb.core.exceptions.MdbException;

public interface CommandProcessor<T,R> {
    
   R process(CommandContext<T> context) throws MdbException;

}
