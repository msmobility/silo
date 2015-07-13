package com.pb.sawdust.util.sql;

import com.pb.sawdust.util.sql.wrappers.WrappedConnection;
import com.pb.sawdust.util.Pool;
import java.sql.*;

/**
 * The {@code SimpleConnectionPool} class provides a simple pool for use with {@code java.sql.Connection}s. The pool
 * allows connections to be reused; the {@code Connection.close()} method returns the connection to the pool to be
 * reused. The pool allows a specified number of connections to be reused, once that number has been reached, then any
 * request for a connection will block (wait for a connection in the pool to be closed) or return {@code null}, depending
 * on the constructor specifications. A previously opened connection will always be used in preference of a new one, when
 * possible. This connection pool is very simple in that it offers no cleanup: stale connections (broken or unused for a
 * long time) cannot be reclaimed nor removed from use.
 *
 * @author crf <br/>
 *         Started: Sep 30, 2008 11:14:02 PM
 */
abstract public class SimpleConnectionPool {
    private final Pool<SimpleConnectionPoolConnection> pool;

    /**
     * Constructor specifying the connection limit and whether this pool should block when it is full.
     *
     * @param connectionLimit
     *        The maximum number of connections this pool can open.
     *
     * @param blockingPool
     *        If {@code true}, this pool will block on {@link #getConnection()} if its connection limit has been reached
     *        and all connections are in use; if {@code false}, {@link #getConnection()} will return {@code null}.
     */
    public SimpleConnectionPool(int connectionLimit, boolean blockingPool) {
        pool = new SimpleConnectionPoolPool(connectionLimit,blockingPool);
    }

    /**
     * Constructor specifying the connection limit for a non-blocking pool. This is an alias for
     * {@code SimpleConnectionPool(connectionLimit,false)}.
     *
     * @param connectionLimit
     *        The maximum number of connections this pool can open.
     */
    public SimpleConnectionPool(int connectionLimit) {
        pool = new SimpleConnectionPoolPool(connectionLimit);
    }

    /**
     * Create a new connection to be used by this pool.
     *
     * @return a new sql connection for use in this pool.
     */
    abstract protected Connection createConnection();

    /**
     * Get a connection from this pool. If this pool is non-blocking, then calls to this method when the pool is
     * full (<i>i.e.</i> its connection limit has been reached) and there are no available connections will return
     * {@code null}; otherwise it will block waiting for an open connection.
     *
     * @return a connection from this pool, or {@code null} if no connections are available and the pool is non-blocking.
     *
     * @throws RuntimeException if the pool is blocking and an {@code InterruptedException} is thrown while waiting for
     *         a connection.
     */
    public Connection getConnection() {
        Pool<SimpleConnectionPoolConnection>.PoolItem pi = pool.get();
        pi.get().setPoolItem(pi);
        return pi.get();
    }

    private class SimpleConnectionPoolConnection extends WrappedConnection {
        private Pool<SimpleConnectionPoolConnection>.PoolItem poolItem = null;

        private SimpleConnectionPoolConnection(Connection connection) {
            super(connection);
        }

        private void setPoolItem(Pool<SimpleConnectionPoolConnection>.PoolItem poolItem) {
            this.poolItem = poolItem;
        }

        public void close() throws SQLException {
            poolItem.finished();
        }
    }


    private class SimpleConnectionPoolPool extends Pool<SimpleConnectionPoolConnection> {
        public SimpleConnectionPoolPool(int limit, boolean blockingPool) {
            super(limit,blockingPool);
        }

        public SimpleConnectionPoolPool(int limit) {
            super(limit);
        }

        protected SimpleConnectionPoolConnection getNew() {
            return new SimpleConnectionPoolConnection(createConnection());
        }
    }

//    private final BlockingQueue<Connection> connectionQueue;
//    private final int connectionLimit;
//    private volatile int createdConnections = 0;
//    private volatile boolean connectionLimitReached = false;
//    private final boolean blockingPool;
//
//    /**
//     * Constructor specifying the connection limit and whether this pool should block when it is full.
//     *
//     * @param connectionLimit
//     *        The maximum number of connections this pool can open.
//     *
//     * @param blockingPool
//     *        If {@code true}, this pool will block on {@link #getConnection()} if its connection limit has been reached
//     *        and all connections are in use; if {@code false}, {@link #getConnection()} will return {@code null}.
//     */
//    public SimpleConnectionPool(int connectionLimit, boolean blockingPool) {
//        this.connectionLimit = connectionLimit;
//        connectionQueue = new LinkedBlockingQueue<Connection>(connectionLimit);
//        this.blockingPool = blockingPool;
//    }
//
//    /**
//     * Constructor specifying the connection limit for a non-blocking pool. This is an alias for
//     * {@code SimpleConnectionPool(connectionLimit,false)}.
//     *
//     * @param connectionLimit
//     *        The maximum number of connections this pool can open.
//     */
//    public SimpleConnectionPool(int connectionLimit) {
//        this(connectionLimit,false);
//    }
//
//    /**
//     * Create a new connection to be used by this pool.
//     *
//     * @return a new sql connection for use in this pool.
//     */
//    abstract protected Connection createConnection();
//
//    /**
//     * Get a connection from this pool. If this pool is non-blocking, then calls to this method when the pool is
//     * full (<i>i.e.</i> its connection limit has been reached) and there are no available connections will return
//     * {@code null}; otherwise it will block waiting for an open connection.
//     *
//     * @return a connection from this pool, or {@code null} if no connections are available and the pool is non-blocking.
//     *
//     * @throws RuntimeException if the pool is blocking and an {@code InterruptedException} is thrown while waiting for
//     *         a connection.
//     */
//    public Connection getConnection() {
//        Connection c = connectionQueue.poll();
//        if (c == null)
//            synchronized(this) {
//                if (!connectionLimitReached) {
//                    c = new SimpleConnectionPoolConnection(createConnection(),connectionQueue);
//                    connectionLimitReached = ++createdConnections == connectionLimit;
//                } else if (blockingPool){
//                    try {
//                        c = connectionQueue.take();
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        return c;
//    }
//
//    private class SimpleConnectionPoolConnection extends WrappedConnection {
//        private final BlockingQueue<Connection> connectionQueue;
//
//        private SimpleConnectionPoolConnection(Connection connection, BlockingQueue<Connection> connectionQueue) {
//            super(connection);
//            this.connectionQueue = connectionQueue;
//        }
//
//        public void close() throws SQLException {
//            connectionQueue.add(this);
//        }
//    }
}
