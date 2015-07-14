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


package org.apache.xmlrpc.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.EncoderException;

/**
 * Provides utility functions useful in HTTP communications
 *
 * @author <a href="mailto:rhoegg@isisnetworks.net">Ryan Hoegg</a>
 */
public class HttpUtil
{
    private static final Base64 base64;
    
    static {
        base64 = new Base64();
    }
    
    private HttpUtil()
    {
        // private because currently we only offer static methods.
    }
    
    public static String encodeBasicAuthentication(String user, String password)
    {
        String auth;
        if (user == null || password == null)
        {
            auth = null;
        }
        else
        {
            try
            {
                Object bytes = (user + ':' + password).getBytes();
                auth = new String((byte[]) base64.encode(bytes)).trim();
            }
            catch (EncoderException e)
            {
                // EncoderException is never thrown in the body of
                // Base64.encode(byte[]) in Commons Codec 1.1.
                throw new RuntimeException("Possibly incompatible version of '"
                                           + Base64.class.getName() +
                                           "' used: " + e);
            }
        }
        return auth;
    }
}
