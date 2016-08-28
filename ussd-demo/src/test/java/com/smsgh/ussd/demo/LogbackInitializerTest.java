/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.demo;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aaron
 */
public class LogbackInitializerTest {
    
    public LogbackInitializerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of normalizeLogDir method, of class LogbackInitializer.
     */
    @Test
    public void testNormalizeLogDir() {
        System.out.println("normalizeLogDir");
        
        String configDir = "";
        String[] expResult = {null, "", ""};
        String[] result = LogbackInitializer.normalizeLogDir(configDir);
        assertArrayEquals(expResult, result);
        
        configDir = "/home/aaron";
        expResult = new String[]{null, "", "/home/aaron"};
        result = LogbackInitializer.normalizeLogDir(configDir);
        assertArrayEquals(expResult, result);
        
        configDir = "C:\\Users\\Aaron";
        expResult = new String[]{null, "C:", "\\Users\\Aaron"};
        result = LogbackInitializer.normalizeLogDir(configDir);
        assertArrayEquals(expResult, result);
        
        configDir = "sysProp.user.home";
        expResult = new String[]{"s", "user.home", ""};
        result = LogbackInitializer.normalizeLogDir(configDir);
        assertArrayEquals(expResult, result);
        
        configDir = "env.HOME/Desktop";
        expResult = new String[]{"e", "HOME", "/Desktop"};
        result = LogbackInitializer.normalizeLogDir(configDir);
        assertArrayEquals(expResult, result);
        assertArrayEquals(expResult, result);
        
        configDir = "Desktop";
        expResult = new String[]{null, "Desktop", ""};
        result = LogbackInitializer.normalizeLogDir(configDir);
        assertArrayEquals(expResult, result);
    }    
}
