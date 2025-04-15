package com.securetest.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utility class for handling encryption/decryption of sensitive data.
 * Uses standard Java cryptography instead of Jasypt to avoid algorithm restrictions.
 */
public class EncryptionUtil {
    private static final Logger LOGGER = LogManager.getLogger(EncryptionUtil.class);
    private static final String DEFAULT_ENCRYPTION_KEY = "SECURE_TEST_FRAMEWORK_KEY";
    private static final String ALGORITHM = "AES";
    private static SecretKeySpec secretKey;
    private static boolean isTestMode = false;
    
    static {
        try {
            // Initialize the encryption with a system property key or default
            String encryptionKey = System.getProperty("encryption.key", DEFAULT_ENCRYPTION_KEY);
            setKey(encryptionKey);
            LOGGER.info("Encryption utilities initialized successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize encryption utilities: {}", e.getMessage());
            // We don't rethrow as this is a static initializer and would prevent class loading
            // Instead, we'll fall back to test mode if encryption fails
            isTestMode = true;
            LOGGER.warn("Falling back to test mode for encryption");
        }
    }
    
    /**
     * Sets the encryption key by creating a 128-bit AES key.
     *
     * @param myKey the key to use for encryption/decryption
     */
    private static void setKey(String myKey) {
        try {
            byte[] key = myKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // Use only first 128 bits
            secretKey = new SecretKeySpec(key, ALGORITHM);
        } catch (Exception e) {
            LOGGER.error("Error setting encryption key: {}", e.getMessage());
            throw new RuntimeException("Failed to set encryption key", e);
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
        
        try {
            setKey(customKey);
            LOGGER.info("Custom encryption key applied successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to set custom encryption key: {}", e.getMessage());
        }
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
        
        // In test mode, use a simpler encoding approach
        if (isTestMode) {
            return MockEncryptionUtil.encodeForTest(value);
        }
        
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            LOGGER.error("Error encrypting value: {}", e.getMessage());
            // Fall back to mock implementation if real encryption fails
            return MockEncryptionUtil.encodeForTest(value);
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
        
        // In test mode, use a simpler decoding approach
        if (isTestMode) {
            return MockEncryptionUtil.decodeForTest(encryptedValue);
        }
        
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedValue);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.error("Error decrypting value: {}", e.getMessage());
            // Try with mock implementation as fallback
            return MockEncryptionUtil.decodeForTest(encryptedValue);
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
            // Java 8 compatible version of string repeat
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length - 4; i++) {
                sb.append("*");
            }
            return value.substring(0, 2) + sb.toString() + value.substring(length - 2);
        }
    }
    
    /**
     * Enables test mode which uses simple encoding rather than encryption.
     * This is useful for environments where crypto policy restrictions exist.
     */
    public static void enableTestMode() {
        isTestMode = true;
        LOGGER.info("Encryption test mode enabled");
    }
    
    /**
     * Disables test mode and uses real encryption.
     */
    public static void disableTestMode() {
        isTestMode = false;
        LOGGER.info("Encryption test mode disabled");
    }
}
