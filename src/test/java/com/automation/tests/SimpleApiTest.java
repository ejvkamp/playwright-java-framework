package com.automation.tests;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.microsoft.playwright.options.RequestOptions;
import java.util.HashMap;
import java.util.Map;

public class SimpleApiTest {

	private Playwright playwright;
	private APIRequestContext apiContext;

	@BeforeClass
	public void setup() {
		// 1. Initialize Playwright (No Browser needed!)
		playwright = Playwright.create();

		// 2. Create the API Context
		// This acts like a "Headless Postman" - ready to send requests
		apiContext = playwright.request()
				.newContext(new APIRequest.NewContextOptions().setBaseURL("https://jsonplaceholder.typicode.com")
						.setExtraHTTPHeaders(Map.of("Accept", "application/json")));
	}

	@Test
	public void verifyUserList() {
		System.out.println("Sending GET request to /users/1...");

		// 1. Performance Check
		long startTime = System.currentTimeMillis();

		// 2. Send GET Request
		APIResponse response = apiContext.get("/users/1");

		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Response Time: " + duration + "ms");

		// 3. Validate Status Code
		int status = response.status();
		System.out.println("Response Status: " + status);
		Assert.assertEquals(status, 200, "Expected HTTP 200 OK");

		// 4. Validate Performance (SLA)
		Assert.assertTrue(duration < 1000, "API took too long! (>1s)");

		// 5. Validate Headers (Important for Content-Type)
		String contentType = response.headers().get("content-type");
		Assert.assertTrue(contentType.contains("application/json"), "Response should be JSON, but was: " + contentType);

		// 6. Validate Body Data
		String body = response.text();

		System.out.println("JSON response is: " + body);

		/*
		 * Expected Response Structure from JSONPlaceholder: { "id": 1, "name":
		 * "Leanne Graham", "username": "Bret", "email": "Sincere@april.biz", ... }
		 */

		// Parse JSON using Gson
		JsonObject jsonResponse = JsonParser.parseString(body).getAsJsonObject();

		// Validation logic
		int id = jsonResponse.get("id").getAsInt();
		String name = jsonResponse.get("name").getAsString();

		System.out.println("User ID: " + id);
		System.out.println("Name: " + name);

		Assert.assertEquals(id, 1, "ID mismatch");
		Assert.assertEquals(name, "Leanne Graham", "Name mismatch");

		// Simple string check
		Assert.assertTrue(body.contains("Sincere@april.biz"), "Body should contain email");
	}

	@Test
	public void verifyUserNotFound() {
		System.out.println("Testing 404 scenario...");

		// Request a user ID that doesn't exist
		APIResponse response = apiContext.get("/api/users/9999");

		// Verify we get a 404 (Not Found), not a 200 or 500
		Assert.assertEquals(response.status(), 404, "Should return 404 for non-existent user");
		System.out.println("Correctly received 404 Not Found.");
	}

	/*
	 * Expected Request: { "title": "API Testing with Playwright", "body":
	 * "Learn how to create test data instantly via API", "userId": 1 } Expected
	 * Response from JSONPlaceholder: { "title": "API Testing with Playwright",
	 * "body": "Learn how to create test data instantly via API", "userId": 1, "id":
	 * 101 // Always 101 for new posts on this mock API }
	 */

	@Test
	public void createNewPost() {
		System.out.println("Creating a new post via API...");

		// 1. Build the request payload
		Map<String, Object> postData = new HashMap<>();
		postData.put("title", "API Testing with Playwright");
		postData.put("body", "Create test data instantly via API");
		postData.put("userId", 1);

		// 2. Measure performance
		long startTime = System.currentTimeMillis();

		// 3. Send POST request
		// We pass the map to .setData(), which handles the JSON conversion
		APIResponse response = apiContext.post("/posts", RequestOptions.create().setData(postData));

		long duration = System.currentTimeMillis() - startTime;
		System.out.println("Post created in: " + duration + "ms");

		// 4. Validate response
		// "201 Created" is the standard success code for POST
		Assert.assertEquals(response.status(), 201, "Should return 201 Created");
		Assert.assertTrue(duration < 1000, "POST should complete within 1s");

		// 5. Parse and verify response body
		JsonObject jsonResponse = JsonParser.parseString(response.text()).getAsJsonObject();

		// Verify all fields were accepted
		Assert.assertEquals(jsonResponse.get("title").getAsString(), "API Testing with Playwright");
		Assert.assertEquals(jsonResponse.get("userId").getAsInt(), 1);

		// Verify the server assigned an ID
		Assert.assertTrue(jsonResponse.has("id"), "Should have generated ID");
		int postId = jsonResponse.get("id").getAsInt();
		System.out.println("Created post with ID: " + postId);
	}

	@Test
	public void createPostWithMissingFields() {
		// JSONPlaceholder is very forgiving, but real APIs validate
		Map<String, Object> incompleteData = new HashMap<>();
		incompleteData.put("title", "Incomplete Post");
		// Missing body and userId

		APIResponse response = apiContext.post("/posts", RequestOptions.create().setData(incompleteData));

		// JSONPlaceholder still accepts this (returns 201)
		// Real APIs would typically return 400 Bad Request
		Assert.assertEquals(response.status(), 201, "JSONPlaceholder accepts partial data (real APIs might not)");
	}

	@Test
	public void fullCRUDFlow() {
		// 1. Create (POST)
		Map<String, Object> postData = new HashMap<>();
		postData.put("title", "CRUD Test");
		postData.put("body", "Testing full CRUD");
		postData.put("userId", 1);

		APIResponse createResponse = apiContext.post("/posts", RequestOptions.create().setData(postData));
		Assert.assertEquals(createResponse.status(), 201);

		// 2. Read (GET)
		// Note: Since JSONPlaceholder doesn't persist data,
		// we can't fetch the *new* ID (101).
		// In a real app, you would use the ID from the POST response:
		// "/posts/" + newId
		APIResponse getResponse = apiContext.get("/posts/1");
		Assert.assertEquals(getResponse.status(), 200);

		// 3. Update (PUT) - Coming in Phase 3
		// 4. Delete (DELETE) - Coming in Phase 4
	}

	@AfterClass
	public void tearDown() {
		if (apiContext != null) {
			apiContext.dispose();
		}
		if (playwright != null) {
			playwright.close();
		}
	}

} // end of class