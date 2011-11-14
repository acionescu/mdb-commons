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
package ro.zg.mdb.commands.builders;

import ro.zg.mdb.core.meta.ObjectDataManager;

public class CommandBuilderContext<T> {
    private ObjectDataManager<T> objectDataManager;
    private String[] fields;
    private T source;

    public CommandBuilderContext(ObjectDataManager<T> objectDataManager) {
	super();
	this.objectDataManager = objectDataManager;
    }

    public CommandBuilderContext(ObjectDataManager<T> objectDataManager, T source) {
	super();
	this.objectDataManager = objectDataManager;
	this.source = source;
    }

    public CommandBuilderContext(ObjectDataManager<T> objectDataManager, String[] fields) {
	super();
	this.objectDataManager = objectDataManager;
	this.fields = fields;
    }

    public CommandBuilderContext(ObjectDataManager<T> objectDataManager, T source, String[] fields) {
	super();
	this.objectDataManager = objectDataManager;
	this.source = source;
	this.fields = fields;
    }

    /**
     * @return the source
     */
    public T getSource() {
	return source;
    }

    /**
     * @return the fields
     */
    public String[] getFields() {
	return fields;
    }

    /**
     * @return the objectDataManager
     */
    public ObjectDataManager<T> getObjectDataManager() {
	return objectDataManager;
    }

}
