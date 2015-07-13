package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.TableSchema;

import java.util.Set;

/**
 * <p>
 * The {@code DataSet} interface provides a framework to hold a number of {@link DataTable}s. By
 * providing a central place through which the data tables may be accessed, relational information contained
 * in a data table's {@link com.pb.sawdust.tabledata.metadata.TableSchema} may be verified and utilized.  It is expected that no more
 * than one table with a given label (name) be placed in a given data set (hence the name "set").  However,
 * there is no expectation nor requirement that two tables in a given data set have any relationship other
 * than their presence in that set.
 * </p>
 * <p>
 * There is some implementation flexibility in defining the relationship between data sets and data tables.
 * Specifically, in some cases data tables may be created first, and data sets are merely containers to which
 * the data tables are added. In other cases (<i>e.g.</i> {@code SqlDataSet}), the data set is the gateway
 * through which data tables are created. Because of this, there is some redundancy in the method definitions
 * (two {@code addTable} methods, a {@code getTableSchema} method as well as a {@code getSchema} method in
 * {@code DataTable}).  It is expected that one method will "dominate" (or be preferred over) the other, and
 * that the implementing class' documentation will state the preferred method for use.  Alternatively, one
 * method may be unimplemented, and an {@code UnsupportedOperationException} will be thrown.
 * </p>
 *
 * @param <T> 
 *        The type of data table this data set holds.
 *
 * @author crf <br/>
 *         Started: May 13, 2008 5:37:00 AM
 */
public interface DataSet<T extends DataTable> {

    /**
     * Check to see if this data set contains a table with the specified label.
     *
     * @param tableLabel
     *        The lable of the table whose existence in this data set is to be checked.
     *
     * @return {@code true} if a table with label {@code tableLabel} exists in this data set, {@code false} otherwise.
     */
    boolean hasTable(String tableLabel);

    /**
     * Get an set of labels for all of the tables contained in this data set.
     *
     * @return the labels for all of the tables in this data set.
     */
    Set<String> getTableLabels();

    /**
     * Add a table to this data set.  This will add the table in its entirety - both structure and data.  This method
     * may be unimplemented, but in that case {@link DataSet#addTable(TableSchema)} must be implemented. The input
     * table need not be of the same type as that held by this data set; this method will make the necessary conversions,
     * as needed.
     *
     * @param table
     *        The table to add to this data set.
     *
     * @return the new table, as it exists in the data set.
     *
     * @throws TableDataException if a table with {@code table}'s label already exists in this data set.
     * @throws UnsupportedOperationException if this method is unimplemented.
     */
    DataTable addTable(DataTable table);

    /**
     * Add a table to this data set via a table schema definition.  This will only add a table with the defined
     * structure, data must be added using one of the various {@code DataTable} methods.  This method may be
     * unimplemented, but in that case {@link DataSet#addTable(DataTable)} must be implemented.
     *
     * @param schema
     *        The schema defining the table to add to this data set.
     *
     * @return the new table, as it exists in the data set.
     *
     * @throws TableDataException if a table with {@code table}'s label already exists in this data set.
     * @throws UnsupportedOperationException if this method is unimplemented.
     */
    DataTable addTable(TableSchema schema);

    /**
     * Remove the specified table from the data set.
     *
     * @param tableLabel
     *        The label of the table to drop.
     *
     * @return the removed table.
     *
     * @throws TableDataException if the table does not exist in the data set.
     */
    DataTable dropTable(String tableLabel);

    /**
     * Get the table in this data set corresponding to a specified label.
     *
     * @param tableLabel
     *        The label of the table to retrieve.
     *
     * @return the table corresponding to {@code tableLabel}.
     *
     * @throws TableDataException if no table with {@code tableLabel} exists in this data set.
     */
    T getTable(String tableLabel);

    /**
     * Get the table schema for the table in this data set corresponding to a specified label.
     *
     * @param tableLabel
     *        The label of the table whose schema is to be retrieved.
     *
     * @return the table schema for the table corresponding to {@code tableLabel}.
     *
     * @throws TableDataException if no table with {@code tableLabel} exists in this data set.
     */
    TableSchema getTableSchema(String tableLabel);

    /**
     * Verify the consistency of the table schemas in this data set.  At a minimum, this should include checking
     * that the schema is consistent with its table, and that the schema's other table references exists in the data
     * set and are themselves consistent. Ideally, this method would also look into the data tables to ensure that
     * the actual data relationships are also consistent (<i>e.g.</i> that a column which is keyed to another table's
     * id column only contains number in the other tables id column).
     *
     * @return {@code true} if the schemas are consistent, {@code false} othewise.
     */
    boolean verifySchemas();

}
