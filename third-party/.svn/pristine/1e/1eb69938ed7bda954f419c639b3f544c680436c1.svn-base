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


import java.util.Hashtable;

/**
 * Provide a default handler mapping, used by the XmlRpcServer. This
 * mapping supports the special handler name "$default" that will
 * handle otherwise unhandled requests.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author Daniel L. Rall
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @see org.apache.xmlrpc.XmlRpcServer
 * @since 1.2
 */
public class DefaultHandlerMapping
    implements XmlRpcHandlerMapping
{
    private Hashtable handlers;

    /**
     * Create a new mapping.
     */
    public DefaultHandlerMapping()
    {
        handlers = new Hashtable();
    }

    /**
     * Register a handler object with this name. Methods of this
     * objects will be callable over XML-RPC as
     * "handlername.methodname". For more information about XML-RPC
     * handlers see the <a href="../index.html#1a">main documentation
     * page</a>.
     *
     * @param handlername The name to identify the handler by.
     * @param handler The handler itself.
     */
    public void addHandler(String handlerName, Object handler)
    {
        if (handler instanceof XmlRpcHandler ||
                handler instanceof AuthenticatedXmlRpcHandler ||
                handler instanceof ContextXmlRpcHandler)
        {
            handlers.put(handlerName, handler);
        }
        else if (handler != null)
        {
            handlers.put(handlerName, new Invoker(handler));
        }
    }

    /**
     * Remove a handler object that was previously registered with
     * this server.
     *
     * @param handlerName The name identifying the handler to remove.
     */
    public void removeHandler(String handlerName)
    {
        handlers.remove(handlerName);
    }

    /**
     * Find the handler and its method name for a given method.
     * Implements the <code>XmlRpcHandlerMapping</code> interface.
     *
     * @param methodName The name of the XML-RPC method to find a
     * handler for (this is <i>not</i> the Java method name).
     * @return A handler object and method name.
     * @see org.apache.xmlrpc.XmlRpcHandlerMapping#getHandler(String)
     */
    public Object getHandler(String methodName)
        throws Exception
    {
        Object handler = null;
        String handlerName = null;
        int dot = methodName.lastIndexOf('.');
        if (dot > -1)
        {
            // The last portion of the XML-RPC method name is the Java
            // method name.
            handlerName = methodName.substring(0, dot);
            handler = handlers.get(handlerName);
        }

        if (handler == null)
        {
            handler = handlers.get("$default");

            if (handler == null)
            {
                if (dot > -1)
                {
                    throw new Exception("RPC handler object \""
                                        + handlerName + "\" not found and no "
                                        + "default handler registered");
                }
                else
                {
                    throw new Exception("RPC handler object not found for \""
                                        + methodName
                                        + "\": No default handler registered");
                }
            }
        }

        return handler;
    }
}
