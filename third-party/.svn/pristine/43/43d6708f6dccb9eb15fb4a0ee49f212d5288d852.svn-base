package com.pb.sawdust.tabledata.sql.impl.hsqldb;

import com.pb.sawdust.tabledata.sql.impl.JdbcServerSqlDataSet;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

/**
 * totally untested - should not be used
 * @author crf <br/>
 *         Started: Dec 2, 2008 8:29:00 AM
 */
public class HsqldbServerDataSet extends JdbcServerSqlDataSet {
    private boolean useDefaultUsernameAndPassword = true;

    public HsqldbServerDataSet(ServerAddress address, int connectionLimit) {
        super(address,connectionLimit);
    }

    public HsqldbServerDataSet(ServerAddress address) {
        super(address,Integer.MAX_VALUE);
    }

    protected boolean checkProtocol(Protocol protocol) {
        return protocol instanceof HsqldbProtocol;
    }

    public void useDefaultNameAndPassword(boolean useDefaultUsernameAndPassword) {
        this.useDefaultUsernameAndPassword = useDefaultUsernameAndPassword;
    }

    protected String getJdbcClassName() {
        return "org.hsqldb.jdbcDriver";
    }

    protected String formConnectionUrl(String serverUrl) {
        return "jdbc:hsqldb:" + serverUrl;
    }

    protected Connection getConnection(String connectionUrl) throws SQLException {
        if (useDefaultUsernameAndPassword) {
            return DriverManager.getConnection(formConnectionUrl(),"sa","");
        } else {
            //todo: determine a good way to get a username and password
            return DriverManager.getConnection(formConnectionUrl(),"sa","");
        }
    }

    /**
     * The {@code HdqldbProtocol} provides the differnet connection protocols available from the HSQLDB database.
     */
    public static enum HsqldbProtocol implements Protocol {
        /**
         * The hsql protocol.
         */
        HSQL("hsql"),
        /**
         * The secure (ssl/tls) hsql protocol.
         */
        HSQLS("hsqls"),
        /**
         * The http protocol.
         */
        HTTP("http"),
        /**
         * The secure (ssl/tls) http protocol.
         */
        HTTPS("https");

        private final String protocolName;

        private HsqldbProtocol(String protocolName) {
            this.protocolName = protocolName;
        }

        public String getProtocolName() {
            return protocolName;
        }
    }
}
