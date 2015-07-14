package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;

/**
 * @author crf <br/>
 *         Started: Sep 24, 2008 8:57:30 PM
 */
public abstract class DataSetTest<T extends DataTable> extends TestBase {

    protected DataSet<T> dataSet;
    protected TableDataTestData testData = new TableDataTestData();

    abstract protected DataSet<T> getDataSet();
    abstract protected T getDataTable(TableSchema schema, Object[][] data);

    protected T getDataTable(String tableLabel) {
        return getDataTable(getTableSchema(tableLabel),getTableData());
    }

    protected Object[][] getTableData() {
        return testData.getTableData(random.nextInt(10,100));
    }

    protected TableSchema getTableSchema(String tableLabel) {
        return new TableSchema(tableLabel,testData.getColumnNames(),testData.getColumnTypes());
    }

    @Before
    public void beforeTest() {
        dataSet = getDataSet();
    }

    @Test
    public void testHasNoTableEmpty() {
        assertFalse(dataSet.hasTable("no tables in set"));
    }

    @Test
    public void testTableEmpty() {
        assertEquals(0,dataSet.getTableLabels().size());
    }

    @Test
    public void testHasTable() {
        String tableLabel = "test";
        dataSet.addTable(getDataTable(tableLabel));
        assertTrue(dataSet.hasTable(tableLabel));
    }

    @Test
    public void testHasNoTable() {
        String tableLabel = "test";
        dataSet.addTable(getDataTable(tableLabel));
        assertFalse(dataSet.hasTable("not_test"));
    }

    @Test(expected=TableDataException.class)
    public void testAddTableFailure() {
        String tableLabel = "test";
        dataSet.addTable(getDataTable(tableLabel));
        dataSet.addTable(getDataTable(tableLabel));
    }

    @Test
    public void testAddTableLabels() {
        String tableLabel1 = "test";
        String tableLabel2 = "test2";
        Set<String> labels = new HashSet<String>();
        labels.add(tableLabel1);
        labels.add(tableLabel2);
        dataSet.addTable(getDataTable(tableLabel1));
        dataSet.addTable(getDataTable(tableLabel2));
        assertEquals(labels,dataSet.getTableLabels());
    }

    @Test
    public void testAddTableFromSchema() {
        String tableName = "testTable";
        dataSet.addTable(getTableSchema(tableName));
        assertTrue(dataSet.hasTable(tableName));
    }

    @Test(expected= TableDataException.class)
    public void testAddTableFromSchemaFailure() {
        String tableName = "testTable";
        dataSet.addTable(getTableSchema(tableName));
        dataSet.addTable(getTableSchema(tableName));
    }

    @Test
    public void testDropTable() {
        String tableLabel = "test";
        dataSet.addTable(getDataTable(tableLabel));
        dataSet.dropTable(tableLabel);
        assertFalse(dataSet.hasTable(tableLabel));
    }

    @Test(expected=TableDataException.class)
    public void testDropTableFailure() {
        String tableLabel = "test";
       dataSet.dropTable(tableLabel);
    }

    @Test
    public void testGetTableSchema() {
        String tableName = "test";
        TableSchema schema = getTableSchema(tableName);
        dataSet.addTable(schema);
        assertEquals(schema,dataSet.getTableSchema(tableName));
    }

    @Test
    public void testGetTableSchemaAddTable() {
        String tableName = "test";
        T table = getDataTable(tableName);
        dataSet.addTable(table);
        assertEquals(table.getSchema(),dataSet.getTableSchema(tableName));
    }

    @Test(expected=TableDataException.class)
    public void testGetTableSchemaFailure() {
        dataSet.getTableSchema("not_a_table");
    }

    @Ignore
    @Test
    public void testGetTable() {
        String tableName = "test";
        T table = getDataTable(tableName);
        dataSet.addTable(table);
        assertEquals(table,dataSet.getTable(tableName));
    }

    @Test(expected=TableDataException.class)
    public void testGetTableFailure() {
        getDataSet().getTable("not_a_table");
    }

    @Ignore
    @Test
    public void testVerifySchema() {
        //todo: this
        //what to do here??
    }

}
