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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import ro.zg.util.data.reflection.ReflectionUtility;

public class CollectionDataModel<T> extends MultivaluedDataModel<Collection<T>, T> {
    private boolean set;
    private boolean list;

    public CollectionDataModel(Class<T> type, Class<? extends Collection<T>> multivaluedType) {
	super(type, multivaluedType);
	this.collection=true;
	initCollectionType();
    }

    private void initCollectionType() {
	if (checkSuperType(List.class)) {
	    list = true;
	} else if (checkSuperType(Set.class)) {
	    set = true;
	} else {
	    throw new IllegalArgumentException("Unknown collection " + multivaluedType.getName());
	}

    }

    private boolean checkSuperType(Class<?> superClass) {
	return ReflectionUtility.checkInstanceOf(multivaluedType, superClass);
    }

    /**
     * @return the set
     */
    public boolean isSet() {
        return set;
    }

    /**
     * @return the list
     */
    public boolean isList() {
        return list;
    }
    
}
