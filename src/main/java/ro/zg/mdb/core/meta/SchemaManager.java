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

import ro.zg.mdb.commands.builders.SchemaCommandBuilder;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.constants.SpecialPaths;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.Schema;
import ro.zg.mdb.core.meta.data.SchemaConfig;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;

public class SchemaManager extends PersistentDataManager {
    private Schema schema;
    private SchemaContext schemaContext;
    
    public SchemaManager(PersistenceManager persistenceManager, SchemaConfig config) throws MdbException {
	super(persistenceManager);
	schema = new Schema(config);
	
	SchemaMetadataManager metadataManager=null;

	if (config.isMetadataAllowed()) {
	    try {
		metadataManager = new SchemaMetadataManager(persistenceManager.getPersistenceManager(SpecialPaths.META));
	    } catch (PersistenceException e) {
		throw new MdbException(e, MdbErrorType.PERSISTENCE_ERROR);
	    }
	}
	SequencesManager sequencesManager = new SequencesManager();
	ObjectDataManagersRepository objectsManagersRepository; objectsManagersRepository=new ObjectDataManagersRepository(schema, schemaContext, persistenceManager);
	TransactionManagerFactory transactionManagerFactory = new DefaultTransactionManagerFactory(schemaContext);
	schemaContext = new SchemaContext(schema,sequencesManager,objectsManagersRepository,transactionManagerFactory,metadataManager);
	
    }

    private void init() {

    }

    private void initSchema() {

    }

    public <T> SchemaCommandBuilder<T> createCommand(Class<T> type) throws MdbException {
	return new SchemaCommandBuilder<T>(new ObjectDataManager<T>( type.getName(),type,schemaContext));
    }

    public <T> SchemaCommandBuilder<T> createCommand(Class<T> type, String objectName) throws MdbException {
	return new SchemaCommandBuilder<T>(new ObjectDataManager<T>( objectName,type,schemaContext));
    }


    /**
     * @return the schema
     */
    public Schema getSchema() {
	return schema;
    }

}
