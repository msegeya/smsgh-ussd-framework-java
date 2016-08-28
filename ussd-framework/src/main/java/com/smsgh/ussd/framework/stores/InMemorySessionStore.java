/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework.stores;

import com.smsgh.ussd.framework.utils.SelfExpiringHashMap;
import java.util.HashMap;
import java.util.Map;

/**
 * A thread-safe in-memory session store that expires its entries after a specified
 * time period. Designed to be used as a singleton per Ussd application.
 * 
 * @author Aaron Baffour-Awuah
 */
public class InMemorySessionStore implements SessionStore {
    private final Map<String, Object> backingStore;

    /**
     * Creates a new in-memory session store.
     * 
     * @param timeoutInMillis the sliding expiration time of entries in the 
     * store in milliseconds.
     * 
     * @exception java.lang.IllegalArgumentException  if timeoutInMillis is
     * not positive.
     */
    public InMemorySessionStore(int timeoutInMillis) {
        if (timeoutInMillis <= 0) {
            throw new IllegalArgumentException("\"timeoutInMillis\" argument "
                    + "must be positive. Received " + timeoutInMillis);
        }
        this.backingStore = new SelfExpiringHashMap<String, Object>(
                timeoutInMillis);
    }
    
    // Hash store implementation.
    
    /**
     *{@inheritDoc} 
     */
    public synchronized String getHashValue(String name, String key) {        
        if (backingStore.containsKey(name)) {
            Map<String, String> hash = 
                    (Map<String, String>)backingStore.get(name);
            return hash.get(key);
        }
        return null;
    }

    /**
     *{@inheritDoc} 
     */
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

    /**
     *{@inheritDoc} 
     */
    public synchronized boolean hashExists(String name) {
        return backingStore.containsKey(name);
    }

    /**
     *{@inheritDoc} 
     */
    public synchronized boolean hashValueExists(String name, String key) {
        if (backingStore.containsKey(name)) {
            Map<String, String> hash = 
                    (Map<String, String>)backingStore.get(name);
            return hash.containsKey(key);
        }
        return false;
    }

    /**
     *{@inheritDoc} 
     */
    public synchronized void deleteHash(String name) {
        backingStore.remove(name);
    }

    /**
     *{@inheritDoc} 
     */
    public synchronized void deleteHashValue(String name, String key) {
        if (backingStore.containsKey(name)) {
            Map<String, String> hash = 
                    (Map<String, String>)backingStore.get(name);
            hash.remove(key);
        }
    }
    
    // Key-Value store implementation.

    /**
     *{@inheritDoc} 
     */
    public synchronized void setValue(String key, String value) {
        backingStore.put(key, value);
    }

    /**
     *{@inheritDoc} 
     */
    public synchronized String getValue(String key) {
        return (String)backingStore.get(key);
    }

    /**
     *{@inheritDoc} 
     */
    public synchronized boolean valueExists(String key) {
        return backingStore.containsKey(key);
    }

    /**
     *{@inheritDoc} 
     */
    public synchronized void deleteValue(String key) {
        backingStore.remove(key);
    }    

    /**
     * Does nothing.
     */
    public void close() {
    }
}
