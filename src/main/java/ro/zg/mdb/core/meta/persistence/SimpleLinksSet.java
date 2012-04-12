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

import java.util.Collection;

import ro.zg.mdb.core.meta.persistence.data.ObjectsLink;

public class SimpleLinksSet implements LinksSet{
    private Collection<ObjectsLink> links;

    public SimpleLinksSet(Collection<ObjectsLink> links) {
	super();
	this.links = links;
    }

    /**
     * @return the links
     */
    public Collection<ObjectsLink> getLinks() {
        return links;
    }
    
    
}
