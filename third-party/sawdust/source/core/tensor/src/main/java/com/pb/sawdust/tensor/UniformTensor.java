package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.decorators.primitive.size.*;
import com.pb.sawdust.tensor.decorators.size.*; 
import com.pb.sawdust.tensor.decorators.id.*;
import com.pb.sawdust.tensor.factory.AbstractTensorFactory;
import com.pb.sawdust.tensor.read.TensorReader;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.tensor.alias.scalar.impl.*;

/**
 * The {@code UniformTensor} is used to construct tensors which hold the same value in all cells. The tensors returned
 * by this class are immutable, so any {@code set} calls will throw {@code UnsupportedOperationException}s. Also, the
 * factory methods to copy tensors and read tensors from a reader are not supported, as there is no way to verify (at
 * compile time) that the values from the source tensor/reader will be uniform.
 *
 * @author crf <br/>
 *         Started Oct 1, 2010 10:05:54 AM
 */
public final class UniformTensor extends AbstractTensorFactory {
    private UniformTensor() {}
    
    private static final UniformTensor factory = new UniformTensor();
    
    /**
     Method to get an array tensor factory.
     
     @return a {@code UniformTensor} instance.
     */
    public static UniformTensor getFactory() {
        return factory;
    }
    

    public ByteTensor byteTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new ByteScalarImpl());
            case 1 : return new ByteD1UniformTensor(new ByteUniformTensor(dimensions));
            case 2 : return new ByteD2UniformTensor(new ByteUniformTensor(dimensions));
            case 3 : return new ByteD3UniformTensor(new ByteUniformTensor(dimensions));
            case 4 : return new ByteD4UniformTensor(new ByteUniformTensor(dimensions));
            case 5 : return new ByteD5UniformTensor(new ByteUniformTensor(dimensions));
            case 6 : return new ByteD6UniformTensor(new ByteUniformTensor(dimensions));
            case 7 : return new ByteD7UniformTensor(new ByteUniformTensor(dimensions));
            case 8 : return new ByteD8UniformTensor(new ByteUniformTensor(dimensions));
            case 9 : return new ByteD9UniformTensor(new ByteUniformTensor(dimensions));
            default : return new ByteUniformTensor(dimensions);
        }
    }

    public ByteTensor initializedByteTensor(byte defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new ByteScalarImpl(defaultValue));
            case 1 : return new ByteD1UniformTensor(new ByteUniformTensor(true,defaultValue,dimensions));
            case 2 : return new ByteD2UniformTensor(new ByteUniformTensor(true,defaultValue,dimensions));
            case 3 : return new ByteD3UniformTensor(new ByteUniformTensor(true,defaultValue,dimensions));
            case 4 : return new ByteD4UniformTensor(new ByteUniformTensor(true,defaultValue,dimensions));
            case 5 : return new ByteD5UniformTensor(new ByteUniformTensor(true,defaultValue,dimensions));
            case 6 : return new ByteD6UniformTensor(new ByteUniformTensor(true,defaultValue,dimensions));
            case 7 : return new ByteD7UniformTensor(new ByteUniformTensor(true,defaultValue,dimensions));
            case 8 : return new ByteD8UniformTensor(new ByteUniformTensor(true,defaultValue,dimensions));
            case 9 : return new ByteD9UniformTensor(new ByteUniformTensor(true,defaultValue,dimensions));
            default : return new ByteUniformTensor(true,defaultValue,dimensions);
        }
    }

    public ShortTensor shortTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new ShortScalarImpl());
            case 1 : return new ShortD1UniformTensor(new ShortUniformTensor(dimensions));
            case 2 : return new ShortD2UniformTensor(new ShortUniformTensor(dimensions));
            case 3 : return new ShortD3UniformTensor(new ShortUniformTensor(dimensions));
            case 4 : return new ShortD4UniformTensor(new ShortUniformTensor(dimensions));
            case 5 : return new ShortD5UniformTensor(new ShortUniformTensor(dimensions));
            case 6 : return new ShortD6UniformTensor(new ShortUniformTensor(dimensions));
            case 7 : return new ShortD7UniformTensor(new ShortUniformTensor(dimensions));
            case 8 : return new ShortD8UniformTensor(new ShortUniformTensor(dimensions));
            case 9 : return new ShortD9UniformTensor(new ShortUniformTensor(dimensions));
            default : return new ShortUniformTensor(dimensions);
        }
    }
    public ShortTensor initializedShortTensor(short defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new ShortScalarImpl(defaultValue));
            case 1 : return new ShortD1UniformTensor(new ShortUniformTensor(true,defaultValue,dimensions));
            case 2 : return new ShortD2UniformTensor(new ShortUniformTensor(true,defaultValue,dimensions));
            case 3 : return new ShortD3UniformTensor(new ShortUniformTensor(true,defaultValue,dimensions));
            case 4 : return new ShortD4UniformTensor(new ShortUniformTensor(true,defaultValue,dimensions));
            case 5 : return new ShortD5UniformTensor(new ShortUniformTensor(true,defaultValue,dimensions));
            case 6 : return new ShortD6UniformTensor(new ShortUniformTensor(true,defaultValue,dimensions));
            case 7 : return new ShortD7UniformTensor(new ShortUniformTensor(true,defaultValue,dimensions));
            case 8 : return new ShortD8UniformTensor(new ShortUniformTensor(true,defaultValue,dimensions));
            case 9 : return new ShortD9UniformTensor(new ShortUniformTensor(true,defaultValue,dimensions));
            default : return new ShortUniformTensor(true,defaultValue,dimensions);
        }
    }

    public IntTensor intTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new IntScalarImpl());
            case 1 : return new IntD1UniformTensor(new IntUniformTensor(dimensions));
            case 2 : return new IntD2UniformTensor(new IntUniformTensor(dimensions));
            case 3 : return new IntD3UniformTensor(new IntUniformTensor(dimensions));
            case 4 : return new IntD4UniformTensor(new IntUniformTensor(dimensions));
            case 5 : return new IntD5UniformTensor(new IntUniformTensor(dimensions));
            case 6 : return new IntD6UniformTensor(new IntUniformTensor(dimensions));
            case 7 : return new IntD7UniformTensor(new IntUniformTensor(dimensions));
            case 8 : return new IntD8UniformTensor(new IntUniformTensor(dimensions));
            case 9 : return new IntD9UniformTensor(new IntUniformTensor(dimensions));
            default : return new IntUniformTensor(dimensions);
        }
    }
    public IntTensor initializedIntTensor(int defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new IntScalarImpl(defaultValue));
            case 1 : return new IntD1UniformTensor(new IntUniformTensor(true,defaultValue,dimensions));
            case 2 : return new IntD2UniformTensor(new IntUniformTensor(true,defaultValue,dimensions));
            case 3 : return new IntD3UniformTensor(new IntUniformTensor(true,defaultValue,dimensions));
            case 4 : return new IntD4UniformTensor(new IntUniformTensor(true,defaultValue,dimensions));
            case 5 : return new IntD5UniformTensor(new IntUniformTensor(true,defaultValue,dimensions));
            case 6 : return new IntD6UniformTensor(new IntUniformTensor(true,defaultValue,dimensions));
            case 7 : return new IntD7UniformTensor(new IntUniformTensor(true,defaultValue,dimensions));
            case 8 : return new IntD8UniformTensor(new IntUniformTensor(true,defaultValue,dimensions));
            case 9 : return new IntD9UniformTensor(new IntUniformTensor(true,defaultValue,dimensions));
            default : return new IntUniformTensor(true,defaultValue,dimensions);
        }
    }

    public LongTensor longTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new LongScalarImpl());
            case 1 : return new LongD1UniformTensor(new LongUniformTensor(dimensions));
            case 2 : return new LongD2UniformTensor(new LongUniformTensor(dimensions));
            case 3 : return new LongD3UniformTensor(new LongUniformTensor(dimensions));
            case 4 : return new LongD4UniformTensor(new LongUniformTensor(dimensions));
            case 5 : return new LongD5UniformTensor(new LongUniformTensor(dimensions));
            case 6 : return new LongD6UniformTensor(new LongUniformTensor(dimensions));
            case 7 : return new LongD7UniformTensor(new LongUniformTensor(dimensions));
            case 8 : return new LongD8UniformTensor(new LongUniformTensor(dimensions));
            case 9 : return new LongD9UniformTensor(new LongUniformTensor(dimensions));
            default : return new LongUniformTensor(dimensions);
        }
    }
    public LongTensor initializedLongTensor(long defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new LongScalarImpl(defaultValue));
            case 1 : return new LongD1UniformTensor(new LongUniformTensor(true,defaultValue,dimensions));
            case 2 : return new LongD2UniformTensor(new LongUniformTensor(true,defaultValue,dimensions));
            case 3 : return new LongD3UniformTensor(new LongUniformTensor(true,defaultValue,dimensions));
            case 4 : return new LongD4UniformTensor(new LongUniformTensor(true,defaultValue,dimensions));
            case 5 : return new LongD5UniformTensor(new LongUniformTensor(true,defaultValue,dimensions));
            case 6 : return new LongD6UniformTensor(new LongUniformTensor(true,defaultValue,dimensions));
            case 7 : return new LongD7UniformTensor(new LongUniformTensor(true,defaultValue,dimensions));
            case 8 : return new LongD8UniformTensor(new LongUniformTensor(true,defaultValue,dimensions));
            case 9 : return new LongD9UniformTensor(new LongUniformTensor(true,defaultValue,dimensions));
            default : return new LongUniformTensor(true,defaultValue,dimensions);
        }
    }

    public FloatTensor floatTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new FloatScalarImpl());
            case 1 : return new FloatD1UniformTensor(new FloatUniformTensor(dimensions));
            case 2 : return new FloatD2UniformTensor(new FloatUniformTensor(dimensions));
            case 3 : return new FloatD3UniformTensor(new FloatUniformTensor(dimensions));
            case 4 : return new FloatD4UniformTensor(new FloatUniformTensor(dimensions));
            case 5 : return new FloatD5UniformTensor(new FloatUniformTensor(dimensions));
            case 6 : return new FloatD6UniformTensor(new FloatUniformTensor(dimensions));
            case 7 : return new FloatD7UniformTensor(new FloatUniformTensor(dimensions));
            case 8 : return new FloatD8UniformTensor(new FloatUniformTensor(dimensions));
            case 9 : return new FloatD9UniformTensor(new FloatUniformTensor(dimensions));
            default : return new FloatUniformTensor(dimensions);
        }
    }
    public FloatTensor initializedFloatTensor(float defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new FloatScalarImpl(defaultValue));
            case 1 : return new FloatD1UniformTensor(new FloatUniformTensor(true,defaultValue,dimensions));
            case 2 : return new FloatD2UniformTensor(new FloatUniformTensor(true,defaultValue,dimensions));
            case 3 : return new FloatD3UniformTensor(new FloatUniformTensor(true,defaultValue,dimensions));
            case 4 : return new FloatD4UniformTensor(new FloatUniformTensor(true,defaultValue,dimensions));
            case 5 : return new FloatD5UniformTensor(new FloatUniformTensor(true,defaultValue,dimensions));
            case 6 : return new FloatD6UniformTensor(new FloatUniformTensor(true,defaultValue,dimensions));
            case 7 : return new FloatD7UniformTensor(new FloatUniformTensor(true,defaultValue,dimensions));
            case 8 : return new FloatD8UniformTensor(new FloatUniformTensor(true,defaultValue,dimensions));
            case 9 : return new FloatD9UniformTensor(new FloatUniformTensor(true,defaultValue,dimensions));
            default : return new FloatUniformTensor(true,defaultValue,dimensions);
        }
    }

    public DoubleTensor doubleTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new DoubleScalarImpl());
            case 1 : return new DoubleD1UniformTensor(new DoubleUniformTensor(dimensions));
            case 2 : return new DoubleD2UniformTensor(new DoubleUniformTensor(dimensions));
            case 3 : return new DoubleD3UniformTensor(new DoubleUniformTensor(dimensions));
            case 4 : return new DoubleD4UniformTensor(new DoubleUniformTensor(dimensions));
            case 5 : return new DoubleD5UniformTensor(new DoubleUniformTensor(dimensions));
            case 6 : return new DoubleD6UniformTensor(new DoubleUniformTensor(dimensions));
            case 7 : return new DoubleD7UniformTensor(new DoubleUniformTensor(dimensions));
            case 8 : return new DoubleD8UniformTensor(new DoubleUniformTensor(dimensions));
            case 9 : return new DoubleD9UniformTensor(new DoubleUniformTensor(dimensions));
            default : return new DoubleUniformTensor(dimensions);
        }
    }
    public DoubleTensor initializedDoubleTensor(double defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new DoubleScalarImpl(defaultValue));
            case 1 : return new DoubleD1UniformTensor(new DoubleUniformTensor(true,defaultValue,dimensions));
            case 2 : return new DoubleD2UniformTensor(new DoubleUniformTensor(true,defaultValue,dimensions));
            case 3 : return new DoubleD3UniformTensor(new DoubleUniformTensor(true,defaultValue,dimensions));
            case 4 : return new DoubleD4UniformTensor(new DoubleUniformTensor(true,defaultValue,dimensions));
            case 5 : return new DoubleD5UniformTensor(new DoubleUniformTensor(true,defaultValue,dimensions));
            case 6 : return new DoubleD6UniformTensor(new DoubleUniformTensor(true,defaultValue,dimensions));
            case 7 : return new DoubleD7UniformTensor(new DoubleUniformTensor(true,defaultValue,dimensions));
            case 8 : return new DoubleD8UniformTensor(new DoubleUniformTensor(true,defaultValue,dimensions));
            case 9 : return new DoubleD9UniformTensor(new DoubleUniformTensor(true,defaultValue,dimensions));
            default : return new DoubleUniformTensor(true,defaultValue,dimensions);
        }
    }

    public CharTensor charTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new CharScalarImpl());
            case 1 : return new CharD1UniformTensor(new CharUniformTensor(dimensions));
            case 2 : return new CharD2UniformTensor(new CharUniformTensor(dimensions));
            case 3 : return new CharD3UniformTensor(new CharUniformTensor(dimensions));
            case 4 : return new CharD4UniformTensor(new CharUniformTensor(dimensions));
            case 5 : return new CharD5UniformTensor(new CharUniformTensor(dimensions));
            case 6 : return new CharD6UniformTensor(new CharUniformTensor(dimensions));
            case 7 : return new CharD7UniformTensor(new CharUniformTensor(dimensions));
            case 8 : return new CharD8UniformTensor(new CharUniformTensor(dimensions));
            case 9 : return new CharD9UniformTensor(new CharUniformTensor(dimensions));
            default : return new CharUniformTensor(dimensions);
        }
    }
    public CharTensor initializedCharTensor(char defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new CharScalarImpl(defaultValue));
            case 1 : return new CharD1UniformTensor(new CharUniformTensor(true,defaultValue,dimensions));
            case 2 : return new CharD2UniformTensor(new CharUniformTensor(true,defaultValue,dimensions));
            case 3 : return new CharD3UniformTensor(new CharUniformTensor(true,defaultValue,dimensions));
            case 4 : return new CharD4UniformTensor(new CharUniformTensor(true,defaultValue,dimensions));
            case 5 : return new CharD5UniformTensor(new CharUniformTensor(true,defaultValue,dimensions));
            case 6 : return new CharD6UniformTensor(new CharUniformTensor(true,defaultValue,dimensions));
            case 7 : return new CharD7UniformTensor(new CharUniformTensor(true,defaultValue,dimensions));
            case 8 : return new CharD8UniformTensor(new CharUniformTensor(true,defaultValue,dimensions));
            case 9 : return new CharD9UniformTensor(new CharUniformTensor(true,defaultValue,dimensions));
            default : return new CharUniformTensor(true,defaultValue,dimensions);
        }
    }

    public BooleanTensor booleanTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new BooleanScalarImpl());
            case 1 : return new BooleanD1UniformTensor(new BooleanUniformTensor(dimensions));
            case 2 : return new BooleanD2UniformTensor(new BooleanUniformTensor(dimensions));
            case 3 : return new BooleanD3UniformTensor(new BooleanUniformTensor(dimensions));
            case 4 : return new BooleanD4UniformTensor(new BooleanUniformTensor(dimensions));
            case 5 : return new BooleanD5UniformTensor(new BooleanUniformTensor(dimensions));
            case 6 : return new BooleanD6UniformTensor(new BooleanUniformTensor(dimensions));
            case 7 : return new BooleanD7UniformTensor(new BooleanUniformTensor(dimensions));
            case 8 : return new BooleanD8UniformTensor(new BooleanUniformTensor(dimensions));
            case 9 : return new BooleanD9UniformTensor(new BooleanUniformTensor(dimensions));
            default : return new BooleanUniformTensor(dimensions);
        }
    }
    public BooleanTensor initializedBooleanTensor(boolean defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new BooleanScalarImpl(defaultValue));
            case 1 : return new BooleanD1UniformTensor(new BooleanUniformTensor(true,defaultValue,dimensions));
            case 2 : return new BooleanD2UniformTensor(new BooleanUniformTensor(true,defaultValue,dimensions));
            case 3 : return new BooleanD3UniformTensor(new BooleanUniformTensor(true,defaultValue,dimensions));
            case 4 : return new BooleanD4UniformTensor(new BooleanUniformTensor(true,defaultValue,dimensions));
            case 5 : return new BooleanD5UniformTensor(new BooleanUniformTensor(true,defaultValue,dimensions));
            case 6 : return new BooleanD6UniformTensor(new BooleanUniformTensor(true,defaultValue,dimensions));
            case 7 : return new BooleanD7UniformTensor(new BooleanUniformTensor(true,defaultValue,dimensions));
            case 8 : return new BooleanD8UniformTensor(new BooleanUniformTensor(true,defaultValue,dimensions));
            case 9 : return new BooleanD9UniformTensor(new BooleanUniformTensor(true,defaultValue,dimensions));
            default : return new BooleanUniformTensor(true,defaultValue,dimensions);
        }
    }

    public <T> Tensor<T> tensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new ScalarImpl<T>()); 
            case 1 : return new ObjectD1UniformTensor<T>(new ObjectUniformTensor<T>(dimensions)); 
            case 2 : return new ObjectD2UniformTensor<T>(new ObjectUniformTensor<T>(dimensions)); 
            case 3 : return new ObjectD3UniformTensor<T>(new ObjectUniformTensor<T>(dimensions)); 
            case 4 : return new ObjectD4UniformTensor<T>(new ObjectUniformTensor<T>(dimensions)); 
            case 5 : return new ObjectD5UniformTensor<T>(new ObjectUniformTensor<T>(dimensions)); 
            case 6 : return new ObjectD6UniformTensor<T>(new ObjectUniformTensor<T>(dimensions)); 
            case 7 : return new ObjectD7UniformTensor<T>(new ObjectUniformTensor<T>(dimensions)); 
            case 8 : return new ObjectD8UniformTensor<T>(new ObjectUniformTensor<T>(dimensions)); 
            case 9 : return new ObjectD9UniformTensor<T>(new ObjectUniformTensor<T>(dimensions));
            default : return new ObjectUniformTensor<T>(dimensions);
        }
    }
    
    public <T> Tensor<T> initializedTensor(T defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return UnmodifiableTensor.unmodifiableTensor(new ScalarImpl<T>(defaultValue)); 
            case 1 : return new ObjectD1UniformTensor<T>(new ObjectUniformTensor<T>(true,defaultValue,dimensions)); 
            case 2 : return new ObjectD2UniformTensor<T>(new ObjectUniformTensor<T>(true,defaultValue,dimensions)); 
            case 3 : return new ObjectD3UniformTensor<T>(new ObjectUniformTensor<T>(true,defaultValue,dimensions)); 
            case 4 : return new ObjectD4UniformTensor<T>(new ObjectUniformTensor<T>(true,defaultValue,dimensions)); 
            case 5 : return new ObjectD5UniformTensor<T>(new ObjectUniformTensor<T>(true,defaultValue,dimensions)); 
            case 6 : return new ObjectD6UniformTensor<T>(new ObjectUniformTensor<T>(true,defaultValue,dimensions)); 
            case 7 : return new ObjectD7UniformTensor<T>(new ObjectUniformTensor<T>(true,defaultValue,dimensions)); 
            case 8 : return new ObjectD8UniformTensor<T>(new ObjectUniformTensor<T>(true,defaultValue,dimensions)); 
            case 9 : return new ObjectD9UniformTensor<T>(new ObjectUniformTensor<T>(true,defaultValue,dimensions));
            default : return new ObjectUniformTensor<T>(true,defaultValue,dimensions);
        }
    }

    /**
     * Since there is no way to verify the reader with produce uniform values, this method is not supported.
     *
     * @throws UnsupportedOperationException always
    **/
    public <T, I> Tensor<T> concurrentTensor(TensorReader<T, I> reader, int concurrencyLevel) {
        throw new UnsupportedOperationException("Cannot load a uniform tensor from a tensor reader.");
    }

    /**
     * Since there is no way to verify the reader with produce uniform values, this method is not supported.
     *
     * @throws UnsupportedOperationException always
    **/
    public <T, I> Tensor<T> tensor(TensorReader<T, I> reader) {
        throw new UnsupportedOperationException("Cannot load a uniform tensor from a tensor reader.");
    }

    /**
     * Since there is no way to verify {@code tensor} is uniform, this method is not supported.
     *
     * @throws UnsupportedOperationException always
    **/
    public <T> Tensor<T> copyTensor(Tensor<T> tensor) {
        throw new UnsupportedOperationException("Cannot copy a tensor to a uniform tensor.");
    }

    /**
     * Since there is no way to verify {@code tensor} is uniform, this method is not supported.
     *
     * @throws UnsupportedOperationException always
    **/
    public <T,I> IdTensor<T,I> copyTensor(IdTensor<T,I> tensor) {
        throw new UnsupportedOperationException("Cannot copy a tensor to a uniform tensor.");
    }

    /**
     * Since there is no way to verify {@code tensor} is uniform, this method is not supported.
     *
     * @throws UnsupportedOperationException always
    **/
    @SuppressWarnings("unchecked") //input tensor already paramaterized by <T,I>, so output will be valid
    public <T> Tensor<T> copyTensor(Tensor<T> tensor, int concurrencyLevel) {
        throw new UnsupportedOperationException("Cannot copy a tensor to a uniform tensor.");
    }

    /**
     * Since there is no way to verify {@code tensor} is uniform, this method is not supported.
     *
     * @throws UnsupportedOperationException always
    **/
    @SuppressWarnings("unchecked") //input tensor already paramaterized by <T,I>, so output will be valid
    public <T,I> IdTensor<T,I> copyTensor(IdTensor<T,I> tensor, int concurrencyLevel) {
        throw new UnsupportedOperationException("Cannot copy a tensor to a uniform tensor.");
    }
 
    private static class ByteUniformTensor extends AbstractByteTensor {
        private final byte value;
    
        private ByteUniformTensor(int ... dimensions) {
            this(true,(byte) 0,dimensions);
        }
    
        private ByteUniformTensor(boolean defaultIndicator, byte defaultValue, int ... dimensions) {
            super(dimensions);
            value = defaultValue;
        }
    
        public byte getCell(int ... indices) {
            return value;
        }
        
        public void setCell(byte value, int ... indices) {
            throw new UnsupportedOperationException("uniform tensor is unmodifiable.");
        }
    }
 
    private static class ShortUniformTensor extends AbstractShortTensor {
        private final short value;
    
        private ShortUniformTensor(int ... dimensions) {
            this(true,(short) 0,dimensions);
        }
    
        private ShortUniformTensor(boolean defaultIndicator, short defaultValue, int ... dimensions) {
            super(dimensions);
            value = defaultValue;
        }
    
        public short getCell(int ... indices) {
            return value;
        }
        
        public void setCell(short value, int ... indices) {
            throw new UnsupportedOperationException("uniform tensor is unmodifiable.");
        }
    }
 
    private static class IntUniformTensor extends AbstractIntTensor {
        private final int value;
    
        private IntUniformTensor(int ... dimensions) {
            this(true,0,dimensions);
        }
    
        private IntUniformTensor(boolean defaultIndicator, int defaultValue, int ... dimensions) {
            super(dimensions);
            value = defaultValue;
        }
    
        public int getCell(int ... indices) {
            return value;
        }
        
        public void setCell(int value, int ... indices) {
            throw new UnsupportedOperationException("uniform tensor is unmodifiable.");
        }
    }
 
    private static class LongUniformTensor extends AbstractLongTensor {
        private final long value;
    
        private LongUniformTensor(int ... dimensions) {
            this(true,0L,dimensions);
        }
    
        private LongUniformTensor(boolean defaultIndicator, long defaultValue, int ... dimensions) {
            super(dimensions);
            value = defaultValue;
        }
    
        public long getCell(int ... indices) {
            return value;
        }
        
        public void setCell(long value, int ... indices) {
            throw new UnsupportedOperationException("uniform tensor is unmodifiable.");
        }
    }
 
    private static class FloatUniformTensor extends AbstractFloatTensor {
        private final float value;
    
        private FloatUniformTensor(int ... dimensions) {
            this(true,0.0f,dimensions);
        }
    
        private FloatUniformTensor(boolean defaultIndicator, float defaultValue, int ... dimensions) {
            super(dimensions);
            value = defaultValue;
        }
    
        public float getCell(int ... indices) {
            return value;
        }
        
        public void setCell(float value, int ... indices) {
            throw new UnsupportedOperationException("uniform tensor is unmodifiable.");
        }
    }
 
    private static class DoubleUniformTensor extends AbstractDoubleTensor {
        private final double value;
    
        private DoubleUniformTensor(int ... dimensions) {
            this(true,0.0,dimensions);
        }
    
        private DoubleUniformTensor(boolean defaultIndicator, double defaultValue, int ... dimensions) {
            super(dimensions);
            value = defaultValue;
        }
    
        public double getCell(int ... indices) {
            return value;
        }
        
        public void setCell(double value, int ... indices) {
            throw new UnsupportedOperationException("uniform tensor is unmodifiable.");
        }
    }
 
    private static class CharUniformTensor extends AbstractCharTensor {
        private final char value;
    
        private CharUniformTensor(int ... dimensions) {
            this(true,'\u0000',dimensions);
        }
    
        private CharUniformTensor(boolean defaultIndicator, char defaultValue, int ... dimensions) {
            super(dimensions);
            value = defaultValue;
        }
    
        public char getCell(int ... indices) {
            return value;
        }
        
        public void setCell(char value, int ... indices) {
            throw new UnsupportedOperationException("uniform tensor is unmodifiable.");
        }
    }
 
    private static class BooleanUniformTensor extends AbstractBooleanTensor {
        private final boolean value;
    
        private BooleanUniformTensor(int ... dimensions) {
            this(true,false,dimensions);
        }
    
        private BooleanUniformTensor(boolean defaultIndicator, boolean defaultValue, int ... dimensions) {
            super(dimensions);
            value = defaultValue;
        }
    
        public boolean getCell(int ... indices) {
            return value;
        }
        
        public void setCell(boolean value, int ... indices) {
            throw new UnsupportedOperationException("uniform tensor is unmodifiable.");
        }
    }
 
    private static class ObjectUniformTensor<T> extends AbstractTensor<T> {
        private final T value;
        
       @SuppressWarnings("unchecked") //component type Object but doesn't matter because it is only used internally - equivalent to erasure
        private ObjectUniformTensor(int ... dimensions) {
            this(true,null,dimensions);
        }
        
        private ObjectUniformTensor(boolean defaultIndicator, T defaultValue, int ... dimensions) {
            super(dimensions);
            value = defaultValue;
        }
    
        public JavaType getType() {
            return JavaType.OBJECT;
        }
    
        public T getValue(int ... indices) {
            return value;
        }
    
        public void setValue(T value,int ... indices) {
            throw new UnsupportedOperationException("uniform tensor is unmodifiable.");
        }
    }
 
    private static class ByteD1UniformTensor extends ByteD1TensorShell {
        private final ByteUniformTensor tensor;

        private ByteD1UniformTensor(ByteUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }

        public byte getCell(int index) {
            return tensor.value;
        }
    }
 
    private static class ByteD2UniformTensor extends ByteD2TensorShell {
        private final ByteUniformTensor tensor;

        private ByteD2UniformTensor(ByteUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index) {
            return tensor.value;
        }
    }
 
    private static class ByteD3UniformTensor extends ByteD3TensorShell {
        private final ByteUniformTensor tensor;

        private ByteD3UniformTensor(ByteUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index) {
            return tensor.value;
        }
    }
 
    private static class ByteD4UniformTensor extends ByteD4TensorShell {
        private final ByteUniformTensor tensor;

        private ByteD4UniformTensor(ByteUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.value;
        }
    }
 
    private static class ByteD5UniformTensor extends ByteD5TensorShell {
        private final ByteUniformTensor tensor;

        private ByteD5UniformTensor(ByteUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.value;
        }
    }
 
    private static class ByteD6UniformTensor extends ByteD6TensorShell {
        private final ByteUniformTensor tensor;

        private ByteD6UniformTensor(ByteUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.value;
        }
    }
 
    private static class ByteD7UniformTensor extends ByteD7TensorShell {
        private final ByteUniformTensor tensor;

        private ByteD7UniformTensor(ByteUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.value;
        }
    }
 
    private static class ByteD8UniformTensor extends ByteD8TensorShell {
        private final ByteUniformTensor tensor;

        private ByteD8UniformTensor(ByteUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.value;
        }
    }
 
    private static class ByteD9UniformTensor extends ByteD9TensorShell {
        private final ByteUniformTensor tensor;

        private ByteD9UniformTensor(ByteUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.value;
        }
    }
 
    private static class ShortD1UniformTensor extends ShortD1TensorShell {
        private final ShortUniformTensor tensor;

        private ShortD1UniformTensor(ShortUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }

        public short getCell(int index) {
            return tensor.value;
        }
    }
 
    private static class ShortD2UniformTensor extends ShortD2TensorShell {
        private final ShortUniformTensor tensor;

        private ShortD2UniformTensor(ShortUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index) {
            return tensor.value;
        }
    }
 
    private static class ShortD3UniformTensor extends ShortD3TensorShell {
        private final ShortUniformTensor tensor;

        private ShortD3UniformTensor(ShortUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index) {
            return tensor.value;
        }
    }
 
    private static class ShortD4UniformTensor extends ShortD4TensorShell {
        private final ShortUniformTensor tensor;

        private ShortD4UniformTensor(ShortUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.value;
        }
    }
 
    private static class ShortD5UniformTensor extends ShortD5TensorShell {
        private final ShortUniformTensor tensor;

        private ShortD5UniformTensor(ShortUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.value;
        }
    }
 
    private static class ShortD6UniformTensor extends ShortD6TensorShell {
        private final ShortUniformTensor tensor;

        private ShortD6UniformTensor(ShortUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.value;
        }
    }
 
    private static class ShortD7UniformTensor extends ShortD7TensorShell {
        private final ShortUniformTensor tensor;

        private ShortD7UniformTensor(ShortUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.value;
        }
    }
 
    private static class ShortD8UniformTensor extends ShortD8TensorShell {
        private final ShortUniformTensor tensor;

        private ShortD8UniformTensor(ShortUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.value;
        }
    }
 
    private static class ShortD9UniformTensor extends ShortD9TensorShell {
        private final ShortUniformTensor tensor;

        private ShortD9UniformTensor(ShortUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.value;
        }
    }
 
    private static class IntD1UniformTensor extends IntD1TensorShell {
        private final IntUniformTensor tensor;

        private IntD1UniformTensor(IntUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }

        public int getCell(int index) {
            return tensor.value;
        }
    }
 
    private static class IntD2UniformTensor extends IntD2TensorShell {
        private final IntUniformTensor tensor;

        private IntD2UniformTensor(IntUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index) {
            return tensor.value;
        }
    }
 
    private static class IntD3UniformTensor extends IntD3TensorShell {
        private final IntUniformTensor tensor;

        private IntD3UniformTensor(IntUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index) {
            return tensor.value;
        }
    }
 
    private static class IntD4UniformTensor extends IntD4TensorShell {
        private final IntUniformTensor tensor;

        private IntD4UniformTensor(IntUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.value;
        }
    }
 
    private static class IntD5UniformTensor extends IntD5TensorShell {
        private final IntUniformTensor tensor;

        private IntD5UniformTensor(IntUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.value;
        }
    }
 
    private static class IntD6UniformTensor extends IntD6TensorShell {
        private final IntUniformTensor tensor;

        private IntD6UniformTensor(IntUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.value;
        }
    }
 
    private static class IntD7UniformTensor extends IntD7TensorShell {
        private final IntUniformTensor tensor;

        private IntD7UniformTensor(IntUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.value;
        }
    }
 
    private static class IntD8UniformTensor extends IntD8TensorShell {
        private final IntUniformTensor tensor;

        private IntD8UniformTensor(IntUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.value;
        }
    }
 
    private static class IntD9UniformTensor extends IntD9TensorShell {
        private final IntUniformTensor tensor;

        private IntD9UniformTensor(IntUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.value;
        }
    }
 
    private static class LongD1UniformTensor extends LongD1TensorShell {
        private final LongUniformTensor tensor;

        private LongD1UniformTensor(LongUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }

        public long getCell(int index) {
            return tensor.value;
        }
    }
 
    private static class LongD2UniformTensor extends LongD2TensorShell {
        private final LongUniformTensor tensor;

        private LongD2UniformTensor(LongUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index) {
            return tensor.value;
        }
    }
 
    private static class LongD3UniformTensor extends LongD3TensorShell {
        private final LongUniformTensor tensor;

        private LongD3UniformTensor(LongUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index) {
            return tensor.value;
        }
    }
 
    private static class LongD4UniformTensor extends LongD4TensorShell {
        private final LongUniformTensor tensor;

        private LongD4UniformTensor(LongUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.value;
        }
    }
 
    private static class LongD5UniformTensor extends LongD5TensorShell {
        private final LongUniformTensor tensor;

        private LongD5UniformTensor(LongUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.value;
        }
    }
 
    private static class LongD6UniformTensor extends LongD6TensorShell {
        private final LongUniformTensor tensor;

        private LongD6UniformTensor(LongUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.value;
        }
    }
 
    private static class LongD7UniformTensor extends LongD7TensorShell {
        private final LongUniformTensor tensor;

        private LongD7UniformTensor(LongUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.value;
        }
    }
 
    private static class LongD8UniformTensor extends LongD8TensorShell {
        private final LongUniformTensor tensor;

        private LongD8UniformTensor(LongUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.value;
        }
    }
 
    private static class LongD9UniformTensor extends LongD9TensorShell {
        private final LongUniformTensor tensor;

        private LongD9UniformTensor(LongUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.value;
        }
    }
 
    private static class FloatD1UniformTensor extends FloatD1TensorShell {
        private final FloatUniformTensor tensor;

        private FloatD1UniformTensor(FloatUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }

        public float getCell(int index) {
            return tensor.value;
        }
    }
 
    private static class FloatD2UniformTensor extends FloatD2TensorShell {
        private final FloatUniformTensor tensor;

        private FloatD2UniformTensor(FloatUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index) {
            return tensor.value;
        }
    }
 
    private static class FloatD3UniformTensor extends FloatD3TensorShell {
        private final FloatUniformTensor tensor;

        private FloatD3UniformTensor(FloatUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index) {
            return tensor.value;
        }
    }
 
    private static class FloatD4UniformTensor extends FloatD4TensorShell {
        private final FloatUniformTensor tensor;

        private FloatD4UniformTensor(FloatUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.value;
        }
    }
 
    private static class FloatD5UniformTensor extends FloatD5TensorShell {
        private final FloatUniformTensor tensor;

        private FloatD5UniformTensor(FloatUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.value;
        }
    }
 
    private static class FloatD6UniformTensor extends FloatD6TensorShell {
        private final FloatUniformTensor tensor;

        private FloatD6UniformTensor(FloatUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.value;
        }
    }
 
    private static class FloatD7UniformTensor extends FloatD7TensorShell {
        private final FloatUniformTensor tensor;

        private FloatD7UniformTensor(FloatUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.value;
        }
    }
 
    private static class FloatD8UniformTensor extends FloatD8TensorShell {
        private final FloatUniformTensor tensor;

        private FloatD8UniformTensor(FloatUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.value;
        }
    }
 
    private static class FloatD9UniformTensor extends FloatD9TensorShell {
        private final FloatUniformTensor tensor;

        private FloatD9UniformTensor(FloatUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.value;
        }
    }
 
    private static class DoubleD1UniformTensor extends DoubleD1TensorShell {
        private final DoubleUniformTensor tensor;

        private DoubleD1UniformTensor(DoubleUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }

        public double getCell(int index) {
            return tensor.value;
        }
    }
 
    private static class DoubleD2UniformTensor extends DoubleD2TensorShell {
        private final DoubleUniformTensor tensor;

        private DoubleD2UniformTensor(DoubleUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index) {
            return tensor.value;
        }
    }
 
    private static class DoubleD3UniformTensor extends DoubleD3TensorShell {
        private final DoubleUniformTensor tensor;

        private DoubleD3UniformTensor(DoubleUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index) {
            return tensor.value;
        }
    }
 
    private static class DoubleD4UniformTensor extends DoubleD4TensorShell {
        private final DoubleUniformTensor tensor;

        private DoubleD4UniformTensor(DoubleUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.value;
        }
    }
 
    private static class DoubleD5UniformTensor extends DoubleD5TensorShell {
        private final DoubleUniformTensor tensor;

        private DoubleD5UniformTensor(DoubleUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.value;
        }
    }
 
    private static class DoubleD6UniformTensor extends DoubleD6TensorShell {
        private final DoubleUniformTensor tensor;

        private DoubleD6UniformTensor(DoubleUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.value;
        }
    }
 
    private static class DoubleD7UniformTensor extends DoubleD7TensorShell {
        private final DoubleUniformTensor tensor;

        private DoubleD7UniformTensor(DoubleUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.value;
        }
    }
 
    private static class DoubleD8UniformTensor extends DoubleD8TensorShell {
        private final DoubleUniformTensor tensor;

        private DoubleD8UniformTensor(DoubleUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.value;
        }
    }
 
    private static class DoubleD9UniformTensor extends DoubleD9TensorShell {
        private final DoubleUniformTensor tensor;

        private DoubleD9UniformTensor(DoubleUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.value;
        }
    }
 
    private static class CharD1UniformTensor extends CharD1TensorShell {
        private final CharUniformTensor tensor;

        private CharD1UniformTensor(CharUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }

        public char getCell(int index) {
            return tensor.value;
        }
    }
 
    private static class CharD2UniformTensor extends CharD2TensorShell {
        private final CharUniformTensor tensor;

        private CharD2UniformTensor(CharUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index) {
            return tensor.value;
        }
    }
 
    private static class CharD3UniformTensor extends CharD3TensorShell {
        private final CharUniformTensor tensor;

        private CharD3UniformTensor(CharUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index) {
            return tensor.value;
        }
    }
 
    private static class CharD4UniformTensor extends CharD4TensorShell {
        private final CharUniformTensor tensor;

        private CharD4UniformTensor(CharUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.value;
        }
    }
 
    private static class CharD5UniformTensor extends CharD5TensorShell {
        private final CharUniformTensor tensor;

        private CharD5UniformTensor(CharUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.value;
        }
    }
 
    private static class CharD6UniformTensor extends CharD6TensorShell {
        private final CharUniformTensor tensor;

        private CharD6UniformTensor(CharUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.value;
        }
    }
 
    private static class CharD7UniformTensor extends CharD7TensorShell {
        private final CharUniformTensor tensor;

        private CharD7UniformTensor(CharUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.value;
        }
    }
 
    private static class CharD8UniformTensor extends CharD8TensorShell {
        private final CharUniformTensor tensor;

        private CharD8UniformTensor(CharUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.value;
        }
    }
 
    private static class CharD9UniformTensor extends CharD9TensorShell {
        private final CharUniformTensor tensor;

        private CharD9UniformTensor(CharUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.value;
        }
    }
 
    private static class BooleanD1UniformTensor extends BooleanD1TensorShell {
        private final BooleanUniformTensor tensor;

        private BooleanD1UniformTensor(BooleanUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }

        public boolean getCell(int index) {
            return tensor.value;
        }
    }
 
    private static class BooleanD2UniformTensor extends BooleanD2TensorShell {
        private final BooleanUniformTensor tensor;

        private BooleanD2UniformTensor(BooleanUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index) {
            return tensor.value;
        }
    }
 
    private static class BooleanD3UniformTensor extends BooleanD3TensorShell {
        private final BooleanUniformTensor tensor;

        private BooleanD3UniformTensor(BooleanUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index) {
            return tensor.value;
        }
    }
 
    private static class BooleanD4UniformTensor extends BooleanD4TensorShell {
        private final BooleanUniformTensor tensor;

        private BooleanD4UniformTensor(BooleanUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.value;
        }
    }
 
    private static class BooleanD5UniformTensor extends BooleanD5TensorShell {
        private final BooleanUniformTensor tensor;

        private BooleanD5UniformTensor(BooleanUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.value;
        }
    }
 
    private static class BooleanD6UniformTensor extends BooleanD6TensorShell {
        private final BooleanUniformTensor tensor;

        private BooleanD6UniformTensor(BooleanUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.value;
        }
    }
 
    private static class BooleanD7UniformTensor extends BooleanD7TensorShell {
        private final BooleanUniformTensor tensor;

        private BooleanD7UniformTensor(BooleanUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.value;
        }
    }
 
    private static class BooleanD8UniformTensor extends BooleanD8TensorShell {
        private final BooleanUniformTensor tensor;

        private BooleanD8UniformTensor(BooleanUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.value;
        }
    }
 
    private static class BooleanD9UniformTensor extends BooleanD9TensorShell {
        private final BooleanUniformTensor tensor;

        private BooleanD9UniformTensor(BooleanUniformTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.value;
        }
    }
 
    private static class ObjectD1UniformTensor<T> extends D1TensorShell<T> {
        private final ObjectUniformTensor<T> tensor;

        private ObjectD1UniformTensor(ObjectUniformTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }

        public T getCell(int index) {
            return tensor.value;
        }
    }
 
    private static class ObjectD2UniformTensor<T> extends D2TensorShell<T> {
        private final ObjectUniformTensor<T> tensor;

        private ObjectD2UniformTensor(ObjectUniformTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getCell(int d0index, int d1index) {
            return tensor.value;
        }
    }
 
    private static class ObjectD3UniformTensor<T> extends D3TensorShell<T> {
        private final ObjectUniformTensor<T> tensor;

        private ObjectD3UniformTensor(ObjectUniformTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getCell(int d0index, int d1index, int d2index) {
            return tensor.value;
        }
    }
 
    private static class ObjectD4UniformTensor<T> extends D4TensorShell<T> {
        private final ObjectUniformTensor<T> tensor;

        private ObjectD4UniformTensor(ObjectUniformTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensor.value;
        }
    }
 
    private static class ObjectD5UniformTensor<T> extends D5TensorShell<T> {
        private final ObjectUniformTensor<T> tensor;

        private ObjectD5UniformTensor(ObjectUniformTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensor.value;
        }
    }
 
    private static class ObjectD6UniformTensor<T> extends D6TensorShell<T> {
        private final ObjectUniformTensor<T> tensor;

        private ObjectD6UniformTensor(ObjectUniformTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensor.value;
        }
    }
 
    private static class ObjectD7UniformTensor<T> extends D7TensorShell<T> {
        private final ObjectUniformTensor<T> tensor;

        private ObjectD7UniformTensor(ObjectUniformTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensor.value;
        }
    }
 
    private static class ObjectD8UniformTensor<T> extends D8TensorShell<T> {
        private final ObjectUniformTensor<T> tensor;

        private ObjectD8UniformTensor(ObjectUniformTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensor.value;
        }
    }
 
    private static class ObjectD9UniformTensor<T> extends D9TensorShell<T> {
        private final ObjectUniformTensor<T> tensor;

        private ObjectD9UniformTensor(ObjectUniformTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensor.value;
        }
    }

}
