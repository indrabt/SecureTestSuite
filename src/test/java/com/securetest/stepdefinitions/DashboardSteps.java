package com.securetest.stepdefinitions;

import com.securetest.pageobjects.DashboardPage;
import com.securetest.utils.DriverFactory;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Step definitions for dashboard-related scenarios.
 */
public class DashboardSteps {
    private static final Logger LOGGER = LogManager.getLogger(DashboardSteps.class);
    private WebDriver driver;
    private DashboardPage dashboardPage;
    
    /**
     * Constructor for DashboardSteps.
     * Initializes WebDriver and page objects.
     */
    public DashboardSteps() {
        this.driver = DriverFactory.getWebDriver();
        this.dashboardPage = new DashboardPage(driver);
    }
    
    /**
     * Step definition for verifying dashboard page is displayed.
     */
    @Then("I should see the dashboard page")
    public void i_should_see_the_dashboard_page() {
        Assert.assertTrue(dashboardPage.isPageLoaded(), "Dashboard page is not loaded");
        LOGGER.info("Verified dashboard page is displayed");
    }
    
    /**
     * Step definition for verifying welcome message on dashboard.
     * 
     * @param expectedMessage The expected welcome message
     */
    @Then("I should see welcome message containing {string}")
    public void i_should_see_welcome_message_containing(String expectedMessage) {
        String actualMessage = dashboardPage.getWelcomeMessage();
        Assert.assertTrue(actualMessage.contains(expectedMessage), 
                "Welcome message does not contain expected text");
        LOGGER.info("Verified welcome message contains: {}", expectedMessage);
    }
    
    /**
     * Step definition for verifying account summary is displayed.
     */
    @Then("I should see my account summary")
    public void i_should_see_my_account_summary() {
        Assert.assertTrue(dashboardPage.isAccountSummaryDisplayed(), "Account summary is not displayed");
        LOGGER.info("Verified account summary is displayed");
    }
    
    /**
     * Step definition for verifying transactions are displayed.
     */
    @Then("I should see my transactions")
    public void i_should_see_my_transactions() {
        Assert.assertTrue(dashboardPage.isTransactionsDisplayed(), "Transactions are not displayed");
        LOGGER.info("Verified transactions are displayed");
    }
    
    /**
     * Step definition for clicking the user profile.
     */
    @When("I click on my user profile")
    public void i_click_on_my_user_profile() {
        dashboardPage.clickUserProfile();
        LOGGER.info("Clicked on user profile");
    }
    
    /**
     * Step definition for clicking the logout button.
     */
    @When("I click the logout button")
    public void i_click_the_logout_button() {
        dashboardPage.logout();
        LOGGER.info("Clicked logout button");
    }
    
    /**
     * Step definition for waiting for dashboard to fully load.
     */
    @When("I wait for the dashboard to fully load")
    public void i_wait_for_the_dashboard_to_fully_load() {
        dashboardPage.waitForDashboardLoad();
        LOGGER.info("Dashboard fully loaded");
    }
}
