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

public class SlotCreatorRobot extends Robot{

    public SlotCreatorRobot(int position) {
	super(position);
    }
    

    public SlotCreatorRobot(int position, boolean moveOn) {
	super(position, moveOn);
    }



    @Override
    public void executeTurn(RobotContext context) {
	Slot slot = context.getSlotManager().readSlot(position);
	if(slot ==null){
	    slot = context.getSlotManager().createSlot(new Slot(position,(char)(Math.random()*126)));
//	    System.out.println("created "+slot+" at "+position);
	}
	move();
    }

}
