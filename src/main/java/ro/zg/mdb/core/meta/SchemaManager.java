/*******************************************************************************
 * Copyright (c) 2011 Adrian Cristian Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Adrian Cristian Ionescu - initial API and implementation
 ******************************************************************************/
package ro.zg.mdb.core.meta;

import ro.zg.mdb.commands.builders.SchemaCommandBuilder;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.constants.SpecialPaths;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.AndObjectConstraintProcessor;
import ro.zg.mdb.core.filter.ConstraintType;
import ro.zg.mdb.core.filter.OrObjectConstraintProcessor;
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
	
	
	schemaContext = new SchemaContext(schema,sequencesManager,objectsManagersRepository,metadataManager);
	
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
