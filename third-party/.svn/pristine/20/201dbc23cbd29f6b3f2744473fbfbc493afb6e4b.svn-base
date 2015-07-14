package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.tabledata.AbstractDataColumnTest;
import com.pb.sawdust.tabledata.DataColumn;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author crf <br/>
 *         Started: Sep 26, 2008 7:50:11 AM
 */
abstract public class SqlDataColumnTest<T> extends AbstractDataColumnTest<T> {
    protected SqlDataColumn<T> sqlColumn;

    protected abstract SqlDataSet getSqlDataSet();

    protected ResultSet getResultSet(T[] columnData, String columnName) {
        String tableName = "a_table";
        SqlTableSchema schema = new SqlTableSchema(tableName);
        SqlDataSet sqlDataSet  = getSqlDataSet();
        //sqlDataSet.executeSqlUpdate("DROP TABLE IF EXISTS " + SqlTableDataUtil.formQuotedIdentifier(tableName,sqlDataSet.getIdentifierQuote()));
        dropTableIfExistsFromDatabase(tableName,sqlDataSet);
        sqlDataSet.addTable(schema);
        sqlDataSet.getTable(tableName).addColumn(columnName,columnData,getColumnDataType());
        return sqlDataSet.executeSqlQuery("SELECT * FROM " + SqlTableDataUtil.formQuotedIdentifier(tableName,sqlDataSet.getIdentifierQuote()));
    }

    protected void dropTableIfExistsFromDatabase(String tableName, SqlDataSet sqlDataSet) {
        sqlDataSet.executeSqlUpdate("DROP TABLE IF EXISTS " + SqlTableDataUtil.formQuotedIdentifier(tableName,sqlDataSet.getIdentifierQuote()));
    }

    protected DataColumn<T> getDataColumn(T[] columnData, String columnName) {
        ResultSet rs = null;
        try {
            rs = getResultSet(columnData,columnName);
            return new SqlDataColumn<T>(rs,getColumnDataType(),columnName,getTableKey());
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    //ignore
                }
        }
    }

    @Before
    public void beforeTest() {
        super.beforeTest();
        sqlColumn = (SqlDataColumn<T>) column;
    }

    @Test
    public void testIndexConstructorLabel() {
         ResultSet rs = null;
        try {
            rs = getResultSet(columnData,columnName);
            sqlColumn = new SqlDataColumn<T>(rs,getColumnDataType(),1,getTableKey());
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    //ignore
                }
        }
        assertEquals(columnName,sqlColumn.getLabel());
    }

    @Test
    public void testIndexConstructorData() {
         ResultSet rs = null;
        try {
            rs = getResultSet(columnData,columnName);
            sqlColumn = new SqlDataColumn<T>(rs,getColumnDataType(),1,getTableKey());
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    //ignore
                }
        }
        assertArrayAlmostEquals(columnData,sqlColumn.getData());
    }

    @Test
    public void testSimpleConstructorLabel() {
         ResultSet rs = null;
        try {
            rs = getResultSet(columnData,columnName);
            sqlColumn = new SqlDataColumn<T>(rs,getColumnDataType(),getTableKey());
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    //ignore
                }
        }
        assertEquals(SqlDataTable.INTERNAL_ROW_NUMBER_KEY_COLUMN_LABEL,sqlColumn.getLabel());
    }

    @Test
    public void testSetResultSet1ColumnName() {
        T[] columnData = getColumnData();
        T[] newData = random.randomizeArray(columnData);
        ResultSet rs = getResultSet(newData,columnName);
        try {
            sqlColumn.setResultSet(rs);
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    //ignore
                }
        }
        assertEquals(SqlDataTable.INTERNAL_ROW_NUMBER_KEY_COLUMN_LABEL,sqlColumn.getLabel());
    }

    @Test
    public void testSetResultSet2ColumnName() {
        T[] columnData = getColumnData();
        T[] newData = random.randomizeArray(columnData);
        ResultSet rs = getResultSet(newData,columnName);
        try {
            sqlColumn.setResultSet(rs,columnName);
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    //ignore
                }
        }
        assertEquals(columnName,sqlColumn.getLabel());
    }

    @Test
    public void testSetResultSet2ColumnData() {
        T[] columnData = getColumnData();
        T[] newData = random.randomizeArray(columnData);
        ResultSet rs = getResultSet(newData,columnName);
        try {
            sqlColumn.setResultSet(rs,columnName);
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    //ignore
                }
        }
        assertArrayAlmostEquals(newData,sqlColumn.getData());
    }

    @Test
    public void testSetResultSet3ColumnName() {
        T[] columnData = getColumnData();
        T[] newData = random.randomizeArray(columnData);
        ResultSet rs = getResultSet(newData,columnName);
        try {
            sqlColumn.setResultSet(rs,1);
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    //ignore
                }
        }
        assertEquals(columnName,sqlColumn.getLabel());
    }

    @Test
    public void testSetResultSet3ColumnData() {
        T[] columnData = getColumnData();
        T[] newData = random.randomizeArray(columnData);
        ResultSet rs = getResultSet(newData,columnName);
        try {
            sqlColumn.setResultSet(rs,1);
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    //ignore
                }
        }
        assertArrayAlmostEquals(newData,sqlColumn.getData());
    }
}
