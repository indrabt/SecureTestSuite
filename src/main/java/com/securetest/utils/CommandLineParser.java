package com.securetest.utils;

import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Utility to parse command line arguments to the test framework.
 * Handles extraction of sensitive data from command line.
 */
public class CommandLineParser {
    private static final Logger LOGGER = LogManager.getLogger(CommandLineParser.class);
    
    private static final Options options = new Options();
    private static CommandLine cmd;
    
    static {
        // Define command line options
        Option username = Option.builder("u")
                .longOpt("username")
                .desc("Username for login")
                .hasArg()
                .required(false)
                .build();
        
        Option password = Option.builder("p")
                .longOpt("password")
                .desc("Password for login")
                .hasArg()
                .required(false)
                .build();
        
        Option apiKey = Option.builder("a")
                .longOpt("apikey")
                .desc("API Key for authentication")
                .hasArg()
                .required(false)
                .build();
        
        Option phoneNumber = Option.builder("phone")
                .longOpt("phone-number")
                .desc("Phone number for OTP")
                .hasArg()
                .required(false)
                .build();
        
        Option userId = Option.builder("id")
                .longOpt("user-id")
                .desc("User ID for authentication")
                .hasArg()
                .required(false)
                .build();
        
        Option phoneName = Option.builder("device")
                .longOpt("device-name")
                .desc("Mobile device name for Appium")
                .hasArg()
                .required(false)
                .build();
        
        Option browser = Option.builder("b")
                .longOpt("browser")
                .desc("Browser to run tests on (chrome, firefox, edge)")
                .hasArg()
                .required(false)
                .build();
        
        Option tags = Option.builder("t")
                .longOpt("tags")
                .desc("Cucumber tags to execute")
                .hasArg()
                .required(false)
                .build();
        
        Option parallel = Option.builder("parallel")
                .longOpt("parallel")
                .desc("Enable parallel execution (true/false)")
                .hasArg()
                .required(false)
                .build();
        
        Option headless = Option.builder("headless")
                .longOpt("headless")
                .desc("Run in headless mode (true/false)")
                .hasArg()
                .required(false)
                .build();
                
        Option help = Option.builder("h")
                .longOpt("help")
                .desc("Display help information")
                .required(false)
                .build();
                
        // Add all options
        options.addOption(username);
        options.addOption(password);
        options.addOption(apiKey);
        options.addOption(phoneNumber);
        options.addOption(userId);
        options.addOption(phoneName);
        options.addOption(browser);
        options.addOption(tags);
        options.addOption(parallel);
        options.addOption(headless);
        options.addOption(help);
    }
    
    /**
     * Parses command line arguments.
     * 
     * @param args Arguments passed to the application
     * @return true if parsing was successful, false otherwise
     */
    public static boolean parseArgs(String[] args) {
        DefaultParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        
        try {
            cmd = parser.parse(options, args);
            
            // Print help and exit if help option is present
            if (cmd.hasOption("h")) {
                formatter.printHelp("SecureTestAutomation", 
                    "\nSecure Test Automation Framework\n\n" +
                    "Example: ./run.sh -u admin -p password -b chrome --headless true\n", 
                    options,
                    "\nFor more information, see README.md", true);
                return false;
            }
            
            return true;
        } catch (ParseException e) {
            LOGGER.error("Error parsing command line arguments: {}", e.getMessage());
            formatter.printHelp("SecureTestAutomation", options);
            return false;
        }
    }
    
    /**
     * Gets a command line parameter value.
     * 
     * @param option The option to get
     * @return The option value or null if not provided
     */
    public static String getOptionValue(String option) {
        if (cmd == null) {
            return null;
        }
        
        return cmd.getOptionValue(option);
    }
    
    /**
     * Gets a command line parameter value with a default.
     * 
     * @param option The option to get
     * @param defaultValue The default value if option not provided
     * @return The option value or the default if not provided
     */
    public static String getOptionValue(String option, String defaultValue) {
        if (cmd == null) {
            return defaultValue;
        }
        
        return cmd.getOptionValue(option, defaultValue);
    }
    
    /**
     * Gets a boolean command line parameter value.
     * 
     * @param option The option to get
     * @param defaultValue The default value if option not provided
     * @return The boolean value of the option or the default
     */
    public static boolean getBooleanOption(String option, boolean defaultValue) {
        if (cmd == null) {
            return defaultValue;
        }
        
        String value = cmd.getOptionValue(option);
        if (value == null) {
            return defaultValue;
        }
        
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Check if a specific option was provided.
     * 
     * @param option The option to check
     * @return true if the option was provided, false otherwise
     */
    public static boolean hasOption(String option) {
        if (cmd == null) {
            return false;
        }
        
        return cmd.hasOption(option);
    }
    
    /**
     * Gets the Options object for help display.
     * 
     * @return The Options object
     */
    public static Options getOptions() {
        return options;
    }
    
    /**
     * Gets a securely encrypted value for a command line option.
     * 
     * @param option The option name
     * @return An encrypted value, or null if not provided
     */
    public static String getEncryptedValue(String option) {
        String value = getOptionValue(option);
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            return EncryptionUtil.encrypt(value);
        } catch (Exception e) {
            LOGGER.error("Failed to encrypt option '{}': {}", option, e.getMessage());
            return null;
        }
    }
    
    /**
     * Gets a securely encrypted username from command line.
     * 
     * @return An encrypted username, or null if not provided
     */
    public static String getEncryptedUsername() {
        return getEncryptedValue("u");
    }
    
    /**
     * Gets a securely encrypted password from command line.
     * 
     * @return An encrypted password, or null if not provided
     */
    public static String getEncryptedPassword() {
        return getEncryptedValue("p");
    }
    
    /**
     * Gets a securely encrypted API key from command line.
     * 
     * @return An encrypted API key, or null if not provided
     */
    public static String getEncryptedApiKey() {
        return getEncryptedValue("a");
    }
}
