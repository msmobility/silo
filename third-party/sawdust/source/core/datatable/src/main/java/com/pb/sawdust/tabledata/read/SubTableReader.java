package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.util.Filter;
import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code SubTableReader} interface provides a framework through which subtables (parts of a table) may be read from
 * an external data source. The user of the interface may specify whih columns and/or rows to include when reading the
 * table, which may provide some performance or resource-use benefits. The specification also allows columns to be reordered
 * (though not repeated) when read in.
 * @author crf <br/>
 *         Started: Nov 2, 2008 12:40:35 PM
 */
public interface SubTableReader extends TableReader {
    /**
     * Set the columns to keep by specifying the columns' indices. Calls to this function will override all previous calls
     * to {@code setColumnsToKeep(int...)}, {@code setColumnsToKeep(String...)}, or {@code resetColumnsToKeep()}. The order
     * of {@code columns} specifies the order of the columns in the {@code DataTable} created by this reader.
     *
     * @param columns
     *        The (0-based) indices of the columns to keep.
     */
    void setColumnsToKeep(int ... columns);

    /**
     * Set the columns to keep by speifying the columns' labels. Calls to this function will override all previous calls
     * to {@code setColumnsToKeep(int...)}, {@code setColumnsToKeep(String...)}, or {@code resetColumnsToKeep()}. The order
     * of {@code columns} specifies the order of the columns in the {@code DataTable} created by this reader.
     *
     * @param columns
     *        The labels of the columns to keep.
     */
    void setColumnsToKeep(String ... columns);

    /**
     * (Re)set the reader to keep all columns from the table.
     */
    void resetColumnsToKeep();

    /**
     * Set a row index filter which will be used to determine rows to keep. If the filter, which takes the (0-based) index
     * of the row, returns {@code true}, the row will be kept. This filter is used in conjuction with (not as a replacement
     * for) the rows specified to be kept by calls to {@code setRowsToKeep(Filter<Object[]>)}.
     *
     * @param rowFilter
     *        A row index filter which specifies if a row is to be kept or not.
     */
    void setRowsToKeep(Filter<Integer> rowFilter);

    /**
     * Set a row data filter which will be used to determine rows to keep. If the filter, which takes an object array of
     * row data (including all columns, not just those to be kept in the returned table data), returns {@code true}, the
     * row will be kept. This filter is used in conjuction with (not as a replacement for) the rows specified to be kept
     * by calls to {@code setRowsToKeep(Filter<Integer>)}.
     *
     * @param rowFilter
     *        A row data filter which specifies if a row is to be kept or not.
     */
    void setRowFilter(Filter<Object[]> rowFilter);

    /**
     * (Re)set the reader to keep all rows from the table.
     */
    void resetRowsToKeep();

    /**
     * {@inheritDoc}
     *
     * This function will return only the names of the columns which are specified to be kept.
     */
    String[] getColumnNames();

    /**
     * {@inheritDoc}
     *
     * This function will return only the types of the columns which are specified to be kept.
     */
    DataType[] getColumnTypes();

    /**
     * {@inheritDoc}
     *
     * This function will return only the rows which are specifiedto be kept and which pass the row filter specified by
     * {@code setRowFilter(Filter<Object[]>)}, and each row of data will only contain those columns specified to be kept.
     */
    Object[][] getData();
}
