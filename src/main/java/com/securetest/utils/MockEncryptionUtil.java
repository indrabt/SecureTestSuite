package com.securetest.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A mock implementation of encryption for testing environments.
 * Uses simple Base64 encoding rather than true encryption to avoid
 * dependency on JCE Unlimited Strength policy files.
 */
public class MockEncryptionUtil {
    private static final Logger LOGGER = LogManager.getLogger(MockEncryptionUtil.class);
    
    private MockEncryptionUtil() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * "Encrypts" data using Base64 encoding (not secure, just for testing).
     * 
     * @param value The value to encode
     * @return The encoded value or null if encoding fails
     */
    public static String encodeForTest(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            // Simple encoding for testing only (NOT SECURE)
            return java.util.Base64.getEncoder().encodeToString(value.getBytes());
        } catch (Exception e) {
            LOGGER.error("Error encoding value: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * "Decrypts" data using Base64 decoding (not secure, just for testing).
     * 
     * @param encodedValue The encoded value to decode
     * @return The decoded value or null if decoding fails
     */
    public static String decodeForTest(String encodedValue) {
        if (encodedValue == null || encodedValue.isEmpty()) {
            return null;
        }
        
        try {
            // Simple decoding for testing only (NOT SECURE)
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(encodedValue);
            return new String(decodedBytes);
        } catch (Exception e) {
            LOGGER.error("Error decoding value: {}", e.getMessage());
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
            // Show first and last two characters, mask the rest
            return value.substring(0, 2) + "*".repeat(length - 4) + value.substring(length - 2);
        }
    }
}