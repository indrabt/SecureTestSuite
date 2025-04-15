# Secure Test Automation Framework

A secure Java-based test automation framework using JUnit, Selenium, and Appium for web and mobile testing with sensitive credential protection.

## Overview

This framework is designed for automating banking authentication flows securely, with a focus on:

1. **Security**: Encryption for all sensitive data, no plaintext credentials in code or config files
2. **Cross-platform**: Web automation with Selenium, mobile automation with Appium
3. **Structure**: JUnit 4.12 test structure with clear separation of concerns
4. **Flexibility**: Command-line based setup for CI/CD integration
5. **Reporting**: Integrated ExtentReports for detailed test execution reporting

## Security Features

- All credentials are input via command line at runtime
- Sensitive data is encrypted in memory
- No logging of actual credential values (masked in logs)
- Secure cleanup of sensitive data after test execution

## Requirements

- Java 8 JDK
- Maven 3.6+
- Chrome or Firefox browser for web tests
- Android SDK & Appium for mobile tests

## Project Structure

```
├── src/
│   ├── main/java/com/securetest/
│   │   ├── utils/           # Utility classes for encryption, drivers, etc.
│   │   └── ...
│   └── test/java/com/securetest/
│       ├── runner/          # Test runners and executors
│       ├── tests/           # Test classes
│       └── ...
├── test-output/             # Test reports and logs
├── pom.xml                  # Maven configuration
└── README.md                # This file
```

## Usage

### Running Tests from Command Line

```bash
mvn clean test -Dusername=your_username -Dpassword=your_password
```

### Command Line Options

| Option | Description | Required |
|--------|-------------|----------|
| `-u, --username` | Username for authentication | Yes |
| `-p, --password` | Password for authentication | Yes |
| `-a, --apikey` | API key for API tests | For API tests |
| `-b, --browser` | Browser to use (chrome/firefox) | No (default: chrome) |
| `--headless` | Run in headless mode | No (default: false) |
| `--device` | Mobile device name for Appium tests | For mobile tests |

### Example Commands

Web test with Chrome:
```bash
java -jar target/secure-test-automation-1.0-SNAPSHOT.jar -u myuser -p mypass -b chrome
```

API test:
```bash
java -jar target/secure-test-automation-1.0-SNAPSHOT.jar -u myuser -p mypass -a myapikey
```

Mobile test:
```bash
java -jar target/secure-test-automation-1.0-SNAPSHOT.jar -u myuser -p mypass --device "Pixel 4"
```

## Creating New Tests

To create a new test:

1. Create a new test class extending `BaseTest`
2. Add `@Test` methods for your test cases
3. Use `SensitiveDataManager` to access secure credentials

Example:
```java
public class MyTest extends BaseTest {
    @Test
    public void testSomething() {
        String username = SensitiveDataManager.getSecureData("username");
        // Test implementation
    }
}
```

## Reports

Test reports are generated in the `test-output/extent-reports` directory after test execution.

## License

Copyright © 2025 - All rights reserved