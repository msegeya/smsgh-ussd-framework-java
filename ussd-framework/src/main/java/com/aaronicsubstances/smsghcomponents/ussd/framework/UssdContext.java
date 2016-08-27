/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import com.aaronicsubstances.smsghcomponents.ussd.framework.stores.SessionStore;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author aaron
 */
public class UssdContext {
    private SessionStore store;
    private UssdRequest request;
    private Map<String, String> data;
    private UssdDataBag dataBag;
    
    public UssdContext(SessionStore store, UssdRequest request, 
            Map<String, String> data) {
        this.store = store;
        this.request = request;
        this.data = data;
        this.dataBag = new UssdDataBag(store, getDataBagKey());
    }
    
    public String getNextRouteKey() {
        return String.format("%s.%s", request.getMobile(), "NextRoute");
    }
    
    public String getDataBagKey() {
        return String.format("%s.%s", request.getMobile(), "DataBag");
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
        int periodIndex = route.lastIndexOf('.');
        if (periodIndex == -1) {
            throw new RuntimeException("Invalid route format. "
                    + "Must be \"SomeController.Action\"." +
                "Current route is " + route);
        }
        String controllerName = route.substring(0, periodIndex);
        String actionName = route.substring(periodIndex+1);
        Class controllerClass;
        try {
            controllerClass = Class.forName(controllerName);
        }
        catch (ClassNotFoundException ex) {
            boolean prefixMayHelp = controllerName.indexOf('.') != -1;
            if (!prefixMayHelp) {
                throw new RuntimeException(controllerName + 
                        " could not be found.");
            }
            // Add places which were searched.
            throw new RuntimeException(controllerName + 
                    " could not be found.");
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
        controller.setData(data);
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

    public void close() {
        store.close();
    }
}
