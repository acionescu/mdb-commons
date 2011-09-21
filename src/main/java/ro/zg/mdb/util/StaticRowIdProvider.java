package ro.zg.mdb.util;

public class StaticRowIdProvider implements RowIdProvider{
    private String rowId;
    
    public StaticRowIdProvider(String rowId) {
	super();
	this.rowId = rowId;
    }

    @Override
    public String provideRowId() {
	return rowId;
    }

}
