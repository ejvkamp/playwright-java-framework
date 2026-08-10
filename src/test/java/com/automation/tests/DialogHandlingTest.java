package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

public class DialogHandlingTest extends BaseTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(DialogHandlingTest.class);

	// Helper method to get the local file path
	private String getTestPageUrl() {
		java.net.URL resourceUrl = getClass().getClassLoader().getResource("test-dialogs.html");
		if (resourceUrl == null) {
			throw new IllegalStateException("File not found! Confirm it exists in classpath.");
		}
		return resourceUrl.toString();
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
		// Playwright auto-dismisses it unless we set a handler
		Object userChoice = page.evaluate("() => confirm('Delete?')");

		// Prints 'false' (auto-dismissed)
		LOGGER.info("User chose: {}", userChoice); 
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
	    
	    page.onDialog(dialog -> {
	        if (dialog.type().equals("beforeunload")) {
	            LOGGER.info("Handling beforeunload dialog");
	            dialog.accept(); // Allow navigation
	        } else {
	        	dialog.accept(); // Dismiss the alert we click below
	        }
	    });
	    
	 	// Need user-activation to trigger beforeunload
	    page.click("#alert");

	    // Need to add close with beforeunload options to fire
	    page.close(new Page.CloseOptions().setRunBeforeUnload(true));
	}
} 
