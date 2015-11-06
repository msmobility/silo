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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.AttributeList;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import uk.co.wilson.xml.MinML;

/**
 * This abstract base class provides basic capabilities for XML-RPC,
 * like parsing of parameters or encoding Java objects into XML-RPC
 * format.  Any XML parser with a <a
 * href="http://www.megginson.com/SAX/">SAX</a> interface can be used.
 *
 * <p>XmlRpcServer and XmlRpcClient are the classes that actually
 * implement an XML-RPC server and client.
 *
 * @see org.apache.xmlrpc.XmlRpcServer
 * @see org.apache.xmlrpc.XmlRpcClient
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author Daniel L. Rall
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 * @version $Id: XmlRpc.java,v 1.42 2005/05/16 21:23:21 dlr Exp $
 */
public abstract class XmlRpc extends HandlerBase
{
    /**
     * The version string used in HTTP communication.
     */
    // FIXME: Use Ant <filter> to pre-process version number into a
    // class-loaded .properties file at build time.  Use here when
    // available, and otherwise provide no version string.
    public static final String version = "Apache XML-RPC 2.0";

    /**
     * The default parser to use (MinML).
     */
    private static final String DEFAULT_PARSER = MinML.class.getName();

    /**
     * The maximum number of threads which can be used concurrently.
     */
    private static int maxThreads = 100;

    String methodName;

    /**
     * The class name of SAX parser to use.
     */
    private static Class parserClass;
    private static Hashtable saxDrivers = new Hashtable (8);

    static
    {
        // A mapping of short identifiers to the fully qualified class
        // names of common SAX parsers.  If more mappings are added
        // here, increase the size of the saxDrivers Map used to store
        // them.
        saxDrivers.put("xerces", "org.apache.xerces.parsers.SAXParser");
        saxDrivers.put("xp", "com.jclark.xml.sax.Driver");
        saxDrivers.put("ibm1", "com.ibm.xml.parser.SAXDriver");
        saxDrivers.put("ibm2", "com.ibm.xml.parsers.SAXParser");
        saxDrivers.put("aelfred", "com.microstar.xml.SAXDriver");
        saxDrivers.put("oracle1", "oracle.xml.parser.XMLParser");
        saxDrivers.put("oracle2", "oracle.xml.parser.v2.SAXParser");
        saxDrivers.put("openxml", "org.openxml.parser.XMLSAXParser");
    }

    // the stack we're parsing our values into.
    Stack values;
    Value currentValue;

    /**
     * Used to collect character data (<code>CDATA</code>) of
     * parameter values.
     */
    StringBuffer cdata;
    boolean readCdata;

    // XML RPC parameter types used for dataMode
    static final int STRING = 0;
    static final int INTEGER = 1;
    static final int BOOLEAN = 2;
    static final int DOUBLE = 3;
    static final int DATE = 4;
    static final int BASE64 = 5;
    static final int STRUCT = 6;
    static final int ARRAY = 7;

    // Error level + message
    int errorLevel;
    String errorMsg;

    static final int NONE = 0;
    static final int RECOVERABLE = 1;
    static final int FATAL = 2;

    /**
     * Wheter to use HTTP Keep-Alive headers.
     */
    static boolean keepalive = false;

    /**
     * Whether to log debugging output.
     */
    public static boolean debug = false;

    /**
     * The list of valid XML elements used for RPC.
     */
    final static String types[] =
    {
        "String",
        "Integer",
        "Boolean",
        "Double",
        "Date",
        "Base64",
        "Struct",
        "Array"
    };

    /**
     * Java's name for the encoding we're using.  Defaults to
     * <code>UTF8</code> (of which <code>ISO8859_1</code> is a
     * subset).
     */
    static String encoding = XmlWriter.UTF8;

    /**
     * Java's name for the input encoding we're using.  Defaults to
     * <code>null</code>, signifying the platform default. This may
     * need to be overridden on platforms where the default encoding
     * is not compatible with ASCII (eg. EBCDIC) but the network is
     * still ASCII-like.
     */
    static String defaultInputEncoding = null;
 
    private TypeFactory typeFactory;
	private String inputEncoding;

    /**
     * Creates a new instance with the {@link
     * org.apache.xmlrpc.TypeFactory} set to an instance of the class
     * named by the <code>org.apache.xmlrpc.TypeFactory</code> System
     * property.  If property not set or class is unavailable, uses
     * the default of {@link org.apache.xmlrpc.TypeFactory}.
     */
    protected XmlRpc()
    {
        String typeFactoryName = null;
        try
        {
            typeFactoryName = System.getProperty(TypeFactory.class.getName());
        }
        catch (SecurityException e)
        {
            // An unsigned applet may not access system properties.
            // No-op means we use the default TypeFactory instead.
            if (debug)
            {
                System.out.println("Unable to determine the value of the " +
                                   "system property '" +
                                   TypeFactory.class.getName() + "': " +
                                   e.getMessage());
            }
        }
        this.typeFactory = createTypeFactory(typeFactoryName);
		this.inputEncoding = defaultInputEncoding;
    }

    /**
     * Creates a new instance with the specified {@link
     * org.apache.xmlrpc.TypeFactory}.
     *
     * @param typeFactoryName The fully qualified class name of the
     * {@link org.apache.xmlrpc.TypeFactory} implementation to use.
     */
    protected XmlRpc(String typeFactoryName)
    {
        this.typeFactory = createTypeFactory(typeFactoryName);
    }

    /**
     * Creates a new instance of the specified {@link
     * org.apache.xmlrpc.TypeFactory}.
     *
     * @param className The fully qualified class name of the
     * implementation to use.
     * @return The new type mapping.
     */
    private TypeFactory createTypeFactory(String className)
    {
        Class c = null;
        if (className != null && className.length() > 0)
        {
            try
            {
                c = Class.forName(className);
            }
            catch (ClassNotFoundException e)
            {
                System.err.println("Error loading TypeFactory '" +
                                   "' " + c.getName() +
                                   "': Using the default instead: " +
                                   e.getMessage());
            }
        }

        // If we're using the default, provide it immediately.
        if (c == null || DefaultTypeFactory.class.equals(c))
        {
            return new DefaultTypeFactory();
        }

        try
        {
            return (TypeFactory) c.newInstance();
        }
        catch (Exception e)
        {
            System.err.println("Unable to create configured TypeFactory '" +
                               c.getName() + "': " + e.getMessage() +
                               ": Falling back to default");
            if (debug)
            {
                e.printStackTrace();
            }
            return new DefaultTypeFactory();
        }
    }

    /**
     * Set the SAX Parser to be used. The argument can either be the
     * full class name or a user friendly shortcut if the parser is
     * known to this class. The parsers that can currently be set by
     * shortcut are listed in the main documentation page. If you are
     * using another parser please send me the name of the SAX driver
     * and I'll include it in a future release.  If setDriver() is
     * never called then the System property "sax.driver" is
     * consulted. If that is not defined the driver defaults to
     * OpenXML.
     */
    public static void setDriver(String driver) throws ClassNotFoundException
    {
        String parserClassName = null;
        try
        {
            parserClassName = (String) saxDrivers.get(driver);
            if (parserClassName == null)
            {
                // Identifier lookup failed, assuming we were provided
                // with the fully qualified class name.
                parserClassName = driver;
            }
            parserClass = Class.forName(parserClassName);
        }
        catch (ClassNotFoundException x)
        {
            throw new ClassNotFoundException ("SAX driver not found: "
                    + parserClassName);
        }
    }

    /**
     * Set the SAX Parser to be used by directly passing the Class object.
     */
    public static void setDriver(Class driver)
    {
        parserClass = driver;
    }

    /**
     * Set the encoding of the XML.
     *
     * @param enc The Java name of the encoding.
     */
    public static void setEncoding(String enc)
    {
        encoding = enc;
    }

    /**
     * Return the encoding, transforming to the canonical name if
     * possible.
     *
     * @see org.apache.xmlrpc.XmlWriter#canonicalizeEncoding(String)
     */
    public String getEncoding()
    {
        return XmlWriter.canonicalizeEncoding(encoding);
    }

    /** Set the default input encoding of the XML.
     * This is used only if set.
     *
     * @param enc The Java name of the encoding.
     * @see #setInputEncoding(String)
     */
    public static void setDefaultInputEncoding(String enc)
    {
        defaultInputEncoding = enc;
    }

    /**
     * Return the default input encoding. This may be null.
     * This is always a Java encoding name, it is not transformed.
     *
     * @return the Java encoding name to use, if set, otherwise null.
     * @see #getInputEncoding()
     */
    public static String getDefaultInputEncoding()
    {
        return defaultInputEncoding;
    }

    /**
     * Set the input encoding for this XmlRpc instance.  This can be
     * used when the XMLRPC response does not contain the proper
     * encoding information in the XML declaration.
     *
     * @param enc The Java name of the encoding.
     */
    public void setInputEncoding(String enc)
    {
        inputEncoding = enc;
    }

    /**
     * Get the input encoding for this XmlRpc instance.  This is a Java
     * encoding name.
     *
     * @return The Java encoding name to use.  <code>null</code> if not set.
     */
    public String getInputEncoding()
    {
        return inputEncoding;
    }

    /**
     * Gets the maximum number of threads used at any given moment.
     */
    public static int getMaxThreads()
    {
        return maxThreads;
    }

    /**
     * Sets the maximum number of threads used at any given moment.
     */
    public static void setMaxThreads(int maxThreads)
    {
        XmlRpc.maxThreads = maxThreads;
    }

    /**
     * Switch debugging output on/off.
     */
    public static void setDebug(boolean val)
    {
        debug = val;
    }

    /**
     * Switch HTTP keepalive on/off.
     */
    public static void setKeepAlive(boolean val)
    {
        keepalive = val;
    }

    /**
     * get current HTTP keepalive mode.
     */
    public static boolean getKeepAlive()
    {
        return keepalive;
    }

    /**
     * Parse the input stream. For each root level object, method
     * <code>objectParsed</code> is called.
     */
    synchronized void parse(InputStream is) throws Exception
    {
        // reset values (XmlRpc objects are reusable)
        errorLevel = NONE;
        errorMsg = null;
        values = new Stack();
        if (cdata == null)
        {
            cdata = new StringBuffer(128);
        }
        else
        {
            cdata.setLength(0);
        }
        readCdata = false;
        currentValue = null;

        long now = System.currentTimeMillis();
        if (parserClass == null)
        {
            // try to get the name of the SAX driver from the System properties
            String driver;
            try
            {
                driver = System.getProperty("sax.driver", DEFAULT_PARSER);
            }
            catch (SecurityException e)
            {
                // An unsigned applet may not access system properties.
                driver = DEFAULT_PARSER;
            }
            setDriver(driver);
        }

        Parser parser = null;
        try
        {
            parser = (Parser) parserClass.newInstance();
        }
        catch (NoSuchMethodError nsm)
        {
            // This is thrown if no constructor exists for the parser class
            // and is transformed into a regular exception.
            throw new Exception("Can't create Parser: " + parserClass);
        }

        parser.setDocumentHandler(this);
        parser.setErrorHandler(this);

        if (debug)
        {
            System.out.println("Beginning parsing XML input stream");
        }
        try
        {
            if(inputEncoding == null)
            {         
              parser.parse(new InputSource(is));
            }
            else
            {
              parser.parse( new InputSource( new InputStreamReader(is, inputEncoding)));
            }
        }
        finally
        {
            // Clear any huge buffers.
            if (cdata.length() > 128 * 4)
            {
                // Exceeded original capacity by greater than 4x; release
                // buffer to prevent leakage.
                cdata = null;
            }
        }
        if (debug)
        {
            System.out.println ("Spent " + (System.currentTimeMillis() - now)
                    + " millis parsing");
        }
    }

    /**
     * This method is called when a root level object has been parsed.
     * Sub-classes implement this callback to receive the fully parsed
     * object.
     */
    protected abstract void objectParsed(Object what);


    ////////////////////////////////////////////////////////////////
    // methods called by XML parser

    /**
     * Method called by SAX driver.
     */
    public void characters(char ch[], int start, int length)
            throws SAXException
    {
        if (readCdata)
        {
            cdata.append(ch, start, length);
        }
    }

    /**
     * Method called by SAX driver.
     */
    public void endElement(String name) throws SAXException
    {

        if (debug)
        {
            System.out.println("endElement: " + name);
        }

        // finalize character data, if appropriate
        if (currentValue != null && readCdata)
        {
            currentValue.characterData(cdata.toString());
            cdata.setLength(0);
            readCdata = false;
        }

        if ("value".equals(name))
        {
            // Only handle top level objects or objects contained in
            // arrays here.  For objects contained in structs, wait
            // for </member> (see code below).
            int depth = values.size();
            if (depth < 2 || values.elementAt(depth - 2).hashCode() != STRUCT)
            {
                Value v = currentValue;
                values.pop();
                if (depth < 2)
                {
                    // This is a top-level object
                    objectParsed(v.value);
                    currentValue = null;
                }
                else
                {
                    // Add object to sub-array; if current container
                    // is a struct, add later (at </member>).
                    currentValue = (Value) values.peek();
                    currentValue.endElement(v);
                }
            }
        }

        // Handle objects contained in structs.
        if ("member".equals(name))
        {
            Value v = currentValue;
            values.pop();
            currentValue = (Value) values.peek();
            currentValue.endElement(v);
        }

        else if ("methodName".equals(name))
        {
            methodName = cdata.toString();
            cdata.setLength(0);
            readCdata = false;
        }
    }

    /**
     * Method called by SAX driver.
     */
    public void startElement(String name, AttributeList atts)
            throws SAXException
    {
        if (debug)
        {
            System.out.println("startElement: " + name);
        }

        if ("value".equals(name))
        {
            Value v = new Value();
            values.push(v);
            currentValue = v;
            // cdata object is reused
            cdata.setLength(0);
            readCdata = true;
        }
        else if ("methodName".equals(name))
        {
            cdata.setLength(0);
            readCdata = true;
        }
        else if ("name".equals(name))
        {
            cdata.setLength(0);
            readCdata = true;
        }
        else if ("string".equals(name))
        {
            // currentValue.setType (STRING);
            cdata.setLength(0);
            readCdata = true;
        }
        else if ("i4".equals(name) || "int".equals(name))
        {
            currentValue.setType(INTEGER);
            cdata.setLength(0);
            readCdata = true;
        }
        else if ("boolean".equals(name))
        {
            currentValue.setType(BOOLEAN);
            cdata.setLength(0);
            readCdata = true;
        }
        else if ("double".equals(name))
        {
            currentValue.setType(DOUBLE);
            cdata.setLength(0);
            readCdata = true;
        }
        else if ("dateTime.iso8601".equals(name))
        {
            currentValue.setType(DATE);
            cdata.setLength(0);
            readCdata = true;
        }
        else if ("base64".equals(name))
        {
            currentValue.setType(BASE64);
            cdata.setLength(0);
            readCdata = true;
        }
        else if ("struct".equals(name))
        {
            currentValue.setType(STRUCT);
        }
        else if ("array".equals(name))
        {
            currentValue.setType(ARRAY);
        }
    }

    /**
     *
     * @param e
     * @throws SAXException
     */
    public void error(SAXParseException e) throws SAXException
    {
        System.err.println("Error parsing XML: " + e);
        errorLevel = RECOVERABLE;
        errorMsg = e.toString();
    }

    /**
     *
     * @param e
     * @throws SAXException
     */
    public void fatalError(SAXParseException e) throws SAXException
    {
        System.err.println("Fatal error parsing XML: " + e);
        errorLevel = FATAL;
        errorMsg = e.toString();
    }

    /**
     * This represents a XML-RPC value parsed from the request.
     */
    class Value
    {
        int type;
        Object value;
        // the name to use for the next member of struct values
        String nextMemberName;

        Hashtable struct;
        Vector array;

        /**
         * Constructor.
         */
        public Value()
        {
            this.type = STRING;
        }

        /**
         * Notification that a new child element has been parsed.
         */
        public void endElement(Value child)
        {
            switch (type)
            {
                case ARRAY:
                    array.addElement(child.value);
                    break;
                case STRUCT:
                    struct.put(nextMemberName, child.value);
            }
        }

        /**
         * Set the type of this value. If it's a container, create the
         * corresponding java container.
         */
        public void setType(int type)
        {
            //System.out.println ("setting type to "+types[type]);
            this.type = type;
            switch (type)
            {
                case ARRAY:
                    value = array = new Vector();
                    break;
                case STRUCT:
                    value = struct = new Hashtable();
                    break;
            }
        }

        /**
         * Set the character data for the element and interpret it
         * according to the element type.
         */
        public void characterData(String cdata)
        {
            switch (type)
            {
                case INTEGER:
                    value = typeFactory.createInteger(cdata);
                    break;
                case BOOLEAN:
                    value = typeFactory.createBoolean(cdata);
                    break;
                case DOUBLE:
                    value = typeFactory.createDouble(cdata);
                    break;
                case DATE:
                    value = typeFactory.createDate(cdata);
                    break;
                case BASE64:
                    value = typeFactory.createBase64(cdata);
                    break;
                case STRING:
                    value = typeFactory.createString(cdata);
                    break;
                case STRUCT:
                    // this is the name to use for the next member of this struct
                    nextMemberName = cdata;
                    break;
            }
        }

        /**
         * This is a performance hack to get the type of a value
         * without casting the Object.  It breaks the contract of
         * method hashCode, but it doesn't matter since Value objects
         * are never used as keys in Hashtables.
         */
        public int hashCode()
        {
            return type;
        }

        /**
         *
         * @return
         */
        public String toString()
        {
            return (types[type] + " element " + value);
        }
    }
}
