package com.automation.utils;

import com.automation.models.User;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.options.FormData;

public class APIService implements AutoCloseable {

	private static final Logger LOGGER = LoggerFactory.getLogger(APIService.class);
	private final APIRequestContext request;

	public APIService(Playwright playwright) {
		// Initialize a fresh request context using the base URL from config
		this.request = playwright.request()
				.newContext(new APIRequest.NewContextOptions().setBaseURL(ConfigReader.getProperty("baseUrl")));
	}

	// REQUIRED by 'AutoCloseable' to ensure resources are freed
	@Override
	public void close() {
		request.dispose();
	}

	// ... Methods will go here ...

	/**
	 * Creates a user by hitting the OpenCart registration endpoint directly. Target
	 * URL: index.php?route=account/register
	 */
	public void registerUser(User user) {
		LOGGER.info("API: Attempting to register user: {}", user.getEmail());

		Allure.step("API: Registering user " + user.getEmail(), () -> {
			int attempt = 1;

			// Loop up to 2 times to handle email collisions
			while (attempt <= 2) {
				// 1. Prepare Form Data
				// These keys MUST match the 'name' attributes in the HTML form
				FormData formData = FormData.create().set("firstname", user.getFirstName())
						.set("lastname", user.getLastName()).set("email", user.getEmail()) // NOTE: uses current
																							// (potentially updated)
																							// email
						.set("telephone", user.getTelephone()).set("password", user.getPassword())
						.set("confirm", user.getPassword()).set("agree", "1"); // Check the "Privacy Policy" box

				// 2. Execute POST Request
				// Disable auto-redirects
				APIResponse response = request.post("index.php?route=account/register",
						RequestOptions.create().setForm(formData).setMaxRedirects(0));

				LOGGER.info("API Response Status: {} {}", response.status(), response.statusText());

				// 3. Validation Logic
				// OpenCart redirects (302) to the success page upon valid registration.
				if (response.status() == 302) {
					String location = response.headers().get("location");
					// Robust check: Ensure we are redirecting to the success route
					if (location != null && location.contains("route=account/success")) {
						LOGGER.info("API: User registered successfully on attempt {}", attempt);
						return; // Success! Exit the step and method.
					} else {
						throw new RuntimeException("Registration redirected unexpectedly to: " + location);
					}
				}

				// If we didn't redirect, something went wrong.
				if (response.status() != 200) {
					// Include the response text so we can see WHY it failed (e.g. 400 Bad Request)
					String failureBody = response.text();
					throw new RuntimeException(
							"API Error " + response.status() + ": " + response.statusText() + "\nBody: " + failureBody);
				}

				// Status 200 means we stayed on the page (Validation Error). Read body once.
				String responseBody = response.text();

				if (responseBody.contains("E-Mail Address is already registered")) {
					if (attempt < 2) {
						String newEmail = user.getEmail().replace("@", "_" + System.currentTimeMillis() + "@");
						LOGGER.warn("Email {} exists! Updating to {} and retrying...", user.getEmail(), newEmail);

						// SIDE EFFECT: We update the User object directly so the calling test
						// knows which email to use for login.
						user.setEmail(newEmail);
						attempt++;
						continue; // Restart the loop with the new email
					} else {
						throw new RuntimeException(
								"Failed to register user after " + attempt + " attempts. Email collision.");
					}
				}

				// Hard Failure (Unknown validation error)
				throw new RuntimeException("Failed to register user via API. Response implies validation error. \nBody: " + responseBody);
			}
		});
	}

}
