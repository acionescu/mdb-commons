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

public class SlotUpdaterRobot extends Robot{

    public SlotUpdaterRobot(int position) {
	super(position);
    }

    public SlotUpdaterRobot(int position, boolean moveOn) {
	super(position, moveOn);
    }


    @Override
    public void executeTurn(RobotContext context) {
	Slot slot = context.getSlotManager().readSlot(position);
	if(slot !=null) {
	    slot.setValue((char)(Math.random()*126));
//	    System.out.println("updating "+slot);
	    long updated = context.getSlotManager().updateSlot(slot);
//	    System.out.println("updated "+updated+" at "+position+" to "+slot);
	}
	move();
    }

}
