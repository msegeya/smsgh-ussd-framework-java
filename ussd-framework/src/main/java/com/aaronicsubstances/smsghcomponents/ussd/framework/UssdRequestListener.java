/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

/**
 *
 * @author aaron
 */
public interface UssdRequestListener {
    
    void requestEntering(UssdRequest ussdRequest);
    
    void responseLeaving(UssdRequest ussdRequest, UssdResponse ussdResponse);
}
