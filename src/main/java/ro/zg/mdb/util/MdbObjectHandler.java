package ro.zg.mdb.util;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.util.io.file.LineHandler;

public abstract class MdbObjectHandler implements LineHandler{
    protected String data;
    protected MdbException error;
    
    public MdbException getError() {
	return error;
    }
    
    public boolean hasError() {
	return error!=null;
    }

    /**
     * @return the data
     */
    public String getData() {
        return data;
    }
    
    
}
