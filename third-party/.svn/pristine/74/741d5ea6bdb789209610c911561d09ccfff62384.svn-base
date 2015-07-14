package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.test.TestBase;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * The {@code ElementByElementTensorCalculationTest} ...
 *
 * @author crf <br/>
 *         Started Nov 24, 2010 6:58:05 AM
 */
public abstract class CellWiseTensorCalculationTest extends TestBase {
    public static final String SECOND_TENSOR_MODE_KEY = "second tensor mode";

    protected static enum SecondTensorMode {
        SCALAR,
        FULL,
        RANDOM;
    }
    private SecondTensorMode mode;

    protected CellWiseTensorCalculation ebe;
    protected TensorFactory factory;
    protected TensorTestUtil util;
    
    abstract protected CellWiseTensorCalculation getCellWiseTensorCalculation(TensorFactory factory);
    
    protected TensorFactory getFactory() {
        return ArrayTensor.getFactory();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> contexts = new LinkedList<Map<String,Object>>();
        for (SecondTensorMode mode : SecondTensorMode.values())
            contexts.add(buildContext(CellWiseTensorCalculationTest.SECOND_TENSOR_MODE_KEY,mode));
        addClassRunContext(this.getClass(),contexts);
        return super.getAdditionalTestClasses();
    }
    
    @Before
    public void beforeTest() {
        factory = getFactory();
        ebe = getCellWiseTensorCalculation(factory);
        util = new TensorTestUtil(factory,random);
        mode = (SecondTensorMode) getTestData(SECOND_TENSOR_MODE_KEY);
    }

    protected SecondTensorMode getSecondTensorMode() {
        return mode;
    }
    
    protected Tensor<? extends Number> getRandomTensor(JavaType type, int minSize) {
        int randomSize = random.nextInt(minSize,6);
        int[] randomDims = new int[randomSize];
        for (int i : range(randomDims.length))
            randomDims[i] = random.nextInt(1,6);
        return getRandomTensor(type,randomDims);
    }

    protected Tensor<? extends Number> getRandomTensor(JavaType type) {
        return getRandomTensor(type,0);
    }
    
    protected Tensor<? extends Number> getRandomTensor(JavaType type, int[] dims) {
        return util.getRandomTensor(type,dims);
    }
    
    protected int[][] getMatchingDims(int[] dimensions, boolean auto, boolean first) {
        //returns {new dimensions, matching dimensions}
        int len = dimensions.length;
        int matchCount;
        switch (mode) {
            case SCALAR : matchCount = 0; break;
            case FULL : matchCount = len == 0 ? 0 : len+(auto || !first ? 0 : -1); break;
            case RANDOM : {
                switch (len) {
                    //try do do everything from 1 to length - 1; have to do a few specials for small tensors
                    case 0 : matchCount = 0; break;
                    case 1 :
                    case 2 : matchCount = 1; break;
                    default : matchCount = random.nextInt(1,len+(auto || !first ? 0 : -1)); //allow everything from vector up to exact match, except for non-auto first args
                } break;
            }
            default : throw new IllegalStateException("Should not be here");
        }
        int[] matchingDims = new int[matchCount];
        boolean[] dimMatched = new boolean[len];
        Arrays.fill(dimMatched,false);
        while(matchCount > 0) {
            int dim = random.nextInt(len);
            if (!dimMatched[dim]) {
                dimMatched[dim] = true;
                matchingDims[matchingDims.length-(matchCount--)] = dim;
            }
        }
        if (auto) {
            //first sort
            Arrays.sort(matchingDims);
            //now have to check about ambiguity, and shift down if there are
            int dim = 0;
            while (dim < matchingDims.length) {
                int d = matchingDims[dim] - 1;
                while (d >= 0) { //not first dimension
                    if (dimMatched[d]) //already matched
                        break;
                    if (dimensions[d] == dimensions[matchingDims[dim]]) { //previous dimension size == current one
                        dimMatched[matchingDims[dim]] = false;
                        matchingDims[dim] = d;
                        dimMatched[matchingDims[dim]] = true;
                    }
                    d--;
                }
                dim++;
            }
        }
        int[] newDims = new int[matchingDims.length];
        for (int i : range(newDims.length))
            newDims[i] = dimensions[matchingDims[i]];
        return new int[][] {newDims,matchingDims};
    }

    @Test
    public void testByteUnary() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        ByteTensor check = factory.byteTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                return (byte) (-1*t.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testByteBinaryByteFirst() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        ByteTensor check = factory.byteTensor(t.getDimensions());
        final byte val = random.nextByte();
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                return (byte) (val-t.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryByteSecond() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        ByteTensor check = factory.byteTensor(t.getDimensions());
        final byte val = random.nextByte();
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                return (byte) (t.getCell(indices)-val);
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryShortFirst() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        ShortTensor check = factory.shortTensor(t.getDimensions());
        final short val = random.nextShort();
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                return (short) (val-t.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryShortSecond() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        ShortTensor check = factory.shortTensor(t.getDimensions());
        final short val = random.nextShort();
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                return (short) (t.getCell(indices)-val);
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryIntFirst() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        IntTensor check = factory.intTensor(t.getDimensions());
        final int val = random.nextInt();
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryIntSecond() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        IntTensor check = factory.intTensor(t.getDimensions());
        final int val = random.nextInt();
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryLongFirst() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        LongTensor check = factory.longTensor(t.getDimensions());
        final long val = random.nextLong();
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryLongSecond() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        LongTensor check = factory.longTensor(t.getDimensions());
        final long val = random.nextLong();
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryFloatFirst() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        final float val = random.nextFloat();
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryFloatSecond() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        final float val = random.nextFloat();
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryDoubleFirst() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryDoubleSecond() {
        final ByteTensor t = (ByteTensor) getRandomTensor(JavaType.BYTE);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryByteTensorMatch() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,t1.getDimensions());
        ByteTensor check = factory.byteTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                return (byte) (t1.getCell(indices) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryShortTensorFirstMatch() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,t1.getDimensions());
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                return (short) (t1.getCell(indices) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryShortTensorSecondMatch() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,t1.getDimensions());
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                return (short) (t1.getCell(indices) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryIntTensorFirstMatch() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,t1.getDimensions());
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryIntTensorSecondMatch() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,t1.getDimensions());
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryLongTensorFirstMatch() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,t1.getDimensions());
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryLongTensorSecondMatch() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,t1.getDimensions());
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryFloatTensorFirstMatch() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,t1.getDimensions());
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryFloatTensorSecondMatch() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,t1.getDimensions());
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryDoubleTensorFirstMatch() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryDoubleTensorSecondMatch() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryByteTensorSmallerFirst() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ByteTensor check = factory.byteTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (byte) (t1.getCell(subDims) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryByteTensorSmallerSecond() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ByteTensor check = factory.byteTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (byte) (t1.getCell(indices) - t2.getCell(subDims));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryShortTensorFirstSmallerFirst() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(subDims) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryShortTensorFirstSmallerSecond() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(indices) - t2.getCell(subDims));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryShortTensorSecondSmallerFirst() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(subDims) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryShortTensorSecondSmallerSecond() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(indices) - t2.getCell(subDims));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryIntTensorFirstSmallerFirst() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryIntTensorFirstSmallerSecond() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryIntTensorSecondSmallerFirst() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        IntTensor check = factory.intTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryIntTensorSecondSmallerSecond() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }



    @Test
    public void testByteBinaryLongTensorFirstSmallerFirst() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryLongTensorFirstSmallerSecond() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryLongTensorSecondSmallerFirst() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryLongTensorSecondSmallerSecond() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryFloatTensorFirstSmallerFirst() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryFloatTensorFirstSmallerSecond() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryFloatTensorSecondSmallerFirst() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryFloatTensorSecondSmallerSecond() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryDoubleTensorFirstSmallerFirst() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryDoubleTensorFirstSmallerSecond() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryDoubleTensorSecondSmallerFirst() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryDoubleTensorSecondSmallerSecond() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testByteBinaryByteTensorSmallerFirstNonAuto() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ByteTensor check = factory.byteTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (byte) (t1.getCell(subDims) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryByteTensorSmallerSecondNonAuto() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ByteTensor check = factory.byteTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
            @Override
            public byte getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (byte) (t1.getCell(indices) - t2.getCell(subDims));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryShortTensorFirstSmallerFirstNonAuto() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(subDims) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryShortTensorFirstSmallerSecondNonAuto() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(indices) - t2.getCell(subDims));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryShortTensorSecondSmallerFirstNonAuto() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(subDims) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryShortTensorSecondSmallerSecondNonAuto() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(indices) - t2.getCell(subDims));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryIntTensorFirstSmallerFirstNonAuto() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryIntTensorFirstSmallerSecondNonAuto() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryIntTensorSecondSmallerFirstNonAuto() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        IntTensor check = factory.intTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryIntTensorSecondSmallerSecondNonAuto() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }



    @Test
    public void testByteBinaryLongTensorFirstSmallerFirstNonAuto() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryLongTensorFirstSmallerSecondNonAuto() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryLongTensorSecondSmallerFirstNonAuto() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryLongTensorSecondSmallerSecondNonAuto() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryFloatTensorFirstSmallerFirstNonAuto() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryFloatTensorFirstSmallerSecondNonAuto() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryFloatTensorSecondSmallerFirstNonAuto() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryFloatTensorSecondSmallerSecondNonAuto() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryDoubleTensorFirstSmallerFirstNonAuto() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryDoubleTensorFirstSmallerSecondNonAuto() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryDoubleTensorSecondSmallerFirstNonAuto() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testByteBinaryDoubleTensorSecondSmallerSecondNonAuto() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerFirstAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerSecondAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerFirstAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerFirstAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerSecondAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerSecondAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerFirstAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerFirstAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerSecondAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerSecondAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerFirstAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerFirstAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerSecondAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerSecondAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerFirstAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerFirstAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerSecondAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerSecondAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerFirstAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerFirstAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerSecondAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerSecondAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerFirstNonAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerSecondNonAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerFirstNonAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerFirstNonAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerSecondNonAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerSecondNonAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerFirstNonAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerFirstNonAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerSecondNonAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerSecondNonAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerFirstNonAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerFirstNonAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerSecondNonAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerSecondNonAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerFirstNonAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerFirstNonAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerSecondNonAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerSecondNonAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerFirstNonAutoBadSize() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerFirstNonAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerSecondNonAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerSecondNonAutoBadSize() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test
    public void testShortUnary() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        ShortTensor check = factory.shortTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                return (short) (-1*t.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testShortBinaryShortFirst() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        ShortTensor check = factory.shortTensor(t.getDimensions());
        final short val = random.nextShort();
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                return (short) (val-t.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryShortSecond() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        ShortTensor check = factory.shortTensor(t.getDimensions());
        final short val = random.nextShort();
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                return (short) (t.getCell(indices)-val);
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryIntFirst() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        IntTensor check = factory.intTensor(t.getDimensions());
        final int val = random.nextInt();
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryIntSecond() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        IntTensor check = factory.intTensor(t.getDimensions());
        final int val = random.nextInt();
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryLongFirst() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        LongTensor check = factory.longTensor(t.getDimensions());
        final long val = random.nextLong();
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryLongSecond() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        LongTensor check = factory.longTensor(t.getDimensions());
        final long val = random.nextLong();
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryFloatFirst() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        final float val = random.nextFloat();
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryFloatSecond() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        final float val = random.nextFloat();
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryDoubleFirst() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryDoubleSecond() {
        final ShortTensor t = (ShortTensor) getRandomTensor(JavaType.SHORT);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryShortTensorMatch() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,t1.getDimensions());
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                return (short) (t1.getCell(indices) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryIntTensorFirstMatch() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,t1.getDimensions());
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryIntTensorSecondMatch() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,t1.getDimensions());
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryLongTensorFirstMatch() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,t1.getDimensions());
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryLongTensorSecondMatch() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,t1.getDimensions());
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryFloatTensorFirstMatch() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,t1.getDimensions());
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryFloatTensorSecondMatch() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,t1.getDimensions());
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryDoubleTensorFirstMatch() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryDoubleTensorSecondMatch() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryShortTensorSmallerFirst() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(subDims) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryShortTensorSmallerSecond() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(indices) - t2.getCell(subDims));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryIntTensorFirstSmallerFirst() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryIntTensorFirstSmallerSecond() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryIntTensorSecondSmallerFirst() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        IntTensor check = factory.intTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryIntTensorSecondSmallerSecond() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }



    @Test
    public void testShortBinaryLongTensorFirstSmallerFirst() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryLongTensorFirstSmallerSecond() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryLongTensorSecondSmallerFirst() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryLongTensorSecondSmallerSecond() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryFloatTensorFirstSmallerFirst() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryFloatTensorFirstSmallerSecond() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryFloatTensorSecondSmallerFirst() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryFloatTensorSecondSmallerSecond() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryDoubleTensorFirstSmallerFirst() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryDoubleTensorFirstSmallerSecond() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryDoubleTensorSecondSmallerFirst() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryDoubleTensorSecondSmallerSecond() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testShortBinaryShortTensorSmallerFirstNonAuto() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(subDims) - t2.getCell(indices));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryShortTensorSmallerSecondNonAuto() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ShortTensor check = factory.shortTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
            @Override
            public short getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return (short) (t1.getCell(indices) - t2.getCell(subDims));
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryIntTensorFirstSmallerFirstNonAuto() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryIntTensorFirstSmallerSecondNonAuto() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryIntTensorSecondSmallerFirstNonAuto() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        IntTensor check = factory.intTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryIntTensorSecondSmallerSecondNonAuto() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }



    @Test
    public void testShortBinaryLongTensorFirstSmallerFirstNonAuto() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryLongTensorFirstSmallerSecondNonAuto() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryLongTensorSecondSmallerFirstNonAuto() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryLongTensorSecondSmallerSecondNonAuto() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryFloatTensorFirstSmallerFirstNonAuto() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryFloatTensorFirstSmallerSecondNonAuto() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryFloatTensorSecondSmallerFirstNonAuto() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryFloatTensorSecondSmallerSecondNonAuto() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryDoubleTensorFirstSmallerFirstNonAuto() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryDoubleTensorFirstSmallerSecondNonAuto() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryDoubleTensorSecondSmallerFirstNonAuto() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testShortBinaryDoubleTensorSecondSmallerSecondNonAuto() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerFirstAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerSecondAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerFirstAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerFirstAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerSecondAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerSecondAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerFirstAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerFirstAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerSecondAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerSecondAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerFirstAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerFirstAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerSecondAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerSecondAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerFirstAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerFirstAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerSecondAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerSecondAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerFirstNonAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerSecondNonAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerFirstNonAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerFirstNonAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerSecondNonAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerSecondNonAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerFirstNonAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerFirstNonAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerSecondNonAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerSecondNonAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerFirstNonAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerFirstNonAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerSecondNonAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerSecondNonAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerFirstNonAutoBadSize() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerFirstNonAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerSecondNonAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerSecondNonAutoBadSize() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test
    public void testIntUnary() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        IntTensor check = factory.intTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return -1*t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testIntBinaryIntFirst() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        IntTensor check = factory.intTensor(t.getDimensions());
        final int val = random.nextInt();
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryIntSecond() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        IntTensor check = factory.intTensor(t.getDimensions());
        final int val = random.nextInt();
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryLongFirst() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        LongTensor check = factory.longTensor(t.getDimensions());
        final long val = random.nextLong();
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryLongSecond() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        LongTensor check = factory.longTensor(t.getDimensions());
        final long val = random.nextLong();
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryFloatFirst() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        final float val = random.nextFloat();
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryFloatSecond() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        final float val = random.nextFloat();
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryDoubleFirst() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryDoubleSecond() {
        final IntTensor t = (IntTensor) getRandomTensor(JavaType.INT);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryIntTensorMatch() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,t1.getDimensions());
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryLongTensorFirstMatch() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,t1.getDimensions());
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryLongTensorSecondMatch() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,t1.getDimensions());
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryFloatTensorFirstMatch() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,t1.getDimensions());
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryFloatTensorSecondMatch() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,t1.getDimensions());
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryDoubleTensorFirstMatch() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryDoubleTensorSecondMatch() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryIntTensorSmallerFirst() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryIntTensorSmallerSecond() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryLongTensorFirstSmallerFirst() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryLongTensorFirstSmallerSecond() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryLongTensorSecondSmallerFirst() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryLongTensorSecondSmallerSecond() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryFloatTensorFirstSmallerFirst() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryFloatTensorFirstSmallerSecond() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryFloatTensorSecondSmallerFirst() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryFloatTensorSecondSmallerSecond() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryDoubleTensorFirstSmallerFirst() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryDoubleTensorFirstSmallerSecond() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryDoubleTensorSecondSmallerFirst() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryDoubleTensorSecondSmallerSecond() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testIntBinaryIntTensorSmallerFirstNonAuto() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryIntTensorSmallerSecondNonAuto() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        IntTensor check = factory.intTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryLongTensorFirstSmallerFirstNonAuto() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryLongTensorFirstSmallerSecondNonAuto() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryLongTensorSecondSmallerFirstNonAuto() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryLongTensorSecondSmallerSecondNonAuto() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryFloatTensorFirstSmallerFirstNonAuto() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryFloatTensorFirstSmallerSecondNonAuto() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryFloatTensorSecondSmallerFirstNonAuto() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryFloatTensorSecondSmallerSecondNonAuto() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryDoubleTensorFirstSmallerFirstNonAuto() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryDoubleTensorFirstSmallerSecondNonAuto() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryDoubleTensorSecondSmallerFirstNonAuto() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testIntBinaryDoubleTensorSecondSmallerSecondNonAuto() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerFirstAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerSecondAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerFirstAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerFirstAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerSecondAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerSecondAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerFirstAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerFirstAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerSecondAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerSecondAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerFirstAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerFirstAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerSecondAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerSecondAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerFirstNonAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerSecondNonAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerFirstNonAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerFirstNonAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerSecondNonAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerSecondNonAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerFirstNonAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerFirstNonAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerSecondNonAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerSecondNonAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerFirstNonAutoBadSize() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerFirstNonAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerSecondNonAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerSecondNonAutoBadSize() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test
    public void testLongUnary() {
        final LongTensor t = (LongTensor) getRandomTensor(JavaType.LONG);
        LongTensor check = factory.longTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return -1*t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testLongBinaryLongFirst() {
        final LongTensor t = (LongTensor) getRandomTensor(JavaType.LONG);
        LongTensor check = factory.longTensor(t.getDimensions());
        final long val = random.nextLong();
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryLongSecond() {
        final LongTensor t = (LongTensor) getRandomTensor(JavaType.LONG);
        LongTensor check = factory.longTensor(t.getDimensions());
        final long val = random.nextLong();
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryFloatFirst() {
        final LongTensor t = (LongTensor) getRandomTensor(JavaType.LONG);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        final float val = random.nextFloat();
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryFloatSecond() {
        final LongTensor t = (LongTensor) getRandomTensor(JavaType.LONG);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        final float val = random.nextFloat();
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryDoubleFirst() {
        final LongTensor t = (LongTensor) getRandomTensor(JavaType.LONG);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryDoubleSecond() {
        final LongTensor t = (LongTensor) getRandomTensor(JavaType.LONG);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryLongTensorMatch() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,t1.getDimensions());
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryFloatTensorFirstMatch() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,t1.getDimensions());
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryFloatTensorSecondMatch() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,t1.getDimensions());
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryDoubleTensorFirstMatch() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryDoubleTensorSecondMatch() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryLongTensorSmallerFirst() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryLongTensorSmallerSecond() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryFloatTensorFirstSmallerFirst() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryFloatTensorFirstSmallerSecond() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryFloatTensorSecondSmallerFirst() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryFloatTensorSecondSmallerSecond() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryDoubleTensorFirstSmallerFirst() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryDoubleTensorFirstSmallerSecond() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryDoubleTensorSecondSmallerFirst() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryDoubleTensorSecondSmallerSecond() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testLongBinaryLongTensorSmallerFirstNonAuto() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testLongBinaryLongTensorSmallerSecondNonAuto() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        LongTensor check = factory.longTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testLongBinaryFloatTensorFirstSmallerFirstNonAuto() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testLongBinaryFloatTensorFirstSmallerSecondNonAuto() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testLongBinaryFloatTensorSecondSmallerFirstNonAuto() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testLongBinaryFloatTensorSecondSmallerSecondNonAuto() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testLongBinaryDoubleTensorFirstSmallerFirstNonAuto() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testLongBinaryDoubleTensorFirstSmallerSecondNonAuto() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testLongBinaryDoubleTensorSecondSmallerFirstNonAuto() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testLongBinaryDoubleTensorSecondSmallerSecondNonAuto() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerFirstAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerSecondAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerFirstAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerFirstAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerSecondAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerSecondAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerFirstAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerFirstAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerSecondAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerSecondAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerFirstNonAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerSecondNonAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerFirstNonAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerFirstNonAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerSecondNonAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerSecondNonAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerFirstNonAutoBadSize() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerFirstNonAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerSecondNonAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerSecondNonAutoBadSize() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test
    public void testFloatUnary() {
        final FloatTensor t = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return -1*t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testFloatBinaryFloatFirst() {
        final FloatTensor t = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        final float val = random.nextFloat();
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryFloatSecond() {
        final FloatTensor t = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        FloatTensor check = factory.floatTensor(t.getDimensions());
        final float val = random.nextFloat();
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryDoubleFirst() {
        final FloatTensor t = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryDoubleSecond() {
        final FloatTensor t = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryFloatTensorMatch() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,t1.getDimensions());
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryDoubleTensorFirstMatch() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryDoubleTensorSecondMatch() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryFloatTensorSmallerFirst() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryFloatTensorSmallerSecond() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryDoubleTensorFirstSmallerFirst() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryDoubleTensorFirstSmallerSecond() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryDoubleTensorSecondSmallerFirst() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryDoubleTensorSecondSmallerSecond() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testFloatBinaryFloatTensorSmallerFirstNonAuto() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testFloatBinaryFloatTensorSmallerSecondNonAuto() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        FloatTensor check = factory.floatTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
            @Override
            public float getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testFloatBinaryDoubleTensorFirstSmallerFirstNonAuto() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testFloatBinaryDoubleTensorFirstSmallerSecondNonAuto() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testFloatBinaryDoubleTensorSecondSmallerFirstNonAuto() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testFloatBinaryDoubleTensorSecondSmallerSecondNonAuto() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerFirstAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerSecondAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerFirstAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerFirstAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerSecondAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerSecondAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerFirstNonAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerSecondNonAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerFirstNonAutoBadSize() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerFirstNonAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerSecondNonAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerSecondNonAutoBadSize() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test
    public void testDoubleUnary() {
        final DoubleTensor t = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return -1*t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t,NumericFunctions.NEGATE),check);
    }

    @Test
    public void testDoubleBinaryDoubleFirst() {
        final DoubleTensor t = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return val-t.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(val,t,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testDoubleBinaryDoubleSecond() {
        final DoubleTensor t = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        DoubleTensor check = factory.doubleTensor(t.getDimensions());
        final double val = random.nextDouble();
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t.getCell(indices)-val;
            }
        });
        util.assertTensorEquals(ebe.calculate(t,val,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testDoubleBinaryDoubleTensorMatch() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,t1.getDimensions());
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                return t1.getCell(indices) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testDoubleBinaryDoubleTensorSmallerFirst() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testDoubleBinaryDoubleTensorSmallerSecond() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT),check);
    }

    @Test
    public void testDoubleBinaryDoubleTensorSmallerFirstNonAuto() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t2.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t1.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(subDims) - t2.getCell(indices);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test
    public void testDoubleBinaryDoubleTensorSmallerSecondNonAuto() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        final int[] dimMatch = matchingDims[1];
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        DoubleTensor check = factory.doubleTensor(t1.getDimensions());
        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
            @Override
            public double getValue(int ... indices) {
                int[] subDims = new int[t2.size()];
                for (int i : range(dimMatch.length))
                    subDims[i] = indices[dimMatch[i]];
                return t1.getCell(indices) - t2.getCell(subDims);
            }
        });
        util.assertTensorEquals(ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,dimMatch),check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerFirstAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),true,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerSecondAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),true,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerFirstNonAutoBadSize() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerSecondNonAutoBadSize() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        if (matchingDims[0].length == 0) {
            matchingDims[0] = new int[] {25};
        } else {
            int dimChange = random.nextInt(matchingDims[0].length);
            matchingDims[0][dimChange] += 25; //now invalid
        }
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerFirstNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerSecondNonAutoDimMatchTooSmall() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length-1];
        System.arraycopy(matchingDims[1],0,badMatch,0,badMatch.length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerFirstNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t2.size();
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerSecondNonAutoDimMatchBadDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int randDim = random.nextInt(matchingDims[1].length);
        matchingDims[1][randDim] = t1.size();
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculate(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    private Number getRandomNumber() {
        switch (random.nextInt(6)) {
            case 0 : return random.nextByte();
            case 1 : return random.nextShort();
            case 2 : return random.nextInt();
            case 3 : return random.nextLong();
            case 4 : return random.nextFloat();
            case 5 : return random.nextDouble();
            default : throw new IllegalStateException("Shouldn't be here");
        }
    }
    
    private Tensor<? extends Number> getRandomScalar() {
        Number n = getRandomNumber();
        switch (JavaType.getPrimitiveJavaType(n.getClass())) {
            case BYTE : return factory.initializedByteScalar(n.byteValue());
            case SHORT : return factory.initializedShortScalar(n.shortValue());
            case INT : return factory.initializedIntScalar(n.intValue());
            case LONG : return factory.initializedLongScalar(n.longValue());
            case FLOAT : return factory.initializedFloatScalar(n.floatValue());
            case DOUBLE : return factory.initializedDoubleScalar(n.doubleValue());
            default : throw new IllegalStateException("Shouldn't be here");
        }
    }

    private JavaType getRandomType() {
        JavaType type = random.getRandomValue(JavaType.values());
        while (!type.isNumeric())
            type = random.getRandomValue(JavaType.values());
        return type;
    }

//    private Object[] getParameters(int count) {
//        //returns [params,largest dimension,dim_matches]
//        Object[] params = new Object[count];
//        int[][] dimMatches = new int[count][];
//        Tensor<? extends Number> largestTensor = getRandomTensor(getRandomType());
//        params[random.nextInt(params.length)] = largestTensor;
//        int[] dims = largestTensor.getDimensions();
//        for (int i : range(count)) {
//            if (params[i] != largestTensor) { //already filled in this element
//                int[][] matchingDims = getMatchingDims(dims,true,false);
//                params[i] = random.nextBoolean() ? getRandomTensor(getRandomType(),matchingDims[0]) : getRandomNumber();
//                dimMatches[i] = matchingDims[1];
//            }
//        }
//        return new Object[] {params,dims,dimMatches};
//    }

    protected Object[] getParametersList(int count) {
        //returns [params,largest dimension,dim_matches]
        Object[] params = new Object[count];
        int[][] dimMatches = new int[count][];
        Tensor<? extends Number> largestTensor = getRandomTensor(getRandomType());
        params[random.nextInt(params.length)] = largestTensor;
        int[] dims = largestTensor.getDimensions();
        for (int i : range(count)) {
            if (params[i] != largestTensor) { //already filled in this element
                int[][] matchingDims = getMatchingDims(dims,true,false);
                params[i] = random.nextBoolean() ? getRandomTensor(getRandomType(),matchingDims[0]) : getRandomScalar();
                dimMatches[i] = matchingDims[1];
            }
        }
        return new Object[] {Arrays.asList(params),dims,dimMatches};
    }

    protected NumericFunctionN getDefaultNumericFunction(int paramCount) {
        //params includes first parameter
        //formula will be a+b-c+d... == (a,b,+,c,d,...,+,...,-)
        int paramsLength = paramCount-1;
        NumericFunctionN[] function = new NumericFunctionN[2*paramsLength+1];
        function[0] = NumericFunctions.PARAMETER;
        function[1] = NumericFunctions.PARAMETER;
        function[2] = NumericFunctions.ADD;
        for (int i : range(3,2+paramsLength))
            function[i] = NumericFunctions.PARAMETER;
        for (int i : range(2+paramsLength,function.length-1))
            function[i] = NumericFunctions.ADD;
        function[function.length-1] = NumericFunctions.SUBTRACT;
        return NumericFunctions.compositeNumericFunction(function);
    }

    protected Number getFunctionNValue(Object o, int[] dimMatch, int[] indices, JavaType outType) {
        Number value = getFunctionNValue(o,dimMatch,indices);
        boolean real = value instanceof Float || value instanceof Double;
        switch (outType) {
            case DOUBLE :
            case FLOAT : return value.doubleValue();
            default : return real ? Math.round(value.doubleValue()) : value.longValue();
        }
    }

    protected Number getFunctionNValue(Object o, int[] dimMatch, int[] indices) {
        if (o instanceof Number)
            return (Number) o;
        @SuppressWarnings("unchecked") //ok - this will be a numeric tensor
        Tensor<? extends Number> t = (Tensor<? extends Number>) o;
        if (t.size() == 0)
            return t.getValue();
        if (dimMatch == null)
            return t.getValue(indices);
        int[] subDims = new int[t.size()];
        for (int i : range(dimMatch.length))
            subDims[i] = indices[dimMatch[i]];
        return t.getValue(subDims);
    }

//    @Test
//    public void testFunctionNByte() {
//        final byte param = random.nextByte();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//
//        ByteTensor check = factory.byteTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
//            @Override
//            public byte getValue(int ... indices) {
//                byte value1 = (byte) (param + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.BYTE).byteValue());
//                byte value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.BYTE).byteValue();
//                for (int i : range(2,params.length))
//                    value2 = (byte) (value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.BYTE).byteValue());
//                return (byte) (value1 - value2);
//            }
//        });
//
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNByteTensor() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final int[] paramMatch = paramDimMatch[1];
//        final ByteTensor param = (ByteTensor) getRandomTensor(JavaType.BYTE,paramDimMatch[0]);
//
//        ByteTensor check = factory.byteTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.ByteTensorValueFunction() {
//            @Override
//            public byte getValue(int ... indices) {
//                byte value1 = (byte) (getFunctionNValue(param,paramMatch,indices,JavaType.BYTE).byteValue() + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.BYTE).byteValue());
//                byte value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.BYTE).byteValue();
//                for (int i : range(2,params.length))
//                    value2 = (byte) (value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.BYTE).byteValue());
//                return (byte) (value1 - value2);
//            }
//        });
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNShort() {
//        final short param = random.nextShort();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//
//        ShortTensor check = factory.shortTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
//            @Override
//            public short getValue(int ... indices) {
//                short value1 = (short) (param + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.SHORT).shortValue());
//                short value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.SHORT).shortValue();
//                for (int i : range(2,params.length))
//                    value2 = (short) (value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.SHORT).shortValue());
//                return (short) (value1 - value2);
//            }
//        });
//
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNShortTensor() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final int[] paramMatch = paramDimMatch[1];
//        final ShortTensor param = (ShortTensor) getRandomTensor(JavaType.SHORT,paramDimMatch[0]);
//
//        ShortTensor check = factory.shortTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.ShortTensorValueFunction() {
//            @Override
//            public short getValue(int ... indices) {
//                short value1 = (short) (getFunctionNValue(param,paramMatch,indices,JavaType.SHORT).shortValue() + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.SHORT).shortValue());
//                short value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.SHORT).shortValue();
//                for (int i : range(2,params.length))
//                    value2 = (short) (value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.SHORT).shortValue());
//                return (short) (value1 - value2);
//            }
//        });
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNInt() {
//        final int param = random.nextInt();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//
//        IntTensor check = factory.intTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
//            @Override
//            public int getValue(int ... indices) {
//                int value1 = param + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.INT).intValue();
//                int value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.INT).intValue();
//                for (int i : range(2,params.length))
//                    value2 = value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.INT).intValue();
//                return value1 - value2;
//            }
//        });
//
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNIntTensor() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final int[] paramMatch = paramDimMatch[1];
//        final IntTensor param = (IntTensor) getRandomTensor(JavaType.INT,paramDimMatch[0]);
//
//        IntTensor check = factory.intTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
//            @Override
//            public int getValue(int ... indices) {
//                int value1 = getFunctionNValue(param,paramMatch,indices,JavaType.INT).intValue() + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.INT).intValue();
//                int value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.INT).intValue();
//                for (int i : range(2,params.length))
//                    value2 = value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.INT).intValue();
//                return value1 - value2;
//            }
//        });
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNLong() {
//        final long param = random.nextLong();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//
//        LongTensor check = factory.longTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
//            @Override
//            public long getValue(int ... indices) {
//                long value1 = param + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.LONG).longValue();
//                long value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.LONG).longValue();
//                for (int i : range(2,params.length))
//                    value2 = value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.LONG).longValue();
//                return value1 - value2;
//            }
//        });
//
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNLongTensor() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final int[] paramMatch = paramDimMatch[1];
//        final LongTensor param = (LongTensor) getRandomTensor(JavaType.LONG,paramDimMatch[0]);
//
//        LongTensor check = factory.longTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.LongTensorValueFunction() {
//            @Override
//            public long getValue(int ... indices) {
//                long value1 = getFunctionNValue(param,paramMatch,indices,JavaType.LONG).longValue() + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.LONG).longValue();
//                long value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.LONG).longValue();
//                for (int i : range(2,params.length))
//                    value2 = value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.LONG).longValue();
//                return value1 - value2;
//            }
//        });
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNFloat() {
//        final float param = random.nextFloat();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//
//        FloatTensor check = factory.floatTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
//            @Override
//            public float getValue(int ... indices) {
//                float value1 = param + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.FLOAT).floatValue();
//                float value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.FLOAT).floatValue();
//                for (int i : range(2,params.length))
//                    value2 = value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.FLOAT).floatValue();
//                return value1 - value2;
//            }
//        });
//
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNFloatTensor() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final int[] paramMatch = paramDimMatch[1];
//        final FloatTensor param = (FloatTensor) getRandomTensor(JavaType.FLOAT,paramDimMatch[0]);
//
//        FloatTensor check = factory.floatTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.FloatTensorValueFunction() {
//            @Override
//            public float getValue(int ... indices) {
//                float value1 = getFunctionNValue(param,paramMatch,indices,JavaType.FLOAT).floatValue() + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.FLOAT).floatValue();
//                float value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.FLOAT).floatValue();
//                for (int i : range(2,params.length))
//                    value2 = value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.FLOAT).floatValue();
//                return value1 - value2;
//            }
//        });
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNDouble() {
//        final double param = random.nextDouble();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//
//        DoubleTensor check = factory.doubleTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
//            @Override
//            public double getValue(int ... indices) {
//                double value1 = param + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.DOUBLE).doubleValue();
//                double value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.DOUBLE).doubleValue();
//                for (int i : range(2,params.length))
//                    value2 = value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.DOUBLE).doubleValue();
//                return value1 - value2;
//            }
//        });
//
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//    @Test
//    public void testFunctionNDoubleTensor() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        final int[][] dimMatches = (int[][]) p[2];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final int[] paramMatch = paramDimMatch[1];
//        final DoubleTensor param = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,paramDimMatch[0]);
//
//        DoubleTensor check = factory.doubleTensor(dims);
//        TensorUtil.fill(check,new TensorUtil.DoubleTensorValueFunction() {
//            @Override
//            public double getValue(int ... indices) {
//                double value1 = getFunctionNValue(param,paramMatch,indices,JavaType.DOUBLE).doubleValue() + getFunctionNValue(params[0],dimMatches[0],indices,JavaType.DOUBLE).doubleValue();
//                double value2 = getFunctionNValue(params[1],dimMatches[1],indices,JavaType.DOUBLE).doubleValue();
//                for (int i : range(2,params.length))
//                    value2 = value2 + getFunctionNValue(params[i],dimMatches[i],indices,JavaType.DOUBLE).doubleValue();
//                return value1 - value2;
//            }
//        });
//        util.assertTensorEquals(check,ebe.calculate(function,param,params));
//    }
//
//
//
//
//
//
//
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNByteBadParameterCount() {
//        final byte param = random.nextByte();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNByteTensorBadParameterCount() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final ByteTensor param = (ByteTensor) getRandomTensor(JavaType.BYTE,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNByteNonNumericParameter() {
//        final byte param = random.nextByte();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNByteTensorNonNumericParameter() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final ByteTensor param = (ByteTensor) getRandomTensor(JavaType.BYTE,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNByteBadMatch() {
//        final byte param = random.nextByte();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.byteTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.byteTensor(badDims2);
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNByteTensorBadMatch() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final ByteTensor param = (ByteTensor) getRandomTensor(JavaType.BYTE,paramDimMatch[0]);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.byteTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.byteTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNByteTensorBadMatchFirstParam() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        ByteTensor param = factory.byteTensor(badDims1);
//        params[random.nextInt(params.length)] = factory.byteTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNShortBadParameterCount() {
//        final short param = random.nextShort();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNShortTensorBadParameterCount() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final ShortTensor param = (ShortTensor) getRandomTensor(JavaType.SHORT,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNShortNonNumericParameter() {
//        final short param = random.nextShort();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNShortTensorNonNumericParameter() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final ShortTensor param = (ShortTensor) getRandomTensor(JavaType.SHORT,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNShortBadMatch() {
//        final short param = random.nextShort();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.shortTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.shortTensor(badDims2);
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNShortTensorBadMatch() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final ShortTensor param = (ShortTensor) getRandomTensor(JavaType.SHORT,paramDimMatch[0]);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.shortTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.shortTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNShortTensorBadMatchFirstParam() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        ShortTensor param = factory.shortTensor(badDims1);
//        params[random.nextInt(params.length)] = factory.shortTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNIntBadParameterCount() {
//        final int param = random.nextInt();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNIntTensorBadParameterCount() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final IntTensor param = (IntTensor) getRandomTensor(JavaType.INT,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNIntNonNumericParameter() {
//        final int param = random.nextInt();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNIntTensorNonNumericParameter() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final IntTensor param = (IntTensor) getRandomTensor(JavaType.INT,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNIntBadMatch() {
//        final int param = random.nextInt();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.intTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.intTensor(badDims2);
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNIntTensorBadMatch() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final IntTensor param = (IntTensor) getRandomTensor(JavaType.INT,paramDimMatch[0]);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.intTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.intTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNIntTensorBadMatchFirstParam() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        IntTensor param = factory.intTensor(badDims1);
//        params[random.nextInt(params.length)] = factory.intTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNLongBadParameterCount() {
//        final long param = random.nextLong();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNLongTensorBadParameterCount() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final LongTensor param = (LongTensor) getRandomTensor(JavaType.LONG,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNLongNonNumericParameter() {
//        final long param = random.nextLong();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNLongTensorNonNumericParameter() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final LongTensor param = (LongTensor) getRandomTensor(JavaType.LONG,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNLongBadMatch() {
//        final long param = random.nextLong();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.longTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.longTensor(badDims2);
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNLongTensorBadMatch() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final LongTensor param = (LongTensor) getRandomTensor(JavaType.LONG,paramDimMatch[0]);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.longTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.longTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNLongTensorBadMatchFirstParam() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        LongTensor param = factory.longTensor(badDims1);
//        params[random.nextInt(params.length)] = factory.longTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNFloatBadParameterCount() {
//        final float param = random.nextFloat();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNFloatTensorBadParameterCount() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final FloatTensor param = (FloatTensor) getRandomTensor(JavaType.FLOAT,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNFloatNonNumericParameter() {
//        final float param = random.nextFloat();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNFloatTensorNonNumericParameter() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final FloatTensor param = (FloatTensor) getRandomTensor(JavaType.FLOAT,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNFloatBadMatch() {
//        final float param = random.nextFloat();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.floatTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.floatTensor(badDims2);
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNFloatTensorBadMatch() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final FloatTensor param = (FloatTensor) getRandomTensor(JavaType.FLOAT,paramDimMatch[0]);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.floatTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.floatTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNFloatTensorBadMatchFirstParam() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        FloatTensor param = factory.floatTensor(badDims1);
//        params[random.nextInt(params.length)] = factory.floatTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNDoubleBadParameterCount() {
//        final double param = random.nextDouble();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNDoubleTensorBadParameterCount() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+2);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final DoubleTensor param = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNDoubleNonNumericParameter() {
//        final double param = random.nextDouble();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNDoubleTensorNonNumericParameter() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        params[0] = new Object();
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final DoubleTensor param = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,paramDimMatch[0]);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNDoubleBadMatch() {
//        final double param = random.nextDouble();
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.doubleTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.doubleTensor(badDims2);
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNDoubleTensorBadMatch() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[][] paramDimMatch = getMatchingDims(dims,true,false);
//        final DoubleTensor param = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,paramDimMatch[0]);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        params[random.nextInt(2)] = factory.doubleTensor(badDims1);
//        params[random.nextInt(2,params.length)] = factory.doubleTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }
//
//    @Test(expected=IllegalArgumentException.class)
//    public void testFunctionNDoubleTensorBadMatchFirstParam() {
//        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
//        int a = random.nextInt(3,4+random.nextInt(5));
//        Object[] p = getParameters(a);
//        final Object[] params = (Object[]) p[0];
//        int[] dims = (int[]) p[1];
//        NumericFunctionN function = getDefaultNumericFunction(params.length+1);
//        int[] badDims1 = new int[] {1,2,3};
//        int[] badDims2 = new int[] {3,2,1};
//        DoubleTensor param = factory.doubleTensor(badDims1);
//        params[random.nextInt(params.length)] = factory.doubleTensor(badDims2);
//        ebe.calculate(function,param,params);
//    }




    @Test
    public void testCalculateByte() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        ByteTensor check = factory.byteTensor(dims);
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
        util.assertTensorEquals(check,ebe.calculateByte(function,params));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateByteBadParameterCountTooBig() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()+random.nextInt(1,4));
        ebe.calculateByte(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateByteBadParameterCountTooSmall() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()-1);
        ebe.calculateByte(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateByteBadMatch() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but ok here
        final List<Tensor<? extends Number>> params = (List<Tensor<? extends Number>>) p[0];
        int[] badDims1 = new int[] {1,2,3};
        int[] badDims2 = new int[] {3,2,1};
        params.set(random.nextInt(2),factory.byteTensor(badDims1));
        params.set(random.nextInt(2,params.size()),factory.byteTensor(badDims2));
        NumericFunctionN function = getDefaultNumericFunction(params.size());
        ebe.calculateByte(function,params);
    }
    
    @Test
    public void testCalculateShort() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        ShortTensor check = factory.shortTensor(dims);
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
        util.assertTensorEquals(check,ebe.calculateShort(function,params));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateShortBadParameterCountTooBig() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()+random.nextInt(1,4));
        ebe.calculateShort(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateShortBadParameterCountTooSmall() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()-1);
        ebe.calculateShort(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateShortBadMatch() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but ok here
        final List<Tensor<? extends Number>> params = (List<Tensor<? extends Number>>) p[0];
        int[] badDims1 = new int[] {1,2,3};
        int[] badDims2 = new int[] {3,2,1};
        params.set(random.nextInt(2),factory.shortTensor(badDims1));
        params.set(random.nextInt(2,params.size()),factory.shortTensor(badDims2));
        NumericFunctionN function = getDefaultNumericFunction(params.size());
        ebe.calculateShort(function,params);
    }
    
    @Test
    public void testCalculateInt() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        IntTensor check = factory.intTensor(dims);
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
        util.assertTensorEquals(check,ebe.calculateInt(function,params));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateIntBadParameterCountTooBig() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()+random.nextInt(1,4));
        ebe.calculateInt(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateIntBadParameterCountTooSmall() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()-1);
        ebe.calculateInt(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateIntBadMatch() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but ok here
        final List<Tensor<? extends Number>> params = (List<Tensor<? extends Number>>) p[0];
        int[] badDims1 = new int[] {1,2,3};
        int[] badDims2 = new int[] {3,2,1};
        params.set(random.nextInt(2),factory.intTensor(badDims1));
        params.set(random.nextInt(2,params.size()),factory.intTensor(badDims2));
        NumericFunctionN function = getDefaultNumericFunction(params.size());
        ebe.calculateInt(function,params);
    }
    
    @Test
    public void testCalculateLong() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        LongTensor check = factory.longTensor(dims);
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
        util.assertTensorEquals(check,ebe.calculateLong(function,params));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateLongBadParameterCountTooBig() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()+random.nextInt(1,4));
        ebe.calculateLong(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateLongBadParameterCountTooSmall() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()-1);
        ebe.calculateLong(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateLongBadMatch() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but ok here
        final List<Tensor<? extends Number>> params = (List<Tensor<? extends Number>>) p[0];
        int[] badDims1 = new int[] {1,2,3};
        int[] badDims2 = new int[] {3,2,1};
        params.set(random.nextInt(2),factory.longTensor(badDims1));
        params.set(random.nextInt(2,params.size()),factory.longTensor(badDims2));
        NumericFunctionN function = getDefaultNumericFunction(params.size());
        ebe.calculateLong(function,params);
    }
    
    @Test
    public void testCalculateFloat() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        FloatTensor check = factory.floatTensor(dims);
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
        util.assertTensorEquals(check,ebe.calculateFloat(function,params));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateFloatBadParameterCountTooBig() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()+random.nextInt(1,4));
        ebe.calculateFloat(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateFloatBadParameterCountTooSmall() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()-1);
        ebe.calculateFloat(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateFloatBadMatch() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but ok here
        final List<Tensor<? extends Number>> params = (List<Tensor<? extends Number>>) p[0];
        int[] badDims1 = new int[] {1,2,3};
        int[] badDims2 = new int[] {3,2,1};
        params.set(random.nextInt(2),factory.floatTensor(badDims1));
        params.set(random.nextInt(2,params.size()),factory.floatTensor(badDims2));
        NumericFunctionN function = getDefaultNumericFunction(params.size());
        ebe.calculateFloat(function,params);
    }
    
    @Test
    public void testCalculateDouble() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        DoubleTensor check = factory.doubleTensor(dims);
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
        util.assertTensorEquals(check,ebe.calculateDouble(function,params));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateDoubleBadParameterCountTooBig() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()+random.nextInt(1,4));
        ebe.calculateDouble(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateDoubleBadParameterCountTooSmall() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()-1);
        ebe.calculateDouble(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateDoubleBadMatch() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but ok here
        final List<Tensor<? extends Number>> params = (List<Tensor<? extends Number>>) p[0];
        int[] badDims1 = new int[] {1,2,3};
        int[] badDims2 = new int[] {3,2,1};
        params.set(random.nextInt(2),factory.doubleTensor(badDims1));
        params.set(random.nextInt(2,params.size()),factory.doubleTensor(badDims2));
        NumericFunctionN function = getDefaultNumericFunction(params.size());
        ebe.calculateDouble(function,params);
    }        
    
    @Test
    public void testCalculateFunctionNByte() {
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
        util.assertTensorEquals(check,ebe.calculateByte(function,params));
    }         
    
    @Test
    public void testCalculateFunctionNShort() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        boolean hasHighest = false;
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE : break;
                case SHORT :
                case INT :
                case LONG :
                case FLOAT :
                case DOUBLE : hasHighest = true; t = TensorUtil.asShortTensor(t);
            }
            params.add(t);
        }
        if (!hasHighest) {
            int ri = random.nextInt(params.size());
            params.set(ri,TensorUtil.asShortTensor(params.get(ri)));
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        ShortTensor check = factory.shortTensor(dims);
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
        util.assertTensorEquals(check,ebe.calculateShort(function,params));
    }                     
    
    @Test
    public void testCalculateFunctionNInt() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        boolean hasHighest = false;
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE : 
                case SHORT : break;
                case INT :
                case LONG :
                case FLOAT :
                case DOUBLE : hasHighest = true; t = TensorUtil.asIntTensor(t);
            }
            params.add(t);
        }
        if (!hasHighest) {
            int ri = random.nextInt(params.size());
            params.set(ri,TensorUtil.asIntTensor(params.get(ri)));
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        IntTensor check = factory.intTensor(dims);
        TensorUtil.fill(check,new TensorUtil.IntTensorValueFunction() {
            @Override
            public int getValue(int ... indices) {
                int value1 =getFunctionNValue(params.get(0),dimMatches[0],indices,JavaType.INT).intValue() + getFunctionNValue(params.get(1),dimMatches[1],indices,JavaType.INT).intValue();
                int value2 = getFunctionNValue(params.get(2),dimMatches[2],indices,JavaType.INT).intValue();
                for (int i : range(3,params.size()))
                    value2 = value2 + getFunctionNValue(params.get(i),dimMatches[i],indices,JavaType.INT).intValue();
                return value1 - value2;
            }
        });
        util.assertTensorEquals(check,ebe.calculateInt(function,params));
    }            
    
    @Test
    public void testCalculateFunctionNLong() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        boolean hasHighest = false;
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE : 
                case SHORT :      
                case INT : break;
                case LONG : 
                case FLOAT :
                case DOUBLE : hasHighest = true; t = TensorUtil.asLongTensor(t);
            }
            params.add(t);
        }
        if (!hasHighest) {
            int ri = random.nextInt(params.size());
            params.set(ri,TensorUtil.asLongTensor(params.get(ri)));
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        LongTensor check = factory.longTensor(dims);
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
        util.assertTensorEquals(check,ebe.calculateLong(function,params));
    }       
    
    @Test
    public void testCalculateFunctionNFloat() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        boolean hasHighest = false;
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE : 
                case SHORT :      
                case INT :
                case LONG : break;
                case FLOAT :
                case DOUBLE : hasHighest = true; t = TensorUtil.asFloatTensor(t);
            }
            params.add(t);
        }
        if (!hasHighest) {
            int ri = random.nextInt(params.size());
            params.set(ri,TensorUtil.asFloatTensor(params.get(ri)));
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        FloatTensor check = factory.floatTensor(dims);
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
        util.assertTensorEquals(check,ebe.calculateFloat(function,params));
    }       
    
    @Test
    public void testCalculateFunctionNDouble() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but will bend to our will\
        final List<Tensor<? extends Number>> oParams = (List<Tensor<? extends Number>>) p[0];
        final List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        boolean hasHighest = false;
        for (Tensor<? extends Number> t : oParams) {
            switch (t.getType()) {
                case BYTE : 
                case SHORT :     
                case INT :
                case LONG :
                case FLOAT : break;
                case DOUBLE : hasHighest = true; t = TensorUtil.asDoubleTensor(t);
            }
            params.add(t);
        }
        if (!hasHighest) {
            int ri = random.nextInt(params.size());
            params.set(ri,TensorUtil.asDoubleTensor(params.get(ri)));
        }
        int[] dims = (int[]) p[1];
        final int[][] dimMatches = (int[][]) p[2];
        NumericFunctionN function = getDefaultNumericFunction(params.size());

        DoubleTensor check = factory.doubleTensor(dims);
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
        util.assertTensorEquals(check,ebe.calculateDouble(function,params));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateFunctionNBadParameterCountTooBig() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()+random.nextInt(1,4));
        ebe.calculate(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateFunctionNBadParameterCountTooSmall() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //correct
        final List<? extends Tensor<? extends Number>> params = (List<? extends Tensor<? extends Number>>) p[0];
        NumericFunctionN function = getDefaultNumericFunction(params.size()-1);
        ebe.calculate(function,params);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCalculateFunctionNBadMatch() {
        //formula will be a+b - c+d... == (a,b,+,c,d,...,+,...,-)
        int a = random.nextInt(4,5+random.nextInt(5));
        Object[] p = getParametersList(a);
        @SuppressWarnings("unchecked") //not technically correct, but ok here
        final List<Tensor<? extends Number>> params = (List<Tensor<? extends Number>>) p[0];
        int[] badDims1 = new int[] {1,2,3};
        int[] badDims2 = new int[] {3,2,1};
        params.set(random.nextInt(2),factory.byteTensor(badDims1));
        params.set(random.nextInt(2,params.size()),factory.byteTensor(badDims2));
        NumericFunctionN function = getDefaultNumericFunction(params.size());
        ebe.calculate(function,params);
    }
}
