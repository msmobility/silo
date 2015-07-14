package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.tabledata.AbstractDataRow;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.TableDataException;

import javax.sql.rowset.CachedRowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.util.HashMap;

/**
 * The {@code SqlDataRow} class provides a {@code DataRow} implemenatation for SQL (database-backed) data tables. The
 * constructor takes a {@code ResultSet} object, which provide access to the data via a JDBC connection. An instance
 * of this class should be looked as a "snapshot" of the data, not a pointer to it; changing the data/information in
 * a {@code SqlDataRow} instance will not alter the underlying data (specific {@code DataTable} methods should be
 * used for that). A given data column may be reused for rows viewed from <i>identically structured</i> result sets.
 * This can help eliminate repetitive object initializations, which load result set metadata which are shared across
 * similar sql data queries.  This functionality is accessed through the {@code setResultSet} methods. Additionally,
 * the result set row that a sql data row refers to may be changed (using the {@code setResultSetRow}) which can help
 * eliminate repetitive (and unnecessary) sql queries to get identical result sets.
 *
 * @author crf <br/>
 *         Started: May 12, 2008 11:18:35 AM
 */
public class SqlDataRow extends AbstractDataRow {
    private int columnCount;
    private String[] columnLabels;
    private Map<String,Integer> colummIndices;
    private DataType[] columnTypes;
    private Object[] row = null;
    private CachedRowSet rowSet = null;

    /**
     * Constructor specifying the result set and row number for the sql data row.
     *
     * @param resultSet
     *        The result set which will provide access to the row data.
     *
     * @param rowNumber
     *        The (0-based) row number of the result set to use for the data row.
     */
    public SqlDataRow(ResultSet resultSet, int rowNumber) {
        try {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            columnCount = rsmd.getColumnCount();
            columnLabels = new String[columnCount];
            columnTypes = new DataType[columnCount];
            colummIndices = new HashMap<String,Integer>();
            for (int i = 0; i < columnCount; i++) {
                columnLabels[i] = rsmd.getColumnLabel(i+1);
                columnTypes[i] = SqlTableDataUtil.getDataType(rsmd.getColumnType(i+1));
                colummIndices.put(columnLabels[i],i);
            }
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
        setResultSet(resultSet,rowNumber);
    }

    public SqlDataRow(ResultSet resultSet, int rowNumber, DataType[] columnTypes, String[] columnLabels) {
        columnCount = columnTypes.length;
        colummIndices = new HashMap<String,Integer>();
        for (int i = 0; i < columnCount; i++)
            colummIndices.put(columnLabels[i],i);
        this.columnTypes = columnTypes;
        this.columnLabels = columnLabels;
        setResultSet(resultSet,rowNumber);
    }

    /**
     * Set the result set for this data row. The result set <i>must</i> have the same structure as that used to
     * initially create this instance.  This method allows the reuse of {@code SqlDataRow} instances, and using it
     * is somewhat more efficient than constructing a new {@code SqlDataRow} instance. A cached copy of the input
     * result set data will be maintained, so the result set can (should) be closed after calling this method.
     *
     * @param resultSet
     *        The result set which will provide access to the row data.
     *
     * @param rowNumber
     *        The (0-based) row number of the result set to use for the data row.
     */
    public void setResultSet(ResultSet resultSet, int rowNumber) {
        try {
            if (rowSet != null)
                rowSet.close();
            rowSet = SqlTableDataUtil.getCachedRowSet(resultSet);
            setResultSetRow(rowNumber);
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    /**
     * Set the row number of the result set that this sql data row refers to.
     *
     * @param rowNumber
     *        The (0-based) row number of the result set to use for this data row.
     */
    public void setResultSetRow(int rowNumber) {
        try {
            rowSet.absolute(rowNumber+1);
            row = null;
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    private void setRowData() {
        //This method is used to set the row data only when needed (so that setResultSetRow doesn't force the row data
        //   to actually be set
        if (row == null) {
            //assumes rowset is pointed at correct row
            row = new Object[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = getCell(i);
            }
        }
    }

    public boolean hasColumn(String columnLabel) {
        return colummIndices.containsKey(columnLabel);
    }

    public int getColumnIndex(String columnLabel) {
        if (hasColumn(columnLabel))
            return colummIndices.get(columnLabel);
        else
            throw new TableDataException(TableDataException.COLUMN_NOT_FOUND,columnLabel);
    }

    public String[] getColumnLabels() {
        return columnLabels;
    }

    public DataType[] getColumnTypes() {
        return columnTypes;
    }

    public Object[] getData() {
        setRowData();
        return row;
    }

    @SuppressWarnings("unchecked") //suppressed because documentation states that T must match the column type
    public <T> T getCell(int columnIndex) {
        try {
            return (T) SqlTableDataUtil.getCellValue(rowSet,columnTypes[columnIndex],columnIndex);
        }  catch (ArrayIndexOutOfBoundsException e) {
            throw new TableDataException(TableDataException.COLUMN_INDEX_OUT_OF_BOUNDS,columnIndex);
        } catch (Exception e) {
            return null;
        }
    }
}
