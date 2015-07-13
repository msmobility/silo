package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * <p>
 * The {@code DataColumn} interface provides a framework through which columns of a {@link DataTable} can
 * be accessed.  It allows access to greater column information than would a simple list of values. It avoids any
 * explicit connection to a data table, except through the optional {@link DataColumn#getCellByKey(Object)}
 * method.  This both simplifies the interface and allows it to be independant of a data source, though its label
 * and type (generally specified in the data table schema) are accesible here.
 * </p>
 * <p>
 * An implementation specifies its type through its generic parameter as well as through an explicit {@link com.pb.sawdust.tabledata.metadata.DataType}.
 * It is required that these two will be coordinated and consistent; both are required to allow type safety and
 * inference. Specifically, if {@code column} is a {@code DataColumn<K>} instance on a {@code DataTable} instance
 * {@code table}, {@code K} must be the class reference corresponding to
 * {@code table.getColumnDataType(column.getLabel()).getObjectClass()}. To ensure that this contract is fulfilled, one
 * of the data column wrapper classes ({@code BooleanDataColumn}, {@code IntegerDataColumn}, <i>etc.</i>) should generally
 * be used.  In addition, by specifying the generic type explicitly, type erasure in these wrapper classes is avoided,
 * which prevents the need for annoying (and seemingly superfluous)
 * casts.
 * </p>
 *
 * @param <T>
 *        The type of data held by this data column.
 *
 * @author crf <br>
 *         Started: May 7, 2008 4:35:56 PM <br>
 */
public interface DataColumn<T>  extends Iterable<T> {

    /**
     * Get the label for this column.
     *
     * @return this column's label.
     */
    String getLabel();

    /**
     * Get the data type for this column.
     *
     * @return this column's data type.
     */
    DataType getType();

    /**
     * Get the number of rows in this column.
     *
     * @return the number of rows in this column.
     */
    int getRowCount();

    /**
     * Get this column as an array of its specified type.
     *
     * @return this column as an array of type {@code T}.
     */
    T[] getData();

    /**
     * Get the column value at a given index. Indices are 0-based, for reason described in {@link DataTable}.
     *
     * @param rowNumber
     *        The (0-based) number of the column value to retrieve.
     *
     * @return the column value at {@code rowIndex}.
     */
    T getCell(int rowNumber);

    /**
     * Get the column value at a specified key location. This method is optional and requires some sort of access
     * to the primary key of the source data set.  Whether the primary key is dynamic (<i>i.e.</i> changing the key
     * on the data table will cause this method to refer to the new key) is implemntation specific.
     *
     * @param key
     *        The key value of the row whose column value is to be retrieved.
     *
     * @return the column value corresponding to {@code key}.
     *
     * @throws UnsupportedOperationException if this method is not implemented.
     *
     * @throws TableDataException if the type of {@code key} does not correspond to key's {@code getColumnType()}
     *                            method, or if the key is invalid (not located in the column's primary key).
     *
     * @see TableKey#getKeyColumnType()
     */
    <K> T getCellByKey(K key);
}
