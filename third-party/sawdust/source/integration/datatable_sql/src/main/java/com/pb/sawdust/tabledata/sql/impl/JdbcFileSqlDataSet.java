package com.pb.sawdust.tabledata.sql.impl;

/**
 * The {@code JdbcFileSqlDataSet} provides a skeletal framework for {@code SqlDataSet} implementations using a
 * database running from (or explicitly connecting to) a file.
 *
 * @author crf <br/>
 *         Started: Dec 1, 2008 11:22:31 AM
 */
public abstract class JdbcFileSqlDataSet extends JdbcSqlDataSet {
    private final String databaseFilePath;

    /**
     * Get the URL used to connect to a file-based database with a specified database file path.
     *
     * @param databaseFilePath
     *        The path to the file (or folder) containing the database.
     *
     * @return the appropriate connection URL to connect to the database held in {@code databaseFilePath}.
     */
    protected abstract String formConnectionUrl(String databaseFilePath);

    /**
     * Constructor specifying the database name and maximum number of simultaneous connections allowed to the database
     * from this data set. Depending on the implementation specifics, if a database located at {@code databaseFilePath}
     * does not exists, a new one may or may not be initialized. As for the connection limit, once the maximum
     * number of connections have been made, then requests for more connections will require that previously made
     * connections be recycled; if no previously opened connections are available, then an exception may be thrown, or
     * a deadlock may result.
     *
     * @param databaseFilePath
     *        The path to the file (or folder) containing the database.
     *
     * @param connectionLimit
     *        The maximum number of simultaneous connections that can be made through this data set.
     */
    public JdbcFileSqlDataSet(String databaseFilePath, int connectionLimit) {
        super(connectionLimit);
        this.databaseFilePath = cleanFilePath(databaseFilePath);
    }

    private String cleanFilePath(String filePath) {
        return filePath.replace("\\","/");
    }

    protected String formConnectionUrl() {
        return formConnectionUrl(databaseFilePath);
    }
}
