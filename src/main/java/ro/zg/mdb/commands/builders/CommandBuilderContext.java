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
