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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.core.annotations.Implementation;
import ro.zg.mdb.core.annotations.Indexed;
import ro.zg.mdb.core.annotations.Link;
import ro.zg.mdb.core.annotations.PrimaryKey;
import ro.zg.mdb.core.annotations.Required;
import ro.zg.mdb.core.annotations.Unique;

public class AnnotationMappersManager<A extends Annotation>{
    private Map<Class<?>,ObjectDataModelAnnotationMapper<? extends Annotation>> mappers;
    public AnnotationMappersManager() {
	mappers = new HashMap<Class<?>, ObjectDataModelAnnotationMapper<? extends Annotation>>();
	mappers.put(Required.class, new RequiredMapper());
	mappers.put(PrimaryKey.class, new PrimaryKeyMapper());
	mappers.put(Unique.class, new UniqueMapper());
	mappers.put(Indexed.class, new IndexMapper());
	mappers.put(Link.class, new LinkMapper());
	mappers.put(Implementation.class, new ImplementationMapper());
    }
    
    public void map(ObjectDataModelAnnotationMapperContext<A> amc) {
	ObjectDataModelAnnotationMapper mapper = mappers.get(amc.getAnnotation().annotationType());
	if(mapper!=null) {
	    mapper.map(amc);
	}
	else {
	    System.out.println("No mapper for "+amc);
	}
    }
}
