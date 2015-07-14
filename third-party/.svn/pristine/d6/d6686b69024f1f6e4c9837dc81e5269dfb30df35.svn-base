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

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;


/**
 * An applet that can be accessed via LiveConnect from JavaScript. It provides
 * methods for adding arguments and triggering method execution for XML-RPC
 * requests. This works on recent Netscape 4.x browsers as well as Internet
 * Explorer 4.0 on Windows 95/NT, but not on IE/Mac. <p>
 *
 * Results from XML-RPC calls are exposed to JavaScript as the are, i.e.
 * &lt;structs>s are <a href=http://java.sun.com/products/jdk/1.1/docs/api/java.util.Hashtable.html>Hashtables</a>
 * and &lt;array>s are <a href=http://java.sun.com/products/jdk/1.1/docs/api/java.util.Vector.html>Vectors</a>
 * and can be accessed thru their public methods. It seems like Date objects are
 * not converted properly between JavaScript and Java, so the dateArg methods
 * take long values instead of Date objects as parameters (date.getTime()).
 *
 * @version $Id: JSXmlRpcApplet.java,v 1.4 2005/04/22 10:25:58 hgomez Exp $
 */
public class JSXmlRpcApplet extends XmlRpcApplet
{
    public Object loaded = null;

    private String errorMessage;
    private Vector arguments;

    /**
     *
     */
    public void init()
    {
        initClient();
        arguments = new Vector();
        loaded = Boolean.TRUE;
        System.out.println("JSXmlRpcApplet initialized");
    }

    // add ints (primitve != object) to structs, vectors
    public void addIntArg(int value)
    {
        arguments.addElement(new Integer(value));
    }

    public void addIntArgToStruct(Hashtable struct, String key, int value)
    {
        struct.put(key, new Integer(value));
    }

    public void addIntArgToArray(Vector ary, int value)
    {
        ary.addElement(new Integer(value));
    }

    // add floats/doubles to structs, vectors
    public void addDoubleArg(float value)
    {
        arguments.addElement(new Double(value));
    }

    public void addDoubleArgToStruct(Hashtable struct, String key, float value)
    {
        struct.put(key, new Double(value));
    }

    public void addDoubleArgToArray(Vector ary, float value)
    {
        ary.addElement(new Double(value));
    }

    public void addDoubleArg(double value)
    {
        arguments.addElement(new Double(value));
    }

    public void addDoubleArgToStruct(Hashtable struct, String key, double value)
    {
        struct.put(key, new Double(value));
    }

    public void addDoubleArgToArray(Vector ary, double value)
    {
        ary.addElement(new Double(value));
    }

    // add bools to structs, vectors
    public void addBooleanArg(boolean value)
    {
        arguments.addElement(new Boolean(value));
    }

    public void addBooleanArgToStruct(Hashtable struct, String key,
            boolean value)
    {
        struct.put(key, new Boolean(value));
    }

    public void addBooleanArgToArray(Vector ary, boolean value)
    {
        ary.addElement(new Boolean(value));
    }

    // add Dates to structs, vectors Date argument in SystemTimeMillis (seems to be the way)
    public void addDateArg(long dateNo)
    {
        arguments.addElement(new Date(dateNo));
    }

    public void addDateArgToStruct(Hashtable struct, String key, long dateNo)
    {
        struct.put(key, new Date(dateNo));
    }

    public void addDateArgToArray(Vector ary, long dateNo)
    {
        ary.addElement(new Date(dateNo));
    }

    // add String arguments
    public void addStringArg(String str)
    {
        arguments.addElement(str);
    }

    public void addStringArgToStruct(Hashtable struct, String key, String str)
    {
        struct.put(key, str);
    }

    public void addStringArgToArray(Vector ary, String str)
    {
        ary.addElement (str);
    }

    // add Array arguments
    public Vector addArrayArg()
    {
        Vector v = new Vector();
        arguments.addElement(v);
        return v;
    }

    public Vector addArrayArgToStruct(Hashtable struct, String key)
    {
        Vector v = new Vector();
        struct.put(key, v);
        return v;
    }

    public Vector addArrayArgToArray(Vector ary)
    {
        Vector v = new Vector();
        ary.addElement(v);
        return v;
    }

    // add Struct arguments
    public Hashtable addStructArg()
    {
        Hashtable ht = new Hashtable();
        arguments.addElement(ht);
        return ht;
    }

    public Hashtable addStructArgToStruct(Hashtable struct, String key)
    {
        Hashtable ht = new Hashtable();
        struct.put(key, ht);
        return ht;
    }

    public Hashtable addStructArgToArray(Vector ary)
    {
        Hashtable ht = new Hashtable();
        ary.addElement(ht);
        return ht;
    }

    // get the errorMessage, null if none
    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void reset()
    {
        arguments = new Vector();
    }

    public Object execute(String methodName)
    {
        // XmlRpcSupport.setDebug (true);
        errorMessage = null;
        showStatus("Connecting to Server...");
        Object returnValue = null;
        try
        {
            returnValue = execute(methodName, arguments);
        }
        catch (Exception e)
        {
            errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage == "")
            {
                errorMessage = e.toString();
            }
        }
        // reset argument array for reuse
        arguments = new Vector();

        showStatus("");
        return returnValue;
    }
}
