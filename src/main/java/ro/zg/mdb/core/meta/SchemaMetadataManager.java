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
    
    public long deleteLink(String linkName, ObjectsLink link) throws MdbException {
	return linksSchema.createCommand(ObjectsLink.class,linkName).delete().where().field("id").eq(link.getId()).execute();
    }
    
    public long deleteLinks(String linkName, String rowId, boolean isFirst) throws MdbException {
	String queryField = null;
	if (isFirst) {
	    queryField = "firstRowId";
	} else {
	    queryField = "secondRowId";
	}
	return linksSchema.createCommand(ObjectsLink.class,linkName).delete().where().field(queryField).eq(rowId).execute();
    }

    public Collection<ObjectsLink> getObjectLinks(LinkModel linkModel, String rowId, boolean reversed)
	    throws MdbException {
	GetCommand<ObjectsLink> getCommand = linksSchema.createCommand(ObjectsLink.class, linkModel.getName()).get();
	String queryField = null;
	boolean first = linkModel.isFirst();
	if (reversed) {
	    first = !first;
	}
	if (first) {
	    queryField = "firstRowId";
	} else {
	    queryField = "secondRowId";
	}

	return getCommand.where().field(queryField).eq(rowId).execute();
    }

}
