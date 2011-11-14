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

public class SlotDestroyerRobot extends Robot{

    public SlotDestroyerRobot(int position) {
	super(position);
    }
    

    public SlotDestroyerRobot(int position, boolean moveOn) {
	super(position, moveOn);
    }

    @Override
    public void executeTurn(RobotContext context) {
	long deleted = context.getSlotManager().deleteSlot(position);
//	System.out.println("deleted: "+deleted+" at "+position);
	move();
    }

}
