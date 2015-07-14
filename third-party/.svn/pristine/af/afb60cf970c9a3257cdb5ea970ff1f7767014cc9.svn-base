package com.pb.sawdust.io;

import java.io.Closeable;

/**
 * The {@code RuntimeCloseable} interface is essentially a replica of the {@code java.io.Closeable} interface, with the
 * checked exceptions removed. It also implements {@code AutoCloseable} so as to make those classes implementing it accessible
 * to new Java features.
 *
 * @author crf <br/>
 *         Started: Jul 3, 2008 5:02:59 PM
 */
public interface RuntimeCloseable extends Closeable,AutoCloseable {

    /**
     * Closes this stream and releases any system resources associated with it. If the stream is already closed then
     * invoking this method has no effect. This is identical to the method prescribed in {@code java.io.Closeable},
     * except it has no checked exception. 
     */
    public void close();
}
