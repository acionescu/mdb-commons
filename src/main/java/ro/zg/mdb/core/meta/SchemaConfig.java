package ro.zg.mdb.core.meta;

public class SchemaConfig {
    private boolean automaticObjectModelCreationOn=true;

    /**
     * @return the automaticObjectModelCreationOn
     */
    public boolean isAutomaticObjectModelCreationOn() {
        return automaticObjectModelCreationOn;
    }

    /**
     * @param automaticObjectModelCreationOn the automaticObjectModelCreationOn to set
     */
    public void setAutomaticObjectModelCreationOn(boolean automaticObjectModelCreationOn) {
        this.automaticObjectModelCreationOn = automaticObjectModelCreationOn;
    }
    
}
