package com.pb.sawdust.tensor.decorators.concurrent.primitive.size;

import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.alias.scalar.primitive.LongScalar;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.concurrent.size.ConcurrentD0TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.LongTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.LongD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdLongTensor;
import com.pb.sawdust.util.array.LongTypeSafeArray;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentLongD0TensorShell} class provides a wrapper for implementations of the {@code LongScalar} interface
 * with support for concurrent access. The locking policy is set by the {@code D0ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Jun 16, 2009 3:17:19 PM
 */
public class ConcurrentLongD0TensorShell extends ConcurrentD0TensorShell<Long> implements LongScalar {
    private final LongD0Tensor tensor;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentD0TensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentLongD0TensorShell(LongD0Tensor tensor, ConcurrentD0TensorLocks locks) {
        super(tensor,locks);
        this.tensor = tensor;
    }
    
    public long getCell() {
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getCell();
        } finally {
            lock.unlock();
        }
    }

    public void setCell(long value) {
        Lock lock = locks.getWriteLock();
        lock.lock();
        try {
            tensor.setCell(value);
        } finally {
            lock.unlock();
        }
    }

    public long getCell(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getCell();
        } finally {
            lock.unlock();
        }
    }

    public void setCell(long value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getWriteLock();
        lock.lock();
        try {
            tensor.setCell(value);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(LongTypeSafeArray valuesArray) {
        Lock lock = locks.getWriteLock();
        lock.lock();
        try {
            tensor.setTensorValues(valuesArray);
        } finally {
            lock.unlock();
        }
    }

    public LongTypeSafeArray getTensorValues() {
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues();
        } finally {
            lock.unlock();
        }
    }               

    public LongTypeSafeArray getTensorValues(Class<Long> type) {
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues(type);
        } finally {
            lock.unlock();
        }
    }

    protected LongTensor getComposedTensor(Index index) {
        return TensorImplUtil.getComposedTensor(this,index);
    }

    public <I> IdLongTensor<I> getReferenceTensor(Index<I> index) {
        return (IdLongTensor<I>) super.getReferenceTensor(index);
    }
}