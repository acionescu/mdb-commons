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
package ro.zg.mdb.core.meta;

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.Sequence;
import ro.zg.util.data.GenericNameValue;

public class SequencesManager {
    private Map<String, Sequence> sequences = new HashMap<String, Sequence>();

    private void addSequence(Sequence sequence) {
	sequences.put(sequence.getId(), sequence);
    }

    private Sequence getSequenceById(String seqId) {
	return sequences.get(seqId);
    }

    public long getNextValForSequence(String seqId) {
	Sequence s = sequences.get(seqId);
	if (s == null) {
	    s = new Sequence(seqId);
	    addSequence(s);
	}
	return s.nextVal();
    }

}
