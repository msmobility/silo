package com.pb.sawdust.calculator.tensor;

import com.pb.sawdust.calculator.NumericFunction1;
import com.pb.sawdust.calculator.NumericFunction2;
import com.pb.sawdust.tensor.decorators.primitive.*;

/**
 * The {@code CellWiseTensorMutatingCalculation} specifies cell-wise tensor calculations which modify one of the tensor
 * arguments to a calculation.  See {@link com.pb.sawdust.calculator.tensor.CellWiseTensorCalculation} for more
 * information about cell-wise tensor calculations.  Mutating composite tensor calculations (<i>i.e.</i> calculations
 * involving more than two tensors) are not specified through this interface, both because specifying the tensor to
 * mutate adds unnecessary complication, and because the savings or convenience derived from such a mutating calculation
 * are not necessarily evident (because it already involves a number of tensors).
 *
 * @see com.pb.sawdust.calculator.tensor.CellWiseTensorCalculation
 *
 * @author crf <br/>
 *         Started Nov 20, 2010 5:50:42 PM
 */
public interface CellWiseTensorMutatingCalculation {

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code byte}s, placing the results in the input
     * tensor
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code t}, once all of its elements have been changed by an application of {@code function}.
     */
    ByteTensor calculateInto(ByteTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code short}s, placing the results in the input
     * tensor
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code t}, once all of its elements have been changed by an application of {@code function}.
     */
    ShortTensor calculateInto(ShortTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code int}s, placing the results in the input
     * tensor
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code t}, once all of its elements have been changed by an application of {@code function}.
     */
    IntTensor calculateInto(IntTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code long}s, placing the results in the input
     * tensor
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code t}, once all of its elements have been changed by an application of {@code function}.
     */
    LongTensor calculateInto(LongTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code float}s, placing the results in the input
     * tensor
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code t}, once all of its elements have been changed by an application of {@code function}.
     */
    FloatTensor calculateInto(FloatTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise unary calculation on a tensor holding {@code double}s, placing the results in the input
     * tensor
     *
     * @param t
     *        The input tensor.
     *
     * @param function
     *        The function to apply to the tensor cells.
     *
     * @return {@code t}, once all of its elements have been changed by an application of {@code function}.
     */
    DoubleTensor calculateInto(DoubleTensor t, NumericFunction1 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code byte} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ByteTensor calculateInto(ByteTensor t, byte value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code short} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ByteTensor calculateInto(ByteTensor t, short value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code int} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ByteTensor calculateInto(ByteTensor t, int value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code long} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ByteTensor calculateInto(ByteTensor t, long value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code float} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ByteTensor calculateInto(ByteTensor t, float value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code byte}s and a {@code double} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ByteTensor calculateInto(ByteTensor t, double value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code short}s and a {@code short} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ShortTensor calculateInto(ShortTensor t, short value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code short}s and a {@code int} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ShortTensor calculateInto(ShortTensor t, int value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code short}s and a {@code long} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ShortTensor calculateInto(ShortTensor t, long value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code short}s and a {@code float} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ShortTensor calculateInto(ShortTensor t, float value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code short}s and a {@code double} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    ShortTensor calculateInto(ShortTensor t, double value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code int}s and a {@code int} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    IntTensor calculateInto(IntTensor t, int value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code int}s and a {@code long} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    IntTensor calculateInto(IntTensor t, long value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code int}s and a {@code float} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    IntTensor calculateInto(IntTensor t, float value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code int}s and a {@code double} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    IntTensor calculateInto(IntTensor t, double value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code long}s and a {@code long} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    LongTensor calculateInto(LongTensor t, long value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code long}s and a {@code float} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    LongTensor calculateInto(LongTensor t, float value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code long}s and a {@code double} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    LongTensor calculateInto(LongTensor t, double value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code float}s and a {@code float} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    FloatTensor calculateInto(FloatTensor t, float value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code float}s and a {@code double} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    FloatTensor calculateInto(FloatTensor t, double value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between a tensor holding {@code double}s and a {@code double} value,
     * saving the result in the tensor passed to this method. To apply the function with the arguments switched, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t
     *        The tensor holding the first arguments to the function.
     *
     * @param value
     *        The second argument to the function.
     *
     * @param function
     *        The function to calculate.
     *
     * @return {@code t}, after it has been updated with itself and {@code value} applied to {@code function}.
     */
    DoubleTensor calculateInto(DoubleTensor t, double value, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ByteTensor calculateInto(ByteTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ShortTensor calculateInto(ShortTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ByteTensor calculateInto(ByteTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    IntTensor calculateInto(IntTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ByteTensor calculateInto(ByteTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    LongTensor calculateInto(LongTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ByteTensor calculateInto(ByteTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    FloatTensor calculateInto(FloatTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ByteTensor calculateInto(ByteTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    DoubleTensor calculateInto(DoubleTensor t1, ByteTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ByteTensor calculateInto(ByteTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ShortTensor calculateInto(ShortTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    IntTensor calculateInto(IntTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ShortTensor calculateInto(ShortTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    LongTensor calculateInto(LongTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ShortTensor calculateInto(ShortTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    FloatTensor calculateInto(FloatTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ShortTensor calculateInto(ShortTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    DoubleTensor calculateInto(DoubleTensor t1, ShortTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    ShortTensor calculateInto(ShortTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    IntTensor calculateInto(IntTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    LongTensor calculateInto(LongTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    IntTensor calculateInto(IntTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    FloatTensor calculateInto(FloatTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    IntTensor calculateInto(IntTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    DoubleTensor calculateInto(DoubleTensor t1, IntTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    IntTensor calculateInto(IntTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    LongTensor calculateInto(LongTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    FloatTensor calculateInto(FloatTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    LongTensor calculateInto(LongTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    DoubleTensor calculateInto(DoubleTensor t1, LongTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    LongTensor calculateInto(LongTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    FloatTensor calculateInto(FloatTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    DoubleTensor calculateInto(DoubleTensor t1, FloatTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    FloatTensor calculateInto(FloatTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors with the specified matching dimensions for second tensor,
     * writing the results into the first tensor, which must have a dimension count greater than or equal to the second.
     * A discussion of matching dimensions is found in this class's documentation. To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @param matchingDimensions
     *        The matching dimensions for {@code t2}.
     *
     * @return {@code t1}, after its contents have been updated by the application of itself and {@code t2} applied to {@code function}.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the size of {@code matchingDimensions} does not equal {@code t2.size()}, if any element of {@code matchingDimensions}
     *                                  does not correspond to a valid dimension in {@code t1}s, or if any value in {@code matchingDimensions} is repeated.
     */
    DoubleTensor calculateInto(DoubleTensor t1, DoubleTensor t2, NumericFunction2 function, int[] matchingDimensions);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ByteTensor calculateInto(ByteTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ShortTensor calculateInto(ShortTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ByteTensor calculateInto(ByteTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    IntTensor calculateInto(IntTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ByteTensor calculateInto(ByteTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    LongTensor calculateInto(LongTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ByteTensor calculateInto(ByteTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    FloatTensor calculateInto(FloatTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ByteTensor calculateInto(ByteTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    DoubleTensor calculateInto(DoubleTensor t1, ByteTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ByteTensor calculateInto(ByteTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ShortTensor calculateInto(ShortTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    IntTensor calculateInto(IntTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ShortTensor calculateInto(ShortTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    LongTensor calculateInto(LongTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ShortTensor calculateInto(ShortTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    FloatTensor calculateInto(FloatTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ShortTensor calculateInto(ShortTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    DoubleTensor calculateInto(DoubleTensor t1, ShortTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    ShortTensor calculateInto(ShortTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    IntTensor calculateInto(IntTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    LongTensor calculateInto(LongTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    IntTensor calculateInto(IntTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    FloatTensor calculateInto(FloatTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    IntTensor calculateInto(IntTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    DoubleTensor calculateInto(DoubleTensor t1, IntTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    IntTensor calculateInto(IntTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    LongTensor calculateInto(LongTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    FloatTensor calculateInto(FloatTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    LongTensor calculateInto(LongTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    DoubleTensor calculateInto(DoubleTensor t1, LongTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    LongTensor calculateInto(LongTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    FloatTensor calculateInto(FloatTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    DoubleTensor calculateInto(DoubleTensor t1, FloatTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * Note that some numerical accuracy of the function may be lost in the returned tensor.
     *
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    FloatTensor calculateInto(FloatTensor t1, DoubleTensor t2, NumericFunction2 function);

    /**
     * Perform an cell-wise calculation between two tensors, saving the results in the first tensor, which must have
     * a dimension count greater than or equal to the second.
     * If they both have the same number of dimensions, then their shapes must be identical.
     * Otherwise, the automatic dimension matching procedure discussed at the top of this class's documentation will be used.
     * To apply the function in with the arguments reversed, use
     * {@link com.pb.sawdust.calculator.NumericFunction2#mirror(com.pb.sawdust.calculator.NumericFunction2)}.
     * @param t1
     *        The tensor holding the first arguments to the function. This tensor must have a dimension count greater than or equal to {@code t2}.
     *
     * @param t2
     *        The tensor holding the second arguments to the function. This tensor must have a dimension count less than or equal to {@code t1}.
     *
     * @param function
     *        The function to calculate.
     *
     * @return the result of {@code t1} and {@code t2} applied to {@code function}. The result tensor will always have a shape equal
     * to that of the larger (in terms of dimension count) of the two input tensors.
     *
     * @throws IllegalArgumentException if <code>t1.size() < t2.size()</code>, if the two tensors have equal dimension counts but different shapes,
     *                                  or if the dimenson matching procedure cannot align the tensors.
     */
    DoubleTensor calculateInto(DoubleTensor t1, DoubleTensor t2, NumericFunction2 function);
}
