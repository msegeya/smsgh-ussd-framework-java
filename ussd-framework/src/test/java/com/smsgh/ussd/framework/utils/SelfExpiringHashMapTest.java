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
