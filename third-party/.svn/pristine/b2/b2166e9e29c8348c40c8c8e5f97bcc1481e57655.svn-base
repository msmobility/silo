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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests XmlWriter.
 *
 * @author Daniel L. Rall
 * @version $Id: XmlWriterTest.java,v 1.12 2005/05/16 22:39:27 dlr Exp $
 */
public class XmlWriterTest
    extends TestCase 
{
    private ByteArrayOutputStream buffer;
    private XmlWriter writer;

    /**
     * Constructor
     */
    public XmlWriterTest(String testName) 
    {
        super(testName);
    }

    /**
     * Return the Test
     */
    public static Test suite() 
    {
        return new TestSuite(XmlWriterTest.class);
    }

    /**
     * Setup the test.
     */
    public void setUp() 
    {
        XmlRpc.setDebug(true);
        buffer = new ByteArrayOutputStream();
    }
   
    /**
     * Tear down the test.
     */
    public void tearDown() 
    {
        XmlRpc.setDebug(false);
    }

    public void testForceAlternateEncoding()
        throws Exception
    {
        writer = new XmlWriter(buffer, null);
        assertEquals("null should be forced to UTF-8",
                     XmlWriter.UTF8, writer.getEncoding());

        writer = new XmlWriter(buffer, XmlWriter.ISO8859_1);
        assertEquals(XmlWriter.ISO8859_1 + " should be forced to " +
                     XmlWriter.UTF8, XmlWriter.UTF8, writer.getEncoding());

        writer = new XmlWriter(buffer, "ISO8859_15");
        assertEquals("ISO8859_15 should be forced to " + XmlWriter.UTF8,
                     XmlWriter.UTF8, writer.getEncoding());

        writer = new XmlWriter(buffer, "EUC_JP");
        assertEquals("EUC_JP should be forced to " + XmlWriter.UTF8,
                     XmlWriter.UTF8, writer.getEncoding());

        writer = new XmlWriter(buffer, XmlWriter.UTF16);
        assertEquals(XmlWriter.UTF16 + " should remain " + XmlWriter.UTF16,
                     XmlWriter.UTF16, writer.getEncoding());
    }

    public void testProlog()
        throws IOException
    {
        final String EXPECTED_PROLOG =
            XmlWriter.PROLOG_START + XmlWriter.PROLOG_END;

        writer = new XmlWriter(buffer, XmlWriter.UTF8);
        writer.write(new char[0], 0, 0);
        writer.flush();
        assertEquals("Unexpected or missing XML prolog when writing char[]",
                     EXPECTED_PROLOG, buffer.toString());
        // Append a space using an overload, and assure non-duplication.
        writer.write(' ');
        writer.flush();
        assertEquals("Unexpected or missing XML prolog when writing char",
                     EXPECTED_PROLOG + ' ', buffer.toString());

        buffer = new ByteArrayOutputStream();
        writer = new XmlWriter(buffer, XmlWriter.UTF8);
        writer.write("");
        writer.flush();
        assertEquals("Unexpected or missing XML prolog when writing String",
                     EXPECTED_PROLOG, buffer.toString());
        // Try again to assure it's not duplicated in the output.
        writer.write("");
        writer.flush();
        assertEquals("Unexpected or missing XML prolog when writing String",
                     EXPECTED_PROLOG, buffer.toString());
        
    }

    public void testBasicResults()
        throws Exception
    {
        try
        {
            writer = new XmlWriter(buffer, XmlWriter.UTF8);

            String foobar = "foobar";
            writer.writeObject(foobar);
            writer.flush();
            String postProlog = "<value>" + foobar + "</value>";
            assertTrue("Unexpected results from writing of String",
                       buffer.toString().endsWith(postProlog));

            Integer thirtySeven = new Integer(37);
            writer.writeObject(thirtySeven);
            writer.flush();
            postProlog += "<value><int>" + thirtySeven + "</int></value>";
            assertTrue("Unexpected results from writing of Integer",
                       buffer.toString().endsWith(postProlog));

            Boolean flag = Boolean.TRUE;
            writer.writeObject(flag);
            writer.flush();
            postProlog += "<value><boolean>1</boolean></value>";
            assertTrue("Unexpected results from writing of Boolean",
                       buffer.toString().endsWith(postProlog));

            Object[] array = { foobar, thirtySeven };
            writer.writeObject(array);
            writer.flush();
            postProlog += "<value><array><data>";
            postProlog += "<value>" + foobar + "</value>";
            postProlog += "<value><int>" + thirtySeven + "</int></value>";
            postProlog += "</data></array></value>";
            assertTrue("Unexpected results from writing of Object[]",
                       buffer.toString().endsWith(postProlog));

            Hashtable map = new Hashtable();
            map.put(foobar, thirtySeven);
            writer.writeObject(map);
            writer.flush();
            postProlog += "<value><struct><member>";
            postProlog += "<name>" + foobar + "</name>";
            postProlog += "<value><int>" + thirtySeven + "</int></value>";
            postProlog += "</member></struct></value>";
            assertTrue("Unexpected results from writing of Hashtable",
                       buffer.toString().endsWith(postProlog));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testWriteCharacterReference()
        throws Exception
    {
        writer = new XmlWriter(buffer, null);
        writer.hasWrittenProlog = true;
        writer.writeObject(String.valueOf((char) 0x80));
        writer.flush();
        String postProlog = "<value>&#128;</value>";
        assertTrue("Character reference not created as expected",
                   buffer.toString().endsWith(postProlog));
    }
}
