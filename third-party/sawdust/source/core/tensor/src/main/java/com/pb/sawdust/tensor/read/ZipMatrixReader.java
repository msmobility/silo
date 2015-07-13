package com.pb.sawdust.tensor.read;

import com.pb.sawdust.util.exceptions.RuntimeIOException;
import com.pb.sawdust.io.ZipFile;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.alias.matrix.primitive.*;
import com.pb.sawdust.tensor.factory.LiterateTensorFactory;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.util.JavaType;
import com.pb.sawdust.util.Range;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * The {@code ZipMatrixWriter} class provides the functionality to read tensors from the zip matrix format. Because the
 * zipMatrix format only holds matrices, only two-dimensional tensors can be read with this class.  A zip matrix may
 * be read into any two-dimension tensor holding numeric data ({@code byte}, {@code short}, <i>etc.</i>), depending on
 * which factory method is used to get a {@code ZipMatrixReader} instance.
 * <p>
 * The name and description held in the zip matrix file will be stored in returned tensor's metadata using the
 * {@link #MATRIX_NAME_METADATA_KEY} and {@link #MATRIX_DESCRIPTION_METADATA_KEY} keys, respectively.
 *
 * @param <T>
 *        The type held by the tensor returned by this reader. 
 *
 * @author crf <br/>
 *         Started: Dec 12, 2009 11:06:12 PM
 */
public class ZipMatrixReader<T> implements TensorReader<T,Integer> {
    /**
     * The metadata key used for the matrix name.
     */
    public static final String MATRIX_NAME_METADATA_KEY = "name";

    /**
     * The metadata key used for the matrix description.
     */
    public static final String MATRIX_DESCRIPTION_METADATA_KEY = "description";

    /**
     * Get a reader instance for creating {@code ByteMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code ByteMatrix}.
     */
    public static ZipMatrixReader<Byte> getByteZipMatrixReader(File zmxFile) {
        return new ZipMatrixReader<Byte>(JavaType.BYTE,zmxFile);
    }    

    /**
     * Get a reader instance for creating {@code ByteMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code ByteMatrix}.
     */
    public static ZipMatrixReader<Byte> getByteZipMatrixReader(String zmxFile) {
        return new ZipMatrixReader<Byte>(JavaType.BYTE,zmxFile);
    }

    /**
     * Get a reader instance for creating {@code ShortMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code ShortMatrix}.
     */
    public static ZipMatrixReader<Short> getShortZipMatrixReader(File zmxFile) {
        return new ZipMatrixReader<Short>(JavaType.SHORT,zmxFile);
    }

    /**
     * Get a reader instance for creating {@code ShortMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code ShortMatrix}.
     */
    public static ZipMatrixReader<Short> getShortZipMatrixReader(String zmxFile) {
        return new ZipMatrixReader<Short>(JavaType.SHORT,zmxFile);
    }

    /**
     * Get a reader instance for creating {@code IntMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code IntMatrix}.
     */
    public static ZipMatrixReader<Integer> getIntZipMatrixReader(File zmxFile) {
        return new ZipMatrixReader<Integer>(JavaType.INT,zmxFile);
    }

    /**
     * Get a reader instance for creating {@code IntMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code IntMatrix}.
     */
    public static ZipMatrixReader<Integer> getIntZipMatrixReader(String zmxFile) {
        return new ZipMatrixReader<Integer>(JavaType.INT,zmxFile);
    }

    /**
     * Get a reader instance for creating {@code LongMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code LongMatrix}.
     */
    public static ZipMatrixReader<Long> getLongZipMatrixReader(File zmxFile) {
        return new ZipMatrixReader<Long>(JavaType.LONG,zmxFile);
    }

    /**
     * Get a reader instance for creating {@code LongMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code LongMatrix}.
     */
    public static ZipMatrixReader<Long> getLongZipMatrixReader(String zmxFile) {
        return new ZipMatrixReader<Long>(JavaType.LONG,zmxFile);
    }

    /**
     * Get a reader instance for creating {@code FloatMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code FloatMatrix}.
     */
    public static ZipMatrixReader<Float> getFloatZipMatrixReader(File zmxFile) {
        return new ZipMatrixReader<Float>(JavaType.FLOAT,zmxFile);
    }

    /**
     * Get a reader instance for creating {@code FloatMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code FloatMatrix}.
     */
    public static ZipMatrixReader<Float> getFloatZipMatrixReader(String zmxFile) {
        return new ZipMatrixReader<Float>(JavaType.FLOAT,zmxFile);
    }

    /**
     * Get a reader instance for creating {@code DoubleMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code DoubleMatrix}.
     */
    public static ZipMatrixReader<Double> getDoubleZipMatrixReader(File zmxFile) {
        return new ZipMatrixReader<Double>(JavaType.DOUBLE,zmxFile);
    }

    /**
     * Get a reader instance for creating {@code DoubleMatrix} tensors.
     *
     * @param zmxFile
     *        The zip matrix file.
     *
     * @return a reader for reading into a {@code DoubleMatrix}.
     */
    public static ZipMatrixReader<Double> getDoubleZipMatrixReader(String zmxFile) {
        return new ZipMatrixReader<Double>(JavaType.DOUBLE,zmxFile);
    } 
    
    private final JavaType type;
    private final File zmxFile;
    private int[] dimensions = null;
    private String name;
    private String description;
    private List<List<Integer>> ids;

    private ZipMatrixReader(JavaType type, File zmxFile) {
        this.type = type;
        this.zmxFile = zmxFile;
    }

    private ZipMatrixReader(JavaType type, String zmxFile) {
        this(type,new File(zmxFile));
    }

    @Override
    public JavaType getType() {
        return type;
    }

    @Override
    public int[] getDimensions() {
        if (dimensions == null)
            loadHeaderData();
        return dimensions;
    }

    @Override
    public Map<String,Object> getTensorMetadata() {
        Map<String,Object> metadata = new HashMap<String,Object>();
        metadata.put(MATRIX_NAME_METADATA_KEY,name);
        metadata.put(MATRIX_DESCRIPTION_METADATA_KEY,description);
        return metadata;
    }

    @Override
    public List<List<Integer>> getIds() {
        if (ids == null)
            loadHeaderData();
        return ids;
    }

    private final Charset charset = Charset.forName("US-ASCII");
    private String readStringData(ZipFile zipFile, String entry) {
        return zipFile.extractString(entry,charset);
    }

    private void loadHeaderData() {
        ZipFile zipFile = new ZipFile(zmxFile);
        int version = Integer.parseInt(readStringData(zipFile,"_version"));
        int rowCount = Integer.parseInt(readStringData(zipFile,"_rows"));
        int colCount = Integer.parseInt(readStringData(zipFile,"_columns"));
        dimensions = new int[] {rowCount,colCount};
        description = readStringData(zipFile,"_description");
        name = readStringData(zipFile,"_name");
        List<Integer> externalRows = stringToIntList(readStringData(zipFile,version == 2 ? "_external row numbers" : "_external numbers"));
        List<Integer> externalCols = version == 2 ? stringToIntList(readStringData(zipFile,"_external column numbers")) : externalRows;
        if (isDefaultExternals(externalRows,rowCount) && isDefaultExternals(externalCols,colCount)) {
            ids = null;
        } else {
            ids = new LinkedList<List<Integer>>();
            ids.add(externalRows);
            ids.add(externalCols);
        }
    }

    private List<Integer> stringToIntList(String s) {
        List<Integer> list = new LinkedList<Integer>();
        for (String si : s.split(","))
            list.add(Integer.parseInt(si));
        return list;
    }

    private boolean isDefaultExternals(List<Integer> externals, int dimSize) {
        Iterator<Integer> it = externals.iterator();
        for (int i : new Range(1,dimSize+1))
            if (it.next() != i)
                return false;
        return true;
    }

    //annoying to have so much copying, but can't abstract out with out doing a lot of repetitive case statements or something equivalent
    public Tensor<T> fillTensor(Tensor<T> tensor) {
        Range colRange = new Range(tensor.size(1));
        ZipFile zipFile = new ZipFile(zmxFile);
        switch (type) {
            case BYTE : {
                ByteMatrix t = (ByteMatrix) tensor;
                for (int row : Range.range(tensor.size(0))) {
                    DataInputStream din = null;
                    try {
                        din = new DataInputStream(zipFile.getInputStream("row_" + row));
                        for (int col : colRange)
                            t.setCell((byte) Math.round(din.readFloat()),row,col);
                    } catch (IOException e) {
                        throw new RuntimeIOException(e);
                    } finally {
                        if (din != null) {
                            try {
                                din.close();
                            } catch (IOException e) {
                                //swallow
                            }
                        }
                    }
                }
                break;
            }
            case SHORT : {       
                ShortMatrix t = (ShortMatrix) tensor;
                for (int row : Range.range(tensor.size(0))) {
                    DataInputStream din = null;
                    try {
                        din = new DataInputStream(zipFile.getInputStream("row_" + row));
                        for (int col : colRange)
                            t.setCell((short) Math.round(din.readFloat()),row,col);
                    } catch (IOException e) {
                        throw new RuntimeIOException(e);
                    } finally {
                        if (din != null) {
                            try {
                                din.close();
                            } catch (IOException e) {
                                //swallow
                            }
                        }
                    }
                }
                break;
            }
            case INT : {
                IntMatrix t = (IntMatrix) tensor;
                for (int row : Range.range(tensor.size(0))) {
                    DataInputStream din = null;
                    try {
                        din = new DataInputStream(zipFile.getInputStream("row_" + row));
                        for (int col : colRange)
                            t.setCell(Math.round(din.readFloat()),row,col);
                    } catch (IOException e) {
                        throw new RuntimeIOException(e);
                    } finally {
                        if (din != null) {
                            try {
                                din.close();
                            } catch (IOException e) {
                                //swallow
                            }
                        }
                    }
                }
                break;
            }
            case LONG : {
                LongMatrix t = (LongMatrix) tensor;
                for (int row : Range.range(tensor.size(0))) {
                    DataInputStream din = null;
                    try {
                        din = new DataInputStream(zipFile.getInputStream("row_" + row));
                        for (int col : colRange)
                            t.setCell(Math.round(din.readFloat()),row,col);
                    } catch (IOException e) {
                        throw new RuntimeIOException(e);
                    } finally {
                        if (din != null) {
                            try {
                                din.close();
                            } catch (IOException e) {
                                //swallow
                            }
                        }
                    }
                }
                break;
            }
            case FLOAT : {
                FloatMatrix t = (FloatMatrix) tensor;
                for (int row : Range.range(tensor.size(0))) {
                    DataInputStream din = null;
                    try {
                        din = new DataInputStream(zipFile.getInputStream("row_" + row));
                        for (int col : colRange)
                            t.setCell(din.readFloat(),row,col);
                    } catch (IOException e) {
                        throw new RuntimeIOException(e);
                    } finally {
                        if (din != null) {
                            try {
                                din.close();
                            } catch (IOException e) {
                                //swallow
                            }
                        }
                    }
                }
                break;
            }
            case DOUBLE : {
                DoubleMatrix t = (DoubleMatrix) tensor;
                for (int row : Range.range(tensor.size(0))) {
                    DataInputStream din = null;
                    try {
                        din = new DataInputStream(zipFile.getInputStream("row_" + row));
                        for (int col : colRange)
                            t.setCell(din.readFloat(),row,col);
                    } catch (IOException e) {
                        throw new RuntimeIOException(e);
                    } finally {
                        if (din != null) {
                            try {
                                din.close();
                            } catch (IOException e) {
                                //swallow
                            }
                        }
                    }
                }
                break;
            }
            default : throw new IllegalStateException("Cannot read zip matrix into " + type + " matrix");
        }
        return tensor;
    }

    public static void main(String ... args) {
        ZipMatrixReader<Integer> zmr = ZipMatrixReader.getIntZipMatrixReader("C:\\transfers\\betaopautotoll.zmx");
        IntMatrix mat = (IntMatrix) ((LiterateTensorFactory)ArrayTensor.getFactory()).tensor(zmr);
        System.out.println(TensorUtil.toString(mat));
    }
}

