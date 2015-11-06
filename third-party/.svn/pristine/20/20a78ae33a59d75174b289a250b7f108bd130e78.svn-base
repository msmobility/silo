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


import java.net.URL;

/**
 * A callback interface for an asynchronous XML-RPC call.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @version $Id: AsyncCallback.java,v 1.3 2005/04/22 10:25:57 hgomez Exp $
 */
public interface AsyncCallback
{
    /**
     * Call went ok, handle result.
     */
    public void handleResult(Object result, URL url, String method);

    /**
     * Something went wrong, handle error.
     */
    public void handleError(Exception exception, URL url, String method);
}
