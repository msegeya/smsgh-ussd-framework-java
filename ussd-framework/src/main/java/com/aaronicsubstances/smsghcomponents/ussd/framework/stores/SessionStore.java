/*
 *  (c)  2016. Aaronic Substances 
 */
package com.aaronicsubstances.smsghcomponents.ussd.framework.stores;

/**
 *
 * @author aaron
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
    
    void close();
}
