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

import java.util.Properties;

/**
 * Interface from XML-RPC to an underlying transport, most likely base on HTTP.
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @version $Id: XmlRpcTransportFactory.java,v 1.5 2005/04/22 10:25:57 hgomez Exp $
 * @since 1.2
 *
 * Constructors for SSL implementations of XmlRpcTransportFactory should have a constructor
 * with a signature defined by CONSTRUCTOR_SIGNATURE:
 * <code>
 * ClassName(Properties properties)
 * </code>
 *
 * and use the default properties defined in this interface.
 */
public interface XmlRpcTransportFactory
{
    public static final String TRANSPORT_URL  = "url"; // Name of property containing URL
    public static final String TRANSPORT_AUTH = "auth"; // Name of property containing Basic Authentication information

    public static final Class [] CONSTRUCTOR_SIGNATURE = new Class [] { Properties.class };
    public static final String CONSTRUCTOR_SIGNATURE_STRING = "(java.util.Properties properties)";

    /**
     * Create a new XML-RPC transport.
     *
     * @return XmlRpcTransport an instance created according to the rules
     * specified to the constructor.
     */
    public XmlRpcTransport createTransport()
    throws XmlRpcClientException;

    /**
     * Set a property for all newly created transports.
     *
     * @param propertyName the property to set.
     * @param value the value to set it to.
     */
    public void setProperty(String propertyName, Object value);
}
