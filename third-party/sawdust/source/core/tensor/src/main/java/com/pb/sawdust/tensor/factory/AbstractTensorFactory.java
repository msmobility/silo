package com.pb.sawdust.tensor.factory;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.alias.matrix.id.*;
import com.pb.sawdust.tensor.alias.matrix.primitive.*;
import com.pb.sawdust.tensor.alias.scalar.Scalar;
import com.pb.sawdust.tensor.alias.scalar.id.*;
import com.pb.sawdust.tensor.alias.scalar.primitive.*;
import com.pb.sawdust.tensor.alias.vector.Vector;
import com.pb.sawdust.tensor.alias.vector.id.*;
import com.pb.sawdust.tensor.alias.vector.primitive.*;
import com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorLocksFactory;
import com.pb.sawdust.tensor.decorators.concurrent.ConcurrentTensorShell;
import com.pb.sawdust.tensor.decorators.concurrent.primitive.*;
import com.pb.sawdust.tensor.decorators.concurrent.primitive.size.*;
import com.pb.sawdust.tensor.decorators.concurrent.size.*;
import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.decorators.id.IdTensorShell;
import com.pb.sawdust.tensor.decorators.id.primitive.*;
import com.pb.sawdust.tensor.decorators.id.primitive.size.*;
import com.pb.sawdust.tensor.decorators.id.size.*;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.decorators.primitive.size.*;
import com.pb.sawdust.tensor.decorators.size.*;
import com.pb.sawdust.util.JavaType;

import java.util.Arrays;
import java.util.List;

/**
 * The {@code AbstractTensorFactory} provides a skeletal implementation of the {@code TensorFactory} and {@code ConcurrentTensorFactory}
 * interfaces.
 *
 * @author crf <br/>
 *         Started Oct 11, 2010 3:28:07 PM
 */
public abstract class AbstractTensorFactory implements TensorFactory,ConcurrentTensorFactory {

    public ByteScalar byteScalar() {
        return (ByteScalar) byteTensor();
    }

    public ByteScalar initializedByteScalar(byte value) {
        return (ByteScalar) initializedByteTensor(value);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdByteScalar<I> initializedByteScalar(byte value, I[] ids, int d0) {
        return (IdByteScalar<I>) initializedByteTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public ByteVector byteVector(int d0) {
        return (ByteVector) byteTensor(d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdByteVector<I> byteVector(List<I> ids, int d0) {
        return (IdByteVector<I>) byteTensor(Arrays.asList(ids),d0);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdByteVector<I> byteVector(I[] ids, int d0) {
        return (IdByteVector<I>) byteTensor((I[][]) new Object[][] {ids},d0);
    }

    public ByteVector initializedByteVector(byte value, int d0) {
        return (ByteVector) initializedByteTensor(value,d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdByteVector<I> initializedByteVector(byte value, List<I> ids, int d0) {
        return (IdByteVector<I>) initializedByteTensor(value,Arrays.asList(ids),d0);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdByteVector<I> initializedByteVector(byte value, I[] ids, int d0) {
        return (IdByteVector<I>) initializedByteTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public ByteMatrix byteMatrix(int d0, int d1) {
        return (ByteMatrix) byteTensor(d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdByteMatrix<I> byteMatrix(List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdByteMatrix<I>) byteTensor(Arrays.asList(d0Ids,d1Ids),d0,d1);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdByteMatrix<I> byteMatrix(I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdByteMatrix<I>) byteTensor((I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public ByteMatrix initializedByteMatrix(byte value, int d0, int d1) {
        return (ByteMatrix) initializedByteTensor(value,d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdByteMatrix<I> initializedByteMatrix(byte value,List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdByteMatrix<I>) initializedByteTensor(value,Arrays.asList(d0Ids,d1Ids),d0,d1);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdByteMatrix<I> initializedByteMatrix(byte value, I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdByteMatrix<I>) initializedByteTensor(value,(I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public ShortScalar shortScalar() {
        return (ShortScalar) shortTensor();
    }

    public ShortScalar initializedShortScalar(short value) {
        return (ShortScalar) initializedShortTensor(value);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdShortScalar<I> initializedShortScalar(short value, I[] ids, int d0) {
        return (IdShortScalar<I>) initializedShortTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public ShortVector shortVector(int d0) {
        return (ShortVector) shortTensor(d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdShortVector<I> shortVector(List<I> ids, int d0) {
        return (IdShortVector<I>) shortTensor(Arrays.asList(ids),d0);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdShortVector<I> shortVector(I[] ids, int d0) {
        return (IdShortVector<I>) shortTensor((I[][]) new Object[][] {ids},d0);
    }

    public ShortVector initializedShortVector(short value, int d0) {
        return (ShortVector) initializedShortTensor(value,d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdShortVector<I> initializedShortVector(short value, List<I> ids, int d0) {
        return (IdShortVector<I>) initializedShortTensor(value,Arrays.asList(ids),d0);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdShortVector<I> initializedShortVector(short value, I[] ids, int d0) {
        return (IdShortVector<I>) initializedShortTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public ShortMatrix shortMatrix(int d0, int d1) {
        return (ShortMatrix) shortTensor(d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdShortMatrix<I> shortMatrix(List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdShortMatrix<I>) shortTensor(Arrays.asList(d0Ids,d1Ids),d0,d1);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdShortMatrix<I> shortMatrix(I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdShortMatrix<I>) shortTensor((I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public ShortMatrix initializedShortMatrix(short value, int d0, int d1) {
        return (ShortMatrix) initializedShortTensor(value,d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdShortMatrix<I> initializedShortMatrix(short value,List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdShortMatrix<I>) initializedShortTensor(value,Arrays.asList(d0Ids,d1Ids),d0,d1);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdShortMatrix<I> initializedShortMatrix(short value, I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdShortMatrix<I>) initializedShortTensor(value,(I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public IntScalar intScalar() {
        return (IntScalar) intTensor();
    }

    public IntScalar initializedIntScalar(int value) {
        return (IntScalar) initializedIntTensor(value);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdIntScalar<I> initializedIntScalar(int value, I[] ids, int d0) {
        return (IdIntScalar<I>) initializedIntTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public IntVector intVector(int d0) {
        return (IntVector) intTensor(d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdIntVector<I> intVector(List<I> ids, int d0) {
        return (IdIntVector<I>) intTensor(Arrays.asList(ids),d0);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdIntVector<I> intVector(I[] ids, int d0) {
        return (IdIntVector<I>) intTensor((I[][]) new Object[][] {ids},d0);
    }

    public IntVector initializedIntVector(int value, int d0) {
        return (IntVector) initializedIntTensor(value,d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdIntVector<I> initializedIntVector(int value, List<I> ids, int d0) {
        return (IdIntVector<I>) initializedIntTensor(value,Arrays.asList(ids),d0);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdIntVector<I> initializedIntVector(int value, I[] ids, int d0) {
        return (IdIntVector<I>) initializedIntTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public IntMatrix intMatrix(int d0, int d1) {
        return (IntMatrix) intTensor(d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdIntMatrix<I> intMatrix(List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdIntMatrix<I>) intTensor(Arrays.asList(d0Ids,d1Ids),d0,d1);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdIntMatrix<I> intMatrix(I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdIntMatrix<I>) intTensor((I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public IntMatrix initializedIntMatrix(int value, int d0, int d1) {
        return (IntMatrix) initializedIntTensor(value,d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdIntMatrix<I> initializedIntMatrix(int value,List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdIntMatrix<I>) initializedIntTensor(value,Arrays.asList(d0Ids,d1Ids),d0,d1);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdIntMatrix<I> initializedIntMatrix(int value, I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdIntMatrix<I>) initializedIntTensor(value,(I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public LongScalar longScalar() {
        return (LongScalar) longTensor();
    }

    public LongScalar initializedLongScalar(long value) {
        return (LongScalar) initializedLongTensor(value);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdLongScalar<I> initializedLongScalar(long value, I[] ids, int d0) {
        return (IdLongScalar<I>) initializedLongTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public LongVector longVector(int d0) {
        return (LongVector) longTensor(d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdLongVector<I> longVector(List<I> ids, int d0) {
        return (IdLongVector<I>) longTensor(Arrays.asList(ids),d0);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdLongVector<I> longVector(I[] ids, int d0) {
        return (IdLongVector<I>) longTensor((I[][]) new Object[][] {ids},d0);
    }

    public LongVector initializedLongVector(long value, int d0) {
        return (LongVector) initializedLongTensor(value,d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdLongVector<I> initializedLongVector(long value, List<I> ids, int d0) {
        return (IdLongVector<I>) initializedLongTensor(value,Arrays.asList(ids),d0);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdLongVector<I> initializedLongVector(long value, I[] ids, int d0) {
        return (IdLongVector<I>) initializedLongTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public LongMatrix longMatrix(int d0, int d1) {
        return (LongMatrix) longTensor(d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdLongMatrix<I> longMatrix(List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdLongMatrix<I>) longTensor(Arrays.asList(d0Ids,d1Ids),d0,d1);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdLongMatrix<I> longMatrix(I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdLongMatrix<I>) longTensor((I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public LongMatrix initializedLongMatrix(long value, int d0, int d1) {
        return (LongMatrix) initializedLongTensor(value,d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdLongMatrix<I> initializedLongMatrix(long value,List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdLongMatrix<I>) initializedLongTensor(value,Arrays.asList(d0Ids,d1Ids),d0,d1);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdLongMatrix<I> initializedLongMatrix(long value, I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdLongMatrix<I>) initializedLongTensor(value,(I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public FloatScalar floatScalar() {
        return (FloatScalar) floatTensor();
    }

    public FloatScalar initializedFloatScalar(float value) {
        return (FloatScalar) initializedFloatTensor(value);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdFloatScalar<I> initializedFloatScalar(float value, I[] ids, int d0) {
        return (IdFloatScalar<I>) initializedFloatTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public FloatVector floatVector(int d0) {
        return (FloatVector) floatTensor(d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdFloatVector<I> floatVector(List<I> ids, int d0) {
        return (IdFloatVector<I>) floatTensor(Arrays.asList(ids),d0);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdFloatVector<I> floatVector(I[] ids, int d0) {
        return (IdFloatVector<I>) floatTensor((I[][]) new Object[][] {ids},d0);
    }

    public FloatVector initializedFloatVector(float value, int d0) {
        return (FloatVector) initializedFloatTensor(value,d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdFloatVector<I> initializedFloatVector(float value, List<I> ids, int d0) {
        return (IdFloatVector<I>) initializedFloatTensor(value,Arrays.asList(ids),d0);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdFloatVector<I> initializedFloatVector(float value, I[] ids, int d0) {
        return (IdFloatVector<I>) initializedFloatTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public FloatMatrix floatMatrix(int d0, int d1) {
        return (FloatMatrix) floatTensor(d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdFloatMatrix<I> floatMatrix(List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdFloatMatrix<I>) floatTensor(Arrays.asList(d0Ids,d1Ids),d0,d1);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdFloatMatrix<I> floatMatrix(I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdFloatMatrix<I>) floatTensor((I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public FloatMatrix initializedFloatMatrix(float value, int d0, int d1) {
        return (FloatMatrix) initializedFloatTensor(value,d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdFloatMatrix<I> initializedFloatMatrix(float value,List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdFloatMatrix<I>) initializedFloatTensor(value,Arrays.asList(d0Ids,d1Ids),d0,d1);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdFloatMatrix<I> initializedFloatMatrix(float value, I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdFloatMatrix<I>) initializedFloatTensor(value,(I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public DoubleScalar doubleScalar() {
        return (DoubleScalar) doubleTensor();
    }

    public DoubleScalar initializedDoubleScalar(double value) {
        return (DoubleScalar) initializedDoubleTensor(value);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdDoubleScalar<I> initializedDoubleScalar(double value, I[] ids, int d0) {
        return (IdDoubleScalar<I>) initializedDoubleTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public DoubleVector doubleVector(int d0) {
        return (DoubleVector) doubleTensor(d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdDoubleVector<I> doubleVector(List<I> ids, int d0) {
        return (IdDoubleVector<I>) doubleTensor(Arrays.asList(ids),d0);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdDoubleVector<I> doubleVector(I[] ids, int d0) {
        return (IdDoubleVector<I>) doubleTensor((I[][]) new Object[][] {ids},d0);
    }

    public DoubleVector initializedDoubleVector(double value, int d0) {
        return (DoubleVector) initializedDoubleTensor(value,d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdDoubleVector<I> initializedDoubleVector(double value, List<I> ids, int d0) {
        return (IdDoubleVector<I>) initializedDoubleTensor(value,Arrays.asList(ids),d0);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdDoubleVector<I> initializedDoubleVector(double value, I[] ids, int d0) {
        return (IdDoubleVector<I>) initializedDoubleTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public DoubleMatrix doubleMatrix(int d0, int d1) {
        return (DoubleMatrix) doubleTensor(d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdDoubleMatrix<I> doubleMatrix(List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdDoubleMatrix<I>) doubleTensor(Arrays.asList(d0Ids,d1Ids),d0,d1);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdDoubleMatrix<I> doubleMatrix(I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdDoubleMatrix<I>) doubleTensor((I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public DoubleMatrix initializedDoubleMatrix(double value, int d0, int d1) {
        return (DoubleMatrix) initializedDoubleTensor(value,d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdDoubleMatrix<I> initializedDoubleMatrix(double value,List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdDoubleMatrix<I>) initializedDoubleTensor(value,Arrays.asList(d0Ids,d1Ids),d0,d1);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdDoubleMatrix<I> initializedDoubleMatrix(double value, I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdDoubleMatrix<I>) initializedDoubleTensor(value,(I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public CharScalar charScalar() {
        return (CharScalar) charTensor();
    }

    public CharScalar initializedCharScalar(char value) {
        return (CharScalar) initializedCharTensor(value);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdCharScalar<I> initializedCharScalar(char value, I[] ids, int d0) {
        return (IdCharScalar<I>) initializedCharTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public CharVector charVector(int d0) {
        return (CharVector) charTensor(d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdCharVector<I> charVector(List<I> ids, int d0) {
        return (IdCharVector<I>) charTensor(Arrays.asList(ids),d0);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdCharVector<I> charVector(I[] ids, int d0) {
        return (IdCharVector<I>) charTensor((I[][]) new Object[][] {ids},d0);
    }

    public CharVector initializedCharVector(char value, int d0) {
        return (CharVector) initializedCharTensor(value,d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdCharVector<I> initializedCharVector(char value, List<I> ids, int d0) {
        return (IdCharVector<I>) initializedCharTensor(value,Arrays.asList(ids),d0);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdCharVector<I> initializedCharVector(char value, I[] ids, int d0) {
        return (IdCharVector<I>) initializedCharTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public CharMatrix charMatrix(int d0, int d1) {
        return (CharMatrix) charTensor(d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdCharMatrix<I> charMatrix(List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdCharMatrix<I>) charTensor(Arrays.asList(d0Ids,d1Ids),d0,d1);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdCharMatrix<I> charMatrix(I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdCharMatrix<I>) charTensor((I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public CharMatrix initializedCharMatrix(char value, int d0, int d1) {
        return (CharMatrix) initializedCharTensor(value,d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdCharMatrix<I> initializedCharMatrix(char value,List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdCharMatrix<I>) initializedCharTensor(value,Arrays.asList(d0Ids,d1Ids),d0,d1);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdCharMatrix<I> initializedCharMatrix(char value, I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdCharMatrix<I>) initializedCharTensor(value,(I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public BooleanScalar booleanScalar() {
        return (BooleanScalar) booleanTensor();
    }

    public BooleanScalar initializedBooleanScalar(boolean value) {
        return (BooleanScalar) initializedBooleanTensor(value);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdBooleanScalar<I> initializedBooleanScalar(boolean value, I[] ids, int d0) {
        return (IdBooleanScalar<I>) initializedBooleanTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public BooleanVector booleanVector(int d0) {
        return (BooleanVector) booleanTensor(d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdBooleanVector<I> booleanVector(List<I> ids, int d0) {
        return (IdBooleanVector<I>) booleanTensor(Arrays.asList(ids),d0);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdBooleanVector<I> booleanVector(I[] ids, int d0) {
        return (IdBooleanVector<I>) booleanTensor((I[][]) new Object[][] {ids},d0);
    }

    public BooleanVector initializedBooleanVector(boolean value, int d0) {
        return (BooleanVector) initializedBooleanTensor(value,d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdBooleanVector<I> initializedBooleanVector(boolean value, List<I> ids, int d0) {
        return (IdBooleanVector<I>) initializedBooleanTensor(value,Arrays.asList(ids),d0);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdBooleanVector<I> initializedBooleanVector(boolean value, I[] ids, int d0) {
        return (IdBooleanVector<I>) initializedBooleanTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    public BooleanMatrix booleanMatrix(int d0, int d1) {
        return (BooleanMatrix) booleanTensor(d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdBooleanMatrix<I> booleanMatrix(List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdBooleanMatrix<I>) booleanTensor(Arrays.asList(d0Ids,d1Ids),d0,d1);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdBooleanMatrix<I> booleanMatrix(I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdBooleanMatrix<I>) booleanTensor((I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public BooleanMatrix initializedBooleanMatrix(boolean value, int d0, int d1) {
        return (BooleanMatrix) initializedBooleanTensor(value,d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <I> IdBooleanMatrix<I> initializedBooleanMatrix(boolean value,List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdBooleanMatrix<I>) initializedBooleanTensor(value,Arrays.asList(d0Ids,d1Ids),d0,d1);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <I> IdBooleanMatrix<I> initializedBooleanMatrix(boolean value, I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdBooleanMatrix<I>) initializedBooleanTensor(value,(I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <T> Scalar<T> scalar() {
        return (Scalar<T>) tensor();
    }

    public <T> Scalar<T> initializedScalar(T value) {
        return (Scalar<T>) initializedTensor(value);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <T,I> IdScalar<T,I> initializedScalar(T value, I[] ids, int d0) {
        return (IdScalar<T,I>) initializedTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    @SuppressWarnings("unchecked") //generic type ok here
    public <T> Vector<T> vector(int d0) {
        return (Vector<T>) tensor(d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <T,I> IdVector<T,I> vector(List<I> ids, int d0) {
        return (IdVector<T,I>) this.<T,I>tensor(Arrays.asList(ids),d0);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <T,I> IdVector<T,I> vector(I[] ids, int d0) {
        return (IdVector<T,I>) this.<T,I>tensor((I[][]) new Object[][] {ids},d0);
    }

    public <T> Vector<T> initializedVector(T value, int d0) {
        return (Vector<T>) initializedTensor(value,d0);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <T,I> IdVector<T,I> initializedVector(T value, List<I> ids, int d0) {
        return (IdVector<T,I>) initializedTensor(value,Arrays.asList(ids),d0);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <T,I> IdVector<T,I> initializedVector(T value, I[] ids, int d0) {
        return (IdVector<T,I>) initializedTensor(value,(I[][]) new Object[][] {ids},d0);
    }

    @SuppressWarnings("unchecked") //generic type ok here
    public <T> Matrix<T> matrix(int d0, int d1) {
        return (Matrix<T>) tensor(d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <T,I> IdMatrix<T,I> matrix(List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdMatrix<T,I>) this.<T,I>tensor(Arrays.asList(d0Ids,d1Ids),d0,d1);

    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <T,I> IdMatrix<T,I> matrix(I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdMatrix<T,I>) this.<T,I>tensor((I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    public <T> Matrix<T> initializedMatrix(T value, int d0, int d1) {
        return (Matrix<T>) initializedTensor(value,d0,d1);
    }

    @SuppressWarnings("unchecked") //generic array ok here
    public <T,I> IdMatrix<T,I> initializedMatrix(T value,List<I> d0Ids, List<I> d1Ids, int d0, int d1) {
        return (IdMatrix<T,I>) initializedTensor(value,Arrays.asList(d0Ids,d1Ids),d0,d1);
    }

    @SuppressWarnings("unchecked") //effectively an I[][]
    public <T,I> IdMatrix<T,I> initializedMatrix(T value, I[] d0Ids, I[] d1Ids, int d0, int d1) {
        return (IdMatrix<T,I>) initializedTensor(value,(I[][]) new Object[][] {d0Ids,d1Ids},d0,d1);
    }

    <I> IdByteTensor<I> byteTensor(List<List<I>> ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdByteD0TensorShell<I>((ByteD0Tensor) tensor);
            case 1 : return new IdByteD1TensorShell<I>((ByteD1Tensor) tensor,ids);
            case 2 : return new IdByteD2TensorShell<I>((ByteD2Tensor) tensor,ids);
            case 3 : return new IdByteD3TensorShell<I>((ByteD3Tensor) tensor,ids);
            case 4 : return new IdByteD4TensorShell<I>((ByteD4Tensor) tensor,ids);
            case 5 : return new IdByteD5TensorShell<I>((ByteD5Tensor) tensor,ids);
            case 6 : return new IdByteD6TensorShell<I>((ByteD6Tensor) tensor,ids);
            case 7 : return new IdByteD7TensorShell<I>((ByteD7Tensor) tensor,ids);
            case 8 : return new IdByteD8TensorShell<I>((ByteD8Tensor) tensor,ids);
            case 9 : return new IdByteD9TensorShell<I>((ByteD9Tensor) tensor,ids);
            default : return new IdByteTensorShell<I>((ByteTensor) tensor,ids);
        }
    }

    <I> IdByteTensor<I> byteTensor(I[][] ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdByteD0TensorShell<I>((ByteD0Tensor) tensor);
            case 1 : return new IdByteD1TensorShell<I>((ByteD1Tensor) tensor,ids);
            case 2 : return new IdByteD2TensorShell<I>((ByteD2Tensor) tensor,ids);
            case 3 : return new IdByteD3TensorShell<I>((ByteD3Tensor) tensor,ids);
            case 4 : return new IdByteD4TensorShell<I>((ByteD4Tensor) tensor,ids);
            case 5 : return new IdByteD5TensorShell<I>((ByteD5Tensor) tensor,ids);
            case 6 : return new IdByteD6TensorShell<I>((ByteD6Tensor) tensor,ids);
            case 7 : return new IdByteD7TensorShell<I>((ByteD7Tensor) tensor,ids);
            case 8 : return new IdByteD8TensorShell<I>((ByteD8Tensor) tensor,ids);
            case 9 : return new IdByteD9TensorShell<I>((ByteD9Tensor) tensor,ids);
            default : return new IdByteTensorShell<I>((ByteTensor) tensor,ids);
        }
    }

    public <I> IdByteTensor<I> byteTensor(List<List<I>> ids, int ... dimensions) {
        return byteTensor(ids,byteTensor(dimensions));
    }

    public <I> IdByteTensor<I> initializedByteTensor(byte defaultValue, List<List<I>> ids, int ... dimensions) {
        return byteTensor(ids,initializedByteTensor(defaultValue,dimensions));
    }

    public <I> IdByteTensor<I> byteTensor(I[][] ids, int ... dimensions) {
        return byteTensor(ids,byteTensor(dimensions));
    }

    public <I> IdByteTensor<I> initializedByteTensor(byte defaultValue, I[][] ids, int ... dimensions) {
        return byteTensor(ids,initializedByteTensor(defaultValue,dimensions));
    }

    ByteTensor concurrentByteTensor(int concurrencyLevel, Tensor tensor, int[] dimensions) {
        switch (dimensions.length) {
            case 0 : return new ConcurrentByteD0TensorShell((ByteD0Tensor) tensor, ConcurrentTensorLocksFactory.getD0Locks());
            case 1 : return new ConcurrentByteD1TensorShell((ByteD1Tensor) tensor,ConcurrentTensorLocksFactory.getD1Locks(dimensions,concurrencyLevel));
            case 2 : return new ConcurrentByteD2TensorShell((ByteD2Tensor) tensor,ConcurrentTensorLocksFactory.getD2Locks(dimensions,concurrencyLevel));
            case 3 : return new ConcurrentByteD3TensorShell((ByteD3Tensor) tensor,ConcurrentTensorLocksFactory.getD3Locks(dimensions,concurrencyLevel));
            case 4 : return new ConcurrentByteD4TensorShell((ByteD4Tensor) tensor,ConcurrentTensorLocksFactory.getD4Locks(dimensions,concurrencyLevel));
            case 5 : return new ConcurrentByteD5TensorShell((ByteD5Tensor) tensor,ConcurrentTensorLocksFactory.getD5Locks(dimensions,concurrencyLevel));
            case 6 : return new ConcurrentByteD6TensorShell((ByteD6Tensor) tensor,ConcurrentTensorLocksFactory.getD6Locks(dimensions,concurrencyLevel));
            case 7 : return new ConcurrentByteD7TensorShell((ByteD7Tensor) tensor,ConcurrentTensorLocksFactory.getD7Locks(dimensions,concurrencyLevel));
            case 8 : return new ConcurrentByteD8TensorShell((ByteD8Tensor) tensor,ConcurrentTensorLocksFactory.getD8Locks(dimensions,concurrencyLevel));
            case 9 : return new ConcurrentByteD9TensorShell((ByteD9Tensor) tensor,ConcurrentTensorLocksFactory.getD9Locks(dimensions,concurrencyLevel));
            default : return new ConcurrentByteTensorShell(byteTensor(dimensions),ConcurrentTensorLocksFactory.getLocks(dimensions,concurrencyLevel));
        }
    }

    public ByteTensor concurrentByteTensor(int concurrencyLevel, int ... dimensions) {
        return concurrentByteTensor(concurrencyLevel,byteTensor(dimensions),dimensions);
    }

    public ByteTensor initializedConcurrentByteTensor(byte defaultValue, int concurrencyLevel, int ... dimensions) {
        return concurrentByteTensor(concurrencyLevel,initializedByteTensor(defaultValue,dimensions),dimensions);
    }

    public <I> IdByteTensor<I> concurrentByteTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return byteTensor(ids,concurrentByteTensor(concurrencyLevel,dimensions));
    }

    public <I> IdByteTensor<I> initializedConcurrentByteTensor(byte defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return byteTensor(ids,initializedConcurrentByteTensor(defaultValue,concurrencyLevel,dimensions));
    }

    public <I> IdByteTensor<I> concurrentByteTensor(I[][] ids, int concurrencyLevel, int ... dimensions) {
        return byteTensor(ids,concurrentByteTensor(concurrencyLevel,dimensions));
    }

    public <I> IdByteTensor<I> initializedConcurrentByteTensor(byte defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions) {
        return byteTensor(ids,initializedConcurrentByteTensor(defaultValue,concurrencyLevel,dimensions));
    }

    <I> IdShortTensor<I> shortTensor(List<List<I>> ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdShortD0TensorShell<I>((ShortD0Tensor) tensor);
            case 1 : return new IdShortD1TensorShell<I>((ShortD1Tensor) tensor,ids);
            case 2 : return new IdShortD2TensorShell<I>((ShortD2Tensor) tensor,ids);
            case 3 : return new IdShortD3TensorShell<I>((ShortD3Tensor) tensor,ids);
            case 4 : return new IdShortD4TensorShell<I>((ShortD4Tensor) tensor,ids);
            case 5 : return new IdShortD5TensorShell<I>((ShortD5Tensor) tensor,ids);
            case 6 : return new IdShortD6TensorShell<I>((ShortD6Tensor) tensor,ids);
            case 7 : return new IdShortD7TensorShell<I>((ShortD7Tensor) tensor,ids);
            case 8 : return new IdShortD8TensorShell<I>((ShortD8Tensor) tensor,ids);
            case 9 : return new IdShortD9TensorShell<I>((ShortD9Tensor) tensor,ids);
            default : return new IdShortTensorShell<I>((ShortTensor) tensor,ids);
        }
    }

    <I> IdShortTensor<I> shortTensor(I[][] ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdShortD0TensorShell<I>((ShortD0Tensor) tensor);
            case 1 : return new IdShortD1TensorShell<I>((ShortD1Tensor) tensor,ids);
            case 2 : return new IdShortD2TensorShell<I>((ShortD2Tensor) tensor,ids);
            case 3 : return new IdShortD3TensorShell<I>((ShortD3Tensor) tensor,ids);
            case 4 : return new IdShortD4TensorShell<I>((ShortD4Tensor) tensor,ids);
            case 5 : return new IdShortD5TensorShell<I>((ShortD5Tensor) tensor,ids);
            case 6 : return new IdShortD6TensorShell<I>((ShortD6Tensor) tensor,ids);
            case 7 : return new IdShortD7TensorShell<I>((ShortD7Tensor) tensor,ids);
            case 8 : return new IdShortD8TensorShell<I>((ShortD8Tensor) tensor,ids);
            case 9 : return new IdShortD9TensorShell<I>((ShortD9Tensor) tensor,ids);
            default : return new IdShortTensorShell<I>((ShortTensor) tensor,ids);
        }
    }

    public <I> IdShortTensor<I> shortTensor(List<List<I>> ids, int ... dimensions) {
        return shortTensor(ids,shortTensor(dimensions));
    }

    public <I> IdShortTensor<I> initializedShortTensor(short defaultValue, List<List<I>> ids, int ... dimensions) {
        return shortTensor(ids,initializedShortTensor(defaultValue,dimensions));
    }

    public <I> IdShortTensor<I> shortTensor(I[][] ids, int ... dimensions) {
        return shortTensor(ids,shortTensor(dimensions));
    }

    public <I> IdShortTensor<I> initializedShortTensor(short defaultValue, I[][] ids, int ... dimensions) {
        return shortTensor(ids,initializedShortTensor(defaultValue,dimensions));
    }

    ShortTensor concurrentShortTensor(int concurrencyLevel, Tensor tensor, int[] dimensions) {
        switch (dimensions.length) {
            case 0 : return new ConcurrentShortD0TensorShell((ShortD0Tensor) tensor,ConcurrentTensorLocksFactory.getD0Locks());
            case 1 : return new ConcurrentShortD1TensorShell((ShortD1Tensor) tensor,ConcurrentTensorLocksFactory.getD1Locks(dimensions,concurrencyLevel));
            case 2 : return new ConcurrentShortD2TensorShell((ShortD2Tensor) tensor,ConcurrentTensorLocksFactory.getD2Locks(dimensions,concurrencyLevel));
            case 3 : return new ConcurrentShortD3TensorShell((ShortD3Tensor) tensor,ConcurrentTensorLocksFactory.getD3Locks(dimensions,concurrencyLevel));
            case 4 : return new ConcurrentShortD4TensorShell((ShortD4Tensor) tensor,ConcurrentTensorLocksFactory.getD4Locks(dimensions,concurrencyLevel));
            case 5 : return new ConcurrentShortD5TensorShell((ShortD5Tensor) tensor,ConcurrentTensorLocksFactory.getD5Locks(dimensions,concurrencyLevel));
            case 6 : return new ConcurrentShortD6TensorShell((ShortD6Tensor) tensor,ConcurrentTensorLocksFactory.getD6Locks(dimensions,concurrencyLevel));
            case 7 : return new ConcurrentShortD7TensorShell((ShortD7Tensor) tensor,ConcurrentTensorLocksFactory.getD7Locks(dimensions,concurrencyLevel));
            case 8 : return new ConcurrentShortD8TensorShell((ShortD8Tensor) tensor,ConcurrentTensorLocksFactory.getD8Locks(dimensions,concurrencyLevel));
            case 9 : return new ConcurrentShortD9TensorShell((ShortD9Tensor) tensor,ConcurrentTensorLocksFactory.getD9Locks(dimensions,concurrencyLevel));
            default : return new ConcurrentShortTensorShell(shortTensor(dimensions),ConcurrentTensorLocksFactory.getLocks(dimensions,concurrencyLevel));
        }
    }

    public ShortTensor concurrentShortTensor(int concurrencyLevel, int ... dimensions) {
        return concurrentShortTensor(concurrencyLevel,shortTensor(dimensions),dimensions);
    }

    public ShortTensor initializedConcurrentShortTensor(short defaultValue, int concurrencyLevel, int ... dimensions) {
        return concurrentShortTensor(concurrencyLevel,initializedShortTensor(defaultValue,dimensions),dimensions);
    }

    public <I> IdShortTensor<I> concurrentShortTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return shortTensor(ids,concurrentShortTensor(concurrencyLevel,dimensions));
    }

    public <I> IdShortTensor<I> initializedConcurrentShortTensor(short defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return shortTensor(ids,initializedConcurrentShortTensor(defaultValue,concurrencyLevel,dimensions));
    }

    public <I> IdShortTensor<I> concurrentShortTensor(I[][] ids, int concurrencyLevel, int ... dimensions) {
        return shortTensor(ids,concurrentShortTensor(concurrencyLevel,dimensions));
    }

    public <I> IdShortTensor<I> initializedConcurrentShortTensor(short defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions) {
        return shortTensor(ids,initializedConcurrentShortTensor(defaultValue,concurrencyLevel,dimensions));
    }

    <I> IdIntTensor<I> intTensor(List<List<I>> ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdIntD0TensorShell<I>((IntD0Tensor) tensor);
            case 1 : return new IdIntD1TensorShell<I>((IntD1Tensor) tensor,ids);
            case 2 : return new IdIntD2TensorShell<I>((IntD2Tensor) tensor,ids);
            case 3 : return new IdIntD3TensorShell<I>((IntD3Tensor) tensor,ids);
            case 4 : return new IdIntD4TensorShell<I>((IntD4Tensor) tensor,ids);
            case 5 : return new IdIntD5TensorShell<I>((IntD5Tensor) tensor,ids);
            case 6 : return new IdIntD6TensorShell<I>((IntD6Tensor) tensor,ids);
            case 7 : return new IdIntD7TensorShell<I>((IntD7Tensor) tensor,ids);
            case 8 : return new IdIntD8TensorShell<I>((IntD8Tensor) tensor,ids);
            case 9 : return new IdIntD9TensorShell<I>((IntD9Tensor) tensor,ids);
            default : return new IdIntTensorShell<I>((IntTensor) tensor,ids);
        }
    }

    <I> IdIntTensor<I> intTensor(I[][] ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdIntD0TensorShell<I>((IntD0Tensor) tensor);
            case 1 : return new IdIntD1TensorShell<I>((IntD1Tensor) tensor,ids);
            case 2 : return new IdIntD2TensorShell<I>((IntD2Tensor) tensor,ids);
            case 3 : return new IdIntD3TensorShell<I>((IntD3Tensor) tensor,ids);
            case 4 : return new IdIntD4TensorShell<I>((IntD4Tensor) tensor,ids);
            case 5 : return new IdIntD5TensorShell<I>((IntD5Tensor) tensor,ids);
            case 6 : return new IdIntD6TensorShell<I>((IntD6Tensor) tensor,ids);
            case 7 : return new IdIntD7TensorShell<I>((IntD7Tensor) tensor,ids);
            case 8 : return new IdIntD8TensorShell<I>((IntD8Tensor) tensor,ids);
            case 9 : return new IdIntD9TensorShell<I>((IntD9Tensor) tensor,ids);
            default : return new IdIntTensorShell<I>((IntTensor) tensor,ids);
        }
    }

    public <I> IdIntTensor<I> intTensor(List<List<I>> ids, int ... dimensions) {
        return intTensor(ids,intTensor(dimensions));
    }

    public <I> IdIntTensor<I> initializedIntTensor(int defaultValue, List<List<I>> ids, int ... dimensions) {
        return intTensor(ids,initializedIntTensor(defaultValue,dimensions));
    }

    public <I> IdIntTensor<I> intTensor(I[][] ids, int ... dimensions) {
        return intTensor(ids,intTensor(dimensions));
    }

    public <I> IdIntTensor<I> initializedIntTensor(int defaultValue, I[][] ids, int ... dimensions) {
        return intTensor(ids,initializedIntTensor(defaultValue,dimensions));
    }

    IntTensor concurrentIntTensor(int concurrencyLevel, Tensor tensor, int[] dimensions) {
        switch (dimensions.length) {
            case 0 : return new ConcurrentIntD0TensorShell((IntD0Tensor) tensor,ConcurrentTensorLocksFactory.getD0Locks());
            case 1 : return new ConcurrentIntD1TensorShell((IntD1Tensor) tensor,ConcurrentTensorLocksFactory.getD1Locks(dimensions,concurrencyLevel));
            case 2 : return new ConcurrentIntD2TensorShell((IntD2Tensor) tensor,ConcurrentTensorLocksFactory.getD2Locks(dimensions,concurrencyLevel));
            case 3 : return new ConcurrentIntD3TensorShell((IntD3Tensor) tensor,ConcurrentTensorLocksFactory.getD3Locks(dimensions,concurrencyLevel));
            case 4 : return new ConcurrentIntD4TensorShell((IntD4Tensor) tensor,ConcurrentTensorLocksFactory.getD4Locks(dimensions,concurrencyLevel));
            case 5 : return new ConcurrentIntD5TensorShell((IntD5Tensor) tensor,ConcurrentTensorLocksFactory.getD5Locks(dimensions,concurrencyLevel));
            case 6 : return new ConcurrentIntD6TensorShell((IntD6Tensor) tensor,ConcurrentTensorLocksFactory.getD6Locks(dimensions,concurrencyLevel));
            case 7 : return new ConcurrentIntD7TensorShell((IntD7Tensor) tensor,ConcurrentTensorLocksFactory.getD7Locks(dimensions,concurrencyLevel));
            case 8 : return new ConcurrentIntD8TensorShell((IntD8Tensor) tensor,ConcurrentTensorLocksFactory.getD8Locks(dimensions,concurrencyLevel));
            case 9 : return new ConcurrentIntD9TensorShell((IntD9Tensor) tensor,ConcurrentTensorLocksFactory.getD9Locks(dimensions,concurrencyLevel));
            default : return new ConcurrentIntTensorShell(intTensor(dimensions),ConcurrentTensorLocksFactory.getLocks(dimensions,concurrencyLevel));
        }
    }

    public IntTensor concurrentIntTensor(int concurrencyLevel, int ... dimensions) {
        return concurrentIntTensor(concurrencyLevel,intTensor(dimensions),dimensions);
    }

    public IntTensor initializedConcurrentIntTensor(int defaultValue, int concurrencyLevel, int ... dimensions) {
        return concurrentIntTensor(concurrencyLevel,initializedIntTensor(defaultValue,dimensions),dimensions);
    }

    public <I> IdIntTensor<I> concurrentIntTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return intTensor(ids,concurrentIntTensor(concurrencyLevel,dimensions));
    }

    public <I> IdIntTensor<I> initializedConcurrentIntTensor(int defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return intTensor(ids,initializedConcurrentIntTensor(defaultValue,concurrencyLevel,dimensions));
    }

    public <I> IdIntTensor<I> concurrentIntTensor(I[][] ids, int concurrencyLevel, int ... dimensions) {
        return intTensor(ids,concurrentIntTensor(concurrencyLevel,dimensions));
    }

    public <I> IdIntTensor<I> initializedConcurrentIntTensor(int defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions) {
        return intTensor(ids,initializedConcurrentIntTensor(defaultValue,concurrencyLevel,dimensions));
    }

    <I> IdLongTensor<I> longTensor(List<List<I>> ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdLongD0TensorShell<I>((LongD0Tensor) tensor);
            case 1 : return new IdLongD1TensorShell<I>((LongD1Tensor) tensor,ids);
            case 2 : return new IdLongD2TensorShell<I>((LongD2Tensor) tensor,ids);
            case 3 : return new IdLongD3TensorShell<I>((LongD3Tensor) tensor,ids);
            case 4 : return new IdLongD4TensorShell<I>((LongD4Tensor) tensor,ids);
            case 5 : return new IdLongD5TensorShell<I>((LongD5Tensor) tensor,ids);
            case 6 : return new IdLongD6TensorShell<I>((LongD6Tensor) tensor,ids);
            case 7 : return new IdLongD7TensorShell<I>((LongD7Tensor) tensor,ids);
            case 8 : return new IdLongD8TensorShell<I>((LongD8Tensor) tensor,ids);
            case 9 : return new IdLongD9TensorShell<I>((LongD9Tensor) tensor,ids);
            default : return new IdLongTensorShell<I>((LongTensor) tensor,ids);
        }
    }

    <I> IdLongTensor<I> longTensor(I[][] ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdLongD0TensorShell<I>((LongD0Tensor) tensor);
            case 1 : return new IdLongD1TensorShell<I>((LongD1Tensor) tensor,ids);
            case 2 : return new IdLongD2TensorShell<I>((LongD2Tensor) tensor,ids);
            case 3 : return new IdLongD3TensorShell<I>((LongD3Tensor) tensor,ids);
            case 4 : return new IdLongD4TensorShell<I>((LongD4Tensor) tensor,ids);
            case 5 : return new IdLongD5TensorShell<I>((LongD5Tensor) tensor,ids);
            case 6 : return new IdLongD6TensorShell<I>((LongD6Tensor) tensor,ids);
            case 7 : return new IdLongD7TensorShell<I>((LongD7Tensor) tensor,ids);
            case 8 : return new IdLongD8TensorShell<I>((LongD8Tensor) tensor,ids);
            case 9 : return new IdLongD9TensorShell<I>((LongD9Tensor) tensor,ids);
            default : return new IdLongTensorShell<I>((LongTensor) tensor,ids);
        }
    }

    public <I> IdLongTensor<I> longTensor(List<List<I>> ids, int ... dimensions) {
        return longTensor(ids,longTensor(dimensions));
    }

    public <I> IdLongTensor<I> initializedLongTensor(long defaultValue, List<List<I>> ids, int ... dimensions) {
        return longTensor(ids,initializedLongTensor(defaultValue,dimensions));
    }

    public <I> IdLongTensor<I> longTensor(I[][] ids, int ... dimensions) {
        return longTensor(ids,longTensor(dimensions));
    }

    public <I> IdLongTensor<I> initializedLongTensor(long defaultValue, I[][] ids, int ... dimensions) {
        return longTensor(ids,initializedLongTensor(defaultValue,dimensions));
    }

    LongTensor concurrentLongTensor(int concurrencyLevel, Tensor tensor, int[] dimensions) {
        switch (dimensions.length) {
            case 0 : return new ConcurrentLongD0TensorShell((LongD0Tensor) tensor,ConcurrentTensorLocksFactory.getD0Locks());
            case 1 : return new ConcurrentLongD1TensorShell((LongD1Tensor) tensor,ConcurrentTensorLocksFactory.getD1Locks(dimensions,concurrencyLevel));
            case 2 : return new ConcurrentLongD2TensorShell((LongD2Tensor) tensor,ConcurrentTensorLocksFactory.getD2Locks(dimensions,concurrencyLevel));
            case 3 : return new ConcurrentLongD3TensorShell((LongD3Tensor) tensor,ConcurrentTensorLocksFactory.getD3Locks(dimensions,concurrencyLevel));
            case 4 : return new ConcurrentLongD4TensorShell((LongD4Tensor) tensor,ConcurrentTensorLocksFactory.getD4Locks(dimensions,concurrencyLevel));
            case 5 : return new ConcurrentLongD5TensorShell((LongD5Tensor) tensor,ConcurrentTensorLocksFactory.getD5Locks(dimensions,concurrencyLevel));
            case 6 : return new ConcurrentLongD6TensorShell((LongD6Tensor) tensor,ConcurrentTensorLocksFactory.getD6Locks(dimensions,concurrencyLevel));
            case 7 : return new ConcurrentLongD7TensorShell((LongD7Tensor) tensor,ConcurrentTensorLocksFactory.getD7Locks(dimensions,concurrencyLevel));
            case 8 : return new ConcurrentLongD8TensorShell((LongD8Tensor) tensor,ConcurrentTensorLocksFactory.getD8Locks(dimensions,concurrencyLevel));
            case 9 : return new ConcurrentLongD9TensorShell((LongD9Tensor) tensor,ConcurrentTensorLocksFactory.getD9Locks(dimensions,concurrencyLevel));
            default : return new ConcurrentLongTensorShell(longTensor(dimensions),ConcurrentTensorLocksFactory.getLocks(dimensions,concurrencyLevel));
        }
    }

    public LongTensor concurrentLongTensor(int concurrencyLevel, int ... dimensions) {
        return concurrentLongTensor(concurrencyLevel,longTensor(dimensions),dimensions);
    }

    public LongTensor initializedConcurrentLongTensor(long defaultValue, int concurrencyLevel, int ... dimensions) {
        return concurrentLongTensor(concurrencyLevel,initializedLongTensor(defaultValue,dimensions),dimensions);
    }

    public <I> IdLongTensor<I> concurrentLongTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return longTensor(ids,concurrentLongTensor(concurrencyLevel,dimensions));
    }

    public <I> IdLongTensor<I> initializedConcurrentLongTensor(long defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return longTensor(ids,initializedConcurrentLongTensor(defaultValue,concurrencyLevel,dimensions));
    }

    public <I> IdLongTensor<I> concurrentLongTensor(I[][] ids, int concurrencyLevel, int ... dimensions) {
        return longTensor(ids,concurrentLongTensor(concurrencyLevel,dimensions));
    }

    public <I> IdLongTensor<I> initializedConcurrentLongTensor(long defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions) {
        return longTensor(ids,initializedConcurrentLongTensor(defaultValue,concurrencyLevel,dimensions));
    }

    <I> IdFloatTensor<I> floatTensor(List<List<I>> ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdFloatD0TensorShell<I>((FloatD0Tensor) tensor);
            case 1 : return new IdFloatD1TensorShell<I>((FloatD1Tensor) tensor,ids);
            case 2 : return new IdFloatD2TensorShell<I>((FloatD2Tensor) tensor,ids);
            case 3 : return new IdFloatD3TensorShell<I>((FloatD3Tensor) tensor,ids);
            case 4 : return new IdFloatD4TensorShell<I>((FloatD4Tensor) tensor,ids);
            case 5 : return new IdFloatD5TensorShell<I>((FloatD5Tensor) tensor,ids);
            case 6 : return new IdFloatD6TensorShell<I>((FloatD6Tensor) tensor,ids);
            case 7 : return new IdFloatD7TensorShell<I>((FloatD7Tensor) tensor,ids);
            case 8 : return new IdFloatD8TensorShell<I>((FloatD8Tensor) tensor,ids);
            case 9 : return new IdFloatD9TensorShell<I>((FloatD9Tensor) tensor,ids);
            default : return new IdFloatTensorShell<I>((FloatTensor) tensor,ids);
        }
    }

    <I> IdFloatTensor<I> floatTensor(I[][] ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdFloatD0TensorShell<I>((FloatD0Tensor) tensor);
            case 1 : return new IdFloatD1TensorShell<I>((FloatD1Tensor) tensor,ids);
            case 2 : return new IdFloatD2TensorShell<I>((FloatD2Tensor) tensor,ids);
            case 3 : return new IdFloatD3TensorShell<I>((FloatD3Tensor) tensor,ids);
            case 4 : return new IdFloatD4TensorShell<I>((FloatD4Tensor) tensor,ids);
            case 5 : return new IdFloatD5TensorShell<I>((FloatD5Tensor) tensor,ids);
            case 6 : return new IdFloatD6TensorShell<I>((FloatD6Tensor) tensor,ids);
            case 7 : return new IdFloatD7TensorShell<I>((FloatD7Tensor) tensor,ids);
            case 8 : return new IdFloatD8TensorShell<I>((FloatD8Tensor) tensor,ids);
            case 9 : return new IdFloatD9TensorShell<I>((FloatD9Tensor) tensor,ids);
            default : return new IdFloatTensorShell<I>((FloatTensor) tensor,ids);
        }
    }

    public <I> IdFloatTensor<I> floatTensor(List<List<I>> ids, int ... dimensions) {
        return floatTensor(ids,floatTensor(dimensions));
    }

    public <I> IdFloatTensor<I> initializedFloatTensor(float defaultValue, List<List<I>> ids, int ... dimensions) {
        return floatTensor(ids,initializedFloatTensor(defaultValue,dimensions));
    }

    public <I> IdFloatTensor<I> floatTensor(I[][] ids, int ... dimensions) {
        return floatTensor(ids,floatTensor(dimensions));
    }

    public <I> IdFloatTensor<I> initializedFloatTensor(float defaultValue, I[][] ids, int ... dimensions) {
        return floatTensor(ids,initializedFloatTensor(defaultValue,dimensions));
    }

    FloatTensor concurrentFloatTensor(int concurrencyLevel, Tensor tensor, int[] dimensions) {
        switch (dimensions.length) {
            case 0 : return new ConcurrentFloatD0TensorShell((FloatD0Tensor) tensor,ConcurrentTensorLocksFactory.getD0Locks());
            case 1 : return new ConcurrentFloatD1TensorShell((FloatD1Tensor) tensor,ConcurrentTensorLocksFactory.getD1Locks(dimensions,concurrencyLevel));
            case 2 : return new ConcurrentFloatD2TensorShell((FloatD2Tensor) tensor,ConcurrentTensorLocksFactory.getD2Locks(dimensions,concurrencyLevel));
            case 3 : return new ConcurrentFloatD3TensorShell((FloatD3Tensor) tensor,ConcurrentTensorLocksFactory.getD3Locks(dimensions,concurrencyLevel));
            case 4 : return new ConcurrentFloatD4TensorShell((FloatD4Tensor) tensor,ConcurrentTensorLocksFactory.getD4Locks(dimensions,concurrencyLevel));
            case 5 : return new ConcurrentFloatD5TensorShell((FloatD5Tensor) tensor,ConcurrentTensorLocksFactory.getD5Locks(dimensions,concurrencyLevel));
            case 6 : return new ConcurrentFloatD6TensorShell((FloatD6Tensor) tensor,ConcurrentTensorLocksFactory.getD6Locks(dimensions,concurrencyLevel));
            case 7 : return new ConcurrentFloatD7TensorShell((FloatD7Tensor) tensor,ConcurrentTensorLocksFactory.getD7Locks(dimensions,concurrencyLevel));
            case 8 : return new ConcurrentFloatD8TensorShell((FloatD8Tensor) tensor,ConcurrentTensorLocksFactory.getD8Locks(dimensions,concurrencyLevel));
            case 9 : return new ConcurrentFloatD9TensorShell((FloatD9Tensor) tensor,ConcurrentTensorLocksFactory.getD9Locks(dimensions,concurrencyLevel));
            default : return new ConcurrentFloatTensorShell(floatTensor(dimensions),ConcurrentTensorLocksFactory.getLocks(dimensions,concurrencyLevel));
        }
    }

    public FloatTensor concurrentFloatTensor(int concurrencyLevel, int ... dimensions) {
        return concurrentFloatTensor(concurrencyLevel,floatTensor(dimensions),dimensions);
    }

    public FloatTensor initializedConcurrentFloatTensor(float defaultValue, int concurrencyLevel, int ... dimensions) {
        return concurrentFloatTensor(concurrencyLevel,initializedFloatTensor(defaultValue,dimensions),dimensions);
    }

    public <I> IdFloatTensor<I> concurrentFloatTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return floatTensor(ids,concurrentFloatTensor(concurrencyLevel,dimensions));
    }

    public <I> IdFloatTensor<I> initializedConcurrentFloatTensor(float defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return floatTensor(ids,initializedConcurrentFloatTensor(defaultValue,concurrencyLevel,dimensions));
    }

    public <I> IdFloatTensor<I> concurrentFloatTensor(I[][] ids, int concurrencyLevel, int ... dimensions) {
        return floatTensor(ids,concurrentFloatTensor(concurrencyLevel,dimensions));
    }

    public <I> IdFloatTensor<I> initializedConcurrentFloatTensor(float defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions) {
        return floatTensor(ids,initializedConcurrentFloatTensor(defaultValue,concurrencyLevel,dimensions));
    }

    <I> IdDoubleTensor<I> doubleTensor(List<List<I>> ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdDoubleD0TensorShell<I>((DoubleD0Tensor) tensor);
            case 1 : return new IdDoubleD1TensorShell<I>((DoubleD1Tensor) tensor,ids);
            case 2 : return new IdDoubleD2TensorShell<I>((DoubleD2Tensor) tensor,ids);
            case 3 : return new IdDoubleD3TensorShell<I>((DoubleD3Tensor) tensor,ids);
            case 4 : return new IdDoubleD4TensorShell<I>((DoubleD4Tensor) tensor,ids);
            case 5 : return new IdDoubleD5TensorShell<I>((DoubleD5Tensor) tensor,ids);
            case 6 : return new IdDoubleD6TensorShell<I>((DoubleD6Tensor) tensor,ids);
            case 7 : return new IdDoubleD7TensorShell<I>((DoubleD7Tensor) tensor,ids);
            case 8 : return new IdDoubleD8TensorShell<I>((DoubleD8Tensor) tensor,ids);
            case 9 : return new IdDoubleD9TensorShell<I>((DoubleD9Tensor) tensor,ids);
            default : return new IdDoubleTensorShell<I>((DoubleTensor) tensor,ids);
        }
    }

    <I> IdDoubleTensor<I> doubleTensor(I[][] ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdDoubleD0TensorShell<I>((DoubleD0Tensor) tensor);
            case 1 : return new IdDoubleD1TensorShell<I>((DoubleD1Tensor) tensor,ids);
            case 2 : return new IdDoubleD2TensorShell<I>((DoubleD2Tensor) tensor,ids);
            case 3 : return new IdDoubleD3TensorShell<I>((DoubleD3Tensor) tensor,ids);
            case 4 : return new IdDoubleD4TensorShell<I>((DoubleD4Tensor) tensor,ids);
            case 5 : return new IdDoubleD5TensorShell<I>((DoubleD5Tensor) tensor,ids);
            case 6 : return new IdDoubleD6TensorShell<I>((DoubleD6Tensor) tensor,ids);
            case 7 : return new IdDoubleD7TensorShell<I>((DoubleD7Tensor) tensor,ids);
            case 8 : return new IdDoubleD8TensorShell<I>((DoubleD8Tensor) tensor,ids);
            case 9 : return new IdDoubleD9TensorShell<I>((DoubleD9Tensor) tensor,ids);
            default : return new IdDoubleTensorShell<I>((DoubleTensor) tensor,ids);
        }
    }

    public <I> IdDoubleTensor<I> doubleTensor(List<List<I>> ids, int ... dimensions) {
        return doubleTensor(ids,doubleTensor(dimensions));
    }

    public <I> IdDoubleTensor<I> initializedDoubleTensor(double defaultValue, List<List<I>> ids, int ... dimensions) {
        return doubleTensor(ids,initializedDoubleTensor(defaultValue,dimensions));
    }

    public <I> IdDoubleTensor<I> doubleTensor(I[][] ids, int ... dimensions) {
        return doubleTensor(ids,doubleTensor(dimensions));
    }

    public <I> IdDoubleTensor<I> initializedDoubleTensor(double defaultValue, I[][] ids, int ... dimensions) {
        return doubleTensor(ids,initializedDoubleTensor(defaultValue,dimensions));
    }

    DoubleTensor concurrentDoubleTensor(int concurrencyLevel, Tensor tensor, int[] dimensions) {
        switch (dimensions.length) {
            case 0 : return new ConcurrentDoubleD0TensorShell((DoubleD0Tensor) tensor,ConcurrentTensorLocksFactory.getD0Locks());
            case 1 : return new ConcurrentDoubleD1TensorShell((DoubleD1Tensor) tensor,ConcurrentTensorLocksFactory.getD1Locks(dimensions,concurrencyLevel));
            case 2 : return new ConcurrentDoubleD2TensorShell((DoubleD2Tensor) tensor,ConcurrentTensorLocksFactory.getD2Locks(dimensions,concurrencyLevel));
            case 3 : return new ConcurrentDoubleD3TensorShell((DoubleD3Tensor) tensor,ConcurrentTensorLocksFactory.getD3Locks(dimensions,concurrencyLevel));
            case 4 : return new ConcurrentDoubleD4TensorShell((DoubleD4Tensor) tensor,ConcurrentTensorLocksFactory.getD4Locks(dimensions,concurrencyLevel));
            case 5 : return new ConcurrentDoubleD5TensorShell((DoubleD5Tensor) tensor,ConcurrentTensorLocksFactory.getD5Locks(dimensions,concurrencyLevel));
            case 6 : return new ConcurrentDoubleD6TensorShell((DoubleD6Tensor) tensor,ConcurrentTensorLocksFactory.getD6Locks(dimensions,concurrencyLevel));
            case 7 : return new ConcurrentDoubleD7TensorShell((DoubleD7Tensor) tensor,ConcurrentTensorLocksFactory.getD7Locks(dimensions,concurrencyLevel));
            case 8 : return new ConcurrentDoubleD8TensorShell((DoubleD8Tensor) tensor,ConcurrentTensorLocksFactory.getD8Locks(dimensions,concurrencyLevel));
            case 9 : return new ConcurrentDoubleD9TensorShell((DoubleD9Tensor) tensor,ConcurrentTensorLocksFactory.getD9Locks(dimensions,concurrencyLevel));
            default : return new ConcurrentDoubleTensorShell(doubleTensor(dimensions),ConcurrentTensorLocksFactory.getLocks(dimensions,concurrencyLevel));
        }
    }

    public DoubleTensor concurrentDoubleTensor(int concurrencyLevel, int ... dimensions) {
        return concurrentDoubleTensor(concurrencyLevel,doubleTensor(dimensions),dimensions);
    }

    public DoubleTensor initializedConcurrentDoubleTensor(double defaultValue, int concurrencyLevel, int ... dimensions) {
        return concurrentDoubleTensor(concurrencyLevel,initializedDoubleTensor(defaultValue,dimensions),dimensions);
    }

    public <I> IdDoubleTensor<I> concurrentDoubleTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return doubleTensor(ids,concurrentDoubleTensor(concurrencyLevel,dimensions));
    }

    public <I> IdDoubleTensor<I> initializedConcurrentDoubleTensor(double defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return doubleTensor(ids,initializedConcurrentDoubleTensor(defaultValue,concurrencyLevel,dimensions));
    }

    public <I> IdDoubleTensor<I> concurrentDoubleTensor(I[][] ids, int concurrencyLevel, int ... dimensions) {
        return doubleTensor(ids,concurrentDoubleTensor(concurrencyLevel,dimensions));
    }

    public <I> IdDoubleTensor<I> initializedConcurrentDoubleTensor(double defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions) {
        return doubleTensor(ids,initializedConcurrentDoubleTensor(defaultValue,concurrencyLevel,dimensions));
    }

    <I> IdBooleanTensor<I> booleanTensor(List<List<I>> ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdBooleanD0TensorShell<I>((BooleanD0Tensor) tensor);
            case 1 : return new IdBooleanD1TensorShell<I>((BooleanD1Tensor) tensor,ids);
            case 2 : return new IdBooleanD2TensorShell<I>((BooleanD2Tensor) tensor,ids);
            case 3 : return new IdBooleanD3TensorShell<I>((BooleanD3Tensor) tensor,ids);
            case 4 : return new IdBooleanD4TensorShell<I>((BooleanD4Tensor) tensor,ids);
            case 5 : return new IdBooleanD5TensorShell<I>((BooleanD5Tensor) tensor,ids);
            case 6 : return new IdBooleanD6TensorShell<I>((BooleanD6Tensor) tensor,ids);
            case 7 : return new IdBooleanD7TensorShell<I>((BooleanD7Tensor) tensor,ids);
            case 8 : return new IdBooleanD8TensorShell<I>((BooleanD8Tensor) tensor,ids);
            case 9 : return new IdBooleanD9TensorShell<I>((BooleanD9Tensor) tensor,ids);
            default : return new IdBooleanTensorShell<I>((BooleanTensor) tensor,ids);
        }
    }

    <I> IdBooleanTensor<I> booleanTensor(I[][] ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdBooleanD0TensorShell<I>((BooleanD0Tensor) tensor);
            case 1 : return new IdBooleanD1TensorShell<I>((BooleanD1Tensor) tensor,ids);
            case 2 : return new IdBooleanD2TensorShell<I>((BooleanD2Tensor) tensor,ids);
            case 3 : return new IdBooleanD3TensorShell<I>((BooleanD3Tensor) tensor,ids);
            case 4 : return new IdBooleanD4TensorShell<I>((BooleanD4Tensor) tensor,ids);
            case 5 : return new IdBooleanD5TensorShell<I>((BooleanD5Tensor) tensor,ids);
            case 6 : return new IdBooleanD6TensorShell<I>((BooleanD6Tensor) tensor,ids);
            case 7 : return new IdBooleanD7TensorShell<I>((BooleanD7Tensor) tensor,ids);
            case 8 : return new IdBooleanD8TensorShell<I>((BooleanD8Tensor) tensor,ids);
            case 9 : return new IdBooleanD9TensorShell<I>((BooleanD9Tensor) tensor,ids);
            default : return new IdBooleanTensorShell<I>((BooleanTensor) tensor,ids);
        }
    }

    public <I> IdBooleanTensor<I> booleanTensor(List<List<I>> ids, int ... dimensions) {
        return booleanTensor(ids,booleanTensor(dimensions));
    }

    public <I> IdBooleanTensor<I> initializedBooleanTensor(boolean defaultValue, List<List<I>> ids, int ... dimensions) {
        return booleanTensor(ids,initializedBooleanTensor(defaultValue,dimensions));
    }

    public <I> IdBooleanTensor<I> booleanTensor(I[][] ids, int ... dimensions) {
        return booleanTensor(ids,booleanTensor(dimensions));
    }

    public <I> IdBooleanTensor<I> initializedBooleanTensor(boolean defaultValue, I[][] ids, int ... dimensions) {
        return booleanTensor(ids,initializedBooleanTensor(defaultValue,dimensions));
    }

    BooleanTensor concurrentBooleanTensor(int concurrencyLevel, Tensor tensor, int[] dimensions) {
        switch (dimensions.length) {
            case 0 : return new ConcurrentBooleanD0TensorShell((BooleanD0Tensor) tensor,ConcurrentTensorLocksFactory.getD0Locks());
            case 1 : return new ConcurrentBooleanD1TensorShell((BooleanD1Tensor) tensor,ConcurrentTensorLocksFactory.getD1Locks(dimensions,concurrencyLevel));
            case 2 : return new ConcurrentBooleanD2TensorShell((BooleanD2Tensor) tensor,ConcurrentTensorLocksFactory.getD2Locks(dimensions,concurrencyLevel));
            case 3 : return new ConcurrentBooleanD3TensorShell((BooleanD3Tensor) tensor,ConcurrentTensorLocksFactory.getD3Locks(dimensions,concurrencyLevel));
            case 4 : return new ConcurrentBooleanD4TensorShell((BooleanD4Tensor) tensor,ConcurrentTensorLocksFactory.getD4Locks(dimensions,concurrencyLevel));
            case 5 : return new ConcurrentBooleanD5TensorShell((BooleanD5Tensor) tensor,ConcurrentTensorLocksFactory.getD5Locks(dimensions,concurrencyLevel));
            case 6 : return new ConcurrentBooleanD6TensorShell((BooleanD6Tensor) tensor,ConcurrentTensorLocksFactory.getD6Locks(dimensions,concurrencyLevel));
            case 7 : return new ConcurrentBooleanD7TensorShell((BooleanD7Tensor) tensor,ConcurrentTensorLocksFactory.getD7Locks(dimensions,concurrencyLevel));
            case 8 : return new ConcurrentBooleanD8TensorShell((BooleanD8Tensor) tensor,ConcurrentTensorLocksFactory.getD8Locks(dimensions,concurrencyLevel));
            case 9 : return new ConcurrentBooleanD9TensorShell((BooleanD9Tensor) tensor,ConcurrentTensorLocksFactory.getD9Locks(dimensions,concurrencyLevel));
            default : return new ConcurrentBooleanTensorShell(booleanTensor(dimensions),ConcurrentTensorLocksFactory.getLocks(dimensions,concurrencyLevel));
        }
    }

    public BooleanTensor concurrentBooleanTensor(int concurrencyLevel, int ... dimensions) {
        return concurrentBooleanTensor(concurrencyLevel,booleanTensor(dimensions),dimensions);
    }

    public BooleanTensor initializedConcurrentBooleanTensor(boolean defaultValue, int concurrencyLevel, int ... dimensions) {
        return concurrentBooleanTensor(concurrencyLevel,initializedBooleanTensor(defaultValue,dimensions),dimensions);
    }

    public <I> IdBooleanTensor<I> concurrentBooleanTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return booleanTensor(ids,concurrentBooleanTensor(concurrencyLevel,dimensions));
    }

    public <I> IdBooleanTensor<I> initializedConcurrentBooleanTensor(boolean defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return booleanTensor(ids,initializedConcurrentBooleanTensor(defaultValue,concurrencyLevel,dimensions));
    }

    public <I> IdBooleanTensor<I> concurrentBooleanTensor(I[][] ids, int concurrencyLevel, int ... dimensions) {
        return booleanTensor(ids,concurrentBooleanTensor(concurrencyLevel,dimensions));
    }

    public <I> IdBooleanTensor<I> initializedConcurrentBooleanTensor(boolean defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions) {
        return booleanTensor(ids,initializedConcurrentBooleanTensor(defaultValue,concurrencyLevel,dimensions));
    }

    <I> IdCharTensor<I> charTensor(List<List<I>> ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdCharD0TensorShell<I>((CharD0Tensor) tensor);
            case 1 : return new IdCharD1TensorShell<I>((CharD1Tensor) tensor,ids);
            case 2 : return new IdCharD2TensorShell<I>((CharD2Tensor) tensor,ids);
            case 3 : return new IdCharD3TensorShell<I>((CharD3Tensor) tensor,ids);
            case 4 : return new IdCharD4TensorShell<I>((CharD4Tensor) tensor,ids);
            case 5 : return new IdCharD5TensorShell<I>((CharD5Tensor) tensor,ids);
            case 6 : return new IdCharD6TensorShell<I>((CharD6Tensor) tensor,ids);
            case 7 : return new IdCharD7TensorShell<I>((CharD7Tensor) tensor,ids);
            case 8 : return new IdCharD8TensorShell<I>((CharD8Tensor) tensor,ids);
            case 9 : return new IdCharD9TensorShell<I>((CharD9Tensor) tensor,ids);
            default : return new IdCharTensorShell<I>((CharTensor) tensor,ids);
        }
    }

    <I> IdCharTensor<I> charTensor(I[][] ids, Tensor tensor) {
        switch (tensor.size()) {
            case 0 : return new IdCharD0TensorShell<I>((CharD0Tensor) tensor);
            case 1 : return new IdCharD1TensorShell<I>((CharD1Tensor) tensor,ids);
            case 2 : return new IdCharD2TensorShell<I>((CharD2Tensor) tensor,ids);
            case 3 : return new IdCharD3TensorShell<I>((CharD3Tensor) tensor,ids);
            case 4 : return new IdCharD4TensorShell<I>((CharD4Tensor) tensor,ids);
            case 5 : return new IdCharD5TensorShell<I>((CharD5Tensor) tensor,ids);
            case 6 : return new IdCharD6TensorShell<I>((CharD6Tensor) tensor,ids);
            case 7 : return new IdCharD7TensorShell<I>((CharD7Tensor) tensor,ids);
            case 8 : return new IdCharD8TensorShell<I>((CharD8Tensor) tensor,ids);
            case 9 : return new IdCharD9TensorShell<I>((CharD9Tensor) tensor,ids);
            default : return new IdCharTensorShell<I>((CharTensor) tensor,ids);
        }
    }

    public <I> IdCharTensor<I> charTensor(List<List<I>> ids, int ... dimensions) {
        return charTensor(ids,charTensor(dimensions));
    }

    public <I> IdCharTensor<I> initializedCharTensor(char defaultValue, List<List<I>> ids, int ... dimensions) {
        return charTensor(ids,initializedCharTensor(defaultValue,dimensions));
    }

    public <I> IdCharTensor<I> charTensor(I[][] ids, int ... dimensions) {
        return charTensor(ids,charTensor(dimensions));
    }

    public <I> IdCharTensor<I> initializedCharTensor(char defaultValue, I[][] ids, int ... dimensions) {
        return charTensor(ids,initializedCharTensor(defaultValue,dimensions));
    }

    CharTensor concurrentCharTensor(int concurrencyLevel, Tensor tensor, int[] dimensions) {
        switch (dimensions.length) {
            case 0 : return new ConcurrentCharD0TensorShell((CharD0Tensor) tensor,ConcurrentTensorLocksFactory.getD0Locks());
            case 1 : return new ConcurrentCharD1TensorShell((CharD1Tensor) tensor,ConcurrentTensorLocksFactory.getD1Locks(dimensions,concurrencyLevel));
            case 2 : return new ConcurrentCharD2TensorShell((CharD2Tensor) tensor,ConcurrentTensorLocksFactory.getD2Locks(dimensions,concurrencyLevel));
            case 3 : return new ConcurrentCharD3TensorShell((CharD3Tensor) tensor,ConcurrentTensorLocksFactory.getD3Locks(dimensions,concurrencyLevel));
            case 4 : return new ConcurrentCharD4TensorShell((CharD4Tensor) tensor,ConcurrentTensorLocksFactory.getD4Locks(dimensions,concurrencyLevel));
            case 5 : return new ConcurrentCharD5TensorShell((CharD5Tensor) tensor,ConcurrentTensorLocksFactory.getD5Locks(dimensions,concurrencyLevel));
            case 6 : return new ConcurrentCharD6TensorShell((CharD6Tensor) tensor,ConcurrentTensorLocksFactory.getD6Locks(dimensions,concurrencyLevel));
            case 7 : return new ConcurrentCharD7TensorShell((CharD7Tensor) tensor,ConcurrentTensorLocksFactory.getD7Locks(dimensions,concurrencyLevel));
            case 8 : return new ConcurrentCharD8TensorShell((CharD8Tensor) tensor,ConcurrentTensorLocksFactory.getD8Locks(dimensions,concurrencyLevel));
            case 9 : return new ConcurrentCharD9TensorShell((CharD9Tensor) tensor,ConcurrentTensorLocksFactory.getD9Locks(dimensions,concurrencyLevel));
            default : return new ConcurrentCharTensorShell(charTensor(dimensions),ConcurrentTensorLocksFactory.getLocks(dimensions,concurrencyLevel));
        }
    }

    public CharTensor concurrentCharTensor(int concurrencyLevel, int ... dimensions) {
        return concurrentCharTensor(concurrencyLevel,charTensor(dimensions),dimensions);
    }

    public CharTensor initializedConcurrentCharTensor(char defaultValue, int concurrencyLevel, int ... dimensions) {
        return concurrentCharTensor(concurrencyLevel,initializedCharTensor(defaultValue,dimensions),dimensions);
    }

    public <I> IdCharTensor<I> concurrentCharTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return charTensor(ids,concurrentCharTensor(concurrencyLevel,dimensions));
    }

    public <I> IdCharTensor<I> initializedConcurrentCharTensor(char defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return charTensor(ids,initializedConcurrentCharTensor(defaultValue,concurrencyLevel,dimensions));
    }

    public <I> IdCharTensor<I> concurrentCharTensor(I[][] ids, int concurrencyLevel, int ... dimensions) {
        return charTensor(ids,concurrentCharTensor(concurrencyLevel,dimensions));
    }

    public <I> IdCharTensor<I> initializedConcurrentCharTensor(char defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions) {
        return charTensor(ids,initializedConcurrentCharTensor(defaultValue,concurrencyLevel,dimensions));
    }

    <T,I> IdTensor<T,I> tensor(List<List<I>> ids, Tensor<T> tensor) {
        switch (tensor.size()) {
            case 0 : return new IdD0TensorShell<T,I>((D0Tensor<T>) tensor);
            case 1 : return new IdD1TensorShell<T,I>((D1Tensor<T>) tensor,ids);
            case 2 : return new IdD2TensorShell<T,I>((D2Tensor<T>) tensor,ids);
            case 3 : return new IdD3TensorShell<T,I>((D3Tensor<T>) tensor,ids);
            case 4 : return new IdD4TensorShell<T,I>((D4Tensor<T>) tensor,ids);
            case 5 : return new IdD5TensorShell<T,I>((D5Tensor<T>) tensor,ids);
            case 6 : return new IdD6TensorShell<T,I>((D6Tensor<T>) tensor,ids);
            case 7 : return new IdD7TensorShell<T,I>((D7Tensor<T>) tensor,ids);
            case 8 : return new IdD8TensorShell<T,I>((D8Tensor<T>) tensor,ids);
            case 9 : return new IdD9TensorShell<T,I>((D9Tensor<T>) tensor,ids);
            default : return new IdTensorShell<T,I>(tensor,ids);
        }
    }

    <T,I> IdTensor<T,I> tensor(I[][] ids, Tensor<T> tensor) {
        switch (tensor.size()) {
            case 0 : return new IdD0TensorShell<T,I>((D0Tensor<T>) tensor);
            case 1 : return new IdD1TensorShell<T,I>((D1Tensor<T>) tensor,ids);
            case 2 : return new IdD2TensorShell<T,I>((D2Tensor<T>) tensor,ids);
            case 3 : return new IdD3TensorShell<T,I>((D3Tensor<T>) tensor,ids);
            case 4 : return new IdD4TensorShell<T,I>((D4Tensor<T>) tensor,ids);
            case 5 : return new IdD5TensorShell<T,I>((D5Tensor<T>) tensor,ids);
            case 6 : return new IdD6TensorShell<T,I>((D6Tensor<T>) tensor,ids);
            case 7 : return new IdD7TensorShell<T,I>((D7Tensor<T>) tensor,ids);
            case 8 : return new IdD8TensorShell<T,I>((D8Tensor<T>) tensor,ids);
            case 9 : return new IdD9TensorShell<T,I>((D9Tensor<T>) tensor,ids);
            default : return new IdTensorShell<T,I>(tensor,ids);
        }
    }

    @SuppressWarnings("unchecked") //is internally valid, so ok
    public <T,I> IdTensor<T,I> tensor(List<List<I>> ids, int ... dimensions) {
        return tensor(ids,(Tensor<T>) tensor(dimensions));
    }

    public <T,I> IdTensor<T,I> initializedTensor(T defaultValue, List<List<I>> ids, int ... dimensions) {
        return tensor(ids,initializedTensor(defaultValue, dimensions));
    }

    @SuppressWarnings("unchecked") //is internally valid, so ok
    public <T,I> IdTensor<T,I> tensor(I[][] ids, int ... dimensions) {
        return tensor(ids,(Tensor<T>) tensor(dimensions));
    }

    public <T,I> IdTensor<T,I> initializedTensor(T defaultValue, I[][] ids, int ... dimensions) {
        return tensor(ids,initializedTensor(defaultValue,dimensions));
    }

    @SuppressWarnings("unchecked") //is internally valid, so ok
    <T> Tensor<T> concurrentTensor(int concurrencyLevel, Tensor<T> tensor, int[] dimensions) {
        switch (dimensions.length) {
            case 0 : return new ConcurrentD0TensorShell<T>((D0Tensor<T>) tensor,ConcurrentTensorLocksFactory.getD0Locks());
            case 1 : return new ConcurrentD1TensorShell<T>((D1Tensor<T>) tensor,ConcurrentTensorLocksFactory.getD1Locks(dimensions,concurrencyLevel));
            case 2 : return new ConcurrentD2TensorShell<T>((D2Tensor<T>) tensor,ConcurrentTensorLocksFactory.getD2Locks(dimensions,concurrencyLevel));
            case 3 : return new ConcurrentD3TensorShell<T>((D3Tensor<T>) tensor,ConcurrentTensorLocksFactory.getD3Locks(dimensions,concurrencyLevel));
            case 4 : return new ConcurrentD4TensorShell<T>((D4Tensor<T>) tensor,ConcurrentTensorLocksFactory.getD4Locks(dimensions,concurrencyLevel));
            case 5 : return new ConcurrentD5TensorShell<T>((D5Tensor<T>) tensor,ConcurrentTensorLocksFactory.getD5Locks(dimensions,concurrencyLevel));
            case 6 : return new ConcurrentD6TensorShell<T>((D6Tensor<T>) tensor,ConcurrentTensorLocksFactory.getD6Locks(dimensions,concurrencyLevel));
            case 7 : return new ConcurrentD7TensorShell<T>((D7Tensor<T>) tensor,ConcurrentTensorLocksFactory.getD7Locks(dimensions,concurrencyLevel));
            case 8 : return new ConcurrentD8TensorShell<T>((D8Tensor<T>) tensor,ConcurrentTensorLocksFactory.getD8Locks(dimensions,concurrencyLevel));
            case 9 : return new ConcurrentD9TensorShell<T>((D9Tensor<T>) tensor,ConcurrentTensorLocksFactory.getD9Locks(dimensions,concurrencyLevel));
            default : return new ConcurrentTensorShell<T>((Tensor<T>) tensor(dimensions),ConcurrentTensorLocksFactory.getLocks(dimensions,concurrencyLevel));
        }
    }

    @SuppressWarnings("unchecked") //is internally valid, so ok
    public <T> Tensor<T> concurrentTensor(int concurrencyLevel, int ... dimensions) {
        return concurrentTensor(concurrencyLevel,(Tensor<T>) tensor(dimensions),dimensions);
    }

    public <T> Tensor<T> initializedConcurrentTensor(T defaultValue, int concurrencyLevel, int ... dimensions) {
        return concurrentTensor(concurrencyLevel,initializedTensor(defaultValue,dimensions),dimensions);
    }

    @SuppressWarnings("unchecked") //is internally valid, so ok
    public <T,I> IdTensor<T,I> concurrentTensor(List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return tensor(ids,(Tensor<T>) concurrentTensor(concurrencyLevel,dimensions));
    }

    public <T,I> IdTensor<T,I> initializedConcurrentTensor(T defaultValue, List<List<I>> ids, int concurrencyLevel, int ... dimensions) {
        return tensor(ids,initializedConcurrentTensor(defaultValue,concurrencyLevel,dimensions));
    }

    @SuppressWarnings("unchecked") //is internally valid, so ok
    public <T,I> IdTensor<T,I> concurrentTensor(I[][] ids, int concurrencyLevel, int ... dimensions) {
        return tensor(ids,(Tensor<T>) concurrentTensor(concurrencyLevel,dimensions));
    }

    public <T,I> IdTensor<T,I> initializedConcurrentTensor(T defaultValue, I[][] ids, int concurrencyLevel, int ... dimensions) {
        return tensor(ids,initializedConcurrentTensor(defaultValue,concurrencyLevel,dimensions));
    }

    @SuppressWarnings("unchecked") //input tensor already paramaterized by <T,I>, so output will be valid
    public <T> Tensor<T> copyTensor(Tensor<T> tensor) {
        JavaType type = tensor.getType();
        int[] dimensions = tensor.getDimensions();
        Tensor newTensor; //erasure means non-typing this ok
        switch (type) {
            case BOOLEAN : newTensor = booleanTensor(dimensions); break;
            case BYTE : newTensor = byteTensor(dimensions); break;
            case CHAR : newTensor = charTensor(dimensions); break;
            case DOUBLE : newTensor = doubleTensor(dimensions); break;
            case FLOAT : newTensor = floatTensor(dimensions); break;
            case INT : newTensor = intTensor(dimensions); break;
            case LONG : newTensor = longTensor(dimensions); break;
            case SHORT : newTensor = shortTensor(dimensions); break;
            case OBJECT : newTensor = tensor(dimensions); break;
            default : newTensor = tensor(dimensions);
        }
        newTensor.setTensorValues(tensor);
        return newTensor;
    }

    @SuppressWarnings("unchecked") //input tensor already paramaterized by <T,I>, so output will be valid
    public <T,I> IdTensor<T,I> copyTensor(IdTensor<T,I> tensor) {
        JavaType type = tensor.getType();
        int[] dimensions = tensor.getDimensions();
        List<List<I>> ids = tensor.getIndex().getIndexIds();
        IdTensor newTensor; //erasure means non-typing this ok
        switch (type) {
            case BOOLEAN : newTensor = booleanTensor(ids,dimensions); break;
            case BYTE : newTensor = byteTensor(ids,dimensions); break;
            case CHAR : newTensor = charTensor(ids,dimensions); break;
            case DOUBLE : newTensor = doubleTensor(ids,dimensions); break;
            case FLOAT : newTensor = floatTensor(ids,dimensions); break;
            case INT : newTensor = intTensor(ids,dimensions); break;
            case LONG : newTensor = longTensor(ids,dimensions); break;
            case SHORT : newTensor = shortTensor(ids,dimensions); break;
            case OBJECT : newTensor = tensor(ids,dimensions); break;
            default : newTensor = tensor(ids,dimensions);
        }
        newTensor.setTensorValues(tensor);
        return newTensor;
    }

    @SuppressWarnings("unchecked") //input tensor already paramaterized by <T,I>, so output will be valid
    public <T> Tensor<T> copyTensor(Tensor<T> tensor, int concurrencyLevel) {
        JavaType type = tensor.getType();
        int[] dimensions = tensor.getDimensions();
        Tensor newTensor; //erasure means non-typing this ok
        switch (type) {
            case BOOLEAN : newTensor = concurrentBooleanTensor(concurrencyLevel,dimensions); break;
            case BYTE : newTensor = concurrentByteTensor(concurrencyLevel,dimensions); break;
            case CHAR : newTensor = concurrentCharTensor(concurrencyLevel,dimensions); break;
            case DOUBLE : newTensor = concurrentDoubleTensor(concurrencyLevel,dimensions); break;
            case FLOAT : newTensor = concurrentFloatTensor(concurrencyLevel,dimensions); break;
            case INT : newTensor = concurrentIntTensor(concurrencyLevel,dimensions); break;
            case LONG : newTensor = concurrentLongTensor(concurrencyLevel,dimensions); break;
            case SHORT : newTensor = concurrentShortTensor(concurrencyLevel,dimensions); break;
            case OBJECT : newTensor = concurrentTensor(concurrencyLevel,dimensions); break;
            default : newTensor = concurrentTensor(concurrencyLevel,dimensions);
        }
        newTensor.setTensorValues(tensor);
        return newTensor;
    }

    @SuppressWarnings("unchecked") //input tensor already paramaterized by <T,I>, so output will be valid
    public <T,I> IdTensor<T,I> copyTensor(IdTensor<T,I> tensor, int concurrencyLevel) {
        JavaType type = tensor.getType();
        int[] dimensions = tensor.getDimensions();
        List<List<I>> ids = tensor.getIndex().getIndexIds();
        IdTensor newTensor; //erasure means non-typing this ok
        switch (type) {
            case BOOLEAN : newTensor = concurrentBooleanTensor(ids,concurrencyLevel,dimensions); break;
            case BYTE : newTensor = concurrentByteTensor(ids,concurrencyLevel,dimensions); break;
            case CHAR : newTensor = concurrentCharTensor(ids,concurrencyLevel,dimensions); break;
            case DOUBLE : newTensor = concurrentDoubleTensor(ids,concurrencyLevel,dimensions); break;
            case FLOAT : newTensor = concurrentFloatTensor(ids,concurrencyLevel,dimensions); break;
            case INT : newTensor = concurrentIntTensor(ids,concurrencyLevel,dimensions); break;
            case LONG : newTensor = concurrentLongTensor(ids,concurrencyLevel,dimensions); break;
            case SHORT : newTensor = concurrentShortTensor(ids,concurrencyLevel,dimensions); break;
            case OBJECT : newTensor = concurrentTensor(ids,concurrencyLevel,dimensions); break;
            default : newTensor = concurrentTensor(ids,concurrencyLevel,dimensions);
        }
        newTensor.setTensorValues(tensor);
        return newTensor;
    }

}
