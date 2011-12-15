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
package ro.zg.mdb.core.meta.data;

import java.util.Arrays;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.util.data.GenericNameValue;

public class LinkModel {
    private String name;
    private boolean first;
    private boolean lazy;
    /* in case of polimorfism */
    /**
     * If allowed types are specified, then the name of the table in which the links are stored will be the
     * concatenation between the actual name field and the class name of the type E.g. if the name is 'links_table_' and
     * the allowed types ro.example.A and ro.example.B are specified, then the links with objects of type A will be
     * stored in a table named 'links_table_ro.example.A' and links with objects of type B will be stored in a table
     * named 'links_table_ro.example.B' <br/>
     * If no allowed type is specified then only the name field will be used
     */
    private Class<?>[] allowedTypes;
    /* in case of map */
    private String keyName;
    
    private boolean multivalued;

    public LinkModel(String name, boolean first, boolean lazy) {
	super();
	this.name = name;
	this.first = first;
	this.lazy = lazy;
    }

    public LinkModel(String name, boolean first, boolean lazy, String keyName) {
	super();
	this.name = name;
	this.first = first;
	this.lazy = lazy;
	this.keyName = keyName;
    }

    public LinkModel(String name, boolean first, boolean lazy, Class<?>[] allowedTypes) {
	super();
	this.name = name;
	this.first = first;
	this.lazy = lazy;
	this.allowedTypes = allowedTypes;
    }

    public LinkModel(String name, boolean first, boolean lazy, Class<?>[] allowedTypes, String keyName) {
	super();
	this.name = name;
	this.first = first;
	this.lazy = lazy;
	this.allowedTypes = allowedTypes;
	this.keyName = keyName;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @return the first
     */
    public boolean isFirst() {
	return first;
    }

    /**
     * @return the lazy
     */
    public boolean isLazy() {
	return lazy;
    }

    /**
     * @return the keyName
     */
    public String getKeyName() {
	return keyName;
    }

    /**
     * @return the allowedTypes
     */
    public Class<?>[] getAllowedTypes() {
	return allowedTypes;
    }

    public boolean isPolymorphic() {
	return allowedTypes != null && allowedTypes.length > 0;
    }

    private boolean isTypeAllowed(Class<?> type) {
	if(type == null) {
	    return false;
	}
	for (Class<?> t : allowedTypes) {
	    if (t.equals(type)) {
		return true;
	    }
	}
	return false;
    }
    
    public String getFullName(Class<?> type) throws MdbException {
	if(!isPolymorphic()) {
	    return name;
	}
	if(!isTypeAllowed(type)) {
	    throw new MdbException(MdbErrorType.WRONG_LINK_TYPE, new GenericNameValue("type",type));
	}
	return name+type.getName();
    }

    
    /**
     * @return the multivalued
     */
    public boolean isMultivalued() {
        return multivalued;
    }

    /**
     * @param multivalued the multivalued to set
     */
    public void setMultivalued(boolean multivalued) {
        this.multivalued = multivalued;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Arrays.hashCode(allowedTypes);
	result = prime * result + (first ? 1231 : 1237);
	result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
	result = prime * result + (lazy ? 1231 : 1237);
	result = prime * result + (multivalued ? 1231 : 1237);
	result = prime * result + ((name == null) ? 0 : name.hashCode());
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
	LinkModel other = (LinkModel) obj;
	if (!Arrays.equals(allowedTypes, other.allowedTypes))
	    return false;
	if (first != other.first)
	    return false;
	if (keyName == null) {
	    if (other.keyName != null)
		return false;
	} else if (!keyName.equals(other.keyName))
	    return false;
	if (lazy != other.lazy)
	    return false;
	if (multivalued != other.multivalued)
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	return true;
    }

}
