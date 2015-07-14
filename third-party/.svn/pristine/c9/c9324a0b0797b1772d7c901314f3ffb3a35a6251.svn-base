package com.pb.sawdust.util.concurrent;

import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;
import java.util.concurrent.TimeUnit;

/**
 * This is a countdown latch that uses longs instead of ints.  This is totally cribbed from Java's
 * {@code java.util.concurrent.CountDownLatch}, with just a few changes.
 *
 * @author crf <br/>
 *         Started: Sep 16, 2009 8:29:04 PM
 */
public class LongCountDownLatch {
    private static final class Sync extends AbstractQueuedLongSynchronizer {
        private static final long serialVersionUID = 493156357487209446L;

        Sync(long count) {
            setState(count);
        }

        long getCount() {
            return getState();
        }

        public long tryAcquireShared(long acquires) {
            return getState() == 0? 1 : -1;
        }

        public boolean tryReleaseShared(long releases) {
            // Decrement count; signal when transition to zero
            for (;;) {
                long c = getState();
                if (c == 0)
                    return false;
                long nextc = c-1;
                if (compareAndSetState(c, nextc))
                    return nextc == 0;
            }
        }
    }

    private final Sync sync;

    public LongCountDownLatch(long count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public boolean await(long timeout, TimeUnit unit)
        throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void countDown() {
        sync.releaseShared(1);
    }

    public long getCount() {
        return sync.getCount();
    }

    public String toString() {
        return super.toString() + "[Count = " + sync.getCount() + "]";
    }
}
