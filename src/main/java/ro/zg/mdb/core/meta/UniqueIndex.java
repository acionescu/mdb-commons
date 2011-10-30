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
import java.util.LinkedHashMap;
import java.util.Map;

public class UniqueIndex {

    private String id;
    private Map<String,FieldDataModel> fields = new LinkedHashMap<String,FieldDataModel>();

    public UniqueIndex() {
	super();
    }

    /**
     * @return the id
     */
    public String getId() {
	return id;
    }

    /**
     * @return the fields
     */
    public Collection<FieldDataModel> getFields() {
	return fields.values();
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
	this.id = id;
    }


    public void addField(FieldDataModel field) {
	String fieldName=field.getName();
	if (!fields.containsKey(fieldName)) {
	    fields.put(fieldName,field);
	}
    }
    
    public boolean containsField(String fieldName) {
	return fields.containsKey(fieldName);
    }
    
    public boolean isComposite() {
	return fields.size() > 1;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((fields == null) ? 0 : fields.hashCode());
	result = prime * result + ((id == null) ? 0 : id.hashCode());
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
	UniqueIndex other = (UniqueIndex) obj;
	if (fields == null) {
	    if (other.fields != null)
		return false;
	} else if (!fields.equals(other.fields))
	    return false;
	if (id == null) {
	    if (other.id != null)
		return false;
	} else if (!id.equals(other.id))
	    return false;
	return true;
    }

}
