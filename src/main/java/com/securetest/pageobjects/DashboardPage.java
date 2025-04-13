package com.securetest.pageobjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page object for the dashboard page after successful authentication.
 */
public class DashboardPage extends BasePage {
    private static final Logger LOGGER = LogManager.getLogger(DashboardPage.class);
    
    // Web elements on the dashboard page
    @FindBy(css = ".dashboard-header")
    private WebElement dashboardHeader;
    
    @FindBy(id = "user-profile")
    private WebElement userProfile;
    
    @FindBy(id = "logout-button")
    private WebElement logoutButton;
    
    @FindBy(id = "account-summary")
    private WebElement accountSummary;
    
    @FindBy(id = "transactions")
    private WebElement transactions;
    
    @FindBy(id = "welcome-message")
    private WebElement welcomeMessage;
    
    /**
     * Constructor for DashboardPage.
     * 
     * @param driver The WebDriver instance
     */
    public DashboardPage(WebDriver driver) {
        super(driver);
        waitForVisibility(dashboardHeader);
    }
    
    /**
     * Gets the welcome message text.
     * 
     * @return The welcome message text
     */
    public String getWelcomeMessage() {
        return waitForVisibility(welcomeMessage).getText();
    }
    
    /**
     * Clicks the user profile link.
     */
    public void clickUserProfile() {
        click(userProfile);
        LOGGER.info("Clicked user profile");
    }
    
    /**
     * Clicks the logout button.
     * 
     * @return A LoginPage after logout
     */
    public LoginPage logout() {
        click(logoutButton);
        LOGGER.info("Clicked logout button");
        return new LoginPage(driver);
    }
    
    /**
     * Checks if the account summary section is displayed.
     * 
     * @return true if account summary is displayed, false otherwise
     */
    public boolean isAccountSummaryDisplayed() {
        return isElementDisplayed(accountSummary);
    }
    
    /**
     * Checks if the transactions section is displayed.
     * 
     * @return true if transactions are displayed, false otherwise
     */
    public boolean isTransactionsDisplayed() {
        return isElementDisplayed(transactions);
    }
    
    /**
     * Verifies that the dashboard page is loaded.
     * 
     * @return true if the dashboard page is loaded, false otherwise
     */
    public boolean isPageLoaded() {
        return isElementDisplayed(dashboardHeader) && 
               isElementDisplayed(userProfile) && 
               isElementDisplayed(logoutButton);
    }
    
    /**
     * Waits for the dashboard to fully load.
     * 
     * @return This DashboardPage instance
     */
    public DashboardPage waitForDashboardLoad() {
        waitForVisibility(dashboardHeader);
        waitForVisibility(accountSummary);
        waitForVisibility(transactions);
        LOGGER.info("Dashboard page fully loaded");
        return this;
    }
}
