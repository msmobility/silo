package com.pb.sawdust.util.exceptions;

/**
 * The {@code RuntimeInterruptedException} is a wrapper for the checked exception {@code InterruptedException} and its
 * descendents. It is intended to be used to wrap the checked exception to allow it to be thrown without being declared.
 * This lower the programming burden (and perhaps annoyance) though it does raise the possiblity that some recoverable
 * exceptions may be allowed to percolate to runtime (and that they will not be indicated clearly in an ide environment).
 * Therefore, the use of this class should be clearly documented.
 *
 * @author crf <br/>
 *         Started: Jul 15, 2008 5:17:54 PM
 */
public class RuntimeInterruptedException extends RuntimeWrappingException  {
    private static final long serialVersionUID = -6824856078304608235L;

    /**
     * Construction specifying the {@code InterruptedException} this class is wrapping.
     *
     * @param interruptedException
     *        The exception for this class to wrap.
     */
    public RuntimeInterruptedException(InterruptedException interruptedException) {
        super(interruptedException);
    }

    /**
     * Constructs a new runtime interrupted exception with <code>null</code> as its detail message.
     */
    public RuntimeInterruptedException() {
        super();
    }

    /**
     * Constructs a new runtime interrupted exception with the specified detail message.
     *
     * @param message
     *        The detail message.
     */
    public RuntimeInterruptedException(String message) {
        super(message);
    }
}
