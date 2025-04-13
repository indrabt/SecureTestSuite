package com.securetest.pageobjects.mobile;

import com.securetest.utils.AppiumHelper;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebElement;

/**
 * Page object for the mobile OTP screen.
 * Used to retrieve OTP from SMS messages.
 */
public class OtpScreen extends MobileBasePage {
    private static final Logger LOGGER = LogManager.getLogger(OtpScreen.class);
    
    // Mobile elements on the messages app
    @AndroidFindBy(id = "com.android.messaging:id/conversation_list")
    @iOSXCUITFindBy(accessibility = "Messages Table")
    private WebElement messagesList;
    
    @AndroidFindBy(id = "com.android.messaging:id/message_text")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[contains(@name, 'verification code')]")
    private WebElement messageText;
    
    @AndroidFindBy(id = "com.android.messaging:id/conversation_name")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeTable/XCUIElementTypeCell")
    private WebElement firstConversation;
    
    /**
     * Constructor for OtpScreen.
     * 
     * @param driver The AppiumDriver instance
     */
    public OtpScreen(AppiumDriver driver) {
        super(driver);
    }
    
    /**
     * Opens the SMS messaging app.
     * 
     * @return This OtpScreen instance
     */
    public OtpScreen openMessagingApp() {
        String devicePlatform = driver.getPlatformName().toLowerCase();
        
        if (devicePlatform.contains("android")) {
            driver.activateApp("com.android.messaging");
        } else if (devicePlatform.contains("ios")) {
            driver.activateApp("com.apple.MobileSMS");
        }
        
        LOGGER.info("Opened messaging app on {}", devicePlatform);
        return this;
    }
    
    /**
     * Checks if the messaging app is open.
     * 
     * @return true if the messaging app is open, false otherwise
     */
    public boolean isMessagingAppOpen() {
        try {
            return isElementDisplayed(messagesList);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Opens the first conversation in the list.
     * 
     * @return This OtpScreen instance
     */
    public OtpScreen openFirstConversation() {
        click(firstConversation);
        LOGGER.info("Opened first conversation");
        return this;
    }
    
    /**
     * Opens a conversation from a specific sender.
     * 
     * @param sender The sender name or number
     * @return This OtpScreen instance
     */
    public OtpScreen openConversationFromSender(String sender) {
        String devicePlatform = driver.getPlatformName().toLowerCase();
        WebElement senderElement = null;
        
        try {
            if (devicePlatform.contains("android")) {
                senderElement = driver.findElement(MobileBy.xpath(
                        "//android.widget.TextView[contains(@text, '" + sender + "')]"));
            } else if (devicePlatform.contains("ios")) {
                senderElement = driver.findElement(MobileBy.xpath(
                        "//XCUIElementTypeStaticText[contains(@name, '" + sender + "')]"));
            }
            
            click(senderElement);
            LOGGER.info("Opened conversation from sender: {}", sender);
        } catch (Exception e) {
            LOGGER.error("Failed to find conversation from sender {}: {}", sender, e.getMessage());
            throw e;
        }
        
        return this;
    }
    
    /**
     * Gets the latest message text.
     * 
     * @return The text of the latest message
     */
    public String getLatestMessageText() {
        try {
            return getText(messageText);
        } catch (Exception e) {
            LOGGER.error("Failed to get latest message text: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Extracts OTP from the latest message.
     * 
     * @return The extracted OTP or null if not found
     */
    public String extractOtp() {
        String messageContent = getLatestMessageText();
        if (messageContent == null || messageContent.isEmpty()) {
            LOGGER.warn("No message content found to extract OTP");
            return null;
        }
        
        // Using AppiumHelper to extract the OTP
        return AppiumHelper.retrieveOtpFromSms(driver, null, 10);
    }
    
    /**
     * Completes the full flow of retrieving an OTP.
     * Opens messaging app, navigates to the message, extracts the OTP.
     * 
     * @param sender Optional sender filter
     * @return The extracted OTP or null if not found
     */
    public String retrieveOtp(String sender) {
        try {
            openMessagingApp();
            
            if (sender != null && !sender.isEmpty()) {
                openConversationFromSender(sender);
            } else {
                openFirstConversation();
            }
            
            return extractOtp();
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve OTP: {}", e.getMessage());
            return null;
        }
    }
}
