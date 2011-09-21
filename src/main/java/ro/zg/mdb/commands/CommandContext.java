package ro.zg.mdb.commands;

import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.meta.ObjectDataManager;
import ro.zg.mdb.core.meta.ObjectDataModel;
import ro.zg.mdb.core.meta.SchemaContext;

public class CommandContext<T> {
   private ObjectDataManager<T> objectDataManager;
    private Filter filter;
    private T source;
    
    public CommandContext(ObjectDataManager<T> objectDataManager, Filter filter) {
	super();
	this.objectDataManager = objectDataManager;
	this.filter = filter;
    }
    
    
    public CommandContext(ObjectDataManager<T> objectDataManager, T source) {
	super();
	this.objectDataManager = objectDataManager;
	this.source = source;
    }


    public CommandContext(ObjectDataManager<T> objectDataManager, T source, Filter filter) {
	super();
	this.objectDataManager = objectDataManager;
	this.source = source;
	this.filter = filter;
    }

    /**
     * @return the objectDataModel
     */
    public ObjectDataModel<T> getObjectDataModel() {
        return objectDataManager.getObjectDataModel();
    }
    
    public SchemaContext getSchemaContext() {
	return objectDataManager.getSchemaContext();
    }
    
    /**
     * @return the filter
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @return the source
     */
    public T getSource() {
        return source;
    }
    
    
}
