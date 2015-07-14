package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.decorators.primitive.size.*;
import com.pb.sawdust.tensor.decorators.size.*;
import com.pb.sawdust.tensor.factory.LiterateTensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.array.*;
import com.pb.sawdust.tensor.alias.scalar.impl.*;

import java.lang.reflect.Array;

/**
 * The {@code ArrayTensor} class offers access to "array" tensor implementations. An array tensor is one whose values are internally held as  
 * multidimensional arrays whose dimensional structure matches that of the tensor. An array tensor is probably useful for situations where
 * direct tensor access is needed. Because of the number of internal arrays needed to match the tensor structure, an {@code ArrayTensor}'s
 * memory footprint will be somewhat higher than an equivalent {@code LinearTensor}.
 * <p>
 * The maximum number of tensor elements allowed in an {@code ArrayTensor} is theoretically unbound (though in practice finite JVM memory will force a limit).
 *
 * @author crf <br/>
 *         Started: Oct 20, 2008 2:24:39 PM
 *         Revised: Dec 14, 2009 12:35:35 PM
 */
public final class ArrayTensor extends LiterateTensorFactory {
    private ArrayTensor() {}
    
    private static final ArrayTensor factory = new ArrayTensor();
    
    /**
     Method to get an array tensor factory.
     
     @return a {@code ArrayTensor} instance.
     */
    public static ArrayTensor getFactory() {
        return factory;
    }
    

    public ByteTensor byteTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ByteScalarImpl();
            case 1 : return new ByteD1ArrayTensor(new ByteArrayTensor(dimensions));
            case 2 : return new ByteD2ArrayTensor(new ByteArrayTensor(dimensions));
            case 3 : return new ByteD3ArrayTensor(new ByteArrayTensor(dimensions));
            case 4 : return new ByteD4ArrayTensor(new ByteArrayTensor(dimensions));
            case 5 : return new ByteD5ArrayTensor(new ByteArrayTensor(dimensions));
            case 6 : return new ByteD6ArrayTensor(new ByteArrayTensor(dimensions));
            case 7 : return new ByteD7ArrayTensor(new ByteArrayTensor(dimensions));
            case 8 : return new ByteD8ArrayTensor(new ByteArrayTensor(dimensions));
            case 9 : return new ByteD9ArrayTensor(new ByteArrayTensor(dimensions));
            default : return new ByteArrayTensor(dimensions);
        }
    }
    public ByteTensor initializedByteTensor(byte defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ByteScalarImpl(defaultValue);
            case 1 : return new ByteD1ArrayTensor(new ByteArrayTensor(true,defaultValue,dimensions));
            case 2 : return new ByteD2ArrayTensor(new ByteArrayTensor(true,defaultValue,dimensions));
            case 3 : return new ByteD3ArrayTensor(new ByteArrayTensor(true,defaultValue,dimensions));
            case 4 : return new ByteD4ArrayTensor(new ByteArrayTensor(true,defaultValue,dimensions));
            case 5 : return new ByteD5ArrayTensor(new ByteArrayTensor(true,defaultValue,dimensions));
            case 6 : return new ByteD6ArrayTensor(new ByteArrayTensor(true,defaultValue,dimensions));
            case 7 : return new ByteD7ArrayTensor(new ByteArrayTensor(true,defaultValue,dimensions));
            case 8 : return new ByteD8ArrayTensor(new ByteArrayTensor(true,defaultValue,dimensions));
            case 9 : return new ByteD9ArrayTensor(new ByteArrayTensor(true,defaultValue,dimensions));
            default : return new ByteArrayTensor(true,defaultValue,dimensions);
        }
    }

    public ShortTensor shortTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ShortScalarImpl();
            case 1 : return new ShortD1ArrayTensor(new ShortArrayTensor(dimensions));
            case 2 : return new ShortD2ArrayTensor(new ShortArrayTensor(dimensions));
            case 3 : return new ShortD3ArrayTensor(new ShortArrayTensor(dimensions));
            case 4 : return new ShortD4ArrayTensor(new ShortArrayTensor(dimensions));
            case 5 : return new ShortD5ArrayTensor(new ShortArrayTensor(dimensions));
            case 6 : return new ShortD6ArrayTensor(new ShortArrayTensor(dimensions));
            case 7 : return new ShortD7ArrayTensor(new ShortArrayTensor(dimensions));
            case 8 : return new ShortD8ArrayTensor(new ShortArrayTensor(dimensions));
            case 9 : return new ShortD9ArrayTensor(new ShortArrayTensor(dimensions));
            default : return new ShortArrayTensor(dimensions);
        }
    }
    public ShortTensor initializedShortTensor(short defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ShortScalarImpl(defaultValue);
            case 1 : return new ShortD1ArrayTensor(new ShortArrayTensor(true,defaultValue,dimensions));
            case 2 : return new ShortD2ArrayTensor(new ShortArrayTensor(true,defaultValue,dimensions));
            case 3 : return new ShortD3ArrayTensor(new ShortArrayTensor(true,defaultValue,dimensions));
            case 4 : return new ShortD4ArrayTensor(new ShortArrayTensor(true,defaultValue,dimensions));
            case 5 : return new ShortD5ArrayTensor(new ShortArrayTensor(true,defaultValue,dimensions));
            case 6 : return new ShortD6ArrayTensor(new ShortArrayTensor(true,defaultValue,dimensions));
            case 7 : return new ShortD7ArrayTensor(new ShortArrayTensor(true,defaultValue,dimensions));
            case 8 : return new ShortD8ArrayTensor(new ShortArrayTensor(true,defaultValue,dimensions));
            case 9 : return new ShortD9ArrayTensor(new ShortArrayTensor(true,defaultValue,dimensions));
            default : return new ShortArrayTensor(true,defaultValue,dimensions);
        }
    }

    public IntTensor intTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new IntScalarImpl();
            case 1 : return new IntD1ArrayTensor(new IntArrayTensor(dimensions));
            case 2 : return new IntD2ArrayTensor(new IntArrayTensor(dimensions));
            case 3 : return new IntD3ArrayTensor(new IntArrayTensor(dimensions));
            case 4 : return new IntD4ArrayTensor(new IntArrayTensor(dimensions));
            case 5 : return new IntD5ArrayTensor(new IntArrayTensor(dimensions));
            case 6 : return new IntD6ArrayTensor(new IntArrayTensor(dimensions));
            case 7 : return new IntD7ArrayTensor(new IntArrayTensor(dimensions));
            case 8 : return new IntD8ArrayTensor(new IntArrayTensor(dimensions));
            case 9 : return new IntD9ArrayTensor(new IntArrayTensor(dimensions));
            default : return new IntArrayTensor(dimensions);
        }
    }
    public IntTensor initializedIntTensor(int defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new IntScalarImpl(defaultValue);
            case 1 : return new IntD1ArrayTensor(new IntArrayTensor(true,defaultValue,dimensions));
            case 2 : return new IntD2ArrayTensor(new IntArrayTensor(true,defaultValue,dimensions));
            case 3 : return new IntD3ArrayTensor(new IntArrayTensor(true,defaultValue,dimensions));
            case 4 : return new IntD4ArrayTensor(new IntArrayTensor(true,defaultValue,dimensions));
            case 5 : return new IntD5ArrayTensor(new IntArrayTensor(true,defaultValue,dimensions));
            case 6 : return new IntD6ArrayTensor(new IntArrayTensor(true,defaultValue,dimensions));
            case 7 : return new IntD7ArrayTensor(new IntArrayTensor(true,defaultValue,dimensions));
            case 8 : return new IntD8ArrayTensor(new IntArrayTensor(true,defaultValue,dimensions));
            case 9 : return new IntD9ArrayTensor(new IntArrayTensor(true,defaultValue,dimensions));
            default : return new IntArrayTensor(true,defaultValue,dimensions);
        }
    }

    public LongTensor longTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new LongScalarImpl();
            case 1 : return new LongD1ArrayTensor(new LongArrayTensor(dimensions));
            case 2 : return new LongD2ArrayTensor(new LongArrayTensor(dimensions));
            case 3 : return new LongD3ArrayTensor(new LongArrayTensor(dimensions));
            case 4 : return new LongD4ArrayTensor(new LongArrayTensor(dimensions));
            case 5 : return new LongD5ArrayTensor(new LongArrayTensor(dimensions));
            case 6 : return new LongD6ArrayTensor(new LongArrayTensor(dimensions));
            case 7 : return new LongD7ArrayTensor(new LongArrayTensor(dimensions));
            case 8 : return new LongD8ArrayTensor(new LongArrayTensor(dimensions));
            case 9 : return new LongD9ArrayTensor(new LongArrayTensor(dimensions));
            default : return new LongArrayTensor(dimensions);
        }
    }
    public LongTensor initializedLongTensor(long defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new LongScalarImpl(defaultValue);
            case 1 : return new LongD1ArrayTensor(new LongArrayTensor(true,defaultValue,dimensions));
            case 2 : return new LongD2ArrayTensor(new LongArrayTensor(true,defaultValue,dimensions));
            case 3 : return new LongD3ArrayTensor(new LongArrayTensor(true,defaultValue,dimensions));
            case 4 : return new LongD4ArrayTensor(new LongArrayTensor(true,defaultValue,dimensions));
            case 5 : return new LongD5ArrayTensor(new LongArrayTensor(true,defaultValue,dimensions));
            case 6 : return new LongD6ArrayTensor(new LongArrayTensor(true,defaultValue,dimensions));
            case 7 : return new LongD7ArrayTensor(new LongArrayTensor(true,defaultValue,dimensions));
            case 8 : return new LongD8ArrayTensor(new LongArrayTensor(true,defaultValue,dimensions));
            case 9 : return new LongD9ArrayTensor(new LongArrayTensor(true,defaultValue,dimensions));
            default : return new LongArrayTensor(true,defaultValue,dimensions);
        }
    }

    public FloatTensor floatTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new FloatScalarImpl();
            case 1 : return new FloatD1ArrayTensor(new FloatArrayTensor(dimensions));
            case 2 : return new FloatD2ArrayTensor(new FloatArrayTensor(dimensions));
            case 3 : return new FloatD3ArrayTensor(new FloatArrayTensor(dimensions));
            case 4 : return new FloatD4ArrayTensor(new FloatArrayTensor(dimensions));
            case 5 : return new FloatD5ArrayTensor(new FloatArrayTensor(dimensions));
            case 6 : return new FloatD6ArrayTensor(new FloatArrayTensor(dimensions));
            case 7 : return new FloatD7ArrayTensor(new FloatArrayTensor(dimensions));
            case 8 : return new FloatD8ArrayTensor(new FloatArrayTensor(dimensions));
            case 9 : return new FloatD9ArrayTensor(new FloatArrayTensor(dimensions));
            default : return new FloatArrayTensor(dimensions);
        }
    }
    public FloatTensor initializedFloatTensor(float defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new FloatScalarImpl(defaultValue);
            case 1 : return new FloatD1ArrayTensor(new FloatArrayTensor(true,defaultValue,dimensions));
            case 2 : return new FloatD2ArrayTensor(new FloatArrayTensor(true,defaultValue,dimensions));
            case 3 : return new FloatD3ArrayTensor(new FloatArrayTensor(true,defaultValue,dimensions));
            case 4 : return new FloatD4ArrayTensor(new FloatArrayTensor(true,defaultValue,dimensions));
            case 5 : return new FloatD5ArrayTensor(new FloatArrayTensor(true,defaultValue,dimensions));
            case 6 : return new FloatD6ArrayTensor(new FloatArrayTensor(true,defaultValue,dimensions));
            case 7 : return new FloatD7ArrayTensor(new FloatArrayTensor(true,defaultValue,dimensions));
            case 8 : return new FloatD8ArrayTensor(new FloatArrayTensor(true,defaultValue,dimensions));
            case 9 : return new FloatD9ArrayTensor(new FloatArrayTensor(true,defaultValue,dimensions));
            default : return new FloatArrayTensor(true,defaultValue,dimensions);
        }
    }

    public DoubleTensor doubleTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new DoubleScalarImpl();
            case 1 : return new DoubleD1ArrayTensor(new DoubleArrayTensor(dimensions));
            case 2 : return new DoubleD2ArrayTensor(new DoubleArrayTensor(dimensions));
            case 3 : return new DoubleD3ArrayTensor(new DoubleArrayTensor(dimensions));
            case 4 : return new DoubleD4ArrayTensor(new DoubleArrayTensor(dimensions));
            case 5 : return new DoubleD5ArrayTensor(new DoubleArrayTensor(dimensions));
            case 6 : return new DoubleD6ArrayTensor(new DoubleArrayTensor(dimensions));
            case 7 : return new DoubleD7ArrayTensor(new DoubleArrayTensor(dimensions));
            case 8 : return new DoubleD8ArrayTensor(new DoubleArrayTensor(dimensions));
            case 9 : return new DoubleD9ArrayTensor(new DoubleArrayTensor(dimensions));
            default : return new DoubleArrayTensor(dimensions);
        }
    }
    public DoubleTensor initializedDoubleTensor(double defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new DoubleScalarImpl(defaultValue);
            case 1 : return new DoubleD1ArrayTensor(new DoubleArrayTensor(true,defaultValue,dimensions));
            case 2 : return new DoubleD2ArrayTensor(new DoubleArrayTensor(true,defaultValue,dimensions));
            case 3 : return new DoubleD3ArrayTensor(new DoubleArrayTensor(true,defaultValue,dimensions));
            case 4 : return new DoubleD4ArrayTensor(new DoubleArrayTensor(true,defaultValue,dimensions));
            case 5 : return new DoubleD5ArrayTensor(new DoubleArrayTensor(true,defaultValue,dimensions));
            case 6 : return new DoubleD6ArrayTensor(new DoubleArrayTensor(true,defaultValue,dimensions));
            case 7 : return new DoubleD7ArrayTensor(new DoubleArrayTensor(true,defaultValue,dimensions));
            case 8 : return new DoubleD8ArrayTensor(new DoubleArrayTensor(true,defaultValue,dimensions));
            case 9 : return new DoubleD9ArrayTensor(new DoubleArrayTensor(true,defaultValue,dimensions));
            default : return new DoubleArrayTensor(true,defaultValue,dimensions);
        }
    }

    public CharTensor charTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new CharScalarImpl();
            case 1 : return new CharD1ArrayTensor(new CharArrayTensor(dimensions));
            case 2 : return new CharD2ArrayTensor(new CharArrayTensor(dimensions));
            case 3 : return new CharD3ArrayTensor(new CharArrayTensor(dimensions));
            case 4 : return new CharD4ArrayTensor(new CharArrayTensor(dimensions));
            case 5 : return new CharD5ArrayTensor(new CharArrayTensor(dimensions));
            case 6 : return new CharD6ArrayTensor(new CharArrayTensor(dimensions));
            case 7 : return new CharD7ArrayTensor(new CharArrayTensor(dimensions));
            case 8 : return new CharD8ArrayTensor(new CharArrayTensor(dimensions));
            case 9 : return new CharD9ArrayTensor(new CharArrayTensor(dimensions));
            default : return new CharArrayTensor(dimensions);
        }
    }
    public CharTensor initializedCharTensor(char defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new CharScalarImpl(defaultValue);
            case 1 : return new CharD1ArrayTensor(new CharArrayTensor(true,defaultValue,dimensions));
            case 2 : return new CharD2ArrayTensor(new CharArrayTensor(true,defaultValue,dimensions));
            case 3 : return new CharD3ArrayTensor(new CharArrayTensor(true,defaultValue,dimensions));
            case 4 : return new CharD4ArrayTensor(new CharArrayTensor(true,defaultValue,dimensions));
            case 5 : return new CharD5ArrayTensor(new CharArrayTensor(true,defaultValue,dimensions));
            case 6 : return new CharD6ArrayTensor(new CharArrayTensor(true,defaultValue,dimensions));
            case 7 : return new CharD7ArrayTensor(new CharArrayTensor(true,defaultValue,dimensions));
            case 8 : return new CharD8ArrayTensor(new CharArrayTensor(true,defaultValue,dimensions));
            case 9 : return new CharD9ArrayTensor(new CharArrayTensor(true,defaultValue,dimensions));
            default : return new CharArrayTensor(true,defaultValue,dimensions);
        }
    }

    public BooleanTensor booleanTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new BooleanScalarImpl();
            case 1 : return new BooleanD1ArrayTensor(new BooleanArrayTensor(dimensions));
            case 2 : return new BooleanD2ArrayTensor(new BooleanArrayTensor(dimensions));
            case 3 : return new BooleanD3ArrayTensor(new BooleanArrayTensor(dimensions));
            case 4 : return new BooleanD4ArrayTensor(new BooleanArrayTensor(dimensions));
            case 5 : return new BooleanD5ArrayTensor(new BooleanArrayTensor(dimensions));
            case 6 : return new BooleanD6ArrayTensor(new BooleanArrayTensor(dimensions));
            case 7 : return new BooleanD7ArrayTensor(new BooleanArrayTensor(dimensions));
            case 8 : return new BooleanD8ArrayTensor(new BooleanArrayTensor(dimensions));
            case 9 : return new BooleanD9ArrayTensor(new BooleanArrayTensor(dimensions));
            default : return new BooleanArrayTensor(dimensions);
        }
    }
    public BooleanTensor initializedBooleanTensor(boolean defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new BooleanScalarImpl(defaultValue);
            case 1 : return new BooleanD1ArrayTensor(new BooleanArrayTensor(true,defaultValue,dimensions));
            case 2 : return new BooleanD2ArrayTensor(new BooleanArrayTensor(true,defaultValue,dimensions));
            case 3 : return new BooleanD3ArrayTensor(new BooleanArrayTensor(true,defaultValue,dimensions));
            case 4 : return new BooleanD4ArrayTensor(new BooleanArrayTensor(true,defaultValue,dimensions));
            case 5 : return new BooleanD5ArrayTensor(new BooleanArrayTensor(true,defaultValue,dimensions));
            case 6 : return new BooleanD6ArrayTensor(new BooleanArrayTensor(true,defaultValue,dimensions));
            case 7 : return new BooleanD7ArrayTensor(new BooleanArrayTensor(true,defaultValue,dimensions));
            case 8 : return new BooleanD8ArrayTensor(new BooleanArrayTensor(true,defaultValue,dimensions));
            case 9 : return new BooleanD9ArrayTensor(new BooleanArrayTensor(true,defaultValue,dimensions));
            default : return new BooleanArrayTensor(true,defaultValue,dimensions);
        }
    }

    public <T> Tensor<T> tensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ScalarImpl<T>(); 
            case 1 : return new ObjectD1ArrayTensor<T>(new ObjectArrayTensor<T>(dimensions)); 
            case 2 : return new ObjectD2ArrayTensor<T>(new ObjectArrayTensor<T>(dimensions)); 
            case 3 : return new ObjectD3ArrayTensor<T>(new ObjectArrayTensor<T>(dimensions)); 
            case 4 : return new ObjectD4ArrayTensor<T>(new ObjectArrayTensor<T>(dimensions)); 
            case 5 : return new ObjectD5ArrayTensor<T>(new ObjectArrayTensor<T>(dimensions)); 
            case 6 : return new ObjectD6ArrayTensor<T>(new ObjectArrayTensor<T>(dimensions)); 
            case 7 : return new ObjectD7ArrayTensor<T>(new ObjectArrayTensor<T>(dimensions)); 
            case 8 : return new ObjectD8ArrayTensor<T>(new ObjectArrayTensor<T>(dimensions)); 
            case 9 : return new ObjectD9ArrayTensor<T>(new ObjectArrayTensor<T>(dimensions));
            default : return new ObjectArrayTensor<T>(dimensions);
        }
    }
    
    public <T> Tensor<T> initializedTensor(T defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ScalarImpl<T>(defaultValue); 
            case 1 : return new ObjectD1ArrayTensor<T>(new ObjectArrayTensor<T>(true,defaultValue,dimensions)); 
            case 2 : return new ObjectD2ArrayTensor<T>(new ObjectArrayTensor<T>(true,defaultValue,dimensions)); 
            case 3 : return new ObjectD3ArrayTensor<T>(new ObjectArrayTensor<T>(true,defaultValue,dimensions)); 
            case 4 : return new ObjectD4ArrayTensor<T>(new ObjectArrayTensor<T>(true,defaultValue,dimensions)); 
            case 5 : return new ObjectD5ArrayTensor<T>(new ObjectArrayTensor<T>(true,defaultValue,dimensions)); 
            case 6 : return new ObjectD6ArrayTensor<T>(new ObjectArrayTensor<T>(true,defaultValue,dimensions)); 
            case 7 : return new ObjectD7ArrayTensor<T>(new ObjectArrayTensor<T>(true,defaultValue,dimensions)); 
            case 8 : return new ObjectD8ArrayTensor<T>(new ObjectArrayTensor<T>(true,defaultValue,dimensions)); 
            case 9 : return new ObjectD9ArrayTensor<T>(new ObjectArrayTensor<T>(true,defaultValue,dimensions));
            default : return new ObjectArrayTensor<T>(true,defaultValue,dimensions);
        }
    }
 
    private static class ByteArrayTensor extends AbstractByteTensor {
        private final ByteTypeSafeArray tensor;
    
        private ByteArrayTensor(int ... dimensions) {
            super(dimensions);
            tensor = new ByteTypeSafeArray(Array.newInstance(byte.class,dimensions));
        }
    
        private ByteArrayTensor(boolean defaultIndicator, byte defaultValue, int ... dimensions) {
            this(dimensions);
            ArrayUtil.deepPrimitiveFill(tensor.getArray(),defaultValue);
        }
    
        public byte getCell(int ... indices) {
            return tensor.get(indices);
        }
        
        public void setCell(byte value, int ... indices) {
            tensor.set(value,indices);
        }
        
        public ByteTypeSafeArray getTensorValues(Class<Byte> type) {
            ByteTypeSafeArray array = TypeSafeArrayFactory.byteTypeSafeArray(getDimensions());
            ArrayUtil.copyArray(tensor.getArray(),array.getArray());
            return array;
        }
    
        public void setTensorValues(ByteTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensor.getArray());
        }
    }
 
    private static class ShortArrayTensor extends AbstractShortTensor {
        private final ShortTypeSafeArray tensor;
    
        private ShortArrayTensor(int ... dimensions) {
            super(dimensions);
            tensor = new ShortTypeSafeArray(Array.newInstance(short.class,dimensions));
        }
    
        private ShortArrayTensor(boolean defaultIndicator, short defaultValue, int ... dimensions) {
            this(dimensions);
            ArrayUtil.deepPrimitiveFill(tensor.getArray(),defaultValue);
        }
    
        public short getCell(int ... indices) {
            return tensor.get(indices);
        }
        
        public void setCell(short value, int ... indices) {
            tensor.set(value,indices);
        }
        
        public ShortTypeSafeArray getTensorValues(Class<Short> type) {
            ShortTypeSafeArray array = TypeSafeArrayFactory.shortTypeSafeArray(getDimensions());
            ArrayUtil.copyArray(tensor.getArray(),array.getArray());
            return array;
        }
    
        public void setTensorValues(ShortTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensor.getArray());
        }
    }
 
    private static class IntArrayTensor extends AbstractIntTensor {
        private final IntTypeSafeArray tensor;
    
        private IntArrayTensor(int ... dimensions) {
            super(dimensions);
            tensor = new IntTypeSafeArray(Array.newInstance(int.class,dimensions));
        }
    
        private IntArrayTensor(boolean defaultIndicator, int defaultValue, int ... dimensions) {
            this(dimensions);
            ArrayUtil.deepPrimitiveFill(tensor.getArray(),defaultValue);
        }
    
        public int getCell(int ... indices) {
            return tensor.get(indices);
        }
        
        public void setCell(int value, int ... indices) {
            tensor.set(value,indices);
        }
        
        public IntTypeSafeArray getTensorValues(Class<Integer> type) {
            IntTypeSafeArray array = TypeSafeArrayFactory.intTypeSafeArray(getDimensions());
            ArrayUtil.copyArray(tensor.getArray(),array.getArray());
            return array;
        }
    
        public void setTensorValues(IntTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensor.getArray());
        }
    }
 
    private static class LongArrayTensor extends AbstractLongTensor {
        private final LongTypeSafeArray tensor;
    
        private LongArrayTensor(int ... dimensions) {
            super(dimensions);
            tensor = new LongTypeSafeArray(Array.newInstance(long.class,dimensions));
        }
    
        private LongArrayTensor(boolean defaultIndicator, long defaultValue, int ... dimensions) {
            this(dimensions);
            ArrayUtil.deepPrimitiveFill(tensor.getArray(),defaultValue);
        }
    
        public long getCell(int ... indices) {
            return tensor.get(indices);
        }
        
        public void setCell(long value, int ... indices) {
            tensor.set(value,indices);
        }
        
        public LongTypeSafeArray getTensorValues(Class<Long> type) {
            LongTypeSafeArray array = TypeSafeArrayFactory.longTypeSafeArray(getDimensions());
            ArrayUtil.copyArray(tensor.getArray(),array.getArray());
            return array;
        }
    
        public void setTensorValues(LongTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensor.getArray());
        }
    }
 
    private static class FloatArrayTensor extends AbstractFloatTensor {
        private final FloatTypeSafeArray tensor;
    
        private FloatArrayTensor(int ... dimensions) {
            super(dimensions);
            tensor = new FloatTypeSafeArray(Array.newInstance(float.class,dimensions));
        }
    
        private FloatArrayTensor(boolean defaultIndicator, float defaultValue, int ... dimensions) {
            this(dimensions);
            ArrayUtil.deepPrimitiveFill(tensor.getArray(),defaultValue);
        }
    
        public float getCell(int ... indices) {
            return tensor.get(indices);
        }
        
        public void setCell(float value, int ... indices) {
            tensor.set(value,indices);
        }
        
        public FloatTypeSafeArray getTensorValues(Class<Float> type) {
            FloatTypeSafeArray array = TypeSafeArrayFactory.floatTypeSafeArray(getDimensions());
            ArrayUtil.copyArray(tensor.getArray(),array.getArray());
            return array;
        }
    
        public void setTensorValues(FloatTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensor.getArray());
        }
    }
 
    private static class DoubleArrayTensor extends AbstractDoubleTensor {
        private final DoubleTypeSafeArray tensor;
    
        private DoubleArrayTensor(int ... dimensions) {
            super(dimensions);
            tensor = new DoubleTypeSafeArray(Array.newInstance(double.class,dimensions));
        }
    
        private DoubleArrayTensor(boolean defaultIndicator, double defaultValue, int ... dimensions) {
            this(dimensions);
            ArrayUtil.deepPrimitiveFill(tensor.getArray(),defaultValue);
        }
    
        public double getCell(int ... indices) {
            return tensor.get(indices);
        }
        
        public void setCell(double value, int ... indices) {
            tensor.set(value,indices);
        }
        
        public DoubleTypeSafeArray getTensorValues(Class<Double> type) {
            DoubleTypeSafeArray array = TypeSafeArrayFactory.doubleTypeSafeArray(getDimensions());
            ArrayUtil.copyArray(tensor.getArray(),array.getArray());
            return array;
        }
    
        public void setTensorValues(DoubleTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensor.getArray());
        }
    }
 
    private static class CharArrayTensor extends AbstractCharTensor {
        private final CharTypeSafeArray tensor;
    
        private CharArrayTensor(int ... dimensions) {
            super(dimensions);
            tensor = new CharTypeSafeArray(Array.newInstance(char.class,dimensions));
        }
    
        private CharArrayTensor(boolean defaultIndicator, char defaultValue, int ... dimensions) {
            this(dimensions);
            ArrayUtil.deepPrimitiveFill(tensor.getArray(),defaultValue);
        }
    
        public char getCell(int ... indices) {
            return tensor.get(indices);
        }
        
        public void setCell(char value, int ... indices) {
            tensor.set(value,indices);
        }
        
        public CharTypeSafeArray getTensorValues(Class<Character> type) {
            CharTypeSafeArray array = TypeSafeArrayFactory.charTypeSafeArray(getDimensions());
            ArrayUtil.copyArray(tensor.getArray(),array.getArray());
            return array;
        }
    
        public void setTensorValues(CharTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensor.getArray());
        }
    }
 
    private static class BooleanArrayTensor extends AbstractBooleanTensor {
        private final BooleanTypeSafeArray tensor;
    
        private BooleanArrayTensor(int ... dimensions) {
            super(dimensions);
            tensor = new BooleanTypeSafeArray(Array.newInstance(boolean.class,dimensions));
        }
    
        private BooleanArrayTensor(boolean defaultIndicator, boolean defaultValue, int ... dimensions) {
            this(dimensions);
            ArrayUtil.deepPrimitiveFill(tensor.getArray(),defaultValue);
        }
    
        public boolean getCell(int ... indices) {
            return tensor.get(indices);
        }
        
        public void setCell(boolean value, int ... indices) {
            tensor.set(value,indices);
        }
        
        public BooleanTypeSafeArray getTensorValues(Class<Boolean> type) {
            BooleanTypeSafeArray array = TypeSafeArrayFactory.booleanTypeSafeArray(getDimensions());
            ArrayUtil.copyArray(tensor.getArray(),array.getArray());
            return array;
        }
    
        public void setTensorValues(BooleanTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensor.getArray());
        }
    }
 
    private static class ObjectArrayTensor<T> extends AbstractTensor<T> {
        private final ObjectTypeSafeArray<T> tensor;
        
       @SuppressWarnings("unchecked") //component type Object but doesn't matter because it is only used internally - equivalent to erasure
        private ObjectArrayTensor(int ... dimensions) {
            super(dimensions);
            tensor = (ObjectTypeSafeArray<T>) new ObjectTypeSafeArray(Array.newInstance(Object.class,dimensions),Object.class);
        }
        
        private ObjectArrayTensor(boolean defaultIndicator, T defaultValue, int ... dimensions) {
            this(dimensions);
            ArrayUtil.deepFill(tensor.getArray(),defaultValue);
        }
    
        public JavaType getType() {
            return JavaType.OBJECT;
        }
    
        public T getValue(int ... indices) {
            return tensor.get(indices);
        }
    
        public void setValue(T value,int ... indices) {
            tensor.set(value,indices);
        }
        
        public TypeSafeArray<T> getTensorValues(Class<T> type) {
            TypeSafeArray<T> array = TypeSafeArrayFactory.typeSafeArray(type,getDimensions());
            ArrayUtil.copyArray(tensor.getArray(),array.getArray());
            return array;
        }

        public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensor.getArray());
        }
    }
 
    private static class ByteD1ArrayTensor extends ByteD1TensorShell {
        private final byte[] tensorArray;

        private ByteD1ArrayTensor(ByteArrayTensor tensor) {
            super(tensor);
            tensorArray = (byte[]) tensor.tensor.getArray();
        }

        public byte getCell(int index) {
            return tensorArray[index];
        }

        public void setCell(byte value,int index) {
            tensorArray[index] = value;
        }
    
        public void setTensorValues(ByteTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ShortD1ArrayTensor extends ShortD1TensorShell {
        private final short[] tensorArray;

        private ShortD1ArrayTensor(ShortArrayTensor tensor) {
            super(tensor);
            tensorArray = (short[]) tensor.tensor.getArray();
        }

        public short getCell(int index) {
            return tensorArray[index];
        }

        public void setCell(short value,int index) {
            tensorArray[index] = value;
        }
    
        public void setTensorValues(ShortTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class IntD1ArrayTensor extends IntD1TensorShell {
        private final int[] tensorArray;

        private IntD1ArrayTensor(IntArrayTensor tensor) {
            super(tensor);
            tensorArray = (int[]) tensor.tensor.getArray();
        }

        public int getCell(int index) {
            return tensorArray[index];
        }

        public void setCell(int value,int index) {
            tensorArray[index] = value;
        }
    
        public void setTensorValues(IntTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class LongD1ArrayTensor extends LongD1TensorShell {
        private final long[] tensorArray;

        private LongD1ArrayTensor(LongArrayTensor tensor) {
            super(tensor);
            tensorArray = (long[]) tensor.tensor.getArray();
        }

        public long getCell(int index) {
            return tensorArray[index];
        }

        public void setCell(long value,int index) {
            tensorArray[index] = value;
        }
    
        public void setTensorValues(LongTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class FloatD1ArrayTensor extends FloatD1TensorShell {
        private final float[] tensorArray;

        private FloatD1ArrayTensor(FloatArrayTensor tensor) {
            super(tensor);
            tensorArray = (float[]) tensor.tensor.getArray();
        }

        public float getCell(int index) {
            return tensorArray[index];
        }

        public void setCell(float value,int index) {
            tensorArray[index] = value;
        }
    
        public void setTensorValues(FloatTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class DoubleD1ArrayTensor extends DoubleD1TensorShell {
        private final double[] tensorArray;

        private DoubleD1ArrayTensor(DoubleArrayTensor tensor) {
            super(tensor);
            tensorArray = (double[]) tensor.tensor.getArray();
        }

        public double getCell(int index) {
            return tensorArray[index];
        }

        public void setCell(double value,int index) {
            tensorArray[index] = value;
        }
    
        public void setTensorValues(DoubleTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class CharD1ArrayTensor extends CharD1TensorShell {
        private final char[] tensorArray;

        private CharD1ArrayTensor(CharArrayTensor tensor) {
            super(tensor);
            tensorArray = (char[]) tensor.tensor.getArray();
        }

        public char getCell(int index) {
            return tensorArray[index];
        }

        public void setCell(char value,int index) {
            tensorArray[index] = value;
        }
    
        public void setTensorValues(CharTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class BooleanD1ArrayTensor extends BooleanD1TensorShell {
        private final boolean[] tensorArray;

        private BooleanD1ArrayTensor(BooleanArrayTensor tensor) {
            super(tensor);
            tensorArray = (boolean[]) tensor.tensor.getArray();
        }

        public boolean getCell(int index) {
            return tensorArray[index];
        }

        public void setCell(boolean value,int index) {
            tensorArray[index] = value;
        }
    
        public void setTensorValues(BooleanTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ByteD2ArrayTensor extends ByteD2TensorShell {
        private final byte[][] tensorArray;

        private ByteD2ArrayTensor(ByteArrayTensor tensor) {
            super(tensor);
            tensorArray = (byte[][]) tensor.tensor.getArray();
        }

        public byte getCell(int d0index, int d1index) {
            return tensorArray[d0index][d1index];
        }

        public void setCell(byte value,int d0index, int d1index) {
            tensorArray[d0index][d1index] = value;
        }
    
        public void setTensorValues(ByteTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ShortD2ArrayTensor extends ShortD2TensorShell {
        private final short[][] tensorArray;

        private ShortD2ArrayTensor(ShortArrayTensor tensor) {
            super(tensor);
            tensorArray = (short[][]) tensor.tensor.getArray();
        }

        public short getCell(int d0index, int d1index) {
            return tensorArray[d0index][d1index];
        }

        public void setCell(short value,int d0index, int d1index) {
            tensorArray[d0index][d1index] = value;
        }
    
        public void setTensorValues(ShortTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class IntD2ArrayTensor extends IntD2TensorShell {
        private final int[][] tensorArray;

        private IntD2ArrayTensor(IntArrayTensor tensor) {
            super(tensor);
            tensorArray = (int[][]) tensor.tensor.getArray();
        }

        public int getCell(int d0index, int d1index) {
            return tensorArray[d0index][d1index];
        }

        public void setCell(int value,int d0index, int d1index) {
            tensorArray[d0index][d1index] = value;
        }
    
        public void setTensorValues(IntTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class LongD2ArrayTensor extends LongD2TensorShell {
        private final long[][] tensorArray;

        private LongD2ArrayTensor(LongArrayTensor tensor) {
            super(tensor);
            tensorArray = (long[][]) tensor.tensor.getArray();
        }

        public long getCell(int d0index, int d1index) {
            return tensorArray[d0index][d1index];
        }

        public void setCell(long value,int d0index, int d1index) {
            tensorArray[d0index][d1index] = value;
        }
    
        public void setTensorValues(LongTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class FloatD2ArrayTensor extends FloatD2TensorShell {
        private final float[][] tensorArray;

        private FloatD2ArrayTensor(FloatArrayTensor tensor) {
            super(tensor);
            tensorArray = (float[][]) tensor.tensor.getArray();
        }

        public float getCell(int d0index, int d1index) {
            return tensorArray[d0index][d1index];
        }

        public void setCell(float value,int d0index, int d1index) {
            tensorArray[d0index][d1index] = value;
        }
    
        public void setTensorValues(FloatTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class DoubleD2ArrayTensor extends DoubleD2TensorShell {
        private final double[][] tensorArray;

        private DoubleD2ArrayTensor(DoubleArrayTensor tensor) {
            super(tensor);
            tensorArray = (double[][]) tensor.tensor.getArray();
        }

        public double getCell(int d0index, int d1index) {
            return tensorArray[d0index][d1index];
        }

        public void setCell(double value,int d0index, int d1index) {
            tensorArray[d0index][d1index] = value;
        }
    
        public void setTensorValues(DoubleTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class CharD2ArrayTensor extends CharD2TensorShell {
        private final char[][] tensorArray;

        private CharD2ArrayTensor(CharArrayTensor tensor) {
            super(tensor);
            tensorArray = (char[][]) tensor.tensor.getArray();
        }

        public char getCell(int d0index, int d1index) {
            return tensorArray[d0index][d1index];
        }

        public void setCell(char value,int d0index, int d1index) {
            tensorArray[d0index][d1index] = value;
        }
    
        public void setTensorValues(CharTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class BooleanD2ArrayTensor extends BooleanD2TensorShell {
        private final boolean[][] tensorArray;

        private BooleanD2ArrayTensor(BooleanArrayTensor tensor) {
            super(tensor);
            tensorArray = (boolean[][]) tensor.tensor.getArray();
        }

        public boolean getCell(int d0index, int d1index) {
            return tensorArray[d0index][d1index];
        }

        public void setCell(boolean value,int d0index, int d1index) {
            tensorArray[d0index][d1index] = value;
        }
    
        public void setTensorValues(BooleanTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ByteD3ArrayTensor extends ByteD3TensorShell {
        private final byte[][][] tensorArray;

        private ByteD3ArrayTensor(ByteArrayTensor tensor) {
            super(tensor);
            tensorArray = (byte[][][]) tensor.tensor.getArray();
        }

        public byte getCell(int d0index, int d1index, int d2index) {
            return tensorArray[d0index][d1index][d2index];
        }

        public void setCell(byte value,int d0index, int d1index, int d2index) {
            tensorArray[d0index][d1index][d2index] = value;
        }
    
        public void setTensorValues(ByteTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ShortD3ArrayTensor extends ShortD3TensorShell {
        private final short[][][] tensorArray;

        private ShortD3ArrayTensor(ShortArrayTensor tensor) {
            super(tensor);
            tensorArray = (short[][][]) tensor.tensor.getArray();
        }

        public short getCell(int d0index, int d1index, int d2index) {
            return tensorArray[d0index][d1index][d2index];
        }

        public void setCell(short value,int d0index, int d1index, int d2index) {
            tensorArray[d0index][d1index][d2index] = value;
        }
    
        public void setTensorValues(ShortTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class IntD3ArrayTensor extends IntD3TensorShell {
        private final int[][][] tensorArray;

        private IntD3ArrayTensor(IntArrayTensor tensor) {
            super(tensor);
            tensorArray = (int[][][]) tensor.tensor.getArray();
        }

        public int getCell(int d0index, int d1index, int d2index) {
            return tensorArray[d0index][d1index][d2index];
        }

        public void setCell(int value,int d0index, int d1index, int d2index) {
            tensorArray[d0index][d1index][d2index] = value;
        }
    
        public void setTensorValues(IntTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class LongD3ArrayTensor extends LongD3TensorShell {
        private final long[][][] tensorArray;

        private LongD3ArrayTensor(LongArrayTensor tensor) {
            super(tensor);
            tensorArray = (long[][][]) tensor.tensor.getArray();
        }

        public long getCell(int d0index, int d1index, int d2index) {
            return tensorArray[d0index][d1index][d2index];
        }

        public void setCell(long value,int d0index, int d1index, int d2index) {
            tensorArray[d0index][d1index][d2index] = value;
        }
    
        public void setTensorValues(LongTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class FloatD3ArrayTensor extends FloatD3TensorShell {
        private final float[][][] tensorArray;

        private FloatD3ArrayTensor(FloatArrayTensor tensor) {
            super(tensor);
            tensorArray = (float[][][]) tensor.tensor.getArray();
        }

        public float getCell(int d0index, int d1index, int d2index) {
            return tensorArray[d0index][d1index][d2index];
        }

        public void setCell(float value,int d0index, int d1index, int d2index) {
            tensorArray[d0index][d1index][d2index] = value;
        }
    
        public void setTensorValues(FloatTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class DoubleD3ArrayTensor extends DoubleD3TensorShell {
        private final double[][][] tensorArray;

        private DoubleD3ArrayTensor(DoubleArrayTensor tensor) {
            super(tensor);
            tensorArray = (double[][][]) tensor.tensor.getArray();
        }

        public double getCell(int d0index, int d1index, int d2index) {
            return tensorArray[d0index][d1index][d2index];
        }

        public void setCell(double value,int d0index, int d1index, int d2index) {
            tensorArray[d0index][d1index][d2index] = value;
        }
    
        public void setTensorValues(DoubleTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class CharD3ArrayTensor extends CharD3TensorShell {
        private final char[][][] tensorArray;

        private CharD3ArrayTensor(CharArrayTensor tensor) {
            super(tensor);
            tensorArray = (char[][][]) tensor.tensor.getArray();
        }

        public char getCell(int d0index, int d1index, int d2index) {
            return tensorArray[d0index][d1index][d2index];
        }

        public void setCell(char value,int d0index, int d1index, int d2index) {
            tensorArray[d0index][d1index][d2index] = value;
        }
    
        public void setTensorValues(CharTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class BooleanD3ArrayTensor extends BooleanD3TensorShell {
        private final boolean[][][] tensorArray;

        private BooleanD3ArrayTensor(BooleanArrayTensor tensor) {
            super(tensor);
            tensorArray = (boolean[][][]) tensor.tensor.getArray();
        }

        public boolean getCell(int d0index, int d1index, int d2index) {
            return tensorArray[d0index][d1index][d2index];
        }

        public void setCell(boolean value,int d0index, int d1index, int d2index) {
            tensorArray[d0index][d1index][d2index] = value;
        }
    
        public void setTensorValues(BooleanTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ByteD4ArrayTensor extends ByteD4TensorShell {
        private final byte[][][][] tensorArray;

        private ByteD4ArrayTensor(ByteArrayTensor tensor) {
            super(tensor);
            tensorArray = (byte[][][][]) tensor.tensor.getArray();
        }

        public byte getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensorArray[d0index][d1index][d2index][d3index];
        }

        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index) {
            tensorArray[d0index][d1index][d2index][d3index] = value;
        }
    
        public void setTensorValues(ByteTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ShortD4ArrayTensor extends ShortD4TensorShell {
        private final short[][][][] tensorArray;

        private ShortD4ArrayTensor(ShortArrayTensor tensor) {
            super(tensor);
            tensorArray = (short[][][][]) tensor.tensor.getArray();
        }

        public short getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensorArray[d0index][d1index][d2index][d3index];
        }

        public void setCell(short value,int d0index, int d1index, int d2index, int d3index) {
            tensorArray[d0index][d1index][d2index][d3index] = value;
        }
    
        public void setTensorValues(ShortTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class IntD4ArrayTensor extends IntD4TensorShell {
        private final int[][][][] tensorArray;

        private IntD4ArrayTensor(IntArrayTensor tensor) {
            super(tensor);
            tensorArray = (int[][][][]) tensor.tensor.getArray();
        }

        public int getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensorArray[d0index][d1index][d2index][d3index];
        }

        public void setCell(int value,int d0index, int d1index, int d2index, int d3index) {
            tensorArray[d0index][d1index][d2index][d3index] = value;
        }
    
        public void setTensorValues(IntTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class LongD4ArrayTensor extends LongD4TensorShell {
        private final long[][][][] tensorArray;

        private LongD4ArrayTensor(LongArrayTensor tensor) {
            super(tensor);
            tensorArray = (long[][][][]) tensor.tensor.getArray();
        }

        public long getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensorArray[d0index][d1index][d2index][d3index];
        }

        public void setCell(long value,int d0index, int d1index, int d2index, int d3index) {
            tensorArray[d0index][d1index][d2index][d3index] = value;
        }
    
        public void setTensorValues(LongTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class FloatD4ArrayTensor extends FloatD4TensorShell {
        private final float[][][][] tensorArray;

        private FloatD4ArrayTensor(FloatArrayTensor tensor) {
            super(tensor);
            tensorArray = (float[][][][]) tensor.tensor.getArray();
        }

        public float getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensorArray[d0index][d1index][d2index][d3index];
        }

        public void setCell(float value,int d0index, int d1index, int d2index, int d3index) {
            tensorArray[d0index][d1index][d2index][d3index] = value;
        }
    
        public void setTensorValues(FloatTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class DoubleD4ArrayTensor extends DoubleD4TensorShell {
        private final double[][][][] tensorArray;

        private DoubleD4ArrayTensor(DoubleArrayTensor tensor) {
            super(tensor);
            tensorArray = (double[][][][]) tensor.tensor.getArray();
        }

        public double getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensorArray[d0index][d1index][d2index][d3index];
        }

        public void setCell(double value,int d0index, int d1index, int d2index, int d3index) {
            tensorArray[d0index][d1index][d2index][d3index] = value;
        }
    
        public void setTensorValues(DoubleTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class CharD4ArrayTensor extends CharD4TensorShell {
        private final char[][][][] tensorArray;

        private CharD4ArrayTensor(CharArrayTensor tensor) {
            super(tensor);
            tensorArray = (char[][][][]) tensor.tensor.getArray();
        }

        public char getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensorArray[d0index][d1index][d2index][d3index];
        }

        public void setCell(char value,int d0index, int d1index, int d2index, int d3index) {
            tensorArray[d0index][d1index][d2index][d3index] = value;
        }
    
        public void setTensorValues(CharTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class BooleanD4ArrayTensor extends BooleanD4TensorShell {
        private final boolean[][][][] tensorArray;

        private BooleanD4ArrayTensor(BooleanArrayTensor tensor) {
            super(tensor);
            tensorArray = (boolean[][][][]) tensor.tensor.getArray();
        }

        public boolean getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensorArray[d0index][d1index][d2index][d3index];
        }

        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index) {
            tensorArray[d0index][d1index][d2index][d3index] = value;
        }
    
        public void setTensorValues(BooleanTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ByteD5ArrayTensor extends ByteD5TensorShell {
        private final byte[][][][][] tensorArray;

        private ByteD5ArrayTensor(ByteArrayTensor tensor) {
            super(tensor);
            tensorArray = (byte[][][][][]) tensor.tensor.getArray();
        }

        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index];
        }

        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index] = value;
        }
    
        public void setTensorValues(ByteTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ShortD5ArrayTensor extends ShortD5TensorShell {
        private final short[][][][][] tensorArray;

        private ShortD5ArrayTensor(ShortArrayTensor tensor) {
            super(tensor);
            tensorArray = (short[][][][][]) tensor.tensor.getArray();
        }

        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index];
        }

        public void setCell(short value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index] = value;
        }
    
        public void setTensorValues(ShortTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class IntD5ArrayTensor extends IntD5TensorShell {
        private final int[][][][][] tensorArray;

        private IntD5ArrayTensor(IntArrayTensor tensor) {
            super(tensor);
            tensorArray = (int[][][][][]) tensor.tensor.getArray();
        }

        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index];
        }

        public void setCell(int value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index] = value;
        }
    
        public void setTensorValues(IntTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class LongD5ArrayTensor extends LongD5TensorShell {
        private final long[][][][][] tensorArray;

        private LongD5ArrayTensor(LongArrayTensor tensor) {
            super(tensor);
            tensorArray = (long[][][][][]) tensor.tensor.getArray();
        }

        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index];
        }

        public void setCell(long value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index] = value;
        }
    
        public void setTensorValues(LongTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class FloatD5ArrayTensor extends FloatD5TensorShell {
        private final float[][][][][] tensorArray;

        private FloatD5ArrayTensor(FloatArrayTensor tensor) {
            super(tensor);
            tensorArray = (float[][][][][]) tensor.tensor.getArray();
        }

        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index];
        }

        public void setCell(float value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index] = value;
        }
    
        public void setTensorValues(FloatTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class DoubleD5ArrayTensor extends DoubleD5TensorShell {
        private final double[][][][][] tensorArray;

        private DoubleD5ArrayTensor(DoubleArrayTensor tensor) {
            super(tensor);
            tensorArray = (double[][][][][]) tensor.tensor.getArray();
        }

        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index];
        }

        public void setCell(double value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index] = value;
        }
    
        public void setTensorValues(DoubleTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class CharD5ArrayTensor extends CharD5TensorShell {
        private final char[][][][][] tensorArray;

        private CharD5ArrayTensor(CharArrayTensor tensor) {
            super(tensor);
            tensorArray = (char[][][][][]) tensor.tensor.getArray();
        }

        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index];
        }

        public void setCell(char value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index] = value;
        }
    
        public void setTensorValues(CharTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class BooleanD5ArrayTensor extends BooleanD5TensorShell {
        private final boolean[][][][][] tensorArray;

        private BooleanD5ArrayTensor(BooleanArrayTensor tensor) {
            super(tensor);
            tensorArray = (boolean[][][][][]) tensor.tensor.getArray();
        }

        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index];
        }

        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index] = value;
        }
    
        public void setTensorValues(BooleanTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ByteD6ArrayTensor extends ByteD6TensorShell {
        private final byte[][][][][][] tensorArray;

        private ByteD6ArrayTensor(ByteArrayTensor tensor) {
            super(tensor);
            tensorArray = (byte[][][][][][]) tensor.tensor.getArray();
        }

        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index] = value;
        }
    
        public void setTensorValues(ByteTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ShortD6ArrayTensor extends ShortD6TensorShell {
        private final short[][][][][][] tensorArray;

        private ShortD6ArrayTensor(ShortArrayTensor tensor) {
            super(tensor);
            tensorArray = (short[][][][][][]) tensor.tensor.getArray();
        }

        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public void setCell(short value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index] = value;
        }
    
        public void setTensorValues(ShortTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class IntD6ArrayTensor extends IntD6TensorShell {
        private final int[][][][][][] tensorArray;

        private IntD6ArrayTensor(IntArrayTensor tensor) {
            super(tensor);
            tensorArray = (int[][][][][][]) tensor.tensor.getArray();
        }

        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public void setCell(int value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index] = value;
        }
    
        public void setTensorValues(IntTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class LongD6ArrayTensor extends LongD6TensorShell {
        private final long[][][][][][] tensorArray;

        private LongD6ArrayTensor(LongArrayTensor tensor) {
            super(tensor);
            tensorArray = (long[][][][][][]) tensor.tensor.getArray();
        }

        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public void setCell(long value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index] = value;
        }
    
        public void setTensorValues(LongTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class FloatD6ArrayTensor extends FloatD6TensorShell {
        private final float[][][][][][] tensorArray;

        private FloatD6ArrayTensor(FloatArrayTensor tensor) {
            super(tensor);
            tensorArray = (float[][][][][][]) tensor.tensor.getArray();
        }

        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public void setCell(float value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index] = value;
        }
    
        public void setTensorValues(FloatTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class DoubleD6ArrayTensor extends DoubleD6TensorShell {
        private final double[][][][][][] tensorArray;

        private DoubleD6ArrayTensor(DoubleArrayTensor tensor) {
            super(tensor);
            tensorArray = (double[][][][][][]) tensor.tensor.getArray();
        }

        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public void setCell(double value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index] = value;
        }
    
        public void setTensorValues(DoubleTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class CharD6ArrayTensor extends CharD6TensorShell {
        private final char[][][][][][] tensorArray;

        private CharD6ArrayTensor(CharArrayTensor tensor) {
            super(tensor);
            tensorArray = (char[][][][][][]) tensor.tensor.getArray();
        }

        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public void setCell(char value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index] = value;
        }
    
        public void setTensorValues(CharTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class BooleanD6ArrayTensor extends BooleanD6TensorShell {
        private final boolean[][][][][][] tensorArray;

        private BooleanD6ArrayTensor(BooleanArrayTensor tensor) {
            super(tensor);
            tensorArray = (boolean[][][][][][]) tensor.tensor.getArray();
        }

        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index] = value;
        }
    
        public void setTensorValues(BooleanTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ByteD7ArrayTensor extends ByteD7TensorShell {
        private final byte[][][][][][][] tensorArray;

        private ByteD7ArrayTensor(ByteArrayTensor tensor) {
            super(tensor);
            tensorArray = (byte[][][][][][][]) tensor.tensor.getArray();
        }

        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index] = value;
        }
    
        public void setTensorValues(ByteTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ShortD7ArrayTensor extends ShortD7TensorShell {
        private final short[][][][][][][] tensorArray;

        private ShortD7ArrayTensor(ShortArrayTensor tensor) {
            super(tensor);
            tensorArray = (short[][][][][][][]) tensor.tensor.getArray();
        }

        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public void setCell(short value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index] = value;
        }
    
        public void setTensorValues(ShortTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class IntD7ArrayTensor extends IntD7TensorShell {
        private final int[][][][][][][] tensorArray;

        private IntD7ArrayTensor(IntArrayTensor tensor) {
            super(tensor);
            tensorArray = (int[][][][][][][]) tensor.tensor.getArray();
        }

        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public void setCell(int value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index] = value;
        }
    
        public void setTensorValues(IntTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class LongD7ArrayTensor extends LongD7TensorShell {
        private final long[][][][][][][] tensorArray;

        private LongD7ArrayTensor(LongArrayTensor tensor) {
            super(tensor);
            tensorArray = (long[][][][][][][]) tensor.tensor.getArray();
        }

        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public void setCell(long value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index] = value;
        }
    
        public void setTensorValues(LongTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class FloatD7ArrayTensor extends FloatD7TensorShell {
        private final float[][][][][][][] tensorArray;

        private FloatD7ArrayTensor(FloatArrayTensor tensor) {
            super(tensor);
            tensorArray = (float[][][][][][][]) tensor.tensor.getArray();
        }

        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public void setCell(float value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index] = value;
        }
    
        public void setTensorValues(FloatTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class DoubleD7ArrayTensor extends DoubleD7TensorShell {
        private final double[][][][][][][] tensorArray;

        private DoubleD7ArrayTensor(DoubleArrayTensor tensor) {
            super(tensor);
            tensorArray = (double[][][][][][][]) tensor.tensor.getArray();
        }

        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public void setCell(double value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index] = value;
        }
    
        public void setTensorValues(DoubleTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class CharD7ArrayTensor extends CharD7TensorShell {
        private final char[][][][][][][] tensorArray;

        private CharD7ArrayTensor(CharArrayTensor tensor) {
            super(tensor);
            tensorArray = (char[][][][][][][]) tensor.tensor.getArray();
        }

        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public void setCell(char value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index] = value;
        }
    
        public void setTensorValues(CharTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class BooleanD7ArrayTensor extends BooleanD7TensorShell {
        private final boolean[][][][][][][] tensorArray;

        private BooleanD7ArrayTensor(BooleanArrayTensor tensor) {
            super(tensor);
            tensorArray = (boolean[][][][][][][]) tensor.tensor.getArray();
        }

        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index] = value;
        }
    
        public void setTensorValues(BooleanTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ByteD8ArrayTensor extends ByteD8TensorShell {
        private final byte[][][][][][][][] tensorArray;

        private ByteD8ArrayTensor(ByteArrayTensor tensor) {
            super(tensor);
            tensorArray = (byte[][][][][][][][]) tensor.tensor.getArray();
        }

        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index] = value;
        }
    
        public void setTensorValues(ByteTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ShortD8ArrayTensor extends ShortD8TensorShell {
        private final short[][][][][][][][] tensorArray;

        private ShortD8ArrayTensor(ShortArrayTensor tensor) {
            super(tensor);
            tensorArray = (short[][][][][][][][]) tensor.tensor.getArray();
        }

        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public void setCell(short value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index] = value;
        }
    
        public void setTensorValues(ShortTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class IntD8ArrayTensor extends IntD8TensorShell {
        private final int[][][][][][][][] tensorArray;

        private IntD8ArrayTensor(IntArrayTensor tensor) {
            super(tensor);
            tensorArray = (int[][][][][][][][]) tensor.tensor.getArray();
        }

        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public void setCell(int value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index] = value;
        }
    
        public void setTensorValues(IntTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class LongD8ArrayTensor extends LongD8TensorShell {
        private final long[][][][][][][][] tensorArray;

        private LongD8ArrayTensor(LongArrayTensor tensor) {
            super(tensor);
            tensorArray = (long[][][][][][][][]) tensor.tensor.getArray();
        }

        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public void setCell(long value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index] = value;
        }
    
        public void setTensorValues(LongTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class FloatD8ArrayTensor extends FloatD8TensorShell {
        private final float[][][][][][][][] tensorArray;

        private FloatD8ArrayTensor(FloatArrayTensor tensor) {
            super(tensor);
            tensorArray = (float[][][][][][][][]) tensor.tensor.getArray();
        }

        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public void setCell(float value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index] = value;
        }
    
        public void setTensorValues(FloatTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class DoubleD8ArrayTensor extends DoubleD8TensorShell {
        private final double[][][][][][][][] tensorArray;

        private DoubleD8ArrayTensor(DoubleArrayTensor tensor) {
            super(tensor);
            tensorArray = (double[][][][][][][][]) tensor.tensor.getArray();
        }

        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public void setCell(double value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index] = value;
        }
    
        public void setTensorValues(DoubleTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class CharD8ArrayTensor extends CharD8TensorShell {
        private final char[][][][][][][][] tensorArray;

        private CharD8ArrayTensor(CharArrayTensor tensor) {
            super(tensor);
            tensorArray = (char[][][][][][][][]) tensor.tensor.getArray();
        }

        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public void setCell(char value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index] = value;
        }
    
        public void setTensorValues(CharTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class BooleanD8ArrayTensor extends BooleanD8TensorShell {
        private final boolean[][][][][][][][] tensorArray;

        private BooleanD8ArrayTensor(BooleanArrayTensor tensor) {
            super(tensor);
            tensorArray = (boolean[][][][][][][][]) tensor.tensor.getArray();
        }

        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index] = value;
        }
    
        public void setTensorValues(BooleanTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ByteD9ArrayTensor extends ByteD9TensorShell {
        private final byte[][][][][][][][][] tensorArray;

        private ByteD9ArrayTensor(ByteArrayTensor tensor) {
            super(tensor);
            tensorArray = (byte[][][][][][][][][]) tensor.tensor.getArray();
        }

        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index] = value;
        }
    
        public void setTensorValues(ByteTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ShortD9ArrayTensor extends ShortD9TensorShell {
        private final short[][][][][][][][][] tensorArray;

        private ShortD9ArrayTensor(ShortArrayTensor tensor) {
            super(tensor);
            tensorArray = (short[][][][][][][][][]) tensor.tensor.getArray();
        }

        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public void setCell(short value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index] = value;
        }
    
        public void setTensorValues(ShortTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class IntD9ArrayTensor extends IntD9TensorShell {
        private final int[][][][][][][][][] tensorArray;

        private IntD9ArrayTensor(IntArrayTensor tensor) {
            super(tensor);
            tensorArray = (int[][][][][][][][][]) tensor.tensor.getArray();
        }

        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public void setCell(int value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index] = value;
        }
    
        public void setTensorValues(IntTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class LongD9ArrayTensor extends LongD9TensorShell {
        private final long[][][][][][][][][] tensorArray;

        private LongD9ArrayTensor(LongArrayTensor tensor) {
            super(tensor);
            tensorArray = (long[][][][][][][][][]) tensor.tensor.getArray();
        }

        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public void setCell(long value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index] = value;
        }
    
        public void setTensorValues(LongTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class FloatD9ArrayTensor extends FloatD9TensorShell {
        private final float[][][][][][][][][] tensorArray;

        private FloatD9ArrayTensor(FloatArrayTensor tensor) {
            super(tensor);
            tensorArray = (float[][][][][][][][][]) tensor.tensor.getArray();
        }

        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public void setCell(float value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index] = value;
        }
    
        public void setTensorValues(FloatTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class DoubleD9ArrayTensor extends DoubleD9TensorShell {
        private final double[][][][][][][][][] tensorArray;

        private DoubleD9ArrayTensor(DoubleArrayTensor tensor) {
            super(tensor);
            tensorArray = (double[][][][][][][][][]) tensor.tensor.getArray();
        }

        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public void setCell(double value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index] = value;
        }
    
        public void setTensorValues(DoubleTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class CharD9ArrayTensor extends CharD9TensorShell {
        private final char[][][][][][][][][] tensorArray;

        private CharD9ArrayTensor(CharArrayTensor tensor) {
            super(tensor);
            tensorArray = (char[][][][][][][][][]) tensor.tensor.getArray();
        }

        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public void setCell(char value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index] = value;
        }
    
        public void setTensorValues(CharTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class BooleanD9ArrayTensor extends BooleanD9TensorShell {
        private final boolean[][][][][][][][][] tensorArray;

        private BooleanD9ArrayTensor(BooleanArrayTensor tensor) {
            super(tensor);
            tensorArray = (boolean[][][][][][][][][]) tensor.tensor.getArray();
        }

        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index] = value;
        }
    
        public void setTensorValues(BooleanTypeSafeArray valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ObjectD1ArrayTensor<T> extends D1TensorShell<T> {
        private final T[] tensorArray;

       @SuppressWarnings("unchecked") //ObjectArrayTensor will have tensor of specified component type
        private ObjectD1ArrayTensor(ObjectArrayTensor<T> tensor) {
            super(tensor);
            tensorArray = (T[]) tensor.tensor.getArray();
        }

        public T getCell(int index) {
            return tensorArray[index];
        }

        public void setCell(T value,int index) {
            tensorArray[index] = value;
        }

        public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ObjectD2ArrayTensor<T> extends D2TensorShell<T> {
        private final T[][] tensorArray;

       @SuppressWarnings("unchecked") //ObjectArrayTensor will have tensor of specified component type
        private ObjectD2ArrayTensor(ObjectArrayTensor<T> tensor) {
            super(tensor);
            tensorArray = (T[][]) tensor.tensor.getArray();
        }

        public T getCell(int d0index, int d1index) {
            return tensorArray[d0index][d1index];
        }

        public void setCell(T value,int d0index, int d1index) {
            tensorArray[d0index][d1index] = value;
        }

        public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ObjectD3ArrayTensor<T> extends D3TensorShell<T> {
        private final T[][][] tensorArray;

       @SuppressWarnings("unchecked") //ObjectArrayTensor will have tensor of specified component type
        private ObjectD3ArrayTensor(ObjectArrayTensor<T> tensor) {
            super(tensor);
            tensorArray = (T[][][]) tensor.tensor.getArray();
        }

        public T getCell(int d0index, int d1index, int d2index) {
            return tensorArray[d0index][d1index][d2index];
        }

        public void setCell(T value,int d0index, int d1index, int d2index) {
            tensorArray[d0index][d1index][d2index] = value;
        }

        public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ObjectD4ArrayTensor<T> extends D4TensorShell<T> {
        private final T[][][][] tensorArray;

       @SuppressWarnings("unchecked") //ObjectArrayTensor will have tensor of specified component type
        private ObjectD4ArrayTensor(ObjectArrayTensor<T> tensor) {
            super(tensor);
            tensorArray = (T[][][][]) tensor.tensor.getArray();
        }

        public T getCell(int d0index, int d1index, int d2index, int d3index) {
            return tensorArray[d0index][d1index][d2index][d3index];
        }

        public void setCell(T value,int d0index, int d1index, int d2index, int d3index) {
            tensorArray[d0index][d1index][d2index][d3index] = value;
        }

        public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ObjectD5ArrayTensor<T> extends D5TensorShell<T> {
        private final T[][][][][] tensorArray;

       @SuppressWarnings("unchecked") //ObjectArrayTensor will have tensor of specified component type
        private ObjectD5ArrayTensor(ObjectArrayTensor<T> tensor) {
            super(tensor);
            tensorArray = (T[][][][][]) tensor.tensor.getArray();
        }

        public T getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index];
        }

        public void setCell(T value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index] = value;
        }

        public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ObjectD6ArrayTensor<T> extends D6TensorShell<T> {
        private final T[][][][][][] tensorArray;

       @SuppressWarnings("unchecked") //ObjectArrayTensor will have tensor of specified component type
        private ObjectD6ArrayTensor(ObjectArrayTensor<T> tensor) {
            super(tensor);
            tensorArray = (T[][][][][][]) tensor.tensor.getArray();
        }

        public T getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index];
        }

        public void setCell(T value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index] = value;
        }

        public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ObjectD7ArrayTensor<T> extends D7TensorShell<T> {
        private final T[][][][][][][] tensorArray;

       @SuppressWarnings("unchecked") //ObjectArrayTensor will have tensor of specified component type
        private ObjectD7ArrayTensor(ObjectArrayTensor<T> tensor) {
            super(tensor);
            tensorArray = (T[][][][][][][]) tensor.tensor.getArray();
        }

        public T getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index];
        }

        public void setCell(T value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index] = value;
        }

        public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ObjectD8ArrayTensor<T> extends D8TensorShell<T> {
        private final T[][][][][][][][] tensorArray;

       @SuppressWarnings("unchecked") //ObjectArrayTensor will have tensor of specified component type
        private ObjectD8ArrayTensor(ObjectArrayTensor<T> tensor) {
            super(tensor);
            tensorArray = (T[][][][][][][][]) tensor.tensor.getArray();
        }

        public T getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index];
        }

        public void setCell(T value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index] = value;
        }

        public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }
 
    private static class ObjectD9ArrayTensor<T> extends D9TensorShell<T> {
        private final T[][][][][][][][][] tensorArray;

       @SuppressWarnings("unchecked") //ObjectArrayTensor will have tensor of specified component type
        private ObjectD9ArrayTensor(ObjectArrayTensor<T> tensor) {
            super(tensor);
            tensorArray = (T[][][][][][][][][]) tensor.tensor.getArray();
        }

        public T getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            return tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index];
        }

        public void setCell(T value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            tensorArray[d0index][d1index][d2index][d3index][d4index][d5index][d6index][d7index][d8index] = value;
        }

        public void setTensorValues(TypeSafeArray<? extends T> valuesArray) {
            TensorImplUtil.checkSetTensorParameters(this,valuesArray);
            ArrayUtil.copyArray(valuesArray.getArray(),tensorArray);
        }
    }

}
