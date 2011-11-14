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

public abstract class Robot {
    protected int position;
    private int moveDirection;
    private boolean moveOn = true;

    public Robot(int position) {
	super();
	this.position = position;
	moveDirection = (Math.random() > 0.5f) ? 1 : -1;
    }

    public Robot(int position, boolean moveOn) {
	super();
	this.position = position;
	this.moveOn = moveOn;
    }

    /* The robot should do something with the slot found at his position */
    public abstract void executeTurn(RobotContext context);

    protected void switchDirection() {
	moveDirection *= -1;
    }

    protected void move() {
	if (moveOn) {
	    position += moveDirection;
	}
    }

    /**
     * @return the position
     */
    public int getPosition() {
	return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(int position) {
	this.position = position;
    }

    /**
     * @return the moveOn
     */
    public boolean isMoveOn() {
	return moveOn;
    }

    /**
     * @param moveOn
     *            the moveOn to set
     */
    public void setMoveOn(boolean moveOn) {
	this.moveOn = moveOn;
    }

}
