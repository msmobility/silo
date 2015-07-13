package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.tabledata.metadata.DataType;
import net.jcip.annotations.NotThreadSafe;

/**
 * The {@code AbstractTableReader} class is a skeletal implementation of {@code TableReader} easing the programming
 * burden on table reader implementations. It requires a table name to be specified at construction, and provides
 * setter methods for column names and types (the getter methods for these elements will probably need to be overridden
 * to deal with the possibility that the column names/types may not have been set). To fulfill the interface contract,
 * only the {@code getData()} method need been implemented.
 * <p>
 * Even though the possiblity is unlikely, it is noted that this class (and its descendants) is not safe for access by
 * multiple threads. 
 *
 * @author crf <br/>
 *         Started: Jul 2, 2008 7:30:25 AM
 */
@NotThreadSafe
public abstract class AbstractTableReader implements TableReader {
    private final String tableName;
    private String[] columnNames = null;
    private DataType[] columnTypes = null;

    /**
     * Constructor specifying the table name.
     *
     * @param tableName
     *        The name to use for the table.
     */
    public AbstractTableReader(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Set the column names for this table.
     *
     * @param columnNames
     *        The names to use for the columns, in the order they appear in the table.
     *
     * @throws NullPointerException if {@code columnNames} is {@code null}.
     */
    public void setColumnNames(String ... columnNames) {
        if (columnNames == null)
            throw new NullPointerException("columnNames cannot be null.");
        this.columnNames = columnNames;
    }

    /**
     * Set the column data types for this table.
     *
     * @param columnTypes
     *        The data types to use for the columns, in the order they appear in the table.
     *
     * @throws NullPointerException if {@code columnTypes} is {@code null}.
     */
    public void setColumnTypes(DataType ... columnTypes) {
        if (columnTypes == null)
            throw new NullPointerException("columnTypes cannot be null.");
        this.columnTypes = columnTypes;
    }

    public String getTableName() {
        return tableName;
    }


    /**
     * {@inheritDoc}
     *
     * Will return {@code null} if the column names have not been set yet; in such a situation an extending class
     * should either determine the column names or throw an exception of some sort ({@code null} should never be 
     * returned to and end user from this method).
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * {@inheritDoc}
     *
     * Will return {@code null} if the column types have not been set yet; in such a situation an extending class
     * should either determine the column types or throw an exception of some sort ({@code null} should never be
     * returned to and end user from this method).
     */
    public DataType[] getColumnTypes() {
        return columnTypes;
    }

}
