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

import java.util.Map;

public class MapDataModel<K,T> extends MultivaluedDataModel<Map<K,T>, T>{
    private Class<K> keyType;
    
    public MapDataModel(Class<K> keyType, Class<T> valueType, Class<? extends Map<K, T>> multivaluedType) {
	super(valueType, multivaluedType);
	this.keyType=keyType;
	this.map=true;
    }

    /**
     * @return the keyType
     */
    public Class<K> getKeyType() {
        return keyType;
    }
    
    
}
