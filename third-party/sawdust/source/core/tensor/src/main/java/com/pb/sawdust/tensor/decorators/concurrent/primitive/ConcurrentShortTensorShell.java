package com.pb.sawdust.tensor.decorators.concurrent.primitive;

import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.id.primitive.IdShortTensor;
import com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocks;
import com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorShell;
import com.pb.sawdust.util.array.ShortTypeSafeArray;
import com.pb.sawdust.tensor.decorators.primitive.ShortTensor;
import com.pb.sawdust.tensor.TensorImplUtil;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentShortTensorShell} class provides a wrapper for implementations of the {@code ShortTensor} interface
 * with support for concurrent access. The locking policy is set by the {@code ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public class ConcurrentShortTensorShell extends ConcurrentTensorShell<Short> implements ShortTensor {
    private final ShortTensor tensor;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentTensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentShortTensorShell(ShortTensor tensor, ConcurrentTensorLocks locks) {
        super(tensor,locks);
        this.tensor = tensor;
    }

    public short getCell(int ... indices) {
        Lock lock = locks.getReadLock(indices);
        lock.lock();
        try {
            return tensor.getCell(indices);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(short value, int ... indices) {
        Lock lock = locks.getWriteLock(indices);
        lock.lock();
        try {
            tensor.setCell(value,indices);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(ShortTypeSafeArray valuesArray) {
        Lock lock = locks.getTensorWriteLock();
        lock.lock();
        try {
            tensor.setTensorValues(valuesArray);
        } finally {
            lock.unlock();
        }
    }

    public ShortTypeSafeArray getTensorValues(Class<Short> type) {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues(type);
        } finally {
            lock.unlock();
        }
    }

    public ShortTypeSafeArray getTensorValues() {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues();
        } finally {
            lock.unlock();
        }
    }

    public <I> IdShortTensor<I> getReferenceTensor(Index<I> index) {
        return (IdShortTensor<I>) super.getReferenceTensor(index);
    }

    protected ShortTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }
}
