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
