package com.pb.sawdust.model.integration.transcad;

import com.pb.sawdust.io.ByteOrderDataInputStream;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.tabledata.metadata.ColumnSchema;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.ShortMatrix;
import com.pb.sawdust.tensor.read.TensorReader;
import com.pb.sawdust.util.JavaType;

import com.pb.sawdust.util.array.*;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * The {@code TranscadBinaryMatrixReader} class provides a {@code MatrixReader} for reading Transcad matrices written
 * to the fixed-format binary format.  It assumes the default binary file extension (".bin") and dictionary file
 * extension (".dcb") are used.
 *
 * @author crf <br/>
 *         Started Aug 21, 2010 3:33:57 PM
 */
public class TranscadBinaryMatrixReader<T> implements TensorReader<T,Integer> {

    private final File dataFile;
    private final File dictFile;

    private JavaType type = null;
    private int[] dimensions;
    private List<List<Integer>> indices;
    private DataType internalType;
    private TypeSafeArray<T> data = null;
    private final TranscadBinaryDataConverter converter;
    private Map<String,Object> metadata = null;

    /**
     * Construct a new reader by specifying the file base and matrix dimensions.  The file base is the path to the
     * matrix data, minus the extension (".bin").  It is assumed that an equivalently named dictionary file (".dcb"
     * extension) exists.
     *
     * @param fileBase
     *        The base file path for the matrix data.
     *
     * @param dim0
     *        The size of the first dimension of the matrix.
     *
     * @param dim1
     *        The size of the second dimension of the matrix.
     */
    public TranscadBinaryMatrixReader(String fileBase, int dim0, int dim1) {
        dataFile = new File(fileBase + "." + TranscadBinaryTableReader.TRANSCAD_MATRIX_BINARY_FILE_EXTENSION);
        dictFile = new File(fileBase + "." + TranscadTableDictionary.TableType.FFB.getDefaultExtension());
        dimensions = new int[] {dim0,dim1};
        converter = new TranscadBinaryDataConverter();
    }

    /**
     * Get the data converter used by this reader. The converter returned by this method can be modified (<i>e.g.</i>
     * changing null data conversions) before reading in the table to modify the conversion behavior.
     *
     * @return the data converter used by this reader.
     */
    public TranscadBinaryDataConverter getConverter() {
        return converter;
    }
    
    private void loadBaseData() {
        if (type == null) { //only read this once...
            TranscadTableDictionary dict = new TranscadTableDictionary(dictFile);
            TableSchema schema = dict.getSchema();
            internalType = dict.getColumnExtraInformation().get(1).getBaseColumnType();
            boolean first = true;
            List<Integer> firstIndex = new LinkedList<Integer>();
            for (ColumnSchema col : schema) {
                if (first) {
                    first = false;
                    continue;
                }
                if (type == null)
                    type = col.getType().getJavaType();
                firstIndex.add(Integer.parseInt(col.getColumnLabel()));
            }                             
            indices = new LinkedList<List<Integer>>();
            indices.add(firstIndex);
        }
    }

    private void loadTensorData(Tensor<T> tensor) {
        loadBaseData();
        if (metadata == null) {
            if (tensor == null)
                loadDataIntoTypesafeArray();
            else
                loadDataIntoTensor(tensor);
            metadata = new HashMap<String, Object>();
        }
    }
    
    private void loadDataIntoTensor(Tensor<T> tensor) {                                
        List<Integer> secondIndex = new LinkedList<Integer>();
        final ByteOrderDataInputStream reader = converter.getBinaryReader(dataFile);
        final int dim0 = dimensions[0];
        try {
            switch (internalType) {
                case BYTE : {
                    ShortMatrix t = (ShortMatrix) tensor; //bytes are unsigned in TransCAD, so must become shorts for compatability
                    for (int i = 0; i < dim0; i++) {
                        secondIndex.add(converter.readInt(reader));
                        for (int j = 0; j < dim0; j++)
                            t.setCell(converter.readByte(reader), i, j);
                    }
                    break;
                }
                case SHORT : {
                    ShortMatrix t = (ShortMatrix) tensor; //bytes are unsigned in TransCAD, so must become shorts for compatability
                    for (int i = 0; i < dim0; i++) {
                        secondIndex.add(converter.readInt(reader));
                        for (int j = 0; j < dim0; j++)
                            t.setCell(converter.readShort(reader), i, j);
                    }
                    break;
                }
                case INT : {
                    IntMatrix t = (IntMatrix) tensor; //bytes are unsigned in TransCAD, so must become ints for compatability
                    for (int i = 0; i < dim0; i++) {
                        secondIndex.add(converter.readInt(reader));
                        for (int j = 0; j < dim0; j++)
                            t.setCell(converter.readInt(reader), i, j);
                    }
                    break;
                }
                case FLOAT : {
                    FloatMatrix t = (FloatMatrix) tensor; //bytes are unsigned in TransCAD, so must become floats for compatability
                    for (int i = 0; i < dim0; i++) {
                        secondIndex.add(converter.readInt(reader));
                        for (int j = 0; j < dim0; j++)
                            t.setCell(converter.readFloat(reader), i, j);
                    }
                    break;
                }
                case DOUBLE : {
                    DoubleMatrix t = (DoubleMatrix) tensor; //bytes are unsigned in TransCAD, so must become doubles for compatability
                    for (int i = 0; i < dim0; i++) {
                        secondIndex.add(converter.readInt(reader));
                        for (int j = 0; j < dim0; j++)
                            t.setCell(converter.readDouble(reader), i, j);
                    }
                    break;
                }
                default : throw new IllegalStateException("Invalid java type for TransCad matrix: " + type);
            }
        } catch (IOException e ) {
            throw new RuntimeIOException(e);
        } finally {
            try {
                reader.close();
            } catch (RuntimeIOException e) {
                //swallow
            }
        }
        indices.add(secondIndex);
    }  
    
    private void loadDataIntoTypesafeArray() {                                   
        List<Integer> secondIndex = new LinkedList<Integer>();
        @SuppressWarnings("unchecked") //cannot verify T matches type, but it is a user error if not, and an exception will/should be eventually thrown
        TypeSafeArray<T> a = (TypeSafeArray<T>) TypeSafeArrayFactory.typeSafeArray(type,dimensions);
        data = a;
        final ByteOrderDataInputStream reader = converter.getBinaryReader(dataFile);
        final int dim0 = dimensions[0];
        try {
            switch (internalType) {
                case BYTE : {
                    ShortTypeSafeArray parray = (ShortTypeSafeArray) data; //bytes are unsigned in TransCAD, so must become shorts for compatability
                    for (int i = 0; i < dim0; i++) {
                        secondIndex.add(converter.readInt(reader));
                        for (int j = 0; j < dim0; j++)
                            parray.set(converter.readByte(reader),i,j);
                    }
                    break;
                }
                case SHORT : {
                    ShortTypeSafeArray parray = (ShortTypeSafeArray) data;
                    for (int i = 0; i < dim0; i++) {
                        secondIndex.add(converter.readInt(reader));
                        for (int j = 0; j < dim0; j++)
                            parray.set(converter.readShort(reader),i,j);
                    }
                    break;
                }
                case INT : {
                    IntTypeSafeArray parray = (IntTypeSafeArray) data;
                    for (int i = 0; i < dim0; i++) {
                        secondIndex.add(converter.readInt(reader));
                        for (int j = 0; j < dim0; j++)
                            parray.set(converter.readInt(reader),i,j);
                    }
                    break;
                }
                case FLOAT : {
                    FloatTypeSafeArray parray = (FloatTypeSafeArray) data;
                    for (int i = 0; i < dim0; i++) {
                        secondIndex.add(converter.readInt(reader));
                        for (int j = 0; j < dim0; j++)
                            parray.set(converter.readFloat(reader),i,j);
                    }
                    break;
                }
                case DOUBLE : {
                    DoubleTypeSafeArray parray = (DoubleTypeSafeArray) data;
                    for (int i = 0; i < dim0; i++) {
                        secondIndex.add(converter.readInt(reader));
                        for (int j = 0; j < dim0; j++)
                            parray.set(converter.readDouble(reader),i,j);
                    }
                    break;
                }
                default : throw new IllegalStateException("Invalid java type for TransCad matrix: " + type);
            }
        } catch (IOException e ) {
            throw new RuntimeIOException(e);
        } finally {
            try {
                reader.close();
            } catch (RuntimeIOException e) {
                //swallow
            }
        }
        indices.add(secondIndex);
    }

    @Override
    public JavaType getType() {
        loadBaseData();
        return type;
    }

    @Override
    public int[] getDimensions() {
        loadBaseData();
        return dimensions;
    }

    @Override
    public List<List<Integer>> getIds() {
        loadTensorData(null);
        return indices;
    }

    @Override
    public Map<String,Object> getTensorMetadata() {
        loadTensorData(null);
        return metadata;
    }

    public Tensor<T> fillTensor(Tensor<T> tensor) {
        if (data != null) {
            tensor.setTensorValues(data);
            data = null;
        } else if (metadata != null) {
            throw new IllegalStateException("Data already read/used for this tensor reader.");
        } else {
            loadTensorData(tensor);
        }
        return tensor;
    }
}
