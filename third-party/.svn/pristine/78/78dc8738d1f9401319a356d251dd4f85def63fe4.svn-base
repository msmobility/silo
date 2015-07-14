package com.pb.sawdust.tabledata.sql.impl.sqlite;

import com.pb.sawdust.tabledata.sql.impl.SqlImplTestUtil;
import com.pb.sawdust.tabledata.sql.SqlDataSet;
import com.pb.sawdust.util.test.TestBase;

import java.io.File;

/**
 * @author crf <br/>
 *         Started: Dec 2, 2008 7:16:01 PM
 */
public class SqlitePackageTests {
    public static final String SQLITE_DATA_SET_TYPE_KEY = "sqlite data set type";

    public static void main(String ... args) {
        TestBase.classStarted();
        SqliteSqlDataRowTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        SqliteSqlDataColumnTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        SqliteSqlDataTableTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        SqliteSqlDataSetTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        TestBase.classFinished(new Runnable() {
            public void run() {
                SqlImplTestUtil.performTestFinishOperations();
            }
        });
    }
        
    public static SqlDataSet getDataSet(SqliteDataSetType type) {
        switch(type) {
            case MEMORY : return new SqliteMemoryDataSet("sqlite_mem" + Thread.currentThread().getId());
            case FILE : return new SqliteFileDataSet(getDatabaseFile());
            default : throw new RuntimeException("Not ready for " + type + " data set tests");
        }
    }

    public static enum SqliteDataSetType {
        MEMORY,
        FILE
    }

    public static String getDatabaseFile() {
        return new File(SqlImplTestUtil.getDatabasePath("sqlite_file") + ".db").getPath();
    }
}
