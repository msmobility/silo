package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import static com.pb.sawdust.util.Range.*;

import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.test.TestBase;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code PieceWiseDataTableTest} ...
 *
 * @author crf
 *         Started 9/30/11 12:05 PM
 */
public class PieceWiseDataTableTest extends FixedSizeDataTableTest {

    public static void main(String ... args) {
        TestBase.main(args);
    }

    @Override
    protected DataTable getDataTable(Object[][] tableData, TableSchema schema) {
        int extraRows = random.nextInt(100,200);
        List<Boolean> match = new LinkedList<Boolean>();
        List<Object[]> data = new LinkedList<Object[]>();
        for (Object[] d : tableData) {
            data.add(d);
            match.add(true);
        }
        int counter = tableData.length;
        int keyColumn = testData.getColumnOrdinal(DataType.INT);
        for (int i : range(extraRows)) {
            int r = random.nextInt(tableData.length);
            int ri = random.nextInt(data.size());
            Object[] row = ArrayUtil.copyArray(tableData[r]);
            row[keyColumn] = counter++;
            data.add(ri,row);
            match.add(ri,false);
        }
        Object[][] sourceData = data.toArray(new Object[data.size()][]);
        int[] index = new int[tableData.length];
        counter = 0;
        int ind = 0;
        for (boolean b : match) {
            if (b)
                index[ind++] = counter;
            counter++;
        }
        return new PieceWiseDataTable(new RowDataTable(schema,sourceData),index);
    }
}
