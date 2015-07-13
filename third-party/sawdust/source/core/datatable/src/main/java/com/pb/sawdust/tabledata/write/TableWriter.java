package com.pb.sawdust.tabledata.write;

import com.pb.sawdust.tabledata.DataTable;

/**
 * The {@code TableWriter} interface provides a generic structure which can be used to write data tables to external
 * sinks. There are no restriction on the external sinks, only that they can receive and (in theory) store table data.
 *
 * @author crf <br/>
 *         Started: Jul 25, 2008 7:18:37 AM
 */
public interface TableWriter {

    /**
     * Write a data table to a external sink.
     *
     * @param table
     *        The data table to write.
     */
    void writeTable(DataTable table);
}
