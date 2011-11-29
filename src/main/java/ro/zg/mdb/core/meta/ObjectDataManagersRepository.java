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

import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.ObjectDataModel;
import ro.zg.mdb.core.meta.data.Schema;
import ro.zg.mdb.persistence.PersistenceManager;

public class ObjectDataManagersRepository extends PersistentDataManager{
    private Schema schema;
    private SchemaContext schemaContext;
    
    public ObjectDataManagersRepository(Schema schema, SchemaContext schemaContext, PersistenceManager persistenceManager) {
	super(persistenceManager);
	this.schema = schema;
	this.schemaContext=schemaContext;
    }

    private Map<String, PersistentObjectDataManager<?>> objectDataManagers = new HashMap<String, PersistentObjectDataManager<?>>();
    
    @SuppressWarnings("unchecked")
    public <T> PersistentObjectDataManager<T> getObjectDataManager(Class<T> type, String objectName) throws MdbException {
	synchronized (type) {
	    PersistentObjectDataManager<T> odm = (PersistentObjectDataManager<T>) objectDataManagers.get(objectName);
	    if (odm == null) {
		ObjectDataModel<T> odModel = (ObjectDataModel<T>) schema.getObjectDataModel(type);
		odm = new PersistentObjectDataManager<T>(getPersistenceManager(objectName), odModel,objectName);
		objectDataManagers.put(objectName, odm);
	    }
	    return odm;
	}
    }
    
   
}
