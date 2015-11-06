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
import java.io.IOException;

/**
 * Interface from XML-RPC to an underlying transport, most likely base on HTTP.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @version $Id: XmlRpcTransport.java,v 1.3 2005/04/22 10:25:57 hgomez Exp $
 * @since 1.2
 */
public interface XmlRpcTransport
{
  /**
   * Send an XML-RPC message. This method is called to send a message to the
   * other party.
   *
   * @param request the request in network encoding.
   *
   * @throws IOException if an IOException occurs in the IO level of the transport.
   * @throws XmlRpcClientException if an exception occurs in the transport.
   */
  public InputStream sendXmlRpc(byte [] request)
  throws IOException, XmlRpcClientException;

  /**
   * End an XML-RPC request. This method is called by the XmlRpcClient when then
   * request has been sent and the response (or an exception) recieved.
   *
   * @throws XmlRpcClientException if an exception occurs in the transport.
   */
  public void endClientRequest()
  throws XmlRpcClientException;
}
