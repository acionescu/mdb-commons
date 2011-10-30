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
package ro.zg.mdb.core.meta;

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
