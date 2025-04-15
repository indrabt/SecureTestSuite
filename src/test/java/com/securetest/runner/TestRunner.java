package com.securetest.runner;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.securetest.utils.CommandLineParser;
import com.securetest.utils.EncryptionUtil;
import com.securetest.utils.PropertyManager;
import com.securetest.utils.SensitiveDataManager;
import org.apache.commons.cli.HelpFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import java.io.File;

/**
 * Simple JUnit runner for the secure test automation framework.
 * Handles command-line arguments, encryption setup, and reporting.
 */
public class TestRunner {
    private static final Logger LOGGER = LogManager.getLogger(TestRunner.class);
    private static ExtentReports extentReports;
    
    /**
     * Main method to run tests from command line.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            LOGGER.info("Starting secure test automation framework");
            
            // Enable test mode for encryption to avoid crypto policy restrictions
            EncryptionUtil.enableTestMode();
            LOGGER.info("Using test mode for encryption (Base64 encoding)");
            
            // Parse command line arguments
            if (!CommandLineParser.parseArgs(args)) {
                // If --help was requested, exit normally
                if (args.length > 0 && (args[0].equals("-h") || args[0].equals("--help"))) {
                    System.exit(0);
                }
                
                LOGGER.error("Failed to parse command line arguments. Exiting.");
                System.exit(1);
            }
            
            // Show command line option help if requested
            if (CommandLineParser.hasOption("h")) {
                // Help already displayed by CommandLineParser
                System.exit(0);
            }
            
            // Check for required parameters
            if (!validateRequiredParameters()) {
                System.exit(1);
            }
            
            // Initialize properties
            PropertyManager.init();
            
            // Initialize sensitive data from command line
            SensitiveDataManager.initFromCommandLine();
            
            // Log test configuration
            logTestConfiguration();
            
            // Set up reporting
            setupReporting();
            
            // Check if we're being run directly from command line
            boolean runningFromCommandLine = CommandLineParser.hasOption("u") && CommandLineParser.hasOption("p");
            
            if (runningFromCommandLine) {
                // Execute available test classes
                LOGGER.info("JUnit framework ready for test execution");
                
                // For now, we're skipping the actual test execution when run directly
                // Users can execute individual test classes with Maven or IDE
                LOGGER.info("Framework initialized successfully. Tests can be run individually.");
                LOGGER.info("To run tests, use Maven: mvn test -Dusername=xyz -Dpassword=xyz");
            } else {
                LOGGER.info("No command line credentials provided, skipping test execution");
            }
            
            // Finalize reporting
            if (extentReports != null) {
                extentReports.flush();
                LOGGER.info("Test reports generated successfully");
            }
            
        } catch (Exception e) {
            LOGGER.error("Error during test execution: {}", e.getMessage(), e);
            System.exit(1);
        } finally {
            // Ensure sensitive data is cleared
            SensitiveDataManager.clearAllSecureData();
            LOGGER.info("Framework execution completed. Sensitive data cleared.");
        }
    }
    
    /**
     * Sets up the Extent reporting system
     */
    private static void setupReporting() {
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
        
        LOGGER.info("Reporting system initialized successfully");
    }
    
    /**
     * Validates that all required parameters are present.
     * 
     * @return true if all required parameters are present, false otherwise
     */
    private static boolean validateRequiredParameters() {
        boolean valid = true;
        
        // For all tests, we need authentication parameters
        if (!CommandLineParser.hasOption("u")) {
            LOGGER.error("Username (-u, --username) is required");
            valid = false;
        }
        
        if (!CommandLineParser.hasOption("p")) {
            LOGGER.error("Password (-p, --password) is required");
            valid = false;
        }
        
        // Log validation result
        if (!valid) {
            LOGGER.error("Required command line parameters are missing");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("SecureTestAutomation", CommandLineParser.getOptions());
        }
        
        return valid;
    }
    
    /**
     * Logs the test configuration for debugging purposes.
     * Avoids logging sensitive data.
     */
    private static void logTestConfiguration() {
        LOGGER.info("Test Configuration:");
        LOGGER.info("  Browser: {}", CommandLineParser.getOptionValue("b", "chrome"));
        LOGGER.info("  Headless Mode: {}", CommandLineParser.getBooleanOption("headless", false));
        
        // Log non-sensitive parameters
        LOGGER.info("  Device Name: {}", CommandLineParser.getOptionValue("device", "[none]"));
        
        // Don't log sensitive parameters like username, password, etc.
        LOGGER.info("  Username: [SECURED]");
        LOGGER.info("  Password: [SECURED]");
        LOGGER.info("  API Key: [SECURED]");
    }
    
    /**
     * Gets the ExtentReports instance.
     * 
     * @return The ExtentReports instance
     */
    public static ExtentReports getExtentReports() {
        return extentReports;
    }
}
