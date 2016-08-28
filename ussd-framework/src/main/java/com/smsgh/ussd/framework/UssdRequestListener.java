/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

/**
 *
 * @author aaron
 */
public interface UssdRequestListener {
    
    void requestEntering(UssdRequest ussdRequest);
    
    void responseLeaving(UssdRequest ussdRequest, UssdResponse ussdResponse);
}
