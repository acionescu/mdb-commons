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

import ro.zg.mdb.commands.DeleteCommandContext;
import ro.zg.mdb.commands.FindCommandContext;
import ro.zg.mdb.commands.InsertCommandContext;
import ro.zg.mdb.commands.UpdateCommandContext;
import ro.zg.mdb.commands.builders.FindResultBuilder;
import ro.zg.mdb.commands.builders.PersistentCollection;
import ro.zg.mdb.commands.builders.SimpleResultBuilder;
import ro.zg.mdb.commands.processors.DeleteProcessor;
import ro.zg.mdb.commands.processors.FindProcessor;
import ro.zg.mdb.commands.processors.InsertProcessor;
import ro.zg.mdb.commands.processors.UpdateProcessor;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;

public class ObjectDataManager<T> {
    private String objectName;
    private Class<T> type;
    private SchemaContext schemaContext;
    private FindProcessor<T> findProcessor;
    private InsertProcessor<T> insertProcessor;
    private DeleteProcessor<T> deleteProcessor;
    private UpdateProcessor<T> updateProcessor;

    public ObjectDataManager(String objectName, Class<T> type, SchemaContext schemaContext) {

	this.schemaContext = schemaContext;

	this.objectName = objectName;
	this.type = type;

	findProcessor = new FindProcessor<T>();
	insertProcessor = new InsertProcessor<T>();
	deleteProcessor = new DeleteProcessor<T>();
	updateProcessor = new UpdateProcessor<T>();
    }

    public T create(T target) throws MdbException {
	InsertCommandContext<T> commandContext = new InsertCommandContext<T>(objectName, type,
		schemaContext.createTransactionManager(), target, new SimpleResultBuilder<T>());
	insertProcessor.process(commandContext);
	return commandContext.getResultBuilder().getResult();
    }

    public <R> PersistentCollection<R> find(Filter filter, FindResultBuilder<T, R> resultBuilder) throws MdbException {
	FindCommandContext<T, R> commandContext = new FindCommandContext<T, R>(objectName, type,
		schemaContext.createTransactionManager(), filter, resultBuilder);
	findProcessor.process(commandContext);
	return commandContext.getResultBuilder().getResult();
    }

    public Long update(T target, Filter filter) throws MdbException {
	SimpleResultBuilder<Long> resultBuilder = new SimpleResultBuilder<Long>();
	updateProcessor.process(new UpdateCommandContext<T>(objectName, type, schemaContext.createTransactionManager(),
		filter, target, resultBuilder));
	return resultBuilder.getResult();
    }

    public Long delete(Filter filter) throws MdbException {
	SimpleResultBuilder<Long> resultBuilder = new SimpleResultBuilder<Long>();
	deleteProcessor.process(new DeleteCommandContext<T>(objectName, type,
		schemaContext.createTransactionManager(),resultBuilder, filter));
	return resultBuilder.getResult();
    }

    /**
     * @return the schemaContext
     */
    public SchemaContext getSchemaContext() {
	return schemaContext;
    }

}
