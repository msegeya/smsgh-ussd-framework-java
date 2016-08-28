/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

/**
 *
 * @author aaron
 */
public class SessionNotFoundException extends RuntimeException {

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
