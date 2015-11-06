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
 * Default implementation of an XML-RPC request for both client and server.
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @version $Id: XmlRpcRequest.java,v 1.5 2005/04/22 10:25:57 hgomez Exp $
 * @since 1.2
 */
public class XmlRpcRequest
implements XmlRpcServerRequest, XmlRpcClientRequest
{
    protected final String methodName;
    protected final Vector parameters;

    public XmlRpcRequest(String methodName, Vector parameters)
    {
        this.parameters = parameters;
        this.methodName = methodName;
    }

    public int getParameterCount()
    {
        return parameters.size();
    }
    
    public Vector getParameters()
    {
        return parameters;
    }

    public Object getParameter(int index)
    {
        return parameters.elementAt(index);
    }

    public String getMethodName()
    {
        return methodName;
    }
}
