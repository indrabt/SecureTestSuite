# Secure Test Automation Framework

A Java-based test automation framework for web and mobile apps using Cucumber, Selenium, and Appium with enhanced security features for sensitive data handling.

## Features

- **Secure credential handling:** Uses in-memory encryption for sensitive data
- **Command-line driven:** All credentials are input via command-line, never stored in code or config files
- **Multi-platform support:** Works with Chrome, Firefox, Edge, and Safari browsers
- **Mobile automation:** Full Appium integration for Android and iOS
- **BDD approach:** Uses Cucumber for behavior-driven test definitions
- **Reporting:** Integrated with ExtentReports for comprehensive test reporting

## Project Structure

- `src/main/java/com/securetest/pageobjects`: Page Object Models for web and mobile
- `src/main/java/com/securetest/utils`: Utility classes for drivers, encryption, etc.
- `src/test/java/com/securetest/stepdefinitions`: Cucumber step definitions
- `src/test/java/com/securetest/runner`: Test runner for Cucumber tests
- `src/test/resources/features`: Cucumber feature files
- `src/test/resources/config.properties`: Configuration settings

## Requirements

- Java 18 or later
- Maven 3.8 or later
- For web testing: Browsers and WebDrivers (managed automatically via WebDriverManager)
- For mobile testing: Appium server and mobile devices/emulators

## Getting Started

1. Clone the repository
2. Run `mvn clean install` to build the project
3. Update `src/test/resources/config.properties` with your environment settings

## Running Tests

### Command Line

Run tests with the provided scripts:

```bash
# On Linux/Mac:
./run.sh -u <username> -p <password> -d <device-name> 

# On Windows:
run.bat -u <username> -p <password> -d <device-name>
```

### Command Line Options

- `-u, --username`: Username for Cuscal portal login
- `-p, --password`: Password for Cuscal portal login
- `-d, --device`: Mobile device name for OTP retrieval
- `-b, --browser`: Browser to use (chrome, firefox, edge, safari)
- `--headless`: Run in headless mode
- `-t, --tags`: Cucumber tags for test filtering
- `--parallel`: Run tests in parallel

## Security Features

- All sensitive data is encrypted in memory using AES-256
- No plaintext credentials in logs or reports
- Command-line entry for credentials with no storage
- Secure disposal of sensitive data after test execution

## Example Test

```gherkin
Feature: Cuscal Portal Authentication
  As a user
  I want to authenticate to the Cuscal portal securely
  So that I can access my banking information

  @login @smoke
  Scenario: Successful login with valid credentials and OTP
    Given I navigate to the Cuscal portal
    When I enter my username
    And I enter my password
    And I click the login button
    Then I should see the OTP page
    When I initialize my mobile device for OTP retrieval
    And I retrieve the OTP from my mobile device
    And I enter the retrieved OTP
    And I click the OTP verify button
    Then I should see the dashboard page
```

## License

Copyright (c) 2025 SecureTest
