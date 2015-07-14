package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.AbstractDataSet;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.TableDataException;

import com.pb.sawdust.tabledata.metadata.TableSchema;

/**
 * The {@code BasicDataSet} class provides a simple implementation of the {@code DataSet} interface.
 *
 * @author crf <br/>
 *         Started: May 27, 2008 9:11:22 PM
 */
public class BasicDataSet extends AbstractDataSet<DataTable> {

    protected DataTable transferTable(DataTable table) {
        return table;
    }

    public DataTable addTable(TableSchema schema) {
        String tableLabel = schema.getTableLabel();
        if (hasTable(tableLabel))
            throw new TableDataException(TableDataException.DATA_TABLE_ALREADY_EXISTS,tableLabel);
        tableLabelToSchema.put(tableLabel,schema);
        return tableLabelToTable.put(tableLabel,new ListDataTable(schema));
    }
}
