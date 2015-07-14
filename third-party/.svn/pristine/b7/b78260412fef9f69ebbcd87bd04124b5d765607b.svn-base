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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Process an XML-RPC client request into a byte array or directly onto
 * an OutputStream.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @version $Id: XmlRpcClientRequestProcessor.java,v 1.5 2005/05/14 21:31:48 jochen Exp $
 * @since 1.2
 */
public class XmlRpcClientRequestProcessor
{
    /**
     * Creates a new instance.
     */
    public XmlRpcClientRequestProcessor()
    {
    }
	
    /**
     * Encode a request from the XmlClientRpcRequest implementation to an
     * output stream in the specified character encoding.
     *
     * @param request the request to encode.
     * @param encoding the Java name for the encoding to use.
     * @return byte [] the encoded request.
     */
    public void encodeRequest(XmlRpcClientRequest request, String encoding,
                              OutputStream out)
        throws XmlRpcClientException, IOException
    {
        XmlWriter writer;

        writer = new XmlWriter(out, encoding);
       
        writer.startElement("methodCall");
        writer.startElement("methodName");
        writer.write(request.getMethodName());
        writer.endElement("methodName");
        writer.startElement("params");

        int l = request.getParameterCount();
        for (int i = 0; i < l; i++)
        {
            writer.startElement("param");
            try
            {
                writer.writeObject(request.getParameter(i));
            }
            catch (XmlRpcException e)
            {
                throw new XmlRpcClientException("Failure writing request", e);
            }
            writer.endElement("param");
        }
        writer.endElement("params");
        writer.endElement("methodCall");
        writer.flush();
    }

    /**
     * Encode a request from the XmlRpcClientRequest implementation to a
     * byte array representing the XML-RPC call, in the specified character
     * encoding.
     *
     * @param request the request to encode.
     * @param encoding the Java name for the encoding to use.
     * @return byte [] the encoded request.
     */
    public byte [] encodeRequestBytes(XmlRpcClientRequest request, String encoding)
    throws XmlRpcClientException
    {
        ByteArrayOutputStream buffer;

        try
        {
            buffer = new ByteArrayOutputStream();
            encodeRequest(request, encoding, buffer);
            return buffer.toByteArray();
        }
        catch (IOException ioe)
        {
            throw new XmlRpcClientException("Error occured encoding XML-RPC request", ioe);
        }
    }

    /**
     * Called by the worker management framework to see if this object can be
     * re-used. Must attempt to clean up any state, and return true if it can
     * be re-used.
     *
     * @return boolean true if this objcet has been cleaned up and may be re-used.
     */
    protected boolean canReUse()
    {
        return true;
    }
}
