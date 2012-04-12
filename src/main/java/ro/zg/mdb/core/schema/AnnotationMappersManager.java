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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.core.annotations.Implementation;
import ro.zg.mdb.core.annotations.Indexed;
import ro.zg.mdb.core.annotations.Link;
import ro.zg.mdb.core.annotations.ObjectId;
import ro.zg.mdb.core.annotations.Sequenced;
import ro.zg.mdb.core.annotations.Unique;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.metadata.annotations.validation.Required;

public class AnnotationMappersManager<A extends Annotation>{
    private Map<Class<? extends Annotation>,ObjectDataModelAnnotationMapper<? extends Annotation>> mappers;
    public AnnotationMappersManager() {
	mappers = new HashMap<Class<? extends Annotation>, ObjectDataModelAnnotationMapper<? extends Annotation>>();
	mappers.put(Required.class, new RequiredMapper());
	mappers.put(ObjectId.class, new ObjectIdMapper());
	mappers.put(Unique.class, new UniqueMapper());
	mappers.put(Indexed.class, new IndexMapper());
	mappers.put(Link.class, new LinkMapper());
	mappers.put(Implementation.class, new ImplementationMapper());
	mappers.put(Sequenced.class, new SequenceMapper());
    }
    
    public void map(ObjectDataModelAnnotationMapperContext<A> amc) throws MdbException {
	ObjectDataModelAnnotationMapper mapper = mappers.get(amc.getAnnotation().annotationType());
	if(mapper!=null) {
	    mapper.map(amc);
	}
	else {
	    System.out.println("No mapper for "+amc);
	}
    }
    
    public Set<Class<? extends Annotation>> getKnownAnnotations(){
	return mappers.keySet();
    }
}
