package com.pb.sawdust.tabledata.sql.impl.sqlite;

import com.pb.sawdust.tabledata.sql.SqlTableDataException;
import com.pb.sawdust.util.sql.wrappers.WrappedConnection;

import java.sql.*;
import java.util.Map;
import java.util.HashMap;

/**
 *  The {@code SqliteMemoryDataSet} class provides a {@code SqlDataSet} implementation which connects to a Sqlite
 * in-memory (embedded) database.
 *
 * @see com.pb.sawdust.tabledata.sql.impl.sqlite.SqliteDataSet
 *
 * @author crf <br/>
 *         Started: Dec 1, 2008 10:40:18 AM
 */
public class SqliteMemoryDataSet extends SqliteDataSet {
    private static Map<String,SqliteMemoryConnection> dataSetMap = new HashMap<String,SqliteMemoryConnection>();
    private final SqliteMemoryConnection connection;

    /**
     * The default database name.
     */
    public static final String DEFAULT_DATABASE_NAME = ".";

    /**
     * Constructor specifying the database name. If an in-memory database with the specified name does nor already exist
     * in the JVM instance, then a new one will be created.
     *
     * @param databaseName
     *        The name of the database.
     */
    public SqliteMemoryDataSet(String databaseName) {
        super();
        this.connection = getSqliteMemoryConnection(databaseName);
        setResultSetType(ResultSet.TYPE_FORWARD_ONLY);
    }

    /**
     * Constructor creating a default {@code H2MemoryDataSet}. The database connected to will be named
     * {@code DEFAULT_DATABASE_NAME}.
     */
    public SqliteMemoryDataSet() {
        this(DEFAULT_DATABASE_NAME);
    }

    public Connection getConnection() {
        return connection;
    }

    private static SqliteMemoryConnection getSqliteMemoryConnection(String databaseName) {
        if (!dataSetMap.containsKey(databaseName))
            dataSetMap.put(databaseName,getSqliteMemoryConnection());
        return dataSetMap.get(databaseName);
    }

    private static SqliteMemoryConnection getSqliteMemoryConnection() {
        try {
            return new SqliteMemoryConnection(DriverManager.getConnection("jdbc:sqlite::memory:"));
        } catch (SQLException e) {
            throw new SqlTableDataException(e);
        }
    }

    private static class SqliteMemoryConnection extends WrappedConnection {

        private SqliteMemoryConnection(Connection connection) {
            super(connection);
        }

        public void close() throws SQLException {
            //do nothing
        }

        private void closeSqliteConnection() throws SQLException {
            connection.close();
        }
    }
}
