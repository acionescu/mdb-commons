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

import java.util.Collection;

import ro.zg.mdb.commands.CommandContext;
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

    public ObjectDataManager(String objectName, Class<T> type,
	    SchemaContext schemaContext) {

	this.schemaContext = schemaContext;

	this.objectName=objectName;
	this.type=type;

	findProcessor = new FindProcessor<T>();
	insertProcessor = new InsertProcessor<T>();
	deleteProcessor = new DeleteProcessor<T>();
	updateProcessor = new UpdateProcessor<T>();
    }

    public T create(T target) throws MdbException {
	return insertProcessor.process(new CommandContext<T>(objectName,type, schemaContext.createTransactionManager(),
		target));
    }

    public Collection<T> find(Filter filter) throws MdbException {
	return findProcessor.process(new CommandContext<T>(objectName,type, schemaContext.createTransactionManager(),
		filter));
    }

    public Long update(T target, Filter filter) throws MdbException {
	return updateProcessor.process(new CommandContext<T>(objectName,type, schemaContext.createTransactionManager(),
		target, filter));
    }

    public Long delete(Filter filter) throws MdbException {
	return deleteProcessor.process(new CommandContext<T>(objectName,type, schemaContext.createTransactionManager(),
		filter));
    }


    /**
     * @return the schemaContext
     */
    public SchemaContext getSchemaContext() {
	return schemaContext;
    }

}
