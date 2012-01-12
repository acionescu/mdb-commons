package ro.zg.mdb.history;

import ro.zg.mdb.core.annotations.ObjectId;
import ro.zg.mdb.core.annotations.Persistable;

@Persistable
public class EntityHistoryData {
    @ObjectId
    private String id;
    
    private long lastChangeTransactionId;

}
