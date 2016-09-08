/*
 *  (c) 2016. SMSGH
 */
package com.smsgh.ussd.framework.stores;

/**
 * Common interface of classes for tracking ussd sessions. 
 * Stores key-value pairs and hashes/hashtables so that the 
 * values in the hashes can be retrieved directly without having
 * to first deserialize the hash.
 * 
 * @author Aaron Baffour-Awuah
 */
public interface SessionStore {
    
    // Hash store
    
    /**
     * Gets the value of a hash.
     * 
     * @param name name of hash.
     * @param key key whose value is to retrieved.
     * 
     * @return value for given key, or null if the hash does not exist in
     * store, or if key does not exist in hash.
     */
    String getHashValue(String name, String key);
    
    /**
     * Sets the value of a hash. If hash does not exist in store, it is created anew.
     * Else if key does not exist in hash, a new entry is created.
     * 
     * @param name name of hash. Will be created if it does not exist in store.
     * @param key key in hash. Will be created if it does not exist in hash.
     * @param value new value for key in hash.
     */
    void setHashValue(String name, String key, String value);
    
    /**
     * Checks whether a hash exists in store.
     * @param name name of hash
     * @return true if and only if hash exists in store.
     */
    boolean hashExists(String name);
    
    /**
     * Checks whether a key exists in hash.
     * @param name name of hash.
     * @param key key whose existence in hash is to be checked.
     * @return true if key exists in hash, or false if hash does not exist in
     * store, or if key does not exist in hash.
     */
    boolean hashValueExists(String name, String key);
    
    /**
     * Deletes a hash and all of its contents from store.
     * @param name name of hash.
     */
    void deleteHash(String name);
    
    /**
     * Deletes a hash entry.
     * @param name name of hash whose entry is to be deleted.
     * @param key key of hash entry to be deleted.
     */
    void deleteHashValue(String name, String key);

    // Key-Value store
    
    /**
     * Changes the value of a key-value pair. If key does not exists,
     * creates a new key-value pair.
     * @param key key to be newly associated with value, or whose value is to
     * be changed.
     * @param value new value for key.
     */
    void setValue(String key, String value);
    
    /**
     * Gets the value associated with a given key.
     * @param key
     * @return value for given key.
     */
    String getValue(String key);
    
    /**
     * Checks if a key is associated with some value.
     * @param key
     * @return true if and only if key is associated with some value.
     */
    boolean valueExists(String key);
    
    /**
     * Deletes a key-value pair.
     * @param key key of key-value pair to be deleted.
     */
    void deleteValue(String key);
    
    /**
     * Performs any necessary clean-up of resources.
     */
    void close();
}
