package com.pb.sawdust.tabledata.sql.impl.hsqldb;

import com.pb.sawdust.tabledata.sql.impl.JdbcFileSqlDataSet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * The {@code HsqldbFileDataSet} class provides a {@code SqlDataSet} implementation which connects to an HSQLDB embedded
 * file database. Currently it only allows connections to databases with the default username ("sa") and password ("").
 *
 * @author crf <br/>
 *         Started: Dec 1, 2008 11:25:13 AM
 */
public class HsqldbFileDataSet extends JdbcFileSqlDataSet {
    private boolean useDefaultUsernameAndPassword = true;

    /**
     * Constructor specifying the path to the database and the connection limit. If a database in the specified directory
     * does not already exist, then a new one will be created. Once the maximum number of connections have been made,
     * then requests for more connections will require that previously made connections be recycled; if available, then
     * an exception may be thrown, or a deadlock may result.
     *
     * @param databaseFilePath
     *        The path to the directory holding the database.
     *
     * @param connectionLimit
     *        The maximum number of simultaneous connections allowed to the database.
     */
    public HsqldbFileDataSet(String databaseFilePath, int connectionLimit) {
        super(databaseFilePath,connectionLimit);
    }

    /**
     * Constructor specifying the path to the database. If a database in the specified directory does not already exist,
     * then a new one will be created. The maximum number of simultaneous connections to the database allowed is set at
     * {@code Integer.MAX_VALUE}.
     *
     * @param databaseFilePath
     *        The path to the directory holding the database.
     */
    public HsqldbFileDataSet(String databaseFilePath) {
        this(databaseFilePath,Integer.MAX_VALUE);
    }

    /**
     * Use the default username and password to connect to the database.
     *
     * Currently, setting this to {@code false} does nothing.
     *
     * @param useDefaultUsernameAndPassword
     *        If {@code true}, use the default username and password, otherwise set up the data set to query the
     *        user for a username and password.
     */
    public void useDefaultNameAndPassword(boolean useDefaultUsernameAndPassword) {
        this.useDefaultUsernameAndPassword = useDefaultUsernameAndPassword;
    }

    protected String getJdbcClassName() {
        return "org.hsqldb.jdbcDriver";
    }

    protected Connection getConnection(String connectionUrl) throws SQLException {
        if (useDefaultUsernameAndPassword) {
            return DriverManager.getConnection(formConnectionUrl(),"sa","");
        } else {
            //todo: determine a good way to get a username and password
            return DriverManager.getConnection(formConnectionUrl(),"sa","");
        }
    }

    protected String formConnectionUrl(String databaseName) {
        return "jdbc:hsqldb:file:" + databaseName;
    }
}
