/*******************************************************************************
 * Copyright 2011 Adrian Cristian Ionescu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ro.zg.mdb.core.meta;

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
