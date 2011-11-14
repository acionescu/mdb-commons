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

import ro.zg.util.io.file.FileHandler;

public abstract class MdbObjectSetHandler extends MdbObjectHandler implements FileHandler{
    protected long rowCount;

    /**
     * @return the rowCount
     */
    public long getRowCount() {
        return rowCount;
    }
    
    
}
