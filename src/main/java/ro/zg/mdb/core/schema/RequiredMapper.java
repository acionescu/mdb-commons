package ro.zg.mdb.core.schema;

import ro.zg.mdb.core.annotations.Required;
import ro.zg.mdb.core.meta.FieldDataModel;

public class RequiredMapper extends ObjectDataModelAnnotationMapper<Required>{

    @Override
    public void map(ObjectDataModelAnnotationMapperContext<Required> amc) {
	FieldDataModel fdm = amc.getFieldDataModel();
	amc.getObjectDataModel().addRequiredField(fdm);
	fdm.setRequired(true);
    }

}
