/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
public class UssdUtilsTest {
    
    public UssdUtilsTest() {
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
     * Test of marshallUssdForm method, of class UssdUtils.
     */
    @Test
    public void testMarshallUssdForm() {
        System.out.println("marshallUssdForm/unmarshallUssdForm");
        
        Map<String, String> data = new HashMap<String, String>();
        data.put("s", "2");
        ArrayList<UssdInput> inputs = new ArrayList<UssdInput>();
        inputs.add(new UssdInput("a").displayName("dk"));
        ArrayList<UssdInput.Option> options = new ArrayList<UssdInput.Option>();
        options.add(new UssdInput.Option("d"));
        options.add(new UssdInput.Option("e"));
        inputs.add(new UssdInput("n").displayName("n").options(options));
        UssdForm expected = new UssdForm("c", "a").inputs(inputs).data(data);
        
        String repr = UssdUtils.marshallUssdForm(expected);        
        UssdForm result = UssdUtils.unmarshallUssdForm(repr);
        
        assertEquals(expected, result);
    }

    /**
     * Test of marshallUssdMenu method, of class UssdUtils.
     */
    @Test
    public void testMarshallUssdMenu() {
        System.out.println("marshallUssdMenu/unmarshallUssdMenu");
        
        ArrayList<UssdMenuItem> items = new  ArrayList<UssdMenuItem>();
        items.add(new UssdMenuItem("i", "dkdk", "con", "dl"));
        items.add(new UssdMenuItem("i1", "dkdk", "con1", "dl1"));
        UssdMenu expected = new UssdMenu().items(items).header("Header");
        
        String repr = UssdUtils.marshallUssdMenu(expected);        
        UssdMenu menu2 = UssdUtils.unmarshallUssdMenu(repr);
        
        assertEquals(expected, menu2);
    }

    /**
     * Test of marshallMap method, of class UssdUtils.
     */
    @Test
    public void testMarshallMap() {
        System.out.println("marshallMap/unmarshallMap");
        
        // Test with null map.
        Map<String, String> expected = null;
        
        String repr = UssdUtils.marshallMap(expected);
        Map<String, String> result = UssdUtils.unmarshallMap(repr);
        
        assertEquals(expected, result);
        
        // Test with empty map.
        expected = new HashMap<String, String>();
        
        repr = UssdUtils.marshallMap(expected);
        result = UssdUtils.unmarshallMap(repr);
        
        assertEquals(expected, result);
        
        // Test with non-empty map.
        expected = new HashMap<String, String>();
        expected.put("a", "b");
        expected.put("a2", "b2");
        
        repr = UssdUtils.marshallMap(expected);
        result = UssdUtils.unmarshallMap(repr);
        
        assertEquals(expected, result);
    }
    
    @Test
    public void testUnMarshallWithBlanks() {
        System.out.println("unmarshall* with blanks");
        
        String[] blanks = { null, "", " "};
        for (String blank : blanks) {
            UssdForm form = UssdUtils.unmarshallUssdForm(blank);
            assertNull(form);
            
            UssdMenu menu = UssdUtils.unmarshallUssdMenu(blank);
            assertNull(menu);
            
            Map<String, String> map = UssdUtils.unmarshallMap(blank);
            assertNull(map);
        }
    }
}
