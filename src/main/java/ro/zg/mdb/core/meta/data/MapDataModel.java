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

import java.util.Map;

import ro.zg.mdb.core.annotations.Persistable;
@Persistable
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
