/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework.stores;

/**
 * Common interface of classes for tracking ussd sessions.
 * 
 * @author Aaron Baffour-Awuah
 */
public interface SessionStore {
    // Hash store
    String getHashValue(String name, String key);
    void setHashValue(String name, String key, String value);
    boolean hashExists(String name);
    boolean hashValueExists(String name, String key);
    void deleteHash(String name);
    void deleteHashValue(String name, String key);

    // Key-Value store
    void setValue(String key, String value);
    String getValue(String key);
    boolean valueExists(String key);
    void deleteValue(String key);
}
