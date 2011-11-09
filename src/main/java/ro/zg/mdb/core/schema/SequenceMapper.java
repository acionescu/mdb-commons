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

import ro.zg.mdb.core.annotations.Sequenced;
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.Schema;
import ro.zg.mdb.core.meta.data.Sequence;

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
