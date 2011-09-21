package ro.zg.mdb.core.meta;

import ro.zg.mdb.core.exceptions.MdbException;

public class SchemaContext {
    private SequencesManager sequencesManager;

    public SchemaContext(SequencesManager sequencesManager) {
	super();
	this.sequencesManager = sequencesManager;
    }
    
    public long getNextValForSequence(String seqId) throws MdbException{
	return sequencesManager.getNextValForSequence(seqId);
    }
}
