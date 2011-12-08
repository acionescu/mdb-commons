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
package ro.zg.mdb.core.meta.data;

public class LinkValue {
    private String linkName;
    private ObjectsLink link;
    
    
    public LinkValue(String linkName, ObjectsLink link) {
	super();
	this.linkName = linkName;
	this.link = link;
    }
    /**
     * @return the linkName
     */
    public String getLinkName() {
        return linkName;
    }
    /**
     * @return the link
     */
    public ObjectsLink getLink() {
        return link;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((link == null) ? 0 : link.hashCode());
	result = prime * result + ((linkName == null) ? 0 : linkName.hashCode());
	return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	LinkValue other = (LinkValue) obj;
	if (link == null) {
	    if (other.link != null)
		return false;
	} else if (!link.equals(other.link))
	    return false;
	if (linkName == null) {
	    if (other.linkName != null)
		return false;
	} else if (!linkName.equals(other.linkName))
	    return false;
	return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "LinkValue [linkName=" + linkName + ", link=" + link + "]";
    }
    
    
}
