/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import java.util.Date;

/**
 *
 * @author aaron
 */
public interface UssdRequestListener {
    
    void requestEntering(Date startTime, UssdRequest ussdRequest);
    
    void responseLeaving(Date startTime, UssdRequest ussdRequest,
            Date endTime, UssdResponse ussdResponse);
}
