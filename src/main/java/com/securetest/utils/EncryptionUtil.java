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
    private static final String ALGORITHM = "PBEWITHHMACSHA512ANDAES_256";
    private static final String LEGACY_ALGORITHM = "PBEWithMD5AndDES";
    private static final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
    private static final StandardPBEStringEncryptor legacyEncryptor = new StandardPBEStringEncryptor();
    
    static {
        try {
            // Initialize the encryptor with a system property key or default
            String encryptionKey = System.getProperty("encryption.key", DEFAULT_ENCRYPTION_KEY);
            
            // Set up the primary strong encryptor
            encryptor.setPassword(encryptionKey);
            encryptor.setAlgorithm(ALGORITHM);
            encryptor.setKeyObtentionIterations(10000);
            
            // Set up the legacy encryptor for backward compatibility
            legacyEncryptor.setPassword(encryptionKey);
            legacyEncryptor.setAlgorithm(LEGACY_ALGORITHM);
            
            LOGGER.info("Encryption utilities initialized successfully with algorithm: {}", ALGORITHM);
        } catch (Exception e) {
            LOGGER.error("Failed to initialize encryption utilities: {}", e.getMessage());
            // We don't rethrow as this is a static initializer and would prevent class loading
        }
    }
    
    /**
     * Sets a custom encryption key for enhanced security.
     * This should be called early in the application lifecycle.
     * 
     * @param customKey The custom encryption key to use
     */
    public static void setCustomEncryptionKey(String customKey) {
        if (customKey == null || customKey.isEmpty()) {
            LOGGER.warn("Custom encryption key is null or empty, using default key");
            return;
        }
        
        encryptor.setPassword(customKey);
        legacyEncryptor.setPassword(customKey);
        LOGGER.info("Custom encryption key applied successfully");
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
            // First try with the primary encryptor
            return encryptor.decrypt(encryptedValue);
        } catch (EncryptionOperationNotPossibleException primaryException) {
            try {
                // If primary decryption fails, try with legacy algorithm
                LOGGER.debug("Primary decryption failed, trying legacy algorithm");
                return legacyEncryptor.decrypt(encryptedValue);
            } catch (EncryptionOperationNotPossibleException legacyException) {
                // Both attempts failed
                LOGGER.error("Error decrypting value with both algorithms");
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Unexpected error during decryption: {}", e.getMessage());
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
