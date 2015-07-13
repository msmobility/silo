package com.pb.sawdust.tensor.decorators.concurrent.primitive.size;

import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.concurrent.size.ConcurrentD9TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.IntTensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdIntTensor;

import com.pb.sawdust.tensor.decorators.primitive.size.IntD9Tensor;
import com.pb.sawdust.util.array.IntTypeSafeArray;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentIntD9TensorShell} class provides a wrapper for implementations of the {@code IntD9Tensor} interface
 * with support for concurrent access. The locking policy is set by the {@code D9ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Dec 14, 2009 12:35:34 PM
 */
public class ConcurrentIntD9TensorShell extends ConcurrentD9TensorShell<Integer> implements IntD9Tensor {
    private final IntD9Tensor tensor;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentD9TensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentIntD9TensorShell(IntD9Tensor tensor, ConcurrentD9TensorLocks locks) {
        super(tensor,locks);
        this.tensor = tensor;
    }
    
    public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
        Lock lock = locks.getReadLock(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        lock.lock();
        try {
            return tensor.getCell(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(int value, int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
        Lock lock = locks.getWriteLock(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        lock.lock();
        try {
            tensor.setCell(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        } finally {
            lock.unlock();
        }
    }

    public int getCell(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getReadLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7],indices[8]);
        lock.lock();
        try {
            return tensor.getCell(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7],indices[8]);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(int value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getWriteLock(indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7],indices[8]);
        lock.lock();
        try {
            tensor.setCell(value,indices[0],indices[1],indices[2],indices[3],indices[4],indices[5],indices[6],indices[7],indices[8]);
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

    public IntTypeSafeArray getTensorValues() {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues();
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

    protected IntTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }

    public <I> IdIntTensor<I> getReferenceTensor(Index<I> index) {
        return (IdIntTensor<I>) super.getReferenceTensor(index);
    }
}