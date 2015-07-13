package com.pb.sawdust.tabledata;

import org.junit.Before;

/**
 * @author crf <br/>
 *         Started: Sep 26, 2008 7:57:19 AM
 */
public abstract class AbstractDataColumnTest<T> extends DataColumnTest<T> {
    protected AbstractDataColumn<T> abstractColumn;

    @Before
    public void beforeTest() {
        super.beforeTest();
        abstractColumn = (AbstractDataColumn<T>) column;
    }
}
