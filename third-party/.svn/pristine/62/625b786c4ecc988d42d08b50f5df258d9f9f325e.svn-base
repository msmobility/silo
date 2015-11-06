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

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the CommonsXmlRpcTransport implementation.
 *
 * @author <a href="mailto:rhoegg@isisnetworks.net">Ryan Hoegg</a>
 * @version $Id: CommonsXmlRpcTransportTest.java,v 1.3 2005/04/22 10:26:17 hgomez Exp $
 */
public class CommonsXmlRpcTransportTest extends XmlRpcTransportTest {
    
    /** Creates a new instance of CommonsXmlRpcTransportTest */
    public CommonsXmlRpcTransportTest(String testName) {
        super(testName);
    }
    
    /**
     * Return the Test
     */
    public static Test suite() 
    {
        return new TestSuite(CommonsXmlRpcTransportTest.class);
    }
    
    /** 
     * Required by superclass.
     */
    protected XmlRpcTransport getTransport(URL url) {
        return new CommonsXmlRpcTransport(url);
    }
}
