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
