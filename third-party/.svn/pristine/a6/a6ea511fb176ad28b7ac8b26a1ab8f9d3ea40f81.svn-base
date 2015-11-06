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

/**
 * Tie together the XmlRequestProcessor and XmlResponseProcessor to handle
 * a request serially in a single thread.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author Daniel L. Rall
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @see org.apache.xmlrpc.XmlRpcServer
 * @since 1.2
 */
public class XmlRpcWorker
{
    protected XmlRpcRequestProcessor requestProcessor;
    protected XmlRpcResponseProcessor responseProcessor;
    protected XmlRpcHandlerMapping handlerMapping;

    /**
     * Create a new instance that will use the specified mapping.
     */
    public XmlRpcWorker(XmlRpcHandlerMapping handlerMapping)
    {
      requestProcessor = new XmlRpcRequestProcessor();
      responseProcessor = new XmlRpcResponseProcessor();
      this.handlerMapping = handlerMapping;
    }

    /**
     * Pass the specified request to the handler. The handler should be an
     * instance of {@link org.apache.xmlrpc.XmlRpcHandler} or
     * {@link org.apache.xmlrpc.AuthenticatedXmlRpcHandler}.
     *
     * @param handler the handler to call.
     * @param request the request information to use.
     * @param context the context information to use.
     * @return Object the result of calling the handler.
     * @throws ClassCastException if the handler is not of an appropriate type.
     * @throws NullPointerException if the handler is null.
     * @throws Exception if the handler throws an exception.
     */
    protected static Object invokeHandler(Object handler, XmlRpcServerRequest request, XmlRpcContext context)
        throws Exception
    {
        long now = 0;

        try
        {
            if (XmlRpc.debug)
            {
                now = System.currentTimeMillis();
            }
            if (handler == null)
            {
              throw new NullPointerException
                  ("Null handler passed to XmlRpcWorker.invokeHandler");
            }
            else if (handler instanceof ContextXmlRpcHandler)
            {
                return ((ContextXmlRpcHandler) handler).execute
                    (request.getMethodName(), request.getParameters(), context);
            }
            else if (handler instanceof XmlRpcHandler)
            {
                return ((XmlRpcHandler) handler).execute
                    (request.getMethodName(), request.getParameters());
            }
            else if (handler instanceof AuthenticatedXmlRpcHandler)
            {
                return ((AuthenticatedXmlRpcHandler) handler)
                    .execute(request.getMethodName(), request.getParameters(),
                             context.getUserName(), context.getPassword());
            }
            else
            {
               throw new ClassCastException("Handler class " +
                                            handler.getClass().getName() +
                                            " is not a valid XML-RPC handler");
            }
        }
        finally
        {
            if (XmlRpc.debug)
            {
                 System.out.println("Spent " + (System.currentTimeMillis() - now)
                         + " millis processing request");
            }
        }
    }

    /**
     * Decode, process and encode the response or exception for an XML-RPC
     * request. This method executes the handler method with the default context.
     */
    public byte[] execute(InputStream is, String user, String password)
    {
        return execute(is, defaultContext(user, password));
    }

    /**
     * Decode, process and encode the response or exception for an XML-RPC
     * request. This method executes will pass the specified context to the
     * handler if the handler supports context.
     *
     * @param is the InputStream to read the request from.
     * @param context the context for the request (may be null).
     * @return byte[] the response.
     * @throws org.apache.xmlrpc.ParseFailed if the request could not be parsed.
     * @throws org.apache.xmlrpc.AuthenticationFailed if the handler for the
     * specific method required authentication and insufficient credentials were
     * supplied.
     */
    public byte[] execute(InputStream is, XmlRpcContext context)
    {
        long now = 0;

        if (XmlRpc.debug)
        {
            now = System.currentTimeMillis();
        }

        try
        {
            XmlRpcServerRequest request = requestProcessor.decodeRequest(is);
            Object handler = handlerMapping.getHandler(request.
                                                       getMethodName());
            Object response = invokeHandler(handler, request, context);
            return responseProcessor.encodeResponse
                (response, requestProcessor.getEncoding());
        }
        catch (AuthenticationFailed alertCallerAuth)
        {
            throw alertCallerAuth;
        }
        catch (ParseFailed alertCallerParse)
        {
            throw alertCallerParse;
        }
        catch (Exception x)
        {
            if (XmlRpc.debug)
            {
                x.printStackTrace();
            }
            return responseProcessor.encodeException
                (x, requestProcessor.getEncoding());
        }
        finally
        {
            if (XmlRpc.debug)
            {
                System.out.println("Spent " + (System.currentTimeMillis() - now)
                                   + " millis in request/process/response");
            }
        }
    }

    /**
     * Factory method to return a default context object for the execute() method.
     * This method can be overridden to return a custom sub-class of XmlRpcContext.
     *
     * @param user the username of the user making the request.
     * @param password the password of the user making the request.
     * @return XmlRpcContext the context for the reqeust.
     */
    protected XmlRpcContext defaultContext(String user, String password)
    {
        return new DefaultXmlRpcContext(user, password, handlerMapping);
    }
}
