package com.pb.sawdust.model.integration.blas;

import com.pb.sawdust.model.integration.libraries.LibUtil;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix;
import com.pb.sawdust.util.ThreadTimer;
import com.pb.sawdust.util.concurrent.Regulator;
import jcuda.LibUtils;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.JCudaDriver;
import jcuda.jcublas.JCublas;
import jcuda.jcublas.cublasStatus;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * The {@code JCuda} ...
 *
 * @author crf <br/>
 *         Started Oct 18, 2010 9:13:14 AM
 */
public class JCuda {

    //public static final float MATRIX_MULTIPLICATION_MEMORY_FACTOR = 1.0005f;
    public static final float MATRIX_MULTIPLICATION_MEMORY_FACTOR = 2.6f; //todo: shift to function?

    private static final char TRANSPOSE_INDICATOR = 't';
    private static final char NO_TRANSPOSE_INDICATOR = 'n';
    private static final String JCUDA_RUNTIME_LIBRARY_NAME = "JCudaRuntime";
    private static final String JCUDA_DRIVER_LIBRARY_NAME = "JCudaDriver";
    private static final String JCUBLAS_LIBRARY_NAME = "JCublas";
    private static final JCuda instance; //only one instance, because only one cuda device (presumably)

    static {
        addLibraryPath(JCUDA_RUNTIME_LIBRARY_NAME);
        addLibraryPath(JCUDA_DRIVER_LIBRARY_NAME);
        addLibraryPath(JCUBLAS_LIBRARY_NAME);
        instance = isJCudaAvailable() ? new JCuda() : null;
    }

    private static void addLibraryPath(String libraryName) {
        LibUtil.addLibraryPath(LibUtils.createLibName(libraryName));
    }

    private static boolean isLibraryAvailable(String library) {
        try {
            System.loadLibrary(LibUtils.createLibName(library)); //checks for library existence
            JCudaDriver.cuInit(0); //checks for library suitability (32 vs. 64 bit)
            return true;
        } catch (UnsatisfiedLinkError e) {
            System.out.println(e);
            return false;
        }
    }

    public static boolean isJCublasAvailable() {
        return isLibraryAvailable(JCUBLAS_LIBRARY_NAME);
    }

    public static boolean isJCudaAvailable() {
        return isLibraryAvailable(JCUDA_RUNTIME_LIBRARY_NAME);
    }

    public static JCuda getJCuda() {
        if (instance == null)
            throw new IllegalStateException("JCuda not installed/available.");
        return instance;
    }

    private final Regulator regulator;

    private JCuda() {
        //initialize cuda
        JCudaDriver.cuInit(0);
        //initialize cublas
        JCublas.cublasInit();
        CUcontext pctx = new CUcontext();
        CUdevice dev = new CUdevice();
        JCudaDriver.cuDeviceGet(dev, 0);  //note that this is the place to change devices if I need to do that in the future
        JCudaDriver.cuCtxCreate(pctx, 0, dev);

        long[] free = new long[1];
        long[] total = new long[1];
        JCudaDriver.cuMemGetInfo(free,total);
//        System.out.println("free: " + free[0]);
        System.out.println("free: " + free[0] + ", total: " + total[0]);
        regulator = new Regulator(total[0]);
        //add hook to shutdown cublas when jvm exits
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                JCublas.cublasShutdown();
            }
        });
    }

    public int getMaxAvailableMemory() {
        return (int)regulator.getCapacity();
    }

    private Semaphore requestMemory(int memoryDemand) {
        try {
            return regulator.request(memoryDemand);
        } catch (IllegalArgumentException e) {
            //rethrow exception with more meaningful message
            throw new IllegalStateException(String.format("Calculation with memory demand of %d bytes is too big to be executed on this device (max calculation size: %d bytes).",memoryDemand,regulator.getCapacity()));
        }
    }

    private int gemmMemoryUsage(int size, int n1, int n2, int n3) {
        //heuristic is total memory size * 1.0005
        return Math.round(MATRIX_MULTIPLICATION_MEMORY_FACTOR*size*(n1+n2+n3));
    }

    private void printMem() {
        long[] free = new long[1];
        long[] total = new long[1];
        JCudaDriver.cuMemGetInfo(free,total);
        System.out.println("free: " + free[0]);
    }

    //private void dgemmWork(double[] h_A, double[] h_B, double[] h_C,int[] dims, boolean transpose1, boolean transpose2, double scale1, double scale2) {
    private void dgemmWork(double[] h_A, double[] h_B, double[] h_C,int[] dims, double scale1, double scale2) {
        int m1D0 = dims[0];
        int m1D1 = dims[1];
        int m2D0 = dims[2];
        int m2D1 = dims[3];
        int n1 = dims[4];
        int n2 = dims[5];
        int no = dims[6];

        Pointer d_A = new Pointer();
        Pointer d_B = new Pointer();
        Pointer d_C = new Pointer();

        /* Allocate device memory for the matrices */
        checkCublasStatus(JCublas.cublasAlloc(n1, Sizeof.DOUBLE,d_A));
//            printMem();
        checkCublasStatus(JCublas.cublasAlloc(n2,Sizeof.DOUBLE,d_B));
//            printMem();
        checkCublasStatus(JCublas.cublasAlloc(no,Sizeof.DOUBLE,d_C));
//            printMem();

        /* Initialize the device matrices with the host matrices */
        checkCublasStatus(JCublas.cublasSetVector(n1,Sizeof.DOUBLE,Pointer.to(h_A),1,d_A,1));
//            printMem();
        checkCublasStatus(JCublas.cublasSetVector(n2,Sizeof.DOUBLE,Pointer.to(h_B),1,d_B,1));
//            printMem();
        checkCublasStatus(JCublas.cublasSetVector(no,Sizeof.DOUBLE,Pointer.to(h_C),1,d_C,1));
//            printMem();

        /* Performs operation using JCublas */
//            JCublas.cublasDgemm(transpose1 ? TRANSPOSE_INDICATOR : NO_TRANSPOSE_INDICATOR,transpose2 ? TRANSPOSE_INDICATOR : NO_TRANSPOSE_INDICATOR,
//                                transpose1 ? m1D1 : m1D0,transpose2 ? m2D0 : m2D1,transpose1 ? m1D0 : m1D1,
//                                scale1,d_A,m1D0,d_B,m2D0,scale2,d_C,transpose1 ? m1D1 : m1D0);

        JCublas.cublasDgemm(NO_TRANSPOSE_INDICATOR,NO_TRANSPOSE_INDICATOR,
                            m1D0,m2D1,m1D1,scale1,d_A,m1D0,d_B,m2D0,scale2,d_C,m1D0);
//            printMem();
        checkCublasStatus();

        /* Read the result back */
        checkCublasStatus(JCublas.cublasGetVector(no,Sizeof.DOUBLE,d_C,1,Pointer.to(h_C),1));
//            printMem();

        /* Memory clean up */
        checkCublasStatus(JCublas.cublasFree(d_A));
        checkCublasStatus(JCublas.cublasFree(d_B));
        checkCublasStatus(JCublas.cublasFree(d_C));
    }

    //private void sgemmWork(float[] h_A, float[] h_B, float[] h_C, int[] dims, boolean transpose1, boolean transpose2, float scale1, float scale2) {
    private void sgemmWork(float[] h_A, float[] h_B, float[] h_C, int[] dims, float scale1, float scale2) {
        int m1D0 = dims[0];
        int m1D1 = dims[1];
        int m2D0 = dims[2];
        int m2D1 = dims[3];
        int n1 = dims[4];
        int n2 = dims[5];
        int no = dims[6];

        Pointer d_A = new Pointer();
        Pointer d_B = new Pointer();
        Pointer d_C = new Pointer();

        /* Allocate device memory for the matrices */
        checkCublasStatus(JCublas.cublasAlloc(n1, Sizeof.FLOAT,d_A));
//            printMem();
        checkCublasStatus(JCublas.cublasAlloc(n2,Sizeof.FLOAT,d_B));
//            printMem();
        checkCublasStatus(JCublas.cublasAlloc(no,Sizeof.FLOAT,d_C));
//            printMem();

        /* Initialize the device matrices with the host matrices */
        checkCublasStatus(JCublas.cublasSetVector(n1,Sizeof.FLOAT,Pointer.to(h_A),1,d_A,1));
//            printMem();
        checkCublasStatus(JCublas.cublasSetVector(n2,Sizeof.FLOAT,Pointer.to(h_B),1,d_B,1));
//            printMem();
        checkCublasStatus(JCublas.cublasSetVector(no,Sizeof.FLOAT,Pointer.to(h_C),1,d_C,1));
//            printMem();

        /* Performs operation using JCublas */
//            JCublas.cublasSgemm(transpose1 ? TRANSPOSE_INDICATOR : NO_TRANSPOSE_INDICATOR,transpose2 ? TRANSPOSE_INDICATOR : NO_TRANSPOSE_INDICATOR,
//                                m1D0,m2D1,m1D1,scale1,d_A,m1D0,d_B,m2D0,scale2,d_C,m1D0);
        JCublas.cublasSgemm(NO_TRANSPOSE_INDICATOR,NO_TRANSPOSE_INDICATOR,
                            m1D0,m2D1,m1D1,scale1,d_A,m1D0,d_B,m2D0,scale2,d_C,m1D0);

//            printMem();
        checkCublasStatus();

        /* Read the result back */
        checkCublasStatus(JCublas.cublasGetVector(no,Sizeof.FLOAT,d_C,1,Pointer.to(h_C),1));
//            printMem();

        /* Memory clean up */
        checkCublasStatus(JCublas.cublasFree(d_A));
        checkCublasStatus(JCublas.cublasFree(d_B));
        checkCublasStatus(JCublas.cublasFree(d_C));
    }

    private int[] gemmGetDimensions(int[] m1Dim, int[] m2Dim, boolean transpose1, boolean transpose2) {
        int m1D0 = m1Dim[0];
        int m1D1 = m1Dim[1];
        int m2D0 = m2Dim[0];
        int m2D1 = m2Dim[1];

        int n1 = m1D0*m1D1;
        int n2 = m2D0*m2D1;
        int no = m1D0*m2D1;
        return new int[] {m1D0,m1D1,m2D0,m2D1,n1,n2,no};
    }

    private Semaphore requestSemaphore(int[] dims, int size) {
        return requestMemory(gemmMemoryUsage(size,dims[4],dims[5],dims[6]));
    }


    //returns scale1*m1*m2+scale2*output
    public DoubleMatrix gemm(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix output, boolean freshOutput,
                                        final boolean transpose1, final boolean transpose2,
                                        final double scale1, final double scale2, int[] m1Dim, int[] m2Dim) {

        int[] dims = gemmGetDimensions(m1Dim,m2Dim,transpose1,transpose2);
        Semaphore s = requestSemaphore(dims,Sizeof.DOUBLE);

        try {
            s.acquireUninterruptibly();
            double[] h_C = freshOutput ? new double[dims[6]] : BlasHelper.getMatrixData(output,false);
            //dgemmWork(BlasHelper.getMatrixData(m1,transpose1),BlasHelper.getMatrixData(m2,transpose2),h_C,dims,transpose1,transpose2,scale1,scale2);
            dgemmWork(BlasHelper.getMatrixData(m1,transpose1),BlasHelper.getMatrixData(m2,transpose2),h_C,dims,scale1,scale2);
            ThreadTimer tt = new ThreadTimer(TimeUnit.MILLISECONDS);
            tt.startTimer();
            BlasHelper.putDataIntoMatrix(output,h_C);
            return output;
        } finally {
            s.release();
        }
    }

    public DoubleMatrix gemm(DoubleMatrix m1, DoubleMatrix m2, DoubleMatrix output, boolean freshOutput,
                                        final boolean transpose1, final boolean transpose2,
                                        final double scale1, final double scale2) {
        return gemm(m1,m2,output,freshOutput,transpose1,transpose2,scale1,scale2,m1.getDimensions(),m2.getDimensions());
    }

    //returns scale1*m1*m2+scale2*output
    public LongMatrix gemm(LongMatrix m1, LongMatrix m2, LongMatrix output, boolean freshOutput,
                                        final boolean transpose1, final boolean transpose2,
                                        final long scale1, final long scale2, int[] m1Dim, int[] m2Dim) {

        int[] dims = gemmGetDimensions(m1Dim,m2Dim,transpose1,transpose2);
        Semaphore s = requestSemaphore(dims,Sizeof.DOUBLE);

        try {
            s.acquireUninterruptibly();
            double[] h_C = freshOutput ? new double[dims[6]] : BlasHelper.getMatrixData(output,false);
            dgemmWork(BlasHelper.getMatrixData(m1,transpose1),BlasHelper.getMatrixData(m2,transpose2),h_C,dims,scale1,scale2);
            BlasHelper.putDataIntoMatrix(output,h_C);
            return output;
        } finally {
            s.release();
        }
    }

    public LongMatrix gemm(LongMatrix m1, LongMatrix m2, LongMatrix output, boolean freshOutput,
                                        final boolean transpose1, final boolean transpose2,
                                        final long scale1, final long scale2) {
        return gemm(m1,m2,output,freshOutput,transpose1,transpose2,scale1,scale2,m1.getDimensions(),m2.getDimensions());
    }
    
    //returns scale1*m1*m2+scale2*output
    public FloatMatrix gemm(FloatMatrix m1, FloatMatrix m2, FloatMatrix output, boolean freshOutput,
                                        final boolean transpose1, final boolean transpose2,
                                        final float scale1, final float scale2, int[] m1Dim, int[] m2Dim) {

        int[] dims = gemmGetDimensions(m1Dim,m2Dim,transpose1,transpose2);
        Semaphore s = requestSemaphore(dims,Sizeof.FLOAT);

        try {
            s.acquireUninterruptibly();
            float[] h_C = freshOutput ? new float[dims[6]] : BlasHelper.getMatrixData(output,false);
//            sgemmWork(BlasHelper.getMatrixData(m1),BlasHelper.getMatrixData(m2),h_C,dims,transpose1,transpose2,scale1,scale2);
            sgemmWork(BlasHelper.getMatrixData(m1,transpose1),BlasHelper.getMatrixData(m2,transpose2),h_C,dims,scale1,scale2);
            BlasHelper.putDataIntoMatrix(output,h_C);
            return output;
        } finally {
            s.release();
        }
    }

    public FloatMatrix gemm(FloatMatrix m1, FloatMatrix m2, FloatMatrix output, boolean freshOutput,
                                        final boolean transpose1, final boolean transpose2,
                                        final float scale1, final float scale2) {
        return gemm(m1,m2,output,freshOutput,transpose1,transpose2,scale1,scale2,m1.getDimensions(),m2.getDimensions());
    }

    //returns scale1*m1*m2+scale2*output
    public IntMatrix gemm(IntMatrix m1, IntMatrix m2, IntMatrix output, boolean freshOutput,
                                        final boolean transpose1, final boolean transpose2,
                                        final int scale1, final int scale2, int[] m1Dim, int[] m2Dim) {

        int[] dims = gemmGetDimensions(m1Dim,m2Dim,transpose1,transpose2);
        Semaphore s = requestSemaphore(dims,Sizeof.FLOAT);

        try {
            s.acquireUninterruptibly();
            float[] h_C = freshOutput ? new float[dims[6]] : BlasHelper.getMatrixData(output,false);
//            sgemmWork(BlasHelper.getMatrixData(m1),BlasHelper.getMatrixData(m2),h_C,dims,transpose1,transpose2,scale1,scale2);
            sgemmWork(BlasHelper.getMatrixData(m1,transpose1),BlasHelper.getMatrixData(m2,transpose2),h_C,dims,scale1,scale2);
            BlasHelper.putDataIntoMatrix(output,h_C);
            return output;
        } finally {
            s.release();
        }
    }

    public IntMatrix gemm(IntMatrix m1, IntMatrix m2, IntMatrix output, boolean freshOutput,
                                        final boolean transpose1, final boolean transpose2,
                                        final int scale1, final int scale2) {
        return gemm(m1,m2,output,freshOutput,transpose1,transpose2,scale1,scale2,m1.getDimensions(),m2.getDimensions());
    }

    public void checkCublasStatus() {
        checkCublasStatus(JCublas.cublasGetError());
    }

    public void checkCublasStatus(int code) {
        String error;
        switch (code) {
            case cublasStatus.CUBLAS_STATUS_ALLOC_FAILED : error = "memory allocation failed."; break;
            case cublasStatus.CUBLAS_STATUS_ARCH_MISMATCH : error = "required architecture feature missing."; break;                        
            case cublasStatus.CUBLAS_STATUS_EXECUTION_FAILED : error = "execution failed to complete."; break;
            case cublasStatus.CUBLAS_STATUS_INTERNAL_ERROR : error = "internal cublas error."; break;
            case cublasStatus.CUBLAS_STATUS_INVALID_VALUE : error = "unsupported value passed to function."; break;
            case cublasStatus.CUBLAS_STATUS_MAPPING_ERROR : error = "access to GPU memory space failed."; break;
            case cublasStatus.CUBLAS_STATUS_NOT_INITIALIZED : error = "cublas library not initialized."; break;
            case cublasStatus.CUBLAS_STATUS_SUCCESS : return;
            default : error = "unknown failure.";
        }
        throw new RuntimeException("Cublas error: " + error);
    }
}
