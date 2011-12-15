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
import java.util.List;
import java.util.Set;

import ro.zg.mdb.core.meta.data.ObjectsLink;
import ro.zg.util.data.ListMap;

public class PolymorphicLinksSet implements LinksSet{
    private ListMap<Class<?>, ObjectsLink> links=new ListMap<Class<?>, ObjectsLink>();
    
    public void addLinks(Class<?> type, List<ObjectsLink> linksList) {
	links.add(type, linksList);
    }
    
    public Collection<ObjectsLink> getLinks(Class<?> type){
	return links.get(type);
    }
    
    public Set<Class<?>> getTypes(){
	return links.keySet();
    }
}
