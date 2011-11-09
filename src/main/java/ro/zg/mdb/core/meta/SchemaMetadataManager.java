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
package ro.zg.mdb.core.meta;

import java.util.Collection;

import ro.zg.mdb.commands.GetCommand;
import ro.zg.mdb.constants.MdbErrorType;
import ro.zg.mdb.constants.SpecialPaths;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.LinkModel;
import ro.zg.mdb.core.meta.data.ObjectsLink;
import ro.zg.mdb.core.meta.data.SchemaConfig;
import ro.zg.mdb.persistence.PersistenceException;
import ro.zg.mdb.persistence.PersistenceManager;

public class SchemaMetadataManager extends PersistentDataManager {
    private SchemaManager linksSchema;
    private SchemaManager metadataSchema;

    public SchemaMetadataManager(PersistenceManager persistenceManager) throws MdbException {
	super(persistenceManager);
	try {
	    linksSchema = new SchemaManager(persistenceManager.getPersistenceManager(SpecialPaths.LINKS),
		    new SchemaConfig(SpecialPaths.LINKS));
	    metadataSchema = new SchemaManager(persistenceManager.getPersistenceManager(SpecialPaths.META),
		    new SchemaConfig(SpecialPaths.META));
	} catch (PersistenceException e) {
	    throw new MdbException(e, MdbErrorType.PERSISTENCE_ERROR);
	}
    }

    public ObjectsLink saveLink(String linkName, ObjectsLink link) throws MdbException {
	return linksSchema.createCommand(ObjectsLink.class, linkName).insert(link).execute();
    }

    public Collection<ObjectsLink> getObjectLinks(LinkModel linkModel, String rowId) throws MdbException{
	GetCommand<ObjectsLink> getCommand = linksSchema.createCommand(ObjectsLink.class, linkModel.getName()).get();
	String queryField=null;
	if(linkModel.isFirst()) {
	    queryField="firstRowId";
	}
	else {
	    queryField="secondRowId";
	}
	
	return getCommand.where().field(queryField).eq(rowId).execute();
    }
    
}
