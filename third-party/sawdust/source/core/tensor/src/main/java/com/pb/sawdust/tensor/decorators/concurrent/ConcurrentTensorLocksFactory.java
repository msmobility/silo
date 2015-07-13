package com.pb.sawdust.tensor.decorators.concurrent;

import com.pb.sawdust.util.MultiLock;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.tensor.decorators.concurrent.size.*;
import com.pb.sawdust.util.abacus.LockableAbacus;
import com.pb.sawdust.util.array.ArrayUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.LinkedList;
import java.util.List;

/**              
 * The {@code ConcurrentTensorLocksFactory} class provides locks which can be used with interfaces in the {@code com.pb.sawdust.tensor.decorators.concurrent}
 * packages. The concurrency strategy for these locks uses {@code java.util.concurrent.locks.ReadWriteLock}s to allow simultaneous read access but 
 * one-thread-at-a-time write access. Each write operation is intended to be atomic; for example, writing a row will lock (at least) the 
 * row until the write is finished. The courseness of the locks is set by a concurrency level parameter, which indicates how many
 * dimensions are locked against. Specifically, for every set of dimensions which are locked against, a lock is created for every point in
 * the dimension set. So, if the concurrency level is set to {@code 0}, one lock (locking the whole tensor} is created, and if the
 * concurrency level is set to the number of dimensions in the tensor, then one lock for each element is created. For other concurrency
 * levels, the dimension locks are created "from the left." For example, a {@code 3x4x5} tensor with a concurrency level of two, will
 * have a lock for each point in the first two dimensions, giving 12 locks. There is no way to lock against specific dimensions - only the
 * number of ("leftmost") dimensions can be specified.
 * <p>
 * The concurrency level can have an significant impact on performance. For example, to read/write to the whole tensor, every lock in the 
 * tensor must be obtained - the higher the concurrency level, the larger the number of locks and the greater the locking overhead for 
 * the operations. As another example, if a row is read/written to and its dimension is also one of those locked on, then every lock in 
 * that dimension (including all of the locks in the dimensions under it) must be obtained. On the other hand, setting a concurrency
 * level of 0 will cause the whole tensor to be locked whenever an element is written to, which can lower throughput if a large number
 * of writes are required. The specific application (along with profiling) will determine which concurrency level is appropriate.
 * 
 * @author crf <br/>
 *         Started: Feb 1, 2009 11:46:26 AM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public class ConcurrentTensorLocksFactory {
    private ConcurrentTensorLocksFactory() {}

    /**
     * Get locks for use with tensors with zero dimensions (scalars).
     *
     * @return locks with {@code concurrencyLevel} for use with zero-dimensional tensors.
     */
    public static ConcurrentD0TensorShell.ConcurrentD0TensorLocks getD0Locks() {
        return new D0Lock();
    }

    /**
     * Get locks for use with tensors with one dimension.
     *  
     * @param dimensions
     *        The dimensions of the tensor to use the locks with.
     * 
     * @param concurrencyLevel
     *        The concurrency level of the locks.
     * 
     * @return locks with {@code concurrencyLevel} for use with one-dimensional tensors.
     * 
     * @throws IllegalArgumentException if {@code dimensions.length != 1}, or if {@code concurrencyLevel} is less than
     *                                  zero or greater than 1.
     */
    public static ConcurrentD1TensorShell.ConcurrentD1TensorLocks getD1Locks(int[] dimensions, int concurrencyLevel) {
        checkLockParameters(dimensions.length,concurrencyLevel,1);
        switch (concurrencyLevel) {
            case 0 : return new D0Lock();
            case 1 : return new D1Lock(dimensions);
            default : throw new IllegalStateException("Should not be able to request a concurrency level (" + concurrencyLevel + ") beyond tensor dimensions (" + dimensions.length + ")"); 
        }
    }

    /**
     * Get locks for use with tensors with two dimensions.
     *  
     * @param dimensions
     *        The dimensions of the tensor to use the locks with.
     * 
     * @param concurrencyLevel
     *        The concurrency level of the locks.
     * 
     * @return locks with {@code concurrencyLevel} for use with two-dimensional tensors.
     * 
     * @throws IllegalArgumentException if {@code dimensions.length != 2}, or if {@code concurrencyLevel} is less than
     *                                  zero or greater than 2.
     */
    public static ConcurrentD2TensorShell.ConcurrentD2TensorLocks getD2Locks(int[] dimensions, int concurrencyLevel) {  
        checkLockParameters(dimensions.length,concurrencyLevel,2);
        switch (concurrencyLevel) {
            case 0 : return new D0Lock();
            case 1 : return new D1Lock(dimensions);
            case 2 : return new D2Lock(dimensions);
            default : throw new IllegalStateException("Should not be able to request a concurrency level (" + concurrencyLevel + ") beyond tensor dimensions (" + dimensions.length + ")"); 
        }
    }

    /**
     * Get locks for use with tensors with three dimensions.
     *  
     * @param dimensions
     *        The dimensions of the tensor to use the locks with.
     * 
     * @param concurrencyLevel
     *        The concurrency level of the locks.
     * 
     * @return locks with {@code concurrencyLevel} for use with three-dimensional tensors.
     * 
     * @throws IllegalArgumentException if {@code dimensions.length != 3}, or if {@code concurrencyLevel} is less than
     *                                  zero or greater than 3.
     */
    public static ConcurrentD3TensorShell.ConcurrentD3TensorLocks getD3Locks(int[] dimensions, int concurrencyLevel) { 
        checkLockParameters(dimensions.length,concurrencyLevel,3);
        switch (concurrencyLevel) {
            case 0 : return new D0Lock();
            case 1 : return new D1Lock(dimensions);
            case 2 : return new D2Lock(dimensions);
            case 3 : return new D3Lock(dimensions);
            default : throw new IllegalStateException("Should not be able to request a concurrency level (" + concurrencyLevel + ") beyond tensor dimensions (" + dimensions.length + ")"); 
        }
    }

    /**
     * Get locks for use with tensors with four dimensions.
     *  
     * @param dimensions
     *        The dimensions of the tensor to use the locks with.
     * 
     * @param concurrencyLevel
     *        The concurrency level of the locks.
     * 
     * @return locks with {@code concurrencyLevel} for use with four-dimensional tensors.
     * 
     * @throws IllegalArgumentException if {@code dimensions.length != 4}, or if {@code concurrencyLevel} is less than
     *                                  zero or greater than 4.
     */
    public static ConcurrentD4TensorShell.ConcurrentD4TensorLocks getD4Locks(int[] dimensions, int concurrencyLevel) { 
        checkLockParameters(dimensions.length,concurrencyLevel,4);
        switch (concurrencyLevel) {
            case 0 : return new D0Lock();
            case 1 : return new D1Lock(dimensions);
            case 2 : return new D2Lock(dimensions);
            case 3 : return new D3Lock(dimensions);
            case 4 : return new D4Lock(dimensions);
            default : throw new IllegalStateException("Should not be able to request a concurrency level (" + concurrencyLevel + ") beyond tensor dimensions (" + dimensions.length + ")"); 
        }
    }

    /**
     * Get locks for use with tensors with five dimensions.
     *  
     * @param dimensions
     *        The dimensions of the tensor to use the locks with.
     * 
     * @param concurrencyLevel
     *        The concurrency level of the locks.
     * 
     * @return locks with {@code concurrencyLevel} for use with five-dimensional tensors.
     * 
     * @throws IllegalArgumentException if {@code dimensions.length != 5}, or if {@code concurrencyLevel} is less than
     *                                  zero or greater than 5.
     */
    public static ConcurrentD5TensorShell.ConcurrentD5TensorLocks getD5Locks(int[] dimensions, int concurrencyLevel) {
        checkLockParameters(dimensions.length,concurrencyLevel,5);
        switch (concurrencyLevel) {
            case 0 : return new D0Lock();
            case 1 : return new D1Lock(dimensions);
            case 2 : return new D2Lock(dimensions);
            case 3 : return new D3Lock(dimensions);
            case 4 : return new D4Lock(dimensions);
            case 5 : return new D5Lock(dimensions);
            default : throw new IllegalStateException("Should not be able to request a concurrency level (" + concurrencyLevel + ") beyond tensor dimensions (" + dimensions.length + ")"); 
        }
    }

    /**
     * Get locks for use with tensors with six dimensions.
     *  
     * @param dimensions
     *        The dimensions of the tensor to use the locks with.
     * 
     * @param concurrencyLevel
     *        The concurrency level of the locks.
     * 
     * @return locks with {@code concurrencyLevel} for use with six-dimensional tensors.
     * 
     * @throws IllegalArgumentException if {@code dimensions.length != 6}, or if {@code concurrencyLevel} is less than
     *                                  zero or greater than 6.
     */
    public static ConcurrentD6TensorShell.ConcurrentD6TensorLocks getD6Locks(int[] dimensions, int concurrencyLevel) { 
        checkLockParameters(dimensions.length,concurrencyLevel,6);
        switch (concurrencyLevel) {
            case 0 : return new D0Lock();
            case 1 : return new D1Lock(dimensions);
            case 2 : return new D2Lock(dimensions);
            case 3 : return new D3Lock(dimensions);
            case 4 : return new D4Lock(dimensions);
            case 5 : return new D5Lock(dimensions);
            case 6 : return new D6Lock(dimensions);
            default : throw new IllegalStateException("Should not be able to request a concurrency level (" + concurrencyLevel + ") beyond tensor dimensions (" + dimensions.length + ")"); 
        }
    }

    /**
     * Get locks for use with tensors with seven dimensions.
     *  
     * @param dimensions
     *        The dimensions of the tensor to use the locks with.
     * 
     * @param concurrencyLevel
     *        The concurrency level of the locks.
     * 
     * @return locks with {@code concurrencyLevel} for use with seven-dimensional tensors.
     * 
     * @throws IllegalArgumentException if {@code dimensions.length != 7}, or if {@code concurrencyLevel} is less than
     *                                  zero or greater than 7.
     */
    public static ConcurrentD7TensorShell.ConcurrentD7TensorLocks getD7Locks(int[] dimensions, int concurrencyLevel) { 
        checkLockParameters(dimensions.length,concurrencyLevel,7);
        switch (concurrencyLevel) {
            case 0 : return new D0Lock();
            case 1 : return new D1Lock(dimensions);
            case 2 : return new D2Lock(dimensions);
            case 3 : return new D3Lock(dimensions);
            case 4 : return new D4Lock(dimensions);
            case 5 : return new D5Lock(dimensions);
            case 6 : return new D6Lock(dimensions);
            case 7 : return new D7Lock(dimensions);
            default : throw new IllegalStateException("Should not be able to request a concurrency level (" + concurrencyLevel + ") beyond tensor dimensions (" + dimensions.length + ")"); 
        }
    }

    /**
     * Get locks for use with tensors with eight dimensions.
     *  
     * @param dimensions
     *        The dimensions of the tensor to use the locks with.
     * 
     * @param concurrencyLevel
     *        The concurrency level of the locks.
     * 
     * @return locks with {@code concurrencyLevel} for use with eight-dimensional tensors.
     * 
     * @throws IllegalArgumentException if {@code dimensions.length != 8}, or if {@code concurrencyLevel} is less than
     *                                  zero or greater than 8.
     */
    public static ConcurrentD8TensorShell.ConcurrentD8TensorLocks getD8Locks(int[] dimensions, int concurrencyLevel) { 
        checkLockParameters(dimensions.length,concurrencyLevel,8);
        switch (concurrencyLevel) {
            case 0 : return new D0Lock();
            case 1 : return new D1Lock(dimensions);
            case 2 : return new D2Lock(dimensions);
            case 3 : return new D3Lock(dimensions);
            case 4 : return new D4Lock(dimensions);
            case 5 : return new D5Lock(dimensions);
            case 6 : return new D6Lock(dimensions);
            case 7 : return new D7Lock(dimensions);
            case 8 : return new D8Lock(dimensions);
            default : throw new IllegalStateException("Should not be able to request a concurrency level (" + concurrencyLevel + ") beyond tensor dimensions (" + dimensions.length + ")"); 
        }
    }

    /**
     * Get locks for use with tensors with nine dimensions.
     *  
     * @param dimensions
     *        The dimensions of the tensor to use the locks with.
     * 
     * @param concurrencyLevel
     *        The concurrency level of the locks.
     * 
     * @return locks with {@code concurrencyLevel} for use with nine-dimensional tensors.
     * 
     * @throws IllegalArgumentException if {@code dimensions.length != 9}, or if {@code concurrencyLevel} is less than
     *                                  zero or greater than 9.
     */
    public static ConcurrentD9TensorShell.ConcurrentD9TensorLocks getD9Locks(int[] dimensions, int concurrencyLevel) {
        checkLockParameters(dimensions.length,concurrencyLevel,9);
        switch (concurrencyLevel) {
            case 0 : return new D0Lock();
            case 1 : return new D1Lock(dimensions);
            case 2 : return new D2Lock(dimensions);
            case 3 : return new D3Lock(dimensions);
            case 4 : return new D4Lock(dimensions);
            case 5 : return new D5Lock(dimensions);
            case 6 : return new D6Lock(dimensions);
            case 7 : return new D7Lock(dimensions);
            case 8 : return new D8Lock(dimensions);
            case 9 : return new D9Lock(dimensions);
            default : throw new IllegalStateException("Should not be able to request a concurrency level (" + concurrencyLevel + ") beyond tensor dimensions (" + dimensions.length + ")"); 
        }
    }

    /**
     * Get locks for use with tensors. If the size of the tensor is known, and is less than 10, then one of the 
     * {@code getDnLocks} (where {@code n} is the tensor size) factories should probably be used.
     *  
     * @param dimensions
     *        The dimensions of the tensor to use the locks with.
     * 
     * @param concurrencyLevel
     *        The concurrency level of the locks.
     * 
     * @return locks with {@code concurrencyLevel} for use with two-dimensional tensors.
     * 
     * @throws IllegalArgumentException if {@code concurrencyLevel} is less than zero or greater than {@code dimensions.length}.
     */
    public static ConcurrentTensorLocks getLocks(int[] dimensions, int concurrencyLevel) {
        checkLockParameters(dimensions.length,concurrencyLevel,-1);
        switch (concurrencyLevel) {
            case 0 : return new D0Lock();
            case 1 : return new D1Lock(dimensions);
            case 2 : return new D2Lock(dimensions);
            case 3 : return new D3Lock(dimensions);
            case 4 : return new D4Lock(dimensions);
            case 5 : return new D5Lock(dimensions);
            case 6 : return new D6Lock(dimensions);
            case 7 : return new D7Lock(dimensions);
            case 8 : return new D8Lock(dimensions);
            case 9 : return new D9Lock(dimensions);
            default : return new DNLock(Arrays.copyOf(dimensions,concurrencyLevel));
        }
    }
    
    private static void checkLockParameters(int dimensions, int concurrencyLevel, int expectedSize) {
        if (expectedSize > 0 && dimensions != expectedSize) 
            throw new IllegalArgumentException("D" + concurrencyLevel + "ConcurrencyLocks can only be used with tensors with " + expectedSize + " dimensions; found: " + dimensions + " dimensions.");
        if (concurrencyLevel < 0)
            throw new IllegalArgumentException("Concurrency level must be positive: " + concurrencyLevel);
        if (concurrencyLevel > dimensions)
            throw new IllegalArgumentException("Concurrency level (" + concurrencyLevel + ") cannot exceed number of tensor dimensions (" + dimensions + ")");
    }

    private static class D0Lock implements ConcurrentTensorLocks,
                                           ConcurrentD0TensorShell.ConcurrentD0TensorLocks,
                                           ConcurrentD1TensorShell.ConcurrentD1TensorLocks,
                                           ConcurrentD2TensorShell.ConcurrentD2TensorLocks,
                                           ConcurrentD3TensorShell.ConcurrentD3TensorLocks,
                                           ConcurrentD4TensorShell.ConcurrentD4TensorLocks,
                                           ConcurrentD5TensorShell.ConcurrentD5TensorLocks,
                                           ConcurrentD6TensorShell.ConcurrentD6TensorLocks,
                                           ConcurrentD7TensorShell.ConcurrentD7TensorLocks,
                                           ConcurrentD8TensorShell.ConcurrentD8TensorLocks,
                                           ConcurrentD9TensorShell.ConcurrentD9TensorLocks {
        private final Lock readLock;
        private final Lock writeLock;

        private D0Lock() {
            ReadWriteLock l = new ReentrantReadWriteLock();
            readLock = l.readLock();
            writeLock = l.writeLock();
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return readLock;
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return readLock;
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return readLock;
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return readLock;
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return readLock;
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index) {
            return readLock;
        }

        public Lock getReadLock(int d0index, int d1index, int d2index) {
            return readLock;
        }

        public Lock getReadLock(int d0index, int d1index) {
            return readLock;
        }

        public Lock getReadLock(int d0index) {
            return readLock;
        }
        
        public Lock getReadLock() {
            return readLock;
        }

        public Lock getReadLock(int ... indices) {
            return readLock;
        }


        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return writeLock;
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return writeLock;
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return writeLock;
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return writeLock;
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return writeLock;
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index) {
            return writeLock;
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index) {
            return writeLock;
        }

        public Lock getWriteLock(int d0index, int d1index) {
            return writeLock;
        }

        public Lock getWriteLock(int d0index) {
            return writeLock;
        }
        
        public Lock getWriteLock() {
            return writeLock;
        }

        public Lock getWriteLock(int ... indices) {
            return writeLock;
        }


        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return readLock;
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return readLock;
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return readLock;
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return readLock;
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            return readLock;
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index) {
            return readLock;
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index) {
            return readLock;
        }

        public Lock getRowReadLock(int dimension, int remainingD0index) {
            return readLock;
        }

        public Lock getRowReadLock(int dimension) {
            return readLock;
        }

        public Lock getRowReadLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            return readLock;
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return writeLock;
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return writeLock;
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return writeLock;
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return writeLock;
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            return writeLock;
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index) {
            return writeLock;
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index) {
            return writeLock;
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index) {
            return writeLock;
        }

        public Lock getRowWriteLock(int dimension) {
            return writeLock;
        }

        public Lock getRowWriteLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            return writeLock;
        }

        public Lock getTensorReadLock() {
            return readLock;
        }

        public Lock getTensorWriteLock() {
            return writeLock;
        }
    }
    
    private static class D1Lock implements ConcurrentTensorLocks,
                                           ConcurrentD1TensorShell.ConcurrentD1TensorLocks,
                                           ConcurrentD2TensorShell.ConcurrentD2TensorLocks,
                                           ConcurrentD3TensorShell.ConcurrentD3TensorLocks,
                                           ConcurrentD4TensorShell.ConcurrentD4TensorLocks,
                                           ConcurrentD5TensorShell.ConcurrentD5TensorLocks,
                                           ConcurrentD6TensorShell.ConcurrentD6TensorLocks,
                                           ConcurrentD7TensorShell.ConcurrentD7TensorLocks,
                                           ConcurrentD8TensorShell.ConcurrentD8TensorLocks,
                                           ConcurrentD9TensorShell.ConcurrentD9TensorLocks {
        private final Lock[] readLocks;
        private final Lock[] writeLocks;
        private final Lock fullReadLock;
        private final Lock fullWriteLock;

        private D1Lock(int[] dimensions) {
            int d0Size = dimensions[0];
            readLocks = new Lock[d0Size];
            writeLocks = new Lock[d0Size];
            List<Lock> rLocks = new LinkedList<Lock>();
            List<Lock> wLocks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d0Size)) {
                ReadWriteLock l = new ReentrantReadWriteLock();
                readLocks[i[0]] = l.readLock();
                writeLocks[i[0]] = l.writeLock();
                rLocks.add(l.readLock());
                wLocks.add(l.writeLock());
            }
            fullReadLock = new MultiLock(rLocks);
            fullWriteLock = new MultiLock(wLocks);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getReadLock(d0index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getReadLock(d0index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getReadLock(d0index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return getReadLock(d0index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return getReadLock(d0index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index) {
            return getReadLock(d0index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index) {
            return getReadLock(d0index);
        }

        public Lock getReadLock(int d0index, int d1index) {
            return getReadLock(d0index);
        }

        public Lock getReadLock(int d0index) {
            return readLocks[d0index];
        }

        public Lock getReadLock(int ... indices) {
            if (indices.length < 1)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 1, found " + indices.length);
            return getReadLock(indices[0]);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getWriteLock(d0index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getWriteLock(d0index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getWriteLock(d0index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return getWriteLock(d0index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return getWriteLock(d0index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index) {
            return getWriteLock(d0index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index) {
            return getWriteLock(d0index);
        }

        public Lock getWriteLock(int d0index, int d1index) {
            return getWriteLock(d0index);
        }

        public Lock getWriteLock(int d0index) {
            return writeLocks[d0index];
        }

        public Lock getWriteLock(int ... indices) {
            if (indices.length < 1)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 1, found " + indices.length);
            return getWriteLock(indices[0]);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowReadLock(dimension,remainingD0index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowReadLock(dimension,remainingD0index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowReadLock(dimension,remainingD0index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return getRowReadLock(dimension,remainingD0index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            return getRowReadLock(dimension,remainingD0index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index) {
            return getRowReadLock(dimension,remainingD0index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index) {
            return getRowReadLock(dimension,remainingD0index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index) {
            switch (dimension) {
                case 0 : return fullReadLock;
                default : return readLocks[remainingD0index];
            }
        }

        public Lock getRowReadLock(int dimension) {
            return getRowReadLock(dimension,0);
        }

        public Lock getRowReadLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 1)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 1, found " + remainingIndices.length);
            return getRowReadLock(dimension,remainingIndices[0]);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowWriteLock(dimension,remainingD0index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowWriteLock(dimension,remainingD0index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowWriteLock(dimension,remainingD0index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return getRowWriteLock(dimension,remainingD0index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            return getRowWriteLock(dimension,remainingD0index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index) {
            return getRowWriteLock(dimension,remainingD0index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index) {
            return getRowWriteLock(dimension,remainingD0index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index) {
            switch (dimension) {
                case 0 : return fullWriteLock;
                default : return writeLocks[remainingD0index];
            }
        }

        public Lock getRowWriteLock(int dimension) {
            return getRowWriteLock(dimension,0);
        }

        public Lock getRowWriteLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 1)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 1, found " + remainingIndices.length);
            return getRowWriteLock(dimension,remainingIndices[0]);
        }

        public Lock getTensorReadLock() {
            return fullReadLock;
        }

        public Lock getTensorWriteLock() {
            return fullWriteLock;
        }
    }
    
    private static class D2Lock implements ConcurrentTensorLocks,
                                           ConcurrentD2TensorShell.ConcurrentD2TensorLocks,
                                           ConcurrentD3TensorShell.ConcurrentD3TensorLocks,
                                           ConcurrentD4TensorShell.ConcurrentD4TensorLocks,
                                           ConcurrentD5TensorShell.ConcurrentD5TensorLocks,
                                           ConcurrentD6TensorShell.ConcurrentD6TensorLocks,
                                           ConcurrentD7TensorShell.ConcurrentD7TensorLocks,
                                           ConcurrentD8TensorShell.ConcurrentD8TensorLocks,
                                           ConcurrentD9TensorShell.ConcurrentD9TensorLocks {
        private final int d1Size;
        private final Lock[][] readLocks;
        private final Lock[][] writeLocks;
        private final Lock fullReadLock;
        private final Lock fullWriteLock;

        private D2Lock(int[] dimensions) {
            int d0Size = dimensions[0];
            d1Size = dimensions[1];
            readLocks = new Lock[d0Size][d1Size];
            writeLocks = new Lock[d0Size][d1Size];
            List<Lock> rLocks = new LinkedList<Lock>();
            List<Lock> wLocks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d0Size,d1Size)) {
                ReadWriteLock l = new ReentrantReadWriteLock();
                readLocks[i[0]][i[1]] = l.readLock();
                writeLocks[i[0]][i[1]] = l.writeLock();
                rLocks.add(l.readLock());
                wLocks.add(l.writeLock());
            }
            fullReadLock = new MultiLock(rLocks);
            fullWriteLock = new MultiLock(wLocks);
        }

        private Lock d1ReadLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size))
                locks.add(readLocks[d0index][i[0]]);
            return new MultiLock(locks);
        }

        private Lock d1WriteLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size))
                locks.add(writeLocks[d0index][i[0]]);
            return new MultiLock(locks);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getReadLock(d0index,d1index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getReadLock(d0index,d1index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getReadLock(d0index,d1index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return getReadLock(d0index,d1index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return getReadLock(d0index,d1index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index) {
            return getReadLock(d0index,d1index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index) {
            return getReadLock(d0index,d1index);
        }

        public Lock getReadLock(int d0index, int d1index) {
            return readLocks[d0index][d1index];
        }

        public Lock getReadLock(int ... indices) {
            if (indices.length < 2)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 2, found " + indices.length);
            return getReadLock(indices[0],indices[1]);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getWriteLock(d0index,d1index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getWriteLock(d0index,d1index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getWriteLock(d0index,d1index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return getWriteLock(d0index,d1index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return getWriteLock(d0index,d1index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index) {
            return getWriteLock(d0index,d1index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index) {
            return getWriteLock(d0index,d1index);
        }

        public Lock getWriteLock(int d0index, int d1index) {
            return writeLocks[d0index][d1index];
        }

        public Lock getWriteLock(int ... indices) {
            if (indices.length < 2)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 2, found " + indices.length);
            return getWriteLock(indices[0],indices[1]);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index) {
            switch (dimension) {
                case 0 : return fullReadLock;
                case 1 : return d1ReadLock(remainingD0index);
                default : return readLocks[remainingD0index][remainingD1index];
            }
        }

        public Lock getRowReadLock(int dimension, int remainingD0index) {
            return getRowReadLock(dimension,remainingD0index,0);
        }

        public Lock getRowReadLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 2)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 2, found " + remainingIndices.length);
            return getRowReadLock(dimension,remainingIndices[0],remainingIndices[1]);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index) {
            switch (dimension) {
                case 0 : return fullWriteLock;
                case 1 : return d1WriteLock(remainingD0index);
                default : return writeLocks[remainingD0index][remainingD1index];
            }
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index) {
            return getRowWriteLock(dimension,remainingD0index,0);
        }

        public Lock getRowWriteLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 2)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 2, found " + remainingIndices.length);
            return getRowWriteLock(dimension,remainingIndices[0],remainingIndices[1]);
        }

        public Lock getTensorReadLock() {
            return fullReadLock;
        }

        public Lock getTensorWriteLock() {
            return fullWriteLock;
        }
    }
    
    private static class D3Lock implements ConcurrentTensorLocks,
                                           ConcurrentD3TensorShell.ConcurrentD3TensorLocks,
                                           ConcurrentD4TensorShell.ConcurrentD4TensorLocks,
                                           ConcurrentD5TensorShell.ConcurrentD5TensorLocks,
                                           ConcurrentD6TensorShell.ConcurrentD6TensorLocks,
                                           ConcurrentD7TensorShell.ConcurrentD7TensorLocks,
                                           ConcurrentD8TensorShell.ConcurrentD8TensorLocks,
                                           ConcurrentD9TensorShell.ConcurrentD9TensorLocks {
        private final int d1Size;
        private final int d2Size;
        private final Lock[][][] readLocks;
        private final Lock[][][] writeLocks;
        private final Lock fullReadLock;
        private final Lock fullWriteLock;

        private D3Lock(int[] dimensions) {
            int d0Size = dimensions[0];
            d1Size = dimensions[1];
            d2Size = dimensions[2];
            readLocks = new Lock[d0Size][d1Size][d2Size];
            writeLocks = new Lock[d0Size][d1Size][d2Size];
            List<Lock> rLocks = new LinkedList<Lock>();
            List<Lock> wLocks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d0Size,d1Size,d2Size)) {
                ReadWriteLock l = new ReentrantReadWriteLock();
                readLocks[i[0]][i[1]][i[2]] = l.readLock();
                writeLocks[i[0]][i[1]][i[2]] = l.writeLock();
                rLocks.add(l.readLock());
                wLocks.add(l.writeLock());
            }
            fullReadLock = new MultiLock(rLocks);
            fullWriteLock = new MultiLock(wLocks);
        }

        private Lock d1ReadLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size))
                locks.add(readLocks[d0index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d1WriteLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size))
                locks.add(writeLocks[d0index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d2ReadLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size))
                locks.add(readLocks[d0index][d1index][i[0]]);
            return new MultiLock(locks);
        }

        private Lock d2WriteLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size))
                locks.add(writeLocks[d0index][d1index][i[0]]);
            return new MultiLock(locks);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getReadLock(d0index,d1index,d2index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getReadLock(d0index,d1index,d2index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getReadLock(d0index,d1index,d2index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return getReadLock(d0index,d1index,d2index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return getReadLock(d0index,d1index,d2index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index) {
            return getReadLock(d0index,d1index,d2index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index) {
            return readLocks[d0index][d1index][d2index];
        }

        public Lock getReadLock(int ... indices) {
            if (indices.length < 3)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 3, found " + indices.length);
            return getReadLock(indices[0],indices[1],indices[2]);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getWriteLock(d0index,d1index,d2index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getWriteLock(d0index,d1index,d2index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getWriteLock(d0index,d1index,d2index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return getWriteLock(d0index,d1index,d2index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return getWriteLock(d0index,d1index,d2index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index) {
            return getWriteLock(d0index,d1index,d2index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index) {
            return writeLocks[d0index][d1index][d2index];
        }

        public Lock getWriteLock(int ... indices) {
            if (indices.length < 3)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 3, found " + indices.length);
            return getWriteLock(indices[0],indices[1],indices[2]);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index) {
            switch (dimension) {
                case 0 : return fullReadLock;
                case 1 : return d1ReadLock(remainingD0index);
                case 2 : return d2ReadLock(remainingD0index,remainingD1index);
                default : return readLocks[remainingD0index][remainingD1index][remainingD2index];
            }
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,0);
        }

        public Lock getRowReadLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 3)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 3, found " + remainingIndices.length);
            return getRowReadLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2]);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index) {
            switch (dimension) {
                case 0 : return fullWriteLock;
                case 1 : return d1WriteLock(remainingD0index);
                case 2 : return d2WriteLock(remainingD0index,remainingD1index);
                default : return writeLocks[remainingD0index][remainingD1index][remainingD2index];
            }
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,0);
        }

        public Lock getRowWriteLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 3)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 3, found " + remainingIndices.length);
            return getRowWriteLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2]);
        }

        public Lock getTensorReadLock() {
            return fullReadLock;
        }

        public Lock getTensorWriteLock() {
            return fullWriteLock;
        }
    }
    
    private static class D4Lock implements ConcurrentTensorLocks,
                                           ConcurrentD4TensorShell.ConcurrentD4TensorLocks,
                                           ConcurrentD5TensorShell.ConcurrentD5TensorLocks,
                                           ConcurrentD6TensorShell.ConcurrentD6TensorLocks,
                                           ConcurrentD7TensorShell.ConcurrentD7TensorLocks,
                                           ConcurrentD8TensorShell.ConcurrentD8TensorLocks,
                                           ConcurrentD9TensorShell.ConcurrentD9TensorLocks {
        private final int d1Size;
        private final int d2Size;
        private final int d3Size;
        private final Lock[][][][] readLocks;
        private final Lock[][][][] writeLocks;
        private final Lock fullReadLock;
        private final Lock fullWriteLock;

        private D4Lock(int[] dimensions) {
            int d0Size = dimensions[0];
            d1Size = dimensions[1];
            d2Size = dimensions[2];
            d3Size = dimensions[3];
            readLocks = new Lock[d0Size][d1Size][d2Size][d3Size];
            writeLocks = new Lock[d0Size][d1Size][d2Size][d3Size];
            List<Lock> rLocks = new LinkedList<Lock>();
            List<Lock> wLocks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d0Size,d1Size,d2Size,d3Size)) {
                ReadWriteLock l = new ReentrantReadWriteLock();
                readLocks[i[0]][i[1]][i[2]][i[3]] = l.readLock();
                writeLocks[i[0]][i[1]][i[2]][i[3]] = l.writeLock();
                rLocks.add(l.readLock());
                wLocks.add(l.writeLock());
            }
            fullReadLock = new MultiLock(rLocks);
            fullWriteLock = new MultiLock(wLocks);
        }

        private Lock d1ReadLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size))
                locks.add(readLocks[d0index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d1WriteLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size))
                locks.add(writeLocks[d0index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d2ReadLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size))
                locks.add(readLocks[d0index][d1index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d2WriteLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size))
                locks.add(writeLocks[d0index][d1index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d3ReadLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size))
                locks.add(readLocks[d0index][d1index][d2index][i[0]]);
            return new MultiLock(locks);
        }

        private Lock d3WriteLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size))
                locks.add(writeLocks[d0index][d1index][d2index][i[0]]);
            return new MultiLock(locks);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getReadLock(d0index,d1index,d2index,d3index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getReadLock(d0index,d1index,d2index,d3index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getReadLock(d0index,d1index,d2index,d3index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return getReadLock(d0index,d1index,d2index,d3index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return getReadLock(d0index,d1index,d2index,d3index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index) {
            return readLocks[d0index][d1index][d2index][d3index];
        }

        public Lock getReadLock(int ... indices) {
            if (indices.length < 4)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 4, found " + indices.length);
            return getReadLock(indices[0],indices[1],indices[2],indices[3]);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getWriteLock(d0index,d1index,d2index,d3index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getWriteLock(d0index,d1index,d2index,d3index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getWriteLock(d0index,d1index,d2index,d3index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return getWriteLock(d0index,d1index,d2index,d3index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return getWriteLock(d0index,d1index,d2index,d3index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index) {
            return writeLocks[d0index][d1index][d2index][d3index];
        }

        public Lock getWriteLock(int ... indices) {
            if (indices.length < 4)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 4, found " + indices.length);
            return getWriteLock(indices[0],indices[1],indices[2],indices[3]);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            switch (dimension) {
                case 0 : return fullReadLock;
                case 1 : return d1ReadLock(remainingD0index);
                case 2 : return d2ReadLock(remainingD0index,remainingD1index);
                case 3 : return d3ReadLock(remainingD0index,remainingD1index,remainingD2index);
                default : return readLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index];
            }
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,0);
        }

        public Lock getRowReadLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 4)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 4, found " + remainingIndices.length);
            return getRowReadLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3]);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            switch (dimension) {
                case 0 : return fullWriteLock;
                case 1 : return d1WriteLock(remainingD0index);
                case 2 : return d2WriteLock(remainingD0index,remainingD1index);
                case 3 : return d3WriteLock(remainingD0index,remainingD1index,remainingD2index);
                default : return writeLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index];
            }
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,0);
        }

        public Lock getRowWriteLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 4)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 4, found " + remainingIndices.length);
            return getRowWriteLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3]);
        }

        public Lock getTensorReadLock() {
            return fullReadLock;
        }

        public Lock getTensorWriteLock() {
            return fullWriteLock;
        }
    }
    
    private static class D5Lock implements ConcurrentTensorLocks,
                                           ConcurrentD5TensorShell.ConcurrentD5TensorLocks,
                                           ConcurrentD6TensorShell.ConcurrentD6TensorLocks,
                                           ConcurrentD7TensorShell.ConcurrentD7TensorLocks,
                                           ConcurrentD8TensorShell.ConcurrentD8TensorLocks,
                                           ConcurrentD9TensorShell.ConcurrentD9TensorLocks {
        private final int d1Size;
        private final int d2Size;
        private final int d3Size;
        private final int d4Size;
        private final Lock[][][][][] readLocks;
        private final Lock[][][][][] writeLocks;
        private final Lock fullReadLock;
        private final Lock fullWriteLock;

        private D5Lock(int[] dimensions) {
            int d0Size = dimensions[0];
            d1Size = dimensions[1];
            d2Size = dimensions[2];
            d3Size = dimensions[3];
            d4Size = dimensions[4];
            readLocks = new Lock[d0Size][d1Size][d2Size][d3Size][d4Size];
            writeLocks = new Lock[d0Size][d1Size][d2Size][d3Size][d4Size];
            List<Lock> rLocks = new LinkedList<Lock>();
            List<Lock> wLocks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d0Size,d1Size,d2Size,d3Size,d4Size)) {
                ReadWriteLock l = new ReentrantReadWriteLock();
                readLocks[i[0]][i[1]][i[2]][i[3]][i[4]] = l.readLock();
                writeLocks[i[0]][i[1]][i[2]][i[3]][i[4]] = l.writeLock();
                rLocks.add(l.readLock());
                wLocks.add(l.writeLock());
            }
            fullReadLock = new MultiLock(rLocks);
            fullWriteLock = new MultiLock(wLocks);
        }

        private Lock d1ReadLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size,d4Size))
                locks.add(readLocks[d0index][i[0]][i[1]][i[2]][i[3]]);
            return new MultiLock(locks);
        }

        private Lock d1WriteLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size,d4Size))
                locks.add(writeLocks[d0index][i[0]][i[1]][i[2]][i[3]]);
            return new MultiLock(locks);
        }

        private Lock d2ReadLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size,d4Size))
                locks.add(readLocks[d0index][d1index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d2WriteLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size,d4Size))
                locks.add(writeLocks[d0index][d1index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d3ReadLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size,d4Size))
                locks.add(readLocks[d0index][d1index][d2index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d3WriteLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size,d4Size))
                locks.add(writeLocks[d0index][d1index][d2index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d4ReadLock(int d0index, int d1index, int d2index, int d3index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d4Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][i[0]]);
            return new MultiLock(locks);
        }

        private Lock d4WriteLock(int d0index, int d1index, int d2index, int d3index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d4Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][i[0]]);
            return new MultiLock(locks);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getReadLock(d0index,d1index,d2index,d3index,d4index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getReadLock(d0index,d1index,d2index,d3index,d4index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getReadLock(d0index,d1index,d2index,d3index,d4index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return getReadLock(d0index,d1index,d2index,d3index,d4index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return readLocks[d0index][d1index][d2index][d3index][d4index];
        }

        public Lock getReadLock(int ... indices) {
            if (indices.length < 5)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 5, found " + indices.length);
            return getReadLock(indices[0],indices[1],indices[2],indices[3],indices[4]);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getWriteLock(d0index,d1index,d2index,d3index,d4index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getWriteLock(d0index,d1index,d2index,d3index,d4index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getWriteLock(d0index,d1index,d2index,d3index,d4index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return getWriteLock(d0index,d1index,d2index,d3index,d4index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return writeLocks[d0index][d1index][d2index][d3index][d4index];
        }

        public Lock getWriteLock(int ... indices) {
            if (indices.length < 5)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 5, found " + indices.length);
            return getWriteLock(indices[0],indices[1],indices[2],indices[3],indices[4]);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            switch (dimension) {
                case 0 : return fullReadLock;
                case 1 : return d1ReadLock(remainingD0index);
                case 2 : return d2ReadLock(remainingD0index,remainingD1index);
                case 3 : return d3ReadLock(remainingD0index,remainingD1index,remainingD2index);
                case 4 : return d4ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index);
                default : return readLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index][remainingD4index];
            }
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,0);
        }

        public Lock getRowReadLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 5)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 5, found " + remainingIndices.length);
            return getRowReadLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3],remainingIndices[4]);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            switch (dimension) {
                case 0 : return fullWriteLock;
                case 1 : return d1WriteLock(remainingD0index);
                case 2 : return d2WriteLock(remainingD0index,remainingD1index);
                case 3 : return d3WriteLock(remainingD0index,remainingD1index,remainingD2index);
                case 4 : return d4WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index);
                default : return writeLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index][remainingD4index];
            }
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,0);
        }

        public Lock getRowWriteLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 5)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 5, found " + remainingIndices.length);
            return getRowWriteLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3],remainingIndices[4]);
        }

        public Lock getTensorReadLock() {
            return fullReadLock;
        }

        public Lock getTensorWriteLock() {
            return fullWriteLock;
        }
    }
    
    private static class D6Lock implements ConcurrentTensorLocks,
                                           ConcurrentD6TensorShell.ConcurrentD6TensorLocks,
                                           ConcurrentD7TensorShell.ConcurrentD7TensorLocks,
                                           ConcurrentD8TensorShell.ConcurrentD8TensorLocks,
                                           ConcurrentD9TensorShell.ConcurrentD9TensorLocks {
        private final int d1Size;
        private final int d2Size;
        private final int d3Size;
        private final int d4Size;
        private final int d5Size;
        private final Lock[][][][][][] readLocks;
        private final Lock[][][][][][] writeLocks;
        private final Lock fullReadLock;
        private final Lock fullWriteLock;

        private D6Lock(int[] dimensions) {
            int d0Size = dimensions[0];
            d1Size = dimensions[1];
            d2Size = dimensions[2];
            d3Size = dimensions[3];
            d4Size = dimensions[4];
            d5Size = dimensions[5];
            readLocks = new Lock[d0Size][d1Size][d2Size][d3Size][d4Size][d5Size];
            writeLocks = new Lock[d0Size][d1Size][d2Size][d3Size][d4Size][d5Size];
            List<Lock> rLocks = new LinkedList<Lock>();
            List<Lock> wLocks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d0Size,d1Size,d2Size,d3Size,d4Size,d5Size)) {
                ReadWriteLock l = new ReentrantReadWriteLock();
                readLocks[i[0]][i[1]][i[2]][i[3]][i[4]][i[5]] = l.readLock();
                writeLocks[i[0]][i[1]][i[2]][i[3]][i[4]][i[5]] = l.writeLock();
                rLocks.add(l.readLock());
                wLocks.add(l.writeLock());
            }
            fullReadLock = new MultiLock(rLocks);
            fullWriteLock = new MultiLock(wLocks);
        }

        private Lock d1ReadLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size,d4Size,d5Size))
                locks.add(readLocks[d0index][i[0]][i[1]][i[2]][i[3]][i[4]]);
            return new MultiLock(locks);
        }

        private Lock d1WriteLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size,d4Size,d5Size))
                locks.add(writeLocks[d0index][i[0]][i[1]][i[2]][i[3]][i[4]]);
            return new MultiLock(locks);
        }

        private Lock d2ReadLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size,d4Size,d5Size))
                locks.add(readLocks[d0index][d1index][i[0]][i[1]][i[2]][i[3]]);
            return new MultiLock(locks);
        }

        private Lock d2WriteLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size,d4Size,d5Size))
                locks.add(writeLocks[d0index][d1index][i[0]][i[1]][i[2]][i[3]]);
            return new MultiLock(locks);
        }

        private Lock d3ReadLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size,d4Size,d5Size))
                locks.add(readLocks[d0index][d1index][d2index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d3WriteLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size,d4Size,d5Size))
                locks.add(writeLocks[d0index][d1index][d2index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d4ReadLock(int d0index, int d1index, int d2index, int d3index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d4Size,d5Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d4WriteLock(int d0index, int d1index, int d2index, int d3index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d4Size,d5Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d5ReadLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d5Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][d4index][i[0]]);
            return new MultiLock(locks);
        }

        private Lock d5WriteLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d5Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][d4index][i[0]]);
            return new MultiLock(locks);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getReadLock(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getReadLock(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getReadLock(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return readLocks[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public Lock getReadLock(int ... indices) {
            if (indices.length < 6)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 6, found " + indices.length);
            return getReadLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getWriteLock(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getWriteLock(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return getWriteLock(d0index,d1index,d2index,d3index,d4index,d5index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return writeLocks[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public Lock getWriteLock(int ... indices) {
            if (indices.length < 6)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 6, found " + indices.length);
            return getWriteLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            switch (dimension) {
                case 0 : return fullReadLock;
                case 1 : return d1ReadLock(remainingD0index);
                case 2 : return d2ReadLock(remainingD0index,remainingD1index);
                case 3 : return d3ReadLock(remainingD0index,remainingD1index,remainingD2index);
                case 4 : return d4ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index);
                case 5 : return d5ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
                default : return readLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index][remainingD4index][remainingD5index];
            }
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,0);
        }

        public Lock getRowReadLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 6)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 6, found " + remainingIndices.length);
            return getRowReadLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3],remainingIndices[4],remainingIndices[5]);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            switch (dimension) {
                case 0 : return fullWriteLock;
                case 1 : return d1WriteLock(remainingD0index);
                case 2 : return d2WriteLock(remainingD0index,remainingD1index);
                case 3 : return d3WriteLock(remainingD0index,remainingD1index,remainingD2index);
                case 4 : return d4WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index);
                case 5 : return d5WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
                default : return writeLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index][remainingD4index][remainingD5index];
            }
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,0);
        }

        public Lock getRowWriteLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 6)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 6, found " + remainingIndices.length);
            return getRowWriteLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3],remainingIndices[4],remainingIndices[5]);
        }

        public Lock getTensorReadLock() {
            return fullReadLock;
        }

        public Lock getTensorWriteLock() {
            return fullWriteLock;
        }
    }
    
    private static class D7Lock implements ConcurrentTensorLocks,
                                           ConcurrentD7TensorShell.ConcurrentD7TensorLocks,
                                           ConcurrentD8TensorShell.ConcurrentD8TensorLocks,
                                           ConcurrentD9TensorShell.ConcurrentD9TensorLocks {
        private final int d1Size;
        private final int d2Size;
        private final int d3Size;
        private final int d4Size;
        private final int d5Size;
        private final int d6Size;
        private final Lock[][][][][][][] readLocks;
        private final Lock[][][][][][][] writeLocks;
        private final Lock fullReadLock;
        private final Lock fullWriteLock;

        private D7Lock(int[] dimensions) {
            int d0Size = dimensions[0];
            d1Size = dimensions[1];
            d2Size = dimensions[2];
            d3Size = dimensions[3];
            d4Size = dimensions[4];
            d5Size = dimensions[5];
            d6Size = dimensions[6];
            readLocks = new Lock[d0Size][d1Size][d2Size][d3Size][d4Size][d5Size][d6Size];
            writeLocks = new Lock[d0Size][d1Size][d2Size][d3Size][d4Size][d5Size][d6Size];
            List<Lock> rLocks = new LinkedList<Lock>();
            List<Lock> wLocks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d0Size,d1Size,d2Size,d3Size,d4Size,d5Size,d6Size)) {
                ReadWriteLock l = new ReentrantReadWriteLock();
                readLocks[i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]] = l.readLock();
                writeLocks[i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]] = l.writeLock();
                rLocks.add(l.readLock());
                wLocks.add(l.writeLock());
            }
            fullReadLock = new MultiLock(rLocks);
            fullWriteLock = new MultiLock(wLocks);
        }

        private Lock d1ReadLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size,d4Size,d5Size,d6Size))
                locks.add(readLocks[d0index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]]);
            return new MultiLock(locks);
        }

        private Lock d1WriteLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size,d4Size,d5Size,d6Size))
                locks.add(writeLocks[d0index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]]);
            return new MultiLock(locks);
        }

        private Lock d2ReadLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size,d4Size,d5Size,d6Size))
                locks.add(readLocks[d0index][d1index][i[0]][i[1]][i[2]][i[3]][i[4]]);
            return new MultiLock(locks);
        }

        private Lock d2WriteLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size,d4Size,d5Size,d6Size))
                locks.add(writeLocks[d0index][d1index][i[0]][i[1]][i[2]][i[3]][i[4]]);
            return new MultiLock(locks);
        }

        private Lock d3ReadLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size,d4Size,d5Size,d6Size))
                locks.add(readLocks[d0index][d1index][d2index][i[0]][i[1]][i[2]][i[3]]);
            return new MultiLock(locks);
        }

        private Lock d3WriteLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size,d4Size,d5Size,d6Size))
                locks.add(writeLocks[d0index][d1index][d2index][i[0]][i[1]][i[2]][i[3]]);
            return new MultiLock(locks);
        }

        private Lock d4ReadLock(int d0index, int d1index, int d2index, int d3index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d4Size,d5Size,d6Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d4WriteLock(int d0index, int d1index, int d2index, int d3index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d4Size,d5Size,d6Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d5ReadLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d5Size,d6Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][d4index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d5WriteLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d5Size,d6Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][d4index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d6ReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d6Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][d4index][d5index][i[0]]);
            return new MultiLock(locks);
        }

        private Lock d6WriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d6Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][d4index][d5index][i[0]]);
            return new MultiLock(locks);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getReadLock(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getReadLock(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return readLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public Lock getReadLock(int ... indices) {
            if (indices.length < 7)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 7, found " + indices.length);
            return getReadLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6]);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getWriteLock(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return getWriteLock(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return writeLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public Lock getWriteLock(int ... indices) {
            if (indices.length < 7)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 7, found " + indices.length);
            return getWriteLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6]);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            switch (dimension) {
                case 0 : return fullReadLock;
                case 1 : return d1ReadLock(remainingD0index);
                case 2 : return d2ReadLock(remainingD0index,remainingD1index);
                case 3 : return d3ReadLock(remainingD0index,remainingD1index,remainingD2index);
                case 4 : return d4ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index);
                case 5 : return d5ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
                case 6 : return d6ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index);
                default : return readLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index][remainingD4index][remainingD5index][remainingD6index];
            }
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,0);
        }

        public Lock getRowReadLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 7)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 7, found " + remainingIndices.length);
            return getRowReadLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3],remainingIndices[4],remainingIndices[5],remainingIndices[6]);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            switch (dimension) {
                case 0 : return fullWriteLock;
                case 1 : return d1WriteLock(remainingD0index);
                case 2 : return d2WriteLock(remainingD0index,remainingD1index);
                case 3 : return d3WriteLock(remainingD0index,remainingD1index,remainingD2index);
                case 4 : return d4WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index);
                case 5 : return d5WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
                case 6 : return d6WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index);
                default : return writeLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index][remainingD4index][remainingD5index][remainingD6index];
            }
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,0);
        }

        public Lock getRowWriteLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 7)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 7, found " + remainingIndices.length);
            return getRowWriteLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3],remainingIndices[4],remainingIndices[5],remainingIndices[6]);
        }

        public Lock getTensorReadLock() {
            return fullReadLock;
        }

        public Lock getTensorWriteLock() {
            return fullWriteLock;
        }
    }
    
    private static class D8Lock implements ConcurrentTensorLocks,
                                           ConcurrentD8TensorShell.ConcurrentD8TensorLocks,
                                           ConcurrentD9TensorShell.ConcurrentD9TensorLocks {
        private final int d1Size;
        private final int d2Size;
        private final int d3Size;
        private final int d4Size;
        private final int d5Size;
        private final int d6Size;
        private final int d7Size;
        private final Lock[][][][][][][][] readLocks;
        private final Lock[][][][][][][][] writeLocks;
        private final Lock fullReadLock;
        private final Lock fullWriteLock;

        private D8Lock(int[] dimensions) {
            int d0Size = dimensions[0];
            d1Size = dimensions[1];
            d2Size = dimensions[2];
            d3Size = dimensions[3];
            d4Size = dimensions[4];
            d5Size = dimensions[5];
            d6Size = dimensions[6];
            d7Size = dimensions[7];
            readLocks = new Lock[d0Size][d1Size][d2Size][d3Size][d4Size][d5Size][d6Size][d7Size];
            writeLocks = new Lock[d0Size][d1Size][d2Size][d3Size][d4Size][d5Size][d6Size][d7Size];
            List<Lock> rLocks = new LinkedList<Lock>();
            List<Lock> wLocks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d0Size,d1Size,d2Size,d3Size,d4Size,d5Size,d6Size,d7Size)) {
                ReadWriteLock l = new ReentrantReadWriteLock();
                readLocks[i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]][i[7]] = l.readLock();
                writeLocks[i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]][i[7]] = l.writeLock();
                rLocks.add(l.readLock());
                wLocks.add(l.writeLock());
            }
            fullReadLock = new MultiLock(rLocks);
            fullWriteLock = new MultiLock(wLocks);
        }

        private Lock d1ReadLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size,d4Size,d5Size,d6Size,d7Size))
                locks.add(readLocks[d0index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]]);
            return new MultiLock(locks);
        }

        private Lock d1WriteLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size,d4Size,d5Size,d6Size,d7Size))
                locks.add(writeLocks[d0index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]]);
            return new MultiLock(locks);
        }

        private Lock d2ReadLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size,d4Size,d5Size,d6Size,d7Size))
                locks.add(readLocks[d0index][d1index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]]);
            return new MultiLock(locks);
        }

        private Lock d2WriteLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size,d4Size,d5Size,d6Size,d7Size))
                locks.add(writeLocks[d0index][d1index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]]);
            return new MultiLock(locks);
        }

        private Lock d3ReadLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size,d4Size,d5Size,d6Size,d7Size))
                locks.add(readLocks[d0index][d1index][d2index][i[0]][i[1]][i[2]][i[3]][i[4]]);
            return new MultiLock(locks);
        }

        private Lock d3WriteLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size,d4Size,d5Size,d6Size,d7Size))
                locks.add(writeLocks[d0index][d1index][d2index][i[0]][i[1]][i[2]][i[3]][i[4]]);
            return new MultiLock(locks);
        }

        private Lock d4ReadLock(int d0index, int d1index, int d2index, int d3index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d4Size,d5Size,d6Size,d7Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][i[0]][i[1]][i[2]][i[3]]);
            return new MultiLock(locks);
        }

        private Lock d4WriteLock(int d0index, int d1index, int d2index, int d3index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d4Size,d5Size,d6Size,d7Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][i[0]][i[1]][i[2]][i[3]]);
            return new MultiLock(locks);
        }

        private Lock d5ReadLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d5Size,d6Size,d7Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][d4index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d5WriteLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d5Size,d6Size,d7Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][d4index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d6ReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d6Size,d7Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][d4index][d5index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d6WriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d6Size,d7Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][d4index][d5index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d7ReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d7Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index][i[0]]);
            return new MultiLock(locks);
        }

        private Lock d7WriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d7Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index][i[0]]);
            return new MultiLock(locks);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getReadLock(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return readLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public Lock getReadLock(int ... indices) {
            if (indices.length < 8)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 8, found " + indices.length);
            return getReadLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7]);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return getWriteLock(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return writeLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public Lock getWriteLock(int ... indices) {
            if (indices.length < 8)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 8, found " + indices.length);
            return getWriteLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7]);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            switch (dimension) {
                case 0 : return fullReadLock;
                case 1 : return d1ReadLock(remainingD0index);
                case 2 : return d2ReadLock(remainingD0index,remainingD1index);
                case 3 : return d3ReadLock(remainingD0index,remainingD1index,remainingD2index);
                case 4 : return d4ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index);
                case 5 : return d5ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
                case 6 : return d6ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index);
                case 7 : return d7ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index);
                default : return readLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index][remainingD4index][remainingD5index][remainingD6index][remainingD7index];
            }
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index,0);
        }

        public Lock getRowReadLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 8)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 8, found " + remainingIndices.length);
            return getRowReadLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3],remainingIndices[4],remainingIndices[5],remainingIndices[6],remainingIndices[7]);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            switch (dimension) {
                case 0 : return fullWriteLock;
                case 1 : return d1WriteLock(remainingD0index);
                case 2 : return d2WriteLock(remainingD0index,remainingD1index);
                case 3 : return d3WriteLock(remainingD0index,remainingD1index,remainingD2index);
                case 4 : return d4WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index);
                case 5 : return d5WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
                case 6 : return d6WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index);
                case 7 : return d7WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index);
                default : return writeLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index][remainingD4index][remainingD5index][remainingD6index][remainingD7index];
            }
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index,0);
        }

        public Lock getRowWriteLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 8)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 8, found " + remainingIndices.length);
            return getRowWriteLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3],remainingIndices[4],remainingIndices[5],remainingIndices[6],remainingIndices[7]);
        }

        public Lock getTensorReadLock() {
            return fullReadLock;
        }

        public Lock getTensorWriteLock() {
            return fullWriteLock;
        }
    }
    
    private static class D9Lock implements ConcurrentTensorLocks,
                                           ConcurrentD9TensorShell.ConcurrentD9TensorLocks {
        private final int d1Size;
        private final int d2Size;
        private final int d3Size;
        private final int d4Size;
        private final int d5Size;
        private final int d6Size;
        private final int d7Size;
        private final int d8Size;
        private final Lock[][][][][][][][][] readLocks;
        private final Lock[][][][][][][][][] writeLocks;
        private final Lock fullReadLock;
        private final Lock fullWriteLock;

        private D9Lock(int[] dimensions) {
            int d0Size = dimensions[0];
            d1Size = dimensions[1];
            d2Size = dimensions[2];
            d3Size = dimensions[3];
            d4Size = dimensions[4];
            d5Size = dimensions[5];
            d6Size = dimensions[6];
            d7Size = dimensions[7];
            d8Size = dimensions[8];
            readLocks = new Lock[d0Size][d1Size][d2Size][d3Size][d4Size][d5Size][d6Size][d7Size][d8Size];
            writeLocks = new Lock[d0Size][d1Size][d2Size][d3Size][d4Size][d5Size][d6Size][d7Size][d8Size];
            List<Lock> rLocks = new LinkedList<Lock>();
            List<Lock> wLocks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d0Size,d1Size,d2Size,d3Size,d4Size,d5Size,d6Size,d7Size,d8Size)) {
                ReadWriteLock l = new ReentrantReadWriteLock();
                readLocks[i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]][i[7]][i[8]] = l.readLock();
                writeLocks[i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]][i[7]][i[8]] = l.writeLock();
                rLocks.add(l.readLock());
                wLocks.add(l.writeLock());
            }
            fullReadLock = new MultiLock(rLocks);
            fullWriteLock = new MultiLock(wLocks);
        }

        private Lock d1ReadLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size,d4Size,d5Size,d6Size,d7Size,d8Size))
                locks.add(readLocks[d0index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]][i[7]]);
            return new MultiLock(locks);
        }

        private Lock d1WriteLock(int d0index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d1Size,d2Size,d3Size,d4Size,d5Size,d6Size,d7Size,d8Size))
                locks.add(writeLocks[d0index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]][i[7]]);
            return new MultiLock(locks);
        }

        private Lock d2ReadLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size,d4Size,d5Size,d6Size,d7Size,d8Size))
                locks.add(readLocks[d0index][d1index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]]);
            return new MultiLock(locks);
        }

        private Lock d2WriteLock(int d0index, int d1index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d2Size,d3Size,d4Size,d5Size,d6Size,d7Size,d8Size))
                locks.add(writeLocks[d0index][d1index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]][i[6]]);
            return new MultiLock(locks);
        }

        private Lock d3ReadLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size,d4Size,d5Size,d6Size,d7Size,d8Size))
                locks.add(readLocks[d0index][d1index][d2index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]]);
            return new MultiLock(locks);
        }

        private Lock d3WriteLock(int d0index, int d1index, int d2index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d3Size,d4Size,d5Size,d6Size,d7Size,d8Size))
                locks.add(writeLocks[d0index][d1index][d2index][i[0]][i[1]][i[2]][i[3]][i[4]][i[5]]);
            return new MultiLock(locks);
        }

        private Lock d4ReadLock(int d0index, int d1index, int d2index, int d3index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d4Size,d5Size,d6Size,d7Size,d8Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][i[0]][i[1]][i[2]][i[3]][i[4]]);
            return new MultiLock(locks);
        }

        private Lock d4WriteLock(int d0index, int d1index, int d2index, int d3index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d4Size,d5Size,d6Size,d7Size,d8Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][i[0]][i[1]][i[2]][i[3]][i[4]]);
            return new MultiLock(locks);
        }

        private Lock d5ReadLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d5Size,d6Size,d7Size,d8Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][d4index][i[0]][i[1]][i[2]][i[3]]);
            return new MultiLock(locks);
        }

        private Lock d5WriteLock(int d0index, int d1index, int d2index, int d3index, int d4index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d5Size,d6Size,d7Size,d8Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][d4index][i[0]][i[1]][i[2]][i[3]]);
            return new MultiLock(locks);
        }

        private Lock d6ReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d6Size,d7Size,d8Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][d4index][d5index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d6WriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d6Size,d7Size,d8Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][d4index][d5index][i[0]][i[1]][i[2]]);
            return new MultiLock(locks);
        }

        private Lock d7ReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d7Size,d8Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d7WriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d7Size,d8Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index][i[0]][i[1]]);
            return new MultiLock(locks);
        }

        private Lock d8ReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d8Size))
                locks.add(readLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][i[0]]);
            return new MultiLock(locks);
        }

        private Lock d8WriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            List<Lock> locks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(d8Size))
                locks.add(writeLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][i[0]]);
            return new MultiLock(locks);
        }

        public Lock getReadLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return readLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public Lock getReadLock(int ... indices) {
            if (indices.length < 9)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 9, found " + indices.length);
            return getReadLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7],indices[8]);
        }

        public Lock getWriteLock(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return writeLocks[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public Lock getWriteLock(int ... indices) {
            if (indices.length < 9)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 9, found " + indices.length);
            return getWriteLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7],indices[8]);
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index, int remainingD8index) {
            switch (dimension) {
                case 0 : return fullReadLock;
                case 1 : return d1ReadLock(remainingD0index);
                case 2 : return d2ReadLock(remainingD0index,remainingD1index);
                case 3 : return d3ReadLock(remainingD0index,remainingD1index,remainingD2index);
                case 4 : return d4ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index);
                case 5 : return d5ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
                case 6 : return d6ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index);
                case 7 : return d7ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index);
                case 8 : return d8ReadLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index,remainingD7index);
                default : return readLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index][remainingD4index][remainingD5index][remainingD6index][remainingD7index][remainingD8index];
            }
        }

        public Lock getRowReadLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowReadLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index,remainingD7index,0);
        }

        public Lock getRowReadLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 9)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 9, found " + remainingIndices.length);
            return getRowReadLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3],remainingIndices[4],remainingIndices[5],remainingIndices[6],remainingIndices[7],remainingIndices[8]);
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index, int remainingD8index) {
            switch (dimension) {
                case 0 : return fullWriteLock;
                case 1 : return d1WriteLock(remainingD0index);
                case 2 : return d2WriteLock(remainingD0index,remainingD1index);
                case 3 : return d3WriteLock(remainingD0index,remainingD1index,remainingD2index);
                case 4 : return d4WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index);
                case 5 : return d5WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index);
                case 6 : return d6WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index);
                case 7 : return d7WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index);
                case 8 : return d8WriteLock(remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index,remainingD7index);
                default : return writeLocks[remainingD0index][remainingD1index][remainingD2index][remainingD3index][remainingD4index][remainingD5index][remainingD6index][remainingD7index][remainingD8index];
            }
        }

        public Lock getRowWriteLock(int dimension, int remainingD0index, int remainingD1index, int remainingD2index, int remainingD3index, int remainingD4index, int remainingD5index, int remainingD6index, int remainingD7index) {
            return getRowWriteLock(dimension,remainingD0index,remainingD1index,remainingD2index,remainingD3index,remainingD4index,remainingD5index,remainingD6index,remainingD7index,0);
        }

        public Lock getRowWriteLock(int dimension, int ... remainingIndices) {
            if (dimension < 0)
                throw new IllegalArgumentException("Lock dimension must be positive: " + dimension);
            if (remainingIndices.length < 9)
                throw new IllegalArgumentException("Remaining indices length too short, expected at least 9, found " + remainingIndices.length);
            return getRowWriteLock(dimension,remainingIndices[0],remainingIndices[1],remainingIndices[2],remainingIndices[3],remainingIndices[4],remainingIndices[5],remainingIndices[6],remainingIndices[7],remainingIndices[8]);
        }

        public Lock getTensorReadLock() {
            return fullReadLock;
        }

        public Lock getTensorWriteLock() {
            return fullWriteLock;
        }
    }

    private static class DNLock implements ConcurrentTensorLocks {
        private final int[] dimensions;
        private final Object readLocks;
        private final Object writeLocks;
        private final Lock fullReadLock;
        private final Lock fullWriteLock;

        private DNLock(int ... dimensions) {
            readLocks = Array.newInstance(Lock.class,dimensions);
            writeLocks = Array.newInstance(Lock.class,dimensions);
            List<Lock> allReadLocks = new LinkedList<Lock>();
            List<Lock> allWriteLocks = new LinkedList<Lock>();
            for (int[] i : IterableAbacus.getIterableAbacus(dimensions)) {
                ReadWriteLock l = new ReentrantReadWriteLock();
                ArrayUtil.setValue(readLocks,l.readLock(),i);
                ArrayUtil.setValue(writeLocks,l.writeLock(),i);
                allReadLocks.add(l.readLock());
                allWriteLocks.add(l.writeLock());
            }
            fullReadLock = new MultiLock(allReadLocks);
            fullWriteLock = new MultiLock(allWriteLocks);
            this.dimensions = ArrayUtil.copyArray(dimensions);
        }

        private Lock dXLock(Object lockArray, int ... indices) {
            List<Lock> locks = new LinkedList<Lock>();
            LockableAbacus a = new LockableAbacus(dimensions);
            for (int i = 0; i < indices.length; i++)
                a.lockDimension(i,indices[i]);
            for (int[] i : new IterableAbacus(a))
                locks.add((Lock) ArrayUtil.getValue(lockArray, i));
            return new MultiLock(locks);
        }

        @Override
        public Lock getReadLock(int ... indices) {
            if (indices.length != dimensions.length)
                throw new IllegalArgumentException(String.format("Indices not correct length (%d) for locks of length %d.",indices.length,dimensions.length));
            return (Lock) ArrayUtil.getValue(readLocks,Arrays.copyOf(indices,dimensions.length));
        }

        @Override
        public Lock getWriteLock(int ... indices) {
            if (indices.length != dimensions.length)
                throw new IllegalArgumentException(String.format("Indices not correct length (%d) for locks of length %d.",indices.length,dimensions.length));
            return (Lock) ArrayUtil.getValue(writeLocks,Arrays.copyOf(indices,dimensions.length));
        }

        @Override
        public Lock getRowReadLock(int dimension, int... remainingIndices) {
            if (remainingIndices.length+dimension != dimensions.length)
                            throw new IllegalArgumentException(String.format("Lock dimension (%d) and remaining indices (%d) not correct length for locks of length %d.",dimension,remainingIndices.length,dimensions.length));
                        switch (dimension) {
                case 0 : return fullReadLock;
                default : return dXLock(readLocks,Arrays.copyOf(remainingIndices,dimensions.length-dimension));
            }
        }

        @Override
        public Lock getRowWriteLock(int dimension, int... remainingIndices) {
            if (remainingIndices.length+dimension != dimensions.length)
                throw new IllegalArgumentException(String.format("Lock dimension (%d) and remaining indices (%d) not correct length for locks of length %d.",dimension,remainingIndices.length,dimensions.length));
            switch (dimension) {
                case 0 : return fullWriteLock;
                default : return dXLock(writeLocks,Arrays.copyOf(remainingIndices,dimensions.length-dimension));
            }
        }

        public Lock getTensorReadLock() {
            return fullReadLock;
        }

        public Lock getTensorWriteLock() {
            return fullWriteLock;
        }
    }
}