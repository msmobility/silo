package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.calculator.NumericFunction1;
import com.pb.sawdust.calculator.NumericFunction2;
import com.pb.sawdust.calculator.NumericFunctionN;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.UniformTensor;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.MirrorIndex;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.*;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code AbstractElementByElementTensorCalculation} provides a skeletal implementation of the {@code CellWiseTensorCalculation}
 * and {@code CellWiseTensorMutatingCalculation} interfaces.  To extend it concretely, only methods for {@code double} and
 * {@code long} tensor calculations (unary, binary, and multiple parameter) need to be implemented. Methods acting on
 * other types can be overridden, if desired for performance reasons.
 *
 * @author crf <br/>
 *         Started Nov 18, 2010 1:46:59 PM
 */
public abstract class AbstractCellWiseTensorCalculation implements CellWiseTensorCalculation,CellWiseTensorMutatingCalculation {
    /**
     * The tensor factory used to create result tensors, as needed.
     */
    protected final TensorFactory factory;

    /**
     * The uniform tensor factory used to create uniform tensors, as needed.
     */
    protected final TensorFactory uniformFactory;

    /**
     * Constructor specifying the tensor factory used to build the result tensors.
     *
     * @param factory
     *        The tensor factory used by this class to create result tensors.
     */
    public AbstractCellWiseTensorCalculation(TensorFactory factory) {
        this.factory = factory;
        uniformFactory = UniformTensor.getFactory();
    }
    
    /**
     * Perform an element-by-element binary calculation on a {@code LongTensor}, placing the result in a specified
     * output tensor.
     * 
     * @param t
     *        The input tensor.
     * 
     * @param result
     *        The tensor the result will be placed in.
     * 
     * @param function
     *        The function to apply to the tensor cells. This is allowed to be the same as {@code t}.
     *
     * @return {@code result}, once all of its elements have been changed by an application of {@code function} on {@code t}.
     */
    abstract protected LongTensor calculate(LongTensor t, LongTensor result, NumericFunction1 function);
    
    
    /**
     * Perform an element-by-element binary calculation on a {@code DoubleTensor}, placing the result in a specified
     * output tensor.
     * 
     * @param t
     *        The input tensor.
     * 
     * @param result
     *        The tensor the result will be placed in. This is allowed to be the same as {@code t}.
     * 
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code result}, once all of its elements have been changed by an application of {@code function} on {@code t}.
     */
    abstract protected DoubleTensor calculate(DoubleTensor t, DoubleTensor result, NumericFunction1 function);
    
    /**
     * Perform an element-by-element binary calculation between two {@code LongTensor}s, placing the result in a specified
     * output tensor. It is assumed that the two tensors will have identical shapes.
     * 
     * @param t1
     *        The tensor holding the first arguments to the function.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1} or {@code t2}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    abstract protected LongTensor calculate(LongTensor t1, LongTensor t2, LongTensor result, NumericFunction2 function);
    
    /**
     * Perform an element-by-element binary calculation between two {@code DoubleTensor}s, placing the result in a specified
     * output tensor. It is assumed that the two tensors will have identical shapes.
     * 
     * @param t1
     *        The tensor holding the first arguments to the function.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1} or {@code t2}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    abstract protected DoubleTensor calculate(DoubleTensor t1, DoubleTensor t2, DoubleTensor result, NumericFunction2 function);
    
    /**
     * Perform an element-by-element binary calculation between two {@code LongTensor}s, with the specified matching and
     * free dimensions, placing the result in a specified output tensor. It is assumed that the first tensor has the larger
     * number of dimensions. Matching dimensions are described in the class documentation for {@link CellWiseTensorCalculation}.
     * The free dimensions are the list of dimensions (sorted) of the first tensor which do not have a match in those of
     * the second tensor.
     * 
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor will have a dimension count greater than
     *        or equal to {@code t2}.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor will have a dimension count less than
     *        or equal to {@code t2}.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     * 
     * @param freeDimensions
     *        The free dimensions of {@code t1}. 
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    abstract protected LongTensor calculate(LongTensor t1, LongTensor t2, LongTensor result, NumericFunction2 function, int[] matchingDimensions, int[] freeDimensions);
    
    /**
     * Perform an element-by-element binary calculation between two {@code DoubleTensor}s, with the specified matching and
     * free dimensions, placing the result in a specified output tensor. It is assumed that the first tensor has the larger
     * number of dimensions. Matching dimensions are described in the class documentation for {@link CellWiseTensorCalculation}.
     * The free dimensions are the list of dimensions (sorted) of the first tensor which do not have a match in those of
     * the second tensor.
     * 
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor will have a dimension count greater than
     *        or equal to {@code t2}.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor will have a dimension count less than
     *        or equal to {@code t2}.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     * 
     * @param freeDimensions
     *        The free dimensions of {@code t1}. 
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    abstract protected DoubleTensor calculate(DoubleTensor t1, DoubleTensor t2, DoubleTensor result, NumericFunction2 function, int[] matchingDimensions, int[] freeDimensions);
    
    /**
     * Perform a compound element-by-element calculation on a series of input parameters.  All parameters will be equal
     * in shape to the result tensor, and will be of the correct count to apply to {@code function} (in order).
     *
     * @param function
     *        The function to apply.
     *
     * @param parameters
     *        The parameters for the function.
     *
     * @param result
     *        The tensor to place the results in.
     *
     * @return {@code result}, after it has been filled with the result of {@code function} applied to {@code parameters}.
     */
    abstract protected LongTensor calculate(NumericFunctionN function, LongTensor[] parameters, LongTensor result);

    /**
     * Perform a compound element-by-element calculation on a series of input parameters.  All parameters will be equal
     * in shape to the result tensor, and will be of the correct count to apply to {@code function} (in order).
     *
     * @param function
     *        The function to apply.
     *
     * @param parameters
     *        The parameters for the function.
     *
     * @param result
     *        The tensor to place the results in.
     *
     * @return {@code result}, after it has been filled with the result of {@code function} applied to {@code parameters}.
     */
    abstract protected DoubleTensor calculate(NumericFunctionN function, DoubleTensor[] parameters, DoubleTensor result);
    

    /**
     * Perform an element-by-element binary calculation on a {@code ByteTensor}, placing the result in a specified
     * output tensor. The default behavior is to treat the tensors like {@code IntTensor}s and apply 
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.calculator.NumericFunction1)} .
     * 
     * @param t
     *        The input tensor.
     * 
     * @param result
     *        The tensor the result will be placed in. This is allowed to be the same as {@code t}.
     * 
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code result}, once all of its elements have been changed by an application of {@code function} on {@code t}.
     */
    protected ByteTensor calculate(ByteTensor t, ByteTensor result, NumericFunction1 function) {
        calculate(TensorUtil.asIntTensor(t),TensorUtil.asIntTensor(result),function);
        return result;
    }

    /**
     * Perform an element-by-element binary calculation on a {@code ShortTensor}, placing the result in a specified
     * output tensor. The default behavior is to treat the tensors like {@code IntTensor}s and apply 
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.calculator.NumericFunction1)} .
     * 
     * @param t
     *        The input tensor.
     * 
     * @param result
     *        The tensor the result will be placed in. This is allowed to be the same as {@code t}.
     * 
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code result}, once all of its elements have been changed by an application of {@code function} on {@code t}.
     */
    protected ShortTensor calculate(ShortTensor t, ShortTensor result, NumericFunction1 function) {
        calculate(TensorUtil.asIntTensor(t),TensorUtil.asIntTensor(result),function);
        return result;
    } 

    /**
     * Perform an element-by-element binary calculation on an {@code IntTensor}, placing the result in a specified
     * output tensor. The default behavior is to treat the tensors like {@code LongTensor}s and apply 
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.LongTensor, com.pb.sawdust.tensor.decorators.primitive.LongTensor, com.pb.sawdust.calculator.NumericFunction1)} .
     * 
     * @param t
     *        The input tensor.
     * 
     * @param result
     *        The tensor the result will be placed in. This is allowed to be the same as {@code t}.
     * 
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code result}, once all of its elements have been changed by an application of {@code function} on {@code t}.
     */                         
    protected IntTensor calculate(IntTensor t, IntTensor result, NumericFunction1 function) {
        calculate(TensorUtil.asLongTensor(t),TensorUtil.asLongTensor(result),function);
        return result;
    }

    /**
     * Perform an element-by-element binary calculation on a {@code FloatTensor}, placing the result in a specified
     * output tensor. The default behavior is to treat the tensors like a {@code DoubleTensor}s and apply 
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.DoubleTensor, com.pb.sawdust.tensor.decorators.primitive.DoubleTensor, com.pb.sawdust.calculator.NumericFunction1)} .
     * 
     * @param t
     *        The input tensor.
     * 
     * @param result
     *        The tensor the result will be placed in. This is allowed to be the same as {@code t}.
     * 
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code result}, once all of its elements have been changed by an application of {@code function} on {@code t}.
     */
    protected FloatTensor calculate(FloatTensor t, FloatTensor result, NumericFunction1 function) {
        calculate(TensorUtil.asDoubleTensor(t),TensorUtil.asDoubleTensor(result),function);
        return result;
    }   
    
    public ByteTensor calculate(ByteTensor t, NumericFunction1 function) {
        ByteTensor result = factory.byteTensor(t.getDimensions());
        calculate(TensorUtil.asIntTensor(t),TensorUtil.asIntTensor(result),function);
        return result;
    }

    public ShortTensor calculate(ShortTensor t, NumericFunction1 function) {
        ShortTensor result = factory.shortTensor(t.getDimensions());
        calculate(TensorUtil.asIntTensor(t),TensorUtil.asIntTensor(result),function);
        return result;
    }

    public IntTensor calculate(IntTensor t, NumericFunction1 function) {
        return calculate(t,factory.intTensor(t.getDimensions()),function);
    }

    public LongTensor calculate(LongTensor t, NumericFunction1 function) {
        return calculate(t,factory.longTensor(t.getDimensions()),function);
    }

    public FloatTensor calculate(FloatTensor t, NumericFunction1 function) {
        return calculate(t,factory.floatTensor(t.getDimensions()),function);
    }

    public DoubleTensor calculate(DoubleTensor t, NumericFunction1 function) {
        return calculate(t,factory.doubleTensor(t.getDimensions()),function);
    }     
    
    public ByteTensor calculateInto(ByteTensor t, NumericFunction1 function) {
        return calculate(t,t,function);
    }                                  
    
    public ShortTensor calculateInto(ShortTensor t, NumericFunction1 function) {
        return calculate(t,t,function);
    }
    
    public IntTensor calculateInto(IntTensor t, NumericFunction1 function) {
        return calculate(t,t,function);
    }
    
    public LongTensor calculateInto(LongTensor t, NumericFunction1 function) {
        return calculate(t,t,function);
    }
    
    public FloatTensor calculateInto(FloatTensor t, NumericFunction1 function) {
        return calculate(t,t,function);
    }
    
    public DoubleTensor calculateInto(DoubleTensor t, NumericFunction1 function) {
        return calculate(t,t,function);
    }
    
    /**
     * Perform an element-by-element binary calculation between two {@code ByteTensor}s, placing the result in a specified
     * output tensor.  It is assumed that the two tensors will have identical shapes. The default behavior is to treat the
     * tensors like {@code IntTensor}s and apply
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.calculator.NumericFunction1)}.
     * 
     * @param t1
     *        The tensor holding the first arguments to the function.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1} or {@code t2}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    protected ByteTensor calculate(ByteTensor t1, ByteTensor t2, ByteTensor result, NumericFunction2 function) {
        calculate(TensorUtil.asIntTensor(t1),TensorUtil.asIntTensor(t2),TensorUtil.asIntTensor(result),function);
        return result;
    }                       
    /**
     * Perform an element-by-element binary calculation between two {@code ShortTensor}s, placing the result in a specified
     * output tensor. It is assumed that the two tensors will have identical shapes. The default behavior is to treat the
     * tensors like {@code IntTensor}s and apply
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.calculator.NumericFunction1)}.
     * 
     * @param t1
     *        The tensor holding the first arguments to the function.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1} or {@code t2}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    protected ShortTensor calculate(ShortTensor t1, ShortTensor t2, ShortTensor result, NumericFunction2 function) {
        calculate(TensorUtil.asIntTensor(t1),TensorUtil.asIntTensor(t2),TensorUtil.asIntTensor(result),function);
        return result;
    }                                                                                 
    
    /**
     * Perform an element-by-element binary calculation between two {@code IntTensor}s, placing the result in a specified
     * output tensor. It is assumed that the two tensors will have identical shapes. The default behavior is to treat the
     * tensors like {@code LongTensor}s and apply
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.LongTensor, com.pb.sawdust.tensor.decorators.primitive.LongTensor, com.pb.sawdust.calculator.NumericFunction1)}.
     * 
     * @param t1
     *        The tensor holding the first arguments to the function.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1} or {@code t2}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    protected IntTensor calculate(IntTensor t1, IntTensor t2, IntTensor result, NumericFunction2 function) {
        calculate(TensorUtil.asLongTensor(t1),TensorUtil.asLongTensor(t2),TensorUtil.asLongTensor(result),function);
        return result;
    }                
    
    /**
     * Perform an element-by-element binary calculation between two {@code FloatTensor}s, placing the result in a specified
     * output tensor. It is assumed that the two tensors will have identical shapes. The default behavior is to treat the
     * tensors like {@code DoubleTensor}s and apply
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.DoubleTensor, com.pb.sawdust.tensor.decorators.primitive.DoubleTensor, com.pb.sawdust.calculator.NumericFunction1)}.
     * 
     * @param t1
     *        The tensor holding the first arguments to the function.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1} or {@code t2}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    protected FloatTensor calculate(FloatTensor t1, FloatTensor t2, FloatTensor result, NumericFunction2 function) {
        calculate(TensorUtil.asDoubleTensor(t1),TensorUtil.asDoubleTensor(t2),TensorUtil.asDoubleTensor(result),function);
        return result;
    }
    
    /**
     * Perform an element-by-element binary calculation between two {@code ByteTensor}s, with the specified matching and
     * free dimensions, placing the result in a specified output tensor. It is assumed that the first tensor has the larger
     * number of dimensions. Matching dimensions are described in the class documentation for {@link CellWiseTensorCalculation}.
     * The free dimensions are the list of dimensions (sorted) of the first tensor which do not have a match in those of
     * the second tensor. The default behavior is to treat all of the tensors as {@code IntTensor}s and apply
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.calculator.NumericFunction2, int[], int[])}.
     *
     * 
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor will have a dimension count greater than
     *        or equal to {@code t2}.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor will have a dimension count less than
     *        or equal to {@code t2}.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     * 
     * @param freeDimensions
     *        The free dimensions of {@code t1}. 
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    protected ByteTensor calculate(ByteTensor t1, ByteTensor t2, ByteTensor result, NumericFunction2 function, int[] matchingDimensions, int[] freeDimensions) {
        calculate(TensorUtil.asIntTensor(t1),TensorUtil.asIntTensor(t2),TensorUtil.asIntTensor(result),function,matchingDimensions,freeDimensions);
        return result;
    }
    
    /**
     * Perform an element-by-element binary calculation between two {@code ShortTensor}s, with the specified matching and
     * free dimensions, placing the result in a specified output tensor. It is assumed that the first tensor has the larger
     * number of dimensions. Matching dimensions are described in the class documentation for {@link CellWiseTensorCalculation}.
     * The free dimensions are the list of dimensions (sorted) of the first tensor which do not have a match in those of
     * the second tensor. The default behavior is to treat all of the tensors as {@code IntTensor}s and apply
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.tensor.decorators.primitive.IntTensor, com.pb.sawdust.calculator.NumericFunction2, int[], int[])}.
     *
     * 
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor will have a dimension count greater than
     *        or equal to {@code t2}.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor will have a dimension count less than
     *        or equal to {@code t2}.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     * 
     * @param freeDimensions
     *        The free dimensions of {@code t1}. 
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    protected ShortTensor calculate(ShortTensor t1, ShortTensor t2, ShortTensor result, NumericFunction2 function, int[] matchingDimensions, int[] freeDimensions) {
        calculate(TensorUtil.asIntTensor(t1),TensorUtil.asIntTensor(t2),TensorUtil.asIntTensor(result),function,matchingDimensions,freeDimensions);
        return result;
    }
   
    /**
     * Perform an element-by-element binary calculation between two {@code IntTensor}s, with the specified matching and
     * free dimensions, placing the result in a specified output tensor. It is assumed that the first tensor has the larger
     * number of dimensions. Matching dimensions are described in the class documentation for {@link CellWiseTensorCalculation}.
     * The free dimensions are the list of dimensions (sorted) of the first tensor which do not have a match in those of
     * the second tensor. The default behavior is to treat all of the tensors as {@code LongTensor}s and apply
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.LongTensor, com.pb.sawdust.tensor.decorators.primitive.LongTensor, com.pb.sawdust.tensor.decorators.primitive.LongTensor, com.pb.sawdust.calculator.NumericFunction2, int[], int[])}.
     *
     * 
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor will have a dimension count greater than
     *        or equal to {@code t2}.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor will have a dimension count less than
     *        or equal to {@code t2}.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     * 
     * @param freeDimensions
     *        The free dimensions of {@code t1}. 
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    protected IntTensor calculate(IntTensor t1, IntTensor t2, IntTensor result, NumericFunction2 function, int[] matchingDimensions, int[] freeDimensions) {
        calculate(TensorUtil.asLongTensor(t1),TensorUtil.asLongTensor(t2),TensorUtil.asLongTensor(result),function,matchingDimensions,freeDimensions);
        return result;
    }
    
    /**
     * Perform an element-by-element binary calculation between two {@code FloatTensor}s, with the specified matching and
     * free dimensions, placing the result in a specified output tensor. It is assumed that the first tensor has the larger
     * number of dimensions. Matching dimensions are described in the class documentation for {@link CellWiseTensorCalculation}.
     * The free dimensions are the list of dimensions (sorted) of the first tensor which do not have a match in those of
     * the second tensor. The default behavior is to treat all of the tensors as {@code DoubleTensor}s and apply
     * {@link #calculate(com.pb.sawdust.tensor.decorators.primitive.DoubleTensor, com.pb.sawdust.tensor.decorators.primitive.DoubleTensor, com.pb.sawdust.tensor.decorators.primitive.DoubleTensor, com.pb.sawdust.calculator.NumericFunction2, int[], int[])}.
     * 
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor will have a dimension count greater than
     *        or equal to {@code t2}.
     * 
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor will have a dimension count less than
     *        or equal to {@code t2}.
     * 
     * @param result
     *        The tensor holding the results of the calculations. This is allowed to be the same as {@code t1}.
     * 
     * @param function
     *        The function to calculate.
     * 
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     * 
     * @param freeDimensions
     *        The free dimensions of {@code t1}. 
     * 
     * @return {@code result}, after it has been filled with the result of {@code t1} and {@code t2} applied to {@code function}.
     */
    protected FloatTensor calculate(FloatTensor t1, FloatTensor t2, FloatTensor result, NumericFunction2 function, int[] matchingDimensions, int[] freeDimensions) {
        calculate(TensorUtil.asDoubleTensor(t1),TensorUtil.asDoubleTensor(t2),TensorUtil.asDoubleTensor(result),function,matchingDimensions,freeDimensions);
        return result;
    }


    /**
     * Perform a compound element-by-element calculation on a series of input parameters.  All parameters will be equal
     * in shape to the result tensor, and will be of the correct count to apply to {@code function} (in order).
     * 
     * @param function
     *        The function to apply.
     * 
     * @param parameters
     *        The parameters for the function.
     * 
     * @param result
     *        The tensor to place the results in.
     * 
     * @return {@code result}, after it has been filled with the result of {@code function} applied to {@code parameters}.
     */
    protected ByteTensor calculate(NumericFunctionN function, ByteTensor[] parameters, ByteTensor result) {
        IntTensor[] p = new IntTensor[parameters.length];
        for (int i : range(p.length))
            p[i] = TensorUtil.asIntTensor(parameters[i]);
        calculate(function,p,TensorUtil.asIntTensor(result));
        return result;
    }

    /**
     * Perform a compound element-by-element calculation on a series of input parameters.  All parameters will be equal
     * in shape to the result tensor, and will be of the correct count to apply to {@code function} (in order).
     * 
     * @param function
     *        The function to apply.
     * 
     * @param parameters
     *        The parameters for the function.
     * 
     * @param result
     *        The tensor to place the results in.
     * 
     * @return {@code result}, after it has been filled with the result of {@code function} applied to {@code parameters}.
     */
    protected ShortTensor calculate(NumericFunctionN function, ShortTensor[] parameters, ShortTensor result) {
        IntTensor[] p = new IntTensor[parameters.length];
        for (int i : range(p.length))
            p[i] = TensorUtil.asIntTensor(parameters[i]);
        calculate(function,p,TensorUtil.asIntTensor(result));
        return result;
    }

    /**
     * Perform a compound element-by-element calculation on a series of input parameters.  All parameters will be equal
     * in shape to the result tensor, and will be of the correct count to apply to {@code function} (in order).
     * 
     * @param function
     *        The function to apply.
     * 
     * @param parameters
     *        The parameters for the function.
     * 
     * @param result
     *        The tensor to place the results in.
     * 
     * @return {@code result}, after it has been filled with the result of {@code function} applied to {@code parameters}.
     */
    protected IntTensor calculate(NumericFunctionN function, IntTensor[] parameters, IntTensor result) {
        LongTensor[] p = new LongTensor[parameters.length];
        for (int i : range(p.length))
            p[i] = TensorUtil.asLongTensor(parameters[i]);
        calculate(function,p,TensorUtil.asLongTensor(result));
        return result;
    }

    /**
     * Perform a compound element-by-element calculation on a series of input parameters.  All parameters will be equal
     * in shape to the result tensor, and will be of the correct count to apply to {@code function} (in order).
     * 
     * @param function
     *        The function to apply.
     * 
     * @param parameters
     *        The parameters for the function.
     * 
     * @param result
     *        The tensor to place the results in.
     * 
     * @return {@code result}, after it has been filled with the result of {@code function} applied to {@code parameters}.
     */
    protected FloatTensor calculate(NumericFunctionN function, FloatTensor[] parameters, FloatTensor result) {
        DoubleTensor[] p = new DoubleTensor[parameters.length];
        for (int i : range(p.length))
            p[i] = TensorUtil.asDoubleTensor(parameters[i]);
        calculate(function,p,TensorUtil.asDoubleTensor(result));
        return result;
    }
    
    private int compareTensorSizes(Tensor t1, Tensor t2) {
        return t1.size() - t2.size();
    }
    
    private int[] checkMatchingTensors(Tensor t1, Tensor t2) {
        //assumes they are of equal size
        int[] dim1 = t1.getDimensions();
        int[] dim2 = t2.getDimensions();
        if (!Arrays.equals(dim1,dim2))
            return buildMatchingTensorMatchArray(dim1,dim2);
//            throw new IllegalArgumentException(String.format("Tensors must have identical shapes for element-by-element calculations: %s vs %s",Arrays.toString(t1.getDimensions()),Arrays.toString(t2.getDimensions())));
        return null;
    }

    private int[] buildMatchingTensorMatchArray(int[] dim1, int[] dim2) {
        Set<Integer> used = new HashSet<Integer>();
        int[] marray = new int[dim2.length];
        int counter = 0;
        boolean error = false;
        outer: for (int i : dim2) {
            if (!used.add(i)) {
                error = true;
                break;
            }
            boolean found = false;
            for (int j : range(dim1.length)) {
                if (dim1[j] == i) {
                    if (found){
                        error = true;
                        break outer;
                    } else {
                        marray[counter++] = j;
                        found = true;
                    }
                }
            }
            if (!found) {
                error = true;
                break;
            }
        }
        if (error)
            throw new IllegalArgumentException(String.format("Tensors must have unambiguously matching shapes for element-by-element calculations: %s vs %s",Arrays.toString(dim1),Arrays.toString(dim2)));
        return marray;
    }
    
    private int[][] getMatchingAndFreeDims(int[] t1Dims, int[] t2Dims) {
        //t1 is assumed to be larger
        List<Integer> matchingDimensions = new LinkedList<Integer>();
        List<Integer> freeDimensions = new LinkedList<Integer>();
        int t2Counter = 0;
        for (int i : range(t1Dims.length)) {
            if (t2Counter >= t2Dims.length || t1Dims[i] != t2Dims[t2Counter]) {
                freeDimensions.add(i);
            } else {
                matchingDimensions.add(i);
                t2Counter++;
            }
        }
        if (t2Counter < t2Dims.length)
            throw new IllegalArgumentException("Tensor with dimensions " + Arrays.toString(t2Dims) + " does not map (element by element) to tensor with dimensions " + Arrays.toString(t1Dims));
        return new int[][] {ArrayUtil.toPrimitive(matchingDimensions.toArray(new Integer[matchingDimensions.size()])),ArrayUtil.toPrimitive(freeDimensions.toArray(new Integer[freeDimensions.size()]))};
    }

    private int[][] getMatchingAndFreeDims(Tensor t1, Tensor t2, int[] matchingDims) {
        if (matchingDims == null)
            return getMatchingAndFreeDims(t1.getDimensions(),t2.getDimensions());
        //t1 is assumed to be larger
        if (matchingDims.length != t2.size())
            throw new IllegalArgumentException(String.format("Matching dimensions element count (%d) must equal dimension count of smaller tensor (%d): %s",matchingDims.length,t2.size(),Arrays.toString(matchingDims)));    
        int[] freeDimensions = new int[t1.size() - matchingDims.length];
        Set<Integer> checked = new HashSet<Integer>();
        for (int i : range(matchingDims.length)) {
            int d = matchingDims[i];
            if (d < 0 || d >= t1.size())
                throw new IllegalArgumentException(String.format("Matching dimension %d out of bounds for tensor of size %d",d,t1.size()));
            if (!checked.add(d))
                throw new IllegalArgumentException(String.format("Repeated matching dimension %d",d));
            if (t2.size(i) != t1.size(d)) 
                throw new IllegalArgumentException(String.format("Dimension size of tensor (%d dimensions) dimension %d (%d) does not match tensor (%d dimensions) dimension %d (%d)",t1.size(),d,t1.size(d),t2.size(),i,t2.size(i)));
        }
        int counter = 0;
        for (int i : range(t1.size()))
            if (!checked.contains(i))
                freeDimensions[counter++] = i;
        return new int[][] {matchingDims,freeDimensions};
    }
    
    public ByteTensor calculate(byte value, ByteTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedByteTensor(value,dimensions),t,factory.byteTensor(dimensions),function);
    }

    public ByteTensor calculate(ByteTensor t, byte value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(t,uniformFactory.initializedByteTensor(value,dimensions),factory.byteTensor(dimensions),function);
    }

    public ShortTensor calculate(short value, ByteTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedShortTensor(value,dimensions),TensorUtil.asShortTensor(t),factory.shortTensor(dimensions),function);
    }

    public ShortTensor calculate(ByteTensor t, short value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asShortTensor(t),uniformFactory.initializedShortTensor(value,dimensions),factory.shortTensor(dimensions),function);
    }

    public IntTensor calculate(int value, ByteTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedIntTensor(value,dimensions),TensorUtil.asIntTensor(t),factory.intTensor(dimensions),function);
    }

    public IntTensor calculate(ByteTensor t, int value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asIntTensor(t),uniformFactory.initializedIntTensor(value,dimensions),factory.intTensor(dimensions),function);
    }

    public LongTensor calculate(long value, ByteTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedLongTensor(value,dimensions),TensorUtil.asLongTensor(t),factory.longTensor(dimensions),function);
    }

    public LongTensor calculate(ByteTensor t, long value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asLongTensor(t),uniformFactory.initializedLongTensor(value,dimensions),factory.longTensor(dimensions),function);
    }

    public FloatTensor calculate(float value, ByteTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedFloatTensor(value,dimensions),TensorUtil.asFloatTensor(t),factory.floatTensor(dimensions),function);
    }

    public FloatTensor calculate(ByteTensor t, float value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asFloatTensor(t),uniformFactory.initializedFloatTensor(value,dimensions),factory.floatTensor(dimensions),function);
    }

    public DoubleTensor calculate(double value, ByteTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedDoubleTensor(value,dimensions),TensorUtil.asDoubleTensor(t),factory.doubleTensor(dimensions),function);
    }

    public DoubleTensor calculate(ByteTensor t, double value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asDoubleTensor(t),uniformFactory.initializedDoubleTensor(value,dimensions),factory.doubleTensor(dimensions),function);
    }
    
    public ShortTensor calculate(short value, ShortTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedShortTensor(value,dimensions),t,factory.shortTensor(dimensions),function);
    }

    public ShortTensor calculate(ShortTensor t, short value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(t,uniformFactory.initializedShortTensor(value,dimensions),factory.shortTensor(dimensions),function);
    }

    public IntTensor calculate(int value, ShortTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedIntTensor(value,dimensions),TensorUtil.asIntTensor(t),factory.intTensor(dimensions),function);
    }

    public IntTensor calculate(ShortTensor t, int value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asIntTensor(t),uniformFactory.initializedIntTensor(value,dimensions),factory.intTensor(dimensions),function);
    }

    public LongTensor calculate(long value, ShortTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedLongTensor(value,dimensions),TensorUtil.asLongTensor(t),factory.longTensor(dimensions),function);
    }

    public LongTensor calculate(ShortTensor t, long value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asLongTensor(t),uniformFactory.initializedLongTensor(value,dimensions),factory.longTensor(dimensions),function);
    }

    public FloatTensor calculate(float value, ShortTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedFloatTensor(value,dimensions),TensorUtil.asFloatTensor(t),factory.floatTensor(dimensions),function);
    }

    public FloatTensor calculate(ShortTensor t, float value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asFloatTensor(t),uniformFactory.initializedFloatTensor(value,dimensions),factory.floatTensor(dimensions),function);
    }

    public DoubleTensor calculate(double value, ShortTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedDoubleTensor(value,dimensions),TensorUtil.asDoubleTensor(t),factory.doubleTensor(dimensions),function);
    }

    public DoubleTensor calculate(ShortTensor t, double value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asDoubleTensor(t),uniformFactory.initializedDoubleTensor(value,dimensions),factory.doubleTensor(dimensions),function);
    }
    
    public IntTensor calculate(int value, IntTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedIntTensor(value,dimensions),t,factory.intTensor(dimensions),function);
    }

    public IntTensor calculate(IntTensor t, int value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(t,uniformFactory.initializedIntTensor(value,dimensions),factory.intTensor(dimensions),function);
    }
    
    public LongTensor calculate(long value, IntTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedLongTensor(value,dimensions),TensorUtil.asLongTensor(t),factory.longTensor(dimensions),function);
    }

    public LongTensor calculate(IntTensor t, long value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asLongTensor(t),uniformFactory.initializedLongTensor(value,dimensions),factory.longTensor(dimensions),function);
    }

    public FloatTensor calculate(float value, IntTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedFloatTensor(value,dimensions),TensorUtil.asFloatTensor(t),factory.floatTensor(dimensions),function);
    }

    public FloatTensor calculate(IntTensor t, float value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asFloatTensor(t),uniformFactory.initializedFloatTensor(value,dimensions),factory.floatTensor(dimensions),function);
    }

    public DoubleTensor calculate(double value, IntTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedDoubleTensor(value,dimensions),TensorUtil.asDoubleTensor(t),factory.doubleTensor(dimensions),function);
    }

    public DoubleTensor calculate(IntTensor t, double value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asDoubleTensor(t),uniformFactory.initializedDoubleTensor(value,dimensions),factory.doubleTensor(dimensions),function);
    }
    
    public LongTensor calculate(long value, LongTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedLongTensor(value,dimensions),t,factory.longTensor(dimensions),function);
    }

    public LongTensor calculate(LongTensor t, long value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(t,uniformFactory.initializedLongTensor(value,dimensions),factory.longTensor(dimensions),function);
    }

    public FloatTensor calculate(float value, LongTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedFloatTensor(value,dimensions),TensorUtil.asFloatTensor(t),factory.floatTensor(dimensions),function);
    }

    public FloatTensor calculate(LongTensor t, float value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asFloatTensor(t),uniformFactory.initializedFloatTensor(value,dimensions),factory.floatTensor(dimensions),function);
    }

    public DoubleTensor calculate(double value, LongTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedDoubleTensor(value,dimensions),TensorUtil.asDoubleTensor(t),factory.doubleTensor(dimensions),function);
    }

    public DoubleTensor calculate(LongTensor t, double value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asDoubleTensor(t),uniformFactory.initializedDoubleTensor(value,dimensions),factory.doubleTensor(dimensions),function);
    }
    
    public FloatTensor calculate(float value, FloatTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedFloatTensor(value,dimensions),t,factory.floatTensor(dimensions),function);
    }

    public FloatTensor calculate(FloatTensor t, float value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(t,uniformFactory.initializedFloatTensor(value,dimensions),factory.floatTensor(dimensions),function);
    }

    public DoubleTensor calculate(double value, FloatTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedDoubleTensor(value,dimensions),TensorUtil.asDoubleTensor(t),factory.doubleTensor(dimensions),function);
    }

    public DoubleTensor calculate(FloatTensor t, double value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(TensorUtil.asDoubleTensor(t),uniformFactory.initializedDoubleTensor(value,dimensions),factory.doubleTensor(dimensions),function);
    }
    
    public DoubleTensor calculate(double value, DoubleTensor t, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(uniformFactory.initializedDoubleTensor(value,dimensions),t,factory.doubleTensor(dimensions),function);
    }

    public DoubleTensor calculate(DoubleTensor t, double value, NumericFunction2 function) {
        int[] dimensions = t.getDimensions();
        return calculate(t,uniformFactory.initializedDoubleTensor(value,dimensions),factory.doubleTensor(dimensions),function);
    }
    
    public ByteTensor calculate(ByteTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,factory.byteTensor(t1.getDimensions()),function);
        } else if (diff < 0) {
            return calculate(t2,t1, NumericFunction2.mirror(function),matchingDimensions);
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,factory.byteTensor(t1.getDimensions()),function,mf[0],mf[1]);
    }

    public ShortTensor calculate(ShortTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asShortTensor(t2),function,matchingDimensions);
    }

    public ShortTensor calculate(ByteTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asShortTensor(t1),t2,function,matchingDimensions);
    }
    
    public IntTensor calculate(IntTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asIntTensor(t2),function,matchingDimensions);
    }

    public IntTensor calculate(ByteTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asIntTensor(t1),t2,function,matchingDimensions);
    }
    
    public LongTensor calculate(LongTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asLongTensor(t2),function,matchingDimensions);
    }

    public LongTensor calculate(ByteTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asLongTensor(t1),t2,function,matchingDimensions);
    }
    
    public FloatTensor calculate(FloatTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asFloatTensor(t2),function,matchingDimensions);
    }

    public FloatTensor calculate(ByteTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asFloatTensor(t1),t2,function,matchingDimensions);
    }
    
    public DoubleTensor calculate(DoubleTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asDoubleTensor(t2),function,matchingDimensions);
    }

    public DoubleTensor calculate(ByteTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asDoubleTensor(t1),t2,function,matchingDimensions);
    }
    
    public ShortTensor calculate(ShortTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,factory.shortTensor(t1.getDimensions()),function);
        } else if (diff < 0) {
            return calculate(t2,t1, NumericFunction2.mirror(function),matchingDimensions);
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,factory.shortTensor(t1.getDimensions()),function,mf[0],mf[1]);
    }

    public IntTensor calculate(IntTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asIntTensor(t2),function,matchingDimensions);
    }
    
    public IntTensor calculate(ShortTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asIntTensor(t1),t2,function,matchingDimensions);
    }

    public LongTensor calculate(LongTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asLongTensor(t2),function,matchingDimensions);
    }
    
    public LongTensor calculate(ShortTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asLongTensor(t1),t2,function,matchingDimensions);
    }

    public FloatTensor calculate(FloatTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asFloatTensor(t2),function,matchingDimensions);
    }
    
    public FloatTensor calculate(ShortTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asFloatTensor(t1),t2,function,matchingDimensions);
    }

    public DoubleTensor calculate(DoubleTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asDoubleTensor(t2),function,matchingDimensions);
    }
    
    public DoubleTensor calculate(ShortTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asDoubleTensor(t1),t2,function,matchingDimensions);
    }

    public IntTensor calculate(IntTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,factory.intTensor(t1.getDimensions()),function);
        } else if (diff < 0) {
            return calculate(t2,t1, NumericFunction2.mirror(function),matchingDimensions);
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,factory.intTensor(t1.getDimensions()),function,mf[0],mf[1]);
    }
    
    public LongTensor calculate(LongTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asLongTensor(t2),function,matchingDimensions);
    }

    public LongTensor calculate(IntTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asLongTensor(t1),t2,function,matchingDimensions);
    }
    
    public FloatTensor calculate(FloatTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asFloatTensor(t2),function,matchingDimensions);
    }

    public FloatTensor calculate(IntTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asFloatTensor(t1),t2,function,matchingDimensions);
    }

    public DoubleTensor calculate(DoubleTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asDoubleTensor(t2),function,matchingDimensions);
    }

    public DoubleTensor calculate(IntTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asDoubleTensor(t1),t2,function,matchingDimensions);
    }
    
    public LongTensor calculate(LongTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,factory.longTensor(t1.getDimensions()),function);
        } else if (diff < 0) {
            return calculate(t2,t1, NumericFunction2.mirror(function),matchingDimensions);
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,factory.longTensor(t1.getDimensions()),function,mf[0],mf[1]);
    }

    public FloatTensor calculate(FloatTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asFloatTensor(t2),function,matchingDimensions);
    }
    
    public FloatTensor calculate(LongTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asFloatTensor(t1),t2,function,matchingDimensions);
    }

    public DoubleTensor calculate(DoubleTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asDoubleTensor(t2),function,matchingDimensions);
    }
    
    public DoubleTensor calculate(LongTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asDoubleTensor(t1),t2,function,matchingDimensions);
    }

    public FloatTensor calculate(FloatTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,factory.floatTensor(t1.getDimensions()),function);
        } else if (diff < 0) {
            return calculate(t2,t1, NumericFunction2.mirror(function),matchingDimensions);
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,factory.floatTensor(t1.getDimensions()),function,mf[0],mf[1]);
    }
    
    public DoubleTensor calculate(DoubleTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(t1,TensorUtil.asDoubleTensor(t2),function,matchingDimensions);
    }

    public DoubleTensor calculate(FloatTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculate(TensorUtil.asDoubleTensor(t1),t2,function,matchingDimensions);
    }
    
    public DoubleTensor calculate(DoubleTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,factory.doubleTensor(t1.getDimensions()),function);
        } else if (diff < 0) {
            return calculate(t2,t1, NumericFunction2.mirror(function),matchingDimensions);
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,factory.doubleTensor(t1.getDimensions()),function,mf[0],mf[1]);
    }

    public ByteTensor calculate(ByteTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }
 
    public ShortTensor calculate(ShortTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public ShortTensor calculate(ByteTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public IntTensor calculate(IntTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public IntTensor calculate(ByteTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public LongTensor calculate(LongTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public LongTensor calculate(ByteTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public FloatTensor calculate(FloatTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public FloatTensor calculate(ByteTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(DoubleTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(ByteTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public ShortTensor calculate(ShortTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public IntTensor calculate(IntTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public IntTensor calculate(ShortTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public LongTensor calculate(LongTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public LongTensor calculate(ShortTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public FloatTensor calculate(FloatTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public FloatTensor calculate(ShortTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(DoubleTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(ShortTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public IntTensor calculate(IntTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public LongTensor calculate(LongTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public LongTensor calculate(IntTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public FloatTensor calculate(FloatTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public FloatTensor calculate(IntTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(DoubleTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(IntTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public LongTensor calculate(LongTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public FloatTensor calculate(FloatTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public FloatTensor calculate(LongTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(DoubleTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(LongTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public FloatTensor calculate(FloatTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(DoubleTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(FloatTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }

    public DoubleTensor calculate(DoubleTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculate(t1,t2,function,null);
    }
    
    public ByteTensor calculateInto(ByteTensor t, byte value, NumericFunction2 function) {
        return calculate(t,uniformFactory.initializedByteTensor(value,t.getDimensions()),t,function);
    }

    public ByteTensor calculateInto(ByteTensor t, short value, NumericFunction2 function) {
        ShortTensor r = TensorUtil.asShortTensor(t); 
        calculate(r,uniformFactory.initializedShortTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public ByteTensor calculateInto(ByteTensor t, int value, NumericFunction2 function) {
        IntTensor r = TensorUtil.asIntTensor(t); 
        calculate(r,uniformFactory.initializedIntTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public ByteTensor calculateInto(ByteTensor t, long value, NumericFunction2 function) {
        LongTensor r = TensorUtil.asLongTensor(t); 
        calculate(r,uniformFactory.initializedLongTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public ByteTensor calculateInto(ByteTensor t, float value, NumericFunction2 function) {
        FloatTensor r = TensorUtil.asFloatTensor(t); 
        calculate(r,uniformFactory.initializedFloatTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public ByteTensor calculateInto(ByteTensor t, double value, NumericFunction2 function) {
        DoubleTensor r = TensorUtil.asDoubleTensor(t); 
        calculate(r,uniformFactory.initializedDoubleTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public ShortTensor calculateInto(ShortTensor t, short value, NumericFunction2 function) {
        return calculate(t,uniformFactory.initializedShortTensor(value,t.getDimensions()),t,function);
    }

    public ShortTensor calculateInto(ShortTensor t, int value, NumericFunction2 function) {
        IntTensor r = TensorUtil.asIntTensor(t);
        calculate(r,uniformFactory.initializedIntTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public ShortTensor calculateInto(ShortTensor t, long value, NumericFunction2 function) {
        LongTensor r = TensorUtil.asLongTensor(t);
        calculate(r,uniformFactory.initializedLongTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public ShortTensor calculateInto(ShortTensor t, float value, NumericFunction2 function) {
        FloatTensor r = TensorUtil.asFloatTensor(t);
        calculate(r,uniformFactory.initializedFloatTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public ShortTensor calculateInto(ShortTensor t, double value, NumericFunction2 function) {
        DoubleTensor r = TensorUtil.asDoubleTensor(t);
        calculate(r,uniformFactory.initializedDoubleTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public IntTensor calculateInto(IntTensor t, int value, NumericFunction2 function) {
        return calculate(t,uniformFactory.initializedIntTensor(value,t.getDimensions()),t,function);
    }

    public IntTensor calculateInto(IntTensor t, long value, NumericFunction2 function) {
        LongTensor r = TensorUtil.asLongTensor(t);
        calculate(r,uniformFactory.initializedLongTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public IntTensor calculateInto(IntTensor t, float value, NumericFunction2 function) {
        FloatTensor r = TensorUtil.asFloatTensor(t);
        calculate(r,uniformFactory.initializedFloatTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public IntTensor calculateInto(IntTensor t, double value, NumericFunction2 function) {
        DoubleTensor r = TensorUtil.asDoubleTensor(t);
        calculate(r,uniformFactory.initializedDoubleTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public LongTensor calculateInto(LongTensor t, long value, NumericFunction2 function) {
        return calculate(t,uniformFactory.initializedLongTensor(value,t.getDimensions()),t,function);
    }

    public LongTensor calculateInto(LongTensor t, float value, NumericFunction2 function) {
        FloatTensor r = TensorUtil.asFloatTensor(t);
        calculate(r,uniformFactory.initializedFloatTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public LongTensor calculateInto(LongTensor t, double value, NumericFunction2 function) {
        DoubleTensor r = TensorUtil.asDoubleTensor(t);
        calculate(r,uniformFactory.initializedDoubleTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public FloatTensor calculateInto(FloatTensor t, float value, NumericFunction2 function) {
        return calculate(t,uniformFactory.initializedFloatTensor(value,t.getDimensions()),t,function);
    }

    public FloatTensor calculateInto(FloatTensor t, double value, NumericFunction2 function) {
        DoubleTensor r = TensorUtil.asDoubleTensor(t);
        calculate(r,uniformFactory.initializedDoubleTensor(value,t.getDimensions()),r,function);
        return t;
    }

    public DoubleTensor calculateInto(DoubleTensor t, double value, NumericFunction2 function) {
        return calculate(t,uniformFactory.initializedDoubleTensor(value,t.getDimensions()),t,function);
    }

    public ByteTensor calculateInto(ByteTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,t1,function);
        } else if (diff < 0) {
            throw new IllegalArgumentException(String.format("Second tensor cannot have more dimensions (%d) than first (%d) when calculating into.",t2.size(),t1.size()));
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,t1,function,mf[0],mf[1]);
    }

    public ShortTensor calculateInto(ShortTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asShortTensor(t2),function,matchingDimensions);
    }

    public ByteTensor calculateInto(ByteTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asShortTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public IntTensor calculateInto(IntTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asIntTensor(t2),function,matchingDimensions);
    }

    public ByteTensor calculateInto(ByteTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asIntTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public LongTensor calculateInto(LongTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asLongTensor(t2),function,matchingDimensions);
    }

    public ByteTensor calculateInto(ByteTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asLongTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public FloatTensor calculateInto(FloatTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asFloatTensor(t2),function,matchingDimensions);
    }

    public ByteTensor calculateInto(ByteTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asFloatTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public DoubleTensor calculateInto(DoubleTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asDoubleTensor(t2),function,matchingDimensions);
    }

    public ByteTensor calculateInto(ByteTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asDoubleTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public ShortTensor calculateInto(ShortTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,t1,function);
        } else if (diff < 0) {
            throw new IllegalArgumentException(String.format("Second tensor cannot have more dimensions (%d) than first (%d) when calculating into.",t2.size(),t1.size()));
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,t1,function,mf[0],mf[1]);
    }

    public IntTensor calculateInto(IntTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asIntTensor(t2),function,matchingDimensions);
    }

    public ShortTensor calculateInto(ShortTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asIntTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public LongTensor calculateInto(LongTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asLongTensor(t2),function,matchingDimensions);
        
    }

    public ShortTensor calculateInto(ShortTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asLongTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public FloatTensor calculateInto(FloatTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asFloatTensor(t2),function,matchingDimensions);
    }

    public ShortTensor calculateInto(ShortTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asFloatTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public DoubleTensor calculateInto(DoubleTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asDoubleTensor(t2),function,matchingDimensions);
    }

    public ShortTensor calculateInto(ShortTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asDoubleTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public IntTensor calculateInto(IntTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,t1,function);
        } else if (diff < 0) {
            throw new IllegalArgumentException(String.format("Second tensor cannot have more dimensions (%d) than first (%d) when calculating into.",t2.size(),t1.size()));
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,t1,function,mf[0],mf[1]);
    }

    public LongTensor calculateInto(LongTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asLongTensor(t2),function,matchingDimensions);
    }

    public IntTensor calculateInto(IntTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asLongTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public FloatTensor calculateInto(FloatTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asFloatTensor(t2),function,matchingDimensions);
    }

    public IntTensor calculateInto(IntTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asFloatTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public DoubleTensor calculateInto(DoubleTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asDoubleTensor(t2),function,matchingDimensions);
    }

    public IntTensor calculateInto(IntTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asDoubleTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public LongTensor calculateInto(LongTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,t1,function);
        } else if (diff < 0) {
            throw new IllegalArgumentException(String.format("Second tensor cannot have more dimensions (%d) than first (%d) when calculating into.",t2.size(),t1.size()));
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,t1,function,mf[0],mf[1]);
    }

    public FloatTensor calculateInto(FloatTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asFloatTensor(t2),function,matchingDimensions);
    }

    public LongTensor calculateInto(LongTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asFloatTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public DoubleTensor calculateInto(DoubleTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asDoubleTensor(t2),function,matchingDimensions);
    }

    public LongTensor calculateInto(LongTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asDoubleTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public FloatTensor calculateInto(FloatTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,t1,function);
        } else if (diff < 0) {
            throw new IllegalArgumentException(String.format("Second tensor cannot have more dimensions (%d) than first (%d) when calculating into.",t2.size(),t1.size()));
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,t1,function,mf[0],mf[1]);
    }

    public DoubleTensor calculateInto(DoubleTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        return calculateInto(t1,TensorUtil.asDoubleTensor(t2),function,matchingDimensions);
    }

    public FloatTensor calculateInto(FloatTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        calculateInto(TensorUtil.asDoubleTensor(t1),t2,function,matchingDimensions);
        return t1;
    }

    public DoubleTensor calculateInto(DoubleTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions) {
        int diff = compareTensorSizes(t1,t2);
        if (diff == 0 && matchingDimensions == null) {
            if ((matchingDimensions = checkMatchingTensors(t1,t2)) != null)
                return calculate(t1,t2,function,matchingDimensions);
            return calculate(t1,t2,t1,function);
        } else if (diff < 0) {
            throw new IllegalArgumentException(String.format("Second tensor cannot have more dimensions (%d) than first (%d) when calculating into.",t2.size(),t1.size()));
        }
        int[][] mf = getMatchingAndFreeDims(t1,t2,matchingDimensions);
        return calculate(t1,t2,t1,function,mf[0],mf[1]);
    }
    
    public ByteTensor calculateInto(ByteTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ShortTensor calculateInto(ShortTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ByteTensor calculateInto(ByteTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public IntTensor calculateInto(IntTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ByteTensor calculateInto(ByteTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public LongTensor calculateInto(LongTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ByteTensor calculateInto(ByteTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public FloatTensor calculateInto(FloatTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ByteTensor calculateInto(ByteTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public DoubleTensor calculateInto(DoubleTensor t1, ByteTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ByteTensor calculateInto(ByteTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ShortTensor calculateInto(ShortTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public IntTensor calculateInto(IntTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ShortTensor calculateInto(ShortTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public LongTensor calculateInto(LongTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ShortTensor calculateInto(ShortTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public FloatTensor calculateInto(FloatTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ShortTensor calculateInto(ShortTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public DoubleTensor calculateInto(DoubleTensor t1, ShortTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public ShortTensor calculateInto(ShortTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public IntTensor calculateInto(IntTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public LongTensor calculateInto(LongTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public IntTensor calculateInto(IntTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public FloatTensor calculateInto(FloatTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public IntTensor calculateInto(IntTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public DoubleTensor calculateInto(DoubleTensor t1, IntTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public IntTensor calculateInto(IntTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public LongTensor calculateInto(LongTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public FloatTensor calculateInto(FloatTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public LongTensor calculateInto(LongTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public DoubleTensor calculateInto(DoubleTensor t1, LongTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public LongTensor calculateInto(LongTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public FloatTensor calculateInto(FloatTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public DoubleTensor calculateInto(DoubleTensor t1, FloatTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public FloatTensor calculateInto(FloatTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }

    public DoubleTensor calculateInto(DoubleTensor t1, DoubleTensor t2, NumericFunction2 function) {
        return calculateInto(t1,t2,function,null);
    }
    
    private Tensor<?> getMatchedTensor(int[] dims, Tensor<?> t) {  
        if (t.size() == dims.length) {
            if (Arrays.equals(dims,t.getDimensions()))
                return t;
            else
                throw new IllegalArgumentException(String.format("Tensor parameter size (%s) must equal result size (%s).",Arrays.toString(t.getDimensions()),Arrays.toString(dims)));
        }
        //build index
        Map<Integer,Integer> fixedDimensions = new HashMap<Integer,Integer>();
        for (int i : getMatchingAndFreeDims(dims,t.getDimensions())[1])
            fixedDimensions.put(i,dims[i]);
        return t.getReferenceTensor(MirrorIndex.getStandardMirrorIndex(t.getIndex(),fixedDimensions));
    }

    private ByteTensor getMatchedByteTensor(int[] dims, Tensor<? extends Number> input) {
        return (ByteTensor) getMatchedTensor(dims,TensorUtil.asByteTensor(input));
    }                                

    private ShortTensor getMatchedShortTensor(int[] dims, Tensor<? extends Number> input) {
        return (ShortTensor) getMatchedTensor(dims,TensorUtil.asShortTensor(input));
    }

    private IntTensor getMatchedIntTensor(int[] dims, Tensor<? extends Number> input) {
        return (IntTensor) getMatchedTensor(dims,TensorUtil.asIntTensor(input));
    }

    private LongTensor getMatchedLongTensor(int[] dims, Tensor<? extends Number> input) {
        return (LongTensor) getMatchedTensor(dims,TensorUtil.asLongTensor(input));
    }

    private FloatTensor getMatchedFloatTensor(int[] dims, Tensor<? extends Number> input) {
        return (FloatTensor) getMatchedTensor(dims,TensorUtil.asFloatTensor(input));
    }

    private DoubleTensor getMatchedDoubleTensor(int[] dims, Tensor<? extends Number> input) {
        return (DoubleTensor) getMatchedTensor(dims,TensorUtil.asDoubleTensor(input));
    }

    private Number castNumber(Number input, JavaType outputType) {
        switch (outputType) {
            case DOUBLE :
            case FLOAT : return input;
            default : return (input instanceof Double || input instanceof Float) ? Math.round(input.doubleValue()) : input;
        }
    }
    
    @SuppressWarnings("unchecked") //will be numeric tensor
    public List<? extends Tensor<? extends Number>> getCalculationParameters(List<?> parameters) {
        List<Tensor<? extends Number>> params = new LinkedList<Tensor<? extends Number>>();
        for (Object o : parameters) {
            if (o instanceof Number) {
                switch (JavaType.getPrimitiveJavaType(o.getClass())) {
                    case BYTE : params.add(factory.initializedByteScalar(((Number) o).byteValue())); break;
                    case SHORT : params.add(factory.initializedShortScalar(((Number) o).shortValue())); break;
                    case INT : params.add(factory.initializedIntScalar(((Number) o).intValue())); break;
                    case LONG : params.add(factory.initializedLongScalar(((Number) o).longValue())); break;
                    case FLOAT : params.add(factory.initializedFloatScalar(((Number) o).floatValue())); break;
                    case DOUBLE : params.add(factory.initializedDoubleScalar(((Number) o).doubleValue())); break;
                }
            } else if (o instanceof Tensor && ((Tensor<?>) o).getType().isNumeric()) {
                params.add((Tensor<? extends Number>) o);
            } else {
                throw new IllegalArgumentException(String.format("Invalid parameter type, must be numeric tensor or number: %s (%s)",o.toString(),o.getClass().toString()));
            }
        }
        return params;
    }
          
    private List<ByteTensor> getByteParameters(int[] dims, List<? extends Tensor<? extends Number>> inParameters) {
        List<ByteTensor> parameters = new LinkedList<ByteTensor>();
        for (Tensor<? extends Number> t : inParameters) 
            parameters.add(t.size() == 0 ? uniformFactory.initializedByteTensor(castNumber(t.getValue(),JavaType.BYTE).byteValue(),dims) :
                                           getMatchedByteTensor(dims,t));
        return parameters;
    }  
        
    private List<ShortTensor> getShortParameters(int[] dims, List<? extends Tensor<? extends Number>> inParameters) {
        List<ShortTensor> parameters = new LinkedList<ShortTensor>();
        for (Tensor<? extends Number> t : inParameters) 
            parameters.add(t.size() == 0 ? uniformFactory.initializedShortTensor(castNumber(t.getValue(),JavaType.SHORT).shortValue(),dims) :
                                           getMatchedShortTensor(dims,t));
        return parameters;
    }
           
    private List<IntTensor> getIntParameters(int[] dims, List<? extends Tensor<? extends Number>> inParameters) {
        List<IntTensor> parameters = new LinkedList<IntTensor>();
        for (Tensor<? extends Number> t : inParameters) 
            parameters.add(t.size() == 0 ? uniformFactory.initializedIntTensor(castNumber(t.getValue(),JavaType.INT).intValue(),dims) :
                                           getMatchedIntTensor(dims,t));
        return parameters;
    }
                         
    private List<LongTensor> getLongParameters(int[] dims, List<? extends Tensor<? extends Number>> inParameters) {
        List<LongTensor> parameters = new LinkedList<LongTensor>();
        for (Tensor<? extends Number> t : inParameters) 
            parameters.add(t.size() == 0 ? uniformFactory.initializedLongTensor(castNumber(t.getValue(),JavaType.LONG).longValue(),dims) :
                                           getMatchedLongTensor(dims,t));
        return parameters;
    }
   
    private List<FloatTensor> getFloatParameters(int[] dims, List<? extends Tensor<? extends Number>> inParameters) {
        List<FloatTensor> parameters = new LinkedList<FloatTensor>();
        for (Tensor<? extends Number> t : inParameters) 
            parameters.add(t.size() == 0 ? uniformFactory.initializedFloatTensor(castNumber(t.getValue(),JavaType.FLOAT).floatValue(),dims) :
                                           getMatchedFloatTensor(dims,t));
        return parameters;
    }

    private List<DoubleTensor> getDoubleParameters(int[] dims, List<? extends Tensor<? extends Number>> inParameters) {
        List<DoubleTensor> parameters = new LinkedList<DoubleTensor>();
        for (Tensor<? extends Number> t : inParameters) 
            parameters.add(t.size() == 0 ? uniformFactory.initializedDoubleTensor(castNumber(t.getValue(),JavaType.DOUBLE).doubleValue(),dims) :
                                           getMatchedDoubleTensor(dims,t));
        return parameters;
    }
    
    private static final Map<JavaType,Integer> typePrecedence = new EnumMap<JavaType,Integer>(JavaType.class);
    static {                                  
        typePrecedence.put(JavaType.OBJECT,0);
        typePrecedence.put(JavaType.BYTE,1);
        typePrecedence.put(JavaType.SHORT,2);
        typePrecedence.put(JavaType.INT,3);
        typePrecedence.put(JavaType.LONG,4);
        typePrecedence.put(JavaType.FLOAT,5);
        typePrecedence.put(JavaType.DOUBLE,6);
    }
    
    private JavaType getHighestJavaType(List<? extends Tensor<? extends Number>> parameters) {
        JavaType t = null;
        for (Tensor<?> tensor : parameters) {
            JavaType newType = tensor.getType();
            if ((t == null) || (t != newType && typePrecedence.get(newType) > typePrecedence.get(t)))
                t = newType;
        }
        return t == JavaType.OBJECT ? JavaType.DOUBLE : t; //if type unknown, return double
    }
    
    private int[] getResultDimensions(List<? extends Tensor<? extends Number>> parameters) {
        int[] dims = null;
        for (Tensor<?> t : parameters)
            if (dims == null || t.size() > dims.length)
                dims = t.getDimensions();
        return dims;
    }
    
    public Tensor<? extends Number> calculate(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters) {
        JavaType type = getHighestJavaType(parameters);
        int[] resultDims = getResultDimensions(parameters);
        switch (type) {
            case BYTE : return calculate(function,getByteParameters(resultDims,parameters).toArray(new ByteTensor[parameters.size()]),factory.byteTensor(resultDims));
            case SHORT : return calculate(function,getShortParameters(resultDims,parameters).toArray(new ShortTensor[parameters.size()]),factory.shortTensor(resultDims));
            case INT : return calculate(function,getIntParameters(resultDims,parameters).toArray(new IntTensor[parameters.size()]),factory.intTensor(resultDims));
            case LONG : return calculate(function,getLongParameters(resultDims,parameters).toArray(new LongTensor[parameters.size()]),factory.longTensor(resultDims));
            case FLOAT : return calculate(function,getFloatParameters(resultDims,parameters).toArray(new FloatTensor[parameters.size()]),factory.floatTensor(resultDims));
            case DOUBLE : return calculate(function,getDoubleParameters(resultDims,parameters).toArray(new DoubleTensor[parameters.size()]),factory.doubleTensor(resultDims));
            default : throw new IllegalStateException("Should not be here...");
        }
    }
    
    public ByteTensor calculateByte(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters) {
        int[] resultDims = getResultDimensions(parameters);
        return calculate(function,getByteParameters(resultDims,parameters).toArray(new ByteTensor[parameters.size()]),factory.byteTensor(resultDims));
    } 
    
    public ShortTensor calculateShort(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters) {
        int[] resultDims = getResultDimensions(parameters);
        return calculate(function,getShortParameters(resultDims,parameters).toArray(new ShortTensor[parameters.size()]),factory.shortTensor(resultDims));
    }
    
    public IntTensor calculateInt(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters) {
        int[] resultDims = getResultDimensions(parameters);
        return calculate(function,getIntParameters(resultDims,parameters).toArray(new IntTensor[parameters.size()]),factory.intTensor(resultDims));
    }
    
    public LongTensor calculateLong(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters) {
        int[] resultDims = getResultDimensions(parameters);
        return calculate(function,getLongParameters(resultDims,parameters).toArray(new LongTensor[parameters.size()]),factory.longTensor(resultDims));
    }
    
    public FloatTensor calculateFloat(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters) {
        int[] resultDims = getResultDimensions(parameters);
        return calculate(function,getFloatParameters(resultDims,parameters).toArray(new FloatTensor[parameters.size()]),factory.floatTensor(resultDims));
    }
    
    public DoubleTensor calculateDouble(NumericFunctionN function, List<? extends Tensor<? extends Number>> parameters) {
        int[] resultDims = getResultDimensions(parameters);
        return calculate(function,getDoubleParameters(resultDims,parameters).toArray(new DoubleTensor[parameters.size()]),factory.doubleTensor(resultDims));
    }
}
