package ro.zg.mdb.core.meta;

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
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
