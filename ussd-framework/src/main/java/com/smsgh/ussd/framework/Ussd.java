/**
 * (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import com.smsgh.ussd.framework.stores.InMemorySessionStore;
import com.smsgh.ussd.framework.stores.SessionStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;

public class Ussd {
    public static final int MAX_REDIRECT_COUNT = 5;
    public static final String DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN =
            "http://apps.smsgh.com";
    public static final String DEFAULT_ENCODING = "utf-8";
    public static final int SESSION_TIMEOUT_MILLIS = 70000;
    
    private static final SessionStore DEFAULT_STORE;
    
    private SessionStore store;
    private String[] controllerPackages;
    private String initiationController;
    private String initiationAction;
    private Map<String, Object> controllerData;
    private String errorMessage;
    private UssdRequestListener requestListener;
    private String accessControlAllowOrigin;
    private String encoding;
    private int maxAutoDialDepth;

    static {
        DEFAULT_STORE = new InMemorySessionStore(SESSION_TIMEOUT_MILLIS);
    }
    
    public Ussd() {
        store = getDefaultStore();
    }
    
    public static SessionStore getDefaultStore() {
        return DEFAULT_STORE;
    }
    
    public SessionStore getStore() {
        return store;
    }

    public Ussd store(SessionStore store) {
        this.store = store;
        return this;
    }

    public String[] getControllerPackages() {
        return controllerPackages;
    }

    public Ussd controllerPackages(String[] controllerPackages) {
        this.controllerPackages = controllerPackages;
        return this;
    }

    public String getInitiationController() {
        return initiationController;
    }

    public Ussd initiationController(String initiationController) {
        this.initiationController = initiationController;
        return this;
    }

    public String getInitiationAction() {
        return initiationAction;
    }

    public Ussd initiationAction(String initiationAction) {
        this.initiationAction = initiationAction;
        return this;
    }

    public Map<String, Object> getControllerData() {
        return controllerData;
    }

    public Ussd controllerData(Map<String, Object> controllerData) {
        this.controllerData = controllerData;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Ussd errorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public int getMaxAutoDialDepth() {
        return maxAutoDialDepth;
    }

    public Ussd maxAutoDialDepth(int maxAutoDialDepth) {
        this.maxAutoDialDepth = maxAutoDialDepth;
        return this;
    }

    public UssdRequestListener getRequestListener() {
        return requestListener;
    }

    public Ussd requestListener(UssdRequestListener requestListener) {
        this.requestListener = requestListener;
        return this;
    }

    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public Ussd accessControlAllowOrigin(String accessControlAllowOrigin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public Ussd encoding(String encoding) {
        this.encoding = encoding;
        return this;
    }
    
    public boolean service(HttpServletRequest request, 
            HttpServletResponse response) 
            throws ServletException, IOException {
        if (request == null) {
            throw new IllegalArgumentException("\"request\" argument cannot be "
                    + "null");
        }
        if (response == null) {
            throw new IllegalArgumentException("\"response\" argument cannot be "
                    + "null");
        }
        if (handlePreflightRequest(request, response)) {
            return true;
        }
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            return false;
        }
        
        UssdRequest ussdRequest = fetchRequest(request);
        
        UssdResponse ussdResponse = processRequest(ussdRequest);
        
        sendResponse(ussdResponse, response);
        
        return true;
    }
    
    public boolean handlePreflightRequest(HttpServletRequest request,
            HttpServletResponse response) 
            throws IOException, ServletException {
        if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return false;
        }
        
        // These CORS headers are necessary for Ussd Simulator at
        // http://apps.smsgh.com/UssdSimulator/ to work with
        // a Ussd app under test.
        response.setHeader("Access-Control-Allow-Origin",
                accessControlAllowOrigin != null ? accessControlAllowOrigin :
                        DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN);
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
    
    public UssdRequest fetchRequest(HttpServletRequest request)
            throws ServletException, IOException {
        String ussdRequestJson = IOUtils.toString(request.getInputStream(),
                encoding != null ? encoding : DEFAULT_ENCODING);
        UssdRequest ussdRequest = UssdRequest.fromJson(ussdRequestJson);
        return ussdRequest;
    }
    
    public void sendResponse(UssdResponse ussdResponse,
            HttpServletResponse response)
            throws ServletException, IOException {        
        String ussdResponseJson = UssdResponse.toJson(ussdResponse);
        byte[] ussdResponseJsonBytes = ussdResponseJson.getBytes(
                encoding != null ? encoding : DEFAULT_ENCODING);
        
        // This CORS header is necessary for Ussd Simulator at
        // http://apps.smsgh.com/UssdSimulator/ to work with
        // a Ussd app under test.
        response.setHeader("Access-Control-Allow-Origin",
                accessControlAllowOrigin != null ? accessControlAllowOrigin :
                        DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN);
        
        response.setContentType("application/json;charset=" +
                (encoding != null ? encoding : DEFAULT_ENCODING));
        response.setContentLength(ussdResponseJsonBytes.length);
        response.getOutputStream().write(ussdResponseJsonBytes);
    }

    public UssdResponse processRequest(UssdRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("\"request\" argument "
                    + "cannot be null");
        }
        if (store == null) {
            throw new IllegalArgumentException("\"store\" property cannot "
                    + "be null.");
        }
        
        if (requestListener != null) {
            requestListener.requestEntering(request);
        }
        UssdContext context = new UssdContext(store, request, 
                controllerPackages, controllerData);
        UssdResponse response;
        try {
            if (request.getType().equalsIgnoreCase(
                    UssdRequest.REQUEST_TYPE_INITIATION)) {
                if (initiationController == null) {
                    throw new IllegalArgumentException(
                            "\"initiationController\" property cannot "
                            + "be null.");
                }
                if (initiationAction == null) {
                    throw new IllegalArgumentException(
                            "\"initiationAction\" property cannot "
                            + "be null.");  
                }
                String route = String.format("%s.%s",
                        initiationController, initiationAction);
                response = processInitiationRequest(context, route);
            }
            else {
                response = processContinuationRequest(context);
            }
        }
        catch (Throwable t) {
            response = UssdResponse.render(errorMessage != null ?
                    errorMessage : t.toString());
            response.setException(t);
        }
        finally {
            context.close();
        }
        if (requestListener != null) {
            requestListener.responseLeaving(request, response);
        }
        return response;
    }

    public UssdResponse processInitiationRequest(UssdContext context, 
            String route) {
        context.sessionSetNextRoute(route);
        UssdResponse ussdResponse = processContinuationRequest(context);
        if (maxAutoDialDepth > 0 && ussdResponse.isAutoDialOn() &&
                !ussdResponse.isRelease()) {
            UssdRequest ussdRequest = context.getRequest();
            String initiationMessage = ussdRequest.getMessage();
            String serviceCode = ussdRequest.getServiceCode();
            
            // To make searching for dial string and split more
            // straightforward, replace # with *.
            initiationMessage = initiationMessage.replaceAll("#", "*");
            serviceCode = serviceCode.replaceAll("#", "*");
            
            int extraIndex = initiationMessage.indexOf(serviceCode);
            if (extraIndex == -1) {
                throw new FrameworkException(String.format(
                        "Service code %s not found in initiation "
                                + "message %s", ussdRequest.getServiceCode(),
                                ussdRequest.getMessage()));
            }
            
            String extra = initiationMessage.substring(extraIndex);
            String[] codes = extra.split("\\*");
            
            // codes may have empty strings if ** was in initiation message.
            // So remove them first.
            ArrayList<String> codeList = new ArrayList<String>();
            for (String code : codes) {
                if (!code.isEmpty()) {
                    codeList.add(code);
                }
            }
            codes = codeList.toArray(new String[codeList.size()]);
            
            int i = 0;
            while (i < maxAutoDialDepth && i < codes.length) {
                String nextMessage = codes[i];
                ussdRequest.setType(UssdRequest.REQUEST_TYPE_RESPONSE);
                ussdRequest.setClientState(ussdResponse.getClientState());
                ussdRequest.setMessage(nextMessage);
                ussdResponse = processContinuationRequest(context);
                if (ussdResponse.isRelease() || !ussdResponse.isAutoDialOn()) {
                    break;
                }
                i++;
            }
        }
        return ussdResponse;
    }

    public UssdResponse processContinuationRequest(UssdContext context) {
        UssdResponse response = null;
        int redirectCount = 0;
        while (redirectCount < MAX_REDIRECT_COUNT && response == null) {
            boolean exists = context.sessionExists();
            if (!exists)
            {
                throw new SessionNotFoundException("Session does not exist.");
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
            throw new FrameworkException(String.format(
                    "Failed to get final ussd response after %d redirect%s.",
                    redirectCount, redirectCount == 1 ? "" : "s"));
        }
        return response;
    }
}
