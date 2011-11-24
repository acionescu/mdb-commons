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
package ro.zg.mdb.test.robo;

import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.annotations.Unique;

@Persistable
public class Slot {
    @Unique(id=1)
    private int position;
    private char value; 
    
    
    public Slot() {
	super();
    }

    public Slot(int position) {
	super();
	this.position = position;
    }

    public Slot(int position, char value) {
	super();
	this.position = position;
	this.value = value;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return the value
     */
    public char getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(char value) {
        this.value = value;
    }
    

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Slot [position=" + position + ", value=" + value + "]";
    }
    
}
