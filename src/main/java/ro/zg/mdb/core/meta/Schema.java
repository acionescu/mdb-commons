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
package ro.zg.mdb.core.meta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.annotations.ForeignKey;
import ro.zg.mdb.core.annotations.Indexed;
import ro.zg.mdb.core.annotations.PrimaryKey;
import ro.zg.mdb.core.annotations.Required;
import ro.zg.mdb.core.annotations.Unique;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.schema.AnnotationMappersManager;
import ro.zg.mdb.core.schema.ObjectDataModelAnnotationMapperContext;
import ro.zg.util.data.GenericNameValue;

public class Schema {
    private String name;
    private SchemaConfig config;
    private Map<Class<?>, ObjectDataModel> objectsModels = new HashMap<Class<?>, ObjectDataModel>();
    private AnnotationMappersManager<Annotation> annotationMappersManager = new AnnotationMappersManager<Annotation>();

    public Schema(String name, SchemaConfig config) {
	this.name = name;
	this.config = config;
    }

    public void addObjectDataModel(ObjectDataModel odm) {
	objectsModels.put(odm.getType(), odm);
    }
    
   
    
    public <T> T getObjectFromString(String data, Class<T> type, Filter filter) throws MdbException {
	ObjectDataModel odm = getObjectDataModel(type);
	return (T)odm.getObjectFromString(data,filter);
    }
    

    public <T> ObjectDataModel<T> getObjectDataModel(Class<T> type) {
	ObjectDataModel<T> odm = objectsModels.get(type);
	if (odm == null) {
	    if (config.isAutomaticObjectModelCreationOn()) {
		odm = createObjectDataModel(type);
		addObjectDataModel(odm);
	    } else {
		throw new RuntimeException("Object data model does not exist for type " + type.getName());
	    }
	}
	return odm;
    }

    public ObjectDataModel createObjectDataModel(Class<?> type) {
	return createObjectDataModel(type, PrimaryKey.class, Unique.class, ForeignKey.class, Required.class,
		Indexed.class);
    }

    public ObjectDataModel createObjectDataModel(Class<?> type, Class<? extends Annotation>... annotationTypes) {
	ObjectDataModel odm = new ObjectDataModel(type);
	do {
	    for (Field field : type.getDeclaredFields()) {
		FieldDataModel fdm = new FieldDataModel(field.getName(), field.getType());
		for (Class<? extends Annotation> annotationType : annotationTypes) {
		    Annotation currentAnnotation = field.getAnnotation(annotationType);
		    if (currentAnnotation != null) {
			ObjectDataModelAnnotationMapperContext mc = new ObjectDataModelAnnotationMapperContext(currentAnnotation, odm, fdm, this);
			annotationMappersManager.map(mc);
		    }
		}
		odm.addFieldDataModel(fdm);
	    }
	    type = type.getSuperclass();
	} while (type != null);
	return odm;
    }

    /**
     * @return the name
     */
    public String getName() {
	return name;
    }

    /**
     * @return the config
     */
    public SchemaConfig getConfig() {
	return config;
    }

}
