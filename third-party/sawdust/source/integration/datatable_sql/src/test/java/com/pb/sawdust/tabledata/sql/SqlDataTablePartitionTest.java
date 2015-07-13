package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.tabledata.DataTablePartitionTest;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The {@code SqlDataTablePartitionTest} ...
 *
 * @author crf <br/>
 *         Started 3/13/11 2:48 PM
 */
public class SqlDataTablePartitionTest extends DataTablePartitionTest {

    public static void main(String ... args) {
        TestBase.main();
    }

    @Test
    public void testGetSchema() {
        assertEquals(new SqlTableSchema(schema),table.getSchema());
    }
}
