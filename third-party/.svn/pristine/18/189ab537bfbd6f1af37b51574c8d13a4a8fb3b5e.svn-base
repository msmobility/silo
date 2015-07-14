package com.pb.sawdust.tabledata.sql.impl.derby;

import com.pb.sawdust.io.FileUtil;
import com.pb.sawdust.tabledata.sql.impl.SqlImplTestUtil;
import com.pb.sawdust.tabledata.sql.SqlDataSet;
import com.pb.sawdust.util.test.TestBase;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author crf <br/>
 *         Started: Dec 7, 2008 2:14:18 PM
 */
public class DerbyPackageTests {
    public static final String DERBY_DATA_SET_TYPE_KEY = "derby data set type";

    public static void main(String ... args) {
        TestBase.classStarted();
        performTestStartOperations();
        DerbySqlDataRowTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        DerbySqlDataTableTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        DerbySqlDataSetTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        DerbySqlDataColumnTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        TestBase.classFinished(new Runnable() {
                public void run() {
                    performTestFinishOperations();
                }
        });
    }

    public static void performTestStartOperations() {
        for (File derbyPath : getDerbyDatabaseFilePath().getParentFile().listFiles(SqlImplTestUtil.getDatabasePathFilter("derby")))
            FileUtil.deleteDir(derbyPath);
    }

    public static void performTestFinishOperations() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            //suppress shutdown exception
        }
        SqlImplTestUtil.performTestFinishOperations();
    }

    public static SqlDataSet getDataSet(DerbyDataSetType type) {
        switch(type) {
            case FILE : return new DerbyFileDataSet(getDerbyDatabaseFilePath().getPath());
//            case MEMORY : return new DerbyMemoryDataSet("memoryDb");
            default : throw new RuntimeException("Not ready for " + type + " data set tests");
        }
    }

    public static enum DerbyDataSetType {
//        MEMORY, - doesn't work right now, and slow as hell anyway
        FILE,
//        SERVER
    }

    public static File getDerbyDatabaseFilePath() {
        return SqlImplTestUtil.getDatabasePath("derby");
    }
}
