/*
 *  (c) 2016. Aaronic Substances
 */
package com.aaronicsubstances.smsghcomponents.ussd.demo;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aaron
 */
public class LogbackInitializer implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(
            LogbackInitializer.class);
    
    public void contextInitialized(ServletContextEvent sce) {
        String configPath = "/WEB-INF/logback.xml";
        // assume SLF4J is bound to logback in the current environment
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            File tempDir = (File)sce.getServletContext(
                ).getAttribute(ServletContext.TEMPDIR);
            File logDir = new File(tempDir, "logs");
            context.putProperty("log-dir", logDir.getAbsolutePath());
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            URL configURL = sce.getServletContext().getResource(
                    configPath);
            if (configURL == null) {
                sce.getServletContext().log("Could not find logback "
                        + "config url \"" +  configPath + '"');
            }
            else {
                configurator.doConfigure(configURL);
            }
        }
        catch (MalformedURLException ex) {
            sce.getServletContext().log("Problem encountered with logback "
                    + "config path \"" + configPath + '"', ex);
        }
        catch (JoranException ex) {
            //StatusPrinter will handler this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
        
        LOG.info("Logback initialized.");
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }
}
