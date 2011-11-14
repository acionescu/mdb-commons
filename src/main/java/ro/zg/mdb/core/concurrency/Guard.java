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
package ro.zg.mdb.core.concurrency;

public class Guard {
    private boolean guarding;
    private int waiting=0;
    private String rowId;
    

    public Guard(String rowId) {
	super();
	this.rowId = rowId;
    }

    /**
     * @return the guarding
     */
    public boolean isGuarding() {
        return guarding;
    }

    /**
     * @param guarding the guarding to set
     */
    public void setGuarding(boolean guarding) {
        this.guarding = guarding;
    }
    
    public void addWaiting() {
	waiting++;
    }
    
    public void removeWaiting() {
	waiting--;
    }

    /**
     * @return the waiting
     */
    public int getWaiting() {
        return waiting;
    }

    /**
     * @return the rowId
     */
    public String getRowId() {
        return rowId;
    }
    
    
}
