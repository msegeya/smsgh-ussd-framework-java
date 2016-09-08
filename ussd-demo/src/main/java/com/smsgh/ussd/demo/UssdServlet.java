/*
 * (c) 2016. SMSGH
 */
package com.smsgh.ussd.demo;

import com.smsgh.ussd.framework.Ussd;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Demonstrates example setup of a servlet for the Ussd framework. The framework
 * needs a servlet to kickstart the ussd request processing.
 * 
 * @author Aaron Baffour-Awuah
 */
public class UssdServlet extends HttpServlet {
   
    /**
     * Handles ussd requests from request parsing to response sending.
     * <p>
     * Works with both the USSD mocker at 
     * <a href="https://github.com/smsgh/ussd-mocker">
     * https://github.com/smsgh/ussd-mocker</a> and the Ussd simulator at 
     * <a href="http://apps.smsgh.com/UssdSimulator/">
     * http://apps.smsgh.com/UssdSimulator/</a>.
     * <p>
     * If you don't intend to use the Ussd simulator and want to use
     * only the USSD mocker, then you can
     * override 
     * {@link javax.servlet.http.HttpServlet#doPost(
     * javax.servlet.http.HttpServletRequest, 
     * javax.servlet.http.HttpServletResponse)} 
     * and call
     * {@link Ussd#doPost(javax.servlet.http.HttpServletRequest, 
     * javax.servlet.http.HttpServletResponse)} instead.
     * 
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        Ussd ussd = new Ussd()
                .controllerPackages(new String[]{"com.smsgh.ussd.demo"})
                .initiationController("controllers.Main")
                .initiationAction("start")
                .maxAutoDialDepth(Integer.MAX_VALUE);        
        ussd.service(req, resp);
    }    
}
