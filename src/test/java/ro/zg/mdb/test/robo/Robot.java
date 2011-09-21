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
