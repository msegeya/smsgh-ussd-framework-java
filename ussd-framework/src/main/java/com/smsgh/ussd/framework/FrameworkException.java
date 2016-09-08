/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

/**
 * Base class of exceptions thrown from framework code.
 * 
 * @author Aaron Baffour-Awuah
 */
public class FrameworkException extends RuntimeException {

    /**
     * Creates a new instance of <code>FrameworkException</code> without detail
     * message.
     */
    public FrameworkException() {
    }

    /**
     * Constructs an instance of <code>FrameworkException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public FrameworkException(String msg) {
        super(msg);
    }

    public FrameworkException(Throwable cause) {
        super(cause);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
