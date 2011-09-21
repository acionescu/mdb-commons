package ro.zg.mdb.util;

import java.util.LinkedHashSet;
import java.util.Set;

import ro.zg.util.io.file.LineHandler;

public class FullReader implements LineHandler{
    Set<String> data=new LinkedHashSet<String>();
    
    @Override
    public boolean handle(String line) {
	data.add(line);
	return true;
    }

    /**
     * @return the data
     */
    public Set<String> getData() {
        return data;
    }
    
    
}
