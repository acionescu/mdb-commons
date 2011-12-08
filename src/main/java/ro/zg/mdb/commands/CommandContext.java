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
package ro.zg.mdb.commands;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.meta.TransactionManager;
import ro.zg.mdb.core.meta.data.ObjectDataModel;

public class CommandContext<T> {
    private String objectName;
    private Class<T> type;
    private TransactionManager transactionManager;
    private Filter filter;
    private T source;

    public CommandContext(String objectName, Class<T> type, TransactionManager transactionManager, Filter filter) {
	super();
	this.objectName = objectName;
	this.type = type;
	this.filter = filter;
	this.transactionManager = transactionManager;
    }

    public CommandContext(String objectName, Class<T> type, TransactionManager transactionManager, T source) {
	super();
	this.objectName = objectName;
	this.type = type;
	this.source = source;
	this.transactionManager = transactionManager;
    }

    public CommandContext(String objectName, Class<T> type, TransactionManager transactionManager, T source,
	    Filter filter) {
	super();
	this.objectName = objectName;
	this.type = type;
	this.source = source;
	this.filter = filter;
	this.transactionManager = transactionManager;
    }

    public ObjectDataModel<T> getObjectDataModel() throws MdbException {
	return transactionManager.getObjectDataModel(type);
    }

    public TransactionManager getTransactionManager() {
	return transactionManager;
    }

    /**
     * @return the filter
     */
    public Filter getFilter() {
	return filter;
    }

    /**
     * @return the source
     */
    public T getSource() {
	return source;
    }

    /**
     * @return the objectName
     */
    public String getObjectName() {
	return objectName;
    }

    /**
     * @return the type
     */
    public Class<T> getType() {
	return type;
    }

}
