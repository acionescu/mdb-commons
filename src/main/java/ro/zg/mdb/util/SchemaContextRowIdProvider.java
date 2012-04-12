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
package ro.zg.mdb.util;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.persistence.SchemaContext;
import ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadata;

public class SchemaContextRowIdProvider<T> implements RowIdProvider {
    private SchemaContext schemaContext;
    private PersistentObjectMetadata<T> objectDataModel;

    public SchemaContextRowIdProvider(SchemaContext schemaContext, PersistentObjectMetadata<T> objectDataModel) {
	super();
	this.schemaContext = schemaContext;
	this.objectDataModel = objectDataModel;
    }

    @Override
    public String provideRowId() throws MdbException {
	return "" + schemaContext.getNextValForSequence(objectDataModel.getTypeName());
    }

}
