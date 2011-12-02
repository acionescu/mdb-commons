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

public class MultivaluedDataModel<M,T> extends DataModel<T>{
    protected Class<? extends M> multivaluedType;
    protected boolean collection;
    protected boolean map;

    public MultivaluedDataModel(Class<T> type, Class<? extends M> multivaluedType) {
	super(type, true);
	this.multivalued=true;
	this.multivaluedType = multivaluedType;
    }

    /**
     * @return the collection
     */
    public boolean isCollection() {
        return collection;
    }

    /**
     * @return the map
     */
    public boolean isMap() {
        return map;
    }

    /**
     * @param collection the collection to set
     */
    public void setCollection(boolean collection) {
        this.collection = collection;
    }

    /**
     * @param map the map to set
     */
    public void setMap(boolean map) {
        this.map = map;
    }
    
    
}
