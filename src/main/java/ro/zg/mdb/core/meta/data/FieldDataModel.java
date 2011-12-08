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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.annotations.Link;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.reflection.ReflectionUtility;

public class FieldDataModel<T> {
    public static Class<?> DEFAULT_SET_IMPLEMENTATION = HashSet.class;
    public static Class<?> DEFAULT_LIST_IMPLEMENTATION = ArrayList.class;
    public static Class<?> DEFAULT_MAP_IMPLEMENTATION = HashMap.class;

    private String name;
    @Link(name = "field_data_model_", lazy = false, allowedTypes = { ObjectDataModel.class, MapDataModel.class, CollectionDataModel.class })
    private DataModel<T> dataModel;
    private Class<? extends T> implementation;
    private boolean required;
    private boolean objectId;
    private boolean indexed;
    private LinkModel linkModel;
    private String uniqueIndexId;
    private String sequenceId;

    public FieldDataModel(String name, DataModel<T> dataModel) {
	this.name = name;
	this.dataModel = dataModel;
    }

    public Object createFromValue(Collection<?> values) throws MdbException {

	if (dataModel.isMultivalued()) {
	    Class<?> impl = implementation;

	    try {
		if (dataModel instanceof CollectionDataModel) {
		    CollectionDataModel<T> collectionModel = (CollectionDataModel<T>) dataModel;

		    if (collectionModel.isList()) {
			if (impl == null) {
			    impl = DEFAULT_LIST_IMPLEMENTATION;
			}
			return ReflectionUtility.createCollection(impl, values);
		    } else if (collectionModel.isSet()) {
			if (impl == null) {
			    impl = DEFAULT_SET_IMPLEMENTATION;
			}
			return ReflectionUtility.createCollection(impl, values);
		    }
		}

		else if (dataModel instanceof MapDataModel) {

		    if (impl == null) {
			impl = DEFAULT_MAP_IMPLEMENTATION;
		    }
		    return ReflectionUtility.createMap(impl, values, linkModel.getKeyName());
		}
	    } catch (Exception e) {
		throw new MdbException(e, MdbErrorType.GET_FIELD_ERROR, new GenericNameValue("name", name),
			new GenericNameValue("value", values));
	    }
	}

	if (values.size() > 0) {
	    return new ArrayList(values).get(0);
	}
	return null;
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
    public DataModel<T> getDataModel() {
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
	this.indexed = true;
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
     * @return the objectId
     */
    public boolean isObjectId() {
	return objectId;
    }

    /**
     * @param objectId
     *            the objectId to set
     */
    public void setObjectId(boolean objectId) {
	this.objectId = objectId;
    }

    public void testValue(Object value) throws MdbException {
	if (required && value == null) {
	    throw new MdbException(MdbErrorType.REQUIRED, new GenericNameValue(name, value));
	}
    }

    /**
     * @return the linkModel
     */
    public LinkModel getLinkModel() {
	return linkModel;
    }

    /**
     * @param linkModel
     *            the linkModel to set
     */
    public void setLinkModel(LinkModel linkModel) {
	this.linkModel = linkModel;
	// /* if this is a direct link ( linkModel.first is true ) , add a reference on the data model of the field */
	// if (linkModel.isFirst()) {
	// ObjectDataModel<T> fodm=null;
	// if(dataModel.isMultivalued()) {
	// fodm = (ObjectDataModel<T>)((MultivaluedDataModel)dataModel).getMultivaluedType();
	// }
	//
	// ObjectDataModel<T> fodm = (ObjectDataModel<T>) getDataModel();
	// fodm.addReference(linkModel);
	// }
    }

    /**
     * @param implementation
     *            the implementation to set
     */
    public void setImplementation(Class<?> implementation) {
	this.implementation = (Class<? extends T>) implementation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((dataModel == null) ? 0 : dataModel.hashCode());
	result = prime * result + ((implementation == null) ? 0 : implementation.hashCode());
	result = prime * result + (indexed ? 1231 : 1237);
	result = prime * result + ((linkModel == null) ? 0 : linkModel.hashCode());
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + (objectId ? 1231 : 1237);
	result = prime * result + (required ? 1231 : 1237);
	result = prime * result + ((sequenceId == null) ? 0 : sequenceId.hashCode());
	result = prime * result + ((uniqueIndexId == null) ? 0 : uniqueIndexId.hashCode());
	return result;
    }

    /*
     * (non-Javadoc)
     * 
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
	if (dataModel == null) {
	    if (other.dataModel != null)
		return false;
	} else if (!dataModel.equals(other.dataModel))
	    return false;
	if (implementation == null) {
	    if (other.implementation != null)
		return false;
	} else if (!implementation.equals(other.implementation))
	    return false;
	if (indexed != other.indexed)
	    return false;
	if (linkModel == null) {
	    if (other.linkModel != null)
		return false;
	} else if (!linkModel.equals(other.linkModel))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (objectId != other.objectId)
	    return false;
	if (required != other.required)
	    return false;
	if (sequenceId == null) {
	    if (other.sequenceId != null)
		return false;
	} else if (!sequenceId.equals(other.sequenceId))
	    return false;
	if (uniqueIndexId == null) {
	    if (other.uniqueIndexId != null)
		return false;
	} else if (!uniqueIndexId.equals(other.uniqueIndexId))
	    return false;
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "FieldDataModel [name=" + name + ", dataModel=" + dataModel + ", implementation=" + implementation
		+ ", required=" + required + ", objectId=" + objectId + ", indexed=" + indexed + ", linkModel="
		+ linkModel + ", uniqueIndexId=" + uniqueIndexId + ", sequenceId=" + sequenceId + "]";
    }

}
