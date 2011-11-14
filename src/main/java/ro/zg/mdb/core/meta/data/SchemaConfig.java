/*******************************************************************************
 * Copyright (c) 2011 Adrian Cristian Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Adrian Cristian Ionescu - initial API and implementation
 ******************************************************************************/
package ro.zg.mdb.core.meta.data;

public class SchemaConfig {
    private String name;
    private boolean automaticObjectModelCreationOn=true;
    private boolean metadataAllowed=false;
    
    
    public SchemaConfig() {
	super();
    }


    public SchemaConfig(String name) {
	super();
	this.name = name;
    }
    

    public SchemaConfig(String name, boolean metadataAllowed) {
	super();
	this.name = name;
	this.metadataAllowed = metadataAllowed;
    }





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

    /**
     * @return the metadataAllowed
     */
    public boolean isMetadataAllowed() {
        return metadataAllowed;
    }

    /**
     * @param metadataAllowed the metadataAllowed to set
     */
    public void setMetadataAllowed(boolean metadataAllowed) {
        this.metadataAllowed = metadataAllowed;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    
}
