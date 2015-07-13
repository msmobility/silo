package com.pb.sawdust.tabledata;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.util.test.TestBase;

/**
 * @author crf <br/>
 *         Started: Sep 24, 2008 9:39:07 AM
 */
public abstract class DataRowTest extends TestBase {

    protected DataRow row;
    private int randomRow;
    private DataType[] columnTypes;
    private String[] columnLabels;
    private Object[] rowData;
    protected TableDataTestData testData = new TableDataTestData();

    abstract protected DataRow getDataRow(Object[] rowData);

    protected String[] getColumnLabels() {
        return testData.getColumnNames();
    }

    protected DataType[] getColumnTypes() {
        return testData.getColumnTypes();
    }

    protected int getNumberOfRows() {
        return 100;
    }

    protected Object[] getRowData() {
        return testData.getTableData(getNumberOfRows())[randomRow];
    }

    @Before
    public void beforeTest() {
        randomRow = random.nextInt(getNumberOfRows());
        rowData = getRowData();
        columnTypes = getColumnTypes();
        columnLabels = getColumnLabels();
        row = getDataRow(rowData);
    }

    @Test
    public void testGetColumnLabels() {
        assertArrayEquals(columnLabels,row.getColumnLabels());
    }

    @Test
    public void testGetColumnLabel() {
        int randomColumn = random.nextInt(columnLabels.length);
        assertEquals(columnLabels[randomColumn],row.getColumnLabel(randomColumn));
    }

    @Test(expected=TableDataException.class)
    public void testGetColumnLabelFailureLow() {
        row.getColumnLabel(-1);
    }

    @Test(expected=TableDataException.class)
    public void testGetColumnLabelFailureHigh() {
        row.getColumnLabel(columnLabels.length);
    }

    @Test
    public void testHasColumn() {
        assertTrue(row.hasColumn(columnLabels[random.nextInt(columnLabels.length)]));
    }

    @Test
    public void testHasNoColumn() {
        assertFalse(row.hasColumn("does not have this column"));
    }

    @Test
    public void testGetColumnIndex() {
        int randomColumn = random.nextInt(columnLabels.length);
        assertEquals(randomColumn,row.getColumnIndex(columnLabels[randomColumn]));
    }

    @Test(expected=TableDataException.class)
    public void testGetColumnIndexFailure() {
        row.getColumnIndex("not a column for sure");
    }

    @Test
    public void testGetColumnTypes() {
        assertArrayEquals(columnTypes,row.getColumnTypes());
    }

    @Test
    public void testGetColumnType() {
        int randomColumn = random.nextInt(columnLabels.length);
        assertEquals(columnTypes[randomColumn],row.getColumnType(randomColumn));
    }

    @Test(expected=TableDataException.class)
    public void testGetColumnTypeFailureLow() {
        row.getColumnType(-1);
    }

    @Test(expected= TableDataException.class)
    public void testGetColumnTypeFailureHigh() {
        row.getColumnType(columnLabels.length);
    }

    @Test
    public void testGetColumnTypeLabel() {
        int randomColumn = random.nextInt(columnLabels.length);
        assertEquals(columnTypes[randomColumn],row.getColumnType(columnLabels[randomColumn]));
    }

    @Test(expected=TableDataException.class)
    public void testGetColumnTypeLabelFailure() {
        row.getColumnType("certainly not a column");
    }

    @Test
    public void testGetRow() {
        assertArrayAlmostEquals(rowData,row.getData());
    }

    @Test
    public void testGetCell() {
        int randomColumn = random.nextInt(columnLabels.length);
        assertAlmostEquals(rowData[randomColumn],row.getCell(randomColumn));
    }

    @Test(expected=TableDataException.class)
    public void testGetCellFailureLow() {
        row.getCell(-1);
    }

    @Test(expected=TableDataException.class)
    public void testGetCellFailureHigh() {
        row.getCell(columnLabels.length);
    }

    @Test
    public void testGetCellLabel() {
        int randomColumn = random.nextInt(columnLabels.length);
        assertAlmostEquals(rowData[randomColumn],row.getCell(columnLabels[randomColumn]));
    }

    @Test(expected=TableDataException.class)
    public void testGetCellLabelFailure() {
        row.getCell("nope, not a column");
    }

}
