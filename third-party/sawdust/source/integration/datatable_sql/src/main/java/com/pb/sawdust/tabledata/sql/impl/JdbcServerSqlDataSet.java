package com.pb.sawdust.tabledata.sql.impl;

import com.pb.sawdust.tabledata.TableDataException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * The {@code JdbcServerSqlDataSet} provides a skeletal framework for {@code SqlDataSet} implementations using an
 * database running from a server.
 *
 * @author crf <br/>
 *         Started: Dec 2, 2008 8:16:38 AM
 */
public abstract class JdbcServerSqlDataSet extends JdbcSqlDataSet {
    private final ServerAddress address;

    /**
     * Get the JDBC connection url for a given server address.
     *
     * @param serverUrl
     *        The url to the database server.
     *
     * @return the JDBC server connection url for connecting to the server specified by {@code serverUrl}.
     */
    protected abstract String formConnectionUrl(String serverUrl);

    /**
     * Constructor specifying the database server url and the maximum number of simultaneous connections that can be
     * made through this data set.
     *
     * @param address
     *        The address to the database server.
     *
     * @param connectionLimit
     *        The maximum number of simultaneous connections that can be made through this data set.
     */
    public JdbcServerSqlDataSet(ServerAddress address, int connectionLimit) {
        super(connectionLimit);
        if (!checkProtocol(address.getProtocol()))
            throw new TableDataException("Invalid protocol for this JdbcServerSqlDataSet: " + address.getProtocol());
        this.address = address;
    }

    protected String formConnectionUrl() {
        return formConnectionUrl(address.getUrl());
    }

    /**
     * Check whether the specified protocol is valid for this data set.
     *
     * @param protocol
     *        The protocol in question.
     *
     * @return {@code true} if the protocol is valid, {@code false} if not.
     */
    protected boolean checkProtocol(Protocol protocol) {
        return true;
    }

    /**
     * The {@code Protocol} interface provides a simple framework for specifying connection protocols for database
     * server addresses. Examples of protocols are {@code http} and {@code https}.
     */
    public static interface Protocol{

        /**
         * Get the name of the protocol as it should be used in a server connection url.
         *
         * @return the name of the protocol as used in an url.
         */
        String getProtocolName();
    }

    /**
     * The {@code ServerAddress} class provides a structure for holding information about a database server connection
     * address. At the very least it holds information about the connection protocol and server host, but also may
     * include information about the connection port and database name.
     */
    public static class ServerAddress {
        private final URI address;
        private Protocol protocol;

        /**
         * Constructor specifying the name of the database, the connection protocol, the server host, and the connection
         * port.
         *
         * @param databaseName
         *        The name of the database.
         *
         * @param protocol
         *        The connection protocol.
         *
         * @param host
         *        The server hostname.
         *
         * @param port
         *        The connection port.
         *
         * @throws RuntimeException if any of the address information cannot be formed into a valid connection uri.
         */
        public ServerAddress(String databaseName, Protocol protocol, String host, int port) {
            this.protocol = protocol;
            try {
                address = new URI(protocol.getProtocolName(),null,host,port,databaseName,null,null);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Constructor specifying the connection protocol, the server host, and the connection port.
         *
         * @param protocol
         *        The connection protocol.
         *
         * @param host
         *        The server hostname.
         *
         * @param port
         *        The connection port.
         *
         * @throws RuntimeException if any of the address information cannot be formed into a valid connection uri.
         */
        public ServerAddress(Protocol protocol, String host, int port) {
            this(null,protocol,host,port);
        }

        /**
         * Constructor specifying the name of the database, the connection protocol, and the server host.
         *
         * @param databaseName
         *        The name of the database.
         *
         * @param protocol
         *        The connection protocol.
         *
         * @param host
         *        The server hostname.
         *
         * @throws RuntimeException if any of the address information cannot be formed into a valid connection url.
         */
        public ServerAddress(String databaseName, Protocol protocol, String host) {
            this(databaseName,protocol,host,-1);
        }

        /**
         * Constructor specifying the connection protocol and the server host.
         *
         * @param protocol
         *        The connection protocol.
         *
         * @param host
         *        The server hostname.
         *
         * @throws RuntimeException if any of the address information cannot be formed into a valid connection uri.
         */
        public ServerAddress(Protocol protocol, String host) {
            this(protocol,host,-1);
        }

        /**
         * Get the connection url corresponding to this server address.
         *
         * @return this server address's connectino url.
         */
        public String getUrl() {
            return address.toString();
        }

        /**
         * Get the connection protocol used by this server address.
         *
         * @return this server address's connection protocol.
         */
        public Protocol getProtocol() {
            return protocol;
        }
    }
}
