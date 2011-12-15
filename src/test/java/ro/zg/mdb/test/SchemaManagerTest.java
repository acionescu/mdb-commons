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
	book.setAuthor("Gigel de la Constanţa");
	book.setTitle("Cum trece valurile vieţii");
	book.setPublisher("Titanic Vas");
	book.setReleaseYear(1999);
	book.setPrintTimestamp(System.currentTimeMillis());

	Book savedBook = sm.createCommand(Book.class).insert(book).execute();
	Assert.assertEquals(book, savedBook);
	Assert.assertNotNull(savedBook.getId());

	Collection<Book> allBooks = sm.createCommand(Book.class).get().execute().getValues();
	Assert.assertNotNull(allBooks);
	Assert.assertTrue(allBooks.size() == 1);

	/* add another book */
	Book book2 = new Book();
	book2.setAuthor("Daniel Quinn");
	book2.setTitle("The Story of B");
	book2.setPublisher("Bantamdell");
	book2.setReleaseYear(1789);
	book2.setPrintTimestamp(System.currentTimeMillis());

	Book savedBook2 = sm.createCommand(Book.class).insert(book2).execute();
	Assert.assertEquals(book2, savedBook2);
	mpm.print(System.out);

	allBooks = sm.createCommand(Book.class).get().execute().getValues();
	Assert.assertNotNull(allBooks);
	Assert.assertTrue(allBooks.size() == 2);

	Collection<Book> yearSearch = sm.createCommand(Book.class).get().where().field("releaseYear").eq(1999)
		.execute().getValues();
	Assert.assertTrue(yearSearch.size() == 1);
	Assert.assertTrue(yearSearch.contains(book));

	yearSearch = sm.createCommand(Book.class).get().where().field("releaseYear").not().eq(1999).execute().getValues();
	Assert.assertTrue(yearSearch.size() == 1);
	Assert.assertTrue(yearSearch.contains(book2));

	Collection<Book> search3 = sm.createCommand(Book.class).get().where().field("releaseYear").eq(1999).and()
		.field("author").eq("Zuza").execute().getValues();
	Assert.assertTrue(search3.isEmpty());

	Collection<Book> search4 = sm.createCommand(Book.class).get().where().field("releaseYear").eq(1999).and()
		.field("id").eq(savedBook.getId()).execute().getValues();
	Assert.assertTrue(search4.size() == 1);
	Assert.assertTrue(search4.contains(book));

	Collection<Book> search6 = sm.createCommand(Book.class).get().where().field("releaseYear").gt(1500).execute().getValues();
	Assert.assertEquals(2, search6.size());

	Collection<Book> search7 = sm.createCommand(Book.class).get().where().field("releaseYear").lt(1995).execute().getValues();
	Assert.assertTrue(search7.size() == 1);

	Collection<Book> search8 = sm.createCommand(Book.class).get().where().field("releaseYear").between(1000, 2000)
		.execute().getValues();
	Assert.assertTrue(search8.size() == 2);

	/* test update */

	Book update1Book = new Book();
	update1Book.setPublisher("Changed");
	long update1 = sm.createCommand(Book.class).update(update1Book, "publisher").where().field("id")
		.eq(book2.getId()).execute();
	Assert.assertTrue(update1 == 1);
	mpm.print(System.out);

	Collection<Book> afterUpdate1 = sm.createCommand(Book.class).get("publisher").where().field("id")
		.eq(book2.getId()).execute().getValues();
	Assert.assertTrue(afterUpdate1.size() == 1);
	Assert.assertEquals(update1Book.getPublisher(), new ArrayList<Book>(afterUpdate1).get(0).getPublisher());

	Book update2Book = new Book();
	update2Book.setPublisher("Same publisher");
	long update2 = sm.createCommand(Book.class).update(update2Book, "publisher").execute();
	Assert.assertTrue(update2 == 2);

	Collection<Book> afterUpdate2 = sm.createCommand(Book.class).get("publisher").execute().getValues();
	Assert.assertTrue(afterUpdate2.size() == 2);
	for (Book b : afterUpdate2) {
	    Assert.assertEquals(update2Book.getPublisher(), b.getPublisher());
	}

	Book update3Book = new Book();
	update3Book.setPublisher("Changed again");
	long update3 = sm.createCommand(Book.class).update(update3Book, "publisher").where().field("id").not()
		.eq(book2.getId()).execute();
	Assert.assertTrue(update3 == 1);

	Collection<Book> afterUpdate3 = sm.createCommand(Book.class).get("publisher").where().field("id")
		.eq(book.getId()).execute().getValues();
	Assert.assertTrue(afterUpdate3.size() == 1);
	Assert.assertEquals(update3Book.getPublisher(), new ArrayList<Book>(afterUpdate3).get(0).getPublisher());

	/* test delete */
	long del1 = sm.createCommand(Book.class).delete().where().field("id").eq(book.getId()).execute();
	Assert.assertTrue(del1 == 1);

	Collection<Book> search5 = sm.createCommand(Book.class).get().where().field("id").eq(book.getId()).execute().getValues();
	Assert.assertTrue(search5.size() == 0);

	long del2 = sm.createCommand(Book.class).delete().where().field("id").not().eq(book.getId()).execute();
	Assert.assertTrue(del2 == 1);

	mpm.print(System.out);
    }

    @Test
    public void testNestedFeature() throws MdbException {
	MemoryPersistenceManager mpm = new MemoryPersistenceManager();
	SchemaManager sm = new MdbInstance("TestInstance", mpm, new MdbConfig()).getSchema("TestSchema");

	User user1 = new User("user1", "I'm not hiding", "simple@mdb.com");
	Entity post1 = new Entity("test 1", "let's see if we can save nested objects", user1);

	/* test create with inexistent nested object */
	post1 = sm.createCommand(Entity.class).insert(post1).execute();

	mpm.print(System.out);

	Assert.assertNotNull(post1.getId());
	Assert.assertNotNull(post1.getUser().getId());

	/* test successful search by nested field */
	Collection<Entity> afterSave1 = sm.createCommand(Entity.class).get().where().field("user.username").eq("user1")
		.execute().getValues();
	Assert.assertEquals(1, afterSave1.size());
	Assert.assertEquals(post1, new ArrayList<Entity>(afterSave1).get(0));

	/* test successful filter out by nested field */
	afterSave1 = sm.createCommand(Entity.class).get().where().field("user.username").eq("user2").execute().getValues();
	Assert.assertEquals(0, afterSave1.size());

	/* test successful search by nested object pk */
	afterSave1 = sm.createCommand(Entity.class).get().where().field("user.id").eq(user1.getId()).execute().getValues();
	Assert.assertEquals(1, afterSave1.size());
	Assert.assertEquals(post1, new ArrayList<Entity>(afterSave1).get(0));

	/* test successful filter by nested object pk */
	afterSave1 = sm.createCommand(Entity.class).get().where().field("user.id").eq("inexistent id").execute().getValues();
	Assert.assertEquals(0, afterSave1.size());

	/* test successful search by main object and nested object indexed fields */
	afterSave1 = sm.createCommand(Entity.class).get().where().field("user.id").eq(user1.getId()).and().field("id")
		.eq(post1.getId()).execute().getValues();
	Assert.assertEquals(1, afterSave1.size());
	Assert.assertEquals(post1, new ArrayList<Entity>(afterSave1).get(0));

	/* test successful filter by main or nested object indexed field */
	afterSave1 = sm.createCommand(Entity.class).get().where().field("user.id").eq(user1.getId()).and().field("id")
		.eq("inexistent id").execute().getValues();
	Assert.assertEquals(0, afterSave1.size());

	/* test create with existent nested object */
	Entity post2 = new Entity("second post", "Will test insert with existent neted object", user1);

	post2 = sm.createCommand(Entity.class).insert(post2).execute();
	Assert.assertEquals(user1.getId(), post2.getUser().getId());

	Collection<Entity> fetchResult1 = sm.createCommand(Entity.class).get().where().field("id").eq(post2.getId())
		.execute().getValues();
	Assert.assertEquals(1, fetchResult1.size());
	Entity fetchedPost2 = new ArrayList<Entity>(fetchResult1).get(0);
	Assert.assertEquals(post2, fetchedPost2);

	User user2 = new User("user2", "password", "user2@mdb.com");
	Entity post3 = new Entity("post 3", "will test update an delte", user2);

	post3 = sm.createCommand(Entity.class).insert(post3).execute();
	mpm.print(System.out);
	/* try to delete user2, should fail because it is being reference by post3 */
	try {
	    sm.createCommand(User.class).delete().where().field("id").eq(user2.getId()).execute();
	    mpm.print(System.out);
	    Assert.fail("Should have failed due to direct reference by post3");
	} catch (MdbException e) {
	    /* pass */
	}

	user2.setPassword("123456");
	post3.setMessage("will test update an delete");

	/* update the entity, only message field */
	long updated1 = sm.createCommand(Entity.class).update(post3, "message").where().field("id").eq(post3.getId())
		.execute();
	Assert.assertEquals(1L, updated1);

	Collection<Entity> afterUpdate1 = sm.createCommand(Entity.class).get().where().field("id").eq(post3.getId())
		.execute().getValues();
	Assert.assertEquals(1, afterUpdate1.size());
	Entity post3AfterUpdate1 = new ArrayList<Entity>(afterUpdate1).get(0);

	Assert.assertEquals(post3.getMessage(), post3AfterUpdate1.getMessage());
	/* the user shouldn't have updated because it was not specified as an update field */
	Assert.assertTrue(post3.getUser().getPassword() != post3AfterUpdate1.getUser().getPassword());

	/* test update all fields */

	post3.setTitle("post3 updated");

	long updated2 = sm.createCommand(Entity.class).update(post3).where().field("id").eq(post3.getId()).execute();
	Assert.assertEquals(1L, updated2);

	/* check if we still have only 2 users */
	Collection<User> users = sm.createCommand(User.class).get().execute().getValues();
	Assert.assertEquals(2, users.size());

	Collection<Entity> afterUpdate2 = sm.createCommand(Entity.class).get().where().field("id").eq(post3.getId())
		.execute().getValues();
	Assert.assertEquals(1, afterUpdate2.size());
	Entity post3AfterUpdate2 = new ArrayList<Entity>(afterUpdate2).get(0);

	Assert.assertEquals(post3, post3AfterUpdate2);

	/* test changing the reference to a nested object */
	post3.setUser(user1);

	long updated3 = sm.createCommand(Entity.class).update(post3).where().field("id").eq(post3.getId()).execute();
	Assert.assertEquals(1L, updated3);

	Collection<Entity> afterUpdate3 = sm.createCommand(Entity.class).get().where().field("id").eq(post3.getId())
		.execute().getValues();
	Assert.assertEquals(1, afterUpdate3.size());
	Entity post3AfterUpdate3 = new ArrayList<Entity>(afterUpdate3).get(0);

	Assert.assertEquals(user1, post3AfterUpdate3.getUser());

	/* test successful delete */
	long delete1 = sm.createCommand(User.class).delete().where().field("id").eq(user2.getId()).execute();
	Assert.assertEquals(1L, delete1);

	Collection<User> afterDelete1 = sm.createCommand(User.class).get().where().field("id").eq(user2.getId())
		.execute().getValues();
	Assert.assertEquals(0, afterDelete1.size());
	
	/* test successful delete of objects with other nested objects but without a direct referece to it */
	
	long delete2 = sm.createCommand(Entity.class).delete().execute();
	Assert.assertEquals(3L, delete2);
	
	Collection<Entity> afterDelete2 = sm.createCommand(Entity.class).get().execute().getValues();
	Assert.assertEquals(0, afterDelete2.size());

	mpm.print(System.out);
    }

}
