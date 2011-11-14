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
package ro.zg.mdb.core.meta;

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
