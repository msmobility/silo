package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.RandomDeluxe;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.test.TestBase;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

/**
 * The {@code TensorTestUtil} ...
 *
 * @author crf <br/>
 *         Started Dec 2, 2010 10:37:19 PM
 */
public class TensorTestUtil {
    private final TensorFactory factory;
    private final RandomDeluxe random;
    boolean printFailedMatrices = false;
    
    public TensorTestUtil(TensorFactory factory, RandomDeluxe random) {
        this.factory = factory;
        this.random= random;
    }

    public void setPrintFailedMatrices(boolean printFailedMatrices) {
        this.printFailedMatrices = printFailedMatrices;
    }
    
    public Tensor<? extends Number> getRandomTensor(JavaType type, int[] dims) {
        Tensor<? extends Number> t;
        switch (type) {
            case BYTE : {
                t = factory.byteTensor(dims);
                TensorUtil.fill((ByteTensor) t, new TensorUtil.ByteTensorValueFunction() {
                    @Override
                    public byte getValue(int ... indices) {
//                        return random.nextByte();
                        return (byte) random.nextInt(-20,20); //overflows possible - not guaranteed to be the same across non-java implementations
                    }
                });
                return t;
            }
            case SHORT : {
                t = factory.shortTensor(dims);
                TensorUtil.fill((ShortTensor) t, new TensorUtil.ShortTensorValueFunction() {
                    @Override
                    public short getValue(int ... indices) {
                        //return random.nextShort();
                        return (short) random.nextInt(-1000,1000); //overflows possible - not guaranteed to be the same across non-java implementations
                    }
                });
                return t;
            }
            case INT : {
                t = factory.intTensor(dims);
                TensorUtil.fill((IntTensor) t, new TensorUtil.IntTensorValueFunction() {
                    @Override
                    public int getValue(int ... indices) {
                        return random.nextInt(-1000,1000); //overflows possible if up to max int - not guaranteed to be the same across non-java implementations
                    }
                });
                return t;
            }
            case LONG : {
                t = factory.longTensor(dims);
                TensorUtil.fill((LongTensor) t, new TensorUtil.LongTensorValueFunction() {
                    @Override
                    public long getValue(int ... indices) {
                        //return random.nextLong();
                        return random.nextInt(-1000,1000); //using longs sometimes causes overflows, which are bad for testing
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

    private String getTensorUnequalExtraInformation(Tensor<?> t1, Tensor<?> t2) {
        StringBuilder sb = new StringBuilder();
        String eol = FileUtil.getLineSeparator();
        sb.append("expected:").append(eol);
        sb.append(TensorUtil.toString(t1)).append(eol);
        sb.append("actual:").append(eol);
        sb.append(TensorUtil.toString(t2)).append(eol);
        if (!Arrays.equals(t1.getDimensions(),t2.getDimensions())) {
            sb.append("non matching dimensions: ").append(Arrays.toString(t1.getDimensions()))
                    .append(" vs. ").append(Arrays.toString(t2.getDimensions())).append(eol);
        } else {
            int[] badIndex = new int[0];
            if (t1.size() > 0) {
            for (int[] index : IterableAbacus.getIterableAbacus(t1.getDimensions()))
                if (!t1.getValue(index).equals(t2.getValue(index))) {
                    badIndex = index;
                    break;
                }
            }
            sb.append("first unequal: index (").append(Arrays.toString(badIndex)).append(") = ")
                    .append(t1.getValue(badIndex)).append(" vs. ").append(t2.getValue(badIndex));
        }
        return sb.toString();
    }
    
    public void assertTensorEquals(ByteTensor t1, ByteTensor t2) {
        boolean eq = TensorUtil.equals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        if (!eq)
            TestBase.addTestFailureInformation(getTensorUnequalExtraInformation(t1,t2));
        assertTrue(eq);
    }   
    
    public void assertTensorEquals(ShortTensor t1, ShortTensor t2) {
        boolean eq = TensorUtil.equals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        if (!eq)
            TestBase.addTestFailureInformation(getTensorUnequalExtraInformation(t1,t2));
        assertTrue(eq);
    }
    
    public void assertTensorEquals(IntTensor t1, IntTensor t2) {
        boolean eq = TensorUtil.equals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        if (!eq)
            TestBase.addTestFailureInformation(getTensorUnequalExtraInformation(t1,t2));
        assertTrue(eq);
    }
    
    public void assertTensorEquals(LongTensor t1, LongTensor t2) {
        boolean eq = TensorUtil.equals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        if (!eq)
            TestBase.addTestFailureInformation(getTensorUnequalExtraInformation(t1,t2));
        assertTrue(eq);
    }
    
    public void assertTensorEquals(FloatTensor t1, FloatTensor t2) {
        boolean eq = TensorUtil.almostEquals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        if (!eq)
            TestBase.addTestFailureInformation(getTensorUnequalExtraInformation(t1,t2));
        assertTrue(eq);
    }
    
    public void assertTensorEquals(DoubleTensor t1, DoubleTensor t2) {
        boolean eq = TensorUtil.almostEquals(t1,t2);
        if (printFailedMatrices && !eq) {
            System.out.println(TensorUtil.toString(t1));
            System.out.println(TensorUtil.toString(t2));
        }
        if (!eq)
            TestBase.addTestFailureInformation(getTensorUnequalExtraInformation(t1,t2));
        assertTrue(eq);
    }
}
