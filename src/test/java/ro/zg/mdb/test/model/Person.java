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
package ro.zg.mdb.test.model;

import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.annotations.PrimaryKey;
import ro.zg.mdb.core.annotations.Sequenced;

@Persistable
public class Person {
    @PrimaryKey
    @Sequenced(id="PersonPk")
    private long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private byte yearOfBirth;
    
    
    public Person(String firstName, String lastName) {
	super();
	this.firstName = firstName;
	this.lastName = lastName;
    }
    public Person(String firstName, String middleName, String lastName) {
	super();
	this.firstName = firstName;
	this.middleName = middleName;
	this.lastName = lastName;
    }
    /**
     * @return the id
     */
    public long getId() {
        return id;
    }
    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * @return the middleName
     */
    public String getMiddleName() {
        return middleName;
    }
    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * @return the yearOfBirth
     */
    public byte getYearOfBirth() {
        return yearOfBirth;
    }
    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }
    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * @param middleName the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * @param yearOfBirth the yearOfBirth to set
     */
    public void setYearOfBirth(byte yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
	result = prime * result + (int) (id ^ (id >>> 32));
	result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
	result = prime * result + ((middleName == null) ? 0 : middleName.hashCode());
	result = prime * result + yearOfBirth;
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
	Person other = (Person) obj;
	if (firstName == null) {
	    if (other.firstName != null)
		return false;
	} else if (!firstName.equals(other.firstName))
	    return false;
	if (id != other.id)
	    return false;
	if (lastName == null) {
	    if (other.lastName != null)
		return false;
	} else if (!lastName.equals(other.lastName))
	    return false;
	if (middleName == null) {
	    if (other.middleName != null)
		return false;
	} else if (!middleName.equals(other.middleName))
	    return false;
	if (yearOfBirth != other.yearOfBirth)
	    return false;
	return true;
    }
    
    
}
