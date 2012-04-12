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
package ro.zg.mdb.core.meta.persistence;

import ro.zg.mdb.commands.builders.SchemaCommandBuilder;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.constants.SpecialPaths;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.AndObjectConstraintProcessor;
import ro.zg.mdb.core.filter.ConstraintType;
import ro.zg.mdb.core.filter.OrObjectConstraintProcessor;
import ro.zg.mdb.core.meta.persistence.data.SchemaConfig;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;

public class SchemaManager extends PersistentDataManager {
    private SchemaContext schemaContext;

    public SchemaManager(PersistenceManager persistenceManager, SchemaConfig config) throws MdbException {
	super(persistenceManager);

	LinksManager linksManager = null;
	if (config.isObjectReferenceAllowed()) {
	    try {
		linksManager = new LinksManager(persistenceManager.getPersistenceManager(SpecialPaths.LINKS));
	    } catch (PersistenceException e) {
		throw new MdbException(e, MdbErrorType.PERSISTENCE_ERROR);
	    }
	}
	SequencesManager sequencesManager = null;
	if (config.isSequenceUsageAllowed()) {
	    try {
		sequencesManager = new SequencesManager(
			persistenceManager.getPersistenceManager(SpecialPaths.SEQUENCES));
	    } catch (PersistenceException e) {
		throw new MdbException(e, MdbErrorType.PERSISTENCE_ERROR);
	    }
	}
	ObjectDataManagersRepository objectsManagersRepository;
	try {
	    objectsManagersRepository = new ObjectDataManagersRepository(schemaContext,
		    persistenceManager.getPersistenceManager(SpecialPaths.TABLES));
	} catch (PersistenceException e) {
	    throw new MdbException(e, MdbErrorType.PERSISTENCE_ERROR);
	}

	SchemaMetadataManager metadataManager = new SchemaMetadataManager(persistenceManager, config);
	

	schemaContext = new SchemaContext(sequencesManager, objectsManagersRepository, linksManager,
		metadataManager);
	objectsManagersRepository.setSchemaContext(schemaContext);

	schemaContext.addConstraintProcessor(ConstraintType.AND, new AndObjectConstraintProcessor());
	schemaContext.addConstraintProcessor(ConstraintType.OR, new OrObjectConstraintProcessor());

	TransactionManagerFactory transactionManagerFactory = new DefaultTransactionManagerFactory(schemaContext);
	schemaContext.setTransactionManagerFactory(transactionManagerFactory);

    }

    private void init() {

    }

    private void initSchema() {

    }

    public <T> SchemaCommandBuilder<T> createCommand(Class<T> type) throws MdbException {
	return new SchemaCommandBuilder<T>(new ObjectDataManager<T>(type.getName(), type, schemaContext));
    }

    public <T> SchemaCommandBuilder<T> createCommand(Class<T> type, String objectName) throws MdbException {
	return new SchemaCommandBuilder<T>(new ObjectDataManager<T>(objectName, type, schemaContext));
    }


}
