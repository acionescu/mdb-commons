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

    public GetCommand<T, T> get() {
	return new GetCommand<T, T>(new CommandBuilderContext<T>(objectDataManager));
    }

    public GetCommand<T, T> get(String... fields) {
	return new GetCommand<T, T>(new CommandBuilderContext<T>(objectDataManager, fields));
    }

    public <R> GetCommand<T, R> get(FindResultBuilderFactory<T, R> resultBuilderFactory) {
	return new GetCommand<T, R>(new CommandBuilderContext<T>(objectDataManager), resultBuilderFactory);
    }

    public <R> GetCommand<T, R> get(FindResultBuilderFactory<T, R> resultBuilderFactory, String... fields) {
	return new GetCommand<T, R>(new CommandBuilderContext<T>(objectDataManager, fields), resultBuilderFactory);
    }

    public InsertCommand<T> insert(T source) {
	return new InsertCommand<T>(new CommandBuilderContext<T>(objectDataManager, source));
    }

    public UpdateCommand<T> update(T source, String... fields) {
	return new UpdateCommand<T>(new CommandBuilderContext<T>(objectDataManager, source, fields));
    }

    public DeleteCommand<T> delete() {
	return new DeleteCommand<T>(new CommandBuilderContext<T>(objectDataManager));
    }

}
