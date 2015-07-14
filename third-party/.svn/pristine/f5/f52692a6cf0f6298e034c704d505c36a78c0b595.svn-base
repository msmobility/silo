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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A <code>HttpServlet</code> that acts as a XML-RPC proxy.
 *
 * The URL of the server to connect to is taken from the servlet
 * initialization parameter <code>url</code>.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @version $Id: XmlRpcProxyServlet.java,v 1.5 2005/04/22 10:25:57 hgomez Exp $
 */
public class XmlRpcProxyServlet extends HttpServlet
{
    private XmlRpcServer xmlrpc;

    /**
     *
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException
    {
        if ("true".equalsIgnoreCase(config.getInitParameter("debug")))
        {
            XmlRpc.setDebug(true);
        }
        String url = config.getInitParameter("url");
        xmlrpc = new XmlRpcServer();
        try
        {
            xmlrpc.addHandler("$default", new XmlRpcClientLite(url));
        }
        catch (Exception x)
        {
            throw new ServletException("Invalid URL: " + url + " ("
                    + x.toString () + ")");
        }
    }

    /**
     *
     * @param req
     * @param res
     * @throws ServletException
     * @throws IOException
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException
    {
        byte[] result = xmlrpc.execute(req.getInputStream ());
        res.setContentType("text/xml");
        res.setContentLength(result.length);
        OutputStream output = res.getOutputStream();
        output.write(result);
        output.flush();
    }
}
