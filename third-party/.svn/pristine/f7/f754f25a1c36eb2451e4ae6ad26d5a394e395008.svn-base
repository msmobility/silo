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
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * A multithreaded, reusable XML-RPC server object. The name may be misleading
 * because this does not open any server sockets. Instead it is fed by passing
 * an XML-RPC input stream to the execute method. If you want to open a
 * HTTP listener, use the WebServer class instead.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author Daniel L. Rall
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 */
public class XmlRpcServer
{
    private Stack pool;
    private int nbrWorkers;

    /**
     * The maximum number of threads which can be used concurrently, by defaut use the one defined
     * in XmlRpc
     */
    private int maxThreads = -1;

    /**
     * We want the <code>$default</code> handler to always be
     * available.
     */
    private DefaultHandlerMapping handlerMapping;

    /**
     * Construct a new XML-RPC server. You have to register handlers
     * to make it do something useful.
     */
    public XmlRpcServer()
    {
        pool = new Stack();
        nbrWorkers = 0;
        handlerMapping = new DefaultHandlerMapping();
    }

    /**
     * @see org.apache.xmlrpc.DefaultHandlerMapping#addHandler(String, Object)
     */
    public void addHandler(String handlerName, Object handler)
    {
        handlerMapping.addHandler(handlerName, handler);
    }

    /**
     * @see org.apache.xmlrpc.DefaultHandlerMapping#removeHandler(String)
     */
    public void removeHandler(String handlerName)
    {
        handlerMapping.removeHandler(handlerName);
    }

    /**
     * Return the current XmlRpcHandlerMapping.
     */
    public XmlRpcHandlerMapping getHandlerMapping()
    {
        return handlerMapping;
    }

    /**
     * Set the MaxThreads for this Client
     */
    public void setMaxThreads(int maxThreads) 
    {
    	this.maxThreads = maxThreads;
    }
    
    /**
     * Get the MaxThreads for this Server
     */
    public int getMaxThreads() 
    {
    	if (maxThreads == -1)
    		return (XmlRpc.getMaxThreads());
    	
    	return (maxThreads);
    }
    
    /**
     * Parse the request and execute the handler method, if one is
     * found. Returns the result as XML.  The calling Java code
     * doesn't need to know whether the call was successful or not
     * since this is all packed into the response. No context information
     * is passed.
     */
    public byte[] execute(InputStream is)
    {
        return execute(is, new DefaultXmlRpcContext(null, null, getHandlerMapping()));
    }

    /**
     * Parse the request and execute the handler method, if one is
     * found. If the invoked handler is AuthenticatedXmlRpcHandler,
     * use the credentials to authenticate the user. No context information
     * is passed.
     */
    public byte[] execute(InputStream is, String user, String password)
    {
        return execute(is, new DefaultXmlRpcContext(user, password, getHandlerMapping()));
    }
    
    /**
     * Parse the request and execute the handler method, if one is
     * found. If the invoked handler is AuthenticatedXmlRpcHandler,
     * use the credentials to authenticate the user. Context information
     * is passed to the worker, and may be passed to the request handler.
     */
    public byte[] execute(InputStream is, XmlRpcContext context)
    {
        XmlRpcWorker worker = getWorker();
        try
        {
            return worker.execute(is, context);
        }
        finally
        {
            pool.push(worker);
        }
    }

    /**
     * Hands out pooled workers.
     *
     * @return A worker (never <code>null</code>).
     * @throws RuntimeException If the server exceeds its maximum
     * number of allowed requests.
     */
    protected XmlRpcWorker getWorker()
    {
        try
        {
            return (XmlRpcWorker) pool.pop();
        }
        catch(EmptyStackException x)
        {
            int maxThreads = getMaxThreads();
            if (nbrWorkers < maxThreads)
            {
                nbrWorkers += 1;
                if (nbrWorkers >= maxThreads * .95)
                {
                    System.out.println("95% of XML-RPC server threads in use");
                }
                return createWorker();
            }
            throw new RuntimeException("System overload: Maximum number of " +
                                       "concurrent requests (" + maxThreads +
                                       ") exceeded");
        }
    }

    protected XmlRpcWorker createWorker()
    {
        return new XmlRpcWorker(handlerMapping);
    }
}
