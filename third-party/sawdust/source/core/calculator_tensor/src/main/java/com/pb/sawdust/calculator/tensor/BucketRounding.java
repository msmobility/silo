package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.decorators.primitive.FloatTensor;
import com.pb.sawdust.tensor.decorators.primitive.IntTensor;
import com.pb.sawdust.tensor.decorators.primitive.LongTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.util.probability.MonteCarlo;
import com.pb.sawdust.util.abacus.Abacus;
import com.pb.sawdust.util.abacus.IterableAbacus;

import java.util.*;

/**
 * The {@code BucketRounding} ...
 *
 * @author crf
 *         Started 9/29/11 11:09 AM
 */
public class BucketRounding {
    private final TensorFactory factory;

    public BucketRounding(TensorFactory factory) {
        this.factory = factory;
    }

    private void checkElementCount(Tensor<?> tensor) {
        if (TensorUtil.getElementCount(tensor) > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Cannot bucket round a tensor with more than Integer.MAX_VALUE elements.");
    }

    public LongTensor roundDeterministically(DoubleTensor tensor) {
        checkElementCount(tensor);
        return roundDeterministically(tensor,factory.longTensor(tensor.getDimensions()));
    }

    public IntTensor roundDeterministicallyToInt(DoubleTensor tensor) {
        checkElementCount(tensor);
        IntTensor result = factory.intTensor(tensor.getDimensions());
        roundDeterministically(tensor,TensorUtil.asLongTensor(result));
        return result;
    }

    public IntTensor roundDeterministically(FloatTensor tensor) {
        return roundDeterministicallyToInt(TensorUtil.asDoubleTensor(tensor));
    }

    private LongTensor roundDeterministically(DoubleTensor tensor, LongTensor result) {
        Set<WeightedIndex> bucketList = new TreeSet<WeightedIndex>();
        double fractionSum = 0.0;
        int counter = 0;
        Abacus ab = new Abacus(result.getDimensions());
        for (int[] index : new IterableAbacus(ab)) {
            double value = tensor.getCell(index);
            long base = (long) Math.floor(value);
            double fraction = value - base;
            fractionSum += fraction;
            result.setCell(base,index);
            bucketList.add(new WeightedIndex(fraction,counter++));
        }
        Iterator<WeightedIndex> it = bucketList.iterator();
        for (int i : range((int) Math.round(fractionSum))) {
            int[] index = ab.getAbacusPoint(it.next().index);
            result.setCell(result.getCell(index)+1,index);
        }
        return result;
    }

    public LongTensor roundRandomly(DoubleTensor tensor) {
        return roundRandomly(tensor,new Random(),true);
    }

    public IntTensor roundRandomlyToInt(DoubleTensor tensor) {
        return roundRandomlyToInt(tensor,new Random(),true);
    }

    public IntTensor roundRandomly(FloatTensor tensor) {
        return roundRandomly(tensor,new Random(),true);
    }

    public IntTensor roundRandomlyToInt(DoubleTensor tensor, Random random, boolean withReplacement) {
        checkElementCount(tensor);
        IntTensor result = factory.intTensor(tensor.getDimensions());
        roundRandomly(tensor,TensorUtil.asLongTensor(result),random,withReplacement);
        return result;
    }

    public LongTensor roundRandomly(DoubleTensor tensor, Random random, boolean withReplacement) {
        checkElementCount(tensor);
        LongTensor result = factory.longTensor(tensor.getDimensions());
        return roundRandomly(tensor,result,random,withReplacement);
    }

    public IntTensor roundRandomly(FloatTensor tensor, Random random, boolean withReplacement) {
        return roundRandomlyToInt(TensorUtil.asDoubleTensor(tensor),random,withReplacement);
    }

    private LongTensor roundRandomly(DoubleTensor tensor, LongTensor result, Random random, boolean withReplacement) {
        long elements = TensorUtil.getElementCount(tensor);
        double[] dist = new double[(int) elements];
        Abacus ab = new Abacus(result.getDimensions());
        double fractionSum = 0.0;
        int counter = 0;
        for (int[] index : new IterableAbacus(ab)) {
            double value = tensor.getCell(index);
            long base = (long) Math.floor(value);
            double fraction = value - base;
            fractionSum += fraction;
            result.setCell(base,index);
            dist[counter++] = fraction;
        }
        MonteCarlo mc = MonteCarlo.getMonteCarloFromDistribution(dist,random);
        Set<Integer> used = new HashSet<Integer>();
        for (int i : range((int) Math.round(fractionSum))) {
            int[] index = ab.getAbacusPoint(withReplacement ? mc.draw() : mc.drawWithoutReplacement(used));
            result.setCell(result.getCell(index)+1,index);
        }
        return result;
    }

    private class WeightedIndex implements Comparable<WeightedIndex> {
        private final double weight;
        private final int index;

        public WeightedIndex(double weight, int index) {
            this.weight = weight;
            this.index = index;
        }

        @Override
        public int compareTo(WeightedIndex o) {
            return weight < o.weight ? 1 : (weight == o.weight ? 0 : -1); //reverse weighting
        }
    }
}
