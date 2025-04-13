package com.securetest.stepdefinitions;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.securetest.runner.TestRunner;
import com.securetest.utils.CommandLineParser;
import com.securetest.utils.DriverFactory;
import com.securetest.utils.PropertyManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Cucumber hooks for test setup and teardown.
 * Handles driver initialization, reporting, and cleanup.
 */
public class Hooks {
    private static final Logger LOGGER = LogManager.getLogger(Hooks.class);
    private static final ThreadLocal<ExtentTest> scenarioTestThreadLocal = new ThreadLocal<>();
    
    /**
     * Setup method run before each scenario.
     * Initializes WebDriver and test reporting.
     * 
     * @param scenario The Cucumber scenario
     */
    @Before
    public void setUp(Scenario scenario) {
        LOGGER.info("Starting scenario: {}", scenario.getName());
        
        // Initialize WebDriver
        String browser = CommandLineParser.getOptionValue("b", "chrome");
        boolean headless = CommandLineParser.getBooleanOption("headless", false);
        DriverFactory.initWebDriver(browser, headless);
        
        // Set up reporting
        String featureName = scenario.getSourceTagNames().toString();
        ExtentTest featureTest = TestRunner.getFeatureTest(featureName);
        ExtentTest scenarioTest = featureTest.createNode(scenario.getName());
        scenarioTestThreadLocal.set(scenarioTest);
        
        LOGGER.info("WebDriver initialized for browser: {}, headless: {}", browser, headless);
    }
    
    /**
     * Teardown method run after each scenario.
     * Cleans up WebDriver and finalizes reporting.
     * 
     * @param scenario The Cucumber scenario
     */
    @After
    public void tearDown(Scenario scenario) {
        LOGGER.info("Finishing scenario: {}, Status: {}", scenario.getName(), scenario.getStatus());
        
        WebDriver driver = DriverFactory.getWebDriver();
        
        // Capture screenshot if scenario failed
        if (scenario.isFailed() && driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Screenshot on failure");
                
                // Add screenshot to extent report
                ExtentTest scenarioTest = scenarioTestThreadLocal.get();
                if (scenarioTest != null) {
                    scenarioTest.log(Status.FAIL, "Scenario failed. See attached screenshot.");
                    scenarioTest.addScreenCaptureFromBase64String(
                            java.util.Base64.getEncoder().encodeToString(screenshot),
                            "Failure Screenshot");
                }
                
                LOGGER.info("Captured failure screenshot for scenario: {}", scenario.getName());
            } catch (Exception e) {
                LOGGER.error("Failed to capture screenshot: {}", e.getMessage());
            }
        }
        
        // Update scenario status in extent report
        ExtentTest scenarioTest = scenarioTestThreadLocal.get();
        if (scenarioTest != null) {
            if (scenario.isFailed()) {
                scenarioTest.log(Status.FAIL, "Scenario failed");
            } else {
                scenarioTest.log(Status.PASS, "Scenario passed");
            }
        }
        
        // Quit WebDriver
        DriverFactory.quitWebDriver();
        
        // Quit AppiumDriver if initialized
        DriverFactory.quitAppiumDriver();
        
        LOGGER.info("WebDriver and AppiumDriver closed");
    }
    
    /**
     * Method run after each step in a scenario.
     * Updates reporting with step details.
     * 
     * @param scenario The Cucumber scenario
     */
    @AfterStep
    public void afterStep(Scenario scenario) {
        ExtentTest scenarioTest = scenarioTestThreadLocal.get();
        if (scenarioTest != null) {
            // Get the last step
            String lastStepText = scenario.getName(); // Simplified for this example
            
            if (scenario.getStatus().toString().equals("PASSED")) {
                scenarioTest.log(Status.PASS, lastStepText);
            } else if (scenario.getStatus().toString().equals("FAILED")) {
                scenarioTest.log(Status.FAIL, lastStepText);
            } else {
                scenarioTest.log(Status.INFO, lastStepText);
            }
        }
    }
}
