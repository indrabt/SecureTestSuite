package com.securetest.pageobjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the OTP (One-Time Password) verification page.
 */
public class OtpPage extends BasePage {
    private static final Logger LOGGER = LogManager.getLogger(OtpPage.class);
    
    // Web elements on the OTP page
    @FindBy(id = "otp-input")
    private WebElement otpInputField;
    
    @FindBy(id = "verify-button")
    private WebElement verifyButton;
    
    @FindBy(id = "resend-otp")
    private WebElement resendOtpLink;
    
    @FindBy(id = "otp-error")
    private WebElement otpErrorMessage;
    
    @FindBy(css = ".otp-form")
    private WebElement otpForm;
    
    /**
     * Constructor for OtpPage.
     * 
     * @param driver The WebDriver instance
     */
    public OtpPage(WebDriver driver) {
        super(driver);
        waitForVisibility(otpForm);
    }
    
    /**
     * Enters OTP code in the input field.
     * 
     * @param otpCode The OTP code to enter
     * @return This OtpPage instance
     */
    public OtpPage enterOtp(String otpCode) {
        type(otpInputField, otpCode, true);
        return this;
    }
    
    /**
     * Clicks the verify button.
     * 
     * @return A DashboardPage if verification is successful
     */
    public DashboardPage clickVerify() {
        click(verifyButton);
        LOGGER.info("Clicked verify button for OTP");
        return new DashboardPage(driver);
    }
    
    /**
     * Clicks the resend OTP link.
     */
    public void clickResendOtp() {
        click(resendOtpLink);
        LOGGER.info("Clicked resend OTP link");
        // Wait for resend confirmation if needed
        waitForVisibility(otpInputField);
    }
    
    /**
     * Checks if an error message is displayed.
     * 
     * @return true if an error message is displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        try {
            return otpErrorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets the text of the error message.
     * 
     * @return The text of the error message
     */
    public String getErrorMessage() {
        if (isErrorMessageDisplayed()) {
            return otpErrorMessage.getText();
        }
        return null;
    }
    
    /**
     * Verifies that the OTP page is loaded.
     * 
     * @return true if the OTP page is loaded, false otherwise
     */
    public boolean isPageLoaded() {
        return isElementDisplayed(otpForm) && 
               isElementDisplayed(otpInputField) && 
               isElementDisplayed(verifyButton);
    }
    
    /**
     * Performs a full OTP verification sequence.
     * 
     * @param otpCode The OTP code to use
     * @return A DashboardPage if verification is successful
     */
    public DashboardPage verifyOtp(String otpCode) {
        enterOtp(otpCode);
        return clickVerify();
    }
}
