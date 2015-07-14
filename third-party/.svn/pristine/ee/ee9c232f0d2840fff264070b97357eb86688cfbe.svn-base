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

import java.util.Vector;

/**
 * The XML-RPC server uses this interface to call a method of an RPC handler.
 * This should be implemented by any class that wants to directly take control
 * when it is called over RPC. Classes not implementing this interface will be
 * wrapped into an Invoker object that tries to find the matching method
 * for an XML-RPC request.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @version $Id: XmlRpcHandler.java,v 1.3 2005/04/22 10:25:57 hgomez Exp $
 */
public interface XmlRpcHandler
{
    /**
     * Return the result, or throw an Exception if something went wrong.
     */
    public Object execute (String method, Vector params)
            throws Exception;
}
