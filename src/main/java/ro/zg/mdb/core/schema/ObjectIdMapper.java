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
package ro.zg.mdb.core.schema;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.annotations.ObjectId;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.ObjectDataModel;
import ro.zg.util.data.GenericNameValue;

public class ObjectIdMapper extends ObjectDataModelAnnotationMapper<ObjectId>{

    @Override
    public void map(ObjectDataModelAnnotationMapperContext<ObjectId> amc) throws MdbException {
	ObjectDataModel<?> odm=amc.getObjectDataModel();
	
	if(odm.getObjectIdField() != null) {
	    throw new MdbException(MdbErrorType.MULTIPLE_OBEJCT_ID_FIELDS, new GenericNameValue("type",odm.getType()));
	}
	FieldDataModel<String> fdm = (FieldDataModel<String>)amc.getFieldDataModel();
	if(!fdm.getType().equals(String.class)) {
	    throw new MdbException(MdbErrorType.WRONG_FIELD_TYPE, new GenericNameValue("objectType",odm.getType()),new GenericNameValue("field",fdm.getName()),new GenericNameValue("expectedType","String"));
	}
	fdm.setObjectId(true);
	odm.setObjectIdField(fdm);
    }

}
