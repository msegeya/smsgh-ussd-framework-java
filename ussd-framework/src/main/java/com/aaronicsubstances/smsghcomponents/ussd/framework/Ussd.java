package com.aaronicsubstances.smsghcomponents.ussd.framework;

import com.aaronicsubstances.smsghcomponents.ussd.framework.stores.LoggingStore;
import com.aaronicsubstances.smsghcomponents.ussd.framework.stores.SessionStore;
import java.io.*;
import java.net.*;
import java.util.*;

public class Ussd {

    public static UssdResponse process(SessionStore store, UssdRequest request,
            String initiationController, String initiationAction,
            Map<String, String> data, LoggingStore loggingStore,
            String arbitraryLogData) {
        return processRequest(store, request, initiationController,
                initiationAction, data, loggingStore, arbitraryLogData, false);
    }

    public static UssdResponse processRequest(SessionStore store, UssdRequest request,
            String initiationController, String initiationAction,
            Map<String, String> data, LoggingStore loggingStore,
            String arbitraryLogData, boolean close) {
        Date startTime = new Date();
        if (data == null) {
            data = new HashMap<String, String>();
        }
        UssdContext context = new UssdContext(store, request, data);
        UssdResponse response;
        Date endTime;
        try {
            switch (request.getRequestType()) {
                case INITIATION:
                    String route = String.format("%s.%s", initiationController,
                            initiationAction);
                    response = processInitiationRequest(context, route);
                    break;
                default:
                    response = processContinuationRequest(context);
                    break;
            }
        }
        catch (Throwable t) {
            response = UssdResponse.render(t.getMessage());
            response.setException(t);
        }
        finally {
            if (close) {
                context.close();
            }
            endTime = new Date();
        }
        return response;
    }

    private static UssdResponse processInitiationRequest(UssdContext context, 
            String route) {
        context.sessionClose();
        context.sessionSetNextRoute(route);
        return processContinuationRequest(context);
    }

    private static UssdResponse processContinuationRequest(UssdContext context) {
        while (true) {
            boolean exists = context.sessionExists();
            if (!exists)
            {
                throw new RuntimeException("Session does not exist.");
            }
            UssdResponse response = context.sessionExecuteAction();
            if (!response.isRelease())
            {
                context.sessionSetNextRoute(response.getNextRoute());
            }
            if (response.isRedirect())
            {
                continue;
            }
            return response;
        }
    }
}
