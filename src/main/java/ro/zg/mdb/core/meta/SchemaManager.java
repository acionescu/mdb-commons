package ro.zg.mdb.core.meta;

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.commands.builders.SchemaCommandBuilder;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.persistence.PersistenceManager;

public class SchemaManager extends PersistentDataManager {
    private Schema schema;
    private SchemaContext schemaContext;
    private SequencesManager sequencesManager;
    private Map<Class<?>, ObjectDataManager<?>> objectDataManagers = new HashMap<Class<?>, ObjectDataManager<?>>();

    public SchemaManager(String name, PersistenceManager persistanceManager) {
	this(name, persistanceManager, new SchemaConfig());
    }

    public SchemaManager(String name, PersistenceManager persistenceManager, SchemaConfig config) {
	super(persistenceManager);
	schema = new Schema(name, config);
	sequencesManager = new SequencesManager();
	schemaContext = new SchemaContext(sequencesManager);
    }

    private void init() {

    }

    private void initSchema() {

    }

    public <T> SchemaCommandBuilder<T> createCommand(Class<T> type) throws MdbException {
	return new SchemaCommandBuilder<T>(getObjectDataManager(type));
    }

    @SuppressWarnings("unchecked")
    private <T> ObjectDataManager<T> getObjectDataManager(Class<T> type) throws MdbException {
	synchronized (type) {
	    ObjectDataManager<T> odm = (ObjectDataManager<T>) objectDataManagers.get(type);
	    if (odm == null) {
		ObjectDataModel<T> odModel = schema.getObjectDataModel(type);
		odm = new ObjectDataManager<T>(getPersistenceManager(type.getName()), odModel, schemaContext);
		objectDataManagers.put(type, odm);
	    }
	    return odm;
	}
    }

    /**
     * @return the schema
     */
    public Schema getSchema() {
	return schema;
    }

}
