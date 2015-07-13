package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.MirrorIndex;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import static com.pb.sawdust.util.Range.range;
import static org.junit.Assert.*;

import java.util.*;

/**
 * The {@code AbstractElementByElementTensorCalculationTest} ...
 *
 * @author crf <br/>
 *         Started Nov 24, 2010 7:53:22 AM
 */
public abstract class AbstractCellWiseTensorCalculationTest extends CellWiseTensorCalculationTest {
    protected AbstractCellWiseTensorCalculation abstractEbe;

    protected abstract AbstractCellWiseTensorCalculation getCellWiseTensorCalculation(TensorFactory factory);

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Class<? extends TestBase>> additionalTestClasses = new LinkedList<Class<? extends TestBase>>();
        additionalTestClasses.add(getCellWiseTensorMutatingCalculationTestClass());
        additionalTestClasses.addAll(super.getAdditionalTestClasses());
        return additionalTestClasses;
    }

    protected static Class<? extends TestBase> getCellWiseTensorMutatingCalculationTestClass() {
        return AbstractCellWiseTensorMutatingCalculationTest.class;
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        abstractEbe = (AbstractCellWiseTensorCalculation) ebe;
    }

    public TensorFactory getFactory() {
        return ArrayTensor.getFactory();
    }

    public static class AbstractCellWiseTensorMutatingCalculationTest extends CellWiseTensorMutatingCalculationTest {
        protected AbstractCellWiseTensorCalculationTest mmt;

        @Before
        public void beforeTest() {
            mmt = (AbstractCellWiseTensorCalculationTest) getCallingContextInstance();
            super.beforeTest();
        }

        @Override
        protected CellWiseTensorMutatingCalculation getCellWiseTensorMutatingCalculation(TensorFactory factory) {
            return mmt.getCellWiseTensorCalculation(factory);
        }

        @Override
        protected TensorFactory getFactory() {
            return mmt.getFactory();
        }
    }

    @Test
    public void testAbstractByteUnary() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        ByteTensor result = factory.byteTensor(t.getDimensions());
        ByteTensor check = factory.byteTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                return (byte) (-1*t.getCell(indices));
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t,result,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testAbstractByteUnaryCorrectReturn() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        ByteTensor result = factory.byteTensor(t.getDimensions());
        assertTrue(result == abstractEbe.calculate(t,result,NumericFunctions.NEGATE));
    }

    @Test
    public void testAbstractByteByteBinaryTensorMatch() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,t1.getDimensions());
        ByteTensor result = factory.byteTensor(t1.getDimensions());
        ByteTensor check = factory.byteTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                return (byte) (t1.getCell(indices) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testAbstractByteByteBinaryTensorMatchCorrectReturn() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,t1.getDimensions());
        ByteTensor result = factory.byteTensor(t1.getDimensions());
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT));
    }

    @Test
    public void testAbstractByteByteBinaryTensorFirstSmaller() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ByteTensor check = factory.byteTensor(t1.getDimensions());
        ByteTensor result = factory.byteTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (byte) (t1.getCell(indices) - t2.getCell(subDims));
            }
        });
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims),check);
    }

    @Test
    public void testAbstractByteByteBinaryTensorFirstSmallerCorrectReturn() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ByteTensor result = factory.byteTensor(t1.getDimensions());
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims));
    }

    @Test
    public void testAbstractCalculateFunctionNByte() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE : break;
                case SHORT :
                case INT :
                case LONG :
                case FLOAT :
                case DOUBLE : t = TensorUtil.asByteTensor(t);
            }
            params.add(t);
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        ByteTensor check = factory.byteTensor(dims);
        ByteTensor result = factory.byteTensor(dims);
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                byte value1 = (byte) (getFunctionNValue(params.get(0),dimMatches[0],indices,JavaType.BYTE).byteValue() + getFunctionNValue(params.get(1),dimMatches[1],indices,JavaType.BYTE).byteValue());
                byte value2 = getFunctionNValue(params.get(2),dimMatches[2],indices,JavaType.BYTE).byteValue();
                for (int i : range(3,params.size()))
                    value2 = (byte) (value2 + getFunctionNValue(params.get(i),dimMatches[i],indices,JavaType.BYTE).byteValue());
                return (byte) (value1 - value2);
            }
        });
        ByteTensor[] pms = new ByteTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            ByteTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedByteTensor(t.getValue().byteValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asByteTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asByteTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        util.assertTensorEquals(check,abstractEbe.calculate(function,pms,result));
    }

    @Test
    public void testAbstractCalculateFunctionNByteCorrectReturn() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE : break;
                case SHORT :
                case INT :
                case LONG :
                case FLOAT :
                case DOUBLE : t = TensorUtil.asByteTensor(t);
            }
            params.add(t);
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        ByteTensor result = factory.byteTensor(dims);
        ByteTensor[] pms = new ByteTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            ByteTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedByteTensor(t.getValue().byteValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asByteTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asByteTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        assertTrue(result == abstractEbe.calculate(function,pms,result));
    }




    @Test
    public void testAbstractShortUnary() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        ShortTensor result = factory.shortTensor(t.getDimensions());
        ShortTensor check = factory.shortTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                return (short) (-1*t.getCell(indices));
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t,result,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testAbstractShortUnaryCorrectReturn() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        ShortTensor result = factory.shortTensor(t.getDimensions());
        assertTrue(result == abstractEbe.calculate(t,result,NumericFunctions.NEGATE));
    }

    @Test
    public void testAbstractShortShortBinaryTensorMatch() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,t1.getDimensions());
        ShortTensor result = factory.shortTensor(t1.getDimensions());
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                return (short) (t1.getCell(indices) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testAbstractShortShortBinaryTensorMatchCorrectReturn() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,t1.getDimensions());
        ShortTensor result = factory.shortTensor(t1.getDimensions());
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT));
    }

    @Test
    public void testAbstractShortShortBinaryTensorFirstSmaller() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        ShortTensor result = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(indices) - t2.getCell(subDims));
            }
        });
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims),check);
    }

    @Test
    public void testAbstractShortShortBinaryTensorFirstSmallerCorrectReturn() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ShortTensor result = factory.shortTensor(t1.getDimensions());
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims));
    }

    @Test
    public void testAbstractCalculateFunctionNShort() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE :
                case SHORT : break;
                case INT :
                case LONG :
                case FLOAT :
                case DOUBLE : t = TensorUtil.asShortTensor(t);
            }
            params.add(t);
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        ShortTensor check = factory.shortTensor(dims);
        ShortTensor result = factory.shortTensor(dims);
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                short value1 = (short) (getFunctionNValue(params.get(0),dimMatches[0],indices,JavaType.SHORT).shortValue() + getFunctionNValue(params.get(1),dimMatches[1],indices,JavaType.SHORT).shortValue());
                short value2 = getFunctionNValue(params.get(2),dimMatches[2],indices,JavaType.SHORT).shortValue();
                for (int i : range(3,params.size()))
                    value2 = (short) (value2 + getFunctionNValue(params.get(i),dimMatches[i],indices,JavaType.SHORT).shortValue());
                return (short) (value1 - value2);
            }
        });
        ShortTensor[] pms = new ShortTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            ShortTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedShortTensor(t.getValue().shortValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asShortTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asShortTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        util.assertTensorEquals(check,abstractEbe.calculate(function,pms,result));
    }

    @Test
    public void testAbstractCalculateFunctionNShortCorrectReturn() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE :
                case SHORT : break;
                case INT :
                case LONG :
                case FLOAT :
                case DOUBLE : t = TensorUtil.asShortTensor(t);
            }
            params.add(t);
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        ShortTensor result = factory.shortTensor(dims);
        ShortTensor[] pms = new ShortTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            ShortTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedShortTensor(t.getValue().shortValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asShortTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asShortTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        assertTrue(result == abstractEbe.calculate(function,pms,result));
    }

    @Test
    public void testAbstractIntUnary() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        IntTensor result = factory.intTensor(t.getDimensions());
        IntTensor check = factory.intTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return -1*t.getCell(indices);
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t,result,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testAbstractIntUnaryCorrectReturn() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        IntTensor result = factory.intTensor(t.getDimensions());
        assertTrue(result == abstractEbe.calculate(t,result,NumericFunctions.NEGATE));
    }

    @Test
    public void testAbstractIntIntBinaryTensorMatch() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,t1.getDimensions());
        IntTensor result = factory.intTensor(t1.getDimensions());
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testAbstractIntIntBinaryTensorMatchCorrectReturn() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,t1.getDimensions());
        IntTensor result = factory.intTensor(t1.getDimensions());
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT));
    }

    @Test
    public void testAbstractIntIntBinaryTensorFirstSmaller() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        IntTensor result = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims),check);
    }

    @Test
    public void testAbstractIntIntBinaryTensorFirstSmallerCorrectReturn() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor result = factory.intTensor(t1.getDimensions());
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims));
    }

    @Test
    public void testAbstractCalculateFunctionNInt() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE :
                case SHORT :
                case INT : break;
                case LONG :
                case FLOAT :
                case DOUBLE : t = TensorUtil.asIntTensor(t);
            }
            params.add(t);
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        IntTensor check = factory.intTensor(dims);
        IntTensor result = factory.intTensor(dims);
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int value1 = getFunctionNValue(params.get(0),dimMatches[0],indices,JavaType.INT).intValue() + getFunctionNValue(params.get(1),dimMatches[1],indices,JavaType.INT).intValue();
                int value2 = getFunctionNValue(params.get(2),dimMatches[2],indices,JavaType.INT).intValue();
                for (int i : range(3,params.size()))
                    value2 = value2 + getFunctionNValue(params.get(i),dimMatches[i],indices,JavaType.INT).intValue();
                return value1 - value2;
            }
        });
        IntTensor[] pms = new IntTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            IntTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedIntTensor(t.getValue().intValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asIntTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asIntTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        util.assertTensorEquals(check,abstractEbe.calculate(function,pms,result));
    }

    @Test
    public void testAbstractCalculateFunctionNIntCorrectReturn() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE :
                case SHORT :
                case INT : break;
                case LONG :
                case FLOAT :
                case DOUBLE : t = TensorUtil.asIntTensor(t);
            }
            params.add(t);
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        IntTensor result = factory.intTensor(dims);
        IntTensor[] pms = new IntTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            IntTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedIntTensor(t.getValue().intValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asIntTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asIntTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        assertTrue(result == abstractEbe.calculate(function,pms,result));
    }

    @Test
    public void testAbstractLongUnary() {
        final LongTensor t = (LongTensor) getRandomTensor(JavaType.LONG);
        LongTensor result = factory.longTensor(t.getDimensions());
        LongTensor check = factory.longTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return -1*t.getCell(indices);
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t,result,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testAbstractLongUnaryCorrectReturn() {
        final LongTensor t = (LongTensor) getRandomTensor(JavaType.LONG);
        LongTensor result = factory.longTensor(t.getDimensions());
        assertTrue(result == abstractEbe.calculate(t,result,NumericFunctions.NEGATE));
    }

    @Test
    public void testAbstractLongLongBinaryTensorMatch() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,t1.getDimensions());
        LongTensor result = factory.longTensor(t1.getDimensions());
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testAbstractLongLongBinaryTensorMatchCorrectReturn() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,t1.getDimensions());
        LongTensor result = factory.longTensor(t1.getDimensions());
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT));
    }

    @Test
    public void testAbstractLongLongBinaryTensorFirstSmaller() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        LongTensor result = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims),check);
    }

    @Test
    public void testAbstractLongLongBinaryTensorFirstSmallerCorrectReturn() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor result = factory.longTensor(t1.getDimensions());
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims));
    }

    @Test
    public void testAbstractCalculateFunctionNLong() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE :
                case SHORT :
                case INT :
                case LONG : break;
                case FLOAT :
                case DOUBLE : t = TensorUtil.asLongTensor(t);
            }
            params.add(t);
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        LongTensor check = factory.longTensor(dims);
        LongTensor result = factory.longTensor(dims);
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                long value1 = getFunctionNValue(params.get(0),dimMatches[0],indices,JavaType.LONG).longValue() + getFunctionNValue(params.get(1),dimMatches[1],indices,JavaType.LONG).longValue();
                long value2 = getFunctionNValue(params.get(2),dimMatches[2],indices,JavaType.LONG).longValue();
                for (int i : range(3,params.size()))
                    value2 = value2 + getFunctionNValue(params.get(i),dimMatches[i],indices,JavaType.LONG).longValue();
                return value1 - value2;
            }
        });
        LongTensor[] pms = new LongTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            LongTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedLongTensor(t.getValue().longValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asLongTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asLongTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        util.assertTensorEquals(check,abstractEbe.calculate(function,pms,result));
    }

    @Test
    public void testAbstractCalculateFunctionNLongCorrectReturn() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE :
                case SHORT :
                case INT :
                case LONG : break;
                case FLOAT :
                case DOUBLE : t = TensorUtil.asLongTensor(t);
            }
            params.add(t);
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        LongTensor result = factory.longTensor(dims);
        LongTensor[] pms = new LongTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            LongTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedLongTensor(t.getValue().longValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asLongTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asLongTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        assertTrue(result == abstractEbe.calculate(function,pms,result));
    }

    @Test
    public void testAbstractFloatUnary() {
        final FloatTensor t = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        FloatTensor result = factory.floatTensor(t.getDimensions());
        FloatTensor check = factory.floatTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return -1*t.getCell(indices);
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t,result,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testAbstractFloatUnaryCorrectReturn() {
        final FloatTensor t = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        FloatTensor result = factory.floatTensor(t.getDimensions());
        assertTrue(result == abstractEbe.calculate(t,result,NumericFunctions.NEGATE));
    }

    @Test
    public void testAbstractFloatFloatBinaryTensorMatch() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,t1.getDimensions());
        FloatTensor result = factory.floatTensor(t1.getDimensions());
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testAbstractFloatFloatBinaryTensorMatchCorrectReturn() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,t1.getDimensions());
        FloatTensor result = factory.floatTensor(t1.getDimensions());
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT));
    }

    @Test
    public void testAbstractFloatFloatBinaryTensorFirstSmaller() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        FloatTensor result = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims),check);
    }

    @Test
    public void testAbstractFloatFloatBinaryTensorFirstSmallerCorrectReturn() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor result = factory.floatTensor(t1.getDimensions());
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims));
    }

    @Test
    public void testAbstractCalculateFunctionNFloat() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE :
                case SHORT :
                case INT :
                case LONG :
                case FLOAT : break;
                case DOUBLE : t = TensorUtil.asFloatTensor(t);
            }
            params.add(t);
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        FloatTensor check = factory.floatTensor(dims);
        FloatTensor result = factory.floatTensor(dims);
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                float value1 = getFunctionNValue(params.get(0),dimMatches[0],indices,JavaType.FLOAT).floatValue() + getFunctionNValue(params.get(1),dimMatches[1],indices,JavaType.FLOAT).floatValue();
                float value2 = getFunctionNValue(params.get(2),dimMatches[2],indices,JavaType.FLOAT).floatValue();
                for (int i : range(3,params.size()))
                    value2 = value2 + getFunctionNValue(params.get(i),dimMatches[i],indices,JavaType.FLOAT).floatValue();
                return value1 - value2;
            }
        });
        FloatTensor[] pms = new FloatTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            FloatTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedFloatTensor(t.getValue().floatValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asFloatTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asFloatTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        util.assertTensorEquals(check,abstractEbe.calculate(function,pms,result));
    }

    @Test
    public void testAbstractCalculateFunctionNFloatCorrectReturn() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE :
                case SHORT :
                case INT :
                case LONG :
                case FLOAT : break;
                case DOUBLE : t = TensorUtil.asFloatTensor(t);
            }
            params.add(t);
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        FloatTensor result = factory.floatTensor(dims);
        FloatTensor[] pms = new FloatTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            FloatTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedFloatTensor(t.getValue().floatValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asFloatTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asFloatTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        assertTrue(result == abstractEbe.calculate(function,pms,result));
    }

    @Test
    public void testAbstractDoubleUnary() {
        final DoubleTensor t = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        DoubleTensor result = factory.doubleTensor(t.getDimensions());
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return -1*t.getCell(indices);
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t,result,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testAbstractDoubleUnaryCorrectReturn() {
        final DoubleTensor t = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        DoubleTensor result = factory.doubleTensor(t.getDimensions());
        assertTrue(result == abstractEbe.calculate(t,result,NumericFunctions.NEGATE));
    }

    @Test
    public void testAbstractDoubleDoubleBinaryTensorMatch() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,t1.getDimensions());
        DoubleTensor result = factory.doubleTensor(t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testAbstractDoubleDoubleBinaryTensorMatchCorrectReturn() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,t1.getDimensions());
        DoubleTensor result = factory.doubleTensor(t1.getDimensions());
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT));
    }

    @Test
    public void testAbstractDoubleDoubleBinaryTensorFirstSmaller() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        DoubleTensor result = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        util.assertTensorEquals(abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims),check);
    }

    @Test
    public void testAbstractDoubleDoubleBinaryTensorFirstSmallerCorrectReturn() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor result = factory.doubleTensor(t1.getDimensions());
        Set<Integer> freeDs = new HashSet<Integer>(Arrays.asList(ArrayUtil.toIntegerArray(range(t1.size()).getRangeArray())));
        for (int d : dimMatch)
            freeDs.remove(d);
        int[] freeDims = ArrayUtil.toPrimitive(freeDs.toArray(new Integer[freeDs.size()]));
        Arrays.sort(freeDims);
        assertTrue(result == abstractEbe.calculate(t1,t2,result,NumericFunctions.SUBTRACT,dimMatch,freeDims));
    }

    @Test
    public void testAbstractCalculateFunctionNDouble() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams)
            params.add(t);
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        DoubleTensor check = factory.doubleTensor(dims);
        DoubleTensor result = factory.doubleTensor(dims);
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                double value1 = getFunctionNValue(params.get(0),dimMatches[0],indices,JavaType.DOUBLE).doubleValue() + getFunctionNValue(params.get(1),dimMatches[1],indices,JavaType.DOUBLE).doubleValue();
                double value2 = getFunctionNValue(params.get(2),dimMatches[2],indices,JavaType.DOUBLE).doubleValue();
                for (int i : range(3,params.size()))
                    value2 = value2 + getFunctionNValue(params.get(i),dimMatches[i],indices,JavaType.DOUBLE).doubleValue();
                return value1 - value2;
            }
        });
        DoubleTensor[] pms = new DoubleTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            DoubleTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedDoubleTensor(t.getValue().doubleValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asDoubleTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asDoubleTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        util.assertTensorEquals(check,abstractEbe.calculate(function,pms,result));
    }

    @Test
    public void testAbstractCalculateFunctionNDoubleCorrectReturn() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Tensor<? extends Number> t : oParams)
            params.add(t);
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        DoubleTensor result = factory.doubleTensor(dims);
        DoubleTensor[] pms = new DoubleTensor[params.size()];
        int counter = 0;
        for (Tensor<? extends Number> t : params) {
            DoubleTensor nt;
            switch (t.size()) {
                case 0 : nt = abstractEbe.uniformFactory.initializedDoubleTensor(t.getValue().doubleValue(),dims); break;
                default : {
                    if (dims.length == t.size()) {
                        nt = TensorUtil.asDoubleTensor(t);
                    } else {
                        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
                        for (int i : range(dims.length)) {
                            boolean fixed = true;
                            for (int d : dimMatches[counter]) {
                                if (d == i) {
                                    fixed = false;
                                    break;
                                }
                            }
                            if (fixed)
                                fixedDimensions.put(i,dims[i]);
                        }
                        nt = TensorUtil.asDoubleTensor(t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions)));
                    }
                }
            }
            pms[counter++] = nt;
        }
        assertTrue(result == abstractEbe.calculate(function,pms,result));
    }
}
