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


package org.apache.xmlrpc.secure;


import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyStore;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcServer;

import com.sun.net.ssl.KeyManagerFactory;
import com.sun.net.ssl.SSLContext;

/**
 * A minimal web server that exclusively handles XML-RPC requests
 * over a secure channel.
 *
 * Standard security properties must be set before the SecureWebserver
 * can be used. The SecurityTool takes care of retrieving these
 * values, but the parent application must set the necessary
 * values before anything will work.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id: SecureWebServer.java,v 1.7 2005/04/22 10:25:58 hgomez Exp $
 */
public class SecureWebServer 
    extends WebServer
    implements SecurityConstants
{
    /**
     * Creates a secure web server configured to run on the specified
     * port number.
     *
     * @param int port number of secure web server.
     * @see #SecureWebServer(int, InetAddress)
     */
    public SecureWebServer(int port)
    {
        this(port, null);
    }

    /**
     * Creates a secure web server configured to run on the specified
     * port number and IP address.
     *
     * @param int port number of the secure web server
     * @param addr The IP address to bind to.
     * @see org.apache.xmlrpc.WebServer#WebServer(int, InetAddress)
     */
    public SecureWebServer(int port, InetAddress addr)
    {
        super(port, addr);
    }


    /**
     * Creates a secure web server at the specified port number and IP
     * address.
     */
    public SecureWebServer(int port, InetAddress addr, XmlRpcServer xmlrpc)
    {
        super(port, addr, xmlrpc);
    }

    /**
     * @see org.apache.xmlrpc.WebServer#createServerSocket(int port, int backlog, InetAddress add)
     */
    protected ServerSocket createServerSocket(int port, int backlog, InetAddress add)
        throws Exception
    {
        SecurityTool.setup();
    
        SSLContext context = SSLContext.getInstance(SecurityTool.getSecurityProtocol());
          
        KeyManagerFactory keyManagerFactory = 
            KeyManagerFactory.getInstance(SecurityTool.getKeyManagerType());
            
        KeyStore keyStore = KeyStore.getInstance(SecurityTool.getKeyStoreType());
            
        keyStore.load(new FileInputStream(SecurityTool.getKeyStore()), 
            SecurityTool.getKeyStorePassword().toCharArray());
            
        keyManagerFactory.init(keyStore, SecurityTool.getKeyStorePassword().toCharArray());
            
        context.init(keyManagerFactory.getKeyManagers(), null, null);
        SSLServerSocketFactory sslSrvFact = context.getServerSocketFactory();
        return (SSLServerSocket) sslSrvFact.createServerSocket(port);
    }

    /**
     * This <em>can</em> be called from command line, but you'll have to 
     * edit and recompile to change the server port or handler objects. 
     *
     * @see org.apache.xmlrpc.WebServer#addDefaultHandlers()
     */
    public static void main(String[] argv)
    {
        int p = determinePort(argv, 10000);
        XmlRpc.setKeepAlive (true);
        SecureWebServer webserver = new SecureWebServer (p);

        try
        {
            webserver.addDefaultHandlers();
            webserver.start();
        }
        catch (Exception e)
        {
            System.err.println("Error running secure web server");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
