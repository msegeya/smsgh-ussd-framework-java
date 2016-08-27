package com.aaronicsubstances.smsghcomponents.ussd.framework;

import com.aaronicsubstances.smsghcomponents.ussd.framework.stores.LoggingStore;
import com.aaronicsubstances.smsghcomponents.ussd.framework.stores.SessionStore;
import com.google.gson.Gson;
import java.io.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

public class Ussd {

    public static boolean process(
            HttpServletRequest request, HttpServletResponse response,
            SessionStore store,
            String initiationController, String initiationAction,
            Map<String, String> data, LoggingStore loggingStore,
            String arbitraryLogData) throws ServletException, IOException {
        
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            // These CORS headers are necessary for Ussd Simulator at
            // http://apps.smsgh.com/UssdSimulator/ to work with
            // a Ussd app under test.
            response.setHeader("Access-Control-Allow-Origin",
                    "http://apps.smsgh.com");
            response.setHeader("Access-Control-Allow-Methods",
                    "POST, OPTIONS");
            response.setHeader("Access-Control-Max-Age",
                    "1");
            String accessControlRequestHeaders = request.getHeader(
                    "Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.setHeader("Access-Control-Allow-Headers",
                        accessControlRequestHeaders);
            }
            return true;
        }
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            return false;
        }
        String ussdRequestJson = IOUtils.toString(request.getInputStream(),
                "utf-8");
        Gson gson = new Gson();
        UssdRequest ussdRequest = gson.fromJson(ussdRequestJson, 
                UssdRequest.class);
        UssdResponse ussdResponse = processRequest(store, ussdRequest, 
                initiationController, initiationAction, data, loggingStore, 
                arbitraryLogData, false);
        String ussdResponseJson = gson.toJson(ussdResponse);
        byte [] ussdResponseJsonBytes = ussdResponseJson.getBytes("UTF-8");
        
        // This CORS header is necessary for Ussd Simulator at
        // http://apps.smsgh.com/UssdSimulator/ to work with
        // a Ussd app under test.
        response.setHeader("Access-Control-Allow-Origin",
                "http://apps.smsgh.com");
        
        response.setContentType("application/json;charset=utf-8");
        response.setContentLength(ussdResponseJsonBytes.length);
        response.getOutputStream().write(ussdResponseJsonBytes);
        return true;
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
