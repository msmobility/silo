package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.DataType;


/**
 * <p>
 * A {@code TableKey} object is a specialized {@code TableIndex} which maps a single column's values uniquely to a
 * data table's rows. Specifically, for every row in the data table, there will be only one index column value, which
 * itself maps uniquely to the data row. Thus, the number of unique key values in a table key will always be equal to
 * the number of rows in the data table.
 * <p>
 * A table key is generified by a key type paramter {@code K}. This key type, if declared, must correspond to the
 * object correspondence of the data type of the key column. That is, if {@code key} is a {@code TableKey<K>} instance,
 * {@code K} must be the class reference corresponding to {@code getKeyColumnType().getObjectClass()}.
 * <p>
 * Extending the {@code TableIndex} interface leverages that interface's specification as well as its use in data tables,
 * however, it is emphasized that this interface describes a <i>more restrictive</i> version of a table index, and as
 * such should be more efficient. To help fully realize efficiency gains, though, the methods described in this
 * interface have been set up to replace methods in {@code TableIndex}, and should be used in place of them whenever
 * possible.
 *
 * @param <K>
 *        The type of the table key.
 *
 * @author crf <br/>
 *         Started: May 21, 2008 8:07:06 AM
 */
public interface TableKey<K> extends TableIndex<K> {

    /**
     * Get the label of the column which this key refers to. This method should replace calls to {@link #getIndexColumnLabels()}.
     *
     * @return this key's column label.
     */
    String getKeyColumnLabel();

    /**
     * Get the data type of the column which this key refers to.
     *
     * @return the data type of this key's column.
     */
    DataType getKeyColumnType();

    /**
     * Get the row number corresponding to a given key value.  If no row corresponds to the key value, an exception
     * is thrown. This method should replace calls to {@link #getRowNumbers(Object[])}.
     *
     * @param keyValue
     *        The index column value.
     *
     * @return the row number whose key column contains {@code indexValue}.
     *
     * @throws TableDataException if {@code keyValue} is of the wrong type, or no row has a key column value matching
                                  {@code keyValue}.
     */
    int getRowNumber(K keyValue);

    /**
     * Get the key corresponding to a given row number.
     *
     * @param rowNumber
     *        The row number.
     *
     * @return the key value corresponding to {@code rowNumber}.
     *
     * @throws TableDataException if {@code rowNumber} is less than zero or greater than or equal to the number of rows
     *                            in its data table.
     */
    K getKey(int rowNumber);

    /**
     * Get the unique index column values for this key. This method should replace calls to {@link #getUniqueValues()}.
     *
     * @return an array of this key's unique key column values.
     */
    K[] getUniqueKeys();

    /**
     * Build the key (index) as described in {@link TableIndex#buildIndex()}. If the building the key results in more
     * than one row being mapped to a single key column value, then an exception will be thrown.
     *
     * @throws TableDataException if more than one row number maps to a single key column value.
     */
    void buildIndex();
}
