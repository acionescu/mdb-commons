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

import ro.zg.mdb.core.annotations.Persistable;

@Persistable
public class DataModel<T> {
    private Class<T> type;
    private boolean complexType;
    protected boolean multivalued;
    
    public DataModel(Class<T> type, boolean complexType) {
	super();
	this.type = type;
	this.complexType = complexType;
    }


    public DataModel(Class<T> type) {
	super();
	this.type = type;
    }
    
    
    /**
     * @return the multivalued
     */
    public boolean isMultivalued() {
        return multivalued;
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
