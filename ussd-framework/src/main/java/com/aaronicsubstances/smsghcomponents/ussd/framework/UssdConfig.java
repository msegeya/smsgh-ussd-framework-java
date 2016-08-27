/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import com.aaronicsubstances.smsghcomponents.ussd.framework.stores.SessionStore;
import java.util.Map;

/**
 *
 * @author aaron
 */
public class UssdConfig {
    private SessionStore store;
    private String[] controllerPackages;
    private String initiationController;
    private String initiationAction;
    private Map<String, Object> controllerData;
    private UssdRequestListener requestListener;
    private String accessControlAllowOrigin;

    public SessionStore getStore() {
        return store;
    }

    public UssdConfig store(SessionStore store) {
        this.store = store;
        return this;
    }

    public String[] getControllerPackages() {
        return controllerPackages;
    }

    public UssdConfig controllerPackages(String[] controllerPackages) {
        this.controllerPackages = controllerPackages;
        return this;
    }

    public String getInitiationController() {
        return initiationController;
    }

    public UssdConfig initiationController(String initiationController) {
        this.initiationController = initiationController;
        return this;
    }

    public String getInitiationAction() {
        return initiationAction;
    }

    public UssdConfig initiationAction(String initiationAction) {
        this.initiationAction = initiationAction;
        return this;
    }

    public Map<String, Object> getControllerData() {
        return controllerData;
    }

    public UssdConfig controllerData(Map<String, Object> controllerData) {
        this.controllerData = controllerData;
        return this;
    }

    public UssdRequestListener getRequestListener() {
        return requestListener;
    }

    public UssdConfig requestListener(UssdRequestListener requestListener) {
        this.requestListener = requestListener;
        return this;
    }

    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public UssdConfig accessControlAllowOrigin(String accessControlAllowOrigin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
        return this;
    }
}
