package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.array.ArrayUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code FilteredDataTable} ...
 *
 * @author crf
 *         Started 3/24/12 11:47 AM
 */
public class FilteredDataTable extends PieceWiseDataTable {

    public FilteredDataTable(DataTable sourceTable, Filter<DataRow> rowFilter) {
        super(sourceTable,getRows(sourceTable,rowFilter));
    }

    private static int[] getRows(DataTable table, Filter<DataRow> rowFilter) {
        int counter = 0;
        List<Integer> rows = new LinkedList<>();
        for (DataRow row : table) {
            if (rowFilter.filter(row))
                rows.add(counter);
            counter++;
        }
        return ArrayUtil.toIntArray(rows);
    }
}
