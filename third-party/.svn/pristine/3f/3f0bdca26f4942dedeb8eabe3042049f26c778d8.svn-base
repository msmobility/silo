package com.pb.sawdust.tabledata;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.pb.sawdust.tabledata.util.TableDataFactory;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.basic.BasicTableIndex;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.test.TestBase;
import static com.pb.sawdust.util.Range.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author crf <br/>
 *         Started: Sep 24, 2008 10:12:58 AM
 */
public abstract class DataTableTest extends TestBase {
    //todo: coersion tests

    protected String tableLabel;
    protected TableSchema schema;
    protected Object[][] tableData;
    protected int numberOfRows;
    protected int integerKeyColumnIndex;
    protected String integerKeyColumnLabel;
    protected TableKey<Integer> tableKey;
    protected DataTable table;
    protected TableDataTestData testData = new TableDataTestData();

    protected abstract DataTable getDataTable(Object[][] tableData, TableSchema schema);

    protected Collection<Class<? extends TestBase>> getAdditionalTestClasses() {
        List<Class<? extends TestBase>> additionalTestClasses = new LinkedList<Class<? extends TestBase>>();
        additionalTestClasses.addAll(super.getAdditionalTestClasses());
        additionalTestClasses.add(getDataTablePartitionTestClass());
        return additionalTestClasses;
    }

    protected Class<? extends TestBase> getDataTablePartitionTestClass() {
        return DataTablePartitionTest.class;
    }

    protected TableSchema getTableSchema() {
        return new TableSchema(getTableLabel(),testData.getColumnNames(),testData.getColumnTypes());
    }

    protected int getNumberOfRows() {
        return 100;
    }

    protected Object[][] getTableData() {
        return testData.getTableData(getNumberOfRows());
    }

    protected String getTableLabel() {
        return "test_table";
    }

    protected int getIntegerKeyColumnIndex() {
        return testData.getColumnOrdinal(DataType.INT);
    }

    protected String getIntegerKeyColumnLabel() {
        return testData.getColumnName(DataType.INT);
    }

    protected TableKey<Integer> getTableKey() {
        TableKey<Integer> key = testData.getSimpleKey(getNumberOfRows());
        key.buildIndex();
        return key;
    }

    protected int getUnusedKeyInteger() {
        return testData.getUnusedKeyInteger();
    }

    protected TableIndex<Object> getTableIndex() {
        return new BasicTableIndex<Object>(table,testData.getColumnName(DataType.BOOLEAN),testData.getColumnName(DataType.BYTE));
    }

    protected Object[] getColumnByType(DataType type) {
        switch (type) {
            case BOOLEAN : return testData.getBooleanColumnData(getNumberOfRows());
            case BYTE : return testData.getByteColumnData(getNumberOfRows());
            case DOUBLE : return testData.getDoubleColumnData(getNumberOfRows());
            case FLOAT : return testData.getFloatColumnData(getNumberOfRows());
            case INT : return testData.getIntegerColumnData(getNumberOfRows());
            case LONG : return testData.getLongColumnData(getNumberOfRows());
            case SHORT : return testData.getShortColumnData(getNumberOfRows());
            case STRING : return testData.getStringColumnData(getNumberOfRows());
            default : return null;
        }
    }

    private Boolean[] getBooleanColumnData() {
        return (Boolean[]) getColumnByType(DataType.BOOLEAN);
    }

    private Byte[] getByteColumnData() {
        return (Byte[]) getColumnByType(DataType.BYTE);
    }

    private Double[] getDoubleColumnData() {
        return (Double[]) getColumnByType(DataType.DOUBLE);
    }

    private Float[] getFloatColumnData() {
        return (Float[]) getColumnByType(DataType.FLOAT);
    }

    private Integer[] getIntegerColumnData() {
        return (Integer[]) getColumnByType(DataType.INT);
    }

    private Long[] getLongColumnData() {
        return (Long[]) getColumnByType(DataType.LONG);
    }

    private Short[] getShortColumnData() {
        return (Short[]) getColumnByType(DataType.SHORT);
    }

    private String[] getStringColumnData() {
        return (String[]) getColumnByType(DataType.STRING);
    }

    @Before
    public void beforeTest() {
        numberOfRows = getNumberOfRows();
        tableData = getTableData();
        tableLabel = getTableLabel();
        schema = getTableSchema();
        integerKeyColumnIndex = getIntegerKeyColumnIndex();
        integerKeyColumnLabel = getIntegerKeyColumnLabel();
        table = getDataTable(tableData,schema);
        tableKey = getTableKey();
    }

    @Test
    public void testGetLabel() {
        assertEquals(tableLabel,table.getLabel());
    }

    @Test
    public void testGetRowCount() {
        assertEquals(numberOfRows,table.getRowCount());
    }

    @Test
    public void testGetColumnCount() {
        assertEquals(schema.getColumnTypes().length,table.getColumnCount());
    }

    @Test
    public void testGetColumnLabels() {
        assertArrayEquals(schema.getColumnLabels(),table.getColumnLabels());
    }

    @Test
    public void testGetColumnTypes() {
        assertArrayEquals(schema.getColumnTypes(),table.getColumnTypes());
    }

    @Test
    public void testGetSchema() {
        assertEquals(schema,table.getSchema());
    }

    @Test
    public void testDefaultPrimaryKey() {
        int randomKey = random.nextInt(numberOfRows);
        assertEquals(randomKey,table.getPrimaryKey().getRowNumber(randomKey));
    }

    @Test
    public void testGetRowByDefaultKey() {
        int randomRow = random.nextInt(numberOfRows);
        assertArrayAlmostEquals(tableData[randomRow],table.getRowByKey(randomRow).getData());
    }

    @Test
    public void testSetPrimaryKey() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        table.setPrimaryKey(tableKey);
        assertEquals(tableKey.getRowNumber(randomKey),table.getPrimaryKey().getRowNumber(randomKey));
    }

    @Test
    public void testSetPrimaryKeyByColumn() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        table.setPrimaryKey(integerKeyColumnLabel);
        assertEquals(tableKey.getRowNumber(randomKey),table.getPrimaryKey().getRowNumber(randomKey));
    }

    @Test
    public void testSetPrimaryKeyColumnDoesntExist() {
        assertFalse(table.setPrimaryKey("not a column"));
    }

    @Test(expected=TableDataException.class)
    public void testSetPrimaryKeyFailureNonUnique() {
        table.addRow(tableData[0]);
        table.setPrimaryKey(integerKeyColumnLabel);
    }

    @Test
    public void testGetRowByKey() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        table.setPrimaryKey(tableKey);
        assertArrayAlmostEquals(tableData[tableKey.getRowNumber(randomKey)],table.getRowByKey(randomKey).getData());
    }

    @Test(expected=TableDataException.class)
    public void testGetRowByInvalidKey() {
        table.getRowByKey(numberOfRows + 100);
    }

    @Test
    public void testGetIndexedRowsCount() {
        TableIndex<Object> index = getTableIndex();
        Object[][] uniqueIndices =  index.getUniqueValues();
        Object[] ind = uniqueIndices[random.nextInt(uniqueIndices.length)];
        assertEquals(index.getRowNumbers(ind).size(),table.getIndexedRows(index,ind).getRowCount());
    }

    @Test
    public void testGetIndexedRowsData() {
        TableIndex<Object> index = getTableIndex();
        Object[][] uniqueIndices =  index.getUniqueValues();
        Object[] ind = uniqueIndices[random.nextInt(uniqueIndices.length)];
        int rowNumber = index.getRowNumbers(ind).iterator().next();
        Object[] rowData = table.getRow(rowNumber).getData();
        int uniqueColumnIndex = getIntegerKeyColumnIndex();
        for (DataRow row : table.getIndexedRows(index,ind)) {
            if (row.getCell(uniqueColumnIndex) == rowData[uniqueColumnIndex]) {
                //assertArrayEquals(rowData,row.getData());
                assertArrayAlmostEquals(rowData,row.getData());
                break;
            }
        }
    }

    @Test
    public void testAddRow() {
        int randomIndex = random.nextInt(numberOfRows);
        table.addRow(tableData[randomIndex]);
        assertArrayAlmostEquals(tableData[randomIndex],table.getRow(table.getRowCount()-1).getData());
    }

    @Test
    public void testAddRowReturn() {
        int randomIndex = random.nextInt(numberOfRows);
        assertTrue(table.addRow(tableData[randomIndex]));
    }

    @Test
    public void testAddRowCount() {
        int randomIndex = random.nextInt(numberOfRows);
        table.addRow(tableData[randomIndex]);
        assertEquals(numberOfRows+1,table.getRowCount());
    }

    @Test
    public void testAddDataRow() {
        int randomIndex = random.nextInt(numberOfRows);
        table.addRow(table.getRow(randomIndex));
        assertArrayAlmostEquals(table.getRow(randomIndex).getData(),table.getRow(table.getRowCount()-1).getData());
    }

    @Test
    public void testAddDataRowCount() {
        int randomIndex = random.nextInt(numberOfRows);
        table.addRow(table.getRow(randomIndex));
        assertEquals(numberOfRows+1,table.getRowCount());
    }

    @Test
    public void testAddDataByRows() {
        Object[][] newRows = new Object[2][];
        int randomIndex1 = random.nextInt(numberOfRows);
        int randomIndex2 = random.nextInt(numberOfRows);
        newRows[0] = tableData[randomIndex1];
        newRows[1] = tableData[randomIndex2];
        table.addDataByRow(newRows);
        assertArrayAlmostEquals(tableData[randomIndex1],table.getRow(table.getRowCount()-2).getData());
    }

    @Test
    public void testAddDataByRowsCount() {
        Object[][] newRows = new Object[2][];
        int randomIndex1 = random.nextInt(numberOfRows);
        int randomIndex2 = random.nextInt(numberOfRows);
        newRows[0] = tableData[randomIndex1];
        newRows[1] = tableData[randomIndex2];
        table.addDataByRow(newRows);
        assertEquals(numberOfRows+2,table.getRowCount());
    }

    @Test
    public void testAddColumn() {
        Boolean[] newColumn = new Boolean[table.getRowCount()];
        String label = "__newBool";
        for (int i = 0; i < newColumn.length; i++)
            newColumn[i] = random.nextBoolean();
        int oldCount = schema.getColumnLabels().length;
        table.addColumn(label,newColumn, DataType.BOOLEAN);
        assertArrayEquals(newColumn,table.getBooleanColumn(oldCount).getData());
    }   

    @Test
    public void testAddColumnReturn() {
        Boolean[] newColumn = new Boolean[table.getRowCount()];
        String label = "__newBool";
        for (int i = 0; i < newColumn.length; i++)
            newColumn[i] = random.nextBoolean();
        assertTrue(table.addColumn(label,newColumn, DataType.BOOLEAN));
    }

    @Test
    public void testAddColumnCount() {
        Boolean[] newColumn = new Boolean[table.getRowCount()];
        String label = "__newBool";
        for (int i = 0; i < newColumn.length; i++)
            newColumn[i] = random.nextBoolean();
        int oldCount = schema.getColumnLabels().length;
        table.addColumn(label,newColumn,DataType.BOOLEAN);
        assertEquals(oldCount+1,table.getColumnCount());
    }

    @Test
    public void testAddColumnLabel() {
        Boolean[] newColumn = new Boolean[table.getRowCount()];
        String label = "__newBool";
        for (int i = 0; i < newColumn.length; i++)
            newColumn[i] = random.nextBoolean();
        table.addColumn(label,newColumn,DataType.BOOLEAN);
        assertEquals(label,table.getColumnLabels()[table.getColumnCount()-1]);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddColumnFailure() {
        String label = "__newBool";
        table.addColumn(label,1,DataType.BOOLEAN);
    }

    @Test(expected=TableDataException.class)
    public void testAddColumnWrongType() {
        Boolean[] newColumn = new Boolean[table.getRowCount()];
        String label = "__newBool";
        for (int i = 0; i < newColumn.length; i++)
            newColumn[i] = random.nextBoolean();
        table.addColumn(label,newColumn,DataType.BYTE);
    }

    private Object formColumnData(DataType type, Object ... data) {
        switch (type) {
            case BOOLEAN : {
                Boolean[] array = new Boolean[data.length];
                int counter = 0;
                for (Object d : data) {
                    array[counter++] = (Boolean) d;
                }
                return array;
            }
            case BYTE : {
                Byte[] array = new Byte[data.length];
                int counter = 0;
                for (Object d : data) {
                    array[counter++] = (Byte) d;
                }
                return array;
            }
            case SHORT : {
                Short[] array = new Short[data.length];
                int counter = 0;
                for (Object d : data) {
                    array[counter++] = (Short) d;
                }
                return array;
            }
            case INT : {
                Integer[] array = new Integer[data.length];
                int counter = 0;
                for (Object d : data) {
                    array[counter++] = (Integer) d;
                }
                return array;
            }
            case LONG : {
                Long[] array = new Long[data.length];
                int counter = 0;
                for (Object d : data) {
                    array[counter++] = (Long) d;
                }
                return array;
            }
            case FLOAT : {
                Float[] array = new Float[data.length];
                int counter = 0;
                for (Object d : data) {
                    array[counter++] = (Float) d;
                }
                return array;
            }
            case DOUBLE : {
                Double[] array = new Double[data.length];
                int counter = 0;
                for (Object d : data) {
                    array[counter++] = (Double) d;
                }
                return array;
            }
            case STRING : {
                String[] array = new String[data.length];
                int counter = 0;
                for (Object d : data) {
                    array[counter++] = (String) d;
                }
                return array;
            }
            default : return null;
        }
    }

    @Test
    public void testAddDataByColumns() {
        Object[] newData = new Object[schema.getColumnLabels().length];
        int randomIndex1 = random.nextInt(numberOfRows);
        int randomIndex2 = random.nextInt(numberOfRows);
        for (int i = 0; i < newData.length; i++)
            newData[i] = formColumnData(schema.getColumnTypes()[i],tableData[randomIndex1][i],tableData[randomIndex2][i]);
        table.addDataByColumn(newData);
        assertArrayAlmostEquals(tableData[randomIndex1],table.getRow(table.getRowCount()-2).getData());
    }

    @Test
    public void testAddDataByColumnsCount() {
        Object[] newData = new Object[schema.getColumnLabels().length];
        int randomIndex1 = random.nextInt(numberOfRows);
        int randomIndex2 = random.nextInt(numberOfRows);
        for (int i = 0; i < newData.length; i++)
            newData[i] = formColumnData(schema.getColumnTypes()[i],tableData[randomIndex1][i],tableData[randomIndex2][i]);
        table.addDataByColumn(newData);
        assertEquals(numberOfRows+2,table.getRowCount());
    }

    @Test
    public void testAddDataByColumnsPrimitive() {
        Object[] newData = new Object[schema.getColumnLabels().length];
        int randomIndex1 = random.nextInt(numberOfRows);
        int randomIndex2 = random.nextInt(numberOfRows);
        for (int i = 0; i < newData.length; i++)
            newData[i] = formPrimitiveColumnData(schema.getColumnTypes()[i],tableData[randomIndex1][i],tableData[randomIndex2][i]);
        table.addDataByColumn(newData);
        assertArrayAlmostEquals(tableData[randomIndex1],table.getRow(table.getRowCount()-2).getData());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddDataByColumnsNotArray() {
        Object[] newData = new Object[schema.getColumnLabels().length];
        int randomIndex1 = random.nextInt(numberOfRows);
        int randomIndex2 = random.nextInt(numberOfRows);
        for (int i = 0; i < newData.length; i++)
            newData[i] = formColumnData(schema.getColumnTypes()[i],tableData[randomIndex1][i],tableData[randomIndex2][i]);
        //throw some invalid data into the new data
        newData[0] = 0;
        table.addDataByColumn(newData);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testAddDataByColumnsRaggedArray() {
        Object[] newData = new Object[schema.getColumnLabels().length];
        int randomIndex1 = random.nextInt(numberOfRows);
        int randomIndex2 = random.nextInt(numberOfRows);
        for (int i = 0; i < newData.length; i++)
            newData[i] = formColumnData(schema.getColumnTypes()[i],tableData[randomIndex1][i],tableData[randomIndex2][i]);
        Object raggedData = Array.newInstance(newData[0].getClass().getComponentType(),Array.getLength(newData[0])-1);
        newData[0] = raggedData;
        table.addDataByColumn(newData);
    }

    @Test(expected=TableDataException.class)
    public void testAddDataByColumnsWrongColumnCount() {
        Object[] newData = new Object[schema.getColumnLabels().length-1];
        int randomIndex1 = random.nextInt(numberOfRows);
        int randomIndex2 = random.nextInt(numberOfRows);
        for (int i = 0; i < newData.length; i++)
            newData[i] = formColumnData(schema.getColumnTypes()[i],tableData[randomIndex1][i],tableData[randomIndex2][i]);
        table.addDataByColumn(newData);
    }

    @Test(expected=TableDataException.class)
    public void testAddDataByColumnsWrongColumnType() {
        Object[] newData = new Object[schema.getColumnLabels().length];
        int randomIndex1 = random.nextInt(numberOfRows);
        int randomIndex2 = random.nextInt(numberOfRows);
        //throw some invalid data into the new data
        for (int i = 0; i < newData.length; i++)
            newData[i] = formColumnData(schema.getColumnTypes()[0],tableData[randomIndex1][0],tableData[randomIndex2][0]);
        table.addDataByColumn(newData);
    }

    private Object formPrimitiveColumnData(DataType type, Object ... data) {
        Object array = formColumnData(type,data);
        switch (type) {
            case BOOLEAN : return ArrayUtil.toPrimitive((Boolean[]) array);
            case BYTE : return ArrayUtil.toPrimitive((Byte[]) array);
            case SHORT : return ArrayUtil.toPrimitive((Short[]) array);
            case INT : return ArrayUtil.toPrimitive((Integer[]) array);
            case LONG : return ArrayUtil.toPrimitive((Long[]) array);
            case FLOAT : return ArrayUtil.toPrimitive((Float[]) array);
            case DOUBLE : return ArrayUtil.toPrimitive((Double[]) array);
            case STRING : return array;
            default : return null;
        }
    }

    @Test
    public void testAddDataRowsCount() {
        Object[][] newRows = new Object[2][];
        int randomIndex1 = random.nextInt(numberOfRows);
        int randomIndex2 = random.nextInt(numberOfRows);
        newRows[0] = tableData[randomIndex1];
        newRows[1] = tableData[randomIndex2];
        table.addDataByRow(newRows);
        assertEquals(numberOfRows+2,table.getRowCount());
    }

    @Test
    public void testGetRowData() {
        int randomIndex = random.nextInt(numberOfRows);
        assertArrayAlmostEquals(tableData[randomIndex],table.getRow(randomIndex).getData());
    }

    @Test
    public void testGetRowLabels() {
        int randomIndex = random.nextInt(numberOfRows);
        assertArrayAlmostEquals(schema.getColumnLabels(),table.getRow(randomIndex).getColumnLabels());
    }

    @Test
    public void testGetRowTypes() {
        int randomIndex = random.nextInt(numberOfRows);
        assertArrayAlmostEquals(schema.getColumnTypes(),table.getRow(randomIndex).getColumnTypes());
    }

    @Test
    public void testGetBooleanColumn() {
        assertArrayEquals(getBooleanColumnData(),table.getBooleanColumn(getIndex(DataType.BOOLEAN)).getData());
    }

    @Test
    public void testGetBooleanColumnLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.BOOLEAN)],table.getBooleanColumn(getIndex(DataType.BOOLEAN)).getLabel());
    }

    @Test
    public void testGetBooleanColumnByName() {
        assertArrayEquals(getBooleanColumnData(),table.getBooleanColumn(schema.getColumnLabels()[getIndex(DataType.BOOLEAN)]).getData());
    }

    @Test
    public void testGetBooleanColumnByNameLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.BOOLEAN)],table.getBooleanColumn(schema.getColumnLabels()[getIndex(DataType.BOOLEAN)]).getLabel());
    }

    @Test
    public void testGetByteColumn() {
        assertArrayEquals(getByteColumnData(),table.getByteColumn(getIndex(DataType.BYTE)).getData());
    }

    @Test
    public void testGetByteColumnLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.BYTE)],table.getByteColumn(getIndex(DataType.BYTE)).getLabel());
    }

    @Test
    public void testGetByteColumnByName() {
        assertArrayEquals(getByteColumnData(),table.getByteColumn(schema.getColumnLabels()[getIndex(DataType.BYTE)]).getData());
    }

    @Test
    public void testGetByteColumnByNameLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.BYTE)],table.getByteColumn(schema.getColumnLabels()[getIndex(DataType.BYTE)]).getLabel());
    }

    @Test
    public void testGetShortColumn() {
        assertArrayEquals(getShortColumnData(),table.getShortColumn(getIndex(DataType.SHORT)).getData());
    }

    @Test
    public void testGetShortColumnLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.SHORT)],table.getShortColumn(getIndex(DataType.SHORT)).getLabel());
    }

    @Test
    public void testGetShortColumnByName() {
        assertArrayEquals(getShortColumnData(),table.getShortColumn(schema.getColumnLabels()[getIndex(DataType.SHORT)]).getData());
    }

    @Test
    public void testGetShortColumnByNameLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.SHORT)],table.getShortColumn(schema.getColumnLabels()[getIndex(DataType.SHORT)]).getLabel());
    }

    @Test
    public void testGetIntegerColumn() {
        assertArrayEquals(getIntegerColumnData(),table.getIntColumn(getIndex(DataType.INT)).getData());
    }

    @Test
    public void testGetIntegerColumnLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.INT)],table.getIntColumn(getIndex(DataType.INT)).getLabel());
    }

    @Test
    public void testGetIntegerColumnByName() {
        assertArrayEquals(getIntegerColumnData(),table.getIntColumn(schema.getColumnLabels()[getIndex(DataType.INT)]).getData());
    }

    @Test
    public void testGetIntegerColumnByNameLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.INT)],table.getIntColumn(schema.getColumnLabels()[getIndex(DataType.INT)]).getLabel());
    }

    @Test
    public void testGetLongColumn() {
        assertArrayEquals(getLongColumnData(),table.getLongColumn(getIndex(DataType.LONG)).getData());
    }

    @Test
    public void testGetLongColumnLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.LONG)],table.getLongColumn(getIndex(DataType.LONG)).getLabel());
    }

    @Test
    public void testGetLongColumnByName() {
        assertArrayEquals(getLongColumnData(),table.getLongColumn(schema.getColumnLabels()[getIndex(DataType.LONG)]).getData());
    }

    @Test
    public void testGetLongColumnByNameLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.LONG)],table.getLongColumn(schema.getColumnLabels()[getIndex(DataType.LONG)]).getLabel());
    }

    @Test
    public void testGetFloatColumn() {
        assertArrayAlmostEquals(getFloatColumnData(),table.getFloatColumn(getIndex(DataType.FLOAT)).getData());
    }

    @Test
    public void testGetFloatColumnLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.FLOAT)],table.getFloatColumn(getIndex(DataType.FLOAT)).getLabel());
    }

    @Test
    public void testGetFloatColumnByName() {
        assertArrayAlmostEquals(getFloatColumnData(),table.getFloatColumn(schema.getColumnLabels()[getIndex(DataType.FLOAT)]).getData());
    }

    @Test
    public void testGetFloatColumnByNameLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.FLOAT)],table.getFloatColumn(schema.getColumnLabels()[getIndex(DataType.FLOAT)]).getLabel());
    }

    @Test
    public void testGetDoubleColumn() {
        assertArrayAlmostEquals(getDoubleColumnData(),table.getDoubleColumn(getIndex(DataType.DOUBLE)).getData());
    }

    @Test
    public void testGetDoubleColumnLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.DOUBLE)],table.getDoubleColumn(getIndex(DataType.DOUBLE)).getLabel());
    }

    @Test
    public void testGetDoubleColumnByName() {
        assertArrayAlmostEquals(getDoubleColumnData(),table.getDoubleColumn(schema.getColumnLabels()[getIndex(DataType.DOUBLE)]).getData());
    }

    @Test
    public void testGetDoubleColumnByNameLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.DOUBLE)],table.getDoubleColumn(schema.getColumnLabels()[getIndex(DataType.DOUBLE)]).getLabel());
    }

    @Test
    public void testGetStringColumn() {
        assertArrayEquals(getStringColumnData(),table.getStringColumn(getIndex(DataType.STRING)).getData());
    }

    @Test
    public void testGetStringColumnLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.STRING)],table.getStringColumn(getIndex(DataType.STRING)).getLabel());
    }

    @Test
    public void testGetStringColumnByName() {
        assertArrayEquals(getStringColumnData(),table.getStringColumn(schema.getColumnLabels()[getIndex(DataType.STRING)]).getData());
    }

    @Test
    public void testGetStringColumnByNameLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.STRING)],table.getStringColumn(schema.getColumnLabels()[getIndex(DataType.STRING)]).getLabel());
    }

    @Test
    public void testGetColumn() {
        assertArrayAlmostEquals(getFloatColumnData(),table.getColumn(getIndex(DataType.FLOAT)).getData());
    }

    @Test
    public void testGetColumnLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.FLOAT)],table.getColumn(getIndex(DataType.FLOAT)).getLabel());
    }

    @Test
    public void testGetColumnByName() {
        assertArrayAlmostEquals(getFloatColumnData(),table.getColumn(schema.getColumnLabels()[getIndex(DataType.FLOAT)]).getData());
    }

    @Test
    public void testGetColumnByNameLabel() {
        assertEquals(schema.getColumnLabels()[getIndex(DataType.FLOAT)],table.getColumn(schema.getColumnLabels()[getIndex(DataType.FLOAT)]).getLabel());
    }

    @Test
    public void testSetRowData() {
        int randomRow = random.nextInt(numberOfRows);
        Object[] randomRowData = tableData[random.nextInt(numberOfRows)];
        table.setRowData(randomRow,randomRowData);
        assertArrayAlmostEquals(randomRowData,table.getRow(randomRow).getData());
    }

    @Test(expected=TableDataException.class)
    public void testSetRowDataRowTooLow() {
        Object[] randomRowData = tableData[random.nextInt(numberOfRows)];
        table.setRowData(-1,randomRowData);
    }

    @Test(expected=TableDataException.class)
    public void testSetRowDataRowTooHigh() {
        Object[] randomRowData = tableData[random.nextInt(numberOfRows)];
        table.setRowData(table.getRowCount(),randomRowData);
    }

    @Test(expected=TableDataException.class)
    public void testSetRowDataWrongColumnCount() {
        Object[] randomRowData = new Object[schema.getColumnLabels().length-1];
        System.arraycopy(tableData[random.nextInt(numberOfRows)],0,randomRowData,0,randomRowData.length);
        table.setRowData(0,randomRowData);
    }

    @Test(expected=TableDataException.class)
    public void testSetRowDataWrongDataType() {
        Object[] randomRowData = new Object[schema.getColumnLabels().length];
        Arrays.fill(randomRowData,"");
        table.setRowData(0,randomRowData);
    }

    @Test
    public void testSetRowDataByKey() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        int randomRow = tableKey.getRowNumber(randomKey);
        table.setPrimaryKey(tableKey);
        Object[] randomRowData = tableData[random.nextInt(numberOfRows)];
        randomRowData[getIntegerKeyColumnIndex()] = getUnusedKeyInteger();
        table.setRowDataByKey(randomKey,randomRowData);
        //index column has changed, so update it
        assertArrayAlmostEquals(randomRowData,table.getRow(randomRow).getData());
    }

    @Test
    public void testSetRowDataByKeyColumn() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        table.setPrimaryKey(integerKeyColumnLabel);
        Object[] randomRowData = new Object[schema.getColumnLabels().length];
        System.arraycopy(tableData[random.nextInt(numberOfRows)],0,randomRowData,0,randomRowData.length);
        randomRowData[integerKeyColumnIndex] = getUnusedKeyInteger();
        table.setRowDataByKey(randomKey,randomRowData);
        //index column has changed, so update it
        assertArrayAlmostEquals(randomRowData,table.getRowByKey(randomRowData[getIntegerKeyColumnIndex()]).getData());
    }

    @Test(expected=TableDataException.class)
    public void testSetRowDataRowByKeyInvalidKey() {
        table.setPrimaryKey(tableKey);
        Object[] randomRowData = tableData[random.nextInt(numberOfRows)];
        table.setRowDataByKey("not a key",randomRowData);
    }

    @Test(expected=TableDataException.class)
    public void testSetRowDataByKeyWrongColumnCount() {
        table.setPrimaryKey(tableKey);
        Object[] randomRowData = new Object[schema.getColumnLabels().length-1];
        System.arraycopy(tableData[random.nextInt(numberOfRows)],0,randomRowData,0,randomRowData.length);
        table.setRowDataByKey(0,randomRowData);
    }

    @Test(expected=TableDataException.class)
    public void testSetRowDataByKeyWrongDataType() {
        table.setPrimaryKey(tableKey);
        Object[] randomRowData = new Object[schema.getColumnLabels().length];
        Arrays.fill(randomRowData,"");
        table.setRowDataByKey(0,randomRowData);
    }

    protected int getIndex(DataType type) {
        DataType[] types = schema.getColumnTypes();
        int index = 0;
        for (int i : range(types.length)) {
            if (types[i] == type) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Test
    public void testSetColumnData() {
        Float[] newColumn = new Float[table.getRowCount()];
        for (int i : range(newColumn.length))
            newColumn[i] = random.nextFloat();
        int floatIndex =getIndex(DataType.FLOAT);
        table.setColumnData(floatIndex,newColumn);
        assertArrayAlmostEquals(newColumn,table.getFloatColumn(floatIndex).getData());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetColumnDataNotArray() {
        table.setColumnData(0,0);
    }

    @Test(expected=TableDataException.class)
    public void testSetColumnDataInvalidData() {
        Float[] newColumn = new Float[table.getRowCount()];
        for (int i : range(newColumn.length))
            newColumn[i] = random.nextFloat();
        table.setColumnData(getIndex(DataType.INT),newColumn);
    }

    @Test
    public void testSetColumnDataLabel() {
        Float[] newColumn = new Float[table.getRowCount()];
        for (int i : range(newColumn.length))
            newColumn[i] = random.nextFloat();
        int floatIndex = getIndex(DataType.FLOAT);
        table.setColumnData(schema.getColumnLabels()[floatIndex],newColumn);
        assertArrayAlmostEquals(newColumn,table.getFloatColumn(floatIndex).getData());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetColumnDataLabelNotArray() {
        table.setColumnData(schema.getColumnLabels()[0],0);
    }

    @Test(expected=TableDataException.class)
    public void testSetColumnDataLabelWrongArraySize() {
        Float[] newColumn = new Float[table.getRowCount()+1];
        for (int i : range(newColumn.length))
            newColumn[i] = random.nextFloat();
        table.setColumnData(schema.getColumnLabels()[getIndex(DataType.FLOAT)],newColumn);
    }

    @Test(expected=TableDataException.class)
    public void testSetColumnDataLabelInvalidData() {
        Float[] newColumn = new Float[table.getRowCount()];
        for (int i : range(newColumn.length))
            newColumn[i] = random.nextFloat();
        table.setColumnData(schema.getColumnLabels()[getIndex(DataType.INT)],newColumn);
    }

    @Test
    public void testSetCellValue() {
        Float randomFloat = random.nextFloat();
        int randomRow = random.nextInt(numberOfRows);
        table.setCellValue(randomRow,getIndex(DataType.FLOAT),randomFloat);
        assertEquals(randomFloat,table.getRow(randomRow).getCell(getIndex(DataType.FLOAT)));
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueRowTooLow() {
        float randomFloat = random.nextFloat();
        table.setCellValue(-1,getIndex(DataType.FLOAT),randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueRowTooHigh() {
        float randomFloat = random.nextFloat();
        table.setCellValue(numberOfRows,getIndex(DataType.FLOAT),randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueColumnTooLow() {
        float randomFloat = random.nextFloat();
        int randomRow = random.nextInt(numberOfRows);
        table.setCellValue(randomRow,-1,randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueColumnTooHigh() {
        float randomFloat = random.nextFloat();
        int randomRow = random.nextInt(numberOfRows);
        table.setCellValue(randomRow,schema.getColumnLabels().length,randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueInvalidData() {
        int randomRow = random.nextInt(numberOfRows);
        table.setCellValue(randomRow,getIndex(DataType.FLOAT),"a string, not a float");
    }

    @Test
    public void testSetCellValueLabel() {
        Float randomFloat = random.nextFloat();
        int randomRow = random.nextInt(numberOfRows);
        int floatIndex = getIndex(DataType.FLOAT);
        table.setCellValue(randomRow,schema.getColumnLabels()[floatIndex],randomFloat);
        assertAlmostEquals(randomFloat,table.getRow(randomRow).getCell(floatIndex));
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueLabelRowTooLow() {
        float randomFloat = random.nextFloat();
        table.setCellValue(-1,schema.getColumnLabels()[getIndex(DataType.FLOAT)],randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueLabelRowTooHigh() {
        float randomFloat = random.nextFloat();
        table.setCellValue(numberOfRows,schema.getColumnLabels()[getIndex(DataType.FLOAT)],randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueInvalidLabel() {
        float randomFloat = random.nextFloat();
        int randomRow = random.nextInt(numberOfRows);
        table.setCellValue(randomRow,"not a column",randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueLabelInvalidData() {
        int randomRow = random.nextInt(numberOfRows);
        table.setCellValue(randomRow,schema.getColumnLabels()[getIndex(DataType.FLOAT)],"a string, not a float");
    }

    @Test
    public void testSetCellValueByKey() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        String columnName = schema.getColumnLabels()[getIndex(DataType.INT)];
        table.setPrimaryKey(TableDataFactory.getTableKey(table,columnName));
        Float randomFloat = random.nextFloat();
        int floatIndex = getIndex(DataType.FLOAT);
        table.setCellValueByKey(randomKey,floatIndex,randomFloat);
        assertAlmostEquals(randomFloat,table.getRowByKey(randomKey).getCell(floatIndex));
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueByInvalidKey() {
        String columnName = schema.getColumnLabels()[getIndex(DataType.INT)];
        table.setPrimaryKey(TableDataFactory.getTableKey(table,columnName));
        float randomFloat = random.nextFloat();
        table.setCellValueByKey("not a key",getIndex(DataType.FLOAT),randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueByKeyColumnTooLow() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        table.setPrimaryKey(tableKey);
        float randomFloat = random.nextFloat();
        table.setCellValueByKey(randomKey,-1,randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueByKeyColumnTooHigh() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        table.setPrimaryKey(tableKey);
        float randomFloat = random.nextFloat();
        table.setCellValueByKey(randomKey,schema.getColumnLabels().length,randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueByKeyInvalidData() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        table.setPrimaryKey(tableKey);
        table.setCellValueByKey(randomKey,getIndex(DataType.FLOAT),"a string, not a float");
    }

    @Test
    public void testSetCellValueLabelByKey() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        table.setPrimaryKey(tableKey);
        Float randomFloat = random.nextFloat();
        table.setCellValueByKey(randomKey,schema.getColumnLabels()[getIndex(DataType.FLOAT)],randomFloat);
        assertAlmostEquals(randomFloat,table.getRowByKey(randomKey).getCell(getIndex(DataType.FLOAT)));
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueLabelByInvalidKey() {
        table.setPrimaryKey(tableKey);
        float randomFloat = random.nextFloat();
        table.setCellValueByKey("not a key",schema.getColumnLabels()[getIndex(DataType.FLOAT)],randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueInvalidLabelByKey() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        table.setPrimaryKey(tableKey);
        float randomFloat = random.nextFloat();
        table.setCellValueByKey(randomKey,"not a column",randomFloat);
    }

    @Test(expected=TableDataException.class)
    public void testSetCellValueLabelByKeyInvalidData() {
        int randomKey = (Integer) tableData[random.nextInt(numberOfRows)][integerKeyColumnIndex];
        table.setPrimaryKey(tableKey);
        table.setCellValueByKey(randomKey,schema.getColumnLabels()[getIndex(DataType.FLOAT)],"a string, not a float");
    }

    @Test
    public void testDeleteRowReturn() {
        int randomRow = random.nextInt(table.getRowCount());
        Object[] data = table.getRow(randomRow).getData();
        assertArrayAlmostEquals(data,table.deleteRow(randomRow).getData());
    }

    @Test
    public void testDeleteRowCount() {
        int oldCount = table.getRowCount();
        table.deleteRow(random.nextInt(table.getRowCount()));
        assertEquals(oldCount-1,table.getRowCount());
    }

    @Test(expected= TableDataException.class)
    public void testDeleteRowFailureTooLow() {
        table.deleteRow(-1);
    }

    @Test(expected= TableDataException.class)
    public void testDeleteRowFailureTooHigh() {
        table.deleteRow(table.getRowCount());
    }

    @Test
    public void testDeleteRowByKeyReturn() {
        int randomRow = random.nextInt(table.getRowCount());
        table.setPrimaryKey(tableKey);
        Object keyValue = tableKey.getKey(randomRow);
        Object[] data = table.getRow(randomRow).getData();
        assertArrayAlmostEquals(data,table.deleteRowByKey(keyValue).getData());
    }

    @Test
    public void testDeleteRowByKeyCount() {
        int oldCount = table.getRowCount();
        table.setPrimaryKey(tableKey);
        table.deleteRowByKey(tableKey.getKey(random.nextInt(table.getRowCount())));
        assertEquals(oldCount-1,table.getRowCount());
    }

    @Test(expected= TableDataException.class)
    public void testDeleteRowByKeyFailureTooLow() {
        table.deleteRowByKey("");
    }

    @Test
    public void testDeleteColumnReturn() {
        int randomColumn = random.nextInt(table.getColumnCount());
        Object[] data = table.getColumn(randomColumn).getData();
        assertArrayAlmostEquals(data,table.deleteColumn(randomColumn).getData());
    }

    @Test
    public void testDeleteColumnCount() {
        int randomColumn = random.nextInt(table.getColumnCount());
        int oldCount = table.getColumnCount();
        table.deleteColumn(randomColumn);
        assertEquals(oldCount-1,table.getColumnCount());
    }

    @Test
    public void testDeleteColumnLabel() {
        int randomColumn = random.nextInt(table.getColumnCount());
        String label = table.deleteColumn(randomColumn).getLabel();
        boolean answer = false;
        for (String colLabel : table.getColumnLabels())
            if (colLabel.equals(label))
                answer = true;
        assertFalse(answer);
    }

    @Test
    public void testDeleteColumnSchemaCount() {
        int randomColumn = random.nextInt(table.getColumnCount());
        int oldCount = table.getSchema().getColumnLabels().length;
        table.deleteColumn(randomColumn);
        assertEquals(oldCount-1,table.getSchema().getColumnLabels().length);
    }

    @Test
    public void testDeleteColumnSchemaLabel() {
        int randomColumn = random.nextInt(table.getColumnCount());
        String label = table.deleteColumn(randomColumn).getLabel();
        boolean answer = false;
        for (String colLabel : table.getSchema().getColumnLabels())
            if (colLabel.equals(label))
                answer = true;
        assertFalse(answer);
    }

    @Test(expected=TableDataException.class)
    public void testDeleteColumnFailureTooLow() {
        table.deleteColumn(-1);
    }

    @Test(expected=TableDataException.class)
    public void testDeleteColumnFailureTooHigh() {
        table.deleteColumn(table.getColumnCount());
    }

    @Test
    public void testDeleteColumnLabelReturn() {
        String randomColumn = table.getColumnLabel(random.nextInt(table.getColumnCount()));
        Object[] data = table.getColumn(randomColumn).getData();
        assertArrayAlmostEquals(data,table.deleteColumn(randomColumn).getData());
    }

    @Test
    public void testDeleteColumnLabelCount() {
        String randomColumn = table.getColumnLabel(random.nextInt(table.getColumnCount()));
        int oldCount = table.getColumnCount();
        table.deleteColumn(randomColumn);
        assertEquals(oldCount-1,table.getColumnCount());
    }

    @Test
    public void testDeleteColumnLabelLabel() {
        String randomColumn = table.getColumnLabel(random.nextInt(table.getColumnCount()));
        String label = table.deleteColumn(randomColumn).getLabel();
        boolean answer = false;
        for (String colLabel : table.getColumnLabels())
            if (colLabel.equals(label))
                answer = true;
        assertFalse(answer);
    }

    @Test
    public void testDeleteColumnLabelSchemaCount() {
        String randomColumn = table.getColumnLabel(random.nextInt(table.getColumnCount()));
        int oldCount = table.getSchema().getColumnLabels().length;
        table.deleteColumn(randomColumn);
        assertEquals(oldCount-1,table.getSchema().getColumnLabels().length);
    }

    @Test
    public void testDeleteColumnLabelSchemaLabel() {
        String randomColumn = table.getColumnLabel(random.nextInt(table.getColumnCount()));
        String label = table.deleteColumn(randomColumn).getLabel();
        boolean answer = false;
        for (String colLabel : table.getSchema().getColumnLabels())
            if (colLabel.equals(label))
                answer = true;
        assertFalse(answer);
    }

    @Test(expected=TableDataException.class)
    public void testDeleteColumnLabelFailure() {
        table.deleteColumn("not a column");
    }

    @Test
    public void testGetCellValue() {
        int randomRow = random.nextInt(table.getRowCount());
        int randomColumn = random.nextInt(table.getColumnCount());
        assertAlmostEquals(getTableData()[randomRow][randomColumn],table.getCellValue(randomRow,randomColumn));
    }

    @Test(expected=TableDataException.class)
    public void testGetCellValueFailure1() {
        int randomColumn = random.nextInt(table.getColumnCount());
        table.getCellValue(-1,randomColumn);
    }

    @Test(expected=TableDataException.class)
    public void testGetCellValueFailure2() {
        int randomColumn = random.nextInt(table.getColumnCount());
        table.getCellValue(table.getRowCount(),randomColumn);
    }

    @Test(expected=TableDataException.class)
    public void testGetCellValueFailure3() {
        int randomRow = random.nextInt(table.getRowCount());
        table.getCellValue(randomRow,-1);
    }

    @Test(expected=TableDataException.class)
    public void testGetCellValueFailure4() {
        int randomRow = random.nextInt(table.getRowCount());
        table.getCellValue(randomRow,table.getColumnCount());
    }

    @Test
    public void testGetCellValueLabel() {
        int randomRow = random.nextInt(table.getRowCount());
        int randomColumn = random.nextInt(table.getColumnCount());
        assertAlmostEquals(getTableData()[randomRow][randomColumn],table.getCellValue(randomRow,table.getColumnLabel(randomColumn)));
    }

    @Test(expected=TableDataException.class)
    public void testGetCellValueLabelFailure1() {
        int randomColumn = random.nextInt(table.getColumnCount());
        table.getCellValue(-1,table.getColumnLabel(randomColumn));
    }

    @Test(expected=TableDataException.class)
    public void testGetCellValueLabelFailure2() {
        int randomColumn = random.nextInt(table.getColumnCount());
        table.getCellValue(table.getRowCount(),table.getColumnLabel(randomColumn));
    }

    @Test(expected=TableDataException.class)
    public void testGetCellValueLabelFailure3() {
        int randomRow = random.nextInt(table.getRowCount());
        table.getCellValue(randomRow,"not a column");
    }

    @Test
    public void testGetCellValueByKey() {
        int randomRow = random.nextInt(table.getRowCount());
        int randomColumn = random.nextInt(table.getColumnCount());
        table.setPrimaryKey(tableKey);
        assertAlmostEquals(getTableData()[randomRow][randomColumn],table.getCellValueByKey(tableKey.getKey(randomRow),randomColumn));
    }

    @Test(expected=TableDataException.class)
    public void testGetCellValueByKeyFailure1() {
        int randomColumn = random.nextInt(table.getColumnCount());
        table.setPrimaryKey(tableKey);
        table.getCellValueByKey("",randomColumn);
    }

    @Test(expected=TableDataException.class)
    public void testGetCellValueByKeyFailure2() {
        int randomRow = random.nextInt(table.getRowCount());
        table.setPrimaryKey(tableKey);
        table.getCellValueByKey(tableKey.getKey(randomRow),-1);
    }

    @Test(expected=TableDataException.class)
    public void testGetCellValueByKeyFailure3() {
        int randomRow = random.nextInt(table.getRowCount());
        table.setPrimaryKey(tableKey);
        table.getCellValueByKey(tableKey.getKey(randomRow),table.getColumnCount());
    }

    @Test
    public void testIterator() {
        Object[][] testData = new Object[table.getRowCount()][];
        int counter = 0;
        for (DataRow row : table)
            testData[counter++] = row.getData();
        assertArrayAlmostEquals(getTableData(),testData);
    }

    @Test
    public void testPartitionTableLength() {
        int rows = getNumberOfRows();
        int start = random.nextInt(0,rows/2);
        int end = random.nextInt(rows/2,rows);
        assertEquals(end-start,table.getTablePartition(start,end).getRowCount());
    }

    @Test
    public void testPartitionTableData() {
        int rows = getNumberOfRows();
        int start = random.nextInt(0,rows/2);
        int end = random.nextInt(rows/2,rows);
        int randomRow = random.nextInt(end-start);
        assertArrayAlmostEquals(getTableData()[randomRow+start],table.getTablePartition(start,end).getRow(randomRow).getData());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPartitionTableLengthStartNegative() {
        table.getTablePartition(-1,getNumberOfRows()/2);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPartitionTableLengthEndNegative() {
        table.getTablePartition(getNumberOfRows()/2,-1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPartitionTableLengthStartBiggerThandEnd() {
        int rows = getNumberOfRows();
        int start = random.nextInt(0,rows/2);
        int end = random.nextInt(rows/2,rows);
        table.getTablePartition(end,start);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPartitionTableLengthEndOutOfBounds() {
        table.getTablePartition(getNumberOfRows()/2,getNumberOfRows()+1);
    }

    //todo: test getting data columns and data, I think that this is possibly broken in AbstractDataTable

}
