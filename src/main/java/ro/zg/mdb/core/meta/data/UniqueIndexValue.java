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

import ro.zg.mdb.constants.Constants;

public class UniqueIndexValue {
    private String name;
    private String value;
    private boolean composite=false;
    
    public UniqueIndexValue(String name) {
	super();
	this.name = name;
    }

    public void addValue(String v) {
	if(value==null) {
	    value=v;
	}
	else {
	    value+=Constants.COMPOSITE_INDEX_SEPARATOR+v;
	    composite=true;
	}
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the composite
     */
    public boolean isComposite() {
        return composite;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "UniqueIndexValue [name=" + name + ", value=" + value + ", composite=" + composite + "]";
    }
    
}
