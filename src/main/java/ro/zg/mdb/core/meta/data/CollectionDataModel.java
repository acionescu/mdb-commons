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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ro.zg.commons.exceptions.ContextAwareException;
import ro.zg.commons.exceptions.ExceptionContext;
import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.util.data.GenericNameValue;
import ro.zg.util.data.reflection.ReflectionUtility;
@Persistable
public class CollectionDataModel<T> extends MultivaluedDataModel<Collection<T>, T> {
    private boolean set;
    private boolean list;
    private boolean array;

    public CollectionDataModel(Class<T> type, Class<? extends Collection<T>> multivaluedType) {
	super(type, multivaluedType);
	this.collection = true;
	initCollectionType();
    }

    private void initCollectionType() {
	if (multivaluedType.equals(Array.class)) {
	    array = true;
	} else if (checkSuperType(List.class)) {
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

    /**
     * @return the array
     */
    public boolean isArray() {
        return array;
    }

    public Collection<T> fromString(Collection<String> input) throws ContextAwareException{
	try {
	    Collection<T> container = multivaluedType.newInstance();
	    for(String item : input) {
		container.add((T)ReflectionUtility.createObjectByTypeAndValue(getType(), item));
	    }
	    return container;
	    
	} catch (Exception e) {
	    ExceptionContext ec = new ExceptionContext();
	    ec.put(new GenericNameValue("input",input));
	    ec.put(new GenericNameValue("collectionType",multivaluedType));
	    throw new ContextAwareException("CONVERSION_EXCEPTION",e,ec);
	} 
    }
    
    
}
