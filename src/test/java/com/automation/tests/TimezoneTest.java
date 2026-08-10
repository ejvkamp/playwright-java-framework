package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TimezoneTest extends BaseTest {

	@Test
	public void verifyTimezoneHandling() {
		// Set timezone to Istanbul (UTC/GMT+3)
		BrowserContext tzContext = browser.newContext(new Browser.NewContextOptions().setTimezoneId("Europe/Istanbul"));

		try {
			Page tzPage = tzContext.newPage();

			// Verify the browser reports the correct timezone via JavaScript
			String timezone = (String) tzPage.evaluate("() => Intl.DateTimeFormat().resolvedOptions().timeZone");

			System.out.println("Browser thinks it is in: " + timezone);
			Assert.assertEquals(timezone, "Europe/Istanbul", "Browser timezone did not match expected value!");
		} finally {
			tzContext.close();
		}
	}
}
