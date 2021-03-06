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
package ro.zg.mdb.test.model;

import ro.zg.mdb.core.annotations.Indexed;
import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.annotations.ObjectId;
import ro.zg.mdb.core.annotations.Unique;
import ro.zg.metadata.annotations.validation.Required;

@Persistable
public class Book {
    @ObjectId
    private String id;
    @Unique(name=1)
    private String author;
    @Unique(name=1)
    private String title;
    @Unique(name=1)
    private String publisher;
    @Required
    @Indexed
    private Integer releaseYear;    
    @Unique(name=2)
    private long printTimestamp;
    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }
    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @param publisher the publisher to set
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    /**
     * @return the printTimestamp
     */
    public long getPrintTimestamp() {
        return printTimestamp;
    }
    /**
     * @param printTimestamp the printTimestamp to set
     */
    public void setPrintTimestamp(long printTimestamp) {
        this.printTimestamp = printTimestamp;
    }
    /**
     * @return the releaseYear
     */
    public Integer getReleaseYear() {
        return releaseYear;
    }
    /**
     * @param releaseYear the releaseYear to set
     */
    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }
  
        
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Book [id=" + id + ", author=" + author + ", title=" + title + ", publisher=" + publisher
		+ ", releaseYear=" + releaseYear + ", printTimestamp=" + printTimestamp + "]";
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((author == null) ? 0 : author.hashCode());
	result = prime * result + ((id == null) ? 0 : id.hashCode());
	result = prime * result + (int) (printTimestamp ^ (printTimestamp >>> 32));
	result = prime * result + ((publisher == null) ? 0 : publisher.hashCode());
	result = prime * result + ((releaseYear == null) ? 0 : releaseYear.hashCode());
	result = prime * result + ((title == null) ? 0 : title.hashCode());
	return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Book other = (Book) obj;
	if (author == null) {
	    if (other.author != null)
		return false;
	} else if (!author.equals(other.author))
	    return false;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	if (printTimestamp != other.printTimestamp)
	    return false;
	if (publisher == null) {
	    if (other.publisher != null)
		return false;
	} else if (!publisher.equals(other.publisher))
	    return false;
	if (releaseYear == null) {
	    if (other.releaseYear != null)
		return false;
	} else if (!releaseYear.equals(other.releaseYear))
	    return false;
	if (title == null) {
	    if (other.title != null)
		return false;
	} else if (!title.equals(other.title))
	    return false;
	return true;
    }
    
}
