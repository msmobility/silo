package com.pb.sawdust.tabledata.sql.impl.h2;

import com.pb.sawdust.tabledata.sql.impl.JdbcFileSqlDataSet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * The {@code H2FileDataSet} class provides a {@code SqlDataSet} implementation which connects to an H2 embedded file
 * database. Currently it only allows connections to databases with the default username ("sa") and password ("").
 *
 * @author crf <br/>
 *         Started: Dec 8, 2008 2:29:12 PM
 */
public class H2FileDataSet extends JdbcFileSqlDataSet {
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
    public H2FileDataSet(String databaseFilePath, int connectionLimit) {
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
    public H2FileDataSet(String databaseFilePath) {
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
        return "org.h2.Driver";
    }

    protected Connection getConnection(String connectionUrl) throws SQLException {
        if (useDefaultUsernameAndPassword) {
            return DriverManager.getConnection(formConnectionUrl(),"sa","");
        } else {
            //todo: determine a good way to get a username and password
            return DriverManager.getConnection(formConnectionUrl(),"sa","");
        }
    }

    protected String formConnectionUrl(String databaseFilePath) {
        return "jdbc:h2:" + databaseFilePath;
    }
}
