package com.pb.sawdust.model.integration.transcad;

import com.pb.sawdust.tensor.StandardTensorMetadataKey;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.ShortMatrix;
import com.pb.sawdust.tensor.group.TensorGroup;
import com.pb.sawdust.tensor.write.TensorGroupWriter;
import com.pb.sawdust.tensor.write.TensorWriter;
import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import transcad.DATA_TYPE;
import transcad.MATRIX_DIM;
import transcad.Matrix;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code TranscadNativeMatrixWriter} ...
 *
 * @author crf
 *         Started 7/13/12 6:39 AM
 */
public class TranscadNativeMatrixWriter<T extends Number> implements TensorWriter<T>,TensorGroupWriter<T,Integer> {
    public static final String TRANSCAD_MATRIX_EXTENSION = "mtx";
    public static final String DEFAULT_MATRIX_CORE_NAME = "matrix";

    private final Path outputFile;
    private final short compression;
    private final String matrixName;
    private final JavaType javaType;
    private List<List<Integer>> ids = null;

    public TranscadNativeMatrixWriter(Path outputFile, boolean compression, String matrixName, JavaType javaType, List<List<Integer>> ids) {
        this.outputFile = outputFile;
        this.compression = (short) (compression ? 1 : 0);
        this.matrixName = matrixName == null ? outputFile.getFileName().toString().replace("." + TRANSCAD_MATRIX_EXTENSION,"") : matrixName;
        this.javaType = javaType;
        this.ids = ids;
    }
    
    public TranscadNativeMatrixWriter(Path outputFile) {
        this(outputFile,true,null,null,null);
    }

    @SuppressWarnings("unchecked") //strange castings are correct, as TC ids are always integers
    private void setTensorIds(Tensor<? extends T> tensor) {
        ids = ((List<List<Integer>>) (List<?>) tensor.getIndex().getIndexIds());
    }

    private void checkDimensions(int[] dimensions) {
        if (dimensions.length != 2)
            throw new IllegalArgumentException("Cannot write non-matrix to TransCAD matrix; dimensions = " + Arrays.toString(dimensions));
    }

    @Override
    public void writeTensorGroup(TensorGroup<? extends T,? extends Integer> tensorGroup) {
        checkDimensions(tensorGroup.getDimensions());
        if (ids == null)
            setTensorIds(tensorGroup.getTensor(tensorGroup.tensorKeySet().iterator().next()));
        List<String> cores = new ArrayList<>(tensorGroup.tensorKeySet());
        JavaType actualJavaType = javaType == null ? tensorGroup.getTensor(tensorGroup.tensorKeySet().iterator().next()).getType() : javaType;
        Matrix matrix = setupMatrix(cores,actualJavaType);
        int counter = 0;
        for (String name : cores) {
            writeMatrix(matrix,tensorGroup.getTensor(name),counter++,actualJavaType);
        }
        finishActions(matrix);
    }

    private String getDefaultTensorName(Tensor<? extends T> tensor) {
        return (tensor.containsMetadataKey(StandardTensorMetadataKey.NAME.getKey())) ?
                    (String) tensor.getMetadataValue(StandardTensorMetadataKey.NAME.getKey()) : DEFAULT_MATRIX_CORE_NAME;
    }

    @Override
    public void writeTensor(Tensor<? extends T> tensor) {
        checkDimensions(tensor.getDimensions());
        if (ids == null)
            setTensorIds(tensor);
        JavaType actualJavaType = javaType == null ? tensor.getType() : javaType;
        Matrix matrix = setupMatrix(Arrays.asList(getDefaultTensorName(tensor)),actualJavaType);
        writeMatrix(matrix,tensor,0,actualJavaType);
        finishActions(matrix);
    }

    private long[] getIds(List<Integer> ids) {
        long[] lids = new long[ids.size()];
        int counter = 0;
        for (int id : ids)
            lids[counter++] = id;
        return lids;
    }

    private byte getJavaType(JavaType actualJavaType) {
        switch (actualJavaType) {
            case BYTE :
            case SHORT : return DATA_TYPE.SHORT_TYPE;
            case INT :
            case LONG : return DATA_TYPE.LONG_TYPE;
            case FLOAT : return DATA_TYPE.FLOAT_TYPE;
            case DOUBLE : return DATA_TYPE.DOUBLE_TYPE;
            default : throw new IllegalArgumentException("Invalid data type for TransCAD matrix: " + actualJavaType);
        }
    }

    private Matrix setupMatrix(List<String> cores, JavaType actualJavaType) {
        if (Files.exists(outputFile)) {
            try {
                Files.delete(outputFile);
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
        long[] rowIds = getIds(ids.get(0));
        long[] colIds = getIds(ids.get(1));
        try {
            return new Matrix(outputFile.toString(),matrixName,(short) cores.size(),(long) rowIds.length,(long) colIds.length,getJavaType(actualJavaType),compression,cores.toArray(new String[cores.size()]),rowIds,colIds);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private Object getValueArray(int columns, JavaType actualJavaType) {
        byte javaType = getJavaType(actualJavaType);
        if (javaType == DATA_TYPE.SHORT_TYPE)
            return new short[columns];
        else if (javaType == DATA_TYPE.LONG_TYPE)
            return new int[columns];
        else if (javaType == DATA_TYPE.FLOAT_TYPE)
            return new float[columns];
        else if (javaType == DATA_TYPE.DOUBLE_TYPE)
            return new double[columns];
        throw new IllegalStateException("Should not be here");
    } 
    
    private void setValues(short[] valueArray, ShortMatrix matrix, int rowNumber) {
        for (int i : range(valueArray.length))
            valueArray[i] = matrix.getCell(rowNumber,i);
    }
    
    private void setValues(int[] valueArray, IntMatrix matrix, int rowNumber) {
        for (int i : range(valueArray.length))
            valueArray[i] = matrix.getCell(rowNumber,i);
    }
    
    private void setValues(float[] valueArray, FloatMatrix matrix, int rowNumber) {
        for (int i : range(valueArray.length))
            valueArray[i] = matrix.getCell(rowNumber,i);
    }
    
    private void setValues(double[] valueArray, DoubleMatrix matrix, int rowNumber) {
        for (int i : range(valueArray.length))
            valueArray[i] = matrix.getCell(rowNumber,i);
    }

    private void writeMatrix(Matrix matrix, Tensor<? extends T> tensor, int coreNumber, JavaType actualJavaType) {
        int rows = (int) matrix.getBaseNRows();
        int cols = (int) matrix.getBaseNCols();
        if (rows != tensor.size(0) || cols != tensor.size(1))
            throw new IllegalArgumentException("Tensor and matrix are of incompatible sizes: " + Arrays.toString(tensor.getDimensions()) + " vs. [" + rows + "," + cols + "]");

        matrix.setCore(coreNumber);
        Object valueArray = getValueArray(cols,actualJavaType);
        switch (JavaType.getBaseComponentType(valueArray)) {
            case SHORT : {
                short[] varray = (short[]) valueArray;
                ShortMatrix m = (ShortMatrix) TensorUtil.asShortTensor(tensor);
                for (int r = 0; r < rows; r++) {
                    setValues(varray,m,r);
                    matrix.setBaseVector(MATRIX_DIM.MATRIX_ROW,r,valueArray);
                }
            } break;
            case INT : {
                int[] varray = (int[]) valueArray;
                IntMatrix m = (IntMatrix) TensorUtil.asIntTensor(tensor);
                for (int r = 0; r < rows; r++) {
                    setValues(varray,m,r);
                    matrix.setBaseVector(MATRIX_DIM.MATRIX_ROW,r,valueArray);
                }
            } break;
            case FLOAT : {
                float[] varray = (float[]) valueArray;
                FloatMatrix m = (FloatMatrix) TensorUtil.asFloatTensor(tensor);
                for (int r = 0; r < rows; r++) {
                    setValues(varray,m,r);
                    matrix.setBaseVector(MATRIX_DIM.MATRIX_ROW,r,valueArray);
                }
            } break;
            case DOUBLE : {
                double[] varray = (double[]) valueArray;
                DoubleMatrix m = (DoubleMatrix) TensorUtil.asDoubleTensor(tensor);
                for (int r = 0; r < rows; r++) {
                    setValues(varray,m,r);
                    matrix.setBaseVector(MATRIX_DIM.MATRIX_ROW,r,valueArray);
                }
            } break;
            default : throw new IllegalStateException("Should not be here");
        }
    }

    private void finishActions(Matrix matrix) {
        if (matrix != null)
            TranscadMatrixUtil.checkStatus(matrix.dispose(),matrix);

        String new_file = "MyMatrix.mtx";
//                existing_file = new java.io.File(new_file);
//                if (existing_file.exists()) {
//                    existing_file.delete();
//                }
//                boolean copied = my.Copy(new_file,null);
//                my.dispose();
//                my = null;
//
//
//                my = new transcad.Matrix("MyMatrix.MTX");
//                n_rows = (int) my.getBaseNRows();
//                n_cols = (int)  my.getBaseNCols();
//
//                //
//                // Write values to the matrix
//                //
//
//                tc_status = my.setCore(1);
//                for (r=0;r<n_rows;r++) {
//                    column[r] = (float) (1 + r);
//                }
//                for (c=1;c<n_cols-1;c++) {
//                    tc_status = my.setBaseVector(transcad.MATRIX_DIM.MATRIX_COL,c,column);
//                }
//
//                tc_status = my.setCore(1);
//                for (c=0;c<n_cols;c++) {
//                    row[c] = (float)(n_cols - c);
//                }
//                for (r=1;r<n_rows-1;r++) {
//                    tc_status = my.setBaseVector(transcad.MATRIX_DIM.MATRIX_ROW,r,row);
//                }
//
//                float float_value = my.getElementFloat(n_rows-1,n_cols-1);
//
//                debug = my.toString();
//                status = my.getStatusString();
//
//
//                System.err.println(debug);
//
//                System.err.println(status);
//
//                my.dispose();
//                my = null;
//
//                //
//                // Create a new matrix
//                //
//
//                String[] CoreNames = { "Core One" , "Core Two" , "Core Three" };
//                new_file = "MyNewMatrix.mtx";
//                String MatrixLabel = "My New Matrix";
//                short n_cores = 3;
//                byte data_type = transcad.DATA_TYPE.FLOAT_TYPE;
//                existing_file = new java.io.File(new_file);
//                if (existing_file.exists()) {
//                    existing_file.delete();
//                }
//                my = new transcad.Matrix(new_file,MatrixLabel,n_cores,n_rows,n_cols,data_type,CoreNames);
//
//                debug = my.toString();
//                status = my.getStatusString();
//
//
//                System.err.println(debug);
//
//                // System.err.println(status);
//
//                // Do something with the matrix
//                tc_status = my.setCore(1);
//
//                r = 2;
//                tc_status = my.setBaseVector(transcad.MATRIX_DIM.MATRIX_ROW,r,row);
//
//                c = 2;
//                tc_status = my.setBaseVector(transcad.MATRIX_DIM.MATRIX_COL,c,column);
//
//                my.dispose();
//                my = null;




    }
}
