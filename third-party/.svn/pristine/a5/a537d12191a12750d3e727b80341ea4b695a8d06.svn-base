package com.pb.sawdust.tabledata.read;

import com.pb.sawdust.tabledata.metadata.DataType;

/**
 * The {@code TableReader} interface is used to read table data from external data sources. From this table data,
 * {@code DataTable} object can be created. There is no restriction on the external data source, so long as the required
 * table data can be formed for access.
 *
 * @author crf <br/>
 *         Started: Jun 19, 2008 3:50:03 PM
 */
public interface TableReader {

    /**
     * Get the name of the table.
     *
     * @return the table's name.
     */
    String getTableName();

    /**
     * Get the names of the columns, in the order that they appear in the data table. As required by {@code DataTable},
     * each column name should be unique.
     *
     * @return the names of the columns in the table.
     */
    String[] getColumnNames();

    /**
     * Get the column types, in the order that they appear in the table.
     *
     * @return the types of the column in the table.
     */
    DataType[] getColumnTypes();

    /**
     * Get the table data, organized by rows. The data should be structured such that it conforms to the parameter
     * specification in {@link com.pb.sawdust.tabledata.DataTable#addDataByRow(Object[][])}.
     *
     * @return the table data.
     */
    Object[][] getData();
}
