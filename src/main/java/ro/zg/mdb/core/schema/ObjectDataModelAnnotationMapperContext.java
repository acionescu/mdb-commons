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

import ro.zg.mdb.core.meta.persistence.SchemaMetadataManager;
import ro.zg.mdb.core.meta.persistence.data.PersistentFieldMetadata;
import ro.zg.mdb.core.meta.persistence.data.PersistentFieldMetadataImpl;
import ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadata;
import ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadataImpl;
import ro.zg.metadata.commons.AnnotationProcessorContext;

public class ObjectDataModelAnnotationMapperContext<A extends Annotation> extends AnnotationProcessorContext<A> {
    private PersistentObjectMetadataImpl<?> objectDataModel;
    private PersistentFieldMetadataImpl<?> fieldDataModel;
    private SchemaMetadataManager schema;
    
    public ObjectDataModelAnnotationMapperContext(A annotation, PersistentObjectMetadataImpl<?> objectDataModel,
	    PersistentFieldMetadataImpl<?> fieldDataModel, SchemaMetadataManager schema) {
	super(annotation);
	this.objectDataModel = objectDataModel;
	this.fieldDataModel = fieldDataModel;
	this.schema = schema;
    }
    /**
     * @return the objectDataModel
     */
    public PersistentObjectMetadataImpl<?> getObjectDataModel() {
        return objectDataModel;
    }
    /**
     * @return the fieldDataModel
     */
    public PersistentFieldMetadataImpl<?> getFieldDataModel() {
        return fieldDataModel;
    }
    /**
     * @return the schema
     */
    public SchemaMetadataManager getSchema() {
        return schema;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "ObjectDataModelAnnotationMapperContext [fieldDataModel=" + fieldDataModel + ", objectDataModel="
		+ objectDataModel + ", schema=" + schema + "]";
    }
    
}
