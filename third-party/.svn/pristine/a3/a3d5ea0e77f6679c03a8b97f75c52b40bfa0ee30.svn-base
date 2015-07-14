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


package org.apache.xmlrpc.applet;

/**
 * This is thrown by the XmlRpcClient if the remote server reported an error.
 * If something went wrong at a lower level (e.g. no http connection) an
 * IOException will be thrown instead.
 *
 * @version $Id: XmlRpcException.java,v 1.3 2005/04/22 10:25:58 hgomez Exp $
 */
public class XmlRpcException extends Exception
{
    /**
     * The fault code of the exception. For servers based on this library, this
     * will always be 0. (If there are predefined error codes, they should be in
     * the XML-RPC spec.)
     */
    public final int code;

    /**
     *
     * @param code
     * @param message
     */
    public XmlRpcException (int code, String message)
    {
        super (message);
        this.code = code;
    }
}
