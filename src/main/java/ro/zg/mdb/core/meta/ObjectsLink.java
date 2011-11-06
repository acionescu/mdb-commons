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

public class ObjectsLink {
    private String firstRowId;
    private String secondRowId;
    /**
     * @return the firstRowId
     */
    public String getFirstRowId() {
        return firstRowId;
    }
    /**
     * @return the secondRowId
     */
    public String getSecondRowId() {
        return secondRowId;
    }
    /**
     * @param firstRowId the firstRowId to set
     */
    public void setFirstRowId(String firstRowId) {
        this.firstRowId = firstRowId;
    }
    /**
     * @param secondRowId the secondRowId to set
     */
    public void setSecondRowId(String secondRowId) {
        this.secondRowId = secondRowId;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((firstRowId == null) ? 0 : firstRowId.hashCode());
	result = prime * result + ((secondRowId == null) ? 0 : secondRowId.hashCode());
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
	ObjectsLink other = (ObjectsLink) obj;
	if (firstRowId == null) {
	    if (other.firstRowId != null)
		return false;
	} else if (!firstRowId.equals(other.firstRowId))
	    return false;
	if (secondRowId == null) {
	    if (other.secondRowId != null)
		return false;
	} else if (!secondRowId.equals(other.secondRowId))
	    return false;
	return true;
    }
    
    
}
