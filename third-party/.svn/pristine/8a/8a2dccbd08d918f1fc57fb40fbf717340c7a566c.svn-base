package com.pb.sawdust.tabledata.sql.impl.hsqldb;

import com.pb.sawdust.tabledata.sql.SqlDataSetTest;
import com.pb.sawdust.tabledata.sql.SqlDataSet;
import com.pb.sawdust.tabledata.sql.impl.SqlImplTestUtil;
import com.pb.sawdust.util.test.TestBase;
import com.pb.sawdust.util.Filter;

import java.util.*;

/**
 * @author crf <br/>
 *         Started: Dec 2, 2008 10:09:54 AM
 */
public class HsqldbSqlDataSetTest extends SqlDataSetTest {

    public static void main(String ... args) {
        TestBase.main();
        if (SqlImplTestUtil.shouldPerformTestFinishOperations(args))
            HsqldbPackageTests.performTestFinishOperations();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Map<String,Object>> context = new LinkedList<Map<String,Object>>();
        for (HsqldbPackageTests.HsqldbDataSetType dataSetType : HsqldbPackageTests.HsqldbDataSetType.values())
            context.add(buildContext(HsqldbPackageTests.HSQLDB_DATA_SET_TYPE_KEY,dataSetType));
        addClassRunContext(this.getClass(),context);
        return super.getAdditionalTestClasses();
    }
    
    protected SqlDataSet getDistinctSqlDataSet() {
        return HsqldbPackageTests.getDistinctSqlDataSet();
    }

    private static final Set<String> TABLES_TO_SKIP = new HashSet<String>();
    static {
        TABLES_TO_SKIP.add("ADMINISTRABLE_ROLE_AUTHORIZATIONS");
        TABLES_TO_SKIP.add("APPLICABLE_ROLES");
        TABLES_TO_SKIP.add("ASSERTIONS");
        TABLES_TO_SKIP.add("AUTHORIZATIONS");
        TABLES_TO_SKIP.add("BLOCKS");
        TABLES_TO_SKIP.add("CHARACTER_SETS");
        TABLES_TO_SKIP.add("CHECK_CONSTRAINT_ROUTINE_USAGE");
        TABLES_TO_SKIP.add("CHECK_CONSTRAINTS");
        TABLES_TO_SKIP.add("COLLATIONS");
        TABLES_TO_SKIP.add("COLUMN_COLUMN_USAGE");
        TABLES_TO_SKIP.add("COLUMN_DOMAIN_USAGE");
        TABLES_TO_SKIP.add("COLUMN_PRIVILEGES");
        TABLES_TO_SKIP.add("COLUMN_UDT_USAGE");
        TABLES_TO_SKIP.add("COLUMNS");
        TABLES_TO_SKIP.add("CONSTRAINT_COLUMN_USAGE");
        TABLES_TO_SKIP.add("CONSTRAINT_TABLE_USAGE");
        TABLES_TO_SKIP.add("DATA_TYPE_PRIVILEGES");
        TABLES_TO_SKIP.add("DOMAIN_CONSTRAINTS");
        TABLES_TO_SKIP.add("DOMAINS");
        TABLES_TO_SKIP.add("ENABLED_ROLES");
        TABLES_TO_SKIP.add("ELEMENT_TYPES");
        TABLES_TO_SKIP.add("INFORMATION_SCHEMA_CATALOG_NAME");
        TABLES_TO_SKIP.add("JARS");
        TABLES_TO_SKIP.add("JAR_JAR_USAGE");
        TABLES_TO_SKIP.add("KEY_COLUMN_USAGE");
        TABLES_TO_SKIP.add("LOBS");
        TABLES_TO_SKIP.add("LOB_IDS");
        TABLES_TO_SKIP.add("PARAMETERS");
        TABLES_TO_SKIP.add("REFERENTIAL_CONSTRAINTS");
        TABLES_TO_SKIP.add("ROLE_AUTHORIZATION_DESCRIPTORS");
        TABLES_TO_SKIP.add("ROLE_COLUMN_GRANTS");
        TABLES_TO_SKIP.add("ROLE_ROUTINE_GRANTS");
        TABLES_TO_SKIP.add("ROLE_TABLE_GRANTS");
        TABLES_TO_SKIP.add("ROLE_UDT_GRANTS");
        TABLES_TO_SKIP.add("ROLE_USAGE_GRANTS");
        TABLES_TO_SKIP.add("ROUTINE_COLUMN_USAGE");
        TABLES_TO_SKIP.add("ROUTINE_JAR_USAGE");
        TABLES_TO_SKIP.add("ROUTINE_PRIVILEGES");
        TABLES_TO_SKIP.add("ROUTINE_ROUTINE_USAGE");
        TABLES_TO_SKIP.add("ROUTINE_SEQUENCE_USAGE");
        TABLES_TO_SKIP.add("ROUTINE_TABLE_USAGE");
        TABLES_TO_SKIP.add("ROUTINES");
        TABLES_TO_SKIP.add("SCHEMATA");
        TABLES_TO_SKIP.add("SEQUENCES");
        TABLES_TO_SKIP.add("SQL_FEATURES");
        TABLES_TO_SKIP.add("SQL_IMPLEMENTATION_INFO");
        TABLES_TO_SKIP.add("SQL_PACKAGES");
        TABLES_TO_SKIP.add("SQL_PARTS");
        TABLES_TO_SKIP.add("SQL_SIZING");
        TABLES_TO_SKIP.add("SQL_SIZING_PROFILES");
        TABLES_TO_SKIP.add("SYSTEM_BESTROWIDENTIFIER");
        TABLES_TO_SKIP.add("SYSTEM_CACHEINFO");
        TABLES_TO_SKIP.add("SYSTEM_COLUMNS");
        TABLES_TO_SKIP.add("SYSTEM_COLUMN_SEQUENCE_USAGE");
        TABLES_TO_SKIP.add("SYSTEM_COMMENTS");
        TABLES_TO_SKIP.add("SYSTEM_CONNECTION_PROPERTIES");
        TABLES_TO_SKIP.add("SYSTEM_CROSSREFERENCE");
        TABLES_TO_SKIP.add("SYSTEM_INDEXINFO");
        TABLES_TO_SKIP.add("SYSTEM_PRIMARYKEYS");
        TABLES_TO_SKIP.add("SYSTEM_PROCEDURECOLUMNS");
        TABLES_TO_SKIP.add("SYSTEM_PROCEDURES");
        TABLES_TO_SKIP.add("SYSTEM_PROPERTIES");
        TABLES_TO_SKIP.add("SYSTEM_SCHEMAS");
        TABLES_TO_SKIP.add("SYSTEM_SEQUENCES");
        TABLES_TO_SKIP.add("SYSTEM_SESSIONINFO");
        TABLES_TO_SKIP.add("SYSTEM_SESSIONS");
        TABLES_TO_SKIP.add("SYSTEM_TABLES");
        TABLES_TO_SKIP.add("SYSTEM_TABLETYPES");
        TABLES_TO_SKIP.add("SYSTEM_TEXTTABLES");
        TABLES_TO_SKIP.add("SYSTEM_TYPEINFO");
        TABLES_TO_SKIP.add("SYSTEM_UDTS");
        TABLES_TO_SKIP.add("SYSTEM_USERS");
        TABLES_TO_SKIP.add("SYSTEM_VERSIONCOLUMNS");
        TABLES_TO_SKIP.add("TABLE_CONSTRAINTS");
        TABLES_TO_SKIP.add("TABLE_PRIVILEGES");
        TABLES_TO_SKIP.add("TABLES");
        TABLES_TO_SKIP.add("TRANSLATIONS");
        TABLES_TO_SKIP.add("TRIGGER_COLUMN_USAGE");
        TABLES_TO_SKIP.add("TRIGGER_ROUTINE_USAGE");
        TABLES_TO_SKIP.add("TRIGGER_SEQUENCE_USAGE");
        TABLES_TO_SKIP.add("TRIGGER_TABLE_USAGE");
        TABLES_TO_SKIP.add("TRIGGERED_UPDATE_COLUMNS");
        TABLES_TO_SKIP.add("TRIGGERS");
        TABLES_TO_SKIP.add("USAGE_PRIVILEGES");
        TABLES_TO_SKIP.add("USER_DEFINED_TYPES");
        TABLES_TO_SKIP.add("UDT_PRIVILEGES");
        TABLES_TO_SKIP.add("VIEW_COLUMN_USAGE");
        TABLES_TO_SKIP.add("VIEW_ROUTINE_USAGE");
        TABLES_TO_SKIP.add("VIEW_TABLE_USAGE");
        TABLES_TO_SKIP.add("VIEWS");
    }
    
    protected Filter<String> getTableDropFilter() {
        return new Filter<String>() {
            public boolean filter(String input) {
                return !TABLES_TO_SKIP.contains(input);
            }
        };
    }

    protected SqlDataSet getDataSet() {
        return HsqldbPackageTests.getDataSet((HsqldbPackageTests.HsqldbDataSetType) getTestData(HsqldbPackageTests.HSQLDB_DATA_SET_TYPE_KEY));
    }
}
