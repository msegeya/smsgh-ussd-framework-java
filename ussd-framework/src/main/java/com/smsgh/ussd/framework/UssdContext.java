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
 * @author Aaron Baffour-Awuah
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

    /**
     * Gets the request that is currently being processed.
     * @return current request.
     */
    public UssdRequest getRequest() {
        return request;
    }
    
    /**
     * Gets the key used to keep track of the route-controller/action pair
     * - used to handle requests.
     * 
     * @return key under which next route is kept. 
     */
    public String getNextRouteKey() {
        return String.format("%s.%s", request.getSessionId(), "NextRoute");
    }
    
    /**
     * Gets the key for the data associated with the session of 
     * this context's request.
     * 
     * @return key for session's data bag.
     */
    public String getDataBagKey() {
        return String.format("%s.%s", request.getSessionId(), "DataBag");
    }

    /**
     * Inserts into session store the next route - controller/action pair.
     * @param nextRoute the route to store.
     */
    public void sessionSetNextRoute(String nextRoute) {
        store.setValue(getNextRouteKey(), nextRoute);
        
    }

    /**
     * Removes next route and data associated with 
     * session of this context's request.
     */
    public void sessionClose() {
        store.deleteValue(getNextRouteKey());
        store.deleteHash(getDataBagKey());
    }

    /**
     * Determines whether or not some session data exists for the session of
     * a context's request.
     * @return true or false if session of this context's request exists or not
     * respectively.
     */
    public boolean sessionExists() {
        return store.valueExists(getNextRouteKey());
    }

    /**
     * Uses the data used to create a context to create a controller and
     * execute a specified action.
     * 
     * @return response from action executed.
     */
    public UssdResponse sessionExecuteAction() {
        // Get route which has the controller and action to execute.
        String route = store.getValue(getNextRouteKey());
        if (route == null) {
            throw new FrameworkException("No route was found.");
        }
        
        // Split route up to get the controller and action.
        int periodIndex = route.lastIndexOf('.');
        if (periodIndex == -1) {
            throw new FrameworkException("Invalid route format. "
                    + "Must be \"SomeController.action\"." +
                "Current route is: " + route);
        }
        String controllerName = route.substring(0, periodIndex);
        String actionName = route.substring(periodIndex+1);
        
        // First trying loading class using only given controller's name.
        Class controllerClass = null;
        try {
            controllerClass = Class.forName(controllerName);
        }
        catch (ClassNotFoundException ex) { }
        
        // If class was not found, then it may be because it has not
        // been qualified with its package. Use given controller packages
        // to attempt class loading again.
        if (controllerClass == null) {
            
            // Use this string builder to store all the classes
            // we tried loading. If we eventually don't find the
            // controller class, we'll let user know what classes
            // were attempted.
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
                    
                    // Have support for omission of "Controller" suffix from
                    // controller class names.
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
            
            // If controller class wasn't found, throw exception with
            // details of classes we tried loading.
            if (controllerClass == null) {                    
                throw new RuntimeException(String.format(
                        "Class \"%s\" could not be found. Tried to load "
                                + "the following classes: %s",
                        controllerName, attemptedClasses));
            }
        }
        
        // Check that controller class subclasses UssdController.
        if (!UssdController.class.isAssignableFrom(controllerClass)) {
            throw new RuntimeException(String.format("Class \"%s\" does not "
                    + "subclass \"%s\"", controllerClass, 
                    UssdController.class.getName()));
        }
        
        // Get action method.
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
        
        // Create controller instance. Possible problems include
        // non-public class, non-public constructor, absence of
        // no-arg constructor or error in constructor.
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
        
        // Initialize newly created controller.
        controller.setRequest(request);
        controller.setDataBag(dataBag);
        controller.setControllerData(controllerData);
        controller.init();
        
        // Now invoke action on controller.
        Object someObj;
        try {
            someObj = action.invoke(controller);
        }
        catch (InvocationTargetException ex) {
            // InvocationTargetException doesn't have
            // any interesting message. Thus pull out
            // the exception it wraps and throw that
            // instead.
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
        
        // Check that return value of action is not null, and is
        // a UssdResponse instance.
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
    
    /**
     * Gives opportunity to SessionStore implementation to release any
     * resources it might be holding on to.
     */
    public void close() {
        store.close();
    }
}
