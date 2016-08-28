/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import com.smsgh.ussd.framework.stores.SessionStore;
import java.lang.reflect.InvocationTargetException;
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
                    if (!fullControllerName.endsWith("Controller")) {
                        fullControllerName = fullControllerName + "Controller";
                        try {
                            controllerClass = Class.forName(fullControllerName);
                            break;
                        }
                        catch (ClassNotFoundException ex) {}
                        attemptedClasses.append(", ");
                        attemptedClasses.append(fullControllerName);
                    }
                }
            }
            if (controllerClass == null) {                    
                throw new RuntimeException(String.format(
                        "Class \"%s\" could not be found. Tried to load "
                                + "the following classes: %s",
                        controllerName, attemptedClasses));
            }
        }
        
        if (!UssdController.class.isAssignableFrom(controllerClass)) {
            throw new RuntimeException(String.format("Class \"%s\" does not "
                    + "subclass \"%s\"", controllerClass, 
                    UssdController.class.getName()));
        }
        Method action;
        try {
            action = controllerClass.getMethod(actionName);
        }
        catch (NoSuchMethodException ex) {
            throw new RuntimeException(String.format(
                    "Class \"%s\" does not have a public no-arg action "
                            + "named \"%s\".",
                    controllerClass.getName(), actionName));
        }
        
        UssdController controller;        
        try {
            controller = (UssdController)controllerClass.newInstance();
        }
        catch (IllegalAccessException ex) {
            throw new RuntimeException(String.format("Failed to create "
                    + "instance of class \"%s\". Is class a public class having a "
                    + "public no-arg constructor?", controllerClass.getName()));
        }
        catch (InstantiationException ex) {
            throw new RuntimeException(String.format("Failed to create "
                    + "instance of class \"%s\". Is class a public class having a "
                    + "public no-arg constructor?", controllerClass.getName()));
        }
        
        controller.setRequest(request);
        controller.setDataBag(dataBag);
        controller.setControllerData(controllerData);
        controller.init();
        
        Object someObj;
        try {
            someObj = action.invoke(controller);
        }
        catch (InvocationTargetException ex) {
            Throwable t = ex.getTargetException();
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new RuntimeException(t);
        }
        catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            throw new RuntimeException(ex);
        }
        if (!(someObj instanceof UssdResponse)) {
            throw new RuntimeException(String.format("Action \"%s.%s()\" "
                    + "did not return an instance of class \"%s\", "
                    + "but rather returned: %s{%s}",
                    controllerClass.getName(),
                    actionName, UssdResponse.class.getName(),
                    someObj != null ? someObj.getClass().getName() : null, 
                    someObj));
        }
        UssdResponse response = (UssdResponse)someObj;
        return response;
    }
}
