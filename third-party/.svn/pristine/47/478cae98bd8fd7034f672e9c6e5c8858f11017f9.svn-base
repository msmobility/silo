package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.util.array.ArrayUtil;
import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.sql.IsolatedResultSet;
import com.pb.sawdust.util.collections.InjectiveMap;
import com.pb.sawdust.util.collections.InjectiveHashMap;
import com.pb.sawdust.tabledata.*;
import com.pb.sawdust.tabledata.read.TableReader;

import com.pb.sawdust.tabledata.metadata.DataType;

import java.sql.SQLException;
import java.util.*;
import java.lang.reflect.Array;

/**
 * The {@code SqlDataTable} class provides a {@code DataTable} implemenatation for SQL (database-backed) data tables.
 * Sql data tables are essentially views into tables in the underlying database (accessed through a {@code SqlDataSet},
 * so the table must exist in the database before a sql data table can be created. Thus, all constructors require a
 * sql data set, which represents the database holding the underlying table, or require one has been set in the
 * {@link SqlDataSetProvider} class.
 * <p>
 * Every sql data table must have a row number key which allows the table to identify rows by their numbers (<i>i.e.</i>
 * their "natural" ordering). This will be used as the ordering for data rows during iterating, for adding and
 * getting columns, and when using keys/indices. The default is to create an internal row number key column which is
 * invisible to the user and which uses the current order of the rows as their natural ordering. Alternatively, a column
 * in the table which contains row numbers may be sepecified (in the {@code SqlTableSchema} used to define the table}
 * and a key will be built automatically. This column must contain one entry for each row number, starting at {@code 0}
 * and finishing at {@code 1 - getRowCount}. Another alternative is to specify a column to use for the table ordering
 * using the {@code orderBy(String)} method. This will create a row number key which will match the table ordering
 * returned by a sql statement using the column with an <tt>ORDER BY</tt> clause.
 * <p>
 * The final alternative is to specify a {@code TableKey<Integer>} instance to use as the row number key. Unlike the
 * other methods, this alternative may cause the row ordering to differ from that of the row numbering (see
 * {@link #setRowNumberKey(com.pb.sawdust.tabledata.TableKey)} for details).  For this reason, this alternative should generally be
 * used only when a suitable row number field cannot be created (such as for previously existing tables).
 * <p>
 * Sql data tables, like database tables, are particularly suited to row-wise data operations.  Column-wise data
 * operations, especially adding data columns, are much less efficient. When column-wise data operations are required,
 * it is suggested that the sql data table be transferred to a data table better suited to such operaions (such as a
 * {@code ColumnDataTable}). The {@code Iterator<DataRow>} returned by the {@code iterator()} method in this class actually
 * iterates over {@code SqlDataRow}s, so the iterators elements may be cast to this data row class as needed.
 * <p>
 * As in a {@code SqlDataSet} sql queries and updates may be called through a sql data table. These queries/updates must
 * pass one or more {@code SqlFilter}s which determine whether or not it will be executed. The default filter used with
 * a data table simply requires that the table label (which matches the table name in the underlying database) is found
 * in the query/update.
 *
 * @author crf <br/>
 *         Started: May 12, 2008 9:45:11 PM
 */
public class SqlDataTable extends AbstractDataTable {

    /**
     * The name of the internal row number key column. This value should not be needed for direct use, except for
     * debugging, and (possibly) ensuring that no existing columns already use it as a label.
     */
    public static final String INTERNAL_ROW_NUMBER_KEY_COLUMN_LABEL = "row___number___key";

    private SqlTableSchema schema;
    private SqlDataSet dataSet;
    private Filter<String> sqlFilter;
    private String quotedTableLabel;
    private String quotedRowNumberKeyLabel;
    private TableKey<?> rowNumberKey = null;
    private boolean rowNumberKeySpoiled = true;
    private String orderByQuotedColumnLabel = null;

    /**
     * Constructor specifying the data set, table name, and sql filter. This constructor is for a view into a table that
     * already has been created in the data set (database).
     *
     * @param dataSet
     *        The data set holding the table.
     *
     * @param tableLabel
     *        The name of the table in {@code dataSet} which this table will represent.
     *
     * @param sqlFilter
     *        The sql filter to use with this table.
     */
    public SqlDataTable(SqlDataSet dataSet, String tableLabel, Filter<String> sqlFilter) {
        setClassData(dataSet, tableLabel, sqlFilter);
    }

    /**
     * Constructor specifying the data set and table name. This constructor is for a view into a table that already has
     * been created in the data set (database). The constructed sql data table will use the default sql filter (which
     * requires the table name to be in the query/update).
     *
     * @param dataSet
     *        The data set holding the table.
     *
     * @param tableLabel
     *        The name of the table in {@code dataSet} which this table will represent.
     */
    public SqlDataTable(SqlDataSet dataSet, String tableLabel) {
        this(dataSet,tableLabel,getDefaultSqlFilter(tableLabel));
    }

    /**
     * Constructor specifying the data set and table schema. This method will add the table to the specified data set
     * (database), so a table with the same name must not already exist in the data set. The constructed sql data table
     * will use the default sql filter (which requires the table name to be in the query/update).
     *
     * @param dataSet
     *        The data set which will hold the table.
     *
     * @param schema
     *        The schema describing the table structure.
     *
     * @throws TableDataException if a table with {@code table}'s label already exists in this data set.
     */
    public SqlDataTable(SqlDataSet dataSet, SqlTableSchema schema) {
        dataSet.addTable(schema);
        setClassData(dataSet,schema.getTableLabel(),getDefaultSqlFilter(schema.getTableLabel()));
    }

    /**
     * Constructor specifying the data set, table schema, and data to fill the table with. This method will add the table
     * to the specified data set (database), so a table with the same name must not already exist in the data set. The
     * data should be organized as required by the {@link #addDataByRow(Object[][])} method - its first dimension is the
     * row, and second dimension the column entries. The constructed sql data table will use the default sql filter (which
     * requires the table name to be in the query/update).
     *
     * @param dataSet
     *        The data set which will hold the table.
     *
     * @param schema
     *        The schema describing the table structure.
     *
     * @param data
     *        The data to fill the table with.  The first dimension is rows, and the second is columns.
     *
     * @throws TableDataException if a table with {@code table}'s label already exists in this data set.
     */
    public SqlDataTable(SqlDataSet dataSet, SqlTableSchema schema, Object[][] data) {
        this(dataSet,schema);
        addDataByRow(data);
    }

    /**
     * Constructor used to create a sql data table whose structure and data is identical to a given data table. This
     * method will add the table to the specified data set (database), so a table with the same name must not already
     * exist in the data set. This method uses the method {@code SqlDataSet.addTable(DataTable)} to add the table to the
     * data set, which the constructed sql data table then references. The constructed sql data table will use the default
     * sql filter (which requires the table name to be in the query/update).
     *
     * @param dataSet
     *        The data set which will hold the table.
     *
     * @param table
     *        The data table to transform.
     *
     * @throws TableDataException if a table with {@code table}'s label already exists in this data set.
     */
    public SqlDataTable(SqlDataSet dataSet, DataTable table) {
        dataSet.addTable(table);
        setClassData(dataSet,table.getLabel(),getDefaultSqlFilter(table.getLabel()));
    }

    /**
     * Constructor which uses a {@code TableReader} to define the table structure and load its data. This method will
     * add the table to the specified data set (database), so a table with the same name must not already exist in the
     * data set. The constructed sql data table will use the default sql filter (which requires the table name to be in
     * the query/update).
     *
     * @param dataSet
     *        The data set which will hold the table.
     *
     * @param reader
     *        The table reader to create the table from.
     */
    public SqlDataTable(SqlDataSet dataSet, TableReader reader) {
        this(dataSet,new SqlTableSchema(reader.getTableName(),reader.getColumnNames(),reader.getColumnTypes()),reader.getData());
    }

    /**
     * Constructor specifying the table name, and sql filter. The {@code SqlDataSet} used to hold this data table will
     * be retrieved from {@link SqlDataSetProvider}, so {@code SqlDataSetProvider.setSqlDataSet(SqlDataSet)} must be called
     * prior to this constructor. This constructor is for a view into a table that already has been created in the data
     * set (database).
     *
     * @param tableLabel
     *        The name of the table in data set which this table will represent.
     *
     * @param sqlFilter
     *        The sql filter to use with this table.
     */
    public SqlDataTable(String tableLabel, Filter<String> sqlFilter) {
        setClassData(SqlDataSetProvider.getSqlDataSet(), tableLabel, sqlFilter);
    }

    /**
     * Constructor specifying the table name. The {@code SqlDataSet} used to hold this data table will be retrieved from
     * {@link SqlDataSetProvider}, so {@code SqlDataSetProvider.setSqlDataSet(SqlDataSet)} must be called prior to this
     * constructor. This constructor is for a view into a table that already has been created in the data set (database).
     * The constructed sql data table will use the default sql filter (which requires the table name to be in the
     * query/update).
     *
     * @param tableLabel
     *        The name of the table in {@code dataSet} which this table will represent.
     */
    public SqlDataTable(String tableLabel) {
        this(SqlDataSetProvider.getSqlDataSet(), tableLabel,getDefaultSqlFilter(tableLabel));
    }

    /**
     * Constructor specifying the table schema. The {@code SqlDataSet} used to hold this data table will be retrieved from
     * {@link SqlDataSetProvider}, so {@code SqlDataSetProvider.setSqlDataSet(SqlDataSet)} must be called prior to this
     * constructor. This method will add the table to the data set (database), so a table with the same name must not
     * already exist in the data set. The constructed sql data table will use the default sql filter (which requires the
     * table name to be in the query/update).
     *
     * @param schema
     *        The schema describing the table structure.
     *
     * @throws TableDataException if a table with {@code table}'s label already exists in this data set.
     */
    public SqlDataTable(SqlTableSchema schema) {
        this(SqlDataSetProvider.getSqlDataSet(),schema);
    }

    /**
     * Constructor specifying the able schema and data to fill the table with. The {@code SqlDataSet} used to hold this
     * data table will be retrieved from {@link SqlDataSetProvider}, so {@code SqlDataSetProvider.setSqlDataSet(SqlDataSet)}
     * must be called prior to this constructor. This method will add the table to the data set (database), so
     * a table with the same name must not already exist in the data set. The data should be organized as required by
     * the {@link #addDataByRow(Object[][])} method - its first dimension is the row, and second dimension the column
     * entries. The constructed sql data table will use the default sql filter (which requires the table name to be in
     * the query/update).
     *
     * @param schema
     *        The schema describing the table structure.
     *
     * @param data
     *        The data to fill the table with.  The first dimension is rows, and the second is columns.
     *
     * @throws TableDataException if a table with {@code table}'s label already exists in this data set.
     */
    public SqlDataTable(SqlTableSchema schema, Object[][] data) {
        this(SqlDataSetProvider.getSqlDataSet(),schema,data);
    }

    /**
     * Constructor used to create a sql data table whose structure and data is identical to a given data table. The
     * {@code SqlDataSet} used to hold this data table will be retrieved from {@link SqlDataSetProvider}, so
     * {@code SqlDataSetProvider.setSqlDataSet(SqlDataSet)} must be called prior to this constructor.  This
     * method will add the table to the data set (database), so a table with the same name must not already
     * exist in the data set. This method uses the method {@code SqlDataSet.addTable(DataTable)} to add the table to the
     * data set, which the constructed sql data table then references. The constructed sql data table will use the default
     * sql filter (which requires the table name to be in the query/update).
     *
     * @param table
     *        The data table to transform.
     *
     * @throws TableDataException if a table with {@code table}'s label already exists in this data set.
     */
    public SqlDataTable(DataTable table) {
        this(SqlDataSetProvider.getSqlDataSet(),table);
    }

    /**
     * Constructor which uses a {@code TableReader} to define the table structure and load its data. The {@code SqlDataSet}
     * used to hold this data table will be retrieved from {@link SqlDataSetProvider}, so
     * {@code SqlDataSetProvider.setSqlDataSet(SqlDataSet)} must be called prior to this constructor. This method will
     * add the table to the data set (database), so a table with the same name must not already exist in the
     * data set. The constructed sql data table will use the default sql filter (which requires the table name to be in
     * the query/update).
     *
     * @param reader
     *        The table reader to create the table from.
     */
    public SqlDataTable(TableReader reader) {
        this(SqlDataSetProvider.getSqlDataSet(),reader);
    }

    private void setClassData(SqlDataSet dataSet, String tableLabel, Filter<String> sqlFilter) {
        this.dataSet = dataSet;
        schema = (SqlTableSchema) dataSet.getTableSchema(tableLabel);
        quotedTableLabel = formQuotedIdentifier(tableLabel);
        this.sqlFilter = sqlFilter;
        if (schema.usesColumnAsRowNumberKey())
            setRowNumberKey(getSimpleRowNumberKey(schema.getRowNumberKeyColumnLabel()));
    }

    /**
     * Set the row number key for this table. This key will be used to map row number references (<i>e.g.</i> in
     * {@code getRow(int)}) and as such should not use methods which use row numbers to build its index. This key's
     * table column will also be used for row ordering, and will replace any ordering set through a
     * {@code orderBy(String)} call. Note that this will order the table by the <i>key column</i> values, not the
     * row numbers as defined in the key. Thus, unless the column values, when ordered, matches the key row numbering,
     * the ordering of the rows will not match that of the row numbering.
     *
     * @param rowNumberKey
     *        The row number key to use for this table.
     */
    public void setRowNumberKey(TableKey<?> rowNumberKey) {
        this.rowNumberKey = rowNumberKey;
        quotedRowNumberKeyLabel = formQuotedIdentifier(rowNumberKey.getKeyColumnLabel());
        rowNumberKeySpoiled = true;
        orderByQuotedColumnLabel = null;
    }

    private TableKey<?> getRowNumberKey() {
        if (rowNumberKey == null)
            throw new TableDataException("Row number key not set in SqlDataTable: " + schema.getTableLabel());
        if (rowNumberKeySpoiled) {
            rowNumberKey.buildIndex();
            rowNumberKeySpoiled = false;
        }
        return rowNumberKey;
    }

    /**
     * Get the default primary key to be used for this table, which is the row number key as described in this class'
     * head description.
     *
     * @return the row number key for this table.
     */
    protected TableKey<?> getDefaultPrimaryKey() {
        return getRowNumberKey();
    }

    /**
     * Get a simple row number key. This method can be used to create a key when the default internal row number key
     * will not be used, and another column in the table may be used instead.  The idiom to use in such a case is:
     * <pre><tt>
     *     setRowNumberKey(getSimpleRowNumberKey(rowNumberKeyColumnLabel))
     * </tt></pre>
     *
     * @param rowNumberKeyColumnLabel
     *        The label of the column to use for the row number key.
     *
     * @return a table key which can be used as a row number key in this table.
     *
     * @throws TableDataException if the values in {@code rowNumberKeyColumnLabel} are not unique, or if the values in
                                  {@code rowNumberKeyColumnLabel} do not form the integer sequence on <tt>[0,getRowCount())</tt>.
     */
    public TableKey<Integer> getSimpleRowNumberKey(final String rowNumberKeyColumnLabel) {
        //rowNumberKeyColumn must have entries = 0...(1-rowCount), otherwise error will be thrown
        if (!schema.usesInternalRowNumberKey() && !hasColumn(rowNumberKeyColumnLabel))
            throw new TableDataException(TableDataException.COLUMN_NOT_FOUND,rowNumberKeyColumnLabel);
        return new AbstractTableKey<Integer>(rowNumberKeyColumnLabel,DataType.INT) {

            protected Integer[] getTypeArray(int size) {
                return new Integer[size];
            }

            protected InjectiveMap<Integer, Integer> getKeyIndex() {
                InjectiveMap<Integer,Integer> keyMap = new InjectiveHashMap<Integer,Integer>();
                Set<Integer> keyCheckList = new HashSet<Integer>();
                int rowCount = SqlDataTable.this.getRowCount();
                for (int i = 0; i < rowCount; i++)
                    keyCheckList.add(i);
                for (Integer value : SqlDataTable.this.getIntegerColumnData(rowNumberKeyColumnLabel)) {
                    try {
                        keyMap.put(value,value);
                    } catch (IllegalArgumentException e) {
                        throw new TableDataException(TableDataException.KEY_COLUMN_NOT_UNIQUE,rowNumberKeyColumnLabel);
                    }
                    keyCheckList.remove(value);
                }
                if (keyCheckList.size() > 0)
                    throw new TableDataException("SqlDataTable row number key column must have values from 0 to (1 - getRowCount()): " + rowNumberKeyColumnLabel);
                return keyMap;
            }
        };
    }

    private int[] getIntegerColumnData(String columnLabel) {
        //this is used internally both as a convenience function and to avoid issues with "invisible" columns and
        //   the need to access data before keys have been created
        int[] columnData = new int[getRowCount()];
        IsolatedResultSet column = null;
        try {
            column = dataSet.executeSqlQueryFilterless(("SELECT " + formQuotedIdentifier(columnLabel) + " FROM " + quotedTableLabel));
            int counter = 0;
            while (column.next()) {
                columnData[counter++] = column.getInt(1);
            }
            return columnData;
        } catch(SQLException e) {
            throw new SqlTableDataException(e);
        } finally {
            if (column != null)
                column.close();
        }
    }


    /**
     * Set the sql filter to use with this data table. This filter will restrict what queries/updates may be performed
     * through this class on the underlying database.
     *
     * @param sqlFilter
     *        The sql filter to use.
     */
    public void setSqlFilter(Filter<String> sqlFilter) {
        this.sqlFilter = sqlFilter;
    }

    private String formQuotedIdentifier(String identifier) {
        return SqlTableDataUtil.formQuotedIdentifier(identifier,dataSet.getIdentifierQuote());
    }

    /**
     * Execute a sql query on this data table's underlying database.  The query must pass through this table's sql
     * filter, as well as that in the {@code SqlDataSet} it is contained in. Also, the automatic commit settings of
     * this table's containing data set are transferred to this method as well.
     *
     * @param sqlQuery
     *        The query to execute.
     *
     * @return the result set returned by this sql query.
     *
     * @throws TableDataException if the sql query is blocked by a sql filters.
     */
    public IsolatedResultSet executeSqlQuery(String sqlQuery) {
        return dataSet.executeSqlQuery(sqlQuery,sqlFilter);
    }


    /**
     * Execute a sql update on this data table's underlying database.  The update must pass through this table's sql
     * filter, as well as that in the {@code SqlDataSet} it is contained in. Also, the automatic commit settings of
     * this table's containing data set are transferred to this method as well.
     *
     * @param sqlUpdate
     *        The update to execute.
     *
     * @return the return value of {@code java.sql.Statement.executeUpdate(sqlUpdate)}.
     *
     * @throws com.pb.sawdust.tabledata.TableDataException if the sql update is blocked by one of the sql filters.
     */
    public int executeSqlUpdate(String sqlUpdate){
        return dataSet.executeSqlUpdate(sqlUpdate,sqlFilter);
    }

    /**
     * Execute a batch of sql updates on this data table's undelying database. The updates will be executed as a batch,
     * instead of individually, offering some performance benefits. The updates must pass through this data table's sql
     * filter, as well as that in the {@code SqlDataSet} it is contained in.
     *
     * @param sqlUpdates
     *        The sql updates to execute.
     *
     * @return the return value of {@code java.sql.Statement.executeBatch()}.
     *
     * @throws TableDataException if the sql update is blocked by one of the sql filters.
     */
    public int[] executeSqlBatch(String[] sqlUpdates) {
        return dataSet.executeSqlBatch(sqlUpdates,sqlFilter);
    }

    /**
     * Get a prepared statement to use with this data table. The prepared statement returned is a "shell" in
     * that it only allows methods which will build up a batch statement. To use the returned prepared statement, it
     * must be sent through {@code executePreparedStatmentQuery(SqlDataSetPreparedStatement)},
     * {@code executePreparedStatmentUpdate(SqlDataSetPreparedStatement)}, or
     * {@code executePreparedStatmentBatch(SqlDataSetPreparedStatement)}.  To help manage resource usage, the prepared
     * statement should be sent through one of the aforementioned methods as soon as possible after calling this method.
     * The updates must pass through this data table's sql filter, as well as that in the {@code SqlDataSet} it is
     * contained in.
     *
     * @param preparedStatement
     *        The prepared sql statement following the syntax defined in {@code java.sql.PreparedStatement}.
     *
     * @return a prepared statement which can be used with this data set.
     *
     * @throws TableDataException if the sql update is blocked by one of the sql filters.
     */
    public SqlDataSet.SqlDataSetPreparedStatement getPreparedStatement(String preparedStatement) {
        return dataSet.getPreparedStatement(preparedStatement,sqlFilter);
    }

    /**
     * Execute a prepared statement query through this data table's underlying database.  To help
     * manage resource usage, the returned result should be closed as soon as possible after calling this method. Upon
     * closing the result set, all other resources relating to it, including {@code preparedStatement}, will be closed,
     * so explicitly closing the prepared statement is unnecessary.
     *
     * @param preparedStatement
     *        The prepared statement holding the query to execute.
     *
     * @return the result set returned by this sql query.
     *
     * @throws TableDataException if the prepared statement did not originate from this data table's data set instance.
     */
    public IsolatedResultSet executePreparedStatmentQuery(SqlDataSet.SqlDataSetPreparedStatement preparedStatement) {
        return dataSet.executePreparedStatmentQuery(preparedStatement);
    }

    /**
     * Execute a prepared statement update through this data table's underlying database. This method will automatically
     * close all used resources, including {@code preparedStatement}, so explicitly closing the prepared statement is
     * unnecessary.
     *
     * @param preparedStatement
     *        The prepared statement holding the query to execute.
     *
     * @return the return value of {@code java.sql.PreparedStatement.executeUpdate()}.
     *
     * @throws TableDataException if the prepared statement did not originate from this data table's data set instance.
     */
    public int executePreparedStatmentUpdate(SqlDataSet.SqlDataSetPreparedStatement preparedStatement) {
        return dataSet.executePreparedStatmentUpdate(preparedStatement);
    }

    /**
     * Execute a prepared statement batch update through this data table's underlying database. This method will
     * automatically close all used resources, including {@code preparedStatement}, so explicitly closing the prepared
     * statement is unnecessary.
     *
     * @param preparedStatement
     *        The prepared statement holding the batch updates to execute.
     *
     * @return the return value of {@code java.sql.PreparedStatement.executeBatch()}.
     *
     * @throws TableDataException if the prepared statement did not originate from this data table's data set instance.
     */
    public int[] executePreparedStatementBatch(SqlDataSet.SqlDataSetPreparedStatement preparedStatement) {
        return dataSet.executePreparedStatementBatch(preparedStatement);
    }

    /**
     * Order the table by the specified column label. This will change the row number key to reflect the new ordering
     * of the table.
     *
     * @param columnLabel
     *        The name of the column to use for the row ordering.
     *
     * @throws TableDataException if {@code columnLabel} does not exist in this table.
     */
    public void orderBy(final String columnLabel) {
        if (!hasColumn(columnLabel))
            throw new TableDataException(TableDataException.COLUMN_NOT_FOUND,columnLabel);
        orderByQuotedColumnLabel = formQuotedIdentifier(columnLabel);
        final String internalKeyColumn = schema.getRowNumberKeyColumnLabel();
        setRowNumberKey(
            new AbstractTableKey<Integer>(internalKeyColumn,DataType.INT) {
                protected InjectiveMap<Integer,Integer> getKeyIndex() {
                    InjectiveMap<Integer,Integer> keyMap = new InjectiveHashMap<Integer,Integer>();
                    IsolatedResultSet rowList = null;                     
                    int counter = 0;
                    try {
//                        try {
                        rowList = dataSet.executeSqlQueryFilterless("SELECT " + formQuotedIdentifier(schema.getRowNumberKeyColumnLabel()) + " FROM " + quotedTableLabel + getOrderByStatement());
                        while (rowList.next())
                            keyMap.put(rowList.getInt(1),counter++);
//                        } finally {
//                            rowList.close();
//                        }
                    } catch (SQLException e) {
                        throw new TableDataException(e);
                    }  finally {
                        if (rowList != null)
                            rowList.close();
                    }
                    return keyMap;
                }
            });
//        setRowNumberKey(
//            new AbstractTableKey<Integer>(internalKeyColumn,DataType.INT) {
//                protected InjectiveMap<Integer,Integer> getKeyIndex() {
//                    InjectiveMap<Integer,Integer> keyMap = new InjectiveHashMap<Integer,Integer>();
//                    int counter = 0;
//                    //order by column may have non-unique values; must build key from unique column
//                    for (DataRow row : SqlDataTable.this)
//                        keyMap.put(row.<Integer>getCell(internalKeyColumn),counter++);
//                    return keyMap;
//                }
//            }
//        );
    }

    /**
     * Order the table by the default ordering as specified by its schema's {@code SqlTableSchema.getRowNumberKeyColumnLabel()}
     * method.
     *
     * @throws TableDataException if row number key column is specified in the table schema.
     */
    public void orderByDefault() {
        orderByQuotedColumnLabel = null;
        setRowNumberKey(getSimpleRowNumberKey(schema.getRowNumberKeyColumnLabel()));
    }

    private String getOrderByStatement() {
        if (orderByQuotedColumnLabel != null)
            return " ORDER BY " + orderByQuotedColumnLabel;
        if (rowNumberKey != null)
            //return " ORDER BY " + formQuotedIdentifier(getRowNumberKey().getKeyColumnLabel());
            //rebuilding key often needs this, and we don't need to rebuild key to get this statement,
            //  so don't use getRowNumberKey()
            return " ORDER BY " + formQuotedIdentifier(rowNumberKey.getKeyColumnLabel());
        else
            return "";
    }

    private IsolatedResultSet getTable() {
        return dataSet.executeSqlQueryFilterless("SELECT " + schema.getVisibleColumnList(dataSet.getIdentifierQuote()) + " FROM " + quotedTableLabel + getOrderByStatement());
    }

    private IsolatedResultSet getColumnResultSet(String columnName) {
        return dataSet.executeSqlQueryFilterless("SELECT " + formQuotedIdentifier(columnName) + " FROM " + quotedTableLabel + getOrderByStatement());
    }

    public int getRowCount() {
        IsolatedResultSet rowCount = null;
        try {
            rowCount = dataSet.executeSqlQueryFilterless("SELECT COUNT(*) FROM " + quotedTableLabel);
            rowCount.next();
            return rowCount.getInt(1);
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        } finally {
            if (rowCount != null)
                rowCount.close();
        }
    }

    public int getColumnCount() {
        return getColumnLabels().length;
    }

    public SqlTableSchema getSchema() {
        return schema;
    }

    protected boolean setCellValueInData(int rowNumber, int columnIndex, Object value) {
        String statement = "UPDATE " + quotedTableLabel + " SET " + formQuotedIdentifier(getColumnLabel(columnIndex)) + " = " + dataSet.formRowEntry(value,getColumnDataType(columnIndex)) +
                           " WHERE " + quotedRowNumberKeyLabel + "=" + getRowNumberKey().getKey(rowNumber);
        executeSqlUpdate(statement);
        return true;
    }

    public boolean setColumnData(int columnIndex, Object columnValues) {
        if (!columnValues.getClass().isArray())
            throw new IllegalArgumentException("Column values in setColumnData must be an array.");
        if (Array.getLength(columnValues) != getRowCount())
            throw new TableDataException("Column values array length should equal number of rows in table");
        DataType columnType = getColumnDataType(columnIndex);
        columnValues = getPrimitiveColumn(columnValues,columnType);
        if (!checkPrimitiveArrayType(columnValues,columnType))
            throw new TableDataException(TableDataException.INVALID_DATA_TYPE,columnValues.getClass().getComponentType(),columnType.getPrimitiveTypeString());
        TableKey key = getRowNumberKey();
        SqlDataSet.SqlDataSetPreparedStatement ps =  getPreparedStatement("UPDATE " + quotedTableLabel + " SET " + formQuotedIdentifier(getColumnLabel(columnIndex)) + " = ? WHERE " + quotedRowNumberKeyLabel + "= ?");
        Iterator columnIterator = ArrayUtil.getIterator(columnValues);
        int counter = 0;
        while (columnIterator.hasNext()) {
            ps.setObject(1,dataSet.formRowEntry(columnIterator.next(),columnType));
            ps.setObject(2,key.getKey(counter++));
            ps.addBatch();
        }
        executePreparedStatementBatch(ps);
        return true;
    }

    protected void deleteRowFromData(int rowNumber) {
        executeSqlUpdate("DELETE FROM " + quotedTableLabel + " WHERE " + quotedRowNumberKeyLabel + "=" + getRowNumberKey().getKey(rowNumber));
    }

    protected void deleteColumnFromData(int columnNumber) {
        executeSqlUpdate("ALTER TABLE " + quotedTableLabel + " DROP COLUMN " + formQuotedIdentifier(getColumnLabel(columnNumber)));
    }

    protected boolean addRow(int nextRowIndex,Object ... rowData) {
        return addRow(nextRowIndex,schema.getColumnTypes(),rowData);
    }

    private boolean addRow(int nextRowIndex, DataType[] types, Object ... rowData) {
        executeSqlUpdate(getAddRowStatement(nextRowIndex,types,rowData));
        rowNumberKeySpoiled = true;
        return true;
    }

    private String getAddRowStatement(int nextRowIndex, DataType[] types, Object ... rowData) {
        //row index must be 1 higer than highest index - this method is for efficiency
        // expect 1st column to be autoincrement if using internal row number key
        StringBuilder statement = new StringBuilder("INSERT INTO " + quotedTableLabel + " VALUES (");
        boolean first = true;
        if (schema.usesInternalRowNumberKey()) {
            statement.append(nextRowIndex);
            first = false;
        }
        int counter = 0;
        for (Object cellData : rowData) {
            if (first)
                first = false;
            else
                statement.append(",");
            statement.append(dataSet.formRowEntry(cellData,types[counter++]));
        }
        statement.append(")");
        return statement.toString();
    }

    public boolean addDataByRow(Object[][] data) {
        int rowOffset = getRowCount();
        DataType[] types = schema.getColumnTypes();
        String[] statements = new String[data.length];
        for (int i = 0; i < data.length; i++)
            statements[i] = getAddRowStatement(rowOffset++,types,data[i]);
        executeSqlBatch(statements);
        rowNumberKeySpoiled = true;
        return true;
    }

    protected <A> boolean addColumnToData(String columnLabel, A columnData, DataType type) {
        String updateStatement = "ALTER TABLE " + quotedTableLabel + " ADD COLUMN " + formQuotedIdentifier(columnLabel) + " " + dataSet.getColumnDefinition(type);
        executeSqlUpdate(updateStatement);
        Iterator columnIterator = ArrayUtil.getIterator(columnData);
        TableKey key = getRowNumberKey();
        int counter = 0;
        if (getColumnCount() == 0) {
            DataType[] types = new DataType[] {type};
            int rowOffset = 0;
            int dataLength = Array.getLength(columnData);
            String[] statements = new String[dataLength];
            for (int i = 0; i < dataLength; i++)
                statements[i] = getAddRowStatement(rowOffset++,types,Array.get(columnData,i));
            executeSqlBatch(statements);
            rowNumberKeySpoiled = true;
        } else {
            SqlDataSet.SqlDataSetPreparedStatement ps = getPreparedStatement("UPDATE " + quotedTableLabel + " SET " + formQuotedIdentifier(columnLabel) + " = ? WHERE " + quotedRowNumberKeyLabel + "= ?");
            while (columnIterator.hasNext()) {
                ps.setObject(1,dataSet.formRowEntry(columnIterator.next(),type));
                ps.setObject(2,key.getKey(counter++));
                ps.addBatch();
            }
            executePreparedStatementBatch(ps);
        }
        return true;
    }

    public SqlDataRow getRow(int rowNumber) {
        IsolatedResultSet table = null;
        try {
            table = dataSet.executeSqlQueryFilterless("SELECT " + schema.getVisibleColumnList(dataSet.getIdentifierQuote()) + " FROM " + quotedTableLabel + " WHERE " + quotedRowNumberKeyLabel + "=" + getRowNumberKey().getKey(rowNumber));
            //return new SqlDataRow(table,0);
            return new SqlDataRow(table,0,schema.getColumnTypes(),schema.getColumnLabels());
            //optional other way
            //table = getTable();
            //return new SqlDataRow(table,row);
        } finally {
            if (table != null)
                table.close();
        }
    }

    public <K> SqlDataRow getRowByKey(K key) {
        return (SqlDataRow) super.getRowByKey(key);
    }

    protected <T> DataColumn<T> getDataColumn(int columnIndex, DataType type) {
        return getDataColumn(getColumnLabels()[columnIndex],type);
    }

    protected <T> DataColumn<T> getDataColumn(String columnLabel,DataType type) {
        //get table key first before getting row to avoid locking contention
        TableKey<?> frozenKey = new FrozenTableKey<Object>(getPrimaryKey());
        IsolatedResultSet row = null;
        try {
            row = getColumnResultSet(columnLabel);
            return new SqlDataColumn<T>(row,type,columnLabel,frozenKey);
        } finally {
            if(row != null)
                row.close();
        }
    }

    public void updateColumnTypes(DataType[] types) {
        String label = getLabel();
        if (!dataSet.isTableSchemaInferred(label))
            throw new TableDataException("Schema may only be updated for a table whose schema was inferred: " + label);
        //todo verify schema is compatible
        this.schema = new SqlTableSchema(label,this.schema.getColumnLabels(),types,this.schema.usesInternalRowNumberKey());
        dataSet.inferredTableSchemaReplaced(label);
    }

    public Iterator<DataRow> iterator() {
        return iterator(getTable());
    }

    private Iterator<DataRow> iterator(IsolatedResultSet table) {
        try {
            return new SqlTableDataUtil.ResultSetIterator<DataRow>(SqlTableDataUtil.getCachedRowSet(table)) {
                private int currentRow = 0;
                private SqlDataRow dataRow = new SqlDataRow(resultSet,currentRow,schema.getColumnTypes(),schema.getColumnLabels());

                public SqlDataRow getNextIteratorValue() {
                    dataRow.setResultSetRow(currentRow++);
                    return dataRow;
                }
            };
        } finally {
            if (table != null)
                table.close();
        }
    }

    public DataTable getTablePartition(int start, int end) {
        if (start >= end)
            throw new IllegalArgumentException(String.format("Table partition start (%d) must be strictly less than end (%d).",start,end));
        int rows = getRowCount();
        if (start < 0 || end < 0 || start > rows || end > rows)
            throw new IllegalArgumentException(String.format("Table partition (%d,%d) is out of bounds for table with %d rows.",start,end,rows));
        return new SqlDataTablePartition(start,end);
    }


    private static Filter<String> getDefaultSqlFilter(String tableLabel) {
        final String tableLabelUpperCase = tableLabel.toUpperCase();
        return new Filter<String>() {
            public boolean filter(String sqlStatement) {
                return sqlStatement.toUpperCase().contains(tableLabelUpperCase);
            }
        };
    }

    private class SqlDataTablePartition extends DataTablePartition {
        private final String partitionClause;

        private SqlDataTablePartition(int start, int end) {
            super(SqlDataTable.this,start,end,SqlDataTable.this.willCoerceData());
            partitionClause = " WHERE " +  quotedRowNumberKeyLabel + ">=" + start + " AND " + quotedRowNumberKeyLabel + "<" + end;
        }

        public <K> SqlDataRow getRowByKey(K key) {
            return (SqlDataRow) super.getRowByKey(key);
        }

        protected <T> DataColumn<T> getDataColumn(int columnIndex, DataType type) {
            return getDataColumn(getColumnLabels()[columnIndex],type);
        }

        protected <T> DataColumn<T> getDataColumn(String columnLabel,DataType type) {
            IsolatedResultSet row = null;
            try {
                row = dataSet.executeSqlQueryFilterless("SELECT " + formQuotedIdentifier(columnLabel) + " FROM " + quotedTableLabel + partitionClause + getOrderByStatement());
                @SuppressWarnings("unchecked") //unchecked because some wierd erasure thing is causing
                                               // new FrozenTableKey(getPrimaryKey()) to not infer getPrimaryKey()'s type correctly
                TableKey<?> frozenKey = new FrozenTableKey(getPrimaryKey());
                return new SqlDataColumn<T>(row,type,columnLabel,frozenKey);
            } finally {
                if(row != null)
                    row.close();
            }
        }

        public Iterator<DataRow> iterator() {
            return SqlDataTable.this.iterator(dataSet.executeSqlQueryFilterless("SELECT " + schema.getVisibleColumnList(dataSet.getIdentifierQuote()) + " FROM " + quotedTableLabel + partitionClause + getOrderByStatement()));
        }
    }
}
