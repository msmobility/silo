package com.pb.sawdust.tensor.factory;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.decorators.id.primitive.*;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.read.TensorReader;

import java.util.List;

/**
 * The {@code WrappedConcurrentTensorFactory} wraps a {@code ConcurrentTensorFactory} and delegates all method calls to it.
 * This class is useful for developing tensor factories which need to override a small subset of the factory functionality/contract.
 *
 * @author crf
 *         Started 10/18/11 10:35 AM
 */
public class WrappedConcurrentTensorFactory implements ConcurrentTensorFactory {
    private final ConcurrentTensorFactory factory;

    /**
     * Constructor specifying the factory to wrap.
     *
     * @param factory
     *        The wrapped factory.
     */
    public WrappedConcurrentTensorFactory(ConcurrentTensorFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public ByteTensor concurrentByteTensor(int concurrencyLevel, int... dimensions) {
        return factory.concurrentByteTensor(concurrencyLevel,dimensions);
    }

    @Override
    public ByteTensor initializedConcurrentByteTensor(byte defaultValue, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentByteTensor(defaultValue,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdByteTensor<I> concurrentByteTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentByteTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdByteTensor<I> initializedConcurrentByteTensor(byte defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentByteTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdByteTensor<I> concurrentByteTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentByteTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdByteTensor<I> initializedConcurrentByteTensor(byte defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentByteTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public ShortTensor concurrentShortTensor(int concurrencyLevel, int... dimensions) {
        return factory.concurrentShortTensor(concurrencyLevel,dimensions);
    }

    @Override
    public ShortTensor initializedConcurrentShortTensor(short defaultValue, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentShortTensor(defaultValue,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdShortTensor<I> concurrentShortTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentShortTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdShortTensor<I> initializedConcurrentShortTensor(short defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentShortTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdShortTensor<I> concurrentShortTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentShortTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdShortTensor<I> initializedConcurrentShortTensor(short defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentShortTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public IntTensor concurrentIntTensor(int concurrencyLevel, int... dimensions) {
        return factory.concurrentIntTensor(concurrencyLevel,dimensions);
    }

    @Override
    public IntTensor initializedConcurrentIntTensor(int defaultValue, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentIntTensor(defaultValue,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdIntTensor<I> concurrentIntTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentIntTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdIntTensor<I> initializedConcurrentIntTensor(int defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentIntTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdIntTensor<I> concurrentIntTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentIntTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdIntTensor<I> initializedConcurrentIntTensor(int defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentIntTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public LongTensor concurrentLongTensor(int concurrencyLevel, int... dimensions) {
        return factory.concurrentLongTensor(concurrencyLevel,dimensions);
    }

    @Override
    public LongTensor initializedConcurrentLongTensor(long defaultValue, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentLongTensor(defaultValue,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdLongTensor<I> concurrentLongTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentLongTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdLongTensor<I> initializedConcurrentLongTensor(long defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentLongTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdLongTensor<I> concurrentLongTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentLongTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdLongTensor<I> initializedConcurrentLongTensor(long defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentLongTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public FloatTensor concurrentFloatTensor(int concurrencyLevel, int... dimensions) {
        return factory.concurrentFloatTensor(concurrencyLevel,dimensions);
    }

    @Override
    public FloatTensor initializedConcurrentFloatTensor(float defaultValue, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentFloatTensor(defaultValue,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdFloatTensor<I> concurrentFloatTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentFloatTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdFloatTensor<I> initializedConcurrentFloatTensor(float defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentFloatTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdFloatTensor<I> concurrentFloatTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentFloatTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdFloatTensor<I> initializedConcurrentFloatTensor(float defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentFloatTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public DoubleTensor concurrentDoubleTensor(int concurrencyLevel, int... dimensions) {
        return factory.concurrentDoubleTensor(concurrencyLevel,dimensions);
    }

    @Override
    public DoubleTensor initializedConcurrentDoubleTensor(double defaultValue, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentDoubleTensor(defaultValue,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdDoubleTensor<I> concurrentDoubleTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentDoubleTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdDoubleTensor<I> initializedConcurrentDoubleTensor(double defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentDoubleTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdDoubleTensor<I> concurrentDoubleTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentDoubleTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdDoubleTensor<I> initializedConcurrentDoubleTensor(double defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentDoubleTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public CharTensor concurrentCharTensor(int concurrencyLevel, int... dimensions) {
        return factory.concurrentCharTensor(concurrencyLevel,dimensions);
    }

    @Override
    public CharTensor initializedConcurrentCharTensor(char defaultValue, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentCharTensor(defaultValue,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdCharTensor<I> concurrentCharTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentCharTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdCharTensor<I> initializedConcurrentCharTensor(char defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentCharTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdCharTensor<I> concurrentCharTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentCharTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdCharTensor<I> initializedConcurrentCharTensor(char defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentCharTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public BooleanTensor concurrentBooleanTensor(int concurrencyLevel, int... dimensions) {
        return factory.concurrentBooleanTensor(concurrencyLevel,dimensions);
    }

    @Override
    public BooleanTensor initializedConcurrentBooleanTensor(boolean defaultValue, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentBooleanTensor(defaultValue,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdBooleanTensor<I> concurrentBooleanTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentBooleanTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdBooleanTensor<I> initializedConcurrentBooleanTensor(boolean defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentBooleanTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdBooleanTensor<I> concurrentBooleanTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentBooleanTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <I> IdBooleanTensor<I> initializedConcurrentBooleanTensor(boolean defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentBooleanTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <T> Tensor<T> copyTensor(Tensor<T> tensor, int concurrencyLevel) {
        return factory.copyTensor(tensor,concurrencyLevel);
    }

    @Override
    public <T,I> IdTensor<T,I> copyTensor(IdTensor<T,I> tensor, int concurrencyLevel) {
        return factory.copyTensor(tensor,concurrencyLevel);
    }

    @Override
    public <T> Tensor<T> concurrentTensor(int concurrencyLevel, int... dimensions) {
        return factory.concurrentTensor(concurrencyLevel,dimensions);
    }

    @Override
    public <T> Tensor<T> initializedConcurrentTensor(T defaultValue, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentTensor(defaultValue,concurrencyLevel,dimensions);
    }

    @Override
    public <T,I> IdTensor<T,I> concurrentTensor(List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <T,I> IdTensor<T,I> initializedConcurrentTensor(T defaultValue, List<List<I>> ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <T,I> IdTensor<T,I> concurrentTensor(I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.concurrentTensor(ids,concurrencyLevel,dimensions);
    }

    @Override
    public <T,I> IdTensor<T,I> initializedConcurrentTensor(T defaultValue, I[][] ids, int concurrencyLevel, int... dimensions) {
        return factory.initializedConcurrentTensor(defaultValue,ids,concurrencyLevel,dimensions);
    }

    @Override
    public <T,I> Tensor<T> concurrentTensor(TensorReader<T,I> reader, int concurrencyLevel) {
        return factory.concurrentTensor(reader,concurrencyLevel);
    }
}
