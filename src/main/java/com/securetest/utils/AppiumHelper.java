package com.securetest.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.TouchAction;
import io.appium.java_client.touch.WaitOptions;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

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
            boolean isAndroid = driver.getPlatformName().equalsIgnoreCase("Android");
            
            if (isAndroid) {
                // Android implementation - opens SMS app and reads latest message
                // This is a simplified example - actual implementation would depend on device specifics
                driver.activateApp("com.android.messaging");
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
                
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
            new TouchAction(driver)
                    .press(PointOption.point(startX, startY))
                    .waitAction(WaitOptions.waitOptions(Duration.ofMillis(duration)))
                    .moveTo(PointOption.point(endX, endY))
                    .release()
                    .perform();
            
            LOGGER.debug("Swiped from ({},{}) to ({},{})", startX, startY, endX, endY);
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
            
            new TouchAction(driver)
                    .tap(PointOption.point(centerX, centerY))
                    .perform();
            
            LOGGER.debug("Tapped element at ({},{})", centerX, centerY);
        } catch (Exception e) {
            LOGGER.error("Failed to tap element: {}", e.getMessage());
        }
    }
}
