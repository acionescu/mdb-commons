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
package ro.zg.mdb.core.concurrency;

import java.util.HashMap;
import java.util.Map;

public class PersistableObjectLockManager {
    private Map<ResourceType,ResourceLockManager> locksManagers=new HashMap<ResourceType, ResourceLockManager>();
    
    public PersistableObjectLockManager() {
	locksManagers.put(ResourceType.ROW, new ResourceLockManager(ResourceType.ROW));
	locksManagers.put(ResourceType.INDEX, new ResourceLockManager(ResourceType.INDEX));
    }
    
    public ResourceLock getRowLock(String rowId) {
//	System.out.println("get row lock for "+rowId);
	return locksManagers.get(ResourceType.ROW).getResourceLock(rowId);
    }
    
    public void releaseRowLock(ResourceLock rowLock) {
//	System.out.println("release row lock  "+rowLock);
	locksManagers.get(ResourceType.ROW).releaseResourceLock(rowLock);
    }
    
    public ResourceLock getIndexLock(String indexPath) {
//	System.out.println("get index lock from "+this);
	return locksManagers.get(ResourceType.INDEX).getResourceLock(indexPath);
    }
    
    public void releaseIndexLock(ResourceLock indexLock) {
//	System.out.println("release index lock to "+this);
	locksManagers.get(ResourceType.INDEX).releaseResourceLock(indexLock);
    }
}
