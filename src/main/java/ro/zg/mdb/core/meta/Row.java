package ro.zg.mdb.core.meta;

import ro.zg.mdb.core.annotations.ForeignKey;
import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.annotations.PrimaryKey;
import ro.zg.mdb.core.annotations.Unique;

@Persistable
public class Row {
    @PrimaryKey
    private String rowId;
    @Unique(id=1)
    private String hash;
    @Unique(id=1)
    private long timestamp;
    @ForeignKey(targetType=Row.class)
    private String parentRowId;
}
