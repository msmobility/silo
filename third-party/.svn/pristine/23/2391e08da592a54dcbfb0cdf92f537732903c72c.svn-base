package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.AbstractDataRowTest;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author crf <br/>
 *         Started: Sep 28, 2008 2:55:30 PM
 */
abstract public class SqlDataRowTest extends AbstractDataRowTest {
    protected SqlDataRow sqlDataRow;
    protected Object[] differentDataRow = new Object[0];

    protected abstract SqlDataSet getSqlDataSet();

    protected Object[] getDifferentRowOfData() {
        return testData.getTableData(getNumberOfRows())[random.nextInt(getNumberOfRows())];
    }
    
    protected ResultSet getResultSet(Object[] rowData) {
        //return a result set with two rows of data
        String tableName = "a_table";
        SqlTableSchema schema = new SqlTableSchema(tableName,getColumnLabels(),getColumnTypes());
        SqlDataSet sqlDataSet  = getSqlDataSet();
//        sqlDataSet.executeSqlUpdate("DROP TABLE IF EXISTS " + SqlTableDataUtil.formQuotedIdentifier(tableName,sqlDataSet.getIdentifierQuote()));
        dropTableIfExistsFromDatabase(tableName,sqlDataSet);
        sqlDataSet.addTable(schema);
        differentDataRow = getDifferentRowOfData();
        SqlDataTable table = sqlDataSet.getTable(tableName);
        table.addRow(rowData);
        table.addRow(differentDataRow);
        return sqlDataSet.executeSqlQuery("SELECT " + table.getSchema().getVisibleColumnList(sqlDataSet.getIdentifierQuote()) + " FROM " + SqlTableDataUtil.formQuotedIdentifier(tableName,sqlDataSet.getIdentifierQuote()));
    }

    protected void dropTableIfExistsFromDatabase(String tableName, SqlDataSet sqlDataSet) {
        sqlDataSet.executeSqlUpdate("DROP TABLE IF EXISTS " + SqlTableDataUtil.formQuotedIdentifier(tableName,sqlDataSet.getIdentifierQuote()));
    }

    protected DataRow getDataRow(Object[] rowData) {
        ResultSet rs = null;
        try {
            rs = getResultSet(rowData);
            return new SqlDataRow(rs,0,getColumnTypes(),getColumnLabels());
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                //ignore
            }
        }
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        sqlDataRow = (SqlDataRow) row;
    }

    @Test
    public void testSetResultSet() {
        Object[] newRow = getDifferentRowOfData();
        ResultSet rs = null;
        try {
            rs = getResultSet(newRow);
            sqlDataRow.setResultSet(rs,0);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                //ignore
            }
        }
        assertArrayAlmostEquals(newRow,sqlDataRow.getData());
    }

    @Test
    public void testSetResultSetRow() {
        sqlDataRow.setResultSetRow(1);
        assertArrayAlmostEquals(differentDataRow,sqlDataRow.getData());
    }
}
