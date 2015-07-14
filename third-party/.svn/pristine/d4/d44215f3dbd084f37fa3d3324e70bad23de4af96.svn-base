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

import java.io.InputStream;
import java.util.Vector;

/**
 * Process an InputStream and produce an XmlRpcServerRequest.  This class
 * is NOT thread safe.
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author Daniel L. Rall
 * @since 1.2
 */
public class XmlRpcRequestProcessor extends XmlRpc
{
    private Vector requestParams;

    /**
     * Creates a new instance.
     */
    public XmlRpcRequestProcessor()
    {
        requestParams = new Vector();
    }

    /**
     * Decode a request from an InputStream to the internal XmlRpcRequest
     * implementation. This method must read data from the specified stream and
     * return an XmlRpcRequest object, or throw an exception.
     *
     * @param is the stream to read the request from.
     * @returns XMLRpcRequest the request.
     * @throws ParseFailed if unable to parse the request.
     */
    public XmlRpcServerRequest decodeRequest(InputStream is)
    {
        long now = 0;

        if (XmlRpc.debug)
        {
            now = System.currentTimeMillis();
        }
        try
        {
            try
            {
                parse(is);
            }
            catch (Exception e)
            {
                throw new ParseFailed(e);
            }
            if (XmlRpc.debug)
            {
                System.out.println("XML-RPC method name: " + methodName);
                System.out.println("Request parameters: " + requestParams);
            }
            // check for errors from the XML parser
            if (errorLevel > NONE)
            {
                throw new ParseFailed(errorMsg);
            }

            return new XmlRpcRequest(methodName, (Vector) requestParams.clone());
        }
        finally
        {
            requestParams.removeAllElements();
            if (XmlRpc.debug)
            {
                System.out.println("Spent " + (System.currentTimeMillis() - now)
                        + " millis decoding request");
            }
        }
    }

    /**
     * Called when an object to be added to the argument list has been
     * parsed.
     *
     * @param what The parameter parsed from the request.
     */
    protected void objectParsed(Object what)
    {
        requestParams.addElement(what);
    }
}
