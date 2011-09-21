package ro.zg.mdb.core.schema;

import java.lang.annotation.Annotation;

import ro.zg.mdb.core.meta.FieldDataModel;
import ro.zg.mdb.core.meta.ObjectDataModel;
import ro.zg.mdb.core.meta.Schema;

public class ObjectDataModelAnnotationMapperContext<A extends Annotation> extends AnnotationMapperContext<A> {
    private ObjectDataModel objectDataModel;
    private FieldDataModel fieldDataModel;
    private Schema schema;
    
    public ObjectDataModelAnnotationMapperContext(A annotation, ObjectDataModel objectDataModel,
	    FieldDataModel fieldDataModel, Schema schema) {
	super(annotation);
	this.objectDataModel = objectDataModel;
	this.fieldDataModel = fieldDataModel;
	this.schema = schema;
    }
    /**
     * @return the objectDataModel
     */
    public ObjectDataModel getObjectDataModel() {
        return objectDataModel;
    }
    /**
     * @return the fieldDataModel
     */
    public FieldDataModel getFieldDataModel() {
        return fieldDataModel;
    }
    /**
     * @return the schema
     */
    public Schema getSchema() {
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
