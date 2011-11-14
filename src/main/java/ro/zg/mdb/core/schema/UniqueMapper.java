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

import ro.zg.mdb.core.annotations.Unique;
import ro.zg.mdb.core.meta.data.FieldDataModel;

public class UniqueMapper extends ObjectDataModelAnnotationMapper<Unique>{

    @Override
    public void map(ObjectDataModelAnnotationMapperContext<Unique> amc) {
	FieldDataModel fdm = amc.getFieldDataModel();
	String indexId=""+amc.getAnnotation().id();
	amc.getObjectDataModel().addUniqueField(indexId, fdm);
	fdm.setUniqueIndexId(indexId);
    }

}
