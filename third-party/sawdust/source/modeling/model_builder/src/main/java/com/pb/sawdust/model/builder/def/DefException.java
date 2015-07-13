package com.pb.sawdust.model.builder.def;

/**
 * The {@code DefException} ...
 *
 * @author crf
 *         Started 12/29/11 2:00 PM
 */
public class DefException extends RuntimeException {
    private static final long serialVersionUID = -6526806580465465409L;

    public DefException() {
    }

    public DefException(String message) {
        super(message);
    }

    public DefException(String message, Throwable cause) {
        super(message,cause);
    }

    public DefException(Throwable cause) {
        super(cause);
    }

    public DefException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message,cause,enableSuppression,writableStackTrace);
    }
}
