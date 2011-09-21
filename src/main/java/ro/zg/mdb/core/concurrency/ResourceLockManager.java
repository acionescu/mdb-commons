package ro.zg.mdb.core.concurrency;

import java.util.Hashtable;
import java.util.Map;

public class ResourceLockManager {
    private ResourceType resourceType;
    private Map<String, Guard> locksGuards = new Hashtable<String, Guard>();
    private Map<String, ResourceLock> resourcesLocks = new Hashtable<String, ResourceLock>();

    
    
    public ResourceLockManager(ResourceType resourceType) {
	super();
	this.resourceType = resourceType;
    }

    public ResourceLock getResourceLock(String resourceId) {
	Guard guard = getGuard(resourceId);
	addGuard(guard);

	ResourceLock lock = resourcesLocks.get(resourceId);
	if (lock == null) {
	    lock = new ResourceLock(resourceId,resourceType);
	    resourcesLocks.put(resourceId, lock);
	}
	lock.addClient();
//	System.out.println("acquired "+lock);
	removeGuard(guard);
	return lock;
    }

    public void releaseResourceLock(ResourceLock lock) {
	String resourceId=lock.getResourceId();
	Guard guard = getGuard(resourceId);
	addGuard(guard);
	lock.removeClient();
	if(lock.getClients() ==0) {
	    resourcesLocks.remove(resourceId);
	}
//	System.out.println("released "+lock);
	removeGuard(guard);
    }

    private synchronized Guard getGuard(String resourceId) {
	Guard guard = locksGuards.get(resourceId);
	if (guard == null) {
	    guard = new Guard(resourceId);
	    locksGuards.put(resourceId, guard);
	}
	return guard;
    }

    private void addGuard(Guard guard) {
	synchronized (guard) {
	    if (guard.isGuarding()) {
		try {
		    guard.addWaiting();
		    guard.wait();
		    guard.removeWaiting();
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    }
	    guard.setGuarding(true);
	}
    }

    private void removeGuard(Guard guard) {
	synchronized (guard) {
	    guard.setGuarding(false);
	    if (guard.getWaiting() > 0) {
		guard.notify();
	    }
	    else {
		locksGuards.remove(guard.getRowId());
	    }
	}
    }
}
