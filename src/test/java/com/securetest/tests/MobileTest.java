package com.securetest.tests;

import com.securetest.utils.AppiumHelper;
import com.securetest.utils.CommandLineParser;
import com.securetest.utils.SensitiveDataManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example mobile test class for demonstration purposes.
 * Shows how to integrate with Appium and handle OTP retrieval.
 */
public class MobileTest {
    private static final Logger LOGGER = LogManager.getLogger(MobileTest.class);
    private AppiumDriver<MobileElement> driver;
    
    /**
     * Sets up the Appium driver and configuration before each test.
     */
    @Before
    public void setUp() throws Exception {
        LOGGER.info("Setting up mobile test environment");
        
        // Get device name from command line
        String deviceName = CommandLineParser.getOptionValue("device", "Android Emulator");
        LOGGER.info("Using device: {}", deviceName);
        
        // Initialize Appium driver configuration
        driver = AppiumHelper.initializeDriver(deviceName);
        assertNotNull("Failed to initialize Appium driver", driver);
        
        LOGGER.info("Mobile test environment setup completed");
    }
    
    /**
     * Tear down the Appium driver after each test.
     */
    @After
    public void tearDown() {
        if (driver != null) {
            LOGGER.info("Quitting Appium driver");
            driver.quit();
        }
    }
    
    /**
     * Test method that simulates retrieving an OTP from a mobile banking app.
     * This is a demonstration of how the framework would integrate with Appium.
     */
    @Test
    public void testRetrieveOtp() {
        LOGGER.info("Starting mobile OTP retrieval test");
        
        // This is a simulation - in a real test we would:
        // 1. Launch the banking app
        // 2. Log in with secure credentials
        // 3. Navigate to the OTP screen
        // 4. Extract the OTP
        
        try {
            // Simulate opening the app
            LOGGER.info("Simulating opening the banking app");
            
            // Get secure credentials for app login
            String username = SensitiveDataManager.getSecureData("username");
            String password = SensitiveDataManager.getSecureData("password");
            assertNotNull("Username not found in secure storage", username);
            assertNotNull("Password not found in secure storage", password);
            
            // Simulate retrieving OTP
            LOGGER.info("Simulating OTP retrieval from mobile app");
            String otp = AppiumHelper.simulateOtpRetrieval(driver);
            assertNotNull("Failed to retrieve OTP", otp);
            assertTrue("OTP should be 6 digits", otp.matches("\\d{6}"));
            
            LOGGER.info("Successfully retrieved OTP: [SECURED]");
            
            // In a real test, we would now store this OTP securely
            SensitiveDataManager.storeSecureData("otp", otp);
            
        } catch (Exception e) {
            LOGGER.error("Error during mobile OTP test: {}", e.getMessage(), e);
            throw new RuntimeException("Mobile OTP test failed", e);
        }
        
        LOGGER.info("Mobile OTP retrieval test completed successfully");
    }
}