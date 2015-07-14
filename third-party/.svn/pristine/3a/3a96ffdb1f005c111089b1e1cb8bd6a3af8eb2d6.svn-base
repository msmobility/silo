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
 * This is thrown by the XmlRpcClient if the remote server reported an error.
 * If something went wrong at a lower level (e.g. no http connection) an
 * IOException will be thrown instead.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @version $Id: XmlRpcException.java,v 1.4 2005/05/02 04:22:21 dlr Exp $
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
     * The underlying cause of this exception.
     */
    private Throwable cause;

    /**
     * @see #XmlRpcException(int, String, Throwable)
     */
    public XmlRpcException(int code, String message)
    {
        this(code, message, null);
    }

    /**
     * Creates an instance with the specified message and root cause
     * exception.
     *
     * @param int The fault code for this problem.
     * @param message The message describing this exception.
     * @param cause The root cause of this exception.
     */
    public XmlRpcException(int code, String message, Throwable cause)
    {
        super(message);
        this.code = code;
        this.cause = cause;
    }

    /**
     * Returns the cause of this throwable or null if the cause is nonexistent
     * or unknown. (The cause is the throwable that caused this throwable to
     * get thrown.)
     * 
     * This implementation returns the cause that was supplied via the constructor,
     * according to the rules specified for a "legacy chained throwable" that
     * predates the addition of chained exceptions to Throwable.
     *
     * See the <a
     * href="http://java.sun.com/j2se/1.4.1/docs/api/java/lang/Throwable.html">JDK
     * 1.4 Throwable documentation</a> for more information.
     */
    public Throwable getCause()
    {
        return cause;
    }
}
