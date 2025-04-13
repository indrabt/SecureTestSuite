package com.securetest.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages sensitive data securely in memory.
 * Stores encrypted credentials and provides controlled access.
 */
public class SensitiveDataManager {
    private static final Logger LOGGER = LogManager.getLogger(SensitiveDataManager.class);
    private static final Map<String, String> encryptedData = new HashMap<>();
    
    // Define keys for sensitive data
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String API_KEY = "apiKey";
    public static final String USER_ID = "userId";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String DEVICE_NAME = "deviceName";
    
    private SensitiveDataManager() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Initializes sensitive data from command line arguments.
     * Encrypts and stores provided credentials.
     */
    public static void initFromCommandLine() {
        LOGGER.info("Initializing sensitive data from command line arguments");
        
        // Get sensitive data from command line
        String username = CommandLineParser.getOptionValue("u");
        String password = CommandLineParser.getOptionValue("p");
        String apiKey = CommandLineParser.getOptionValue("a");
        String userId = CommandLineParser.getOptionValue("id");
        String phoneNumber = CommandLineParser.getOptionValue("phone");
        String deviceName = CommandLineParser.getOptionValue("device");
        
        // Store encrypted values
        storeSecurely(USERNAME, username);
        storeSecurely(PASSWORD, password);
        storeSecurely(API_KEY, apiKey);
        storeSecurely(USER_ID, userId);
        storeSecurely(PHONE_NUMBER, phoneNumber);
        storeSecurely(DEVICE_NAME, deviceName);
    }
    
    /**
     * Stores a value securely by encrypting it first.
     * 
     * @param key The key to store the value under
     * @param value The sensitive value to encrypt and store
     */
    public static void storeSecurely(String key, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        
        String encrypted = EncryptionUtil.encrypt(value);
        if (encrypted != null) {
            encryptedData.put(key, encrypted);
            LOGGER.debug("Stored encrypted value for key: {}", key);
        } else {
            LOGGER.error("Failed to encrypt value for key: {}", key);
        }
    }
    
    /**
     * Retrieves a securely stored value by decrypting it.
     * 
     * @param key The key to retrieve the value for
     * @return The decrypted value or null if not found
     */
    public static String getSecureValue(String key) {
        String encrypted = encryptedData.get(key);
        if (encrypted == null) {
            return null;
        }
        
        return EncryptionUtil.decrypt(encrypted);
    }
    
    /**
     * Checks if a secure value exists for a given key.
     * 
     * @param key The key to check
     * @return true if a value exists, false otherwise
     */
    public static boolean hasSecureValue(String key) {
        return encryptedData.containsKey(key);
    }
    
    /**
     * Removes a secure value from storage.
     * 
     * @param key The key to remove
     */
    public static void removeSecureValue(String key) {
        encryptedData.remove(key);
        LOGGER.debug("Removed secure value for key: {}", key);
    }
    
    /**
     * Clears all sensitive data from memory.
     * Should be called at the end of test execution.
     */
    public static void clearAllSecureData() {
        encryptedData.clear();
        LOGGER.info("All secure data cleared from memory");
    }
}
