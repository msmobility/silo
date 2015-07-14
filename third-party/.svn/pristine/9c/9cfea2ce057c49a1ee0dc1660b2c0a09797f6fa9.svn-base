package com.pb.sawdust.tabledata.sql.impl.derby;

import com.pb.sawdust.tabledata.sql.SqlDataRowTest;
import com.pb.sawdust.tabledata.sql.SqlDataSet;
import com.pb.sawdust.tabledata.sql.SqlTableDataUtil;
import com.pb.sawdust.tabledata.sql.impl.SqlImplTestUtil;
import com.pb.sawdust.util.test.TestBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author crf <br/>
 *         Started: Dec 7, 2008 3:53:22 PM
 */
public class DerbySqlDataRowTest extends SqlDataRowTest {

    public static void main(String ... args) {
        //if should perform finish operations, then this test is being run by itself, so make sure that the derby dir is clear
        if (SqlImplTestUtil.shouldPerformTestFinishOperations(args)) {
            TestBase.classStarted();
            DerbyPackageTests.performTestStartOperations();
        }
        TestBase.main();
        if (SqlImplTestUtil.shouldPerformTestFinishOperations(args))
            TestBase.classFinished(new Runnable() {
                public void run() {
                     DerbyPackageTests.performTestFinishOperations();
                }
            });
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (DerbyPackageTests.DerbyDataSetType dataSetType : DerbyPackageTests.DerbyDataSetType.values())
            context.add(buildContext(DerbyPackageTests.DERBY_DATA_SET_TYPE_KEY,dataSetType));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }

    protected SqlDataSet getSqlDataSet() {
        return DerbyPackageTests.getDataSet((DerbyPackageTests.DerbyDataSetType) getTestData(DerbyPackageTests.DERBY_DATA_SET_TYPE_KEY));
    }

    protected void dropTableIfExistsFromDatabase(String tableName, SqlDataSet sqlDataSet) {
        Connection c = null;
        ResultSet r = null;
        try {
            c = sqlDataSet.getConnection();
            r = c.getMetaData().getTables(null,null,tableName,null);
            if (r.next())
                sqlDataSet.executeSqlUpdate("DROP TABLE " + SqlTableDataUtil.formQuotedIdentifier(tableName,sqlDataSet.getIdentifierQuote()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (r != null)
                    r.close();
            } catch (SQLException e) {
                //ignore
            }
            try {
                if (c != null)
                    c.close();
            } catch (SQLException e) {
                //ignore
            }
        }
    }
}
