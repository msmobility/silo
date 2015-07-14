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
import java.util.Vector;

public class AsyncBenchmark 
    implements Runnable
{

    XmlRpcClient client;
    static String url;
    static int clients = 16;
    static int loops = 100;

    int calls = 0;

    int gCalls = 0, gErrors = 0;
    long start;


    public AsyncBenchmark () throws Exception
    {
        client = new XmlRpcClientLite (url);

        Vector args = new Vector ();
        // Some JITs (Symantec, IBM) have problems with several Threads
        // starting all at the same time.
        // This initial XML-RPC call seems to pacify them.
        args.addElement (new Integer (123));
        client.execute ("math.abs", args);

        start = System.currentTimeMillis ();

        for (int i = 0; i < clients; i++)
            new Thread (this).start ();
    }

    public void run ()
    {
        int calls = 0;
        long start = System.currentTimeMillis ();

        for (int i = 0; i < loops; i++)
        {

            Vector args = new Vector ();
            Integer n = new Integer (
                    Math.round ((int)(Math.random () * -1000)));
            args.addElement (n);
            client.executeAsync ("math.abs", args, new Callback (n));
            calls += 1;
        }
        int millis = (int)(System.currentTimeMillis () - start);
        System.err.println ("Benchmark thread finished: "+calls + " calls in "+
                millis + " milliseconds.");
    }

    public static void main (String args[]) throws Exception
    {
        if (args.length > 0 && args.length < 3)
        {
            url = args[0];
            XmlRpc.setKeepAlive (true);
            if (args.length == 2)
                XmlRpc.setDriver (args[1]);
            new AsyncBenchmark ();
        }
        else
        {
            System.err.println ("Usage: java org.apache.xmlrpc.Benchmark URL [SAXDriver]");
        }
    }

    class Callback implements AsyncCallback
    {


        int n;

        public Callback (Integer n)
        {
            this.n = Math.abs (n.intValue());
        }

        public synchronized void handleResult (Object result, URL url,
                String method)
        {
            if (n == ((Integer) result).intValue ())
                gCalls += 1;
            else
                gErrors += 1;
            if (gCalls + gErrors >= clients * loops)
                printStats ();
        }

        public synchronized void handleError (Exception exception,
                URL url, String method)
        {
            System.err.println (exception);
            exception.printStackTrace ();
            gErrors += 1;
            if (gCalls + gErrors >= clients * loops)
                printStats ();
        }

        public void printStats ()
        {
            System.err.println ("");
            System.err.println (gCalls + " calls, "+gErrors + " errors in "+
                    (System.currentTimeMillis() - start) + " millis");
            System.err.println ( (1000 * (gCalls + gErrors) /
                    (System.currentTimeMillis() - start)) + " calls per second");
        }

    }


}
