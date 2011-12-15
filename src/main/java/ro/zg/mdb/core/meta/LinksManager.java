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

import java.util.List;

import ro.zg.mdb.commands.GetCommand;
import ro.zg.mdb.constants.SpecialPaths;
import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.LinkModel;
import ro.zg.mdb.core.meta.data.ObjectsLink;
import ro.zg.mdb.core.meta.data.SchemaConfig;
import ro.zg.mdb.persistence.PersistenceManager;

public class LinksManager extends PersistentDataManager {
    private SchemaManager linksSchema;

    public LinksManager(PersistenceManager persistenceManager) throws MdbException {
	super(persistenceManager);
	linksSchema = new SchemaManager(persistenceManager, new SchemaConfig(SpecialPaths.LINKS));
    }

    public ObjectsLink saveLink(String linkName, ObjectsLink link) throws MdbException {
	return linksSchema.createCommand(ObjectsLink.class, linkName).insert(link).execute();
    }

    public long deleteLink(String linkName, ObjectsLink link) throws MdbException {
	return linksSchema.createCommand(ObjectsLink.class, linkName).delete().where().field("id").eq(link.getId())
		.execute();
    }

    public long deleteLinks(String linkName, String rowId, boolean isFirst) throws MdbException {
	String queryField = null;
	if (isFirst) {
	    queryField = "firstRowId";
	} else {
	    queryField = "secondRowId";
	}
	return linksSchema.createCommand(ObjectsLink.class, linkName).delete().where().field(queryField).eq(rowId)
		.execute();
    }

    public LinksSet getObjectLinks(LinkModel linkModel, String rowId, boolean reversed)
	    throws MdbException {

	
	boolean first = linkModel.isFirst();
	if (reversed) {
	    first = !first;
	}
	String queryField = getQueryField(first);

	if(linkModel.isPolymorphic()) {
	    return getPolymorphicLinks(linkModel, rowId, queryField);
	}
	else {
	    return new SimpleLinksSet(getLinks(linkModel.getName(), rowId, queryField));
	}
	
    }
    
    private String getQueryField(boolean first) {
	String queryField = null;
	if (first) {
	    queryField = "firstRowId";
	} else {
	    queryField = "secondRowId";
	}
	return queryField;
    }
    
    public SimpleLinksSet getLinks(String linkName, String rowId, boolean first) throws MdbException {
	String queryField = getQueryField(first);
	return new SimpleLinksSet(getLinks(linkName, rowId, queryField));
    }

    private LinksSet getPolymorphicLinks(LinkModel linkModel, String rowId, String queryField) throws MdbException {
	PolymorphicLinksSet linksSet = new PolymorphicLinksSet();

	for (Class<?> type : linkModel.getAllowedTypes()) {
	    String fullLinkName = linkModel.getFullName(type);
	    List<ObjectsLink> links = getLinks(fullLinkName, rowId, queryField);
	    if (links.size() > 0) {
		linksSet.addLinks(type, links);
		/* if we've fond a value and the field is not multivalued, return */
		if(!linkModel.isMultivalued()) {
		    return linksSet;
		}
	    }
	}
	return linksSet;
    }

    private List<ObjectsLink> getLinks(String linkName, String rowId, String queryField) throws MdbException {
	GetCommand<ObjectsLink, ObjectsLink> getCommand = linksSchema.createCommand(ObjectsLink.class, linkName).get();
	return getCommand.where().field(queryField).eq(rowId).execute().getValues();
    }
}