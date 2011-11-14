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
package ro.zg.mdb.commands.builders;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.ObjectDataModel;
import ro.zg.mdb.core.meta.data.UniqueIndex;
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
