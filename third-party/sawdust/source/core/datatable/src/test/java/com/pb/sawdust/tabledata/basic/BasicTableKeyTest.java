package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.tabledata.TableKey;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.AbstractTableKeyTest;

/**
 * @author crf <br/>
 *         Started: Sep 25, 2008 2:53:13 PM
 */
public class BasicTableKeyTest<I> extends AbstractTableKeyTest<I> {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected TableKey<I> getTableKey(String integerIndexColumnLabel, DataTable table) {
        return new BasicTableKey<I>(table,integerIndexColumnLabel);
    }
}
