/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

/**
 *
 * Exception thrown when a session is not found for a ussd continuation
 * message.
 * 
 * @author Aaron Baffour-Awuah
 */
public class SessionNotFoundException extends FrameworkException {

    /**
     * Creates a new instance of <code>SessionNotFoundException</code> without
     * detail message.
     */
    public SessionNotFoundException() {
    }

    /**
     * Constructs an instance of <code>SessionNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public SessionNotFoundException(String msg) {
        super(msg);
    }
}
