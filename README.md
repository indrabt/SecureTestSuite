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

### Credential Security

- All credentials are input via command line at runtime, never stored in code or config files
- Sensitive data is encrypted in memory using AES encryption (or Base64 encoding in test mode)
- No logging of actual credential values (masked in logs with patterns like `[SECURED]`)
- Secure cleanup of sensitive data after test execution with explicit memory clearing

### Implementation Details

- `SensitiveDataManager`: Central class for securely storing and accessing credentials
- `EncryptionUtil`: Handles encryption/decryption of sensitive data
- `CommandLineParser`: Securely processes command-line arguments containing credentials
- Memory hygiene: Explicit clearing of sensitive data after use

### Best Practices Enforced

- Credentials are never stored in test code or config files
- Credentials are never logged, even in debug mode
- Secure handling of OTPs and session tokens
- All tests use the secure credential access pattern

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

### Example Web Test

```java
public class MyWebTest extends BaseTest {
    @Test
    public void testLogin() {
        // Get secure credentials
        String username = SensitiveDataManager.getSecureData("username");
        String password = SensitiveDataManager.getSecureData("password");
        
        // Navigate to the login page
        driver.get("https://example.com/login");
        
        // Enter credentials (never log the actual values)
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        
        // Submit the form
        driver.findElement(By.id("loginButton")).click();
        
        // Verify successful login
        assertTrue(driver.findElement(By.id("welcomeMessage")).isDisplayed());
    }
}
```

### Example API Test

```java
public class MyApiTest {
    @Test
    public void testApiAccess() throws IOException {
        // Get secure API key
        String apiKey = SensitiveDataManager.getSecureData("apiKey");
        assertNotNull("API Key not found", apiKey);
        
        // Set up HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("https://api.example.com/data");
        
        // Add secure authentication header
        request.addHeader("Authorization", "Bearer " + apiKey);
        
        // Execute request and verify response
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            assertEquals(200, statusCode);
            
            // Process response
            String responseBody = EntityUtils.toString(response.getEntity());
            JSONObject jsonResponse = new JSONObject(responseBody);
            assertTrue(jsonResponse.has("success"));
        }
    }
}
```

### Example Mobile Test

```java
public class MyMobileTest {
    private AppiumDriver<MobileElement> driver;
    
    @Before
    public void setUp() {
        driver = AppiumHelper.initializeDriver("Android Device");
    }
    
    @Test
    public void testMobileApp() {
        // Get secure credentials
        String username = SensitiveDataManager.getSecureData("username");
        String password = SensitiveDataManager.getSecureData("password");
        
        // Enter credentials in app
        driver.findElement(By.id("username_field")).sendKeys(username);
        driver.findElement(By.id("password_field")).sendKeys(password);
        driver.findElement(By.id("login_button")).click();
        
        // Extract OTP if needed
        String otp = AppiumHelper.retrieveOtpFromSms(driver, "BankApp", 30);
        assertNotNull("Failed to retrieve OTP", otp);
        
        // Use OTP for authentication
        driver.findElement(By.id("otp_field")).sendKeys(otp);
        driver.findElement(By.id("verify_button")).click();
        
        // Verify successful authentication
        assertTrue(driver.findElement(By.id("welcome_screen")).isDisplayed());
    }
    
    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

## Reports

Test reports are generated in the `test-output/extent-reports` directory after test execution.

## License

Copyright © 2025 - All rights reserved