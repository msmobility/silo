package com.pb.sawdust.tensor.decorators.concurrent.primitive.size;

import com.pb.sawdust.tensor.TensorImplUtil;
import com.pb.sawdust.tensor.alias.scalar.primitive.ByteScalar;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.concurrent.size.ConcurrentD0TensorShell;
import com.pb.sawdust.tensor.decorators.primitive.ByteTensor;
import com.pb.sawdust.tensor.decorators.primitive.size.ByteD0Tensor;
import com.pb.sawdust.tensor.decorators.id.primitive.IdByteTensor;
import com.pb.sawdust.util.array.ByteTypeSafeArray;

import java.util.concurrent.locks.Lock;

/**
 * The {@code ConcurrentByteD0TensorShell} class provides a wrapper for implementations of the {@code ByteScalar} interface
 * with support for concurrent access. The locking policy is set by the {@code D0ConcurrentTensorLocks} implementation used
 * in the class.
 *
 * @author crf <br/>
 *         Started: January 30, 2009 10:47:31 PM
 *         Revised: Jun 16, 2009 3:17:19 PM
 */
public class ConcurrentByteD0TensorShell extends ConcurrentD0TensorShell<Byte> implements ByteScalar {
    private final ByteD0Tensor tensor;

    /**
     * Constructor specifying the tensor to wrap and the concurrency policy used for locking the tensor.
     *
     * @param tensor
     *        The tensor to wrap.
     *
     * @param locks
     *        The {@code ConcurrentD0TensorLocks} instance holding the concurrency policy used when locking the tensor.
     */
    public ConcurrentByteD0TensorShell(ByteD0Tensor tensor, ConcurrentD0TensorLocks locks) {
        super(tensor,locks);
        this.tensor = tensor;
    }
    
    public byte getCell() {
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getCell();
        } finally {
            lock.unlock();
        }
    }

    public void setCell(byte value) {
        Lock lock = locks.getWriteLock();
        lock.lock();
        try {
            tensor.setCell(value);
        } finally {
            lock.unlock();
        }
    }

    public byte getCell(int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getCell();
        } finally {
            lock.unlock();
        }
    }

    public void setCell(byte value, int ... indices) {
        TensorImplUtil.checkIndicesLength(this,indices);
        Lock lock = locks.getWriteLock();
        lock.lock();
        try {
            tensor.setCell(value);
        } finally {
            lock.unlock();
        }
    }

    public void setTensorValues(ByteTypeSafeArray valuesArray) {
        Lock lock = locks.getWriteLock();
        lock.lock();
        try {
            tensor.setTensorValues(valuesArray);
        } finally {
            lock.unlock();
        }
    }

    public ByteTypeSafeArray getTensorValues() {
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues();
        } finally {
            lock.unlock();
        }
    }               

    public ByteTypeSafeArray getTensorValues(Class<Byte> type) {
        Lock lock = locks.getReadLock();
        lock.lock();
        try {
            return tensor.getTensorValues(type);
        } finally {
            lock.unlock();
        }
    }

    protected ByteTensor getComposedTensor(Index index) {
        return TensorImplUtil.getComposedTensor(this,index);
    }

    public <I> IdByteTensor<I> getReferenceTensor(Index<I> index) {
        return (IdByteTensor<I>) super.getReferenceTensor(index);
    }
}