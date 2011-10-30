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

import java.util.Collection;

import ro.zg.mdb.commands.CommandContext;
import ro.zg.mdb.commands.processors.DeleteProcessor;
import ro.zg.mdb.commands.processors.FindProcessor;
import ro.zg.mdb.commands.processors.InsertProcessor;
import ro.zg.mdb.commands.processors.UpdateProcessor;
import ro.zg.mdb.core.concurrency.PersistableObjectLockManager;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.persistence.PersistenceManager;

public class ObjectDataManager<T> {
    private ObjectDataModel<T> objectDataModel;
    private SchemaContext schemaContext;
    private PersistableObjectLockManager locksManager;
    private FindProcessor<T> findProcessor;
    private InsertProcessor<T> insertProcessor;
    private DeleteProcessor<T> deleteProcessor;
    private UpdateProcessor<T> updateProcessor;

    public ObjectDataManager(PersistenceManager persistenceManager, ObjectDataModel<T> objectDataModel,
	    SchemaContext schemaContext) {
	
	this.objectDataModel = objectDataModel;
	this.schemaContext = schemaContext;
	this.locksManager=new PersistableObjectLockManager();
	
	findProcessor = new FindProcessor<T>(persistenceManager,locksManager);
	insertProcessor = new InsertProcessor<T>(persistenceManager,locksManager);
	deleteProcessor = new DeleteProcessor<T>(persistenceManager,locksManager);
	updateProcessor = new UpdateProcessor<T>(persistenceManager,locksManager);
    }

    public T save(T target) throws MdbException {
	return insertProcessor.process(new CommandContext<T>(this, target));
    }

    public Collection<T> find(Filter filter) throws MdbException {
	return findProcessor.process(new CommandContext<T>(this,filter));
    }
    
    public Long update(T target, Filter filter) throws MdbException {
	return updateProcessor.process(new CommandContext<T>(this, target, filter));
    }
    
    public Long delete(Filter filter) throws MdbException {
	return deleteProcessor.process(new CommandContext<T>(this, filter));
    }

    /**
     * @return the objectDataModel
     */
    public ObjectDataModel<T> getObjectDataModel() {
        return objectDataModel;
    }

    /**
     * @return the schemaContext
     */
    public SchemaContext getSchemaContext() {
        return schemaContext;
    }

}
