package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.models.FormData;
import com.automation.pages.InputFormPage;
import com.automation.utils.TestDataFactory; // For fallback data
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HybridTest extends BaseTest {

	private APIRequestContext apiContext;

	@BeforeClass
	public void setupAPI() {
		// Initialize the API context specifically for this test
		apiContext = playwright.request()
				.newContext(new APIRequest.NewContextOptions().setBaseURL("https://jsonplaceholder.typicode.com"));
	}

	@Test
	public void registerUserFromApiData() {
		System.out.println("Step 1: Fetching User Data via API...");

		FormData userData;

		try {
			// 1. API Call: Get User ID 1
			APIResponse response = apiContext.get("/users/1");
			Assert.assertEquals(response.status(), 200);

			// 2. Parse JSON
			JsonObject json = JsonParser.parseString(response.text()).getAsJsonObject();

			/*
			 * API Response Structure -> FormData Mapping: json.name -> fullName json.email
			 * -> email json.company.name -> company json.website -> website
			 * json.address.city -> city // ... (mapping other fields) ...
			 */

			// 3. Map JSON to our POJO (The Adapter Step)
			String fullName = json.get("name").getAsString();
			String email = json.get("email").getAsString();
			String website = json.get("website").getAsString();
			String companyName = json.get("company").getAsJsonObject().get("name").getAsString();

			// Create the object (Filling in blanks with defaults as needed)
			userData = new FormData(fullName, email, "Password123!", // Default password
					companyName, website, "United States",
					json.get("address").getAsJsonObject().get("city").getAsString(),
					json.get("address").getAsJsonObject().get("street").getAsString(),
					json.get("address").getAsJsonObject().get("suite").getAsString(), "NY", // Default State
					json.get("address").getAsJsonObject().get("zipcode").getAsString());

		} catch (Exception e) {
			// Fallback: If API fails, use Faker so test can still proceed
			System.err.println("API Data Fetch Failed: " + e.getMessage());
			System.out.println("Falling back to Synthetic Data (Faker)...");
			userData = TestDataFactory.createValidData();
		}

		System.out.println("Step 2: Driving UI with Data: " + userData);

		// 4. UI Interaction (Standard Page Object usage)
		InputFormPage inputPage = new InputFormPage(page);
		inputPage.navigate();

		// The Page Object processes the POJO blindly
		inputPage.submitForm(userData);

		// 5. Verification
		Assert.assertTrue(inputPage.isSuccessMessageVisible(),
				"Form submission failed for user: " + userData.getName());

		System.out.println("Hybrid test completed successfully!");

	} // end of registerUserFromApiData

	@AfterClass
	public void tearDownAPI() {
		if (apiContext != null) {
			apiContext.dispose();
		}

	} // end of tearDownAPI

} // end of class