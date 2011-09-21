package ro.zg.mdb.core.meta;

public class MdbConfig {
    
    
    private boolean automaticSchemaCreationOn=true;

    /**
     * @return the automaticSchemaCreationOn
     */
    public boolean isAutomaticSchemaCreationOn() {
        return automaticSchemaCreationOn;
    }

    /**
     * @param automaticSchemaCreationOn the automaticSchemaCreationOn to set
     */
    public void setAutomaticSchemaCreationOn(boolean automaticSchemaCreationOn) {
        this.automaticSchemaCreationOn = automaticSchemaCreationOn;
    }
    
}
