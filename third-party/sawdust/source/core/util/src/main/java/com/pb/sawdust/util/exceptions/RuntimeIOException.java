package com.pb.sawdust.util.exceptions;

import java.io.*;

/**
 * The {@code RuntimeIOException} is a wrapper for the checked exception {@code IOException} and its descendents. It is
 * intended to be used to wrap the checked exception to allow it to be thrown without being declared. This lower the
 * programming burden (and perhaps annoyance) though it does raise the possiblity that some recoverable exceptions may
 * be allowed to percolate to runtime. Therefore, the use of this class should be clearly documented.
 *
 * @author crf <br/>
 *         Started: May 16, 2008 10:58:39 AM
 */
public class RuntimeIOException extends RuntimeWrappingException {
    private static final long serialVersionUID = -9009082297499647170L;

    /**
     * Construction specifying the {@code IOException} this class is wrapping.
     *
     * @param ioException
     *        The exception for this class to wrap.
     */
    public RuntimeIOException(IOException ioException) {
        super(ioException);
    }

    /**
     * Constructs a new runtime io exception with <code>null</code> as its detail message. The cause is not initialized,
     * and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public RuntimeIOException() {
        super();
    }

    /**
     * Constructs a new runtime io exception with the specified detail message. The cause is not initialized, and may
     * subsequently be initialized by a call to {@link #initCause}.
     *
     * @param message
     *        The detail message.
     */
    public RuntimeIOException(String message) {
        super(message);
    }

    /**
     * Constructs a new runtime io exception with the specified detail message and
     * cause.
     *
     * @param message
     *        The detail message.
     *
     * @param cause
     *        The cause of the exception. (A {@code null} value is permitted, and indicates that the cause is
     *        nonexistent or unknown.)
     */
    public RuntimeIOException(String message, Throwable cause) {
        super(message,cause);
    }

    /**
     * Constructs a new runtime wrapping exception with the specified cause.
     *
     * @param cause
     *        The cause of the exception. (A {@code null} value is permitted, and indicates that the cause is
     *        nonexistent or unknown.)
     *
     * @param dummyForCause
     *        A dummy variable which is not used, but which differentiates this method from
              {@code RuntimeIOException(Exception)}.
     */
    public RuntimeIOException(Throwable cause, boolean dummyForCause) {
        super(cause,dummyForCause);
    }
}
