package com.pb.sawdust.tabledata.sql;

import com.pb.sawdust.tabledata.basic.ListDataTable;
import com.pb.sawdust.tabledata.metadata.TableSchema;
import com.pb.sawdust.tabledata.metadata.DataType;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.math.BigDecimal;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import com.pb.sawdust.tabledata.TableDataException;
import com.pb.sawdust.tabledata.DataTable;
import com.pb.sawdust.tabledata.DataRow;
import com.pb.sawdust.tabledata.AbstractDataSet;
import com.pb.sawdust.util.Filter;
import com.pb.sawdust.util.sql.IsolatedResultSet;
import static com.pb.sawdust.util.Range.*;

/**
 * The {@code SqlDataSet} class provides a {@code DataSet} implemenatation for SQL (database-backed) data tables. More
 * specifically, a sql data set is intended to be a representation of a SQL database, so it actually becomes the gateway
 * through which {@code SqlDataTable}s and their related classes are accessed. This class is intended to be a relatively
 * generic implementation which is not tied to a specific database vendor; the database specifics are specified in
 * extending classes through the implementation of the {@code getConnection()} method (which returns a JDBC connection).
 * Default values for certain JDBC settings and database specifics - result set types and concurrency, identifier quote
 * strings, and commit statements - have been specified, but may be changed to match the JDBC implementation details.
 * Whether table updates should be automatically committed can also be specified by the user and/or implementation.
 * <p>
 * Sql queries and updates may be executed through this class, which opens up the full power of the database to the end
 * user of the sql data set. However, to provide a level of security, a sql filter can be specified, which prevents sql
 * queries/updates which meet a certain criteria from being executed. The default filter is to let all sql queries and
 * statements through.
 *
 * @author crf <br/>
 *         Started: May 13, 2008 5:37:28 AM
 */
public abstract class SqlDataSet extends AbstractDataSet<SqlDataTable> {
    private Filter<String> sqlFilter;
    private boolean commitUpdates = true;
    private String commitStatement = "COMMIT";
    private String identifierQuote = "\"";
    private int resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
    private int resultSetConcurrency = ResultSet.CONCUR_READ_ONLY;
    private Set<String> tablesWithSchemaInferred = new HashSet<String>();

    /**
     * Get a JDBC connection to the database that this data set represents.
     *
     * @return a connection to the underlying database.
     */
    public abstract Connection getConnection();

    /**
     * Constructor specifying the data set's sql filter.
     *
     * @param sqlFilter
     *        The sql filter to use with this data set.
     */
    public SqlDataSet(Filter<String> sqlFilter) {
        setSqlFilter(sqlFilter);
        tableLabelToTable = null; //not used
    }

    /**
     * Constructor for a sql data set with a defualt sql filter. The default filter allows all sql updates and queries
     * to execute.
     */
    public SqlDataSet() {
        this(new Filter<String>() {
            public boolean filter(String input) {
                return true;
            }
        });
    }

    /**
     * Set the SQL filter for this data set. This filter will povide a global check for whether any given sql update/query
     * may be executed.
     *
     * @param sqlFilter
     *        The sql filter to use with this data set.
     */
    public void setSqlFilter(Filter<String> sqlFilter) {
        this.sqlFilter = sqlFilter;
    }

    /**
     * Set the sql identifier quote string. For example, if the identifier quote string is <tt>"</tt>, then an identifier
     * named <tt>some_identifier</tt> is represented as a quoted identifer as <tt>"some_identifer"</tt>.
     *
     * @param identifierQuote
     *        The string used to identify quoted sql identifiers.
     */
    public void setIdentifierQuote(String identifierQuote) {
        this.identifierQuote = identifierQuote;
    }

    /**
     * Get the sql identifier quote string. The default value is <tt>"</tt>. As an example, using this default, an
     * identifier named <tt>some_identifier</tt> is represented as a quoted identifer as <tt>"some_identifer"</tt>.
     *
     * @return the sql identifier quote string used in this data set.
     */
    public String getIdentifierQuote() {
        return identifierQuote;
    }

    /**
     * Set the sql statement that is used to commit changes to the underlying database. No semicolon is required to
     * declare the end of the statement.
     *
     * @param commitStatement
     *        The statement used to commit changes in this data set's underlying database.
     */
    public void setCommitStatement(String commitStatement) {
        this.commitStatement = commitStatement;
    }

    /**
     * Get the sql statement that is used to commit changes to the underlying database. The default commit statement is
     * <tt>COMMIT</tt>.
     *
     * @return the statement used to commit changes to this data set's underlying database.
     */
    public String getCommitStatement() {
        return commitStatement;
    }

    /**
     * Set whether or not sql updates should be automatically committed to the underlying database. If this method has
     * not been called, the default is to commit changes automatically.
     *
     * @param commitUpdates
     *        {@code true} if updates are to be automatically committed, {@code false} otherwise.
     */
    public void setCommitUpdates(boolean commitUpdates) {
        this.commitUpdates = commitUpdates;
    }

    /**
     * Set the type of result set returned by queries executed with this data set's JDBC connection. The entered value
     * should be one of {@code java.sql.ResultSet.TYPE_FORWARD_ONLY}, {@code java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE},
     * or {@code java.sql.ResultSet.TYPE_SCROLL_SENSITIVE}. If this method has never been called, the default type is
     * {@code java.sql.ResultSet.TYPE_FORWARD_ONLY}.
     *
     * @param resultSetType
     *        The type of result set returned by this data set's JDBC connection.
     */
    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    /**
     * Set the concurrency of result sets returned by queries executed on this data set's JDBC connection. The entered
     * value should be one of {@code java.sql.ResultSet.CONCUR_READ_ONLY} or {@code java.sql.ResultSet.CONCUR_UPDATABLE}.
     * If this method has never been called, then the default conncurrently is {@code java.sql.ResultSet.CONCUR_READ_ONLY}.
     *
     * @param resultSetConcurrency
     *        The concurrency of result sets returned by this data set's JDBC connection.
     */
    public void setResultSetConcurrency(int resultSetConcurrency) {
        this.resultSetConcurrency = resultSetConcurrency;
    }

    /**
     * Execute a sql query bypassing this data set's sql filter. This method is protected and intended to be used only
     * internally by extending classes to avoid having necessary queries blocked. All sql queries executed through this
     * class (or those extending it) by end users should use {@code executeSqlQuery(String, SqlFilter[])}}. To help
     * manage resource usage, the returned result should be closed as soon as possible after calling this method.
     *
     * @param sqlQuery
     *        The sql query to execute.
     *
     * @return the result set returned by this sql query.
     */
    protected IsolatedResultSet executeSqlQueryFilterless(String sqlQuery) {
        Connection c = null;
        Statement s = null;
        try {
            c = getConnection();
            s = c.createStatement(resultSetType,resultSetConcurrency);
            return new IsolatedResultSet(executeSqlQuery(sqlQuery,s),c,s);
        } catch (SQLException e) {
            try {
                if (s != null)
                    s.close();
            } catch (SQLException ignored) {
                //ignore
            }
            try {
//                if (c != null)
                c.close();
            } catch (SQLException ignored) {
                //ignore
            }
            throw new SqlTableDataException(e);
        }
    }

    /**
     * Execute a sql update bypassing this data set's sql filter. This method is protected and intended to be used only
     * internally by extending classes to avoid having necessary updates blocked. All sql updates executed through this
     * class (or those extending it) by end users should use {@code executeSqlUpdate(String, SqlFilter[])} }.
     *
     * @param sqlUpdate
     *        The sql update to execute.
     *
     * @return the return value of {@code java.sql.Statement.executeUpdate(sqlUpdate)}.
     */
    protected int executeSqlUpdateFilterless(String sqlUpdate) {
        Connection c = null;
        Statement s = null;
        try {
            c = getConnection();
            c.setAutoCommit(false);
            s = c.createStatement(resultSetType,resultSetConcurrency);
            int result = s.executeUpdate(sqlUpdate);
            if (commitUpdates)
                c.commit();
            return result;
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        } finally {
            try {
                if (s != null)
                    s.close();
            } catch (SQLException e) {
                //ignore
            }
            try {
                if (c != null)
                    c.close();
            } catch (SQLException e) {
                //ignore
            }
        }
    }

    /**
     * Execute a sql batch update bypassing this data set's sql filter. The updates will be executed as a batch, instead
     * of individually, offering some performance benefits. This method is protected and intended to be used only
     * internally by extending classes to avoid having necessary updates blocked. All sql updates executed through this
     * class (or those extending it) by end users should use {@code executeSqlUpdate(String, SqlFilter[])} }.
     *
     * @param sqlUpdates
     *        The sql updates to execute.
     *
     * @return the return value of {@code java.sql.Statement.executeBatch()}.
     */
    protected int[] executeSqlBatchFilterless(String[] sqlUpdates) {
        Connection c = null;
        Statement s = null;
        try {
            c = getConnection();
            c.setAutoCommit(false);
            s = c.createStatement(resultSetType,resultSetConcurrency);
            for (String sqlUpdate : sqlUpdates)
                s.addBatch(sqlUpdate);
            int[] result = s.executeBatch();
            if (commitUpdates)
                c.commit();
            return result;
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        } finally {
            try {
                if (s != null)
                    s.close();
            } catch (SQLException e) {
                //ignore
            }
            try {
                if (c != null)
                    c.close();
            } catch (SQLException e) {
                //ignore
            }
        }
    }

    /**
     * Get a prepared statement bypassing this data set's sql filter. The prepared statement returned is a "shell" in
     * that it only allows methods which will build up a batch statement. To use the returned prepared statement, it
     * must be sent through {@code executePreparedStatmentQuery(SqlDataSetPreparedStatement)},
     * {@code executePreparedStatmentUpdate(SqlDataSetPreparedStatement)}, or
     * {@code executePreparedStatmentBatch(SqlDataSetPreparedStatement)}.  To help manage resource usage, the prepared
     * statement should be sent through one of the aforementioned methods as soon as possible after calling this method.
     *
     * @param preparedStatement
     *        The prepared sql statement following the syntax defined in {@code java.sql.PreparedStatement}.
     *
     * @return a prepared statement which can be used with this data set.
     */
    protected SqlDataSetPreparedStatement getPreparedStatementFilterless(String preparedStatement) {
        try {
            Connection c = getConnection();
            c.setAutoCommit(false);
            return new SqlDataSetPreparedStatement(c.prepareStatement(preparedStatement,resultSetType,resultSetConcurrency),c);
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    /**
     * Execute a sql query on this data set's underlying database.  The query must pass through this data set's sql
     * filter. To help manage resource usage, the returned result should be closed as soon as possible after calling
     * this method.
     *
     * @param sqlQuery
     *        The query to execute.
     *
     * @return the result set returned by this sql query.
     *
     * @throws TableDataException if the sql query is blocked by one of the sql filters.
     */
    public IsolatedResultSet executeSqlQuery(String sqlQuery) {
        if (sqlFilter.filter(sqlQuery))
            return executeSqlQueryFilterless(sqlQuery);
        else
            throw new TableDataException("Sql query not allowed: %1$s",sqlQuery);
    }

    /**
     * Execute a sql query on this data set's underlying database.  The query must pass through this data set's sql
     * filter, as well as an additional sql filter specified in the method call. To help manage resource usage, the
     * returned result should be closed as soon as possible after calling this method.
     *
     * @param sqlQuery
     *        The query to execute.
     *
     * @param additionalFilter
     *        The additional filter the query must pass before being allowed to execute.
     *
     * @return the result set returned by this sql query.
     *
     * @throws TableDataException if the sql query is blocked by one of the sql filters.
     */
    public IsolatedResultSet executeSqlQuery(String sqlQuery, Filter<String> additionalFilter) {
        if (sqlFilter.filter(sqlQuery) && additionalFilter.filter(sqlQuery))
            return executeSqlQueryFilterless(sqlQuery);
        else
            throw new TableDataException("Sql query not allowed: %1$s",sqlQuery);
    }

//    /**
//     * Execute a sql query on this data set's underlying database.  The query must pass through this data set's sql
//     * filter, as well as any additional sql filters (composited into a {@code FilterChain}) specified in the method call.
//     *
//     * @param sqlQuery
//     *        The query to execute.
//     *
//     * @param additionalFilters
//     *        The additional filters the query must pass before being allowed to execute.
//     *
//     * @return the result set returned by this sql query.
//     *
//     * @throws TableDataException if the sql query is blocked by one of the sql filters.
//     */
//    public IsolatedResultSet executeSqlQuery(String sqlQuery, FilterChain<String> additionalFilters) {
//        if (sqlFilter.filter(sqlQuery) && additionalFilters.filter(sqlQuery))
//            return executeSqlQueryFilterless(sqlQuery);
//        else
//            throw new TableDataException("Sql query not allowed: %1$s",sqlQuery);
//    }

    /**
     * Execute a sql update on this data set's underlying database.  The update must pass through this data set's sql
     * filter.
     *
     * @param sqlUpdate
     *        The update to execute.
     *
     * @return the return value of {@code java.sql.Statement.executeUpdate(sqlUpdate)}.
     *
     * @throws TableDataException if the sql update is blocked by one of the sql filters.
     */
    public int executeSqlUpdate(String sqlUpdate) {
        if (sqlFilter.filter(sqlUpdate))
            return executeSqlUpdateFilterless(sqlUpdate);
        else
            throw new TableDataException("Sql update not allowed: %1$s",sqlUpdate);
    }

    /**
     * Execute a sql update on this data set's underlying database.  The update must pass through this data set's sql
     * filter, as well as anonther additional sql filter specified in the method call.
     *
     * @param sqlUpdate
     *        The update to execute.
     *
     * @param additionalFilter
     *        The additional filter the query must pass before being allowed to execute.
     *
     * @return the return value of {@code java.sql.Statement.executeUpdate(sqlUpdate)}.
     *
     * @throws TableDataException if the sql update is blocked by one of the sql filters.
     */
    public int executeSqlUpdate(String sqlUpdate, Filter<String> additionalFilter) {
        if (sqlFilter.filter(sqlUpdate) && additionalFilter.filter(sqlUpdate))
            return executeSqlUpdateFilterless(sqlUpdate);
        else
            throw new TableDataException("Sql update not allowed: %1$s",sqlUpdate);
    }
//
//    /**
//     * Execute a sql update on this data set's underlying database.  The update must pass through this data set's sql
//     * filter, as well as any additional sql filters (composited into a {@code FilterChain}) specified in the method call.
//     *
//     * @param sqlUpdate
//     *        The update to execute.
//     *
//     * @param additionalFilters
//     *        The additional filters the query must pass before being allowed to execute.
//     *
//     * @return the return value of {@code java.sql.Statement.executeUpdate(sqlUpdate)}.
//     *
//     * @throws TableDataException if the sql update is blocked by one of the sql filters.
//     */
//    public int executeSqlUpdate(String sqlUpdate, FilterChain<String> additionalFilters) {
//        if (sqlFilter.filter(sqlUpdate) && additionalFilters.filter(sqlUpdate))
//            return executeSqlUpdateFilterless(sqlUpdate);
//        else
//            throw new TableDataException("Sql update not allowed: %1$s",sqlUpdate);
//    }

    /**
     * Execute a batch of sql updates. The updates will be executed as a batch, instead of individually, offering some
     * performance benefits. The updates must pass through this data set's sql filter.
     *
     * @param sqlUpdates
     *        The sql updates to execute.
     *
     * @return the return value of {@code java.sql.Statement.executeBatch()}.
     *
     * @throws TableDataException if the sql update is blocked by one of the sql filters.
     */
    public int[] executeSqlBatch(String[] sqlUpdates) {
        for (String sqlUpdate : sqlUpdates)
            if (!sqlFilter.filter(sqlUpdate))
                throw new TableDataException("Sql update not allowed: %1$s",sqlUpdate);
        return executeSqlBatchFilterless(sqlUpdates);
    }

    /**
     * Execute a batch of sql updates. The updates will be executed as a batch, instead of individually, offering some
     * performance benefits. The updates must pass through this data set's sql filter, as well as anonther additional
     * sql filter specified in the method call.
     *
     * @param sqlUpdates
     *        The sql updates to execute.
     *
     * @param additionalFilter
     *        The additional filter the updates must pass before being allowed to execute.
     *
     * @return the return value of {@code java.sql.Statement.executeBatch()}.
     *
     * @throws TableDataException if the sql update is blocked by one of the sql filters.
     */
        public int[] executeSqlBatch(String[] sqlUpdates, Filter<String> additionalFilter) {
        for (String sqlUpdate : sqlUpdates)
            if (!sqlFilter.filter(sqlUpdate) && additionalFilter.filter(sqlUpdate))
                throw new TableDataException("Sql update not allowed: %1$s",sqlUpdate);
        return executeSqlBatchFilterless(sqlUpdates);
    }

    /**
     * Get a prepared statement to use with this data set. The prepared statement returned is a "shell" in
     * that it only allows methods which will build up a batch statement. To use the returned prepared statement, it
     * must be sent through {@code executePreparedStatmentQuery(SqlDataSetPreparedStatement)},
     * {@code executePreparedStatmentUpdate(SqlDataSetPreparedStatement)}, or
     * {@code executePreparedStatmentBatch(SqlDataSetPreparedStatement)}.  To help manage resource usage, the prepared
     * statement should be sent through one of the aforementioned methods as soon as possible after calling this method.
     * The statement must pass through this data set's sql filter.
     *
     * @param preparedStatement
     *        The prepared sql statement following the syntax defined in {@code java.sql.PreparedStatement}.
     *
     * @return a prepared statement which can be used with this data set.
     *
     * @throws TableDataException if the sql update is blocked by one of the sql filters.
     */
    public SqlDataSetPreparedStatement getPreparedStatement(String preparedStatement) {
        if (sqlFilter.filter(preparedStatement))
            return getPreparedStatementFilterless(preparedStatement);
        else
            throw new TableDataException("Prepared sql query not allowed: %1$s",preparedStatement);
    }


    /**
     * Get a prepared statement to use with this data set. The prepared statement returned is a "shell" in
     * that it only allows methods which will build up a batch statement. To use the returned prepared statement, it
     * must be sent through {@code executePreparedStatmentQuery(SqlDataSetPreparedStatement)},
     * {@code executePreparedStatmentUpdate(SqlDataSetPreparedStatement)}, or
     * {@code executePreparedStatmentBatch(SqlDataSetPreparedStatement)}.  To help manage resource usage, the prepared
     * statement should be sent through one of the aforementioned methods as soon as possible after calling this method.
     * The statement must pass through this data set's sql filter, as well as anonther additional sql filter specified
     * in the method call
     *
     * @param preparedStatement
     *        The prepared sql statement following the syntax defined in {@code java.sql.PreparedStatement}.
     *
     * @param additionalFilter
     *        The additional filter the updates must pass before being allowed to execute.
     *
     * @return a prepared statement which can be used with this data set.
     *
     * @throws TableDataException if the sql update is blocked by one of the sql filters.
    **/
    public SqlDataSetPreparedStatement getPreparedStatement(String preparedStatement, Filter<String> additionalFilter) {
        if (sqlFilter.filter(preparedStatement) && additionalFilter.filter(preparedStatement))
            return getPreparedStatementFilterless(preparedStatement);
        else
            throw new TableDataException("Prepared sql query not allowed: %1$s",preparedStatement);
    }

    /**
     * Execute a prepared statement query through this data set. The prepared statement must have originated from this
     * instance (through one of its {@code getPreparedStatement} methods), or an exception will be thrown. To help
     * manage resource usage, the returned result should be closed as soon as possible after calling this method. Upon
     * closing the result set, all other resources relating to it, including {@code preparedStatement}, will be closed,
     * so explicitly closing the prepared statement is unnecessary.
     *
     * @param preparedStatement
     *        The prepared statement holding the query to execute.
     *
     * @return the result set returned by this sql query.
     *
     * @throws TableDataException if the prepared statement did not originate from this data set instance.
     */
    public IsolatedResultSet executePreparedStatmentQuery(SqlDataSetPreparedStatement preparedStatement) {
        if (!preparedStatement.verifyParent(this))
            throw new TableDataException("Prepared statement does not originate from this SqlDataSet.");
        Connection c = null;
        try {
            c = preparedStatement.getConnection();
            return new IsolatedResultSet(executePreparedStatement(preparedStatement.ps),c,preparedStatement.ps);
        } catch (SQLException e) {
            try {
                preparedStatement.ps.close();
            } catch (SQLException ignored) {
                //ignore
            }
            try {
                if (c != null)
                    c.close();
            } catch (SQLException ignored) {
                //ignore
            }
            throw new SqlTableDataException(e);
        }
    }

    /**
     * Execute a sql query through a statement. This method may be overidden if special error handling is needed for failures.
     * All queries will be processed through either this method or {@link #executePreparedStatement(java.sql.PreparedStatement)}.
     *
     * @param sql
     *        The sql statement to execute.
     *
     * @param s
     *        The statement the sql will be executed.
     *
     * @return the result set resulting from the query.
     *
     * @throws SQLException if {@code s} fails to execute {@code sql}.
     */
    protected ResultSet executeSqlQuery(String sql, Statement s) throws SQLException {
        return s.executeQuery(sql);
    }

    /**
     * Execute a prepared sql statement. This method may be overidden if special error handling is needed for failures.
     * All queries will be processed through either this method or {@link #executeSqlQuery(String, java.sql.Statement)}..
     *
     * @param statement
     *        The prepared statement to execute.
     *
     * @return the result set resulting from the prepared statement.
     *
     * @throws SQLException if {@code statement} fails to execute properly.
     */
    protected ResultSet executePreparedStatement(PreparedStatement statement) throws SQLException {
        return statement.executeQuery();
    }

    /**
     * Execute a prepared statement update through this data set. The prepared statement must have originated from this
     * instance (through one of its {@code getPreparedStatement} methods), or an exception will be thrown. This method
     * will automatically close all used resources, including {@code preparedStatement}, so explicitly closing the
     * prepared statement is unnecessary.
     *
     * @param preparedStatement
     *        The prepared statement holding the query to execute.
     *
     * @return the return value of {@code java.sql.PreparedStatement.executeUpdate()}.
     *
     * @throws TableDataException if the prepared statement did not originate from this data set instance.
     */
    public int executePreparedStatmentUpdate(SqlDataSetPreparedStatement preparedStatement) {
        if (!preparedStatement.verifyParent(this))
            throw new TableDataException("Prepared statement does not originate from this SqlDataSet.");
        Connection c = null;
        try {
            c = preparedStatement.getConnection();
            int result = preparedStatement.ps.executeUpdate();
            if (commitUpdates)
                c.commit();
            return result;
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        } finally {
            preparedStatement.close();
            try {
                if (c != null)
                    c.close();
            } catch (SQLException e) {
                //ignore
            }
        }
    }

    /**
     * Execute a prepared statement batch update through this data set. The prepared statement must have originated from
     * this instance (through one of its {@code getPreparedStatement} methods), or an exception will be thrown. This
     * method will automatically close all used resources, including {@code preparedStatement}, so explicitly closing
     * the prepared statement is unnecessary.
     *
     * @param preparedStatement
     *        The prepared statement holding the query to execute.
     *
     * @return the return value of {@code java.sql.PreparedStatement.executeBatch()}.
     *
     * @throws TableDataException if the prepared statement did not originate from this data set instance.
     */
    public int[] executePreparedStatementBatch(SqlDataSetPreparedStatement preparedStatement) {
        if (!preparedStatement.verifyParent(this))
            throw new TableDataException("Prepared statement does not originate from this SqlDataSet.");
        Connection c = null;
        try {
            c = preparedStatement.getConnection();
            int[] result = preparedStatement.ps.executeBatch();
            if (commitUpdates)
                c.commit();
            return result;
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        } finally {
            preparedStatement.close();
            try {
                if (c != null)
                    c.close();
            } catch (SQLException e) {
                //ignore
            }
        }
    }

    protected SqlDataTable transferTable(DataTable table) {
        return new SqlDataTable(this, table);
    }

    public boolean hasTable(String tableLabel) {
        if (super.hasTable(tableLabel))
            return true;
        //check if table exists in database
        ResultSet rs = null;
        Connection c = null;
        boolean tableExists = false;
        try {
            c = getConnection();
            //rs = c.getMetaData().getTables(null,null,null,null);
            rs = c.getMetaData().getTables(null,null,tableLabel,null);
            tableExists = rs.next();
//            while (rs.next()) {
//                if (rs.getString(3).equalsIgnoreCase(tableLabel)) {
//                    tableExists = true;
//                    break;
//            }
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            }  catch (SQLException e) {
                //ignore
            }
            try {
                if (c != null)
                    c.close();
            }  catch (SQLException e) {
                //ignore
            }
        }
        if (tableExists)
            inferTableMetaData(tableLabel); //do this out here rather than inside so that connections are seperate
        return tableExists;
    }

//    protected void inferTableMetaData(String tableLabel) {
    private void inferTableMetaData(String tableLabel) {
        IsolatedResultSet irs = null;
        try {
            irs = executeSqlQueryFilterless("SELECT * FROM " + SqlTableDataUtil.formQuotedIdentifier(tableLabel,getIdentifierQuote()));
            ResultSetMetaData rsmd = irs.getMetaData();
            Map<String, DataType> columns = new LinkedHashMap<String,DataType>();
            boolean usesInternalRowNumber = false;
            for (int i : range(1,rsmd.getColumnCount()+1)) {
                String name = rsmd.getColumnName(i);
                if (name.equals(SqlDataTable.INTERNAL_ROW_NUMBER_KEY_COLUMN_LABEL))
                    usesInternalRowNumber = true;
                else
                    columns.put(name,SqlTableDataUtil.getDataType(rsmd.getColumnType(i)));
            }
            SqlTableSchema schema = new SqlTableSchema(tableLabel,usesInternalRowNumber);
            for (String columnLabel : columns.keySet())
                schema.addColumn(columnLabel,columns.get(columnLabel)); 
            tableLabelToSchema.put(tableLabel, schema);
        } catch(SQLException e) {
            throw new SqlTableDataException(e);
        } finally {
            if (irs != null)
                irs.close();
        }
        tablesWithSchemaInferred.add(tableLabel);
    }

    boolean isTableSchemaInferred(String tableLabel) {
        return tablesWithSchemaInferred.contains(tableLabel);
    }

    void inferredTableSchemaReplaced(String tableLabel) {
        tablesWithSchemaInferred.remove(tableLabel);
    }

    /**
     * Add a table to this data set.  This will add the table in its entirety - both structure and data.  This method
     * may be unimplemented, but in that case {@link com.pb.sawdust.tabledata.DataSet#addTable(TableSchema)} must be implemented. The input
     * table need not be of the same type as that held by this data set; this method will make the necessary conversions,
     * as needed.
     *
     * @param table
     *        The table to add to this data set.
     *
     * @return a {@code SqlDataTable} instance corresponding to the newly added table.
     *
     * @throws TableDataException if a table with {@code table}'s label already exists in this data set.
     * @throws UnsupportedOperationException if this method is unimplemented.
     */
    public SqlDataTable addTable(DataTable table) {
        if (hasTable(table.getLabel()))
            throw new TableDataException(TableDataException.DATA_TABLE_ALREADY_EXISTS,table.getLabel());
        SqlDataTable newTable = addTable(new SqlTableSchema(table.getSchema()));
        Object[][] rowData = new Object[table.getRowCount()][];
        int counter = 0;
        for (DataRow row : table)
//            newTable.addRow((DataRow) row);
            rowData[counter++] = row.getData();
        newTable.addDataByRow(rowData);
        return newTable;
    }

    /**
     * Add a table to this data set via a table schema definition.  This will only add a table with the defined
     * structure, data must be added using one of the various {@code DataTable} methods. This method requires a
     * {@code SqlTableSchema} (not just a {@code TableSchema}, and will throw an exception if another type of schema is
     * passed in.
     *
     * @param schema
     *        The schema defining the table to add to this data set; must be a {@code SqlTableSchema} instance.
     *
     * @return a {@code SqlDataTable} instance corresponding to the newly added table.
     *
     * @throws TableDataException if a table with {@code table}'s label already exists in this data set, or if {@code schema}
     *         is not a {@code SqlTableSchema}.
     */
    public SqlDataTable addTable(TableSchema schema) {
        if (hasTable(schema.getTableLabel()))
            throw new TableDataException(TableDataException.DATA_TABLE_ALREADY_EXISTS,schema.getTableLabel());
        try {
            SqlTableSchema tableSchema = (SqlTableSchema) schema;
            executeSqlUpdate(tableSchema.getTableCreationSqlStatement(getIdentifierQuote(),this));
            tableLabelToSchema.put(tableSchema.getTableLabel(),tableSchema);
            return getTable(schema.getTableLabel());
        } catch (ClassCastException e) {
            throw new TableDataException("DataSet.addTable(TableSchema) requires a SqlTableSchema (not just TableSchema)");
        }
    }

    /**
     * Get the column data definition (for a SQL {@code CREATE TABLE...} statement) for a given data type.
     *
     * @param columnType
     *        The column data type.
     *
     * @return the sql data type definition corresponding to {@code columnType}.
     */
    public String getColumnDefinition(DataType columnType) {
        return SqlTableDataUtil.getColumnDefinition(columnType);
    }

    /**
     * Remove the specified table from the data set as well as the underlying database. It is up to the user to delete
     * (or flag for by setting to {@code null}) {@code SqlDataTable} instances which refer to the dropped table. To
     * disable this method, without overriding it, use a sql filter which does not allow the {@code DROP TABLE} statement.
     *
     * @param tableLabel
     *        The label of the table to remove.
     *
     * @return the removed table. This table will be a read-only version of the dropped table, and will probably  not be
     *         a {@code SqlDataTable} instance.
     */
    public DataTable dropTable(String tableLabel) {
        if (!hasTable(tableLabel))
            throw new TableDataException(TableDataException.DATA_TABLE_NOT_FOUND,tableLabel);
        DataTable droppedTable = new ListDataTable(getTable(tableLabel));
        executeSqlUpdate("DROP TABLE " + SqlTableDataUtil.formQuotedIdentifier(tableLabel,getIdentifierQuote()));
//        dropTableFromDatabase(tableLabel);
        tableLabelToSchema.remove(tableLabel);
        return droppedTable;
    }

    public SqlDataTable getTable(String tableLabel) {
        if (!hasTable(tableLabel))
            throw new TableDataException(TableDataException.DATA_TABLE_NOT_FOUND,tableLabel);
        return new SqlDataTable(this,tableLabel);
    }

    /**
     * Form an entry suitable for adding/changing sql data of a given type.  <i>e.g.</i>, strings are single quoted.
     * This is called solely by {@code SqlDataTable}, but resides here to deal with vendor-specific data formatting
     * issues. The default behavior is to single quote strings (with double single quotes replacing pre-existing
     * single quotes), to format booleans as all caps, and call {@code toString()} for all other types.
     *
     * @param cellData
     *        The cell data value to transform into a form suitable for a sql statement.
     *
     * @param type
     *        The data type of the cell data.
     *
     * @return a string suitable for use as a certain data type with sql.
     */
    public String formRowEntry(Object cellData, DataType type) {
        switch (type) {
            case BOOLEAN : return cellData.toString().toUpperCase();
            case STRING : return "'" + cellData.toString().replace("'","''") + "'";
            default : return cellData.toString();
        }
    }

//    public boolean verifySchemas() {
//        //todo: this
//        return  true;
//    }



    public class SqlDataSetPreparedStatement {
        private final PreparedStatement ps;
        private final Connection connection;

        private SqlDataSetPreparedStatement(PreparedStatement ps, Connection connection) {
            this.ps = ps;
            this.connection = connection;
        }

        private boolean verifyParent(SqlDataSet possibleParent) {
            return possibleParent == SqlDataSet.this;
        }

        public Connection getConnection() {
            return connection;
        }

        public void close() {
            try {
                ps.close();
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public ResultSet executeQuery() {
            try {
                return ps.executeQuery();
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public int executeUpdate() {
            try {
                return ps.executeUpdate();
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setNull(int parameterIndex, int sqlType) {
            try {
                ps.setNull(parameterIndex,sqlType);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setBoolean(int parameterIndex, boolean x) {
            try {
                ps.setBoolean(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setByte(int parameterIndex, byte x) {
            try {
                ps.setByte(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setShort(int parameterIndex, short x) {
            try {
                ps.setShort(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setInt(int parameterIndex, int x) {
            try {
                ps.setInt(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setLong(int parameterIndex, long x) {
            try {
                ps.setLong(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setFloat(int parameterIndex, float x) {
            try {
                ps.setFloat(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setDouble(int parameterIndex, double x) {
            try {
                ps.setDouble(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setBigDecimal(int parameterIndex, BigDecimal x) {
            try {
                ps.setBigDecimal(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setString(int parameterIndex, String x) {
            try {
                ps.setString(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setBytes(int parameterIndex, byte[] x) {
            try {
                ps.setBytes(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setDate(int parameterIndex, Date x) {
            try {
                ps.setDate(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setTime(int parameterIndex, Time x) {
            try {
                ps.setTime(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setTimestamp(int parameterIndex, Timestamp x) {
            try {
                ps.setTimestamp(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setAsciiStream(int parameterIndex, InputStream x, int length) {
            try {
                ps.setAsciiStream(parameterIndex,x,length);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

//        public void setUnicodeStream(int parameterIndex, InputStream x, int length) {
//              ps.setUnicodeStream(parameterIndex,x,length);
//        }

        public void setBinaryStream(int parameterIndex, InputStream x, int length) {
            try {
                ps.setBinaryStream(parameterIndex,x,length);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void clearParameters() {
            try {
                ps.clearParameters();
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setObject(int parameterIndex, Object x, int targetSqlType) {
            try {
                ps.setObject(parameterIndex,x,targetSqlType);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setObject(int parameterIndex, Object x) {
            try {
                ps.setObject(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public boolean execute() {
            try {
                return ps.execute();
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void addBatch() {
            try {
                ps.addBatch();
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setCharacterStream(int parameterIndex, Reader reader, int length) {
            try {
                ps.setCharacterStream(parameterIndex,reader,length);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setRef(int parameterIndex, Ref x) {
            try {
                ps.setRef(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setBlob(int parameterIndex, Blob x) {
            try {
                ps.setBlob(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setClob(int parameterIndex, Clob x) {
            try {
                ps.setClob(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setArray(int parameterIndex, Array x) {
            try {
                ps.setArray(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public ResultSetMetaData getMetaData() {
            try {
                return ps.getMetaData();
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setDate(int parameterIndex, Date x, Calendar cal) {
            try {
                ps.setDate(parameterIndex,x,cal);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setTime(int parameterIndex, Time x, Calendar cal) {
            try {
                ps.setTime(parameterIndex,x,cal);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) {
            try {
                ps.setTimestamp(parameterIndex,x,cal);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setNull(int parameterIndex, int sqlType, String typeName) {
            try {
                ps.setNull(parameterIndex,sqlType,typeName);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setURL(int parameterIndex, URL x) {
            try {
                ps.setURL(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public ParameterMetaData getParameterMetaData() {
            try {
                return ps.getParameterMetaData();
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setRowId(int parameterIndex, RowId x) {
            try {
                ps.setRowId(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setNString(int parameterIndex, String value) {
            try {
                ps.setNString(parameterIndex,value);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setNCharacterStream(int parameterIndex, Reader value, long length) {
            try {
                ps.setNCharacterStream(parameterIndex,value,length);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setNClob(int parameterIndex, NClob value) {
            try {
                ps.setNClob(parameterIndex,value);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setClob(int parameterIndex, Reader reader, long length) {
            try {
                ps.setClob(parameterIndex,reader,length);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setBlob(int parameterIndex, InputStream inputStream, long length) {
            try {
                ps.setBlob(parameterIndex,inputStream,length);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setNClob(int parameterIndex, Reader reader, long length) {
            try {
                ps.setNClob(parameterIndex,reader,length);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setSQLXML(int parameterIndex, SQLXML xmlObject) {
            try {
                ps.setSQLXML(parameterIndex,xmlObject);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) {
            try {
                ps.setObject(parameterIndex,x,targetSqlType,scaleOrLength);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setAsciiStream(int parameterIndex, InputStream x, long length) {
            try {
                ps.setAsciiStream(parameterIndex,x,length);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setBinaryStream(int parameterIndex, InputStream x, long length) {
            try {
                ps.setBinaryStream(parameterIndex,x,length);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setCharacterStream(int parameterIndex, Reader reader, long length) {
            try {
                ps.setCharacterStream(parameterIndex,reader,length);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setAsciiStream(int parameterIndex, InputStream x) {
            try {
                ps.setAsciiStream(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setBinaryStream(int parameterIndex, InputStream x) {
            try {
                ps.setBinaryStream(parameterIndex,x);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setCharacterStream(int parameterIndex, Reader reader) {
            try {
                ps.setCharacterStream(parameterIndex,reader);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setNCharacterStream(int parameterIndex, Reader value) {
            try {
                ps.setNCharacterStream(parameterIndex,value);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setClob(int parameterIndex, Reader reader) {
            try {
                ps.setClob(parameterIndex,reader);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setBlob(int parameterIndex, InputStream inputStream) {
            try {
                ps.setBlob(parameterIndex,inputStream);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }

        public void setNClob(int parameterIndex, Reader reader) {
            try {
                ps.setNClob(parameterIndex,reader);
            } catch (SQLException e) {
                throw new SqlTableDataException(e);
            }
        }
    }

}
