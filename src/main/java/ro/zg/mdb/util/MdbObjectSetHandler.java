package ro.zg.mdb.util;

import ro.zg.util.io.file.FileHandler;

public abstract class MdbObjectSetHandler extends MdbObjectHandler implements FileHandler{
    protected long rowCount;

    /**
     * @return the rowCount
     */
    public long getRowCount() {
        return rowCount;
    }
    
    
}
