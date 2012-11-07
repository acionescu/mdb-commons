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
package ro.zg.mdb.core.schema;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.annotations.ObjectId;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.persistence.data.PersistentFieldMetadataImpl;
import ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadataImpl;
import ro.zg.metadata.annotations.validation.Required;
import ro.zg.metadata.commons.AnnotationMapper;
import ro.zg.metadata.commons.AnnotationMapperContext;
import ro.zg.metadata.commons.FieldMetadataImpl;
import ro.zg.metadata.exceptions.MetadataException;
import ro.zg.util.data.GenericNameValue;

public class ObjectIdMapper implements AnnotationMapper<AnnotationMapperContext<Required, PersistentObjectMetadataImpl<?>>>{

    @Override
    public void map(
	    AnnotationMapperContext<Required, PersistentObjectMetadataImpl<?>> amc)
	    throws MetadataException {
	PersistentObjectMetadataImpl<?> odm=amc.getMetadata();
	
	/* here should only mark the field as object id, and handle the logic of the object model somewhere else */
	if(odm.getObjectIdField() != null) {
	    throw new MdbException(MdbErrorType.MULTIPLE_OBEJCT_ID_FIELDS, new GenericNameValue("type",odm.getType()));
	}
	PersistentFieldMetadataImpl<String> fdm = (PersistentFieldMetadataImpl<String>)amc.getFieldDataModel();
	if(!fdm.getType().equals(String.class)) {
	    throw new MdbException(MdbErrorType.WRONG_FIELD_TYPE, new GenericNameValue("objectType",odm.getType()),new GenericNameValue("field",fdm.getName()),new GenericNameValue("expectedType","String"));
	}
	fdm.setObjectId(true);
	odm.setObjectIdField(fdm);
	
    }

//    @Override
//    public void map(ObjectDataModelAnnotationMapperContext<ObjectId> amc) throws MdbException {
//	PersistentObjectMetadataImpl<?> odm=amc.getObjectDataModel();
//	
//	if(odm.getObjectIdField() != null) {
//	    throw new MdbException(MdbErrorType.MULTIPLE_OBEJCT_ID_FIELDS, new GenericNameValue("type",odm.getType()));
//	}
//	PersistentFieldMetadataImpl<String> fdm = (PersistentFieldMetadataImpl<String>)amc.getFieldDataModel();
//	if(!fdm.getType().equals(String.class)) {
//	    throw new MdbException(MdbErrorType.WRONG_FIELD_TYPE, new GenericNameValue("objectType",odm.getType()),new GenericNameValue("field",fdm.getName()),new GenericNameValue("expectedType","String"));
//	}
//	fdm.setObjectId(true);
//	odm.setObjectIdField(fdm);
//    }

}
