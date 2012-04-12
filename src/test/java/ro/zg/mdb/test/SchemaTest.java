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

import org.junit.Test;

import ro.zg.mdb.core.meta.persistence.data.Schema;
import ro.zg.mdb.core.meta.persistence.data.SchemaConfig;
import ro.zg.mdb.test.model.Book;

public class SchemaTest {
    private Schema schema = new Schema(new SchemaConfig("test"));

    @Test
    public void testGetObjectDataModel() {
	// System.out.println(bookDataModel);

	Long id = 1L;
	String author = "Gigel de la Constanţa";
	String title = "Cum trece valurile vieţii";
	String publisher = "Titanic Vas";
	int releaseYear = 1999;

	Book book = new Book();
	book.setAuthor(author);
	book.setTitle(title);
	book.setPublisher(publisher);
	book.setReleaseYear(releaseYear);
	book.setPrintTimestamp(System.currentTimeMillis());

//	ObjectContext oc = null;
//	PersistentObjectMetadata<Book> odModel=schema.getObjectDataModel(Book.class);
//	SchemaContext schemaContext = new SchemaContext(new SequencesManager());
//	ObjectDataManager<Book> sm=new ObjectDataManager<Book>(null, odModel, schemaContext);
//
//	/* test null pk */
//	try {
//	    oc = sm.getObjectContext(book);
//	    Assert.fail("Should have failed due to null pk");
//	} catch (MdbException e) {
//	    Assert.assertEquals(MdbErrorType.REQUIRED, e.getErrorType());
//	}
//	book.setId(id);
//	/* test null unique index */
//	book.setAuthor(null);
//	try {
//	    oc = sm.getObjectContext(book);
//	    Assert.fail("Should have failed due to null unique index");
//	} catch (MdbException e) {
//	    Assert.assertEquals(MdbErrorType.REQUIRED, e.getErrorType());
//	}
//
//	book.setAuthor(author);
//
//	/* test null required */
//	book.setReleaseYear(null);
//	try {
//	    oc = sm.getObjectContext(book);
//	    Assert.fail("Should have failed due to null required field");
//	} catch (MdbException e) {
//	    Assert.assertEquals(MdbErrorType.REQUIRED, e.getErrorType());
//	}
//	book.setReleaseYear(releaseYear);
//
//	/* test ok */
//	try {
//	    oc = sm.getObjectContext(book);
//	    Assert.assertNotNull(oc);
//	} catch (MdbException e) {
//	    Assert.fail("Should have succeeded");
//	}
//
//	/* test indexed values */
//	Map<String, String> expectedIndexedValues = new HashMap<String, String>();
//	expectedIndexedValues.put("releaseYear", book.getReleaseYear().toString());
//
//	Assert.assertEquals(expectedIndexedValues, oc.getIndexedValues());
//
//	/* test data */
//	String expectedData = id + Constants.COLUMN_SEPARATOR + author + Constants.COLUMN_SEPARATOR + title
//		+ Constants.COLUMN_SEPARATOR + publisher + Constants.COLUMN_SEPARATOR + releaseYear
//		+ Constants.COLUMN_SEPARATOR + book.getPrintTimestamp();
//
//	Assert.assertEquals(expectedData, oc.getData());
//
//	/* object restore from raw data */
//	try {
//	    Book restoredBook = schema.getObjectFromString(oc.getData(), Book.class, new Filter());
//	    Assert.assertEquals(book, restoredBook);
//	} catch (MdbException e) {
//	    Assert.fail("The restored object should have matched the original one");
//	}
    }

}
