package com.pb.sawdust.model.integration.transcad;

import com.pb.sawdust.io.ByteOrderDataInputStream;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.StandardTensorMetadataKey;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.alias.matrix.primitive.DoubleMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.FloatMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.IntMatrix;
import com.pb.sawdust.tensor.alias.matrix.primitive.ShortMatrix;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.IndexFactory;
import com.pb.sawdust.tensor.read.IndexReader;
import com.pb.sawdust.tensor.read.TensorGroupReader;
import com.pb.sawdust.tensor.read.TensorReader;
import com.pb.sawdust.util.JavaType;

import static com.pb.sawdust.util.Range.*;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;

/**
 * The {@code TranscadUncompressedMatrixReader} ...
 *
 * @author crf <br/>
 *         Started 5/2/11 7:53 AM
 */
public class TranscadUncompressedMatrixReader<T> implements TensorGroupReader<T,Integer>,IndexReader<Integer>,TensorReader<T,Integer> {
    private static final int MATRIX_ENTRY_HEADER_LENGTH = 76;
    private static final int INDEX_ENTRY_HEADER_LENGTH = 102;

    private final File matrixFile;
    private final TranscadMatrixHeader header;
    private final Map<String,Long> matrixPositionMap; //names will be in order
    private final Map<String,Long> indexPositionMap; //names will be in order
    private final List<TranscadMatrixUtil.SubIndexSpec> indexSpecs;

    private String currentMatrix;      
    private String currentMatrixRowIndex; //for reading matrices - currently set as default and unchangeable
    private String currentMatrixColumnIndex; //for reading matrices - currently set as default and unchangeable
    private String currentRowIndex; //for reading indices
    private String currentColumnIndex; //for reading indices
    
    private Map<String,Map<Integer,Integer>> subIndexCache = new HashMap<String,Map<Integer,Integer>>();

    public TranscadUncompressedMatrixReader(String matrixFile) {
        this(new File(matrixFile));
    }

    public TranscadUncompressedMatrixReader(File matrixFile) {
        this.matrixFile = matrixFile;
        header = new TranscadMatrixHeader(matrixFile);
        if (header.isCompressed())
            throw new IllegalStateException("Matrix file is compressed, cannot be read by this class: " + matrixFile.getPath());
        matrixPositionMap = getNamePositionMap(header.getMatrixStartPosition(), header.getMatrixCount());
        Map<TranscadMatrixUtil.SubIndexSpec,Long> indexSpecMap = getIndexSpecPositionMap(header.getIndexStartPosition(),header.getIndexCount());
        indexPositionMap = new LinkedHashMap<String,Long>();
        indexSpecs = new LinkedList<TranscadMatrixUtil.SubIndexSpec>();
        for (TranscadMatrixUtil.SubIndexSpec spec : indexSpecMap.keySet()) {
            indexPositionMap.put(spec.getName(),indexSpecMap.get(spec));
            indexSpecs.add(spec);
        }

        //set defaults
        currentMatrix = matrixPositionMap.keySet().iterator().next();
        for (TranscadMatrixUtil.SubIndexSpec spec : indexSpecs) {
            if (spec.isForRows()) {
                if (currentRowIndex == null)
                    currentRowIndex = spec.getName();
                if (currentMatrixRowIndex == null)
                    currentMatrixRowIndex = spec.getName();
            }
        }                                                            
        for (TranscadMatrixUtil.SubIndexSpec spec : indexSpecs) {
            if (spec.isForColumns()) {
                if (currentColumnIndex == null)
                    currentColumnIndex = spec.getName();
                if (currentMatrixColumnIndex == null)
                    currentMatrixColumnIndex = spec.getName();
            }
        }
    }
    
    public void setCurrentMatrix(String matrixName) {
        if (!matrixPositionMap.containsKey(matrixName))
            throw new IllegalArgumentException(String.format("Matrix not found in %s: %s",matrixFile.getPath(),matrixName));
        currentMatrix = matrixName;
    }
    
    public void setCurrentIndex(String rowIndex, String columnIndex) {
        if (!indexPositionMap.containsKey(rowIndex))                                                                        
            throw new IllegalArgumentException(String.format("Row index not found in %s: %s",matrixFile.getPath(),rowIndex));
        if (!indexPositionMap.containsKey(columnIndex))                                                                        
            throw new IllegalArgumentException(String.format("Column index not found in %s: %s",matrixFile.getPath(),columnIndex));
        if (!getSubIndexSpec(rowIndex).isForRows())
            throw new IllegalArgumentException(String.format("Specified row index os for columns only in %s: %s",matrixFile.getPath(),rowIndex));
        if (!getSubIndexSpec(columnIndex).isForColumns())
            throw new IllegalArgumentException(String.format("Specified column index os for columns only in %s: %s",matrixFile.getPath(),columnIndex));
        this.currentRowIndex = rowIndex;
        this.currentColumnIndex = columnIndex;
    }

    @Override
    public JavaType getType() {
        return header.getType();
    }

    @Override
    public int[] getDimensions() {
        return new int[] {header.getRowCount(),header.getColumnCount()};
    }

    @Override
    @SuppressWarnings("unchecked") //generic array is ok here
    public List<List<Integer>> getIds() {
        return Arrays.asList(getFullIdList(getSubIndexMap(currentMatrixRowIndex)),getFullIdList(getSubIndexMap(currentMatrixColumnIndex)));
    }

    @Override
    public Map<String,Object> getTensorMetadata() {
        Map<String,Object> metadata = new HashMap<String,Object>();
        metadata.put(StandardTensorMetadataKey.SOURCE.getKey(),matrixFile.getPath());
        metadata.put(StandardTensorMetadataKey.NAME.getKey(),currentMatrix);
        return metadata;
    }

    @Override
    public Tensor<T> fillTensor(Tensor<T> tensor) {
        ByteOrderDataInputStream inputStream = null;
        try {
            inputStream = getInputStream();
            fillTensor(inputStream,0,currentMatrix,tensor);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
        return tensor;
    }

    @Override
    @SuppressWarnings("unchecked") //cast to Tensor<T> is ok here, as we check types internally
    public Map<String,Tensor<T>> getTensorMap(TensorFactory defaultFactory) {
        Map<String,Tensor<T>> tensorMap = new HashMap<String,Tensor<T>>();
        int[] dimensions = getDimensions();
        for (String t : matrixPositionMap.keySet()) {
            Tensor<T> tensor;
            switch (header.getType()) {
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
            setCurrentMatrix(t);
            Map<String,Object> md = getTensorMetadata();
            for (String key : md.keySet())
                tensor.setMetadataValue(key,md.get(key));
            tensorMap.put(t,tensor);
        }
        return tensorMap;
    }

    /**
     * {@inheritDoc}
     *
     * This method returns an empty map. This is because TransCAD indices are inherently tied to rows and/or columns, and
     * combinations of them (which would be needed in this case) are not specified. Thus, to pull indices from this reader,
     * use the {@code IndexReader} functionality.
     */
    @Override
    public Map<String,Index<Integer>> getIndexMap(IndexFactory defaultFactory) {
        return Collections.emptyMap();
    }


    @Override
    public Map<String,Object> getTensorGroupMetadata() {
        Map<String,Object> metadata = new HashMap<String,Object>();
        metadata.put(StandardTensorMetadataKey.SOURCE.getKey(),matrixFile.getPath());
        return metadata;
    }

    @Override
    public Map<String,Tensor<T>> fillTensorGroup(Map<String,Tensor<T>> tensorGroup) {
        ByteOrderDataInputStream inputStream = null;
        try {
            inputStream = getInputStream();
            int lastPosition = 0;
            boolean nextPosition = false;
            for (String name : matrixPositionMap.keySet()) {
                if (nextPosition) {
                    lastPosition = matrixPositionMap.get(name).intValue(); //read to this position
                    nextPosition = false;
                }
                if (tensorGroup.containsKey(name)) {
                    fillTensor(inputStream,lastPosition,name,tensorGroup.get(name));
                    nextPosition = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
        return tensorGroup;
    }

    @Override
    public int[] getBaseDimensions() {
        return getDimensions();
    }

    @Override
    public int[][] getReferenceIndex() {
        TranscadMatrixUtil.SubIndexSpec rowSpec = getSubIndexSpec(currentRowIndex);
        int[] rowIndices = new int[rowSpec.getLength()];
        int counter = 0;
        for (Map.Entry<Integer,Integer> entry : getSubIndexMap(currentRowIndex).entrySet())
            rowIndices[counter++] = entry.getValue();

        TranscadMatrixUtil.SubIndexSpec columnSpec = getSubIndexSpec(currentColumnIndex);
        int[] columnIndices = new int[columnSpec.getLength()];
        counter = 0;
        for (Map.Entry<Integer,Integer> entry : getSubIndexMap(currentColumnIndex).entrySet())
            columnIndices[counter++] = entry.getValue();

        return new int[][] {rowIndices,columnIndices};
    }

    @Override
    @SuppressWarnings("unchecked") //generic array is ok here
    public List<List<Integer>> getIndexIds() {
        return Arrays.asList((List<Integer>) new LinkedList<Integer>(getSubIndexMap(currentRowIndex).keySet()),
                                             new LinkedList<Integer>(getSubIndexMap(currentColumnIndex).keySet()));
    }

    @Override
    public Map<String, Object> getIndexMetadata() {
        Map<String,Object> metadata = new HashMap<String,Object>();
        metadata.put(StandardTensorMetadataKey.SOURCE.getKey(),matrixFile.getPath());
        return metadata;
    }

    public List<String> getMatrixNames() {
        return new LinkedList<String>(matrixPositionMap.keySet());
    }

    public List<String> getRowIndexNames() {
        List<String> rowIndices = new LinkedList<String>(indexPositionMap.keySet());
        for (String indexName : indexPositionMap.keySet())
            if (getSubIndexSpec(indexName).isForRows())
                rowIndices.add(indexName);
        return rowIndices;
    }                                       

    public List<String> getColumnIndexNames() {
        List<String> columnIndices = new LinkedList<String>(indexPositionMap.keySet());
        for (String indexName : indexPositionMap.keySet())
            if (getSubIndexSpec(indexName).isForColumns())
                columnIndices.add(indexName);
        return columnIndices;
    }

    private ByteOrderDataInputStream getInputStream() throws IOException {
        return new ByteOrderDataInputStream(new BufferedInputStream(new FileInputStream(matrixFile)),ByteOrder.LITTLE_ENDIAN);
    }

    //sometimes skip inputStream.skip doesn't skip all of the bytes, so have to recursively do it till completion
    private void skip(long amount, ByteOrderDataInputStream inputStream) throws IOException {
        while (amount > 0)
            amount -= inputStream.skip(amount);
    }

    private Map<String,Long> getNamePositionMap(long startPosition, int count) {
        Map<String,Long> names = new LinkedHashMap<String,Long>(); //keep names in order

        ByteOrderDataInputStream inputStream = null;
        try {
            inputStream = getInputStream();
            long nextPosition = startPosition;
            long nextSkip = startPosition;
            while (names.size() < count) {
                skip(nextSkip,inputStream);
                names.put(TranscadMatrixUtil.readPaddedString(inputStream,64), nextPosition);
                skip(4,inputStream);
                long np = inputStream.readLong();
                nextSkip = np - nextPosition - MATRIX_ENTRY_HEADER_LENGTH; //76 = 64 ascii bytes, 4 paddings, and a long (8 bytes)
                nextPosition = np;
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
        return names;
    }

    private Map<TranscadMatrixUtil.SubIndexSpec,Long> getIndexSpecPositionMap(long startPosition, int count) {
        Map<TranscadMatrixUtil.SubIndexSpec,Long> names = new LinkedHashMap<TranscadMatrixUtil.SubIndexSpec,Long>(); //keep names in order

        ByteOrderDataInputStream inputStream = null;
        try {
            inputStream = getInputStream();
            long nextPosition = startPosition;
            long nextSkip = startPosition;
            while (names.size() < count) {
                skip(nextSkip,inputStream);
                String name = TranscadMatrixUtil.readPaddedString(inputStream,64); //64 bytes
                skip(4,inputStream);                                               // 4 bytes
                long np = inputStream.readLong();                                  // 8 bytes
                skip(12,inputStream);                                              //12 bytes
                boolean fullCoverage = inputStream.readShort() == 0;               // 2 bytes
                boolean forRows = inputStream.readShort() == 1;                    // 2 bytes
                boolean forCols = inputStream.readShort() == 1;                    // 2 bytes
                int length = inputStream.readInt();                                // 4 bytes
                                                                                   // 4 more bytes (another length entry)
                                                                                   // = 102 bytes

                names.put(new TranscadMatrixUtil.SubIndexSpec(name,forRows,forCols,length,fullCoverage),nextPosition);

                nextSkip = np - nextPosition - INDEX_ENTRY_HEADER_LENGTH + 4;
                nextPosition = np;
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
        return names;
    }

    private TranscadMatrixUtil.SubIndexSpec getSubIndexSpec(String indexName) {
        for (TranscadMatrixUtil.SubIndexSpec spec : indexSpecs)
            if (spec.getName().equals(indexName))
                return spec;
        throw new IllegalStateException(String.format("Index name not found in %s: %s",matrixFile.getPath(),indexName));
    }
    
    private Map<Integer,Integer> getSubIndexMap(String name) {
        if (!subIndexCache.containsKey(name)) {
            ByteOrderDataInputStream inputStream = null;
            try {
                inputStream = getInputStream();
                subIndexCache.put(name,readSubIndexMap(inputStream,0,getSubIndexSpec(name)));
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            } finally {
                if (inputStream != null)
                    inputStream.close();
            }
        }
        return  subIndexCache.get(name);
    }

    private List<Integer> getFullIdList(Map<Integer,Integer> subIndexMap) {
        Integer[] ids = new Integer[subIndexMap.size()];
        for (Map.Entry<Integer,Integer> entry : subIndexMap.entrySet())
            ids[entry.getValue()] = entry.getKey();
        return Arrays.asList(ids);
    }

    private void readAllSubIndices() { //only reads indices that are not already read
        ByteOrderDataInputStream inputStream = null;
        try {
            inputStream = getInputStream();
            int lastPosition = 0;
            boolean nextPosition = false;
            for (String name : indexPositionMap.keySet()) {
                if (nextPosition) {
                    lastPosition = indexPositionMap.get(name).intValue(); //read to this position
                    nextPosition = false;
                }
                if (!subIndexCache.containsKey(name)) {
                    TranscadMatrixUtil.SubIndexSpec spec = getSubIndexSpec(name);
                    subIndexCache.put(name,readSubIndexMap(inputStream,lastPosition,spec));
                    nextPosition = true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    private Map<Integer,Integer> readSubIndexMap(ByteOrderDataInputStream inputStream, long currentPosition, TranscadMatrixUtil.SubIndexSpec spec) throws IOException {
        Map<Integer,Integer> subIndexMap = new LinkedHashMap<Integer,Integer>(); //keep order
        skip(indexPositionMap.get(spec.getName()) - currentPosition + INDEX_ENTRY_HEADER_LENGTH,inputStream);
        for (int i : range(spec.getLength())) {
            int k = inputStream.readInt();
            int v = inputStream.readInt();
            subIndexMap.put(k,v);
        }
        return subIndexMap;
    }

    private Index<Integer> buildIndex(String rowSubIndex, String columnSubIndex) {
        TranscadMatrixUtil.SubIndexSpec rowSpec = getSubIndexSpec(rowSubIndex);
        int[] rowIndices = new int[rowSpec.getLength()];
        List<Integer> rowIds = new LinkedList<Integer>();
        int counter = 0;
        for (Map.Entry<Integer,Integer> entry : getSubIndexMap(rowSubIndex).entrySet()) {
            rowIndices[counter++] = entry.getValue();
            rowIds.add(entry.getKey());
        }

        TranscadMatrixUtil.SubIndexSpec columnSpec = getSubIndexSpec(columnSubIndex);
        int[] columnIndices = new int[columnSpec.getLength()];
        List<Integer> columnIds = new LinkedList<Integer>();
        counter = 0;
        for (Map.Entry<Integer,Integer> entry : getSubIndexMap(columnSubIndex).entrySet()) {
            columnIndices[counter++] = entry.getValue();
            columnIds.add(entry.getKey());
        }

        @SuppressWarnings("unchecked") //generic array is ok here
        Index<Integer> ind = new BaseIndex<Integer>(new int[][] {rowIndices,columnIndices}, Arrays.asList(rowIds,columnIds));
        return ind;
    }
    
    private void fillTensor(ByteOrderDataInputStream inputStream, long currentPosition, String matrixName, Tensor<T> tensor) throws IOException {
        skip(matrixPositionMap.get(matrixName) - currentPosition + MATRIX_ENTRY_HEADER_LENGTH,inputStream);
        int rows = tensor.size(0);
        int columns = tensor.size(1);
        if (header.isRowMajor()) {
            switch(header.getType()) {
                case SHORT : {
                    ShortMatrix matrix = (ShortMatrix) tensor;
                    for (int i : range(rows)) 
                        for (int j : range(columns)) 
                            matrix.setCell(inputStream.readShort(),i,j);
                    break;
                }                     
                case INT : {    
                    IntMatrix matrix = (IntMatrix) tensor;
                    for (int i : range(rows)) 
                        for (int j : range(columns)) 
                            matrix.setCell(inputStream.readInt(),i,j);
                    break;
                }
                case FLOAT : {  
                    FloatMatrix matrix = (FloatMatrix) tensor;
                    for (int i : range(rows)) 
                        for (int j : range(columns)) 
                            matrix.setCell(inputStream.readFloat(),i,j);
                    break;
                }
                case DOUBLE : {
                    DoubleMatrix matrix = (DoubleMatrix) tensor;
                    for (int i : range(rows)) 
                        for (int j : range(columns)) 
                            matrix.setCell(inputStream.readDouble(),i,j);
                    break;
                }
            }
        } else {
            switch(header.getType()) {
                case SHORT : {         
                    ShortMatrix matrix = (ShortMatrix) tensor;
                    for (int j : range(columns)) 
                        for (int i : range(rows)) 
                            matrix.setCell(inputStream.readShort(),i,j);
                    break;
                }                     
                case INT : {          
                    IntMatrix matrix = (IntMatrix) tensor;
                    for (int j : range(columns)) 
                        for (int i : range(rows)) 
                            matrix.setCell(inputStream.readInt(),i,j);
                    break;
                }
                case FLOAT : {         
                    FloatMatrix matrix = (FloatMatrix) tensor;
                    for (int j : range(columns))  
                        for (int i : range(rows)) 
                            matrix.setCell(inputStream.readFloat(),i,j);
                    break;
                }
                case DOUBLE : {             
                    DoubleMatrix matrix = (DoubleMatrix) tensor;
                    for (int j : range(columns))  
                        for (int i : range(rows)) 
                            matrix.setCell(inputStream.readDouble(),i,j);
                    break;
                }
            }
        }
    }

    public static void main(String ... args) {
        TensorReader<Integer,Integer> reader1 = new TranscadUncompressedMatrixReader<Integer>("d:/transfers/tc_mat/4x3_init_nc.mtx");
        IntMatrix mat1 = (IntMatrix) ArrayTensor.getFactory().tensor(reader1);
        System.out.println(TensorUtil.toString(mat1));
        System.out.println();

        TensorReader<Float,Integer> reader2 = new TranscadUncompressedMatrixReader<Float>("d:/transfers/tc_mat/ExternalDistanceMatrix.mtx");
        FloatMatrix mat2 = (FloatMatrix) ArrayTensor.getFactory().tensor(reader2);
        System.out.println(TensorUtil.toString(mat2));
        System.out.println();

        TranscadUncompressedMatrixReader<Float> reader3 = new TranscadUncompressedMatrixReader<Float>("d:/transfers/tc_mat/Trips_AM.mtx");
        reader3.setCurrentMatrix("Total Drive Trips");
        FloatMatrix mat3 = (FloatMatrix) ArrayTensor.getFactory().tensor(reader3);
        System.out.println(TensorUtil.toString(mat3));
        System.out.println();
    }
}
