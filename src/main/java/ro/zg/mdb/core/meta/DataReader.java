package ro.zg.mdb.core.meta;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;
import ro.zg.mdb.util.MdbObjectHandler;
import ro.zg.mdb.util.MdbObjectSetHandler;
import ro.zg.util.io.file.LineHandler;

public class DataReader {
    private PersistenceManager persistenceManager;

    public DataReader(PersistenceManager persistenceManager) {
	super();
	this.persistenceManager = persistenceManager;
    }

    public Collection<String> read(String id, final Collection<String> data) throws MdbException {
	try {
	    persistenceManager.read(id, new LineHandler() {

	        @Override
	        public boolean handle(String line) {
	    	data.add(line);
	    	return true;
	        }
	    });
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
	return data;
    }

    public Collection<String> read(String id) throws MdbException {
	final Set<String> data = new LinkedHashSet<String>();
	return read(id, data);
    }

    public <T> Collection<T> readAllObjects(String id, final ObjectDataModel<T> odm, final Filter filter,
	    final Collection<T> dataHolder) throws MdbException {

	MdbObjectSetHandler handler=new MdbObjectSetHandler() {
	    private int readCount = 0;

	    @Override
	    public boolean handle(String line) {
		T object;
		try {
		    object = odm.getObjectFromString(line, filter);
		} catch (MdbException e) {
		    error=e;
		    return false;
		}
		if (object != null) {
		    dataHolder.add(object);
		}
		return true;
	    }

	    @Override
	    public boolean startFile(String name) {
		return true;
	    }

	    @Override
	    public boolean endFile(String name) {
		return true;
	    }
	};
	try {
	    persistenceManager.readAllChildren(id, handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
	
	if(handler.hasError()) {
	    throw handler.getError();
	}
	return dataHolder;
    }

    public <T> Collection<T> readAllObjectsBut(String id, final ObjectDataModel<T> odm, final Filter filter,
	    final Collection<T> dataHolder, final Collection<String> restricted) throws MdbException {

	MdbObjectSetHandler handler=new MdbObjectSetHandler() {
	    private int readCount = 0;

	    @Override
	    public boolean handle(String line) {
		T object;
		try {
		    object = odm.getObjectFromString(line, filter);
		} catch (MdbException e) {
		    error=e;
		    return false;
		}
		if (object != null) {
		    dataHolder.add(object);
		}
		return true;
	    }

	    @Override
	    public boolean startFile(String name) {
		return !restricted.contains(name);
	    }

	    @Override
	    public boolean endFile(String name) {
		return true;
	    }
	};
	
	try {
	    persistenceManager.readAllChildren(id, handler);
	} catch (PersistenceException e) {
	    throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	}
	
	if(handler.hasError()) {
	    throw handler.getError();
	}
	
	return dataHolder;
    }

    public <T> Collection<T> readObjects(Collection<String> ids, final ObjectDataModel<T> odm, final Filter filter,
	    final Collection<T> dataHolder) throws MdbException {

	MdbObjectHandler handler = new MdbObjectHandler() {

	    @Override
	    public boolean handle(String line) {
		T object;
		try {
		    object = odm.getObjectFromString(line, filter);
		} catch (MdbException e) {
		    error = e;
		    return false;
		}
		if (object != null) {
		    dataHolder.add(object);
		}
		return true;
	    }
	};
	String typeName;
	for (String id : ids) {
	    try {
		persistenceManager.read(id, handler);
	    } catch (PersistenceException e) {
		throw new MdbException(MdbErrorType.PERSISTENCE_ERROR, e);
	    }
	}
	if(handler.hasError()) {
	    throw handler.getError();
	}
	
	return dataHolder;
    }
}
