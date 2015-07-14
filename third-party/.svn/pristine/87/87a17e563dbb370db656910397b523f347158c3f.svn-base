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
import java.util.Date;
import java.util.Vector;

public class Benchmark
    implements Runnable
{

    XmlRpcClient client;
    static String url;
    static int clients = 16;
    static int loops = 100;

    long start;

    int gCalls = 0, gErrors = 0;

    Date date;

    public Benchmark () throws Exception
    {
        client = new XmlRpcClientLite (url);

        Vector args = new Vector ();
        // Some JITs (Symantec, IBM) have problems with several Threads
        // starting all at the same time.
        // This initial XML-RPC call seems to pacify them.
        args.addElement (new Integer (123));
        client.execute ("math.abs", args);
        date = new Date ();
        date = new Date ((date.getTime() / 1000) * 1000);

        start = System.currentTimeMillis ();
        int nclients = clients;

        for (int i = 0; i < nclients; i++)
            new Thread (this).start ();
    }

    public void run ()
    {
        int errors = 0;
        int calls = 0;
        try
        {
            int val = (int)(-100 * Math.random ());
            Vector args = new Vector ();

            // ECHO STRING
            // args.addElement (Integer.toString (val));
            // ABS INT
            args.addElement (new Integer (val));
            // ECHO DATE
            // args.addElement (date);

            for (int i = 0; i < loops; i++)
            {

                // ABS INT
                Integer ret = (Integer) client.execute ("math.abs", args);
                // ECHO
                // Vector v = (Vector) client.execute ("echo", args);
                // ECHO DATE
                // Date d = (Date) v.elementAt (0);

                // ABS INT
                if (ret.intValue () != Math.abs (val))
                {
                    // ECHO DATE
                    // if (date.getTime() !=  d.getTime()) {
                    // ECHO STRING
                    // if (!Integer.toString(val).equals (v.elementAt (0))) {
                    errors += 1;
                }
                calls += 1;
            }
        }
        catch (IOException x)
        {
            System.err.println ("Exception in client: "+x);
            x.printStackTrace ();
        }
        catch (XmlRpcException x)
        {
            System.err.println ("Server reported error: "+x);
        }
        catch (Exception other)
        {
            System.err.println ("Exception in Benchmark client: "+other);
        }
        int millis = (int)(System.currentTimeMillis () - start);
        checkout (calls, errors, millis);
    }

    private synchronized void checkout (int calls, int errors, int millis)
    {
        clients--;
        gCalls += calls;
        gErrors += errors;
        System.err.println ("Benchmark thread finished: "+calls + " calls, "+
                errors + " errors in "+millis + " milliseconds.");
        if (clients == 0)
        {
            System.err.println ("");
            System.err.println ("Benchmark result: "+
                    (1000 * gCalls / millis) + " calls per second.");
        }
    }

    public static void main (String args[]) throws Exception
    {
        if (args.length > 0 && args.length < 3)
        {
            url = args[0];
            XmlRpc.setKeepAlive (true);
            if (args.length == 2)
                XmlRpc.setDriver (args[1]);
            new Benchmark ();
        }
        else
        {
            System.err.println ("Usage: java org.apache.xmlrpc.Benchmark URL [SAXDriver]");
        }
    }

}
