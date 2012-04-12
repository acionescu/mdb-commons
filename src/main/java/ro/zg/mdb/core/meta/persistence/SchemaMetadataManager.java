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
package ro.zg.mdb.core.meta.persistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.constants.SpecialPaths;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.persistence.data.LinkMetadata;
import ro.zg.mdb.core.meta.persistence.data.PersistentFieldMetadataImpl;
import ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadata;
import ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadataImpl;
import ro.zg.mdb.core.meta.persistence.data.Schema;
import ro.zg.mdb.core.meta.persistence.data.SchemaConfig;
import ro.zg.mdb.core.schema.AnnotationMappersManager;
import ro.zg.mdb.core.schema.ObjectDataModelAnnotationMapperContext;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;
import ro.zg.metadata.commons.CollectionDataModel;
import ro.zg.metadata.commons.MapDataModel;
import ro.zg.metadata.commons.Metadata;
import ro.zg.metadata.commons.MetadataImpl;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.reflection.ReflectionUtility;

public class SchemaMetadataManager extends PersistentDataManager {
    private Schema schema;
    private SchemaManager metadataSchema;
    private SchemaConfig config;
    private AnnotationMappersManager<Annotation> annotationMappersManager = new AnnotationMappersManager<Annotation>();

    public SchemaMetadataManager(PersistenceManager persistenceManager, SchemaConfig config) throws MdbException {
	super(persistenceManager);
	this.config = config;
	schema = new Schema();
	if (config.isMetadataPersistanceAllowed()) {
	    SchemaConfig metadataConfig = new SchemaConfig(SpecialPaths.META);
	    metadataConfig.setSequenceUsageAllowed(true);
	    metadataConfig.setObjectReferenceAllowed(true);
	    try {
		metadataSchema = new SchemaManager(persistenceManager.getPersistenceManager(SpecialPaths.META),
			metadataConfig);
		loadSchema();
	    } catch (PersistenceException e) {
		throw new MdbException(e, MdbErrorType.PERSISTENCE_ERROR);
	    }
	}

    }

    private void loadSchema() throws MdbException {
	Collection results = metadataSchema.createCommand(Metadata.class).get().execute().getValues();
	schema.addDataModels(results);

	results = metadataSchema.createCommand(PersistentObjectMetadata.class).get().execute().getValues();
	schema.addDataModels(results);

	results = metadataSchema.createCommand(MapDataModel.class).get().execute().getValues();
	schema.addDataModels(results);

	results = metadataSchema.createCommand(CollectionDataModel.class).get().execute().getValues();
	schema.addDataModels(results);

    }

    public <T> PersistentObjectMetadata<T> getObjectDataModel(Class<T> type) throws MdbException {
	try {
	    return (PersistentObjectMetadata<T>) getDataModel(type, true);
	} catch (Exception e) {
	    throw new RuntimeException("Failed to get object data model for " + type, e);
	}
    }

    private <T> PersistentObjectMetadataImpl<T> getObjectDataModel(Class<T> type, boolean saveIfMissing) throws MdbException {
	try {
	    return (PersistentObjectMetadataImpl<T>) getDataModel(type, saveIfMissing);
	} catch (Exception e) {
	    throw new RuntimeException("Failed to get object data model for " + type, e);
	}
    }

    private <T> Metadata<T> getDataModel(Type type) throws MdbException {
	return getDataModel(type, true);
    }

    private <T> Metadata<T> getDataModel(Type type, boolean saveIfMissing) throws MdbException {
	Metadata<T> odm = null;
	synchronized (type) {
	    if (type instanceof Class) {
		Class<T> clazz = (Class<T>) type;

		odm = schema.getDataModelByClass(clazz);
		if (odm == null) {
		    if (config.isAutomaticObjectModelCreationOn()) {
			if (ReflectionUtility.checkSimpleFieldType(clazz) || clazz.isAssignableFrom(Class.class)) {
			    odm = new MetadataImpl<T>(clazz);
			} else {
			    odm = createObjectDataModel(clazz);
			}

		    } else {
			throw new RuntimeException("Object data model does not exist for type " + clazz.getName());
		    }
		} else {
		    return odm;
		}

	    } else if (type instanceof ParameterizedType) {
		ParameterizedType pt = (ParameterizedType) type;
		odm = schema.getMultivaluedDataModel(pt);
		if (odm != null) {
		    return odm;
		}

		odm = createDataModelForParameterizedType((ParameterizedType) type);
	    } else if (type instanceof GenericArrayType) {
		GenericArrayType gat = (GenericArrayType) type;
		odm = schema.getMultivaluedDataModel(gat);
		if (odm != null) {
		    return odm;
		}

		odm = createDataModelForGenericArrayType(gat);
	    } else {
		throw new IllegalArgumentException("Unsupported type " + type);
	    }
	    if (saveIfMissing) {
		saveDataModel(odm);
	    }
	}
	return odm;
    }

    private <T> Metadata<T> createDataModelForParameterizedType(ParameterizedType pt) throws MdbException {
	Class<T> clazz = (Class<T>) pt.getRawType();
	if (ReflectionUtility.checkInstanceOf(clazz, Collection.class)) {
	    return createCollectionDataModel(pt);
	} else if (ReflectionUtility.checkInstanceOf(clazz, Map.class)) {
	    return createMapDataModel(pt);
	}
	return getDataModel(clazz);
	// throw new IllegalArgumentException("Unsupported parameterized type " + pt);
    }

    private Metadata createDataModelForGenericArrayType(GenericArrayType gat) {
	Type arrayType = gat.getGenericComponentType();
	return new CollectionDataModel(getRawType(arrayType), Array.class);

    }

    private Class<?> getRawType(Type type) {
	if (type instanceof Class) {
	    return (Class<?>) type;
	} else if (type instanceof ParameterizedType) {
	    return (Class<?>) ((ParameterizedType) type).getRawType();
	} 
	throw new IllegalArgumentException("Unknown type " + type);
    }

    private <T> Metadata<T> createCollectionDataModel(ParameterizedType pt) {

	Type[] typeArguments = pt.getActualTypeArguments();
	if (typeArguments.length == 0) {
	    throw new IllegalArgumentException("Expecting one generic type argument but there was none");
	}

	Class<T> nestedType = (Class<T>) getRawType(typeArguments[0]);

	return new CollectionDataModel<T>(nestedType, (Class<? extends Collection<T>>) pt.getRawType());

    }

    private <K, T> Metadata<T> createMapDataModel(ParameterizedType pt) {
	Type[] typeArguments = pt.getActualTypeArguments();
	if (typeArguments.length < 2) {
	    throw new IllegalArgumentException("Expecting two generic type arguments but there were less");
	}
	Class<K> keyType = (Class<K>) getRawType(typeArguments[0]);
	Class<T> valueType = (Class<T>) getRawType(typeArguments[1]);

	return new MapDataModel<K, T>(keyType, valueType, (Class<? extends Map<K, T>>) pt.getRawType());

    }

    private <T> PersistentObjectMetadata<T> createObjectDataModel(Class<T> type) throws MdbException {
	Set<Class<? extends Annotation>> annotationTypes = annotationMappersManager.getKnownAnnotations();
	PersistentObjectMetadataImpl<T> odm = new PersistentObjectMetadataImpl<T>(type);
	Class<?> currentType = type;
	do {
	    for (Field field : currentType.getDeclaredFields()) {
		int modifiers = field.getModifiers();
		if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers)) {
		    continue;
		}
		PersistentFieldMetadataImpl<?> fdm = null;
		/* in case a class has a reference to itself */
		if (field.getType().equals(currentType)) {
		    fdm = new PersistentFieldMetadataImpl(field.getName(), odm);
		} else {
		    fdm = new PersistentFieldMetadataImpl(field.getName(), getDataModel(field.getGenericType()));
		}
		for (Class<? extends Annotation> annotationType : annotationTypes) {
		    Annotation currentAnnotation = field.getAnnotation(annotationType);
		    if (currentAnnotation != null) {
			ObjectDataModelAnnotationMapperContext mc = new ObjectDataModelAnnotationMapperContext(
				currentAnnotation, odm, fdm, this);
			annotationMappersManager.map(mc);
		    }
		}
		odm.addFieldMetadata(fdm);
	    }
	    currentType = currentType.getSuperclass();
	} while (currentType != null);
	return odm;
    }

    private synchronized void saveDataModel(Metadata<?> dm) throws MdbException {
	schema.addDataModel(dm);
	if (config.isMetadataPersistanceAllowed()) {
	    metadataSchema.createCommand((Class<Metadata>) dm.getClass()).insert(dm).execute();
	}
    }

    public void updateReference(Class<?> type, LinkMetadata lm) throws MdbException {
	PersistentObjectMetadataImpl<?> odm = (PersistentObjectMetadataImpl<?>) schema.getDataModelByClass(type);
	if (odm != null) {
	    updateReference(odm, lm);
	} else {
	    odm = getObjectDataModel(type, false);
	    odm.addReference(lm);
	    saveDataModel(odm);
	}
    }

    public synchronized void updateReference(PersistentObjectMetadataImpl<?> odm, LinkMetadata lm) throws MdbException {
	if (odm.getReferences().contains(lm)) {
	    return;
	}
	odm.addReference(lm);
	if (config.isMetadataPersistanceAllowed()) {
	    long updated = metadataSchema.createCommand(((Class<PersistentObjectMetadata>) odm.getClass()))
		    .update(odm, "references").where().field("id").eq(odm.getId()).execute();
	    if (updated != 1) {
		throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, new GenericNameValue("object", odm));
	    }
	}
    }
}
