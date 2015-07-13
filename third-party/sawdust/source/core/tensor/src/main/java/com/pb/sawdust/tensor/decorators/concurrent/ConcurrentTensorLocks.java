package com.pb.sawdust.tensor.decorators.concurrent;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentTensorLocks} interface provides methods through which locks protecting concurrent tensors can
 * be obtained. The locking policies/strategies (such as whether reads and writes have different semantics, whether
 * operations should be atomic, etc.) are left to the implementation.
 *  
 * @author crf <br/>
 *         Started: Jan 31, 2009 4:58:42 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public interface ConcurrentTensorLocks { 
    /**
     * Get the lock protecting read access for the specified tensor position.
     *
     * @param indices
     *        The index location for each dimension.
     *
     * @return the read access lock for the specified tensor position.
     *
     * @throws IllegalArgumentException if {@code indices} are invalid for these locks - what invalid means will be
     *                                  implementation specific.
     */
    Lock getReadLock(int ... indices);
    
    /**
     * Get the lock protecting write access for the specified tensor position.
     *
     * @param indices
     *        The index location for each dimension.
     *
     * @return the write access lock for the specified tensor position.
     *
     * @throws IllegalArgumentException if {@code indices} are invalid for these locks - what invalid means will be
     *                                  implementation specific.
     */
    Lock getWriteLock(int ... indices);
    
    /**
     * Get the lock protecting read access for the specified tensor row.
     *
     * @param dimension
     *        The (0-based) dimension which the row resides in.
     *
     * @param remainingIndices
     *        The remaining dimension's index location.
     *
     * @return the read lock for the specified tensor row.
     *
     * @throws IllegalArgumentException if {@code indices} are invalid for these locks - what invalid means will be
     *                                  implementation specific.
     *
     * @throws IndexOutOfBoundsException if dimension is invalid for these locks - what invalid means will be
     *                                   implementation specific, but will at least include the case where
     *                                   {@code dimension < 0}.
     */
    Lock getRowReadLock(int dimension, int ... remainingIndices);
    
    /**
     * Get the lock protecting write access for the specified tensor row.
     *
     * @param dimension
     *        The (0-based) dimension which the row resides in.
     *
     * @param remainingIndices
     *        The remaining dimension's index location.
     *
     * @return the write lock for the specified tensor row.
     *
     * @throws IllegalArgumentException if {@code indices} are invalid for these locks - what invalid means will be
     *                                  implementation specific.
     *
     * @throws IndexOutOfBoundsException if dimension is invalid for these locks - what invalid means will be
     *                                   implementation specific, but will at least include the case where
     *                                   {@code dimension < 0}.
     */
    Lock getRowWriteLock(int dimension, int ... remainingIndices);
    
    /**
     * Get the lock protecting read access for the entire tensor.
     *
     * @return the read lock for this tensor.
     */
    Lock getTensorReadLock();
    
    /**
     * Get the lock protecting write access for the entire tensor.
     *
     * @return the write lock for this tensor.
     */
    Lock getTensorWriteLock();
}
