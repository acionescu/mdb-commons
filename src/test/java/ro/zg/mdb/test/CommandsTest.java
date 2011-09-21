package ro.zg.mdb.test;

import java.awt.print.Book;

import org.junit.Assert;
import org.junit.Test;

import ro.zg.mdb.commands.GetCommand;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.SchemaManager;

public class CommandsTest {
    private SchemaManager sm = new SchemaManager("test", null);
    
    @Test
    public void testCommandBuilder() throws Exception {
	GetCommand<Book> getCommand = sm.createCommand(Book.class).get();
	Assert.assertNotNull(getCommand);
	getCommand.execute();
	
    }
}
