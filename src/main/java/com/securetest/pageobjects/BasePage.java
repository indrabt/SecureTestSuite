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
 * Base class for all web page objects.
 * Provides common functionality for web interactions.
 */
public abstract class BasePage {
    private static final Logger LOGGER = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected final int DEFAULT_TIMEOUT = 30;
    
    /**
     * Constructor for BasePage.
     * 
     * @param driver The WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Waits for an element to be clickable and then clicks it.
     * 
     * @param element The element to click
     */
    protected void click(WebElement element) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            LOGGER.debug("Clicked element: {}", element);
        } catch (Exception e) {
            LOGGER.error("Failed to click element: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Clicks an element using JavaScript.
     * Useful for elements that are not directly clickable.
     * 
     * @param element The element to click
     */
    protected void jsClick(WebElement element) {
        try {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", element);
            LOGGER.debug("JavaScript clicked element: {}", element);
        } catch (Exception e) {
            LOGGER.error("Failed to JavaScript click element: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Types text into an input field.
     * Clears the field first, then types the text.
     * 
     * @param element The input element
     * @param text The text to type
     * @param sensitive Whether the text is sensitive (passwords, etc.)
     */
    protected void type(WebElement element, String text, boolean sensitive) {
        try {
            wait.until(ExpectedConditions.visibilityOf(element));
            element.clear();
            element.sendKeys(text);
            
            if (sensitive) {
                LOGGER.debug("Typed sensitive text into element: {}", element);
            } else {
                LOGGER.debug("Typed text '{}' into element: {}", text, element);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to type into element: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Waits for an element to be visible.
     * 
     * @param element The element to wait for
     * @return The WebElement once visible
     */
    protected WebElement waitForVisibility(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Waits for an element to be visible using a locator.
     * 
     * @param locator The locator to find the element
     * @return The WebElement once visible
     */
    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
    
    /**
     * Waits for an element to be clickable.
     * 
     * @param element The element to wait for
     * @return The WebElement once clickable
     */
    protected WebElement waitForClickability(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }
    
    /**
     * Checks if an element is displayed.
     * 
     * @param element The element to check
     * @return true if the element is displayed, false otherwise
     */
    protected boolean isElementDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Scrolls to an element using JavaScript.
     * 
     * @param element The element to scroll to
     */
    protected void scrollToElement(WebElement element) {
        try {
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].scrollIntoView(true);", element);
            LOGGER.debug("Scrolled to element: {}", element);
        } catch (Exception e) {
            LOGGER.error("Failed to scroll to element: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Gets the page title.
     * 
     * @return The page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
    
    /**
     * Gets the current page URL.
     * 
     * @return The current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
