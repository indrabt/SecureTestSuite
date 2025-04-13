package com.securetest.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;

/**
 * Utility class for handling encryption/decryption of sensitive data.
 * Uses Jasypt for encryption operations.
 */
public class EncryptionUtil {
    private static final Logger LOGGER = LogManager.getLogger(EncryptionUtil.class);
    private static final String DEFAULT_ENCRYPTION_KEY = "SECURE_TEST_FRAMEWORK_KEY";
    private static final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
    
    static {
        // Initialize the encryptor with a system property key or default
        String encryptionKey = System.getProperty("encryption.key", DEFAULT_ENCRYPTION_KEY);
        encryptor.setPassword(encryptionKey);
        encryptor.setAlgorithm("PBEWithMD5AndDES");
    }
    
    /**
     * Encrypts sensitive data.
     * 
     * @param value The value to encrypt
     * @return The encrypted value or null if encryption fails
     */
    public static String encrypt(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            return encryptor.encrypt(value);
        } catch (Exception e) {
            LOGGER.error("Error encrypting value: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Decrypts encrypted data.
     * 
     * @param encryptedValue The encrypted value to decrypt
     * @return The decrypted value or null if decryption fails
     */
    public static String decrypt(String encryptedValue) {
        if (encryptedValue == null || encryptedValue.isEmpty()) {
            return null;
        }
        
        try {
            return encryptor.decrypt(encryptedValue);
        } catch (EncryptionOperationNotPossibleException e) {
            LOGGER.error("Error decrypting value: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Masks a sensitive value for display/logging purposes.
     * 
     * @param value The value to mask
     * @return The masked value
     */
    public static String maskSensitiveValue(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        
        int length = value.length();
        if (length <= 4) {
            return "****";
        } else {
            return value.substring(0, 2) + "*".repeat(length - 4) + value.substring(length - 2);
        }
    }
}
