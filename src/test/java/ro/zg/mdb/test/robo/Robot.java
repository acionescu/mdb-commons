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
