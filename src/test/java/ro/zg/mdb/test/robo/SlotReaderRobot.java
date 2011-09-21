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
