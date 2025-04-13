package com.securetest.runner;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.securetest.utils.CommandLineParser;
import com.securetest.utils.PropertyManager;
import com.securetest.utils.SensitiveDataManager;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * TestNG runner for Cucumber tests.
 * Handles test execution, reporting, and command-line arguments.
 */
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.securetest.stepdefinitions"},
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-pretty.html",
                "json:target/cucumber-reports/CucumberTestReport.json",
                "rerun:target/cucumber-reports/rerun.txt"
        },
        monochrome = true,
        publish = false
)
public class TestRunner extends AbstractTestNGCucumberTests {
    private static final Logger LOGGER = LogManager.getLogger(TestRunner.class);
    private static ExtentReports extentReports;
    private static final Map<String, ExtentTest> featureTestMap = new HashMap<>();
    
    /**
     * Main method to run tests from command line.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            LOGGER.info("Starting test execution from command line");
            
            // Parse command line arguments
            if (!CommandLineParser.parseArgs(args)) {
                LOGGER.error("Failed to parse command line arguments. Exiting.");
                System.exit(1);
            }
            
            // Initialize properties
            PropertyManager.init();
            
            // Initialize sensitive data from command line
            SensitiveDataManager.initFromCommandLine();
            
            // Set cucumber tags if provided
            String tags = CommandLineParser.getOptionValue("t");
            if (tags != null && !tags.isEmpty()) {
                System.setProperty("cucumber.filter.tags", tags);
                LOGGER.info("Running with cucumber tags: {}", tags);
            }
            
            // Execute tests using TestNG
            org.testng.TestNG testng = new org.testng.TestNG();
            testng.setTestClasses(new Class[] { TestRunner.class });
            testng.run();
            
        } catch (Exception e) {
            LOGGER.error("Error during test execution: {}", e.getMessage(), e);
            System.exit(1);
        } finally {
            // Ensure sensitive data is cleared
            SensitiveDataManager.clearAllSecureData();
        }
    }
    
    /**
     * Overrides the default data provider to support parallel execution.
     * 
     * @param context The TestNG context
     * @return The scenarios data provider
     */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios(ITestContext context) {
        boolean parallelExecution = CommandLineParser.getBooleanOption("parallel", false);
        
        if (parallelExecution) {
            LOGGER.info("Running tests in parallel mode");
            return super.scenarios(context);
        } else {
            LOGGER.info("Running tests in sequential mode");
            return super.scenarios(context);
        }
    }
    
    /**
     * Setup method run before the test suite.
     * Initializes reporting and configuration.
     */
    @BeforeSuite
    public void beforeSuite() {
        LOGGER.info("Initializing test suite");
        
        // Set up reporting directory
        String reportDir = "test-output/extent-reports";
        new File(reportDir).mkdirs();
        
        // Initialize Extent Reports
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter(reportDir + "/extent-report.html");
        htmlReporter.config().setDocumentTitle("Automation Test Report");
        htmlReporter.config().setReportName("Secure Test Automation Report");
        
        extentReports = new ExtentReports();
        extentReports.attachReporter(htmlReporter);
        extentReports.setSystemInfo("Environment", PropertyManager.getProperty("environment", "Test"));
        extentReports.setSystemInfo("Browser", CommandLineParser.getOptionValue("b", "chrome"));
        
        LOGGER.info("Test suite initialized successfully");
    }
    
    /**
     * Cleanup method run after the test suite.
     * Finalizes reporting and clears sensitive data.
     */
    @AfterSuite
    public void afterSuite() {
        LOGGER.info("Finalizing test suite");
        
        // Flush reports
        if (extentReports != null) {
            extentReports.flush();
            LOGGER.info("Test reports generated successfully");
        }
        
        // Clear sensitive data
        SensitiveDataManager.clearAllSecureData();
        LOGGER.info("Sensitive data cleared from memory");
        
        LOGGER.info("Test suite finalized successfully");
    }
    
    /**
     * Gets the ExtentReports instance.
     * 
     * @return The ExtentReports instance
     */
    public static ExtentReports getExtentReports() {
        return extentReports;
    }
    
    /**
     * Gets or creates an ExtentTest for a feature.
     * 
     * @param featureName The name of the feature
     * @return The ExtentTest for the feature
     */
    public static ExtentTest getFeatureTest(String featureName) {
        if (!featureTestMap.containsKey(featureName)) {
            ExtentTest featureTest = extentReports.createTest(featureName);
            featureTestMap.put(featureName, featureTest);
        }
        return featureTestMap.get(featureName);
    }
}
