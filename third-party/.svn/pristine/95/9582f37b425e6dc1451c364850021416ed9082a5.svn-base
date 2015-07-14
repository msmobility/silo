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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

/**
 * Introspects handlers using Java Reflection to call methods matching
 * a XML-RPC call.
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @author Daniel L. Rall
 * @author <a href="mailto:andrew@kungfoocoder.org">Andrew Evers</a>
 */
public class Invoker implements XmlRpcHandler
{
    private Object invokeTarget;
    private Class targetClass;

    public Invoker(Object target)
    {
        invokeTarget = target;
        targetClass = (invokeTarget instanceof Class) ? (Class) invokeTarget :
                invokeTarget.getClass();
        if (XmlRpc.debug)
        {
            System.out.println("Target object is " + targetClass);
        }
    }

    /**
     * main method, sucht methode in object, wenn gefunden dann aufrufen.
     */
    public Object execute(String methodName, Vector params) throws Exception
    {
        // Array mit Classtype bilden, ObjectAry mit Values bilden
        Class[] argClasses = null;
        Object[] argValues = null;
        if (params != null)
        {
            argClasses = new Class[params.size()];
            argValues = new Object[params.size()];
            for (int i = 0; i < params.size(); i++)
            {
                argValues[i] = params.elementAt(i);
                if (argValues[i] instanceof Integer)
                {
                    argClasses[i] = Integer.TYPE;
                }
                else if (argValues[i] instanceof Double)
                {
                    argClasses[i] = Double.TYPE;
                }
                else if (argValues[i] instanceof Boolean)
                {
                    argClasses[i] = Boolean.TYPE;
                }
                else
                {
                    argClasses[i] = argValues[i].getClass();
                }
            }
        }

        // Methode da ?
        Method method = null;

        // The last element of the XML-RPC method name is the Java
        // method name.
        int dot = methodName.lastIndexOf('.');
        if (dot > -1 && dot + 1 < methodName.length())
        {
            methodName = methodName.substring(dot + 1);
        }

        if (XmlRpc.debug)
        {
            System.out.println("Searching for method: " + methodName +
                               " in class " + targetClass.getName());
            for (int i = 0; i < argClasses.length; i++)
            {
                System.out.println("Parameter " + i + ": " + argValues[i]
                        + " (" + argClasses[i] + ')');
            }
        }

        try
        {
            method = targetClass.getMethod(methodName, argClasses);
        }
        // Wenn nicht da dann entsprechende Exception returnen
        catch(NoSuchMethodException nsm_e)
        {
            throw nsm_e;
        }
        catch(SecurityException s_e)
        {
            throw s_e;
        }

        // Our policy is to make all public methods callable except
        // the ones defined in java.lang.Object.
        if (method.getDeclaringClass() == Object.class)
        {
            throw new XmlRpcException(0, "Invoker can't call methods "
                    + "defined in java.lang.Object");
        }

        // invoke
        Object returnValue = null;
        try
        {
            returnValue = method.invoke(invokeTarget, argValues);
        }
        catch(IllegalAccessException iacc_e)
        {
            throw iacc_e;
        }
        catch(IllegalArgumentException iarg_e)
        {
            throw iarg_e;
        }
        catch(InvocationTargetException it_e)
        {
            if (XmlRpc.debug)
            {
                it_e.getTargetException().printStackTrace();
            }
            // check whether the thrown exception is XmlRpcException
            Throwable t = it_e.getTargetException();
            if (t instanceof XmlRpcException)
            {
                throw (XmlRpcException) t;
            }
            // It is some other exception
            throw new Exception(t.toString());
        }
        if (returnValue == null && method.getReturnType() == Void.TYPE)
        {
            // Not supported by the spec.
            throw new IllegalArgumentException
                ("void return types for handler methods not supported");
        }
        return returnValue;
    }
}
