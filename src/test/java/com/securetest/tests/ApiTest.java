package com.securetest.tests;

import com.securetest.utils.SensitiveDataManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example API test class for secure API testing.
 * Demonstrates secure handling of API keys and tokens.
 */
public class ApiTest {
    private static final Logger LOGGER = LogManager.getLogger(ApiTest.class);
    
    /**
     * Test method for API authentication with secure credentials.
     * Uses encrypted API key from command line.
     */
    @Test
    public void testSecureApiAuthentication() throws IOException {
        LOGGER.info("Starting secure API authentication test");
        
        // Get securely stored API key
        String apiKey = SensitiveDataManager.getSecureData("apiKey");
        assertNotNull("API Key not found in secure storage", apiKey);
        LOGGER.info("Retrieved securely stored API key");
        
        // Create HTTP client
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Example API endpoint (using a public test API)
            String apiUrl = "https://jsonplaceholder.typicode.com/posts/1";
            HttpGet request = new HttpGet(apiUrl);
            
            // Add authorization header with API key (never log the actual key)
            request.addHeader("Authorization", "Bearer " + apiKey);
            request.addHeader("Content-Type", "application/json");
            LOGGER.info("Added secure authorization header to request");
            
            // Execute request
            LOGGER.info("Sending API request to: {}", apiUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Verify response status
                int statusCode = response.getStatusLine().getStatusCode();
                LOGGER.info("Received API response with status code: {}", statusCode);
                
                // This is just a simulation with a public API, so we expect 200
                // even though we're adding an Authorization header that isn't needed
                assertEquals("Unexpected API response status", 200, statusCode);
                
                // Process response body
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                LOGGER.info("API response body length: {} characters", responseBody.length());
                
                // Verify response contains expected data
                JSONObject jsonResponse = new JSONObject(responseBody);
                assertNotNull("Response missing 'id' field", jsonResponse.get("id"));
                LOGGER.info("API response contains expected fields");
            }
        }
        
        LOGGER.info("Secure API authentication test completed successfully");
    }
    
    /**
     * Test method for secure API data submission.
     * Demonstrates how to securely send sensitive data.
     */
    @Test
    public void testSecureApiDataSubmission() throws IOException {
        LOGGER.info("Starting secure API data submission test");
        
        // Get securely stored API key
        String apiKey = SensitiveDataManager.getSecureData("apiKey");
        assertNotNull("API Key not found in secure storage", apiKey);
        
        // Create secure payload with sensitive data
        JSONObject payload = new JSONObject();
        payload.put("customerId", "SECURE_CUSTOMER_ID"); // This would come from secure storage in a real app
        payload.put("timestamp", System.currentTimeMillis());
        payload.put("action", "balance_check");
        LOGGER.info("Created secure API payload");
        
        // Create HTTP client
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Example API endpoint (using a public test API)
            String apiUrl = "https://jsonplaceholder.typicode.com/posts";
            HttpPost request = new HttpPost(apiUrl);
            
            // Add authorization header with API key (never log the actual key)
            request.addHeader("Authorization", "Bearer " + apiKey);
            request.addHeader("Content-Type", "application/json");
            
            // Add payload to request
            request.setEntity(new StringEntity(payload.toString()));
            LOGGER.info("Added secure payload to API request");
            
            // Execute request
            LOGGER.info("Sending API POST request to: {}", apiUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Verify response status
                int statusCode = response.getStatusLine().getStatusCode();
                LOGGER.info("Received API response with status code: {}", statusCode);
                
                // For this test API, POST requests return 201 Created
                assertEquals("Unexpected API response status", 201, statusCode);
                
                // Process response body
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                LOGGER.info("API response body length: {} characters", responseBody.length());
                
                // Verify response contains expected data
                JSONObject jsonResponse = new JSONObject(responseBody);
                assertNotNull("Response missing 'id' field", jsonResponse.get("id"));
                LOGGER.info("API response indicates successful data submission");
            }
        }
        
        LOGGER.info("Secure API data submission test completed successfully");
    }
}