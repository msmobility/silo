package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.DataType;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * @author crf <br/>
 *         Started: Sep 25, 2008 2:40:38 PM
 */
@SuppressWarnings("unchecked") //a whole slew of valid warnings, but they won't affect test because framework ensures
                               // safety, so suppress them
public abstract class TableKeyTest<I> extends TableIndexTest<I> {

    protected TableKey<I> key;

    abstract protected TableKey<I> getTableKey(String integerIndexColumnLabel, DataTable table);

    protected String getKeyColumnLabel() {
        return testData.getColumnName(DataType.INT);
    }

    protected TableIndex<I> getTableIndex(String[] indexColumnLabels, DataTable table) {
        return getTableKey(indexColumnLabels[0],table);
    }

    protected String[] getIndexColumnLabels() {
        return new String[] {getKeyColumnLabel()};
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        key = (TableKey<I>) index;
    }

    @Test
    public void testGetKeyColumnLabel() {
        assertEquals(indexColumnLabels[0],key.getKeyColumnLabel());
    }

    @Test
    public void testGetKeyColumnType() {
        assertEquals(dataTable.getColumnDataType(indexColumnLabels[0]),key.getKeyColumnType());
    }

    @Test
    public void testGetRowNumber() {
        int randomRow = random.nextInt(dataTable.getRowCount());
        assertEquals(randomRow,key.getRowNumber((I) dataTable.getRow(randomRow).getCell(indexColumnLabels[0])));
    }

    @Test(expected=TableDataException.class)
    public void testGetRowNumberFailure() {
        //override if I == String
        key.getRowNumber((I) "");
    }

    @Test
    public void testGetKey() {
        int randomRow = random.nextInt(dataTable.getRowCount());
        assertEquals(dataTable.getRow(randomRow).getCell(indexColumnLabels[0]),key.getKey(randomRow));
    }

    @Test(expected=TableDataException.class)
    public void testGetKeyFailureTooLow() {
        key.getKey(-1);
    }

    @Test(expected=TableDataException.class)
    public void testGetKeyFailureTooHigh() {
        key.getKey(dataTable.getRowCount());
    }

    @Test
    public void testGetUniqueKeys() {
        Set<I> uniques = new HashSet<I>();
        Set<I> keyUniques = new HashSet<I>();
        for (DataRow row : dataTable)
            uniques.add((I) row.getCell(indexColumnLabels[0]));
        keyUniques.addAll(Arrays.asList(key.getUniqueKeys()));
        assertEquals(uniques,keyUniques);
    }
}
