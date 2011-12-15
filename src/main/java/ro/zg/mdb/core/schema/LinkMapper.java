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

import ro.zg.mdb.core.annotations.Link;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.LinkModel;
import ro.zg.mdb.core.meta.data.ObjectDataModel;

public class LinkMapper extends ObjectDataModelAnnotationMapper<Link> {

    @Override
    public void map(ObjectDataModelAnnotationMapperContext<Link> amc) throws MdbException {
	Link al = amc.getAnnotation();
	LinkModel lm = new LinkModel(al.name(), al.first(), al.lazy(), al.allowedTypes(), al.key());
	FieldDataModel<?> fdm = amc.getFieldDataModel();
	fdm.setLinkModel(lm);

	boolean multivalued=fdm.getDataModel().isMultivalued();
	lm.setMultivalued(multivalued);
	if (lm.isFirst()) {
	    ObjectDataModel<?> odm = amc.getObjectDataModel();
	    if (multivalued) {
		Class<?> fieldType = fdm.getType();
		if (fieldType.equals(odm.getType())) {
		    odm.addReference(lm);
		}
		else {
		    amc.getSchema().getObjectDataModel(fieldType).addReference(lm);
		}
	    }
	    else {
		((ObjectDataModel<?>)fdm.getDataModel()).addReference(lm);
	    }
	}

    }

}
