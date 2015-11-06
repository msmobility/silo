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
import java.util.Vector;

/**
 * The <code>system.multicall</code> handler performs several RPC
 * calls at a time.
 *
 * @author <a href="mailto:adam@megacz.com">Adam Megacz</a>
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @author Daniel L. Rall
 * @version $Id: MultiCall.java,v 1.5 2005/04/22 10:25:57 hgomez Exp $
 * @since 1.2
 */
public class MultiCall
implements ContextXmlRpcHandler
{
    public Object execute(String method, Vector params, XmlRpcContext context)
            throws Exception
    {
        if ("multicall".equals(method))
        {
            return multicall(params, context);
        }

        throw new NoSuchMethodException("No method '" + method + "' in " + this.getClass().getName());
    }

    public Vector multicall(Vector requests, XmlRpcContext context)
    {
        // The array of calls is passed as a single parameter of type array.
        requests=(Vector)requests.elementAt(0);
        Vector response = new Vector();
        XmlRpcServerRequest request;
        for (int i = 0; i < requests.size(); i++)
        {
            try
            {
                Hashtable call = (Hashtable) requests.elementAt(i);
                request = new XmlRpcRequest((String) call.get("methodName"),
                                            (Vector) call.get("params"));
                Object handler = context.getHandlerMapping().getHandler(request.getMethodName());
                Vector v = new Vector();
                v.addElement(XmlRpcWorker.invokeHandler(handler, request, context));
                response.addElement(v);
            }
            catch (Exception x)
            {
                String message = x.toString();
                int code = (x instanceof XmlRpcException ?
                            ((XmlRpcException) x).code : 0);
                Hashtable h = new Hashtable();
                h.put("faultString", message);
                h.put("faultCode", new Integer(code));
                response.addElement(h);
            }
        }
        return response;
    }
}
