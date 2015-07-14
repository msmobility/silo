package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.util.array.ArrayUtil;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;

/**
 * @author crf <br/>
 *         Started: Sep 26, 2008 8:07:31 AM
 */
abstract public class AbstractDataTableTest extends DataTableTest {
    protected AbstractDataTable abstractTable;

    @Before
    public void beforeTest() {
        super.beforeTest();
        abstractTable = (AbstractDataTable) table;
    }

    public void testAddColumnToData() {
        Boolean[] newColumn = new Boolean[abstractTable.getRowCount()];
        String label = "__newBool";
        for (int i = 0; i < newColumn.length; i++)
            newColumn[i] = random.nextBoolean();
        int oldCount = schema.getColumnLabels().length;
        abstractTable.addColumnToData(label,newColumn, DataType.BOOLEAN);
        assertArrayEquals(newColumn,abstractTable.getBooleanColumn(oldCount).getData());
    }

    @Test
    public void testAddColumnToDataCount() {
        Boolean[] newColumn = new Boolean[abstractTable.getRowCount()];
        String label = "__newBool";
        for (int i = 0; i < newColumn.length; i++)
            newColumn[i] = random.nextBoolean();
        int oldCount = schema.getColumnLabels().length;
        abstractTable.addColumnToData(label,newColumn,DataType.BOOLEAN);
        assertEquals(oldCount+1,abstractTable.getColumnCount());
    }

    @Test
    public void testAddColumnToDataReturn() {
        Boolean[] newColumn = new Boolean[abstractTable.getRowCount()];
        String label = "__newBool";
        for (int i = 0; i < newColumn.length; i++)
            newColumn[i] = random.nextBoolean();
        assertTrue(abstractTable.addColumnToData(label,newColumn, DataType.BOOLEAN));
    }

    @Test
    public void testAddRowAbstract() {
        int randomIndex = random.nextInt(numberOfRows);
        abstractTable.addRow(table.getRowCount(),tableData[randomIndex]);
        assertArrayAlmostEquals(tableData[randomIndex],abstractTable.getRow(abstractTable.getRowCount()-1).getData());
    }

    @Test
    public void testAddRowAbstractReturn() {
        int randomIndex = random.nextInt(numberOfRows);
        assertTrue(abstractTable.addRow(abstractTable.getRowCount(),tableData[randomIndex]));
    }

    @Test
    public void deleteColumnFromData() {
        int randomRow = random.nextInt(abstractTable.getRowCount());
        Object newFirstElement = abstractTable.getRow(randomRow).getData()[1];
        abstractTable.deleteColumnFromData(0);
        assertEquals(newFirstElement,abstractTable.getCellValue(randomRow,0));
    }

    @Test
    public void deleteColumnFromDataCount() {
        int newSize = abstractTable.getColumnCount()-1;
        abstractTable.deleteColumnFromData(0);
        assertEquals(newSize,abstractTable.getColumnCount());
    }

    @Test
    public void deleteRowFromData() {
        int randomRow = random.nextInt(abstractTable.getRowCount());
        Object newFirstElement = abstractTable.getRow(randomRow+1).getData()[0];
        abstractTable.deleteRowFromData(randomRow);
        assertEquals(newFirstElement,abstractTable.getCellValue(randomRow,0));
    }

    @Test
    public void deleteRowFromDataCount() {
        int randomRow = random.nextInt(abstractTable.getRowCount());
        int newSize = abstractTable.getRowCount()-1;
        abstractTable.deleteRowFromData(randomRow);
        assertEquals(newSize,abstractTable.getRowCount());
    }

    @Test
    public void testSetCellValueInData() {
        int randomRow = random.nextInt(abstractTable.getRowCount());
        Float randomFloat = random.nextFloat();
        abstractTable.setCellValueInData(randomRow,getIndex(DataType.FLOAT),randomFloat);
        //assertEquals(randomFloat,abstractTable.getCellValue(randomRow,getIndexPoint(DataType.FLOAT)));
        assertAlmostEquals(randomFloat,abstractTable.getCellValue(randomRow,getIndex(DataType.FLOAT)));
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueInDataFailure() {
        int randomRow = random.nextInt(abstractTable.getRowCount());
        abstractTable.setCellValueInData(randomRow,getIndex(DataType.FLOAT),"");
    }

    @Test
    public void testCheckPrimitiveArrayType() {
        //do a bunch of checks here to be sure
        for (DataType type : DataType.values()) {
            assertTrue(abstractTable.checkPrimitiveArrayType(type.getPrimitiveArray(0),type));
            int badOrdinal = type.ordinal()-1;
            if (badOrdinal == -1)
                badOrdinal = 1;
            assertFalse(abstractTable.checkPrimitiveArrayType(DataType.values()[badOrdinal].getPrimitiveArray(0),type));
        }
    }

    @Test
    public void testCheckObjectArrayType() {
        //do a bunch of checks here to be sure
        for (DataType type : DataType.values()) {
            assertTrue(abstractTable.checkObjectArrayType(type.getObjectArray(0),type));
            int badOrdinal = type.ordinal()-1;
            if (badOrdinal == -1)
                badOrdinal = 1;
            assertFalse(abstractTable.checkObjectArrayType(DataType.values()[badOrdinal].getObjectArray(0),type));
        }
    }

    @Test(expected=IllegalArgumentException.class)
    public void testGetPrimitiveColumnNotArray() {
        abstractTable.getPrimitiveColumn("",DataType.BOOLEAN);
    }

    @Test(expected=TableDataException.class)
    public void testGetPrimitiveColumnWrongPrimitiveType() {
        abstractTable.getPrimitiveColumn(new int[9],DataType.BOOLEAN);
    }

    @Test(expected=TableDataException.class)
    public void testGetPrimitiveColumnWrongType() {
        abstractTable.getPrimitiveColumn(new Integer[9],DataType.BOOLEAN);
    }

    @Test
    public void testGetPrimitiveColumn() {
        Byte[] input = new Byte[] {1,2};
        byte[] primitive = ArrayUtil.toPrimitive(input);
        assertArrayEquals(primitive,(byte[]) abstractTable.getPrimitiveColumn(input,DataType.BYTE));
    }

    @Test
    public void testGetPrimitiveColumnPrimitive() {
        byte[] input = new byte[] {1,2};
        assertArrayEquals(input,(byte[]) abstractTable.getPrimitiveColumn(input,DataType.BYTE));
    }

    @Test
    public void testHasColumn() {
        int randomColumn = random.nextInt(abstractTable.getColumnCount());
        assertTrue(abstractTable.hasColumn(abstractTable.getColumnLabel(randomColumn)));
    }

    @Test
    public void testHasNoColumn() {
        assertFalse(abstractTable.hasColumn("not a column"));
    }

    @Test
    public void testBoundAndTypeCheckRowDataSuccess() {
        //no assertions, just no exceptions
        int randomRow = random.nextInt(abstractTable.getRowCount());
        abstractTable.boundAndTypeCheckRowData(abstractTable.getRow(randomRow).getData());
    }

    @Test(expected=TableDataException.class)
    public void testBoundAndTypeCheckRowDataWrongSizeSmall() {
        int randomRow = random.nextInt(abstractTable.getRowCount());
        Object[] data = new Object[abstractTable.getColumnCount()-1];
        System.arraycopy(abstractTable.getRow(randomRow).getData(),0,data,0,data.length);
        abstractTable.boundAndTypeCheckRowData(data);
    }

    @Test(expected=TableDataException.class)
    public void testBoundAndTypeCheckRowDataWrongSizeLarge() {
        int randomRow = random.nextInt(abstractTable.getRowCount());
        Object[] data = new Object[abstractTable.getColumnCount()+1];
        System.arraycopy(abstractTable.getRow(randomRow).getData(),0,data,0,data.length-1);
        data[data.length-1] = data[data.length-2];
        abstractTable.boundAndTypeCheckRowData(data);
    }

    @Test(expected=TableDataException.class)
    public void testBoundAndTypeCheckRowDataWrongType() {
        int randomRow = random.nextInt(abstractTable.getRowCount());
        Object[] data = ArrayUtil.copyArray(abstractTable.getRow(randomRow).getData());
        data[0] = data[1];
        abstractTable.boundAndTypeCheckRowData(data);
    }

    @Test
    @SuppressWarnings("unchecked") //ok because default key K = Integer by the documentation (so long as it hasn't been overridden)
    public void testGetDefaultPrimaryKey() {
        int randomRow = random.nextInt(abstractTable.getRowCount());
        TableKey<Integer> key = (TableKey<Integer>) abstractTable.getDefaultPrimaryKey();
        key.buildIndex();
        assertEquals(randomRow,(key.getRowNumber(randomRow)));
    }

    @Test
    public void testGetDataColumn() {
        //only test if data is correct, assume column object has already been tested
        int randomColumn = random.nextInt(abstractTable.getColumnCount());
        DataType type = schema.getColumnTypes()[randomColumn];
        assertArrayAlmostEquals(getColumnByType(type),abstractTable.getDataColumn(randomColumn,type).getData());
    }

    @Test
    public void testGetDataColumnByName() {
        //only test if data is correct, assume column object has already been tested
        int randomColumn = random.nextInt(abstractTable.getColumnCount());
        DataType type = schema.getColumnTypes()[randomColumn];
        assertArrayAlmostEquals(getColumnByType(type),abstractTable.getDataColumn(schema.getColumnLabels()[randomColumn],type).getData());
    }

}
