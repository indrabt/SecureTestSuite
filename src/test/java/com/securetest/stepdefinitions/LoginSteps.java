package com.securetest.stepdefinitions;

import com.securetest.pageobjects.LoginPage;
import com.securetest.utils.DriverFactory;
import com.securetest.utils.PropertyManager;
import com.securetest.utils.SensitiveDataManager;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Step definitions for login-related scenarios.
 */
public class LoginSteps {
    private static final Logger LOGGER = LogManager.getLogger(LoginSteps.class);
    private WebDriver driver;
    private LoginPage loginPage;
    
    /**
     * Constructor for LoginSteps.
     * Initializes WebDriver and page objects.
     */
    public LoginSteps() {
        this.driver = DriverFactory.getWebDriver();
        this.loginPage = new LoginPage(driver);
    }
    
    /**
     * Step definition for navigating to the login page.
     * 
     * @param url The login page URL
     */
    @Given("I navigate to the login page {string}")
    public void i_navigate_to_the_login_page(String url) {
        loginPage.navigateTo(url);
        LOGGER.info("Navigated to login page: {}", url);
    }
    
    /**
     * Step definition for navigating to the Cuscal portal.
     */
    @Given("I navigate to the Cuscal portal")
    public void i_navigate_to_the_cuscal_portal() {
        String portalUrl = PropertyManager.getProperty("cuscal.portal.url", "https://portal.cuscal.com.au");
        loginPage.navigateTo(portalUrl);
        LOGGER.info("Navigated to Cuscal portal: {}", portalUrl);
    }
    
    /**
     * Step definition for entering username.
     */
    @When("I enter my username")
    public void i_enter_my_username() {
        String username = SensitiveDataManager.getSecureValue(SensitiveDataManager.USERNAME);
        Assert.assertNotNull(username, "Username is required but was not provided");
        
        loginPage.enterUsername(username);
        LOGGER.info("Entered username");
    }
    
    /**
     * Step definition for entering password.
     */
    @When("I enter my password")
    public void i_enter_my_password() {
        String password = SensitiveDataManager.getSecureValue(SensitiveDataManager.PASSWORD);
        Assert.assertNotNull(password, "Password is required but was not provided");
        
        loginPage.enterPassword(password);
        LOGGER.info("Entered password");
    }
    
    /**
     * Step definition for entering specific username.
     * 
     * @param username The username to enter
     */
    @When("I enter username {string}")
    public void i_enter_username(String username) {
        loginPage.enterUsername(username);
        LOGGER.info("Entered username: {}", username);
    }
    
    /**
     * Step definition for entering specific password.
     * 
     * @param password The password to enter
     */
    @When("I enter password {string}")
    public void i_enter_password(String password) {
        loginPage.enterPassword(password);
        LOGGER.info("Entered password: [MASKED]");
    }
    
    /**
     * Step definition for clicking the login button.
     */
    @When("I click the login button")
    public void i_click_the_login_button() {
        loginPage.clickLogin();
        LOGGER.info("Clicked login button");
    }
    
    /**
     * Step definition for verifying login page is displayed.
     */
    @Then("I should see the login page")
    public void i_should_see_the_login_page() {
        Assert.assertTrue(loginPage.isPageLoaded(), "Login page is not loaded");
        LOGGER.info("Verified login page is displayed");
    }
    
    /**
     * Step definition for verifying login error message.
     */
    @Then("I should see a login error message")
    public void i_should_see_a_login_error_message() {
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Login error message is not displayed");
        LOGGER.info("Verified login error message is displayed: {}", loginPage.getErrorMessage());
    }
    
    /**
     * Step definition for verifying specific login error message.
     * 
     * @param errorMessage The expected error message
     */
    @Then("I should see login error message containing {string}")
    public void i_should_see_login_error_message_containing(String errorMessage) {
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), "Login error message is not displayed");
        Assert.assertTrue(loginPage.getErrorMessage().contains(errorMessage), 
                "Login error message does not contain expected text");
        LOGGER.info("Verified login error message contains: {}", errorMessage);
    }
}
