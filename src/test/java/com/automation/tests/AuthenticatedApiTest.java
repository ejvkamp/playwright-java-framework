package com.automation.tests;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class AuthenticatedApiTest {

	private Playwright playwright;
	private APIRequestContext apiContext;

	@BeforeClass
	public void setup() {
		playwright = Playwright.create();

		// 1. Define your "Secret" Token
		String apiToken = "secret-token-12345";

		/*
		 * Real-World Authentication Flow: In a real app, you wouldn't hardcode the
		 * token. You would fetch it first:
		 * 
		 * APIResponse loginResponse = apiContext.post("/auth/login",
		 * RequestOptions.create().setData(Map.of( "username", "testuser", "password",
		 * "testpass" )));
		 * 
		 * String token = JsonParser.parseString(loginResponse.text())
		 * .getAsJsonObject().get("token").getAsString();
		 */

		// 2. Create Context with Auth Headers
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		headers.put("Authorization", "Bearer " + apiToken);

		apiContext = playwright.request().newContext(new APIRequest.NewContextOptions()
				.setBaseURL("https://jsonplaceholder.typicode.com").setExtraHTTPHeaders(headers));

	} // end of setup()

	@Test(priority = 1)
	public void updatePost() {
		System.out.println("Sending PUT request to update a post...");

		// 1. Prepare New Data
		Map<String, Object> updateData = new HashMap<>();
		updateData.put("id", 1);
		updateData.put("title", "Updated Title by Playwright");
		updateData.put("body", "This post has been completely replaced.");
		updateData.put("userId", 1);

		// 2. Send PUT Request to specific resource (/posts/1)
		APIResponse response = apiContext.put("/posts/1", RequestOptions.create().setData(updateData));

		// 3. Validate Response
		// Status 200 OK is standard for updates (sometimes 204 No Content)
		Assert.assertEquals(response.status(), 200, "Expected 200 OK");

		// Verify the server echoed back our new title
		JsonObject json = JsonParser.parseString(response.text()).getAsJsonObject();
		Assert.assertEquals(json.get("title").getAsString(), "Updated Title by Playwright");

		System.out.println("Update successful: " + json.get("title").getAsString());
	}

	@Test(priority = 2)
	public void patchPostTitle() {
		System.out.println("Sending PATCH request to modify title only...");

		// 1. Prepare Partial Data
		Map<String, Object> partialData = new HashMap<>();
		partialData.put("title", "Patched Title Only");

		// 2. Send PATCH Request
		APIResponse response = apiContext.patch("/posts/1", RequestOptions.create().setData(partialData));

		Assert.assertEquals(response.status(), 200);

		JsonObject json = JsonParser.parseString(response.text()).getAsJsonObject();
		Assert.assertEquals(json.get("title").getAsString(), "Patched Title Only");

		// Verify other fields (like body) are still present
		// (simulated by Mock API)
		Assert.assertTrue(json.has("body"), "Body field should remain");

		System.out.println("Patch successful.");
	}

	@Test(priority = 3)
	public void deletePost() {
		System.out.println("Sending DELETE request...");

		// 1. Send DELETE Request
		APIResponse response = apiContext.delete("/posts/1");

		// 2. Validate Status
		// 200 OK or 204 No Content are standard success codes for Delete
		int status = response.status();
		Assert.assertTrue(status == 200 || status == 204, "Expected 200 or 204, got: " + status);

		System.out.println("Delete successful. Status: " + status);
	}

	@Test
	public void testUnauthorizedAccess() {
		// Create a separate context WITHOUT the token
		APIRequestContext unauthorizedContext = playwright.request()
				.newContext(new APIRequest.NewContextOptions().setBaseURL("https://jsonplaceholder.typicode.com"));

		// Try to delete a protected resource
		APIResponse response = unauthorizedContext.delete("/posts/1");

		// Note: JSONPlaceholder is a public mock, so it returns 200.
		// In a REAL API, this would return 401 Unauthorized.
		System.out.println("Mock API Response: " + response.status());
		// Assert.assertEquals(response.status(), 401);
		// Uncomment for real API

		unauthorizedContext.dispose();
	}

	@Test
	public void completeResourceLifecycle() {
		// 1. Create
		Map<String, Object> newPost = new HashMap<>();
		newPost.put("title", "Lifecycle Test");
		newPost.put("body", "Testing complete flow");
		newPost.put("userId", 1);

		APIResponse created = apiContext.post("/posts", RequestOptions.create().setData(newPost));
		Assert.assertEquals(created.status(), 201);

		// 2. Update (using the mock ID)
		newPost.put("title", "Updated Lifecycle Test");
		APIResponse updated = apiContext.put("/posts/1", RequestOptions.create().setData(newPost));
		Assert.assertEquals(updated.status(), 200);

		// 3. Delete
		APIResponse deleted = apiContext.delete("/posts/1");
		Assert.assertTrue(deleted.status() == 200 || deleted.status() == 204);

		System.out.println("Complete lifecycle executed successfully");
	}

	@AfterClass
	public void tearDown() {
		if (apiContext != null)
			apiContext.dispose();
		if (playwright != null)
			playwright.close();
	}

} // end of class
