package com.pb.sawdust.tensor;

import com.pb.sawdust.util.array.*;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.CollapsingIndex;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.decorators.primitive.size.*;
import com.pb.sawdust.tensor.decorators.size.*;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.decorators.id.IdTensorShell;
import com.pb.sawdust.tensor.decorators.id.size.*;
import com.pb.sawdust.tensor.decorators.id.primitive.size.*;
import com.pb.sawdust.tensor.decorators.id.primitive.*;

import java.util.Arrays;
import java.util.Iterator;

/**
 * The {@code TensorImplUtil} class provides static methods useful for classes iplementing the various {@code Tensor}
 * interfaces. These methods are primarily "checker" methods useful for helping satisfy exception requirements for
 * set out by the various interfaces.
 *
 * @author crf <br/>
 *         Started: Jan 11, 2009 6:13:11 PM
 */
public class TensorImplUtil {

    private TensorImplUtil(){}

    static private void checkIndicesLength(Tensor<?> tensor, int indicesLength) {
        if (indicesLength != tensor.size())
            throw new IllegalArgumentException("Tensor is " + tensor.size() + " dimension in size, getValue passed with " + indicesLength + " indices.");
    }

    /**
     * Check whether a set of indices referring to a point (element) in a tensor is of the correct length.
     *
     * @param tensor
     *        The tensor against which the indices will be checked.
     *
     * @param indices
     *        The indices to check.
     *
     * @throws IllegalArgumentException if {@code indices.length} is not equal to {@code tensor.size()}.
     */
    static public void checkIndicesLength(Tensor<?> tensor, int ... indices) {
        checkIndicesLength(tensor,indices.length);
    }

    /**
     * Check whether a set of ids referring to a point (element) in a tensor is of the correct length.
     *
     * @param tensor
     *        The tensor against which the indices will be checked.
     *
     * @param ids
     *        The indices to check.
     *
     * @throws IllegalArgumentException if {@code ids.length} is not equal to {@code tensor.size()}.
     */
    @SafeVarargs
    static public <I> void checkIndicesLength(Tensor<?> tensor, I ... ids) {
        checkIndicesLength(tensor,ids.length);
    }

    /**
     * Check whether each of the indices referring to a point (element) in a tensor correctly fit within the tensor's
     * dimensional bounds.
     * 
     * @param dimensions
     *        The size (dimensionality) array to check the indices against.
     *
     * @param indices
     *        The indices to check.
     *
     * @throws IllegalArgumentException if any index does not fit within {@code dimensions}.
     */
    static public void checkIndexLengths(int[] dimensions, int ... indices) {
        for (int i = 0; i < indices.length; i++)
            checkIndexLength(i,dimensions[i],indices[i]);
    }

    /**
     * Check whether the index referring to a point (element) in a 1-dimensional tensor correctly fit within the 
     * tensor's dimensional bounds.
     * 
     * @param dimensions
     *        The size (dimensionality) array to check the indices against.
     *
     * @param index0
     *        The index of the first dimension.
     *        
     * @throws IllegalArgumentException if any index does not fit within {@code dimensions}.
     */
    static public void checkIndexLengths(int[] dimensions, int index0) {
        checkIndexLength(0,dimensions[0],index0);
    }

    /**
     * Check whether each of the indices referring to a point (element) in a 2-dimensional tensor correctly fit within the
     * tensor's dimensional bounds.
     * 
     * @param dimensions
     *        The size (dimensionality) array to check the indices against.
     *
     * @param index0
     *        The index of the first dimension.
     *
     * @param index1
     *        The index of the second dimension.
     *
     * @throws IllegalArgumentException if any index does not fit within {@code dimensions}.
     */
    static public void checkIndexLengths(int[] dimensions, int index0, int index1) {
        checkIndexLength(0,dimensions[0],index0);
        checkIndexLength(1,dimensions[1],index1);
    }

    /**
     * Check whether each of the indices referring to a point (element) in a 9-dimensional tensor correctly fit within the 
     * tensor's dimensional bounds.
     * 
     * @param dimensions
     *        The size (dimensionality) array to check the indices against.
     *
     * @param index0
     *        The index of the first dimension.
     *
     * @param index1
     *        The index of the second dimension.
     *
     * @param index2
     *        The index of the third dimension.
     *
     * @throws IllegalArgumentException if any index does not fit within {@code dimensions}.
     */
    static public void checkIndexLengths(int[] dimensions, int index0, int index1, int index2) {
        checkIndexLength(0,dimensions[0],index0);
        checkIndexLength(1,dimensions[1],index1);
        checkIndexLength(2,dimensions[2],index2);
    }

    /**
     * Check whether each of the indices referring to a point (element) in a 9-dimensional tensor correctly fit within the 
     * tensor's dimensional bounds.
     * 
     * @param dimensions
     *        The size (dimensionality) array to check the indices against.
     *
     * @param index0
     *        The index of the first dimension.
     *
     * @param index1
     *        The index of the second dimension.
     *
     * @param index2
     *        The index of the third dimension.
     *
     * @param index3
     *        The index of the fourth dimension.
     * @throws IllegalArgumentException if any index does not fit within {@code dimensions}.
     */
    static public void checkIndexLengths(int[] dimensions, int index0, int index1, int index2, int index3) {
        checkIndexLength(0,dimensions[0],index0);
        checkIndexLength(1,dimensions[1],index1);
        checkIndexLength(2,dimensions[2],index2);
        checkIndexLength(3,dimensions[3],index3);
    }

    /**
     * Check whether each of the indices referring to a point (element) in a 9-dimensional tensor correctly fit within the 
     * tensor's dimensional bounds.
     * 
     * @param dimensions
     *        The size (dimensionality) array to check the indices against.
     *
     * @param index0
     *        The index of the first dimension.
     *
     * @param index1
     *        The index of the second dimension.
     *
     * @param index2
     *        The index of the third dimension.
     *
     * @param index3
     *        The index of the fourth dimension.
     *
     * @param index4
     *        The index of the fifth dimension.
     *
     * @throws IllegalArgumentException if any index does not fit within {@code dimensions}.
     */
    static public void checkIndexLengths(int[] dimensions, int index0, int index1, int index2, int index3, int index4) {
        checkIndexLength(0,dimensions[0],index0);
        checkIndexLength(1,dimensions[1],index1);
        checkIndexLength(2,dimensions[2],index2);
        checkIndexLength(3,dimensions[3],index3);
        checkIndexLength(4,dimensions[4],index4);
    }

    /**
     * Check whether each of the indices referring to a point (element) in a 9-dimensional tensor correctly fit within the 
     * tensor's dimensional bounds.
     * 
     * @param dimensions
     *        The size (dimensionality) array to check the indices against.
     *
     * @param index0
     *        The index of the first dimension.
     *
     * @param index1
     *        The index of the second dimension.
     *
     * @param index2
     *        The index of the third dimension.
     *
     * @param index3
     *        The index of the fourth dimension.
     *
     * @param index4
     *        The index of the fifth dimension.
     *
     * @param index5
     *        The index of the sixth dimension.
     *
     * @throws IllegalArgumentException if any index does not fit within {@code dimensions}.
     */
    static public void checkIndexLengths(int[] dimensions, int index0, int index1, int index2, int index3, int index4, int index5) {
        checkIndexLength(0,dimensions[0],index0);
        checkIndexLength(1,dimensions[1],index1);
        checkIndexLength(2,dimensions[2],index2);
        checkIndexLength(3,dimensions[3],index3);
        checkIndexLength(4,dimensions[4],index4);
        checkIndexLength(5,dimensions[5],index5);
    }

    /**
     * Check whether each of the indices referring to a point (element) in a 9-dimensional tensor correctly fit within the 
     * tensor's dimensional bounds.
     * 
     * @param dimensions
     *        The size (dimensionality) array to check the indices against.
     *
     * @param index0
     *        The index of the first dimension.
     *
     * @param index1
     *        The index of the second dimension.
     *
     * @param index2
     *        The index of the third dimension.
     *
     * @param index3
     *        The index of the fourth dimension.
     *
     * @param index4
     *        The index of the fifth dimension.
     *
     * @param index5
     *        The index of the sixth dimension.
     *
     * @param index6
     *        The index of the seventh dimension.
     *
     * @throws IllegalArgumentException if any index does not fit within {@code dimensions}.
     */
    static public void checkIndexLengths(int[] dimensions, int index0, int index1, int index2, int index3, int index4, int index5, int index6) {
        checkIndexLength(0,dimensions[0],index0);
        checkIndexLength(1,dimensions[1],index1);
        checkIndexLength(2,dimensions[2],index2);
        checkIndexLength(3,dimensions[3],index3);
        checkIndexLength(4,dimensions[4],index4);
        checkIndexLength(5,dimensions[5],index5);
        checkIndexLength(6,dimensions[6],index6);
    }

    /**
     * Check whether each of the indices referring to a point (element) in a 9-dimensional tensor correctly fit within the 
     * tensor's dimensional bounds.
     * 
     * @param dimensions
     *        The size (dimensionality) array to check the indices against.
     *
     * @param index0
     *        The index of the first dimension.
     *
     * @param index1
     *        The index of the second dimension.
     *
     * @param index2
     *        The index of the third dimension.
     *
     * @param index3
     *        The index of the fourth dimension.
     *
     * @param index4
     *        The index of the fifth dimension.
     *
     * @param index5
     *        The index of the sixth dimension.
     *
     * @param index6
     *        The index of the seventh dimension.
     *
     * @param index7
     *        The index of the eighth dimension. 
     *
     * @throws IllegalArgumentException if any index does not fit within {@code dimensions}.
     */
    static public void checkIndexLengths(int[] dimensions, int index0, int index1, int index2, int index3, int index4, int index5, int index6, int index7) {
        checkIndexLength(0,dimensions[0],index0);
        checkIndexLength(1,dimensions[1],index1);
        checkIndexLength(2,dimensions[2],index2);
        checkIndexLength(3,dimensions[3],index3);
        checkIndexLength(4,dimensions[4],index4);
        checkIndexLength(5,dimensions[5],index5);
        checkIndexLength(6,dimensions[6],index6);
        checkIndexLength(7,dimensions[7],index7);
    }

    /**
     * Check whether each of the indices referring to a point (element) in a 9-dimensional tensor correctly fit within the 
     * tensor's dimensional bounds.
     * 
     * @param dimensions
     *        The size (dimensionality) array to check the indices against.
     *
     * @param index0
     *        The index of the first dimension.
     *
     * @param index1
     *        The index of the second dimension.
     *
     * @param index2
     *        The index of the third dimension.
     *
     * @param index3
     *        The index of the fourth dimension.
     *
     * @param index4
     *        The index of the fifth dimension.
     *
     * @param index5
     *        The index of the sixth dimension.
     *
     * @param index6
     *        The index of the seventh dimension.
     *
     * @param index7
     *        The index of the eighth dimension. 
     *
     * @param index8
     *        The index of the ninth dimension.
     *        
     * @throws IllegalArgumentException if any index does not fit within {@code dimensions}.
     */
    static public void checkIndexLengths(int[] dimensions, int index0, int index1, int index2, int index3, int index4, int index5, int index6, int index7, int index8) {
        checkIndexLength(0,dimensions[0],index0);
        checkIndexLength(1,dimensions[1],index1);
        checkIndexLength(2,dimensions[2],index2);
        checkIndexLength(3,dimensions[3],index3);
        checkIndexLength(4,dimensions[4],index4);
        checkIndexLength(5,dimensions[5],index5);
        checkIndexLength(6,dimensions[6],index6);
        checkIndexLength(7,dimensions[7],index7);
        checkIndexLength(8,dimensions[8],index8);
    }

    static private void checkIndexLength(int dimension, int dimensionSize, int index) {
        if (index < 0 || index >= dimensionSize)
            throw new IndexOutOfBoundsException("Tensor index out of bounds for dimension " + dimension + "; dimension size=" + dimensionSize + ", index=" + index);
    }

    /**
     * Check whether the indices referring to a point (element) in a tensor are valid (both in length and index values).
     *
     * @param tensor
     *        The tensor to check against.
     *
     * @param indices
     *        The indices to check.
     *
     * @throws IllegalArgumentException if {@code indices} is not a valid point in {@code tensor}.
     */
    static public void checkIndices(Tensor<?> tensor, int ... indices) {
        checkIndicesLength(tensor,indices);
        checkIndexLengths(tensor.getDimensions(),indices);
    }

    /**
     * Check whether two tensors's are equivalent in the sense that the values of one can be copied to the other.
     *
     * @param destinationTensor
     *        The destination tensor.
     *
     * @param valuesTensor
     *        The source tensor.
     *
     * @throws IllegalArgumentException if the tensors are not equivalent.
     */
    static public void checkSetTensorParameters(Tensor<?> destinationTensor, Tensor<?> valuesTensor) {
        if (!Arrays.equals(destinationTensor.getDimensions(),valuesTensor.getDimensions()))
            throw new IllegalArgumentException("Tensor dimensions do not match: expected " + Arrays.toString(destinationTensor.getDimensions()) + ", found " + Arrays.toString(valuesTensor.getDimensions()));
   }

    /**
     * Check whether a tensor and an array are equivalent in the sense that the values of one can be copied to the other.
     *
     * @param tensor
     *        The tensor.
     *
     * @param array
     *        The array.
     *
     * @throws IllegalArgumentException if the tensor and array are not equivalent.
     */
    static public void checkSetTensorParameters(Tensor<?> tensor, TypeSafeArray<?> array) {
        if (!ArrayUtil.isOfDimension(array.getArray(),tensor.getDimensions()))
            throw new IllegalArgumentException("Tensor and array dimensions do not match: expected " + Arrays.toString(tensor.getDimensions()) + ", found " + Arrays.toString(ArrayUtil.getDimensions(array.getArray())));
    }                       
    
    private static class TypeSafeArrayValueFunction<T> implements TensorUtil.TensorValueFunction<T> {
        private final TypeSafeArray<T> array;
        
        private TypeSafeArrayValueFunction(TypeSafeArray<T> array) {
            this.array = array;
        }
        
        public T getValue(int ... indices) {
            return array.getValue(indices);
        }
    }

    /**
     * Fill a tensor with the values of an array.
     *
     * @param tensor
     *        The tensor.
     *
     * @param array
     *        The array.
     *
     * @param <T>
     *        The type held by the tensor and array.
     *
     * @throws IllegalArgumentException if the tensor and array are not equivalent for copying.
     */
    public static <T> void setTensorValues(Tensor<T> tensor, TypeSafeArray<T> array) {
        checkSetTensorParameters(tensor,array);
        TensorUtil.fill(tensor,new TypeSafeArrayValueFunction<T>(array));
    }     
    
    private static class ByteTypeSafeArrayValueFunction implements TensorUtil.ByteTensorValueFunction {
        private final ByteTypeSafeArray array;
        
        private ByteTypeSafeArrayValueFunction(ByteTypeSafeArray array) {
            this.array = array;
        }
        
        public byte getValue(int ... indices) {
            return array.get(indices);
        }
    }         

    /**
     * Fill a tensor holding {@code byte}s with the values of an array.
     *
     * @param tensor
     *        The tensor.
     *
     * @param array
     *        The array.
     *
     * @throws IllegalArgumentException if the tensor and array are not equivalent for copying.
     */
    public static void setTensorValues(ByteTensor tensor, ByteTypeSafeArray array) {
        checkSetTensorParameters(tensor,array);
        TensorUtil.fill(tensor,new ByteTypeSafeArrayValueFunction(array));
    }                   
    
    private static class ShortTypeSafeArrayValueFunction implements TensorUtil.ShortTensorValueFunction {
        private final ShortTypeSafeArray array;
        
        private ShortTypeSafeArrayValueFunction(ShortTypeSafeArray array) {
            this.array = array;
        }
        
        public short getValue(int ... indices) {
            return array.get(indices);
        }
    }  

    /**
     * Fill a tensor holding {@code short}s with the values of an array.
     *
     * @param tensor
     *        The tensor.
     *
     * @param array
     *        The array.
     *
     * @throws IllegalArgumentException if the tensor and array are not equivalent for copying.
     */
    public static void setTensorValues(ShortTensor tensor, ShortTypeSafeArray array) {
        checkSetTensorParameters(tensor,array);
        TensorUtil.fill(tensor,new ShortTypeSafeArrayValueFunction(array));
    }   
    
    private static class IntTypeSafeArrayValueFunction implements TensorUtil.IntTensorValueFunction {
        private final IntTypeSafeArray array;
        
        private IntTypeSafeArrayValueFunction(IntTypeSafeArray array) {
            this.array = array;
        }
        
        public int getValue(int ... indices) {
            return array.get(indices);
        }
    }         

    /**
     * Fill a tensor holding {@code int}s with the values of an array.
     *
     * @param tensor
     *        The tensor.
     *
     * @param array
     *        The array.
     *
     * @throws IllegalArgumentException if the tensor and array are not equivalent for copying.
     */
    public static void setTensorValues(IntTensor tensor, IntTypeSafeArray array) {
        checkSetTensorParameters(tensor,array);
        TensorUtil.fill(tensor,new IntTypeSafeArrayValueFunction(array));
    }  
    
    private static class LongTypeSafeArrayValueFunction implements TensorUtil.LongTensorValueFunction {
        private final LongTypeSafeArray array;
        
        private LongTypeSafeArrayValueFunction(LongTypeSafeArray array) {
            this.array = array;
        }
        
        public long getValue(int ... indices) {
            return array.get(indices);
        }
    }   

    /**
     * Fill a tensor holding {@code long}s with the values of an array.
     *
     * @param tensor
     *        The tensor.
     *
     * @param array
     *        The array.
     *
     * @throws IllegalArgumentException if the tensor and array are not equivalent for copying.
     */
    public static void setTensorValues(LongTensor tensor, LongTypeSafeArray array) {
        checkSetTensorParameters(tensor,array);
        TensorUtil.fill(tensor,new LongTypeSafeArrayValueFunction(array));
    } 
    
    private static class FloatTypeSafeArrayValueFunction implements TensorUtil.FloatTensorValueFunction {
        private final FloatTypeSafeArray array;
        
        private FloatTypeSafeArrayValueFunction(FloatTypeSafeArray array) {
            this.array = array;
        }
        
        public float getValue(int ... indices) {
            return array.get(indices);
        }
    }       

    /**
     * Fill a tensor holding {@code float}s with the values of an array.
     *
     * @param tensor
     *        The tensor.
     *
     * @param array
     *        The array.
     *
     * @throws IllegalArgumentException if the tensor and array are not equivalent for copying.
     */
    public static void setTensorValues(FloatTensor tensor, FloatTypeSafeArray array) {
        checkSetTensorParameters(tensor,array);
        TensorUtil.fill(tensor,new FloatTypeSafeArrayValueFunction(array));
    }
    
    private static class DoubleTypeSafeArrayValueFunction implements TensorUtil.DoubleTensorValueFunction {
        private final DoubleTypeSafeArray array;
        
        private DoubleTypeSafeArrayValueFunction(DoubleTypeSafeArray array) {
            this.array = array;
        }
        
        public double getValue(int ... indices) {
            return array.get(indices);
        }
    }

    /**
     * Fill a tensor holding {@code double}s with the values of an array.
     *
     * @param tensor
     *        The tensor.
     *
     * @param array
     *        The array.
     *
     * @throws IllegalArgumentException if the tensor and array are not equivalent for copying.
     */
    public static void setTensorValues(DoubleTensor tensor, DoubleTypeSafeArray array) {
        checkSetTensorParameters(tensor,array);
        TensorUtil.fill(tensor,new DoubleTypeSafeArrayValueFunction(array));
    }    
    
    private static class BooleanTypeSafeArrayValueFunction implements TensorUtil.BooleanTensorValueFunction {
        private final BooleanTypeSafeArray array;
        
        private BooleanTypeSafeArrayValueFunction(BooleanTypeSafeArray array) {
            this.array = array;
        }
        
        public boolean getValue(int ... indices) {
            return array.get(indices);
        }
    }     

    /**
     * Fill a tensor holding {@code boolean}s with the values of an array.
     *
     * @param tensor
     *        The tensor.
     *
     * @param array
     *        The array.
     *
     * @throws IllegalArgumentException if the tensor and array are not equivalent for copying.
     */
    public static void setTensorValues(BooleanTensor tensor, BooleanTypeSafeArray array) {
        checkSetTensorParameters(tensor,array);
        TensorUtil.fill(tensor,new BooleanTypeSafeArrayValueFunction(array));
    }       
    
    private static class CharTypeSafeArrayValueFunction implements TensorUtil.CharTensorValueFunction {
        private final CharTypeSafeArray array;
        
        private CharTypeSafeArrayValueFunction(CharTypeSafeArray array) {
            this.array = array;
        }
        
        public char getValue(int ... indices) {
            return array.get(indices);
        }
    }      

    /**
     * Fill a tensor holding {@code char}s with the values of an array.
     *
     * @param tensor
     *        The tensor.
     *
     * @param array
     *        The array.
     *
     * @throws IllegalArgumentException if the tensor and array are not equivalent for copying.
     */
    public static void setTensorValues(CharTensor tensor, CharTypeSafeArray array) {
        checkSetTensorParameters(tensor,array);
        TensorUtil.fill(tensor,new CharTypeSafeArrayValueFunction(array));
    }

    /**
     * Get an iterator that can be used to satisfy the iterator requirements for the {@code Tensor} interface. Specifically,
     * the indices cycled trough by the iterator returned by this method can be used (in order) with the tensor's
     * {@code getReferenceTensor(Index)} method to create a tensor iterator satisfying {@code Tensor#iterator()}.
     *
     * @param index
     *        The index to build the iterator from.
     *
     * @param <I>
     *        The type of ids used in the index.
     *
     * @return an index iterator that cycles over sub-indexes satisfying the {@code Tensor} iterator requirments.
     */
    @SuppressWarnings("unchecked") //scalar iterator array is ok, because generic array will be assumed, not enforced
    public static <I> Iterator<Index<I>> getIndexIterator(final Index<I> index) {
        return getIndexIterator(index,index.size()-1);
    }

    /**
     * Get an iterator over the sub-indices for a given tensor an dimension. That is, the returned iterator will cycle
     * through the each of the possible values of the index's iterationg dimension and return sub-indices representing
     * the index with its iterating dimension held constant at that value.
     *
     * @param index
     *        The index to build the iterator from.
     *
     * @param iteratingDimension
     *        The dimension to iterate over.
     *
     * @param <I>
     *        The type of ids used in the index.
     *
     * @return an index iterator that cycles over sub-indexes satisfying the {@code Tensor} iterator requirments.
     */
    @SuppressWarnings("unchecked") //scalar iterator array is ok, because generic array will be assumed, not enforced
    public static <I> Iterator<Index<I>> getIndexIterator(final Index<I> index, final int iteratingDimension) {
        final boolean isRank0 = index.size() == 0;
        final int endSize = isRank0 ? 1 : index.size(iteratingDimension);
        return new Iterator<Index<I>>() {
            private int position = 0;
            public boolean hasNext() {
                return position < endSize;
            }

            public Index<I> next() {
                //if 0 rank then iterates over itself
                Index<I> i = isRank0 ? index : new CollapsingIndex<I>(index,iteratingDimension,position);
                position++;
                return i;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Get a reference tensor based on a specified index. The returned tensor is "composed" because it is built from
     * an already existing tensor. This method <i>does not</i> check to ensure that the index is valid for the source
     * tensor.
     *
     * @param tensor
     *        The source tensor.
     *
     * @param index
     *        The index to build the reference tensor from.
     *
     * @param <T>
     *        The type held by the tensor and its reference.
     *
     * @return a reference tensor based on {@code tensor} and {@code index}.
     */
    public static <T> Tensor<T> getComposedTensor(Tensor<T> tensor, Index<?> index) {
        switch (index.size()) {
            case 0 : return new ComposedTensor.ComposedD0Tensor<T>(tensor,index);
            case 1 : return new ComposedTensor.ComposedD1Tensor<T>(tensor,index);
            case 2 : return new ComposedTensor.ComposedD2Tensor<T>(tensor,index);
            case 3 : return new ComposedTensor.ComposedD3Tensor<T>(tensor,index);
            case 4 : return new ComposedTensor.ComposedD4Tensor<T>(tensor,index);
            case 5 : return new ComposedTensor.ComposedD5Tensor<T>(tensor,index);
            case 6 : return new ComposedTensor.ComposedD6Tensor<T>(tensor,index);
            case 7 : return new ComposedTensor.ComposedD7Tensor<T>(tensor,index);
            case 8 : return new ComposedTensor.ComposedD8Tensor<T>(tensor,index);
            case 9 : return new ComposedTensor.ComposedD9Tensor<T>(tensor,index);
            default : return new ComposedTensor.ComposedObjectTensor<T>(tensor,index);
        }
    }

    /**
     * Get a reference tensor holding {@code boolean}s based on a specified index. The returned tensor is "composed" because
     * it is built from an already existing tensor. This method <i>does not</i> check to ensure that the index is valid for
     * the source tensor.
     *
     * @param tensor
     *        The source tensor.
     *
     * @param index
     *        The index to build the reference tensor from.
     *
     * @return a reference tensor based on {@code tensor} and {@code index}.
     */
    public static BooleanTensor getComposedTensor(BooleanTensor tensor, Index<?> index) {
        switch (index.size()) {
            case 0 : return new ComposedTensor.ComposedBooleanD0Tensor(tensor,index);
            case 1 : return new ComposedTensor.ComposedBooleanD1Tensor(tensor,index);
            case 2 : return new ComposedTensor.ComposedBooleanD2Tensor(tensor,index);
            case 3 : return new ComposedTensor.ComposedBooleanD3Tensor(tensor,index);
            case 4 : return new ComposedTensor.ComposedBooleanD4Tensor(tensor,index);
            case 5 : return new ComposedTensor.ComposedBooleanD5Tensor(tensor,index);
            case 6 : return new ComposedTensor.ComposedBooleanD6Tensor(tensor,index);
            case 7 : return new ComposedTensor.ComposedBooleanD7Tensor(tensor,index);
            case 8 : return new ComposedTensor.ComposedBooleanD8Tensor(tensor,index);
            case 9 : return new ComposedTensor.ComposedBooleanD9Tensor(tensor,index);
            default : return new ComposedTensor.ComposedBooleanTensor(tensor,index);
        }
    }

    /**
     * Get a reference tensor holding {@code char}s based on a specified index. The returned tensor is "composed" because
     * it is built from an already existing tensor. This method <i>does not</i> check to ensure that the index is valid for
     * the source tensor.
     *
     * @param tensor
     *        The source tensor.
     *
     * @param index
     *        The index to build the reference tensor from.
     *
     * @return a reference tensor based on {@code tensor} and {@code index}.
     */
    public static CharTensor getComposedTensor(CharTensor tensor, Index<?> index) {
        switch (index.size()) {
            case 0 : return new ComposedTensor.ComposedCharD0Tensor(tensor,index);
            case 1 : return new ComposedTensor.ComposedCharD1Tensor(tensor,index);
            case 2 : return new ComposedTensor.ComposedCharD2Tensor(tensor,index);
            case 3 : return new ComposedTensor.ComposedCharD3Tensor(tensor,index);
            case 4 : return new ComposedTensor.ComposedCharD4Tensor(tensor,index);
            case 5 : return new ComposedTensor.ComposedCharD5Tensor(tensor,index);
            case 6 : return new ComposedTensor.ComposedCharD6Tensor(tensor,index);
            case 7 : return new ComposedTensor.ComposedCharD7Tensor(tensor,index);
            case 8 : return new ComposedTensor.ComposedCharD8Tensor(tensor,index);
            case 9 : return new ComposedTensor.ComposedCharD9Tensor(tensor,index);
            default : return new ComposedTensor.ComposedCharTensor(tensor,index);
        }
    }

    /**
     * Get a reference tensor holding {@code byte}s based on a specified index. The returned tensor is "composed" because
     * it is built from an already existing tensor. This method <i>does not</i> check to ensure that the index is valid for
     * the source tensor.
     *
     * @param tensor
     *        The source tensor.
     *
     * @param index
     *        The index to build the reference tensor from.
     *
     * @return a reference tensor based on {@code tensor} and {@code index}.
     */
    public static ByteTensor getComposedTensor(ByteTensor tensor, Index<?> index) {
        switch (index.size()) {
            case 0 : return new ComposedTensor.ComposedByteD0Tensor(tensor,index);
            case 1 : return new ComposedTensor.ComposedByteD1Tensor(tensor,index);
            case 2 : return new ComposedTensor.ComposedByteD2Tensor(tensor,index);
            case 3 : return new ComposedTensor.ComposedByteD3Tensor(tensor,index);
            case 4 : return new ComposedTensor.ComposedByteD4Tensor(tensor,index);
            case 5 : return new ComposedTensor.ComposedByteD5Tensor(tensor,index);
            case 6 : return new ComposedTensor.ComposedByteD6Tensor(tensor,index);
            case 7 : return new ComposedTensor.ComposedByteD7Tensor(tensor,index);
            case 8 : return new ComposedTensor.ComposedByteD8Tensor(tensor,index);
            case 9 : return new ComposedTensor.ComposedByteD9Tensor(tensor,index);
            default : return new ComposedTensor.ComposedByteTensor(tensor,index);
        }
    }

    /**
     * Get a reference tensor holding {@code short}s based on a specified index. The returned tensor is "composed" because
     * it is built from an already existing tensor. This method <i>does not</i> check to ensure that the index is valid for
     * the source tensor.
     *
     * @param tensor
     *        The source tensor.
     *
     * @param index
     *        The index to build the reference tensor from.
     *
     * @return a reference tensor based on {@code tensor} and {@code index}.
     */
    public static ShortTensor getComposedTensor(ShortTensor tensor, Index<?> index) {
        switch (index.size()) {
            case 0 : return new ComposedTensor.ComposedShortD0Tensor(tensor,index);
            case 1 : return new ComposedTensor.ComposedShortD1Tensor(tensor,index);
            case 2 : return new ComposedTensor.ComposedShortD2Tensor(tensor,index);
            case 3 : return new ComposedTensor.ComposedShortD3Tensor(tensor,index);
            case 4 : return new ComposedTensor.ComposedShortD4Tensor(tensor,index);
            case 5 : return new ComposedTensor.ComposedShortD5Tensor(tensor,index);
            case 6 : return new ComposedTensor.ComposedShortD6Tensor(tensor,index);
            case 7 : return new ComposedTensor.ComposedShortD7Tensor(tensor,index);
            case 8 : return new ComposedTensor.ComposedShortD8Tensor(tensor,index);
            case 9 : return new ComposedTensor.ComposedShortD9Tensor(tensor,index);
            default : return new ComposedTensor.ComposedShortTensor(tensor,index);
        }
    }

    /**
     * Get a reference tensor holding {@code int}s based on a specified index. The returned tensor is "composed" because
     * it is built from an already existing tensor. This method <i>does not</i> check to ensure that the index is valid for
     * the source tensor.
     *
     * @param tensor
     *        The source tensor.
     *
     * @param index
     *        The index to build the reference tensor from.
     *
     * @return a reference tensor based on {@code tensor} and {@code index}.
     */
    public static IntTensor getComposedTensor(IntTensor tensor, Index<?> index) {
        switch (index.size()) {
            case 0 : return new ComposedTensor.ComposedIntD0Tensor(tensor,index);
            case 1 : return new ComposedTensor.ComposedIntD1Tensor(tensor,index);
            case 2 : return new ComposedTensor.ComposedIntD2Tensor(tensor,index);
            case 3 : return new ComposedTensor.ComposedIntD3Tensor(tensor,index);
            case 4 : return new ComposedTensor.ComposedIntD4Tensor(tensor,index);
            case 5 : return new ComposedTensor.ComposedIntD5Tensor(tensor,index);
            case 6 : return new ComposedTensor.ComposedIntD6Tensor(tensor,index);
            case 7 : return new ComposedTensor.ComposedIntD7Tensor(tensor,index);
            case 8 : return new ComposedTensor.ComposedIntD8Tensor(tensor,index);
            case 9 : return new ComposedTensor.ComposedIntD9Tensor(tensor,index);
            default : return new ComposedTensor.ComposedIntTensor(tensor,index);
        }
    }

    /**
     * Get a reference tensor holding {@code long}s based on a specified index. The returned tensor is "composed" because
     * it is built from an already existing tensor. This method <i>does not</i> check to ensure that the index is valid for
     * the source tensor.
     *
     * @param tensor
     *        The source tensor.
     *
     * @param index
     *        The index to build the reference tensor from.
     *
     * @return a reference tensor based on {@code tensor} and {@code index}.
     */
    public static LongTensor getComposedTensor(LongTensor tensor, Index<?> index) {
        switch (index.size()) {
            case 0 : return new ComposedTensor.ComposedLongD0Tensor(tensor,index);
            case 1 : return new ComposedTensor.ComposedLongD1Tensor(tensor,index);
            case 2 : return new ComposedTensor.ComposedLongD2Tensor(tensor,index);
            case 3 : return new ComposedTensor.ComposedLongD3Tensor(tensor,index);
            case 4 : return new ComposedTensor.ComposedLongD4Tensor(tensor,index);
            case 5 : return new ComposedTensor.ComposedLongD5Tensor(tensor,index);
            case 6 : return new ComposedTensor.ComposedLongD6Tensor(tensor,index);
            case 7 : return new ComposedTensor.ComposedLongD7Tensor(tensor,index);
            case 8 : return new ComposedTensor.ComposedLongD8Tensor(tensor,index);
            case 9 : return new ComposedTensor.ComposedLongD9Tensor(tensor,index);
            default : return new ComposedTensor.ComposedLongTensor(tensor,index);
        }
    }

    /**
     * Get a reference tensor holding {@code float}s based on a specified index. The returned tensor is "composed" because
     * it is built from an already existing tensor. This method <i>does not</i> check to ensure that the index is valid for
     * the source tensor.
     *
     * @param tensor
     *        The source tensor.
     *
     * @param index
     *        The index to build the reference tensor from.
     *
     * @return a reference tensor based on {@code tensor} and {@code index}.
     */
    public static FloatTensor getComposedTensor(FloatTensor tensor, Index<?> index) {
        switch (index.size()) {
            case 0 : return new ComposedTensor.ComposedFloatD0Tensor(tensor,index);
            case 1 : return new ComposedTensor.ComposedFloatD1Tensor(tensor,index);
            case 2 : return new ComposedTensor.ComposedFloatD2Tensor(tensor,index);
            case 3 : return new ComposedTensor.ComposedFloatD3Tensor(tensor,index);
            case 4 : return new ComposedTensor.ComposedFloatD4Tensor(tensor,index);
            case 5 : return new ComposedTensor.ComposedFloatD5Tensor(tensor,index);
            case 6 : return new ComposedTensor.ComposedFloatD6Tensor(tensor,index);
            case 7 : return new ComposedTensor.ComposedFloatD7Tensor(tensor,index);
            case 8 : return new ComposedTensor.ComposedFloatD8Tensor(tensor,index);
            case 9 : return new ComposedTensor.ComposedFloatD9Tensor(tensor,index);
            default : return new ComposedTensor.ComposedFloatTensor(tensor,index);
        }
    }

    /**
     * Get a reference tensor holding {@code double}s based on a specified index. The returned tensor is "composed" because
     * it is built from an already existing tensor. This method <i>does not</i> check to ensure that the index is valid for
     * the source tensor.
     *
     * @param tensor
     *        The source tensor.
     *
     * @param index
     *        The index to build the reference tensor from.
     *
     * @return a reference tensor based on {@code tensor} and {@code index}.
     */
    public static DoubleTensor getComposedTensor(DoubleTensor tensor, Index<?> index) {
        switch (index.size()) {
            case 0 : return new ComposedTensor.ComposedDoubleD0Tensor(tensor,index);
            case 1 : return new ComposedTensor.ComposedDoubleD1Tensor(tensor,index);
            case 2 : return new ComposedTensor.ComposedDoubleD2Tensor(tensor,index);
            case 3 : return new ComposedTensor.ComposedDoubleD3Tensor(tensor,index);
            case 4 : return new ComposedTensor.ComposedDoubleD4Tensor(tensor,index);
            case 5 : return new ComposedTensor.ComposedDoubleD5Tensor(tensor,index);
            case 6 : return new ComposedTensor.ComposedDoubleD6Tensor(tensor,index);
            case 7 : return new ComposedTensor.ComposedDoubleD7Tensor(tensor,index);
            case 8 : return new ComposedTensor.ComposedDoubleD8Tensor(tensor,index);
            case 9 : return new ComposedTensor.ComposedDoubleD9Tensor(tensor,index);
            default : return new ComposedTensor.ComposedDoubleTensor(tensor,index);
        }
    }

    /**
     * Get a tensor which implements the appropriate size and type interfaces for a source tensor. That is, the returned
     * tensor will be a reference to the source tensor, implementing the most specific size and type interfaces from
     * the subpackages of {@code com.pb.sawdust.tensor.decorators}.
     *
     * @param tensor
     *        The original tensor.
     *
     * @param <T>
     *        The type held by the tensor.
     *
     * @return a tensor equivalent to {@code tensor} implementing the appropriate size and type interfaces.
     */
    public static <T> Tensor<T> tensorCaster(Tensor<T> tensor) {
        return sizePrimitiveCaster(tensor);
    }

    @SuppressWarnings("unchecked") //cast checking makes this ok
    private static <T> Tensor<T> sizePrimitiveCaster(Tensor<T> tensor) {
        switch (tensor.getType()) {
            case BOOLEAN : {
                if (!(tensor instanceof BooleanTensor)) break;
                switch (tensor.size()) {
                    case 0 : return tensor instanceof BooleanD0Tensor ? tensor : (Tensor<T>) new BooleanD0TensorShell((BooleanTensor)  tensor);
                    case 1 : return tensor instanceof BooleanD1Tensor ? tensor : (Tensor<T>) new BooleanD1TensorShell((BooleanTensor)  tensor);
                    case 2 : return tensor instanceof BooleanD2Tensor ? tensor : (Tensor<T>) new BooleanD2TensorShell((BooleanTensor)  tensor);
                    case 3 : return tensor instanceof BooleanD3Tensor ? tensor : (Tensor<T>) new BooleanD3TensorShell((BooleanTensor)  tensor);
                    case 4 : return tensor instanceof BooleanD4Tensor ? tensor : (Tensor<T>) new BooleanD4TensorShell((BooleanTensor)  tensor);
                    case 5 : return tensor instanceof BooleanD5Tensor ? tensor : (Tensor<T>) new BooleanD5TensorShell((BooleanTensor)  tensor);
                    case 6 : return tensor instanceof BooleanD6Tensor ? tensor : (Tensor<T>) new BooleanD6TensorShell((BooleanTensor)  tensor);
                    case 7 : return tensor instanceof BooleanD7Tensor ? tensor : (Tensor<T>) new BooleanD7TensorShell((BooleanTensor)  tensor);
                    case 8 : return tensor instanceof BooleanD8Tensor ? tensor : (Tensor<T>) new BooleanD8TensorShell((BooleanTensor)  tensor);
                    case 9 : return tensor instanceof BooleanD9Tensor ? tensor : (Tensor<T>) new BooleanD9TensorShell((BooleanTensor)  tensor);
                    default : return tensor;
                }
            }
            case BYTE : {
                if (!(tensor instanceof ByteTensor)) break;
                switch (tensor.size()) {
                    case 0 : return tensor instanceof ByteD0Tensor ? tensor : (Tensor<T>) new ByteD0TensorShell((ByteTensor)  tensor);
                    case 1 : return tensor instanceof ByteD1Tensor ? tensor : (Tensor<T>) new ByteD1TensorShell((ByteTensor)  tensor);
                    case 2 : return tensor instanceof ByteD2Tensor ? tensor : (Tensor<T>) new ByteD2TensorShell((ByteTensor)  tensor);
                    case 3 : return tensor instanceof ByteD3Tensor ? tensor : (Tensor<T>) new ByteD3TensorShell((ByteTensor)  tensor);
                    case 4 : return tensor instanceof ByteD4Tensor ? tensor : (Tensor<T>) new ByteD4TensorShell((ByteTensor)  tensor);
                    case 5 : return tensor instanceof ByteD5Tensor ? tensor : (Tensor<T>) new ByteD5TensorShell((ByteTensor)  tensor);
                    case 6 : return tensor instanceof ByteD6Tensor ? tensor : (Tensor<T>) new ByteD6TensorShell((ByteTensor)  tensor);
                    case 7 : return tensor instanceof ByteD7Tensor ? tensor : (Tensor<T>) new ByteD7TensorShell((ByteTensor)  tensor);
                    case 8 : return tensor instanceof ByteD8Tensor ? tensor : (Tensor<T>) new ByteD8TensorShell((ByteTensor)  tensor);
                    case 9 : return tensor instanceof ByteD9Tensor ? tensor : (Tensor<T>) new ByteD9TensorShell((ByteTensor)  tensor);
                    default : return tensor;
                }
            }
            case CHAR : {
                if (!(tensor instanceof CharTensor)) break;
                switch (tensor.size()) {
                    case 0 : return tensor instanceof CharD0Tensor ? tensor : (Tensor<T>) new CharD0TensorShell((CharTensor)  tensor);
                    case 1 : return tensor instanceof CharD1Tensor ? tensor : (Tensor<T>) new CharD1TensorShell((CharTensor)  tensor);
                    case 2 : return tensor instanceof CharD2Tensor ? tensor : (Tensor<T>) new CharD2TensorShell((CharTensor)  tensor);
                    case 3 : return tensor instanceof CharD3Tensor ? tensor : (Tensor<T>) new CharD3TensorShell((CharTensor)  tensor);
                    case 4 : return tensor instanceof CharD4Tensor ? tensor : (Tensor<T>) new CharD4TensorShell((CharTensor)  tensor);
                    case 5 : return tensor instanceof CharD5Tensor ? tensor : (Tensor<T>) new CharD5TensorShell((CharTensor)  tensor);
                    case 6 : return tensor instanceof CharD6Tensor ? tensor : (Tensor<T>) new CharD6TensorShell((CharTensor)  tensor);
                    case 7 : return tensor instanceof CharD7Tensor ? tensor : (Tensor<T>) new CharD7TensorShell((CharTensor)  tensor);
                    case 8 : return tensor instanceof CharD8Tensor ? tensor : (Tensor<T>) new CharD8TensorShell((CharTensor)  tensor);
                    case 9 : return tensor instanceof CharD9Tensor ? tensor : (Tensor<T>) new CharD9TensorShell((CharTensor)  tensor);
                    default : return tensor;
                }
            }
            case DOUBLE : {
                if (!(tensor instanceof DoubleTensor)) break;
                switch (tensor.size()) {
                    case 0 : return tensor instanceof DoubleD0Tensor ? tensor : (Tensor<T>) new DoubleD0TensorShell((DoubleTensor)  tensor);
                    case 1 : return tensor instanceof DoubleD1Tensor ? tensor : (Tensor<T>) new DoubleD1TensorShell((DoubleTensor)  tensor);
                    case 2 : return tensor instanceof DoubleD2Tensor ? tensor : (Tensor<T>) new DoubleD2TensorShell((DoubleTensor)  tensor);
                    case 3 : return tensor instanceof DoubleD3Tensor ? tensor : (Tensor<T>) new DoubleD3TensorShell((DoubleTensor)  tensor);
                    case 4 : return tensor instanceof DoubleD4Tensor ? tensor : (Tensor<T>) new DoubleD4TensorShell((DoubleTensor)  tensor);
                    case 5 : return tensor instanceof DoubleD5Tensor ? tensor : (Tensor<T>) new DoubleD5TensorShell((DoubleTensor)  tensor);
                    case 6 : return tensor instanceof DoubleD6Tensor ? tensor : (Tensor<T>) new DoubleD6TensorShell((DoubleTensor)  tensor);
                    case 7 : return tensor instanceof DoubleD7Tensor ? tensor : (Tensor<T>) new DoubleD7TensorShell((DoubleTensor)  tensor);
                    case 8 : return tensor instanceof DoubleD8Tensor ? tensor : (Tensor<T>) new DoubleD8TensorShell((DoubleTensor)  tensor);
                    case 9 : return tensor instanceof DoubleD9Tensor ? tensor : (Tensor<T>) new DoubleD9TensorShell((DoubleTensor)  tensor);
                    default : return tensor;
                }
            }
            case FLOAT : {
                if (!(tensor instanceof FloatTensor)) break;
                switch (tensor.size()) {
                    case 0 : return tensor instanceof FloatD0Tensor ? tensor : (Tensor<T>) new FloatD0TensorShell((FloatTensor)  tensor);
                    case 1 : return tensor instanceof FloatD1Tensor ? tensor : (Tensor<T>) new FloatD1TensorShell((FloatTensor)  tensor);
                    case 2 : return tensor instanceof FloatD2Tensor ? tensor : (Tensor<T>) new FloatD2TensorShell((FloatTensor)  tensor);
                    case 3 : return tensor instanceof FloatD3Tensor ? tensor : (Tensor<T>) new FloatD3TensorShell((FloatTensor)  tensor);
                    case 4 : return tensor instanceof FloatD4Tensor ? tensor : (Tensor<T>) new FloatD4TensorShell((FloatTensor)  tensor);
                    case 5 : return tensor instanceof FloatD5Tensor ? tensor : (Tensor<T>) new FloatD5TensorShell((FloatTensor)  tensor);
                    case 6 : return tensor instanceof FloatD6Tensor ? tensor : (Tensor<T>) new FloatD6TensorShell((FloatTensor)  tensor);
                    case 7 : return tensor instanceof FloatD7Tensor ? tensor : (Tensor<T>) new FloatD7TensorShell((FloatTensor)  tensor);
                    case 8 : return tensor instanceof FloatD8Tensor ? tensor : (Tensor<T>) new FloatD8TensorShell((FloatTensor)  tensor);
                    case 9 : return tensor instanceof FloatD9Tensor ? tensor : (Tensor<T>) new FloatD9TensorShell((FloatTensor)  tensor);
                    default : return tensor;
                }
            }
            case INT : {
                if (!(tensor instanceof IntTensor)) break;
                switch (tensor.size()) {
                    case 0 : return tensor instanceof IntD0Tensor ? tensor : (Tensor<T>) new IntD0TensorShell((IntTensor)  tensor);
                    case 1 : return tensor instanceof IntD1Tensor ? tensor : (Tensor<T>) new IntD1TensorShell((IntTensor)  tensor);
                    case 2 : return tensor instanceof IntD2Tensor ? tensor : (Tensor<T>) new IntD2TensorShell((IntTensor)  tensor);
                    case 3 : return tensor instanceof IntD3Tensor ? tensor : (Tensor<T>) new IntD3TensorShell((IntTensor)  tensor);
                    case 4 : return tensor instanceof IntD4Tensor ? tensor : (Tensor<T>) new IntD4TensorShell((IntTensor)  tensor);
                    case 5 : return tensor instanceof IntD5Tensor ? tensor : (Tensor<T>) new IntD5TensorShell((IntTensor)  tensor);
                    case 6 : return tensor instanceof IntD6Tensor ? tensor : (Tensor<T>) new IntD6TensorShell((IntTensor)  tensor);
                    case 7 : return tensor instanceof IntD7Tensor ? tensor : (Tensor<T>) new IntD7TensorShell((IntTensor)  tensor);
                    case 8 : return tensor instanceof IntD8Tensor ? tensor : (Tensor<T>) new IntD8TensorShell((IntTensor)  tensor);
                    case 9 : return tensor instanceof IntD9Tensor ? tensor : (Tensor<T>) new IntD9TensorShell((IntTensor)  tensor);
                    default : return tensor;
                }
            }
            case LONG : {
                if (!(tensor instanceof LongTensor)) break;
                switch (tensor.size()) {
                    case 0 : return tensor instanceof LongD0Tensor ? tensor : (Tensor<T>) new LongD0TensorShell((LongTensor)  tensor);
                    case 1 : return tensor instanceof LongD1Tensor ? tensor : (Tensor<T>) new LongD1TensorShell((LongTensor)  tensor);
                    case 2 : return tensor instanceof LongD2Tensor ? tensor : (Tensor<T>) new LongD2TensorShell((LongTensor)  tensor);
                    case 3 : return tensor instanceof LongD3Tensor ? tensor : (Tensor<T>) new LongD3TensorShell((LongTensor)  tensor);
                    case 4 : return tensor instanceof LongD4Tensor ? tensor : (Tensor<T>) new LongD4TensorShell((LongTensor)  tensor);
                    case 5 : return tensor instanceof LongD5Tensor ? tensor : (Tensor<T>) new LongD5TensorShell((LongTensor)  tensor);
                    case 6 : return tensor instanceof LongD6Tensor ? tensor : (Tensor<T>) new LongD6TensorShell((LongTensor)  tensor);
                    case 7 : return tensor instanceof LongD7Tensor ? tensor : (Tensor<T>) new LongD7TensorShell((LongTensor)  tensor);
                    case 8 : return tensor instanceof LongD8Tensor ? tensor : (Tensor<T>) new LongD8TensorShell((LongTensor)  tensor);
                    case 9 : return tensor instanceof LongD9Tensor ? tensor : (Tensor<T>) new LongD9TensorShell((LongTensor)  tensor);
                    default : return tensor;
                }
            }
            case SHORT : {
                if (!(tensor instanceof ShortTensor)) break;
                switch (tensor.size()) {
                    case 0 : return tensor instanceof ShortD0Tensor ? tensor : (Tensor<T>) new ShortD0TensorShell((ShortTensor)  tensor);
                    case 1 : return tensor instanceof ShortD1Tensor ? tensor : (Tensor<T>) new ShortD1TensorShell((ShortTensor)  tensor);
                    case 2 : return tensor instanceof ShortD2Tensor ? tensor : (Tensor<T>) new ShortD2TensorShell((ShortTensor)  tensor);
                    case 3 : return tensor instanceof ShortD3Tensor ? tensor : (Tensor<T>) new ShortD3TensorShell((ShortTensor)  tensor);
                    case 4 : return tensor instanceof ShortD4Tensor ? tensor : (Tensor<T>) new ShortD4TensorShell((ShortTensor)  tensor);
                    case 5 : return tensor instanceof ShortD5Tensor ? tensor : (Tensor<T>) new ShortD5TensorShell((ShortTensor)  tensor);
                    case 6 : return tensor instanceof ShortD6Tensor ? tensor : (Tensor<T>) new ShortD6TensorShell((ShortTensor)  tensor);
                    case 7 : return tensor instanceof ShortD7Tensor ? tensor : (Tensor<T>) new ShortD7TensorShell((ShortTensor)  tensor);
                    case 8 : return tensor instanceof ShortD8Tensor ? tensor : (Tensor<T>) new ShortD8TensorShell((ShortTensor)  tensor);
                    case 9 : return tensor instanceof ShortD9Tensor ? tensor : (Tensor<T>) new ShortD9TensorShell((ShortTensor)  tensor);
                    default : return tensor;
                }
            }
        }
        switch (tensor.size()) {
            case 0 : return tensor instanceof D0Tensor ? tensor : new D0TensorShell<T>(tensor);
            case 1 : return tensor instanceof D1Tensor ? tensor : new D1TensorShell<T>(tensor);
            case 2 : return tensor instanceof D2Tensor ? tensor : new D2TensorShell<T>(tensor);
            case 3 : return tensor instanceof D3Tensor ? tensor : new D3TensorShell<T>(tensor);
            case 4 : return tensor instanceof D4Tensor ? tensor : new D4TensorShell<T>(tensor);
            case 5 : return tensor instanceof D5Tensor ? tensor : new D5TensorShell<T>(tensor);
            case 6 : return tensor instanceof D6Tensor ? tensor : new D6TensorShell<T>(tensor);
            case 7 : return tensor instanceof D7Tensor ? tensor : new D7TensorShell<T>(tensor);
            case 8 : return tensor instanceof D8Tensor ? tensor : new D8TensorShell<T>(tensor);
            case 9 : return tensor instanceof D9Tensor ? tensor : new D9TensorShell<T>(tensor);
            default : return tensor;
        }
    }

    /**
     * Get a tensor which implements the appropriate size and type interfaces for a source id tensor. That is, the returned
     * tensor will be a reference to the source tensor, implementing the most specific size and type interfaces from
     * the subpackages of {@code com.pb.sawdust.tensor.decorators.id}.
     *
     * @param tensor
     *        The original tensor.
     *
     * @param <T>
     *        The type held by the tensor.
     *
     * @return an id tensor equivalent to {@code tensor} implementing the appropriate size and type interfaces.
     */
    public static <T,I> IdTensor<T,I> idTensorCaster(Tensor<T> tensor) {
        return idSizePrimitiveCaster(tensor);
    }

    @SuppressWarnings("unchecked") //cast checking makes this ok
    private static <T,I> IdTensor<T,I> idSizePrimitiveCaster(Tensor<T> tensor) {
        switch (tensor.getType()) {
            case BOOLEAN : {
                if (!(tensor instanceof BooleanTensor)) break;
                switch (tensor.size()) {
                    case 0 : return (IdTensor<T,I>) new IdBooleanD0TensorShell<I>((BooleanD0Tensor) (tensor instanceof BooleanD0Tensor ? tensor : (Tensor<T>) new BooleanD0TensorShell((BooleanTensor)  tensor)));
                    case 1 : return (IdTensor<T,I>) new IdBooleanD1TensorShell<I>((BooleanD1Tensor) (tensor instanceof BooleanD1Tensor ? tensor : (Tensor<T>) new BooleanD1TensorShell((BooleanTensor)  tensor)));
                    case 2 : return (IdTensor<T,I>) new IdBooleanD2TensorShell<I>((BooleanD2Tensor) (tensor instanceof BooleanD2Tensor ? tensor : (Tensor<T>) new BooleanD2TensorShell((BooleanTensor)  tensor)));
                    case 3 : return (IdTensor<T,I>) new IdBooleanD3TensorShell<I>((BooleanD3Tensor) (tensor instanceof BooleanD3Tensor ? tensor : (Tensor<T>) new BooleanD3TensorShell((BooleanTensor)  tensor)));
                    case 4 : return (IdTensor<T,I>) new IdBooleanD4TensorShell<I>((BooleanD4Tensor) (tensor instanceof BooleanD4Tensor ? tensor : (Tensor<T>) new BooleanD4TensorShell((BooleanTensor)  tensor)));
                    case 5 : return (IdTensor<T,I>) new IdBooleanD5TensorShell<I>((BooleanD5Tensor) (tensor instanceof BooleanD5Tensor ? tensor : (Tensor<T>) new BooleanD5TensorShell((BooleanTensor)  tensor)));
                    case 6 : return (IdTensor<T,I>) new IdBooleanD6TensorShell<I>((BooleanD6Tensor) (tensor instanceof BooleanD6Tensor ? tensor : (Tensor<T>) new BooleanD6TensorShell((BooleanTensor)  tensor)));
                    case 7 : return (IdTensor<T,I>) new IdBooleanD7TensorShell<I>((BooleanD7Tensor) (tensor instanceof BooleanD7Tensor ? tensor : (Tensor<T>) new BooleanD7TensorShell((BooleanTensor)  tensor)));
                    case 8 : return (IdTensor<T,I>) new IdBooleanD8TensorShell<I>((BooleanD8Tensor) (tensor instanceof BooleanD8Tensor ? tensor : (Tensor<T>) new BooleanD8TensorShell((BooleanTensor)  tensor)));
                    case 9 : return (IdTensor<T,I>) new IdBooleanD9TensorShell<I>((BooleanD9Tensor) (tensor instanceof BooleanD9Tensor ? tensor : (Tensor<T>) new BooleanD9TensorShell((BooleanTensor)  tensor)));
                    default : return (IdTensor<T,I>) new IdBooleanTensorShell<I>((BooleanTensor) tensor);
                }
            }
            case CHAR : {
                if (!(tensor instanceof CharTensor)) break;
                switch (tensor.size()) {
                    case 0 : return (IdTensor<T,I>) new IdCharD0TensorShell<I>((CharD0Tensor) (tensor instanceof CharD0Tensor ? tensor : (Tensor<T>) new CharD0TensorShell((CharTensor)  tensor)));
                    case 1 : return (IdTensor<T,I>) new IdCharD1TensorShell<I>((CharD1Tensor) (tensor instanceof CharD1Tensor ? tensor : (Tensor<T>) new CharD1TensorShell((CharTensor)  tensor)));
                    case 2 : return (IdTensor<T,I>) new IdCharD2TensorShell<I>((CharD2Tensor) (tensor instanceof CharD2Tensor ? tensor : (Tensor<T>) new CharD2TensorShell((CharTensor)  tensor)));
                    case 3 : return (IdTensor<T,I>) new IdCharD3TensorShell<I>((CharD3Tensor) (tensor instanceof CharD3Tensor ? tensor : (Tensor<T>) new CharD3TensorShell((CharTensor)  tensor)));
                    case 4 : return (IdTensor<T,I>) new IdCharD4TensorShell<I>((CharD4Tensor) (tensor instanceof CharD4Tensor ? tensor : (Tensor<T>) new CharD4TensorShell((CharTensor)  tensor)));
                    case 5 : return (IdTensor<T,I>) new IdCharD5TensorShell<I>((CharD5Tensor) (tensor instanceof CharD5Tensor ? tensor : (Tensor<T>) new CharD5TensorShell((CharTensor)  tensor)));
                    case 6 : return (IdTensor<T,I>) new IdCharD6TensorShell<I>((CharD6Tensor) (tensor instanceof CharD6Tensor ? tensor : (Tensor<T>) new CharD6TensorShell((CharTensor)  tensor)));
                    case 7 : return (IdTensor<T,I>) new IdCharD7TensorShell<I>((CharD7Tensor) (tensor instanceof CharD7Tensor ? tensor : (Tensor<T>) new CharD7TensorShell((CharTensor)  tensor)));
                    case 8 : return (IdTensor<T,I>) new IdCharD8TensorShell<I>((CharD8Tensor) (tensor instanceof CharD8Tensor ? tensor : (Tensor<T>) new CharD8TensorShell((CharTensor)  tensor)));
                    case 9 : return (IdTensor<T,I>) new IdCharD9TensorShell<I>((CharD9Tensor) (tensor instanceof CharD9Tensor ? tensor : (Tensor<T>) new CharD9TensorShell((CharTensor)  tensor)));
                    default : return (IdTensor<T,I>) new IdCharTensorShell<I>((CharTensor) tensor);
                }
            }
            case BYTE : {
                if (!(tensor instanceof ByteTensor)) break;
                switch (tensor.size()) {
                    case 0 : return (IdTensor<T,I>) new IdByteD0TensorShell<I>((ByteD0Tensor) (tensor instanceof ByteD0Tensor ? tensor : (Tensor<T>) new ByteD0TensorShell((ByteTensor)  tensor)));
                    case 1 : return (IdTensor<T,I>) new IdByteD1TensorShell<I>((ByteD1Tensor) (tensor instanceof ByteD1Tensor ? tensor : (Tensor<T>) new ByteD1TensorShell((ByteTensor)  tensor)));
                    case 2 : return (IdTensor<T,I>) new IdByteD2TensorShell<I>((ByteD2Tensor) (tensor instanceof ByteD2Tensor ? tensor : (Tensor<T>) new ByteD2TensorShell((ByteTensor)  tensor)));
                    case 3 : return (IdTensor<T,I>) new IdByteD3TensorShell<I>((ByteD3Tensor) (tensor instanceof ByteD3Tensor ? tensor : (Tensor<T>) new ByteD3TensorShell((ByteTensor)  tensor)));
                    case 4 : return (IdTensor<T,I>) new IdByteD4TensorShell<I>((ByteD4Tensor) (tensor instanceof ByteD4Tensor ? tensor : (Tensor<T>) new ByteD4TensorShell((ByteTensor)  tensor)));
                    case 5 : return (IdTensor<T,I>) new IdByteD5TensorShell<I>((ByteD5Tensor) (tensor instanceof ByteD5Tensor ? tensor : (Tensor<T>) new ByteD5TensorShell((ByteTensor)  tensor)));
                    case 6 : return (IdTensor<T,I>) new IdByteD6TensorShell<I>((ByteD6Tensor) (tensor instanceof ByteD6Tensor ? tensor : (Tensor<T>) new ByteD6TensorShell((ByteTensor)  tensor)));
                    case 7 : return (IdTensor<T,I>) new IdByteD7TensorShell<I>((ByteD7Tensor) (tensor instanceof ByteD7Tensor ? tensor : (Tensor<T>) new ByteD7TensorShell((ByteTensor)  tensor)));
                    case 8 : return (IdTensor<T,I>) new IdByteD8TensorShell<I>((ByteD8Tensor) (tensor instanceof ByteD8Tensor ? tensor : (Tensor<T>) new ByteD8TensorShell((ByteTensor)  tensor)));
                    case 9 : return (IdTensor<T,I>) new IdByteD9TensorShell<I>((ByteD9Tensor) (tensor instanceof ByteD9Tensor ? tensor : (Tensor<T>) new ByteD9TensorShell((ByteTensor)  tensor)));
                    default : return (IdTensor<T,I>) new IdByteTensorShell<I>((ByteTensor) tensor);
                }
            }
            case SHORT : {
                if (!(tensor instanceof ShortTensor)) break;
                switch (tensor.size()) {
                    case 0 : return (IdTensor<T,I>) new IdShortD0TensorShell<I>((ShortD0Tensor) (tensor instanceof ShortD0Tensor ? tensor : (Tensor<T>) new ShortD0TensorShell((ShortTensor)  tensor)));
                    case 1 : return (IdTensor<T,I>) new IdShortD1TensorShell<I>((ShortD1Tensor) (tensor instanceof ShortD1Tensor ? tensor : (Tensor<T>) new ShortD1TensorShell((ShortTensor)  tensor)));
                    case 2 : return (IdTensor<T,I>) new IdShortD2TensorShell<I>((ShortD2Tensor) (tensor instanceof ShortD2Tensor ? tensor : (Tensor<T>) new ShortD2TensorShell((ShortTensor)  tensor)));
                    case 3 : return (IdTensor<T,I>) new IdShortD3TensorShell<I>((ShortD3Tensor) (tensor instanceof ShortD3Tensor ? tensor : (Tensor<T>) new ShortD3TensorShell((ShortTensor)  tensor)));
                    case 4 : return (IdTensor<T,I>) new IdShortD4TensorShell<I>((ShortD4Tensor) (tensor instanceof ShortD4Tensor ? tensor : (Tensor<T>) new ShortD4TensorShell((ShortTensor)  tensor)));
                    case 5 : return (IdTensor<T,I>) new IdShortD5TensorShell<I>((ShortD5Tensor) (tensor instanceof ShortD5Tensor ? tensor : (Tensor<T>) new ShortD5TensorShell((ShortTensor)  tensor)));
                    case 6 : return (IdTensor<T,I>) new IdShortD6TensorShell<I>((ShortD6Tensor) (tensor instanceof ShortD6Tensor ? tensor : (Tensor<T>) new ShortD6TensorShell((ShortTensor)  tensor)));
                    case 7 : return (IdTensor<T,I>) new IdShortD7TensorShell<I>((ShortD7Tensor) (tensor instanceof ShortD7Tensor ? tensor : (Tensor<T>) new ShortD7TensorShell((ShortTensor)  tensor)));
                    case 8 : return (IdTensor<T,I>) new IdShortD8TensorShell<I>((ShortD8Tensor) (tensor instanceof ShortD8Tensor ? tensor : (Tensor<T>) new ShortD8TensorShell((ShortTensor)  tensor)));
                    case 9 : return (IdTensor<T,I>) new IdShortD9TensorShell<I>((ShortD9Tensor) (tensor instanceof ShortD9Tensor ? tensor : (Tensor<T>) new ShortD9TensorShell((ShortTensor)  tensor)));
                    default : return (IdTensor<T,I>) new IdShortTensorShell<I>((ShortTensor) tensor);
                }
            }
            case INT : {
                if (!(tensor instanceof IntTensor)) break;
                switch (tensor.size()) {
                    case 0 : return (IdTensor<T,I>) new IdIntD0TensorShell<I>((IntD0Tensor) (tensor instanceof IntD0Tensor ? tensor : (Tensor<T>) new IntD0TensorShell((IntTensor)  tensor)));
                    case 1 : return (IdTensor<T,I>) new IdIntD1TensorShell<I>((IntD1Tensor) (tensor instanceof IntD1Tensor ? tensor : (Tensor<T>) new IntD1TensorShell((IntTensor)  tensor)));
                    case 2 : return (IdTensor<T,I>) new IdIntD2TensorShell<I>((IntD2Tensor) (tensor instanceof IntD2Tensor ? tensor : (Tensor<T>) new IntD2TensorShell((IntTensor)  tensor)));
                    case 3 : return (IdTensor<T,I>) new IdIntD3TensorShell<I>((IntD3Tensor) (tensor instanceof IntD3Tensor ? tensor : (Tensor<T>) new IntD3TensorShell((IntTensor)  tensor)));
                    case 4 : return (IdTensor<T,I>) new IdIntD4TensorShell<I>((IntD4Tensor) (tensor instanceof IntD4Tensor ? tensor : (Tensor<T>) new IntD4TensorShell((IntTensor)  tensor)));
                    case 5 : return (IdTensor<T,I>) new IdIntD5TensorShell<I>((IntD5Tensor) (tensor instanceof IntD5Tensor ? tensor : (Tensor<T>) new IntD5TensorShell((IntTensor)  tensor)));
                    case 6 : return (IdTensor<T,I>) new IdIntD6TensorShell<I>((IntD6Tensor) (tensor instanceof IntD6Tensor ? tensor : (Tensor<T>) new IntD6TensorShell((IntTensor)  tensor)));
                    case 7 : return (IdTensor<T,I>) new IdIntD7TensorShell<I>((IntD7Tensor) (tensor instanceof IntD7Tensor ? tensor : (Tensor<T>) new IntD7TensorShell((IntTensor)  tensor)));
                    case 8 : return (IdTensor<T,I>) new IdIntD8TensorShell<I>((IntD8Tensor) (tensor instanceof IntD8Tensor ? tensor : (Tensor<T>) new IntD8TensorShell((IntTensor)  tensor)));
                    case 9 : return (IdTensor<T,I>) new IdIntD9TensorShell<I>((IntD9Tensor) (tensor instanceof IntD9Tensor ? tensor : (Tensor<T>) new IntD9TensorShell((IntTensor)  tensor)));
                    default : return (IdTensor<T,I>) new IdIntTensorShell<I>((IntTensor) tensor);
                }
            }
            case LONG : {
                if (!(tensor instanceof LongTensor)) break;
                switch (tensor.size()) {
                    case 0 : return (IdTensor<T,I>) new IdLongD0TensorShell<I>((LongD0Tensor) (tensor instanceof LongD0Tensor ? tensor : (Tensor<T>) new LongD0TensorShell((LongTensor)  tensor)));
                    case 1 : return (IdTensor<T,I>) new IdLongD1TensorShell<I>((LongD1Tensor) (tensor instanceof LongD1Tensor ? tensor : (Tensor<T>) new LongD1TensorShell((LongTensor)  tensor)));
                    case 2 : return (IdTensor<T,I>) new IdLongD2TensorShell<I>((LongD2Tensor) (tensor instanceof LongD2Tensor ? tensor : (Tensor<T>) new LongD2TensorShell((LongTensor)  tensor)));
                    case 3 : return (IdTensor<T,I>) new IdLongD3TensorShell<I>((LongD3Tensor) (tensor instanceof LongD3Tensor ? tensor : (Tensor<T>) new LongD3TensorShell((LongTensor)  tensor)));
                    case 4 : return (IdTensor<T,I>) new IdLongD4TensorShell<I>((LongD4Tensor) (tensor instanceof LongD4Tensor ? tensor : (Tensor<T>) new LongD4TensorShell((LongTensor)  tensor)));
                    case 5 : return (IdTensor<T,I>) new IdLongD5TensorShell<I>((LongD5Tensor) (tensor instanceof LongD5Tensor ? tensor : (Tensor<T>) new LongD5TensorShell((LongTensor)  tensor)));
                    case 6 : return (IdTensor<T,I>) new IdLongD6TensorShell<I>((LongD6Tensor) (tensor instanceof LongD6Tensor ? tensor : (Tensor<T>) new LongD6TensorShell((LongTensor)  tensor)));
                    case 7 : return (IdTensor<T,I>) new IdLongD7TensorShell<I>((LongD7Tensor) (tensor instanceof LongD7Tensor ? tensor : (Tensor<T>) new LongD7TensorShell((LongTensor)  tensor)));
                    case 8 : return (IdTensor<T,I>) new IdLongD8TensorShell<I>((LongD8Tensor) (tensor instanceof LongD8Tensor ? tensor : (Tensor<T>) new LongD8TensorShell((LongTensor)  tensor)));
                    case 9 : return (IdTensor<T,I>) new IdLongD9TensorShell<I>((LongD9Tensor) (tensor instanceof LongD9Tensor ? tensor : (Tensor<T>) new LongD9TensorShell((LongTensor)  tensor)));
                    default : return (IdTensor<T,I>) new IdLongTensorShell<I>((LongTensor) tensor);
                }
            }
            case FLOAT : {
                if (!(tensor instanceof FloatTensor)) break;
                switch (tensor.size()) {
                    case 0 : return (IdTensor<T,I>) new IdFloatD0TensorShell<I>((FloatD0Tensor) (tensor instanceof FloatD0Tensor ? tensor : (Tensor<T>) new FloatD0TensorShell((FloatTensor)  tensor)));
                    case 1 : return (IdTensor<T,I>) new IdFloatD1TensorShell<I>((FloatD1Tensor) (tensor instanceof FloatD1Tensor ? tensor : (Tensor<T>) new FloatD1TensorShell((FloatTensor)  tensor)));
                    case 2 : return (IdTensor<T,I>) new IdFloatD2TensorShell<I>((FloatD2Tensor) (tensor instanceof FloatD2Tensor ? tensor : (Tensor<T>) new FloatD2TensorShell((FloatTensor)  tensor)));
                    case 3 : return (IdTensor<T,I>) new IdFloatD3TensorShell<I>((FloatD3Tensor) (tensor instanceof FloatD3Tensor ? tensor : (Tensor<T>) new FloatD3TensorShell((FloatTensor)  tensor)));
                    case 4 : return (IdTensor<T,I>) new IdFloatD4TensorShell<I>((FloatD4Tensor) (tensor instanceof FloatD4Tensor ? tensor : (Tensor<T>) new FloatD4TensorShell((FloatTensor)  tensor)));
                    case 5 : return (IdTensor<T,I>) new IdFloatD5TensorShell<I>((FloatD5Tensor) (tensor instanceof FloatD5Tensor ? tensor : (Tensor<T>) new FloatD5TensorShell((FloatTensor)  tensor)));
                    case 6 : return (IdTensor<T,I>) new IdFloatD6TensorShell<I>((FloatD6Tensor) (tensor instanceof FloatD6Tensor ? tensor : (Tensor<T>) new FloatD6TensorShell((FloatTensor)  tensor)));
                    case 7 : return (IdTensor<T,I>) new IdFloatD7TensorShell<I>((FloatD7Tensor) (tensor instanceof FloatD7Tensor ? tensor : (Tensor<T>) new FloatD7TensorShell((FloatTensor)  tensor)));
                    case 8 : return (IdTensor<T,I>) new IdFloatD8TensorShell<I>((FloatD8Tensor) (tensor instanceof FloatD8Tensor ? tensor : (Tensor<T>) new FloatD8TensorShell((FloatTensor)  tensor)));
                    case 9 : return (IdTensor<T,I>) new IdFloatD9TensorShell<I>((FloatD9Tensor) (tensor instanceof FloatD9Tensor ? tensor : (Tensor<T>) new FloatD9TensorShell((FloatTensor)  tensor)));
                    default : return (IdTensor<T,I>) new IdFloatTensorShell<I>((FloatTensor) tensor);
                }
            }
            case DOUBLE : {
                if (!(tensor instanceof DoubleTensor)) break;
                switch (tensor.size()) {
                    case 0 : return (IdTensor<T,I>) new IdDoubleD0TensorShell<I>((DoubleD0Tensor) (tensor instanceof DoubleD0Tensor ? tensor : (Tensor<T>) new DoubleD0TensorShell((DoubleTensor)  tensor)));
                    case 1 : return (IdTensor<T,I>) new IdDoubleD1TensorShell<I>((DoubleD1Tensor) (tensor instanceof DoubleD1Tensor ? tensor : (Tensor<T>) new DoubleD1TensorShell((DoubleTensor)  tensor)));
                    case 2 : return (IdTensor<T,I>) new IdDoubleD2TensorShell<I>((DoubleD2Tensor) (tensor instanceof DoubleD2Tensor ? tensor : (Tensor<T>) new DoubleD2TensorShell((DoubleTensor)  tensor)));
                    case 3 : return (IdTensor<T,I>) new IdDoubleD3TensorShell<I>((DoubleD3Tensor) (tensor instanceof DoubleD3Tensor ? tensor : (Tensor<T>) new DoubleD3TensorShell((DoubleTensor)  tensor)));
                    case 4 : return (IdTensor<T,I>) new IdDoubleD4TensorShell<I>((DoubleD4Tensor) (tensor instanceof DoubleD4Tensor ? tensor : (Tensor<T>) new DoubleD4TensorShell((DoubleTensor)  tensor)));
                    case 5 : return (IdTensor<T,I>) new IdDoubleD5TensorShell<I>((DoubleD5Tensor) (tensor instanceof DoubleD5Tensor ? tensor : (Tensor<T>) new DoubleD5TensorShell((DoubleTensor)  tensor)));
                    case 6 : return (IdTensor<T,I>) new IdDoubleD6TensorShell<I>((DoubleD6Tensor) (tensor instanceof DoubleD6Tensor ? tensor : (Tensor<T>) new DoubleD6TensorShell((DoubleTensor)  tensor)));
                    case 7 : return (IdTensor<T,I>) new IdDoubleD7TensorShell<I>((DoubleD7Tensor) (tensor instanceof DoubleD7Tensor ? tensor : (Tensor<T>) new DoubleD7TensorShell((DoubleTensor)  tensor)));
                    case 8 : return (IdTensor<T,I>) new IdDoubleD8TensorShell<I>((DoubleD8Tensor) (tensor instanceof DoubleD8Tensor ? tensor : (Tensor<T>) new DoubleD8TensorShell((DoubleTensor)  tensor)));
                    case 9 : return (IdTensor<T,I>) new IdDoubleD9TensorShell<I>((DoubleD9Tensor) (tensor instanceof DoubleD9Tensor ? tensor : (Tensor<T>) new DoubleD9TensorShell((DoubleTensor)  tensor)));
                    default : return (IdTensor<T,I>) new IdDoubleTensorShell<I>((DoubleTensor) tensor);
                }
            }
        }
        switch (tensor.size()) {
            case 0 : return new IdD0TensorShell<T,I>((D0Tensor<T>) (tensor instanceof D0Tensor ? tensor : new D0TensorShell<T>(tensor)));
            case 1 : return new IdD1TensorShell<T,I>((D1Tensor<T>) (tensor instanceof D1Tensor ? tensor : new D1TensorShell<T>(tensor)));
            case 2 : return new IdD2TensorShell<T,I>((D2Tensor<T>) (tensor instanceof D2Tensor ? tensor : new D2TensorShell<T>(tensor)));
            case 3 : return new IdD3TensorShell<T,I>((D3Tensor<T>) (tensor instanceof D3Tensor ? tensor : new D3TensorShell<T>(tensor)));
            case 4 : return new IdD4TensorShell<T,I>((D4Tensor<T>) (tensor instanceof D4Tensor ? tensor : new D4TensorShell<T>(tensor)));
            case 5 : return new IdD5TensorShell<T,I>((D5Tensor<T>) (tensor instanceof D5Tensor ? tensor : new D5TensorShell<T>(tensor)));
            case 6 : return new IdD6TensorShell<T,I>((D6Tensor<T>) (tensor instanceof D6Tensor ? tensor : new D6TensorShell<T>(tensor)));
            case 7 : return new IdD7TensorShell<T,I>((D7Tensor<T>) (tensor instanceof D7Tensor ? tensor : new D7TensorShell<T>(tensor)));
            case 8 : return new IdD8TensorShell<T,I>((D8Tensor<T>) (tensor instanceof D8Tensor ? tensor : new D8TensorShell<T>(tensor)));
            case 9 : return new IdD9TensorShell<T,I>((D9Tensor<T>) (tensor instanceof D9Tensor ? tensor : new D9TensorShell<T>(tensor)));
            default : return new IdTensorShell<T,I>(tensor);
        }
    }
}
