package ro.zg.mdb.test.robo;

public class RobotContext {
    private SlotManager slotManager;
    
    

    public RobotContext(SlotManager slotManager) {
	super();
	this.slotManager = slotManager;
    }



    /**
     * @return the slotManager
     */
    public SlotManager getSlotManager() {
        return slotManager;
    }
    
    
    
}
