package com.pb.sawdust.tensor.read;

import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.io.ZipFile;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.IndexFactory;
import com.pb.sawdust.tensor.write.ZipTensorWriter;
import com.pb.sawdust.util.JavaType;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * The {@code ZipTensorReader} is used to read tensors, tensor groups, and indices from a zip tensor file. The information
 * about the contents and format of a zip tensor file can be found in {@link ZipTensorWriter}.
 * <p>
 * Because a zip tensor file can hold multiple indices, tensors, and tensor groups, some additional methods are provided
 * to allow the specification of what is to be read. For each, there is a {@code setCurrentXXX} method, where <code>XXX</code>
 * is <code>Tensor</code>,<code>Index</code>, or <code>TensorGroup</code>; each is specified via its integer index in the
 * zip tensor. They all default to use the first entry (index = 0) in the zip tensor.
 *
 * @author crf <br/>
 *         Started Feb 8, 2010 10:01:59 PM
 */
public class ZipTensorReader<T,I> implements TensorReader<T,I>,IndexReader<I>,TensorGroupReader<T,I> {
    private final ZipFile zipFile;
    private int currentTensor;
    private int currentIndex;
    private int currentTensorGroup;

    private int[] dimensions;

    private JavaType type;
    private boolean isString;
    private List<List<I>> ids;
    private Map<String,Object> tensorMetadata;

    private List<List<I>> indexIds;
    private int[][] referenceIndices;
    private Map<String,Object> indexMetadata;
    
    private Map<String,Integer> tensorGroupTensorMap;
    private Map<String,Integer> tensorGroupIndexMap;
    private Map<String,Object> tensorGroupMetadata; 

    private boolean dimensionsInitialized = false;
    private boolean tensorInitialized;
    private boolean indexInitialized;
    private boolean tensorGroupInitialized;
//    private TensorFactory factory = ArrayTensor.getFactory();
    private Map<Integer,Tensor<T>> readTensors = new HashMap<Integer,Tensor<T>>();
    private Map<Integer,Index<I>> readIndices = new HashMap<Integer,Index<I>>();

    /**
     * Constructor specifying the zip tensor file.
     *
     * @param zipFile
     *        The zip tensor file.
     */
    public ZipTensorReader(File zipFile) {
        this.zipFile = new ZipFile(zipFile);
        setCurrentTensor(0);
        setCurrentIndex(0);
        setCurrentTensorGroup(0);
    }

    /**
     * Constructor specifying the zip tensor file.
     *
     * @param zipFile
     *        The zip tensor file.
     */
    public ZipTensorReader(String zipFile) {
        this(new File(zipFile));
    }

    /**
     * Set the tensor to be read. This is used when reading a single tensor via the {@code TensorReader} interface.
     *
     * @param currentTensor
     *        The index of tensor to be read.
     */
    public void setCurrentTensor(int currentTensor) {
        this.currentTensor = currentTensor;
        ids = null;
        tensorInitialized = false;
    }

    /**
     * Set the index to be read. This is used when reading a single index via the {@code IndexReader} interface, and specifies
     * the index to use when reading a tensor via the {@code TensorReader} interface.
     *
     * @param currentIndex
     *        The index of index to be read.
     */
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        indexIds = null;
        referenceIndices = null;
        indexInitialized = false;
    }

    /**
     * Set the tensor group to read. This is used when reading a tensor group via the {@code TensorGroupReader} interface.
     *
     * @param currentTensorGroup
     *        The index of the tensor group to be read.
     */
    public void setCurrentTensorGroup(int currentTensorGroup) {
        this.currentTensorGroup = currentTensorGroup;
        tensorGroupTensorMap = null;
        tensorGroupIndexMap = null;
        tensorGroupInitialized = false;
    }

    @Override
    public JavaType getType() {
        initializeTensor();
        return type;
    }

    @Override
    public int[] getDimensions() {
        initializeDimensions();
        return dimensions;
    }

    @Override
    public List<List<I>> getIds() {
        initializeTensor();
        return ids;
    }

    @Override
    public Map<String,Object> getTensorMetadata() {
        initializeTensor();
        return tensorMetadata;
    }

    @Override
    public int[] getBaseDimensions() {
        return getDimensions();
    }

    @Override
    public int[][] getReferenceIndex() {
        initializeIndex();
        return referenceIndices;
    }

    @Override
    public List<List<I>> getIndexIds() {
        initializeIndex();
        return indexIds;
    }

    @Override
    public Map<String, Object> getIndexMetadata() {
        initializeIndex();
        return indexMetadata;
    }
    
    @Override
    public Map<String,Tensor<T>> getTensorMap(TensorFactory defaultFactory) {
        initializeTensorGroup();
        Map<String,Tensor<T>> tensorMap = new LinkedHashMap<String,Tensor<T>>();
        int memory = currentTensor;
        for (String key : tensorGroupTensorMap.keySet()) {
            currentTensor = tensorGroupTensorMap.get(key);
            Tensor<T> tensor = readTensors.containsKey(currentTensor) ? readTensors.get(currentTensor) : defaultFactory.tensor(this);
            tensorMap.put(key,tensor);
        }
        currentTensor = memory;
        return tensorMap;
    }
    
    @Override
    public Map<String,Index<I>> getIndexMap(IndexFactory defaultFactory) {
        initializeTensorGroup();
        Map<String,Index<I>> indexMap = new LinkedHashMap<String,Index<I>>();
        int memory = currentIndex;
        for (String key : tensorGroupIndexMap.keySet()) {
            currentIndex = tensorGroupIndexMap.get(key);
            Index<I> index = readIndices.containsKey(currentIndex) ? readIndices.get(currentIndex) : defaultFactory.index(this);
            indexMap.put(key,index);
        }
        currentIndex = memory;
        return indexMap;
    }

    @Override
    public Map<String,Object> getTensorGroupMetadata() {
        initializeTensorGroup();
        return tensorGroupMetadata;
    }

    private void initializeDimensions() {
        if (!dimensionsInitialized) {
            loadDimensionData();
            dimensionsInitialized = true;
        }
    }

    private void initializeTensor() {
        initializeDimensions();
        if (!tensorInitialized) {
            loadTensorData();
            tensorInitialized = true;
        }
    }

    private void initializeIndex() {
        initializeDimensions();
        if (!indexInitialized) {
            loadIndexData();
            indexInitialized = true;
        }
    } 

    private void initializeTensorGroup() {
        initializeDimensions();
        if (!tensorGroupInitialized) {
            loadTensorGroupData();
            tensorGroupInitialized = true;
        }
    }

    private void loadDimensionData() {
        String dimString = zipFile.extractString(ZipTensorWriter.DIMENSION_ENTRY, Charset.forName("US-ASCII"));
        if (dimString.length() == 0) {
            dimensions = new int[0];
        } else {
        String[] dims = dimString.split("\\s");
        dimensions = new int[dims.length];
        for (int i = 0; i < dimensions.length; i++)
            dimensions[i] = Integer.parseInt(dims[i]);
        }
    }

    private void loadTensorData() {
        fillTensor(null);
    }
    
    private Object readObject(String type, DataInputStream dis) throws IOException {
        Object value;
        if (type.equals(ZipTensorWriter.STRING_TYPE_IDENTIFIER)) {
            value = dis.readUTF();
        } else {
            switch (JavaType.valueOf(type)) {
                case BOOLEAN : value = dis.readBoolean(); break;
                case CHAR : value = dis.readChar(); break;
                case BYTE : value = dis.readByte(); break;
                case SHORT : value = dis.readShort(); break;
                case INT : value = dis.readInt(); break;
                case LONG : value = dis.readLong(); break;
                case FLOAT : value = dis.readFloat(); break;
                case DOUBLE : value = dis.readDouble(); break;
                case OBJECT : {
                    ObjectInputStream os = new ObjectInputStream(dis);
                    try {
                        value = os.readObject();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeWrappingException(e);
                    }
                    break; 
                }
                default : throw new IllegalStateException("should not be here");
            }
        }
        return value;
    }
    
    private void loadTensorGroupData() {  
        tensorGroupTensorMap = new LinkedHashMap<String,Integer>();
        tensorGroupIndexMap = new LinkedHashMap<String,Integer>();
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new BufferedInputStream(zipFile.getInputStream(ZipTensorWriter.TENSOR_GROUP_ENTRY_PREFIX + currentTensorGroup)));
            tensorGroupMetadata = loadMetadata(dis);
            int count = dis.readInt();
            if (count > 0) {
                for (int i : range(count)) {
                    String key = dis.readUTF();
                    int tensorNumber = dis.readInt();
                    tensorGroupTensorMap.put(key,tensorNumber);
                }
            }                                                                                 
            count = dis.readInt();
            if (count > 0) {
                for (int i : range(count)) {
                    String key = dis.readUTF();
                    int indexNumber = dis.readInt();
                    tensorGroupIndexMap.put(key,indexNumber);
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    //swallow
                }
            }
        }
    }

    private Map<String,Object> loadMetadata(DataInputStream dis) throws IOException {           
        Map<String,Object> metadata = new HashMap<String,Object>();
        int count = dis.readInt();                                  
        while(count > 0) {
            String key = dis.readUTF();
            String type = dis.readUTF();
            metadata.put(key,readObject(type,dis));
            count--;
        }
        return metadata;
    }

    @SuppressWarnings("unchecked") //cannot ensure T will match read object type, but if not an exception will (usually - except for non-typical Tensor implementations) be thrown, so holding nose and suppressing
    private void loadTensorData(DataInputStream dis, Tensor<T> tensor) throws IOException {
        Iterable<int[]> ab = dimensions.length == 0 ? ab = Arrays.asList(new int[] {0}) : IterableAbacus.getIterableAbacus(true,dimensions);
        if (isString) {
            for (int[] index : ab)
                tensor.setValue((T) dis.readUTF(),index);
        } else {
            switch (type) {
                case BOOLEAN : {
                    BooleanTensor t = (BooleanTensor) tensor;
                    for (int[] index : ab)
                        t.setValue(dis.readBoolean(),index);
                    break;
                }
                case CHAR : {
                    CharTensor t = (CharTensor) tensor;
                    for (int[] index : ab)
                        t.setValue(dis.readChar(),index);
                    break;
                }
                case BYTE : {
                    ByteTensor t = (ByteTensor) tensor;
                    for (int[] index : ab)
                        t.setValue(dis.readByte(),index);
                    break;
                }
                case SHORT : {
                    ShortTensor t = (ShortTensor) tensor;
                    for (int[] index : ab)
                        t.setValue(dis.readShort(),index);
                    break;
                }
                case INT : {
                    IntTensor t = (IntTensor) tensor;
                    for (int[] index : ab)
                        t.setValue(dis.readInt(),index);
                    break;
                }
                case LONG : {
                    LongTensor t = (LongTensor) tensor;
                    for (int[] index : ab)
                        t.setValue(dis.readLong(),index);
                    break;
                }
                case FLOAT : {
                    FloatTensor t = (FloatTensor) tensor;
                    for (int[] index : ab)
                        t.setValue(dis.readFloat(),index);
                    break;
                }
                case DOUBLE : {
                    DoubleTensor t = (DoubleTensor) tensor;
                    for (int[] index : ab)
                        t.setValue(dis.readDouble(),index);
                    break;
                }
                case OBJECT : {
                    ObjectInputStream os = new ObjectInputStream(dis);
                    try {
                        for(int[] index : ab)
                            tensor.setValue((T) os.readObject(),index);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeWrappingException(e);
                    }
                    break;
                }
                default : throw new IllegalStateException("should not be here");
            }
        }
    }

    private void loadIndexData() {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new BufferedInputStream(zipFile.getInputStream(ZipTensorWriter.INDEX_ENTRY_PREFIX + currentIndex)));
            indexMetadata = loadMetadata(dis);
            int refSize = dis.readInt();
            if (refSize > -1) {
                referenceIndices = new int[refSize][];
                while (refSize > 0)
                    referenceIndices[--refSize] = new int[dis.readInt()];
                for (int i : range(referenceIndices.length))
                    for (int j : range(referenceIndices[i].length))
                        referenceIndices[i][j] = dis.readInt();
            }

            if (dis.readBoolean()) {
                indexIds = new LinkedList<List<I>>();
                for (int i : range(referenceIndices == null ? dimensions.length : referenceIndices.length)) {
                    List<Object> ids = new LinkedList<Object>(); //as generic a list as possible to avoid casts - we'll (unchecked) cast to I at end
                    String type = dis.readUTF();
                    boolean isString = type.equals(ZipTensorWriter.STRING_TYPE_IDENTIFIER);
                    if (isString) {
                        for (int j : range(referenceIndices == null ? dimensions[i] : referenceIndices[i].length))
                            ids.add(dis.readUTF());
                    } else {
                        switch (JavaType.valueOf(type)) {
                            case BOOLEAN : {
                                for (int j : range(referenceIndices == null ? dimensions[i] : referenceIndices[i].length))
                                    ids.add(dis.readBoolean());
                                break;
                            }     
                            case CHAR : {
                                for (int j : range(referenceIndices == null ? dimensions[i] : referenceIndices[i].length))
                                    ids.add(dis.readChar());
                                break;
                            }   
                            case BYTE : {
                                for (int j : range(referenceIndices == null ? dimensions[i] : referenceIndices[i].length))
                                    ids.add(dis.readByte());
                                break;
                            }
                            case SHORT : {
                                for (int j : range(referenceIndices == null ? dimensions[i] : referenceIndices[i].length))
                                    ids.add(dis.readShort());
                                break;
                            }
                            case INT : {
                                for (int j : range(referenceIndices == null ? dimensions[i] : referenceIndices[i].length))
                                    ids.add(dis.readInt());
                                break;
                            }
                            case LONG : {
                                for (int j : range(referenceIndices == null ? dimensions[i] : referenceIndices[i].length))
                                    ids.add(dis.readLong());
                                break;
                            }
                            case FLOAT : {
                                for (int j : range(referenceIndices == null ? dimensions[i] : referenceIndices[i].length))
                                    ids.add(dis.readFloat());
                                break;
                            }
                            case DOUBLE : {
                                for (int j : range(referenceIndices == null ? dimensions[i] : referenceIndices[i].length))
                                    ids.add(dis.readDouble());
                                break;
                            }
                            case OBJECT : {
                                 ObjectInputStream os = new ObjectInputStream(dis);
                                try {
                                    for (int j : range(referenceIndices == null ? dimensions[i] : referenceIndices[i].length))
                                        ids.add(os.readObject());
                                } catch (ClassNotFoundException e) {
                                    throw new RuntimeWrappingException(e);
                                }
                                break;
                            }
                        }
                    }
                    @SuppressWarnings("unchecked") //cannot ensure I will match actual ids, but if not the (usually) a user-definition/use error, so suppressing
                    List<I> lids = (List<I>) ids;
                    indexIds.add(lids);
                }
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    //swallow
                }
            }
        }

     }

    private List<List<I>> loadIndexIds(int indexId) {
        //if currently loaded, then just return
        if (currentIndex == indexId && indexInitialized)
            return indexIds;
        //remember current index settings, load up index, get ids, then put back to remembered index
        int memCurrentIndex = currentIndex;
        List<List<I>> memIndexIds = indexIds;
        int[][] memReferenceIndices = referenceIndices;
        currentIndex = indexId;
        loadIndexData();
        List<List<I>> ids = indexIds;
        if (memIndexIds != null) { //put back to original index state
            currentIndex = memCurrentIndex;
            indexIds = memIndexIds;
            referenceIndices = memReferenceIndices;
        }
        return ids;
    }

    public Tensor<T> fillTensor(Tensor<T> tensor) {
        DataInputStream dis = null;
        int idIndex = -1;
        try {
            dis = new DataInputStream(new BufferedInputStream(zipFile.getInputStream(ZipTensorWriter.TENSOR_ENTRY_PREFIX + currentTensor)));
            tensorMetadata = loadMetadata(dis);
            String sType = dis.readUTF();
            isString = sType.equals(ZipTensorWriter.STRING_TYPE_IDENTIFIER);
            type = isString ? JavaType.OBJECT : JavaType.valueOf(sType);
            idIndex = dis.readInt();
            if (tensor != null)
                loadTensorData(dis,tensor);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        } finally {
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    //swallow
                }
            }
        }
        if (idIndex > -1)
            ids = loadIndexIds(idIndex);
        return tensor;
    }

    public Map<String,Tensor<T>> fillTensorGroup(Map<String,Tensor<T>> tensorGroup) {
        initializeTensorGroup();
        for (String s : tensorGroup.keySet()) {
            setCurrentTensor(tensorGroupTensorMap.get(s));
            fillTensor(tensorGroup.get(s));
        }
        return tensorGroup;
    }

}
