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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.core.exceptions.MdbException;
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

    public <T> ObjectDataModel<T> getObjectDataModel(Class<T> type) throws MdbException {
	return (ObjectDataModel<T>) getDataModel(type);
    }

    private <T> DataModel<T> getDataModel(Type type) throws MdbException {

	if (type instanceof Class) {
	    Class<T> clazz = (Class<T>) type;
	    DataModel<T> odm = (DataModel<T>) objectsModels.get(clazz);
	    if (odm == null) {
		if (config.isAutomaticObjectModelCreationOn()) {
		    if (ReflectionUtility.checkSimpleFieldType(clazz)) {
			odm = new DataModel<T>(clazz);
		    } else {
			odm = createObjectDataModel(clazz);
		    }
		    addDataModel(odm);
		} else {
		    throw new RuntimeException("Object data model does not exist for type " + clazz.getName());
		}
	    }
	    return odm;
	} else if (type instanceof ParameterizedType) {
	    return getDataModelForParameterizedType((ParameterizedType)type);
	}
	
	throw new IllegalArgumentException("Unsupported type "+type);
    }

    public <T> DataModel<T> getDataModelForParameterizedType(ParameterizedType pt) {
	Class<T> clazz = (Class<T>) pt.getRawType();
	if (ReflectionUtility.checkInstanceOf(clazz, Collection.class)) {
	    return createCollectionDataModel(pt);
	} else if (ReflectionUtility.checkInstanceOf(clazz, Map.class)) {
	    return createMapDataModel(pt);
	}
	
	throw new IllegalArgumentException("Unsupported parameterized type "+pt);
    }

    public <T> DataModel<T> createCollectionDataModel(ParameterizedType pt) {

	Type[] typeArguments = pt.getActualTypeArguments();
	if (typeArguments.length == 0) {
	    throw new IllegalArgumentException("Expecting one generic type argument but there was none");
	}

	Class<T> nestedType = (Class<T>) typeArguments[0];

	return new CollectionDataModel<T>(nestedType, (Class<? extends Collection<T>>) pt.getRawType());

    }

    public <K, T> DataModel<T> createMapDataModel(ParameterizedType pt) {
	Type[] typeArguments = pt.getActualTypeArguments();
	if (typeArguments.length < 2) {
	    throw new IllegalArgumentException("Expecting two generic type arguments but there were less");
	}
	Class<K> keyType = (Class<K>) typeArguments[0];
	Class<T> valueType = (Class<T>) typeArguments[1];

	return new MapDataModel<K, T>(keyType, valueType, (Class<? extends Map<K, T>>) pt.getRawType());

    }

    public <T> ObjectDataModel<T> createObjectDataModel(Class<T> type) throws MdbException {
	Set<Class<? extends Annotation>> annotationTypes = annotationMappersManager.getKnownAnnotations();
	ObjectDataModel<T> odm = new ObjectDataModel<T>(type);
	Class<?> currentType = type;
	do {
	    for (Field field : currentType.getDeclaredFields()) {
		FieldDataModel<?> fdm = null;
		/* in case a class has a reference to itself */
		if (field.getType().equals(currentType)) {
		    fdm = new FieldDataModel(field.getName(), odm);
		} else {
		    fdm = new FieldDataModel(field.getName(), getDataModel(field.getGenericType()));
		}
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
