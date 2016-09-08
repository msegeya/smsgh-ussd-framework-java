/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework;

import com.smsgh.ussd.framework.stores.SessionStore;

/**
 * Map-like class for use inside ussd controllers for persisting data
 * across ussd requests, but within the same ussd session.
 * 
 * @author Aaron Baffour-Awuah
 */
public class UssdDataBag {
    private String dataBagKey;
    private SessionStore store;

    /**
     * Creates new UssdDataBag instance.
     * 
     * @param store the backing store for the UssdDataBag contents.
     * @param dataBagKey the key under which the UssdDataBag is stored in the
     * backing store.
     */
    public UssdDataBag(SessionStore store, String dataBagKey) {
        this.store = store;
        this.dataBagKey = dataBagKey;
    }

    /**
     * Gets the value associated with a key.
     * @param key
     * @return value of existing key-value pair, or null if key does not exist.
     */
    public String get(String key) {
        return store.getHashValue(dataBagKey, key);
    }

    /**
     * Determines whether a key is associated with any value in the
     * UssdDataBag instance.
     * @param key 
     * @return true if and only if key exists as part of some key-value pair.
     */
    public boolean exists(String key) {
        return store.hashValueExists(dataBagKey, key);
    }

    /**
     * Sets or changes the value of a key-value pair in the
     * UssdDataBag instance.
     * @param key the key to be associated with a new value, or the key of
     * the key-value pair whose value is to be changed.
     * @param value the new value to be associated with the key.
     */
    public void set(String key, String value) {
        store.setHashValue(dataBagKey, key, value);
    }

    /**
     * Deletes a key-value pair in the UssdDataBag instance.
     * @param key the key of the pair to delete.
     */
    public void delete(String key) {
        store.deleteHashValue(dataBagKey, key);
    }

    /**
     * Deletes all the contents of the UssdDataBag instance.
     */
    public void clear() {
        store.deleteHash(dataBagKey);
    }
}
