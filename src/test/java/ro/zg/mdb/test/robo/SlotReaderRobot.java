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

public class SlotReaderRobot extends Robot {
    private String slotsRead = "";

    public SlotReaderRobot(int position) {
	super(position);
    }
    

    public SlotReaderRobot(int position, boolean moveOn) {
	super(position, moveOn);
    }

    @Override
    public void executeTurn(RobotContext context) {
	Slot slot = context.getSlotManager().readSlot(position);
	if (slot == null) {
	    if (slotsRead.length() > 0) {
//		System.out.println(slotsRead);
	    }
	    slotsRead = "";
	    // switchDirection();
	} else {
	    slotsRead += slot.getValue();
	}
	move();
    }

}
