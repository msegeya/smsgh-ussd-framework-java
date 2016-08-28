/*
 * (c) 2016. SMSGH
 */
package com.smsgh.ussd.demo;

import com.smsgh.ussd.framework.Ussd;
import com.smsgh.ussd.framework.UssdRequest;
import com.smsgh.ussd.framework.UssdRequestListener;
import com.smsgh.ussd.framework.UssdResponse;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstrates example setup of a servlet for the Ussd framework. The framework
 * needs a servlet to kickstart the ussd request processing.
 * 
 * @author Aaron Baffour-Awuah
 */
public class UssdServlet extends HttpServlet {
   
    private static final Logger LOG = LoggerFactory.getLogger(
            UssdServlet.class);
    
    /**
     * Overridden to handle both POST and OPTIONS verbs. Ussd API uses only POST,
     * but Ussd simulator at http://apps.smsgh.com/UssdSimulator/ uses OPTIONS
     * as well.
     * <p>
     * If you don't intend to use the simulator, then you can
     * override the doPost method instead, in which case you don't have to
     * check for the return value of Ussd.service() to do anything special.
     * 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Set up a Ussd object to service request.
        // Only the first controller and action are required.
        // A singleton instance of InMemorySessionStore will be used to
        // track ussd sessions and expire them when no longer in use.
        Ussd ussd = new Ussd()
                .initiationController("controllers.Main")
                .initiationAction("start");
        
        // Optional, but almost certainly something you would want to
        // do to avoid specifying controller names in full each time,
        // and making it laborious to refactor the names of packages.
        ussd.controllerPackages(new String[]{
            getClass().getPackage().getName()
        });
        
        // Optionally set up a request listener for logging requests,
        // processing errors and responses.
        ussd.requestListener(new UssdRequestListener() {
            
            public void requestEntering(UssdRequest ussdRequest) {
                LOG.debug("New ussd request: {}", ussdRequest);
            }

            public void responseLeaving(UssdRequest ussdRequest, 
                    UssdResponse ussdResponse) {
                // Log exception if any occurred.
                if (ussdResponse.getException() != null) {
                    LOG.error("An error occured during ussd request processing.", 
                            ussdResponse.getException());
                }
                LOG.debug("Sending ussd response: {}", ussdResponse);
            }
        });
        
        // By calling upon service, POST and OPTIONS requests are handled.
        // The USSD Simulator requires setting up CORS handling. Good news is
        // that all this is handled for you.
        boolean handled = ussd.service(req, resp);
        if (!handled) {
            // If ussd's service didn't handle request, then it means HTTP method was
            // neither POST nor OPTIONS. In this case it fallbacks back to
            // what it would have done: looking for doGet(), doPost(), etc.
            super.service(req, resp);
        }
    }
}
