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
package ro.zg.mdb.core.meta;

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.constants.SpecialPaths;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.SchemaConfig;
import ro.zg.mdb.core.meta.data.Sequence;
import ro.zg.mdb.persistence.PersistenceManager;

public class SequencesManager extends PersistentDataManager{
    private SchemaManager sequencesSchema;
    
    private Map<String, Sequence> sequences = new HashMap<String, Sequence>();
    
    
    public SequencesManager(PersistenceManager persistenceManager) throws MdbException {
   	super(persistenceManager);
   	SchemaConfig schemaConfig =new SchemaConfig(SpecialPaths.SEQUENCES);
   	sequencesSchema = new SchemaManager(persistenceManager, new SchemaConfig(SpecialPaths.SEQUENCES));
       }

    private void addSequence(Sequence sequence) {
	sequences.put(sequence.getName(), sequence);
    }

    public long getNextValForSequence(String seqName) {
	Sequence s = sequences.get(seqName);
	if (s == null) {
	    s = new Sequence(seqName);
	    addSequence(s);
	}
	return s.nextVal();
    }

}
