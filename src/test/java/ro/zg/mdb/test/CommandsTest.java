/*******************************************************************************
 * Copyright (c) 2011 Adrian Cristian Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Adrian Cristian Ionescu - initial API and implementation
 ******************************************************************************/
package ro.zg.mdb.test;

import java.awt.print.Book;

import org.junit.Assert;
import org.junit.Test;

import ro.zg.mdb.commands.GetCommand;
import ro.zg.mdb.core.meta.SchemaManager;
import ro.zg.mdb.core.meta.data.SchemaConfig;

public class CommandsTest {
    
    
    @Test
    public void testCommandBuilder() throws Exception {
	SchemaManager sm = new SchemaManager(null,new SchemaConfig("test"));
	GetCommand<Book> getCommand = sm.createCommand(Book.class).get();
	Assert.assertNotNull(getCommand);
	getCommand.execute();
	
    }
}
