package com.pb.sawdust.tabledata;

import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.util.TableDataFactory;
import static com.pb.sawdust.util.Range.*;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

/**
 * @author crf <br/>
 *         Started: Sep 25, 2008 10:55:14 AM
 */                              
@SuppressWarnings("unchecked") //a whole slew of valid warnings, but they won't affect test because framework ensures
                               // safety, so suppress them
public abstract class TableIndexTest<I> extends TestBase {

    protected TableIndex<I> index;
    protected String[] indexColumnLabels;
    protected TableDataTestData testData = new TableDataTestData();
    protected DataTable dataTable;

    abstract protected TableIndex<I> getTableIndex(String[] indexColumnLabels, DataTable table);

    protected DataTable getDataTable(TableSchema schema, Object[][] data) {
        return TableDataFactory.getDataTable(schema,data);
    }

    protected Object[][] getTableData() {
        return testData.getTableData(1000);
    }

    protected TableSchema getTableSchema() {
        return new TableSchema("table_name",testData.getColumnNames(),testData.getColumnTypes());
    }

    protected String[] getIndexColumnLabels() {
        String[] columnLabels = new String[2]; //hope for repeats of booleans n bytes
        String[] tableColumnLabels = testData.getColumnNames();
        for (int i : range(columnLabels.length))
            columnLabels[i] = tableColumnLabels[i];
        return columnLabels;
    }

    @Before
    public void beforeTest() {
        dataTable = getDataTable(getTableSchema(),getTableData());
        indexColumnLabels = getIndexColumnLabels();
        index = getTableIndex(indexColumnLabels,dataTable);
        index.buildIndex();
    }

    @Test
    public void testGetIndexColumnLabels() {
        assertArrayEquals(indexColumnLabels,index.getIndexColumnLabels());
    }

    @Test
    public void testGetUniqueValues() {
        Set<List<I>> uniques = new HashSet<List<I>>();
        for (DataRow row : dataTable) {
            List<I> rowData = new LinkedList<I>();
            for (String column : indexColumnLabels)
                rowData.add((I) row.getCell(column));
            if (!uniques.contains(rowData))
                uniques.add(rowData);
        }
        Set<List<I>> indexUniques = new HashSet<List<I>>();
        for (I[] row : index.getUniqueValues()) {
            List<I> rowData = new LinkedList<I>();
            rowData.addAll(Arrays.asList(row));
            indexUniques.add(rowData);
        }
        assertEquals(uniques,indexUniques);
    }

    @Test
    public void testGetUniqueValuesSize() {
        Set<List<I>> uniques = new HashSet<List<I>>();
        for (DataRow row : dataTable) {
            List<I> rowData = new LinkedList<I>();
            for (String column : indexColumnLabels)
                rowData.add((I) row.getCell(column));
            if (!uniques.contains(rowData))
                uniques.add(rowData);
        }
        assertEquals(uniques.size(),index.getUniqueValues().length);
    }

    @Test
    public void testGetRowNumbers() {
        //pick set with pair or more, if possible
        Map<List<I>,Set<Integer>> uniques = new HashMap<List<I>,Set<Integer>>();
        int rowNumber = 0;
        for (DataRow row : dataTable) {
            List<I> rowData = new LinkedList<I>();
            for (String column : indexColumnLabels)
                rowData.add((I) row.getCell(column));
            if (!uniques.containsKey(rowData))
                uniques.put(rowData,new HashSet<Integer>());
            uniques.get(rowData).add(rowNumber++);
        }
        List<I> selectedKey = null;
        for (List<I> key : uniques.keySet()) {
            if (uniques.get(key).size() > 1) {
                selectedKey = key;
                break;
            }
            if (selectedKey == null)
                selectedKey = key;
        }
        assertEquals(uniques.get(selectedKey),index.getRowNumbers(selectedKey.toArray((I[]) new Object[0])));
    }

    @Test(expected=TableDataException.class)
    public void testGetRowNumbersFailure() {
        Object[] badValues = new Object[indexColumnLabels.length];
        for (int i : range(badValues.length))
            badValues[i] = "not here";
        index.getRowNumbers((I[]) badValues);
    }

    @Test
    public void testGetIndexCount() {
        //pick set with pair or more, if possible
        Map<List<I>,Set<Integer>> uniques = new HashMap<List<I>,Set<Integer>>();
        int rowNumber = 0;
        for (DataRow row : dataTable) {
            List<I> rowData = new LinkedList<I>();
            for (String column : indexColumnLabels)
                rowData.add((I) row.getCell(column));
            if (!uniques.containsKey(rowData))
                uniques.put(rowData,new HashSet<Integer>());
            uniques.get(rowData).add(rowNumber++);
        }
        List<I> selectedKey = null;
        for (List<I> key : uniques.keySet()) {
            if (uniques.get(key).size() > 1) {
                selectedKey = key;
                break;
            }
            if (selectedKey == null)
                selectedKey = key;
        }
        assertEquals(uniques.get(selectedKey).size(),index.getIndexCount(selectedKey.toArray((I[]) new Object[0])));
    }

    @Test
    public void testGetIndexCountEmpty() {
        Object[] badValues = new Object[indexColumnLabels.length];
        for (int i : range(badValues.length))
            badValues[i] = "not here";
        assertEquals(0,index.getIndexCount((I[]) badValues));
    }
}
