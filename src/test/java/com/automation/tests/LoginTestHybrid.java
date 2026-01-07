package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.models.User;
import com.automation.pages.HomePage;
import com.automation.pages.LoginPage;
import com.automation.utils.APIService;
import com.github.javafaker.Faker;
import io.qameta.allure.*;
import io.qameta.allure.testng.AllureTestNg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners({ AllureTestNg.class })
@Epic("User Management")
@Feature("Authentication")
public class LoginTestHybrid extends BaseTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginTestHybrid.class);

	@Test(description = "Verify user can log in with account created via API")
	@Severity(SeverityLevel.CRITICAL)
	@Story("As a user, I can log in with valid credentials")
	public void testLoginWithApiUser() {
		LOGGER.info("Starting Hybrid Login Test...");
		// ... Logic continues in Segment B ...

		// --- 1. ARRANGE (API) ---
		// Create unique test data to avoid "Email already exists" errors
		Faker faker = new Faker();
		String firstName = faker.name().firstName();
		String lastName = faker.name().lastName();
		String email = faker.internet().emailAddress();
		// Parameters: minLength(10), maxLength(20), includeUppercase, includeSpecial,
		// includeDigit
		String password = faker.internet().password(10, 20, true, true, true);

		// Data Stability: Use strictly numeric phone numbers to avoid locale formatting
		// issues.
		String phoneNumber = faker.number().digits(10);

		User testUser = new User(firstName, lastName, email, phoneNumber, password);

		// Log the identity, but NEVER the credentials.
		LOGGER.info("Generated Initial Test Data for User: {} {} ({})", firstName, lastName, email);

		// "Magic Step": Create the user instantly via API
		// Wrap in an Allure.step for reporting
		Allure.step("Arrange: Create User via API", () -> {
			try (APIService apiService = new APIService(playwright)) {
				// If this fails it should throw an exception immediately
				apiService.registerUser(testUser);
			}
		});
		
		// Logging the final user details in case email changed
		LOGGER.info("Registered User Identity for Login: {} {} ({})", testUser.getFirstName(),testUser.getLastName(), testUser.getEmail());
		
		// Report Visibility: Attach the final test data to the Allure Report Parameters section.
		Allure.parameter("Email", testUser.getEmail());
		Allure.parameter("First Name", testUser.getFirstName());
		Allure.parameter("Last Name", testUser.getLastName());

		// --- 2. ACT (UI) ---

		LOGGER.info("User created. Switching to UI for Login...");

		HomePage home = new HomePage(page);

		// Step: Open the application
		Allure.step("Act: Navigate to Home Page", () -> {
			home.navigate();
		});

		// Step: Navigate to Login (Note: navigateToLoginPage() already has an internal
		// Allure step)
		LoginPage loginPage = home.navigateToLoginPage();

		// Step: Enter credentials
		Allure.step("Act: Login via UI", () -> {
			loginPage.login(testUser.getEmail(), password);
		});

		// --- 3. ASSERT (UI) ---
		LOGGER.info("Verifying Login Success in UI...");

		// Step: Verify outcome
		Allure.step("Assert: Login succeeded", () -> {
			loginPage.verifyLoginSuccess();
		});

		LOGGER.info("Hybrid Test Completed Successfully.");

	} // end of testLoginWithApiUser

} // end of class
