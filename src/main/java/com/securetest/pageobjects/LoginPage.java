package com.securetest.pageobjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the Cuscal portal login page.
 */
public class LoginPage extends BasePage {
    private static final Logger LOGGER = LogManager.getLogger(LoginPage.class);
    
    // Web elements on the login page
    @FindBy(id = "username")
    private WebElement usernameField;
    
    @FindBy(id = "password")
    private WebElement passwordField;
    
    @FindBy(id = "login-button")
    private WebElement loginButton;
    
    @FindBy(id = "error-message")
    private WebElement errorMessage;
    
    @FindBy(css = ".login-form")
    private WebElement loginForm;
    
    /**
     * Constructor for LoginPage.
     * 
     * @param driver The WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Navigates to the login page.
     * 
     * @param url The login page URL
     * @return This LoginPage instance
     */
    public LoginPage navigateTo(String url) {
        driver.get(url);
        LOGGER.info("Navigated to login page: {}", url);
        waitForVisibility(loginForm);
        return this;
    }
    
    /**
     * Enters username in the username field.
     * 
     * @param username The username to enter
     * @return This LoginPage instance
     */
    public LoginPage enterUsername(String username) {
        type(usernameField, username, false);
        return this;
    }
    
    /**
     * Enters password in the password field.
     * 
     * @param password The password to enter
     * @return This LoginPage instance
     */
    public LoginPage enterPassword(String password) {
        type(passwordField, password, true);
        return this;
    }
    
    /**
     * Clicks the login button.
     * 
     * @return An OtpPage if login is successful
     */
    public OtpPage clickLogin() {
        click(loginButton);
        LOGGER.info("Clicked login button");
        return new OtpPage(driver);
    }
    
    /**
     * Performs a full login sequence.
     * 
     * @param username The username to use
     * @param password The password to use
     * @return An OtpPage if login is successful
     */
    public OtpPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }
    
    /**
     * Checks if an error message is displayed.
     * 
     * @return true if an error message is displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        try {
            return errorMessage.isDisplayed();
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
            return errorMessage.getText();
        }
        return null;
    }
    
    /**
     * Verifies that the login page is loaded.
     * 
     * @return true if the login page is loaded, false otherwise
     */
    public boolean isPageLoaded() {
        return isElementDisplayed(loginForm) && 
               isElementDisplayed(usernameField) && 
               isElementDisplayed(passwordField) && 
               isElementDisplayed(loginButton);
    }
}
