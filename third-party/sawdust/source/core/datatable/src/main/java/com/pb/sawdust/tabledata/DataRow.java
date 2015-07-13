package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * <p>
 * The {@code DataRow} interface provides a framework through which rows of a {@link DataTable} can be
 * accessed. It allows access to greater row information than would a simple list of row data. It does not require any
 * explicit connection to a data table, which both simplifies the interface and allows it to be independant of a data
 * source (though column labels, types and cardinality, as well as table indices provide an implicit link to the
 * parent data table).
 * </p>
 *
 * @author crf <br>
 *         Started: May 6, 2008 9:29:50 PM<br>
 */
public interface DataRow {

    /**
     * Get the column labels, ordered as they are in this data row.
     *
     * @return this data row's column labels.
     */
    String[] getColumnLabels();

    /**
     * Get the label of a column at a specified index.
     *
     * @param columnIndex
     *        The (0-based) index whose column label is to be retrieved.
     *
     * @return the column label corresponding to the column at {@code columnIndex}
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row. 
     */
    String getColumnLabel(int columnIndex);

    /**
     * Determine whether this data row has a column with a specified label.
     *
     * @param columnLabel
     *        The column label whose presence is to be queried.
     *
     * @return {@code true} if this data row has a column with the specified label, {@code false} otherwise.
     */
    boolean hasColumn(String columnLabel);

    /**
     * Get the index of the column with a specified label. Indices are 0-based, for reasons described in
     * {@link DataTable}.
     *
     * @param columnLabel
     *        The label of the column whose index is to be retrieved.
     *
     * @return the (0-based) index of the column corresponding to {@code columnLabel}.
     *
     * @throws TableDataException if no column corresponding to {@code columnLabel} exists.
     */
    int getColumnIndex(String columnLabel);

    /**
     * Get the column types, ordered as they are in this data row.
     *
     * @return this data row's column types.
     */
    DataType[] getColumnTypes();

    /**
     * Get the data type of the column at a specified index.  Indices are 0-based, for reasons described in
     * {@link DataTable}.
     *
     * @param columnIndex
     *        The (0-based) index of the column whose type is to be retrieved.
     *
     * @return the data type of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row.
     */
    DataType getColumnType(int columnIndex);

    /**
     * Get the data type of the column with a specified label.
     *
     * @param columnLabel
     *        The label of the column whose type is to be retrieved.
     *
     * @return the data type of the column corresponding to {@code columnLabel}.
     *
     * @throws TableDataException if no column corresponding to {@code columnLabel} exists.
     */
    DataType getColumnType(String columnLabel);

    /**
     * Get the elements of this data row as an array. Because each data element may be of a different type, the
     * returned data array is a generic {@code Object} array; to cast each element to its true class the column
     * type information available in the {@code getColumnType} methods can be used.
     *
     * @return the elements of this data row.
     */
    Object[] getData();

    /**
     * Get the row value at a specified column index.  If the type parameter {@code T} is specified, then it
     * must match the data column's type (the class of {@code T} must match {@code getColumnType(columnIndex).getObjectClass()}).
     *
     * @param columnIndex
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row.
     */
    <T> T getCell(int columnIndex);

    /**
     * Get the row value at a column with the specified label. If the type parameter {@code T} is specified, then it
     * must match the data column's type (the class of {@code T} must match {@code getColumnType(columnLabel).getObjectClass()}).
     *
     * @param columnLabel
     *        The label of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnLabel}.
     *
     * @throws TableDataException if no column corresponding to {@code columnLabel} exists.
     */
    <T> T getCell(String columnLabel);

    /**
     * Get the row value at a specified column index as a {@code byte}. The value must be convertible to a {@code byte}
     * (or {@code byte}) or an exception will be thrown.
     *
     * @param columnIndex
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code byte}}.
     */
    byte getCellAsByte(int columnIndex);

    /**
     * Get the row value at a column with the specified label as a {@code byte}. The value must be convertible to a
     * {@code byte} (or {@code Byte}) or an exception will be thrown.
     *
     * @param columnLabel
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code byte}}.
     */
    byte getCellAsByte(String columnLabel);

    /**
     * Get the row value at a specified column index as a {@code short}. The value must be convertible to a {@code short}
     * (or {@code Short}) or an exception will be thrown.
     *
     * @param columnIndex
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code short}}.
     */
    short getCellAsShort(int columnIndex);

    /**
     * Get the row value at a column with the specified label as a {@code short}. The value must be convertible to a
     * {@code short} (or {@code Short}) or an exception will be thrown.
     *
     * @param columnLabel
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code short}}.
     */
    short getCellAsShort(String columnLabel);

    /**
     * Get the row value at a specified column index as a {@code int}. The value must be convertible to a {@code int}
     * (or {@code Integer}) or an exception will be thrown.
     *
     * @param columnIndex
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code int}}.
     */
    int getCellAsInt(int columnIndex);

    /**
     * Get the row value at a column with the specified label as a {@code int}. The value must be convertible to a
     * {@code int} (or {@code Int}) or an exception will be thrown.
     *
     * @param columnLabel
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code int}}.
     */
    int getCellAsInt(String columnLabel);

    /**
     * Get the row value at a specified column index as a {@code long}. The value must be convertible to a {@code long}
     * (or {@code Long}) or an exception will be thrown.
     *
     * @param columnIndex
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code long}}.
     */
    long getCellAsLong(int columnIndex);

    /**
     * Get the row value at a column with the specified label as a {@code long}. The value must be convertible to a
     * {@code long} (or {@code Long}) or an exception will be thrown.
     *
     * @param columnLabel
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code long}}.
     */
    long getCellAsLong(String columnLabel);

    /**
     * Get the row value at a specified column index as a {@code float}. The value must be convertible to a {@code float}
     * (or {@code Float}) or an exception will be thrown.
     *
     * @param columnIndex
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code float}}.
     */
    float getCellAsFloat(int columnIndex);

    /**
     * Get the row value at a column with the specified label as a {@code float}. The value must be convertible to a
     * {@code float} (or {@code Float}) or an exception will be thrown.
     *
     * @param columnLabel
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code float}}.
     */
    float getCellAsFloat(String columnLabel);

    /**
     * Get the row value at a specified column index as a {@code double}. The value must be convertible to a {@code double}
     * (or {@code Double}) or an exception will be thrown.
     *
     * @param columnIndex
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code double}}.
     */
    double getCellAsDouble(int columnIndex);

    /**
     * Get the row value at a column with the specified label as a {@code double}. The value must be convertible to a
     * {@code double} (or {@code Double}) or an exception will be thrown.
     *
     * @param columnLabel
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code double}}.
     */
    double getCellAsDouble(String columnLabel);

    /**
     * Get the row value at a specified column index as a {@code boolean}. The value must be convertible to a {@code boolean}
     * (or {@code Boolean}) or an exception will be thrown.
     *
     * @param columnIndex
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code boolean}}.
     */
    boolean getCellAsBoolean(int columnIndex);

    /**
     * Get the row value at a column with the specified label as a {@code boolean}. The value must be convertible to a
     * {@code boolean} (or {@code Boolean}) or an exception will be thrown.
     *
     * @param columnLabel
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     * @throws TableDataException if the column index is less than 0 or greater than or equal to the number of columns
     *         in this data row, or if the cell value cannot be converted to a {@code boolean}}.
     */
    boolean getCellAsBoolean(String columnLabel);

    /**
     * Get the row value at a specified column index as a {@code String}. The default (JVM) formatting will be
     * used.
     *
     * @param columnIndex
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     *
     */
    String getCellAsString(int columnIndex);

    /**
     * Get the row value at a column with the specified label as a {@code String}. The default (JVM) formatting will be
     * used.
     *
     * @param columnLabel
     *        The (0-based) index of the column whose value is to be retrieved.
     *
     * @return the value of the column corresponding to {@code columnIndex}.
     */
    String getCellAsString(String columnLabel);

    /**
     * Get the column values from this row corresponding to a given index. The column values should be ordered as the
     * columns are in the index.
     *
     * @param index
     *        The table index.
     *
     * @return the column values corresponding to {@code index}.
     */
    Object[] getIndexValues(TableIndex index);

    /**
     * Indicates whether this data table will attempt to coerce data to the correct types when getting. This will be inherited
     * from the row's parent {@code DataTable} at the time of creation.
     *
     * @return {@code true} if data will be coerced, {@code false} otherwise.
     */
    boolean willCoerceData();

}
