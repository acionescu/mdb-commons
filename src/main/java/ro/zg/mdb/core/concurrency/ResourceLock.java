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
