/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

/**
 *
 * Enables framework users to be notified of pre- and post- ussd request
 * processing events through {@link Ussd#requestListener(
 * com.smsgh.ussd.framework.UssdRequestListener) }.
 * 
 * @author Aaron Baffour-Awuah
 */
public interface UssdRequestListener {
    
    /**
     * Called just before ussd request is processed.
     * @param ussdRequest ussd request to be processed.
     */
    void requestEntering(UssdRequest ussdRequest);
    
    /**
     * Called just after ussd response is obtained.
     * @param ussdRequest the ussd request which was processed.
     * @param ussdResponse the ussd response obtained.
     */
    void responseLeaving(UssdRequest ussdRequest, UssdResponse ussdResponse);
}
