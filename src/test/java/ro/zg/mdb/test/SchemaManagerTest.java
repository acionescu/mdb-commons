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

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Test;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.MdbInstance;
import ro.zg.mdb.core.meta.SchemaManager;
import ro.zg.mdb.core.meta.data.MdbConfig;
import ro.zg.mdb.persistence.MemoryPersistenceManager;
import ro.zg.mdb.test.model.Book;
import ro.zg.mdb.test.model.Entity;
import ro.zg.mdb.test.model.User;

public class SchemaManagerTest {

    @Test
    public void testSchemaManager() throws MdbException {
	MemoryPersistenceManager mpm = new MemoryPersistenceManager();
	SchemaManager sm = new MdbInstance("TestInstance", mpm, new MdbConfig()).getSchema("TestSchema");
	

	Book book = new Book();
	book.setId(1L);
	book.setAuthor("Gigel de la Constanţa");
	book.setTitle("Cum trece valurile vieţii");
	book.setPublisher("Titanic Vas");
	book.setReleaseYear(1999);
	book.setPrintTimestamp(System.currentTimeMillis());

	Book savedBook = sm.createCommand(Book.class).insert(book).execute();
	Assert.assertEquals(book, savedBook);

	Collection<Book> allBooks = sm.createCommand(Book.class).get().execute();
	Assert.assertNotNull(allBooks);
	Assert.assertTrue(allBooks.size() == 1);

	/* add another book */
	Book book2 = new Book();
	book2.setId(2L);
	book2.setAuthor("Daniel Quinn");
	book2.setTitle("The Story of B");
	book2.setPublisher("Bantamdell");
	book2.setReleaseYear(1789);
	book2.setPrintTimestamp(System.currentTimeMillis());
	
	Book savedBook2 = sm.createCommand(Book.class).insert(book2).execute();
	Assert.assertEquals(book2, savedBook2);
	mpm.print(System.out);
	
	allBooks = sm.createCommand(Book.class).get().execute();
	Assert.assertNotNull(allBooks);
	Assert.assertTrue(allBooks.size() == 2);
	
	Collection<Book> yearSearch=sm.createCommand(Book.class).get().where().field("releaseYear").eq(1999).execute();
	Assert.assertTrue(yearSearch.size()==1);
	Assert.assertTrue(yearSearch.contains(book));
	
	yearSearch=sm.createCommand(Book.class).get().where().field("releaseYear").not().eq(1999).execute();
	Assert.assertTrue(yearSearch.size()==1);
	Assert.assertTrue(yearSearch.contains(book2));
	
	Collection<Book> search3=sm.createCommand(Book.class).get().where().field("releaseYear").eq(1999).and().field("author").eq("Zuza").execute();
	Assert.assertTrue(search3.isEmpty());
	
	Collection<Book> search4=sm.createCommand(Book.class).get().where().field("releaseYear").eq(1999).and().field("id").eq(1L).execute();
	Assert.assertTrue(search4.size()==1);
	Assert.assertTrue(search4.contains(book));
	
	Collection<Book> search6=sm.createCommand(Book.class).get().where().field("id").gt(0L).execute();
	System.out.println(search6.size());
	Assert.assertTrue(search6.size()==2);
	
	Collection<Book> search7=sm.createCommand(Book.class).get().where().field("id").lt(2L).execute();
	System.out.println(search7.size());
	Assert.assertTrue(search7.size()==1);
	
	Collection<Book> search8=sm.createCommand(Book.class).get().where().field("id").between(-6L, 6L).execute();
	Assert.assertTrue(search8.size()==2);
	
	/* test update */
	
	Book update1Book=new Book();
	update1Book.setPublisher("Changed");
	long update1 = sm.createCommand(Book.class).update(update1Book, "publisher").where().field("id").eq(book2.getId()).execute();
	Assert.assertTrue(update1==1);
	mpm.print(System.out);
	
	Collection<Book> afterUpdate1 = sm.createCommand(Book.class).get("publisher").where().field("id").eq(book2.getId()).execute();
	Assert.assertTrue(afterUpdate1.size()==1);
	Assert.assertTrue(afterUpdate1.contains(update1Book));
	
	Book update2Book=new Book();
	update2Book.setPublisher("Same publisher");
	long update2 = sm.createCommand(Book.class).update(update2Book, "publisher").execute();
	Assert.assertTrue(update2==2);
	
	Collection<Book> afterUpdate2=sm.createCommand(Book.class).get("publisher").execute();
	Assert.assertTrue(afterUpdate2.size()==2);
	for(Book b : afterUpdate2) {
	    Assert.assertEquals(update2Book, b);
	}
	
	Book update3Book=new Book();
	update3Book.setPublisher("Changed again");
	long update3 = sm.createCommand(Book.class).update(update3Book, "publisher").where().field("id").not().eq(book2.getId()).execute();
	Assert.assertTrue(update3==1);
	
	Collection<Book> afterUpdate3 = sm.createCommand(Book.class).get("publisher").where().field("id").eq(book.getId()).execute();
	Assert.assertTrue(afterUpdate3.size()==1);
	Assert.assertTrue(afterUpdate3.contains(update3Book));
	
	/* test delete */
	long del1 = sm.createCommand(Book.class).delete().where().field("id").eq(1L).execute();
	Assert.assertTrue(del1==1);
	
	Collection<Book> search5=sm.createCommand(Book.class).get().where().field("id").eq(1L).execute();
	Assert.assertTrue(search5.size()==0);
	
	long del2 = sm.createCommand(Book.class).delete().where().field("id").not().eq(1L).execute();
	Assert.assertTrue(del2==1);
	
	mpm.print(System.out);
    }

    @Test
    public void testNestedFeature() throws MdbException {
	MemoryPersistenceManager mpm = new MemoryPersistenceManager();
	SchemaManager sm = new MdbInstance("TestInstance", mpm, new MdbConfig()).getSchema("TestSchema");
	
	User user1 = new User("user1","I'm not hiding","simple@mdb.com");
	Entity post1=new Entity("test 1","let's see if we can save nested objects",user1);
	
	/* test create with inexistent nested object */
	post1 = sm.createCommand(Entity.class).insert(post1).execute();
	
	Assert.assertEquals(1L, (long)post1.getId());
	Assert.assertEquals(1L, (long)post1.getUser().getId());
	
	/* test successful search by nested field*/
	Collection<Entity> afterSave1 = sm.createCommand(Entity.class).get().where().field("user.username").eq("user1").execute();
	Assert.assertEquals(1, afterSave1.size());
	Assert.assertEquals(post1, new ArrayList<Entity>(afterSave1).get(0));
	
	/* test successful filter out by nested field */
	afterSave1 = sm.createCommand(Entity.class).get().where().field("user.username").eq("user2").execute();
	Assert.assertEquals(0, afterSave1.size());
	
	/* test successful search by nested object pk */
	afterSave1 = sm.createCommand(Entity.class).get().where().field("user.id").eq(1L).execute();
	Assert.assertEquals(1, afterSave1.size());
	Assert.assertEquals(post1, new ArrayList<Entity>(afterSave1).get(0));
	
	/* test successful filter by nested object pk */
	afterSave1 = sm.createCommand(Entity.class).get().where().field("user.id").eq(5L).execute();
	Assert.assertEquals(0, afterSave1.size());
	
	/* test successful search by main object and nested object indexed fields */
	afterSave1 = sm.createCommand(Entity.class).get().where().field("user.id").eq(1L).and().field("id").eq(1L).execute();
	Assert.assertEquals(1, afterSave1.size());
	Assert.assertEquals(post1, new ArrayList<Entity>(afterSave1).get(0));
	
	/* test successful filter by main or nested object indexed field */
	afterSave1 = sm.createCommand(Entity.class).get().where().field("user.id").eq(1L).and().field("id").eq(5L).execute();
	Assert.assertEquals(0, afterSave1.size());
	
	/* test create with existent nested object */
	Entity post2=new Entity("second post", "Will test insert with existent neted object", user1);
	
	post2=sm.createCommand(Entity.class).insert(post2).execute();
	Assert.assertEquals(1L, (long)post2.getUser().getId());
	
	
	mpm.print(System.out);
    }
    
}
