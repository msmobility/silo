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


package org.apache.xmlrpc.secure.sunssl;

import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Properties;

import org.apache.xmlrpc.DefaultXmlRpcTransport;
import org.apache.xmlrpc.XmlRpcTransport;
import org.apache.xmlrpc.XmlRpcTransportFactory;
import org.apache.xmlrpc.secure.SecurityTool;

import com.sun.net.ssl.HostnameVerifier;
import com.sun.net.ssl.HttpsURLConnection;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.X509TrustManager;

/**
 * Interface from XML-RPC to the HTTPS transport based on the
 * @see javax.net.ssl.httpsURLConnection class.
 *
 * @author <a href="mailto:lmeader@ghsinc.com">Larry Meader</a>
 * @author <a href="mailto:cjackson@ghsinc.com">Chris Jackson</a> 
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @version $Id: SunSSLTransportFactory.java,v 1.4 2005/04/22 10:25:58 hgomez Exp $
 * @since 1.2
 */
public class SunSSLTransportFactory implements XmlRpcTransportFactory
{
    protected URL url;
    protected String auth;

    public static final String TRANSPORT_TRUSTMANAGER = "hostnameverifier";
    public static final String TRANSPORT_HOSTNAMEVERIFIER = "trustmanager";

    // The openTrustManager trusts all certificates
    private static X509TrustManager openTrustManager = new X509TrustManager()
    {
        public boolean isClientTrusted(X509Certificate[] chain)
        {
            return true;
        }
 
        public boolean isServerTrusted(X509Certificate[] chain)
        {
            return true;
        }
 
        public X509Certificate[] getAcceptedIssuers() 
        {
            return null;
        }
    };

    // The openHostnameVerifier trusts all hostnames
    private static HostnameVerifier openHostnameVerifier = new HostnameVerifier() 
    {
        public boolean verify(String hostname, String session) 
        {
            return true;
        }
    };

    public static Properties getProperties()
    {
        Properties properties = new Properties();

        properties.setProperty(XmlRpcTransportFactory.TRANSPORT_URL, "(java.net.URL) - URL to connect to");
        properties.setProperty(XmlRpcTransportFactory.TRANSPORT_AUTH, "(java.lang.String) - HTTP Basic Authentication string (encoded).");
        properties.setProperty(TRANSPORT_TRUSTMANAGER, "(com.sun.net.ssl.X509TrustManager) - X.509 Trust Manager to use");
        properties.setProperty(TRANSPORT_HOSTNAMEVERIFIER, "(com.sun.net.ssl.HostnameVerifier) - Hostname verifier to use");

        return properties;
    }

    public SunSSLTransportFactory(Properties properties)
    throws GeneralSecurityException
    {
        X509TrustManager trustManager;
        HostnameVerifier hostnameVerifier;
        SSLContext sslContext;

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        url = (URL) properties.get(XmlRpcTransportFactory.TRANSPORT_URL);
        auth = properties.getProperty(XmlRpcTransportFactory.TRANSPORT_AUTH);

        trustManager = (X509TrustManager) properties.get(TRANSPORT_TRUSTMANAGER);
        if (trustManager == null)
        {
            trustManager = openTrustManager;
        }

        hostnameVerifier = (HostnameVerifier) properties.get(TRANSPORT_HOSTNAMEVERIFIER);
        if (hostnameVerifier == null)
        {
            hostnameVerifier = openHostnameVerifier;
        }  

        sslContext = SSLContext.getInstance(SecurityTool.getSecurityProtocol());
        X509TrustManager[] tmArray = new X509TrustManager[] { trustManager };
        sslContext.init(null, tmArray, new SecureRandom());

        // Set the default SocketFactory and HostnameVerifier
        // for javax.net.ssl.HttpsURLConnection
        if (sslContext != null) 
        {
            HttpsURLConnection.setDefaultSSLSocketFactory(
                sslContext.getSocketFactory());
        }
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }

    public XmlRpcTransport createTransport()
    {
       return new DefaultXmlRpcTransport(url, auth);
    }

    public void setProperty(String propertyName, Object value)
    {
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
