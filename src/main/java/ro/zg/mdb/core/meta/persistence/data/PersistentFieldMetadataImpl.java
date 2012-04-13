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
package ro.zg.mdb.core.meta.persistence.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.annotations.Link;
import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.metadata.commons.CollectionMetadata;
import ro.zg.metadata.commons.FieldMetadata;
import ro.zg.metadata.commons.MapMetadata;
import ro.zg.metadata.commons.Metadata;
import ro.zg.metadata.commons.MetadataDecoratorImpl;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.reflection.ReflectionUtility;

@Persistable
public class PersistentFieldMetadataImpl<T> extends MetadataDecoratorImpl<T, FieldMetadata<T, ? extends Metadata<T>>> implements PersistentFieldMetadata<T>{
    public static Class<?> DEFAULT_SET_IMPLEMENTATION = HashSet.class;
    public static Class<?> DEFAULT_LIST_IMPLEMENTATION = ArrayList.class;
    public static Class<?> DEFAULT_MAP_IMPLEMENTATION = HashMap.class;

//    @ObjectId
//    private String id;
//    private String name;
//
//    @Link(name = "field_data_model_", lazy = false, allowedTypes = { PersistentObjectMetadata.class, MapDataModel.class,
//	    CollectionMetadata.class, Metadata.class })
//    private Metadata<T> valueMetadata;
//    private boolean required;
    private boolean objectId;
    private boolean indexed;

    @Link(name = "field_link_model", lazy = false)
    private LinkMetadata linkMetadata;
    private String uniqueIndexId;
    private String sequenceId;
//    private Class<? extends T> implementation;

   

    public PersistentFieldMetadataImpl() {

    }

    public Object createFromValue(Collection<?> values) throws MdbException {
	if (nestedMetadata.isMultivalued()) {
	    Class<?> impl = getImplementation();
	    Metadata<?> valueMetadata = getValueMetadata();
	    try {
		if (valueMetadata instanceof CollectionMetadata) {
		    CollectionMetadata<T> collectionModel = (CollectionMetadata<T>) valueMetadata;

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
		    } else if (collectionModel.isArray()) {
			return values.toArray();
		    }
		}

		else if (valueMetadata instanceof MapMetadata) {

		    if (impl == null) {
			impl = DEFAULT_MAP_IMPLEMENTATION;
		    }
		    return ReflectionUtility.createMap(impl, values, linkMetadata.getKeyName());
		}
	    } catch (Exception e) {
		throw new MdbException(e, MdbErrorType.GET_FIELD_ERROR, new GenericNameValue("name", getName()),
			new GenericNameValue("value", values));
	    }
	}

	if (values.size() > 0) {
	    return new ArrayList(values).get(0);
	}
	return null;
    }



    /**
     * @return the indexed
     */
    public boolean isIndexed() {
	return indexed;
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

    /**
     * @return the linkMetadata
     */
    public LinkMetadata getLinkMetadata() {
	return linkMetadata;
    }

    /**
     * @param linkMetadata
     *            the linkMetadata to set
     */
    public void setLinkMetadata(LinkMetadata linkModel) {
	this.linkMetadata = linkModel;
	// /* if this is a direct link ( linkMetadata.first is true ) , add a reference on the data model of the field */
	// if (linkMetadata.isFirst()) {
	// PersistentObjectMetadata<T> fodm=null;
	// if(valueMetadata.isMultivalued()) {
	// fodm = (PersistentObjectMetadata<T>)((MultivaluedDataModel)valueMetadata).getMultivaluedType();
	// }
	//
	// PersistentObjectMetadata<T> fodm = (PersistentObjectMetadata<T>) getDataModel();
	// fodm.addReference(linkMetadata);
	// }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + (indexed ? 1231 : 1237);
	result = prime * result + ((linkMetadata == null) ? 0 : linkMetadata.hashCode());
	result = prime * result + (objectId ? 1231 : 1237);
	result = prime * result + ((sequenceId == null) ? 0 : sequenceId.hashCode());
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
	if (!super.equals(obj))
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	PersistentFieldMetadataImpl other = (PersistentFieldMetadataImpl) obj;
	if (indexed != other.indexed)
	    return false;
	if (linkMetadata == null) {
	    if (other.linkMetadata != null)
		return false;
	} else if (!linkMetadata.equals(other.linkMetadata))
	    return false;
	if (objectId != other.objectId)
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "PersistentFieldMetadataImpl [objectId=" + objectId + ", indexed=" + indexed + ", linkMetadata="
		+ linkMetadata + ", uniqueIndexId=" + uniqueIndexId + ", sequenceId=" + sequenceId + "]";
    }

    @Override
    public String getName() {
	return nestedMetadata.getName();
    }

    @Override
    public Class<? extends T> getImplementation() {
	return nestedMetadata.getImplementation();
    }

   


    @Override
    public Object createValueFromString(String data) throws Exception {
	return nestedMetadata.createValueFromString(data);
    }

    @Override
    public Metadata<T> getValueMetadata() {
	return nestedMetadata.getValueMetadata();
    }
  
}
