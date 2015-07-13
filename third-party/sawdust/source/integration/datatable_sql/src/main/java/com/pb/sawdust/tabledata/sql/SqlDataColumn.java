package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.AbstractDataColumn;
import com.pb.sawdust.tabledata.TableKey;

import javax.sql.rowset.CachedRowSet;
import javax.sql.RowSet;
import java.util.Iterator;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;


/**
 * The {@code SqlDataColumn} class provides a {@code DataColumn} implemenatation for SQL (database-backed) data tables.
 * The constructors take a {@code ResultSet} object, which provide access to the data via a JDBC connection. An instance
 * of this class should be looked as a "snapshot" of the data, not a pointer to it; changing the data/information in
 * a {@code SqlDataColumn} instance will not alter the underlying data (specific {@code DataTable} methods should be
 * used for that). A given data column may be reused for columns viewed from <i>identically structured</i> result sets.
 * This can help eliminate repetitive object initializations, which load result set metadata which are shared across
 * similar sql data queries.  This functionality is accessed through the {@code setResultSet} methods.
 * <p>
 * The iterator for this class is, by default, not threadsafe.  However, a flag may be set which will cause the iterator
 * to switch to threadsafe (and less efficient) mode.
 * @author crf <br/>
 *         Started: May 8, 2008 9:58:22 PM
 */
public class SqlDataColumn<T> extends AbstractDataColumn<T> {
    private String columnLabel;
    private CachedRowSet rowSet = null;
    private int columnIndex; //these are indexed by 0, but the jdbc sql stuff is indexed by 0's, so jdbc calls needing this add one to it as needed
    private DataType type;
    private T[] column = null;
    private boolean iteratorThreadsafe = false; //should be quicker, but not "synchronized"
    private int rowCount;
    private TableKey<?> primaryKey;

    /**
     * Constructor specifying the result set and various column level data about the data column, including its label.
     *
     * @param resultSet
     *        The result set which will provide access to the column data.
     *
     * @param type
     *        The data type of the column.
     *
     * @param columnLabel
     *        The label of the column that the object will represent.
     *
     * @param primaryKey
     *        The primary key to use for the column.
     */
    public SqlDataColumn(ResultSet resultSet, DataType type, String columnLabel, TableKey<?> primaryKey) {
        this.columnLabel = columnLabel;
        this.type = type;
        this.primaryKey = primaryKey;
        setResultSet(resultSet,columnLabel);
    }

    /**
     * Constructor specifying the result set and various column level data about the data column, including its index
     * in the result set.
     *
     * @param resultSet
     *        The result set which will provide access to the column data.
     *
     * @param type
     *        The data type of the column.
     *
     * @param columnIndex
     *        The (0-based) index of the column in the result set (not the underlying table) that the object will represent.
     *
     * @param primaryKey
     *        The primary key to use for the column.
     */
    public <K> SqlDataColumn(ResultSet resultSet, DataType type, int columnIndex, TableKey<K> primaryKey) {
        try {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            columnLabel = rsmd.getColumnLabel(columnIndex+1);
            this.type = type;
            this.primaryKey = primaryKey;
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
        setResultSet(resultSet,columnIndex);
    }

    /**
     * Constructor specifying the result set and various column level data about the data column which uses the first
     * column in the result set to build the sql data column from. This is best used as a convenience constructor
     * for result sets containing only one column of data.
     *
     * @param resultSet
     *        The result set which will provide access to the column data.
     *
     * @param type
     *        The data type of the column.
     *
     * @param primaryKey
     *        The primary key to use for the column.
     */

    public <K> SqlDataColumn(ResultSet resultSet, DataType type, TableKey<K> primaryKey) {
        this(resultSet,type,0,primaryKey);
    }

    /**
     * Set the result set for this data column using the column label as the column identifier. The result set
     * <i>must</i> have the same structure as that used to initially create this instance.  This method allows the
     * reuse of {@code SqlDataColumn} instances, and using it is somewhat more efficient than constructing a new
     * {@code SqlDataColumn} instance. A cached copy of the result set data wil be maintained, so the input result set
     * may (should) be closed after calling this method.
     *
     * @param resultSet
     *        The result set which will provide access to the column data.
     *
     * @param columnLabel
     *        The label of the column that this object will represent.
     */
    public void setResultSet(ResultSet resultSet, String columnLabel) {
        try {
            setResultSet(resultSet,resultSet.findColumn(columnLabel)-1);
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    /**
     * Set the result set for this data column using the column index as the column identifier. The result set
     * <i>must</i> have the same structure as that used to initially create this instance.  This method allows the
     * reuse of {@code SqlDataColumn} instances. A cached copy of the result set data wil be maintained, so the
     * input result set may (should) be closed after calling this method.
     *
     * @param resultSet
     *        The result set to use for this data column.
     *
     * @param columnIndex
     *        The (0-based) index of the column that this object will represent.
     */
    public void setResultSet(ResultSet resultSet, int columnIndex) {
        try {
            this.columnIndex = columnIndex;
            if (rowSet != null)
                rowSet.close();
            rowSet = SqlTableDataUtil.getCachedRowSet(resultSet);
            //ResultSetMetaData rsmd = resultSet.getMetaData();
            ResultSetMetaData rsmd = rowSet.getMetaData();
            columnLabel = rsmd.getColumnLabel(columnIndex+1);
            setRowCount();
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    /**
     * Set the result set for this data column assuming that the first column in the result set is to be used. The
     * result set <i>must</i> have the same structure as that used to initially create this instance.  This method
     * allows the reuse of {@code SqlDataColumn} instances. This method should generally be used as a convenience
     * method for result sets containing only one column of data. A cached copy of the result set data wil be maintained,
     * so the input result set may (should) be closed after calling this method.
     *
     * @param resultSet
     *        The result set to use for this data column.
     */
    public void setResultSet(ResultSet resultSet) {
        setResultSet(resultSet,0);
    }

    /**
     * Set the flag which determines whether the iterator used by this class is threadsafe or not. A threadsafe
     * iterator is (as expected) less efficient than a non-threadsafe iterator.
     *
     * @param iteratorThreadsafe
     *        {@code true} if the iterator is to be threadsafe, {@code false} if not.
     */
    public void setIteratorThreadsafe(boolean iteratorThreadsafe) {
        this.iteratorThreadsafe = iteratorThreadsafe;
    }

    /**
     * Set the number of rows in this data column.  This method is necessary because result sets do not hold iternal
     * row counts.
     */
    private void setRowCount() {
        try {
            rowSet.last();
            rowCount = rowSet.getRow();
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    public DataType getType() {
        return type;
    }

    public String getLabel() {
        return columnLabel;
    }

    public int getRowCount() {
        return rowCount;
    }

    public T[] getData() {
        setColumn();
        return column;
    }

    @SuppressWarnings("unchecked") //T and type will match
    private void setColumn() {
        if (column == null) {
            try {
                rowSet.beforeFirst();
                int counter = 0;
//                int sqlColumnIndex = columnIndex + 1;
                column = (T[]) type.getObjectArray(rowCount);
                while (rowSet.next())
                    column[counter++] = SqlTableDataUtil.<T>getCellValue(rowSet,type,columnIndex);
//                switch (type) {
//                    case BOOLEAN : {
//                        Boolean[] array = new Boolean[rowCount];
//                        while (rowSet.next()) {
//                            array[counter++] = rowSet.getBoolean(sqlColumnIndex);
//                        }
//                        column = (T[]) array;
//                        break;
//                    }
//                    case BYTE : {
//                        Byte[] array = new Byte[rowCount];
//                        while (rowSet.next()) {
//                            array[counter++] = rowSet.getByte(sqlColumnIndex);
//                        }
//                        column = (T[]) array;
//                        break;
//                    }
//                    case SHORT : {
//                        Short[] array = new Short[rowCount];
//                        while (rowSet.next()) {
//                            array[counter++] = rowSet.getShort(sqlColumnIndex);
//                        }
//                        column = (T[]) array;
//                        break;
//                    }
//                    case INT : {
//                        Integer[] array = new Integer[rowCount];
//                        while (rowSet.next()) {
//                            array[counter++] = rowSet.getInt(sqlColumnIndex);
//                        }
//                        column = (T[]) array;
//                        break;
//                    }
//                    case LONG : {
//                        Long[] array = new Long[rowCount];
//                        while (rowSet.next()) {
//                            array[counter++] = rowSet.getLong(sqlColumnIndex);
//                        }
//                        column = (T[]) array;
//                        break;
//                    }
//                    case FLOAT : {
//                        Float[] array = new Float[rowCount];
//                        while (rowSet.next()) {
//                            array[counter++] = (float) rowSet.getDouble(sqlColumnIndex);
//                        }
//                        column = (T[]) array;
//                        break;
//                    }
//                    case DOUBLE : {
//                        Double[] array = new Double[rowCount];
//                        while (rowSet.next()) {
//                            array[counter++] = rowSet.getDouble(sqlColumnIndex);
//                        }
//                        column = (T[]) array;
//                        break;
//                    }
//                    case STRING : {
//                        String[] array = new String[rowCount];
//                        while (rowSet.next()) {
//                            array[counter++] = rowSet.getString(sqlColumnIndex);
//                        }
//                        column = (T[]) array;
//                        break;
//                    }
//                }
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }
    }

    public T getCell(int rowIndex) {
        try {
            rowSet.absolute(rowIndex+1); //sql indexed by 1, we are indexed by 0
            return getCellValue();
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    public Iterator<T> iterator() {
        RowSet iteratorRowSet;
        if (iteratorThreadsafe) {
            //I think this will create a new copy seperate from the main rowset so synchronization need not be carried out
            iteratorRowSet = SqlTableDataUtil.getCachedRowSet(rowSet);
        } else {
            iteratorRowSet = rowSet;
        }
        return new SqlTableDataUtil.ResultSetIterator<T>(iteratorRowSet) {
            @SuppressWarnings("unchecked") //suppressed because ColumnData doc requires that T match with column
            public T getNextIteratorValue() {
                return (T) SqlTableDataUtil.getCellValue((RowSet) resultSet,type,columnIndex);
            }
        };
    }

    @SuppressWarnings("unchecked") //suppressed because ColumnData doc requires that T match with column
    private T getCellValue(){
        return (T) SqlTableDataUtil.getCellValue(rowSet,type,columnIndex);
    }

    @SuppressWarnings("unchecked") //suppressed because a TableDataException is thrown if type check fails
    public <K> T getCellByKey(K key) {
        return getCell(((TableKey<K>) primaryKey).getRowNumber(key));
    }

}
