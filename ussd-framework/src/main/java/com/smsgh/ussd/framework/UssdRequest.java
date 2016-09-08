/*
 * (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Represents ussd request received from SMSGH.
 * 
 * @author Aaron Baffour-Awuah
 */
public class UssdRequest {
    public static final String REQUEST_TYPE_INITIATION = "Initiation";
    
    public static final String REQUEST_TYPE_RESPONSE = "Response";
    
    public static final String REQUEST_TYPE_RELEASE = "Release";
    
    public static final String REQUEST_TYPE_TIMEOUT = "Timeout";

    /**
     * Creates new UssdRequest instance.
     */
    public UssdRequest() {
    }
    
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
    
    private transient boolean autoDialOriginated;
    private transient int autoDialIndex;
    
    public static UssdRequest fromJson(String json) {
        Gson gson = new Gson();
        UssdRequest instance = gson.fromJson(json, UssdRequest.class);
        return instance;
    }
    
    /**
     * Tells whether or not the ussd request was manufactured from
     * a ussd initiation message during auto dial processing.
     * @return true if ussd request originated from auto dial processing 
     * rather than from SMSGH; false if it came directly from SMSGH.
     */
    public boolean isAutoDialOriginated() {
        return autoDialOriginated;
    }

    public void setAutoDialOriginated(boolean autoDialOriginated) {
        this.autoDialOriginated = autoDialOriginated;
    }

    /**
     * Gets the index 
     * @return 
     */
    public int getAutoDialIndex() {
        return autoDialIndex;
    }

    public void setAutoDialIndex(int autoDialIndex) {
        this.autoDialIndex = autoDialIndex;
    }

    /**
     * Gets the phone number of the ussd app user.
     * @return phone number of ussd app user in international format.
     */
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * Gets the unique session id SMSGH associates with each ussd session.
     * <p>
     * Note that session id is 32 characters long.
     * 
     * @return ussd session id.
     */
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the purchased ussd code associated with ussd app. E.g.
     * 714, 714*2, 713*2*10000000
     * @return purchased ussd code through which ussd request arrived.
     */
    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    /**
     * Gets the ussd request type, which is one of the REQUEST_TYPE_*
     * constants of this class.
     * @return ussd request type.
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the ussd request message.
     * @return ussd request message.
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the telecommunications network that the ussd request came from.
     * E.g. vodafone, mtn, tigo, airtel, glo.
     * @return 
     */
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Gets the sequence of the ussd request in its session. The 
     * sequence of the first request is 1.
     * @return sequence of ussd request in ussd session.
     */
    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
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
    
    public String getTrimmedMessage() {
        if (message == null) {
            return null;
        }
        return message.trim();
    }

    /**
     * @inheritDoc 
     */
    @Override
    public String toString() {
        return "UssdRequest{" + "mobile=" + mobile + ", sessionId=" + 
                sessionId + ", serviceCode=" + serviceCode + ", type=" + 
                type + ", message=" + message + ", operator=" + operator + 
                ", sequence=" + sequence + ", clientState=" + clientState + 
                ", autoDialIndex=" + autoDialIndex + '}';
    }
}
