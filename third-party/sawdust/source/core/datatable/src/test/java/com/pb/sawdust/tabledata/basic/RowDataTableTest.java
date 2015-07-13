package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.AbstractDataTableTest;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author crf <br/>
 *         Started: Sep 24, 2008 4:55:22 PM
 */
public class RowDataTableTest extends AbstractDataTableTest {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected DataTable getDataTable(Object[][] tableData, TableSchema schema) {
        return new RowDataTable(schema,tableData);
    }

    @Ignore
    @Test
    public void deleteColumnFromDataCount() {
        //restrictions on method make this test impossible
    }

    @Ignore
    @Test
     public void testAddColumnToDataCount() {
        //restrictions on method make this test impossible
    }
}
