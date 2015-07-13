package com.pb.sawdust.tensor.write;

import com.pb.sawdust.io.ZipFile;
import com.pb.sawdust.tensor.alias.matrix.primitive.*;
import com.pb.sawdust.tensor.factory.LiterateTensorFactory;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.tensor.Tensor;
import com.pb.sawdust.tensor.TensorUtil;
import com.pb.sawdust.tensor.index.Index;
import com.pb.sawdust.tensor.index.StandardIndex;
import com.pb.sawdust.tensor.read.ZipMatrixReader;

import static com.pb.sawdust.util.Range.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

/**
 * The {@code ZipMatrixWriter} class provides the functionality to write tensors to the zip matrix format. Because the
 * zip matrix format only write matrices, only two-dimensional tensors can be written with this class.  Also, if the tensor
 * ids (if they exist) are not integer and increasing, they will not be written (a standard 0-based index will be used
 * instead).
 * <p>
 * This class is provided for legacy support, and generally should be discarded in favor of the {@link ZipTensorWriter}.
 *
 * @author crf <br/>
 *         Started: Dec 13, 2009 6:00:45 PM
 */
public class ZipMatrixWriter implements TensorWriter<Number> {
    /**
     * The standard zip matrix extension.
     */
    public static final String ZIP_MATRIX_EXTENSION = "zmx";

    private final File file;

    /**
     * Constructor specifying the zip matrix file.  If the file already exists, it will be overwritten.
     *
     * @param file
     *        The zip matrix file.
     */
    public ZipMatrixWriter(File file) {
        this.file = file;
    }

    /**
     * Constructor specifying the zip matrix file.  If the file already exists, it will be overwritten.
     *
     * @param file
     *        The zip matrix file.
     */
    public ZipMatrixWriter(String file) {
        this(new File(file));
    }

    /**
     * {@inheritDoc}
     *
     * If the tensor has metadata with the key {@link com.pb.sawdust.tensor.read.ZipMatrixReader#MATRIX_NAME_METADATA_KEY}
     * and/or {@link com.pb.sawdust.tensor.read.ZipMatrixReader#MATRIX_DESCRIPTION_METADATA_KEY} then those metadata
     * values will be used for the name and description, respectively, in the zip matrix.
     */
    public void writeTensor(Tensor<? extends Number> tensor) {
        int[] dims = tensor.getDimensions();
        if (dims.length != 2)
            throw new IllegalArgumentException("ZipMatrixWriter can only write tensors with 2 dimensions (" + dims.length + " found).");
        switch (tensor.getType()) {
            case BOOLEAN :
            case CHAR :
            case OBJECT : throw new IllegalArgumentException("ZipMatrixWriter can only write primitive numeric matrices.");
        }
        ZipFile zipFile = new ZipFile(file,true);
        writeHeader(tensor,zipFile);
        writeData(tensor,zipFile);
        zipFile.write();
    }

    private final Charset charset = Charset.forName("US-ASCII");
    private void addStringEntry(ZipFile zipFile, String entry, String data) {
        zipFile.addEntry(entry,data,charset);
    }

    private void writeHeader(Tensor<?> tensor, ZipFile zipFile) {
        addStringEntry(zipFile,"_version","2");
        int[] dims = tensor.getDimensions();
        addStringEntry(zipFile,"_rows",Integer.toString(dims[0]));
        addStringEntry(zipFile,"_columns",Integer.toString(dims[1]));
        addStringEntry(zipFile,"_name",tensor.containsMetadataKey(ZipMatrixReader.MATRIX_NAME_METADATA_KEY) ?
                                 (String) tensor.getMetadataValue(ZipMatrixReader.MATRIX_NAME_METADATA_KEY) : ""); //might throw an exception here if name metadata isn't string
        addStringEntry(zipFile,"_description",tensor.containsMetadataKey(ZipMatrixReader.MATRIX_DESCRIPTION_METADATA_KEY) ?
                                        (String) tensor.getMetadataValue(ZipMatrixReader.MATRIX_DESCRIPTION_METADATA_KEY) : "");
        String[] sids = formIdStrings(tensor.getIndex());
        addStringEntry(zipFile,"_external row numbers",sids[0]);
        addStringEntry(zipFile,"_external column numbers",sids[1]);
    }

    private String[] formIdStrings(Index<?> index) {
        List<? extends List<?>> ids = index.getIndexIds();
        String[] sids = new String[2];
        int counter = 0;
        int last = 0;
        for (List<?> list : ids) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Object i : list) {
                if (!(i instanceof Integer) || (!first && last > (Integer) i))
                    //todo: set up some sort of warning that ids aren't being used
                    // all i's must be integers, and i must be increasing
                    return formIdStrings(new StandardIndex(index.getDimensions()));
                if (first)
                    first = false;
                else
                    sb.append(",");
                sb.append(i);
            }
            sids[counter++] = sb.toString();
        }
        return sids;
    }

    private void writeData(Tensor<? extends Number> tensor, ZipFile zipFile) {
        for (int row : range(tensor.size(0)))
            writeData(tensor,row,zipFile);
    }

    private void writeData(final Tensor<? extends Number> tensor, final int row, ZipFile zipFile) {
        zipFile.addEntry("row_" + (row+1),new ZipFile.ZipEntrySource() {
            public void writeData(OutputStream os) throws IOException {
                final int s = tensor.size(1);
                DataOutputStream dos = new DataOutputStream(os);
                switch (tensor.getType()) {
                    case BYTE : {
                        ByteMatrix m = (ByteMatrix) tensor;
                        for (int i : range(s))
                            dos.writeFloat(m.getCell(row, i));
                        break;
                    }
                    case SHORT : {
                        ShortMatrix m = (ShortMatrix) tensor;
                        for (int i : range(s))
                            dos.writeFloat(m.getCell(row,i));
                        break;
                    }
                    case INT : {
                        IntMatrix m = (IntMatrix) tensor;
                        for (int i : range(s))
                            dos.writeFloat(m.getCell(row,i));
                        break;
                    }
                    case LONG : {
                        LongMatrix m = (LongMatrix) tensor;
                        for (int i : range(s))
                            dos.writeFloat(m.getCell(row,i));
                        break;
                    }
                    case FLOAT : {
                        FloatMatrix m = (FloatMatrix) tensor;
                        for (int i = 0; i < s; i++)
                            dos.writeFloat(m.getCell(row,i));
                        break;
                    }
                    case DOUBLE : {
                        DoubleMatrix m = (DoubleMatrix) tensor;
                        for (int i : range(s))
                            dos.writeFloat((float) m.getCell(row,i));
                        break;
                    }
                    case OBJECT : {
                        for (int i : range(s))
                            dos.writeFloat(tensor.getValue(row,i).floatValue());
                        break;
                    }
                    default : throw new IllegalStateException("Invalid matrix type for zip matrix: " + tensor.getType());
                }
            }
            public void close() {}
        });
    }

    public static void main(String ... args) {     
        ZipMatrixReader<Integer> zmr = ZipMatrixReader.getIntZipMatrixReader("d:\\transfers\\betaopautotoll.zmx");
        IntMatrix mat = (IntMatrix) ((LiterateTensorFactory) ArrayTensor.getFactory()).tensor(zmr);
        System.out.println(TensorUtil.toString(mat));

        ZipMatrixWriter zmw = new ZipMatrixWriter("D:\\transfers\\betaopautotoll_copy.zmx");
        zmw.writeTensor(mat);

        ZipMatrixReader<Integer> zmr2 = ZipMatrixReader.getIntZipMatrixReader("D:\\transfers\\betaopautotoll_copy.zmx");
        IntMatrix mat2 = (IntMatrix) ((LiterateTensorFactory) ArrayTensor.getFactory()).tensor(zmr2);
        System.out.println(TensorUtil.toString(mat2));
    }
}

