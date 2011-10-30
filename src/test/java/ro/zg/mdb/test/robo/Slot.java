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
package ro.zg.mdb.test.robo;

import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.annotations.PrimaryKey;

@Persistable
public class Slot {
    @PrimaryKey
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
