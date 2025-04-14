package com.securetest.utils;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Factory class for creating WebDriver and AppiumDriver instances.
 * Supports various browsers and mobile platforms.
 */
public class DriverFactory {
    private static final Logger LOGGER = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<AppiumDriver> appiumDriverThreadLocal = new ThreadLocal<>();
    
    private DriverFactory() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Gets the WebDriver instance for the current thread.
     * 
     * @return The WebDriver instance
     */
    public static WebDriver getWebDriver() {
        return webDriverThreadLocal.get();
    }
    
    /**
     * Gets the AppiumDriver instance for the current thread.
     * 
     * @return The AppiumDriver instance
     */
    public static AppiumDriver getAppiumDriver() {
        return appiumDriverThreadLocal.get();
    }
    
    /**
     * Initializes a WebDriver based on browser name and settings.
     * 
     * @param browserName The browser to initialize (chrome, firefox, edge, safari)
     * @param headless Whether to run in headless mode
     * @return The initialized WebDriver
     */
    public static WebDriver initWebDriver(String browserName, boolean headless) {
        WebDriver driver;
        
        switch (browserName.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless");
                }
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-extensions");
                driver = new ChromeDriver(chromeOptions);
                break;
                
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("--headless");
                }
                firefoxOptions.addArguments("--no-sandbox");
                firefoxOptions.addArguments("--disable-dev-shm-usage");
                driver = new FirefoxDriver(firefoxOptions);
                break;
                
            case "edge":
                WebDriverManager.edgedriver().setup();
                // In Selenium 3.x, EdgeOptions did not have addArguments method
                // Using DesiredCapabilities instead for Edge
                DesiredCapabilities edgeCapabilities = DesiredCapabilities.edge();
                driver = new EdgeDriver(edgeCapabilities);
                break;
                
            case "safari":
                driver = new SafariDriver();
                break;
                
            default:
                LOGGER.warn("Browser '{}' not recognized, defaulting to Chrome", browserName);
                WebDriverManager.chromedriver().setup();
                ChromeOptions defaultOptions = new ChromeOptions();
                if (headless) {
                    defaultOptions.addArguments("--headless");
                }
                defaultOptions.addArguments("--no-sandbox");
                defaultOptions.addArguments("--disable-dev-shm-usage");
                defaultOptions.addArguments("--disable-extensions");
                driver = new ChromeDriver(defaultOptions);
                break;
        }
        
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        
        webDriverThreadLocal.set(driver);
        LOGGER.info("Initialized WebDriver for browser: {}", browserName);
        return driver;
    }
    
    /**
     * Initializes an AppiumDriver for mobile automation.
     * 
     * @param platformName The mobile platform (android, ios)
     * @param deviceName The device name to use
     * @param udid The device UDID (optional)
     * @param appPackage The app package name (for Android)
     * @param appActivity The app activity name (for Android)
     * @param bundleId The app bundle ID (for iOS)
     * @return The initialized AppiumDriver
     */
    public static AppiumDriver initAppiumDriver(String platformName, String deviceName, 
                                               String udid, String appPackage, 
                                               String appActivity, String bundleId) {
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(MobileCapabilityType.PLATFORM_NAME, platformName);
            capabilities.setCapability(MobileCapabilityType.DEVICE_NAME, deviceName);
            
            if (udid != null && !udid.isEmpty()) {
                capabilities.setCapability(MobileCapabilityType.UDID, udid);
            }
            
            capabilities.setCapability(MobileCapabilityType.AUTOMATION_NAME, 
                    platformName.equalsIgnoreCase("android") ? "UiAutomator2" : "XCUITest");
            capabilities.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 60);
            
            String appiumServerUrl = PropertyManager.getProperty("appium.server.url", "http://localhost:4723/wd/hub");
            
            AppiumDriver driver;
            if (platformName.equalsIgnoreCase("android")) {
                capabilities.setCapability("appPackage", appPackage);
                capabilities.setCapability("appActivity", appActivity);
                driver = new AndroidDriver(new URL(appiumServerUrl), capabilities);
            } else {
                capabilities.setCapability("bundleId", bundleId);
                driver = new IOSDriver(new URL(appiumServerUrl), capabilities);
            }
            
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            appiumDriverThreadLocal.set(driver);
            LOGGER.info("Initialized AppiumDriver for platform: {}, device: {}", platformName, deviceName);
            return driver;
        } catch (Exception e) {
            LOGGER.error("Failed to initialize AppiumDriver: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize AppiumDriver", e);
        }
    }
    
    /**
     * Closes the WebDriver for the current thread and removes it from ThreadLocal.
     */
    public static void quitWebDriver() {
        WebDriver driver = webDriverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            webDriverThreadLocal.remove();
            LOGGER.info("WebDriver closed successfully");
        }
    }
    
    /**
     * Closes the AppiumDriver for the current thread and removes it from ThreadLocal.
     */
    public static void quitAppiumDriver() {
        AppiumDriver driver = appiumDriverThreadLocal.get();
        if (driver != null) {
            driver.quit();
            appiumDriverThreadLocal.remove();
            LOGGER.info("AppiumDriver closed successfully");
        }
    }
}
