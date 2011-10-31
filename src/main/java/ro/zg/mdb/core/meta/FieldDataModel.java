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
package ro.zg.mdb.core.meta;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.util.data.GenericNameValue;

public class FieldDataModel {
    private String name;
    private DataModel<?> dataModel;
    private boolean required;
    private boolean primaryKey;
    private String uniqueIndexId;
    private boolean indexed;
    private String sequenceId;
    private int position=-1;

    public FieldDataModel(String name, DataModel<?> dataModel) {
	this.name = name;
	this.dataModel=dataModel;
    }
    

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    
    
    /**
     * @return the dataModel
     */
    public DataModel<?> getDataModel() {
        return dataModel;
    }


    /**
     * @return the type
     */
    public Class<?> getType() {
	return dataModel.getType();
    }


    /**
     * @return the required
     */
    public boolean isRequired() {
	return required;
    }

    /**
     * @return the primaryKey
     */
    public boolean isPrimaryKey() {
	return primaryKey;
    }

    /**
     * @return the indexed
     */
    public boolean isIndexed() {
	return indexed;
    }

    /**
     * @param required
     *            the required to set
     */
    public void setRequired(boolean required) {
	this.required = required;
    }

    /**
     * @param primaryKey
     *            the primaryKey to set
     */
    public void setPrimaryKey(boolean primaryKey) {
	this.primaryKey = primaryKey;
	if (primaryKey == true) {
	    this.required = true;
	    this.indexed=true;
	}
    }

    /**
     * @return the uniqueIndexId
     */
    public String getUniqueIndexId() {
	return uniqueIndexId;
    }

    /**
     * @param uniqueIndexId
     *            the uniqueIndexId to set
     */
    public void setUniqueIndexId(String uniqueIndexId) {
	this.uniqueIndexId = uniqueIndexId;
	this.required = true;
	this.indexed=true;
    }

    /**
     * @param indexed
     *            the indexed to set
     */
    public void setIndexed(boolean indexed) {
	this.indexed = indexed;
    }

    /**
     * @return the sequenceId
     */
    public String getSequenceId() {
	return sequenceId;
    }

    /**
     * @param sequenceId
     *            the sequenceId to set
     */
    public void setSequenceId(String sequenceId) {
	this.sequenceId = sequenceId;
    }
    
    
    
    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }
    

    public void testValue(Object value) throws MdbException {
	if (required && value == null) {
	    throw new MdbException(MdbErrorType.REQUIRED, new GenericNameValue(name, value));
	}
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (indexed ? 1231 : 1237);
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + position;
	result = prime * result + (primaryKey ? 1231 : 1237);
	result = prime * result + (required ? 1231 : 1237);
	result = prime * result + ((sequenceId == null) ? 0 : sequenceId.hashCode());
	result = prime * result + ((dataModel == null) ? 0 : dataModel.hashCode());
	result = prime * result + ((uniqueIndexId == null) ? 0 : uniqueIndexId.hashCode());
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
	FieldDataModel other = (FieldDataModel) obj;
	if (indexed != other.indexed)
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (position != other.position)
	    return false;
	if (primaryKey != other.primaryKey)
	    return false;
	if (required != other.required)
	    return false;
	if (sequenceId == null) {
	    if (other.sequenceId != null)
		return false;
	} else if (!sequenceId.equals(other.sequenceId))
	    return false;
	if (dataModel == null) {
	    if (other.dataModel != null)
		return false;
	} else if (!dataModel.equals(other.dataModel))
	    return false;
	if (uniqueIndexId == null) {
	    if (other.uniqueIndexId != null)
		return false;
	} else if (!uniqueIndexId.equals(other.uniqueIndexId))
	    return false;
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "FieldDataModel [indexed=" + indexed + ", name=" + name + ", position=" + position + ", primaryKey="
		+ primaryKey + ", required=" + required + ", sequenceId=" + sequenceId + ", dataModel=" + dataModel
		+ ", uniqueIndexId=" + uniqueIndexId + "]";
    }

}
