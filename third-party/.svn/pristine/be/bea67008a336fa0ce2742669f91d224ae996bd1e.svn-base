package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.AbstractDataColumn;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.TableKey;
import com.pb.sawdust.tabledata.FrozenTableKey;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.Iterator;

/**
 * The {@code BasicDataColumn} class provides a simple implementation of the {@code DataColumn} interface. The primary
 * key of the underlying table is frozen upon instantiation; that is, updates to the underlying table's primary key
 * will not be reflected in the {@code  BasicDataColumn} instance.
 *
 * @param <T>
 *        The type of data held by this data column.
 * 
 * @author crf <br/>
 *         Started: May 21, 2008 4:49:00 PM
 */
public class BasicDataColumn<T> extends AbstractDataColumn<T> {

    private String columnLabel;
    private T[] columnValues;
    private DataType type;
    private TableKey<?> primaryKey;

    /**
     * Constructor specifying the column label, values, type, and primary key.
     *
     * @param columnLabel
     *        The column's label.
     *
     * @param columnValues
     *        The data values for the column.
     *
     * @param type
     *        The data type for the column. This should appropriately match the component type of the {@code columnValues}
     *        array.
     *
     * @param primaryKey
     *        The primary key to use for this column.
     */
    public <K> BasicDataColumn(String columnLabel, T[] columnValues, DataType type, TableKey<K> primaryKey) {
        this.columnLabel = columnLabel;
        this.columnValues = columnValues;
        this.type = type;
        this.primaryKey = new FrozenTableKey<K>(primaryKey);
    }

    public String getLabel() {
        return columnLabel;
    }

    public DataType getType() {
        return type;
    }

    public int getRowCount() {
        return columnValues.length;
    }

    public T[] getData() {
        return columnValues;
    }

    public T getCell(int rowIndex) {
        return columnValues[rowIndex];
    }

    @SuppressWarnings("unchecked") //suppressed because a TableDataException is thrown if type check fails
    public <K> T getCellByKey(K key) {
        return columnValues[((TableKey<K>) primaryKey).getRowNumber(key)];
    }

    public Iterator<T> iterator() {
        return ArrayUtil.getIterator(columnValues);
    }
}
