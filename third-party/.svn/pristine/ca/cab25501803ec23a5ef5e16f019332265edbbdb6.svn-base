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

import java.util.Vector;

/**
 * Wraps calls to the XML-RPC standard system.* methods (such as
 * <code>system.multicall</code>).
 *
 * @author <a href="mailto:adam@megacz.com">Adam Megacz</a>
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @author Daniel L. Rall
 * @since 1.2
 */
public class SystemHandler
implements ContextXmlRpcHandler
{
    private DefaultHandlerMapping systemMapping = null;

    /**
     * Creates a new instance. This instance contains no system calls. Use the
     * addDefaultSystemHandlers() method to add the 'default' set of handlers,
     * or add handlers manually.
     */
    public SystemHandler()
    {
        this.systemMapping = new DefaultHandlerMapping();
    }

   /**
     * Creates a new instance that delegates calls via the
     * specified {@link org.apache.xmlrpc.XmlRpcHandlerMapping}. This
     * method will add the system.multicall handler when a non-null
     * handlerMapping is specified. The value itself is ignored.
     *
     * @deprecated use new SystemHandler() and addDefaultSystemHandlers() instead.
     */
    public SystemHandler(XmlRpcHandlerMapping handlerMapping)
    {
        this();
        if (handlerMapping != null)
        {
          addDefaultSystemHandlers();
        }
    }

    /**
     * Creates a new instance that delegates its multicalls via
     * the mapping used by the specified {@link org.apache.xmlrpc.XmlRpcServer}.
     * This method will add the default handlers when the specfied server's
     * getHandlerMapping() returns a non-null handler mapping.
     *
     * @param server The server to retrieve the XmlRpcHandlerMapping from.
     *
     * @deprecated use new SystemHandler() and addDefaultSystemHandlers() instead.
     */
    protected SystemHandler(XmlRpcServer server)
    {
        this(server.getHandlerMapping());
    }

    /**
     * Add the default system handlers. The default system handlers are:
     * <dl>
     *  <dt>system.multicall</dt>
     *  <dd>Make multiple XML-RPC calls in one request and receive multiple
     *  responses.</dd>
     * </dl>
     */
    public void addDefaultSystemHandlers()
    {
        addSystemHandler("multicall", new MultiCall());
    }

    /**
     * @see org.apache.xmlrpc.DefaultHandlerMapping#addHandler(String, Object)
     */
    public void addSystemHandler(String handlerName, ContextXmlRpcHandler handler)
    {
        systemMapping.addHandler(handlerName, handler);
    }

    /**
     * @see org.apache.xmlrpc.DefaultHandlerMapping#removeHandler(String)
     */
    public void removeSystemHandler(String handlerName)
    {
        systemMapping.removeHandler(handlerName);
    }

    /**
     * Execute a &lt;ignored&gt;.&lt;name&gt; call by calling the handler for
     * &lt;name&gt; in the the system handler mapping.
     */
    public Object execute(String method, Vector params, XmlRpcContext context)
            throws Exception
    {
        Object handler = null;
        String systemMethod = null;
        int dot = method.lastIndexOf('.');
        if (dot > -1)
        {
            // The last portion of the XML-RPC method name is the systen
	    // method name. 
	    systemMethod = method.substring(dot + 1);

            // Add the "." in at the end, the systemMapping will strip it off
            handler = systemMapping.getHandler(systemMethod + ".");
            if (handler != null)
            {
                return ((ContextXmlRpcHandler) handler).execute(systemMethod, params, context);
            }
        }

        throw new NoSuchMethodException("No method '" + method + "' registered.");
    }
}
