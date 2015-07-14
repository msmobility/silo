package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.tensor.*;

import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.tensor.alias.vector.Vector;
import com.pb.sawdust.tensor.alias.vector.primitive.*;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.RandomDeluxe;
import com.pb.sawdust.util.abacus.Abacus;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.concurrent.DnCRecursiveAction;
import com.pb.sawdust.util.concurrent.ForkJoinPoolFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;


/**
 * The {@code TensorMarginal} class provides methods for calculating marginals (operations across indices) on numeric
 * tensors.  A tensor marginal is some function (such as summing or averaging) applied to all cells in a tensor with
 * all but one index fixed.  A collapsed tensor marginal is the function applied across all cells in a tensor except a
 * fixed dimension.
 * <p>
 * To illustrate the difference, consider the following <code>2x2x2</code> tensor:
 * <p>
 * <pre><code>
 * dimension 0 = 0
 * [1   2]
 * [3   4]
 *
 * dimension 0 = 1
 * [5   6]
 * [7   0]
 * </code></pre>
 * <p>
 * The tensor marginal using the function {@code ADD} (+) across dimension 0 would be:
 * <p>
 * <pre><code>
 * [6   8]
 * [10  4]
 * </code></pre>
 * <p>
 * whereas the collapsed tensor marginal (for {@code ADD}) for dimension 0 would be:
 * <p>
 * <pre><code>
 * [10  18]
 * </code></pre>
 * <p>
 * In this class, tensor marginals are arranged as a list of tensors, where the rank of the marginals is one less than the
 * rank of the source tensor.  The position of the marginal tensor in the list indicates which tensor dimension was not 
 * held constant.  For example, the marginal sums for a two-dimensional tensor (matrix) are a list of one-dimensional
 * tensors (vectors) where the first vector is the column marginal (summing all rows (dimension 0) for a given column),
 *  and the second vector is the row marginal.
 * <p>
 * Collapsed tensor marginals are arranged as a list of vectors, where the position of the marginal vector in the list
 * indicates which dimension was fixed in the calculation.
 *
 * @author crf <br/>
 *         Started Aug 12, 2010 8:40:18 PM
 */
public class TensorMarginal {
    private final TensorFactory factory;

    /**
     * Constructor specifying the {@code TensorFactory} from which the marginal results will be constructed.
     * 
     * @param factory
     *        The tensor factory.
     */
    public TensorMarginal(TensorFactory factory) {
        this.factory = factory;
    }

    public static void main(String ... args) {
        TensorFactory factory = ArrayTensor.getFactory();
        LongTensor m = factory.longTensor(5,7);
//        LongTensor m = factory.longTensor();
        final RandomDeluxe rand = new RandomDeluxe();
        TensorUtil.fill(m,new TensorUtil.LongTensorValueFunction() {
            @Override
            public long getValue(int ... indices) {
                return rand.nextInt(0,10);
            }
        });
        System.out.println(TensorUtil.toString(m));
        System.out.println();
        TensorMarginal tm = new TensorMarginal(factory);
        for (Tensor<? extends Number> t : tm.getMarginals(m,Marginal.AVERAGE,JavaType.FLOAT)) {
            System.out.println(TensorUtil.toString(t));
            System.out.println();
        }
    }

    /**
     * Get the tensor marginals for a double tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @return the tensor marginals.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<DoubleTensor> getMarginals(DoubleTensor tensor, MarginalFunction function) {
        return (List<DoubleTensor>) getMarginals(tensor,function,tensor.getType());
    } 

    /**
     * Get the tensor marginals for a float tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @return the tensor marginals.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<FloatTensor> getMarginals(FloatTensor tensor, MarginalFunction function) {
        return (List<FloatTensor>) getMarginals(tensor,function,tensor.getType());
    }   

    /**
     * Get the tensor marginals for a long tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @return the tensor marginals.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<LongTensor> getMarginals(LongTensor tensor, MarginalFunction function) {
        return (List<LongTensor>) getMarginals(tensor,function,tensor.getType());
    }    

    /**
     * Get the tensor marginals for an int tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @return the tensor marginals.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<IntTensor> getMarginals(IntTensor tensor, MarginalFunction function) {
        return (List<IntTensor>) getMarginals(tensor,function,tensor.getType());
    }

    /**
     * Get the tensor marginals for a short tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @return the tensor marginals.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<ShortTensor> getMarginals(ShortTensor tensor, MarginalFunction function) {
        return (List<ShortTensor>) getMarginals(tensor,function,tensor.getType());
    }

    /**
     * Get the tensor marginals for a byte tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @return the tensor marginals.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<ByteTensor> getMarginals(ByteTensor tensor, MarginalFunction function) {
        return (List<ByteTensor>) getMarginals(tensor,function,tensor.getType());
    }

    /**
     * Get the tensor marginals for a double tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     * 
     * @param outType
     *        The type of the output tensors the marginals will be placed in.
     *
     * @return the tensor marginals.
     *
     * @throws IllegalArgumentException if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<? extends Tensor<? extends Number>> getMarginals(DoubleTensor tensor, MarginalFunction function, JavaType outType) {
        return getMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the tensor marginals for a float tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensors the marginals will be placed in.
     *
     * @return the tensor marginals.
     *
     * @throws IllegalArgumentException if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<? extends Tensor<? extends Number>> getMarginals(FloatTensor tensor, MarginalFunction function, JavaType outType) {
        return getMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the tensor marginals for a long tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensors the marginals will be placed in.
     *
     * @return the tensor marginals.
     *
     * @throws IllegalArgumentException if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<? extends Tensor<? extends Number>> getMarginals(LongTensor tensor, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getMarginalsGeneral(TensorUtil.asDoubleTensor(tensor),function,outType);
        else
            return getMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the tensor marginals for a int tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensors the marginals will be placed in.
     *
     * @return the tensor marginals.
     *
     * @throws IllegalArgumentException if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<? extends Tensor<? extends Number>> getMarginals(IntTensor tensor, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getMarginalsGeneral(TensorUtil.asDoubleTensor(tensor),function,outType);
        else
            return getMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the tensor marginals for a short tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensors the marginals will be placed in.
     *
     * @return the tensor marginals.
     *
     * @throws IllegalArgumentException if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<? extends Tensor<? extends Number>> getMarginals(ShortTensor tensor, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getMarginalsGeneral(TensorUtil.asDoubleTensor(tensor),function,outType);
        else
            return getMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the tensor marginals for a byte tensor.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensors the marginals will be placed in.
     *
     * @return the tensor marginals.
     *
     * @throws IllegalArgumentException if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public List<? extends Tensor<? extends Number>> getMarginals(ByteTensor tensor, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getMarginalsGeneral(TensorUtil.asDoubleTensor(tensor),function,outType);
        else
            return getMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get a tensor marginal for a double tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public DoubleTensor getMarginal(DoubleTensor tensor, int acrossDimension, MarginalFunction function) {
        return (DoubleTensor) getMarginal(tensor,acrossDimension,function,tensor.getType());
    }

    /**
     * Get a tensor marginal for a float tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public FloatTensor getMarginal(FloatTensor tensor, int acrossDimension, MarginalFunction function) {
        return (FloatTensor) getMarginal(tensor,acrossDimension,function,tensor.getType());
    }

    /**
     * Get a tensor marginal for a long tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public LongTensor getMarginal(LongTensor tensor, int acrossDimension, MarginalFunction function) {
        return (LongTensor) getMarginal(tensor,acrossDimension,function,tensor.getType());
    }

    /**
     * Get a tensor marginal for a int tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public IntTensor getMarginal(IntTensor tensor, int acrossDimension, MarginalFunction function) {
        return (IntTensor) getMarginal(tensor,acrossDimension,function,tensor.getType());
    }

    /**
     * Get a tensor marginal for a short tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public ShortTensor getMarginal(ShortTensor tensor, int acrossDimension, MarginalFunction function) {
        return (ShortTensor) getMarginal(tensor,acrossDimension,function,tensor.getType());
    }

    /**
     * Get a tensor marginal for a byte tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public ByteTensor getMarginal(ByteTensor tensor, int acrossDimension, MarginalFunction function) {
        return (ByteTensor) getMarginal(tensor,acrossDimension,function,tensor.getType());
    }

    /**
     * Get a tensor marginal for a double tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     * 
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     * 
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public Tensor<? extends Number> getMarginal(DoubleTensor tensor, int acrossDimension, MarginalFunction function, JavaType outType) {
        return  getMarginalGeneral(tensor,acrossDimension,function,outType);
    }

    /**
     * Get a tensor marginal for a float tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.      
     * 
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public Tensor<? extends Number> getMarginal(FloatTensor tensor, int acrossDimension, MarginalFunction function, JavaType outType) {
        return  getMarginalGeneral(tensor,acrossDimension,function,outType);
    }

    /**
     * Get a tensor marginal for a long tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.      
     * 
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public Tensor<? extends Number> getMarginal(LongTensor tensor, int acrossDimension, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return  getMarginalGeneral(TensorUtil.asDoubleTensor(tensor),acrossDimension,function,outType);
        else
            return  getMarginalGeneral(tensor,acrossDimension,function,outType);
    }

    /**
     * Get a tensor marginal for a int tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.  
     * 
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public Tensor<? extends Number> getMarginal(IntTensor tensor, int acrossDimension, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return  getMarginalGeneral(TensorUtil.asDoubleTensor(tensor),acrossDimension,function,outType);
        else
            return  getMarginalGeneral(tensor,acrossDimension,function,outType);
    }

    /**
     * Get a tensor marginal for a short tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.   
     * 
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public Tensor<? extends Number> getMarginal(ShortTensor tensor, int acrossDimension, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return  getMarginalGeneral(TensorUtil.asDoubleTensor(tensor),acrossDimension,function,outType);
        else
            return  getMarginalGeneral(tensor,acrossDimension,function,outType);
    }

    /**
     * Get a tensor marginal for a byte tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.  
     * 
     * @param acrossDimension
     *        The (non-constant) dimension across which the marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the tensor marginal across {@code accrossDimension}.
     *
     * @throws IllegalArgumentException if {@code acrossDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //the type is enforced by the getType call
    public Tensor<? extends Number> getMarginal(ByteTensor tensor, int acrossDimension, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return  getMarginalGeneral(TensorUtil.asDoubleTensor(tensor),acrossDimension,function,outType);
        else
            return  getMarginalGeneral(tensor,acrossDimension,function,outType);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of a double tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public DoubleVector getCollapsedMarginal(DoubleTensor tensor, int marginalDimension, MarginalFunction function) {
        return (DoubleVector) getCollapsedMarginal(tensor,marginalDimension,function,JavaType.DOUBLE);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of a float tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public FloatVector getCollapsedMarginal(FloatTensor tensor, int marginalDimension, MarginalFunction function) {
        return (FloatVector) getCollapsedMarginal(tensor,marginalDimension,function,JavaType.FLOAT);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of a long tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public LongVector getCollapsedMarginal(LongTensor tensor, int marginalDimension, MarginalFunction function) {
        return (LongVector) getCollapsedMarginal(tensor,marginalDimension,function,JavaType.LONG);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of an int tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public IntVector getCollapsedMarginal(IntTensor tensor, int marginalDimension, MarginalFunction function) {
        return (IntVector) getCollapsedMarginal(tensor,marginalDimension,function,JavaType.INT);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of a short tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public ShortVector getCollapsedMarginal(ShortTensor tensor, int marginalDimension, MarginalFunction function) {
        return (ShortVector) getCollapsedMarginal(tensor,marginalDimension,function,JavaType.SHORT);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of a byte tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}.
     */
    public ByteVector getCollapsedMarginal(ByteTensor tensor, int marginalDimension, MarginalFunction function) {
        return (ByteVector) getCollapsedMarginal(tensor,marginalDimension,function,JavaType.BYTE);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of a double tensor. Note that certain values of
     * the output type may result in a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    public Vector<? extends Number> getCollapsedMarginal(DoubleTensor tensor, int marginalDimension, MarginalFunction function, JavaType outType) {
        return getCollapsedMarginalGeneral(tensor,marginalDimension,function,outType);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of a float tensor. Note that certain values of
     * the output type may result in a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    public Vector<? extends Number> getCollapsedMarginal(FloatTensor tensor, int marginalDimension, MarginalFunction function, JavaType outType) {
        return getCollapsedMarginalGeneral(tensor,marginalDimension,function,outType);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of a long tensor. Note that certain values of
     * the output type may result in a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    public Vector<? extends Number> getCollapsedMarginal(LongTensor tensor, int marginalDimension, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getCollapsedMarginalGeneral(TensorUtil.asDoubleTensor(tensor),marginalDimension,function,outType);
        else
            return getCollapsedMarginalGeneral(tensor,marginalDimension,function,outType);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of an int tensor. Note that certain values of
     * the output type may result in a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    public Vector<? extends Number> getCollapsedMarginal(IntTensor tensor, int marginalDimension, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getCollapsedMarginalGeneral(TensorUtil.asDoubleTensor(tensor),marginalDimension,function,outType);
        else
            return getCollapsedMarginalGeneral(tensor,marginalDimension,function,outType);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of a short tensor. Note that certain values of
     * the output type may result in a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    public Vector<? extends Number> getCollapsedMarginal(ShortTensor tensor, int marginalDimension, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getCollapsedMarginalGeneral(TensorUtil.asDoubleTensor(tensor),marginalDimension,function,outType);
        else
            return getCollapsedMarginalGeneral(tensor,marginalDimension,function,outType);
    }

    /**
     * Get the collapsed tensor marginal for the specified dimension of a byte tensor. Note that certain values of
     * the output type may result in a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param marginalDimension
     *        The dimension the collapsed marginal will be calculated for.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginal will be placed in.
     *
     * @return the collapsed tensor marginal for dimension {@code marginalDimension} on {@code tensor}.
     *
     * @throws IllegalArgumentException if {@code marginalDimension} is less than zero or greater than or equal to the
     *                                  size of {@code tensor}, or if {@code outType} is a non-numeric type.
     */
    public Vector<? extends Number> getCollapsedMarginal(ByteTensor tensor, int marginalDimension, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getCollapsedMarginalGeneral(TensorUtil.asDoubleTensor(tensor),marginalDimension,function,outType);
        else
            return getCollapsedMarginalGeneral(tensor,marginalDimension,function,outType);
    }

    /**
     * Get the collapsed tensor marginals for a double tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginals.
     */
    @SuppressWarnings("unchecked") //correct cast on the list
    public List<DoubleVector> getCollapsedMarginals(DoubleTensor tensor, MarginalFunction function) {
        return (List<DoubleVector>) getCollapsedMarginals(tensor,function,JavaType.DOUBLE);
    }

    /**
     * Get the collapsed tensor marginals for a float tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginals.
     */
    @SuppressWarnings("unchecked") //correct cast on the list
    public List<FloatVector> getCollapsedMarginals(FloatTensor tensor, MarginalFunction function) {
        return (List<FloatVector>) getCollapsedMarginals(tensor,function,JavaType.FLOAT);
    }

    /**
     * Get the collapsed tensor marginals for a long tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginals.
     */
    @SuppressWarnings("unchecked") //correct cast on the list
    public List<LongVector> getCollapsedMarginals(LongTensor tensor, MarginalFunction function) {
        return (List<LongVector>) getCollapsedMarginals(tensor,function,JavaType.LONG);
    }

    /**
     * Get the collapsed tensor marginals for an int tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginals.
     */
    @SuppressWarnings("unchecked") //correct cast on the list
    public List<IntVector> getCollapsedMarginals(IntTensor tensor, MarginalFunction function) {
        return (List<IntVector>) getCollapsedMarginals(tensor,function,JavaType.INT);
    }

    /**
     * Get the collapsed tensor marginals for a short tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginals.
     */
    @SuppressWarnings("unchecked") //correct cast on the list
    public List<ShortVector> getCollapsedMarginals(ShortTensor tensor, MarginalFunction function) {
        return (List<ShortVector>) getCollapsedMarginals(tensor,function,JavaType.SHORT);
    }

    /**
     * Get the collapsed tensor marginals for a byte tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the collapsed tensor marginals.
     */
    @SuppressWarnings("unchecked") //correct cast on the list
    public List<ByteVector> getCollapsedMarginals(ByteTensor tensor, MarginalFunction function) {
        return (List<ByteVector>) getCollapsedMarginals(tensor,function,JavaType.BYTE);
    }

    /**
     * Get the collapsed tensor marginals for a double tensor. Note that certain values of the output type may result in
     * a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginals will be placed in.
     *
     * @return the collapsed tensor marginals.
     *
     * @throws IllegalArgumentException iif {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //correct cast on the list
    public List<? extends Vector<? extends Number>> getCollapsedMarginals(DoubleTensor tensor, MarginalFunction function, JavaType outType) {
        return getCollapsedMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the collapsed tensor marginals for a float tensor. Note that certain values of the output type may result in
     * a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginals will be placed in.
     *
     * @return the collapsed tensor marginals.
     *
     * @throws IllegalArgumentException iif {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //correct cast on the list
    public List<? extends Vector<? extends Number>> getCollapsedMarginals(FloatTensor tensor, MarginalFunction function, JavaType outType) {
        return getCollapsedMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the collapsed tensor marginals for a long tensor. Note that certain values of the output type may result in
     * a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginals will be placed in.
     *
     * @return the collapsed tensor marginals.
     *
     * @throws IllegalArgumentException iif {@code outType} is a non-numeric type.
     */
    @SuppressWarnings("unchecked") //correct cast on the list
    public List<? extends Vector<? extends Number>> getCollapsedMarginals(LongTensor tensor, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getCollapsedMarginalsGeneral(TensorUtil.asDoubleTensor(tensor),function,outType);
        else
            return getCollapsedMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the collapsed tensor marginals for an int tensor. Note that certain values of the output type may result in
     * a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginals will be placed in.
     *
     * @return the collapsed tensor marginals.
     *
     * @throws IllegalArgumentException iif {@code outType} is a non-numeric type.
     */
    public List<? extends Vector<? extends Number>> getCollapsedMarginals(IntTensor tensor, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getCollapsedMarginalsGeneral(TensorUtil.asDoubleTensor(tensor),function,outType);
        else
            return getCollapsedMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the collapsed tensor marginals for a short tensor. Note that certain values of the output type may result in
     * a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginals will be placed in.
     *
     * @return the collapsed tensor marginals.
     *
     * @throws IllegalArgumentException iif {@code outType} is a non-numeric type.
     */
    public List<? extends Vector<? extends Number>> getCollapsedMarginals(ShortTensor tensor, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getCollapsedMarginalsGeneral(TensorUtil.asDoubleTensor(tensor),function,outType);
        else
            return getCollapsedMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the collapsed tensor marginals for a byte tensor. Note that certain values of the output type may result in
     * a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginals will be calculated.
     *
     * @param function
     *        The function from which the marginals will be calculated.
     *
     * @param outType
     *        The type of the output tensor the marginals will be placed in.
     *
     * @return the collapsed tensor marginals.
     *
     * @throws IllegalArgumentException iif {@code outType} is a non-numeric type.
     */
    public List<? extends Vector<? extends Number>> getCollapsedMarginals(ByteTensor tensor, MarginalFunction function, JavaType outType) {
        if (outType == JavaType.DOUBLE || outType == JavaType.FLOAT)
            return getCollapsedMarginalsGeneral(TensorUtil.asDoubleTensor(tensor),function,outType);
        else
            return getCollapsedMarginalsGeneral(tensor,function,outType);
    }

    /**
     * Get the full tensor marginal for a double tensor. A full tensor marginal is the marginal function applied across
     * all values in the tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the full tensor marginal.
     */
    public double getFullMarginal(DoubleTensor tensor, MarginalFunction function) {
        long size = TensorUtil.getElementCount(tensor);
        DoubleMarginalFunction f = function.getDoubleMarginalFunction();
        if (size == 1 && tensor.size() == 0)
            f.processNextValue(tensor.getValue(new int[0]));
        else if (function instanceof ConcurrentMarginalFunction || size > MARGINAL_ACTION_PARALLEL_SIZE_LIMIT)
            ForkJoinPoolFactory.getForkJoinPool().invoke(new DoubleTensorMarginalAction(tensor,size,f));
        else
            for (int[] indices : IterableAbacus.getIterableAbacus(tensor.getDimensions()))
                f.processNextValue(tensor.getValue(indices));
        return f.getMarginal();
    }

    /**
     * Get the full tensor marginal for a float tensor. A full tensor marginal is the marginal function applied across
     * all values in the tensor. Note that certain values of the output type may result in a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the full tensor marginal.
     */
    public float getFullMarginal(FloatTensor tensor, MarginalFunction function) {
        long size = TensorUtil.getElementCount(tensor);
        DoubleMarginalFunction f = function.getDoubleMarginalFunction();
        if (size == 1 && tensor.size() == 0)
            f.processNextValue(tensor.getValue(new int[0]));
        else if (function instanceof ConcurrentMarginalFunction || size > MARGINAL_ACTION_PARALLEL_SIZE_LIMIT)
            ForkJoinPoolFactory.getForkJoinPool().invoke(new DoubleTensorMarginalAction(TensorUtil.asDoubleTensor(tensor),size,f));
        else
            for (int[] indices : IterableAbacus.getIterableAbacus(tensor.getDimensions()))
                f.processNextValue(tensor.getValue(indices));
        return (float) f.getMarginal();
    }

    /**
     * Get the full tensor marginal for a long tensor. A full tensor marginal is the marginal function applied across
     * all values in the tensor.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the full tensor marginal.
     */
    public long getFullMarginal(LongTensor tensor, MarginalFunction function) {
        long size = TensorUtil.getElementCount(tensor);
        LongMarginalFunction f = function.getLongMarginalFunction();
        if (size == 1 && tensor.size() == 0)
            f.processNextValue(tensor.getValue(new int[0]));
        else if (function instanceof ConcurrentMarginalFunction || size > MARGINAL_ACTION_PARALLEL_SIZE_LIMIT)
            ForkJoinPoolFactory.getForkJoinPool().invoke(new LongTensorMarginalAction(tensor,size,f));
        else
            for (int[] indices : IterableAbacus.getIterableAbacus(tensor.getDimensions()))
                f.processNextValue(tensor.getValue(indices));
        return f.getMarginal();
    } 

    /**
     * Get the full tensor marginal for an int tensor. A full tensor marginal is the marginal function applied across
     * all values in the tensor. Note that certain values of the output type may result in a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the full tensor marginal.
     */
    public int getFullMarginal(IntTensor tensor, MarginalFunction function) {
        long size = TensorUtil.getElementCount(tensor);
        LongMarginalFunction f = function.getLongMarginalFunction();
        if (size == 1 && tensor.size() == 0)
            f.processNextValue(tensor.getValue(new int[0]));
        else if (function instanceof ConcurrentMarginalFunction || size > MARGINAL_ACTION_PARALLEL_SIZE_LIMIT)
            ForkJoinPoolFactory.getForkJoinPool().invoke(new LongTensorMarginalAction(TensorUtil.asLongTensor(tensor),size,f));
        else
            for (int[] indices : IterableAbacus.getIterableAbacus(tensor.getDimensions()))
                f.processNextValue(tensor.getValue(indices));
        return (int) f.getMarginal();
    }   

    /**
     * Get the full tensor marginal for a short tensor. A full tensor marginal is the marginal function applied across
     * all values in the tensor. Note that certain values of the output type may result in a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the full tensor marginal.
     */
    public short getFullMarginal(ShortTensor tensor, MarginalFunction function) {
        long size = TensorUtil.getElementCount(tensor);
        LongMarginalFunction f = function.getLongMarginalFunction();
        if (size == 1 && tensor.size() == 0)
            f.processNextValue(tensor.getValue(new int[0]));
        else if (function instanceof ConcurrentMarginalFunction || size > MARGINAL_ACTION_PARALLEL_SIZE_LIMIT)
            ForkJoinPoolFactory.getForkJoinPool().invoke(new LongTensorMarginalAction(TensorUtil.asLongTensor(tensor),size,f));
        else
            for (int[] indices : IterableAbacus.getIterableAbacus(tensor.getDimensions()))
                f.processNextValue(tensor.getValue(indices));
        return (short) f.getMarginal();
    } 

    /**
     * Get the full tensor marginal for a byte tensor. A full tensor marginal is the marginal function applied across
     * all values in the tensor. Note that certain values of the output type may result in a loss of precision.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @return the full tensor marginal.
     */
    public byte getFullMarginal(ByteTensor tensor, MarginalFunction function) {
        long size = TensorUtil.getElementCount(tensor);
        LongMarginalFunction f = function.getLongMarginalFunction();
        if (size == 1 && tensor.size() == 0)
            f.processNextValue(tensor.getValue(new int[0]));
        else if (function instanceof ConcurrentMarginalFunction || size > MARGINAL_ACTION_PARALLEL_SIZE_LIMIT)
            ForkJoinPoolFactory.getForkJoinPool().invoke(new LongTensorMarginalAction(TensorUtil.asLongTensor(tensor),size,f));
        else
            for (int[] indices : IterableAbacus.getIterableAbacus(tensor.getDimensions()))
                f.processNextValue(tensor.getValue(indices));
        return (byte) f.getMarginal();
    }

    /**
     * Get the full tensor marginal for a numeric tensor. A full tensor marginal is the marginal function applied across
     * all values in the tensor. Note that certain values of the output type may result in a loss of precision.
     *
     * @param <N>
     *        The type of the output.
     *
     * @param tensor
     *        The tensor whose marginal will be calculated.
     *
     * @param function
     *        The function from which the marginal will be calculated.
     *
     * @param type
     *        The type of the output. This should correspond to the type of {@code N} (or it's primitive equivalent);
     *
     * @return the full tensor marginal.
     */
    @SuppressWarnings("unchecked") //if N doesn't match type, then a user definition error
    public <N extends Number> N getFullMarginal(Tensor<? extends Number> tensor, MarginalFunction function, JavaType type) {
        switch (type) {
            case FLOAT : return (N) (Float) getFullMarginal(TensorUtil.asFloatTensor(tensor),function);
            case LONG : return (N) (Long) getFullMarginal(TensorUtil.asLongTensor(tensor),function);
            case INT : return (N) (Integer) getFullMarginal(TensorUtil.asIntTensor(tensor),function);
            case SHORT : return (N) (Short) getFullMarginal(TensorUtil.asShortTensor(tensor),function);
            case BYTE : return (N) (Byte) getFullMarginal(TensorUtil.asByteTensor(tensor),function);
            default : return (N) (Double) getFullMarginal(TensorUtil.asDoubleTensor(tensor),function);
        }
    }

    private static final long MARGINAL_ACTION_PARALLEL_SIZE_LIMIT = 5000L;
    private abstract static class TensorMarginalAction extends DnCRecursiveAction {
        final Abacus abacus;

        private TensorMarginalAction(Abacus abacus, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.abacus = abacus.freshClone();
        }

        private TensorMarginalAction(Tensor tensor, long size) {
            super(0,size);
            abacus = new Abacus(tensor.getDimensions());
        }

        abstract protected void applyMarginal(int[] indices);

        @Override
        protected void computeAction(long position, long length) {
            abacus.setAbacusAtPosition(position);
            while (length-- > 0)
                applyMarginal(abacus.next());
        }

        @Override
        protected boolean continueDividing(long newLength) {
            return newLength > MARGINAL_ACTION_PARALLEL_SIZE_LIMIT && getSurplusQueuedTaskCount() <= 3;
        }
    }

    private static class DoubleTensorMarginalAction extends TensorMarginalAction {
        private static final long serialVersionUID = 1584475016249168039L;

        private final DoubleMarginalFunction function;
        private final DoubleTensor tensor;

        private DoubleTensorMarginalAction(DoubleMarginalFunction function, DoubleTensor tensor, Abacus abacus, long start, long length, DnCRecursiveAction next) {
            super(abacus,start,length,next);
            this.function = function;
            this.tensor = tensor;
        }

        private DoubleTensorMarginalAction(DoubleTensor tensor, long size, DoubleMarginalFunction function) {
            super(tensor,size);
            this.function = function;
            this.tensor = tensor;
        }

        protected void applyMarginal(int[] indices) {
            function.processNextValue(tensor.getValue(indices));
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new DoubleTensorMarginalAction(function,tensor,abacus,start,length,next);
        }
    }
    
    private static class LongTensorMarginalAction extends TensorMarginalAction {
        private static final long serialVersionUID = -5523644650882425689L;

        private final LongMarginalFunction function;
        private final LongTensor tensor;

        private LongTensorMarginalAction(LongMarginalFunction function, LongTensor tensor, Abacus abacus, long start, long length, DnCRecursiveAction next) {
            super(abacus,start,length,next);
            this.function = function;
            this.tensor = tensor;
        }

        private LongTensorMarginalAction(LongTensor tensor, long size, LongMarginalFunction function) {
            super(tensor,size);
            this.function = function;
            this.tensor = tensor;
        }

        protected void applyMarginal(int[] indices) {
            function.processNextValue(tensor.getValue(indices));
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new LongTensorMarginalAction(function,tensor,abacus,start,length,next);
        }
    }
    
    
    private LongVector fillCollapsedMarginal(LongTensor tensor, LongVector result, int marginalDimension, MarginalFunction function) {
        if (tensor.size() == 0) {
            //scalar
            LongMarginalFunction mf = function.getLongMarginalFunction();
            mf.processNextValue(tensor.getCell());
            result.setCell(mf.getMarginal(),0);
            return result;
        }
        LongMarginalFunction[] mfs = new LongMarginalFunction[tensor.size(marginalDimension)];
        for (int i : range(mfs.length)) 
            mfs[i] = function.getLongMarginalFunction();
        for (int[] dim : IterableAbacus.getIterableAbacus(tensor.getDimensions())) 
            mfs[dim[marginalDimension]].processNextValue(tensor.getCell(dim));  
        for (int i : range(mfs.length)) 
            result.setCell(mfs[i].getMarginal(),i);
        return result;
    }     
    
    private DoubleVector fillCollapsedMarginal(DoubleTensor tensor, DoubleVector result, int marginalDimension, MarginalFunction function) {
        if (tensor.size() == 0) {
            //scalar
            DoubleMarginalFunction mf = function.getDoubleMarginalFunction();
            mf.processNextValue(tensor.getCell());
            result.setCell(mf.getMarginal(),0);
            return result;
        }
        DoubleMarginalFunction[] mfs = new DoubleMarginalFunction[tensor.size(marginalDimension)];
        for (int i : range(mfs.length)) 
            mfs[i] = function.getDoubleMarginalFunction();
        for (int[] dim : IterableAbacus.getIterableAbacus(tensor.getDimensions())) 
            mfs[dim[marginalDimension]].processNextValue(tensor.getCell(dim));  
        for (int i : range(mfs.length)) 
            result.setCell(mfs[i].getMarginal(),i);
        return result;
    } 
    
    private List<LongVector> fillCollapsedMarginals(LongTensor tensor, List<LongVector> result, MarginalFunction function) {
        if (tensor.size() == 0) {
            //scalar
            LongMarginalFunction mf = function.getLongMarginalFunction();
            mf.processNextValue(tensor.getCell());
            result.get(0).setCell(mf.getMarginal(),0);
            return result;
        }
        LongMarginalFunction[][] mfs = new LongMarginalFunction[tensor.size()][];
        for (int i : range(mfs.length)) {
            int size = tensor.size(i);
            mfs[i] = new LongMarginalFunction[size];
            for (int j : range(size))
                mfs[i][j] = function.getLongMarginalFunction();
        }
        for (int[] dim : IterableAbacus.getIterableAbacus(tensor.getDimensions())) {
            long cellValue = tensor.getCell(dim);
            for (int i : range(dim.length))
                mfs[i][dim[i]].processNextValue(cellValue);
        }
        for (int i : range(result.size())) {
            LongVector v = result.get(i);
            for (int j : range(v.size(0)))
                v.setCell(mfs[i][j].getMarginal(),j);
        }
        return result;
    }  
    
    private List<DoubleVector> fillCollapsedMarginals(DoubleTensor tensor, List<DoubleVector> result, MarginalFunction function) {
        if (tensor.size() == 0) {
            //scalar
            DoubleMarginalFunction mf = function.getDoubleMarginalFunction();
            mf.processNextValue(tensor.getCell());
            result.get(0).setCell(mf.getMarginal(),0);
            return result;
        }
        DoubleMarginalFunction[][] mfs = new DoubleMarginalFunction[tensor.size()][];
        for (int i : range(mfs.length)) {
            int size = tensor.size(i);
            mfs[i] = new DoubleMarginalFunction[size];
            for (int j : range(size))
                mfs[i][j] = function.getDoubleMarginalFunction();
        }
        for (int[] dim : IterableAbacus.getIterableAbacus(tensor.getDimensions())) {
            double cellValue = tensor.getCell(dim);
            for (int i : range(dim.length))
                mfs[i][dim[i]].processNextValue(cellValue);
        }
        for (int i : range(result.size())) {
            DoubleVector v = result.get(i);
            for (int j : range(v.size(0)))
                v.setCell(mfs[i][j].getMarginal(),j);
        }
        return result;
    }

    @SuppressWarnings("unchecked") //tensor was wrapped intentionally, and is intentionally unwrapped
    private <T extends Number> Vector<? extends Number> getCollapsedMarginalGeneral(Tensor<T> tensor, int marginalDim, MarginalFunction function, JavaType outType) {
        int tSize = tensor.size();
        if (marginalDim < 0 || marginalDim >= tSize)
            throw new IllegalArgumentException(String.format("Marginal dimension (%d) out of bounds for tensor of size %d.",marginalDim,tSize));
        if (!outType.isNumeric())
            throw new IllegalArgumentException("Marginal output java type must be numeric: " + outType);
        JavaType inType = tensor.getType();
        Tensor<? extends Number> nt;
        if (tSize > 0) {
            int size = tensor.size(marginalDim);
            switch (outType) {
                case DOUBLE : nt = factory.doubleVector(size); break;
                case FLOAT : nt = factory.floatVector(size); break;
                case LONG : nt = factory.longVector(size); break;
                case INT : nt = factory.intVector(size); break;
                case SHORT : nt = factory.shortVector(size); break;
                case BYTE : nt = factory.byteVector(size); break;
                default : throw new IllegalStateException("Shouldn't be here.");
            }
        } else {
            //scalar
            switch (outType) {
                case DOUBLE : nt = factory.doubleVector(1); break;
                case FLOAT : nt = factory.floatVector(1); break;
                case LONG : nt = factory.longVector(1); break;
                case INT : nt = factory.intVector(1); break;
                case SHORT : nt = factory.shortVector(1); break;
                case BYTE : nt = factory.byteVector(1); break;
                default : throw new IllegalStateException("Shouldn't be here.");
            }
        }
        switch (inType) {
            case DOUBLE :
            case FLOAT : fillCollapsedMarginal(TensorUtil.asDoubleTensor(tensor),(DoubleVector) TensorUtil.asDoubleTensor(nt),marginalDim,function); break;
            case LONG :
            case INT :
            case SHORT :
            case BYTE : fillCollapsedMarginal(TensorUtil.asLongTensor(tensor),(LongVector) TensorUtil.asLongTensor(nt),marginalDim,function); break;
        }
        return (Vector<? extends Number>) (nt instanceof WrappedTensor ? ((WrappedTensor<Tensor<? extends Number>>) nt).getWrappedTensor() : (Tensor<? extends Number>) nt);
    }

    private <T extends Number> List<Vector<? extends Number>> getCollapsedMarginalsGeneral(Tensor<T> tensor, MarginalFunction function, JavaType outType) {
        if (!outType.isNumeric())
            throw new IllegalArgumentException("Marginal output java type must be numeric: " + outType);
        JavaType inType = tensor.getType();
        List<Vector<? extends Number>> nt = new LinkedList<Vector<? extends Number>>();
        if (tensor.size() > 0) {
            for (int i : range(tensor.size())) {
                int size = tensor.size(i);
                switch (outType) {
                    case DOUBLE : nt.add(factory.doubleVector(size)); break;
                    case FLOAT : nt.add(factory.floatVector(size)); break;
                    case LONG : nt.add(factory.longVector(size)); break;
                    case INT : nt.add(factory.intVector(size)); break;
                    case SHORT : nt.add(factory.shortVector(size)); break;
                    case BYTE : nt.add(factory.byteVector(size)); break;
                    default : throw new IllegalStateException("Shouldn't be here.");
                }
            }
        } else {
            //scalar
            switch (outType) {
                case DOUBLE : nt.add(factory.doubleVector(1)); break;
                case FLOAT : nt.add(factory.floatVector(1)); break;
                case LONG : nt.add(factory.longVector(1)); break;
                case INT : nt.add(factory.intVector(1)); break;
                case SHORT : nt.add(factory.shortVector(1)); break;
                case BYTE : nt.add(factory.byteVector(1)); break;
                default : throw new IllegalStateException("Shouldn't be here.");
            }
        }

        switch (inType) {
            case DOUBLE :
            case FLOAT : {
                List<DoubleVector> t = new LinkedList<DoubleVector>();
                for (Vector<? extends Number> v : nt)
                    t.add((DoubleVector) TensorUtil.asDoubleTensor(v));
                fillCollapsedMarginals(TensorUtil.asDoubleTensor(tensor),t,function); break;
            }
            case LONG :
            case INT :
            case SHORT :
            case BYTE :  {
                List<LongVector> t = new LinkedList<LongVector>();
                for (Vector<? extends Number> v : nt)
                    t.add((LongVector) TensorUtil.asLongTensor(v));
                fillCollapsedMarginals(TensorUtil.asLongTensor(tensor),t,function); break;
            }
        }
        return nt;
    }


    @SuppressWarnings("unchecked") //wrapped tensors are wrapped because I check them
    private <T extends Number> Tensor<? extends Number> getMarginalGeneral(Tensor<T> tensor, int marginalDim, MarginalFunction function, JavaType outType) {int tSize = tensor.size();
        if (marginalDim < 0 || marginalDim >= tSize)
            throw new IllegalArgumentException(String.format("Marginal dimension (%d) out of bounds for tensor of size %d.",marginalDim,tSize));
        if (!outType.isNumeric())
            throw new IllegalArgumentException("Marginal output java type must be numeric: " + outType);
        JavaType inType = tensor.getType();
        Tensor<? extends Number> nt;
        if (tSize > 0) {
            int[] dims = tensor.getDimensions();
            int[] size = new int[dims.length-1];
            System.arraycopy(dims,0,size,0,marginalDim);
            if (marginalDim < size.length)
                System.arraycopy(dims,marginalDim+1,size,marginalDim,size.length-marginalDim);
            switch (outType) {
                case DOUBLE : nt = factory.doubleTensor(size); break;
                case FLOAT : nt = factory.floatTensor(size); break;
                case LONG : nt = factory.longTensor(size); break;
                case INT : nt = factory.intTensor(size); break;
                case SHORT : nt = factory.shortTensor(size); break;
                case BYTE : nt = factory.byteTensor(size); break;
                default : throw new IllegalStateException("Shouldn't be here.");
            }
            switch (inType) {
                case DOUBLE :
                case FLOAT : fillMarginals(TensorUtil.asDoubleTensor(tensor),function,TensorUtil.asDoubleTensor(nt),marginalDim); break;
                case LONG :
                case INT :
                case SHORT :
                case BYTE : fillMarginals(TensorUtil.asLongTensor(tensor),function,TensorUtil.asLongTensor(nt),marginalDim);
            }
        } else {
            //scalar
            switch (outType) {
                case DOUBLE : nt = factory.doubleTensor(); break;
                case FLOAT : nt = factory.floatTensor(); break;
                case LONG : nt = factory.longTensor(); break;
                case INT : nt = factory.intTensor(); break;
                case SHORT : nt = factory.shortTensor(); break;
                case BYTE : nt = factory.byteTensor(); break;
                default : throw new IllegalStateException("Shouldn't be here.");
            }
            switch (inType) {
                case DOUBLE :
                case FLOAT : fillMarginals(TensorUtil.asDoubleTensor(tensor),function,TensorUtil.asDoubleTensor(nt),-1); break;
                case LONG :
                case INT :
                case SHORT :
                case BYTE : fillMarginals(TensorUtil.asLongTensor(tensor),function,TensorUtil.asLongTensor(nt),-1);
            }
        }
        return nt instanceof WrappedTensor ? ((WrappedTensor<Tensor<T>>) nt).getWrappedTensor() : (Tensor<T>) nt;
    }

    @SuppressWarnings("unchecked") //wrapped tensors are wrapped because I check them
    private <T extends Number> List getMarginalsGeneral(Tensor<T> tensor, MarginalFunction function, JavaType outType) {
        List<Tensor<T>> marginals = new ArrayList<Tensor<T>>(tensor.size());
        int s = tensor.size();
        for (int i : range(s == 0 ? 1 : s)) {
            marginals.add((Tensor<T>) getMarginalGeneral(tensor,i,function,outType));
        }
        return marginals;
    }
    
    private void fillMarginals(DoubleTensor tensor, MarginalFunction function, DoubleTensor result, int cycleDimension) {
        int size = tensor.size();
        if (size > 0) {
            int[] point = new int[size-1];
            Abacus ab = new Abacus(tensor.getDimensions());
            for (int[] pt : new IterableAbacus(ab)) {
                DoubleMarginalFunction mf = function.getDoubleMarginalFunction();
                for (int i : range(tensor.size(cycleDimension))) {
                    pt[cycleDimension] = i;
                    mf.processNextValue(tensor.getCell(pt));
                }
                System.arraycopy(pt,0,point,0,cycleDimension);
                if (cycleDimension < pt.length-1)
                    System.arraycopy(pt,cycleDimension+1,point,cycleDimension,point.length - cycleDimension);
                result.setCell(mf.getMarginal(),point);
            }
        } else {
            //scalar
            DoubleMarginalFunction mf = function.getDoubleMarginalFunction();
            mf.processNextValue(tensor.getCell());
            result.setCell(mf.getMarginal());
        }
    }               
    
    private void fillMarginals(LongTensor tensor, MarginalFunction function, LongTensor result, int cycleDimension) {
        int size = tensor.size();
        if (size > 0) {
            int[] point = new int[size-1];
            Abacus ab = new Abacus(tensor.getDimensions());
            for (int[] pt : new IterableAbacus(ab)) {
                LongMarginalFunction mf = function.getLongMarginalFunction();
                for (int i : range(tensor.size(cycleDimension))) {
                    pt[cycleDimension] = i;
                    mf.processNextValue(tensor.getCell(pt));
                }
                System.arraycopy(pt,0,point,0,cycleDimension);
                if (cycleDimension < pt.length-1)
                    System.arraycopy(pt,cycleDimension+1,point,cycleDimension,point.length - cycleDimension);
                result.setCell(mf.getMarginal(),point);
            }
        } else {
            //scalar
            LongMarginalFunction mf = function.getLongMarginalFunction();
            mf.processNextValue(tensor.getCell());
            result.setCell(mf.getMarginal());
        }
    }

    /**
     * The {@code MarginalFunction} interface provides the basis for all numeric marginal functions.  It provides
     * marginal functions for both {@code double}s and {@code long}s, which can themselves be applied to all real or
     * integer tensors.
     */
    public static interface MarginalFunction {
        /**
         * Get the function used to calculate the tensor marginals acting on {@code double}s.
         *
         * @return the marginal function for {@code double}s.
         */
        DoubleMarginalFunction getDoubleMarginalFunction();

        /**
         * Get the function used to calculate the tensor marginals acting on {@code long}s.
         *
         * @return the marginal function for {@code long}s.
         */
        LongMarginalFunction getLongMarginalFunction();
    }

    /**
     * The {@code ConcurrentMarginalFunction} interface indicates a {@code MarginalFunction} which can be calculated
     * concurrently. That is, its {@code processNextValue} methods can be safely executed in parallel before the
     * {@code getMarginal} method is called.
     */
    public static interface ConcurrentMarginalFunction extends MarginalFunction { }

    /**
     * The {@code DoubleMarginalFunction} provides the framework for calculating marginals based on {@code double}s.
     * The marginal is calculated in two steps:
     * <ol>
     *    <li>Each value for the marginal is processed (via {@code processNextValue(double)}).</li>
     *    <li>The final marginal value is calculated and returned (via {@code getMarginal()}).</li>
     * </ol>
     * This framework keeps the marginal function independent of the size of the marginal elements.
     */
    public static interface DoubleMarginalFunction {
        /**
         * Process the next value in the marginal calculation.
         *
         * @param value
         *        The value to process.
         */
        void processNextValue(double value);

        /**
         * Get the marginal.  This method should assume that all values for the marginal calculation have been sent
         * through {@code processNextValue}.
         *
         * @return the marginal value.
         */
        double getMarginal();
    }

    /**
     * The {@code LongMarginalFunction} provides the framework for calculating marginals based on {@code long}s.
     * The marginal is calculated in two steps:
     * <ol>
     *    <li>Each value for the marginal is processed (via {@code processNextValue(long)}).</li>
     *    <li>The final marginal value is calculated and returned (via {@code getMarginal()}).</li>
     * </ol>
     * This framework keeps the marginal function independent of the size of the marginal elements.
     */
    public static interface LongMarginalFunction {
        /**
         * Process the next value in the marginal calculation.
         *
         * @param value
         *        The value to process.
         */
        void processNextValue(long value);

        /**
         * Get the marginal.  This method should assume that all values for the marginal calculation have been sent
         * through {@code processNextValue}.
         *
         * @return the marginal value.
         */
        long getMarginal();
    }

    /**
     * The {@code Marginal} enum provides a standard set of {@code MarginalFunction}s.
     */
    public static enum Marginal implements MarginalFunction {
        /**
         * The marginal function which sums all elements.
         */
        SUM(new MarginalSum()),
        /**
         * The marginal function which averages the elements.
         */
        AVERAGE(new MarginalAverage()),
        /**
         * The marginal functions which returns the maximum value of the elements.
         */
        MAX(new MarginalMax()),
        /**
         * The marginal functions which returns the minimum value of the elements.
         */
        MIN(new MarginalMin());

        private final MarginalFunction function;

        private Marginal(MarginalFunction function) {
            this.function = function;
        }

        public DoubleMarginalFunction getDoubleMarginalFunction() {
            return function.getDoubleMarginalFunction();
        }

        public LongMarginalFunction getLongMarginalFunction() {
            return function.getLongMarginalFunction();
        }
    }

    //todo: this isn't really concurrent, just thread safe; need to sacrifice memory to make it really parallel
    //      some sort of a parallel or nested data structure, as opposed to a single incrementing variable
    private static class DoubleMarginalSum implements DoubleMarginalFunction {
        private AtomicLong sum = new AtomicLong(Double.doubleToLongBits(0.0));
        
        public void processNextValue(double value) {
            long v = sum.get();
            while (!sum.compareAndSet(v,Double.doubleToLongBits(value + Double.longBitsToDouble(v))))
                v = sum.get();
        }
        
        public double getMarginal() {
            return Double.longBitsToDouble(sum.get());
        }
    }   
    
    private static class LongMarginalSum implements LongMarginalFunction {
        private AtomicLong sum = new AtomicLong(0L);
        
        public void processNextValue(long value) {
            sum.addAndGet(value);
        }
        
        public long getMarginal() {
            return sum.get();
        }
    }
    
    private static class MarginalSum implements ConcurrentMarginalFunction {
        public DoubleMarginalFunction getDoubleMarginalFunction() {
            return new DoubleMarginalSum();
        } 
        
        public LongMarginalFunction getLongMarginalFunction() {
            return new LongMarginalSum();
        } 
        
    }                                                                     
    
    private static class MarginalAverage implements ConcurrentMarginalFunction {
        public DoubleMarginalFunction getDoubleMarginalFunction() {
            return new DoubleMarginalSum() {
                private AtomicLong count = new AtomicLong(0L);
        
                public void processNextValue(double value) {
                    super.processNextValue(value);
                    count.incrementAndGet();
                }
                
                public double getMarginal() {
                    return super.getMarginal()/count.get();
                }  
            };
        } 
        
        public LongMarginalFunction getLongMarginalFunction() {
            return new LongMarginalSum() {
                private AtomicLong count = new AtomicLong(0L);
        
                public void processNextValue(long value) {
                    super.processNextValue(value);
                    count.incrementAndGet();
                }
                
                public long getMarginal() {
                    return super.getMarginal()/count.get();
                }  
            };
        } 
    }                                                                     
    
    private static class MarginalMax implements ConcurrentMarginalFunction {
        public DoubleMarginalFunction getDoubleMarginalFunction() {
            return new DoubleMarginalFunction() {
                private AtomicLong max = new AtomicLong(Double.doubleToLongBits(Double.MIN_VALUE));
        
                public void processNextValue(double value) {
                    long v = max.get();
                    while (!max.compareAndSet(v,Double.doubleToLongBits(Math.max(value,Double.longBitsToDouble(v)))))
                        v = max.get();
                }
                
                public double getMarginal() {
                    return Double.longBitsToDouble(max.get());
                }  
            };
        } 
        
        public LongMarginalFunction getLongMarginalFunction() {
            return new LongMarginalFunction() {
                private AtomicLong max = new AtomicLong(Long.MIN_VALUE);
        
                public void processNextValue(long value) {
                    long v = max.get();
                    while (!max.compareAndSet(v,Math.max(v,value)))
                        v = max.get();
                }
                
                public long getMarginal() {
                    return max.get();
                }  
            };
        } 
    }                                                                     
    
    private static class MarginalMin implements ConcurrentMarginalFunction {
        public DoubleMarginalFunction getDoubleMarginalFunction() {
            return new DoubleMarginalFunction() {
                private AtomicLong min = new AtomicLong(Double.doubleToLongBits(Double.MAX_VALUE));
        
                public void processNextValue(double value) {
                    long v = min.get();
                    while (!min.compareAndSet(v,Double.doubleToLongBits(Math.min(value,Double.longBitsToDouble(v)))))
                        v = min.get();
                }
                
                public double getMarginal() {
                    return Double.longBitsToDouble(min.get());
                }  
            };
        } 
        
        public LongMarginalFunction getLongMarginalFunction() {
            return new LongMarginalFunction() {
                private AtomicLong min = new AtomicLong(Long.MAX_VALUE);
        
                public void processNextValue(long value) {
                    long v = min.get();
                    while (!min.compareAndSet(v,Math.min(v,value)))
                        v = min.get();
                }
                
                public long getMarginal() {
                    return min.get();
                }  
            };
        } 
    } 
}
