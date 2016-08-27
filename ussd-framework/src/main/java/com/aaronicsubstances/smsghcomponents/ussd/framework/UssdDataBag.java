/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework;

import com.aaronicsubstances.smsghcomponents.ussd.framework.stores.SessionStore;

/**
 *
 * @author aaron
 */
public class UssdDataBag {
    private String dataBagKey;
    private SessionStore store;

    public UssdDataBag(SessionStore store, String dataBagKey) {
        this.store = store;
        this.dataBagKey = dataBagKey;
    }

    public void set(String key, String value) {
        store.setHashValue(dataBagKey, key, value);
    }

    public String get(String key) {
        return store.getHashValue(dataBagKey, key);
    }

    public boolean exists(String key) {
        return store.hashValueExists(dataBagKey, key);
    }

    public void delete(String key) {
        store.deleteHashValue(dataBagKey, key);
    }

    public void clear() {
        store.deleteHash(dataBagKey);
    }
}
