package com.pb.sawdust.tabledata.sql.impl.sqlite;

import com.pb.sawdust.tabledata.sql.SqlDataRowTest;
import com.pb.sawdust.tabledata.sql.SqlDataSet;
import com.pb.sawdust.tabledata.sql.impl.SqlImplTestUtil;
import com.pb.sawdust.util.test.TestBase;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author crf <br/>
 *         Started: Dec 2, 2008 7:20:48 PM
 */
public class SqliteSqlDataRowTest extends SqlDataRowTest {

    public static void main(String ... args) {
        TestBase.main();
        if (SqlImplTestUtil.shouldPerformTestFinishOperations(args))
            SqlImplTestUtil.performTestFinishOperations();
    }    

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (SqlitePackageTests.SqliteDataSetType dataSetType : SqlitePackageTests.SqliteDataSetType.values())
            context.add(buildContext(SqlitePackageTests.SQLITE_DATA_SET_TYPE_KEY,dataSetType));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }

    protected SqlDataSet getSqlDataSet() {
        return SqlitePackageTests.getDataSet((SqlitePackageTests.SqliteDataSetType) getTestData(SqlitePackageTests.SQLITE_DATA_SET_TYPE_KEY));
    }
}
