package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.tabledata.TableDataException;

/**
 * The {@code TableReaderFactory} class provides static convenience methods for creating {@code TableReader}s.
 * 
 * @author crf <br/>
 *         Started: Jun 19, 2008 3:54:04 PM
 */
public class TableReaderFactory {

    //class should not be instantiated
    private TableReaderFactory() {}

    /**
     * Get a table reader of the specified type corresponding to the given file and table name.
     *
     * @param filePath
     *        The path to the data file.
     *
     * @param tableName
     *        The name to use for the table.
     *
     * @param readerType
     *        The type of reader to construct.
     *
     * @return a reader corresponding to the given arguments.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code fileName} was not found.
     */
    public static TableReader getTableReader(String filePath, String tableName, ReaderType readerType) {
        switch (readerType) {
            case CSV : return new CsvTableReader(filePath,tableName);
            case DBF : return new DbfTableReader(filePath,tableName);
            case WHITESPACE_DELIMITED : return new WhitespaceDelimitedTextTableReader(filePath,tableName);
            case FIXED_WIDTH : return new FixedWidthTextTableReader(filePath,tableName);
            default : throw new UnsupportedOperationException("Table reader not abailable for this type: " + readerType);
        }
    }

    /**
     * Get a table reader of the specified type corresponding to the given file. The file name (excluding directories)
     * will be used as the table name.
     *
     * @param filePath
     *        The path to the data file.
     *
     * @param readerType
     *        The type of reader to construct.
     *
     * @return a reader corresponding to the given arguments.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code fileName} was not found.
     */
    public static TableReader getTableReader(String filePath, ReaderType readerType) {
        switch (readerType) {
            case CSV : return new CsvTableReader(filePath);
            case DBF : return new DbfTableReader(filePath);
            case WHITESPACE_DELIMITED : return new WhitespaceDelimitedTextTableReader(filePath);
            case FIXED_WIDTH : return new FixedWidthTextTableReader(filePath);
            default : throw new UnsupportedOperationException("Table reader not abailable for this type: " + readerType);
        }
    }

    /**
     * Get a table reader for the given file and table name. The reader type will be inferred from the file extension.
     *
     * @param filePath
     *        The path to the data file.
     *
     * @param tableName
     *        The name to use for the table.
     *
     * @return a reader corresponding to the given arguments.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code fileName} was not found.
     * @throws IllegalArgumentException if {@code filePath} contains no extension to determine the reader.
     * @throws TableDataException if the reader type could not be deteremined from {@code filePath}.
     *
     * @see ReaderType#getReaderType(String)
     */
    public static TableReader getTableReader(String filePath, String tableName) {
        return getTableReader(filePath,tableName,ReaderType.getReaderType(filePath));
    }

    /**
     * Get a table reader for the given file. The file name (excluding directories) will be used as the table name. The
     * reader type will be inferred from the file extension.
     *
     * @param filePath
     *        The path to the delimited data file.
     *
     * @return a reader corresponding to the given arguments.
     *
     * @throws com.pb.sawdust.util.exceptions.RuntimeIOException if {@code fileName} was not found.
     * @throws TableDataException if the reader type could not be deteremined from {@code filePath}.
     *
     * @see ReaderType#getReaderType(String)
     */
    public static TableReader getTableReader(String filePath) {
        return getTableReader(filePath,ReaderType.getReaderType(filePath));
    }

}
