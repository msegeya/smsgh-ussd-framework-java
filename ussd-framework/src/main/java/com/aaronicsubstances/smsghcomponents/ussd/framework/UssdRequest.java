/*
 * (c) 2016. Aaronic Substances
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

/**
 *
 * @author aaron
 */
public class UssdRequest {
    private String mobile;
    private String sessionId;
    private String serviceCode;
    private String type;
    private String message;
    private String operator;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

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

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public UssdRequest.Type getRequestType() {
        if (type == null) {
            return null;
        }
        if (type.equalsIgnoreCase("Initiation")) {
            return UssdRequest.Type.INITIATION;
        }
        if (type.equalsIgnoreCase("Response")) {
            return UssdRequest.Type.RESPONSE;
        }
        if (type.equalsIgnoreCase("Release")) {
            return UssdRequest.Type.RELEASE;
        }
        if (type.equalsIgnoreCase("Timeout")) {
            return UssdRequest.Type.TIMEOUT;
            
        }
        throw new RuntimeException();
    }
    
    public String getTrimmedMessage() {
        if (message == null) {
            return null;
        }
        return message.trim();
    }
    
    public static enum Type {
        INITIATION, RESPONSE, RELEASE, TIMEOUT
    }
}
