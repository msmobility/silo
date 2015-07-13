package com.pb.sawdust.calculator.tensor.la.mm;

import com.pb.sawdust.tensor.alias.matrix.primitive.*;
import com.pb.sawdust.tensor.alias.vector.primitive.*;

/**
 * The {@code MatrixVectorMultiplication} interface defines methods for calculating matrix multiplication between a vector
 * and a matrix. Matrix multiplication here is defined "linear-algebraically" - that is:
 * <pre><tt>
 *     O<sub>i</sub> = M1<sub>i,1</sub>*V2<sub>i</sub> + ... + M1<sub>i,N</sub>*V2<sub>N</sub>
 * </tt></pre>
 * where <tt>M1</tt> and <tt>V2</tt> are the input matrix and vector, <tt>O</tt> is the result vector, and <tt>N</tt> is
 * the number elements in the vector.
 *
 * @author crf <br/>
 *         Started Dec 13, 2010 6:51:57 AM
 */
public interface MatrixVectorMultiplication {
    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public ByteVector multiply(ByteVector v1, ByteMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public ShortVector multiply(ShortVector v1, ShortMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code short}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public ShortVector multiply(ShortVector v1, ByteMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code short}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public ShortVector multiply(ByteVector v1, ShortMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public IntVector multiply(IntVector v1, IntMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code int}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public IntVector multiply(IntVector v1, ByteMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code int}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public IntVector multiply(ByteVector v1, IntMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code int}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public IntVector multiply(IntVector v1, ShortMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code int}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public IntVector multiply(ShortVector v1, IntMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public LongVector multiply(LongVector v1, LongMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public LongVector multiply(LongVector v1, ByteMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public LongVector multiply(ByteVector v1, LongMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public LongVector multiply(LongVector v1, ShortMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public LongVector multiply(ShortVector v1, LongMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public LongVector multiply(LongVector v1, IntMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public LongVector multiply(IntVector v1, LongMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public FloatVector multiply(FloatVector v1, FloatMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public FloatVector multiply(FloatVector v1, ByteMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code float}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public FloatVector multiply(ByteVector v1, FloatMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code float}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public FloatVector multiply(FloatVector v1, ShortMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code float}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public FloatVector multiply(ShortVector v1, FloatMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code float}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public FloatVector multiply(FloatVector v1, IntMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code float}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public FloatVector multiply(IntVector v1, FloatMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public FloatVector multiply(FloatVector v1, LongMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public FloatVector multiply(LongVector v1, FloatMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix. 
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(DoubleVector v1, DoubleMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(DoubleVector v1, ByteMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(ByteVector v1, DoubleMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(DoubleVector v1, ShortMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(ShortVector v1, DoubleMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(DoubleVector v1, IntMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(IntVector v1, DoubleMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(DoubleVector v1, LongMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(LongVector v1, DoubleMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(DoubleVector v1, FloatMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a vector and a matrix, specifying whether the matrix should be transposed or not.
     * The vector will be transposed to align with the matrix.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param v1
     *        The vector.
     *
     * @param m2
     *        The matrix.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code v1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>v1.size(0) != m2.size(1)<code> if the matrix is transposed.
     */
    public DoubleVector multiply(FloatVector v1, DoubleMatrix m2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public ByteVector multiply(ByteMatrix m1, ByteVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public ShortVector multiply(ShortMatrix m1, ShortVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code short}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public ShortVector multiply(ShortMatrix m1, ByteVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code short}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public ShortVector multiply(ByteMatrix m1, ShortVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public IntVector multiply(IntMatrix m1, IntVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code int}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public IntVector multiply(IntMatrix m1, ByteVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code int}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public IntVector multiply(ByteMatrix m1, IntVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code int}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public IntVector multiply(IntMatrix m1, ShortVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code int}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public IntVector multiply(ShortMatrix m1, IntVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public LongVector multiply(LongMatrix m1, LongVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public LongVector multiply(LongMatrix m1, ByteVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public LongVector multiply(ByteMatrix m1, LongVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public LongVector multiply(LongMatrix m1, ShortVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public LongVector multiply(ShortMatrix m1, LongVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public LongVector multiply(LongMatrix m1, IntVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public LongVector multiply(IntMatrix m1, LongVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public FloatVector multiply(FloatMatrix m1, FloatVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public FloatVector multiply(FloatMatrix m1, ByteVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code float}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public FloatVector multiply(ByteMatrix m1, FloatVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code float}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public FloatVector multiply(FloatMatrix m1, ShortVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code float}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public FloatVector multiply(ShortMatrix m1, FloatVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code float}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public FloatVector multiply(FloatMatrix m1, IntVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code float}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public FloatVector multiply(IntMatrix m1, FloatVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public FloatVector multiply(FloatMatrix m1, LongVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public FloatVector multiply(LongMatrix m1, FloatVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(DoubleMatrix m1, DoubleVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(DoubleMatrix m1, ByteVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(ByteMatrix m1, DoubleVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(DoubleMatrix m1, ShortVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(ShortMatrix m1, DoubleVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(DoubleMatrix m1, IntVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(IntMatrix m1, DoubleVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(DoubleMatrix m1, LongVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(LongMatrix m1, DoubleVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(DoubleMatrix m1, FloatVector v2, boolean transposeMatrix);

    /**
     * Matrix multiply a matrix and a vector, specifying whether the matrix should be transposed or not.  The values in the vector and matrix
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The matrix.
     *
     * @param v2
     *        The vector.
     *
     * @param transposeMatrix
     *        If {@code true}, the multiplication will be carried out on the transpose of the matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code v2}.
     *
     * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>
     *                                  if the matrix is not transposed, or <code>m1.size(0) != v2.size(0)<code> if the matrix is transposed..
     */
    public DoubleVector multiply(FloatMatrix m1, DoubleVector v2, boolean transposeMatrix);

    /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public ByteVector multiply(ByteVector v1, ByteMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public ShortVector multiply(ShortVector v1, ShortMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code short}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public ShortVector multiply(ShortVector v1, ByteMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code short}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public ShortVector multiply(ByteVector v1, ShortMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public IntVector multiply(IntVector v1, IntMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code int}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public IntVector multiply(IntVector v1, ByteMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code int}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public IntVector multiply(ByteVector v1, IntMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code int}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public IntVector multiply(IntVector v1, ShortMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code int}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public IntVector multiply(ShortVector v1, IntMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public LongVector multiply(LongVector v1, LongMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public LongVector multiply(LongVector v1, ByteMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public LongVector multiply(ByteVector v1, LongMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public LongVector multiply(LongVector v1, ShortMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public LongVector multiply(ShortVector v1, LongMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public LongVector multiply(LongVector v1, IntMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public LongVector multiply(IntVector v1, LongMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public FloatVector multiply(FloatVector v1, FloatMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public FloatVector multiply(FloatVector v1, ByteMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code float}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public FloatVector multiply(ByteVector v1, FloatMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code float}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public FloatVector multiply(FloatVector v1, ShortMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code float}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public FloatVector multiply(ShortVector v1, FloatMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code float}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public FloatVector multiply(FloatVector v1, IntMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code float}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public FloatVector multiply(IntVector v1, FloatMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public FloatVector multiply(FloatVector v1, LongMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public FloatVector multiply(LongVector v1, FloatMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleVector v1, DoubleMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleVector v1, ByteMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(ByteVector v1, DoubleMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleVector v1, ShortMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(ShortVector v1, DoubleMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleVector v1, IntMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(IntVector v1, DoubleMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleVector v1, LongMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(LongVector v1, DoubleMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleVector v1, FloatMatrix m2);

        /**
         * Matrix multiply a vector and a matrix.
         * The vector will be transposed to align with the matrix.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param v1
         *        The vector.
         *
         * @param m2
         *        The matrix.
         *
         * @return the matrix multiplication of {@code v1} and {@code m2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>v1.size(0) != m2.size(0)</code>.
         */
        public DoubleVector multiply(FloatVector v1, DoubleMatrix m2);

        /**
         * Matrix multiply a matrix and a vector.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public ByteVector multiply(ByteMatrix m1, ByteVector v2);

        /**
         * Matrix multiply a matrix and a vector.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public ShortVector multiply(ShortMatrix m1, ShortVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code short}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public ShortVector multiply(ShortMatrix m1, ByteVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code short}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public ShortVector multiply(ByteMatrix m1, ShortVector v2);

        /**
         * Matrix multiply a matrix and a vector.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public IntVector multiply(IntMatrix m1, IntVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code int}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public IntVector multiply(IntMatrix m1, ByteVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code int}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public IntVector multiply(ByteMatrix m1, IntVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code int}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public IntVector multiply(IntMatrix m1, ShortVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code int}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public IntVector multiply(ShortMatrix m1, IntVector v2);

        /**
         * Matrix multiply a matrix and a vector.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public LongVector multiply(LongMatrix m1, LongVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public LongVector multiply(LongMatrix m1, ByteVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public LongVector multiply(ByteMatrix m1, LongVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public LongVector multiply(LongMatrix m1, ShortVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public LongVector multiply(ShortMatrix m1, LongVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public LongVector multiply(LongMatrix m1, IntVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public LongVector multiply(IntMatrix m1, LongVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code long}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public FloatVector multiply(FloatMatrix m1, FloatVector v2);

        /**
         * Matrix multiply a matrix and a vector.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public FloatVector multiply(FloatMatrix m1, ByteVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code float}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public FloatVector multiply(ByteMatrix m1, FloatVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code float}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public FloatVector multiply(FloatMatrix m1, ShortVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code float}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public FloatVector multiply(ShortMatrix m1, FloatVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code float}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public FloatVector multiply(FloatMatrix m1, IntVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code float}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public FloatVector multiply(IntMatrix m1, FloatVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public FloatVector multiply(FloatMatrix m1, LongVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public FloatVector multiply(LongMatrix m1, FloatVector v2);

        /**
         * Matrix multiply a matrix and a vector.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleMatrix m1, DoubleVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleMatrix m1, ByteVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(ByteMatrix m1, DoubleVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleMatrix m1, ShortVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(ShortMatrix m1, DoubleVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleMatrix m1, IntVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(IntMatrix m1, DoubleVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleMatrix m1, LongVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(LongMatrix m1, DoubleVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(DoubleMatrix m1, FloatVector v2);

        /**
         * Matrix multiply a matrix and a vector.  The values in the vector and matrix  will be treated as {@code double}s.
         *
         * @param m1
         *        The matrix.
         *
         * @param v2
         *        The vector.
         *
         * @return the matrix multiplication of {@code m1} and {@code v2}.
         *
         * @throws IllegalArgumentException if the matrix and vector do not align for multiplication. <i>i.e.</i> <code>m1.size(1) != v2.size(0)</code>.
         */
        public DoubleVector multiply(FloatMatrix m1, DoubleVector v2);
}
