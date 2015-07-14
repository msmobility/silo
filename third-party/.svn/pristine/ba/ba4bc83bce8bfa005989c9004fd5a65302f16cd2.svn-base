package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.calculator.NumericFunctions;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.pb.sawdust.util.Range.range;
import static org.junit.Assert.assertTrue;

/**
 * The {@code CellWiseTensorMutatingCalculationTest} ...
 *
 * @author crf <br/>
 *         Started Nov 28, 2010 9:43:02 PM
 */
public abstract class CellWiseTensorMutatingCalculationTest extends TestBase {
    public static final String SECOND_TENSOR_MODE_KEY = "second tensor mode";

    protected static enum SecondTensorMode {
        SCALAR,
        FULL,
        RANDOM;
    }
    private SecondTensorMode mode;

    protected CellWiseTensorMutatingCalculation ebe;
    protected TensorFactory factory;
    
    abstract protected CellWiseTensorMutatingCalculation getCellWiseTensorMutatingCalculation(TensorFactory factory);
    
    protected TensorFactory getFactory() {
        return ArrayTensor.getFactory();
    }

    protected SecondTensorMode getSecondTensorMode() {
        return mode;
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
        ebe = getCellWiseTensorMutatingCalculation(factory);
        mode = (SecondTensorMode) getTestData(SECOND_TENSOR_MODE_KEY);
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
        Tensor<? extends Number> t;
        switch (type) {
            case BYTE : {
                t = factory.byteTensor(dims);
                TensorUtil.fill((ByteTensor) t, new TensorUtil.ByteTensorValueFunction() {
                    @Override
                    public byte getValue(int ... indices) {
                        return random.nextByte();
                    }
                });
                return t;
            }
            case SHORT : {
                t = factory.shortTensor(dims);
                TensorUtil.fill((ShortTensor) t, new TensorUtil.ShortTensorValueFunction() {
                    @Override
                    public short getValue(int ... indices) {
                        return random.nextShort();
                    }
                });
                return t;
            }
            case INT : {
                t = factory.intTensor(dims);
                TensorUtil.fill((IntTensor) t, new TensorUtil.IntTensorValueFunction() {
                    @Override
                    public int getValue(int ... indices) {
                        return random.nextInt();
                    }
                });
                return t;
            }
            case LONG : {
                t = factory.longTensor(dims);
                TensorUtil.fill((LongTensor) t, new TensorUtil.LongTensorValueFunction() {
                    @Override
                    public long getValue(int ... indices) {
                        return random.nextLong();
                    }
                });
                return t;
            }
            case FLOAT : {
                t = factory.floatTensor(dims);
                TensorUtil.fill((FloatTensor) t, new TensorUtil.FloatTensorValueFunction() {
                    @Override
                    public float getValue(int ... indices) {
                        return random.nextFloat();
                    }
                });
                return t;
            }
            case DOUBLE : {
                t = factory.doubleTensor(dims);
                TensorUtil.fill((DoubleTensor) t, new TensorUtil.DoubleTensorValueFunction() {
                    @Override
                    public double getValue(int ... indices) {
                        return random.nextDouble();
                    }
                });
                return t;
            }
            default : throw new IllegalStateException("shouldn't be here");
        }
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
                    case 1 : matchCount = first ? 0 : 1; break; //need to have smaller first smaller if possible
                    case 2 : matchCount = 1; break;
                    default : matchCount = random.nextInt(1,len+(auto && !first ? 0 : -1)); //allow everything from vector up to exact match, except for non-auto first args
                                                                                            //and for conditional because first argument means less, and expecting error
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
    
    protected boolean printFailedMatrices = false;
    
    protected void assertTensorEquals(ByteTensor t1, ByteTensor t2) {
        boolean eq = TensorUtil.equals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        assertTrue(eq);
    }   
    
    protected void assertTensorEquals(ShortTensor t1, ShortTensor t2) {
        boolean eq = TensorUtil.equals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        assertTrue(eq);
    }
    
    protected void assertTensorEquals(IntTensor t1, IntTensor t2) {
        boolean eq = TensorUtil.equals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        assertTrue(eq);
    }
    
    protected void assertTensorEquals(LongTensor t1, LongTensor t2) {
        boolean eq = TensorUtil.equals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        assertTrue(eq);
    }
    
    protected void assertTensorEquals(FloatTensor t1, FloatTensor t2) {
        boolean eq = TensorUtil.almostEquals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        assertTrue(eq);
    }
    
    protected void assertTensorEquals(DoubleTensor t1, DoubleTensor t2) {
        boolean eq = TensorUtil.almostEquals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        assertTrue(eq);
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
        ebe.calculateInto(t, NumericFunctions.NEGATE);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asByteTensor(check));
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asByteTensor(check));
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asByteTensor(check));
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asByteTensor(check));
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asByteTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
    }

    

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
    }  

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
    }



    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asByteTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryByteTensorSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryShortTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryIntTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryLongTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ByteTensor t2 = (ByteTensor) getRandomTensor(JavaType.BYTE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testByteBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ByteTensor t1 = (ByteTensor) getRandomTensor(JavaType.BYTE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t, NumericFunctions.NEGATE);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asShortTensor(check));
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asShortTensor(check));
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asShortTensor(check));
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asShortTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
    }



    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
    }



    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asShortTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryShortTensorSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryIntTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryLongTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final ShortTensor t2 = (ShortTensor) getRandomTensor(JavaType.SHORT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShortBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final ShortTensor t1 = (ShortTensor) getRandomTensor(JavaType.SHORT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t, NumericFunctions.NEGATE);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asIntTensor(check));
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asIntTensor(check));
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asIntTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asIntTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asIntTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asIntTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asIntTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asIntTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asIntTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asIntTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asIntTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asIntTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryIntTensorSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryLongTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final IntTensor t2 = (IntTensor) getRandomTensor(JavaType.INT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIntBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final IntTensor t1 = (IntTensor) getRandomTensor(JavaType.INT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t, NumericFunctions.NEGATE);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asLongTensor(check));
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asLongTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asLongTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asLongTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asLongTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asLongTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asLongTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asLongTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryLongTensorSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryFloatTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final LongTensor t2 = (LongTensor) getRandomTensor(JavaType.LONG,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLongBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final LongTensor t1 = (LongTensor) getRandomTensor(JavaType.LONG,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t, NumericFunctions.NEGATE);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,TensorUtil.asFloatTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asFloatTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,TensorUtil.asFloatTensor(check));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,TensorUtil.asFloatTensor(check));
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryFloatTensorSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerFirstNonAutoDimMatchTooBig() {
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorFirstSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final FloatTensor t2 = (FloatTensor) getRandomTensor(JavaType.FLOAT,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testFloatBinaryDoubleTensorSecondSmallerSecondNonAutoDimMatchTooBig() {
        final FloatTensor t1 = (FloatTensor) getRandomTensor(JavaType.FLOAT,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t, NumericFunctions.NEGATE);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t,val,NumericFunctions.SUBTRACT);
        assertTensorEquals(t,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerFirst() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
        assertTensorEquals(t1,check);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerFirstNonAuto() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR || getSecondTensorMode() == SecondTensorMode.FULL)
            throw new IllegalArgumentException("Invalid test for scalar or full");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,1);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,dimMatch);
        assertTensorEquals(t1,check);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerFirstNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerSecondNonAutoRepeatedMatchingDim() {
        if(getSecondTensorMode() == SecondTensorMode.SCALAR)
            throw new IllegalArgumentException("Invalid test for scalar");
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        matchingDims[1] = new int[] {matchingDims[1][0],matchingDims[1][0]};
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerFirstNonAutoDimMatchTooBig() {
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t2.getDimensions(),false,true);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testDoubleBinaryDoubleTensorSmallerSecondNonAutoDimMatchTooBig() {
        final DoubleTensor t1 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,2);
        int[][] matchingDims = getMatchingDims(t1.getDimensions(),false,false);
        int[] badMatch = new int[matchingDims[1].length+1];
        System.arraycopy(matchingDims[1],0,badMatch,0,matchingDims[1].length);
        final DoubleTensor t2 = (DoubleTensor) getRandomTensor(JavaType.DOUBLE,matchingDims[0]);
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,badMatch);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
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
        ebe.calculateInto(t1,t2,NumericFunctions.SUBTRACT,matchingDims[1]);
    }
}
