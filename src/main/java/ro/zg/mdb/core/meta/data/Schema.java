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
package ro.zg.mdb.core.meta.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.core.annotations.Link;
import ro.zg.mdb.core.annotations.Indexed;
import ro.zg.mdb.core.annotations.PrimaryKey;
import ro.zg.mdb.core.annotations.Required;
import ro.zg.mdb.core.annotations.Unique;
import ro.zg.mdb.core.schema.AnnotationMappersManager;
import ro.zg.mdb.core.schema.ObjectDataModelAnnotationMapperContext;
import ro.zg.util.data.reflection.ReflectionUtility;

public class Schema {
    private SchemaConfig config;
    private Map<Class<?>, DataModel<?>> objectsModels = new HashMap<Class<?>, DataModel<?>>();
    private AnnotationMappersManager<Annotation> annotationMappersManager = new AnnotationMappersManager<Annotation>();

    public Schema(SchemaConfig config) {
	this.config = config;
    }

    private void addDataModel(DataModel<?> odm) {
	objectsModels.put(odm.getType(), odm);
    }

    //
    //
    // public <T> T getObjectFromString(String data, Class<T> type, Filter filter) throws MdbException {
    // ObjectDataModel<T> odm = getObjectDataModel(type);
    // return (T)odm.getObjectFromString(data,filter);
    // }
    
    public <T> ObjectDataModel<T> getObjectDataModel(Class<T> type){
	return (ObjectDataModel<T>)getDataModel(type);
    }

    private <T> DataModel<T> getDataModel(Class<T> type) {
	DataModel<T> odm = (DataModel<T>) objectsModels.get(type);
	if (odm == null) {
	    if (config.isAutomaticObjectModelCreationOn()) {
		if (ReflectionUtility.checkSimpleFieldType(type)) {
		    odm = new DataModel<T>(type);
		} else {
		    odm = createObjectDataModel(type);
		}
		addDataModel(odm);
	    } else {
		throw new RuntimeException("Object data model does not exist for type " + type.getName());
	    }
	}
	return odm;
    }

    public <T> ObjectDataModel<T> createObjectDataModel(Class<T> type) {
	return createObjectDataModel(type, PrimaryKey.class, Unique.class, Link.class, Required.class,
		Indexed.class);
    }

    public <T> ObjectDataModel<T> createObjectDataModel(Class<T> type, Class<? extends Annotation>... annotationTypes) {
	ObjectDataModel<T> odm = new ObjectDataModel<T>(type);
	Class<?> currentType=type;
	do {
	    for (Field field : currentType.getDeclaredFields()) {
		FieldDataModel<?> fdm = new FieldDataModel(field.getName(), getDataModel(field.getType()));
		for (Class<? extends Annotation> annotationType : annotationTypes) {
		    Annotation currentAnnotation = field.getAnnotation(annotationType);
		    if (currentAnnotation != null) {
			ObjectDataModelAnnotationMapperContext mc = new ObjectDataModelAnnotationMapperContext(
				currentAnnotation, odm, fdm, this);
			annotationMappersManager.map(mc);
		    }
		}
		odm.addFieldDataModel(fdm);
	    }
	    currentType = currentType.getSuperclass();
	} while (currentType != null);
	return odm;
    }


    /**
     * @return the config
     */
    public SchemaConfig getConfig() {
	return config;
    }

}
