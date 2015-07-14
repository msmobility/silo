package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.vector.Vector;
import com.pb.sawdust.tensor.decorators.primitive.DoubleTensor;
import com.pb.sawdust.util.MathUtil;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.abacus.LockableAbacus;

import java.util.Arrays;

import static com.pb.sawdust.util.Range.*;

/**
 * The {@code IterativeProportionalFitting} class provides the functionality to perform n-dimensional iterative proportional
 * fitting to tensors. Iterative proportional fitting (IPF), also known as matrix balancing for the two-diemnsional case,
 * scales a seed tensor such that its marginals match target vectors (within a certain tolerance). The seed tensor provides
 * an initial distribution that is used to guide the scaling, but generally this distribution will not be maintained in
 * the final result tensor.
 * <p>
 * This class also contains a variety of methods used to construct marginals for use in the fitting procedure. Specifically,
 * the methods take a series of marginals with the correct internal proportionality, but scale them to match a certain
 * target value. These procedures create marginal arrays which fulfill the requirement that all marginal sums must be
 * equal to the same amount (which will be identical to the sum of the final result tensor).
 *
 * @author crf <br/>
 *         Started Jul 13, 2010 10:44:26 AM
 */
public class IterativeProportionalFitting {
    private final TensorFactory factory;
    private int lastIpfTotalIterations = -1;

    /**
     * Constructor specifying the tensor factory to use to build the tensors created by the instance.
     *
     * @param factory
     *        The tensor factory used to build tensors used (internally and externally) by this instance.
     */
    public IterativeProportionalFitting(TensorFactory factory) {
        this.factory = factory;
    }

    @SafeVarargs
    private final double[][] getMarginalArray(Vector<? extends Number> ... marginals) {
        double[][] margs = new double[marginals.length][];
        for (int i : range(margs.length))
            margs[i] = TensorUtil.asDoubleTensor(marginals[i]).getTensorValues().getUltimateArray();
        return margs;
    }

    private static final int TARGET_PROVIDED = -1;
    private static final int TARGET_AVERAGE = -2;

    /**
     * Get adjusted marginals such that the sum across each marginal dimension equals that of the specified dimension. That
     * is, each marginal (that is not the base dimension marginal) will be scaled such that they will sum to the same amount
     * as that of the base dimension, but also so that the proportionality across each element is maintained. The result
     * of this method can be passed directly into the {@code ipf} methods in this class. This method does not directly
     * modify the original marginal arrays, but rather returns a new array with the adjusted marginals.
     *
     * @param baseDimension
     *        The base dimensions whose marginal sum will provide the target value.
     *
     * @param marginals
     *        The marginals to adjust.
     *
     * @return an array of marginals, adjusted to match the marginal sum in {@code baseDimension}.
     */
    public double[][] getAdjustedMarginals(int baseDimension, double[] ... marginals) {
        if (baseDimension < 0 || baseDimension > marginals.length)
            throw new IllegalArgumentException(String.format("Base dimension for marginal adjustment (%d) is out of bounds for %d marginals",baseDimension,marginals.length));
        return getAdjustedMarginals(0.0,baseDimension,marginals);
    }

    /**
     * Get adjusted marginals such that the sum across each marginal dimension equals that of the specified target value.
     * That is, each marginal will be scaled such that they will sum to the target value, but also so that the proportionality
     * across each element is maintained. The result of this method can be passed directly into the {@code ipf} methods in
     * this class. This method does not directly modify the original marginal arrays, but rather returns a new array with
     * the adjusted marginals.
     *
     * @param target
     *        The target value.
     *
     * @param marginals
     *        The marginals to adjust.
     *
     * @return an array of marginals, adjusted to match the {@code target}.
     */
    public double[][] getAdjustedMarginals(double target, double[] ... marginals) {
        return  getAdjustedMarginals(target,TARGET_PROVIDED,marginals);
    }

    /**
     * Get adjusted marginals such that the sum across each marginal dimension equals the average of the original marginal
     * sums. That is, each marginal will be scaled such that they will sum to the target value (the average of the original
     * marginal sums), but also so that the proportionality across each element is maintained. The result of this method
     * can be passed directly into the {@code ipf} methods in this class. This method does not directly modify the original
     * marginal arrays, but rather returns a new array with the adjusted marginals.
     *
     * @param marginals
     *        The marginals to adjust.
     *
     * @return an array of marginals, adjusted to match the {@code target}.
     */
    public double[][] getAdjustedMarginals(double[] ... marginals) {
        return getAdjustedMarginals(0.0,TARGET_AVERAGE,marginals);
    }

    /**
     * Get adjusted marginals such that the sum across each marginal dimension equals that of the specified dimension. That
     * is, each marginal (that is not the base dimension marginal) will be scaled such that they will sum to the same amount
     * as that of the base dimension, but also so that the proportionality across each element is maintained. The result
     * of this method can be passed directly into the {@code ipf} methods in this class. This method does not directly
     * modify the original marginal vectors, but rather returns a new array with the adjusted marginals.
     *
     * @param baseDimension
     *        The base dimensions whose marginal sum will provide the target value.
     *
     * @param marginals
     *        The marginals to adjust.
     *
     * @return an array of marginals, adjusted to match the marginal sum in {@code baseDimension}.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    public double[][] getAdjustedMarginals(int baseDimension, Vector<? extends Number> ... marginals) {
        return getAdjustedMarginals(baseDimension,getMarginalArray(marginals));
    }

    /**
     * Get adjusted marginals such that the sum across each marginal dimension equals that of the specified target value.
     * That is, each marginal will be scaled such that they will sum to the target value, but also so that the proportionality
     * across each element is maintained. The result of this method can be passed directly into the {@code ipf} methods in
     * this class. This method does not directly modify the original marginal vectors, but rather returns a new array with
     * the adjusted marginals.
     *
     * @param target
     *        The target value.
     *
     * @param marginals
     *        The marginals to adjust.
     *
     * @return an array of marginals, adjusted to match the {@code target}.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    public double[][] getAdjustedMarginals(double target, Vector<? extends Number> ... marginals) {
        return getAdjustedMarginals(target,getMarginalArray(marginals));
    }

    /**
     * Get adjusted marginals such that the sum across each marginal dimension equals the average of the original marginal
     * sums. That is, each marginal will be scaled such that they will sum to the target value (the average of the original
     * marginal sums), but also so that the proportionality across each element is maintained. The result of this method
     * can be passed directly into the {@code ipf} methods in this class. This method does not directly modify the original
     * marginal vectors, but rather returns a new array with the adjusted marginals.
     *
     * @param marginals
     *        The marginals to adjust.
     *
     * @return an array of marginals, adjusted to match the {@code target}.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    public double[][] getAdjustedMarginals(Vector<? extends Number> ... marginals) {
        return getAdjustedMarginals(getMarginalArray(marginals));
    }

    private double[][] getAdjustedMarginals(double target, int targetId, double[] ... marginals) {
        double[] factors = new double[marginals.length];
        //factors get marginal sums first
        for (int i : range(factors.length))
            for (double d : marginals[i])
                factors[i] += d;
        //scale to marginals[baseDimension]
        if (targetId == TARGET_PROVIDED) {
            //do nothing, target provided
        } else if (targetId == TARGET_AVERAGE) {
            target = 0.0;
            for (double f : factors)
                target += f;
            target /= factors.length;
        } else if (targetId > -1) {
            target = factors[targetId];
        }
        double[][] adjustedMarginals = new double[marginals.length][];
        for (int i : range(factors.length)) {
            double factor = factors[i] == 0.0 ? 0.0 : target / factors[i];
            adjustedMarginals[i] = new double[marginals[i].length];
            for (int j : range(marginals[i].length))
                adjustedMarginals[i][j] = marginals[i][j] * factor;
        }
        return adjustedMarginals;
    }

    /**
     * Run the iterative proportional fitting procedure on a seed tensor using the specified marginals. The procedure will
     * stop when the tolerance is achieved across all dimensions, or when the maximum number of iterations has been achieved,
     * whichever comes first. The tolerance is considered achieved if the normalized difference between a result marginal
     * element and the corresponding goal marginal element is less than the tolerance (<i>i.e.</i>:
     * <pre><code>
     *     |<i>result_marginal<sub>i</sub></i> - <i>goal_marginal<sub>i</sub></i>| / <i>goal_marginal<sub>i</sub></i> &lt; <i>tolerance</i>
     * </code></pre>
     * ) for every element in every marginal.
     *
     * @param seed
     *        The seed tensor for the fitting procedure.
     *
     * @param maxIterations
     *        The maximum number of iterations to use in the procedure.
     *
     * @param tolerance
     *        The stopping tolerance.
     *
     * @param marginals
     *        The goal marginals for the procedure, in the same order as the dimensions of the seed. Each marginal must
     *        sum to the same value.
     *
     * @return the result of the IPF procedure on {@code seed} and {@code marginals}
     *
     * @throws IllegalArgumentException if the number of marginals does not equal the number of dimensions of {@code seed},
     *                                  if the size of any of the marginal vectors does not equal the size of the corresponding
     *                                  dimension in {@code seed}, or if each marginal in {@code marginals} does not sum
     *                                  to the same value.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    public DoubleTensor ipf(Tensor<? extends Number> seed, int maxIterations, double tolerance, Vector<? extends Number> ... marginals) {
        return ipf(seed,maxIterations,tolerance,getMarginalArray(marginals));
    }

    /**
     * Run the iterative proportional fitting procedure on a seed tensor using the specified marginals. The procedure will
     * stop when the tolerance is achieved across all dimensions, or when the maximum number of iterations has been achieved,
     * whichever comes first. The tolerance is considered achieved if the normalized difference between a result marginal
     * element and the corresponding goal marginal element is less than the tolerance (<i>i.e.</i>:
     * <pre><code>
     *     |<i>result_marginal<sub>i</sub></i> - <i>goal_marginal<sub>i</sub></i>| / <i>goal_marginal<sub>i</sub></i> &lt; <i>tolerance</i>
     * </code></pre>
     * ) for every element in every marginal.
     *
     * @param seed
     *        The seed tensor for the fitting procedure.
     *
     * @param maxIterations
     *        The maximum number of iterations to use in the procedure.
     *
     * @param tolerance
     *        The stopping tolerance.
     *
     * @param marginals
     *        The goal marginals for the procedure, in the same order as the dimensions of the seed. Each marginal must
     *        sum to the same value.
     *
     * @return the result of the IPF procedure on {@code seed} and {@code marginals}
     *
     * @throws IllegalArgumentException if the number of marginals does not equal the number of dimensions of {@code seed},
     *                                  if the size of any of the marginal arrays does not equal the size of the corresponding
     *                                  dimension in {@code seed}, or if each marginal in {@code marginals} does not sum
     *                                  to the same value.
     */
    public DoubleTensor ipf(Tensor<? extends Number> seed, int maxIterations, double tolerance, double[] ... marginals) {
        return ipf(true,seed,maxIterations,tolerance,marginals);
    }

    /**
     * Run the iterative proportional fitting procedure using the specified marginals. The seed tensor will be a uniform
     * tensor of 1's, and its dimensionality will be specified by the size of the marginals. The procedure will
     * stop when the tolerance is achieved across all dimensions, or when the maximum number of iterations has been achieved,
     * whichever comes first. The tolerance is considered achieved if the normalized difference between a result marginal
     * element and the corresponding goal marginal element is less than the tolerance (<i>i.e.</i>:
     * <pre><code>
     *     |<i>result_marginal<sub>i</sub></i> - <i>goal_marginal<sub>i</sub></i>| / <i>goal_marginal<sub>i</sub></i> &lt; <i>tolerance</i>
     * </code></pre>
     * ) for every element in every marginal.
     *
     * @param maxIterations
     *        The maximum number of iterations to use in the procedure.
     *
     * @param tolerance
     *        The stopping tolerance.
     *
     * @param marginals
     *        The goal marginals for the procedure. Each marginal must sum to the same value.
     *
     * @return the result of the IPF procedure using {@code marginals}
     *
     * @throws IllegalArgumentException if each marginal in {@code marginals} does not sum to the same value.
     */
    public DoubleTensor ipf(int maxIterations, double tolerance, double[] ... marginals) {
        int[] dims = new int[marginals.length];
        int counter = 0;
        for (double[] marginal : marginals)
            dims[counter++] = marginal.length;
        DoubleTensor result = factory.initializedDoubleTensor(1.0,dims);
        return ipf(false,result,maxIterations,tolerance,marginals);
    }

    /**
     * Run the iterative proportional fitting procedure using the specified marginals. The seed tensor will be a uniform
     * tensor of 1's, and its dimensionality will be specified by the size of the marginals. The procedure will
     * stop when the tolerance is achieved across all dimensions, or when the maximum number of iterations has been achieved,
     * whichever comes first. The tolerance is considered achieved if the normalized difference between a result marginal
     * element and the corresponding goal marginal element is less than the tolerance (<i>i.e.</i>:
     * <pre><code>
     *     |<i>result_marginal<sub>i</sub></i> - <i>goal_marginal<sub>i</sub></i>| / <i>goal_marginal<sub>i</sub></i> &lt; <i>tolerance</i>
     * </code></pre>
     * ) for every element in every marginal.
     *
     * @param maxIterations
     *        The maximum number of iterations to use in the procedure.
     *
     * @param tolerance
     *        The stopping tolerance.
     *
     * @param marginals
     *        The goal marginals for the procedure. Each marginal must sum to the same value.
     *
     * @return the result of the IPF procedure using {@code marginals}
     *
     * @throws IllegalArgumentException if each marginal in {@code marginals} does not sum to the same value.
     */
    @SuppressWarnings({"unchecked", "varargs"})
    public DoubleTensor ipf(int maxIterations, double tolerance, Vector<? extends Number> ... marginals) {
        return ipf(maxIterations,tolerance,getMarginalArray(marginals));
    }

    /**
     * Get the total number of IPF iterations that were needed for the last call to one of the {@code ipf} methods.
     *
     * @return the total number of IPF iterations for the last {@code ipf} call to this instance.
     *
     * @throws IllegalStateException if an {@code ipf} method has not been called yet.
     */
    public int getLastIpfTotalIterations() {
        if (lastIpfTotalIterations < 0)
            throw new IllegalStateException("IPF procedure has not been run yet.");
        return lastIpfTotalIterations;
    }

    private void checkMarginals(double[][] marginals) {
        double check = 0.0;
        for (int i : range(marginals.length)) {
            double checkAgainst = 0.0;
            for (int j : range(marginals[i].length))
                checkAgainst += marginals[i][j];
            if (i == 0)
                check = checkAgainst;
            else if (!MathUtil.almostEquals(check,checkAgainst))
                throw new IllegalArgumentException("Marginals in each dimension must sum equally: " + check + " (dim 0), " + checkAgainst + "(dim  " + i + ")");
        }
    }

    private DoubleTensor ipf(boolean sendingSeed, Tensor<? extends Number> seedOrResult, int maxIterations, double tolerance, double[] ... marginals) {
        checkMarginals(marginals);
        if (marginals.length != seedOrResult.size())
            throw new IllegalArgumentException("Marginal count must equal seed dimension count.");
        for (int d : range(seedOrResult.size()))
            if (seedOrResult.size(d) != marginals[d].length)
                throw new IllegalArgumentException("Marginal size (" + marginals[d].length + ") for dimension " + d + " does not match seed size (" + seedOrResult.size(d) + ")");
        DoubleTensor result;
        if (sendingSeed) //seed in, need result
            result = TensorUtil.copyOfAsDouble(TensorUtil.asDoubleTensor(seedOrResult),factory);
        else
            result = (DoubleTensor) seedOrResult;
        double[][] factors = new double[marginals.length][];
        double[][] currentMarginals = new double[marginals.length][];
        for (int i : range(marginals.length)) {
            double[] d = new double[marginals[i].length];
            Arrays.fill(d,1.0);
            factors[i] = d;
            currentMarginals[i] = new double[marginals[i].length];
        }

        int iterationCount = 0;
        while (iterationCount++ < maxIterations && !isConverged(marginals,updateCurrentMarginals(currentMarginals,result),tolerance)) {
            updateFactors(factors,marginals,result);
            updateResult(factors,result);
        }
        lastIpfTotalIterations = iterationCount;
        return result;
    }

    private double[][] updateCurrentMarginals(double[][] currentMarginals, DoubleTensor result) {
        TensorMarginal tm = new TensorMarginal(ArrayTensor.getFactory());
        int counter = 0;
        for (DoubleTensor t : tm.getCollapsedMarginals(result,TensorMarginal.Marginal.SUM)) {
            for (int i : range(t.size(0))) {
                currentMarginals[counter][i] = t.getCell(i);
            }
            counter++;
        }
        return currentMarginals;
    }

    private boolean isConverged(double[][] marginals, double[][] currentMarginals, double tolerance) {
        for (int i : range(marginals.length))
            for (int j : range(marginals[i].length))
                if (Math.abs((marginals[i][j] - currentMarginals[i][j])/marginals[i][j]) > tolerance)
                    return false;
        return true;
    }

    private void updateResult(double[][] factors, DoubleTensor result) {
        for (int[] dim : IterableAbacus.getIterableAbacus(result.getDimensions()))
            result.setCell(result.getCell(dim)*getFactorMultiple(factors,dim),dim);
    }

    private void updateFactors(double[][] factors, double[][] marginals, DoubleTensor result) {
        for (int i : range(factors.length)) {
            for (int j : range(factors[i].length)) {
                double divisor = 0.0;
                LockableAbacus a = new LockableAbacus(result.getDimensions());
                a.lockDimension(i,j);
                for (int[] dim : new IterableAbacus(a)) {
                    divisor += result.getCell(dim)*getFactorMultiple(factors,dim,i);
                }
                factors[i][j] = divisor == 0.0 ? 0.0 : marginals[i][j] / divisor;
            }
        }
    }

    private double getFactorMultiple(double[][] factors, int[] indices, final int holdDimension) {
        double divisor = 1.0;
        for (int i : range(factors.length))
            if (i == holdDimension)
                continue;
            else
                divisor *= factors[i][indices[i]];
        return divisor;
    }

    private double getFactorMultiple(double[][] factors, int[] indices) {
        double divisor = 1.0;
        for (int i : range(factors.length))
            divisor *= factors[i][indices[i]];
        return divisor;
    }

    public static void main(String ... args) {
        //test code
        TensorFactory factory = ArrayTensor.getFactory();

        DoubleTensor seed = factory.initializedDoubleTensor(1.0,4,4,3);
        seed.setCell(25.0,1,2,0);
        seed.setCell(0.0,1,1,1);
        seed.setCell(0.0,2,2,1);
        seed.setCell(0.0,3,3,1);
        seed.setCell(0.0,0,2,2);
        seed.setCell(0.0,1,1,2);
        seed.setCell(0.0,2,0,2);
        seed.setCell(0.0,3,0,2);
        seed.setCell(0.0,3,3,2);

        double[][] marginals = {{4,6,4,8},
                                {4,5,9,4},
                                {5,8,9}};

        System.out.println(TensorUtil.toString(seed));
        DoubleTensor result = new IterativeProportionalFitting(factory).ipf(seed,20,0.001,marginals);
        System.out.println(TensorUtil.toString(result));
    }
}
