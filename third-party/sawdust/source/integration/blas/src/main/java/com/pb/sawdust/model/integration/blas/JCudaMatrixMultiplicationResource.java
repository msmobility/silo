package com.pb.sawdust.model.integration.blas;

import com.pb.sawdust.calculator.tensor.la.mm.partition.MemoryLimitMatrixMultiplicationResource;
import com.pb.sawdust.tensor.factory.TensorFactory;

/**
* The {@code JCudaMatrixMultiplicationResource} ...
*
* @author crf <br/>
*         Started 1/11/11 10:19 AM
*/
public class JCudaMatrixMultiplicationResource extends MemoryLimitMatrixMultiplicationResource {

    public JCudaMatrixMultiplicationResource(TensorFactory factory) {
        super((int) Math.round(Math.floor(JCuda.getJCuda().getMaxAvailableMemory() / JCuda.MATRIX_MULTIPLICATION_MEMORY_FACTOR)),new JCudaMatrixMultiplication(factory));
    }

    @Override
    public boolean isAvailable() {
        return JCuda.isJCublasAvailable();
    }


    public static void main(String ... args) {
//        ThreadTimer tt = new ThreadTimer();
//        TensorFactory factory = ArrayTensor.getFactory();
////        MatrixMultiplication mm = new PartitionedMatrixMultiplication(factory,Arrays.asList(new JCudaMatrixMultiplicationResource(factory)));
//
//        DoubleMatrix m1 = factory.doubleMatrix(4000,1700);
//        DoubleMatrix m2 = factory.doubleMatrix(1700,6000);
//
//        TensorUtil.fill(m1,1.0);
//        TensorUtil.fill(m2,1.0);
//
//        tt.startTimer();
//        DoubleMatrix mo = mm.multiply(m1,m2,false,false);
//        System.out.println(tt.endTimer());
//
//        System.out.println(TensorUtil.toString(m1));
//        System.out.println(TensorUtil.toString(m2));
//        System.out.println(TensorUtil.toString(mo));

    }
}
