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
package ro.zg.mdb.core.meta.data;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.zg.util.data.reflection.ReflectionUtility;

public class DataModel<T> {
    private Class<T> type;
    private boolean complexType;
    private boolean multivalued;
    private boolean set;
    private boolean list;
    private boolean map;
    
    public DataModel(Class<T> type, boolean complexType) {
	super();
	this.type = type;
	this.complexType = complexType;
	initMultivalued();
    }


    public DataModel(Class<T> type) {
	super();
	this.type = type;
	initMultivalued();
    }
    
    
    private void initMultivalued() {
	if( checkSuperType(Collection.class)) {
	    if(checkSuperType(List.class)) {
		list=true;
	    }
	    else if(checkSuperType(Set.class)) {
		set=true;
	    }
	    else {
		throw new IllegalArgumentException("Unknown collection "+type.getName());
	    }
	    multivalued=true;
	}
	else if(checkSuperType(Map.class)) {
	    map=true;
	    multivalued=true;
	}
    }
    
    private boolean checkSuperType(Class<?> superClass) {
	return ReflectionUtility.checkInstanceOf(type, superClass);
    }
    
    
    

    /**
     * @return the multivalued
     */
    public boolean isMultivalued() {
        return multivalued;
    }


    /**
     * @return the set
     */
    public boolean isSet() {
        return set;
    }


    /**
     * @return the list
     */
    public boolean isList() {
        return list;
    }


    /**
     * @return the map
     */
    public boolean isMap() {
        return map;
    }


    /**
     * @return the type
     */
    public Class<T> getType() {
        return type;
    }


    /**
     * @return the complexType
     */
    public boolean isComplexType() {
        return complexType;
    }
    
    public String getTypeName() {
	return type.getName();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "DataModel [type=" + type + ", complexType=" + complexType + "]";
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (complexType ? 1231 : 1237);
	result = prime * result + ((type == null) ? 0 : type.hashCode());
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
	DataModel other = (DataModel) obj;
	if (complexType != other.complexType)
	    return false;
	if (type == null) {
	    if (other.type != null)
		return false;
	} else if (!type.equals(other.type))
	    return false;
	return true;
    }
    
    
    
}
