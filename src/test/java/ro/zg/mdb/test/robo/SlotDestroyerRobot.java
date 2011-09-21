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
