/*
 * (c) 2016. Aaronic Substances.
 */
package com.aaronicsubstances.smsghcomponents.ussd.demo;

import com.aaronicsubstances.smsghcomponents.ussd.framework.Ussd;
import com.aaronicsubstances.smsghcomponents.ussd.framework.UssdConfig;
import com.aaronicsubstances.smsghcomponents.ussd.framework.UssdRequest;
import com.aaronicsubstances.smsghcomponents.ussd.framework.UssdRequestListener;
import com.aaronicsubstances.smsghcomponents.ussd.framework.UssdResponse;
import com.aaronicsubstances.smsghcomponents.ussd.framework.stores.InMemorySessionStore;
import com.aaronicsubstances.smsghcomponents.ussd.framework.stores.SessionStore;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aaron
 */
public class UssdServlet extends HttpServlet implements UssdRequestListener {
    private static final int SESSION_STORE_TIMEOUT_MILLIS = 65000;
    private SessionStore ussdSessionStore;
    
    private static final Logger LOG = LoggerFactory.getLogger(UssdServlet.class);
    
    @Override
    public void init() {
        ussdSessionStore = new InMemorySessionStore(
                SESSION_STORE_TIMEOUT_MILLIS);
    }
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        UssdConfig ussdConfig = new UssdConfig()
                .store(ussdSessionStore)
                .controllerPackages(new String[]{
                    "com.aaronicsubstances.smsghcomponents.ussd.demo"})
                .initiationController("controllers.MainController")
                .initiationAction("start")
                .requestListener(this);
        if (!Ussd.process(req, resp, ussdConfig)) {
            super.service(req, resp);
        }
    }

    public void requestEntering(Date startTime, UssdRequest ussdRequest) {
        LOG.debug("New ussd request: {}", ussdRequest);
    }

    public void responseLeaving(Date startTime, UssdRequest ussdRequest,
            Date endTime, UssdResponse ussdResponse) {
        // Check for exceptions. If one occured, then the message of
        // the ussdResponse was set from the exception.
        // You may set a different message before response is sent.
        if (ussdResponse.getException() != null) {
            LOG.error("An error occured during ussd request processing.", 
                    ussdResponse.getException());
        }
        LOG.debug("Sending ussd response: {}", ussdResponse);
    }
}
