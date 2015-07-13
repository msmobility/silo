package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.calculator.NumericFunction2;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Arrays;

/**
 * The {@code TensorComparison} ...
 *
 * @author crf
 *         Started 8/5/11 9:55 AM
 */
public class TensorComparison {

    public static double meanSquaredError(Tensor<? extends Number> t1, Tensor<? extends Number> t2, TensorFactory factory) {
        if (!Arrays.equals(t1.getDimensions(),t2.getDimensions()))
            throw new IllegalArgumentException(String.format("Tensor dimensions must be equal to perform mean squared error calculation: %s vs %s",Arrays.toString(t1.getDimensions()),Arrays.toString(t2.getDimensions())));
        CellWiseTensorCalculation cwtc = new DefaultCellWiseTensorCalculation(factory);
        TensorMarginal tm = new TensorMarginal(factory);
        NumericFunction2 errorSquared = new NumericFunction2.DecimalFunction2() {
            @Override
            public double apply(double x, double y) {
                return Math.pow(x-y,2);
            }
        };
        DoubleTensor esq = cwtc.calculate(TensorUtil.asDoubleTensor(t1),TensorUtil.asDoubleTensor(t2),errorSquared);
        double totalSquaredError = tm.getFullMarginal(esq,TensorMarginal.Marginal.SUM);
        //tm.getMarginal(tm.getCollapsedMarginal(esq,0,TensorMarginal.Marginal.SUM),0,TensorMarginal.Marginal.SUM).getCell();
        long count = TensorUtil.getElementCount(t1);
        return totalSquaredError/(count-1);
    }

    public static double rootMeanSquaredError(Tensor<? extends Number> t1, Tensor<? extends Number> t2, TensorFactory factory) {
        return Math.sqrt(meanSquaredError(t1,t2,factory));
    }

    public static double percentRootMeanSquaredError(Tensor<? extends Number> t1, Tensor<? extends Number> t2, TensorFactory factory) {
        TensorMarginal tm = new TensorMarginal(factory);
        double rmse = rootMeanSquaredError(t1,t2,factory);
        double totalBase = tm.getFullMarginal(TensorUtil.asDoubleTensor(t1),TensorMarginal.Marginal.SUM);
        //double totalBase = tm.getMarginal(tm.getCollapsedMarginal(TensorUtil.asDoubleTensor(t1),0,TensorMarginal.Marginal.SUM),0,TensorMarginal.Marginal.SUM).getCell();
        long count = TensorUtil.getElementCount(t1);
        return 100*rmse / (totalBase/count);
    }

}
