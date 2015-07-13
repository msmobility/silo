package com.pb.sawdust.model.integration.transcad;

import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.StandardTensorMetadataKey;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.ShortMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.group.StandardTensorGroup;
import com.pb.sawdust.tensor.group.TensorGroup;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.IndexFactory;
import com.pb.sawdust.tensor.read.TensorGroupReader;
import com.pb.sawdust.tensor.read.TensorReader;
import com.pb.sawdust.util.JavaType;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import transcad.DATA_TYPE;
import transcad.Matrix;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The {@code TranscadNativeMatrixReader} is used to read TransCAD matrices using the native access API provided by the
 * TransCAD program. To allow this (and other related) class to compile, stub classes in the {@code transcad} package
 * are provided, but <i>they will not allow this class to operate correctly</i>. To use this class, one must place the
 * actual TransCAD <code>TranscadMatrix.jar</code> library provided by the TransCAD program (usually in
 * <code>[TransCAD_installation_directory]/GISDK/Matrices/</code>) in the classpath <i>before</i> the sources holding the
 * stub classes. Additionally, the TransCAD installation directory must be included in the system path (<i>not</i>
 * <code>java.library.path</code>) to allow the native programs to access their required linked libraries correctly. This
 * system path modification can be made permanently through the Windows system settings, or temporarily for a session via
 * a batch script.
 * <p>
 * This class implements both the {@code TensorReader} and {@code TensorGroupReader} interfaces. As a {@code TensorReader}
 * it reads a single tensor specified by the user (through {@link #setCurrentMatrix(String)}, or the first matrix core as
 * a default); as an {@code TensorGroupReader} all of the cores are read. Because of limitations in the TransCAD API,
 * matrices can only be read using a single matrix index; additionally, indices are only identifiable by their (0-based)
 * id, not their name. By default, the base row and column indices will be used, but these can be changed through the
 * {@link #setCurrentIndex(int,int)} method.
 * <p>
 * The reader uses a {@link TranscadBinaryDataConverter} when reading in the matrix data. This allows the conversion of
 * null matrix entries to user-specified values by setting the null-conversion value via {@link #setNullValue(Number)}.
 * <p>
 * If the TransCAD native code throws an exception or fails, then this will be captured and rethrown as an {@code IllegalStateException},
 * with a message attempting to provide as much information about the failure as possible.
 *
 * @author crf
 *         Started 4/6/12 1:55 PM
 */
public class TranscadNativeMatrixReader<T> implements TensorReader<T,Integer>,TensorGroupReader<T,Integer> {
    private final Path matrixFilePath;
    private final JavaType type;
    private final List<String> cores;
    private final int rowIndexCount;
    private final int columnIndexCount;
    private int currentCore = 0;
    private short currentRowIndex = 0;
    private short currentColumnIndex = 0;

    private final TranscadBinaryDataConverter dataConverter = new TranscadBinaryDataConverter();

    /**
     * Constructor specifying the matrix file. The first matrix core and default row/column indices will be set as the
     * current matrix and indices (these can be subsequently changed by calls to {@link #setCurrentMatrix(String)} and
     * {@link #setCurrentIndex(int,int)}).
     *
     * @param matrixFilePath
     *        The path to the matrix file.
     */
    public TranscadNativeMatrixReader(Path matrixFilePath) {
        this(matrixFilePath,null,0,0);
    }

    /**
     * Constructor specifying the matrix file and the current core. The default row/column indices will be set as the
     * current indices (these can be subsequently changed by calls to {@link #setCurrentIndex(int,int)}).
     *
     * @param matrixFilePath
     *        The path to the matrix file.
     *
     * @param core
     *        The matrix core to set as current.
     */
    public TranscadNativeMatrixReader(Path matrixFilePath, String core) {
        this(matrixFilePath,core,0,0);
    }

    /**
     * Constructor specifying the matrix file, as well as which core and row/column indices to set as the current ones.
     *
     * @param matrixFilePath
     *        The path to the matrix file.
     *
     * @param core
     *        The matrix core to set as current.
     *
     * @param rowIndex
     *        The (0-based) row index to set as current.
     *
     * @param columnIndex
     *        The (0-based) column index to set as current.
     *
     * @throws IllegalArgumentException if {@code core} is not a core in the matrix, or if either of {@code rowIndex} or
     *                                  {@code columnIndex} are invalid indices.
     */
    public TranscadNativeMatrixReader(Path matrixFilePath, String core, int rowIndex, int columnIndex) {
        this.matrixFilePath = matrixFilePath;
        Matrix matrix = null;
        try {
            matrix = new Matrix(matrixFilePath.toString());

            byte elementType = matrix.getElementType();
            if (elementType == DATA_TYPE.DOUBLE_TYPE)
               type = JavaType.DOUBLE;
            else if (elementType == DATA_TYPE.FLOAT_TYPE)
               type = JavaType.FLOAT;
            else if (elementType == DATA_TYPE.LONG_TYPE)
               type = JavaType.INT;
            else if (elementType == DATA_TYPE.SHORT_TYPE)
               type = JavaType.SHORT;
            else
                throw new IllegalStateException("Unknown matrix type from TransCAD: " + elementType);

            cores = new LinkedList<>();
            for (int i : range(matrix.getNCores()))
                cores.add(matrix.GetLabel(i));
            rowIndexCount = matrix.GetNIndices(0);
            columnIndexCount = matrix.GetNIndices(1);

        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (matrix != null)
                matrix.dispose();
        }

        if (core != null)
            setCurrentMatrix(core);
        setCurrentIndex(rowIndex,columnIndex);
    }

    /**
     * Set the value stored in a matrix when a (TransCAD) null value is read in.
     *
     * @param nullValue
     *        The value to use for null when reading a matrix.
     */
    public void setNullValue(Number nullValue) {
        switch (type) {
            case DOUBLE : dataConverter.setNullDoubleConversionValue(nullValue.doubleValue()); break;
            case FLOAT : dataConverter.setNullDoubleConversionValue(nullValue.floatValue()); break;
            case INT : dataConverter.setNullDoubleConversionValue(nullValue.intValue()); break;
            case SHORT : dataConverter.setNullDoubleConversionValue(nullValue.shortValue()); break;
        }
    }

    /**
     * Set the current matrix core. This specifies which matrix is read in when using the {@code TensorReader} methods.
     *
     * @param core
     *        The core to specify as the current matrix.
     *
     * @throws IllegalArgumentException if {@code core} does not exist in the matrix file.
     */
    public void setCurrentMatrix(String core) {
        int index = cores.indexOf(core);
        if (index < 0)
            throw new IllegalArgumentException("Core '" + core + "' not found in " + matrixFilePath);
        currentCore = index;
    }

    /**
     * Set the current indices for the matrix. This specifies the indices used when reading in any matrix; this affects
     * the number of entries and their ordering.
     *
     * @param rowIndex
     *        The (0-based) id of the row index.
     *
     * @param columnIndex
     *        The (0-based) id of the column index.
     *
     * @throws IllegalArgumentException if either {@code rowIndex} or {@code columnIndex} are invalid.
     */
    public void setCurrentIndex(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= rowIndexCount)
            throw new IllegalArgumentException("Invalid row index: " + rowIndex);
        if (columnIndex < 0 || columnIndex >= columnIndexCount)
            throw new IllegalArgumentException("Invalid column index: " + columnIndex);
        currentRowIndex = (short) rowIndex;
        currentColumnIndex = (short) columnIndex;
    }

    private void setIndices(Matrix matrix) {
        TranscadMatrixUtil.checkStatus(matrix.setIndex(0,currentRowIndex),matrix);
        TranscadMatrixUtil.checkStatus(matrix.setIndex(1,currentColumnIndex),matrix);
    }

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public int[] getDimensions() {
        Matrix matrix = null;
        try {
            matrix = new Matrix(matrixFilePath.toString());
            setIndices(matrix);
            int[] dimensions = new int[2];
            dimensions[0] = (int) matrix.getNRows();
            TranscadMatrixUtil.checkStatus(matrix);
            dimensions[1] = (int) matrix.getNCols();
            TranscadMatrixUtil.checkStatus(matrix);
            return dimensions;
        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (matrix != null)
                matrix.dispose();
        }
    }

    @Override
    @SuppressWarnings("unchecked") //cast to Tensor<T> is ok here, as we check types internally
    public Map<String,Tensor<T>> getTensorMap(TensorFactory defaultFactory) {
        Map<String,Tensor<T>> tensorMap = new HashMap<String,Tensor<T>>();
        int[] dimensions = getDimensions();
        for (String t : cores) {
            Tensor<T> tensor;
            List<List<Integer>> ids = getIds();
            if (ids != null) {
                switch (getType()) {
                    case BYTE : tensor = (Tensor<T>) defaultFactory.byteTensor(ids,dimensions); break;
                    case SHORT : tensor = (Tensor<T>) defaultFactory.shortTensor(ids,dimensions); break;
                    case INT : tensor = (Tensor<T>) defaultFactory.intTensor(ids,dimensions); break;
                    case LONG : tensor = (Tensor<T>) defaultFactory.longTensor(ids,dimensions); break;
                    case FLOAT : tensor = (Tensor<T>) defaultFactory.floatTensor(ids,dimensions); break;
                    case DOUBLE : tensor = (Tensor<T>) defaultFactory.doubleTensor(ids,dimensions); break;
                    case BOOLEAN : tensor = (Tensor<T>) defaultFactory.booleanTensor(ids,dimensions); break;
                    case CHAR : tensor = (Tensor<T>) defaultFactory.charTensor(ids,dimensions); break;
                    default : tensor = defaultFactory.tensor(ids,dimensions); break;
                }
            } else {
                switch (getType()) {
                    case BYTE : tensor = (Tensor<T>) defaultFactory.byteTensor(dimensions); break;
                    case SHORT : tensor = (Tensor<T>) defaultFactory.shortTensor(dimensions); break;
                    case INT : tensor = (Tensor<T>) defaultFactory.intTensor(dimensions); break;
                    case LONG : tensor = (Tensor<T>) defaultFactory.longTensor(dimensions); break;
                    case FLOAT : tensor = (Tensor<T>) defaultFactory.floatTensor(dimensions); break;
                    case DOUBLE : tensor = (Tensor<T>) defaultFactory.doubleTensor(dimensions); break;
                    case BOOLEAN : tensor = (Tensor<T>) defaultFactory.booleanTensor(dimensions); break;
                    case CHAR : tensor = (Tensor<T>) defaultFactory.charTensor(dimensions); break;
                    default : tensor = defaultFactory.<T>tensor(dimensions); break;
                }
            }
            setCurrentMatrix(t);
            Map<String,Object> md = getTensorMetadata();
            for (String key : md.keySet())
                tensor.setMetadataValue(key,md.get(key));
            tensorMap.put(t,tensor);
        }
        return tensorMap;
    }

    private String getRowIndexName() {
        return "rowIndex"+ currentRowIndex;
    }

    private String getColumnIndexName() {
        return "columnIndex"+ currentColumnIndex;
    }

    private String getIndexName() {
        return getRowIndexName() + "_" + getColumnIndexName();
    }

    @Override
    public Map<String,Index<Integer>> getIndexMap(IndexFactory defaultFactory) {
        Map<String,Index<Integer>> indexMap = new HashMap<>();
        indexMap.put(getIndexName(),new BaseIndex<>(getIds()));
        return indexMap;
    }

    @Override
    public Map<String,Object> getTensorGroupMetadata() {
        Map<String,Object> metadata = new HashMap<String,Object>();
        metadata.put(StandardTensorMetadataKey.SOURCE.getKey(),matrixFilePath.toString());
        return metadata;
    }

    @Override
    public Map<String,Tensor<T>> fillTensorGroup(Map<String,Tensor<T>> tensorGroup) {
        for (String core : tensorGroup.keySet()) {
            setCurrentMatrix(core);
            fillTensor(tensorGroup.get(core));
        }
        return tensorGroup;
    }

    @Override
    public List<List<Integer>> getIds() {
        List<List<Integer>> ids = new LinkedList<>();
        Matrix matrix = null;
        try {
            matrix = new Matrix(matrixFilePath.toString());
            setIndices(matrix);
            int[] tids = new int[(int) matrix.getNRows()];
            TranscadMatrixUtil.checkStatus(matrix.GetIDs(0,tids),matrix);
            ids.add(Arrays.asList(ArrayUtil.toIntegerArray(tids)));
            tids = new int[(int) matrix.getNCols()];
            TranscadMatrixUtil.checkStatus(matrix.GetIDs(1,tids),matrix);
            ids.add(Arrays.asList(ArrayUtil.toIntegerArray(tids)));
        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (matrix != null)
                matrix.dispose();
        }
        return ids;
    }

    @Override
    public Map<String,Object> getTensorMetadata() {
        Map<String,Object> metadata = new HashMap<String,Object>();
        metadata.put(StandardTensorMetadataKey.SOURCE.getKey(),matrixFilePath.toString());
        metadata.put(StandardTensorMetadataKey.NAME.getKey(),cores.get(currentCore));
        return metadata;
    }

    @Override
    public Tensor<T> fillTensor(Tensor<T> tensor) {
        Matrix matrix = null;
        int rowCount = tensor.size(0);
        int colCount = tensor.size(1);
        Object rowDataHolder;
        List<List<Integer>> ids = getIds();
        List<Integer> rowIds = ids.get(0);
        switch (type) {
            case DOUBLE : rowDataHolder = new double[rowCount]; break;
            case FLOAT : rowDataHolder = new float[rowCount]; break;
            case INT : rowDataHolder = new int[rowCount]; break;
            case SHORT : rowDataHolder = new short[rowCount]; break;
            default : throw new IllegalStateException("Shouldn't be here!");
        }
        try {
            matrix = new Matrix(matrixFilePath.toString());
            setIndices(matrix);
            TranscadMatrixUtil.checkStatus(matrix.setCore(currentCore),matrix);
            int rowCounter = -1;
            for (int i : rowIds) {
                rowCounter++;
                TranscadMatrixUtil.checkStatus(matrix.getVector(0,i,rowDataHolder),matrix);
                switch (type) {
                    case DOUBLE : {
                        double[] row = (double[]) rowDataHolder;
                        DoubleMatrix m = (DoubleMatrix) tensor;
                        for (int j : range(colCount))
                            m.setCell(dataConverter.getDouble(row[j]),rowCounter,j);
                    } break;
                    case FLOAT : {
                        float[] row = (float[]) rowDataHolder;
                        FloatMatrix m = (FloatMatrix) tensor;
                        for (int j : range(colCount))
                            m.setCell(dataConverter.getFloat(row[j]),rowCounter,j);
                    } break;

                    case INT : {
                        int[] row = (int[]) rowDataHolder;
                        IntMatrix m = (IntMatrix) tensor;
                        for (int j : range(colCount))
                            m.setCell(dataConverter.getInt(row[j]),rowCounter,j);
                    } break;

                    case SHORT : {
                        short[] row = (short[]) rowDataHolder;
                        ShortMatrix m = (ShortMatrix) tensor;
                        for (int j : range(colCount))
                            m.setCell(dataConverter.getShort(row[j]),rowCounter,j);
                    } break;
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (matrix != null)
                matrix.dispose();
        }
        return tensor;
    }

    public static void main(String ... args) {
        Path matrixFile = Paths.get("D:\\projects\\reno\\model\\scenarios\\test\\outputs\\mode_choice\\mode_choice_logsum_hbo.mtx");
        TensorFactory factory = ArrayTensor.getFactory();
//        Tensor<?> m = factory.tensor(new TranscadNativeMatrixReader(matrixFile,"bike",0,0));
//        Tensor<?> m = factory.tensor(new TranscadNativeMatrixReader(matrixFile));
//
//        IdTensor<?,Integer> mm = (IdTensor<?,Integer>) m;
//        System.out.println(TensorUtil.toString(m));
//        System.out.println(Arrays.toString(m.getDimensions()));

        TensorGroup<Float,Integer> group = new StandardTensorGroup<Float,Integer>(new TranscadNativeMatrixReader<Float>(matrixFile),factory,new IndexFactory());
        for (String tensorName : group.tensorKeySet()) {
            System.out.println(tensorName);
            System.out.println(TensorUtil.toString(group.getTensor(tensorName)));
        }

        TranscadMatrixHeader header = new TranscadMatrixHeader(matrixFile.toFile());
        System.out.println(header.getIndexCount());
    }
}
