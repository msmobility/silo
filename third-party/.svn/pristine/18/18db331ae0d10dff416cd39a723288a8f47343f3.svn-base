package com.pb.sawdust.model.integration.blas;

import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix;
import org.jblas.NativeBlas;

/**
 * The {@code JBlas} ...
 *
 * @author crf <br/>
 *         Started Oct 18, 2010 11:07:22 AM
 */
public class JBlas {

    public static boolean isJBlasAvailable() {
        String message = "JBlas not available for this platform.\n" +
                         "If error says can't find dependent libraries, then make sure \n" +
                         "the dependent libraries (dlls) are available from the *system* \n" +
                         "(not java) path.";
        try {
            NativeBlas.dcopy(0,new double[0],0,0,new double[0],0,0);
            return true;
        } catch (UnsatisfiedLinkError e) {
            //swallow - no jblas for this platform
            System.out.println(message);
        }
        return false;
    }
    
    
    
    //returns scale1*m1*m2+scale2*output
    public static DoubleMatrix gemm(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix output, boolean freshOutput,
                                        boolean transpose1, boolean transpose2,
                                        double scale1, double scale2, int[] m1Dim, int[] m2Dim) {   
        double[] d = freshOutput ? new double[output.size(0)*output.size(1)] : BlasHelper.getMatrixData(output,false);
        NativeBlas.dgemm('N','N',
                output.size(0),output.size(1),
                m1Dim[1],scale1,BlasHelper.getMatrixData(m1,transpose1),0,m1Dim[0],
                BlasHelper.getMatrixData(m2,transpose2),0,m2Dim[0],scale2,
                d,0,output.size(0));
        BlasHelper.putDataIntoMatrix(output,d);
        return output;
    }
    
    //returns scale1*m1*m2+scale2*output
    public static FloatMatrix gemm(FloatMatrix m1, FloatMatrix m2, FloatMatrix output, boolean freshOutput,
                                        boolean transpose1, boolean transpose2,
                                        float scale1, float scale2, int[] m1Dim, int[] m2Dim) {     
        float[] d = freshOutput ? new float[output.size(0)*output.size(1)] : BlasHelper.getMatrixData(output,false);
        NativeBlas.sgemm('N','N',
                output.size(0),output.size(1),
                m1Dim[1],scale1,BlasHelper.getMatrixData(m1,transpose1),0,m1Dim[0],
                BlasHelper.getMatrixData(m2,transpose2),0,m2Dim[0],scale2,
                d,0,output.size(0));
        BlasHelper.putDataIntoMatrix(output,d);
        return output;
    }
    
    //returns scale1*m1*m2+scale2*output
    public static LongMatrix gemm(LongMatrix m1, LongMatrix m2, LongMatrix output, boolean freshOutput,
                                    boolean transpose1, boolean transpose2,
                                    long scale1, long scale2, int[] m1Dim, int[] m2Dim) {   
        double[] d = freshOutput ? new double[output.size(0)*output.size(1)] : BlasHelper.getMatrixData(output,false);
        NativeBlas.dgemm('N','N',
                output.size(0),output.size(1),
                m1Dim[1],scale1, BlasHelper.getMatrixData(m1,transpose1),0,m1Dim[0],
                BlasHelper.getMatrixData(m2,transpose2),0,m2Dim[0],scale2,
                d,0,output.size(0));
        BlasHelper.putDataIntoMatrix(output,d);
        return output;
    }
    
    //returns scale1*m1*m2+scale2*output
    public static IntMatrix gemm(IntMatrix m1, IntMatrix m2, IntMatrix output, boolean freshOutput,
                                        boolean transpose1, boolean transpose2,
                                        int scale1, int scale2, int[] m1Dim, int[] m2Dim) {
        float[] d = freshOutput ? new float[output.size(0)*output.size(1)] : BlasHelper.getMatrixData(output,false);
        NativeBlas.sgemm('N','N',
                output.size(0),output.size(1),
                m1Dim[1],scale1,BlasHelper.getMatrixData(m1,transpose1),0,m1Dim[0],
                BlasHelper.getMatrixData(m2,transpose2),0,m2Dim[0],scale2,
                d,0,output.size(0));
        BlasHelper.putDataIntoMatrix(output,d);
        return output;
    }
}
