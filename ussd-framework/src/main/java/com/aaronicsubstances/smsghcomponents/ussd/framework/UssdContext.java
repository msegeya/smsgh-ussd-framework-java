/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import com.aaronicsubstances.smsghcomponents.ussd.framework.stores.SessionStore;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @author aaron
 */
public class UssdContext {
    private SessionStore store;
    private UssdRequest request;
    private String[] controllerPackages;
    private Map<String, Object> controllerData;
    private UssdDataBag dataBag;
    
    public UssdContext(SessionStore store, UssdRequest request, 
            String[] controllerPackages,
            Map<String, Object> controllerData) {
        if (store == null) {
            throw new IllegalArgumentException("\"store\" argument "
                    + "cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("\"request\" argument "
                    + "cannot be null");
        }
        this.store = store;
        this.request = request;
        this.controllerPackages = controllerPackages;
        this.controllerData = controllerData;
        this.dataBag = new UssdDataBag(store, getDataBagKey());
    }
    
    public String getNextRouteKey() {
        return String.format("%s.%s", request.getSessionId(), "NextRoute");
    }
    
    public String getDataBagKey() {
        return String.format("%s.%s", request.getSessionId(), "DataBag");
    }

    public void sessionSetNextRoute(String nextRoute) {
        store.setValue(getNextRouteKey(), nextRoute);
        
    }

    public void sessionClose() {
        store.deleteValue(getNextRouteKey());
        store.deleteHash(getDataBagKey());
    }

    public boolean sessionExists() {
        return store.valueExists(getNextRouteKey());
    }

    public UssdResponse sessionExecuteAction() {
        String route = store.getValue(getNextRouteKey());
        if (route == null) {
            throw new FrameworkException("No route was found.");
        }
        int periodIndex = route.lastIndexOf('.');
        if (periodIndex == -1) {
            throw new FrameworkException("Invalid route format. "
                    + "Must be \"SomeController.action\"." +
                "Current route is: " + route);
        }
        String controllerName = route.substring(0, periodIndex);
        String actionName = route.substring(periodIndex+1);
        Class controllerClass = null;
        try {
            controllerClass = Class.forName(controllerName);
        }
        catch (ClassNotFoundException ex) { }
        if (controllerClass == null) {
            StringBuilder attemptedClasses = new StringBuilder();
            attemptedClasses.append(' ').append(controllerName);
            if (controllerPackages != null) {
                for (String controllerPackage : controllerPackages) {
                    String fullControllerName = controllerPackage + '.' +
                            controllerName;
                    try {
                        controllerClass = Class.forName(fullControllerName);
                        break;
                    }
                    catch (ClassNotFoundException ex) {}
                    attemptedClasses.append(", ");
                    attemptedClasses.append(fullControllerName);
                }
            }
            if (controllerClass == null) {                    
                throw new RuntimeException(String.format(
                        "\"%s\" class could not be found. Tried to load "
                                + "the following classes: %s",
                        controllerName, attemptedClasses));
            }
        }
        
        if (!UssdController.class.isAssignableFrom(controllerClass)) {
            throw new RuntimeException(String.format("%s is not a subclass of "
                    + "%s", controllerClass, UssdController.class));
        }
        Method action;
        try {
            action = controllerClass.getMethod(actionName);
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException(String.format(
                    "%s class does not have action %s.",
                    controllerClass, actionName));
        }
        
        UssdController controller;        
        try {
            controller = (UssdController)controllerClass.newInstance();
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(String.format("Failed to create "
                    + "instance of %s class. Does class have a "
                    + "public no-arg constructor?", controllerClass));
        }
        catch (InstantiationException ex) {
            throw new RuntimeException(String.format("Failed to create "
                    + "instance of %s class. Does class have a "
                    + "public no-arg constructor?", controllerClass), ex);
        }
        
        controller.setRequest(request);
        controller.setDataBag(dataBag);
        controller.setControllerData(controllerData);
        controller.init();
        
        Object someObj;
        try {
            someObj = action.invoke(controller);
        }
        catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            throw new RuntimeException(ex);
        }
        if (!(someObj instanceof UssdResponse)) {
            throw new RuntimeException(String.format("%s action did not return "
                    + "an instance of %s class, but rather returned %s",
                    actionName, UssdResponse.class, someObj));
        }
        UssdResponse response = (UssdResponse)someObj;
        return response;
    }
}
