/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework.stores;

import com.aaronicsubstances.smsghcomponents.ussd.framework.utils.SelfExpiringHashMap;
import java.util.HashMap;
import java.util.Map;

/**
 * A thread-safe in-memory session store. Designed to be used as a singleton per
 * ussd application.
 * 
 * @author aaron
 */
public class InMemorySessionStore implements SessionStore {
    private final Map<String, Object> backingStore;

    public InMemorySessionStore(int timeoutInMillis) {
        this.backingStore = new SelfExpiringHashMap<String, Object>(
                timeoutInMillis);
    }
    
    // Hash store implementation.
    
    public synchronized String getHashValue(String name, String key) {        
        if (backingStore.containsKey(name)) {
            Map<String, String> hash = 
                    (Map<String, String>)backingStore.get(name);
            return hash.get(key);
        }
        return null;
    }

    public synchronized void setHashValue(String name, String key, String value) {
        Map<String, String> hash;
        if (backingStore.containsKey(name)) {
            hash = (Map<String, String>)backingStore.get(name);
        }
        else {
            hash = new HashMap<String, String>();
            backingStore.put(name, hash);
        }
        hash.put(key, value);
    }

    public synchronized boolean hashExists(String name) {
        return backingStore.containsKey(name);
    }

    public synchronized boolean hashValueExists(String name, String key) {
        if (backingStore.containsKey(name)) {
            Map<String, String> hash = 
                    (Map<String, String>)backingStore.get(name);
            return hash.containsKey(key);
        }
        return false;
    }

    public synchronized void deleteHash(String name) {
        backingStore.remove(name);
    }

    public synchronized void deleteHashValue(String name, String key) {
        if (backingStore.containsKey(name)) {
            Map<String, String> hash = 
                    (Map<String, String>)backingStore.get(name);
            hash.remove(key);
        }
    }
    
    // Key-Value store implementation.

    public synchronized void setValue(String key, String value) {
        backingStore.put(key, value);
    }

    public synchronized String getValue(String key) {
        return (String)backingStore.get(key);
    }

    public synchronized boolean valueExists(String key) {
        return backingStore.containsKey(key);
    }

    public synchronized void deleteValue(String key) {
        backingStore.remove(key);
    }    
}
