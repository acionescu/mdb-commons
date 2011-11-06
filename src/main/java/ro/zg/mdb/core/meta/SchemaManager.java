/*******************************************************************************
 * Copyright 2011 Adrian Cristian Ionescu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ro.zg.mdb.core.meta;

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.commands.builders.SchemaCommandBuilder;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.constants.SpecialPaths;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;

public class SchemaManager extends PersistentDataManager {
    private Schema schema;
    private SchemaContext schemaContext;
    private SequencesManager sequencesManager;
    private Map<Class<?>, ObjectDataManager<?>> objectDataManagers = new HashMap<Class<?>, ObjectDataManager<?>>();
    private SchemaMetadataManager metadataManager;

    public SchemaManager(PersistenceManager persistenceManager, SchemaConfig config) throws MdbException {
	super(persistenceManager);
	schema = new Schema(config);
	sequencesManager = new SequencesManager();

	if (config.isMetadataAllowed()) {
	    try {
		metadataManager = new SchemaMetadataManager(persistenceManager.getPersistenceManager(SpecialPaths.META));
	    } catch (PersistenceException e) {
		throw new MdbException(e, MdbErrorType.PERSISTENCE_ERROR);
	    }
	}
	schemaContext = new SchemaContext(sequencesManager);

    }

    private void init() {

    }

    private void initSchema() {

    }

    public <T> SchemaCommandBuilder<T> createCommand(Class<T> type) throws MdbException {
	return new SchemaCommandBuilder<T>(getObjectDataManager(type, type.getName()));
    }

    public <T> SchemaCommandBuilder<T> createCommand(Class<T> type, String objectName) throws MdbException {
	return new SchemaCommandBuilder<T>(getObjectDataManager(type, objectName));
    }

    @SuppressWarnings("unchecked")
    private <T> ObjectDataManager<T> getObjectDataManager(Class<T> type, String objectName) throws MdbException {
	synchronized (type) {
	    ObjectDataManager<T> odm = (ObjectDataManager<T>) objectDataManagers.get(type);
	    if (odm == null) {
		ObjectDataModel<T> odModel = (ObjectDataModel<T>) schema.getObjectDataModel(type);
		odm = new ObjectDataManager<T>(getPersistenceManager(objectName), odModel, schemaContext);
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
