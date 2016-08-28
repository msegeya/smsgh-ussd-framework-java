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
import java.util.Arrays;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Context listener for configuring Logback and hence SLF4J.
 * <p>
 * Uses context parameter "com.smsgh.logging.config.path"
 * (defaults to /WEB-INF/logback.xml) to find logback configuration.
 * Use context parameter "com.smsgh.logging.log.dirs" 
 * (defaults to user's home directory) to set
 * the "log-dir" property in the logback configuration file.
 * 
 * @author Aaron Baffour-Awuah
 */
public class LogbackInitializer implements ServletContextListener {
    private static final String CONTEXT_PARAM_CONFIG_PATH = 
            "com.smsgh.logging.config.path";
    private static final String CONTEXT_PARAM_LOG_DIRS = 
            "com.smsgh.logging.log.dirs";
    private static final String SYS_PROP_PREFIX = "sysProp.";
    private static final String ENV_VAR_PREFIX = "env.";
    
    private static final String DEFAULT_CONFIG_PATH = "/WEB-INF/logback.xml";
    private static final String DEFAULT_LOG_DIR = ".logback";
    
    private static final Logger LOG = LoggerFactory.getLogger(
            LogbackInitializer.class);
    
    public void contextInitialized(ServletContextEvent sce) {
        String configPath = sce.getServletContext(
            ).getInitParameter(CONTEXT_PARAM_CONFIG_PATH);
        
        LOG.info("Value for context parameter \"{}\": {}",
                CONTEXT_PARAM_CONFIG_PATH, configPath);
        if (configPath == null) {
            configPath = DEFAULT_CONFIG_PATH;
        }
        
        String logDirPathsParam = sce.getServletContext(
            ).getInitParameter(CONTEXT_PARAM_LOG_DIRS);
        
        LOG.info("Value for context parameter \"{}\": {}",
                CONTEXT_PARAM_LOG_DIRS, logDirPathsParam);
        
        File logDir = null;
        if (logDirPathsParam != null) {
            String[] logDirPaths = StringUtils.split(logDirPathsParam, ", ");
            LOG.info("Split log dirs: {}", Arrays.toString(logDirPaths));
            for (String logDirPath : logDirPaths) {
                // Substitute any sysProp. or env. prefix
                String[] splitLogDirPath = normalizeLogDir(logDirPath);
                if (splitLogDirPath[0] != null) {
                    String type = splitLogDirPath[0];
                    String firstSegment = splitLogDirPath[1];
                    String remainder = splitLogDirPath[2];
                    if (SYS_PROP_PREFIX.startsWith(type)) {
                        String sysPropName = firstSegment;
                        try {
                            String sysProp = System.getProperty(sysPropName);
                            if (sysProp != null && !sysProp.isEmpty()) {
                                logDir = new File(sysProp + remainder);
                                break;
                            }
                            LOG.warn("Could not find this system property: " +
                                    sysPropName);
                        }
                        catch (Exception ex) {
                            LOG.warn("Problem with fetching this system "
                                    + "property: " + sysPropName, ex);
                        }
                    }
                    else if (ENV_VAR_PREFIX.startsWith(type)) {
                        String envVarName = firstSegment;
                        try {
                            String envVar = System.getenv(envVarName);
                            if (envVar != null && !envVar.isEmpty()) {
                                logDir = new File(envVar + remainder);
                                break;
                            }
                            LOG.warn("Could not find this environment variable: " +
                                    envVarName);
                        }
                        catch (Exception ex) {
                            LOG.warn("Problem with fetching this environment "
                                    + "variable: " + envVarName, ex);
                        }
                    }
                    else {
                        LOG.warn("Unknown log dir type: " + type);
                    }
                }
                else {
                    logDir = new File(logDirPath);
                    break;
                }
            }
        }
        
        if (logDir == null) {
            logDir = new File(System.getProperty("user.home"), DEFAULT_LOG_DIR);
        }
        
        LOG.info("Using the following config path: " + configPath);
        LOG.info("Using the following log directory: " + 
                logDir.getAbsolutePath());
        
        // Assumes SLF4J is bound to logback in the current environment
        // which is the case here.
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {            
            // Proceed with configuration as suggested in 
            // Chapter 3: Configuration in Logback documentation.
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            // Call context.reset() to clear any previous configuration, 
            // e.g. default configuration of printing to console.
            context.reset();
            context.putProperty("log-dir", logDir.getAbsolutePath());
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
    
    public static String[] normalizeLogDir(String configDir) {
        String type = null, firstSegment = configDir, remainder = "";
        int slashIndex = configDir.indexOf("/");
        if (slashIndex == -1) {
            slashIndex = configDir.indexOf("\\");
        }
        if (slashIndex != -1) {
            firstSegment = configDir.substring(0, slashIndex);
            remainder = configDir.substring(slashIndex);
        }
        if (firstSegment.startsWith(SYS_PROP_PREFIX)) {
            type = "s";
            firstSegment = firstSegment.substring(SYS_PROP_PREFIX.length());
        }
        else if (firstSegment.startsWith(ENV_VAR_PREFIX)) {
            type = "e";
            firstSegment = firstSegment.substring(ENV_VAR_PREFIX.length());
        }
        return new String[]{ type, firstSegment, remainder };
    }
}
