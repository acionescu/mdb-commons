/*******************************************************************************
 * Copyright 2012 AdrianIonescu
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
package ro.zg.mdb.core.meta.persistence.data;

import java.util.Collection;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.metadata.commons.FieldMetadata;
import ro.zg.metadata.commons.Metadata;

public interface PersistentFieldMetadata<T> extends  FieldMetadata<T, Metadata<T>> {
    
    boolean isIndexed();

    LinkMetadata getLinkMetadata();

    String getSequenceId();

    String getUniqueIndexId();
    
    Object createFromValue(Collection<?> values) throws MdbException;
    
    boolean isObjectId();

}
