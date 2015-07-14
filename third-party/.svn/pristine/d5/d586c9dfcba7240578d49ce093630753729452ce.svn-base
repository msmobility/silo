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

/**
 * A basic context object that stores the userName, password and
 * handler mapping.
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @version $Id: DefaultXmlRpcContext.java,v 1.2 2005/04/22 10:25:57 hgomez Exp $
 * @since 1.2
 */
public class DefaultXmlRpcContext
implements XmlRpcContext
{
    protected String userName, password;
    protected XmlRpcHandlerMapping handlerMapping;

    public DefaultXmlRpcContext(String userName, String password, XmlRpcHandlerMapping handlerMapping)
    {
        this.userName = userName;
        this.password = password;
        this.handlerMapping = handlerMapping;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getPassword()
    {
        return password;
    }

    public XmlRpcHandlerMapping getHandlerMapping()
    {
        return handlerMapping;
    }
}
