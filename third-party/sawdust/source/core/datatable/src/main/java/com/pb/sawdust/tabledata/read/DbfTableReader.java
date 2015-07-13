package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.TableDataException;
import com.pb.sawdust.util.exceptions.RuntimeIOException;
import static com.pb.sawdust.util.Range.*;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The {@code DbfTableReader} class is a {@code TableReader} which reads DBF (dBase-format) files into data tables.
 *
 * @author crf <br/>
 *         Started: Nov 1, 2008 12:49:24 PM
 */
public class DbfTableReader extends FileTableReader {
    private DBFReader dbfReader = null;
    private DataType[] types = null;
    private String[] names = null;

    /**
     * Constructor specifying the file and table name for the table data.
     *
     * @param tableFilePath
     *        The path to the table data file.
     *
     * @param tableName
     *        The name to use for the table.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code tableFilePath} was not found.
     */
    public DbfTableReader(String tableFilePath, String tableName) {
        super(tableFilePath,tableName);

    }

    /**
     * Constructor specifying the file the table data. The file name (excluding directories) will be used as the
     * table name.
     *
     * @param tableFilePath
     *        The path to the table data file.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code tableFilePath} was not found.
     */
    public DbfTableReader(String tableFilePath) {
        super(tableFilePath);
    }

    protected String[] getAllColumnNames() {
        if (names == null) {
            loadDbfReader();
            try {
                names = new String[dbfReader.getFieldCount()];
                for (int i : range(names.length))
                    names[i] = dbfReader.getField(i).getName();
            } catch (DBFException e) {
                throw new RuntimeIOException(e);
            }
        }
        return names;
    }

    protected DataType[] getAllColumnTypes() {
        if (types == null) {
            loadDbfReader();
            try {
                types = new DataType[dbfReader.getFieldCount()];
                for (int i : range(types.length))
                    types[i] = getDataType(dbfReader.getField(i).getDataType());
            } catch (DBFException e) {
                throw new RuntimeIOException(e);
            }
        }
        return types;
    }

    private DataType getDataType(byte dbfType) {
        switch (dbfType) {
            case DBFField.FIELD_TYPE_C : return DataType.STRING;
            case DBFField.FIELD_TYPE_N : return DataType.INT;
            case DBFField.FIELD_TYPE_F : return DataType.DOUBLE;
            case DBFField.FIELD_TYPE_L : return DataType.BOOLEAN;
        }
        throw new TableDataException("Unsupported dbf data type: " + dbfType);
    }

    protected Iterator<Object[]> getDataIterator() {
        loadDbfReader();
        final DataType[] types = getAllColumnTypes();
        final DBFReader dbfReader = this.dbfReader;
        return new Iterator<Object[]>() {
            int rowCount = dbfReader.getRecordCount();
            int currentRow = 0;

            public boolean hasNext() {
                return currentRow < rowCount;
            }

            public Object[] next() {
                if (hasNext())
                    try {
                        return castData(dbfReader.nextRecord(),types);
                    } catch (DBFException e) {
                        throw new RuntimeIOException(e);
                    } finally {
                        currentRow++;
                    }
                else
                    throw new NoSuchElementException();
            }

            public void remove() {
                throw new IllegalArgumentException();
            }
        };
    }

    private Object[] castData(Object[] data, DataType[] types) {
        for (int i = 0; i < data.length; i++) {
            Object d = data[i];
            switch (types[i]) {
                case BOOLEAN :
                    if (d.getClass() == String.class)
                        data[i] = Boolean.parseBoolean((String) d);
                    break;
                case BYTE :
                    if (Number.class.isInstance(d))
                        data[i] = ((Number) d).byteValue();
                    if (d.getClass() == String.class)
                        data[i] = Byte.parseByte((String) d);
                    break;
                case DOUBLE :
                    if (Number.class.isInstance(d))
                        data[i] = ((Number) d).doubleValue();
                    if (d.getClass() == String.class)
                        data[i] = Double.parseDouble((String) d);
                    break;
                case FLOAT :
                    if (Number.class.isInstance(d))
                        data[i] = ((Number) d).floatValue();
                    if (d.getClass() == String.class)
                        data[i] = Float.parseFloat((String) d);
                    break;
                case INT :
                    if (Number.class.isInstance(d))
                        data[i] = ((Number) d).intValue();
                    if (d.getClass() == String.class)
                        data[i] = Integer.parseInt((String) d);
                    break;
                case LONG :
                    if (Number.class.isInstance(d))
                        data[i] = ((Number) d).longValue();
                    if (d.getClass() == String.class)
                        data[i] = Long.parseLong((String) d);
                    break;
                case SHORT :
                    if (Number.class.isInstance(d))
                        data[i] = ((Number) d).shortValue();
                    if (d.getClass() == String.class)
                        data[i] = Short.parseShort((String) d);
                    break;
                case STRING :
                    data[i] = d.toString();
            }
        }
        return data;
    }

    private void loadDbfReader() {
        if (dbfReader == null) {
            try {
                dbfReader = new DBFReader(new FileInputStream(tableFile));
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }
    }
}
