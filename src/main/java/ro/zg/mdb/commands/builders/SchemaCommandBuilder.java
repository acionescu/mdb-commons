package ro.zg.mdb.commands.builders;

import ro.zg.mdb.commands.DeleteCommand;
import ro.zg.mdb.commands.GetCommand;
import ro.zg.mdb.commands.InsertCommand;
import ro.zg.mdb.commands.UpdateCommand;
import ro.zg.mdb.core.meta.ObjectDataManager;
import ro.zg.mdb.core.meta.SchemaManager;


public class SchemaCommandBuilder<T> {
    private ObjectDataManager<T> objectDataManager;
    
   

    public SchemaCommandBuilder(ObjectDataManager<T> objectDataManager) {
	super();
	this.objectDataManager = objectDataManager;
    }

    public GetCommand<T> get(){
	return new GetCommand<T>(new CommandBuilderContext<T>(objectDataManager));
    }
    
    public GetCommand<T> get(String... fields){
	return new GetCommand<T>(new CommandBuilderContext<T>(objectDataManager, fields));
    }
    
    public InsertCommand<T> insert(T source) {
	return new InsertCommand<T>(new CommandBuilderContext<T>(objectDataManager,source));
    }
    
    public UpdateCommand<T> update(T source, String... fields) {
	return new UpdateCommand<T>(new CommandBuilderContext<T>(objectDataManager, source, fields));
    }
    
    public DeleteCommand<T> delete() {
	return new DeleteCommand<T>(new CommandBuilderContext<T>(objectDataManager));
    }
    
}
