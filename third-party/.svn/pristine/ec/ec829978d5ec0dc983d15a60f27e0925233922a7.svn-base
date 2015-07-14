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
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Process an Object and produce byte array that represents the specified
 * encoding of the output as an XML-RPC response. This is NOT thread safe.
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author Daniel L. Rall
 * @since 1.2
 */
public class XmlRpcResponseProcessor
{
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /**
     * Creates a new instance.
     */
    public XmlRpcResponseProcessor()
    {
    }

    /**
     * Process a successful response, and return output in the
     * specified encoding.
     *
     * @param responseParam The response to process.
     * @param encoding The output encoding.
     * @return byte[] The XML-RPC response.
     */
    public byte[] encodeResponse(Object responseParam, String encoding)
        throws IOException, UnsupportedEncodingException, XmlRpcException
    {
        long now = 0;
        if (XmlRpc.debug)
        {
            now = System.currentTimeMillis();
        }

        try
        {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            XmlWriter writer = new XmlWriter(buffer, encoding);
            writeResponse(responseParam, writer);
            writer.flush();
            return buffer.toByteArray();
        }
        finally
        {
            if (XmlRpc.debug)
            {
                System.out.println("Spent " + (System.currentTimeMillis() - now)
                        + " millis encoding response");
            }
        }
    }

    /**
     * Process an exception, and return output in the specified
     * encoding.
     *
     * @param e The exception to process;
     * @param encoding The output encoding.
     * @param code The XML-RPC faultCode.
     * @return byte[] The XML-RPC response.
     */
    public byte[] encodeException(Exception x, String encoding, int code)
    {
        if (XmlRpc.debug)
        {
            x.printStackTrace();
        }
        // Ensure that if there is anything in the buffer, it
        // is cleared before continuing with the writing of exceptions.
        // It is possible that something is in the buffer
        // if there were an exception during the writeResponse()
        // call above.
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        XmlWriter writer = null;
        try
        {
            writer = new XmlWriter(buffer, encoding);
        }
        catch (UnsupportedEncodingException encx)
        {
            System.err.println("XmlRpcServer attempted to use "
                    + "unsupported encoding: " + encx);
            // NOTE: If we weren't already using the default
            // encoding, we could try it here.
        }
        catch (IOException iox)
        {
            System.err.println("XmlRpcServer experienced I/O error "
                    + "writing error response: " + iox);
        }

        String message = x.toString();
        // Retrieve XmlRpcException error code(if possible).
        try
        {
            writeError(code, message, writer);
            writer.flush();
        }
        catch (Exception e)
        {
            // Unlikely to occur, as we just sent a struct
            // with an int and a string.
            System.err.println("Unable to send error response to "
                    + "client: " + e);
        }

        return (writer != null ? buffer.toByteArray() : EMPTY_BYTE_ARRAY);
    }

     /**
     * Process an exception, and return output in the specified
     * encoding.
     *
     * @param e The exception to process;
     * @param encoding The output encoding.
     * @return byte[] The XML-RPC response.
     */
    public byte[] encodeException(Exception x, String encoding)
    {
        return encodeException(x, encoding, (x instanceof XmlRpcException) ? ((XmlRpcException) x).code : 0);
    }
     /**
      * Writes an XML-RPC response to the XML writer.
      */
    void writeResponse(Object param, XmlWriter writer)
        throws XmlRpcException, IOException
    {
        writer.startElement("methodResponse");
        // if (param == null) param = ""; // workaround for Frontier bug
        writer.startElement("params");
        writer.startElement("param");
        writer.writeObject(param);
        writer.endElement("param");
        writer.endElement("params");
        writer.endElement("methodResponse");
    }

    /**
     * Writes an XML-RPC error response to the XML writer.
     */
    void writeError(int code, String message, XmlWriter writer)
        throws XmlRpcException, IOException
    {
        // System.err.println("error: "+message);
        Hashtable h = new Hashtable();
        h.put("faultCode", new Integer(code));
        h.put("faultString", message);
        writer.startElement("methodResponse");
        writer.startElement("fault");
        writer.writeObject(h);
        writer.endElement("fault");
        writer.endElement("methodResponse");
    }
}
