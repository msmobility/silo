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
import java.io.IOException;

/**
 * Tie together the XmlRequestProcessor and XmlResponseProcessor to handle
 * a request serially in a single thread.
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @since 2.0
 */
public class XmlRpcClientWorker
{
    protected XmlRpcClientRequestProcessor requestProcessor;
    protected XmlRpcClientResponseProcessor responseProcessor;

    /**
     * Used as an internal marker value in {@link
     * #execute(XmlRpcClientRequest, XmlRpcTransport)}.
     */
    private static final Object PROCESSING_ERROR_FLAG = new Object();

    public XmlRpcClientWorker()
    {
        this(new XmlRpcClientRequestProcessor(), 
             new XmlRpcClientResponseProcessor()
        );
    }

    public XmlRpcClientWorker(XmlRpcClientRequestProcessor requestProcessor,
                              XmlRpcClientResponseProcessor responseProcessor)
    {
        this.requestProcessor = requestProcessor;
        this.responseProcessor = responseProcessor;
    }

    public Object execute(XmlRpcClientRequest xmlRpcRequest,
                          XmlRpcTransport transport)
        throws XmlRpcException, XmlRpcClientException, IOException
    {
        long now = 0;
        Object response = PROCESSING_ERROR_FLAG;

        if (XmlRpc.debug)
        {
            now = System.currentTimeMillis();
        }

        try
        {
            byte[] request = requestProcessor.encodeRequestBytes
                (xmlRpcRequest, responseProcessor.getEncoding());
            InputStream is  = transport.sendXmlRpc(request);
            response = responseProcessor.decodeResponse(is);
            return response;
        }
        catch (IOException ioe)
        {
            throw ioe;
        }
        catch (XmlRpcClientException xrce)
        {
            throw xrce;
        }
        catch (RuntimeException x)
        {
            if (XmlRpc.debug)
            {
                x.printStackTrace();
            }
            throw new XmlRpcClientException
                ("Unexpected exception in client processing", x);
        }
        finally
        {
            if (XmlRpc.debug)
            {
                System.out.println("Spent " + (System.currentTimeMillis() - now)
                                   + " millis in request/process/response");
            }

            // End the transport's session, handling any problem while
            // avoiding hiding of any earlier exception.
            try
            {
                transport.endClientRequest();
            }
            catch (Throwable t)
            {
                // Don't clobber an earlier exception.
                boolean haveFault = response instanceof XmlRpcException;
                if (haveFault || response == PROCESSING_ERROR_FLAG)
                {
                    System.err.println("Avoiding obscuring previous error " +
                                       "by supressing error encountered " +
                                       "while ending request: " + t);
                    if (haveFault)
                    {
                        throw (XmlRpcException) response;
                    }
                    // else we've already thrown an exception
                }
                else
                {
                    if (t instanceof XmlRpcException)
                    {
                        throw (XmlRpcException) t;
                    }
                    else
                    {
                        throw new XmlRpcClientException
                            ("Unable to end request", t);
                    }
                }
            }
        }
    }

    /**
     * Called by the worker management framework to see if this worker can be
     * re-used. Must attempt to clean up any state, and return true if it can
     * be re-used.
     *
     * @return Whether this worker has been cleaned up and may be re-used.
     */
    // ### isReusable() would be a better name for this set of methods
    protected boolean canReUse()
    {
        return responseProcessor.canReUse() && requestProcessor.canReUse();
    }
}
