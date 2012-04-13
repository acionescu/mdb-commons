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
package ro.zg.mdb.core.meta.persistence.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.schema.AnnotationMappersManager;
import ro.zg.metadata.commons.Metadata;
import ro.zg.metadata.commons.MultivaluedTypeMetadata;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.hash.HashUtil;

public class Schema {
    private SchemaConfig config;
    private Map<Class<?>, Metadata<?>> objectsModels = new HashMap<Class<?>, Metadata<?>>();
    private Map<String, MultivaluedTypeMetadata<?, ?>> multivaluedDataModels = new HashMap<String, MultivaluedTypeMetadata<?, ?>>();
    private AnnotationMappersManager<Annotation> annotationMappersManager = new AnnotationMappersManager<Annotation>();

    public Schema(SchemaConfig config) {
	this.config = config;
    }

    public Schema() {
	super();
    }

    public void addDataModels(Collection<Metadata<?>> dmc) {
	for (Metadata<?> dm : dmc) {
	    addDataModel(dm);
	}
    }

    public void addDataModel(Metadata<?> odm) {
	if (odm.isMultivalued()) {
	    MultivaluedTypeMetadata<?, ?> mvdm = (MultivaluedTypeMetadata<?, ?>) odm;
	    multivaluedDataModels.put(mvdm.getParameteriezedTypeHash(), mvdm);
	} else {
	    objectsModels.put(odm.getType(), odm);
	}
    }

    public <T> Metadata<T> getDataModel(Type type) throws MdbException {
	if(type instanceof Class) {
	    return (Metadata<T>)objectsModels.get((Class<T>)type);
	}
	else if(type instanceof ParameterizedType || type instanceof GenericArrayType) {
	    return (Metadata<T>)multivaluedDataModels.get(getHashForType(type));
	}
	throw new MdbException(MdbErrorType.UNKNOWN_OBJECT_TYPE, new GenericNameValue("type",type));
    }
    
    public <T> Metadata<T> getDataModelByClass(Class<T> clazz){
	return (Metadata<T>)objectsModels.get(clazz);
    }
    
    public Metadata getMultivaluedDataModel(Type pt) {
	return multivaluedDataModels.get(getHashForType(pt));
    }
    
    private String getHashForType(Type type) {
	return HashUtil.digestSHA1(type.toString());
    }

    // private <T> Metadata<T> getDataModel(Type type) throws MdbException {
    //
    // if (type instanceof Class) {
    // Class<T> clazz = (Class<T>) type;
    // Metadata<T> odm = (Metadata<T>) objectsModels.get(clazz);
    // if (odm == null) {
    // if (config.isAutomaticObjectModelCreationOn()) {
    // if (ReflectionUtility.checkSimpleFieldType(clazz)) {
    // odm = new Metadata<T>(clazz);
    // } else {
    // odm = createObjectDataModel(clazz);
    // }
    // addDataModel(odm);
    // } else {
    // throw new RuntimeException("Object data model does not exist for type " + clazz.getName());
    // }
    // }
    // return odm;
    // } else if (type instanceof ParameterizedType) {
    // return getDataModelForParameterizedType((ParameterizedType)type);
    // }
    //
    // throw new IllegalArgumentException("Unsupported type "+type);
    // }
    //
    // public <T> Metadata<T> getDataModelForParameterizedType(ParameterizedType pt) {
    // Class<T> clazz = (Class<T>) pt.getRawType();
    // if (ReflectionUtility.checkInstanceOf(clazz, Collection.class)) {
    // return createCollectionDataModel(pt);
    // } else if (ReflectionUtility.checkInstanceOf(clazz, Map.class)) {
    // return createMapDataModel(pt);
    // }
    //
    // throw new IllegalArgumentException("Unsupported parameterized type "+pt);
    // }
    //
    // public <T> Metadata<T> createCollectionDataModel(ParameterizedType pt) {
    //
    // Type[] typeArguments = pt.getActualTypeArguments();
    // if (typeArguments.length == 0) {
    // throw new IllegalArgumentException("Expecting one generic type argument but there was none");
    // }
    //
    // Class<T> nestedType = (Class<T>) typeArguments[0];
    //
    // return new CollectionDataModel<T>(nestedType, (Class<? extends Collection<T>>) pt.getRawType());
    //
    // }
    //
    // public <K, T> Metadata<T> createMapDataModel(ParameterizedType pt) {
    // Type[] typeArguments = pt.getActualTypeArguments();
    // if (typeArguments.length < 2) {
    // throw new IllegalArgumentException("Expecting two generic type arguments but there were less");
    // }
    // Class<K> keyType = (Class<K>) typeArguments[0];
    // Class<T> valueType = (Class<T>) typeArguments[1];
    //
    // return new MapDataModel<K, T>(keyType, valueType, (Class<? extends Map<K, T>>) pt.getRawType());
    //
    // }
    //
    // public <T> PersistentObjectMetadata<T> createObjectDataModel(Class<T> type) throws MdbException {
    // Set<Class<? extends Annotation>> annotationTypes = annotationMappersManager.getKnownAnnotations();
    // PersistentObjectMetadata<T> odm = new PersistentObjectMetadata<T>(type);
    // Class<?> currentType = type;
    // do {
    // for (Field field : currentType.getDeclaredFields()) {
    // if(Modifier.isTransient(field.getModifiers())) {
    // continue;
    // }
    // PersistentFieldMetadata<?> fdm = null;
    // /* in case a class has a reference to itself */
    // if (field.getType().equals(currentType)) {
    // fdm = new PersistentFieldMetadata(field.getName(), odm);
    // } else {
    // fdm = new PersistentFieldMetadata(field.getName(), getDataModel(field.getGenericType()));
    // }
    // for (Class<? extends Annotation> annotationType : annotationTypes) {
    // Annotation currentAnnotation = field.getAnnotation(annotationType);
    // if (currentAnnotation != null) {
    // ObjectDataModelAnnotationMapperContext mc = new ObjectDataModelAnnotationMapperContext(
    // currentAnnotation, odm, fdm, this);
    // annotationMappersManager.map(mc);
    // }
    // }
    // odm.addFieldDataModel(fdm);
    // }
    // currentType = currentType.getSuperclass();
    // } while (currentType != null);
    // return odm;
    // }

    /**
     * @return the config
     */
    public SchemaConfig getConfig() {
	return config;
    }

}
