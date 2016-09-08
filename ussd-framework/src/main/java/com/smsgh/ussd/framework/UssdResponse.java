/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents ussd responses to be sent to SMSGH.
 * 
 * @author Aaron Baffour-Awuah
 */
public class UssdResponse {
    public static final String RESPONSE_TYPE_RESPONSE = "Response";
    
    public static final String RESPONSE_TYPE_RELEASE = "Release";
    
    @SerializedName("Type")
    private String type;
    
    @SerializedName("Message")
    private String message;
    
    @SerializedName("ClientState")
    private String clientState;
    
    private transient Throwable exception;
    private transient String nextRoute;
    private transient boolean redirect;
    private transient boolean autoDialOn = true;

    /**
     * Creates new UssdResponse instance.
     */
    public UssdResponse() {
    }

    /**
     * Gets the type of the ussd response. SMSGH uses this to determine whether
     * or not session has ended.
     * @return type of ussd response
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the ussd response. 
     * @param type type of ussd response. SMSGH expects it to be one of
     * the RESPONSE_TYPE_* constants of this class.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets ussd response message.
     * @return ussd response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets ussd response message.
     * @param message ussd response message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Currently not used by framework. See SMSGH USSD documentation 
     * for details.
     * @return 
     */
    public String getClientState() {
        return clientState;
    }

    public void setClientState(String clientState) {
        this.clientState = clientState;
    }

    /**
     * Gets any exception which occured during the processing of a 
     * ussd request.
     * @return ussd request processing error or null if no such 
     * error occured.
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Sets any exception which occured during the processing of a ussd
     * request.
     * @param exception a ussd request processing error
     */
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

    /**
     * Gets whether any ongoing auto dial processing should be continued or
     * broken.
     * @return true (by default) to continue any ongoing auto dial processing; 
     * false to break it.
     */
    public boolean isAutoDialOn() {
        return autoDialOn;
    }

    /**
     * Sets whether or not auto dial processing should be continued.
     * For example, framework uses this property to end auto dial when 
     * input validation fails for menus and form options.
     * @param autoDialOn true to continue auto dial processing, false
     * to end it.
     */
    public void setAutoDialOn(boolean autoDialOn) {
        this.autoDialOn = autoDialOn;
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

    /**
     * @inheritDoc 
     */
    @Override
    public String toString() {
        return "UssdResponse{" + "type=" + type + ", message=" + message + 
                ", clientState=" + clientState + ", exception=" + exception + 
                ", nextRoute=" + nextRoute + ", redirect=" + redirect + 
                ", autoDialOn=" + autoDialOn + '}';
    }
}
