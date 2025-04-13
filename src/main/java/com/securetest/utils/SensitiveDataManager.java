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
        
        // Get pre-encrypted values using the CommandLineParser
        String encryptedUsername = CommandLineParser.getEncryptedUsername();
        String encryptedPassword = CommandLineParser.getEncryptedPassword();
        String encryptedApiKey = CommandLineParser.getEncryptedApiKey();
        
        // Get other values and encrypt them
        String userId = CommandLineParser.getOptionValue("id");
        String phoneNumber = CommandLineParser.getOptionValue("phone");
        String deviceName = CommandLineParser.getOptionValue("device");
        
        // Store pre-encrypted values directly
        if (encryptedUsername != null) {
            encryptedData.put(USERNAME, encryptedUsername);
            LOGGER.debug("Stored encrypted username");
        }
        
        if (encryptedPassword != null) {
            encryptedData.put(PASSWORD, encryptedPassword);
            LOGGER.debug("Stored encrypted password");
        }
        
        if (encryptedApiKey != null) {
            encryptedData.put(API_KEY, encryptedApiKey);
            LOGGER.debug("Stored encrypted API key");
        }
        
        // Store other encrypted values
        storeSecurely(USER_ID, userId);
        storeSecurely(PHONE_NUMBER, phoneNumber);
        storeSecurely(DEVICE_NAME, deviceName);
        
        // Log success without revealing actual values
        LOGGER.info("Sensitive data initialized successfully from command line");
        if (encryptedUsername != null) LOGGER.info("✓ Username provided");
        if (encryptedPassword != null) LOGGER.info("✓ Password provided");
        if (encryptedApiKey != null) LOGGER.info("✓ API key provided");
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
    
    /**
     * Gets the securely stored username.
     * 
     * @return The decrypted username
     */
    public static String getUsername() {
        return getSecureValue(USERNAME);
    }
    
    /**
     * Gets the securely stored password.
     * 
     * @return The decrypted password
     */
    public static String getPassword() {
        return getSecureValue(PASSWORD);
    }
    
    /**
     * Gets the securely stored API key.
     * 
     * @return The decrypted API key
     */
    public static String getApiKey() {
        return getSecureValue(API_KEY);
    }
    
    /**
     * Gets the securely stored user ID.
     * 
     * @return The decrypted user ID
     */
    public static String getUserId() {
        return getSecureValue(USER_ID);
    }
    
    /**
     * Gets the securely stored phone number.
     * 
     * @return The decrypted phone number
     */
    public static String getPhoneNumber() {
        return getSecureValue(PHONE_NUMBER);
    }
    
    /**
     * Gets the securely stored device name.
     * 
     * @return The decrypted device name
     */
    public static String getDeviceName() {
        return getSecureValue(DEVICE_NAME);
    }
}
