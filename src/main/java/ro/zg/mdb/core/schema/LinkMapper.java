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

import ro.zg.mdb.core.annotations.Link;
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.LinkModel;

public class LinkMapper extends ObjectDataModelAnnotationMapper<Link>{

    @Override
    public void map(ObjectDataModelAnnotationMapperContext<Link> amc) {
	Link al = amc.getAnnotation();
	LinkModel lm = new LinkModel(al.name(), al.first(), al.lazy(), al.key());
	FieldDataModel<?> fdm=amc.getFieldDataModel();
	fdm.setLinkModel(lm);
	
    }

}
