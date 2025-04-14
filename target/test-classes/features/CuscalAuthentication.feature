Feature: Cuscal Portal Authentication
  As a user
  I want to authenticate to the Cuscal portal securely
  So that I can access my banking information

  Background:
    Given I navigate to the Cuscal portal

  @login @smoke
  Scenario: Successful login with valid credentials and OTP
    When I enter my username
    And I enter my password
    And I click the login button
    Then I should see the OTP page
    When I initialize my mobile device for OTP retrieval
    And I retrieve the OTP from my mobile device
    And I enter the retrieved OTP
    And I click the OTP verify button
    Then I should see the dashboard page
    And I should see my account summary
    And I should see my transactions

  @login @negative
  Scenario: Failed login with invalid credentials
    When I enter username "invalid_user"
    And I enter password "invalid_password"
    And I click the login button
    Then I should see a login error message
    And I should see login error message containing "Invalid username or password"

  @otp @negative
  Scenario: Failed verification with invalid OTP
    When I enter my username
    And I enter my password
    And I click the login button
    Then I should see the OTP page
    When I enter OTP "123456"
    And I click the OTP verify button
    Then I should see an OTP error message
    And I should see OTP error message containing "Invalid verification code"

  @otp
  Scenario: Resend OTP
    When I enter my username
    And I enter my password
    And I click the login button
    Then I should see the OTP page
    When I click the resend OTP link
    And I initialize my mobile device for OTP retrieval
    And I retrieve the OTP from my mobile device
    And I enter the retrieved OTP
    And I click the OTP verify button
    Then I should see the dashboard page

  @logout
  Scenario: Successful logout
    When I enter my username
    And I enter my password
    And I click the login button
    Then I should see the OTP page
    When I initialize my mobile device for OTP retrieval
    And I retrieve the OTP from my mobile device
    And I enter the retrieved OTP
    And I click the OTP verify button
    Then I should see the dashboard page
    When I click the logout button
    Then I should see the login page
