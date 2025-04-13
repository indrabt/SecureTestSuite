#!/bin/bash

# Run the test automation framework with command line arguments
java -cp target/secure-test-automation-1.0-SNAPSHOT.jar com.securetest.runner.TestRunner "$@"
