package ro.zg.mdb.core.meta;


public class Sequence {
    private String id;
    private long start;
    private long stop;
    private long step;
    private boolean recycle;
    
    private long currentValue=-1;
    
    public Sequence() {
	
    }
    

    public Sequence(String id) {
	this(id,1L,Long.MAX_VALUE,1L,true);
    }


    public Sequence(String id, long start, long stop, long step, boolean recycle) {
	super();
	this.id = id;
	setStart(start);
	this.stop = stop;
	this.step = step;
	this.recycle = recycle;
    }

    public long nextVal() {
	return currentValue++;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the start
     */
    public long getStart() {
        return start;
    }

    /**
     * @return the stop
     */
    public long getStop() {
        return stop;
    }

    /**
     * @return the step
     */
    public long getStep() {
        return step;
    }

    /**
     * @return the recycle
     */
    public boolean isRecycle() {
        return recycle;
    }

    /**
     * @return the currentValue
     */
    public long getCurrentValue() {
        return currentValue;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param start the start to set
     */
    public void setStart(long start) {
        this.start = start;
        if(currentValue < 0) {
            currentValue=start;
        }
    }

    /**
     * @param stop the stop to set
     */
    public void setStop(long stop) {
        this.stop = stop;
    }

    /**
     * @param step the step to set
     */
    public void setStep(long step) {
        this.step = step;
    }

    /**
     * @param recycle the recycle to set
     */
    public void setRecycle(boolean recycle) {
        this.recycle = recycle;
    }

    /**
     * @param currentValue the currentValue to set
     */
    public void setCurrentValue(long currentValue) {
        this.currentValue = currentValue;
    }
    
    
}
