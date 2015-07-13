package com.pb.sawdust.tensor.decorators.concurrent.primitive.size;

import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.concurrent.size.ConcurrentD6TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdDoubleTensor;

import com.pb.sawdust.tensor.decorators.primitive.size.DoubleD6Tensor;
import com.pb.sawdust.util.array.DoubleTypeSafeArray;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentDoubleD6TensorShell} class provides a wrapper for implementations of the {@code DoubleD6Tensor} interface
 * with support for concurrent access. The locking policy is set by the {@code D6ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Dec 14, 2009 12:35:30 PM
 */
public class ConcurrentDoubleD6TensorShell extends ConcurrentD6TensorShell<Double> implements DoubleD6Tensor {
    private final DoubleD6Tensor tensor;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentD6TensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentDoubleD6TensorShell(DoubleD6Tensor tensor, ConcurrentD6TensorLocks locks) {
        super(tensor,locks);
        this.tensor = tensor;
    }
    
    public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        Lock lock = locks.getReadLock(d0index,d1index,d2index,d3index,d4index,d5index);
        lock.lock();
        try {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(double value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
        Lock lock = locks.getWriteLock(d0index,d1index,d2index,d3index,d4index,d5index);
        lock.lock();
        try {
            tensor.setCell(value,d0index,d1index,d2index,d3index,d4index,d5index);
        } finally {
            lock.unlock();
        }
    }

    public double getCell(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getReadLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
        lock.lock();
        try {
            return tensor.getCell(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(double value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getWriteLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
        lock.lock();
        try {
            tensor.setCell(value,indices[0],indices[1],indices[2],indices[3],indices[4],indices[5]);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(DoubleTypeSafeArray valuesArray) {
        Lock lock = locks.getTensorWriteLock();
        lock.lock();
        try {
            tensor.setTensorValues(valuesArray);
        } finally {
            lock.unlock();
        }
    }

    public DoubleTypeSafeArray getTensorValues() {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues();
        } finally {
            lock.unlock();
        }
    }               

    public DoubleTypeSafeArray getTensorValues(Class<Double> type) {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues(type);
        } finally {
            lock.unlock();
        }
    }

    protected DoubleTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }

    public <I> IdDoubleTensor<I> getReferenceTensor(Index<I> index) {
        return (IdDoubleTensor<I>) super.getReferenceTensor(index);
    }
}