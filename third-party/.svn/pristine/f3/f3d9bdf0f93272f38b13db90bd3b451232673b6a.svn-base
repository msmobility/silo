package com.pb.sawdust.tensor.decorators.concurrent.primitive.size;

import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.concurrent.size.ConcurrentD1TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.LongTensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdLongTensor;
import com.pb.sawdust.tensor.alias.vector.primitive.LongVector;
import com.pb.sawdust.tensor.decorators.primitive.size.LongD1Tensor;
import com.pb.sawdust.util.array.LongTypeSafeArray;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentLongD1TensorShell} class provides a wrapper for implementations of the {@code LongVector} interface
 * with support for concurrent access. The locking policy is set by the {@code D1ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Dec 14, 2009 12:35:23 PM
 */
public class ConcurrentLongD1TensorShell extends ConcurrentD1TensorShell<Long> implements LongVector {
    private final LongD1Tensor tensor;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentD1TensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentLongD1TensorShell(LongD1Tensor tensor, ConcurrentD1TensorLocks locks) {
        super(tensor,locks);
        this.tensor = tensor;
    }
    
    public long getCell(int index) {
        Lock lock = locks.getReadLock(index);
        lock.lock();
        try {
            return tensor.getCell(index);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(long value, int index) {
        Lock lock = locks.getWriteLock(index);
        lock.lock();
        try {
            tensor.setCell(value,index);
        } finally {
            lock.unlock();
        }
    }

    public long getCell(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getReadLock(indices[0]);
        lock.lock();
        try {
            return tensor.getCell(indices[0]);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(long value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getWriteLock(indices[0]);
        lock.lock();
        try {
            tensor.setCell(value,indices[0]);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(LongTypeSafeArray valuesArray) {
        Lock lock = locks.getTensorWriteLock();
        lock.lock();
        try {
            tensor.setTensorValues(valuesArray);
        } finally {
            lock.unlock();
        }
    }

    public LongTypeSafeArray getTensorValues() {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues();
        } finally {
            lock.unlock();
        }
    }               

    public LongTypeSafeArray getTensorValues(Class<Long> type) {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues(type);
        } finally {
            lock.unlock();
        }
    }

    protected LongTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }

    public <I> IdLongTensor<I> getReferenceTensor(Index<I> index) {
        return (IdLongTensor<I>) super.getReferenceTensor(index);
    }
}