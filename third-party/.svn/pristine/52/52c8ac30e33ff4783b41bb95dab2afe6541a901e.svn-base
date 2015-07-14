package com.pb.sawdust.tabledata;

import org.junit.Before;
import org.junit.Test;

/**
 * @author crf <br/>
 *         Started: Sep 27, 2008 4:23:34 PM
 */
abstract public class AbstractDataSetTest<T extends DataTable> extends DataSetTest<T> {
    protected AbstractDataSet<T> abstractDataSet;

    @Before
    public void beforeTest() {
        super.beforeTest();
        abstractDataSet = (AbstractDataSet<T>) dataSet;
    }

    protected T getTransferTableResult(DataTable input) {
        return abstractDataSet.transferTable(input);
    }

    @Test
    abstract public void testTransferTable();
}
