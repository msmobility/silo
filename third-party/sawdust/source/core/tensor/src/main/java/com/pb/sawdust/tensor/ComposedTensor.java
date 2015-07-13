package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.decorators.size.*;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.decorators.primitive.size.*;
import com.pb.sawdust.util.JavaType;

import java.util.Arrays;

/**
 * @author crf <br/>
 *         Started: Jul 29, 2009 8:17:56 AM
 */
class ComposedTensor {
    static class ComposedObjectTensor<T> extends AbstractTensor<T> {
        private final Tensor<T> tensor;

        ComposedObjectTensor(Tensor<T> tensor, Index index) {
            super(index);
            this.tensor = tensor;
        }

        public JavaType getType() {
            return tensor.getType();
        }

        public T getValue(int ... indices) {
            return tensor.getValue(index.getIndices(indices));
        }

        public void setValue(T value, int ... indices) {
            tensor.setValue(value,index.getIndices(indices));
        }
    }    
                                    
    static class ComposedD0Tensor<T> extends D0TensorShell<T> {
        ComposedD0Tensor(Tensor<T> tensor, Index<?> index) {
            super(new ComposedObjectTensor<T>(tensor,index));
        }
    }
                                    
    static class ComposedD1Tensor<T> extends D1TensorShell<T> {
        ComposedD1Tensor(Tensor<T> tensor, Index<?> index) {
            super(new ComposedObjectTensor<T>(tensor,index));
        }
    }
    
    static class ComposedD2Tensor<T> extends D2TensorShell<T> {
        ComposedD2Tensor(Tensor<T> tensor, Index<?> index) {
            super(new ComposedObjectTensor<T>(tensor,index));
        }
    }                    
    
    static class ComposedD3Tensor<T> extends D3TensorShell<T> {
        ComposedD3Tensor(Tensor<T> tensor, Index<?> index) {
            super(new ComposedObjectTensor<T>(tensor,index));
        }
    }
    
    static class ComposedD4Tensor<T> extends D4TensorShell<T> {
        ComposedD4Tensor(Tensor<T> tensor, Index<?> index) {
            super(new ComposedObjectTensor<T>(tensor,index));
        }
    }
    
    static class ComposedD5Tensor<T> extends D5TensorShell<T> {
        ComposedD5Tensor(Tensor<T> tensor, Index<?> index) {
            super(new ComposedObjectTensor<T>(tensor,index));
        }
    }
    
    static class ComposedD6Tensor<T> extends D6TensorShell<T> {
        ComposedD6Tensor(Tensor<T> tensor, Index<?> index) {
            super(new ComposedObjectTensor<T>(tensor,index));
        }
    }
    
    static class ComposedD7Tensor<T> extends D7TensorShell<T> {
        ComposedD7Tensor(Tensor<T> tensor, Index<?> index) {
            super(new ComposedObjectTensor<T>(tensor,index));
        }
    }
    
    static class ComposedD8Tensor<T> extends D8TensorShell<T> {
        ComposedD8Tensor(Tensor<T> tensor, Index<?> index) {
            super(new ComposedObjectTensor<T>(tensor,index));
        }
    }
    
    static class ComposedD9Tensor<T> extends D9TensorShell<T> {
        ComposedD9Tensor(Tensor<T> tensor, Index<?> index) {
            super(new ComposedObjectTensor<T>(tensor,index));
        }
    }  
    
    static class ComposedBooleanTensor extends AbstractBooleanTensor {
        private final BooleanTensor tensor;

        ComposedBooleanTensor(BooleanTensor tensor, Index index) {
            super(index);
            this.tensor = tensor;
        }

        public boolean getCell(int ... indices) {
            return tensor.getCell(index.getIndices(indices));
        }

        public void setCell(boolean value, int ... indices) {
            tensor.setCell(value,index.getIndices(indices));
        }
    }

    static class ComposedCharTensor extends AbstractCharTensor {
        private final CharTensor tensor;

        ComposedCharTensor(CharTensor tensor, Index index) {
            super(index);
            this.tensor = tensor;
        }

        public char getCell(int ... indices) {
            return tensor.getCell(index.getIndices(indices));
        }

        public void setCell(char value, int ... indices) {
            tensor.setCell(value,index.getIndices(indices));
        }
    }

    static class ComposedByteTensor extends AbstractByteTensor {
        private final ByteTensor tensor;

        ComposedByteTensor(ByteTensor tensor, Index index) {
            super(index);
            this.tensor = tensor;
        }

        public byte getCell(int ... indices) {
            return tensor.getCell(index.getIndices(indices));
        }

        public void setCell(byte value, int ... indices) {
            tensor.setCell(value,index.getIndices(indices));
        }
    }

    static class ComposedShortTensor extends AbstractShortTensor {
        private final ShortTensor tensor;

        ComposedShortTensor(ShortTensor tensor, Index index) {
            super(index);
            this.tensor = tensor;
        }

        public short getCell(int ... indices) {
            return tensor.getCell(index.getIndices(indices));
        }

        public void setCell(short value, int ... indices) {
            tensor.setCell(value,index.getIndices(indices));
        }
    }

    static class ComposedIntTensor extends AbstractIntTensor {
        private final IntTensor tensor;

        ComposedIntTensor(IntTensor tensor, Index index) {
            super(index);
            this.tensor = tensor;
        }

        public int getCell(int ... indices) {
            return tensor.getCell(index.getIndices(indices));
        }

        public void setCell(int value, int ... indices) {
            tensor.setCell(value,index.getIndices(indices));
        }
    }

    static class ComposedLongTensor extends AbstractLongTensor {
        private final LongTensor tensor;

        ComposedLongTensor(LongTensor tensor, Index index) {
            super(index);
            this.tensor = tensor;
        }

        public long getCell(int ... indices) {
            return tensor.getCell(index.getIndices(indices));
        }

        public void setCell(long value, int ... indices) {
            tensor.setCell(value,index.getIndices(indices));
        }
    }

    static class ComposedFloatTensor extends AbstractFloatTensor {
        private final FloatTensor tensor;

        ComposedFloatTensor(FloatTensor tensor, Index index) {
            super(index);
            this.tensor = tensor;
        }

        public float getCell(int ... indices) {
            return tensor.getCell(index.getIndices(indices));
        }

        public void setCell(float value, int ... indices) {
            tensor.setCell(value,index.getIndices(indices));
        }
    }

    static class ComposedDoubleTensor extends AbstractDoubleTensor {
        private final DoubleTensor tensor;

        ComposedDoubleTensor(DoubleTensor tensor, Index index) {
            super(index);
            this.tensor = tensor;
        }

        public double getCell(int ... indices) {
            return tensor.getCell(index.getIndices(indices));
        }

        public void setCell(double value, int ... indices) {
            tensor.setCell(value,index.getIndices(indices));
        }
    }

    static class ComposedBooleanD0Tensor extends BooleanD0TensorShell {
        ComposedBooleanD0Tensor(BooleanTensor tensor, Index<?> index) {
            super(new ComposedBooleanTensor(tensor,index));
        }
    }       

    static class ComposedBooleanD1Tensor extends BooleanD1TensorShell {
        ComposedBooleanD1Tensor(BooleanTensor tensor, Index<?> index) {
            super(new ComposedBooleanTensor(tensor,index));
        }
    }

    static class ComposedBooleanD2Tensor extends BooleanD2TensorShell {
        ComposedBooleanD2Tensor(BooleanTensor tensor, Index<?> index) {
            super(new ComposedBooleanTensor(tensor,index));
        }
    }   

    static class ComposedBooleanD3Tensor extends BooleanD3TensorShell {
        ComposedBooleanD3Tensor(BooleanTensor tensor, Index<?> index) {
            super(new ComposedBooleanTensor(tensor,index));
        }
    }

    static class ComposedBooleanD4Tensor extends BooleanD4TensorShell {
        ComposedBooleanD4Tensor(BooleanTensor tensor, Index<?> index) {
            super(new ComposedBooleanTensor(tensor,index));
        }
    }

    static class ComposedBooleanD5Tensor extends BooleanD5TensorShell {
        ComposedBooleanD5Tensor(BooleanTensor tensor, Index<?> index) {
            super(new ComposedBooleanTensor(tensor,index));
        }
    }

    static class ComposedBooleanD6Tensor extends BooleanD6TensorShell {
        ComposedBooleanD6Tensor(BooleanTensor tensor, Index<?> index) {
            super(new ComposedBooleanTensor(tensor,index));
        }
    }

    static class ComposedBooleanD7Tensor extends BooleanD7TensorShell {
        ComposedBooleanD7Tensor(BooleanTensor tensor, Index<?> index) {
            super(new ComposedBooleanTensor(tensor,index));
        }
    }

    static class ComposedBooleanD8Tensor extends BooleanD8TensorShell {
        ComposedBooleanD8Tensor(BooleanTensor tensor, Index<?> index) {
            super(new ComposedBooleanTensor(tensor,index));
        }
    }

    static class ComposedBooleanD9Tensor extends BooleanD9TensorShell {
        ComposedBooleanD9Tensor(BooleanTensor tensor, Index<?> index) {
            super(new ComposedBooleanTensor(tensor,index));
        }
    }

    static class ComposedByteD0Tensor extends ByteD0TensorShell {
        ComposedByteD0Tensor(ByteTensor tensor, Index<?> index) {
            super(new ComposedByteTensor(tensor,index));
        }
    }

    static class ComposedByteD1Tensor extends ByteD1TensorShell {
        ComposedByteD1Tensor(ByteTensor tensor, Index<?> index) {
            super(new ComposedByteTensor(tensor,index));
        }
    }

    static class ComposedByteD2Tensor extends ByteD2TensorShell {
        ComposedByteD2Tensor(ByteTensor tensor, Index<?> index) {
            super(new ComposedByteTensor(tensor,index));
        }
    }

    static class ComposedByteD3Tensor extends ByteD3TensorShell {
        ComposedByteD3Tensor(ByteTensor tensor, Index<?> index) {
            super(new ComposedByteTensor(tensor,index));
        }
    }

    static class ComposedByteD4Tensor extends ByteD4TensorShell {
        ComposedByteD4Tensor(ByteTensor tensor, Index<?> index) {
            super(new ComposedByteTensor(tensor,index));
        }
    }

    static class ComposedByteD5Tensor extends ByteD5TensorShell {
        ComposedByteD5Tensor(ByteTensor tensor, Index<?> index) {
            super(new ComposedByteTensor(tensor,index));
        }
    }

    static class ComposedByteD6Tensor extends ByteD6TensorShell {
        ComposedByteD6Tensor(ByteTensor tensor, Index<?> index) {
            super(new ComposedByteTensor(tensor,index));
        }
    }

    static class ComposedByteD7Tensor extends ByteD7TensorShell {
        ComposedByteD7Tensor(ByteTensor tensor, Index<?> index) {
            super(new ComposedByteTensor(tensor,index));
        }
    }

    static class ComposedByteD8Tensor extends ByteD8TensorShell {
        ComposedByteD8Tensor(ByteTensor tensor, Index<?> index) {
            super(new ComposedByteTensor(tensor,index));
        }
    }

    static class ComposedByteD9Tensor extends ByteD9TensorShell {
        ComposedByteD9Tensor(ByteTensor tensor, Index<?> index) {
            super(new ComposedByteTensor(tensor,index));
        }
    }

    static class ComposedShortD0Tensor extends ShortD0TensorShell {
        ComposedShortD0Tensor(ShortTensor tensor, Index<?> index) {
            super(new ComposedShortTensor(tensor,index));
        }
    }

    static class ComposedShortD1Tensor extends ShortD1TensorShell {
        ComposedShortD1Tensor(ShortTensor tensor, Index<?> index) {
            super(new ComposedShortTensor(tensor,index));
        }
    }

    static class ComposedShortD2Tensor extends ShortD2TensorShell {
        ComposedShortD2Tensor(ShortTensor tensor, Index<?> index) {
            super(new ComposedShortTensor(tensor,index));
        }
    }

    static class ComposedShortD3Tensor extends ShortD3TensorShell {
        ComposedShortD3Tensor(ShortTensor tensor, Index<?> index) {
            super(new ComposedShortTensor(tensor,index));
        }
    }

    static class ComposedShortD4Tensor extends ShortD4TensorShell {
        ComposedShortD4Tensor(ShortTensor tensor, Index<?> index) {
            super(new ComposedShortTensor(tensor,index));
        }
    }

    static class ComposedShortD5Tensor extends ShortD5TensorShell {
        ComposedShortD5Tensor(ShortTensor tensor, Index<?> index) {
            super(new ComposedShortTensor(tensor,index));
        }
    }

    static class ComposedShortD6Tensor extends ShortD6TensorShell {
        ComposedShortD6Tensor(ShortTensor tensor, Index<?> index) {
            super(new ComposedShortTensor(tensor,index));
        }
    }

    static class ComposedShortD7Tensor extends ShortD7TensorShell {
        ComposedShortD7Tensor(ShortTensor tensor, Index<?> index) {
            super(new ComposedShortTensor(tensor,index));
        }
    }

    static class ComposedShortD8Tensor extends ShortD8TensorShell {
        ComposedShortD8Tensor(ShortTensor tensor, Index<?> index) {
            super(new ComposedShortTensor(tensor,index));
        }
    }

    static class ComposedShortD9Tensor extends ShortD9TensorShell {
        ComposedShortD9Tensor(ShortTensor tensor, Index<?> index) {
            super(new ComposedShortTensor(tensor,index));
        }
    }

    static class ComposedIntD0Tensor extends IntD0TensorShell {
        ComposedIntD0Tensor(IntTensor tensor, Index<?> index) {
            super(new ComposedIntTensor(tensor,index));
        }
    }

    static class ComposedIntD1Tensor extends IntD1TensorShell {
        ComposedIntD1Tensor(IntTensor tensor, Index<?> index) {
            super(new ComposedIntTensor(tensor,index));
        }
    }

    static class ComposedIntD2Tensor extends IntD2TensorShell {
        ComposedIntD2Tensor(IntTensor tensor, Index<?> index) {
            super(new ComposedIntTensor(tensor,index));
        }
    }

    static class ComposedIntD3Tensor extends IntD3TensorShell {
        ComposedIntD3Tensor(IntTensor tensor, Index<?> index) {
            super(new ComposedIntTensor(tensor,index));
        }
    }

    static class ComposedIntD4Tensor extends IntD4TensorShell {
        ComposedIntD4Tensor(IntTensor tensor, Index<?> index) {
            super(new ComposedIntTensor(tensor,index));
        }
    }

    static class ComposedIntD5Tensor extends IntD5TensorShell {
        ComposedIntD5Tensor(IntTensor tensor, Index<?> index) {
            super(new ComposedIntTensor(tensor,index));
        }
    }

    static class ComposedIntD6Tensor extends IntD6TensorShell {
        ComposedIntD6Tensor(IntTensor tensor, Index<?> index) {
            super(new ComposedIntTensor(tensor,index));
        }
    }

    static class ComposedIntD7Tensor extends IntD7TensorShell {
        ComposedIntD7Tensor(IntTensor tensor, Index<?> index) {
            super(new ComposedIntTensor(tensor,index));
        }
    }

    static class ComposedIntD8Tensor extends IntD8TensorShell {
        ComposedIntD8Tensor(IntTensor tensor, Index<?> index) {
            super(new ComposedIntTensor(tensor,index));
        }
    }

    static class ComposedIntD9Tensor extends IntD9TensorShell {
        ComposedIntD9Tensor(IntTensor tensor, Index<?> index) {
            super(new ComposedIntTensor(tensor,index));
        }
    }

    static class ComposedLongD0Tensor extends LongD0TensorShell {
        ComposedLongD0Tensor(LongTensor tensor, Index<?> index) {
            super(new ComposedLongTensor(tensor,index));
        }
    }

    static class ComposedLongD1Tensor extends LongD1TensorShell {
        ComposedLongD1Tensor(LongTensor tensor, Index<?> index) {
            super(new ComposedLongTensor(tensor,index));
        }
    }

    static class ComposedLongD2Tensor extends LongD2TensorShell {
        ComposedLongD2Tensor(LongTensor tensor, Index<?> index) {
            super(new ComposedLongTensor(tensor,index));
        }
    }

    static class ComposedLongD3Tensor extends LongD3TensorShell {
        ComposedLongD3Tensor(LongTensor tensor, Index<?> index) {
            super(new ComposedLongTensor(tensor,index));
        }
    }

    static class ComposedLongD4Tensor extends LongD4TensorShell {
        ComposedLongD4Tensor(LongTensor tensor, Index<?> index) {
            super(new ComposedLongTensor(tensor,index));
        }
    }

    static class ComposedLongD5Tensor extends LongD5TensorShell {
        ComposedLongD5Tensor(LongTensor tensor, Index<?> index) {
            super(new ComposedLongTensor(tensor,index));
        }
    }

    static class ComposedLongD6Tensor extends LongD6TensorShell {
        ComposedLongD6Tensor(LongTensor tensor, Index<?> index) {
            super(new ComposedLongTensor(tensor,index));
        }
    }

    static class ComposedLongD7Tensor extends LongD7TensorShell {
        ComposedLongD7Tensor(LongTensor tensor, Index<?> index) {
            super(new ComposedLongTensor(tensor,index));
        }
    }

    static class ComposedLongD8Tensor extends LongD8TensorShell {
        ComposedLongD8Tensor(LongTensor tensor, Index<?> index) {
            super(new ComposedLongTensor(tensor,index));
        }
    }

    static class ComposedLongD9Tensor extends LongD9TensorShell {
        ComposedLongD9Tensor(LongTensor tensor, Index<?> index) {
            super(new ComposedLongTensor(tensor,index));
        }
    }

    static class ComposedFloatD0Tensor extends FloatD0TensorShell {
        ComposedFloatD0Tensor(FloatTensor tensor, Index<?> index) {
            super(new ComposedFloatTensor(tensor,index));
        }
    }

    static class ComposedFloatD1Tensor extends FloatD1TensorShell {
        ComposedFloatD1Tensor(FloatTensor tensor, Index<?> index) {
            super(new ComposedFloatTensor(tensor,index));
        }
    }

    static class ComposedFloatD2Tensor extends FloatD2TensorShell {
        ComposedFloatD2Tensor(FloatTensor tensor, Index<?> index) {
            super(new ComposedFloatTensor(tensor,index));
        }
    }

    static class ComposedFloatD3Tensor extends FloatD3TensorShell {
        ComposedFloatD3Tensor(FloatTensor tensor, Index<?> index) {
            super(new ComposedFloatTensor(tensor,index));
        }
    }

    static class ComposedFloatD4Tensor extends FloatD4TensorShell {
        ComposedFloatD4Tensor(FloatTensor tensor, Index<?> index) {
            super(new ComposedFloatTensor(tensor,index));
        }
    }

    static class ComposedFloatD5Tensor extends FloatD5TensorShell {
        ComposedFloatD5Tensor(FloatTensor tensor, Index<?> index) {
            super(new ComposedFloatTensor(tensor,index));
        }
    }

    static class ComposedFloatD6Tensor extends FloatD6TensorShell {
        ComposedFloatD6Tensor(FloatTensor tensor, Index<?> index) {
            super(new ComposedFloatTensor(tensor,index));
        }
    }

    static class ComposedFloatD7Tensor extends FloatD7TensorShell {
        ComposedFloatD7Tensor(FloatTensor tensor, Index<?> index) {
            super(new ComposedFloatTensor(tensor,index));
        }
    }

    static class ComposedFloatD8Tensor extends FloatD8TensorShell {
        ComposedFloatD8Tensor(FloatTensor tensor, Index<?> index) {
            super(new ComposedFloatTensor(tensor,index));
        }
    }

    static class ComposedFloatD9Tensor extends FloatD9TensorShell {
        ComposedFloatD9Tensor(FloatTensor tensor, Index<?> index) {
            super(new ComposedFloatTensor(tensor,index));
        }
    }

    static class ComposedDoubleD0Tensor extends DoubleD0TensorShell {
        ComposedDoubleD0Tensor(DoubleTensor tensor, Index<?> index) {
            super(new ComposedDoubleTensor(tensor,index));
        }
    }

    static class ComposedDoubleD1Tensor extends DoubleD1TensorShell {
        ComposedDoubleD1Tensor(DoubleTensor tensor, Index<?> index) {
            super(new ComposedDoubleTensor(tensor,index));
        }
    }

    static class ComposedDoubleD2Tensor extends DoubleD2TensorShell {
        ComposedDoubleD2Tensor(DoubleTensor tensor, Index<?> index) {
            super(new ComposedDoubleTensor(tensor,index));
        }
    }

    static class ComposedDoubleD3Tensor extends DoubleD3TensorShell {
        ComposedDoubleD3Tensor(DoubleTensor tensor, Index<?> index) {
            super(new ComposedDoubleTensor(tensor,index));
        }
    }

    static class ComposedDoubleD4Tensor extends DoubleD4TensorShell {
        ComposedDoubleD4Tensor(DoubleTensor tensor, Index<?> index) {
            super(new ComposedDoubleTensor(tensor,index));
        }
    }

    static class ComposedDoubleD5Tensor extends DoubleD5TensorShell {
        ComposedDoubleD5Tensor(DoubleTensor tensor, Index<?> index) {
            super(new ComposedDoubleTensor(tensor,index));
        }
    }

    static class ComposedDoubleD6Tensor extends DoubleD6TensorShell {
        ComposedDoubleD6Tensor(DoubleTensor tensor, Index<?> index) {
            super(new ComposedDoubleTensor(tensor,index));
        }
    }

    static class ComposedDoubleD7Tensor extends DoubleD7TensorShell {
        ComposedDoubleD7Tensor(DoubleTensor tensor, Index<?> index) {
            super(new ComposedDoubleTensor(tensor,index));
        }
    }

    static class ComposedDoubleD8Tensor extends DoubleD8TensorShell {
        ComposedDoubleD8Tensor(DoubleTensor tensor, Index<?> index) {
            super(new ComposedDoubleTensor(tensor,index));
        }
    }

    static class ComposedDoubleD9Tensor extends DoubleD9TensorShell {
        ComposedDoubleD9Tensor(DoubleTensor tensor, Index<?> index) {
            super(new ComposedDoubleTensor(tensor,index));
        }
    }      

    static class ComposedCharD0Tensor extends CharD0TensorShell {
        ComposedCharD0Tensor(CharTensor tensor, Index<?> index) {
            super(new ComposedCharTensor(tensor,index));
        }
    }       

    static class ComposedCharD1Tensor extends CharD1TensorShell {
        ComposedCharD1Tensor(CharTensor tensor, Index<?> index) {
            super(new ComposedCharTensor(tensor,index));
        }
    }

    static class ComposedCharD2Tensor extends CharD2TensorShell {
        ComposedCharD2Tensor(CharTensor tensor, Index<?> index) {
            super(new ComposedCharTensor(tensor,index));
        }
    }   

    static class ComposedCharD3Tensor extends CharD3TensorShell {
        ComposedCharD3Tensor(CharTensor tensor, Index<?> index) {
            super(new ComposedCharTensor(tensor,index));
        }
    }

    static class ComposedCharD4Tensor extends CharD4TensorShell {
        ComposedCharD4Tensor(CharTensor tensor, Index<?> index) {
            super(new ComposedCharTensor(tensor,index));
        }
    }

    static class ComposedCharD5Tensor extends CharD5TensorShell {
        ComposedCharD5Tensor(CharTensor tensor, Index<?> index) {
            super(new ComposedCharTensor(tensor,index));
        }
    }

    static class ComposedCharD6Tensor extends CharD6TensorShell {
        ComposedCharD6Tensor(CharTensor tensor, Index<?> index) {
            super(new ComposedCharTensor(tensor,index));
        }
    }

    static class ComposedCharD7Tensor extends CharD7TensorShell {
        ComposedCharD7Tensor(CharTensor tensor, Index<?> index) {
            super(new ComposedCharTensor(tensor,index));
        }
    }

    static class ComposedCharD8Tensor extends CharD8TensorShell {
        ComposedCharD8Tensor(CharTensor tensor, Index<?> index) {
            super(new ComposedCharTensor(tensor,index));
        }
    }

    static class ComposedCharD9Tensor extends CharD9TensorShell {
        ComposedCharD9Tensor(CharTensor tensor, Index<?> index) {
            super(new ComposedCharTensor(tensor,index));
        }
    }
    
    
}
