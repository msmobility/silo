package com.pb.sawdust.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * The {@code MultiLock} class provides a simple container for bundling multiple locks into a single entity. A multi-lock
 * is constructed by specifying the locks that it is to hold. Whenever one of the {@code Lock} interface methods is called
 * on it, it recursively calls that method on each of its contained locks. So, calling {@code lock()} will lock all of
 * the contained locks, {@code unlock()} will unlock all of the locks, and so forth. Each method is synchronized, and if
 * any problems occur during an operation, the operation is rolled back, so the lock should never have only part of its
 * contained locks locked/unlocked (at least from within this instance - other references to a contained lock may modify
 * its state outside of this class).
 *
 * @author crf <br/>
 *         Started: Jan 30, 2009 2:29:51 PM
 */
public class MultiLock implements Lock {
    private final List<Lock> locks;

    /**
     * Constructor specifying the locks that are to be held by this multi-lock.
     *
     * @param locks
     *        The locks this multi-lock will hold.
     */
    public MultiLock(Collection<? extends Lock> locks) {
        this.locks = new LinkedList<Lock>(locks);
    }

    synchronized public void lock() {
        for (Lock lock : locks)
            lock.lock();
    }

    synchronized public void lockInterruptibly() throws InterruptedException {
        int currentLock = 0;
        try {
            for (Lock lock : locks) {
                lock.lockInterruptibly();
                currentLock++;
            }
        } catch (InterruptedException e) {
            //unlock the locks we just locked
            int currentUnlock = 0;
            Iterator<Lock> it = locks.iterator();
            while (currentUnlock < currentLock) {
                it.next().unlock();
                currentUnlock++;
            }
            throw e;
        }
    }

    synchronized public boolean tryLock() {
        int currentLock = 0;
        boolean success = true;
        for (Lock lock : locks) {
            success = lock.tryLock();
            if (!success) break;
            currentLock++;
        }
        if (!success) {
            //unlock locks
            int currentUnlock = 0;
            Iterator<Lock> it = locks.iterator();
            while (currentUnlock <= currentLock) {
                it.next().unlock();
                currentUnlock++;
            }
        }
        return success;
    }

    synchronized public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        int currentLock = 0;
        boolean success = true;
        try {
            for (Lock lock : locks) {
                success = lock.tryLock(time,unit);
                if (!success) break;
                currentLock++;
            }
            if (!success) {
                //unlock locks
                int currentUnlock = 0;
                Iterator<Lock> it = locks.iterator();
                while (currentUnlock <= currentLock) {
                    it.next().unlock();
                    currentUnlock++;
                }
            }
        } catch (InterruptedException e) {
            //unlock the locks we just locked
            int currentUnlock = 0;
            Iterator<Lock> it = locks.iterator();
            while (currentUnlock < currentLock) {
                it.next().unlock();
                currentUnlock++;
            }
            throw e;
        }
        return success;
    }

    synchronized public void unlock() {
        for (Lock lock : locks)
            lock.unlock();
    }

    /**
     * Unsupported.
     *
     * @return nothing - throws exception.
     * @throws UnsupportedOperationException
     */
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
