package com.pb.sawdust.io;

import com.pb.sawdust.util.exceptions.RuntimeIOException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The {@code StreamConnector} class provides static convenience methods for connecting {@code InputStream}s to {@code OutputStreams}.
 *
 * @author crf <br/>
 *         Started May 16, 2011 12:11:56 PM
 */
public class StreamConnector {
    /**
     * The default buffer size to use when connecting streams.
     */
    public static int DEFAULT_BUFFER_SIZE = 4096;

    /**
     * Connect an input stream to an output stream, reading at most a specified number of bytes. This method will not
     * close either stream.
     *
     * @param is
     *        The input stream to read from.
     *
     * @param os
     *        The output stream to write to.
     *
     * @param maxBytes
     *        The maximum number of bytes to read/write.
     *
     * @param bufferSize
     *        The size of the buffer to use when connecting the streams.
     *
     * @return the number of bytes transferred between the streams.
     */
    public static long connectStreams(InputStream is, OutputStream os, long maxBytes, int bufferSize) {
        long counter = 0;
        byte[] buffer = new byte[bufferSize];
        int lastRead;
        int nextRead = (int) Math.min(bufferSize,maxBytes);
        try {
            while ((counter < maxBytes) && (lastRead = is.read(buffer,0,nextRead)) != -1) {
                os.write(buffer,0,lastRead);
                counter += lastRead;
                nextRead = (int) Math.min(bufferSize,maxBytes-counter);
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        return counter;
    }

    /**
     * Connect an input stream to an output stream using a the default buffer size, reading at most a specified number of
     * bytes. This method will not close either stream.
     *
     * @param is
     *        The input stream to read from.
     *
     * @param os
     *        The output stream to write to.
     *
     * @param maxBytes
     *        The maximum number of bytes to read/write.
     *
     * @return the number of bytes transferred between the streams.
     */
    public static long connectStreams(InputStream is, OutputStream os, long maxBytes) {
        return connectStreams(is,os,maxBytes,DEFAULT_BUFFER_SIZE);
    }

    /**
     * Connect an input stream to an output stream, reading as much from the input stream as possible. That is, data will
     * continue to be read from the input stream until it signals that it has no more data, or it (or the output stream)
     * throws an exception. This method will not close either stream.
     *
     * @param is
     *        The input stream to read from.
     *
     * @param os
     *        The output stream to write to.
     *
     * @param bufferSize
     *        The size of the buffer to use when connecting the streams.
     *
     * @return the number of bytes transferred between the streams.
     */
    public static long connectStreams(InputStream is, OutputStream os, int bufferSize) {
        long counter = 0;
        byte[] buffer = new byte[bufferSize];
        int lastRead;
        try {
            while ((lastRead = is.read(buffer)) != -1) {
                os.write(buffer,0,lastRead);
                counter += lastRead;
            }
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
        return counter;
    }

    /**
     * Connect an input stream to an output stream using a the default buffer size, reading as much from the input stream
     * as possible. That is, data will continue to be read from the input stream until it signals that it has no more data,
     * or it (or the output stream) throws an exception. This method will not close either stream.
     *
     * @param is
     *        The input stream to read from.
     *
     * @param os
     *        The output stream to write to.
     *
     * @return the number of bytes transferred between the streams.
     */
    public static long connectStreams(InputStream is, OutputStream os) {
        return connectStreams(is,os,DEFAULT_BUFFER_SIZE); 
    }
}
