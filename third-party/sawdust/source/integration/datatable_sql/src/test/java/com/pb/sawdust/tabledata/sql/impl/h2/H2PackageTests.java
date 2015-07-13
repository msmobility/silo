package com.pb.sawdust.tabledata.sql.impl.h2;

import com.pb.sawdust.tabledata.sql.impl.SqlImplTestUtil;
import com.pb.sawdust.tabledata.sql.SqlDataSet;
import com.pb.sawdust.util.test.TestBase;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author crf <br/>
 *         Started: Dec 7, 2008 5:13:58 PM
 */
public class H2PackageTests {
    public static final String H2_DATA_SET_TYPE_KEY = "h2 data set type";
    private static final Map<H2DataSetType,Set<String>> CREATED_DBS = new EnumMap<H2DataSetType, Set<String>>(H2DataSetType.class);

    public static void main(String ... args) {
        TestBase.classStarted();
        H2SqlDataRowTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        H2SqlDataTableTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        H2SqlDataSetTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        H2SqlDataColumnTest.main(SqlImplTestUtil.formShouldSkipFinishOperationsMainArgs());
        TestBase.classFinished(new Runnable() {
            public void run() {
                performTestFinishOperations();
            }
        });
    }

    private static String registerName(H2DataSetType type, String dbName) {
        if (!CREATED_DBS.containsKey(type))
            CREATED_DBS.put(type,new HashSet<String>());
        CREATED_DBS.get(type).add(dbName);
        return dbName;
    }
        
    public static SqlDataSet getDataSet(H2DataSetType type) {
        switch(type) {
            case MEMORY : return new H2MemoryDataSet(registerName(type,"h2_mem" + Thread.currentThread().getId()));
            case FILE : return new H2FileDataSet(registerName(type,SqlImplTestUtil.getDatabasePath("h2_file").getPath()));
            default : throw new RuntimeException("Not ready for " + type + " data set tests");
        }
    }    
    
    public static SqlDataSet getDistinctSqlDataSet() {
        return new H2MemoryDataSet(registerName(H2DataSetType.MEMORY,"distinct" + Thread.currentThread().getId()));
    }

    public static void performTestFinishOperations() {
        for (H2DataSetType type : H2DataSetType.values()) {
            if (CREATED_DBS.containsKey(type)) {
                for (String dbName : CREATED_DBS.get(type)) {
                    SqlDataSet ds;
                    switch (type) {
                        case MEMORY : ds = new H2MemoryDataSet(dbName); break;
                        case FILE : ds = new H2FileDataSet(dbName); break;
                        default : continue; //shouldn't be here, but let cleanup continue
                    }
                    try {
                        ds.executeSqlUpdate("SHUTDOWN");
                    } catch (Exception e) {
                        //swallow - should be because db already shutdown
                        System.out.println(" didn't close " + dbName + ": " + e.getMessage());
                    }
                }
            }
        }
        SqlImplTestUtil.performTestFinishOperations();
    }

    public static enum H2DataSetType {
        MEMORY,
        FILE,
//        SERVER
    }
    
    static Set<String> systemTables = new HashSet<String>();
    static{
        systemTables.add("CATALOGS");
        systemTables.add("COLLATIONS");
        systemTables.add("COLUMNS");
        systemTables.add("COLUMN_PRIVILEGES");
        systemTables.add("CONSTANTS");
        systemTables.add("CONSTRAINTS");
        systemTables.add("CROSS_REFERENCES");
        systemTables.add("DOMAINS");
        systemTables.add("FUNCTION_ALIASES");
        systemTables.add("FUNCTION_COLUMNS");
        systemTables.add("HELP");
        systemTables.add("INDEXES");
        systemTables.add("IN_DOUBT");
        systemTables.add("LOCKS");
        systemTables.add("RIGHTS");
        systemTables.add("ROLES");
        systemTables.add("SCHEMATA");
        systemTables.add("SEQUENCES");
        systemTables.add("SESSIONS");
        systemTables.add("SESSION_STATE");
        systemTables.add("SETTINGS");
        systemTables.add("TABLES");
        systemTables.add("TABLE_PRIVILEGES");
        systemTables.add("TABLE_TYPES");
        systemTables.add("TRIGGERS");
        systemTables.add("TYPE_INFO");
        systemTables.add("USERS");
        systemTables.add("VIEWS");
    }
}
