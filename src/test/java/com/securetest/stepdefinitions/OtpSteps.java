package com.securetest.stepdefinitions;

import com.securetest.pageobjects.OtpPage;
import com.securetest.pageobjects.mobile.OtpScreen;
import com.securetest.utils.AppiumHelper;
import com.securetest.utils.DriverFactory;
import com.securetest.utils.PropertyManager;
import com.securetest.utils.SensitiveDataManager;
import io.appium.java_client.AppiumDriver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Step definitions for OTP-related scenarios.
 */
public class OtpSteps {
    private static final Logger LOGGER = LogManager.getLogger(OtpSteps.class);
    private WebDriver webDriver;
    private AppiumDriver appiumDriver;
    private OtpPage otpPage;
    private OtpScreen otpScreen;
    private String retrievedOtp;
    
    /**
     * Constructor for OtpSteps.
     * Initializes WebDriver and page objects.
     */
    public OtpSteps() {
        this.webDriver = DriverFactory.getWebDriver();
        this.otpPage = new OtpPage(webDriver);
    }
    
    /**
     * Step definition for verifying OTP page is displayed.
     */
    @Then("I should see the OTP page")
    public void i_should_see_the_otp_page() {
        Assert.assertTrue(otpPage.isPageLoaded(), "OTP page is not loaded");
        LOGGER.info("Verified OTP page is displayed");
    }
    
    /**
     * Step definition for initializing mobile device for OTP retrieval.
     */
    @When("I initialize my mobile device for OTP retrieval")
    public void i_initialize_my_mobile_device_for_otp_retrieval() {
        // Get mobile device details
        String deviceName = SensitiveDataManager.getSecureValue(SensitiveDataManager.DEVICE_NAME);
        Assert.assertNotNull(deviceName, "Device name is required but was not provided");
        
        String platformName = PropertyManager.getProperty("mobile.platform", "Android");
        String appPackage = PropertyManager.getProperty("mobile.app.package", "com.android.messaging");
        String appActivity = PropertyManager.getProperty("mobile.app.activity", ".ui.ConversationListActivity");
        
        // Initialize Appium driver
        appiumDriver = DriverFactory.initAppiumDriver(
                platformName, deviceName, null, appPackage, appActivity, null);
        
        otpScreen = new OtpScreen(appiumDriver);
        LOGGER.info("Initialized mobile device for OTP retrieval: {} on {}", deviceName, platformName);
    }
    
    /**
     * Step definition for retrieving OTP from mobile device.
     */
    @When("I retrieve the OTP from my mobile device")
    public void i_retrieve_the_otp_from_my_mobile_device() {
        Assert.assertNotNull(appiumDriver, "AppiumDriver is not initialized");
        Assert.assertNotNull(otpScreen, "OTP Screen is not initialized");
        
        // Get phone number for filtering SMS sender
        String phoneNumber = SensitiveDataManager.getSecureValue(SensitiveDataManager.PHONE_NUMBER);
        
        // Retrieve OTP from mobile device
        retrievedOtp = otpScreen.retrieveOtp(phoneNumber);
        Assert.assertNotNull(retrievedOtp, "Failed to retrieve OTP from mobile device");
        
        LOGGER.info("Successfully retrieved OTP from mobile device");
    }
    
    /**
     * Step definition for entering the retrieved OTP.
     */
    @When("I enter the retrieved OTP")
    public void i_enter_the_retrieved_otp() {
        Assert.assertNotNull(retrievedOtp, "OTP has not been retrieved yet");
        
        otpPage.enterOtp(retrievedOtp);
        LOGGER.info("Entered retrieved OTP");
    }
    
    /**
     * Step definition for entering a specific OTP.
     * 
     * @param otp The OTP to enter
     */
    @When("I enter OTP {string}")
    public void i_enter_otp(String otp) {
        otpPage.enterOtp(otp);
        LOGGER.info("Entered OTP: [MASKED]");
    }
    
    /**
     * Step definition for clicking the OTP verify button.
     */
    @And("I click the OTP verify button")
    public void i_click_the_otp_verify_button() {
        otpPage.clickVerify();
        LOGGER.info("Clicked OTP verify button");
    }
    
    /**
     * Step definition for clicking the resend OTP link.
     */
    @When("I click the resend OTP link")
    public void i_click_the_resend_otp_link() {
        otpPage.clickResendOtp();
        LOGGER.info("Clicked resend OTP link");
    }
    
    /**
     * Step definition for verifying OTP error message.
     */
    @Then("I should see an OTP error message")
    public void i_should_see_an_otp_error_message() {
        Assert.assertTrue(otpPage.isErrorMessageDisplayed(), "OTP error message is not displayed");
        LOGGER.info("Verified OTP error message is displayed: {}", otpPage.getErrorMessage());
    }
    
    /**
     * Step definition for verifying specific OTP error message.
     * 
     * @param errorMessage The expected error message
     */
    @Then("I should see OTP error message containing {string}")
    public void i_should_see_otp_error_message_containing(String errorMessage) {
        Assert.assertTrue(otpPage.isErrorMessageDisplayed(), "OTP error message is not displayed");
        Assert.assertTrue(otpPage.getErrorMessage().contains(errorMessage), 
                "OTP error message does not contain expected text");
        LOGGER.info("Verified OTP error message contains: {}", errorMessage);
    }
}
