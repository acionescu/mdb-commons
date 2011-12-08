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

public class RowLock {
    private String rowId;
    private ReadWriteLock lock = new ReentrantReadWriteLock(true);
    private int clients;

    public RowLock(String rowId) {
	super();
	this.rowId = rowId;
    }

    public void acquireReadLock() {
	lock.readLock().lock();
    }
    
    public void releaseReadLock() {
	lock.readLock().unlock();
    }
    
    public void acquireWriteLock() {
	lock.writeLock().lock();
    }
    
    public void releaseWriteLock() {
	lock.writeLock().unlock();
    }
    
    public void addClient() {
	clients++;
    }

    public void removeClient() {
	clients--;
    }

    /**
     * @return the rowId
     */
    public String getRowId() {
	return rowId;
    }

    /**
     * @return the clients
     */
    public int getClients() {
	return clients;
    }

}
