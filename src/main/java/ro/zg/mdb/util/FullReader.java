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
package ro.zg.mdb.util;

import java.util.LinkedHashSet;
import java.util.Set;

import ro.zg.util.io.file.LineHandler;

public class FullReader implements LineHandler{
    Set<String> data=new LinkedHashSet<String>();
    
    @Override
    public boolean handle(String line) {
	data.add(line);
	return true;
    }

    /**
     * @return the data
     */
    public Set<String> getData() {
        return data;
    }
    
    
}
