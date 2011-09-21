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
