package com.pb.sawdust.util.concurrent;

import com.pb.sawdust.util.exceptions.RuntimeInterruptedException;
import net.jcip.annotations.ThreadSafe;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The {@code Regulator} class is used to restrict access to a fixed, quantifiable resource.  A regulator is instantiated
 * with some ({@code long}) value for its capacity, and then those entities wishing to use the resource can request
 * a certain amount of that resource to use.  As long as the resource amount requested does not exceed the total capacity
 * of the regulator (in which case an exception is thrown), a {@code Semaphore} representing that resource amount is
 * returned to the requestor. This semaphore will block (via its {@code acquire} methods) until the resource has space
 * for the request.  An "acquired" semaphore holds its space on the resource until its {@code release()} method is called.
 * <p>
 * A regulator can be set to "fair" or "unfair" mode; its default is "unfair."  Fair mode will treat each resource request
 * as a FIFO queue, and will always wait until the first (head) request is granted before any others. In unfair mode, though
 * the head request gets priority, if there is not space for it, but there is for a later request, the later request will
 * be granted ahead of it.  Depending on the situation, this can prevent (even perpetually) larger requests from being
 * granted, so care must be taken when working in unfair mode.
 * <p>
 * A regulator does not check to verify that a request {@code Semaphore} has been acquired, so once it has made resource
 * space available to the requestor, it does not free that space until the semaphore has been released. Thus, as with other
 * tasks which acquire resources (such as database connections), it is important that the end user use appropriate code
 * to ensure that resources are not lost because they are never released. As an example, a standard idiom for a regulator
 * request would like the following:
 * <p>
 * <pre><code>
 *     Regulator r;
 *     ...
 *     Semaphore request;
 *     try {
 *         request = r.request(100);
 *         request.acquireUninterruptibly();
 *         ...
 *     } finally {
 *         request.release();
 *     }
 * </code></pre>
 *
 * @author crf <br/>
 *         Started Nov 9, 2010 7:52:14 PM
 */
@ThreadSafe
public class Regulator {
    private final long capacity;
    private final boolean fair;
    private final AtomicLong availableQuantity;
    private final LinkedList<RegulatedSemaphore> semaphoreList;

    /**
     * Constructor specifying the capacity of the regulator, and its fairness policy.
     *
     * @param capacity
     *        The capacity of the regulator.
     *
     * @param fair
     *        If {@code true}, a "fair" policy for requests will be used by this regulator, otherwise an "unfair" policy
     *        will be used.
     */
    public Regulator(long capacity, boolean fair) {
        this.capacity = capacity;
        this.fair = fair;
        availableQuantity = new AtomicLong(capacity);
        semaphoreList = new LinkedList<RegulatedSemaphore>();
    }

    /**
     * Convenience constructor for a regulator with an unfair request policy,specifying the its capacity.
     *
     * @param capacity
     *        The capacity of the regulator.
     */
    public Regulator(long capacity) {
        this(capacity,false);
    }

    private void regulate() {
        synchronized (semaphoreList) {
            if (fair)
                regulateFair();
            else
                regulateUnfair();
        }
    }

    private void regulateFair() {
        while (!semaphoreList.isEmpty()) {
            if (regulateSemaphore(semaphoreList.getFirst()))
                semaphoreList.remove(0);
            else
                return; //head element can't be freed, so wait till we have another chance
        }
    }

    private void regulateUnfair() {
        Iterator<RegulatedSemaphore> it = semaphoreList.iterator();
        while (it.hasNext())
            if (regulateSemaphore(it.next()))
                it.remove();
    }

    private boolean regulateSemaphore(RegulatedSemaphore semaphore) {
        boolean remove = checkQuantity(semaphore.quantity) == Availability.IMMEDIATE;
        if (remove) {
            availableQuantity.addAndGet(-1*semaphore.quantity);
            semaphore.internalRelease();
        }
        return remove;
    }

    /**
     * Get the capacity of this regulator.
     *
     * @return this regulator's capacity.
     */
    public long getCapacity() {
        return capacity;
    }

    /**
     * Get the amount of available resource on this regulator.
     *
     * @return the amount of free resource available from this regulator.
     */
    public long getCurrentAvailability() {
        return availableQuantity.get();
    }

    /**
     * Determine the availability of a given resource request on this regulator.
     *
     * @param quantity
     *        The amount of resource requested.
     *
     * @return the avaialability of the resource request.
     *
     * @throws IllegalArgumentException if {@code quantity} is negative.
     */
    public Availability checkQuantity(long quantity) {
        if (quantity < 0)
            throw new IllegalArgumentException("Regulator request must be strictly positive: " + quantity);
        if (quantity > capacity)
            return Availability.IMPOSSIBLE;
        else if (quantity > availableQuantity.get())
            return Availability.DELAYED;
        else
            return Availability.IMMEDIATE;
    }

    /**
     * Request a quantity of resource from this regulator. The resource space is available and held by this request from
     * the time when the returned semaphore is acquired until it is released.
     *
     * @param quantity
     *        The amount of resource requested.
     *
     * @return a semaphore representing the resource request.
     *
     * @throws IllegalArgumentException if {@code quantity} is negative, or if {@code quantity} exceeds this regulator's
     *                                  capacity.
     */
    public Semaphore request(long quantity) {
        if (checkQuantity(quantity) == Availability.IMPOSSIBLE)
            throw new IllegalArgumentException(String.format("Quantity too large %d to be handled by regulator of max size %d",quantity, capacity));
        RegulatedSemaphore request = new RegulatedSemaphore(quantity);
        synchronized (semaphoreList) {
            semaphoreList.add(request);
        }
        regulate();
        return request;
    }

    private void returnQuantity(long quantity)  {
        availableQuantity.addAndGet(quantity);
        regulate();
    }

    private class RegulatedSemaphore extends Semaphore {
        private static final long serialVersionUID = -2965621013925658826L;

        private final long quantity;
        private final AtomicBoolean released;

        private RegulatedSemaphore(long quantity) {
            super(1);
            this.quantity = quantity;
            released = new AtomicBoolean(false);
            try {
                acquire(); //grab the only lock
            } catch (InterruptedException e) {
                throw new RuntimeInterruptedException(e);
            }
        }

        private void setReleased() {
            if (!released.getAndSet(true))
                returnQuantity(quantity);
        }

        private void internalRelease() {
            super.release();
        }

        public void release() {
            setReleased();
            super.release();
        }

        public void release(int permits) {
            setReleased();
            super.release();
        }
    }

    /**
     * The {@code Availability} enum represents the three availability states for a given resource request on a {@code Regulator}.
     */
    public static enum Availability {
        /**
         * Indicates that a request is immediately available. That is, that there is enough space in the regulator to
         * accomodate the request.
         */
        IMMEDIATE,
        /**
         * Indicates that a request can be accomodated, but not until some of the used resource space has been released
         * by other requestors.
         */
        DELAYED,
        /**
         * Indicates that a request is impossible because it exceeds the capacity of the regulator.
         */
        IMPOSSIBLE
    }

}
