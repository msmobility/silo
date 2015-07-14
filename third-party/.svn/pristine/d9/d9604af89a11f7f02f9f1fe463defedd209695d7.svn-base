package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.pb.sawdust.util.Range.*;

/**
 * The {@code DataTablePartitionTest} ...
 *
 * @author crf <br/>
 *         Started 3/11/11 9:02 PM
 */
public class DataTablePartitionTest extends DataTableTest {

    public static void main(String ... args) {
        TestBase.main();
    }

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        //remove datapartition tests to avoid recursion overflows
        Collection<Class<? extends TestBase>> c = super.getAdditionalTestClasses();
        List<Class<? extends TestBase>> cc = new LinkedList<Class<? extends TestBase>>();
        for (Class<? extends TestBase> cl : c)
            if (!DataTablePartitionTest.class.isAssignableFrom(cl))
                cc.add(cl);
        return cc;
    }

    protected Object[][] builtOutTableData(Object[][] tableData, int paddingAbove, int paddingBelow) {
        int paddingRow = random.nextInt(tableData.length);
        Object[][] builtOutData = new Object[tableData.length+paddingAbove+paddingBelow][];
        for (int r : range(paddingAbove))
            builtOutData[r] = tableData[paddingRow];
        for (int r : range(paddingAbove,paddingAbove+tableData.length))
            builtOutData[r] = tableData[r-paddingAbove];
        for (int r : range(paddingAbove+tableData.length,builtOutData.length))
            builtOutData[r] = tableData[paddingRow];
        return builtOutData;
    }

    @Override
    protected DataTable getDataTable(Object[][] tableData, TableSchema schema) {
        int paddingAbove = random.nextInt(10,50);
        int paddingBelow = random.nextInt(10,50);
        Object[][] paddedData = builtOutTableData(tableData,paddingAbove,paddingBelow);
        DataTable table = getParentDataTable(paddedData,schema);
        return table.getTablePartition(paddingAbove,paddingAbove+tableData.length);
    }

    //can be overridden to get a test which can be run in isolation (as opposed to being from a TableDataTest class context)
    protected DataTable getParentDataTable(Object[][] tableData, TableSchema schema) {
        return ((DataTableTest)  getCallingContextInstance()).getDataTable(tableData,schema);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddRow() {
        super.testAddRow();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddRowReturn() {
        super.testAddRowReturn();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddRowCount() {
        super.testAddRowCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddDataRow() {
        super.testAddDataRow();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddDataRowCount() {
        super.testAddDataRowCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddDataByRows() {
        super.testAddDataByRows();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddDataByRowsCount() {
        super.testAddDataByRowsCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddColumn() {
        super.testAddColumn();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddColumnReturn() {
        super.testAddColumnReturn();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddColumnCount() {
        super.testAddColumnCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddColumnLabel() {
        super.testAddColumnLabel();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddDataByColumns() {
        super.testAddDataByColumns();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddDataByColumnsCount() {
        super.testAddDataByColumnsCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddDataByColumnsPrimitive() {
        super.testAddDataByColumnsPrimitive();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAddDataRowsCount() {
        super.testAddDataRowsCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetRowData() {
        super.testSetRowData();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetRowDataWrongDataType() {
        super.testSetRowDataWrongDataType();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetRowDataByKey() {
        super.testSetRowDataByKey();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetRowDataByKeyColumn() {
        super.testSetRowDataByKeyColumn();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetColumnData() {
        super.testSetColumnData();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetColumnDataLabel() {
        super.testSetColumnDataLabel();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetCellValue() {
        super.testSetCellValue();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetCellValueInvalidData() {
        super.testSetCellValueInvalidData();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetCellValueLabel() {
        super.testSetCellValueLabel();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetCellValueLabelInvalidData() {
        super.testSetCellValueLabelInvalidData();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetCellValueByKey() {
        super.testSetCellValueByKey();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetCellValueByKeyInvalidData() {
        super.testSetCellValueByKeyInvalidData();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetCellValueLabelByKey() {
        super.testSetCellValueLabelByKey();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testSetCellValueLabelByKeyInvalidData() {
        super.testSetCellValueLabelByKeyInvalidData();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteRowReturn() {
        super.testDeleteRowReturn();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteRowCount() {
        super.testDeleteRowCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteRowByKeyReturn() {
        super.testDeleteRowByKeyReturn();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteRowByKeyCount() {
        super.testDeleteRowByKeyCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteColumnReturn() {
        super.testDeleteColumnReturn();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteColumnCount() {
        super.testDeleteColumnCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteColumnLabel() {
        super.testDeleteColumnLabel();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteColumnSchemaCount() {
        super.testDeleteColumnSchemaCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteColumnSchemaLabel() {
        super.testDeleteColumnSchemaLabel();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteColumnLabelReturn() {
        super.testDeleteColumnLabelReturn();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteColumnLabelCount() {
        super.testDeleteColumnLabelCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteColumnLabelLabel() {
        super.testDeleteColumnLabelLabel();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteColumnLabelSchemaCount() {
        super.testDeleteColumnLabelSchemaCount();
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testDeleteColumnLabelSchemaLabel() {
        super.testDeleteColumnLabelSchemaLabel();
    }



//    @Test@Ignore
//    public void testGetCellValueFailure1() {}
//
//    @Test@Ignore
//    public void testGetCellValueLabelFailure1() {}


    @Test@Ignore //can't modify data to be non-unique
    public void testSetPrimaryKeyFailureNonUnique() {}
}
