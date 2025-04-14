package com.securetest.pageobjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base page class with common web element interaction methods.
 * All page objects should extend this class.
 */
public abstract class BasePage {
    protected static final Logger LOGGER = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final int DEFAULT_TIMEOUT = 30;
    
    /**
     * Constructor
     * 
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
        LOGGER.debug("Initialized page object: {}", this.getClass().getSimpleName());
    }
    
    /**
     * Wait for element to be clickable and click on it
     * 
     * @param element WebElement to click
     */
    protected void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (Exception e) {
            LOGGER.error("Error clicking element: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Click on element using JavaScript
     * 
     * @param element WebElement to click
     */
    protected void jsClick(WebElement element) {
        try {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            LOGGER.error("Error JS clicking element: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Wait for element to be visible and enter text
     * 
     * @param element WebElement to type in
     * @param text Text to type
     */
    protected void sendKeys(WebElement element, String text) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            element.clear();
            element.sendKeys(text);
        } catch (Exception e) {
            LOGGER.error("Error entering text: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Alternative method for typing text into elements with option to clear first
     * 
     * @param element WebElement to type in
     * @param text Text to type
     * @param clearFirst Whether to clear the field first
     */
    protected void type(WebElement element, String text, boolean clearFirst) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            if (clearFirst) {
                element.clear();
            }
            element.sendKeys(text);
            LOGGER.debug("Typed text into element: {}", element);
        } catch (Exception e) {
            LOGGER.error("Error typing text: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Wait for element to be visible
     * 
     * @param element WebElement to wait for
     * @return WebElement that is now visible
     */
    protected WebElement waitForElementVisible(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Alternative name for waitForElementVisible
     * 
     * @param element WebElement to wait for
     * @return WebElement that is now visible
     */
    protected WebElement waitForVisibility(WebElement element) {
        return waitForElementVisible(element);
    }
    
    /**
     * Wait for element to be visible based on locator
     * 
     * @param locator By locator
     * @return WebElement that is now visible
     */
    protected WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /**
     * Check if element is displayed with a timeout
     * 
     * @param element WebElement to check
     * @return true if element is displayed, false otherwise
     */
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return waitForElementVisible(element).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get page title
     * 
     * @return page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    /**
     * Get current URL
     * 
     * @return current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    /**
     * Wait for page to load completely
     */
    protected void waitForPageToLoad() {
        wait.until(webDriver ->
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }
}