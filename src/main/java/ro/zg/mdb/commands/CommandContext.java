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

import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.meta.ObjectDataManager;
import ro.zg.mdb.core.meta.ObjectDataModel;
import ro.zg.mdb.core.meta.SchemaContext;

public class CommandContext<T> {
   private ObjectDataManager<T> objectDataManager;
    private Filter filter;
    private T source;
    
    public CommandContext(ObjectDataManager<T> objectDataManager, Filter filter) {
	super();
	this.objectDataManager = objectDataManager;
	this.filter = filter;
    }
    
    
    public CommandContext(ObjectDataManager<T> objectDataManager, T source) {
	super();
	this.objectDataManager = objectDataManager;
	this.source = source;
    }


    public CommandContext(ObjectDataManager<T> objectDataManager, T source, Filter filter) {
	super();
	this.objectDataManager = objectDataManager;
	this.source = source;
	this.filter = filter;
    }

    /**
     * @return the objectDataModel
     */
    public ObjectDataModel<T> getObjectDataModel() {
        return objectDataManager.getObjectDataModel();
    }
    
    public SchemaContext getSchemaContext() {
	return objectDataManager.getSchemaContext();
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
    
    
}
