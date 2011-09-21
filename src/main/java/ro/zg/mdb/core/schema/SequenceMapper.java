package ro.zg.mdb.core.schema;

import ro.zg.mdb.core.annotations.Sequenced;
import ro.zg.mdb.core.meta.FieldDataModel;
import ro.zg.mdb.core.meta.Schema;
import ro.zg.mdb.core.meta.Sequence;

public class SequenceMapper extends ObjectDataModelAnnotationMapper<Sequenced>{

    @Override
    public void map(ObjectDataModelAnnotationMapperContext<Sequenced> amc) {
	FieldDataModel fdm = amc.getFieldDataModel();
	Sequenced a = amc.getAnnotation();
//	Schema schema = amc.getSchema();
//	Sequence sequence = schema.getSequenceById(a.id());
//	if(sequence == null) {
//	    sequence = fromAnnotation(amc.getAnnotation());
//	    schema.addSequence(sequence);
//	}
	fdm.setSequenceId(a.id());
	
    }

    private Sequence fromAnnotation(Sequenced seq) {
	return new Sequence(seq.id(), seq.start(), seq.stop(), seq.step(), seq.recycle());
    }
}
