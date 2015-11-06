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
 * Allows developers to customize the types translated to the XML-RPC
 * &lt;i4&gt; and &lt;double&gt .
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @see org.apache.xmlrpc.DefaultTypeDecoder
 * @since 1.2
 */
public interface TypeDecoder
{
    /**
     * Test if a local object translates to an &lt;i4&gt; tag.
     */
    public boolean isXmlRpcI4(Object o);

    /**
     * Test if a local object translates to a &lt;double&gt; tag.
     */
    public boolean isXmlRpcDouble(Object o);
}
