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

import ro.zg.mdb.commands.DeleteCommand;
import ro.zg.mdb.commands.GetCommand;
import ro.zg.mdb.commands.InsertCommand;
import ro.zg.mdb.commands.UpdateCommand;
import ro.zg.mdb.core.meta.ObjectDataManager;


public class SchemaCommandBuilder<T> {
    private ObjectDataManager<T> objectDataManager;
   
    

    public SchemaCommandBuilder(ObjectDataManager<T> objectDataManager) {
	super();
	this.objectDataManager = objectDataManager;
    }

    public GetCommand<T> get(){
	return new GetCommand<T>(new CommandBuilderContext<T>(objectDataManager));
    }
    
    public GetCommand<T> get(String... fields){
	return new GetCommand<T>(new CommandBuilderContext<T>(objectDataManager, fields));
    }
    
    public InsertCommand<T> insert(T source) {
	return new InsertCommand<T>(new CommandBuilderContext<T>(objectDataManager,source));
    }
    
    public UpdateCommand<T> update(T source, String... fields) {
	return new UpdateCommand<T>(new CommandBuilderContext<T>(objectDataManager, source, fields));
    }
    
    public DeleteCommand<T> delete() {
	return new DeleteCommand<T>(new CommandBuilderContext<T>(objectDataManager));
    }
    
}
