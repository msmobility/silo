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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.apache.xmlrpc.util.DateTool;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.EncoderException;

/**
 * A XML writer intended for single-thread usage.  If you feed it a
 * <code>ByteArrayInputStream</code>, it may be necessary to call
 * <code>writer.flush()</code> before calling
 * <code>buffer.toByteArray()</code> to get the data written to
 * your byte buffer.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author Daniel L. Rall
 * @see <a href="http://www.xml.com/axml/testaxml.htm">Tim Bray's
 * Annotated XML Spec</a>
 */
class XmlWriter extends OutputStreamWriter
{
    // Various XML pieces.
    protected static final String PROLOG_START = "<?xml version=\"1.0";
    protected static final String PROLOG_END = "\"?>";
    protected static final String CLOSING_TAG_START = "</";
    protected static final String SINGLE_TAG_END = "/>";
    protected static final String LESS_THAN_ENTITY = "&lt;";
    protected static final String GREATER_THAN_ENTITY = "&gt;";
    protected static final String AMPERSAND_ENTITY = "&amp;";

    private static final char[] PROLOG =
        new char[PROLOG_START.length() + PROLOG_END.length()];
    static
    {
        int len = PROLOG_START.length();
        PROLOG_START.getChars(0, len, PROLOG, 0);
        PROLOG_END.getChars(0, PROLOG_END.length(), PROLOG, len);
    }

    /**
     * Java's name for the ISO-8859-1 encoding.
     */
    static final String ISO8859_1 = "ISO8859_1";

    /**
     * Java's name for the UTF-8 encoding.
     */
    static final String UTF8 = "UTF8";

    /**
     * Java's name for the UTF-16 encoding.
     */
    static final String UTF16 = "UTF-16";
    
    protected static final Base64 base64Codec = new Base64();

    /**
     * Class to delegate type decoding to.
     */
    protected static TypeDecoder typeDecoder;

    /**
     * Mapping between Java encoding names and "real" names used in
     * XML prolog.
     *
     * @see <a href="http://java.sun.com/j2se/1.4.2/docs/guide/intl/encoding.doc.html">Java character set names</a>
     */
    private static Properties encodings = new Properties();

    static
    {
        encodings.put(UTF8, "UTF-8");
        encodings.put(ISO8859_1, "ISO-8859-1");
        typeDecoder = new DefaultTypeDecoder();
    }

    /**
     * Thread-safe wrapper for the <code>DateFormat</code> object used
     * to parse date/time values.
     */
    private static DateTool dateTool = new DateTool();

    /**
     * Whether the XML prolog has been written.
     */
    boolean hasWrittenProlog = false;

    /**
     * Creates a new instance.
     *
     * @param out The stream to write output to.
     * @param enc The encoding to using for outputing XML.  Only UTF-8
     * and UTF-16 are supported.  If another encoding is specified,
     * UTF-8 will be used instead for widest XML parser
     * interoperability.
     * @exception UnsupportedEncodingException Since unsupported
     * encodings are internally converted to UTF-8, this should only
     * be seen as the result of an internal error.
     */
    public XmlWriter(OutputStream out, String enc)
        throws UnsupportedEncodingException
    {
        // Super-class wants the Java form of the encoding.
        super(out, forceUnicode(enc));
    }

    /**
     * @param encoding A caller-specified encoding.
     * @return An Unicode encoding.
     */
    private static String forceUnicode(String encoding)
    {
        if (encoding == null || !encoding.toUpperCase().startsWith("UTF"))
        {
            encoding = UTF8;
        }
        return encoding;
    }

    /**
     * Tranforms a Java encoding to the canonical XML form (if a
     * mapping is available).
     *
     * @param javaEncoding The name of the encoding as known by Java.
     * @return The XML encoding (if a mapping is available);
     * otherwise, the encoding as provided.
     *
     * @deprecated This method will not be visible in 2.0.
     */
    protected static String canonicalizeEncoding(String javaEncoding)
    {
        return encodings.getProperty(javaEncoding, javaEncoding);
    }

    /**
     * A mostly pass-through implementation wrapping
     * <code>OutputStreamWriter.write()</code> which assures that the
     * XML prolog is written before any other data.
     *
     * @see java.io.OutputStreamWriter.write(char[], int, int)
     */
    public void write(char[] cbuf, int off, int len)
        throws IOException
    {
        if (!hasWrittenProlog)
        {
            super.write(PROLOG, 0, PROLOG.length);
            hasWrittenProlog = true;
        }
        super.write(cbuf, off, len);
    }

    /**
     * A mostly pass-through implementation wrapping
     * <code>OutputStreamWriter.write()</code> which assures that the
     * XML prolog is written before any other data.
     *
     * @see java.io.OutputStreamWriter.write(char)
     */
    public void write(char c)
        throws IOException
    {
        if (!hasWrittenProlog)
        {
            super.write(PROLOG, 0, PROLOG.length);
            hasWrittenProlog = true;
        }
        super.write(c);
    }

    /**
     * A mostly pass-through implementation wrapping
     * <code>OutputStreamWriter.write()</code> which assures that the
     * XML prolog is written before any other data.
     *
     * @see java.io.OutputStreamWriter.write(String, int, int)
     */
    public void write(String str, int off, int len)
        throws IOException
    {
        if (!hasWrittenProlog)
        {
            super.write(PROLOG, 0, PROLOG.length);
            hasWrittenProlog = true;
        }
        super.write(str, off, len);
    }

    /**
     * Writes the XML representation of a supported Java object type.
     *
     * @param obj The <code>Object</code> to write.
     * @exception XmlRpcException Unsupported character data found.
     * @exception IOException Problem writing data.
     * @throws IllegalArgumentException If a <code>null</code>
     * parameter is passed to this method (not supported by the <a
     * href="http://xml-rpc.com/spec">XML-RPC specification</a>).
     */
    public void writeObject(Object obj)
        throws XmlRpcException, IOException
    {
        startElement("value");
        if (obj == null)
        {
            throw new XmlRpcException
                (0, "null values not supported by XML-RPC");
        }
        else if (obj instanceof String)
        {
            chardata(obj.toString());
        }
        else if (typeDecoder.isXmlRpcI4(obj))
        {
            startElement("int");
            write(obj.toString());
            endElement("int");
        }
        else if (obj instanceof Boolean)
        {
            startElement("boolean");
            write(((Boolean) obj).booleanValue() ? "1" : "0");
            endElement("boolean");
        }
        else if (typeDecoder.isXmlRpcDouble(obj))
        {
            startElement("double");
            write(obj.toString());
            endElement("double");
        }
        else if (obj instanceof Date)
        {
            startElement("dateTime.iso8601");
            Date d = (Date) obj;
            write(dateTool.format(d));
            endElement("dateTime.iso8601");
        }
        else if (obj instanceof byte[])
        {
            startElement("base64");
            try
            {
                this.write((byte[]) base64Codec.encode(obj));
            }
            catch (EncoderException e)
            {
                throw new XmlRpcException
                    (0, "Unable to Base 64 encode byte array", e);
            }
            endElement("base64");
        }
        else if (obj instanceof Object[])
        {
            startElement("array");
            startElement("data");
            Object[] array = (Object []) obj;
            for (int i = 0; i < array.length; i++)
            {
                writeObject(array[i]);
            }
            endElement("data");
            endElement("array");
        }
        else if (obj instanceof Vector)
        {
            startElement("array");
            startElement("data");
            Vector array = (Vector) obj;
            int size = array.size();
            for (int i = 0; i < size; i++)
            {
                writeObject(array.elementAt(i));
            }
            endElement("data");
            endElement("array");
        }
        else if (obj instanceof Hashtable)
        {
            startElement("struct");
            Hashtable struct = (Hashtable) obj;
            for (Enumeration e = struct.keys(); e.hasMoreElements(); )
            {
                String key = (String) e.nextElement();
                Object value = struct.get(key);
                startElement("member");
                startElement("name");
                chardata(key);
                endElement("name");
                writeObject(value);
                endElement("member");
            }
            endElement("struct");
        }
        else
        {
            throw new XmlRpcException(0, "Unsupported Java type: "
                                       + obj.getClass(), null);
        }
        endElement("value");
    }

    /**
     * This is used to write out the Base64 output...
     */
    protected void write(byte[] byteData) throws IOException
    {
        for (int i = 0; i < byteData.length; i++)
        {
            write(byteData[i]);
        }
    }

    /**
     * Writes characters like '\r' (0xd) as "&amp;#13;".
     */
    private void writeCharacterReference(char c)
        throws IOException
    {
        write("&#");
        write(String.valueOf((int) c));
        write(';');
    }

    /**
     *
     * @param elem
     * @throws IOException
     */
    protected void startElement(String elem) throws IOException
    {
        write('<');
        write(elem);
        write('>');
    }

    /**
     *
     * @param elem
     * @throws IOException
     */
    protected void endElement(String elem) throws IOException
    {
        write(CLOSING_TAG_START);
        write(elem);
        write('>');
    }

    /**
     *
     * @param elem
     * @throws IOException
     */
    protected void emptyElement(String elem) throws IOException
    {
        write('<');
        write(elem);
        write(SINGLE_TAG_END);
    }

    /**
     * Writes text as <code>PCDATA</code>.
     *
     * @param text The data to write.
     * @exception XmlRpcException Unsupported character data found.
     * @exception IOException Problem writing data.
     */
    protected void chardata(String text)
        throws XmlRpcException, IOException
    {
        int l = text.length ();
        // ### TODO: Use a buffer rather than going character by
        // ### character to scale better for large text sizes.
        //char[] buf = new char[32];
        for (int i = 0; i < l; i++)
        {
            char c = text.charAt (i);
            switch (c)
            {
            case '\t':
            case '\n':
                write(c);
                break;
            case '\r':
                // Avoid normalization of CR to LF.
                writeCharacterReference(c);
                break;
            case '<':
                write(LESS_THAN_ENTITY);
                break;
            case '>':
                write(GREATER_THAN_ENTITY);
                break;
            case '&':
                write(AMPERSAND_ENTITY);
                break;
            default:
                // Though the XML spec requires XML parsers to support
                // Unicode, not all such code points are valid in XML
                // documents.  Additionally, previous to 2003-06-30
                // the XML-RPC spec only allowed ASCII data (in
                // <string> elements).  For interoperability with
                // clients rigidly conforming to the pre-2003 version
                // of the XML-RPC spec, we entity encode characters
                // outside of the valid range for ASCII, too.
                if (c > 0x7f || !isValidXMLChar(c))
                {
                    // Replace the code point with a character reference.
                    writeCharacterReference(c);
                }
                else
                {
                    write(c);
                }
            }
        }
    }

    /**
     * Section 2.2 of the XML spec describes which Unicode code points
     * are valid in XML:
     *
     * <blockquote><code>#x9 | #xA | #xD | [#x20-#xD7FF] |
     * [#xE000-#xFFFD] | [#x10000-#x10FFFF]</code></blockquote>
     *
     * Code points outside this set must be entity encoded to be
     * represented in XML.
     *
     * @param c The character to inspect.
     * @return Whether the specified character is valid in XML.
     */
    private static final boolean isValidXMLChar(char c)
    {
        switch (c)
        {
        case 0x9:
        case 0xa:  // line feed, '\n'
        case 0xd:  // carriage return, '\r'
            return true;

        default:
            return ( (0x20 < c && c <= 0xd7ff) ||
                     (0xe000 < c && c <= 0xfffd) ||
                     (0x10000 < c && c <= 0x10ffff) );
        }
    }

    protected static void setTypeDecoder(TypeDecoder newTypeDecoder)
    {
        typeDecoder = newTypeDecoder;
    }
}
