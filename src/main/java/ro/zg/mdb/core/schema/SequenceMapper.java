/*******************************************************************************
 * Copyright (c) 2011 Adrian Cristian Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Adrian Cristian Ionescu - initial API and implementation
 ******************************************************************************/
package ro.zg.mdb.core.schema;

import ro.zg.mdb.core.annotations.Sequenced;
import ro.zg.mdb.core.meta.data.FieldDataModel;
import ro.zg.mdb.core.meta.data.Sequence;

public class SequenceMapper extends ObjectDataModelAnnotationMapper<Sequenced>{

    @Override
    public void map(ObjectDataModelAnnotationMapperContext<Sequenced> amc) {
	FieldDataModel<?> fdm = amc.getFieldDataModel();
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
