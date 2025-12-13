package com.automation.tests;

import com.automation.base.BaseTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class DialogHandlingTest extends BaseTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(DialogHandlingTest.class);

	// Helper method to get the local file path
	private String getTestPageUrl() {
		return getClass().getClassLoader().getResource("test-dialogs.html").toString();
	}

	@Test
	public void handleAlertDialog() {
		page.navigate(getTestPageUrl()); // Always navigate first!

		// 1. Register the listener BEFORE the action
		page.onceDialog(dialog -> {
			LOGGER.info("Alert says: {}", dialog.message());
			assertEquals(dialog.message(), "Hello Playwright!");
			dialog.accept(); // Click OK
		});

		// 2. Trigger the dialog
		page.click("#alert");
	}

	@Test
	public void handleConfirmDialog() {
		page.navigate(getTestPageUrl()); // Always navigate first!

		// Scenario: User clicks Cancel
		page.onceDialog(dialog -> {
			LOGGER.info("Confirm says: {}", dialog.message());
			dialog.dismiss(); // Click Cancel
		});

		page.click("#confirm");
	}

	@Test
	public void captureDialogResult() {
		page.navigate(getTestPageUrl()); // Always navigate first!

		// We execute the JavaScript confirm() function directly in the browser
		// Playwright auto-dismisses it (returns false) unless we set a handler
		Object userChoice = page.evaluate("() => confirm('Delete?')");

		LOGGER.info("User chose: {}", userChoice); // Prints 'false' (auto-dismissed)
	}

	@Test
	public void handlePromptDialog() {
		page.navigate(getTestPageUrl()); // Always navigate first!
		page.onceDialog(dialog -> {
			LOGGER.info("Default value: {}", dialog.defaultValue());
			LOGGER.info("Type: {}", dialog.type()); // alert, confirm, prompt
			LOGGER.info("Message: {}", dialog.message());
			dialog.accept("My Secret Input"); // Type text and click OK
		});

		page.click("#prompt");
	}

	@Test
	public void handleBeforeUnload() {
		page.navigate(getTestPageUrl());

		// This listener acts as insurance.
		// In this specific test, the dialog likely won't appear (browsers suppress it),
		// but in a complex app, this code ensures the test doesn't hang.
		page.onDialog(dialog -> {
			if (dialog.type().equals("beforeunload")) {
				LOGGER.info("Handling beforeunload dialog");
				dialog.accept(); // Allow navigation
			}
		});

	}

}
