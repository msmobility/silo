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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Implementor of the XmlRpcTransport interface using the Apache
 * Commons HttpClient library v2.0 available at
 * http://jakarta.apache.org/commons/httpclient/
 *
 * Note: <b>Currently this transport is not thread safe</b>
 *
 * @author <a href="mailto:rhoegg@isisnetworks.net">Ryan Hoegg</a>
 * @version $Id: CommonsXmlRpcTransport.java,v 1.7 2005/04/28 09:05:06 dlr Exp $
 * @since 2.0
 */
public class CommonsXmlRpcTransport implements XmlRpcTransport 
{
    
    protected PostMethod method;

    /** Creates a new instance of CommonsXmlRpcTransport */
    public CommonsXmlRpcTransport(URL url, HttpClient client) 
    {
        this.url = url;
        if (client == null) 
        {
            HttpClient newClient = new HttpClient();
            this.client = newClient;
        } 
        else 
        {
            this.client = client;
        }
    }
    
    public CommonsXmlRpcTransport(URL url) 
    {
        this(url, null);
    }
    
    private URL url;
    private HttpClient client;
    private final Header userAgentHeader = new Header("User-Agent", XmlRpc.version);
    private boolean http11 = false; // defaults to HTTP 1.0
    private boolean gzip = false;
    private boolean rgzip = false;
    private Credentials creds;
    
    public InputStream sendXmlRpc(byte[] request) throws IOException, XmlRpcClientException 
    {
        method = new PostMethod(url.toString());
        method.setHttp11(http11);
        method.setRequestHeader(new Header("Content-Type", "text/xml"));
        
        if (rgzip)
        	method.setRequestHeader(new Header("Content-Encoding", "gzip"));
        
        if (gzip)
        	method.setRequestHeader(new Header("Accept-Encoding", "gzip"));
                
        method.setRequestHeader(userAgentHeader);

        if (rgzip)
        {
        	ByteArrayOutputStream lBo = new ByteArrayOutputStream();
        	GZIPOutputStream lGzo = new GZIPOutputStream(lBo);
        	lGzo.write(request);
        	lGzo.finish();        	
        	lGzo.close();        	
        	byte[] lArray = lBo.toByteArray();
        	method.setRequestBody(new ByteArrayInputStream(lArray));
        	method.setRequestContentLength(-1);
        }
        else
        	method.setRequestBody(new ByteArrayInputStream(request));
        
        URI hostURI = new URI(url.toString());
        HostConfiguration hostConfig = new HostConfiguration();
        hostConfig.setHost(hostURI);
        client.executeMethod(hostConfig, method);

        boolean lgzipo = false;
        
        Header lHeader = method.getResponseHeader( "Content-Encoding" );
        if ( lHeader != null ) {
            String lValue = lHeader.getValue();
            if ( lValue != null )
        		lgzipo = (lValue.indexOf( "gzip" ) >= 0);
        }

        if (lgzipo)
        	return( new GZIPInputStream( method.getResponseBodyAsStream() ) );
        else
        	return method.getResponseBodyAsStream();
    }
    
    /**
     * Make use of HTTP 1.1 
     * 
     * @param http11 HTTP 1.1 will be used if http11 is true
     */
    public void setHttp11(boolean http11) 
    {
        this.http11 = http11;
    }
    
    /**
     * Transport make use of the 'Accept-Encoding: gzip', so compliant HTTP servers
     * could return HTTP reply compressed with gzip 
     *   
     * @param gzip  Gzip compression will be used if gzip is true
     */
    public void setGzip(boolean gzip) {
        this.gzip = gzip;
    }
    
    /**
     * Transport make use of the 'Content-Encoding: gzip' and send HTTP request
     * compressed with gzip : works only with some compliant HTTP servers like Apache 2.x
     * using SetInputFilter DEFLATE.
     *   
     * @param gzip  Compress request with gzip if gzip is true
     */
    public void setRGzip(boolean gzip) {
        this.rgzip = gzip;
    }
    
    /**
     * Set the UserAgent for this client
     * 
     * @param userAgent
     */
    public void setUserAgent(String userAgent) 
    {
        userAgentHeader.setValue(userAgent);
    }

    /**
     * Sets Authentication for this client, very basic for now user/password
     * 
     * @param user
     * @param password
     */
    public void setBasicAuthentication(String user, String password)
    {
        creds = new UsernamePasswordCredentials(user, password);
        client.getState().setCredentials(null, null, creds);
    }

    /**
     * Releases connection resources.
     *
     * @exception XmlRpcClientException
     */
    public void endClientRequest()
        throws XmlRpcClientException
    {
        method.releaseConnection();
    }
}
