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


package org.apache.xmlrpc.applet;

import java.applet.Applet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;


/**
 * An applet that provides basic XML-RPC client functionality.
 *
 * @version $Id: XmlRpcApplet.java,v 1.3 2005/04/22 10:25:58 hgomez Exp $
 */
public class XmlRpcApplet extends Applet {

    SimpleXmlRpcClient client;


    /**
     * Initialize the XML-RPC client, trying to get the port number from the
     * applet parameter tags. The default for port is 80. The client connects to
     * the server this applet came from.
     */
    public void initClient()
    {
        int port = 80;
        String p = getParameter("PORT");
        if (p != null)
        {
            try
            {
                port = Integer.parseInt(p);
            }
            catch (NumberFormatException nfx)
            {
                System.out.println("Error parsing port: " + nfx);
            }
        }
        initClient(port);
    }

    /**
     * Initialize the XML-RPC client with the specified port and the server this
     * applet came from.
     */
    public void initClient(int port)
    {
        String uri = getParameter("URI");
        if (uri == null)
        {
            uri = "/RPC2";
        }
        else if (!uri.startsWith("/"))
        {
            uri = "/" + uri;
        }
        initClient(port, uri);
    }

    /**
     * Initialize the XML-RPC client with the specified port and request path
     * and the server this applet came from.
     */
    public void initClient(int port, String uri)
    {
        String host = getCodeBase().getHost();
        try
        {
            URL url = new URL("http://" + host + ":" + port + uri);
            System.out.println("XML-RPC URL: " + url);
            client = new SimpleXmlRpcClient(url);
        }
        catch (MalformedURLException unlikely)
        {
            System.out.println("Error constructing XML-RPC client for "
                    + host + ":" + port + ": " + unlikely);
        }
    }

    /**
     * Calls the XML-RPC server with the specified methodname and argument list.
     */
    public Object execute(String methodName, Vector arguments)
            throws XmlRpcException, IOException
    {
        if (client == null)
        {
            initClient ();
        }
        Object returnValue = null;
        return returnValue = client.execute(methodName, arguments);
    }
}
