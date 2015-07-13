package com.pb.sawdust.tabledata.sql.impl.h2;

import com.pb.sawdust.tabledata.sql.impl.JdbcMemorySqlDataSet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * The {@code H2MemoryDataSet} class provides a {@code SqlDataSet} implementation which connects to an H2 in-memory
 * embedded database.
 *
 * @author crf <br/>
 *         Started: Dec 7, 2008 5:02:44 PM
 */
public class H2MemoryDataSet extends JdbcMemorySqlDataSet {

    /**
     * The default database name.
     */
    public static final String DEFAULT_DATABASE_NAME = ".";

    /**
     * Constructor specifying the database name and connection limit. If an in-memory database with the specified name
     * does nor already exist in the JVM instance, then a new one will be created. Once the maximum number of connections
     * have been made, then requests for more connections will require that previously made connections be recycled; if
     * available, then an exception may be thrown, or a deadlock may result.
     *
     * @param databaseName
     *        The name of the database.
     *
     * @param connectionLimit
     *        The maximum number of simultaneous connections allowed to the database.
     */
    public H2MemoryDataSet(String databaseName, int connectionLimit) {
        super(databaseName,connectionLimit);
    }

    /**
     * Constructor specifying the database name. If an in-memory database with the specified name does nor already exist
     * in the JVM instance, then a new one will be created. The maximum number of simultaneous connections to the
     * database allowed is set at {@code Integer.MAX_VALUE}.
     *
     * @param databaseName
     *        The name of the database.
     */
    public H2MemoryDataSet(String databaseName) {
        this(databaseName,Integer.MAX_VALUE);
    }

    /**
     * Constructor specifying the connection limit. The database connected to will be named {@code DEFAULT_DATABASE_NAME}.
     * Once the maximum number of connections have been made, then requests for more connections will require that
     * previously made connections be recycled; if available, then an exception may be thrown, or a deadlock may result.
     *
     * @param connectionLimit
     *        The maximum number of simultaneous connections allowed to the database.
     */
    public H2MemoryDataSet(int connectionLimit) {
        this(DEFAULT_DATABASE_NAME,connectionLimit);
    }

    /**
     * Constructor creating a default {@code H2MemoryDataSet}. The database connected to will be named
     * {@code DEFAULT_DATABASE_NAME}, and the maximum number of simultaneous connections to the database allowed will be
     * set at {@code Integer.MAX_VALUE}.
     */
    public H2MemoryDataSet() {
        this(DEFAULT_DATABASE_NAME);
    }

    protected String getJdbcClassName() {
        return "org.h2.Driver";
    }

    protected Connection getConnection(String connectionUrl) throws SQLException {
        return DriverManager.getConnection(formConnectionUrl(),"sa","");
    }

    protected String formConnectionUrl(String databaseName) {
        return "jdbc:h2:mem:" + databaseName;
    }
}
