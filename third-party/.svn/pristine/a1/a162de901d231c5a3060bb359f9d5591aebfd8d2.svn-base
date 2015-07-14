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
 * Allows server developers to customize the type of Java objects
 * created for a corresponding XML-RPC type.
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @see org.apache.xmlrpc.DefaultTypeFactory
 * @since 1.2
 */
public interface TypeFactory
{
    /**
     * Create a local object for an &lt;int&gt; or &lt;i4&gt; tag.
     */
    public Object createInteger(String s);

    /**
     * Create a local object for a &lt;boolean&gt; tag.
     */
    public Object createBoolean(String s);

    /**
     * Create a local object for a &lt;double&gt; tag.
     */
    public Object createDouble(String s);

    /**
     * Create a local object for a &lt;dateTime.iso8601&gt; tag.
     */
    public Object createDate(String s);

    /**
     * Create a local object for a &lt;base64&gt; tag.
     */
    public Object createBase64(String s);

    /**
     * Create a local object for a &lt;string&gt; tag.
     */
    public Object createString(String s);
}
