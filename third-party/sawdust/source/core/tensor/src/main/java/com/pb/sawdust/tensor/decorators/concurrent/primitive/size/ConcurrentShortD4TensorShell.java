package com.pb.sawdust.tensor.decorators.concurrent.primitive.size;

import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.concurrent.size.ConcurrentD4TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.ShortTensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdShortTensor;

import com.pb.sawdust.tensor.decorators.primitive.size.ShortD4Tensor;
import com.pb.sawdust.util.array.ShortTypeSafeArray;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentShortD4TensorShell} class provides a wrapper for implementations of the {@code ShortD4Tensor} interface
 * with support for concurrent access. The locking policy is set by the {@code D4ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Dec 14, 2009 12:35:27 PM
 */
public class ConcurrentShortD4TensorShell extends ConcurrentD4TensorShell<Short> implements ShortD4Tensor {
    private final ShortD4Tensor tensor;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentD4TensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentShortD4TensorShell(ShortD4Tensor tensor, ConcurrentD4TensorLocks locks) {
        super(tensor,locks);
        this.tensor = tensor;
    }
    
    public short getCell(int d0index, int d1index, int d2index, int d3index) {
        Lock lock = locks.getReadLock(d0index,d1index,d2index,d3index);
        lock.lock();
        try {
            return tensor.getCell(d0index,d1index,d2index,d3index);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(short value, int d0index, int d1index, int d2index, int d3index) {
        Lock lock = locks.getWriteLock(d0index,d1index,d2index,d3index);
        lock.lock();
        try {
            tensor.setCell(value,d0index,d1index,d2index,d3index);
        } finally {
            lock.unlock();
        }
    }

    public short getCell(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getReadLock(indices[0],indices[1],indices[2],indices[3]);
        lock.lock();
        try {
            return tensor.getCell(indices[0],indices[1],indices[2],indices[3]);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(short value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getWriteLock(indices[0],indices[1],indices[2],indices[3]);
        lock.lock();
        try {
            tensor.setCell(value,indices[0],indices[1],indices[2],indices[3]);
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

    public ShortTypeSafeArray getTensorValues() {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues();
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

    protected ShortTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }

    public <I> IdShortTensor<I> getReferenceTensor(Index<I> index) {
        return (IdShortTensor<I>) super.getReferenceTensor(index);
    }
}