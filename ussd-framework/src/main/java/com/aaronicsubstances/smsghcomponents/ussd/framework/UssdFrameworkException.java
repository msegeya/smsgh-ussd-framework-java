/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

/**
 *
 * @author aaron
 */
public class UssdFrameworkException extends RuntimeException {

    /**
     * Creates a new instance of <code>UssdFrameworkException</code> without detail
     * message.
     */
    public UssdFrameworkException() {
    }

    /**
     * Constructs an instance of <code>UssdFrameworkException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UssdFrameworkException(String msg) {
        super(msg);
    }

    public UssdFrameworkException(Throwable cause) {
        super(cause);
    }

    public UssdFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
