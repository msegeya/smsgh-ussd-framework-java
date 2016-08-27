package com.aaronicsubstances.smsghcomponents.ussd.framework;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Ussd {
    public static final int MAX_REDIRECT_COUNT = 10;
    
    public static boolean process(
            HttpServletRequest request, HttpServletResponse response,
            UssdConfig ussdConfig) 
            throws ServletException, IOException {
        if (ussdConfig == null) {
            throw new IllegalArgumentException("\"ussdConfig\" argument cannot be "
                    + "null");
        }
        if (request == null) {
            throw new IllegalArgumentException("\"request\" argument cannot be "
                    + "null");
        }
        if (response == null) {
            throw new IllegalArgumentException("\"response\" argument cannot be "
                    + "null");
        }
        String accessControlAllowOrigin = "http://apps.smsgh.com";
        if (ussdConfig.getAccessControlAllowOrigin() != null) {
            accessControlAllowOrigin = ussdConfig.getAccessControlAllowOrigin();
        }
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            // These CORS headers are necessary for Ussd Simulator at
            // http://apps.smsgh.com/UssdSimulator/ to work with
            // a Ussd app under test.
            response.setHeader("Access-Control-Allow-Origin",
                    accessControlAllowOrigin);
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
        byte[] ussdRequestJsonBytes = ByteStreams.toByteArray(
                request.getInputStream());
        String ussdRequestJson = new String(ussdRequestJsonBytes,
                "utf-8");
        Gson gson = new Gson();
        UssdRequest ussdRequest = gson.fromJson(ussdRequestJson, 
                UssdRequest.class);
        UssdResponse ussdResponse = processRequest(ussdRequest, ussdConfig);
        String ussdResponseJson = gson.toJson(ussdResponse);
        byte [] ussdResponseJsonBytes = ussdResponseJson.getBytes("UTF-8");
        
        // This CORS header is necessary for Ussd Simulator at
        // http://apps.smsgh.com/UssdSimulator/ to work with
        // a Ussd app under test.
        response.setHeader("Access-Control-Allow-Origin",
                accessControlAllowOrigin);
        
        response.setContentType("application/json;charset=utf-8");
        response.setContentLength(ussdResponseJsonBytes.length);
        response.getOutputStream().write(ussdResponseJsonBytes);
        return true;
    }

    public static UssdResponse processRequest(UssdRequest request,
            UssdConfig ussdConfig) {
        if (request == null) {
            throw new IllegalArgumentException("\"request\" argument "
                    + "cannot be null");
        }
        if (ussdConfig == null) {
            throw new IllegalArgumentException("\"ussdConfig\" argument "
                    + "cannot be null");
        }
        if (ussdConfig.getStore() == null) {
            throw new IllegalArgumentException("\"ussdConfig\" argument cannot "
                    + "have null for \"store\" property.");
        }
        
        Date startTime = new Date();
        if (ussdConfig.getRequestListener() != null) {
            ussdConfig.getRequestListener().requestEntering(startTime, request);
        }
        UssdContext context = new UssdContext(ussdConfig.getStore(), request, 
                ussdConfig.getControllerPackages(), 
                ussdConfig.getControllerData());
        UssdResponse response;
        Date endTime;
        try {
            switch (request.getRequestType()) {
                case INITIATION:
                    if (ussdConfig.getInitiationController() == null) {
                        throw new IllegalArgumentException(
                                "\"ussdConfig\" argument cannot "
                                + "have null for \"initiationController\" "
                                        + "property.");
                    }
                    if (ussdConfig.getInitiationAction() == null) {
                        throw new IllegalArgumentException(
                                "\"ussdConfig\" argument cannot "
                                + "have null for \"initiationAction\" "
                                        + "property.");  
                    }
                    String route = String.format("%s.%s",
                            ussdConfig.getInitiationController(),
                            ussdConfig.getInitiationAction());
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
            endTime = new Date();
        }
        if (ussdConfig.getRequestListener() != null) {
            ussdConfig.getRequestListener().responseLeaving(
                    startTime, request, endTime, response);
        }
        return response;
    }

    private static UssdResponse processInitiationRequest(UssdContext context, 
            String route) {
        context.sessionSetNextRoute(route);
        return processContinuationRequest(context);
    }

    private static UssdResponse processContinuationRequest(UssdContext context) {
        UssdResponse response = null;
        int redirectCount = 0;
        while (redirectCount < MAX_REDIRECT_COUNT && response == null) {
            boolean exists = context.sessionExists();
            if (!exists)
            {
                throw new RuntimeException("Session does not exist.");
            }
            response = context.sessionExecuteAction();
            if (!response.isRelease())
            {
                context.sessionSetNextRoute(response.getNextRoute());
            }
            if (response.isRedirect())
            {
                response = null;
                redirectCount++;
            }
        }
        if (response == null) {
            throw new RuntimeException(String.format(
                    "Failed to get final ussd response after %d redirect%s.",
                    redirectCount, redirectCount == 1 ? "" : "s"));
        }
        return response;
    }
}
