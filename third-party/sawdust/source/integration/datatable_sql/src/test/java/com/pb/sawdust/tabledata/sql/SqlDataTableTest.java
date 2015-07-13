package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.tabledata.AbstractDataTableTest;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.TableDataException;
import com.pb.sawdust.tabledata.basic.ListDataTable;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Sep 28, 2008 5:10:56 PM
 */
abstract public class SqlDataTableTest extends AbstractDataTableTest {
    protected SqlDataTable sqlTable;

    protected abstract SqlDataSet getSqlDataSet();

    protected Class<? extends TestBase> getDataTablePartitionTestClass() {
        return SqlDataTablePartitionTest.class;
    }

    protected void dropTableFromDataSet(TableSchema schema) {
        SqlDataSet sqlDataSet = getSqlDataSet();
        //sqlDataSet.executeSqlUpdate("DROP TABLE IF EXISTS " + SqlTableDataUtil.formQuotedIdentifier(schema.getTableLabel(),sqlDataSet.getIdentifierQuote()));
        dropTableIfExistsFromDatabase(schema.getTableLabel(),sqlDataSet);
    }

    protected void dropTableIfExistsFromDatabase(String tableName, SqlDataSet sqlDataSet) {
        sqlDataSet.executeSqlUpdate("DROP TABLE IF EXISTS " + SqlTableDataUtil.formQuotedIdentifier(tableName,sqlDataSet.getIdentifierQuote()));
    }

    protected DataTable getDataTable(Object[][] tableData, TableSchema schema) {
        dropTableFromDataSet(schema);
        SqlDataSet sqlDataSet = getSqlDataSet();
        sqlDataSet.addTable(new SqlTableSchema(schema)).addDataByRow(tableData);
        return new SqlDataTable(sqlDataSet,schema.getTableLabel());
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        sqlTable = (SqlDataTable) abstractTable;
    }

    @Ignore
    @Test
    public void testAddColumnToDataCount() {
        //no way to check count the way that it is calculated
    }

    @Ignore
    @Test
    public void deleteColumnFromDataCount() {
        //no way to check count the way that it is calculated
    }

    @Test
    public void deleteColumnFromData() {
        //data query will include old column
    }

    @Ignore
    @Test
    public void deleteRowFromData() {
        //now way to check deleted row in implementation
    }

    @Test
    public void testGetSchema() {
        assertEquals(new SqlTableSchema(schema),table.getSchema());
    }

    @Test(expected=TableDataException.class)
    public void testConstructorSqlFilter() {
        SqlTableSchema sqlSchema = new SqlTableSchema(schema);
        dropTableFromDataSet(sqlSchema);
        SqlDataTable table = new SqlDataTable(getSqlDataSet(),sqlSchema.getTableLabel(),
                new Filter<String>() {
                    public boolean filter(String input) {
                        return false;
                }
            });
        table.executeSqlUpdate("SELECT * FROM " + SqlTableDataUtil.formQuotedIdentifier(getSqlDataSet().getIdentifierQuote(),schema.getTableLabel()));
    }

    @Test
    public void testConstructorSqlFilterData() {
        getDataTable(getTableData(),schema);
        SqlDataSet t = getSqlDataSet();
        SqlTableSchema sqlSchema = new SqlTableSchema(schema);
        SqlDataTable table = new SqlDataTable(t,sqlSchema.getTableLabel(),
                new Filter<String>() {
                    public boolean filter(String input) {
                        return false;
                }
            });
        table.updateColumnTypes(schema.getColumnTypes());
        int randomRow = random.nextInt(table.getRowCount());
        assertArrayAlmostEquals(getTableData()[randomRow],table.getRow(randomRow).getData());
    }

    @Test
    public void testConstructorSchema() {
        SqlTableSchema sqlSchema = new SqlTableSchema(schema);
        dropTableFromDataSet(sqlSchema);
        SqlDataTable table = new SqlDataTable(getSqlDataSet(),sqlSchema);
        assertEquals(0,table.getRowCount());
    }

    @Test(expected=TableDataException.class)
    public void testConstructorSchemaFailure() {
        SqlTableSchema sqlSchema = new SqlTableSchema(schema);
        new SqlDataTable(getSqlDataSet(),sqlSchema);
    }

    @Test
    public void testConstructorSchemaData() {
        SqlTableSchema sqlSchema = new SqlTableSchema(schema);
        dropTableFromDataSet(sqlSchema);
        SqlDataTable table = new SqlDataTable(getSqlDataSet(),sqlSchema,getTableData());
        int randomRow = random.nextInt(table.getRowCount());
        assertArrayAlmostEquals(getTableData()[randomRow],table.getRow(randomRow).getData());
    }

    @Test(expected=TableDataException.class)
    public void testConstructorSchemaDataFailure() {
        SqlTableSchema sqlSchema = new SqlTableSchema(schema);
        new SqlDataTable(getSqlDataSet(),sqlSchema,getTableData());
    }

    @Test
    public void testConstructorAnotherTable() {
        dropTableFromDataSet(schema);
        ListDataTable newTable = new ListDataTable(schema,getTableData());
        SqlDataTable table = new SqlDataTable(getSqlDataSet(),newTable);
        int randomRow = random.nextInt(table.getRowCount());
        assertArrayAlmostEquals(getTableData()[randomRow],table.getRow(randomRow).getData());
    }

    @Test(expected=TableDataException.class)
    public void testConstructorAnotherTableFailure() {
        new SqlDataTable(getSqlDataSet(),new ListDataTable(schema,getTableData()));
    }
    
    //todo: test setRowNumberKey
    //todo: test getSimpleRowNumberKey
    //todo: test setSqlFilter
    //todo: test executeSqlQuery/update
    //todo: test orderBy(default)
    //todo: test formRowEntry
    
}
