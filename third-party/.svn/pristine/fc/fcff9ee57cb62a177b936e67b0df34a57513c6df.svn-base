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
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @version $Id: AuthDemo.java,v 1.3 2005/04/22 10:25:57 hgomez Exp $
 */
public class AuthDemo implements AuthenticatedXmlRpcHandler
{
    /**
     *
     */
    public Object execute(String method, Vector v, String user, String password)
            throws Exception
    {
        // our simplistic authentication guidelines never fail ;)
        if (user == null || user.startsWith("bad"))
        {
            throw new XmlRpcException(5, "Sorry, you're not allowed in here!");
        }

        return ("Hello " + user);
    }
}
