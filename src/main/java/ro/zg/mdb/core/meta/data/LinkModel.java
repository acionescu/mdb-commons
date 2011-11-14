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

public class LinkModel {
    private String name;
    private boolean first;
    private boolean lazy;
    /* in case of map */
    private String keyName;
    
    public LinkModel(String name, boolean first, boolean lazy) {
	super();
	this.name = name;
	this.first = first;
	this.lazy = lazy;
    }

    
    
    public LinkModel(String name, boolean first, boolean lazy, String keyName) {
	super();
	this.name = name;
	this.first = first;
	this.lazy = lazy;
	this.keyName = keyName;
    }



    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the first
     */
    public boolean isFirst() {
        return first;
    }

    /**
     * @return the lazy
     */
    public boolean isLazy() {
        return lazy;
    }
    
    

    /**
     * @return the keyName
     */
    public String getKeyName() {
        return keyName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (first ? 1231 : 1237);
	result = prime * result + (lazy ? 1231 : 1237);
	result = prime * result + ((name == null) ? 0 : name.hashCode());
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
	LinkModel other = (LinkModel) obj;
	if (first != other.first)
	    return false;
	if (lazy != other.lazy)
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	return true;
    }
    
    
}
