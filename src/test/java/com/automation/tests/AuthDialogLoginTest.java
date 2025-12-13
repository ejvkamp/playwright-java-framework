package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.HttpCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AuthDialogLoginTest extends BaseTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthDialogLoginTest.class);

	@Test
	public void handleHttpAuth() {
		LOGGER.info("Starting HTTP Basic Auth test...");

		// 1. Define credentials (admin/admin for this test site)
		// Pro Tip: In a real framework you would use ConfigReader!
		HttpCredentials credentials = new HttpCredentials("admin", "admin");

		// 2. Create a new context with these credentials injected
		// This tells Playwright: "If challenged, use this username/password"
		LOGGER.info("Creating BrowserContext with pre-emptive credentials...");
		BrowserContext authContext = browser
				.newContext(new Browser.NewContextOptions().setHttpCredentials(credentials));

		// 3. Create page and navigate
		Page authPage = authContext.newPage();

		LOGGER.info("Navigating to protected site...");
		// The dialog will NEVER appear as we provided the keys in advance!
		authPage.navigate("https://the-internet.herokuapp.com/basic_auth");

		// 4. Verify we are logged in
		assertThat(authPage.locator("p")).containsText("Congratulations!");
		LOGGER.info("Authentication successful. Validated success message.");

		// Cleanup this specific context
		authContext.close();
	}
}
