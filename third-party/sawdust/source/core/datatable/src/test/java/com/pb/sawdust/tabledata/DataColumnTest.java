package com.pb.sawdust.tabledata;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.test.TestBase;

/**
 * @author crf <br/>
 *         Started: Sep 22, 2008 5:27:10 PM
 */
public abstract class DataColumnTest<T> extends TestBase {

    protected DataColumn<T> column;
    protected DataType columnType;
    protected int numberOfRows;
    protected String columnName;
    protected T[] columnData;
    protected TableDataTestData testData = new TableDataTestData();

    abstract protected DataColumn<T> getDataColumn(T[] columnData, String columnName);
    abstract protected DataType getColumnDataType();

    protected int getNumberOfRows() {
        return 100;
    }

    protected String getColumnName() {
        return testData.getColumnName(getColumnDataType());
    }

    @SuppressWarnings("unchecked") //ok because datatype ensures type safety (so long as it is defined correctly)
    protected T[] getColumnData() {
        return (T[]) testData.getColumnData(getColumnDataType(),getNumberOfRows());
    }

    protected TableKey<Integer> getTableKey() {
        return testData.getSimpleKey(getNumberOfRows());
    }

    @Before
    public void beforeTest() {
        numberOfRows = getNumberOfRows();
        columnType = getColumnDataType();
        columnName = getColumnName();
        columnData = getColumnData();
        column = getDataColumn(getColumnData(),getColumnName());
    }

    @Test
    public void testGetLabel() {
        assertEquals(columnName,column.getLabel());
    }

    @Test
    public void testGetType() {
        assertEquals(columnType,column.getType());
    }

    @Test
//    @SuppressWarnings("unchecked")
    public void testGetColumn() {
        assertArrayAlmostEquals(columnData,column.getData());
    }

    @Test
    public void testGetCell() {
        int randomIndex = random.nextInt(numberOfRows);
        assertAlmostEquals(columnData[randomIndex],column.getCell(randomIndex));
    }

    @Test
    public void testGetPrimitiveColumn() {
        switch (columnType) {
            case BOOLEAN : {
                @SuppressWarnings("unchecked") //type safety ensured by switch (provided columnType correctly defined)
                BooleanDataColumn column = new BooleanDataColumn((DataColumn<Boolean>) this.column);
                boolean[] primitiveColumn = column.getPrimitiveColumn();
                boolean[] referenceColumn = ArrayUtil.toPrimitive((Boolean[]) columnData);
                boolean answer = true;
                for (int i = 0; i < primitiveColumn.length; i++) {
                    answer &= (primitiveColumn[i] == referenceColumn[i]);
                }
                assertTrue(answer);
                break;
            }
            case BYTE : {
                @SuppressWarnings("unchecked") //type safety ensured by switch (provided columnType correctly defined)
                ByteDataColumn column = new ByteDataColumn((DataColumn<Byte>) this.column);
                assertArrayEquals(ArrayUtil.toPrimitive((Byte[]) columnData),column.getPrimitiveColumn());
                break;
            }
            case SHORT : {
                @SuppressWarnings("unchecked") //type safety ensured by switch (provided columnType correctly defined)
                ShortDataColumn column = new ShortDataColumn((DataColumn<Short>) this.column);
                assertArrayEquals(ArrayUtil.toPrimitive((Short[]) columnData),column.getPrimitiveColumn());
                break;
            }
            case INT : {
                @SuppressWarnings("unchecked") //type safety ensured by switch (provided columnType correctly defined)
                IntegerDataColumn column = new IntegerDataColumn((DataColumn<Integer>) this.column);
                assertArrayEquals(ArrayUtil.toPrimitive((Integer[]) columnData),column.getPrimitiveColumn());
                break;
            }
            case LONG : {
                @SuppressWarnings("unchecked") //type safety ensured by switch (provided columnType correctly defined)
                LongDataColumn column = new LongDataColumn((DataColumn<Long>) this.column);
                assertArrayEquals(ArrayUtil.toPrimitive((Long[]) columnData),column.getPrimitiveColumn());
                break;
            }
            case FLOAT : {
                @SuppressWarnings("unchecked") //type safety ensured by switch (provided columnType correctly defined)
                FloatDataColumn column = new FloatDataColumn((DataColumn<Float>) this.column);
                assertArrayAlmostEquals(ArrayUtil.toPrimitive((Float[]) columnData),column.getPrimitiveColumn());
                break;
            }
            case DOUBLE : {
                @SuppressWarnings("unchecked") //type safety ensured by switch (provided columnType correctly defined)
                DoubleDataColumn column = new DoubleDataColumn((DataColumn<Double>) this.column);
                assertArrayAlmostEquals(ArrayUtil.toPrimitive((Double[]) columnData),column.getPrimitiveColumn());
                break;
            }
        }
    }

    @Test
    public void testGetCellByKey() {
        TableKey<Integer> key = getTableKey();
        int randomRow = random.nextInt(numberOfRows);
        assertAlmostEquals(getColumnData()[randomRow],column.getCellByKey(key.getKey(randomRow)));
    }

    @Test(expected= TableDataException.class)
    public void testGetCellByKeyWrongKeyType() {
        column.getCellByKey("");
    }

    @Test(expected= TableDataException.class)
    public void testGetCellByKeyInvalidKey() {
        column.getCellByKey(numberOfRows);
    }

    @Test
    @SuppressWarnings("unchecked") //type safety ensured by switch (provided columnType correctly defined)
    public void testIterator() {
        T[] testData;
        switch (getColumnDataType()) {
            case BOOLEAN : testData = (T[]) new Boolean[column.getRowCount()];break;
            case BYTE : testData = (T[]) new Byte[column.getRowCount()];break;
            case DOUBLE : testData = (T[]) new Double[column.getRowCount()];break;
            case FLOAT : testData = (T[]) new Float[column.getRowCount()];break;
            case INT : testData = (T[]) new Integer[column.getRowCount()];break;
            case LONG : testData = (T[]) new Long[column.getRowCount()];break;
            case SHORT : testData = (T[]) new Short[column.getRowCount()];break;
            case STRING : testData = (T[]) new String[column.getRowCount()];break;
            default : testData = (T[]) new Object[column.getRowCount()];break;
        }
        int counter = 0;
        for (T cell : column)
            testData[counter++] = cell;
        assertArrayAlmostEquals(getColumnData(),testData);
    }
}
