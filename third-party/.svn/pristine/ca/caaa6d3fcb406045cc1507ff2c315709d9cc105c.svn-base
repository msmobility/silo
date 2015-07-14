/*
 * Copyright 1999,2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.xmlrpc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.EmptyStackException;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.commons.codec.binary.Base64;

/**
 * A minimal web server that exclusively handles XML-RPC requests.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author Daniel L. Rall
 */
public class WebServer implements Runnable
{
    protected XmlRpcServer xmlrpc;

    protected ServerSocket serverSocket;
    protected Thread listener;
    protected Vector accept, deny;
    protected Stack threadpool;
    protected ThreadGroup runners;

    // Inputs to setupServerSocket()
    private InetAddress address;
    private int port;

    private boolean paranoid;

    protected static final byte[] ctype =
        toHTTPBytes("Content-Type: text/xml\r\n");
    protected static final byte[] clength =
        toHTTPBytes("Content-Length: ");
    protected static final byte[] newline = toHTTPBytes("\r\n");
    protected static final byte[] doubleNewline = toHTTPBytes("\r\n\r\n");
    protected static final byte[] conkeep =
        toHTTPBytes("Connection: Keep-Alive\r\n");
    protected static final byte[] conclose =
        toHTTPBytes("Connection: close\r\n");
    protected static final byte[] ok = toHTTPBytes(" 200 OK\r\n");
    protected static final byte[] server =
        toHTTPBytes("Server: Apache XML-RPC 1.0\r\n");
    protected static final byte[] wwwAuthenticate =
        toHTTPBytes("WWW-Authenticate: Basic realm=XML-RPC\r\n");

    private static final String HTTP_11 = "HTTP/1.1";
    private static final String STAR = "*";

    /**
     * This <em>can</em> be called from command line, but you'll have to edit
     * and recompile to change the server port or handler objects. By default,
     * it sets up the following responders:
     * <ul>
     *   <li> A java.lang.String object </li>
     *   <li> The java.lang.Math class (making its static methods callable via
     *        XML-RPC) </li>
     *   <li> An Echo handler that returns the argument array </li>
     * </ul>
     *
     * @see #addDefaultHandlers()
     */
    public static void main(String[] argv)
    {
        int p = determinePort(argv, 8080);
        // XmlRpc.setDebug (true);
        XmlRpc.setKeepAlive(true);
        WebServer webserver = new WebServer(p);

        try
        {
            webserver.addDefaultHandlers();
            webserver.start();
        }
        catch (Exception e)
        {
            System.err.println("Error running web server");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Examines command line arguments from <code>argv</code>.  If a
     * port may have been provided, parses that port (exiting with
     * error status if the port cannot be parsed).  If no port is
     * specified, defaults to <code>defaultPort</code>.
     *
     * @param defaultPort The port to use if none was specified.
     */
    protected static int determinePort(String[] argv, int defaultPort)
    {
        int port = defaultPort;
        if (argv.length > 0)
        {
            try
            {
                port = Integer.parseInt(argv[0]);
            }
            catch (NumberFormatException nfx)
            {
                System.err.println("Error parsing port number: " + argv[0]);
                System.err.println("Usage: java " + WebServer.class.getName()
                                   + " [port]");
                System.exit(1);
            }
        }
        return port;
    }

    /**
     * Creates a web server at the specified port number.
     */
    public WebServer(int port)
    {
        this(port, null);
    }

    /**
     * Creates a web server at the specified port number and IP address.
     */
    public WebServer(int port, InetAddress addr)
    {
        this(port, addr, new XmlRpcServer());
    }

    /**
     * Creates a web server at the specified port number and IP
     * address.
     */
    public WebServer(int port, InetAddress addr, XmlRpcServer xmlrpc)
    {
        this.address = addr;
        this.port = port;
        this.xmlrpc = xmlrpc;
        accept = new Vector();
        deny = new Vector();
        threadpool = new Stack();
        runners = new ThreadGroup("XML-RPC Runner");
    }

    /**
     * Returns the US-ASCII encoded byte representation of text for
     * HTTP use (as per section 2.2 of RFC 2068).
     */
    protected static final byte[] toHTTPBytes(String text)
    {
        try
        {
            return text.getBytes("US-ASCII");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new Error(e.getMessage() +
                            ": HTTP requires US-ASCII encoding");
        }
    }

    /**
     * Factory method to manufacture the server socket.  Useful as a
     * hook method for subclasses to override when they desire
     * different flavor of socket (i.e. a <code>SSLServerSocket</code>).
     *
     * @param port
     * @param backlog
     * @param addr If <code>null</code>, binds to
     * <code>INADDR_ANY</code>, meaning that all network interfaces on
     * a multi-homed host will be listening.
     * @exception Exception Error creating listener socket.
     */
    protected ServerSocket createServerSocket(int port, int backlog,
            InetAddress addr)
            throws Exception
    {
        return new ServerSocket(port, backlog, addr);
    }

    /**
     * Initializes this server's listener socket with the specified
     * attributes, assuring that a socket timeout has been set.  The
     * {@link #createServerSocket(int, int, InetAddress)} method can
     * be overridden to change the flavor of socket used.
     *
     * @see #createServerSocket(int, int, InetAddress)
     */
    private synchronized void setupServerSocket(int backlog)
            throws Exception
    {
        // Since we can't reliably set SO_REUSEADDR until JDK 1.4 is
        // the standard, try to (re-)open the server socket several
        // times.  Some OSes (Linux and Solaris, for example), hold on
        // to listener sockets for a brief period of time for security
        // reasons before relinquishing their hold.
        int attempt = 1;
        while (serverSocket == null)
        {
            try
            {
                serverSocket = createServerSocket(port, backlog, address);
            }
            catch (BindException e)
            {
                if (attempt == 10)
                {
                    throw e;
                }

                attempt++;
                Thread.sleep(1000);
            }
        }

        if (XmlRpc.debug)
        {
            StringBuffer msg = new StringBuffer();
            msg.append("Opened XML-RPC server socket for ");
            msg.append(address != null ? address.getHostName() : "localhost");
            msg.append(':').append(port);
            if (attempt > 1)
            {
                msg.append(" after ").append(attempt).append(" tries");
            }
            System.out.println(msg.toString());
        }

        // A socket timeout must be set.
        if (serverSocket.getSoTimeout() <= 0)
        {
            serverSocket.setSoTimeout(4096);
        }
    }

    /**
     * Spawns a new thread which binds this server to the port it's
     * configured to accept connections on.
     *
     * @see #run()
     */
    public void start()
    {
        try
        {
            setupServerSocket(50);
        }
        catch (Exception e)
        {
            listener = null;
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        // The listener reference is released upon shutdown().
        if (listener == null)
        {
            listener = new Thread(this, "XML-RPC Weblistener");
            // Not marked as daemon thread since run directly via main().
            listener.start();
        }
    }

    /**
     * Register a handler object with this name. Methods of this objects will be
     * callable over XML-RPC as "name.method".
     */
    public void addHandler(String name, Object target)
    {
        xmlrpc.addHandler(name, target);
    }

    /**
     * Adds the bundled handlers to the server.  Called by {@link
     * #main(String[])}.
     */
    protected void addDefaultHandlers()
        throws Exception
    {
        // webserver.setParanoid (true);
        // webserver.acceptClient ("192.168.*.*");
        addHandler("string", "Welcome to XML-RPC!");
        addHandler("math", Math.class);
        addHandler("auth", new AuthDemo());
        addHandler("$default", new Echo());
        // XmlRpcClients can be used as Proxies in XmlRpcServers which is a
        // cool feature for applets.
        String url = "http://www.mailtothefuture.com:80/RPC2";
        addHandler("mttf", new XmlRpcClient(url));
        SystemHandler system = new SystemHandler();
        system.addDefaultSystemHandlers();
        addHandler("system", system);
    }

    /**
     * Remove a handler object that was previously registered with this server.
     */
    public void removeHandler(String name)
    {
        xmlrpc.removeHandler(name);
    }

    /**
     * Switch client filtering on/off.
     * @see #acceptClient(java.lang.String)
     * @see #denyClient(java.lang.String)
     */
    public void setParanoid(boolean p)
    {
        paranoid = p;
    }

    /**
     * Add an IP address to the list of accepted clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must call
     * setParanoid(true) in order for this to have any effect.
     *
     * @see #denyClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public void acceptClient(String address) throws IllegalArgumentException
    {
        try
        {
            AddressMatcher m = new AddressMatcher(address);
            accept.addElement(m);
        }
        catch (Exception x)
        {
            throw new IllegalArgumentException("\"" + address
                    + "\" does not represent a valid IP address");
        }
    }

    /**
     * Add an IP address to the list of denied clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must call
     * setParanoid(true) in order for this to have any effect.
     *
     * @see #acceptClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public void denyClient(String address) throws IllegalArgumentException
    {
        try
        {
            AddressMatcher m = new AddressMatcher(address);
            deny.addElement(m);
        }
        catch (Exception x)
        {
            throw new IllegalArgumentException("\"" + address
                    + "\" does not represent a valid IP address");
        }
    }

    /**
     * Checks incoming connections to see if they should be allowed.
     * If not in paranoid mode, always returns true.
     *
     * @param s The socket to inspect.
     * @return Whether the connection should be allowed.
     */
    protected boolean allowConnection(Socket s)
    {
        if (!paranoid)
        {
            return true;
        }

        int l = deny.size();
        byte address[] = s.getInetAddress().getAddress();
        for (int i = 0; i < l; i++)
        {
            AddressMatcher match = (AddressMatcher)deny.elementAt(i);
            if (match.matches(address))
            {
                return false;
            }
        }
        l = accept.size();
        for (int i = 0; i < l; i++)
        {
            AddressMatcher match = (AddressMatcher)accept.elementAt(i);
            if (match.matches(address))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * DEPRECATED: Do not use this method, it will be removed soon.
     * Use {@link #allowConnection(Socket)} instead.
     *
     * @deprecated Use allowConnection(Socket) instead.
     * @see #allowConnection(Socket)
     */
    protected boolean checkSocket(Socket s)
    {
        return allowConnection(s);
    }

    /**
     * Listens for client requests until stopped.  Call {@link
     * #start()} to invoke this method, and {@link #shutdown()} to
     * break out of it.
     *
     * @throws RuntimeException Generally caused by either an
     * <code>UnknownHostException</code> or <code>BindException</code>
     * with the vanilla web server.
     *
     * @see #start()
     * @see #shutdown()
     */
    public void run()
    {
        try
        {
            while (listener != null)
            {
                try
                {
                    Socket socket = serverSocket.accept();
                    try
                    {
                        socket.setTcpNoDelay(true);
                    }
                    catch (SocketException socketOptEx)
                    {
                        System.err.println(socketOptEx);
                    }

                    if (allowConnection(socket))
                    {
                        Runner runner = getRunner();
                        runner.handle(socket);
                    }
                    else
                    {
                        socket.close();
                    }
                }
                catch (InterruptedIOException checkState)
                {
                    // Timeout while waiting for a client (from
                    // SO_TIMEOUT)...try again if still listening.
                }
                catch (Exception ex)
                {
                    System.err.println("Exception in XML-RPC listener loop ("
                            + ex + ").");
                    if (XmlRpc.debug)
                    {
                        ex.printStackTrace();
                    }
                }
                catch (Error err)
                {
                    System.err.println("Error in XML-RPC listener loop ("
                            + err + ").");
                    err.printStackTrace();
                }
            }
        }
        catch (Exception exception)
        {
            System.err.println("Error accepting XML-RPC connections ("
                    + exception + ").");
            if (XmlRpc.debug)
            {
                exception.printStackTrace();
            }
        }
        finally
        {
            if (serverSocket != null)
            {
                try
                {
                    serverSocket.close();
                    if (XmlRpc.debug)
                    {
                        System.out.print("Closed XML-RPC server socket");
                    }
                    serverSocket = null;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            // Shutdown our Runner-based threads
            if (runners != null)
            {
                ThreadGroup g = runners;
                runners = null;
                try
                {
                    g.interrupt();
                }
                catch (Exception e)
                {
                    System.err.println(e);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Stop listening on the server port.  Shutting down our {@link
     * #listener} effectively breaks it out of its {@link #run()}
     * loop.
     *
     * @see #run()
     */
    public synchronized void shutdown()
    {
        // Stop accepting client connections
        if (listener != null)
        {
            Thread l = listener;
            listener = null;
            l.interrupt();
        }
    }

    /**
     *
     * @return
     */
    protected Runner getRunner()
    {
        try
        {
            return (Runner)threadpool.pop();
        }
        catch (EmptyStackException empty)
        {
            int maxRequests = XmlRpc.getMaxThreads();
            if (runners.activeCount() > XmlRpc.getMaxThreads())
            {
                throw new RuntimeException("System overload: Maximum number " +
                                           "of concurrent requests (" +
                                           maxRequests + ") exceeded");
            }
            return new Runner();
        }
    }

    /**
     * Put <code>runner</code> back into {@link #threadpool}.
     *
     * @param runner The instance to reclaim.
     */
    void repoolRunner(Runner runner)
    {
        threadpool.push(runner);
    }

    /**
     * Responsible for handling client connections.
     */
    class Runner implements Runnable
    {
        Thread thread;
        Connection con;
        int count;

        /**
         * Handles the client connection on <code>socket</code>.
         *
         * @param socket The source to read the client's request from.
         */
        public synchronized void handle(Socket socket) throws IOException
        {
            con = new Connection(socket);
            count = 0;
            if (thread == null || !thread.isAlive())
            {
                thread = new Thread(runners, this);
                thread.start();
            }
            else
            {
                // Wake the thread waiting in our run() method.
                this.notify();
            }
        }

        /**
         * Delegates to <code>con.run()</code>.
         */
        public void run()
        {
            while (con != null && Thread.currentThread() == thread)
            {
                con.run();
                count++;
                con = null;

                if (count > 200 || threadpool.size() > 20)
                {
                    // We're old, or the number of threads in the pool
                    // is large.
                    return;
                }
                synchronized(this)
                {
                    repoolRunner(this);
                    try
                    {
                        this.wait();
                    }
                    catch (InterruptedException ir)
                    {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    /**
     *
     */
    class Connection implements Runnable
    {
        private Socket socket;
        private BufferedInputStream input;
        private BufferedOutputStream output;
        private String user, password;
        private Base64 base64Codec;
        byte[] buffer;

        /**
         *
         * @param socket
         * @throws IOException
         */
        public Connection (Socket socket) throws IOException
        {
            // set read timeout to 30 seconds
            socket.setSoTimeout (30000);

            this.socket = socket;
            input = new BufferedInputStream(socket.getInputStream());
            output = new BufferedOutputStream(socket.getOutputStream());
        }

        /**
         *
         */
        public void run()
        {
            try
            {
                boolean keepAlive = false;

                do
                {
                    // reset user authentication
                    user = null;
                    password = null;
                    String line = readLine();
                    // Netscape sends an extra \n\r after bodypart, swallow it
                    if (line != null && line.length() == 0)
                    {
                        line = readLine();
                    }
                    if (XmlRpc.debug)
                    {
                        System.out.println(line);
                    }
                    int contentLength = -1;

                    // tokenize first line of HTTP request
                    StringTokenizer tokens = new StringTokenizer(line);
                    String method = tokens.nextToken();
                    String uri = tokens.nextToken();
                    String httpVersion = tokens.nextToken();
                    keepAlive = XmlRpc.getKeepAlive()
                            && HTTP_11.equals(httpVersion);
                    do
                    {
                        line = readLine();
                        if (line != null)
                        {
                            if (XmlRpc.debug)
                            {
                                System.out.println(line);
                            }
                            String lineLower = line.toLowerCase();
                            if (lineLower.startsWith("content-length:"))
                            {
                                contentLength = Integer.parseInt(
                                        line.substring(15).trim());
                            }
                            if (lineLower.startsWith("connection:"))
                            {
                                keepAlive = XmlRpc.getKeepAlive() &&
                                        lineLower.indexOf("keep-alive") > -1;
                            }
                            if (lineLower.startsWith("authorization: basic "))
                            {
                                parseAuth (line);
                            }
                        }
                    }
                    while (line != null && line.length() != 0);

                    if ("POST".equalsIgnoreCase(method))
                    {
                        ServerInputStream sin = new ServerInputStream(input,
                                contentLength);
                        try
                        {
                            byte[] result = xmlrpc.execute(sin, user, password);
                            writeResponse(result, httpVersion, keepAlive);
                        }
                        catch (AuthenticationFailed unauthorized)
                        {
                            keepAlive = false;
                            writeUnauthorized(httpVersion, method);
                        }
                    }
                    else
                    {
                        keepAlive = false;
                        writeBadRequest(httpVersion, method);
                    }
                    output.flush();
                }
                while (keepAlive);
            }
            catch (Exception exception)
            {
                if (XmlRpc.debug)
                {
                    exception.printStackTrace();
                }
                else
                {
                    System.err.println(exception);
                }
            }
            finally
            {
                try
                {
                    if (socket != null)
                    {
                        socket.close();
                    }
                }
                catch (IOException ignore)
                {
                }
            }
        }

        /**
         *
         * @return
         * @throws IOException
         */
        private String readLine() throws IOException
        {
            if (buffer == null)
            {
                buffer = new byte[2048];
            }
            int next;
            int count = 0;
            for (;;)
            {
                next = input.read();
                if (next < 0 || next == '\n')
                {
                    break;
                }
                if (next != '\r')
                {
                    buffer[count++] = (byte) next;
                }
                if (count >= buffer.length)
                {
                    throw new IOException("HTTP Header too long");
                }
            }
            return new String(buffer, 0, count);
        }

        /**
         *
         * @param line
         */
        private void parseAuth(String line)
        {
            try
            {
                byte[] c = base64Codec.decode(toHTTPBytes(line.substring(21)));
                String str = new String(c);
                int col = str.indexOf(':');
                user = str.substring(0, col);
                password = str.substring(col + 1);
            }
            catch (Throwable ignore)
            {
            }
        }

        private void writeResponse(byte[] payload, String httpVersion,
                                   boolean keepAlive)
            throws IOException
        {
            output.write(toHTTPBytes(httpVersion));
            output.write(ok);
            output.write(server);
            output.write(keepAlive ? conkeep : conclose);
            output.write(ctype);
            output.write(clength);
            output.write(toHTTPBytes(Integer.toString(payload.length)));
            output.write(doubleNewline);
            output.write(payload);
        }

        private void writeBadRequest(String httpVersion, String httpMethod)
            throws IOException
        {
            output.write(toHTTPBytes(httpVersion));
            output.write(toHTTPBytes(" 400 Bad Request"));
            output.write(newline);
            output.write(server);
            output.write(newline);
            output.write(toHTTPBytes("Method " + httpMethod +
                                     " not implemented (try POST)"));
        }

        private void writeUnauthorized(String httpVersion, String httpMethod)
            throws IOException
        {
            output.write(toHTTPBytes(httpVersion));
            output.write(toHTTPBytes(" 401 Unauthorized"));
            output.write(newline);
            output.write(server);
            output.write(wwwAuthenticate);
            output.write(newline);
            output.write(toHTTPBytes("Method " + httpMethod + " requires a " +
                                     "valid user name and password"));
        }
    }

    /**
     *
     */
    class AddressMatcher
    {
        int pattern[];

        /**
         *
         * @param address
         * @throws Exception
         */
        public AddressMatcher(String address) throws Exception
        {
            pattern = new int[4];
            StringTokenizer st = new StringTokenizer(address, ".");
            if (st.countTokens() != 4)
            {
                throw new Exception("\"" + address
                        + "\" does not represent a valid IP address");
            }
            for (int i = 0; i < 4; i++)
            {
                String next = st.nextToken();
                if (STAR.equals(next))
                {
                    pattern[i] = 256;
                }
                else
                {
                    pattern[i] = (byte) Integer.parseInt(next);
                }
            }
        }

        /**
         *
         * @param address
         * @return
         */
        public boolean matches (byte address[])
        {
            for (int i = 0; i < 4; i++)
            {
                if (pattern[i] > 255)// wildcard
                {
                    continue;
                }
                if (pattern[i] != address[i])
                {
                    return false;
                }
            }
            return true;
        }
    }
}
