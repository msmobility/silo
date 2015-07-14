package com.pb.sawdust.tensor.decorators.concurrent.primitive.size;

import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.concurrent.size.ConcurrentD3TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.CharTensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdCharTensor;

import com.pb.sawdust.tensor.decorators.primitive.size.CharD3Tensor;
import com.pb.sawdust.util.array.CharTypeSafeArray;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentCharD3TensorShell} class provides a wrapper for implementations of the {@code CharD3Tensor} interface
 * with support for concurrent access. The locking policy is set by the {@code D3ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Dec 14, 2009 12:35:26 PM
 */
public class ConcurrentCharD3TensorShell extends ConcurrentD3TensorShell<Character> implements CharD3Tensor {
    private final CharD3Tensor tensor;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentD3TensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentCharD3TensorShell(CharD3Tensor tensor, ConcurrentD3TensorLocks locks) {
        super(tensor,locks);
        this.tensor = tensor;
    }
    
    public char getCell(int d0index, int d1index, int d2index) {
        Lock lock = locks.getReadLock(d0index,d1index,d2index);
        lock.lock();
        try {
            return tensor.getCell(d0index,d1index,d2index);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(char value, int d0index, int d1index, int d2index) {
        Lock lock = locks.getWriteLock(d0index,d1index,d2index);
        lock.lock();
        try {
            tensor.setCell(value,d0index,d1index,d2index);
        } finally {
            lock.unlock();
        }
    }

    public char getCell(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getReadLock(indices[0],indices[1],indices[2]);
        lock.lock();
        try {
            return tensor.getCell(indices[0],indices[1],indices[2]);
        } finally {
            lock.unlock();
        }
    }

    public void setCell(char value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getWriteLock(indices[0],indices[1],indices[2]);
        lock.lock();
        try {
            tensor.setCell(value,indices[0],indices[1],indices[2]);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(CharTypeSafeArray valuesArray) {
        Lock lock = locks.getTensorWriteLock();
        lock.lock();
        try {
            tensor.setTensorValues(valuesArray);
        } finally {
            lock.unlock();
        }
    }

    public CharTypeSafeArray getTensorValues() {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues();
        } finally {
            lock.unlock();
        }
    }               

    public CharTypeSafeArray getTensorValues(Class<Character> type) {
        Lock lock = locks.getTensorReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues(type);
        } finally {
            lock.unlock();
        }
    }

    protected CharTensor getComposedTensor(Index<?> index) {
        return TensorImplUtil.getComposedTensor(this,index); 
    }

    public <I> IdCharTensor<I> getReferenceTensor(Index<I> index) {
        return (IdCharTensor<I>) super.getReferenceTensor(index);
    }
}