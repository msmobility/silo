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
 * Maps from a handler name to a handler object.
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @since 1.2
 */
public interface XmlRpcHandlerMapping
{
  /**
   * Return the handler for the specified handler name.
   *
   * @param handlerName The name of the handler to retrieve.
   * @return Object The desired handler.
   * @throws Exception If a handler can not be found.
   */
  public Object getHandler(String handlerName)
      throws Exception;
}
