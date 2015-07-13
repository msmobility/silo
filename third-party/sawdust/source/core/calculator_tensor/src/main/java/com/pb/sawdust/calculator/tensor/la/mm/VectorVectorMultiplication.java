package com.pb.sawdust.calculator.tensor.la.mm;

import com.pb.sawdust.tensor.alias.scalar.primitive.*;
import com.pb.sawdust.tensor.alias.vector.primitive.*;

/**
 * The {@code VectorVectorMultiplication} interface defines methods for calculating matrix multiplication between two vectors.
 * Matrix multiplication here is defined "linear-algebraically" - that is:
 * <pre><tt>
 *     O = V1<sub>1</sub>*V2<sub>1</sub> + ... + V1<sub>N</sub>*V2<sub>N</sub>
 * </tt></pre>
 * where <tt>V1</tt> and <tt>V2</tt> are the input vectors, <tt>O</tt> is the result value/scalar, and <tt>N</tt> is 
 * the number elements in the vectors.
 *
 * @author crf <br/>
 *         Started Dec 13, 2010 6:52:35 AM
 */
public interface VectorVectorMultiplication {

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public ByteScalar multiply(ByteVector v1, ByteVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public ShortScalar multiply(ShortVector v1, ShortVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code short}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public ShortScalar multiply(ShortVector v1, ByteVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code short}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public ShortScalar multiply(ByteVector v1, ShortVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public IntScalar multiply(IntVector v1, IntVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code int}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public IntScalar multiply(IntVector v1, ByteVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code int}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public IntScalar multiply(ByteVector v1, IntVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code int}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public IntScalar multiply(IntVector v1, ShortVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code int}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public IntScalar multiply(ShortVector v1, IntVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public LongScalar multiply(LongVector v1, LongVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public LongScalar multiply(LongVector v1, ByteVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public LongScalar multiply(ByteVector v1, LongVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public LongScalar multiply(LongVector v1, ShortVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public LongScalar multiply(ShortVector v1, LongVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public LongScalar multiply(LongVector v1, IntVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public LongScalar multiply(IntVector v1, LongVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public FloatScalar multiply(FloatVector v1, FloatVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public FloatScalar multiply(FloatVector v1, ByteVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code float}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public FloatScalar multiply(ByteVector v1, FloatVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code float}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public FloatScalar multiply(FloatVector v1, ShortVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code float}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public FloatScalar multiply(ShortVector v1, FloatVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code float}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public FloatScalar multiply(FloatVector v1, IntVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code float}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public FloatScalar multiply(IntVector v1, FloatVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public FloatScalar multiply(FloatVector v1, LongVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public FloatScalar multiply(LongVector v1, FloatVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(DoubleVector v1, DoubleVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(DoubleVector v1, ByteVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(ByteVector v1, DoubleVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(DoubleVector v1, ShortVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(ShortVector v1, DoubleVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(DoubleVector v1, IntVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(IntVector v1, DoubleVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(DoubleVector v1, LongVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(LongVector v1, DoubleVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(DoubleVector v1, FloatVector v2);

    /**
     * Matrix multiply two vectors. The first vector will be transposed to align with the second.  The values in the vectors
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The first vector.
     *
     * @param v2
     *        The second vector.
     *
     * @return the matrix multiplication of {@code v1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the vectors do not align for multiplication. <i>i.e.</i> if <code>v1.size(0) != v1.size(0)</code>
     */
    public DoubleScalar multiply(FloatVector v1, DoubleVector v2);
}
