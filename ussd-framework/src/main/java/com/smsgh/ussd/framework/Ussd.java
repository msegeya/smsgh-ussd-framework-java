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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point to USSD framework.
 * 
 * @author Aaron Baffour-Awuah
 */
public class Ussd  {
    
    /**
     * The limit on the number of internal redirects that may
     * occur before an error is thrown.
     */
    public static final int MAX_REDIRECT_COUNT = 5;
    
    /**
     * The root url of the online USSD Simulator for setting up
     * the Access-Control-Allow-Origin header needed by
     * the cross-origin nature of the USSD simulator to work.
     */
    public static final String DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN =
            "http://apps.smsgh.com";
    
    /**
     * The sliding expiration time for entries in the session store. Set
     * to 10 seconds more than the USSD API session timeout.
     */
    public static final int SESSION_TIMEOUT_MILLIS = 70000;
    
    /**
     * Character encoding of USSD requests and responses.
     */
    public static final String DEFAULT_ENCODING = "utf-8";
    
    // Session store.
    private static final SessionStore DEFAULT_STORE;    
    private SessionStore store;
    
    // Controller- and action-related fields.
    private String[] controllerPackages;
    private String initiationController;
    private String initiationAction;    
    private Map<String, Object> controllerData;
    
    // Log- and error- related fields.
    private String errorMessage;
    private UssdRequestListener requestListener;
    
    // Root url of USSD simulator, just in case simulator is 
    // moved to a different url, so developers don't have to wait 
    // for an update to the default value.
    private String accessControlAllowOrigin;
    
    // Enables auto dialling 
    private int maxAutoDialDepth;
    
    private static final Logger LOG = LoggerFactory.getLogger(Ussd.class);

    /**
     * Create default store as singleton.
     */
    static {
        DEFAULT_STORE = new InMemorySessionStore(SESSION_TIMEOUT_MILLIS);
    }
    
    /**
     * Creates a new Ussd instance with its session store set to the
     * in-memory session store singleton.
     */
    public Ussd() {
        store = getDefaultStore();
    }
    
    /**
     * Gets the singleton in-memory session store set on newly created
     * Ussd instances.
     * 
     * @return in-memory session store singleton 
     */
    public static SessionStore getDefaultStore() {
        return DEFAULT_STORE;
    }
    
    /**
     * Gets the session store used by the Ussd instance.
     * 
     * @return instance's session store. 
     */
    public SessionStore getStore() {
        return store;
    }

    /**
     * Sets the session store used by the Ussd instance. Use this to
     * override the default in-memory session store singleton.
     * 
     * @param store new session store for the instance.
     * 
     * @return this instance to enable chaining of property mutator methods.
     * 
     * @exception java.lang.IllegalArgumentException if store argument is
     * null.
     */
    public Ussd store(SessionStore store) {
        if (store == null) {
            throw new IllegalArgumentException("\"store\" "
                    + "argument cannot be null.");
        }
        this.store = store;
        return this;
    }

    /**
     * Gets the packages in which the ussd controller to handle
     * the current request is located. This enables the setting of 
     * controller names with unqualified names.
     * 
     * @return packages for qualifying controller name.
     * 
     * @see #controllerPackages(java.lang.String[]) 
     */
    public String[] getControllerPackages() {
        return controllerPackages;
    }

    /**
     * Sets the packages used to qualify the ussd controller to handle the
     * current request. By default nothing (null) is set.
     * <p>
     * The controller package can be partial. So a controller whose full name is 
     * com.smsgh.ussd.demo.controllers.MainController can have its
     * package specified as
     * <ol>
     *  <li>com
     *  <li>com.smsgh
     *  <li>com.smsgh.ussd
     *  <li>com.smsgh.ussd.demo
     *  <li>com.smsgh.ussd.demo.controllers
     * </ol>
     * 
     * @param controllerPackages the packages for qualifying controller name.
     * 
     * @return this instance to enable chaining of property mutator methods.
     */
    public Ussd controllerPackages(String[] controllerPackages) {
        this.controllerPackages = controllerPackages;
        return this;
    }

    /**
     * Gets the name of the controller which handles the very first request
     * in a ussd session.
     * 
     * @return name of controller used to handle initiation requests
     * 
     * @see #initiationController(java.lang.String)
     */
    public String getInitiationController() {
        return initiationController;
    }

    /**
     * Sets the name of the controller used to handle ussd initiation 
     * requests. The name must point to a public concrete subclass
     * of {@link UssdController} with a public no-arg constructor.
     * <p>
     * Leveraging the controllerPackages property, some or all
     * of the components of a controller's package can be left out. Also,
     * if the name of the controller ends with the "Controller" suffix, this
     * suffix can be left out.
     * <p>
     * So a controller whose full name is 
     * com.smsgh.ussd.demo.controllers.MainController can be specified as
     * <ol>
     *  <li>com.smsgh.ussd.demo.controllers.MainController
     *  <li>smsgh.ussd.demo.controllers.Main
     *  <li>ussd.demo.controllers.MainController
     *  <li>demo.controllers.Main
     *  <li>controllers.MainController
     *  <li>MainController
     *  <li>Main
     * </ol>
     * In all but the first name however, the controllerPackages property must
     * be set or else the controller will not be found.
     * 
     * @param initiationController name of controller.
     * 
     * @return this instance to enable chaining of property mutator methods.
     */
    public Ussd initiationController(String initiationController) {
        this.initiationController = initiationController;
        return this;
    }

    /**
     * Gets the name of the action that will handle initiation requests.
     * 
     * @return name of action handling initiation requests.
     * 
     * @see #initiationAction(java.lang.String)
     */
    public String getInitiationAction() {
        return initiationAction;
    }

    /**
     * Sets the name of the action that will handle initiation requests.
     * This action must be a public no-arg method of the controller class
     * that returns a {@link UssdResponse} instance.
     * 
     * @param initiationAction the name of the action handling
     * ussd initiation requests.
     * 
     * @return this instance to enable chaining of property mutators.
     */
    public Ussd initiationAction(String initiationAction) {
        this.initiationAction = initiationAction;
        return this;
    }

    /**
     * Gets any data that will be passed unmodified to controllers. By
     * default nothing (null) is sent.
     * 
     * @return data set on newly created controllers.
     */
    public Map<String, Object> getControllerData() {
        return controllerData;
    }

    /**
     * Hook for the ussd application developer to pass
     * any data to his/her ussd controllers for every request.
     * 
     * @param controllerData custom data for ussd controllers.
     * 
     * @return this instance to enable chaining of property mutators.
     */
    public Ussd controllerData(Map<String, Object> controllerData) {
        this.controllerData = controllerData;
        return this;
    }

    /**
     * Gets the error message returned by the ussd request processing
     * pipeline if an exception is raised at any point. The default is null,
     * which triggers the default behaviour of sending exception.toString()
     * to the phone.
     * 
     * @return error message to be used in place of default behaviour, or
     * null which indicates default behaviour.
     * 
     * @see #errorMessage(java.lang.String) 
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Hook for overriding the default exception handling behaviour of
     * setting the response message to the result of calling toString()
     * on the exception which was raised.
     * <p>
     * It is expected that this property is left as null during development
     * so exception messages are seen immediately, but set to something
     * more meaningful in a production environment so users don't see
     * cryptic error messages.
     * 
     * @param errorMessage error message to send as ussd response if an
     * exception occurs, or null to stick to default behaviour.
     * 
     * @return this instance to enable chaining of property mutators.
     */
    public Ussd errorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    /**
     * Gets the maximum allowable depth for automatically handling 
     * auto dial requests. See {@link Ussd#maxAutoDialDepth(int)} for
     * an explanation to this very useful feature. Default value is 0, which
     * means auto dial requests are not handled specially.
     * 
     * @return maximum allowable depth.
     * 
     */
    public int getMaxAutoDialDepth() {
        return maxAutoDialDepth;
    }

    /**
     * Sets the maximum allowable depth for treating auto dial requests 
     * specially. Auto dial requests are initiation requests containing
     * extra messages after the ussd code, e.g. *714*2*4# when the ussd
     * code is *714*2#. 
     * <p>
     * In this example, the extra message is "4", and it is possible that the
     * phone user wants be shown the screen that would have appeared if
     * he/she had texted a "4" in response to texting *714*2#.
     * <p>
     * Auto dial support enables ussd application developers to handle this
     * scenario with little effort on their side, by setting this
     * property to a positive value. By default this value is 0, thus
     * disabling auto dialing until it is explicitly requested.
     * <p>
     * Auto dialing however, can be ended during request processing by
     * setting the autoDialOn property of the {@link UssdResponse} to false
     * During validation for example, autoDialOn
     * must be turned off upon invalid input, or else the phone user will 
     * have his/her requests wrongly interpreted.
     * <p>
     * For example, if the ussd code is *110# and the phone user texts
     * *110*9*2# (so that "9" and "2" are the extra messages), and "9"
     * is invalid, the auto dialing session must be broken (and the "2"
     * discarded) for the user to retry his/her input at the second screen.
     * Fortunately, the {@link UssdController} caters for the breaking
     * of auto dialing sessions when processing {@link UssdMenu} items and
     * {@link UssdForm} options. Any other validation however (including
     * {@link UssdForm} with no options) must set to false the
     * autoDialOn property on the UssdResponse upon invalid input.
     * 
     * @param maxAutoDialDepth positive value to indicate how far auto dialing
     * is handled, or 0 to disable it.
     * 
     * @return this instance to enable chaining of property mutators.
     * 
     * @see UssdResponse#setAutoDialOn(boolean)
     */
    public Ussd maxAutoDialDepth(int maxAutoDialDepth) {
        if (maxAutoDialDepth < 0) {
            throw new IllegalArgumentException("\"maxAutoDialDepth\" argument "
                    + "cannot be negative: " + maxAutoDialDepth);
        }
        this.maxAutoDialDepth = maxAutoDialDepth;
        return this;
    }

    /**
     * Gets any request listener set to listen for pre-process and 
     * post-process events.
     * 
     * @return custom request listener
     */
    public UssdRequestListener getRequestListener() {
        return requestListener;
    }

    /**
     * Hook for listening to pre-process and post-process events.
     * 
     * @param requestListener custom request listener
     * 
     * @return this instance to enable chaining of property mutators.
     */
    public Ussd requestListener(UssdRequestListener requestListener) {
        this.requestListener = requestListener;
        return this;
    }

    /**
     * Gets the root url set for the online USSD simulator.
     * 
     * @return USSD simulator root url or null to use the default
     * {@link Ussd#DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN}.
     */
    public String getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    /**
     * Sets the root url for the online USSD simulator (or any browser
     * script requiring CORS support for that matter). Default is null,
     * meaning that {@link Ussd#DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN} will
     * be used.
     * <p>
     * This property is not intended to be set by developers, except 
     * when the USSD simulator is moved and 
     * {@link Ussd#DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN} still points to it.
     * Or if another browser script requires it in advanced usage scenarios.
     * 
     * @param accessControlAllowOrigin value for Access-Control-Allow-Origin
     * header or null to use {@link Ussd#DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN}
     * 
     * @return this instance to allow chaining of property mutators.
     */
    public Ussd accessControlAllowOrigin(String accessControlAllowOrigin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
        return this;
    }
    
    /**
     * Alternative point of call for processing USSD requests.
     * <p>
     * Although the SMSGH USSD API works with the POST verb, the
     * USSD simulator works with the OPTIONS verb as well, and thus
     * this method is intended to be the only method to call from
     * {@link javax.servlet.http.HttpServlet#service(
     * javax.servlet.http.HttpServletRequest, 
     * javax.servlet.http.HttpServletResponse)}
     * in order to handle both verbs automatically.
     * <p>
     * Calling this method is actually equivalent to returning
     * {@link Ussd#doPost(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)} or
     * {@link Ussd#doOptions(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)} or false if
     * HTTP verb is POST, OPTIONS or something else respectively.
     * 
     * @param request
     * @param response
     * @return true if and only if request was handled (HTTP verb was POST
     * or OPTIONS)
     * @throws ServletException
     * @throws IOException 
     */
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
        if (doOptions(request, response)) {
            return true;
        }
        
        return doPost(request, response);
    }
    
    /**
     * Implements CORS requirement of USSD simulator.
     * 
     * @param request
     * @param response
     * @return true if and only if HTTP verb is OPTIONS.
     * @throws IOException
     * @throws ServletException 
     */
    public boolean doOptions(HttpServletRequest request,
            HttpServletResponse response) 
            throws IOException, ServletException {
        if (request == null) {
            throw new IllegalArgumentException("\"request\" argument cannot be "
                    + "null");
        }
        if (response == null) {
            throw new IllegalArgumentException("\"response\" argument cannot be "
                    + "null");
        }
        
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
    
    /**
     * Main point of call for processing USSD requests.
     * <p>
     * The SMSGH USSD API works with the POST verb only, 
     * and thus this method is intended to be the only method to
     * call from
     * {@link javax.servlet.http.HttpServlet#doPost(
     * javax.servlet.http.HttpServletRequest, 
     * javax.servlet.http.HttpServletResponse)}
     * in order to handle the POST verb.
     * 
     * @param request
     * @param response
     * @return true if and only if HTTP verb is POST.
     * @throws IOException
     * @throws ServletException
     */
    public boolean doPost(HttpServletRequest request,
            HttpServletResponse response) 
            throws IOException, ServletException {
        if (request == null) {
            throw new IllegalArgumentException("\"request\" argument cannot be "
                    + "null");
        }
        if (response == null) {
            throw new IllegalArgumentException("\"response\" argument cannot be "
                    + "null");
        }
        
        if (!request.getMethod().equalsIgnoreCase("POST")) {
            return false;
        }
        
        UssdRequest ussdRequest = fetchRequest(request);
        
        UssdResponse ussdResponse = processRequest(ussdRequest);
        
        sendResponse(ussdResponse, response);
        
        return true;
    }
    
    /**
     * Hook for subclasses to override how {@link UssdRequest} instances are
     * parsed from the HTTP request.
     * 
     * @param request HTTP response
     * @return parsed {@link UssdRequest} instance.
     * @throws ServletException
     * @throws IOException 
     */
    protected UssdRequest fetchRequest(HttpServletRequest request)
            throws ServletException, IOException {
        String ussdRequestJson = IOUtils.toString(request.getInputStream(),
                DEFAULT_ENCODING);
        UssdRequest ussdRequest = UssdRequest.fromJson(ussdRequestJson);
        return ussdRequest;
    }
    
    /**
     * Hook for subclasses to override how {@link UssdResponse} instances
     * are sent in the HTTP response.
     * 
     * @param ussdResponse result of ussd request processing.
     * @param response HTTP response
     * @throws ServletException
     * @throws IOException 
     */
    protected void sendResponse(UssdResponse ussdResponse,
            HttpServletResponse response)
            throws ServletException, IOException {        
        String ussdResponseJson = UssdResponse.toJson(ussdResponse);
        byte[] ussdResponseJsonBytes = ussdResponseJson.getBytes(
                DEFAULT_ENCODING);
        
        // This CORS header is necessary for Ussd Simulator at
        // http://apps.smsgh.com/UssdSimulator/ to work with
        // a Ussd app under test.
        response.setHeader("Access-Control-Allow-Origin",
                accessControlAllowOrigin != null ? accessControlAllowOrigin :
                        DEFAULT_ACCESS_CONTROL_ALLOW_ORIGIN);
        
        response.setContentType("application/json;charset=" +
                DEFAULT_ENCODING);
        response.setContentLength(ussdResponseJsonBytes.length);
        response.getOutputStream().write(ussdResponseJsonBytes);
    }

    private UssdResponse processRequest(UssdRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("\"request\" argument "
                    + "cannot be null");
        }
        
        if (requestListener != null) {
            requestListener.requestEntering(request);
        }
        UssdContext context = new UssdContext(store, request, 
                controllerPackages, controllerData);
        UssdResponse response = null;
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
            logResponse(response);
        }
        finally {
            if (response.isRelease()) {
                context.sessionClose();
            }
            context.close();
        }
        if (requestListener != null) {
            requestListener.responseLeaving(request, response);
        }
        return response;
    }

    /**
     * Processes initiation requests and implements auto dial
     * mechanism.
     * 
     * @param context
     * @param route
     * @return 
     */
    private UssdResponse processInitiationRequest(UssdContext context, 
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
            
            String extra = initiationMessage.substring(
                    extraIndex + serviceCode.length());
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
                ussdRequest.setAutoDialIndex(i+1);
                ussdResponse = processContinuationRequest(context);
                if (ussdResponse.isRelease() || !ussdResponse.isAutoDialOn()) {
                    break;
                }
                i++;
            }
        }
        return ussdResponse;
    }

    private UssdResponse processContinuationRequest(UssdContext context) {
        logRequest(context.getRequest());
        UssdResponse response = null;
        int redirectCount = 0;
        while (redirectCount < MAX_REDIRECT_COUNT && response == null) {
            boolean exists = context.sessionExists();
            if (!exists)
            {
                throw new SessionNotFoundException("Session does not exist.");
            }
            response = context.sessionExecuteAction();
            logResponse(response);
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
    
    private void logRequest(UssdRequest request) {
        LOG.debug("New ussd request: {}", request);
    }
    
    private void logResponse(UssdResponse response) {
        // Log exception if any occurred.
        if (response.getException() != null) {
            LOG.error("An error occured during ussd request processing.", 
                    response.getException());
        }
        LOG.debug("Sending ussd response: {}", response);
    }
}
