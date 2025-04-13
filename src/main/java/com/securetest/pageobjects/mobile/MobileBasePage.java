package com.securetest.pageobjects.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
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
 * Base class for all mobile page objects.
 * Provides common functionality for mobile app interactions.
 */
public abstract class MobileBasePage {
    private static final Logger LOGGER = LogManager.getLogger(MobileBasePage.class);
    protected AppiumDriver driver;
    protected WebDriverWait wait;
    protected final int DEFAULT_TIMEOUT = 30;
    
    /**
     * Constructor for MobileBasePage.
     * 
     * @param driver The AppiumDriver instance
     */
    public MobileBasePage(AppiumDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
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
            LOGGER.debug("Clicked mobile element: {}", element);
        } catch (Exception e) {
            LOGGER.error("Failed to click mobile element: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Types text into an input field.
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
                LOGGER.debug("Typed sensitive text into mobile element: {}", element);
            } else {
                LOGGER.debug("Typed text '{}' into mobile element: {}", text, element);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to type into mobile element: {}", e.getMessage());
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
     * Finds an element by its accessibility ID.
     * 
     * @param accessibilityId The accessibility ID
     * @return The WebElement found
     */
    protected WebElement findByAccessibilityId(String accessibilityId) {
        return driver.findElement(MobileBy.AccessibilityId(accessibilityId));
    }
    
    /**
     * Finds an element by XPath.
     * 
     * @param xpath The XPath expression
     * @return The WebElement found
     */
    protected WebElement findByXPath(String xpath) {
        return driver.findElement(MobileBy.xpath(xpath));
    }
    
    /**
     * Finds an element by ID.
     * 
     * @param id The ID
     * @return The WebElement found
     */
    protected WebElement findById(String id) {
        return driver.findElement(MobileBy.id(id));
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
     * Gets the text of an element.
     * 
     * @param element The element to get text from
     * @return The text of the element
     */
    protected String getText(WebElement element) {
        return waitForVisibility(element).getText();
    }
}
