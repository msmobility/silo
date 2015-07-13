package com.pb.sawdust.tabledata.basic;

import com.pb.sawdust.tabledata.DataColumn;
import com.pb.sawdust.tabledata.AbstractDataColumnTest;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author crf <br/>
 *         Started: Sep 23, 2008 9:16:25 AM
 */
public class BasicDataColumnTest<T> extends AbstractDataColumnTest<T> {
    public static final String DATA_TYPE_KEY = "data type key";

    protected DataType type;

    public static void main(String ... args) {
        TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (DataType type : DataType.values())
            context.add(buildContext(DATA_TYPE_KEY,type));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }

    @Before
    public void beforeTest() {
        type = (DataType) getTestData(DATA_TYPE_KEY);
        super.beforeTest();
    }

    protected DataType getColumnDataType() {
        return type;
    }

    protected DataColumn<T> getDataColumn(T[] columnData, String columnName) {
        return new BasicDataColumn<T>(columnName,columnData,getColumnDataType(),getTableKey());
    }
}
