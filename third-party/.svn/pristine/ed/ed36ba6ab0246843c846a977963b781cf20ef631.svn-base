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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;

/**
 * A multithreaded, reusable XML-RPC client object. Use this if you need a full-grown
 * HTTP client (e.g. for Proxy and Cookies support). If you don't need that, <code>XmlRpcClientLite</code>
 * may work better for you.
 */
public class SecureXmlRpcClient 
    extends XmlRpcClient
{
    /** 
     * Construct a XML-RPC client with this URL.
     */
    public SecureXmlRpcClient (URL url) {
        super(url);
    }

    /** 
     * Construct a XML-RPC client for the URL represented by this String.
     */
    public SecureXmlRpcClient (String url) throws MalformedURLException {
        super(url);
    }
   
    /** 
     * Construct a XML-RPC client for the specified hostname and port.
     */
    public SecureXmlRpcClient (String hostname, int port) throws MalformedURLException 
    {
        super("https://" + hostname + ':' + port + "/RPC2");
    }
    
    /**
     * This allows us to setup
     */
     public void setup() throws Exception
     {
         SecurityTool.setup();    
     }

    /** 
     * Just for testing.
     */
    public static void main (String args[]) throws Exception
    {
        // XmlRpc.setDebug (true);
        try {
            String url = args[0];
            String method = args[1];
            Vector v = new Vector ();
            for (int i=2; i<args.length; i++) try {
                v.addElement (new Integer (Integer.parseInt (args[i])));
            } catch (NumberFormatException nfx) {
                v.addElement (args[i]);
            }
            SecureXmlRpcClient client = new SecureXmlRpcClient (url);
            try {
                System.err.println (client.execute (method, v));
            } catch (Exception ex) {
                System.err.println ("Error: "+ex.getMessage());
            }
        } catch (Exception x) {
            System.err.println (x);
            System.err.println ("Usage: java " +
                                SecureXmlRpcClient.class.getName() +
                                " <url> <method> [args]");
            System.err.println ("Arguments are sent as integers or strings.");
        }
    }
}
