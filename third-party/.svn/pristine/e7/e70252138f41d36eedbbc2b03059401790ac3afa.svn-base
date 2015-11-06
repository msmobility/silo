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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 *
 * @author <a href="mailto:hannes@apache.org">Hannes Wallnoefer</a>
 * @version $Id: ServerInputStream.java,v 1.4 2005/04/22 10:25:57 hgomez Exp $
 */
class ServerInputStream extends InputStream
{
    // bytes remaining to be read from the input stream. This is
    // initialized from CONTENT_LENGTH (or getContentLength()).
    // This is used in order to correctly return a -1 when all the
    // data POSTed was read. If this is left to -1, content length is
    // assumed as unknown and the standard InputStream methods will be used
    private long available = -1;
    private long markedAvailable;

    private BufferedInputStream in;

    /**
     *
     * @param in
     * @param available
     */
    public ServerInputStream(BufferedInputStream in, int available)
    {
        this.in = in;
        this.available = available;
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public int read() throws IOException
    {
        if (available > 0)
        {
            available--;
            return in.read();
        }
        else if (available == -1)
        {
            return in.read ();
        }
        return -1;
    }

    /**
     *
     * @param b
     * @return
     * @throws IOException
     */
    public int read(byte b[]) throws IOException
    {
        return read(b, 0, b.length);
    }

    /**
     *
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    public int read(byte b[], int off, int len) throws IOException
    {
        if (available > 0)
        {
            if (len > available)
            {
                // shrink len
                len = (int) available;
            }
            int read = in.read(b, off, len);
            if (read != -1)
            {
                available -= read;
            }
            else
            {
                available = -1;
            }
            return read;
        }
        else if (available == -1)
        {
            return in.read(b, off, len);
        }
        return -1;
    }

    /**
     *
     * @param n
     * @return
     * @throws IOException
     */
    public long skip(long n) throws IOException
    {
        long skip = in.skip(n);
        if (available > 0)
        {
            available -= skip;
        }
        return skip;
    }

    /**
     *
     * @param readlimit
     */
    public void mark(int readlimit)
    {
        in.mark(readlimit);
        markedAvailable = available;
    }

    /**
     *
     * @throws IOException
     */
    public void reset() throws IOException
    {
        in.reset();
        available = markedAvailable;
    }

    /**
     *
     * @return
     */
    public boolean markSupported()
    {
        return true;
    }
}
