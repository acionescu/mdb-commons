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
package ro.zg.mdb.commands;

import ro.zg.mdb.commands.builders.SimpleResultBuilder;
import ro.zg.mdb.core.filter.Filter;
import ro.zg.mdb.core.meta.persistence.TransactionManager;

public class UpdateCommandContext<T> extends FilteredCommandContext<T, Long, Long, SimpleResultBuilder<Long>> {
    private T source;

    public UpdateCommandContext(String objectName, Class<T> type, TransactionManager transactionManager, Filter filter,
	    T source, SimpleResultBuilder<Long> resultBuilder) {
	super(objectName, type, transactionManager, resultBuilder, filter);
	this.source = source;
    }

    /**
     * @return the source
     */
    public T getSource() {
	return source;
    }

}
