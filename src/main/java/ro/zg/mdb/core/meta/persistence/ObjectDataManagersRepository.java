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
package ro.zg.mdb.core.meta.persistence;

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadata;
import ro.zg.mdb.persistence.PersistenceManager;

public class ObjectDataManagersRepository extends PersistentDataManager{
    private SchemaContext schemaContext;
    
    public ObjectDataManagersRepository(SchemaContext schemaContext, PersistenceManager persistenceManager) {
	super(persistenceManager);
	this.schemaContext=schemaContext;
    }

    private Map<String, PersistentObjectDataManager<?>> objectDataManagers = new HashMap<String, PersistentObjectDataManager<?>>();
    
    @SuppressWarnings("unchecked")
    public <T> PersistentObjectDataManager<T> getObjectDataManager(Class<T> type, String objectName) throws MdbException {
	synchronized (type) {
	    PersistentObjectDataManager<T> odm = (PersistentObjectDataManager<T>) objectDataManagers.get(objectName);
	    if (odm == null) {
		PersistentObjectMetadata<T> odModel = (PersistentObjectMetadata<T>) schemaContext.getObjectDataModel(type);
		odm = new PersistentObjectDataManager<T>(getPersistenceManager(objectName), odModel,objectName);
		objectDataManagers.put(objectName, odm);
	    }
	    return odm;
	}
    }

    /**
     * @return the schemaContext
     */
    public SchemaContext getSchemaContext() {
        return schemaContext;
    }

    /**
     * @param schemaContext the schemaContext to set
     */
    public void setSchemaContext(SchemaContext schemaContext) {
        this.schemaContext = schemaContext;
    }
    
   
}
