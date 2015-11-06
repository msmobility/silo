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
 * A simple handler which echos its input parameters.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @version $Id: Echo.java,v 1.4 2005/04/22 10:25:57 hgomez Exp $
 */
public class Echo implements XmlRpcHandler
{
    /**
     * Echos <code>parameters</code>.
     *
     * @param method Ignored.
     * @param parameters Handler input parameters.
     * @return The input parameters.
     */
    public Object execute(String method, Vector parameters)
            throws Exception
    {
        return parameters;
    }
}
