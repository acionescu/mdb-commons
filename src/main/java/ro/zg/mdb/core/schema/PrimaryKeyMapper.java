package ro.zg.mdb.core.schema;

import ro.zg.mdb.core.annotations.PrimaryKey;
import ro.zg.mdb.core.meta.FieldDataModel;

public class PrimaryKeyMapper extends ObjectDataModelAnnotationMapper<PrimaryKey>{

    @Override
    public void map(ObjectDataModelAnnotationMapperContext<PrimaryKey> amc) {
	FieldDataModel fdm = amc.getFieldDataModel();
	amc.getObjectDataModel().addPkField(fdm);
	fdm.setPrimaryKey(true);
	fdm.setUniqueIndexId(amc.getObjectDataModel().getPrimaryKeyId());
    }

}
