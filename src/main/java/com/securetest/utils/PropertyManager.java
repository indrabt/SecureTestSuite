package com.securetest.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Manages configuration properties for the test framework.
 * Loads properties from config files and environment variables.
 */
public class PropertyManager {
    private static final Logger LOGGER = LogManager.getLogger(PropertyManager.class);
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "src/test/resources/config.properties";
    private static boolean initialized = false;
    
    /**
     * Initialize the property manager by loading configuration from file.
     */
    public static void init() {
        if (initialized) {
            return;
        }
        
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            initialized = true;
            LOGGER.info("Configuration properties loaded successfully");
        } catch (IOException e) {
            LOGGER.error("Failed to load configuration properties: {}", e.getMessage());
        }
    }
    
    /**
     * Gets a property value from config file or system properties.
     * 
     * @param key The property key
     * @return The property value or null if not found
     */
    public static String getProperty(String key) {
        if (!initialized) {
            init();
        }
        
        // First check system properties (highest priority)
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        
        // Then check environment variables
        value = System.getenv(key);
        if (value != null) {
            return value;
        }
        
        // Finally check properties file
        return properties.getProperty(key);
    }
    
    /**
     * Gets a property value with a default if not found.
     * 
     * @param key The property key
     * @param defaultValue The default value if property not found
     * @return The property value or the default
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return (value != null) ? value : defaultValue;
    }
    
    /**
     * Gets a boolean property value.
     * 
     * @param key The property key
     * @param defaultValue The default value if property not found
     * @return The boolean property value or the default
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Gets an integer property value.
     * 
     * @param key The property key
     * @param defaultValue The default value if property not found
     * @return The integer property value or the default
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid integer property value for {}: {}", key, value);
            return defaultValue;
        }
    }
    
    /**
     * Sets a property value.
     * 
     * @param key The property key
     * @param value The property value
     */
    public static void setProperty(String key, String value) {
        if (!initialized) {
            init();
        }
        properties.setProperty(key, value);
    }
}
