package com.pb.sawdust.tensor.decorators.concurrent.size;

import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.ComposableTensor;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.decorators.size.D2Tensor;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.TypeSafeArray;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentD2TensorShell} class provides a wrapper for implementations of the {@code Matrix} interface
 * with support for concurrent access. The locking policy is set by the {@code D2ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Dec 14, 2009 12:35:25 PM
 */
public class ConcurrentD2TensorShell<T> extends ComposableTensor<T> implements Matrix<T> {
    private final D2Tensor<T> tensor;
    
    /**
     * The {@code ConcurrentD2TensorLocks} instance holding the concurrency policy used when locking this tensor.
     */
    protected final ConcurrentD2TensorLocks locks;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentD2TensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentD2TensorShell(D2Tensor<T> tensor, ConcurrentD2TensorLocks locks) {
        this.tensor = tensor;
        this.locks = locks;
    }
    
    /**
     * The {@code ConcurrentD2TensorLocks} interface provides methods through which locks protecting concurrent 2-dimensional tensors 
     * can be obtained. The locking policies/strategies (such as whether reads and writes have different semantics, whether operations should be atomic, 
     * etc.) are left to the implementation.  
     */
    public static interface ConcurrentD2TensorLocks {
    
        /**
         * Get the lock protecting read access for the specified tensor position.
         *
         * @param d0index
         *        The index of dimension 0 of the tensor element.
         *
         * @param d1index
         *        The index of dimension 1 of the tensor element.
         *
         * @return the read access lock for the specified tensor position.
         */
        public abstract Lock getReadLock(int d0index, int d1index);
        
        /**
         * Get the lock protecting write access for the specified tensor position.
         *
         * @param d0index
         *        The index of dimension 0 of the tensor element.
         *
         * @param d1index
         *        The index of dimension 1 of the tensor element.
         *
         * @return the write access lock for the specified tensor position.
         */
        public abstract Lock getWriteLock(int d0index, int d1index);
        
        /**
         * Get the lock protecting read access for the specified tensor row.
         *
         * @param dimension
         *        The (0-based) dimension which the row resides in.
     *
     * @param remainingD0index
     *        The index of remaining dimension 0 of the tensor element.
         *
         * @return the read lock for the specified tensor row.
         */
        public abstract Lock getRowReadLock(int dimension, int remainingD0index);
        
        /**
         * Get the lock protecting write access for the specified tensor row.
         *
         * @param dimension
         *        The (0-based) dimension which the row resides in.
     *
     * @param remainingD0index
     *        The index of remaining dimension 0 of the tensor element.
         *
         * @return the write lock for the specified tensor row.
         */
        public abstract Lock getRowWriteLock(int dimension, int remainingD0index);
        
        /**
         * Get the lock protecting read access for the entire tensor.
         *
         * @return the read lock for this tensor.
         */
        public abstract Lock getTensorReadLock();
        
        /**
         * Get the lock protecting write access for the entire tensor.
         *
         * @return the write lock for this tensor.
         */
        public abstract Lock getTensorWriteLock();
    }

    public int[] getDimensions() {
        return tensor.getDimensions();
    }

    public int size(int dimension) {
        return tensor.size(dimension);
    }

    public int size() {
        return tensor.size();
    }

    public JavaType getType() {
        return tensor.getType();
    }

    public T getValue(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getReadLock(indices[0],indices[1]);
        lock.lock();
        try {
            return tensor.getValue(indices[0],indices[1]);
        } finally {
            lock.unlock();
        }
    }

    public void setValue(T value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getWriteLock(indices[0],indices[1]);
        lock.lock();
        try {
            tensor.setValue(value,indices[0],indices[1]);
        } finally {
            lock.unlock();
        }
    }

    public TypeSafeArray<T> getTensorValues(Class<T> type) {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues(type);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(TypeSafeArray<? extends T> typeSafeArray) {
        Lock lock = locks.getTensorWriteLock();
        lock.lock();
        try {
            tensor.setTensorValues(typeSafeArray);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(Tensor<? extends T> tensor) {
        Lock lock = locks.getTensorWriteLock();
        lock.lock();
        try {
            this.tensor.setTensorValues(tensor);
        } finally {
            lock.unlock();
        }
    }

//    public <I> IdTensor<T,I> getReferenceTensor(Index<I> index) {
//        return tensor.getReferenceTensor(index);
//    }

    public Index<?> getIndex() {
        return tensor.getIndex();
    }

    public T getValue(int d0index, int d1index) {
        Lock lock = locks.getReadLock(d0index,d1index);
        lock.lock();
        try {
            return tensor.getValue(d0index,d1index);
        } finally {
            lock.unlock();
        }
    }

    public void setValue(T value, int d0index, int d1index) {
        Lock lock = locks.getWriteLock(d0index,d1index);
        lock.lock();
        try {
            tensor.setValue(value,d0index,d1index);
        } finally {
            lock.unlock();
        }
    }
}
