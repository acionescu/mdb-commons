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
package ro.zg.mdb.commands.builders;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.meta.FieldDataModel;
import ro.zg.mdb.core.meta.ObjectDataModel;
import ro.zg.mdb.core.meta.UniqueIndex;
import ro.zg.util.data.GenericNameValue;

public class FilterBuilderUtil {

    public static <T> Filter getPkFilter(T target, ObjectDataModel<T> odm) throws MdbException {
	FilterBuilderContext<T, T> fbc=new FilterBuilderContext<T, T>(null);
	FilterBuilder<T, T> fb=new FilterBuilder<T, T>(fbc);
	
	UniqueIndex pk = odm.getPrimaryKey();
	if(pk == null) {
	    throw new MdbException(MdbErrorType.NO_PK_DEFINED, new GenericNameValue("type",odm.getTypeName()));
	}
	for(FieldDataModel<?> fdm : pk.getFields()) {
	    Object fieldValue=odm.getValueForField(target, fdm.getName());
	    if(fieldValue==null) {
		return null;
	    }
	    fb.field(fdm.getName()).eq(fieldValue);
	}
	return fbc.getFilter();
    }
    
}
