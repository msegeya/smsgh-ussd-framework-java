/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

/**
 *
 * @author aaron
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
