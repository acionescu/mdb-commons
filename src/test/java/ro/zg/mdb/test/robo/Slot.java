package ro.zg.mdb.test.robo;

import ro.zg.mdb.core.annotations.Persistable;
import ro.zg.mdb.core.annotations.PrimaryKey;

@Persistable
public class Slot {
    @PrimaryKey
    private int position;
    private char value; 
    
    
    public Slot() {
	super();
    }

    public Slot(int position) {
	super();
	this.position = position;
    }

    public Slot(int position, char value) {
	super();
	this.position = position;
	this.value = value;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return the value
     */
    public char getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(char value) {
        this.value = value;
    }
    

    /**
     * @param position the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Slot [position=" + position + ", value=" + value + "]";
    }
    
}
