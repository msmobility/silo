package com.pb.sawdust.model.integration.transcad;

import com.pb.sawdust.io.ByteOrderDataInputStream;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.ColumnDataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.read.FileTableReader;

import com.pb.sawdust.tabledata.util.TableDataUtil;
import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The {@code TranscadBinaryTableReader} class is a data table reader for Transcad fixed-format binary table files.
 *
 * @author crf <br/>
 *         Started Oct 30, 2010 8:41:46 AM
 */
public class TranscadBinaryTableReader extends FileTableReader {
    /**
     * The TransCad fixed-format binary file extension.
     */
    public static final String TRANSCAD_MATRIX_BINARY_FILE_EXTENSION = "bin";

    private final File dictFile;
    private final TranscadBinaryDataConverter converter;
    private TranscadTableDictionary tableDictionary = null;

    /**
     * Get the file base from a full filename. If the file path has no extension, or its extension is not equal to {@link #TRANSCAD_MATRIX_BINARY_FILE_EXTENSION}
     * or {@code TranscadTableDictionary.TableType.FFB.getDefaultExtension()} then the file path will be returned unaltered;
     * otherwise, the filepath minus the extension is returned.
     *
     * @param fullFilePath
     *        The full file path.
     *
     * @return the (Transcad table) base filename of {@code fullFilePath}.
     */
    public static String formFileBase(String fullFilePath) {
        int extensionIndex = fullFilePath.lastIndexOf(".");
        if (extensionIndex >= 0) {
            String extension = fullFilePath.substring(extensionIndex+1);
            if (extension.equalsIgnoreCase(TRANSCAD_MATRIX_BINARY_FILE_EXTENSION) || extension.equalsIgnoreCase(TranscadTableDictionary.TableType.FFB.getDefaultExtension()))
                return fullFilePath.substring(0,extensionIndex);
        }
        return fullFilePath;
    }

    /**
     * Constructor specifying the file base and table name.  The file base is the path to the table file, minus the
     * file extension, and is used to build the binary and dictionary file paths.
     *
     * @param tableFileBase
     *        The base file path for the input file.
     *
     * @param tableName
     *        The label to use for the table name.
     */
    public TranscadBinaryTableReader(String tableFileBase, String tableName) {
        super(tableFileBase + "." + TRANSCAD_MATRIX_BINARY_FILE_EXTENSION,tableName);
        dictFile = new File(tableFileBase + "." + TranscadTableDictionary.TableType.FFB.getDefaultExtension());
        converter = new TranscadBinaryDataConverter();
    }

    /**
     * Constructor specifying the file base and table name.  The file base is the path to the table file, minus the
     * file extension, and is used to build the binary and dictionary file paths. The file name (including the binary
     * file extension but excluding directories) will be used as the table name.
     *
     * @param tableFileBase
     *        The base file path for the input file.
     */
    public TranscadBinaryTableReader(String tableFileBase) {
        super(tableFileBase + "." + TRANSCAD_MATRIX_BINARY_FILE_EXTENSION);
        dictFile = new File(tableFileBase + "." + TranscadTableDictionary.TableType.FFB.getDefaultExtension());
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

    private TranscadTableDictionary getTableDictionary() {
        if (tableDictionary == null)
            tableDictionary = new TranscadTableDictionary(dictFile);
        return tableDictionary;
    }

    @Override
    protected String[] getAllColumnNames() {
        return getTableDictionary().getSchema().getColumnLabels();
    }

    @Override
    protected DataType[] getAllColumnTypes() {
        return getTableDictionary().getSchema().getColumnTypes();
    }

    @Override
    protected Iterator<Object[]> getDataIterator() {

        final ByteOrderDataInputStream reader = converter.getBinaryReader(tableFile);
        final int[] lengths = new int[getTableDictionary().getSchema().getColumnLabels().length];
        final DataType[] types = new DataType[lengths.length];
        int counter = 0;
        for (TranscadTableDictionary.ColumnExtraInformation ei : getTableDictionary().getColumnExtraInformation()) {
            lengths[counter] = ei.getWidth();
            types[counter++] = ei.getBaseColumnType();
        }

        return new Iterator<Object[]>() {
            Object[] next = new Object[0]; //to prevent initial increment failure
            boolean incremented = false;

            private boolean increment() {
                if (incremented)
                    return true;
                else if (next == null)
                    return false;
                next = new Object[types.length];
                int i;
                boolean close = false;
                for (i = 0; i < types.length; i++) {
                    try {
                        next[i] = converter.readValue(types[i],reader,lengths[i]);
                    } catch (EOFException e) {
                        close = true;
                        if (i > 0)
                            throw new RuntimeIOException(e);
                        //assume at end of data - no way to (easily) tell if some bytes were read in
                        next = null;
                        break;
                    } catch (IOException e) {
                        close = true;
                        throw new RuntimeIOException(e);
                    } finally {
                        if (close) {
                            try {
                                reader.close();
                            } catch (RuntimeIOException e) {
                                //swallow
                            }
                        }
                    }
                }
                return (incremented = next != null);
            }
            
            @Override
            public boolean hasNext() {
                return increment();
            }

            @Override
            public Object[] next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                incremented = false;
                return next;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static void main(String ... args) {
        String testFileBase = "d:/transfers/test_table_string";
        TranscadBinaryTableReader reader = new TranscadBinaryTableReader(testFileBase,"test");
        TranscadBinaryDataConverter converter = reader.getConverter();
        converter.setNullShortConversionValue(TranscadBinaryDataConverter.TRANSCAD_NULL_SHORT);
        converter.setNullIntConversionValue(TranscadBinaryDataConverter.TRANSCAD_NULL_INT);
        converter.setNullFloatConversionValue(TranscadBinaryDataConverter.TRANSCAD_NULL_FLOAT);
        converter.setNullDoubleConversionValue(TranscadBinaryDataConverter.TRANSCAD_NULL_DOUBLE);
        DataTable dt = new ColumnDataTable(reader);
        System.out.println(TableDataUtil.toString(dt));

        String testFileBase2 = testFileBase + "_tester";
        
        new TranscadBinaryTableWriter(testFileBase2).writeTable(dt);

        DataTable dt2 = new ColumnDataTable(new TranscadBinaryTableReader(testFileBase2,"test"));
        System.out.println(TableDataUtil.toString(dt2));


    }
}
