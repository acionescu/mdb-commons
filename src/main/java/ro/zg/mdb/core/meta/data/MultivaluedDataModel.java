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

    /**
     * @return the multivaluedType
     */
    public Class<? extends M> getMultivaluedType() {
        return multivaluedType;
    }
    
    
}
