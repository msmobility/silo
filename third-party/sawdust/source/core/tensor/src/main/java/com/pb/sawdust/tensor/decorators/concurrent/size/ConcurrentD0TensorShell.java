package com.pb.sawdust.tensor.decorators.concurrent.size;

import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.ComposableTensor;
import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.alias.scalar.Scalar;
import com.pb.sawdust.tensor.decorators.size.D0Tensor;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.TypeSafeArray;

import java.util.concurrent.locks.Lock;
import java.util.Iterator;

/**
 * The {@code ConcurrentD0TensorShell} class provides a wrapper for implementations of the {@code D0Tensor} interface
 * with support for concurrent access. The locking policy is set by the {@code D0ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Jun 16, 2009 3:17:19 PM
 */
public class ConcurrentD0TensorShell<T> extends ComposableTensor<T> implements Scalar<T> {
    private final D0Tensor<T> tensor;
    
    /**
     * The {@code ConcurrentD0TensorLocks} instance holding the concurrency policy used when locking this tensor.
     */
    protected final ConcurrentD0TensorLocks locks;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentD0TensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentD0TensorShell(D0Tensor<T> tensor, ConcurrentD0TensorLocks locks) {
        this.tensor = tensor;
        this.locks = locks;
    }
    
    /**
     * The {@code ConcurrentD0TensorLocks} interface provides methods through which locks protecting concurrent 0-dimensional tensors 
     * can be obtained. The locking policies/strategies (such as whether reads and writes have different semantics, whether operations should be atomic, 
     * etc.) are left to the implementation.  
     */
    public static interface ConcurrentD0TensorLocks {
    
        /**
         * Get the lock protecting read access for the entire tensor.
         *
         * @return the read lock for this tensor.
         */
        public abstract Lock getReadLock();
        
        /**
         * Get the lock protecting write access for the entire tensor.
         *
         * @return the write lock for this tensor.
         */
        public abstract Lock getWriteLock();
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
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getValue();
        } finally {
            lock.unlock();
        }
    }

    public void setValue(T value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getWriteLock();
        lock.lock();
        try {
            tensor.setValue(value);
        } finally {
            lock.unlock();
        }
    }

    public TypeSafeArray<T> getTensorValues(Class<T> type) {
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues(type);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(TypeSafeArray<? extends T> typeSafeArray) {
        Lock lock = locks.getWriteLock();
        lock.lock();
        try {
            tensor.setTensorValues(typeSafeArray);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(Tensor<? extends T> tensor) {
        Lock lock = locks.getWriteLock();
        lock.lock();
        try {
            this.tensor.setTensorValues(tensor);
        } finally {
            lock.unlock();
        }
    }

    public Index<?> getIndex() {
        return tensor.getIndex();
    }

    public T getValue() {
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getValue();
        } finally {
            lock.unlock();
        }
    }

    public void setValue(T value) {
        Lock lock = locks.getWriteLock();
        lock.lock();
        try {
            tensor.setValue(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Iterator<Tensor<T>> iterator() {
        return new Iterator<Tensor<T>>() {
            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Tensor<T> next() {
                hasNext = false;
                return ConcurrentD0TensorShell.this;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
