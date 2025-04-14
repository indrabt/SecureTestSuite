package com.securetest.pageobjects.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base page object for mobile application screens.
 * Contains common mobile interactions and utilities.
 */
public abstract class MobileBasePage {
    protected static final Logger LOGGER = LogManager.getLogger(MobileBasePage.class);
    protected AppiumDriver driver;
    protected WebDriverWait wait;
    protected final int DEFAULT_TIMEOUT = 30;
    
    /**
     * Constructor
     * 
     * @param driver AppiumDriver instance
     */
    public MobileBasePage(AppiumDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
        LOGGER.debug("Initialized mobile page object: {}", this.getClass().getSimpleName());
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
            LOGGER.error("Error clicking mobile element: {}", e.getMessage());
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
            LOGGER.error("Error entering text on mobile: {}", e.getMessage());
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
            LOGGER.debug("Typed text into mobile element: {}", element);
        } catch (Exception e) {
            LOGGER.error("Error typing text on mobile: {}", e.getMessage());
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
     * Get text from element with wait
     * 
     * @param element WebElement to get text from
     * @return text content of element
     */
    protected String getText(WebElement element) {
        waitForElementVisible(element);
        return element.getText();
    }
    
    /**
     * Tap on coordinates
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     */
    protected void tapByCoordinates(int x, int y) {
        try {
            driver.executeScript("mobile: tap", x, y);
            LOGGER.debug("Tapped on coordinates x={}, y={}", x, y);
        } catch (Exception e) {
            LOGGER.error("Error tapping on coordinates: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Swipe from one point to another
     * 
     * @param startX starting x-coordinate
     * @param startY starting y-coordinate
     * @param endX ending x-coordinate
     * @param endY ending y-coordinate
     * @param duration duration of swipe in milliseconds
     */
    protected void swipe(int startX, int startY, int endX, int endY, int duration) {
        try {
            driver.executeScript("mobile: swipe", startX, startY, endX, endY, duration);
            LOGGER.debug("Swiped from ({},{}) to ({},{})", startX, startY, endX, endY);
        } catch (Exception e) {
            LOGGER.error("Error swiping: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get OTP code from notifications or SMS
     * This is a placeholder method to be implemented by specific pages
     * 
     * @return OTP code as string
     */
    public String getOtpCode() {
        throw new UnsupportedOperationException("getOtpCode must be implemented in subclass");
    }
}