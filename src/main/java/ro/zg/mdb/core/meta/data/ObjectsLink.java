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
package ro.zg.mdb.core.meta.data;

import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.annotations.PrimaryKey;

@Persistable
public class ObjectsLink {
    @PrimaryKey
    private String firstRowId;
    @PrimaryKey
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
    
    public String getRowId(boolean first) {
	if(first) {
	    return firstRowId;
	}
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
