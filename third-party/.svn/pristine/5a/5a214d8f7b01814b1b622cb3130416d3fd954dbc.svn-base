package com.pb.sawdust.tensor.read;

import com.pb.sawdust.io.DelimitedDataReader;
import com.pb.sawdust.io.IterableReader;
import com.pb.sawdust.tensor.StandardTensorMetadataKey;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.index.BaseIndex;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.StandardIndex;
import com.pb.sawdust.tensor.read.id.IdReader;
import com.pb.sawdust.tensor.read.id.IdTransfers;
import com.pb.sawdust.tensor.read.id.UniformIdReader;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.ArrayUtil;

import javax.xml.crypto.Data;
import java.io.File;
import java.util.*;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code CsvRectangularTensorReader} ...
 *
 * @author crf
 *         Started 8/30/11 7:19 AM
 */
public class CsvRectangularTensorReader<T,I> implements TensorReader<T,I> {
    //csv format: index0,index1,...,indexN-1,indexN_1,indexN_2,...
    private static final String DEFAULT_LAST_DIMENSION_PREFIX = "dim";

    /**
     * The tensor name that will be used for the tensor read by this class if none is specified.
     */
    public static final String DEFAULT_TENSOR_NAME = "tensor";
    
    /**
     * The index name which will be used for the index built by this class when reading a file.
     */
    public static final String DEFAULT_INDEX_NAME = "index";

    /*
    states:
    index,idreader specified: all reads go through it

    dimensions,idreader specified: index created/built on the fly
                                   ids go with index # in order they are encountered

    dimensions specified: index prebuilt as standard
     */
    
    private static enum ReaderMode {
        STANDARD,
        SPECIFIED_INDEX,
        BUILD_INDEX
    }

    private final String csvFile;
    private Index<I> index;
    private final IdReader<String,I> idReader;
    private final JavaType type;
    private final int[] dimensions;
    private String tensor = null;
    private String lastDimensionName = null;
    private final ReaderMode mode;
    private boolean condensedMode;
    private Object defaultValue = null;

    @SuppressWarnings("unchecked") //cannot ensure I will be Integer for standard mode, but if not it is a user definition error, so suppressing
    CsvRectangularTensorReader(String csvFile, Index<I> index, IdReader<String, I> idReader, JavaType type, int[] dimensions) {
        mode = idReader == null ? ReaderMode.STANDARD : (index == null ? ReaderMode.BUILD_INDEX : ReaderMode.SPECIFIED_INDEX);
        this.csvFile = csvFile;
        this.idReader = mode == ReaderMode.STANDARD ? (IdReader<String,I>) new UniformIdReader<String,Integer>(IdTransfers.STRING_TO_INT_TRANSFER) : idReader;
        this.type = type;
        this.dimensions = dimensions;
        this.index = mode == ReaderMode.STANDARD && index == null ? (Index<I>)  new StandardIndex(dimensions) : index;
    }

    /**
     * Constructor specifying the csv file, the type of the tensor, the index to use when building the tensor, and the id
     * reader to use to read the ids.
     *
     * @param csvFile
     *        The csv file.
     *
     * @param type
     *        The type the tensor will hold.
     *
     * @param index
     *        The index to use when building the tensor.
     *
     * @param idReader
     *        The reader to use to read the tensor ids.
     */
    public CsvRectangularTensorReader(String csvFile, JavaType type, Index<I> index, IdReader<String, I> idReader) {
        this(csvFile,index,idReader,type,index.getDimensions());
    }

    /**
     * Constructor specifying the csv file, the type of the tensor, the dimensions of the tensor, and the id
     * reader to use to read the ids. A default index built from the specified dimensions will be used to build the tensor.
     *
     * @param csvFile
     *        The csv file.
     *
     * @param type
     *        The type the tensor will hold.
     *
     * @param dimensions
     *        The dimensions of the tensor.
     *
     * @param idReader
     *        The reader to use to read the tensor ids.
     */
    public CsvRectangularTensorReader(String csvFile, JavaType type, int[] dimensions, IdReader<String, I> idReader) {
        this(csvFile,null,idReader,type,dimensions);
    }

    /**
     * Constructor specifying the csv file, the type of the tensor, and the dimensions of the tensor. A default index built
     * from the specified dimensions will be used to build the tensor, and a standard index reader will be used to read
     * the tensor indices.
     *
     * @param csvFile
     *        The csv file.
     *
     * @param type
     *        The type the tensor will hold.
     *
     * @param dimensions
     *        The dimensions of the tensor.
     */
    public CsvRectangularTensorReader(String csvFile, JavaType type, int[] dimensions) {
        this(csvFile,null,null,type,dimensions);
    }
    
    public void setTensorName(String name) {
        tensor = name;
    }
    
    public void setLastDimensionName(String name) {
        this.lastDimensionName = name;
    }
    
    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public int[] getDimensions() {
        return dimensions;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<List<I>> getIds() {
        getIndex();
        return index.getIndexIds();
    }

    private Index<I> getIndex() {
        if (index == null && mode == ReaderMode.BUILD_INDEX)
            buildIndex();
        return index;
    }

    private void buildIndex() {
        readInto(null);
    }

    @Override
    public Map<String, Object> getTensorMetadata() {
        Map<String,Object> metadata = new HashMap<String,Object>();
        metadata.put(StandardTensorMetadataKey.SOURCE.getKey(),csvFile);
        metadata.put(StandardTensorMetadataKey.NAME.getKey(), tensor);
        return metadata;
    }  

    @Override
    public Tensor<T> fillTensor(Tensor<T> tTensor) {
        setNames();
        return(readInto(tTensor));
    }
    
    private void setNames() {
        if (tensor == null)
            tensor = DEFAULT_TENSOR_NAME;
        if (lastDimensionName == null)
            lastDimensionName = DEFAULT_LAST_DIMENSION_PREFIX + (dimensions.length-1);
    }

    //used to get extension to improve performance
    Iterator<int[]> getAbacus(int[] dimensions) {
        return null;
    }

    private int[] updateIndex(List<List<I>> ids, I[] currentIds) {
        int[] indices = new int[currentIds.length];
        int counter = 0;
        for (List<I> dimIds : ids) {
            I currentId = currentIds[counter];
            int index = dimIds.indexOf(currentId);
            if (index < 0) {
                dimIds.add(currentId);
                index = dimIds.size()-1;
            }
            indices[counter++] = index;
        }
        return indices;
    }

    private Tensor<T> readInto(Tensor<T> t) {
        if (!Arrays.equals(getDimensions(), t.getDimensions()))
            throw new IllegalArgumentException(String.format("Tensor dimensions (%s) do not match reader dimensions {%s).",Arrays.toString(t.getDimensions()),Arrays.toString(getDimensions())));
        if (getType() != t.getType())
            throw new IllegalArgumentException(String.format("Tensor type (%s) and reader type (%s) do not match.",t.getType(),getType()));
                
        setNames();
//        int[] columnIndex = new int[tensorMapping.size()];
        int offset = getDimensions().length-1;
//        int counter = 0;
//        for (String tensor : tensorMapping.keySet()) {
//            columnIndex[counter++] = tensors.indexOf(tensor)+offset; //so we skip past index columns
//            objectTensors.add(tensorMapping.get(tensor));
//        }

        if (condensedMode && defaultValue != null) {
            //fill tensors with default
            switch (type) {
                case BYTE : TensorUtil.fill((ByteTensor) t, (Byte) defaultValue); break;
                case SHORT : TensorUtil.fill((ShortTensor) t,(Short) defaultValue); break;
                case INT : TensorUtil.fill((IntTensor) t,(Integer) defaultValue); break;
                case LONG : TensorUtil.fill((LongTensor) t,(Long) defaultValue); break;
                case FLOAT : TensorUtil.fill((FloatTensor) t,(Float) defaultValue); break;
                case DOUBLE : TensorUtil.fill((DoubleTensor) t,(Double) defaultValue); break;
                case CHAR : TensorUtil.fill((CharTensor) t,(Character) defaultValue); break;
                case BOOLEAN : TensorUtil.fill((BooleanTensor) t,(Boolean) defaultValue); break;
                default : {
                    @SuppressWarnings("unchecked") //cannot ensure default will be T, but generally should be correct (unless user error), so holding nose and suppressing
                    T dv = (T) defaultValue;
                    TensorUtil.fill(t,dv);
                } break;
            }
        }
                
        //setup reader, read header, and add index dimension name metadata
        IterableReader<String[]> reader = null;
        try {
            reader = new DelimitedDataReader(',').getLineIterator(new File(csvFile));
            Iterator<String[]> it = reader.iterator();
            Map<String,Object> indexMetadata = new HashMap<String,Object>();
            indexMetadata.put(StandardTensorMetadataKey.NAME.getKey(),DEFAULT_INDEX_NAME);
            String[] headerData = it.next();
            for (int i : range(dimensions.length-1))
                indexMetadata.put(StandardTensorMetadataKey.DIMENSION_NAME.getDetokenizedKey(i),headerData[i]);
            indexMetadata.put(StandardTensorMetadataKey.DIMENSION_NAME.getDetokenizedKey((dimensions.length-1)),lastDimensionName);

            //setup id reading stuff, if needed
            String[] fakeInput = new String[dimensions.length];
            Arrays.fill(fakeInput,"");
            @SuppressWarnings("unchecked") //cannot guarantee I type, but used internally so it doesn't matter
            I[] ids = (I[]) (mode == ReaderMode.STANDARD ? new Integer[dimensions.length] : idReader.getIdSink(fakeInput));
            List<List<I>> newIndexIds = null;
            int[] cutoffs = null;
            int cutoffsNeeded = dimensions.length-1;
            if (mode == ReaderMode.BUILD_INDEX) {
                newIndexIds = new LinkedList<List<I>>();
                for (int i : range(dimensions.length))
                    newIndexIds.add(new LinkedList<I>());
                if (!condensedMode) {
                    cutoffs = new int[dimensions.length-1];
                    cutoffs[cutoffs.length-1] = 1;
                    for (int i : range(cutoffs.length-1,0))
                        cutoffs[i-1] = dimensions[i]*cutoffs[i];
                }
                for (int i : range(getDimensions()[offset])) {
                    @SuppressWarnings("unchecked") //cannot ensure I will be Integer fro standard mode, but if not it is a user definition error, so suppressing
                    I id = (I) (mode == ReaderMode.STANDARD ? i : idReader.getId(headerData[i+offset],offset));
                    newIndexIds.get(offset).add(id);
                }
            }

            //generally used variables
            final boolean buildIndex = index == null && mode == ReaderMode.BUILD_INDEX;
            final boolean justBuildIndex = buildIndex && t == null;
            //final Abacus abacus = new Abacus(dimensions);
            final Iterator<int[]> abacus = getAbacus(dimensions);
            final boolean useAbacus = abacus != null;
            int[] ind;
            //go ahead and read the data
            
            //data mirrors
            ByteTensor byteT = null;
            ShortTensor shortT = null;
            IntTensor intT = null;
            LongTensor longT = null;
            FloatTensor floatT = null;
            DoubleTensor doubleT = null;
            BooleanTensor booleanT = null;
            CharTensor charT = null;
            switch (type) {
                case BYTE : byteT = (ByteTensor) t; break;
                case SHORT : shortT = (ShortTensor) t; break;
                case INT : intT = (IntTensor) t; break;
                case LONG : longT = (LongTensor) t; break;
                case FLOAT : floatT = (FloatTensor) t; break;
                case DOUBLE : doubleT = (DoubleTensor) t; break;
                case BOOLEAN : booleanT = (BooleanTensor) t; break;
                case CHAR : charT = (CharTensor) t; break;
            }

            int overallCounter = 0;
            while (it.hasNext()) {
                String[] data = it.next();
                String[] subData = new String[dimensions.length];
                if (useAbacus){
                    if (buildIndex && cutoffsNeeded > 0) {
                        for (int i = 0; i < cutoffsNeeded; i++) {
                            if (overallCounter % cutoffs[i] == 0) {
                                I id = idReader.getId(data[i],i);
                                newIndexIds.get(i).add(id);
                                if (newIndexIds.get(i).size() == dimensions[i])
                                    cutoffsNeeded--;
                            }
                        }
                    }
                }
                overallCounter++;
                if (justBuildIndex)
                    continue;
                System.arraycopy(data,0,subData,0,offset);
                for (int i : range(dimensions[offset])) {
                    if (useAbacus) {
                        ind = abacus.next();
                    } else {
                        subData[offset] = headerData[i+offset];
                        idReader.getIds(subData,0,ids);
                        ind = buildIndex ? updateIndex(newIndexIds,ids) : index.getIndices(ids);
                    }
                    switch (type) {
                        case BYTE : byteT.setCell(Byte.parseByte(data[offset+i]),ind); break;
                        case SHORT : shortT.setCell(Short.parseShort(data[offset+i]),ind); break;
                        case INT : intT.setCell(Integer.parseInt(data[offset+i]),ind); break;
                        case LONG : longT.setCell(Long.parseLong(data[offset+i]),ind); break;
                        case FLOAT : floatT.setCell(Float.parseFloat(data[offset+i]),ind); break;
                        case DOUBLE : doubleT.setCell(Double.parseDouble(data[offset+i]),ind); break;
                        case BOOLEAN : booleanT.setCell(Boolean.parseBoolean(data[offset+i]),ind); break;
                        case CHAR : charT.setCell(data[offset+i].charAt(offset+i),ind); break;
                        case OBJECT : {
                            @SuppressWarnings("unchecked") //cannot ensure default will be T, but generally should be correct (unless user error), so holding nose and suppressing
                            T tv = (T) data[offset+i];
                            t.setValue(tv,ind);
                        } break;
                    }
                }
            }

            if (buildIndex)
                index = new BaseIndex<I>(newIndexIds);
            if (buildIndex || mode == ReaderMode.SPECIFIED_INDEX)
                t = t.getReferenceTensor(index);
            for (String key : indexMetadata.keySet())
                if (!index.containsMetadataKey(key))
                    index.setMetadataValue(key,indexMetadata.get(key));
        } finally {
            if (reader != null)
                reader.close();
        }
        return t;
    }
}
