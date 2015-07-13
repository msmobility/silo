package com.pb.sawdust.tensor.decorators.concurrent.primitive;

import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.id.primitive.IdIntTensor;
import com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocks;
import com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorShell;
import com.pb.sawdust.util.array.IntTypeSafeArray;
import com.pb.sawdust.tensor.decorators.primitive.IntTensor;
import com.pb.sawdust.tensor.TensorImplUtil;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentIntTensorShell} class provides a wrapper for implementations of the {@code IntTensor} interface
 * with support for concurrent access. The locking policy is set by the {@code ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public class ConcurrentIntTensorShell extends ConcurrentTensorShell<Integer> implements IntTensor {
    private final IntTensor tensor;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentTensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentIntTensorShell(IntTensor tensor, ConcurrentTensorLocks locks) {
        super(tensor,locks);
        this.tensor = tensor;
    }

    public int getCell(int ... indices) {
        Lock lock = locks.getReadLock(indices);
        lock.lock();
        try {
            return tensor.getCell(indices);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(int value, int ... indices) {
        Lock lock = locks.getWriteLock(indices);
        lock.lock();
        try {
            tensor.setCell(value,indices);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(IntTypeSafeArray valuesArray) {
        Lock lock = locks.getTensorWriteLock();
        lock.lock();
        try {
            tensor.setTensorValues(valuesArray);
        } finally {
            lock.unlock();
        }
    }

    public IntTypeSafeArray getTensorValues(Class<Integer> type) {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues(type);
        } finally {
            lock.unlock();
        }
    }

    public IntTypeSafeArray getTensorValues() {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues();
        } finally {
            lock.unlock();
        }
    }

    public <I> IdIntTensor<I> getReferenceTensor(Index<I> index) {
        return (IdIntTensor<I>) super.getReferenceTensor(index);
    }

    protected IntTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}
