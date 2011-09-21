package ro.zg.mdb.core.schema;

import ro.zg.mdb.core.annotations.Indexed;
import ro.zg.mdb.core.meta.FieldDataModel;

public class IndexMapper extends ObjectDataModelAnnotationMapper<Indexed>{

    @Override
    public void map(ObjectDataModelAnnotationMapperContext<Indexed> amc) {
	FieldDataModel fdm = amc.getFieldDataModel();
	amc.getObjectDataModel().addIndexedField(fdm);
	fdm.setIndexed(true);
    }

}
