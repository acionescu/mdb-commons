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

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.ConstraintType;
import ro.zg.mdb.core.filter.ObjectConstraintProcessor;
import ro.zg.mdb.core.meta.data.ObjectDataModel;
import ro.zg.mdb.core.meta.data.Schema;

public class SchemaContext {
    private SequencesManager sequencesManager;
    private ObjectDataManagersRepository objectsManagersRepository;
    private TransactionManagerFactory transactionManagerFactory;
    private SchemaMetadataManager metadataManager;
    private Schema schema;
    private Map<ConstraintType, ObjectConstraintProcessor> constraintProcessors = new HashMap<ConstraintType, ObjectConstraintProcessor>();

    public SchemaContext(Schema schema, SequencesManager sequencesManager,
	    ObjectDataManagersRepository objectsManagersRepository, SchemaMetadataManager metadataManager) {
	super();
	this.schema = schema;
	this.sequencesManager = sequencesManager;
	this.objectsManagersRepository = objectsManagersRepository;
	this.metadataManager = metadataManager;
    }

    public long getNextValForSequence(String seqId) throws MdbException {
	return sequencesManager.getNextValForSequence(seqId);
    }

    public <T> PersistentObjectDataManager<T> getObjectDataManager(Class<T> type, String objectName)
	    throws MdbException {
	return objectsManagersRepository.getObjectDataManager(type, objectName);
    }

    public <T> PersistentObjectDataManager<T> getObjectDataManager(Class<T> type) throws MdbException {
	return objectsManagersRepository.getObjectDataManager(type, type.getName());
    }

    public TransactionManager createTransactionManager() {
	return transactionManagerFactory.getTransactionManager();
    }

    public <T> ObjectDataModel<T> getObjectDataModel(Class<T> type) throws MdbException {
	return schema.getObjectDataModel(type);
    }

    /**
     * @return the sequencesManager
     */
    public SequencesManager getSequencesManager() {
	return sequencesManager;
    }

    /**
     * @return the objectsManagersRepository
     */
    public ObjectDataManagersRepository getObjectsManagersRepository() {
	return objectsManagersRepository;
    }

    /**
     * @return the transactionManagerFactory
     */
    public TransactionManagerFactory getTransactionManagerFactory() {
	return transactionManagerFactory;
    }

    /**
     * @return the metadataManager
     */
    public SchemaMetadataManager getMetadataManager() {
	return metadataManager;
    }

    /**
     * @return the schema
     */
    public Schema getSchema() {
	return schema;
    }

    public void addConstraintProcessor(ConstraintType constraintType, ObjectConstraintProcessor processor) {
	constraintProcessors.put(constraintType, processor);
    }

    public ObjectConstraintProcessor getConstraintProcessor(ConstraintType constraintType) {
	return constraintProcessors.get(constraintType);
    }

    /**
     * @param transactionManagerFactory
     *            the transactionManagerFactory to set
     */
    public void setTransactionManagerFactory(TransactionManagerFactory transactionManagerFactory) {
	this.transactionManagerFactory = transactionManagerFactory;
    }

}
