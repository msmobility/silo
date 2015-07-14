package com.pb.sawdust.tabledata.sql.impl.h2;

import com.pb.sawdust.tabledata.sql.SqlDataSetTest;
import com.pb.sawdust.tabledata.sql.SqlDataSet;
import com.pb.sawdust.tabledata.sql.SqlDataTable;
import com.pb.sawdust.tabledata.DataSet;
import com.pb.sawdust.tabledata.sql.impl.SqlImplTestUtil;
import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.util.Filter;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author crf <br/>
 *         Started: Dec 7, 2008 5:12:29 PM
 */
public class H2SqlDataSetTest extends SqlDataSetTest {

    public static void main(String ... args) {
        TestBase.main();
        if (SqlImplTestUtil.shouldPerformTestFinishOperations(args))
            H2PackageTests.performTestFinishOperations();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (H2PackageTests.H2DataSetType dataSetType : H2PackageTests.H2DataSetType.values())
            context.add(buildContext(H2PackageTests.H2_DATA_SET_TYPE_KEY,dataSetType));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }

    protected SqlDataSet getDistinctSqlDataSet() {
        return H2PackageTests.getDistinctSqlDataSet();
    }

     protected Filter<String> getTableDropFilter() {
        return new Filter<String>() {
            public boolean filter(String input) {
                return !H2PackageTests.systemTables.contains(input);
            }
        };
    }

    protected DataSet<SqlDataTable> getDataSet() {
        return H2PackageTests.getDataSet((H2PackageTests.H2DataSetType) getTestData(H2PackageTests.H2_DATA_SET_TYPE_KEY));
    }
}
