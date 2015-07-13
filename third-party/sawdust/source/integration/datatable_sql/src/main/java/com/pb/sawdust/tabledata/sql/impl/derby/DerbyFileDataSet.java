package com.pb.sawdust.tabledata.sql.impl.derby;

import com.pb.sawdust.tabledata.sql.impl.JdbcMemorySqlDataSet;
import com.pb.sawdust.tabledata.metadata.DataType;
import com.pb.sawdust.tabledata.TableDataException;

import java.io.File;

/**
 * The {@code DerbyFileDataSet} class provides a {@code SqlDataSet} implementation which connects to an Apache Derby (or
 * JavaDB) database.
 *
 * @author crf <br/>
 *         Started: Dec 6, 2008 2:01:17 PM
 */
public class DerbyFileDataSet extends JdbcMemorySqlDataSet {
    static {
        DerbyOutput.turnOffDerbyLog(); //turn this puppy off
    }

    private Boolean create;

    /**
     * Constructor specifying the path to the database, the connection limit, and whether the database should be created
     * if it does not already exist. Once the maximum number of connections have been made, then requests for more
     * connections will require that previously made connections be recycled; if no previously opened connections are
     * available, then an exception may be thrown, or a deadlock may result.
     *
     * @param databaseFilePath
     *        The path to the directory holding the database.
     *
     * @param connectionLimit
     *        The maximum number of simultaneous connections allowed to the database.
     *
     * @param create
     *        If {@code true}, then a new database will be initialized in {@code databaseFilePath} if one does not
     *        already exist.
     */
    public DerbyFileDataSet(String databaseFilePath, int connectionLimit, boolean create) {
        super(databaseFilePath,connectionLimit);
        this.create = create;
    }

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
    public DerbyFileDataSet(String databaseFilePath, int connectionLimit) {
        this(databaseFilePath,connectionLimit,!new File(databaseFilePath).exists());
    }

    /**
     * Constructor specifying the path to the database and whether the database should be created if it does not already
     * exist. The maximum number of simultaneous connections to the database allowed is set at {@code Integer.MAX_VALUE}.
     *
     * @param databaseFilePath
     *        The path to the directory holding the database.
     *
     * @param create
     *        If {@code true}, then a new database will be initialized in {@code databaseFilePath} if one does not
     *        already exist.
     */
    public DerbyFileDataSet(String databaseFilePath,boolean create) {
        this(databaseFilePath,Integer.MAX_VALUE,create);
    }

    /**
     * Constructor specifying the path to the database. If a database in the specified directory does not already exist,
     * then a new one will be created. The maximum number of simultaneous connections to the database allowed is set at
     * {@code Integer.MAX_VALUE}.
     *
     * @param databaseFilePath
     *        The path to the directory holding the database.
     */
    public DerbyFileDataSet(String databaseFilePath) {
        this(databaseFilePath,Integer.MAX_VALUE);
    }

    protected String getJdbcClassName() {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }

    protected String formConnectionUrl(String databaseFilePath) {
        String connectionUrl = "jdbc:derby:" + databaseFilePath;
//        synchronized (this) {
        //create will only raise a warning if already exists
        //  this function does not ensure a connection is made, so cannot issue
        //  create=true statement only to first call
        if (create) //{
            connectionUrl += ";create=true" ;
//                create = false;
//            }
//            else connectionUrl += ";create=true";
//        }
        return connectionUrl;
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

    public String getColumnDefinition(DataType columnType) {
        switch (columnType) {
            case BOOLEAN : return "SMALLINT";
            case BYTE : return "SMALLINT";
            case SHORT : return "SMALLINT";
            case INT : return "INTEGER";
            case LONG : return "BIGINT";
            case DOUBLE : return "DOUBLE";
            case FLOAT : return "FLOAT";
            case STRING : return "LONG VARCHAR";
            default : return null;
        }
    }
}
