package com.pb.sawdust.model.integration.blas;

import com.pb.sawdust.calculator.tensor.la.mm.AbstractMatrixMultiplication;
import com.pb.sawdust.calculator.tensor.la.mm.MatrixMultiplication;
import com.pb.sawdust.calculator.tensor.la.mm.partition.PartitionedMatrixMultiplication;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;

import java.util.Arrays;

/**
 * The {@code JCudaMatrixMultiplication} provides a {@code MatrixMultiplication} implementation which employs the CUBLAS GPU
 * accelerated linear algebra library. Beyond the {@code JCublas} and {@code JCuda} java libraries, this class requires
 * a CUDA-compatible device and the appropriate CUDA and CUBLAS toolkits installed on the system.
 * <p>
 * Though this class can be used directly, the limited memory of some CUDA cards can place limitations on how large a
 * matrix multiplication can be performed without failure. Because of this, unless the matrix multiplication is known
 * to fit within the CUDA device's memory limits, it is recommended that a resource limited matrix multiplication instance
 * (from {@link #getResourceLimitedJCudaMM(com.pb.sawdust.tensor.factory.TensorFactory)}) is used.  A resource limited
 * instance will partition the problem (through {@link PartitionedMatrixMultiplication}) so that no part will exceed the
 * memory limits of the CUDA device.
 * <p>
 * Currently only one CUDA device is supported; more cards will not break this implementation, but only one will be used
 * in calculations.
 *
 * @see com.pb.sawdust.model.integration.blas.JCuda
 *
 * @author crf <br/>
 *         Started Dec 13, 2010 8:26:04 AM
 */
public class JCudaMatrixMultiplication extends AbstractMatrixMultiplication {
    /**
     * Get a resource limited {@code JCuda} {@code MatrixMultiplication} implementation. The implementation returned by
     * this instance uses {@link PartitionedMatrixMultiplication} to prevent a given matrix multiplication problem from
     * overrunning the CUDA device's memory. In general, the implementation returned by this method should be used in
     * preference to the raw {@link #JCudaMatrixMultiplication(com.pb.sawdust.tensor.factory.TensorFactory)} constructor,
     * as device memory limits can vary and be difficult to predict.
     *
     * @param factory
     *        The tensor factory.
     *
     * @return a resource limted {@code JCuda} {@code MatrixMultiplication} implementation.
     */
    public static MatrixMultiplication getResourceLimitedJCudaMM(TensorFactory factory) {
        return new PartitionedMatrixMultiplication(factory, Arrays.asList(new JCudaMatrixMultiplicationResource(factory)));
    }

    private final JCuda jCuda;

    /**
     * Constructor specifying the tensor factory used to create output matrices.
     *
     * @param factory
     *        The tensor factory.
     *
     * @throws Error if a CUDA device or appropriate libraries are not found on the system.
     */
    public JCudaMatrixMultiplication(TensorFactory factory) {
        super(factory);
        jCuda = JCuda.getJCuda();
    }

    protected void multiply(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        jCuda.gemm(m1,m2,output,true,transpose1,transpose2,1.0,1.0,m1Dim,m2Dim);
    }

    protected void multiply(LongMatrix m1, LongMatrix m2, LongMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
         jCuda.gemm(m1,m2,output,true,transpose1,transpose2,1L,1L,m1Dim,m2Dim);
    }

    protected void multiply(FloatMatrix m1, FloatMatrix m2, FloatMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        jCuda.gemm(m1,m2,output,true,transpose1,transpose2,1.0f,1.0f,m1Dim,m2Dim);
    }

    protected void multiply(IntMatrix m1, IntMatrix m2, IntMatrix output, final boolean transpose1, final boolean transpose2, int[] m1Dim, int[] m2Dim) {
        jCuda.gemm(m1,m2,output,true,transpose1,transpose2,1,1,m1Dim,m2Dim);
    }
}
