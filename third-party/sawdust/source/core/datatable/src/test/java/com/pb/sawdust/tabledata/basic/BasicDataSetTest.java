package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.tabledata.DataSet;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.AbstractDataSetTest;
import com.pb.sawdust.tabledata.util.TableDataFactory;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Sep 25, 2008 10:45:55 AM
 */
public class BasicDataSetTest extends AbstractDataSetTest<DataTable> {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected DataTable getDataTable(TableSchema schema, Object[][] data) {
        return TableDataFactory.getDataTable(schema,data);
    }

    protected DataSet<DataTable> getDataSet() {
        return new BasicDataSet();
    }

    public void testTransferTable() {
        DataTable table = getDataTable(getTableSchema("just_a_table"),getTableData());
        assertEquals(table,getTransferTableResult(table));
    }
}
