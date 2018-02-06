package de.tum.bgu.msm;

import cern.colt.matrix.tfloat.impl.DenseFloatMatrix2D;
import com.pb.common.matrix.Matrix;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
//        keys = new ArrayList<>();
//        for(int i = 0; i < NUMBER_OF_ITEMS; i++) {
//            keys.add(i);
//        }


    }

//    @Benchmark
//    public void testRandomUtilitySelectionArray () {
//
//        double[] itemsArray = new double[NUMBER_OF_ITEMS];
//        for(int i = 0; i < itemsArray.length; i++) {
//            itemsArray[i] = i;
//        }
//        writer.println(SiloUtil.select(itemsArray, new Random()));
//    }
//
//    @Benchmark
//    public void testRandomUtilitySelectionEnumDist () {
//
//        List<Pair<Double, Double>> itemsList = new ArrayList<>();
//        for(double i = 0; i < NUMBER_OF_ITEMS; i++) {
//            itemsList.add(new Pair(i,i));
//        }
//        EnumeratedDistribution distribution  = new EnumeratedDistribution(itemsList);
//        writer.println(distribution.sample());
//    }

//    @Benchmark
//    public void testHashTable() {
//        Table<Integer, Integer, Double> table = HashBasedTable.create();
//        for(int i = 0; i < NUMBER_OF_ITEMS; i++) {
//            for(int j = 0; j < NUMBER_OF_ITEMS; j++) {
//                table.put(i, j, (double) i+j);
//            }
//        }
//        for(int i = 0; i < NUMBER_OF_ITEMS; i++) {
//            for(int j = 0; j < NUMBER_OF_ITEMS; j++) {
//                table.get(i,j);
//            }
//        }
//    }
//
//    @Benchmark
//    public void testArrayTable() {
//        Table<Integer, Integer, Double> table = ArrayTable.create(keys, keys);
//        for(int i = 0; i < NUMBER_OF_ITEMS; i++) {
//            for(int j = 0; j < NUMBER_OF_ITEMS; j++) {
//                table.put(i, j, (double) i+j);
//            }
//        }
//
//        for(int i = 0; i < NUMBER_OF_ITEMS; i++) {
//            for(int j = 0; j < NUMBER_OF_ITEMS; j++) {
//                table.get(i,j);
//            }
//        }
//    }


//    @Test
//    public void testApacheMatrix() throws IOException {
//
//        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/nkueh/Desktop/apacheMatrix" + NUMBER_OF_ITEMS + "_" + SPARSE_FACTOR + ".csv")));
//        writer.println("Initialization,Set,Get,Scale,Function");
//
//        for(int r= 0; r < RUNS; r++) {
//            long time = System.nanoTime();
//            BlockRealMatrix blockRealMatrix = new BlockRealMatrix(NUMBER_OF_ITEMS, NUMBER_OF_ITEMS);
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    blockRealMatrix.setEntry(i, j, (double) i + j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    blockRealMatrix.getEntry(i, j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            blockRealMatrix.scalarMultiply(5);
//            writer.println((System.nanoTime() - time)+",");
//
//            time = System.nanoTime();
//            for (int i :keys) {
//                for (int j :keys) {
//                    blockRealMatrix.setEntry(i, j, blockRealMatrix.getEntry(i,j) + Math.sqrt(i*j));
//                }
//            }
//            writer.println((System.nanoTime() - time));
//
//            writer.flush();
//        }
//    }

//    @Test
//    public void testJblasMatrix() throws IOException {
//        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/nkueh/Desktop/jblasMatrix" + NUMBER_OF_ITEMS + "_" + SPARSE_FACTOR + ".csv")));
//        writer.println("Initialization,Set,Get,Scale,Function");
//
//        for(int r= 0; r < RUNS; r++) {
//            long time = System.nanoTime();
//            final FloatMatrix floatMatrix = new FloatMatrix(NUMBER_OF_ITEMS, NUMBER_OF_ITEMS);
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    floatMatrix.put(i, j, (float) i + j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    floatMatrix.get(i, j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            floatMatrix.muli(5);
//            writer.println((System.nanoTime() - time)+",");
//
//            time = System.nanoTime();
//            for (int i :keys) {
//                for (int j :keys) {
//                    floatMatrix.put(i, j, floatMatrix.get(i,j) + (float) Math.sqrt(i*j));
//                }
//            }
//            writer.println((System.nanoTime() - time));
//
//            writer.flush();
//        }
//    }

    @Test
    public void testMatrix() throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/nkueh/Desktop/pbMatrix" + NUMBER_OF_ITEMS + "_" + SPARSE_FACTOR + ".csv")));
        writer.println("Initialization,Set,Get,Scale,Function");

        for(int r= 0; r < RUNS; r++) {

            long time = System.nanoTime();

            final Matrix matrix = new Matrix(NUMBER_OF_ITEMS, NUMBER_OF_ITEMS);

            writer.print((System.nanoTime() - time)+",");
            time = System.nanoTime();

            for (int i :keys) {
                for (int j :keys) {
                    matrix.setValueAt(i, j, (float) i + j);
                }
            }

            writer.print((System.nanoTime() - time)+",");
            time = System.nanoTime();

            for (int i :keys) {
                for (int j :keys) {
                    matrix.getValueAt(i, j);
                }
            }

            writer.print((System.nanoTime() - time)+",");
            time = System.nanoTime();

            matrix.scale(5);
            writer.println((System.nanoTime() - time)+",");

            time = System.nanoTime();
            for (int i :keys) {
                for (int j :keys) {
                    matrix.setValueAt(i, j, (float) Math.sqrt(i*j));
                }
            }
            writer.println((System.nanoTime() - time));

            writer.flush();
        }
    }

//    @Test
//    public void testJamaMatrix() throws IOException {
//        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/nkueh/Desktop/jamaMatrix" + NUMBER_OF_ITEMS + "_" + SPARSE_FACTOR + ".csv")));
//        writer.println("Initialization,Set,Get,Scale,Function");
//
//        for(int r= 0; r < RUNS; r++) {
//
//            long time = System.nanoTime();
//
//            final Jama.Matrix matrix = new Jama.Matrix(NUMBER_OF_ITEMS, NUMBER_OF_ITEMS);
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    matrix.set(i, j, (double) i + j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    matrix.get(i, j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//            matrix.times(5);
//            writer.println((System.nanoTime() - time)+",");
//
//            time = System.nanoTime();
//            for (int i :keys) {
//                for (int j :keys) {
//                    matrix.set(i, j, matrix.get(i,j) + Math.sqrt(i*j));
//                }
//            }
//            writer.println((System.nanoTime() - time));
//
//            writer.flush();
//        }
//    }

//    @Test
//    public void testEjmlMatrix() throws IOException {
//        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/nkueh/Desktop/ejmlMatrix" + NUMBER_OF_ITEMS + "_" + SPARSE_FACTOR + ".csv")));
//        writer.println("Initialization,Set,Get,Scale,Function");
//        for(int r= 0; r < RUNS; r++) {
//
//            long time = System.nanoTime();
//            final BlockMatrix64F ejmlMatrix = new BlockMatrix64F(NUMBER_OF_ITEMS, NUMBER_OF_ITEMS);
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    ejmlMatrix.set(i, j, (double) i + j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    ejmlMatrix.get(i, j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            CommonOps.scale(5, ejmlMatrix);
//            writer.println((System.nanoTime() - time)+",");
//
//            time = System.nanoTime();
//            for (int i :keys) {
//                for (int j :keys) {
//                    ejmlMatrix.set(i, j, ejmlMatrix.get(i,j) + Math.sqrt(i*j));
//                }
//            }
//            writer.println((System.nanoTime() - time));
//
//            writer.flush();
//        }
//    }

//    @Test
//    public void testMTJMatrix() throws IOException {
//        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/nkueh/Desktop/mtjMatrix" + NUMBER_OF_ITEMS + "_" + SPARSE_FACTOR + ".csv")));
//        writer.println("Initialization,Set,Get,Scale,Function");
//
//        for(int r= 0; r < RUNS; r++) {
//
//            long time = System.nanoTime();
//            DenseMatrix mtjMatrix = new DenseMatrix(NUMBER_OF_ITEMS, NUMBER_OF_ITEMS);
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//            for (int i :keys) {
//                for (int j :keys) {
//                    mtjMatrix.set(i, j, (double) i + j);
//                }
//            }
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    mtjMatrix.get(i, j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//            mtjMatrix.scale(5);
//            writer.println((System.nanoTime() - time)+",");
//
//            time = System.nanoTime();
//            for (int i :keys) {
//                for (int j :keys) {
//                    mtjMatrix.set(i, j, mtjMatrix.get(i,j) + Math.sqrt(i*j));
//                }
//            }
//            writer.println((System.nanoTime() - time));
//
//            writer.flush();
//        }
//    }


//    @Test
//    public void testSparseMatrix() throws IOException {
//        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/nkueh/Desktop/pbSparseMatrix" + NUMBER_OF_ITEMS + "_" + SPARSE_FACTOR + ".csv")));
//        writer.println("Initialization,Set,Get,Scale,Function");
//
//        for(int r= 0; r < RUNS; r++) {
//
//            long time = System.nanoTime();
//            final Matrix sparseMatrix = new Matrix(NUMBER_OF_ITEMS / SPARSE_FACTOR, NUMBER_OF_ITEMS / SPARSE_FACTOR);
//            int[] externalNumebrs = new int[NUMBER_OF_ITEMS / SPARSE_FACTOR];
//            for (int i = 0; i < NUMBER_OF_ITEMS / SPARSE_FACTOR; i++) {
//                externalNumebrs[i] = i * SPARSE_FACTOR;
//            }
//            sparseMatrix.setExternalNumbers(externalNumebrs, externalNumebrs);
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//            for (int i :sparseMatrix.getExternalRowNumbers()) {
//                for (int j :sparseMatrix.getExternalColumnNumbers()) {
//                    sparseMatrix.setValueAt(i, j, (float) i + j);
//                }
//            }
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//
//            for (int i :sparseMatrix.getExternalRowNumbers()) {
//                for (int j :sparseMatrix.getExternalColumnNumbers()) {
//                    sparseMatrix.getValueAt(i, j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            sparseMatrix.scale(5);
//
//            writer.println((System.nanoTime() - time)+",");
//
//            time = System.nanoTime();
//            for (int i :keys) {
//                for (int j :keys) {
//                    sparseMatrix.setValueAt(i, j, sparseMatrix.getValueAt(i,j) + (float) Math.sqrt(i*j));
//                }
//            }
//            writer.println((System.nanoTime() - time));
//
//            writer.flush();
//        }
//    }


//    @Test
//    public void testDenseTumMatrix() throws IOException {
//        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/nkueh/Desktop/jblWrapperMatrix" + NUMBER_OF_ITEMS + "_" + SPARSE_FACTOR + ".csv")));
//        writer.println("Initialization,Set,Get,Scale,Function");
//
//        for (int r = 0; r < RUNS; r++) {
//
//            long time = System.nanoTime();
//            de.tum.bgu.msm.utils.DenseMatrix denseMatrix = new de.tum.bgu.msm.utils.DenseMatrix(keys, keys);
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    denseMatrix.set(i, j, (float) i + j);
//                }
//            }
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    denseMatrix.get(i, j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//
//            denseMatrix.scale(5);
//
//            writer.println((System.nanoTime() - time)+",");
//
//            time = System.nanoTime();
//            for (int i :keys) {
//                for (int j :keys) {
//                    denseMatrix.set(i, j, denseMatrix.get(i,j) + (float)Math.sqrt(i*j));
//                }
//            }
//            writer.println((System.nanoTime() - time));
//
//            writer.flush();
//        }
//    }

    @Test
    public void testColtMatrix() throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/nkueh/Desktop/coltMatrix" + NUMBER_OF_ITEMS + "_" + SPARSE_FACTOR + ".csv")));
        writer.println("Initialization,Set,Get,Scale,Function");

        for(int r= 0; r < RUNS; r++) {

            long time = System.nanoTime();
            DenseFloatMatrix2D floatMatrix2D = new DenseFloatMatrix2D(NUMBER_OF_ITEMS, NUMBER_OF_ITEMS);

            writer.print((System.nanoTime() - time)+",");
            time = System.nanoTime();
            writer.print((System.nanoTime() - time)+",");
            time = System.nanoTime();

            for (int i :keys) {
                for (int j :keys) {
                    floatMatrix2D.get(i,j);
                }
            }

            writer.print((System.nanoTime() - time)+",");
            time = System.nanoTime();
            floatMatrix2D.forEachNonZero((i, i1, v) -> v*5);
            writer.println((System.nanoTime() - time)+",");


            time = System.nanoTime();
            floatMatrix2D.forEachNonZero((i, j, v) -> (float) Math.sqrt(i*j));

            writer.println((System.nanoTime() - time));

            writer.flush();
        }
    }

//    @Test
//    public void testColtSparseMatrix() throws IOException {
//        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:/Users/nkueh/Desktop/coltsparseMatrix" + NUMBER_OF_ITEMS + "_" + SPARSE_FACTOR + ".csv")));
//        writer.println("Initialization,Set,Get,Scale,Function");
//
//        for(int r= 0; r < RUNS; r++) {
//
//            long time = System.nanoTime();
//            SparseFloatMatrix2D floatMatrix2D = new SparseFloatMatrix2D(NUMBER_OF_ITEMS, NUMBER_OF_ITEMS);
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//            for (int i :keys) {
//                for (int j :keys) {
//                    floatMatrix2D.set(i, j, (float) i + j);
//                }
//            }
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//
//            for (int i :keys) {
//                for (int j :keys) {
//                    floatMatrix2D.get(i,j);
//                }
//            }
//
//            writer.print((System.nanoTime() - time)+",");
//            time = System.nanoTime();
//            floatMatrix2D.forEachNonZero((i, i1, v) -> v*5);
//            writer.println((System.nanoTime() - time)+",");
//
//            time = System.nanoTime();
//            floatMatrix2D.forEachNonZero((i, j, v) -> (float) Math.sqrt(i*j));
//
//            writer.println((System.nanoTime() - time));
//            writer.flush();
//        }
//    }
}
