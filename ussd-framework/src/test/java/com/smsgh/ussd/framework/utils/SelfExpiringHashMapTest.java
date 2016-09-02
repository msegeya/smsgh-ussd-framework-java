/**
 * (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework.utils;

import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Aaron Baffour-Awuah
 */
public class SelfExpiringHashMapTest {

    private final static int SLEEP_MULTIPLIER = 10;
    
    @Test
    public void logInternals() throws InterruptedException {
        SelfExpiringHashMap<String, String> map = new
            SelfExpiringHashMap<String, String>();
        // Expecting no entries in all three internal collections.
        showInternals(map);
        
        map.put("fruit", "banana", 2000);
        // Expecting single entry in all three internal collections.
        showInternals(map);
        
        map.put("cat", "lion", 1000);
        // Expecting two entries in all three internal collections.
        // Expecting "cat" before "fruit" in delayedQueue even though it was
        // inserted after "fruit".
        showInternals(map);
        
        assertEquals("banana", map.get("fruit"));
        // Expecting two entries in all three internal collections.
        // Expecting "cat" before "fruit".
        showInternals(map);
        
        map.put("country", "Ghana", 1000);
        // Expecting three entries in all three internal collections.
        // Expecting "cat", "country", "fruit" in that order.
        // FAILED! Was rather "cat", "fruit", "country".
        showInternals(map);
        
        assertEquals("lion", map.get("cat"));
        // Expecting three entries in all three internal collections.
        // Expecting "country", "cat", "fruit" in that order.
        // FAILED! Was rather "cat", "fruit", "country".
        showInternals(map);        
        
        Thread.sleep(1000);
        map.remove("cat");
        // Expecting single entry of "fruit" in all three internal
        // collections.        
        showInternals(map);
        
        assertNull(map.get("cat"));
        assertNull(map.get("country"));
        // Expecting single entry of "fruit" in all three internal
        // collections.
        showInternals(map);
        
        Thread.sleep(2000);
        assertNull(map.get("fruit"));
        // Expecting all three internal collections to be empty.
        showInternals(map);
    }
    
    private void showInternals(SelfExpiringHashMap<String, String> map) {
        System.out.format("internalMap: %s\n", map.internalMap);
        System.out.format("delayQueue: %s\n", map.delayQueue);
        System.out.format("expiringKeys: %s\n\n", map.expiringKeys);
    }

    @Test
    public void basicGetTest() throws InterruptedException {
        Map<String, String> map = new SelfExpiringHashMap<String, String>(
                2 * SLEEP_MULTIPLIER);
        map.put("a", "b");
        Thread.sleep(1 * SLEEP_MULTIPLIER);
        assertEquals(map.get("a"), "b");
    }

    @Test
    public void basicExpireTest() throws InterruptedException {
        Map<String, String> map = new SelfExpiringHashMap<String, String>(
            2 * SLEEP_MULTIPLIER);
        map.put("a", "b");
        Thread.sleep(3 * SLEEP_MULTIPLIER);
        map.put("c", "d");
        assertNull(map.get("a"));
    }

    @Test
    public void basicRenewTest() throws InterruptedException {
        SelfExpiringHashMap<String, String> map = 
                new SelfExpiringHashMap<String, String>( 3 * SLEEP_MULTIPLIER);
        map.put("a", "b");
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        map.renewKey("a");
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        assertEquals(map.get("a"), "b");
    }

    @Test
    public void getRenewTest() throws InterruptedException {
        SelfExpiringHashMap<String, String> map = 
                new SelfExpiringHashMap<String, String>(3 * SLEEP_MULTIPLIER);
        map.put("a", "b");
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        assertEquals(map.get("a"), "b");
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        assertEquals(map.get("a"), "b");
    }

    @Test
    public void multiplePutThenRemoveTest() throws InterruptedException {
        SelfExpiringHashMap<String, String> map = 
                new SelfExpiringHashMap<String, String>(2 * SLEEP_MULTIPLIER);
        map.put("a", "b");
        Thread.sleep(1 * SLEEP_MULTIPLIER);
        map.put("a", "c", 2 * SLEEP_MULTIPLIER);
        Thread.sleep(1 * SLEEP_MULTIPLIER);
        map.put("a", "d", 400 * SLEEP_MULTIPLIER);
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        assertEquals(map.remove("a"), "d");
    }

    @Test
    public void multiplePutThenGetTest() throws InterruptedException {
        SelfExpiringHashMap<String, String> map = 
                new SelfExpiringHashMap<String, String>();
        map.put("a", "b", 2 * SLEEP_MULTIPLIER);
        Thread.sleep(1 * SLEEP_MULTIPLIER);
        map.put("a", "c", 2 * SLEEP_MULTIPLIER);
        Thread.sleep(1 * SLEEP_MULTIPLIER);
        map.put("a", "d", 400 * SLEEP_MULTIPLIER);
        Thread.sleep(2 * SLEEP_MULTIPLIER);
        assertEquals(map.get("a"), "d");
    }

}
