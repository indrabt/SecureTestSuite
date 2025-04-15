#!/bin/bash

# Secure Test Automation Framework Runner
# This script simplifies running the test framework with command-line arguments

# Display usage information
function show_usage {
  echo "Secure Test Automation Framework"
  echo ""
  echo "Usage: $0 [options]"
  echo ""
  echo "Options:"
  echo "  -u, --username USERNAME    Username for authentication (required)"
  echo "  -p, --password PASSWORD    Password for authentication (required)"
  echo "  -a, --apikey API_KEY       API key for API tests"
  echo "  -b, --browser BROWSER      Browser to use (chrome, firefox, edge, safari)"
  echo "  --headless                 Run in headless mode"
  echo "  --device DEVICE            Mobile device name for Appium tests"
  echo "  -h, --help                 Show this help message"
  echo ""
  echo "Examples:"
  echo "  $0 -u myusername -p mypassword                # Basic web test"
  echo "  $0 -u myusername -p mypassword -b firefox     # Test with Firefox"
  echo "  $0 -u myusername -p mypassword -a myapikey    # API test"
  echo ""
}

# Check if help is requested
if [[ "$1" == "-h" || "$1" == "--help" || $# -eq 0 ]]; then
  show_usage
  exit 0
fi

# Build the project if needed
if [ ! -f "target/secure-test-automation-1.0-SNAPSHOT.jar" ]; then
  echo "Building project..."
  mvn clean package -DskipTests
  
  # Check if build was successful
  if [ $? -ne 0 ]; then
    echo "Build failed. Please fix the errors and try again."
    exit 1
  fi
fi

# Run the tests with all provided arguments
echo "Running tests..."
java -jar target/secure-test-automation-1.0-SNAPSHOT.jar "$@"