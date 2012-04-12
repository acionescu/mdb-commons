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
package ro.zg.mdb.core.meta.persistence.data;

import ro.zg.mdb.core.annotations.ObjectId;


public class Sequence {
    @ObjectId
    private String id;
    private String name;
    private long start;
    private long stop;
    private long step;
    private boolean recycle;
    
    private long currentValue=-1;
    
    public Sequence() {
	
    }
    

    public Sequence(String name) {
	this(name,1L,Long.MAX_VALUE,1L,true);
    }


    public Sequence(String name, long start, long stop, long step, boolean recycle) {
	super();
	this.name = name;
	setStart(start);
	this.stop = stop;
	this.step = step;
	this.recycle = recycle;
    }

    public long nextVal() {
	return currentValue++;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @return the stop
     */
    public long getStop() {
        return stop;
    }

    /**
     * @return the step
     */
    public long getStep() {
        return step;
    }

    /**
     * @return the recycle
     */
    public boolean isRecycle() {
        return recycle;
    }

    /**
     * @return the currentValue
     */
    public long getCurrentValue() {
        return currentValue;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param start the start to set
     */
    public void setStart(long start) {
        this.start = start;
        if(currentValue < 0) {
            currentValue=start;
        }
    }

    /**
     * @param stop the stop to set
     */
    public void setStop(long stop) {
        this.stop = stop;
    }

    /**
     * @param step the step to set
     */
    public void setStep(long step) {
        this.step = step;
    }

    /**
     * @param recycle the recycle to set
     */
    public void setRecycle(boolean recycle) {
        this.recycle = recycle;
    }

    /**
     * @param currentValue the currentValue to set
     */
    public void setCurrentValue(long currentValue) {
        this.currentValue = currentValue;
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }


    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    
}
