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
