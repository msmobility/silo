package com.pb.sawdust.util.concurrent;

import com.pb.sawdust.util.annotations.Transient;
import com.pb.sawdust.util.Range;
import com.pb.sawdust.util.ThreadTimer;
import com.pb.sawdust.util.array.ArrayUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLongArray;

/**
 * @author crf <br/>
 *         Started: Mar 2, 2010 1:09:47 PM
 */
@Transient
public class ForkJoinExamples {
    private static final ForkJoinPool pool = ForkJoinPoolFactory.getForkJoinPool();

    public ForkJoinExamples() {
        System.out.println("Parallelism: " + pool.getParallelism());
    }

    /* *************************Calculate Utilities********************************* */
    public double[][] calculateUtilitiesST(double[][] coefficients, double[][] variables) {
        double[][] result = new double[variables.length][coefficients.length];
        for (int i : Range.range(result.length)) {
            for (int j : Range.range(coefficients.length)) {
                double r = 0.0;
                for (int k : Range.range(coefficients[j].length)) {
                    r += coefficients[j][k]*variables[i][k];
                }
                result[i][j] = r;
            }
        }
        return result;
    }

    public double[][] calculateProbabilitiesST(double[][] coefficients, double[][] variables) {
        double[][] utilities = calculateUtilitiesST(coefficients,variables);
        double[][] probs = new double[utilities.length][utilities[0].length];
        for (int i : Range.range(probs.length)) {
            double total = 0.0;
            for (int j : Range.range(utilities[i].length)) {
                double result = Math.exp(utilities[i][j]);
                total += result;
                probs[i][j] = result;
            }
            for (int j : Range.range(utilities[i].length)) {
                probs[i][j] /= total;
            }
        }
        return probs;
    }

    public double[][] calculateUtilitiesFJ(double[][] coefficients, double[][] variables) {
        //hopefully variables' second dimension == coefficients
        double[][] result = new double[variables.length][coefficients.length];
        DnCRecursiveAction action = new CalculateUtilitiesTask1(coefficients,variables,result);
        pool.execute(action);
        action.getResult();
        return result;
    }

    public double[][] calculateProbabilitiesFJ(double[][] coefficients, double[][] variables) {
        double[][] results = calculateUtilitiesFJ(coefficients,variables);
        AtomicLongArray sums = new AtomicLongArray(results.length);
        DnCRecursiveAction action = new CalculateUtilitiesTask2(results,sums);
        pool.execute(action);
        action.getResult();
        double[] sumTotals = new double[results.length];
        for (int i : Range.range(sumTotals.length))
            sumTotals[i] = Double.longBitsToDouble(sums.get(i));
        action = new CalculateUtilitiesTask3(results,sumTotals);
        pool.execute(action);
        action.getResult();
        return results;
    }

    private class CalculateUtilitiesTask1 extends CalculateUtilitiesTaskBase {
        private static final long serialVersionUID = -8433079142052923154L;

        private final double[][] coefficients;
        private double[][] results;

        private CalculateUtilitiesTask1(double[][] coefficients, double[][] variables, double[][] results) {
            super(variables,coefficients.length);
            this.coefficients = coefficients;
            this.results = results;
        }

        private CalculateUtilitiesTask1(double[][] coefficients, double[][] variables, double[][] results, long start, long length, DnCRecursiveAction next) {
            super(variables,start,length,next);
            this.coefficients = coefficients;
            this.results = results;
        }

        protected void computeActionTask(int dim1Location, int dim2Location) {
            double result = 0.0;
            for (int i : Range.range(coefficients[dim2Location].length)) {
                result += coefficients[dim2Location][i]*variables[dim1Location][i];
            }
            results[dim1Location][dim2Location] = result;
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new CalculateUtilitiesTask1(coefficients,variables,results,start,length,next);
        }
    }

    private class CalculateUtilitiesTask2 extends CalculateUtilitiesTaskBase {
        private static final long serialVersionUID = -2886314839150504088L;

        private final AtomicLongArray sums;

        private CalculateUtilitiesTask2(double[][] results, AtomicLongArray sums) {
            super(results,results[0].length);
            this.sums = sums;
        }

        private CalculateUtilitiesTask2(double[][] results, AtomicLongArray sums, long start, long length, DnCRecursiveAction next) {
            super(results,start,length,next);
            this.sums = sums;
        }

        protected void computeActionTask(int dim1Location, int dim2Location) {
            double result = Math.exp(variables[dim1Location][dim2Location]);
            variables[dim1Location][dim2Location] = result;
            long lastValue = sums.get(dim1Location);
            while (!sums.compareAndSet(dim1Location,lastValue,Double.doubleToLongBits(result+Double.longBitsToDouble(lastValue))))
                lastValue = sums.get(dim1Location);
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new CalculateUtilitiesTask2(variables,sums,start,length,next);
        }
    }

    private class CalculateUtilitiesTask3 extends CalculateUtilitiesTaskBase {
        private static final long serialVersionUID = -574038602763105015L;

        private final double[] sums;

        private CalculateUtilitiesTask3(double[][] results, double[] sums) {
            super(results,results[0].length);
            this.sums = sums;
        }

        private CalculateUtilitiesTask3(double[][] results, double[] sums, long start, long length, DnCRecursiveAction next) {
            super(results,start,length,next);
            this.sums = sums;
        }

        protected void computeActionTask(int dim1Location, int dim2Location) {
            variables[dim1Location][dim2Location] /= sums[dim1Location];
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new CalculateUtilitiesTask3(variables,sums,start,length,next);
        }
    }

    private abstract class CalculateUtilitiesTaskBase extends DnCRecursiveAction {
        protected final double[][] variables;

        private CalculateUtilitiesTaskBase(double[][] variables,int secondLength) {
            super(0,variables.length*secondLength);
            this.variables = variables;
        }

        private CalculateUtilitiesTaskBase(double[][] variables, long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
            this.variables = variables;
        }

        abstract protected void computeActionTask(int dim1Location, int dim2Location);

        @Override
        protected void computeAction(long start, long length) {
            //does c[0],v[0];c[0],v[1]....c[1],v[0],...and so on
            long counter = 0;
            int dim2Location = (int) (start / variables.length);
            int dim1Location = (int) (start % variables.length);
            while (counter < length) {
                computeActionTask(dim1Location,dim2Location);
                if (++dim1Location == variables.length) {
                    dim1Location = 0;
                    dim2Location++;
                }
                counter++;
            }
        }

        @Override
        protected boolean continueDividing(long length) {
            return length > 50 && getSurplusQueuedTaskCount() <= 3;
        }
    }


    /* **********Vector Multiply*************** */

    public double vectorMultiplyST(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            throw new IllegalArgumentException("Vectors must be of equal length.");
        double result = 0.0;
        for (int i : Range.range(v1.length)) {
            result += v1[i]*v2[i];
        }
        return result;
    }

    public double vectorMultiplyFJ(double[] v1, double[] v2) {
        if (v1.length != v2.length)
            throw new IllegalArgumentException("Vectors must be of equal length.");
        DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction task = new FJVectorMultiplyAction(v1,v2);
        pool.execute(task);
        return task.getResult(); 
    }

    private class FJVectorMultiplyAction extends DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction {
        private static final long serialVersionUID = 1557187563888456455L;

        private final double[] v1;
        private final double[] v2;

        public FJVectorMultiplyAction(double[] v1, double v2[]) {
            super(0,v1.length);
            this.v1 = v1;
            this.v2 = v2;
        }

        private FJVectorMultiplyAction(double[] v1, double v2[], long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            super(start,length,next);
            this.v1 = v1;
            this.v2 = v2;
        }

        @Override
        protected void computeAction(long start, long length) {
            double result = 0;
            int end = (int) (start+length);
            for (int i : Range.range((int) start,end)) {
                result += v1[i]*v2[i];
            }
            setResult(result);
        }

        @Override
        protected DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction getNextAction(long start, long length, DnCRecursivePrimitiveAction.DnCRecursiveDoubleAction next) {
            return new FJVectorMultiplyAction(v1,v2,start,length,next);
        }

        @Override
        protected boolean continueDividing(long length) {
            return length > 1000 && getSurplusQueuedTaskCount() <= 3;
        }
    }

    /* ********************Sort******************* */
    private final int splitCriteria = 1000;
    public void sortST(int[] array) {
        int count = array.length/splitCriteria;
        if (array.length % splitCriteria == 0) count--;
        int[] current = Arrays.copyOfRange(array,count*splitCriteria,array.length);
        sort(current);
        for (int i : Range.range(count)) {
            int[] next = Arrays.copyOfRange(array,i*splitCriteria,(i+1)*splitCriteria);
            sort(next);
            current = mergeSortedArrays(current,next);
        }
        System.arraycopy(current,0,array,0,array.length);
    }

    public void sort(int[] array) {
        //bubble sort
        int end = array.length-1;
        for (int i : Range.range(end)) {
            for (int j : Range.range(0,end-i)) {
                if (array[j] > array[j+1]) {
                    int temp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = temp;
                }
            }
        }
    }

    private int[] mergeSortedArrays(int[] result1, int[] result2) {
        int[] result = new int[result1.length+result2.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < result1.length && j < result2.length) {
            result[k++] = (result1[i] < result2[j]) ? result1[i++] : result2[j++];
        }
        if (i < result1.length) {
            System.arraycopy(result1,i,result,k,result1.length-i);
        } else {
            System.arraycopy(result2,j,result,k,result2.length-j);
        }
        return result;
    }

    public void sortFJ(int[] array) {
        DnCRecursiveTask<int[]> task = new FJSortTask(array);
        pool.execute(task);
        System.arraycopy(task.getResult(),0,array,0,array.length);
    }

    private class FJSortTask extends DnCRecursiveTask<int[]> {
        private static final long serialVersionUID = -6234392662535140969L;

        private final int[] sourceArray;

        private FJSortTask(int[] sourceArray) {
            super(0,sourceArray.length);
            this.sourceArray = sourceArray;
        }

        private FJSortTask(int[] sourceArray, long start, long length, DnCRecursiveTask<int[]> next) {
            super(start,length,next);
            this.sourceArray = sourceArray;
        }

        @Override
        protected int[] computeTask(long start, long length) {
            int[] sub = Arrays.copyOfRange(sourceArray,(int) start,(int) (start+length));
            sort(sub);
            return sub;
        }

        @Override
        protected DnCRecursiveTask<int[]> getNextTask(long start, long length, DnCRecursiveTask<int[]> next) {
            return new FJSortTask(sourceArray,start,length,next);
        }

        @Override
        protected boolean continueDividing(long length) {
            return length > splitCriteria && getSurplusQueuedTaskCount() <= 3;
        }

        @Override
        protected int[] joinResults(int[] result1, int[] result2) {
            return mergeSortedArrays(result1,result2);
        }
    }

    /* *******************Process*********************** */
    private void runProcess(String output) {
        ProcessBuilder pb = new ProcessBuilder("python","--version");// > c:\\transfers\\" + output);
        try {
            Process p = pb.start();
            p.waitFor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void runProcessesST(int count) {
        for (int i : Range.range(count))
            runProcess("_output_test_" + count + ".tmp");
    }

    public void runProcessesFJ(int count) {
        ProcessFJAction pfja = new ProcessFJAction(count);
        pool.execute(pfja);
        pfja.getResult();
    }

    private class ProcessFJAction extends DnCRecursiveAction {
        private static final long serialVersionUID = 5676009752892154883L;

        public ProcessFJAction(int size) {
            super(0,size);
        }

        public ProcessFJAction(long start, long length, DnCRecursiveAction next) {
            super(start,length,next);
        }

        @Override
        protected void computeAction(long start, long length) {
            int end = (int) (start+length);
            for (int i : Range.range((int) start,end))
                runProcess("_output_test_" + i + ".tmp");
        }

        @Override
        protected DnCRecursiveAction getNextAction(long start, long length, DnCRecursiveAction next) {
            return new ProcessFJAction(start,length,next);
        }

        @Override
        protected boolean continueDividing(long length) {
            return length > 10 && getSurplusQueuedTaskCount() <= 3;
        }
    }

    /* ************************************************* */

    private void runReportOneVM(double[] v1, double[] v2, boolean st, boolean print) {
        ThreadTimer timer = new ThreadTimer();
        timer.startTimer();
        double result = st ? vectorMultiplyST(v1,v2) : vectorMultiplyFJ(v1,v2);
        long time = timer.endTimer();
        if (print)
            System.out.println((st ? "Single threaded" : "Fork-join") + " vector multiply" + (st ? " " : "       ") +
                " size = " + String.format("%8d",v1.length) +
                " (result = " + String.format("%15.5f",result) + "): " + time + " ns");
    }

    private void runReportVM(int size, boolean print) {
        Random random = new Random();
        double[] v1 = new double[size];
        double[] v2 = new double[size];
        for (int i : Range.range(v1.length)) {
            v1[i] = random.nextDouble();
            v2[i] = random.nextDouble();
        }
        runReportOneVM(v1,v2,true,print);
        runReportOneVM(v1,v2,false,print);
    }

    private double calculateResultSum(double[][] result) {
        double rsum = 0.0;
        for (double[] da : result)
            for (double d : da)
                rsum += d;
        return rsum;
    }

    private String calculateResultString(double[][] result) {
        return Arrays.toString(result[0]);
    }

    private void runReportOneUC(double[][] variables, double[][] coefficients, boolean st, boolean print) {
        ThreadTimer timer = new ThreadTimer();
        timer.startTimer();
        double[][] result = st ? calculateUtilitiesST(coefficients,variables) : calculateUtilitiesFJ(coefficients,variables);
        long time = timer.endTimer();
        if (print)
            System.out.println((st ? "Single threaded" : "Fork-join") + " utility calculation" + (st ? " " : "       ") +
                "     size = " + String.format("%8d,%8d,%8d",variables.length,coefficients.length,variables[0].length) +
    //            " (result = " + String.format("%15.5f",calculateResultSum(result)) + "): " + time + " ns");
                            " (result = " + calculateResultString(result) + "): " + time + " ns");
    }


    private void runReportOneUCP(double[][] variables, double[][] coefficients, boolean st, boolean print) {
        ThreadTimer timer = new ThreadTimer();
        timer.startTimer();
        double[][] result = st ? calculateProbabilitiesST(coefficients,variables) : calculateProbabilitiesFJ(coefficients,variables);
        long time = timer.endTimer();
        if (print)
            System.out.println((st ? "Single threaded" : "Fork-join") + " probability calculation" + (st ? " " : "       ") +
                " size = " + String.format("%8d,%8d,%8d",variables.length,coefficients.length,variables[0].length) +
    //            " (result = " + String.format("%15.5f",calculateResultSum(result)) + "): " + time + " ns");
                            " (result = " + calculateResultString(result) + "): " + time + " ns");
    }

    private void runReportUC(int observations, int variables, int choices, boolean print) {
        Random random = new Random();
        double[][] vars = new double[observations][variables];
        double[][] coeff = new double[choices][variables];
        for (int i : Range.range(variables)) {
            for (int j : Range.range(observations)) {
                vars[j][i] = random.nextDouble();
            }
            for (int j : Range.range(choices)) {
                coeff[j][i] = random.nextDouble();
            }
        }
        runReportOneUC(vars,coeff,true,print);
        runReportOneUC(vars,coeff,false,print);
        runReportOneUCP(vars,coeff,true,print);
        runReportOneUCP(vars,coeff,false,print);
    }

    public void runReportSort(int elementCount, boolean print) {
        ThreadTimer timer = new ThreadTimer();
        Random r = new Random();
        int[] array1 = new int[elementCount];
        for (int i : Range.range(array1.length))
            array1[i] = r.nextInt();
        int[] array2 = ArrayUtil.copyArray(array1);
        int[] array3 = ArrayUtil.copyArray(array1);
        timer.startTimer();
//        sort(array1);
        long time = timer.endTimer();
//        System.out.println("Single threaded bubblesort (size = " + array1.length + ": " + time + " ns");
        timer.startTimer();
        sortST(array2);
        time = timer.endTimer();
        if (print)
            System.out.println("Single threaded mergesort  (size = " + array1.length + ") result( " + getSortResult(array2) + "): " + time + " ns");
        timer.startTimer();
        sortFJ(array3);
        time = timer.endTimer();
        if (print)
            System.out.println("Fork join mergesort        (size = " + array1.length + ") result( " + getSortResult(array3) + "): " + time + " ns");

    }

    private String getSortResult(int[] array) {
        return "[" + array[0] + "," + array[1] + ",...," + array[array.length/2] + ",...," + array[array.length-2] + "," + array[array.length-1] + "]";
    }

    public static void main(String ... args) {
        ForkJoinExamples fje = new ForkJoinExamples();

        for (int z : Range.range(10)) {
            int size = 100;
            for (int i : Range.range(6)) {
                fje.runReportVM(size,z==9);
                size *= 10;
            }
        }

        for (int z : Range.range(10)) {
            fje.runReportUC(300000,30,5,z==9);
//            fje.runReportUC(30000,30,2,z==9);
        }

        for (int z : Range.range(10)) {
            fje.runReportSort(80000,z==9);
        }

        ThreadTimer timer = new ThreadTimer();
        int pcount = 50;
        timer.startTimer();
        fje.runProcessesST(pcount);
        long time = timer.endTimer();
        System.out.println("Single threaded process   : " + time + " ns");
        timer.startTimer();
        fje.runProcessesFJ(pcount);
        time = timer.endTimer();
        System.out.println("Fork join threaded process: " + time + " ns");

    }
}
