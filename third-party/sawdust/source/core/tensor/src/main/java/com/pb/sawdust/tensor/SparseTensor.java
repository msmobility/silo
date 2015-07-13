package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.factory.LiterateTensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.tensor.decorators.size.*;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.decorators.primitive.size.*;
import com.pb.sawdust.tensor.alias.scalar.impl.*;

import java.util.Map;
import java.util.HashMap;

/**
 * The {@code SparseTensor} class offers access to "sparse" tensor implementations. A sparse tensor is one filled primarily
 * with the same value (a limited number of "non-default" entries). The memory footprint of a sparse tensor will be lower
 * than an equivalent array or linear tensor (so long as the tensor is actually sparse - if the tensor is non-sparse,
 * then the memory footprint could be significantly worse). The performance of the various tensor methods in this class
 * will be expected to underperform equivalent methods performed on array and linear tensors.
 * <p>
 * The maximum number of tensor elements (default and non-default) allowed in a {@code SparseTensor} is
 * 9,223,372,036,854,775,807 ({@code Long.MAX_VALUE}).
 *
 * @author crf <br/>
 *         Started: Jan 18, 2009 2:00:48 PM
 *         Revised: Dec 14, 2009 12:35:35 PM
 */
public final class SparseTensor extends LiterateTensorFactory {
    private SparseTensor() {}
    
    private static final SparseTensor factory = new SparseTensor();
    
    /**
     Method to get an sparse tensor factory.
     
     @return a {@code SparseTensor} instance.
     */
    public static SparseTensor getFactory() {
        return factory;
    }
    

    public ByteTensor byteTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ByteScalarImpl();
            case 1 : return new ByteD1SparseTensor(new ByteSparseTensor(dimensions));
            case 2 : return new ByteD2SparseTensor(new ByteSparseTensor(dimensions));
            case 3 : return new ByteD3SparseTensor(new ByteSparseTensor(dimensions));
            case 4 : return new ByteD4SparseTensor(new ByteSparseTensor(dimensions));
            case 5 : return new ByteD5SparseTensor(new ByteSparseTensor(dimensions));
            case 6 : return new ByteD6SparseTensor(new ByteSparseTensor(dimensions));
            case 7 : return new ByteD7SparseTensor(new ByteSparseTensor(dimensions));
            case 8 : return new ByteD8SparseTensor(new ByteSparseTensor(dimensions));
            case 9 : return new ByteD9SparseTensor(new ByteSparseTensor(dimensions));
            default : return new ByteSparseTensor(dimensions);
        }
    }
    public ByteTensor initializedByteTensor(byte defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ByteScalarImpl(defaultValue);
            case 1 : return new ByteD1SparseTensor(new ByteSparseTensor(true,defaultValue,dimensions));
            case 2 : return new ByteD2SparseTensor(new ByteSparseTensor(true,defaultValue,dimensions));
            case 3 : return new ByteD3SparseTensor(new ByteSparseTensor(true,defaultValue,dimensions));
            case 4 : return new ByteD4SparseTensor(new ByteSparseTensor(true,defaultValue,dimensions));
            case 5 : return new ByteD5SparseTensor(new ByteSparseTensor(true,defaultValue,dimensions));
            case 6 : return new ByteD6SparseTensor(new ByteSparseTensor(true,defaultValue,dimensions));
            case 7 : return new ByteD7SparseTensor(new ByteSparseTensor(true,defaultValue,dimensions));
            case 8 : return new ByteD8SparseTensor(new ByteSparseTensor(true,defaultValue,dimensions));
            case 9 : return new ByteD9SparseTensor(new ByteSparseTensor(true,defaultValue,dimensions));
            default : return new ByteSparseTensor(true,defaultValue,dimensions);
        }
    }

    public ShortTensor shortTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ShortScalarImpl();
            case 1 : return new ShortD1SparseTensor(new ShortSparseTensor(dimensions));
            case 2 : return new ShortD2SparseTensor(new ShortSparseTensor(dimensions));
            case 3 : return new ShortD3SparseTensor(new ShortSparseTensor(dimensions));
            case 4 : return new ShortD4SparseTensor(new ShortSparseTensor(dimensions));
            case 5 : return new ShortD5SparseTensor(new ShortSparseTensor(dimensions));
            case 6 : return new ShortD6SparseTensor(new ShortSparseTensor(dimensions));
            case 7 : return new ShortD7SparseTensor(new ShortSparseTensor(dimensions));
            case 8 : return new ShortD8SparseTensor(new ShortSparseTensor(dimensions));
            case 9 : return new ShortD9SparseTensor(new ShortSparseTensor(dimensions));
            default : return new ShortSparseTensor(dimensions);
        }
    }
    public ShortTensor initializedShortTensor(short defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ShortScalarImpl(defaultValue);
            case 1 : return new ShortD1SparseTensor(new ShortSparseTensor(true,defaultValue,dimensions));
            case 2 : return new ShortD2SparseTensor(new ShortSparseTensor(true,defaultValue,dimensions));
            case 3 : return new ShortD3SparseTensor(new ShortSparseTensor(true,defaultValue,dimensions));
            case 4 : return new ShortD4SparseTensor(new ShortSparseTensor(true,defaultValue,dimensions));
            case 5 : return new ShortD5SparseTensor(new ShortSparseTensor(true,defaultValue,dimensions));
            case 6 : return new ShortD6SparseTensor(new ShortSparseTensor(true,defaultValue,dimensions));
            case 7 : return new ShortD7SparseTensor(new ShortSparseTensor(true,defaultValue,dimensions));
            case 8 : return new ShortD8SparseTensor(new ShortSparseTensor(true,defaultValue,dimensions));
            case 9 : return new ShortD9SparseTensor(new ShortSparseTensor(true,defaultValue,dimensions));
            default : return new ShortSparseTensor(true,defaultValue,dimensions);
        }
    }

    public IntTensor intTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new IntScalarImpl();
            case 1 : return new IntD1SparseTensor(new IntSparseTensor(dimensions));
            case 2 : return new IntD2SparseTensor(new IntSparseTensor(dimensions));
            case 3 : return new IntD3SparseTensor(new IntSparseTensor(dimensions));
            case 4 : return new IntD4SparseTensor(new IntSparseTensor(dimensions));
            case 5 : return new IntD5SparseTensor(new IntSparseTensor(dimensions));
            case 6 : return new IntD6SparseTensor(new IntSparseTensor(dimensions));
            case 7 : return new IntD7SparseTensor(new IntSparseTensor(dimensions));
            case 8 : return new IntD8SparseTensor(new IntSparseTensor(dimensions));
            case 9 : return new IntD9SparseTensor(new IntSparseTensor(dimensions));
            default : return new IntSparseTensor(dimensions);
        }
    }
    public IntTensor initializedIntTensor(int defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new IntScalarImpl(defaultValue);
            case 1 : return new IntD1SparseTensor(new IntSparseTensor(true,defaultValue,dimensions));
            case 2 : return new IntD2SparseTensor(new IntSparseTensor(true,defaultValue,dimensions));
            case 3 : return new IntD3SparseTensor(new IntSparseTensor(true,defaultValue,dimensions));
            case 4 : return new IntD4SparseTensor(new IntSparseTensor(true,defaultValue,dimensions));
            case 5 : return new IntD5SparseTensor(new IntSparseTensor(true,defaultValue,dimensions));
            case 6 : return new IntD6SparseTensor(new IntSparseTensor(true,defaultValue,dimensions));
            case 7 : return new IntD7SparseTensor(new IntSparseTensor(true,defaultValue,dimensions));
            case 8 : return new IntD8SparseTensor(new IntSparseTensor(true,defaultValue,dimensions));
            case 9 : return new IntD9SparseTensor(new IntSparseTensor(true,defaultValue,dimensions));
            default : return new IntSparseTensor(true,defaultValue,dimensions);
        }
    }

    public LongTensor longTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new LongScalarImpl();
            case 1 : return new LongD1SparseTensor(new LongSparseTensor(dimensions));
            case 2 : return new LongD2SparseTensor(new LongSparseTensor(dimensions));
            case 3 : return new LongD3SparseTensor(new LongSparseTensor(dimensions));
            case 4 : return new LongD4SparseTensor(new LongSparseTensor(dimensions));
            case 5 : return new LongD5SparseTensor(new LongSparseTensor(dimensions));
            case 6 : return new LongD6SparseTensor(new LongSparseTensor(dimensions));
            case 7 : return new LongD7SparseTensor(new LongSparseTensor(dimensions));
            case 8 : return new LongD8SparseTensor(new LongSparseTensor(dimensions));
            case 9 : return new LongD9SparseTensor(new LongSparseTensor(dimensions));
            default : return new LongSparseTensor(dimensions);
        }
    }
    public LongTensor initializedLongTensor(long defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new LongScalarImpl(defaultValue);
            case 1 : return new LongD1SparseTensor(new LongSparseTensor(true,defaultValue,dimensions));
            case 2 : return new LongD2SparseTensor(new LongSparseTensor(true,defaultValue,dimensions));
            case 3 : return new LongD3SparseTensor(new LongSparseTensor(true,defaultValue,dimensions));
            case 4 : return new LongD4SparseTensor(new LongSparseTensor(true,defaultValue,dimensions));
            case 5 : return new LongD5SparseTensor(new LongSparseTensor(true,defaultValue,dimensions));
            case 6 : return new LongD6SparseTensor(new LongSparseTensor(true,defaultValue,dimensions));
            case 7 : return new LongD7SparseTensor(new LongSparseTensor(true,defaultValue,dimensions));
            case 8 : return new LongD8SparseTensor(new LongSparseTensor(true,defaultValue,dimensions));
            case 9 : return new LongD9SparseTensor(new LongSparseTensor(true,defaultValue,dimensions));
            default : return new LongSparseTensor(true,defaultValue,dimensions);
        }
    }

    public FloatTensor floatTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new FloatScalarImpl();
            case 1 : return new FloatD1SparseTensor(new FloatSparseTensor(dimensions));
            case 2 : return new FloatD2SparseTensor(new FloatSparseTensor(dimensions));
            case 3 : return new FloatD3SparseTensor(new FloatSparseTensor(dimensions));
            case 4 : return new FloatD4SparseTensor(new FloatSparseTensor(dimensions));
            case 5 : return new FloatD5SparseTensor(new FloatSparseTensor(dimensions));
            case 6 : return new FloatD6SparseTensor(new FloatSparseTensor(dimensions));
            case 7 : return new FloatD7SparseTensor(new FloatSparseTensor(dimensions));
            case 8 : return new FloatD8SparseTensor(new FloatSparseTensor(dimensions));
            case 9 : return new FloatD9SparseTensor(new FloatSparseTensor(dimensions));
            default : return new FloatSparseTensor(dimensions);
        }
    }
    public FloatTensor initializedFloatTensor(float defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new FloatScalarImpl(defaultValue);
            case 1 : return new FloatD1SparseTensor(new FloatSparseTensor(true,defaultValue,dimensions));
            case 2 : return new FloatD2SparseTensor(new FloatSparseTensor(true,defaultValue,dimensions));
            case 3 : return new FloatD3SparseTensor(new FloatSparseTensor(true,defaultValue,dimensions));
            case 4 : return new FloatD4SparseTensor(new FloatSparseTensor(true,defaultValue,dimensions));
            case 5 : return new FloatD5SparseTensor(new FloatSparseTensor(true,defaultValue,dimensions));
            case 6 : return new FloatD6SparseTensor(new FloatSparseTensor(true,defaultValue,dimensions));
            case 7 : return new FloatD7SparseTensor(new FloatSparseTensor(true,defaultValue,dimensions));
            case 8 : return new FloatD8SparseTensor(new FloatSparseTensor(true,defaultValue,dimensions));
            case 9 : return new FloatD9SparseTensor(new FloatSparseTensor(true,defaultValue,dimensions));
            default : return new FloatSparseTensor(true,defaultValue,dimensions);
        }
    }

    public DoubleTensor doubleTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new DoubleScalarImpl();
            case 1 : return new DoubleD1SparseTensor(new DoubleSparseTensor(dimensions));
            case 2 : return new DoubleD2SparseTensor(new DoubleSparseTensor(dimensions));
            case 3 : return new DoubleD3SparseTensor(new DoubleSparseTensor(dimensions));
            case 4 : return new DoubleD4SparseTensor(new DoubleSparseTensor(dimensions));
            case 5 : return new DoubleD5SparseTensor(new DoubleSparseTensor(dimensions));
            case 6 : return new DoubleD6SparseTensor(new DoubleSparseTensor(dimensions));
            case 7 : return new DoubleD7SparseTensor(new DoubleSparseTensor(dimensions));
            case 8 : return new DoubleD8SparseTensor(new DoubleSparseTensor(dimensions));
            case 9 : return new DoubleD9SparseTensor(new DoubleSparseTensor(dimensions));
            default : return new DoubleSparseTensor(dimensions);
        }
    }
    public DoubleTensor initializedDoubleTensor(double defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new DoubleScalarImpl(defaultValue);
            case 1 : return new DoubleD1SparseTensor(new DoubleSparseTensor(true,defaultValue,dimensions));
            case 2 : return new DoubleD2SparseTensor(new DoubleSparseTensor(true,defaultValue,dimensions));
            case 3 : return new DoubleD3SparseTensor(new DoubleSparseTensor(true,defaultValue,dimensions));
            case 4 : return new DoubleD4SparseTensor(new DoubleSparseTensor(true,defaultValue,dimensions));
            case 5 : return new DoubleD5SparseTensor(new DoubleSparseTensor(true,defaultValue,dimensions));
            case 6 : return new DoubleD6SparseTensor(new DoubleSparseTensor(true,defaultValue,dimensions));
            case 7 : return new DoubleD7SparseTensor(new DoubleSparseTensor(true,defaultValue,dimensions));
            case 8 : return new DoubleD8SparseTensor(new DoubleSparseTensor(true,defaultValue,dimensions));
            case 9 : return new DoubleD9SparseTensor(new DoubleSparseTensor(true,defaultValue,dimensions));
            default : return new DoubleSparseTensor(true,defaultValue,dimensions);
        }
    }

    public CharTensor charTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new CharScalarImpl();
            case 1 : return new CharD1SparseTensor(new CharSparseTensor(dimensions));
            case 2 : return new CharD2SparseTensor(new CharSparseTensor(dimensions));
            case 3 : return new CharD3SparseTensor(new CharSparseTensor(dimensions));
            case 4 : return new CharD4SparseTensor(new CharSparseTensor(dimensions));
            case 5 : return new CharD5SparseTensor(new CharSparseTensor(dimensions));
            case 6 : return new CharD6SparseTensor(new CharSparseTensor(dimensions));
            case 7 : return new CharD7SparseTensor(new CharSparseTensor(dimensions));
            case 8 : return new CharD8SparseTensor(new CharSparseTensor(dimensions));
            case 9 : return new CharD9SparseTensor(new CharSparseTensor(dimensions));
            default : return new CharSparseTensor(dimensions);
        }
    }
    public CharTensor initializedCharTensor(char defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new CharScalarImpl(defaultValue);
            case 1 : return new CharD1SparseTensor(new CharSparseTensor(true,defaultValue,dimensions));
            case 2 : return new CharD2SparseTensor(new CharSparseTensor(true,defaultValue,dimensions));
            case 3 : return new CharD3SparseTensor(new CharSparseTensor(true,defaultValue,dimensions));
            case 4 : return new CharD4SparseTensor(new CharSparseTensor(true,defaultValue,dimensions));
            case 5 : return new CharD5SparseTensor(new CharSparseTensor(true,defaultValue,dimensions));
            case 6 : return new CharD6SparseTensor(new CharSparseTensor(true,defaultValue,dimensions));
            case 7 : return new CharD7SparseTensor(new CharSparseTensor(true,defaultValue,dimensions));
            case 8 : return new CharD8SparseTensor(new CharSparseTensor(true,defaultValue,dimensions));
            case 9 : return new CharD9SparseTensor(new CharSparseTensor(true,defaultValue,dimensions));
            default : return new CharSparseTensor(true,defaultValue,dimensions);
        }
    }

    public BooleanTensor booleanTensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new BooleanScalarImpl();
            case 1 : return new BooleanD1SparseTensor(new BooleanSparseTensor(dimensions));
            case 2 : return new BooleanD2SparseTensor(new BooleanSparseTensor(dimensions));
            case 3 : return new BooleanD3SparseTensor(new BooleanSparseTensor(dimensions));
            case 4 : return new BooleanD4SparseTensor(new BooleanSparseTensor(dimensions));
            case 5 : return new BooleanD5SparseTensor(new BooleanSparseTensor(dimensions));
            case 6 : return new BooleanD6SparseTensor(new BooleanSparseTensor(dimensions));
            case 7 : return new BooleanD7SparseTensor(new BooleanSparseTensor(dimensions));
            case 8 : return new BooleanD8SparseTensor(new BooleanSparseTensor(dimensions));
            case 9 : return new BooleanD9SparseTensor(new BooleanSparseTensor(dimensions));
            default : return new BooleanSparseTensor(dimensions);
        }
    }
    public BooleanTensor initializedBooleanTensor(boolean defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new BooleanScalarImpl(defaultValue);
            case 1 : return new BooleanD1SparseTensor(new BooleanSparseTensor(true,defaultValue,dimensions));
            case 2 : return new BooleanD2SparseTensor(new BooleanSparseTensor(true,defaultValue,dimensions));
            case 3 : return new BooleanD3SparseTensor(new BooleanSparseTensor(true,defaultValue,dimensions));
            case 4 : return new BooleanD4SparseTensor(new BooleanSparseTensor(true,defaultValue,dimensions));
            case 5 : return new BooleanD5SparseTensor(new BooleanSparseTensor(true,defaultValue,dimensions));
            case 6 : return new BooleanD6SparseTensor(new BooleanSparseTensor(true,defaultValue,dimensions));
            case 7 : return new BooleanD7SparseTensor(new BooleanSparseTensor(true,defaultValue,dimensions));
            case 8 : return new BooleanD8SparseTensor(new BooleanSparseTensor(true,defaultValue,dimensions));
            case 9 : return new BooleanD9SparseTensor(new BooleanSparseTensor(true,defaultValue,dimensions));
            default : return new BooleanSparseTensor(true,defaultValue,dimensions);
        }
    }

    public <T> Tensor<T> tensor(int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ScalarImpl<T>();
            case 1 : return new ObjectD1SparseTensor<T>(new ObjectSparseTensor<T>(dimensions));
            case 2 : return new ObjectD2SparseTensor<T>(new ObjectSparseTensor<T>(dimensions));
            case 3 : return new ObjectD3SparseTensor<T>(new ObjectSparseTensor<T>(dimensions));
            case 4 : return new ObjectD4SparseTensor<T>(new ObjectSparseTensor<T>(dimensions));
            case 5 : return new ObjectD5SparseTensor<T>(new ObjectSparseTensor<T>(dimensions));
            case 6 : return new ObjectD6SparseTensor<T>(new ObjectSparseTensor<T>(dimensions));
            case 7 : return new ObjectD7SparseTensor<T>(new ObjectSparseTensor<T>(dimensions));
            case 8 : return new ObjectD8SparseTensor<T>(new ObjectSparseTensor<T>(dimensions));
            case 9 : return new ObjectD9SparseTensor<T>(new ObjectSparseTensor<T>(dimensions));
            default : return new ObjectSparseTensor<T>(dimensions);
        }
    }
    
    public <T> Tensor<T> initializedTensor(T defaultValue, int ... dimensions) {
        switch (dimensions.length) {
            case 0 : return new ScalarImpl<T>(defaultValue);
            case 1 : return new ObjectD1SparseTensor<T>(new ObjectSparseTensor<T>(true,defaultValue,dimensions));
            case 2 : return new ObjectD2SparseTensor<T>(new ObjectSparseTensor<T>(true,defaultValue,dimensions));
            case 3 : return new ObjectD3SparseTensor<T>(new ObjectSparseTensor<T>(true,defaultValue,dimensions));
            case 4 : return new ObjectD4SparseTensor<T>(new ObjectSparseTensor<T>(true,defaultValue,dimensions));
            case 5 : return new ObjectD5SparseTensor<T>(new ObjectSparseTensor<T>(true,defaultValue,dimensions));
            case 6 : return new ObjectD6SparseTensor<T>(new ObjectSparseTensor<T>(true,defaultValue,dimensions));
            case 7 : return new ObjectD7SparseTensor<T>(new ObjectSparseTensor<T>(true,defaultValue,dimensions));
            case 8 : return new ObjectD8SparseTensor<T>(new ObjectSparseTensor<T>(true,defaultValue,dimensions));
            case 9 : return new ObjectD9SparseTensor<T>(new ObjectSparseTensor<T>(true,defaultValue,dimensions));
            default : return new ObjectSparseTensor<T>(true,defaultValue,dimensions);
        }
    }

    private static class SparseIndex {
        private final long[] offsets;

        private SparseIndex(int ... dimensions) {
            offsets = new long[dimensions.length];
            offsets[0] = 1l;
            double f = dimensions[0];
            for (int i = 1; i < dimensions.length; i++) {
                f *= i;
                offsets[i] = offsets[i-1]*dimensions[i-1];
            }
            if (f > (double) Long.MAX_VALUE)
                throw new IllegalStateException("Total number of possible tensor elements cannot exceed " + Long.MAX_VALUE + ", found " + f);
        }

        private long getIndexLocation(int d0Index) {
            return offsets[0]*d0Index;
        }

        private long getIndexLocation(int d0Index, int d1Index) {
            return offsets[0]*d0Index + offsets[1]*d1Index;
        }

        private long getIndexLocation(int d0Index, int d1Index, int d2Index) {
            return offsets[0]*d0Index + offsets[1]*d1Index + offsets[2]*d2Index;
        }

        private long getIndexLocation(int d0Index, int d1Index, int d2Index, int d3Index) {
            return offsets[0]*d0Index + offsets[1]*d1Index + offsets[2]*d2Index + offsets[3]*d3Index;
        }

        private long getIndexLocation(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            return offsets[0]*d0Index + offsets[1]*d1Index + offsets[2]*d2Index + offsets[3]*d3Index + offsets[4]*d4Index;
        }

        private long getIndexLocation(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            return offsets[0]*d0Index + offsets[1]*d1Index + offsets[2]*d2Index + offsets[3]*d3Index + offsets[4]*d4Index + offsets[5]*d5Index;
        }

        private long getIndexLocation(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            return offsets[0]*d0Index + offsets[1]*d1Index + offsets[2]*d2Index + offsets[3]*d3Index + offsets[4]*d4Index + offsets[5]*d5Index + offsets[6]*d6Index;
        }

        private long getIndexLocation(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            return offsets[0]*d0Index + offsets[1]*d1Index + offsets[2]*d2Index + offsets[3]*d3Index + offsets[4]*d4Index + offsets[5]*d5Index + offsets[6]*d6Index + offsets[7]*d7Index;
        }

        private long getIndexLocation(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            return offsets[0]*d0Index + offsets[1]*d1Index + offsets[2]*d2Index + offsets[3]*d3Index + offsets[4]*d4Index + offsets[5]*d5Index + offsets[6]*d6Index + offsets[7]*d7Index + offsets[8]*d8Index;
        }

        private long getIndexLocation(int ... indices) {
            long index = 0;
            for (int i = 0; i < offsets.length; i++)
                index += indices[i]*offsets[i];
            return index;
        }
    }

    private static class ByteSparseTensor extends AbstractByteTensor {
        private final Map<Long,Byte> tensor;
        private SparseIndex index;
        private final byte defaultValue;

        private ByteSparseTensor(boolean defaultIndicator, byte defaultValue, int ... dimensions) {
            super(dimensions);
            tensor = new HashMap<Long,Byte>();
            index = new SparseIndex(dimensions);
            this.defaultValue = defaultValue;
        }

        private ByteSparseTensor(int ... dimensions) {
            this(true,(byte) 0,dimensions);
        }

        public byte getCell(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            return getCellUnchecked(indices);
        }

        public void setCell(byte value, int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            setCellUnchecked(value,indices);
        }

        private byte getCellUnchecked(int ... indices) {
            Long indexPoint = index.getIndexLocation(indices);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(byte value, int ... indices) {
            tensor.put(index.getIndexLocation(indices),value);
        }

        private byte getCellUnchecked(int d0Index) {
            Long indexPoint = index.getIndexLocation(d0Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(byte value, int d0Index) {
            tensor.put(index.getIndexLocation(d0Index),value);
        }

        private byte getCellUnchecked(int d0Index, int d1Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(byte value, int d0Index, int d1Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index),value);
        }

        private byte getCellUnchecked(int d0Index, int d1Index, int d2Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(byte value, int d0Index, int d1Index, int d2Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index),value);
        }

        private byte getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(byte value, int d0Index, int d1Index, int d2Index, int d3Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index),value);
        }

        private byte getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(byte value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index),value);
        }

        private byte getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(byte value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index),value);
        }

        private byte getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(byte value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index),value);
        }

        private byte getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(byte value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index),value);
        }

        private byte getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(byte value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index),value);
        }
    }

    private static class ShortSparseTensor extends AbstractShortTensor {
        private final Map<Long,Short> tensor;
        private SparseIndex index;
        private final short defaultValue;

        private ShortSparseTensor(boolean defaultIndicator, short defaultValue, int ... dimensions) {
            super(dimensions);
            tensor = new HashMap<Long,Short>();
            index = new SparseIndex(dimensions);
            this.defaultValue = defaultValue;
        }

        private ShortSparseTensor(int ... dimensions) {
            this(true,(byte) 0,dimensions);
        }

        public short getCell(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            return getCellUnchecked(indices);
        }

        public void setCell(short value, int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            setCellUnchecked(value,indices);
        }

        private short getCellUnchecked(int ... indices) {
            Long indexPoint = index.getIndexLocation(indices);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(short value, int ... indices) {
            tensor.put(index.getIndexLocation(indices),value);
        }

        private short getCellUnchecked(int d0Index) {
            Long indexPoint = index.getIndexLocation(d0Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(short value, int d0Index) {
            tensor.put(index.getIndexLocation(d0Index),value);
        }

        private short getCellUnchecked(int d0Index, int d1Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(short value, int d0Index, int d1Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index),value);
        }

        private short getCellUnchecked(int d0Index, int d1Index, int d2Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(short value, int d0Index, int d1Index, int d2Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index),value);
        }

        private short getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(short value, int d0Index, int d1Index, int d2Index, int d3Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index),value);
        }

        private short getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(short value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index),value);
        }

        private short getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(short value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index),value);
        }

        private short getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(short value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index),value);
        }

        private short getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(short value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index),value);
        }

        private short getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(short value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index),value);
        }
    }

    private static class IntSparseTensor extends AbstractIntTensor {
        private final Map<Long,Integer> tensor;
        private SparseIndex index;
        private final int defaultValue;

        private IntSparseTensor(boolean defaultIndicator, int defaultValue, int ... dimensions) {
            super(dimensions);
            tensor = new HashMap<Long,Integer>();
            index = new SparseIndex(dimensions);
            this.defaultValue = defaultValue;
        }

        private IntSparseTensor(int ... dimensions) {
            this(true,0,dimensions);
        }

        public int getCell(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            return getCellUnchecked(indices);
        }

        public void setCell(int value, int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            setCellUnchecked(value,indices);
        }

        private int getCellUnchecked(int ... indices) {
            Long indexPoint = index.getIndexLocation(indices);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(int value, int ... indices) {
            tensor.put(index.getIndexLocation(indices),value);
        }

        private int getCellUnchecked(int d0Index) {
            Long indexPoint = index.getIndexLocation(d0Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(int value, int d0Index) {
            tensor.put(index.getIndexLocation(d0Index),value);
        }

        private int getCellUnchecked(int d0Index, int d1Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(int value, int d0Index, int d1Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index),value);
        }

        private int getCellUnchecked(int d0Index, int d1Index, int d2Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(int value, int d0Index, int d1Index, int d2Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index),value);
        }

        private int getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(int value, int d0Index, int d1Index, int d2Index, int d3Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index),value);
        }

        private int getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(int value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index),value);
        }

        private int getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(int value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index),value);
        }

        private int getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(int value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index),value);
        }

        private int getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(int value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index),value);
        }

        private int getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(int value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index),value);
        }
    }

    private static class LongSparseTensor extends AbstractLongTensor {
        private final Map<Long,Long> tensor;
        private SparseIndex index;
        private final long defaultValue;

        private LongSparseTensor(boolean defaultIndicator, long defaultValue, int ... dimensions) {
            super(dimensions);
            tensor = new HashMap<Long,Long>();
            index = new SparseIndex(dimensions);
            this.defaultValue = defaultValue;
        }

        private LongSparseTensor(int ... dimensions) {
            this(true,0l,dimensions);
        }

        public long getCell(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            return getCellUnchecked(indices);
        }

        public void setCell(long value, int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            setCellUnchecked(value,indices);
        }

        private long getCellUnchecked(int ... indices) {
            Long indexPoint = index.getIndexLocation(indices);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(long value, int ... indices) {
            tensor.put(index.getIndexLocation(indices),value);
        }

        private long getCellUnchecked(int d0Index) {
            Long indexPoint = index.getIndexLocation(d0Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(long value, int d0Index) {
            tensor.put(index.getIndexLocation(d0Index),value);
        }

        private long getCellUnchecked(int d0Index, int d1Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(long value, int d0Index, int d1Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index),value);
        }

        private long getCellUnchecked(int d0Index, int d1Index, int d2Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(long value, int d0Index, int d1Index, int d2Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index),value);
        }

        private long getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(long value, int d0Index, int d1Index, int d2Index, int d3Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index),value);
        }

        private long getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(long value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index),value);
        }

        private long getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(long value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index),value);
        }

        private long getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(long value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index),value);
        }

        private long getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(long value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index),value);
        }

        private long getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(long value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index),value);
        }
    }

    private static class FloatSparseTensor extends AbstractFloatTensor {
        private final Map<Long,Float> tensor;
        private SparseIndex index;
        private final float defaultValue;

        private FloatSparseTensor(boolean defaultIndicator, float defaultValue, int ... dimensions) {
            super(dimensions);
            tensor = new HashMap<Long,Float>();
            index = new SparseIndex(dimensions);
            this.defaultValue = defaultValue;
        }

        private FloatSparseTensor(int ... dimensions) {
            this(true,0.0f,dimensions);
        }

        public float getCell(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            return getCellUnchecked(indices);
        }

        public void setCell(float value, int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            setCellUnchecked(value,indices);
        }

        private float getCellUnchecked(int ... indices) {
            Long indexPoint = index.getIndexLocation(indices);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(float value, int ... indices) {
            tensor.put(index.getIndexLocation(indices),value);
        }

        private float getCellUnchecked(int d0Index) {
            Long indexPoint = index.getIndexLocation(d0Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(float value, int d0Index) {
            tensor.put(index.getIndexLocation(d0Index),value);
        }

        private float getCellUnchecked(int d0Index, int d1Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(float value, int d0Index, int d1Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index),value);
        }

        private float getCellUnchecked(int d0Index, int d1Index, int d2Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(float value, int d0Index, int d1Index, int d2Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index),value);
        }

        private float getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(float value, int d0Index, int d1Index, int d2Index, int d3Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index),value);
        }

        private float getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(float value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index),value);
        }

        private float getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(float value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index),value);
        }

        private float getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(float value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index),value);
        }

        private float getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(float value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index),value);
        }

        private float getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(float value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index),value);
        }
    }

    private static class DoubleSparseTensor extends AbstractDoubleTensor {
        private final Map<Long,Double> tensor;
        private SparseIndex index;
        private final double defaultValue;

        private DoubleSparseTensor(boolean defaultIndicator, double defaultValue, int ... dimensions) {
            super(dimensions);
            tensor = new HashMap<Long,Double>();
            index = new SparseIndex(dimensions);
            this.defaultValue = defaultValue;
        }

        private DoubleSparseTensor(int ... dimensions) {
            this(true,0.0d,dimensions);
        }

        public double getCell(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            return getCellUnchecked(indices);
        }

        public void setCell(double value, int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            setCellUnchecked(value,indices);
        }

        private double getCellUnchecked(int ... indices) {
            Long indexPoint = index.getIndexLocation(indices);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(double value, int ... indices) {
            tensor.put(index.getIndexLocation(indices),value);
        }

        private double getCellUnchecked(int d0Index) {
            Long indexPoint = index.getIndexLocation(d0Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(double value, int d0Index) {
            tensor.put(index.getIndexLocation(d0Index),value);
        }

        private double getCellUnchecked(int d0Index, int d1Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(double value, int d0Index, int d1Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index),value);
        }

        private double getCellUnchecked(int d0Index, int d1Index, int d2Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(double value, int d0Index, int d1Index, int d2Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index),value);
        }

        private double getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(double value, int d0Index, int d1Index, int d2Index, int d3Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index),value);
        }

        private double getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(double value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index),value);
        }

        private double getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(double value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index),value);
        }

        private double getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(double value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index),value);
        }

        private double getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(double value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index),value);
        }

        private double getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(double value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index),value);
        }
    }

    private static class CharSparseTensor extends AbstractCharTensor {
        private final Map<Long,Character> tensor;
        private SparseIndex index;
        private final char defaultValue;

        private CharSparseTensor(boolean defaultIndicator, char defaultValue, int ... dimensions) {
            super(dimensions);
            tensor = new HashMap<Long,Character>();
            index = new SparseIndex(dimensions);
            this.defaultValue = defaultValue;
        }

        private CharSparseTensor(int ... dimensions) {
            this(true,'\u0000',dimensions);
        }

        public char getCell(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            return getCellUnchecked(indices);
        }

        public void setCell(char value, int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            setCellUnchecked(value,indices);
        }

        private char getCellUnchecked(int ... indices) {
            Long indexPoint = index.getIndexLocation(indices);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(char value, int ... indices) {
            tensor.put(index.getIndexLocation(indices),value);
        }

        private char getCellUnchecked(int d0Index) {
            Long indexPoint = index.getIndexLocation(d0Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(char value, int d0Index) {
            tensor.put(index.getIndexLocation(d0Index),value);
        }

        private char getCellUnchecked(int d0Index, int d1Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(char value, int d0Index, int d1Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index),value);
        }

        private char getCellUnchecked(int d0Index, int d1Index, int d2Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(char value, int d0Index, int d1Index, int d2Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index),value);
        }

        private char getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(char value, int d0Index, int d1Index, int d2Index, int d3Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index),value);
        }

        private char getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(char value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index),value);
        }

        private char getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(char value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index),value);
        }

        private char getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(char value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index),value);
        }

        private char getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(char value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index),value);
        }

        private char getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(char value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index),value);
        }
    }

    private static class BooleanSparseTensor extends AbstractBooleanTensor {
        private final Map<Long,Boolean> tensor;
        private SparseIndex index;
        private final boolean defaultValue;

        private BooleanSparseTensor(boolean defaultIndicator, boolean defaultValue, int ... dimensions) {
            super(dimensions);
            tensor = new HashMap<Long,Boolean>();
            index = new SparseIndex(dimensions);
            this.defaultValue = defaultValue;
        }

        private BooleanSparseTensor(int ... dimensions) {
            this(true,false,dimensions);
        }

        public boolean getCell(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            return getCellUnchecked(indices);
        }

        public void setCell(boolean value, int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            setCellUnchecked(value,indices);
        }

        private boolean getCellUnchecked(int ... indices) {
            Long indexPoint = index.getIndexLocation(indices);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(boolean value, int ... indices) {
            tensor.put(index.getIndexLocation(indices),value);
        }

        private boolean getCellUnchecked(int d0Index) {
            Long indexPoint = index.getIndexLocation(d0Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(boolean value, int d0Index) {
            tensor.put(index.getIndexLocation(d0Index),value);
        }

        private boolean getCellUnchecked(int d0Index, int d1Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(boolean value, int d0Index, int d1Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index),value);
        }

        private boolean getCellUnchecked(int d0Index, int d1Index, int d2Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(boolean value, int d0Index, int d1Index, int d2Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index),value);
        }

        private boolean getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(boolean value, int d0Index, int d1Index, int d2Index, int d3Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index),value);
        }

        private boolean getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(boolean value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index),value);
        }

        private boolean getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(boolean value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index),value);
        }

        private boolean getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(boolean value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index),value);
        }

        private boolean getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(boolean value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index),value);
        }

        private boolean getCellUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setCellUnchecked(boolean value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index),value);
        }
    }

    private static class ObjectSparseTensor<T> extends AbstractTensor<T> {
        private final Map<Long,T> tensor;
        private SparseIndex index;
        private final T defaultValue;

        private ObjectSparseTensor(boolean defaultIndicator, T defaultValue, int ... dimensions) {
            super(dimensions);
            tensor = new HashMap<Long,T>();
            index = new SparseIndex(dimensions);
            this.defaultValue = defaultValue;
        }

        private ObjectSparseTensor(int ... dimensions) {
            this(true,null,dimensions);
        }

        public JavaType getType() {
            return JavaType.OBJECT;
        }
        
        public T getValue(int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            return getValueUnchecked(indices);
        }

        public void setValue(T value, int ... indices) {
            TensorImplUtil.checkIndices(this,indices);
            setValueUnchecked(value,indices);
        }
                                              
        private T getValueUnchecked(int ... indices) {
            Long indexPoint = index.getIndexLocation(indices);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setValueUnchecked(T value, int ... indices) {
            tensor.put(index.getIndexLocation(indices),value);
        }

        private T getValueUnchecked(int d0Index) {
            Long indexPoint = index.getIndexLocation(d0Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setValueUnchecked(T value, int d0Index) {
            tensor.put(index.getIndexLocation(d0Index),value);
        }

        private T getValueUnchecked(int d0Index, int d1Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setValueUnchecked(T value, int d0Index, int d1Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index),value);
        }

        private T getValueUnchecked(int d0Index, int d1Index, int d2Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setValueUnchecked(T value, int d0Index, int d1Index, int d2Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index),value);
        }

        private T getValueUnchecked(int d0Index, int d1Index, int d2Index, int d3Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setValueUnchecked(T value, int d0Index, int d1Index, int d2Index, int d3Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index),value);
        }

        private T getValueUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setValueUnchecked(T value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index),value);
        }

        private T getValueUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setValueUnchecked(T value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index),value);
        }

        private T getValueUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setValueUnchecked(T value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index),value);
        }

        private T getValueUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setValueUnchecked(T value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index),value);
        }

        private T getValueUnchecked(int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            Long indexPoint = index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index);
            return tensor.containsKey(indexPoint) ? tensor.get(indexPoint) : defaultValue;
        }

        private void setValueUnchecked(T value, int d0Index, int d1Index, int d2Index, int d3Index, int d4Index, int d5Index, int d6Index, int d7Index, int d8Index) {
            tensor.put(index.getIndexLocation(d0Index,d1Index,d2Index,d3Index,d4Index,d5Index,d6Index,d7Index,d8Index),value);
        }
    }
 
    private static class ByteD1SparseTensor extends ByteD1TensorShell {
        private final ByteSparseTensor tensor;
        
        private ByteD1SparseTensor(ByteSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            return tensor.getCellUnchecked(index);
        }
        
        public void setCell(byte value,int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            tensor.setCellUnchecked(value,index);
        }
    }
 
    private static class ShortD1SparseTensor extends ShortD1TensorShell {
        private final ShortSparseTensor tensor;
        
        private ShortD1SparseTensor(ShortSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            return tensor.getCellUnchecked(index);
        }
        
        public void setCell(short value,int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            tensor.setCellUnchecked(value,index);
        }
    }
 
    private static class IntD1SparseTensor extends IntD1TensorShell {
        private final IntSparseTensor tensor;
        
        private IntD1SparseTensor(IntSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            return tensor.getCellUnchecked(index);
        }
        
        public void setCell(int value,int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            tensor.setCellUnchecked(value,index);
        }
    }
 
    private static class LongD1SparseTensor extends LongD1TensorShell {
        private final LongSparseTensor tensor;
        
        private LongD1SparseTensor(LongSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            return tensor.getCellUnchecked(index);
        }
        
        public void setCell(long value,int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            tensor.setCellUnchecked(value,index);
        }
    }
 
    private static class FloatD1SparseTensor extends FloatD1TensorShell {
        private final FloatSparseTensor tensor;
        
        private FloatD1SparseTensor(FloatSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            return tensor.getCellUnchecked(index);
        }
        
        public void setCell(float value,int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            tensor.setCellUnchecked(value,index);
        }
    }
 
    private static class DoubleD1SparseTensor extends DoubleD1TensorShell {
        private final DoubleSparseTensor tensor;
        
        private DoubleD1SparseTensor(DoubleSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            return tensor.getCellUnchecked(index);
        }
        
        public void setCell(double value,int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            tensor.setCellUnchecked(value,index);
        }
    }
 
    private static class CharD1SparseTensor extends CharD1TensorShell {
        private final CharSparseTensor tensor;
        
        private CharD1SparseTensor(CharSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            return tensor.getCellUnchecked(index);
        }
        
        public void setCell(char value,int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            tensor.setCellUnchecked(value,index);
        }
    }
 
    private static class BooleanD1SparseTensor extends BooleanD1TensorShell {
        private final BooleanSparseTensor tensor;
        
        private BooleanD1SparseTensor(BooleanSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            return tensor.getCellUnchecked(index);
        }
        
        public void setCell(boolean value,int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            tensor.setCellUnchecked(value,index);
        }
    }
 
    private static class ByteD2SparseTensor extends ByteD2TensorShell {
        private final ByteSparseTensor tensor;
        
        private ByteD2SparseTensor(ByteSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            return tensor.getCellUnchecked(d0index,d1index);
        }
        
        public void setCell(byte value,int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            tensor.setCellUnchecked(value,d0index,d1index);
        }
    }
 
    private static class ShortD2SparseTensor extends ShortD2TensorShell {
        private final ShortSparseTensor tensor;
        
        private ShortD2SparseTensor(ShortSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            return tensor.getCellUnchecked(d0index,d1index);
        }
        
        public void setCell(short value,int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            tensor.setCellUnchecked(value,d0index,d1index);
        }
    }
 
    private static class IntD2SparseTensor extends IntD2TensorShell {
        private final IntSparseTensor tensor;
        
        private IntD2SparseTensor(IntSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            return tensor.getCellUnchecked(d0index,d1index);
        }
        
        public void setCell(int value,int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            tensor.setCellUnchecked(value,d0index,d1index);
        }
    }
 
    private static class LongD2SparseTensor extends LongD2TensorShell {
        private final LongSparseTensor tensor;
        
        private LongD2SparseTensor(LongSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            return tensor.getCellUnchecked(d0index,d1index);
        }
        
        public void setCell(long value,int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            tensor.setCellUnchecked(value,d0index,d1index);
        }
    }
 
    private static class FloatD2SparseTensor extends FloatD2TensorShell {
        private final FloatSparseTensor tensor;
        
        private FloatD2SparseTensor(FloatSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            return tensor.getCellUnchecked(d0index,d1index);
        }
        
        public void setCell(float value,int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            tensor.setCellUnchecked(value,d0index,d1index);
        }
    }
 
    private static class DoubleD2SparseTensor extends DoubleD2TensorShell {
        private final DoubleSparseTensor tensor;
        
        private DoubleD2SparseTensor(DoubleSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            return tensor.getCellUnchecked(d0index,d1index);
        }
        
        public void setCell(double value,int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            tensor.setCellUnchecked(value,d0index,d1index);
        }
    }
 
    private static class CharD2SparseTensor extends CharD2TensorShell {
        private final CharSparseTensor tensor;
        
        private CharD2SparseTensor(CharSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            return tensor.getCellUnchecked(d0index,d1index);
        }
        
        public void setCell(char value,int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            tensor.setCellUnchecked(value,d0index,d1index);
        }
    }
 
    private static class BooleanD2SparseTensor extends BooleanD2TensorShell {
        private final BooleanSparseTensor tensor;
        
        private BooleanD2SparseTensor(BooleanSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            return tensor.getCellUnchecked(d0index,d1index);
        }
        
        public void setCell(boolean value,int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            tensor.setCellUnchecked(value,d0index,d1index);
        }
    }
 
    private static class ByteD3SparseTensor extends ByteD3TensorShell {
        private final ByteSparseTensor tensor;
        
        private ByteD3SparseTensor(ByteSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            return tensor.getCellUnchecked(d0index,d1index,d2index);
        }
        
        public void setCell(byte value,int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index);
        }
    }
 
    private static class ShortD3SparseTensor extends ShortD3TensorShell {
        private final ShortSparseTensor tensor;
        
        private ShortD3SparseTensor(ShortSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            return tensor.getCellUnchecked(d0index,d1index,d2index);
        }
        
        public void setCell(short value,int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index);
        }
    }
 
    private static class IntD3SparseTensor extends IntD3TensorShell {
        private final IntSparseTensor tensor;
        
        private IntD3SparseTensor(IntSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            return tensor.getCellUnchecked(d0index,d1index,d2index);
        }
        
        public void setCell(int value,int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index);
        }
    }
 
    private static class LongD3SparseTensor extends LongD3TensorShell {
        private final LongSparseTensor tensor;
        
        private LongD3SparseTensor(LongSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            return tensor.getCellUnchecked(d0index,d1index,d2index);
        }
        
        public void setCell(long value,int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index);
        }
    }
 
    private static class FloatD3SparseTensor extends FloatD3TensorShell {
        private final FloatSparseTensor tensor;
        
        private FloatD3SparseTensor(FloatSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            return tensor.getCellUnchecked(d0index,d1index,d2index);
        }
        
        public void setCell(float value,int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index);
        }
    }
 
    private static class DoubleD3SparseTensor extends DoubleD3TensorShell {
        private final DoubleSparseTensor tensor;
        
        private DoubleD3SparseTensor(DoubleSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            return tensor.getCellUnchecked(d0index,d1index,d2index);
        }
        
        public void setCell(double value,int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index);
        }
    }
 
    private static class CharD3SparseTensor extends CharD3TensorShell {
        private final CharSparseTensor tensor;
        
        private CharD3SparseTensor(CharSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            return tensor.getCellUnchecked(d0index,d1index,d2index);
        }
        
        public void setCell(char value,int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index);
        }
    }
 
    private static class BooleanD3SparseTensor extends BooleanD3TensorShell {
        private final BooleanSparseTensor tensor;
        
        private BooleanD3SparseTensor(BooleanSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            return tensor.getCellUnchecked(d0index,d1index,d2index);
        }
        
        public void setCell(boolean value,int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index);
        }
    }
 
    private static class ByteD4SparseTensor extends ByteD4TensorShell {
        private final ByteSparseTensor tensor;
        
        private ByteD4SparseTensor(ByteSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index);
        }
        
        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index);
        }
    }
 
    private static class ShortD4SparseTensor extends ShortD4TensorShell {
        private final ShortSparseTensor tensor;
        
        private ShortD4SparseTensor(ShortSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index);
        }
        
        public void setCell(short value,int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index);
        }
    }
 
    private static class IntD4SparseTensor extends IntD4TensorShell {
        private final IntSparseTensor tensor;
        
        private IntD4SparseTensor(IntSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index);
        }
        
        public void setCell(int value,int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index);
        }
    }
 
    private static class LongD4SparseTensor extends LongD4TensorShell {
        private final LongSparseTensor tensor;
        
        private LongD4SparseTensor(LongSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index);
        }
        
        public void setCell(long value,int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index);
        }
    }
 
    private static class FloatD4SparseTensor extends FloatD4TensorShell {
        private final FloatSparseTensor tensor;
        
        private FloatD4SparseTensor(FloatSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index);
        }
        
        public void setCell(float value,int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index);
        }
    }
 
    private static class DoubleD4SparseTensor extends DoubleD4TensorShell {
        private final DoubleSparseTensor tensor;
        
        private DoubleD4SparseTensor(DoubleSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index);
        }
        
        public void setCell(double value,int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index);
        }
    }
 
    private static class CharD4SparseTensor extends CharD4TensorShell {
        private final CharSparseTensor tensor;
        
        private CharD4SparseTensor(CharSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index);
        }
        
        public void setCell(char value,int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index);
        }
    }
 
    private static class BooleanD4SparseTensor extends BooleanD4TensorShell {
        private final BooleanSparseTensor tensor;
        
        private BooleanD4SparseTensor(BooleanSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index);
        }
        
        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index);
        }
    }
 
    private static class ByteD5SparseTensor extends ByteD5TensorShell {
        private final ByteSparseTensor tensor;
        
        private ByteD5SparseTensor(ByteSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index);
        }
        
        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index);
        }
    }
 
    private static class ShortD5SparseTensor extends ShortD5TensorShell {
        private final ShortSparseTensor tensor;
        
        private ShortD5SparseTensor(ShortSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index);
        }
        
        public void setCell(short value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index);
        }
    }
 
    private static class IntD5SparseTensor extends IntD5TensorShell {
        private final IntSparseTensor tensor;
        
        private IntD5SparseTensor(IntSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index);
        }
        
        public void setCell(int value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index);
        }
    }
 
    private static class LongD5SparseTensor extends LongD5TensorShell {
        private final LongSparseTensor tensor;
        
        private LongD5SparseTensor(LongSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index);
        }
        
        public void setCell(long value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index);
        }
    }
 
    private static class FloatD5SparseTensor extends FloatD5TensorShell {
        private final FloatSparseTensor tensor;
        
        private FloatD5SparseTensor(FloatSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index);
        }
        
        public void setCell(float value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index);
        }
    }
 
    private static class DoubleD5SparseTensor extends DoubleD5TensorShell {
        private final DoubleSparseTensor tensor;
        
        private DoubleD5SparseTensor(DoubleSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index);
        }
        
        public void setCell(double value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index);
        }
    }
 
    private static class CharD5SparseTensor extends CharD5TensorShell {
        private final CharSparseTensor tensor;
        
        private CharD5SparseTensor(CharSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index);
        }
        
        public void setCell(char value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index);
        }
    }
 
    private static class BooleanD5SparseTensor extends BooleanD5TensorShell {
        private final BooleanSparseTensor tensor;
        
        private BooleanD5SparseTensor(BooleanSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index);
        }
        
        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index);
        }
    }
 
    private static class ByteD6SparseTensor extends ByteD6TensorShell {
        private final ByteSparseTensor tensor;
        
        private ByteD6SparseTensor(ByteSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index);
        }
        
        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index);
        }
    }
 
    private static class ShortD6SparseTensor extends ShortD6TensorShell {
        private final ShortSparseTensor tensor;
        
        private ShortD6SparseTensor(ShortSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index);
        }
        
        public void setCell(short value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index);
        }
    }
 
    private static class IntD6SparseTensor extends IntD6TensorShell {
        private final IntSparseTensor tensor;
        
        private IntD6SparseTensor(IntSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index);
        }
        
        public void setCell(int value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index);
        }
    }
 
    private static class LongD6SparseTensor extends LongD6TensorShell {
        private final LongSparseTensor tensor;
        
        private LongD6SparseTensor(LongSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index);
        }
        
        public void setCell(long value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index);
        }
    }
 
    private static class FloatD6SparseTensor extends FloatD6TensorShell {
        private final FloatSparseTensor tensor;
        
        private FloatD6SparseTensor(FloatSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index);
        }
        
        public void setCell(float value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index);
        }
    }
 
    private static class DoubleD6SparseTensor extends DoubleD6TensorShell {
        private final DoubleSparseTensor tensor;
        
        private DoubleD6SparseTensor(DoubleSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index);
        }
        
        public void setCell(double value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index);
        }
    }
 
    private static class CharD6SparseTensor extends CharD6TensorShell {
        private final CharSparseTensor tensor;
        
        private CharD6SparseTensor(CharSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index);
        }
        
        public void setCell(char value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index);
        }
    }
 
    private static class BooleanD6SparseTensor extends BooleanD6TensorShell {
        private final BooleanSparseTensor tensor;
        
        private BooleanD6SparseTensor(BooleanSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index);
        }
        
        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index);
        }
    }
 
    private static class ByteD7SparseTensor extends ByteD7TensorShell {
        private final ByteSparseTensor tensor;
        
        private ByteD7SparseTensor(ByteSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
        
        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
    }
 
    private static class ShortD7SparseTensor extends ShortD7TensorShell {
        private final ShortSparseTensor tensor;
        
        private ShortD7SparseTensor(ShortSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
        
        public void setCell(short value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
    }
 
    private static class IntD7SparseTensor extends IntD7TensorShell {
        private final IntSparseTensor tensor;
        
        private IntD7SparseTensor(IntSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
        
        public void setCell(int value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
    }
 
    private static class LongD7SparseTensor extends LongD7TensorShell {
        private final LongSparseTensor tensor;
        
        private LongD7SparseTensor(LongSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
        
        public void setCell(long value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
    }
 
    private static class FloatD7SparseTensor extends FloatD7TensorShell {
        private final FloatSparseTensor tensor;
        
        private FloatD7SparseTensor(FloatSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
        
        public void setCell(float value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
    }
 
    private static class DoubleD7SparseTensor extends DoubleD7TensorShell {
        private final DoubleSparseTensor tensor;
        
        private DoubleD7SparseTensor(DoubleSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
        
        public void setCell(double value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
    }
 
    private static class CharD7SparseTensor extends CharD7TensorShell {
        private final CharSparseTensor tensor;
        
        private CharD7SparseTensor(CharSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
        
        public void setCell(char value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
    }
 
    private static class BooleanD7SparseTensor extends BooleanD7TensorShell {
        private final BooleanSparseTensor tensor;
        
        private BooleanD7SparseTensor(BooleanSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
        
        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
    }
 
    private static class ByteD8SparseTensor extends ByteD8TensorShell {
        private final ByteSparseTensor tensor;
        
        private ByteD8SparseTensor(ByteSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
        
        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
    }
 
    private static class ShortD8SparseTensor extends ShortD8TensorShell {
        private final ShortSparseTensor tensor;
        
        private ShortD8SparseTensor(ShortSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
        
        public void setCell(short value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
    }
 
    private static class IntD8SparseTensor extends IntD8TensorShell {
        private final IntSparseTensor tensor;
        
        private IntD8SparseTensor(IntSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
        
        public void setCell(int value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
    }
 
    private static class LongD8SparseTensor extends LongD8TensorShell {
        private final LongSparseTensor tensor;
        
        private LongD8SparseTensor(LongSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
        
        public void setCell(long value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
    }
 
    private static class FloatD8SparseTensor extends FloatD8TensorShell {
        private final FloatSparseTensor tensor;
        
        private FloatD8SparseTensor(FloatSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
        
        public void setCell(float value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
    }
 
    private static class DoubleD8SparseTensor extends DoubleD8TensorShell {
        private final DoubleSparseTensor tensor;
        
        private DoubleD8SparseTensor(DoubleSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
        
        public void setCell(double value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
    }
 
    private static class CharD8SparseTensor extends CharD8TensorShell {
        private final CharSparseTensor tensor;
        
        private CharD8SparseTensor(CharSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
        
        public void setCell(char value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
    }
 
    private static class BooleanD8SparseTensor extends BooleanD8TensorShell {
        private final BooleanSparseTensor tensor;
        
        private BooleanD8SparseTensor(BooleanSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
        
        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
    }
 
    private static class ByteD9SparseTensor extends ByteD9TensorShell {
        private final ByteSparseTensor tensor;
        
        private ByteD9SparseTensor(ByteSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public byte getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
        
        public void setCell(byte value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
    }
 
    private static class ShortD9SparseTensor extends ShortD9TensorShell {
        private final ShortSparseTensor tensor;
        
        private ShortD9SparseTensor(ShortSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public short getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
        
        public void setCell(short value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
    }
 
    private static class IntD9SparseTensor extends IntD9TensorShell {
        private final IntSparseTensor tensor;
        
        private IntD9SparseTensor(IntSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public int getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
        
        public void setCell(int value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
    }
 
    private static class LongD9SparseTensor extends LongD9TensorShell {
        private final LongSparseTensor tensor;
        
        private LongD9SparseTensor(LongSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public long getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
        
        public void setCell(long value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
    }
 
    private static class FloatD9SparseTensor extends FloatD9TensorShell {
        private final FloatSparseTensor tensor;
        
        private FloatD9SparseTensor(FloatSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public float getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
        
        public void setCell(float value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
    }
 
    private static class DoubleD9SparseTensor extends DoubleD9TensorShell {
        private final DoubleSparseTensor tensor;
        
        private DoubleD9SparseTensor(DoubleSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public double getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
        
        public void setCell(double value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
    }
 
    private static class CharD9SparseTensor extends CharD9TensorShell {
        private final CharSparseTensor tensor;
        
        private CharD9SparseTensor(CharSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public char getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
        
        public void setCell(char value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
    }
 
    private static class BooleanD9SparseTensor extends BooleanD9TensorShell {
        private final BooleanSparseTensor tensor;
        
        private BooleanD9SparseTensor(BooleanSparseTensor tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public boolean getCell(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            return tensor.getCellUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
        
        public void setCell(boolean value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            tensor.setCellUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
    }
 
    private static class ObjectD1SparseTensor<T> extends D1TensorShell<T> {
    private final ObjectSparseTensor<T> tensor;
    
        private ObjectD1SparseTensor(ObjectSparseTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getValue(int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            return tensor.getValueUnchecked(index);
        }
        
        public void setValue(T value,int index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),index);
            tensor.setValueUnchecked(value,index);
        }
    }
 
    private static class ObjectD2SparseTensor<T> extends D2TensorShell<T> {
    private final ObjectSparseTensor<T> tensor;
    
        private ObjectD2SparseTensor(ObjectSparseTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getValue(int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            return tensor.getValueUnchecked(d0index,d1index);
        }
        
        public void setValue(T value,int d0index, int d1index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index);
            tensor.setValueUnchecked(value,d0index,d1index);
        }
    }
 
    private static class ObjectD3SparseTensor<T> extends D3TensorShell<T> {
    private final ObjectSparseTensor<T> tensor;
    
        private ObjectD3SparseTensor(ObjectSparseTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getValue(int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            return tensor.getValueUnchecked(d0index,d1index,d2index);
        }
        
        public void setValue(T value,int d0index, int d1index, int d2index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index);
            tensor.setValueUnchecked(value,d0index,d1index,d2index);
        }
    }
 
    private static class ObjectD4SparseTensor<T> extends D4TensorShell<T> {
    private final ObjectSparseTensor<T> tensor;
    
        private ObjectD4SparseTensor(ObjectSparseTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getValue(int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            return tensor.getValueUnchecked(d0index,d1index,d2index,d3index);
        }
        
        public void setValue(T value,int d0index, int d1index, int d2index, int d3index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index);
            tensor.setValueUnchecked(value,d0index,d1index,d2index,d3index);
        }
    }
 
    private static class ObjectD5SparseTensor<T> extends D5TensorShell<T> {
    private final ObjectSparseTensor<T> tensor;
    
        private ObjectD5SparseTensor(ObjectSparseTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getValue(int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            return tensor.getValueUnchecked(d0index,d1index,d2index,d3index,d4index);
        }
        
        public void setValue(T value,int d0index, int d1index, int d2index, int d3index, int d4index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index);
            tensor.setValueUnchecked(value,d0index,d1index,d2index,d3index,d4index);
        }
    }
 
    private static class ObjectD6SparseTensor<T> extends D6TensorShell<T> {
    private final ObjectSparseTensor<T> tensor;
    
        private ObjectD6SparseTensor(ObjectSparseTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getValue(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            return tensor.getValueUnchecked(d0index,d1index,d2index,d3index,d4index,d5index);
        }
        
        public void setValue(T value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index);
            tensor.setValueUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index);
        }
    }
 
    private static class ObjectD7SparseTensor<T> extends D7TensorShell<T> {
    private final ObjectSparseTensor<T> tensor;
    
        private ObjectD7SparseTensor(ObjectSparseTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getValue(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            return tensor.getValueUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
        
        public void setValue(T value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index);
            tensor.setValueUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index);
        }
    }
 
    private static class ObjectD8SparseTensor<T> extends D8TensorShell<T> {
    private final ObjectSparseTensor<T> tensor;
    
        private ObjectD8SparseTensor(ObjectSparseTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getValue(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            return tensor.getValueUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
        
        public void setValue(T value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
            tensor.setValueUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index);
        }
    }
 
    private static class ObjectD9SparseTensor<T> extends D9TensorShell<T> {
    private final ObjectSparseTensor<T> tensor;
    
        private ObjectD9SparseTensor(ObjectSparseTensor<T> tensor) {
            super(tensor);
            this.tensor = tensor;
        }
        
        public T getValue(int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            return tensor.getValueUnchecked(d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
        
        public void setValue(T value,int d0index, int d1index, int d2index, int d3index, int d4index, int d5index, int d6index, int d7index, int d8index) {
            TensorImplUtil.checkIndexLengths(getDimensions(),d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
            tensor.setValueUnchecked(value,d0index,d1index,d2index,d3index,d4index,d5index,d6index,d7index,d8index);
        }
    }
}
