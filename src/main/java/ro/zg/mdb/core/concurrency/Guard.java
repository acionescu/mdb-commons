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
