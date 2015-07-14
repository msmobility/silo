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


import java.text.ParseException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.DecoderException;
import org.apache.xmlrpc.util.DateTool;

/**
 * The default implementation of the <code>TypeFactory</code>
 * interface.  Provides the following mappings:
 *
 * <table cellpadding="3" cellspacing="2" border="1" width="100%">
 *   <tr><th>XML-RPC data type</th>         <th>Java class</th></tr>
 *   <tr><td>&lt;i4&gt; or &lt;int&gt;</td> <td>java.lang.Integer</td></tr>
 *   <tr><td>&lt;boolean&gt;</td>           <td>java.lang.Boolean</td></tr>
 *   <tr><td>&lt;string&gt;</td>            <td>java.lang.String</td></tr>
 *   <tr><td>&lt;double&gt;</td>            <td>java.lang.Double</td></tr>
 *   <tr><td>&lt;dateTime.iso8601&gt;</td>  <td>java.util.Date</td></tr>
 *   <tr><td>&lt;base64&gt;</td>            <td>byte[ ]</td></tr> 
 * </table>
 *
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @see org.apache.xmlrpc.TypeFactory
 * @since 1.2
 */
public class DefaultTypeFactory
    implements TypeFactory
{
    /**
     * Thread-safe wrapper for the <code>DateFormat</code> object used
     * to parse date/time values.
     */
    private static DateTool dateTool = new DateTool();
    private static final Base64 base64Codec = new Base64();

    /**
     * Creates a new instance.
     */
    public DefaultTypeFactory()
    {
    }

    public Object createInteger(String cdata)
    {
        return new Integer(cdata.trim());
    }

    public Object createBoolean(String cdata)
    {
        return ("1".equals(cdata.trim ())
               ? Boolean.TRUE : Boolean.FALSE);
    }

    public Object createDouble(String cdata)
    {
        return new Double(cdata.trim ());

    }

    public Object createDate(String cdata)
    {
        try
        {
            return dateTool.parse(cdata.trim());
        }
        catch (ParseException p)
        {
            throw new RuntimeException(p.getMessage());
        }
    }

    public Object createBase64(String cdata)
    {
        try
        {
            return base64Codec.decode((Object) cdata.getBytes());
        }
        catch (DecoderException e) {
            //TODO: consider throwing an exception here?
            return new byte[0];
        }
    }

    public Object createString(String cdata)
    {
        return cdata;
    }
}
