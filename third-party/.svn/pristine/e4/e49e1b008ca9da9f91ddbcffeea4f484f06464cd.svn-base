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
 * The default implementation of the <code>TypeDecoder</code>
 * interface.  Provides the following mappings:
 *
 * <table cellpadding="3" cellspacing="2" border="1" width="100%">
 *   <tr><th>XML-RPC data type</th>         <th>Java class</th></tr>
 *   <tr><td>&lt;i4&gt; or &lt;int&gt;</td> <td>java.lang.Integer</td></tr>
 *   <tr><td>&lt;double&gt;</td>            <td>java.lang.Double, java.lang.Float</td></tr>
 * </table>
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @see org.apache.xmlrpc.TypeDecoder
 * @since 1.2
 */
public class DefaultTypeDecoder
    implements TypeDecoder
{
    /**
     * Creates a new instance.
     */
    public DefaultTypeDecoder()
    {
    }

    public boolean isXmlRpcI4(Object o)
    {
        return (o instanceof Integer);
    }

    public boolean isXmlRpcDouble(Object o)
    {
        return (o instanceof Float || o instanceof Double);
    }
}
