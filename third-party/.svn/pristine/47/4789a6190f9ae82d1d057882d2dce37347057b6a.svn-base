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
import java.io.OutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.xmlrpc.util.HttpUtil;

/**
 * Interface from XML-RPC to the default HTTP transport based on the
 * @see java.net.URLConnection class.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @author <a href="mailto:rhoegg@isisnetworks.net">Ryan Hoegg</a>
 * @version $Id: DefaultXmlRpcTransport.java,v 1.4 2005/04/22 10:25:57 hgomez Exp $
 * @since 1.2
 */
public class DefaultXmlRpcTransport implements XmlRpcTransport
{
    protected URL url;
    protected String auth;
    protected URLConnection con;

    /**
     * Create a new DefaultXmlRpcTransport with the specified URL and basic
     * authorization string.
     *
     * @deprecated Use setBasicAuthentication instead of passing an encoded authentication String.
     *
     * @param url the url to POST XML-RPC requests to.
     * @param auth the Base64 encoded HTTP Basic authentication value.
     */
    public DefaultXmlRpcTransport(URL url, String auth)
    {
        this.url = url;
        this.auth = auth;
    }

    /**
     * Create a new DefaultXmlRpcTransport with the specified URL.
     *
     * @param url the url to POST XML-RPC requests to.
     */
    public DefaultXmlRpcTransport(URL url)
    {
        this(url, null);
    }

    public InputStream sendXmlRpc(byte [] request)
    throws IOException
    {
        con = url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false);
        con.setAllowUserInteraction(false);
        con.setRequestProperty("Content-Length",
        Integer.toString(request.length));
        con.setRequestProperty("Content-Type", "text/xml");
        if (auth != null)
        {
            con.setRequestProperty("Authorization", "Basic " + auth);
        }
        OutputStream out = con.getOutputStream();
        out.write(request);
        out.flush();
        out.close();
        return con.getInputStream();
    }

    /**
     * Sets Authentication for this client. This will be sent as Basic
     * Authentication header to the server as described in
     * <a href="http://www.ietf.org/rfc/rfc2617.txt">
     * http://www.ietf.org/rfc/rfc2617.txt</a>.
     */
    public void setBasicAuthentication(String user, String password)
    {
        auth = HttpUtil.encodeBasicAuthentication(user, password);
    }

    public void endClientRequest()
    throws XmlRpcClientException
    {
        try
        {
            con.getInputStream().close();
        }
        catch (Exception e)
        {
            throw new XmlRpcClientException("Exception closing URLConnection", e);
        }
    }
}
