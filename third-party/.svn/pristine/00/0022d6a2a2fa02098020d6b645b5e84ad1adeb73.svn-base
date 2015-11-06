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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * A multithreaded, reusable XML-RPC client object. This version uses a homegrown
 * HTTP client which can be quite a bit faster than java.net.URLConnection, especially
 * when used with XmlRpc.setKeepAlive(true).
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @version $Id: XmlRpcClientLite.java,v 1.13 2005/04/22 10:25:57 hgomez Exp $
 */
public class XmlRpcClientLite extends XmlRpcClient
{
    /**
     * Construct a XML-RPC client with this URL.
     */
    public XmlRpcClientLite (URL url)
    {
        super (url);
    }

    /**
     * Construct a XML-RPC client for the URL represented by this String.
     */
    public XmlRpcClientLite (String url) throws MalformedURLException
    {
        super (url);
    }

    /**
     * Construct a XML-RPC client for the specified hostname and port.
     */
    public XmlRpcClientLite (String hostname, int port)
            throws MalformedURLException
    {
        super (hostname, port);
    }

    protected XmlRpcTransport createTransport()
    {
        return new LiteXmlRpcTransport(url);
    }

    /**
     * Just for testing.
     */
    public static void main(String args[]) throws Exception
    {
        // XmlRpc.setDebug (true);
        try
        {
            String url = args[0];
            String method = args[1];
            XmlRpcClientLite client = new XmlRpcClientLite (url);
            Vector v = new Vector ();
            for (int i = 2; i < args.length; i++)
            {
                try
                {
                    v.addElement(new Integer(Integer.parseInt(args[i])));
                }
                catch (NumberFormatException nfx)
                {
                    v.addElement(args[i]);
                }
            }
            // XmlRpc.setEncoding ("UTF-8");
            try
            {
                System.out.println(client.execute(method, v));
            }
            catch (Exception ex)
            {
                System.err.println("Error: " + ex.getMessage());
            }
        }
        catch (Exception x)
        {
            System.err.println(x);
            System.err.println("Usage: java org.apache.xmlrpc.XmlRpcClient "
                    + "<url> <method> <arg> ....");
            System.err.println("Arguments are sent as integers or strings.");
        }
    }
}
