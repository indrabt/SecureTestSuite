package com.securetest.tests;

import com.aventstack.extentreports.ExtentTest;
import com.securetest.runner.TestRunner;
import com.securetest.utils.CommandLineParser;
import com.securetest.utils.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;

/**
 * Base class for all JUnit tests.
 * Provides common setup and teardown methods.
 */
public class BaseTest {
    protected static final Logger LOGGER = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected ExtentTest test;
    
    /**
     * Setup method run before each test.
     * Initializes WebDriver, reporting, and configuration.
     */
    @Before
    public void baseSetUp() {
        LOGGER.info("Setting up test environment");
        
        // Get browser from command line or use default
        String browser = CommandLineParser.getOptionValue("b", "chrome");
        boolean headless = CommandLineParser.getBooleanOption("headless", false);
        
        // Initialize driver
        driver = DriverFactory.createDriver(browser, headless);
        LOGGER.info("WebDriver initialized for browser: {}, headless: {}", browser, headless);
        
        // Initialize test reporting
        test = TestRunner.getExtentReports().createTest(getClass().getSimpleName());
        test.info("Test started");
        
        LOGGER.info("Test environment setup complete");
    }
    
    /**
     * Teardown method run after each test.
     * Closes WebDriver and finalizes reporting.
     */
    @After
    public void baseTearDown() {
        LOGGER.info("Tearing down test environment");
        
        // Close browser
        if (driver != null) {
            driver.quit();
            LOGGER.info("WebDriver closed");
        }
        
        // Update test reporting
        if (test != null) {
            test.info("Test completed");
        }
        
        LOGGER.info("Test environment teardown complete");
    }
}