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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Abstract Test class to be extended by the tests for each Transport.
 * Guarantees that transports implement a base contract expected by the
 * XmlRpcClient classes
 *
 * @author <a href="mailto:rhoegg@isisnetworks.net">Ryan Hoegg</a>
 * @version $Id: XmlRpcTransportTest.java,v 1.4 2005/04/22 10:26:17 hgomez Exp $
 */
abstract public class XmlRpcTransportTest
    extends LocalServerRpcTest
{
    /**
     * Constructor
     */
    public XmlRpcTransportTest(String testName)
    {
        super(testName);
    }
    
    /**
     * This should return a new instance of the XmlRpcTransport being tested.
     */
    abstract protected XmlRpcTransport getTransport(URL url);
    
    /**
     * This test is to enforce that every alternate implementation of 
     * XmlRpcTransport provides a minimum of the same functionality as
     * @link DefaultXmlRpcTransport.  We trust DefaultXmlRpcTransport
     * because it is used by default in XmlRpcClient, which is tested
     * in @link ClientServerRpcTest.
     */
    public void testSendXmlRpc() {
        
        try {
            setUpWebServer();
            startWebServer();
            URL testUrl = buildURL("localhost", SERVER_PORT);
            XmlRpcTransport controlTransport = new DefaultXmlRpcTransport(testUrl);
            XmlRpcTransport testTransport = getTransport(testUrl);
            InputStream controlResponse = controlTransport.sendXmlRpc(RPC_REQUEST.getBytes());
            InputStream testResponse = testTransport.sendXmlRpc(RPC_REQUEST.getBytes());
            assertTrue(
                "Response from XmlRpcTransport does not match that of DefaultXmlRpcTransport.",
                equalsInputStream(controlResponse, testResponse));
            stopWebServer();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        catch (XmlRpcClientException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    private URL buildURL(String hostname, int port) throws MalformedURLException {
        return new URL("http://" + hostname + ':' + port + "/RPC2");
    }
    
    protected boolean equalsInputStream(InputStream is1, InputStream is2) throws IOException {
        BufferedInputStream stream1 = new BufferedInputStream(is1);
        BufferedInputStream stream2 = new BufferedInputStream(is2);
        int char1 = is1.read();
        int char2 = is2.read();
        while ((char1 != -1) && (char2 != -1) && (char1 == char2)) {
            char1 = is1.read();
            char2 = is2.read();
        }
        return char1 == char2;
    }
}
