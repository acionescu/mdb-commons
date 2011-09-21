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
