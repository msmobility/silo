package com.pb.sawdust.model.models.provider;

import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.basic.ColumnDataTable;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tensor.ArrayTensor;
import com.pb.sawdust.util.test.TestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * The {@code DataTableDataProviderTest} ...
 *
 * @author crf <br/>
 *         Started Sep 26, 2010 1:47:55 PM
 */
public class DataTableDataProviderTest extends DataProviderTest {
    protected DataTable sourceTable;
    protected DataTableDataProvider dataTableProvider;

    public static void main(String ... args) {
        TestBase.main();
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        dataTableProvider = (DataTableDataProvider) provider;
    }

    @Override
    protected DataProvider getProvider(int id, int dataLength, Map<String, double[]> data) {
        TableSchema schema = new TableSchema("provider source");
        double[][] dataArray = new double[data.keySet().size()][];
        int counter = 0;
        for (String variable : data.keySet()) {
            schema.addColumn(variable, DataType.DOUBLE);
            dataArray[counter++] = data.get(variable);
        }
        sourceTable = new ColumnDataTable(schema);
        sourceTable.addDataByColumn(dataArray);
        return new DataTableDataProvider(id,sourceTable,ArrayTensor.getFactory());
    }

    @Override
    protected DataProvider getUninitializedProvider(Set<String> variables) {
        while (sourceTable.getRowCount() > 0)
            sourceTable.deleteRow(0);
        return dataTableProvider;
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorEmptyDataTable() {
        TableSchema schema = new TableSchema("provider source");
        for (String variable : data.keySet())
            schema.addColumn(variable, DataType.DOUBLE);
        sourceTable = new ColumnDataTable(schema);
        new DataTableDataProvider(id,sourceTable,ArrayTensor.getFactory());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorBadId() {
        //assumes haven't done Integer.MAX_VALUE data ids in between
        getProvider(id+Integer.MAX_VALUE,provider.getDataLength(),data);
    }
}
