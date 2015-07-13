package com.pb.sawdust.util.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * The {@code MultiException} is a {@code RuntimeException} which can wrap many excpetions into a single container. This
 * is useful if a given operation (or series of operations) need to be executed as a block and exceptions captured, rather
 * than thrown, during the block call. When the block operation finishes, the single multi-exception, which encapsulates
 * all caught exceptions, can be thrown (or dealt with).
 *
 * @author crf <br/>
 *         Started: Dec 23, 2008 10:54:45 AM
 */
public class MultiException extends RuntimeException {
    private  static final long serialVersionUID = 2074284845627907954L;

    private final MultiThrowable exceptions;

    /**
     * Constructor specifying the excptions contained by the multi-exception.
     *
     * @param e1
     *        The first exception.
     *
     * @param additionalExceptions
     *        Any addition exceptions.
     */
    public MultiException(Exception e1, Exception ... additionalExceptions) {
        exceptions = new MultiThrowable(e1,additionalExceptions);
        exceptions.setThrowableName("exception");
        exceptions.setMultiThrowableName("MultiException");
    }

    public MultiThrowable getCause() {
        return exceptions.getCause();
    }

    public String getMessage() {
        return exceptions.getMessage();
    }

    public String toString() {
        return exceptions.toString();
    }

    public void printStackTrace(PrintStream s) {
        exceptions.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter s) {
        exceptions.printStackTrace(s);
    }
}
