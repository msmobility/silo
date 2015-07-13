package com.pb.sawdust.tabledata.sql.impl.sqlite;

import com.pb.sawdust.tabledata.sql.SqlDataSet;
import com.pb.sawdust.tabledata.TableDataException;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.util.sql.IsolatedResultSet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The {@code SqliteDataSet} class provides a skeletal implementation of {@code SqlDataSet} to use with Sqlite JDBC
 * connections. This class bypasses the {@code Jdbc*SqlDataSet} classes because Sqlite has some usage details which
 * don't allow some standard methodologies.
 * <p>
 * One issue with Sqlite is that it does not allow columns to be dropped from a given database table.  Subsequently, this
 * functionality is not available in data sets deriving from this class. In future versions, a workaround to provide
 * this functionality may be provided.
 *
 * @author crf <br/>
 *         Started: Dec 9, 2008 8:27:46 AM
 */
public abstract class SqliteDataSet extends SqlDataSet {
    /**
     * Maximum number of retries on a query due to a locked database before an exception is thrown.
     */
    public static int LOCKED_DATABASE_QUERY_RETRY_LIMIT = 3;

    /**
     * Standard constructor. This constructor loads up the Sqlite JDBC device driver, so that step is not needed in
     * extending classes.
     */
    public SqliteDataSet() {
        try {
            synchronized(java.sql.DriverManager.class) { //deadlocks can result if more than one thread call this concurrently
                Class.forName("org.sqlite.JDBC");
            }
        } catch (ClassNotFoundException e) {
            throw new TableDataException("JDBC driver not found: org.sqlite.JDBC");
        }
    }

    protected IsolatedResultSet executeSqlQueryFilterless(String sqlQuery) {
        checkSqlQuery(sqlQuery);
        return super.executeSqlQueryFilterless(sqlQuery);
    }

    protected int executeSqlUpdateFilterless(String sqlQuery) {
        checkSqlQuery(sqlQuery);
        return super.executeSqlUpdateFilterless(sqlQuery);
    }

    protected ResultSet executeSqlQuery(String sql, Statement s) throws SQLException {
        SQLException innerE = null;
        int retryCount = 0;
        while (retryCount < LOCKED_DATABASE_QUERY_RETRY_LIMIT) {
            try {
                return s.executeQuery(sql);
            } catch (SQLException e) {
                innerE = e;
                if (e.getMessage().equals("database is locked")) {
                    retryCount++;
                } else {
                    throw e;
                }
            }
        }
        throw innerE;
    }

    protected ResultSet executePreparedStatement(PreparedStatement statement) throws SQLException {
        SQLException innerE = null;
        int retryCount = 0;
        while (retryCount < LOCKED_DATABASE_QUERY_RETRY_LIMIT) {
            try {
                return statement.executeQuery();
            } catch (SQLException e) {
                innerE = e;
                if (e.getMessage().equals("database is locked")) {
                    retryCount++;
                    System.out.println("db locked: " + retryCount + " " + e.getErrorCode() + " " + e.getSQLState());
                } else {
                    throw e;
                }
            }
        }
        throw innerE;
   }


    private void checkSqlQuery(String sqlQuery) {
        if (sqlQuery.indexOf("DROP COLUMN") > -1)
            throw new TableDataException("Sqlite data sets do not allow removal of columns.");
    }

    public String formRowEntry(Object cellData, DataType type) {
        switch (type) {
            case BOOLEAN : try {
                return ((Boolean) cellData) ? "1" : "0";
            } catch (ClassCastException e) {
                throw new TableDataException(TableDataException.INVALID_DATA_TYPE,cellData.getClass().getName(),type.getPrimitiveTypeString());
            }
            default : return super.formRowEntry(cellData,type);
        }
    }
}
