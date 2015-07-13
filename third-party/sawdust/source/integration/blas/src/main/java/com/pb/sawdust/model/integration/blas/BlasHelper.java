package com.pb.sawdust.model.integration.blas;

import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.LongMatrix;

import java.util.Arrays;

/**
 * The {@code BlasHelper} ...
 *
 * @author crf <br/>
 *         Started Oct 18, 2010 9:25:58 AM
 */
public class BlasHelper {

    public static double[] getMatrixData(DoubleMatrix m, boolean transpose) {
        int s0 = m.size(0);
        int s1 = m.size(1);
        double[] d = new double[s0*s1];
        int shift;
        if (transpose) {
            for (int i = 0; i < s0; i++) {
                shift = s1*i;
                for (int j = 0; j < s1; j++) {
                    d[j+shift] = m.getCell(i,j);
                }
            }
        } else {
            for (int j = 0; j < s1; j++) {
                shift = s0*j;
                for (int i = 0; i < s0; i++) {
                    d[i+shift] = m.getCell(i,j);
                }
            }
        }
        return d;
    }

    public static double[] getMatrixData(LongMatrix m, boolean transpose) {
        int s0 = m.size(0);
        int s1 = m.size(1);
        double[] d = new double[s0*s1];
        int shift;
        if (transpose) {
            for (int i = 0; i < s0; i++) {
                shift = s1*i;
                for (int j = 0; j < s1; j++) {
                    d[j+shift] = m.getCell(i,j);
                }
            }
        } else {
            for (int j = 0; j < s1; j++) {
                shift = s0*j;
                for (int i = 0; i < s0; i++) {
                    d[i+shift] = m.getCell(i,j);
                }
            }
        }
        return d;
    }

    public static float[] getMatrixData(FloatMatrix m, boolean transpose) {
        int s0 = m.size(0);
        int s1 = m.size(1);
        float[] d = new float[s0*s1];
        int shift;
        if (transpose) {
            for (int i = 0; i < s0; i++) {
                shift = s1*i;
                for (int j = 0; j < s1; j++) {
                    d[j+shift] = m.getCell(i,j);
                }
            }
        } else {
            for (int j = 0; j < s1; j++) {
                shift = s0*j;
                for (int i = 0; i < s0; i++) {
                    d[i+shift] = m.getCell(i,j);
                }
            }
        }
        return d;
    }

    public static float[] getMatrixData(IntMatrix m, boolean transpose) {
        int s0 = m.size(0);
        int s1 = m.size(1);
        float[] d = new float[s0*s1];
        int shift;
        if (transpose) {
            for (int i = 0; i < s0; i++) {
                shift = s1*i;
                for (int j = 0; j < s1; j++) {
                    d[j+shift] = m.getCell(i,j);
                }
            }
        } else {
            for (int j = 0; j < s1; j++) {
                shift = s0*j;
                for (int i = 0; i < s0; i++) {
                    d[i+shift] = m.getCell(i,j);
                }
            }
        }
        return d;
    }


    public static double[] getMatrixData(DoubleMatrix m) {
        return getMatrixData(m,false);
    }


    public static float[] getMatrixData(FloatMatrix m) {
        return getMatrixData(m,false);
    }


    public static double[] getMatrixData(LongMatrix m) {
        return getMatrixData(m,false);
    }


    public static float[] getMatrixData(IntMatrix m) {
        return getMatrixData(m,false);
    }

    public static void putDataIntoMatrix(DoubleMatrix m, double[] d) {
        int s0 = m.size(0);
        int s1 = m.size(1);
        int shift;
        for (int j = 0; j < s1; j++) {
            shift = s0*j;
            for (int i = 0; i < s0; i++) {
                m.setCell(d[i+shift],i,j);
            }
        }
    }

    public static void putDataIntoMatrix(LongMatrix m, double[] d) {
        int s0 = m.size(0);
        int s1 = m.size(1);
        int shift;
        for (int j = 0; j < s1; j++) {
            shift = s0*j;
            for (int i = 0; i < s0; i++) {
                m.setCell((long) d[i+shift],i,j);
            }
        }
    }

    public static void putDataIntoMatrix(FloatMatrix m, float[] d) {
        int s0 = m.size(0);
        int s1 = m.size(1);
        int shift;
        for (int j = 0; j < s1; j++) {
            shift = s0*j;
            for (int i = 0; i < s0; i++) {
                m.setCell(d[i+shift],i,j);
            }
        }
    }

    public static void putDataIntoMatrix(IntMatrix m, float[] d) {
        int s0 = m.size(0);
        int s1 = m.size(1);
        int shift;
        for (int j = 0; j < s1; j++) {
            shift = s0*j;
            for (int i = 0; i < s0; i++) {
                m.setCell((int) d[i+shift],i,j);
            }
        }
    }
}
