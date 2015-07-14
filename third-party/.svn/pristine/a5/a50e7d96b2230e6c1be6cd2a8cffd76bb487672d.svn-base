package com.pb.sawdust.tensor.read;

import com.pb.sawdust.io.RuntimeCloseable;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.array.TypeSafeArray;
import com.pb.sawdust.util.array.TypeSafeArrayFactory;
import com.pb.sawdust.util.exceptions.RuntimeWrappingException;

import java.io.*;
import java.util.*;

import static com.pb.sawdust.util.Range.range;

/**
 * The {@code SerializedTensorReader} provides a reader for recreating tensors written with {@code SerializedTensorWriter}s.
 * This class also provides a static method for reading serialization streams generated with {@code SerializedTensorWriter}
 * without using the {@code TensorReader} framework.
 *
 * @author crf <br/>
 *         Started Feb 8, 2010 4:38:29 PM
 *
 * @see com.pb.sawdust.tensor.SerializableTensor
 * @see com.pb.sawdust.tensor.write.SerializedTensorWriter
 */
public class SerializedTensorReader<T,I> implements TensorReader<T,I>,RuntimeCloseable {
    private final ObjectInput oi;
    private JavaType type;
    private int[] dimensions;
    private TypeSafeArray<T> data = null; //will cache if necessary
    private List<List<I>> ids = null;
    private boolean readThroughIds = false;
    private Map<String,Object> metadata = null;

    private SerializedTensorReader(ObjectInput oi) {
        this.oi = oi;
    }

    /**
     * Constructor specifying an input stream to read from.
     *
     * @param is
     *        The input stream for this reader.
     */
    public SerializedTensorReader(InputStream is) {
        this(getObjectInput(is));
    }

    /**
     * Constructor specifying a file to read from.
     *
     * @param file
     *        The file to read from.
     */
    public SerializedTensorReader(File file) {
        this(getFileIntputStream(file));
    }

    /**
     * Constructor specifying a file to read from.
     *
     * @param file
     *        The file to read from.
     */
    public SerializedTensorReader(String file) {
        this(getFileIntputStream(new File(file)));
    }

    private static ObjectInput getObjectInput(InputStream is) {
        try {
            return new ObjectInputStream(is);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private static InputStream getFileIntputStream(File file) {
        try {
            return new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        }
    }
    
    @Override
    public JavaType getType() {
        initialize();
        return type;
    }

    @Override
    public int[] getDimensions() {
        initialize();
        return dimensions;
    }

    @Override
    public List<List<I>> getIds() {
        initialize();
        return ids;
    }

    @Override
    public Map<String,Object> getTensorMetadata() {
        deserializeDataAndMetadata(null);
        return metadata;
    }

    public Tensor<T> fillTensor(Tensor<T> tensor) {
        if (data != null) {
            tensor.setTensorValues(data);
            data = null;
        } else if (metadata != null) {
            throw new IllegalStateException("Data already read/used for this tensor reader.");
        } else {
            deserializeDataAndMetadata(tensor);
        }
        return tensor;
    }

    private void initialize() {
        if (!readThroughIds) { 
            deserializeToThroughIndices();      
            readThroughIds = true;
        }
    }

    /**
     * Close the input this reader reads from, if the reader implements {@code Closeable}.  This method should generally
     * be called when the reader (or, more specifically input) is no longer in use, to free up resources.
     */
    @Override
    public void close() {
        try {
            oi.close();
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private void deserializeToThroughIndices() {
        try {
            dimensions = (int[]) oi.readObject();
            type = (JavaType) oi.readObject();
            boolean hasIds = oi.readBoolean();
            if (hasIds) {                     
                ids = new LinkedList<List<I>>();
                for (int d : dimensions) {
                    List<I> dids = new LinkedList<I>();
                    for (int i : range(d)) {
                        @SuppressWarnings("unchecked") //cannot verify object will be of type I, but it is an error on the consumer/producer (user) end if not, so suppressing
                        I id = (I) oi.readObject();
                        dids.add(id);
                    }
                    ids.add(dids);
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeWrappingException(e);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private void deserializeDataAndMetadata(Tensor<T> tensor) {
        initialize();
        if (metadata == null) {
            if (tensor == null)
                deserializeDataToTypeSafeArray();
            else
                deserializeDataToTensor(tensor);
            deserializeMetadata();
        }
    }

    private void deserializeMetadata() {
        try {
            metadata = new HashMap<String,Object>();
            int mdCount = oi.readInt();
            while (mdCount > 0) {
                String key = (String) oi.readObject();
                metadata.put(key,oi.readObject());
                mdCount--;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeWrappingException(e);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    private void deserializeDataToTensor(Tensor<T> tensor) {
        try {
            IterableAbacus ab = IterableAbacus.getIterableAbacus(dimensions);
            switch (type) {
                case BOOLEAN : {
                    BooleanTensor t = (BooleanTensor) tensor;
                    for (int[] index : ab)
                        t.setCell(oi.readBoolean(),index);
                    break;
                }
                case CHAR : {
                    CharTensor t = (CharTensor) tensor;
                    for (int[] index : ab)
                        t.setCell(oi.readChar(),index);
                    break;
                }
                case BYTE : {
                    ByteTensor t = (ByteTensor) tensor;
                    for (int[] index : ab)
                        t.setCell(oi.readByte(),index);
                    break;
                }
                case SHORT : {
                    ShortTensor t = (ShortTensor) tensor;
                    for (int[] index : ab)
                        t.setCell(oi.readShort(),index);
                    break;
                }
                case INT : {
                    IntTensor t = (IntTensor) tensor;
                    for (int[] index : ab)
                        t.setCell(oi.readInt(),index);
                    break;
                }
                case LONG : {
                    LongTensor t = (LongTensor) tensor;
                    for (int[] index : ab)
                        t.setCell(oi.readLong(),index);
                    break;
                }
                case FLOAT : {
                    FloatTensor t = (FloatTensor) tensor;
                    for (int[] index : ab)
                        t.setCell(oi.readFloat(),index);
                    break;
                }
                case DOUBLE : {
                    DoubleTensor t = (DoubleTensor) tensor;
                    for (int[] index : ab)
                        t.setCell(oi.readDouble(),index);
                    break;
                }
                case OBJECT : {
                    for (int[] index : ab) {
                        @SuppressWarnings("unchecked") //cannot verify object will be of type T, but it is an error on the consumer/producer (user) end if not, so suppressing
                        T t = (T) oi.readObject();
                        tensor.setValue(t,index);
                    }
                    break;
                }
                default : throw new IllegalStateException("Shouldn't be here");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeWrappingException(e);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    @SuppressWarnings("unchecked") //few unchecked casts, but ok
    private void deserializeDataToTypeSafeArray() {
        try {
            data = (TypeSafeArray<T>) TypeSafeArrayFactory.typeSafeArray(type,dimensions);
            IterableAbacus ab = IterableAbacus.getIterableAbacus(dimensions);
            switch (type) {
                case BOOLEAN : {
                    for (int[] index : ab)
                        data.setValue((T) (Boolean) oi.readBoolean(),index);
                    break;
                }
                case CHAR : {
                    for (int[] index : ab)
                        data.setValue((T) (Character) oi.readChar(),index);
                    break;
                }
                case BYTE : {
                    for (int[] index : ab)
                        data.setValue((T) (Byte) oi.readByte(),index);
                    break;
                }
                case SHORT : {
                    for (int[] index : ab)
                        data.setValue((T) (Short) oi.readShort(),index);
                    break;
                }
                case INT : {
                    for (int[] index : ab)
                        data.setValue((T) (Integer) oi.readInt(),index);
                    break;
                }
                case LONG : {
                    for (int[] index : ab)
                        data.setValue((T) (Long) oi.readLong(),index);
                    break;
                }
                case FLOAT : {
                    for (int[] index : ab)
                        data.setValue((T) (Float) oi.readFloat(),index);
                    break;
                }
                case DOUBLE : {
                    for (int[] index : ab)
                        data.setValue((T) (Double) oi.readDouble(),index);
                    break;
                }
                case OBJECT : {
                    for (int[] index : ab)
                        data.setValue((T) oi.readObject(),index);
                    break;
                }
                default : throw new IllegalStateException("Shouldn't be here");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeWrappingException(e);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }

    /**
     * Deserialize a tensor from an input stream.  The stream must be positioned at the beginning of a tensor serialized
     * using {@link com.pb.sawdust.tensor.write.SerializedTensorWriter}.
     *
     * @param factory
     *        The tensor factory used to construct the new tensor.
     *
     * @param oi
     *        The input stream to read the serialized tensor from.
     *
     * @param <T>
     *        The type held by the tensor.
     *
     * @return a tensor constructed from {@code oi}.
     */
    public static <T> Tensor<T> deserializeTensor(TensorFactory factory, ObjectInput oi) {
        return factory.tensor(new SerializedTensorReader<T,Object>(oi));
    }
}
