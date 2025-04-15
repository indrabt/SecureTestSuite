package com.securetest.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.TouchAction;
import io.appium.java_client.PerformsTouchActions;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

/**
 * Helper class for Appium-specific operations.
 * Provides methods for common mobile automation tasks.
 */
public class AppiumHelper {
    private static final Logger LOGGER = LogManager.getLogger(AppiumHelper.class);
    
    private AppiumHelper() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Retrieves OTP from SMS messages on the mobile device.
     * 
     * @param driver The AppiumDriver instance
     * @param senderFilter Optional filter for SMS sender
     * @param timeoutSeconds Timeout in seconds to wait for OTP
     * @return The extracted OTP or null if not found
     */
    public static String retrieveOtpFromSms(AppiumDriver driver, String senderFilter, int timeoutSeconds) {
        try {
            LOGGER.info("Attempting to retrieve OTP from SMS");
            
            // Check if we're on Android
            boolean isAndroid = driver.getCapabilities().getCapability("platformName").toString().equalsIgnoreCase("Android");
            
            if (isAndroid) {
                // Android implementation - opens SMS app and reads latest message
                // This is a simplified example - actual implementation would depend on device specifics
                // Use launchApp instead of startActivity for older Appium versions
                driver.launchApp();
                WebDriverWait wait = new WebDriverWait(driver, timeoutSeconds);
                
                // Wait for the first conversation to appear if filter is provided
                if (senderFilter != null && !senderFilter.isEmpty()) {
                    WebElement conversation = wait.until(ExpectedConditions.presenceOfElementLocated(
                            MobileBy.xpath("//android.widget.TextView[contains(@text, '" + senderFilter + "')]")));
                    conversation.click();
                } else {
                    // Just click on the first conversation
                    WebElement firstConversation = wait.until(ExpectedConditions.presenceOfElementLocated(
                            MobileBy.xpath("(//android.widget.TextView)[1]")));
                    firstConversation.click();
                }
                
                // Get the latest message
                WebElement latestMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
                        MobileBy.xpath("(//android.widget.TextView[contains(@resource-id, 'message_text')])[last()]")));
                
                String messageText = latestMessage.getText();
                LOGGER.debug("Latest message text: {}", messageText);
                
                // Extract OTP using regex pattern - assuming OTP is 6 digits
                return extractOtpFromText(messageText);
            } else {
                // iOS implementation would go here
                LOGGER.warn("OTP retrieval for iOS not implemented yet");
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve OTP from SMS: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Extracts OTP from text using regex pattern.
     * 
     * @param text The text to extract OTP from
     * @return The extracted OTP or null if not found
     */
    private static String extractOtpFromText(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        // Looking for 4-8 digit sequence which is likely to be an OTP
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\b\\d{4,8}\\b");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            String otp = matcher.group(0);
            LOGGER.info("Extracted OTP: {}", EncryptionUtil.maskSensitiveValue(otp));
            return otp;
        }
        
        LOGGER.warn("No OTP pattern found in text");
        return null;
    }
    
    /**
     * Swipes on the screen from one point to another.
     * 
     * @param driver The AppiumDriver instance
     * @param startX Starting x-coordinate
     * @param startY Starting y-coordinate
     * @param endX Ending x-coordinate
     * @param endY Ending y-coordinate
     * @param duration Duration of swipe in milliseconds
     */
    public static void swipe(AppiumDriver driver, int startX, int startY, int endX, int endY, int duration) {
        try {
            if (driver instanceof PerformsTouchActions) {
                // In older Appium Java client, use different approach
                Map<String, Object> params = new HashMap<>();
                params.put("startX", startX);
                params.put("startY", startY);
                params.put("endX", endX);
                params.put("endY", endY);
                params.put("duration", duration);
                driver.executeScript("mobile: swipe", params);
                
                LOGGER.debug("Swiped from ({},{}) to ({},{})", startX, startY, endX, endY);
            } else {
                LOGGER.error("Driver does not support touch actions");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to perform swipe: {}", e.getMessage());
        }
    }
    
    /**
     * Swipes up on the screen.
     * 
     * @param driver The AppiumDriver instance
     */
    public static void swipeUp(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.8);
        int endY = (int) (size.height * 0.2);
        
        swipe(driver, startX, startY, startX, endY, 500);
    }
    
    /**
     * Swipes down on the screen.
     * 
     * @param driver The AppiumDriver instance
     */
    public static void swipeDown(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.2);
        int endY = (int) (size.height * 0.8);
        
        swipe(driver, startX, startY, startX, endY, 500);
    }
    
    /**
     * Swipes left on the screen.
     * 
     * @param driver The AppiumDriver instance
     */
    public static void swipeLeft(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.8);
        int endX = (int) (size.width * 0.2);
        int startY = size.height / 2;
        
        swipe(driver, startX, startY, endX, startY, 500);
    }
    
    /**
     * Swipes right on the screen.
     * 
     * @param driver The AppiumDriver instance
     */
    public static void swipeRight(AppiumDriver driver) {
        Dimension size = driver.manage().window().getSize();
        int startX = (int) (size.width * 0.2);
        int endX = (int) (size.width * 0.8);
        int startY = size.height / 2;
        
        swipe(driver, startX, startY, endX, startY, 500);
    }
    
    /**
     * Taps on an element by coordinates.
     * 
     * @param driver The AppiumDriver instance
     * @param element The element to tap on
     */
    public static void tapElement(AppiumDriver driver, WebElement element) {
        try {
            Point location = element.getLocation();
            Dimension size = element.getSize();
            
            int centerX = location.getX() + size.getWidth() / 2;
            int centerY = location.getY() + size.getHeight() / 2;
            
            if (driver instanceof PerformsTouchActions) {
                PerformsTouchActions touchDriver = (PerformsTouchActions) driver;
                // For Java 8 compatibility
                Map<String, Object> tapParams = new HashMap<>();
                tapParams.put("x", centerX);
                tapParams.put("y", centerY);
                driver.executeScript("mobile: tap", tapParams);
                
                LOGGER.debug("Tapped element at ({},{})", centerX, centerY);
            } else {
                // Fallback to standard click if touch actions not supported
                element.click();
                LOGGER.debug("Clicked element as fallback (touch actions not supported)");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to tap element: {}", e.getMessage());
        }
    }
    
    /**
     * Initializes the Appium driver with appropriate capabilities.
     * 
     * @param deviceName The name of the device to use
     * @return The initialized AppiumDriver instance
     */
    public static AppiumDriver<MobileElement> initializeDriver(String deviceName) {
        try {
            LOGGER.info("Initializing Appium driver for device: {}", deviceName);
            
            DesiredCapabilities capabilities = new DesiredCapabilities();
            
            // Set common capabilities
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 60);
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2");
            
            // Determine platform based on device name (simple example)
            boolean isAndroid = !deviceName.toLowerCase().contains("iphone") && 
                               !deviceName.toLowerCase().contains("ipad") &&
                               !deviceName.toLowerCase().contains("ios");
            
            if (isAndroid) {
                LOGGER.info("Setting up Android driver capabilities");
                capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "Android");
                capabilities.setCapability(MobileCapabilityType.APP, PropertyManager.getProperty("android.app.path", ""));
                capabilities.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, PropertyManager.getProperty("android.app.package", ""));
                capabilities.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, PropertyManager.getProperty("android.app.activity", ""));
                
                // For simulation purposes, we'll just log info instead of creating an actual driver
                LOGGER.info("Android capabilities configured. Would connect to Appium server in a real environment.");
                
                // In a real implementation, this would be:
                // return new AndroidDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
                return null;
            } else {
                LOGGER.info("Setting up iOS driver capabilities");
                capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, "iOS");
                capabilities.setCapability(MobileCapabilityType.APP, PropertyManager.getProperty("ios.app.path", ""));
                capabilities.setCapability(IOSMobileCapabilityType.BUNDLE_ID, PropertyManager.getProperty("ios.bundle.id", ""));
                
                // For simulation purposes, we'll just log info instead of creating an actual driver
                LOGGER.info("iOS capabilities configured. Would connect to Appium server in a real environment.");
                
                // In a real implementation, this would be:
                // return new IOSDriver<>(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize Appium driver: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Simulates retrieving an OTP from a mobile banking app.
     * Used for demonstration purposes in the test framework.
     * 
     * @param driver The AppiumDriver instance
     * @return A simulated 6-digit OTP code
     */
    public static String simulateOtpRetrieval(AppiumDriver<MobileElement> driver) {
        LOGGER.info("Simulating OTP retrieval from mobile banking app");
        
        try {
            // In a real implementation, we would navigate to the app's OTP screen
            // and extract the actual OTP code
            
            // For simulation purposes, generate a random 6-digit OTP
            Random random = new Random();
            int otpInt = 100000 + random.nextInt(900000); // Generates a number between 100000 and 999999
            String otp = String.valueOf(otpInt);
            
            LOGGER.info("Successfully simulated OTP retrieval. OTP value masked for security.");
            return otp;
        } catch (Exception e) {
            LOGGER.error("Error during OTP simulation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to simulate OTP retrieval", e);
        }
    }
}
