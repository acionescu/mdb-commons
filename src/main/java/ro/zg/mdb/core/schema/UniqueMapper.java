package ro.zg.mdb.core.schema;

import ro.zg.mdb.core.annotations.Unique;
import ro.zg.mdb.core.meta.FieldDataModel;

public class UniqueMapper extends ObjectDataModelAnnotationMapper<Unique>{

    @Override
    public void map(ObjectDataModelAnnotationMapperContext<Unique> amc) {
	FieldDataModel fdm = amc.getFieldDataModel();
	String indexId=""+amc.getAnnotation().id();
	amc.getObjectDataModel().addUniqueField(indexId, fdm);
	fdm.setUniqueIndexId(indexId);
    }

}
