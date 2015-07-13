package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.AbstractDataRowTest;
import com.pb.sawdust.util.test.TestBase;

/**
 * @author crf <br/>
 *         Started: Sep 24, 2008 10:07:52 AM
 */
public class BasicDataRowTest extends AbstractDataRowTest {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected DataRow getDataRow(Object[] rowData) {
        return new BasicDataRow(rowData,getColumnLabels(),getColumnTypes(),false);
    }
}
