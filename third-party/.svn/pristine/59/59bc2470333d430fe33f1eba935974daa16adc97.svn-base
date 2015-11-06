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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import org.apache.xmlrpc.util.HttpUtil;

/**
 * Default XML-RPC transport factory, produces HTTP, HTTPS with SSL or TLS based on URI protocol.
 *
 * @author <a href="mailto:lmeader@ghsinc.com">Larry Meader</a>
 * @author <a href="mailto:cjackson@ghsinc.com">Chris Jackson</a>
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @version $Id: DefaultXmlRpcTransportFactory.java,v 1.6 2005/04/22 10:25:57 hgomez Exp $
 * @since 1.2
 */
public class DefaultXmlRpcTransportFactory implements XmlRpcTransportFactory 
{
    // Default properties for new http transports
    protected URL url;
    protected String auth;

    protected static XmlRpcTransportFactory httpsTransportFactory;

    public static final String DEFAULT_HTTPS_PROVIDER = "comnetsun";

    private static Hashtable transports = new Hashtable (1);

    static
    {
        // A mapping of short identifiers to the fully qualified class names of
        // common transport factories. If more mappings are added here,
        // increase the size of the transports Hashtable used to store them.
        transports.put("comnetsun", "org.apache.xmlrpc.secure.sunssl.SunSSLTransportFactory");
    }

    public static void setHTTPSTransport(String transport, Properties properties)
        throws XmlRpcClientException
    {
        httpsTransportFactory = createTransportFactory(transport, properties);    
    }

    public static XmlRpcTransportFactory createTransportFactory(String transport, Properties properties)
        throws XmlRpcClientException
    {
        String transportFactoryClassName = null;
        Class transportFactoryClass;
        Constructor transportFactoryConstructor;
        Object transportFactoryInstance;

        try
        {
            transportFactoryClassName = (String) transports.get(transport);
            if (transportFactoryClassName == null)
            {
                // Identifier lookup failed, assuming we were provided
                // with the fully qualified class name.
                transportFactoryClassName = transport;
            }
            transportFactoryClass = Class.forName(transportFactoryClassName);

            transportFactoryConstructor = transportFactoryClass.getConstructor(
                XmlRpcTransportFactory.CONSTRUCTOR_SIGNATURE);
            transportFactoryInstance = transportFactoryConstructor.newInstance(
                new Object [] { properties });
            if (transportFactoryInstance instanceof XmlRpcTransportFactory)
            {
                return (XmlRpcTransportFactory) transportFactoryInstance;
            }
            else
            {
                throw new XmlRpcClientException("Class '" + 
                    transportFactoryClass.getName() + "' does not implement '" +
                    XmlRpcTransportFactory.class.getName() + "'", null);
            }
        }
        catch (ClassNotFoundException cnfe)
        {
            throw new XmlRpcClientException("Transport Factory not found: " +
                transportFactoryClassName, cnfe);
        }
        catch (NoSuchMethodException nsme)
        {
            throw new XmlRpcClientException("Transport Factory constructor not found: " +
                transportFactoryClassName + 
                XmlRpcTransportFactory.CONSTRUCTOR_SIGNATURE_STRING, nsme);
        }
        catch (IllegalAccessException iae)
        {
            throw new XmlRpcClientException("Unable to access Transport Factory constructor: " +
                transportFactoryClassName, iae);
        }
        catch (InstantiationException ie)
        {
            throw new XmlRpcClientException("Unable to instantiate Transport Factory: " +
                transportFactoryClassName, ie);
        }
        catch (InvocationTargetException ite)
        {
            throw new XmlRpcClientException("Error calling Transport Factory constructor: ",
                ite.getTargetException());
        }
    }
  
    public DefaultXmlRpcTransportFactory(URL url)
    {
        this.url = url;
    }
    
    /**
     * Contructor taking a Base64 encoded Basic Authentication string.
     *
     * @deprecated use setBasicAuthentication method instead
     */
    public DefaultXmlRpcTransportFactory(URL url, String auth)
    {
        this(url);
        this.auth = auth;
    }
    
    public XmlRpcTransport createTransport() 
    throws XmlRpcClientException
    {
        if ("https".equals(url.getProtocol()))
        {
            if (httpsTransportFactory == null)
            {
                Properties properties = new Properties();
 
                properties.put(XmlRpcTransportFactory.TRANSPORT_URL, url);
                properties.put(XmlRpcTransportFactory.TRANSPORT_AUTH, auth);
 
                setHTTPSTransport(DEFAULT_HTTPS_PROVIDER, properties);
            }
  
            return httpsTransportFactory.createTransport();
        }
         
        return new DefaultXmlRpcTransport(url);
    }
    
    /**
     * Sets Authentication for this client. This will be sent as Basic
     * Authentication header to the server as described in
     * <a href="http://www.ietf.org/rfc/rfc2617.txt">
     * http://www.ietf.org/rfc/rfc2617.txt</a>.
     */
    public void setBasicAuthentication(String user, String password)
    {
        setProperty(TRANSPORT_AUTH, HttpUtil.encodeBasicAuthentication(user, password));
    }

    public void setProperty(String propertyName, Object value)
    {
        if (httpsTransportFactory != null)
        {
            httpsTransportFactory.setProperty(propertyName, value);
        }
        if (TRANSPORT_AUTH.equals(propertyName))
        {
            auth = (String) value;
        }
        else if (TRANSPORT_URL.equals(propertyName))
        {
            url = (URL) value;
        }
    }
}
