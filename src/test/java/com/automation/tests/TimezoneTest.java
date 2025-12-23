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
		// 1. Set timezone to Tokyo (UTC+9)
		BrowserContext tzContext = browser.newContext(new Browser.NewContextOptions().setTimezoneId("Asia/Tokyo"));

		Page tzPage = tzContext.newPage();

		// 2. We’ll query the browser engine directly
		// 3. Assert the browser reports the correct timezone via JavaScript
		String timezone = (String) tzPage.evaluate("() => Intl.DateTimeFormat().resolvedOptions().timeZone");

		System.out.println("Browser thinks it is in: " + timezone);
		Assert.assertEquals(timezone, "Asia/Tokyo", "Browser timezone did not match expected value!");

		tzContext.close();
	}
}
