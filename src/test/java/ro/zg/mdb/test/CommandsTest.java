/*******************************************************************************
 * Copyright 2011 Adrian Cristian Ionescu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ro.zg.mdb.test;

import java.awt.print.Book;

import org.junit.Assert;
import org.junit.Test;

import ro.zg.mdb.commands.GetCommand;
import ro.zg.mdb.core.meta.SchemaConfig;
import ro.zg.mdb.core.meta.SchemaManager;

public class CommandsTest {
    
    
    @Test
    public void testCommandBuilder() throws Exception {
	SchemaManager sm = new SchemaManager(null,new SchemaConfig("test"));
	GetCommand<Book> getCommand = sm.createCommand(Book.class).get();
	Assert.assertNotNull(getCommand);
	getCommand.execute();
	
    }
}
