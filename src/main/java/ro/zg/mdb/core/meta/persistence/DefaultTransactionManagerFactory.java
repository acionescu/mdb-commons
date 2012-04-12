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
package ro.zg.mdb.core.meta.persistence;

public class DefaultTransactionManagerFactory implements TransactionManagerFactory {
    private SchemaContext schemaContext;
    
    
    
    public DefaultTransactionManagerFactory(SchemaContext schemaContext) {
	super();
	this.schemaContext = schemaContext;
    }



    /* (non-Javadoc)
     * @see ro.zg.mdb.core.meta.TransactionManagerFactory#getTransactionManager()
     */
    @Override
    public TransactionManager getTransactionManager() {
	return new TransactionManager(schemaContext);
    }
}
