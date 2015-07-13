package com.pb.sawdust.util.exceptions;

import java.io.PrintWriter;
import java.io.PrintStream;

/**
 * The {@code RuntimeWrappingException} is used to wrap exceptions in a {@code RuntimeException}. Though this class
 * extends {@code RuntimeException}, all of the methods in that class are passed through to the underlying wrapped
 * exception. For {@code RuntimeWrappingException} which are constructed without explicitly stating an exception to wrap,
 * a {@code RuntimeExcpeption} is constructed internally to serve as the wrapped exception.
 *
 * @author crf <br/>
 *         Started: Jun 26, 2008 3:46:00 PM
 */
public class RuntimeWrappingException extends RuntimeException {
    private static final long serialVersionUID = -3318651409995677996L;

    protected final Exception wrappedException;

    /**
     * Construction specifying the exception this class is wrapping.
     *
     * @param wrappedException
     *        The exception for this class to wrap.
     */
    public RuntimeWrappingException(Exception wrappedException) {
        this.wrappedException = wrappedException;
    }

    /**
     * Constructs a new runtime wrapping exception with <code>null</code> as its detail message. The cause is not
     * initialized, and may subsequently be initialized by a call to {@link #initCause}. This exception will use itself
     * as its wrapped exception (though, functionally, it acts as if it had no wrapped exception).
     */
    public RuntimeWrappingException() {
        super();
        wrappedException = this;
    }

    /**
     * Constructs a new runtime wrapping exception with the specified detail message. The cause is not initialized, and
     * may subsequently be initialized by a call to {@link #initCause}. This exception will use itself as its wrapped
     * exception (though, functionally, it acts as if it had no wrapped exception).
     *
     * @param message
     *        The detail message.
     */
    public RuntimeWrappingException(String message) {
        super(message);
        wrappedException = this;
    }

    /**
     * Constructs a new runtime wrapping exception with the specified detail message and cause. This exception will use
     * itself as its wrapped exception (though, functionally, it acts as if it had no wrapped exception).
     *
     * @param message
     *        The detail message.
     *
     * @param cause
     *        The cause of the exception. (A {@code null} value is permitted, and indicates that the cause is
     *        nonexistent or unknown.)
     */
    public RuntimeWrappingException(String message, Throwable cause) {
        super(message,cause);
        wrappedException = this;
    }

    /**
     * Constructs a new runtime wrapping exception with the specified cause. This exception will use itself as its
     * wrapped exception (though, functionally, it acts as if it had no wrapped exception).
     *
     * @param cause
     *        The cause of the exception. (A {@code null} value is permitted, and indicates that the cause is
     *        nonexistent or unknown.)
     *
     * @param dummyForCause
     *        A dummy variable which is not used, but which differentiates this method from
     *        {@code RuntimeWrappingException(Exception)}.
     */
    public RuntimeWrappingException(Throwable cause, boolean dummyForCause) {
        super(cause);
        wrappedException = this;
    }

    /**
     * Get the exception that this class is wrapping.
     *
     * @return the exception that this class wraps.
     */
    public Exception getWrappedException() {
        return wrappedException;
    }

    public String getMessage() {
        return wrappedException == this ? super.getMessage() : wrappedException.getMessage();
    }

    public String getLocalizedMessage() {
        return wrappedException == this ? super.getLocalizedMessage() : wrappedException.getLocalizedMessage();
    }

    public Throwable getCause() {
        return wrappedException == this ? super.getCause() : wrappedException.getCause();
    }

    public Throwable initCause(Throwable cause) {
        return wrappedException == this ? super.initCause(cause) : wrappedException.initCause(cause);
    }

    public String toString() {
        return wrappedException == this ? super.toString() : wrappedException.toString();
    }

    public void printStackTrace() {
        if (wrappedException == this)
            super.printStackTrace();
        else
            wrappedException.printStackTrace();
    }

    public void printStackTrace(PrintStream ps) {
        if (wrappedException == this)
            super.printStackTrace(ps);
        else
            wrappedException.printStackTrace(ps);
    }

    public void printStackTrace(PrintWriter pw) {
        if (wrappedException == this)
            super.printStackTrace(pw);
        else
            wrappedException.printStackTrace(pw);
    }

    public StackTraceElement[] getStackTrace() {
        return wrappedException == this ? super.getStackTrace() : wrappedException.getStackTrace();
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        if (wrappedException == this)
            super.setStackTrace(stackTrace);
        else
            wrappedException.setStackTrace(stackTrace);
    }
}
