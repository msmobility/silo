package de.tum.bgu.msm;

import cern.colt.matrix.tfloat.impl.DenseFloatMatrix2D;
import com.pb.common.matrix.Matrix;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Benchmark {

    private final static int RUNS = 10;
    private final int NUMBER_OF_ITEMS = 10000;
    private final int SPARSE_PERCENTAGE = 0;
    private final int SPARSE_FACTOR = Math.max(1, (int) (NUMBER_OF_ITEMS*(SPARSE_PERCENTAGE/100.)));
    private List<Integer> keys;

    @Before
    public void initializeKeys() {
        keys = new ArrayList<>();
        for (int i =0; i<NUMBER_OF_ITEMS; i+= SPARSE_FACTOR) {
            keys.add(i);
        }
    }

    @Ignore
    @Test
    public void testMatrix() throws IOException {
        for(int r= 0; r < RUNS; r++) {

            final Matrix matrix = new Matrix(NUMBER_OF_ITEMS, NUMBER_OF_ITEMS);
            for (int i :keys) {
                for (int j :keys) {
                    matrix.setValueAt(i, j, (float) i + j);
                }
            }

            for (int i :keys) {
                for (int j : keys) {
                    matrix.getValueAt(i, j);
                }
            }

            for (int i :keys) {
                for (int j :keys) {
                    matrix.setValueAt(i, j, (float) Math.sqrt(i*j));
                }
            }
        }
    }

    @Ignore
    @Test
    public void testColtMatrix() throws IOException {
        for(int r= 0; r < RUNS; r++) {

            DenseFloatMatrix2D floatMatrix2D = new DenseFloatMatrix2D(NUMBER_OF_ITEMS, NUMBER_OF_ITEMS);

            for (int i :keys) {
                for (int j :keys) {
                    floatMatrix2D.setQuick(i,j, i+j);
                }
            }

            for (int i :keys) {
                for (int j :keys) {
                    floatMatrix2D.getQuick(i,j);
                }
            }
            floatMatrix2D.forEachNonZero((i, j, v) -> (float) Math.sqrt(i*j));
        }
    }
}
