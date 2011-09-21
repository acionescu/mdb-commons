package ro.zg.mdb.persistence;

import java.io.File;
import java.io.IOException;

import ro.zg.util.io.file.FileHandler;
import ro.zg.util.io.file.FileUtil;
import ro.zg.util.io.file.LineHandler;

public class FilePersistenceManager implements PersistenceManager {
    private String separator = File.separator;
    private String rootPath;

    public FilePersistenceManager(String rootPath) {
	this.rootPath = rootPath;
	FileUtil.initDirectory(rootPath);
    }

    @Override
    public void append(String id,String data) {
	try {
	    FileUtil.writeToFile(getAbsolutePath(id), data, true);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public long countChildren(String id) {
	return new File(getAbsolutePath(id)).list().length;
    }

    @Override
    public boolean exists(String id) {
	return new File(getAbsolutePath(id)).exists();
    }

    @Override
    public void write(String id,String data) {
	try {
	    FileUtil.writeToFile(getAbsolutePath(id), data, false);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public String getPath(String... ids) {
	String path = "";
	boolean addSeparator = false;
	for (String id : ids) {
	    if (addSeparator) {
		path += separator;
	    } else {
		addSeparator = true;
	    }
	    path += id;
	}
	return path;
    }

    private String getAbsolutePath(String id) {
	return getPath(rootPath, id);
    }

    @Override
    public String getPath(String p1, String p2) {
	return p1 + separator + p2;
    }

    /**
     * @return the rootPath
     */
    public String getRootPath() {
	return rootPath;
    }

    @Override
    public PersistenceManager getPersistenceManager(String id) {
	return new FilePersistenceManager(getAbsolutePath(id));
    }

    @Override
    public void read(String id, LineHandler handler) {
	try {
	    FileUtil.readFromFile(getAbsolutePath(id), handler);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public String[] listChildren(String id) {
	return new File(getAbsolutePath(id)).list();
    }

    @Override
    public void readAllChildren(String parentId, FileHandler handler) {
	String parentPath=getAbsolutePath(parentId);
	for(String child : new File(parentPath).list()) {
	    if(!handler.startFile(child)) {
		continue;
	    }
	    read(getPath(parentPath,child), handler);
	    if(!handler.endFile(child)) {
		break;
	    }
	}
	
    }

    @Override
    public boolean create(String id) throws PersistenceException {
	try {
	    return new File(getAbsolutePath(id)).createNewFile();
	} catch (IOException e) {
	    throw new PersistenceException("Failed to create file '"+id+"'",e);
	}
    }

    @Override
    public boolean delete(String id) {
	return FileUtil.recursiveDelete(new File(getAbsolutePath(id)));
    }

    
}
