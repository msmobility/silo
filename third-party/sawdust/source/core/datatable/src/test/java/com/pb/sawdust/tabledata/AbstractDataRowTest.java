package com.pb.sawdust.tabledata;

import org.junit.Before;

/**
 * @author crf <br/>
 *         Started: Sep 26, 2008 8:01:17 AM
 */
abstract public class AbstractDataRowTest extends DataRowTest {
    protected AbstractDataRow abstractRow;

    @Before
    public void beforeTest() {
        super.beforeTest();
        abstractRow = (AbstractDataRow) row;
    }
}
