package com.pb.sawdust.tensor.write;

import com.pb.sawdust.io.RuntimeCloseable;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.tensor.StandardTensorMetadataKey;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.decorators.primitive.*;
import com.pb.sawdust.tensor.group.TensorGroup;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.StandardIndex;
import com.pb.sawdust.tensor.read.CsvTensorReader;
import com.pb.sawdust.util.JavaType;
import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.util.abacus.IterableAbacus;
import com.pb.sawdust.util.format.DelimitedDataFormat;
import com.pb.sawdust.util.format.TextFormat;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * The {@code CsvTensorWriter} class provides the functionality for writing out tensors to csv (comma-separated values)
 * files. This class can write one or more tensors of the same shape to a csv file, using the following format:
 * <pre><code>
 *     id0,id1,...,idn,t0,t1,...,tn
 * </code></pre>
 * where <code>idX</code> is the index id for (0-based) dimension <code>X</code> and <code>tX</code> is the value for (0-based)
 * tensor <code>X</code>.
 * <p>
 * All files written by this class will have a header, which specifies the dimension and tensor names as specified in the
 * index and tensor metadata (if the {@link StandardTensorMetadataKey#DIMENSION_NAME} and/or {@link StandardTensorMetadataKey#NAME}
 * keys are missing, then {@link #getDefaultDimensionName(int)} and {@link #DEFAULT_TENSOR_NAME} will be used, respectively;
 * for {@code TensorGroup}s, the tensor key value is used for the name instead of that specified in the metadata).
 * <p>
 * Only one index is used when writing tensors to a given file using this class. This class determines which index to use
 * via the following steps (in order):
 * <ul>
 *     <li>
 *         If this writer has been told to use a standard index (via the {@link #useStandardIndex()} method), then a standard
 *         (0-based, sequential numbering) index will be used.
 *     </li>
 *     <li>
 *         If the index has been directly specified (via the {@link #setIndex(com.pb.sawdust.tensor.index.Index)} method)
 *         then it will be used.
 *     </li>
 *     <li>
 *         If a {@code TensorGroup} is being written and the index name has been specified (via the {@link #setTensorGroupIndex(String)}
 *         method), then the index in the group with that name will be used.
 *     </li>
 *     <li>
 *         Otherwise, the tensor being written's index will be used directly (in the case of a {@code TensorGroup}, the
 *         first index of the first tensor returned by its iterator will be used).
 *     </li>
 * </ul>
 * It is noted that although this class implements the {@link TensorGroupWriter} interface, it cannot write all of the indices
 * contained in a given {@code TensorGroup}.
 * <p>
 * This class has the ability to write tensors in <i>condensed mode</i>, in which tensor values that equal a specified default
 * are not written out. If this class is being used in condensed mode, and all of the tensors being written have the default
 * value for a particular index position, then that entry (csv line) will not be written to the file. (It is important to
 * note that <i>all</i> of the tensors being written must have the default value at that index position for the line to be
 * skipped.) This can provide a savings in file size, as many redundant entries will be skipped, but the resulting csv file
 * may not contain enough information about the index to reconstruct it purely based on the file.
 * <p>
 * The standard default values used in condensed mode are just the default values for primitives and objects defined by
 * the Java Language Specification (JLS) (see also {@link com.pb.sawdust.util.JavaType#getDefaultObject()}). These
 * defaults can be overridden via the {@link #setDefaultValue(com.pb.sawdust.util.JavaType, Object)} method. Note that
 * neither the fact that the tensor is written in condensed mode, nor its default value, is recorded in the csv file written
 * by this class; thus, care should be taken when writing in condensed mode, as it limits the portability/independence of
 * the stored data.
 *
 * @author crf <br/>
 *         Started Oct 12, 2010 6:26:18 AM
 */
public class CsvTensorWriter<T,I> implements TensorWriter<T>,TensorGroupWriter<T,I>,RuntimeCloseable {
    private static final TextFormat STRING_FORMAT = new TextFormat(TextFormat.Conversion.STRING);
    private static final Map<JavaType,TextFormat> DEFAULT_FORMATS = new EnumMap<JavaType,TextFormat>(JavaType.class);
    static {
        TextFormat iFormat = new TextFormat(TextFormat.Conversion.INTEGER);
        TextFormat fFormat = new TextFormat(TextFormat.Conversion.FLOATING_POINT);
        DEFAULT_FORMATS.put(JavaType.BYTE,iFormat);
        DEFAULT_FORMATS.put(JavaType.SHORT,iFormat);
        DEFAULT_FORMATS.put(JavaType.INT,iFormat);
        DEFAULT_FORMATS.put(JavaType.LONG,iFormat);
        DEFAULT_FORMATS.put(JavaType.FLOAT,fFormat);
        DEFAULT_FORMATS.put(JavaType.DOUBLE,fFormat);
        DEFAULT_FORMATS.put(JavaType.CHAR,new TextFormat(TextFormat.Conversion.CHARACTER));
        DEFAULT_FORMATS.put(JavaType.BOOLEAN,new TextFormat(TextFormat.Conversion.BOOLEAN));
        DEFAULT_FORMATS.put(JavaType.OBJECT,STRING_FORMAT);
    }

    /**
     * The default tensor name.
     */
    public static final String DEFAULT_TENSOR_NAME = CsvTensorReader.formDefaultTensorName(0); //this is used just for single tensor

    /**
     * Get the default name for a given tensor dimension.
     *
     * @param dim
     *        The tensor dimension.
     *
     * @return the default name for dimension {@code dim}.
     */
    public static String getDefaultDimensionName(int dim) {
        return "dim" + dim;
    }

    private final PrintWriter writer;
    private final DelimitedDataFormat formatter;
    private final Map<JavaType,TextFormat> overrideFormats;
    
    private Index<I> index = null;
    private String indexName = null;
    private boolean useStandardIndex = false;
    private boolean condensedMode = false;

    private static PrintWriter getWriter(File file, Charset charset) {
        try {
            return(new PrintWriter(file,charset.name()));
        } catch (FileNotFoundException e) {
            throw new RuntimeIOException(e);
        }   catch (UnsupportedEncodingException e) {
            throw new RuntimeIOException(e);
        }
    }

    private CsvTensorWriter(PrintWriter writer) {
        this.writer = writer;
        formatter = new DelimitedDataFormat(',');
        overrideFormats = new EnumMap<JavaType,TextFormat>(JavaType.class);
    }

    /**
     * Constructor specifying the writer to write the tensor to.
     *
     * @param writer
     *        The writer to write the tensor to.
     */
    public CsvTensorWriter(Writer writer) {
        this(writer instanceof  PrintWriter ? (PrintWriter) writer : new PrintWriter(writer));
    }

    /**
     * Constructor specifying the file to write the tensor to, as well as the character set to use when writing the
     * tensor.
     *
     * @param file
     *        The file to write the tensor to.
     *
     * @param charset
     *        The character set to use when writing the tensor.
     */
    public CsvTensorWriter(File file, Charset charset) {
        this(getWriter(file,charset));
    }

    /**
     * Constructor specifying the file to write the tensor to, as well as the character set to use when writing the
     * tensor.
     *
     * @param file
     *        The file to write the tensor to.
     *
     * @param charset
     *        The character set to use when writing the tensor.
     */
    public CsvTensorWriter(String file, Charset charset) {
        this(new File(file),charset);
    }

    /**
     * Constructor specifying the file to write the tensor to. The default character set will be used when writing the
     * tensor.
     *
     * @param file
     *        The file to write the tensor to.
     */
    public CsvTensorWriter(File file) {
        this(file,Charset.defaultCharset());
    }

    /**
     * Constructor specifying the file to write the tensor to. The default character set will be used when writing the
     * tensor.
     *
     * @param file
     *        The file to write the tensor to.
     */
    public CsvTensorWriter(String file) {
        this(new File(file));
    }

    /**
     * Constructor specifying the output stream to write the tensor to.
     *
     * @param outputStream
     *        The output stream to write the tensor to.
     */
    public CsvTensorWriter(OutputStream outputStream) {
        this(new PrintWriter(outputStream));
    }

    /**
     * Set the text format to use for the specified java type.
     *
     * @param type
     *        The type the format will apply to.
     *
     * @param format
     *        The format to use for the specified type.
     */
    public void setFormat(JavaType type, TextFormat format) {
        overrideFormats.put(type,format);
    }

    /**
     * Set the writer to use the standard (0-based sequential integer) index when writing out the tensor(s).
     */
    public void useStandardIndex() {
        useStandardIndex = true;
        index = null;
        indexName = null;
    }

    /**
     * Set the index to use when writing out the tensor(s).
     *
     * @param index
     *        The index to use when writing the tensor(s).
     */
    public void setIndex(Index<I> index) {
        this.index = index;
        useStandardIndex = false;
        indexName = null;
    }

    /**
     * Set the writer to use the specified index from the tensor group when writing the tensor(s).
     *
     * @param indexName
     *        The name of the index to use when writing the tensor(s).
     */
    public void setTensorGroupIndex(String indexName) {
        this.indexName = indexName;
        index = null;
        useStandardIndex = false;
    }

    /**
     * Set the writer to use condensed mode or not.
     *
     * @param condensedMode
     *        If {@code true}, the writer will use condensed mode, if {@code false}, the writer will write the entire
     *        tensor(s).
     */
    public void setCondensedMode(boolean condensedMode) {
        this.condensedMode = condensedMode;
    }

    @SuppressWarnings("unchecked") //casting from ? extends I to I, but generally will/should be ok, so suppressing
    private Index<I> getIndexForWriting(Tensor<?> tensor, TensorGroup<?,? extends I> tensorGroup) {
        Index<I> ind;
        if (useStandardIndex) {
            ind = (Index<I>) new StandardIndex(tensor.getDimensions());
        } else if (index != null) {
            if (!index.isValidFor(tensor))
                throw new IllegalStateException("Specified index (dimensions: " + Arrays.toString(index.getDimensions()) +
                                                 ") is not valid for tensor (dimensions: " + Arrays.toString(tensor.getDimensions()) + ")");
            ind = index;
        } else if (tensorGroup != null && indexName != null) {
            ind = (Index<I>) tensorGroup.getIndex(indexName);
        } else {
            ind = (Index<I>) tensor.getIndex();
        }
        if (tensorGroup != null) {
            for (int i : range(tensorGroup.getDimensions().length)) {
                String key = StandardTensorMetadataKey.DIMENSION_NAME.getDetokenizedKey(i);
                if (tensorGroup.containsMetadataKey(key)) {
                    ind.setMetadataValue(key,tensorGroup.getMetadataValue(key));
                }
            }
        }
        return ind;
    }

    @Override
    public void writeTensorGroup(TensorGroup<? extends T,? extends I> tensorGroup) {
        writeTensors(tensorGroup.tensorKeySet(),tensorGroup);
    }

    private void writeTensors(Collection<String> tensorNames, TensorGroup<? extends T,? extends I> tensorGroup) {
        Map<String,Tensor<?>> tensors = new LinkedHashMap<String,Tensor<?>>(); //in case ordering matters
        for (String tensorName : tensorNames)
            tensors.put(tensorName,tensorGroup.getTensor(tensorName));
        //for the moment, write with first tensor's index if none specified
        writeTensors(tensors,getIndexForWriting(tensors.get(tensorNames.iterator().next()),tensorGroup));
    }

    @Override
    public void writeTensor(Tensor<? extends T> tensor) {
        Map<String,Tensor<?>> t = new HashMap<String,Tensor<?>>();
        String nameKey = StandardTensorMetadataKey.NAME.getKey();
        t.put(tensor.containsMetadataKey(nameKey) ? (String) tensor.getMetadataValue(nameKey) : DEFAULT_TENSOR_NAME,tensor);
        writeTensors(t,getIndexForWriting(tensor,null));
    }

    @SuppressWarnings("cast") //Object[] cast is to make explicit the intention when calling this method
    private void writeTensors(Map<String,Tensor<?>> tensors, Index<I> index) {
        int[] dims = index.getDimensions();
        List<String> header = new LinkedList<String>();
        TextFormat[] formats = new TextFormat[tensors.size()+dims.length];
        JavaType[] types = new JavaType[formats.length]; //will ingore dimension types
        for (int i : range(dims.length)) {
            String dKey = StandardTensorMetadataKey.DIMENSION_NAME.getDetokenizedKey(i);
            header.add(index.containsMetadataKey(dKey) ? index.getMetadataValue(dKey).toString() : getDefaultDimensionName(i));
            formats[i] = STRING_FORMAT;
        }
        int counter = 0;
        final int dimLength = dims.length;
        Index[] indices = new Index[tensors.size()];
        List<Tensor<?>> tensorList = new LinkedList<Tensor<?>>();
        JavaType lastType = null;
        boolean allSameTypes = true;
        for (String tensorName : tensors.keySet()) {
            Tensor<?> tensor = tensors.get(tensorName);
            header.add(tensorName);
            tensorList.add(tensor);
            JavaType type = tensor.getType();
            indices[counter] = tensor.getIndex();
            int offset = dimLength + counter++;
            types[offset] = type;
            formats[offset] = overrideFormats.containsKey(type) ? overrideFormats.get(type) : DEFAULT_FORMATS.get(type);
            if (allSameTypes) {
                if ((lastType != null && lastType != type) || type == JavaType.OBJECT)
                    allSameTypes = false;
                lastType = type;
            }
        }

        TextFormat[] hFormats = new TextFormat[header.size()];
        Arrays.fill(hFormats, STRING_FORMAT);
        writer.println(formatter.format(hFormats,(Object[]) header.toArray(new Object[header.size()])));

        if (allSameTypes) {
            writeTensors(tensorList,index,lastType,dims);
        } else {
            String format = formatter.getFormatString(formats);
            Object[] data = new Object[header.size()];

            for (int[] ind : IterableAbacus.getIterableAbacus(dims)) {
                int i = 0;
                for (;i < dimLength; i++)
                    data[i] = index.getIndexId(i,ind[i]);
                for (Tensor<?> tensor : tensorList)
                    data[i++] = tensor.getValue(ind);
                if (!condensedMode || !checkDefaultValues(data,dimLength,types))
                    writer.println(String.format(format,data));
            }
            writer.flush();
        }
    }
    
    //for writing tensors all of the same type, where type != OBJECT
    private void writeTensors(List<Tensor<?>> tensors, Index<I> index, JavaType type, int[] dims) {
        final int dimLength = dims.length;     
        Object[] dim = new Object[dimLength];
        TextFormat[] dimFormats = new TextFormat[dimLength];
        Arrays.fill(dimFormats,STRING_FORMAT);
        String format = formatter.getFormatString(dimFormats);
        switch(type) {
            case BYTE : {
                byte[] data = new byte[tensors.size()];
                @SuppressWarnings("unchecked") //cannot ensure List contents, but it is a user definition error if it doesn't match type, so suppressing
                List<ByteTensor> primitiveTensors = (List<ByteTensor>) (List) tensors;
                for (int[] ind : IterableAbacus.getIterableAbacus(dims)) {
                    for (int i = 0 ;i < dimLength; i++)
                        dim[i] = index.getIndexId(i,ind[i]);
                    int i = 0;
                    for (ByteTensor tensor : primitiveTensors)
                        data[i++] = tensor.getCell(ind);
                    if (!condensedMode || !checkDefaultValues(data))
                        writer.println(String.format(format,dim) + "," + formatter.format(data));
                }        
                break;
            }  
            case SHORT : {
                short[] data = new short[tensors.size()];
                @SuppressWarnings("unchecked") //cannot ensure List contents, but it is a user definition error if it doesn't match type, so suppressing
                List<ShortTensor> primitiveTensors = (List<ShortTensor>) (List) tensors;
                for (int[] ind : IterableAbacus.getIterableAbacus(dims)) {
                    for (int i = 0 ;i < dimLength; i++)
                        dim[i] = index.getIndexId(i,ind[i]);
                    int i = 0;
                    for (ShortTensor tensor : primitiveTensors)
                        data[i++] = tensor.getCell(ind);
                    if (!condensedMode || !checkDefaultValues(data))
                        writer.println(String.format(format,dim) + "," + formatter.format(data));
                }        
                break;
            }
            case INT : {
                int[] data = new int[tensors.size()];
                @SuppressWarnings("unchecked") //cannot ensure List contents, but it is a user definition error if it doesn't match type, so suppressing
                List<IntTensor> primitiveTensors = (List<IntTensor>) (List) tensors;
                for (int[] ind : IterableAbacus.getIterableAbacus(dims)) {
                    for (int i = 0 ;i < dimLength; i++)
                        dim[i] = index.getIndexId(i,ind[i]);
                    int i = 0;
                    for (IntTensor tensor : primitiveTensors)
                        data[i++] = tensor.getCell(ind);
                    if (!condensedMode || !checkDefaultValues(data))
                        writer.println(String.format(format,dim) + "," + formatter.format(data));
                }        
                break;
            }
            case LONG : {
                long[] data = new long[tensors.size()];
                @SuppressWarnings("unchecked") //cannot ensure List contents, but it is a user definition error if it doesn't match type, so suppressing
                List<LongTensor> primitiveTensors = (List<LongTensor>) (List) tensors;
                for (int[] ind : IterableAbacus.getIterableAbacus(dims)) {
                    for (int i = 0 ;i < dimLength; i++)
                        dim[i] = index.getIndexId(i,ind[i]);
                    int i = 0;
                    for (LongTensor tensor : primitiveTensors)
                        data[i++] = tensor.getCell(ind);
                    if (!condensedMode || !checkDefaultValues(data))
                        writer.println(String.format(format,dim) + "," + formatter.format(data));
                }        
                break;
            }
            case FLOAT : {
                float[] data = new float[tensors.size()];
                @SuppressWarnings("unchecked") //cannot ensure List contents, but it is a user definition error if it doesn't match type, so suppressing
                List<FloatTensor> primitiveTensors = (List<FloatTensor>) (List) tensors;
                for (int[] ind : IterableAbacus.getIterableAbacus(dims)) {
                    for (int i = 0 ;i < dimLength; i++)
                        dim[i] = index.getIndexId(i,ind[i]);
                    int i = 0;
                    for (FloatTensor tensor : primitiveTensors)
                        data[i++] = tensor.getCell(ind);
                    if (!condensedMode || !checkDefaultValues(data))
                        writer.println(String.format(format,dim) + "," + formatter.format(data));
                }        
                break;
            }
            case DOUBLE : {
                double[] data = new double[tensors.size()];
                @SuppressWarnings("unchecked") //cannot ensure List contents, but it is a user definition error if it doesn't match type, so suppressing
                List<DoubleTensor> primitiveTensors = (List<DoubleTensor>) (List) tensors;
                for (int[] ind : IterableAbacus.getIterableAbacus(dims)) {
                    for (int i = 0 ;i < dimLength; i++)
                        dim[i] = index.getIndexId(i,ind[i]);
                    int i = 0;
                    for (DoubleTensor tensor : primitiveTensors)
                        data[i++] = tensor.getCell(ind);
                    if (!condensedMode || !checkDefaultValues(data))
                        writer.println(String.format(format,dim) + "," + formatter.format(data));
                }        
                break;
            }
            case CHAR : {
                char[] data = new char[tensors.size()];
                @SuppressWarnings("unchecked") //cannot ensure List contents, but it is a user definition error if it doesn't match type, so suppressing
                List<CharTensor> primitiveTensors = (List<CharTensor>) (List) tensors;
                for (int[] ind : IterableAbacus.getIterableAbacus(dims)) {
                    for (int i = 0 ;i < dimLength; i++)
                        dim[i] = index.getIndexId(i,ind[i]);
                    int i = 0;
                    for (CharTensor tensor : primitiveTensors)
                        data[i++] = tensor.getCell(ind);
                    if (!condensedMode || !checkDefaultValues(data))
                        writer.println(String.format(format,dim) + "," + formatter.format(data));
                }        
                break;
            }
            case BOOLEAN : {
                boolean[] data = new boolean[tensors.size()];
                @SuppressWarnings("unchecked") //cannot ensure List contents, but it is a user definition error if it doesn't match type, so suppressing
                List<BooleanTensor> primitiveTensors = (List<BooleanTensor>) (List) tensors;
                for (int[] ind : IterableAbacus.getIterableAbacus(dims)) {
                    for (int i = 0 ;i < dimLength; i++)
                        dim[i] = index.getIndexId(i,ind[i]);
                    int i = 0;
                    for (BooleanTensor tensor : primitiveTensors)
                        data[i++] = tensor.getCell(ind);
                    if (!condensedMode || !checkDefaultValues(data))
                        writer.println(String.format(format,dim) + "," + formatter.format(data));
                }        
                break;
            }
        }
        writer.flush();
    }
    
    //will get assigned defaults automatically
    private byte defaultByte;      
    private short defaultShort;
    private int defaultInt;
    private long defaultLong;
    private float defaultFloat;
    private double defaultDouble;
    private char defaultChar;
    private boolean defaultBoolean;
    private Object defaultObject;

    /**
     * Set the default value for a given type. The value is used only when writing in condensed mode: if the tensor(s)
     * being written out all equal their default value, then that line will skipped. If the value passed in is {@code null},
     * then the standard default type value (as specified by the JLS ({@link com.pb.sawdust.util.JavaType#getDefaultObject()})
     * will be used.
     *
     * @param type
     *        The type to set the default for.
     *
     * @param value
     *        The value to use for the default, or {@code null} to reset to the standard default.
     *
     * @throws IllegalArgumentException if {@code value} is not of the correct type corresponding to {@code type}.
     */
    public void setDefaultValue(JavaType type, Object value) {
        try {
        switch (type) {
            case BYTE : defaultByte = (Byte) (value == null ? type.getDefaultObject() : value); break;
            case SHORT : defaultShort = (Short) (value == null ? type.getDefaultObject() : value); break;
            case INT : defaultInt = (Integer) (value == null ? type.getDefaultObject() : value); break;
            case LONG : defaultLong = (Long) (value == null ? type.getDefaultObject() : value); break;
            case FLOAT : defaultFloat = (Float) (value == null ? type.getDefaultObject() : value); break;
            case DOUBLE : defaultDouble = (Double) (value == null ? type.getDefaultObject() : value); break;
            case CHAR : defaultChar = (Character) (value == null ? type.getDefaultObject() : value); break;
            case BOOLEAN : defaultBoolean = (Boolean) (value == null ? type.getDefaultObject() : value); break;
            case OBJECT : defaultObject = value; break;
        }
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(String.format("Default value (%s) is of incorrect type (%s) for type %s.",value,value.getClass(),type));
        }
    }

    private boolean checkDefaultValues(Object[] data, int startPoint, JavaType[] types) {
        boolean defaults = true;
        while (defaults && startPoint < data.length) {
            defaults = checkDefaultValue(data[startPoint],types[startPoint]);
            startPoint++;
        }
        return defaults;
    }

    private boolean checkDefaultValue(Object o, JavaType type) {
        switch (type) {
            case BYTE : return ((Byte) defaultByte).equals(o);
            case SHORT : return ((Short) defaultShort).equals(o);
            case INT : return ((Integer) defaultInt).equals(o);
            case LONG : return ((Long) defaultLong).equals(o);
            case FLOAT : return ((Float) defaultFloat).equals(o);
            case DOUBLE : return ((Double) defaultDouble).equals(o);
            case CHAR : return ((Character) defaultChar).equals(o);
            case BOOLEAN : return ((Boolean) defaultBoolean).equals(o);
            default : return defaultObject == null ? o == null : defaultObject.equals(o);
        }
    }
    
    private boolean checkDefaultValues(byte[] data) {
        for (byte d : data)
            if (d != defaultByte)
                return false;
        return true;
    }  
    
    private boolean checkDefaultValues(short[] data) {
        for (short d : data)
            if (d != defaultShort)
                return false;
        return true;
    }
    
    private boolean checkDefaultValues(int[] data) {
        for (int d : data)
            if (d != defaultInt)
                return false;
        return true;
    }
    
    private boolean checkDefaultValues(long[] data) {
        for (long d : data)
            if (d != defaultLong)
                return false;
        return true;
    }
    
    private boolean checkDefaultValues(float[] data) {
        for (float d : data)
            if (d != defaultFloat)
                return false;
        return true;
    }
    
    private boolean checkDefaultValues(double[] data) {
        for (double d : data)
            if (d != defaultDouble)
                return false;
        return true;
    }  
    
    private boolean checkDefaultValues(char[] data) {
        for (char d : data)
            if (d != defaultChar)
                return false;
        return true;
    }
    
    private boolean checkDefaultValues(boolean[] data) {
        for (boolean d : data)
            if (d != defaultBoolean)
                return false;
        return true;
    }

    public void close() {
        writer.close();
    }
}
