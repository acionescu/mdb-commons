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

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.ObjectDataModel;
import ro.zg.mdb.core.meta.data.Schema;

public class SchemaContext {
    private SequencesManager sequencesManager;
    private ObjectDataManagersRepository objectsManagersRepository;
    private TransactionManagerFactory transactionManagerFactory;
    private SchemaMetadataManager metadataManager;
    private Schema schema;

    public SchemaContext(Schema schema, SequencesManager sequencesManager, ObjectDataManagersRepository objectsManagersRepository,
	    TransactionManagerFactory transactionManagerFactory, SchemaMetadataManager metadataManager) {
	super();
	this.schema=schema;
	this.sequencesManager = sequencesManager;
	this.objectsManagersRepository = objectsManagersRepository;
	this.transactionManagerFactory = transactionManagerFactory;
	this.metadataManager = metadataManager;
    }


    public long getNextValForSequence(String seqId) throws MdbException{
	return sequencesManager.getNextValForSequence(seqId);
    }
    
    public <T> PersistentObjectDataManager<T> getObjectDataManager(Class<T> type, String objectName) throws MdbException{
	return objectsManagersRepository.getObjectDataManager(type, objectName);
    }
    
    public <T> PersistentObjectDataManager<T> getObjectDataManager(Class<T> type) throws MdbException{
	return objectsManagersRepository.getObjectDataManager(type, type.getName());
    }

    public TransactionManager createTransactionManager() {
	return transactionManagerFactory.getTransactionManager();
    }
    
    public <T> ObjectDataModel<T> getObjectDataModel(Class<T> type){
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
    
    
}
