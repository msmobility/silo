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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests XmlRpc run-time.
 *
 * @author Daniel L. Rall
 * @version $Id: ClientServerRpcTest.java,v 1.19 2005/04/22 10:26:17 hgomez Exp $
 */
public class ClientServerRpcTest
    extends LocalServerRpcTest
{
    /**
     * The identifier or fully qualified class name of the SAX driver
     * to use.  This is generally <code>uk.co.wilson.xml.MinML</code>,
     * but could be changed to
     * <code>org.apache.xerces.parsers.SAXParser</code> for timing
     * comparisons.
     */
    private static final String SAX_DRIVER = "uk.co.wilson.xml.MinML";

    /**
     * The number of RPCs to make for each test.
     */
    private static final int NBR_REQUESTS = 1000;

    /**
     * The number of calls to batch in the multicall.
     */
    private static final int NUM_MULTICALLS = 10;

    private XmlRpcServer server;

    private XmlRpcClient client;

    private XmlRpcClientLite liteClient;

    /**
     * Constructor
     */
    public ClientServerRpcTest(String testName) 
    {
        super(testName);

        XmlRpc.setDebug(true);
        try
        {
            XmlRpc.setDriver(SAX_DRIVER);
        }
        catch (ClassNotFoundException e)
        {
            fail(e.toString());
        }

        // Server (only)
        server = new XmlRpcServer();
        server.addHandler(HANDLER_NAME, new TestHandler());

        // Setup system handler
        SystemHandler webServerSysHandler = new SystemHandler();
        webServerSysHandler.addSystemHandler("multicall", new MultiCall());

        // WebServer (contains its own XmlRpcServer instance)
        setUpWebServer();
        webServer.addHandler("system", webServerSysHandler);
    }

    /**
     * Return the Test
     */
    public static Test suite() 
    {
        return new TestSuite(ClientServerRpcTest.class);
    }

    /**
     * Setup the server and clients.
     */
    public void setUp() 
    {
        try
        {
            startWebServer();
        }
        catch (RuntimeException e)
        {
            e.printStackTrace();
            fail(e.toString());
        }

        InetAddress localhost = null;
        try
        {
            // localhost will be a random network interface on a
            // multi-homed host.
            localhost = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e)
        {
            fail(e.toString());
        }

		// XML-RPC client(s)
        try
        {
            String hostName = localhost.getHostName();
            client = new XmlRpcClient(hostName, SERVER_PORT);
            //liteClient = new XmlRpcClientLite(hostName, SERVER_PORT);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
    }
   
    /**
     * Tear down the test.
     */
    public void tearDown() 
    {
        try
        {
            stopWebServer();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.toString());
        }
    }

    /**
     * Tests server's RPC capabilities directly.
     */
    public void testServer()
    {
        try
        {
            long time = System.currentTimeMillis();
            for (int i = 0; i < NBR_REQUESTS; i++)
            {
                InputStream in =
                    new ByteArrayInputStream(RPC_REQUEST.getBytes());
                String response = new String(server.execute(in));
                assertTrue("Response did not contain " + REQUEST_PARAM_XML,
                           response.indexOf(REQUEST_PARAM_XML) != -1);
            }
            time = System.currentTimeMillis() - time;
            System.out.println("Total time elapsed for " + NBR_REQUESTS +
                               " iterations was " + time + " milliseconds, " +
                               "averaging " + (time / NBR_REQUESTS) +
                               " milliseconds per request");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    /**
     * Tests client/server RPC (via {@link WebServer}).
     */
    public void testRpc()
    {
        try
        {
            // Test the web server (which also tests the rpc server)
            // by connecting via the clients
            Vector params = new Vector();
            params.add(REQUEST_PARAM_VALUE);
            Object response = client.execute(HANDLER_NAME + ".echo", params);
            assertEquals(REQUEST_PARAM_VALUE, response);
            //params.removeAllElements();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testSystemMultiCall()
    {
        try
        {
            Vector calls = new Vector();

            for (int i = 0; i < NUM_MULTICALLS; i++)
            {
                Hashtable call = new Hashtable();
                Vector params = new Vector();

                params.add(REQUEST_PARAM_VALUE + i);
                call.put("methodName", HANDLER_NAME + ".echo");
                call.put("params", params);
 
                calls.addElement(call);
            }
 
            Vector paramWrapper = new Vector();
            paramWrapper.add(calls);
            
            Object response = client.execute("system.multicall", paramWrapper);

            for (int i = 0; i < NUM_MULTICALLS; i++)
            {
               Vector result = new Vector();
               result.add(REQUEST_PARAM_VALUE + i);

               assertEquals(result, ((Vector)response).elementAt(i));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }   
}
