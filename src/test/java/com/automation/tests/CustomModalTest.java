package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page; // Ensure Page is imported if needed for options
import com.microsoft.playwright.options.AriaRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CustomModalTest extends BaseTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomModalTest.class);

	@Test
	public void handleCustomModal() {
		LOGGER.info("Starting Custom Modal test...");

		// Use the LambdaTest Selenium Playground for a stable modal example
		page.navigate("https://www.lambdatest.com/selenium-playground/bootstrap-modal-demo");

		// 1. Trigger the modal
		LOGGER.info("Triggering the 'Single Modal'...");
		// We find the specific "Launch Modal" button.
		// Using .first() because there are multiple on the page.
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Launch Modal")).first().click();

		// 2. Define the modal container (CRITICAL STEP)
		// Inspecting the page shows the modal has the ID 'myModal0'
		Locator modal = page.locator("#myModal");

		// 3. Verify it appeared
		// Playwright auto-waits, but this assertion makes the
		// test readable/robust
		assertThat(modal).isVisible();
		LOGGER.info("Modal is visible.");

		// 4. Interact with elements INSIDE the modal
		// Notice we call .locator() on 'modal', NOT 'page'.
		// This ensures we ONLY find elements inside the popup.

		// Let's verify the text inside the modal body
		String modalText = modal.locator(".modal-body").textContent();
		LOGGER.info("Modal Text: {}", modalText);

		// Click the 'Save Changes' button inside the modal
		LOGGER.info("Clicking 'Save Changes' inside the modal scope...");
		modal.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Save Changes")).click();

		// 5. Verify it closed
		assertThat(modal).isHidden();
		LOGGER.info("Modal closed successfully.");
	}
}
