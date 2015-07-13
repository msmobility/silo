package com.pb.sawdust.tensor;

import com.pb.sawdust.tensor.decorators.id.IdTensor;
import com.pb.sawdust.tensor.factory.TensorFactory;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.read.SerializedTensorReader;
import com.pb.sawdust.tensor.write.SerializedTensorWriter;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.array.TypeSafeArray;

import java.io.*;
import java.util.Iterator;
import java.util.Set;

/**
 * The {@code SerializableTensor} provides a simple serializable tensor. For serialized output, all that it does is wrap
 * an input tensor and define serialization on it.  For serialized input, a new tensor is constructed with the appropriate
 * data which this object wraps; this unserialized tensor is available through the {@code getTensor()} method.  The
 * simplified serialization scheme from {@code SerializedTensorWriter} is used.
 * <p>
 * In order to deserialize a tensor, a {@code TensorFactory} is required; {@code INITIAL_DEFAULT_TENSOR_FACTORY} is
 * the initial default.  The factory can be changed by calling {@code setTensorFactory(TensorFactory)}, which also
 * sets the default tensor factory, though it has no effect on already created {@code SerializableTensor} accessed in
 * other threads.
 *
 * @author crf <br/>
 *         Started Feb 8, 2010 2:39:28 PM
 *
 * @see com.pb.sawdust.tensor.write.SerializedTensorWriter
 */
public class SerializableTensor<T> implements Tensor<T>,Externalizable {
    private static final long serialVersionUID = 4989524593606845246L;

    public static final TensorFactory INITIAL_DEFAULT_TENSOR_FACTORY = ArrayTensor.getFactory();
    private static volatile TensorFactory currentDefaultFactory = INITIAL_DEFAULT_TENSOR_FACTORY;
    private static ThreadLocal<TensorFactory> factories = new ThreadLocal<TensorFactory>() {
        protected TensorFactory initialValue() {
            return currentDefaultFactory;
        }
    };

    /**
     * Set the factory used by to construct a deserialized tensor. Calling this will set the factory for the current
     * thread as well as the default for all newly created {@code SerializableTensor} instances; all pre-existing
     * {@code SerializableTensor} instances used in separate threads will use the factory defined in that thread.
     *
     * @param factory
     *        The tensor factory to use.
     */
    public static void setTensorFactory(TensorFactory factory) {
        factories.set(factory);
        currentDefaultFactory = factory;
    }

    private Tensor<T> tensor;

    /**
     * Constructor specifying the tensor to wrap.
     *
     * @param tensor
     *        The tensor to wrap.
     */
    public SerializableTensor(Tensor<T> tensor) {
        this.tensor = tensor;
    }

    /**
     * Constructor required for serialization.  Should not be called directly.
     */
    public SerializableTensor() {}

    /**
     * Get the tensor wrapped by this class. This method is used to get the serialized tensor after it has been deserialized.
     *
     * @return the tensor wrapped by this class.
     */
    public Tensor<T> getTensor() {
        return tensor;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        SerializedTensorWriter.serializeTensor(tensor,out);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tensor = SerializedTensorReader.deserializeTensor(factories.get(),in);
    }

    @Override
    public int[] getDimensions() {
        return tensor.getDimensions();
    }

    @Override
    public int size(int dimension) {
        return tensor.size(dimension);
    }

    @Override
    public int size() {
        return tensor.size();
    }

    @Override
    public JavaType getType() {
        return tensor.getType();
    }

    @Override
    public T getValue(int... indices) {
        return tensor.getValue(indices);
    }

    @Override
    public void setValue(T value, int... indices) {
        tensor.setValue(value,indices);
    }

    @Override
    public TypeSafeArray<T> getTensorValues(Class<T> type) {
        return tensor.getTensorValues(type);
    }

    @Override
    public void setTensorValues(TypeSafeArray<? extends T> typeSafeArray) {
        setTensorValues(typeSafeArray);
    }

    @Override
    public void setTensorValues(Tensor<? extends T> tensor) {
        this.tensor.setTensorValues(tensor);
    }

    @Override
    public Index<?> getIndex() {
        return tensor.getIndex();
    }

    @Override
    public Iterator<Tensor<T>> iterator() {
        return tensor.iterator();
    }

    @Override
    public <I> IdTensor<T,I> getReferenceTensor(Index<I> index) {
        return tensor.getReferenceTensor(index);
    }

    @Override
    public int metadataSize() {
        return tensor.metadataSize();
    }

    @Override
    public Set<String> getMetadataKeys() {
        return tensor.getMetadataKeys();
    }

    @Override
    public boolean containsMetadataKey(String key) {
        return tensor.containsMetadataKey(key);
    }

    @Override
    public Object getMetadataValue(String key) {
        return tensor.getMetadataValue(key);
    }

    @Override
    public void setMetadataValue(String key, Object value) {
        tensor.setMetadataValue(key,value);
    }

    @Override
    public Object removeMetadataElement(String key) {
        return tensor.removeMetadataElement(key);
    }
}
