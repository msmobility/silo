package com.pb.sawdust.tabledata.sql.impl.sqlite;

import com.pb.sawdust.tabledata.sql.SqlDataColumnTest;
import com.pb.sawdust.tabledata.sql.SqlDataSet;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.sql.impl.SqlImplTestUtil;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author crf <br/>
 *         Started: Dec 2, 2008 7:20:25 PM
 */
public class SqliteSqlDataColumnTest<T> extends SqlDataColumnTest<T> {
    public static final String DATA_TYPE_KEY = "data type key";

    protected DataType type;

    public static void main(String ... args) {
        TestBase.main();
        if (SqlImplTestUtil.shouldPerformTestFinishOperations(args))
            SqlImplTestUtil.performTestFinishOperations();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (DataType type : DataType.values()) {
            for (SqlitePackageTests.SqliteDataSetType dataSetType : SqlitePackageTests.SqliteDataSetType.values()) {
                Map<String,Object> ct = buildContext(DATA_TYPE_KEY,type);
                ct.put(SqlitePackageTests.SQLITE_DATA_SET_TYPE_KEY,dataSetType);
                context.add(ct);
            }
        }
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

    protected SqlDataSet getSqlDataSet() {
        return SqlitePackageTests.getDataSet((SqlitePackageTests.SqliteDataSetType) getTestData(SqlitePackageTests.SQLITE_DATA_SET_TYPE_KEY));
    }
}
