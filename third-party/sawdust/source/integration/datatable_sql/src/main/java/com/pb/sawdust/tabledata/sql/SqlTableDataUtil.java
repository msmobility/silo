package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.tabledata.metadata.DataType;

import javax.sql.rowset.CachedRowSet;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.sun.rowset.CachedRowSetImpl;

/**
 * The {@code SqlTableDataUtil} provides a series of static methods and classes which are of general use when working
 * with the classes in the {@code sql} package. It includes methods pertaining to data type equivalency,
 * generating sql statements, and creating {@code javax.sql.rowset.CachedRowSet}s, as well as a helper class for
 * iterating over {@code java.sql.ResultSet} instances.
 *  
 * @author crf <br/>
 *         Started: May 12, 2008 2:11:04 PM
 */
public class SqlTableDataUtil {

    private static Map<Integer,DataType> sqlTypeToDataTypes;
    private static Map<DataType,Integer> dataTypesToSqlType;

    static {
        sqlTypeToDataTypes = new HashMap<Integer,DataType>();
        dataTypesToSqlType = new HashMap<DataType,Integer>();
        for (DataType type : DataType.values()) {
            switch (type) {
                case BOOLEAN : dataTypesToSqlType.put(type, Types.BOOLEAN); break;
                case BYTE : dataTypesToSqlType.put(type,Types.TINYINT); break;
                case SHORT : dataTypesToSqlType.put(type,Types.SMALLINT); break;
                case INT : dataTypesToSqlType.put(type,Types.INTEGER); break;
                case LONG : dataTypesToSqlType.put(type,Types.BIGINT); break;
                case FLOAT : dataTypesToSqlType.put(type,Types.FLOAT); break;
                case DOUBLE : dataTypesToSqlType.put(type,Types.DOUBLE); break;
                case STRING : dataTypesToSqlType.put(type,Types.LONGVARCHAR); break;
            }
        }
        for (DataType type : dataTypesToSqlType.keySet()) {
            sqlTypeToDataTypes.put(dataTypesToSqlType.get(type),type);
        }
        sqlTypeToDataTypes.put(Types.ARRAY,null);
        sqlTypeToDataTypes.put(Types.BIGINT,DataType.LONG);
        sqlTypeToDataTypes.put(Types.BINARY,null);
        sqlTypeToDataTypes.put(Types.BIT,DataType.BYTE);
        sqlTypeToDataTypes.put(Types.BLOB,null);
        sqlTypeToDataTypes.put(Types.BOOLEAN,DataType.BOOLEAN);
        sqlTypeToDataTypes.put(Types.CHAR,DataType.STRING);
        sqlTypeToDataTypes.put(Types.CLOB,DataType.STRING);
        sqlTypeToDataTypes.put(Types.DATALINK,null);
        sqlTypeToDataTypes.put(Types.DATE,null);
        sqlTypeToDataTypes.put(Types.DECIMAL,DataType.DOUBLE); //loss of precision, but ok
        sqlTypeToDataTypes.put(Types.DISTINCT,null);
        sqlTypeToDataTypes.put(Types.DOUBLE,DataType.DOUBLE);
        sqlTypeToDataTypes.put(Types.FLOAT,DataType.FLOAT);
        sqlTypeToDataTypes.put(Types.INTEGER,DataType.INT);
        sqlTypeToDataTypes.put(Types.JAVA_OBJECT,null);
        sqlTypeToDataTypes.put(Types.LONGNVARCHAR,DataType.STRING);
        sqlTypeToDataTypes.put(Types.LONGVARBINARY,null);
        sqlTypeToDataTypes.put(Types.LONGVARCHAR, DataType.STRING);
        sqlTypeToDataTypes.put(Types.NCHAR,DataType.STRING);
        sqlTypeToDataTypes.put(Types.NCLOB,DataType.STRING);
        sqlTypeToDataTypes.put(Types.NULL,null);
        sqlTypeToDataTypes.put(Types.NUMERIC,null);
        sqlTypeToDataTypes.put(Types.NVARCHAR,DataType.STRING);
        sqlTypeToDataTypes.put(Types.OTHER,null);
        sqlTypeToDataTypes.put(Types.REAL,DataType.DOUBLE);
        sqlTypeToDataTypes.put(Types.REF,null);
        sqlTypeToDataTypes.put(Types.ROWID,null);
        sqlTypeToDataTypes.put(Types.SMALLINT,DataType.SHORT);
        sqlTypeToDataTypes.put(Types.SQLXML,null);
        sqlTypeToDataTypes.put(Types.STRUCT,null);
        sqlTypeToDataTypes.put(Types.TIME,null);
        sqlTypeToDataTypes.put(Types.TIMESTAMP,null);
        sqlTypeToDataTypes.put(Types.TINYINT,DataType.BYTE);
        sqlTypeToDataTypes.put(Types.VARBINARY,null);
        sqlTypeToDataTypes.put(Types.VARCHAR,DataType.STRING);
    }

    /**
     * Get the data type that corresponds to a given SQL data type specified in {@code java.sql.Types}.
     *
     * @param sqlType
     *        The sql type constant as specified in {@code java.sql.Types}.
     *
     * @return the data type corresponding to {@code sqlType}. If no type corresonds, then {@code null} is returned.
     *
     * @throws IllegalArgumentException if {@code sqlType} does not correspond to a known sql type from java.sql.Types.
     */
    public static DataType getDataType(int sqlType) {
        if (sqlTypeToDataTypes.containsKey(sqlType))
            return sqlTypeToDataTypes.get(sqlType);
        else
            throw new IllegalArgumentException("Unknown java.sql.Types type: " + sqlType);
    }

    /**
     * Get the sql type (as specified in {@code java.sql.Types}) corresponding to a given data type. This method
     * provides the preferred mapping from data types to sql types.
     *
     * @param type
     *        The data type.
     *
     * @return the sql type corresponding to {@code type}.
     */
    public static int getSqlType(DataType type) {
        return dataTypesToSqlType.get(type);
    }

    /**
     * Get the column data definition (for a SQL {@code CREATE TABLE...} statemnet) for a given data type.
     *
     * @param columnType
     *        The column data type.
     *
     * @return the sql data type definition corresponding to {@code columnType}.
     */
    public static String getColumnDefinition(DataType columnType) {
        switch (columnType) {
            case BOOLEAN : return "BOOLEAN";
            case BYTE : return "TINYINT";
            case SHORT : return "SMALLINT";
            case INT : return "INTEGER";
            case LONG : return "BIGINT";
            case DOUBLE : return "DOUBLE";
            case FLOAT : return "FLOAT";
            case STRING : return "LONGVARCHAR";
            default : return null;
        }
    }

    /**
     * Form a quoted sql identifier.
     *
     * @param identifier
     *        The sql identifier.
     *
     * @param identifierQuote
     *        The string used as quote symbols by the sql syntax.
     *
     * @return a string with {@code identifier} quoted using {@code identifierQuote}.
     */
    public static String formQuotedIdentifier(String identifier, String identifierQuote) {
        return identifierQuote + identifier + identifierQuote;
    }

    /**
     * Get a cached rowset from an input result set. The cached rowset acts as a "snapshot" of the result set which is
     * no longer connected to the underlying database. This means that updates to the database will not be reflected in
     * the cached rowset, but it also means the rowset is not dependant on a database connection to access its data.
     * <p>
     * This method requires that a result set be of type {@code java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE}, as it
     * returns the input result set in the same state it was given in (<i>i.e.</i> the cursor's row pointer before and
     * after the method call remains the same). To get a cached rowset for forward-only or scroll-sensitive result sets,
     * use the {@code getCachedRowFromNonscrollable} method in this class.
     *
     * @param resultSet
     *        The input result set.
     *
     * @return a cached rowset representing the data in {@code resultSet}.
     */
    public static CachedRowSet getCachedRowSet(ResultSet resultSet) {
        try {
            if (resultSet.getType() == ResultSet.TYPE_FORWARD_ONLY)
                return getCachedRowFromNonscrollable(resultSet);
            CachedRowSet rowSet = new CachedRowSetImpl();
            //get current resultSet position
            int currentResultSetPosition = resultSet.getRow();
            rowSet.populate(resultSet);
            //return rowset position
            if (currentResultSetPosition == 0)
                resultSet.beforeFirst();
            else
                resultSet.absolute(currentResultSetPosition);
            return rowSet;
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    /**
     * Get a cached rowset from an input result set which is non-scrollable. This method is identical to
     * {@code getCachedRowSet(java.sql.ResultSet)}, except that the input result set's cursor row pointer is not set
     * back to its original place at the and of the call (because it presumably cannot be). The cached rowset returned
     * by this method <i>is</i> scrollable (of type {@code java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE}).
     *
     * @param resultSet
     *        The input result set.
     *
     * @return a cached rowset representing the data in {@code resultSet}.
     *
     * @see #getCachedRowSet(java.sql.ResultSet)
     */
    public static CachedRowSet getCachedRowFromNonscrollable(ResultSet resultSet) {
//         public static CachedRowSet getCachedRowSet(ResultSet resultSet) {
        try {
            CachedRowSet rowSet = new CachedRowSetImpl();
            rowSet.populate(resultSet);
            rowSet.setType(ResultSet.TYPE_SCROLL_INSENSITIVE);
            return rowSet;
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }

    }

    /**
     * Get the value of a cell in a result set for a given column with a stated data type. This method assumes that the
     * result set's cursor has already been set at the row of interest. The stated type must match to to the generic
     * parameter {@code T}, if specified (so {@code T} must correspond to {@code type.getObjectClass()}). If the data
     * type of the column is not known, then {@code getCellValue(java.sql.ResultSet, int)} can be used instead of this method.
     *
     * @param resultSet
     *        The result set from which the cell data is to be retrieved.
     *
     * @param type
     *        The data type of the cell's column.
     *
     * @param columnIndex
     *        The (0-based) index of the cell's column.
     *
     * @param <T>
     *        The type of the returned value.
     *
     * @return the value of the cell.
     *
     * @see #getCellValue(java.sql.ResultSet, int)
     */
    @SuppressWarnings("unchecked") //suppressed because documentation states that type and T must match
    public static <T> T getCellValue(ResultSet resultSet, DataType type, int columnIndex) {
        //assumes result set has been set to correct spot
        int sqlColumnIndex = columnIndex + 1;
        try {
            switch (type) {
                case BOOLEAN : return (T) (Boolean) resultSet.getBoolean(sqlColumnIndex);
                case BYTE : return (T) (Byte) resultSet.getByte(sqlColumnIndex);
                case SHORT : return (T) (Short) resultSet.getShort(sqlColumnIndex);
                case INT : return (T) (Integer) resultSet.getInt(sqlColumnIndex);
                case LONG : return (T) (Long) resultSet.getLong(sqlColumnIndex);
                case FLOAT : return (T) (Float) resultSet.getFloat(sqlColumnIndex);
                case DOUBLE : return (T) (Double) resultSet.getDouble(sqlColumnIndex);
                case STRING : return (T) resultSet.getString(sqlColumnIndex);
                default : return null;
            }
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    /**
     * Get the value of a cell in a result set for a given column. This method assumes that the result set's cursor
     * has already been set at the row of interest. If the data type of the column is already known, then
     * {@code getCellValue(java.sql.ResultSet, com.pb.sawdust.tabledata.metadata.DataType, int)} should be used instead of this method, as
     * it is more efficient (it does not need to query the result set to get the column's type).
     *
     * @param resultSet
     *        The result set from which the cell data is to be retrieved.
     *
     * @param columnIndex
     *        The (0-based) index of the cell's column.
     *
     * @param <T>
     *        The type of the returned value.
     *
     * @return the value of the cell.
     *
     * @see #getCellValue(java.sql.ResultSet,com.pb.sawdust.tabledata.metadata.DataType, int)
     */
    public static <T> T getCellValue(ResultSet resultSet, int columnIndex) {
        try {
            return SqlTableDataUtil.<T>getCellValue(resultSet,getDataType(resultSet.getMetaData().getColumnType(columnIndex)),columnIndex);
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    /**
     * The {@code ResultSetIterator} class provides an iterator used to cycle over a {@code java.sql.ResultSet}. What
     * it actually iterates over depends on the implementation, but this class is intended to reduce the programming
     * burden by requiring only the implementation of the {@code getNextIteratorValue()} method.  This iterator
     * should only be used on result sets of type {@code java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE}.
     *
     * @param <I>
     *        The type of element returned during each iteration cycle.
     */
    public static abstract class ResultSetIterator<I> implements Iterator<I> {
        protected ResultSet resultSet;
        private boolean incremented = false;

        /**
         * Constructor taking the result set to iterate over as an argument.
         *
         * @param resultSet
         *        The result set to iterate over.
         */
        public ResultSetIterator(ResultSet resultSet) {
            this.resultSet = resultSet;
            try {
                resultSet.beforeFirst();
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public boolean hasNext() {
            //can only increment if last increment already been used (incremented = false)
            try {
                if (!incremented)
                    incremented = resultSet.next();
                return incremented;
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public I next() {
            if (hasNext()) {
                incremented = false;
                return getNextIteratorValue();
            } else {
                throw new NoSuchElementException();
            }
        }

        /**
         * Get the next value in the iterator. It should be assumed that when this method is called, the result set is
         * already pointing at the "next" row; that is, this method need not (and should not) increment the result set
         * by calling {@code ResultSet.next()}.
         *
         * @return the next value in the iteration.
         */
        abstract I getNextIteratorValue();

        /**
         * Not supported by this iterator.  Throws an {@code UnsupportedOperationException}.
         */
        public void remove() {
            throw new UnsupportedOperationException("ResultSetIterator does not support remove method.");
        }
    }
}
