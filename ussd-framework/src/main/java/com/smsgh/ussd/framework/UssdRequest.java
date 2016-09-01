/*
 * (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author aaron
 */
public class UssdRequest {
    public static final String REQUEST_TYPE_INITIATION = "Initiation";
    
    public static final String REQUEST_TYPE_RESPONSE = "Response";
    
    public static final String REQUEST_TYPE_RELEASE = "Release";
    
    public static final String REQUEST_TYPE_TIMEOUT = "Timeout";
    
    @SerializedName("Mobile")
    private String mobile;
    
    @SerializedName("SessionId")
    private String sessionId;
    
    @SerializedName("ServiceCode")
    private String serviceCode;
    
    @SerializedName("Type")
    private String type;
    
    @SerializedName("Message")
    private String message;
    
    @SerializedName("Operator")
    private String operator;
    
    @SerializedName("Sequence")
    private int sequence;
    
    @SerializedName("ClientState")
    private String clientState;
    
    private transient boolean autoDialOriginated = false;
    
    public static UssdRequest fromJson(String json) {
        Gson gson = new Gson();
        UssdRequest instance = gson.fromJson(json, UssdRequest.class);
        return instance;
    }

    public boolean isAutoDialOriginated() {
        return autoDialOriginated;
    }

    public void setAutoDialOriginated(boolean autoDialOriginated) {
        this.autoDialOriginated = autoDialOriginated;
    }

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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getClientState() {
        return clientState;
    }

    public void setClientState(String clientState) {
        this.clientState = clientState;
    }
    
    public String getTrimmedMessage() {
        if (message == null) {
            return null;
        }
        return message.trim();
    }

    @Override
    public String toString() {
        return "UssdRequest{" + "mobile=" + mobile + ", sessionId=" + 
                sessionId + ", serviceCode=" + serviceCode + ", type=" + 
                type + ", message=" + message + ", operator=" + operator + 
                ", sequence=" + sequence + ", clientState=" + clientState + 
                ", autoDialOriginated=" + autoDialOriginated + '}';
    }    
}
