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
 * The minimal context that an XML-RPC request will occur in.
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @version $Id: XmlRpcContext.java,v 1.2 2005/04/22 10:25:57 hgomez Exp $
 * @since 1.2
 */
public interface XmlRpcContext
{
    /**
     * Get the username specified in the outer request.
     *
     * @returns the username (may be null).
     */
    public String getUserName();

    /**
     * Get the password specified in the outer request.
     *
     * @returns the password (may be null).
     */
    public String getPassword();

    /**
     * Get the XML-RPC handler mapping for the server handling the request.
     *
     * @returns the handler mapping (may be null).
     */
    public XmlRpcHandlerMapping getHandlerMapping();
}
