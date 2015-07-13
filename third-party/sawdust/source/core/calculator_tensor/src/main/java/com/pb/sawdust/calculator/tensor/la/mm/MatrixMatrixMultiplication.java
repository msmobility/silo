package com.pb.sawdust.calculator.tensor.la.mm;

import com.pb.sawdust.tensor.alias.matrix.primitive.*;

/**
 * The {@code MatrixMatrixMultiplication} interface defines methods for calculating matrix multiplication on two matrices.
 * Matrix multiplication here is defined "linear-algebraically" - that is:
 * <pre><tt>
 *     O<sub>i,j</sub> = M1<sub>i,1</sub>*M2<sub>1,j</sub> + ... + M1<sub>i,N</sub>*M2<sub>1,N</sub>
 * </tt></pre>
 * where <tt>M1</tt> and <tt>M2</tt> are the input matrices, <tt>O</tt> is the result matrix, and <tt>N</tt> is the number
 * of columns/rows in <tt>M1</tt>/<tt>M2</tt>.
 *
 * @author crf <br/>
 *         Started Dec 13, 2010 6:50:38 AM
 */
public interface MatrixMatrixMultiplication {

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public ByteMatrix multiply(ByteMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public ShortMatrix multiply(ShortMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code short}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public ShortMatrix multiply(ShortMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code short}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public ShortMatrix multiply(ByteMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public IntMatrix multiply(IntMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code int}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public IntMatrix multiply(IntMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code int}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public IntMatrix multiply(ByteMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code int}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public IntMatrix multiply(IntMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code int}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public IntMatrix multiply(ShortMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public LongMatrix multiply(LongMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public LongMatrix multiply(LongMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public LongMatrix multiply(ByteMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public LongMatrix multiply(LongMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public LongMatrix multiply(ShortMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public LongMatrix multiply(LongMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public LongMatrix multiply(IntMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public FloatMatrix multiply(FloatMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public FloatMatrix multiply(FloatMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code float}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public FloatMatrix multiply(ByteMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code float}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public FloatMatrix multiply(FloatMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code float}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public FloatMatrix multiply(ShortMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code float}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public FloatMatrix multiply(FloatMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code float}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public FloatMatrix multiply(IntMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public FloatMatrix multiply(FloatMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public FloatMatrix multiply(LongMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(DoubleMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(DoubleMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(ByteMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(DoubleMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(ShortMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(DoubleMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(IntMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(DoubleMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(LongMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(DoubleMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices, specifying whether they should be transposed or not.  The values in the matrices
     * will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>
     *                                  if neither are transposed, <code>m1.size(0) != m2.size(0)<code> if <code>m1</code> is
     *                                  transposed but <code>m2</code> is not, <i>etc.</i>
     */
    public DoubleMatrix multiply(FloatMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2);

    /**
     * Matrix multiply two matrices.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public ByteMatrix multiply(ByteMatrix m1, ByteMatrix m2);

    /**
     * Matrix multiply two matrices.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public ShortMatrix multiply(ShortMatrix m1, ShortMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code short}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public ShortMatrix multiply(ShortMatrix m1, ByteMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code short}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public ShortMatrix multiply(ByteMatrix m1, ShortMatrix m2);

    /**
     * Matrix multiply two matrices.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public IntMatrix multiply(IntMatrix m1, IntMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code int}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public IntMatrix multiply(IntMatrix m1, ByteMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code int}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public IntMatrix multiply(ByteMatrix m1, IntMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code int}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public IntMatrix multiply(IntMatrix m1, ShortMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code int}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public IntMatrix multiply(ShortMatrix m1, IntMatrix m2);

    /**
     * Matrix multiply two matrices.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public LongMatrix multiply(LongMatrix m1, LongMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public LongMatrix multiply(LongMatrix m1, ByteMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public LongMatrix multiply(ByteMatrix m1, LongMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public LongMatrix multiply(LongMatrix m1, ShortMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public LongMatrix multiply(ShortMatrix m1, LongMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public LongMatrix multiply(LongMatrix m1, IntMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public LongMatrix multiply(IntMatrix m1, LongMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code long}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public FloatMatrix multiply(FloatMatrix m1, FloatMatrix m2);

    /**
     * Matrix multiply two matrices.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public FloatMatrix multiply(FloatMatrix m1, ByteMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code float}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public FloatMatrix multiply(ByteMatrix m1, FloatMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code float}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public FloatMatrix multiply(FloatMatrix m1, ShortMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code float}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public FloatMatrix multiply(ShortMatrix m1, FloatMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code float}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public FloatMatrix multiply(FloatMatrix m1, IntMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code float}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public FloatMatrix multiply(IntMatrix m1, FloatMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public FloatMatrix multiply(FloatMatrix m1, LongMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code float}s (note that this can lead to a loss of precision with some {@code long} values.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public FloatMatrix multiply(LongMatrix m1, FloatMatrix m2);

    /**
     * Matrix multiply two matrices.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(DoubleMatrix m1, DoubleMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(DoubleMatrix m1, ByteMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(ByteMatrix m1, DoubleMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(DoubleMatrix m1, ShortMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(ShortMatrix m1, DoubleMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(DoubleMatrix m1, IntMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(IntMatrix m1, DoubleMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(DoubleMatrix m1, LongMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(LongMatrix m1, DoubleMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(DoubleMatrix m1, FloatMatrix m2);

    /**
     * Matrix multiply two matrices.  The values in the matrices will be treated as {@code double}s.
     *
     * @param m1
     *        The first matrix.
     *
     * @param m2
     *        The second matrix.
     *
     * @return the matrix multiplication of {@code m1} and {@code m2}.
     *
     * @throws IllegalArgumentException if the matrices do not align for multiplication. <i>i.e.</i> <code>m1.size(1} != m2.size(0)</code>.
     */
    public DoubleMatrix multiply(FloatMatrix m1, DoubleMatrix m2);
}
