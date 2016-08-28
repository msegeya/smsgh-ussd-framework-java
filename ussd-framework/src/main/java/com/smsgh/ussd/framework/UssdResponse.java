/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author aaron
 */
public class UssdResponse {
    private static final String RESPONSE_TYPE_RESPONSE = "Response";
    
    private static final String RESPONSE_TYPE_RELEASE = "Release";
    
    @SerializedName("Type")
    private String type;
    
    @SerializedName("Message")
    private String message;
    
    @SerializedName("ClientState")
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
        return nextRoute == null;
    }
    
    public static UssdResponse render(String message) {
        return render(message, null);
    }
    
    public static UssdResponse render(String message, String nextRoute) {
        String type = nextRoute == null
                ? RESPONSE_TYPE_RELEASE
                : RESPONSE_TYPE_RESPONSE;
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
    
    public static String toJson(UssdResponse instance) {
        Gson gson = new Gson();
        String json = gson.toJson(instance);
        return json;
    }

    @Override
    public String toString() {
        return "UssdResponse{" + "type=" + type + ", message=" 
                + message + ", clientState=" + clientState + '}';
    }
}
