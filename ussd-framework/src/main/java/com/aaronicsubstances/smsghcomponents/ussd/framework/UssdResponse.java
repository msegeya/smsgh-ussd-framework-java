/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author aaron
 */
public class UssdResponse {
    private String type;
    private String message;
    private String clientState;
    
    private transient Throwable exception;
    private transient String nextRoute;
    private transient boolean redirect;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientState() {
        return clientState;
    }

    public void setClientState(String clientState) {
        this.clientState = clientState;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public String getNextRoute() {
        return nextRoute;
    }

    public void setNextRoute(String nextRoute) {
        this.nextRoute = nextRoute;
    }

    public boolean isRedirect() {
        return redirect;
    }

    public void setRedirect(boolean redirect) {
        this.redirect = redirect;
    }
    
    public boolean isRelease() {
        return StringUtils.isBlank(nextRoute);
    }
    
    public static UssdResponse render(String message) {
        return render(message, null);
    }
    
    public static UssdResponse render(String message, String nextRoute) {
        String type = StringUtils.isBlank(nextRoute)
                ? UssdResponseTypes.RELEASE.toString()
                : UssdResponseTypes.RESPONSE.toString();
        UssdResponse response = new UssdResponse();
        response.setType(type);
        response.setMessage(message);
        response.setNextRoute(nextRoute);
        return response;
    }
    
    public static UssdResponse redirect(String nextRoute) {
        UssdResponse response = new UssdResponse();
        response.setNextRoute(nextRoute);
        response.setRedirect(true);
        return response;
    }
    
    public static enum UssdResponseTypes {
        RESPONSE, RELEASE,
    }
}
