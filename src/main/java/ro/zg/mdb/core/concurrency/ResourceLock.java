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
package ro.zg.mdb.core.concurrency;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ResourceLock {
    private ResourceType resourceType;
    private String resourceId;
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private int clients;

    public ResourceLock(String resourceId,ResourceType resourceType) {
	super();
	this.resourceId = resourceId;
	this.resourceType=resourceType;
    }

    public void acquireReadLock() {
	lock.readLock().lock();
    }
    
    public void releaseReadLock() {
	lock.readLock().unlock();
    }
    
    public void acquireWriteLock() {
//	System.out.println(Thread.currentThread().getId()+" waiting for write lock on "+resourceType+" "+resourceId);
	lock.writeLock().lock();
//	System.out.println(Thread.currentThread().getId()+" acquired write lock on "+resourceType+" "+resourceId);
    }
    
    public void releaseWriteLock() {
//	System.out.println(Thread.currentThread().getId()+" releasing write lock on "+resourceType+" "+resourceId);
	lock.writeLock().unlock();
    }
    
    public void addClient() {
	clients++;
    }

    public void removeClient() {
	clients--;
    }

    /**
     * @return the resourceId
     */
    public String getResourceId() {
	return resourceId;
    }

    /**
     * @return the clients
     */
    public int getClients() {
	return clients;
    }

    /**
     * @return the resourceType
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "ResourceLock [resourceType=" + resourceType + ", resourceId=" + resourceId + ", clients=" + clients
		+ ", lock=" + lock + "]";
    }

}
