package com.pb.sawdust.calculator.tensor.la.mm;

import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.Matrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.*;
import com.pb.sawdust.tensor.alias.scalar.primitive.*;
import com.pb.sawdust.tensor.alias.vector.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Arrays;

/**
 * The {@code AbstractMatrixMultiplication} class provides a skeletal implementation of the {@code MatrixMultiplication}
 * interface.  To extend this class, only methods which multiply two {@code DoubleMatrix}es or two {@code LongMatrix}es
 * need to be implemented.  Methods which multiply two {@code FloatMatrix}es or two {@code IntMatrix}es can be overridden
 * to provide performance beneifits.
 *
 * @author crf <br/>
 *         Started Dec 13, 2010 7:27:14 AM
 */
public abstract class AbstractMatrixMultiplication implements MatrixMultiplication {
    /**
     * The tensor factory used to create the output matrices.
     */
    protected final TensorFactory factory;

    /**
     * Constructor specifying the tensor factory used to create output matrices.
     *
     * @param factory
     *        The tensor factory.
     */
    public AbstractMatrixMultiplication(TensorFactory factory) {
        this.factory = factory;
    }

    /**
     * Multiply two {@code DoubleMatrix}es into an output matrix, specifying whether int inputs need to be transposed or
     * not. It should be assumed that all aligment checks have been made; that is, that the two input matrices will line
     * up correctly (when transposed, if specified) and that the output matrix will be of the correct shape.
     *
     * @param m1
     *        The first matrix argument.
     *
     * @param m2
     *        The second matrix argument.
     *
     * @param output
     *        The output matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix argument.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix argument.
     *
     * @param m1Dim
     *        The shape of the first matrix argument (<i>i.e.</i> <code>m1.getDimensions()</code>).
     *
     * @param m2Dim
     *        The shape of the second matrix argument (<i>i.e.</i> <code>m2.getDimensions()</code>).
     */
    abstract protected void multiply(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix output, boolean transpose1, boolean transpose2, int[] m1Dim, int[] m2Dim);

    /**
     * Multiply two {@code LongMatrix}es into an output matrix, specifying whether int inputs need to be transposed or
     * not. It should be assumed that all aligment checks have been made; that is, that the two input matrices will line
     * up correctly (when transposed, if specified) and that the output matrix will be of the correct shape.
     *
     * @param m1
     *        The first matrix argument.
     *
     * @param m2
     *        The second matrix argument.
     *
     * @param output
     *        The output matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix argument.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix argument.
     *
     * @param m1Dim
     *        The shape of the first matrix argument (<i>i.e.</i> <code>m1.getDimensions()</code>).
     *
     * @param m2Dim
     *        The shape of the second matrix argument (<i>i.e.</i> <code>m2.getDimensions()</code>).
     */
    abstract protected void multiply(LongMatrix m1, LongMatrix m2, LongMatrix output, boolean transpose1, boolean transpose2, int[] m1Dim, int[] m2Dim);

    /**
     * Multiply two {@code IntMatrix}es into an output matrix, specifying whether int inputs need to be transposed or
     * not. It should be assumed that all aligment checks have been made; that is, that the two input matrices will line
     * up correctly (when transposed, if specified) and that the output matrix will be of the correct shape.
     * <p>
     * The default implementation of this method will treat the input and output matrices as {@code LongMatrix}es and call
     * {@link #multiply(com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix, boolean, boolean, int[], int[])}.
     * Some efficiencies might be gained if this method is overridden to deal with the inputs and outputs purely at an
     * {@code int} level.
     *
     * @param m1
     *        The first matrix argument.
     *
     * @param m2
     *        The second matrix argument.
     *
     * @param output
     *        The output matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix argument.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix argument.
     *
     * @param m1Dim
     *        The shape of the first matrix argument (<i>i.e.</i> <code>m1.getDimensions()</code>).
     *
     * @param m2Dim
     *        The shape of the second matrix argument (<i>i.e.</i> <code>m2.getDimensions()</code>).
     */
    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    protected void multiply(IntMatrix m1, IntMatrix m2, IntMatrix output, boolean transpose1, boolean transpose2, int[] m1Dim, int[] m2Dim) {
        multiply((LongMatrix) TensorUtil.asLongTensor(m1),(LongMatrix) TensorUtil.asLongTensor(m2),(LongMatrix) TensorUtil.asLongTensor(output),transpose1,transpose2,m1Dim,m2Dim);
    }

    /**
     * Multiply two {@code FloatMatrix}es into an output matrix, specifying whether int inputs need to be transposed or
     * not. It should be assumed that all aligment checks have been made; that is, that the two input matrices will line
     * up correctly (when transposed, if specified) and that the output matrix will be of the correct shape.
     * <p>
     * The default implementation of this method will treat the input and output matrices as {@code DoubleMatrix}es and call
     * {@link #multiply(com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix, boolean, boolean, int[], int[])}.
     * Some efficiencies might be gained if this method is overridden to deal with the inputs and outputs purely at a
     * {@code float} level. 
     *
     * @param m1
     *        The first matrix argument.
     *
     * @param m2
     *        The second matrix argument.
     *
     * @param output
     *        The output matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix argument.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix argument.
     *
     * @param m1Dim
     *        The shape of the first matrix argument (<i>i.e.</i> <code>m1.getDimensions()</code>).
     *
     * @param m2Dim
     *        The shape of the second matrix argument (<i>i.e.</i> <code>m2.getDimensions()</code>).
     */
    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    protected void multiply(FloatMatrix m1, FloatMatrix m2, FloatMatrix output, boolean transpose1, boolean transpose2, int[] m1Dim, int[] m2Dim) {
        multiply((DoubleMatrix) TensorUtil.asDoubleTensor(m1),(DoubleMatrix) TensorUtil.asDoubleTensor(m2),(DoubleMatrix) TensorUtil.asDoubleTensor(output),transpose1,transpose2,m1Dim,m2Dim);
    }

    private int[] getMultiplyDimensions(int[] matrix1Dim, int[] matrix2Dim) {
        if (matrix1Dim[1] != matrix2Dim[0])
            throw new IllegalArgumentException("Matrix multiply dimensions do not align: " + Arrays.toString(matrix1Dim) + ", " + Arrays.toString(matrix2Dim));
        return new int[] {matrix1Dim[0],matrix2Dim[1]};
    }

    private int[][] getDims(Matrix m1, Matrix m2, boolean transpose1, boolean transpose2) {
        int[] m1Dim = m1.getDimensions();
        int[] m2Dim = m2.getDimensions();
        if (transpose1)
            swap(m1Dim);
        if (transpose2)
            swap(m2Dim);
        return new int[][] {m1Dim,m2Dim};
    }

    private void swap(int[] array) {
        int temp = array[0];
        array[0] = array[1];
        array[1] = temp;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public ByteMatrix multiply(ByteMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        ByteMatrix output = (ByteMatrix) factory.byteTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((IntMatrix) TensorUtil.asIntTensor(m1),(IntMatrix) TensorUtil.asIntTensor(m2),(IntMatrix) TensorUtil.asIntTensor(output),transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public ShortMatrix multiply(ShortMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        ShortMatrix output = (ShortMatrix) factory.shortTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((IntMatrix) TensorUtil.asIntTensor(m1),(IntMatrix) TensorUtil.asIntTensor(m2),(IntMatrix) TensorUtil.asIntTensor(output),transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public ShortMatrix multiply(ShortMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);        
        ShortMatrix output = (ShortMatrix) factory.shortTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((IntMatrix) TensorUtil.asIntTensor(m1),(IntMatrix) TensorUtil.asIntTensor(m2),(IntMatrix) TensorUtil.asIntTensor(output),transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public ShortMatrix multiply(ByteMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        ShortMatrix output = (ShortMatrix) factory.shortTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((IntMatrix) TensorUtil.asIntTensor(m1),(IntMatrix) TensorUtil.asIntTensor(m2),(IntMatrix) TensorUtil.asIntTensor(output),transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public IntMatrix multiply(IntMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        IntMatrix output = (IntMatrix) factory.intTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public IntMatrix multiply(IntMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        IntMatrix output = (IntMatrix) factory.intTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(IntMatrix) TensorUtil.asIntTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public IntMatrix multiply(ByteMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        IntMatrix output = (IntMatrix) factory.intTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((IntMatrix) TensorUtil.asIntTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public IntMatrix multiply(IntMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        IntMatrix output = (IntMatrix) factory.intTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(IntMatrix) TensorUtil.asIntTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public IntMatrix multiply(ShortMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        IntMatrix output = (IntMatrix) factory.intTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((IntMatrix) TensorUtil.asIntTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public LongMatrix multiply(LongMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        LongMatrix output = (LongMatrix) factory.longTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,m2,output,transpose1,transpose2,dims[0],dims[1]);           
        return output;
    }

    public LongMatrix multiply(LongMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        LongMatrix output = (LongMatrix) factory.longTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(LongMatrix) TensorUtil.asLongTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public LongMatrix multiply(ByteMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        LongMatrix output = (LongMatrix) factory.longTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((LongMatrix) TensorUtil.asLongTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }
    public LongMatrix multiply(LongMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        LongMatrix output = (LongMatrix) factory.longTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(LongMatrix) TensorUtil.asLongTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public LongMatrix multiply(ShortMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        LongMatrix output = (LongMatrix) factory.longTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((LongMatrix) TensorUtil.asLongTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public LongMatrix multiply(LongMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        LongMatrix output = (LongMatrix) factory.longTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(LongMatrix) TensorUtil.asLongTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public LongMatrix multiply(IntMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        LongMatrix output = (LongMatrix) factory.longTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((LongMatrix) TensorUtil.asLongTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public FloatMatrix multiply(FloatMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        FloatMatrix output = (FloatMatrix) factory.floatTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public FloatMatrix multiply(FloatMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        FloatMatrix output = (FloatMatrix) factory.floatTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(FloatMatrix) TensorUtil.asFloatTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public FloatMatrix multiply(ByteMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        FloatMatrix output = (FloatMatrix) factory.floatTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((FloatMatrix) TensorUtil.asFloatTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }
    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public FloatMatrix multiply(FloatMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        FloatMatrix output = (FloatMatrix) factory.floatTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(FloatMatrix) TensorUtil.asFloatTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public FloatMatrix multiply(ShortMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        FloatMatrix output = (FloatMatrix) factory.floatTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((FloatMatrix) TensorUtil.asFloatTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public FloatMatrix multiply(FloatMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        FloatMatrix output = (FloatMatrix) factory.floatTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(FloatMatrix) TensorUtil.asFloatTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public FloatMatrix multiply(IntMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        FloatMatrix output = (FloatMatrix) factory.floatTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((FloatMatrix) TensorUtil.asFloatTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public FloatMatrix multiply(FloatMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        FloatMatrix output = (FloatMatrix) factory.floatTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(FloatMatrix) TensorUtil.asFloatTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    @SuppressWarnings("unchecked") //wrapped tensor type correct, in spirit
    public FloatMatrix multiply(LongMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        FloatMatrix output = (FloatMatrix) factory.floatTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((FloatMatrix) TensorUtil.asFloatTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public DoubleMatrix multiply(DoubleMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public DoubleMatrix multiply(DoubleMatrix m1, ByteMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(DoubleMatrix) TensorUtil.asDoubleTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public DoubleMatrix multiply(ByteMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((DoubleMatrix) TensorUtil.asDoubleTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public DoubleMatrix multiply(DoubleMatrix m1, ShortMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(DoubleMatrix) TensorUtil.asDoubleTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public DoubleMatrix multiply(ShortMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((DoubleMatrix) TensorUtil.asDoubleTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public DoubleMatrix multiply(DoubleMatrix m1, IntMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(DoubleMatrix) TensorUtil.asDoubleTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public DoubleMatrix multiply(IntMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((DoubleMatrix) TensorUtil.asDoubleTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public DoubleMatrix multiply(DoubleMatrix m1, LongMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(DoubleMatrix) TensorUtil.asDoubleTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }
    public DoubleMatrix multiply(LongMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((DoubleMatrix) TensorUtil.asDoubleTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public DoubleMatrix multiply(DoubleMatrix m1, FloatMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply(m1,(DoubleMatrix) TensorUtil.asDoubleTensor(m2),output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }

    public DoubleMatrix multiply(FloatMatrix m1, DoubleMatrix m2, boolean transpose1, boolean transpose2) {
        int[][] dims = getDims(m1,m2,transpose1,transpose2);
        DoubleMatrix output = (DoubleMatrix) factory.doubleTensor(getMultiplyDimensions(dims[0],dims[1]));
        multiply((DoubleMatrix) TensorUtil.asDoubleTensor(m1),m2,output,transpose1,transpose2,dims[0],dims[1]);
        return output;
    }
    
    public ByteVector multiply(ByteVector v1, ByteMatrix m2, boolean transposeMatrix) {
        return (ByteVector) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }  
    
    public ShortVector multiply(ShortVector v1, ShortMatrix m2, boolean transposeMatrix) {
        return (ShortVector) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }  
    
    public ShortVector multiply(ByteVector v1, ShortMatrix m2, boolean transposeMatrix) {
        return (ShortVector) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public ShortVector multiply(ShortVector v1, ByteMatrix m2, boolean transposeMatrix) {
        return (ShortVector) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }  
    
    public IntVector multiply(IntVector v1, IntMatrix m2, boolean transposeMatrix) {
        return (IntVector) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }     
    
    public IntVector multiply(ByteVector v1, IntMatrix m2, boolean transposeMatrix) {
        return (IntVector) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public IntVector multiply(IntVector v1, ByteMatrix m2, boolean transposeMatrix) {
        return (IntVector) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }  
    
    public IntVector multiply(ShortVector v1, IntMatrix m2, boolean transposeMatrix) {
        return (IntVector) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public IntVector multiply(IntVector v1, ShortMatrix m2, boolean transposeMatrix) {
        return (IntVector) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }
    
    public LongVector multiply(LongVector v1, LongMatrix m2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }  
    
    public LongVector multiply(ByteVector v1, LongMatrix m2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public LongVector multiply(LongVector v1, ByteMatrix m2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }  
    
    public LongVector multiply(ShortVector v1, LongMatrix m2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public LongVector multiply(LongVector v1, ShortMatrix m2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }  
    
    public LongVector multiply(IntVector v1, LongMatrix m2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public LongVector multiply(LongVector v1, IntMatrix m2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }  
    
    public FloatVector multiply(FloatVector v1, FloatMatrix m2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }        
    
    public FloatVector multiply(ByteVector v1, FloatMatrix m2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public FloatVector multiply(FloatVector v1, ByteMatrix m2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    } 
    
    public FloatVector multiply(ShortVector v1, FloatMatrix m2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public FloatVector multiply(FloatVector v1, ShortMatrix m2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    } 
    
    public FloatVector multiply(IntVector v1, FloatMatrix m2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public FloatVector multiply(FloatVector v1, IntMatrix m2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    } 
    
    public FloatVector multiply(LongVector v1, FloatMatrix m2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public FloatVector multiply(FloatVector v1, LongMatrix m2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    } 
    
    public DoubleVector multiply(DoubleVector v1, DoubleMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }        
    
    public DoubleVector multiply(ByteVector v1, DoubleMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public DoubleVector multiply(DoubleVector v1, ByteMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    } 
    
    public DoubleVector multiply(ShortVector v1, DoubleMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public DoubleVector multiply(DoubleVector v1, ShortMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    } 
    
    public DoubleVector multiply(IntVector v1, DoubleMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public DoubleVector multiply(DoubleVector v1, IntMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    } 
    
    public DoubleVector multiply(LongVector v1, DoubleMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public DoubleVector multiply(DoubleVector v1, LongMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    } 
    
    public DoubleVector multiply(FloatVector v1, DoubleMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }      
    
    public DoubleVector multiply(DoubleVector v1, FloatMatrix m2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),m2,true,transposeMatrix),0);
    }

    public ByteVector multiply(ByteMatrix m1, ByteVector v2, boolean transposeMatrix) {
        return (ByteVector) TensorUtil.collapse(multiply(m1,(ByteMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public ShortVector multiply(ShortMatrix m1, ShortVector v2, boolean transposeMatrix) {
        return (ShortVector) TensorUtil.collapse(multiply(m1,(ShortMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public ShortVector multiply(ByteMatrix m1, ShortVector v2, boolean transposeMatrix) {
        return (ShortVector) TensorUtil.collapse(multiply(m1,(ShortMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public ShortVector multiply(ShortMatrix m1, ByteVector v2, boolean transposeMatrix) {
        return (ShortVector) TensorUtil.collapse(multiply(m1,(ByteMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public IntVector multiply(IntMatrix m1, IntVector v2, boolean transposeMatrix) {
        return (IntVector) TensorUtil.collapse(multiply(m1,(IntMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public IntVector multiply(ByteMatrix m1, IntVector v2, boolean transposeMatrix) {
        return (IntVector) TensorUtil.collapse(multiply(m1,(IntMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public IntVector multiply(IntMatrix m1, ByteVector v2, boolean transposeMatrix) {
        return (IntVector) TensorUtil.collapse(multiply(m1,(ByteMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public IntVector multiply(ShortMatrix m1, IntVector v2, boolean transposeMatrix) {
        return (IntVector) TensorUtil.collapse(multiply(m1,(IntMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public IntVector multiply(IntMatrix m1, ShortVector v2, boolean transposeMatrix) {
        return (IntVector) TensorUtil.collapse(multiply(m1,(ShortMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public LongVector multiply(LongMatrix m1, LongVector v2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply(m1,(LongMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public LongVector multiply(ByteMatrix m1, LongVector v2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply(m1,(LongMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public LongVector multiply(LongMatrix m1, ByteVector v2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply(m1,(ByteMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public LongVector multiply(ShortMatrix m1, LongVector v2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply(m1,(LongMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public LongVector multiply(LongMatrix m1, ShortVector v2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply(m1,(ShortMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public LongVector multiply(IntMatrix m1, LongVector v2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply(m1,(LongMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public LongVector multiply(LongMatrix m1, IntVector v2, boolean transposeMatrix) {
        return (LongVector) TensorUtil.collapse(multiply(m1,(IntMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public FloatVector multiply(FloatMatrix m1, FloatVector v2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply(m1,(FloatMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public FloatVector multiply(ByteMatrix m1, FloatVector v2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply(m1,(FloatMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public FloatVector multiply(FloatMatrix m1, ByteVector v2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply(m1,(ByteMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public FloatVector multiply(ShortMatrix m1, FloatVector v2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply(m1,(FloatMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public FloatVector multiply(FloatMatrix m1, ShortVector v2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply(m1,(ShortMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public FloatVector multiply(IntMatrix m1, FloatVector v2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply(m1,(FloatMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public FloatVector multiply(FloatMatrix m1, IntVector v2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply(m1,(IntMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public FloatVector multiply(LongMatrix m1, FloatVector v2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply(m1,(FloatMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public FloatVector multiply(FloatMatrix m1, LongVector v2, boolean transposeMatrix) {
        return (FloatVector) TensorUtil.collapse(multiply(m1,(LongMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(DoubleMatrix m1, DoubleVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(DoubleMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(ByteMatrix m1, DoubleVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(DoubleMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(DoubleMatrix m1, ByteVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(ByteMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(ShortMatrix m1, DoubleVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(DoubleMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(DoubleMatrix m1, ShortVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(ShortMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(IntMatrix m1, DoubleVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(DoubleMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(DoubleMatrix m1, IntVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(IntMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(LongMatrix m1, DoubleVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(DoubleMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(DoubleMatrix m1, LongVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(LongMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(FloatMatrix m1, DoubleVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(DoubleMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public DoubleVector multiply(DoubleMatrix m1, FloatVector v2, boolean transposeMatrix) {
        return (DoubleVector) TensorUtil.collapse(multiply(m1,(FloatMatrix) TensorUtil.expand(v2,1),transposeMatrix,false),1);
    }

    public ByteScalar multiply(ByteVector v1, ByteVector v2) {
        return (ByteScalar) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),(ByteMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public ShortScalar multiply(ShortVector v1, ShortVector v2) {
        return (ShortScalar) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),(ShortMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public ShortScalar multiply(ShortVector v1, ByteVector v2) {
        return (ShortScalar) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),(ByteMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public ShortScalar multiply(ByteVector v1, ShortVector v2) {
        return (ShortScalar) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),(ShortMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public IntScalar multiply(IntVector v1, IntVector v2) {
        return (IntScalar) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),(IntMatrix) TensorUtil.expand(v2,1),true,false));
    }   
    
    public IntScalar multiply(IntVector v1, ByteVector v2) {
        return (IntScalar) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),(ByteMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public IntScalar multiply(ByteVector v1, IntVector v2) {
        return (IntScalar) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),(IntMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public IntScalar multiply(IntVector v1, ShortVector v2) {
        return (IntScalar) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),(ShortMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public IntScalar multiply(ShortVector v1, IntVector v2) {
        return (IntScalar) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),(IntMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public LongScalar multiply(LongVector v1, LongVector v2) {
        return (LongScalar) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),(LongMatrix) TensorUtil.expand(v2,1),true,false));
    }     
    
    public LongScalar multiply(LongVector v1, ByteVector v2) {
        return (LongScalar) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),(ByteMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public LongScalar multiply(ByteVector v1, LongVector v2) {
        return (LongScalar) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),(LongMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public LongScalar multiply(LongVector v1, ShortVector v2) {
        return (LongScalar) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),(ShortMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public LongScalar multiply(ShortVector v1, LongVector v2) {
        return (LongScalar) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),(LongMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public LongScalar multiply(LongVector v1, IntVector v2) {
        return (LongScalar) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),(IntMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public LongScalar multiply(IntVector v1, LongVector v2) {
        return (LongScalar) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),(LongMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public FloatScalar multiply(FloatVector v1, FloatVector v2) {
        return (FloatScalar) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),(FloatMatrix) TensorUtil.expand(v2,1),true,false));
    }         
    
    public FloatScalar multiply(FloatVector v1, ByteVector v2) {
        return (FloatScalar) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),(ByteMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public FloatScalar multiply(ByteVector v1, FloatVector v2) {
        return (FloatScalar) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),(FloatMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public FloatScalar multiply(FloatVector v1, ShortVector v2) {
        return (FloatScalar) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),(ShortMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public FloatScalar multiply(ShortVector v1, FloatVector v2) {
        return (FloatScalar) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),(FloatMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public FloatScalar multiply(FloatVector v1, IntVector v2) {
        return (FloatScalar) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),(IntMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public FloatScalar multiply(IntVector v1, FloatVector v2) {
        return (FloatScalar) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),(FloatMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public FloatScalar multiply(FloatVector v1, LongVector v2) {
        return (FloatScalar) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),(LongMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public FloatScalar multiply(LongVector v1, FloatVector v2) {
        return (FloatScalar) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),(FloatMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public DoubleScalar multiply(DoubleVector v1, DoubleVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),(DoubleMatrix) TensorUtil.expand(v2,1),true,false));
    }         
    
    public DoubleScalar multiply(DoubleVector v1, ByteVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),(ByteMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public DoubleScalar multiply(ByteVector v1, DoubleVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((ByteMatrix) TensorUtil.expand(v1,1),(DoubleMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public DoubleScalar multiply(DoubleVector v1, ShortVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),(ShortMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public DoubleScalar multiply(ShortVector v1, DoubleVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((ShortMatrix) TensorUtil.expand(v1,1),(DoubleMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public DoubleScalar multiply(DoubleVector v1, IntVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),(IntMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public DoubleScalar multiply(IntVector v1, DoubleVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((IntMatrix) TensorUtil.expand(v1,1),(DoubleMatrix) TensorUtil.expand(v2,1),true,false));
    }
    
    public DoubleScalar multiply(DoubleVector v1, LongVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),(LongMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public DoubleScalar multiply(LongVector v1, DoubleVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((LongMatrix) TensorUtil.expand(v1,1),(DoubleMatrix) TensorUtil.expand(v2,1),true,false));
    }      
    
    public DoubleScalar multiply(DoubleVector v1, FloatVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((DoubleMatrix) TensorUtil.expand(v1,1),(FloatMatrix) TensorUtil.expand(v2,1),true,false));
    }                                                                                                
    
    public DoubleScalar multiply(FloatVector v1, DoubleVector v2) {
        return (DoubleScalar) TensorUtil.collapse(multiply((FloatMatrix) TensorUtil.expand(v1,1),(DoubleMatrix) TensorUtil.expand(v2,1),true,false));
    }

    public ByteMatrix multiply(ByteMatrix m1, ByteMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public ShortMatrix multiply(ShortMatrix m1, ShortMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public ShortMatrix multiply(ShortMatrix m1, ByteMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public ShortMatrix multiply(ByteMatrix m1, ShortMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public IntMatrix multiply(IntMatrix m1, IntMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public IntMatrix multiply(IntMatrix m1, ByteMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public IntMatrix multiply(ByteMatrix m1, IntMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public IntMatrix multiply(IntMatrix m1, ShortMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public IntMatrix multiply(ShortMatrix m1, IntMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public LongMatrix multiply(LongMatrix m1, LongMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public LongMatrix multiply(LongMatrix m1, ByteMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public LongMatrix multiply(ByteMatrix m1, LongMatrix m2) {
        return multiply(m1,m2,false,false);
    }
    public LongMatrix multiply(LongMatrix m1, ShortMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public LongMatrix multiply(ShortMatrix m1, LongMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public LongMatrix multiply(LongMatrix m1, IntMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public LongMatrix multiply(IntMatrix m1, LongMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public FloatMatrix multiply(FloatMatrix m1, FloatMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public FloatMatrix multiply(FloatMatrix m1, ByteMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public FloatMatrix multiply(ByteMatrix m1, FloatMatrix m2) {
        return multiply(m1,m2,false,false);
    }
    public FloatMatrix multiply(FloatMatrix m1, ShortMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public FloatMatrix multiply(ShortMatrix m1, FloatMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public FloatMatrix multiply(FloatMatrix m1, IntMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public FloatMatrix multiply(IntMatrix m1, FloatMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public FloatMatrix multiply(FloatMatrix m1, LongMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public FloatMatrix multiply(LongMatrix m1, FloatMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public DoubleMatrix multiply(DoubleMatrix m1, DoubleMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public DoubleMatrix multiply(DoubleMatrix m1, ByteMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public DoubleMatrix multiply(ByteMatrix m1, DoubleMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public DoubleMatrix multiply(DoubleMatrix m1, ShortMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public DoubleMatrix multiply(ShortMatrix m1, DoubleMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public DoubleMatrix multiply(DoubleMatrix m1, IntMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public DoubleMatrix multiply(IntMatrix m1, DoubleMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public DoubleMatrix multiply(DoubleMatrix m1, LongMatrix m2) {
        return multiply(m1,m2,false,false);
    }
    public DoubleMatrix multiply(LongMatrix m1, DoubleMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public DoubleMatrix multiply(DoubleMatrix m1, FloatMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public DoubleMatrix multiply(FloatMatrix m1, DoubleMatrix m2) {
        return multiply(m1,m2,false,false);
    }

    public ByteVector multiply(ByteVector v1, ByteMatrix m2) {
        return multiply(v1,m2,false);
    }

    public ShortVector multiply(ShortVector v1, ShortMatrix m2) {
        return multiply(v1,m2,false);
    }

    public ShortVector multiply(ByteVector v1, ShortMatrix m2) {
        return multiply(v1,m2,false);
    }

    public ShortVector multiply(ShortVector v1, ByteMatrix m2) {
        return multiply(v1,m2,false);
    }

    public IntVector multiply(IntVector v1, IntMatrix m2) {
        return multiply(v1,m2,false);
    }

    public IntVector multiply(ByteVector v1, IntMatrix m2) {
        return multiply(v1,m2,false);
    }

    public IntVector multiply(IntVector v1, ByteMatrix m2) {
        return multiply(v1,m2,false);
    }

    public IntVector multiply(ShortVector v1, IntMatrix m2) {
        return multiply(v1,m2,false);
    }

    public IntVector multiply(IntVector v1, ShortMatrix m2) {
        return multiply(v1,m2,false);
    }

    public LongVector multiply(LongVector v1, LongMatrix m2) {
        return multiply(v1,m2,false);
    }

    public LongVector multiply(ByteVector v1, LongMatrix m2) {
        return multiply(v1,m2,false);
    }

    public LongVector multiply(LongVector v1, ByteMatrix m2) {
        return multiply(v1,m2,false);
    }

    public LongVector multiply(ShortVector v1, LongMatrix m2) {
        return multiply(v1,m2,false);
    }

    public LongVector multiply(LongVector v1, ShortMatrix m2) {
        return multiply(v1,m2,false);
    }

    public LongVector multiply(IntVector v1, LongMatrix m2) {
        return multiply(v1,m2,false);
    }

    public LongVector multiply(LongVector v1, IntMatrix m2) {
        return multiply(v1,m2,false);
    }

    public FloatVector multiply(FloatVector v1, FloatMatrix m2) {
        return multiply(v1,m2,false);
    }

    public FloatVector multiply(ByteVector v1, FloatMatrix m2) {
        return multiply(v1,m2,false);
    }

    public FloatVector multiply(FloatVector v1, ByteMatrix m2) {
        return multiply(v1,m2,false);
    }

    public FloatVector multiply(ShortVector v1, FloatMatrix m2) {
        return multiply(v1,m2,false);
    }

    public FloatVector multiply(FloatVector v1, ShortMatrix m2) {
        return multiply(v1,m2,false);
    }

    public FloatVector multiply(IntVector v1, FloatMatrix m2) {
        return multiply(v1,m2,false);
    }

    public FloatVector multiply(FloatVector v1, IntMatrix m2) {
        return multiply(v1,m2,false);
    }

    public FloatVector multiply(LongVector v1, FloatMatrix m2) {
        return multiply(v1,m2,false);
    }

    public FloatVector multiply(FloatVector v1, LongMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(DoubleVector v1, DoubleMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(ByteVector v1, DoubleMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(DoubleVector v1, ByteMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(ShortVector v1, DoubleMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(DoubleVector v1, ShortMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(IntVector v1, DoubleMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(DoubleVector v1, IntMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(LongVector v1, DoubleMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(DoubleVector v1, LongMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(FloatVector v1, DoubleMatrix m2) {
        return multiply(v1,m2,false);
    }

    public DoubleVector multiply(DoubleVector v1, FloatMatrix m2) {
        return multiply(v1,m2,false);
    }

    public ByteVector multiply(ByteMatrix m1, ByteVector v2) {
        return multiply(m1,v2,false);
    }

    public ShortVector multiply(ShortMatrix m1, ShortVector v2) {
        return multiply(m1,v2,false);
    }

    public ShortVector multiply(ByteMatrix m1, ShortVector v2) {
        return multiply(m1,v2,false);
    }

    public ShortVector multiply(ShortMatrix m1, ByteVector v2) {
        return multiply(m1,v2,false);
    }

    public IntVector multiply(IntMatrix m1, IntVector v2) {
        return multiply(m1,v2,false);
    }

    public IntVector multiply(ByteMatrix m1, IntVector v2) {
        return multiply(m1,v2,false);
    }

    public IntVector multiply(IntMatrix m1, ByteVector v2) {
        return multiply(m1,v2,false);
    }

    public IntVector multiply(ShortMatrix m1, IntVector v2) {
        return multiply(m1,v2,false);
    }

    public IntVector multiply(IntMatrix m1, ShortVector v2) {
        return multiply(m1,v2,false);
    }

    public LongVector multiply(LongMatrix m1, LongVector v2) {
        return multiply(m1,v2,false);
    }

    public LongVector multiply(ByteMatrix m1, LongVector v2) {
        return multiply(m1,v2,false);
    }

    public LongVector multiply(LongMatrix m1, ByteVector v2) {
        return multiply(m1,v2,false);
    }

    public LongVector multiply(ShortMatrix m1, LongVector v2) {
        return multiply(m1,v2,false);
    }

    public LongVector multiply(LongMatrix m1, ShortVector v2) {
        return multiply(m1,v2,false);
    }

    public LongVector multiply(IntMatrix m1, LongVector v2) {
        return multiply(m1,v2,false);
    }

    public LongVector multiply(LongMatrix m1, IntVector v2) {
        return multiply(m1,v2,false);
    }

    public FloatVector multiply(FloatMatrix m1, FloatVector v2) {
        return multiply(m1,v2,false);
    }

    public FloatVector multiply(ByteMatrix m1, FloatVector v2) {
        return multiply(m1,v2,false);
    }

    public FloatVector multiply(FloatMatrix m1, ByteVector v2) {
        return multiply(m1,v2,false);
    }

    public FloatVector multiply(ShortMatrix m1, FloatVector v2) {
        return multiply(m1,v2,false);
    }

    public FloatVector multiply(FloatMatrix m1, ShortVector v2) {
        return multiply(m1,v2,false);
    }

    public FloatVector multiply(IntMatrix m1, FloatVector v2) {
        return multiply(m1,v2,false);
    }

    public FloatVector multiply(FloatMatrix m1, IntVector v2) {
        return multiply(m1,v2,false);
    }

    public FloatVector multiply(LongMatrix m1, FloatVector v2) {
        return multiply(m1,v2,false);
    }

    public FloatVector multiply(FloatMatrix m1, LongVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(DoubleMatrix m1, DoubleVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(ByteMatrix m1, DoubleVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(DoubleMatrix m1, ByteVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(ShortMatrix m1, DoubleVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(DoubleMatrix m1, ShortVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(IntMatrix m1, DoubleVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(DoubleMatrix m1, IntVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(LongMatrix m1, DoubleVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(DoubleMatrix m1, LongVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(FloatMatrix m1, DoubleVector v2) {
        return multiply(m1,v2,false);
    }

    public DoubleVector multiply(DoubleMatrix m1, FloatVector v2) {
        return multiply(m1,v2,false);
    }
    
    /**
     * Convenience method to access the protected {@code double} multiply function of {@code AbstractMatrixMultiplication}.
     * This method is included to allow the API for this class to "leak" out in a deliberate fashion, and should not be
     * called unless there is a compelling reason (with no obvious alternative) abailable.
     * 
     * @param mm
     *        The {@code AbstractMatrixMultiplication} upon which the multiply function will be called.
     *
     * @param m1
     *        The first matrix argument.
     *
     * @param m2
     *        The second matrix argument.
     *
     * @param output
     *        The output matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix argument.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix argument.
     *
     * @param m1Dim
     *        The shape of the first matrix argument (<i>i.e.</i> <code>m1.getDimensions()</code>).
     *
     * @param m2Dim
     *        The shape of the second matrix argument (<i>i.e.</i> <code>m2.getDimensions()</code>).
     * 
     * @see #multiply(com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix, boolean, boolean, int[], int[]) 
     */
    public static void multiply(AbstractMatrixMultiplication mm, DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix output, boolean transpose1, boolean transpose2, int[] m1Dim, int[] m2Dim) {
        mm.multiply(m1,m2,output,transpose1,transpose2,m1Dim,m2Dim);
    }      
    
    /**
     * Convenience method to access the protected {@code float} multiply function of {@code AbstractMatrixMultiplication}.
     * This method is included to allow the API for this class to "leak" out in a deliberate fashion, and should not be
     * called unless there is a compelling reason (with no obvious alternative) abailable.
     * 
     * @param mm
     *        The {@code AbstractMatrixMultiplication} upon which the multiply function will be called.
     *
     * @param m1
     *        The first matrix argument.
     *
     * @param m2
     *        The second matrix argument.
     *
     * @param output
     *        The output matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix argument.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix argument.
     *
     * @param m1Dim
     *        The shape of the first matrix argument (<i>i.e.</i> <code>m1.getDimensions()</code>).
     *
     * @param m2Dim
     *        The shape of the second matrix argument (<i>i.e.</i> <code>m2.getDimensions()</code>).
     * 
     * @see #multiply(com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix, boolean, boolean, int[], int[]) 
     */
    public static void multiply(AbstractMatrixMultiplication mm, FloatMatrix m1, FloatMatrix m2, FloatMatrix output, boolean transpose1, boolean transpose2, int[] m1Dim, int[] m2Dim) {
        mm.multiply(m1,m2,output,transpose1,transpose2,m1Dim,m2Dim);
    }
    
    /**
     * Convenience method to access the protected {@code long} multiply function of {@code AbstractMatrixMultiplication}.
     * This method is included to allow the API for this class to "leak" out in a deliberate fashion, and should not be
     * called unless there is a compelling reason (with no obvious alternative) abailable.
     * 
     * @param mm
     *        The {@code AbstractMatrixMultiplication} upon which the multiply function will be called.
     *
     * @param m1
     *        The first matrix argument.
     *
     * @param m2
     *        The second matrix argument.
     *
     * @param output
     *        The output matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix argument.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix argument.
     *
     * @param m1Dim
     *        The shape of the first matrix argument (<i>i.e.</i> <code>m1.getDimensions()</code>).
     *
     * @param m2Dim
     *        The shape of the second matrix argument (<i>i.e.</i> <code>m2.getDimensions()</code>).
     * 
     * @see #multiply(com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix, boolean, boolean, int[], int[]) 
     */
    public static void multiply(AbstractMatrixMultiplication mm, LongMatrix m1, LongMatrix m2, LongMatrix output, boolean transpose1, boolean transpose2, int[] m1Dim, int[] m2Dim) {
        mm.multiply(m1,m2,output,transpose1,transpose2,m1Dim,m2Dim);
    }
    
    /**
     * Convenience method to access the protected {@code int} multiply function of {@code AbstractMatrixMultiplication}.
     * This method is included to allow the API for this class to "leak" out in a deliberate fashion, and should not be
     * called unless there is a compelling reason (with no obvious alternative) abailable.
     * 
     * @param mm
     *        The {@code AbstractMatrixMultiplication} upon which the multiply function will be called.
     *
     * @param m1
     *        The first matrix argument.
     *
     * @param m2
     *        The second matrix argument.
     *
     * @param output
     *        The output matrix.
     *
     * @param transpose1
     *        If {@code true}, the multiplication will be carried out on the transpose of the first matrix argument.
     *
     * @param transpose2
     *        If {@code true}, the multiplication will be carried out on the transpose of the second matrix argument.
     *
     * @param m1Dim
     *        The shape of the first matrix argument (<i>i.e.</i> <code>m1.getDimensions()</code>).
     *
     * @param m2Dim
     *        The shape of the second matrix argument (<i>i.e.</i> <code>m2.getDimensions()</code>).
     * 
     * @see #multiply(com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix, com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix, boolean, boolean, int[], int[]) 
     */
    public static void multiply(AbstractMatrixMultiplication mm, IntMatrix m1, IntMatrix m2, IntMatrix output, boolean transpose1, boolean transpose2, int[] m1Dim, int[] m2Dim) {
        mm.multiply(m1,m2,output,transpose1,transpose2,m1Dim,m2Dim);
    }
}
