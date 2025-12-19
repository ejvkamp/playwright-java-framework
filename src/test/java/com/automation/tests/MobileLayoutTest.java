package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MobileLayoutTest extends BaseTest {

	// We keep a reference to the mobile context so we can close it
	private BrowserContext mobileContext;

	@Test
	public void verifyMobileMenuOniPhone() {
		// 1. Configure the Context for iPhone 13
		// Note: Playwright Java does not have a built-in device dictionary like Node.js
		// We must manually set the Viewport, User Agent, and Screen settings.
		Browser.NewContextOptions options = new Browser.NewContextOptions().setViewportSize(390, 844) // iPhone 13 width x height
				.setDeviceScaleFactor(3.0) // Pixel density (DPR)
				.setIsMobile(true).setHasTouch(true).setUserAgent(
						"Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1");

		// 2. Launch a New Context with Device Emulation
		mobileContext = browser.newContext(options);

		// Create a page within this mobile context
		Page mobilePage = mobileContext.newPage();

		// 3. Navigate
		mobilePage.navigate("https://ecommerce-playground.lambdatest.io/");

		// Debugging: Verify we are actually emulating
		System.out.println("User Agent: " + mobilePage.evaluate("navigator.userAgent"));
		System.out.println("Viewport: " + mobilePage.viewportSize().width + "x" + mobilePage.viewportSize().height);

		// 4. Verification: Check for Mobile-Specific Elements
		// On Desktop, the "Mega Menu" is a visible horizontal bar.
		// On Mobile, it collapses into a "Hamburger" icon or specific toggle.

		// Assertion: The desktop menu should be hidden
		assertThat(mobilePage.locator("ul.navbar-nav.horizontal")).isAttached();		
		assertThat(mobilePage.locator("ul.navbar-nav.horizontal")).isHidden();

		// Assertion: The mobile toggle button should be visible
		// Used Inspect to identify the correct button locator
		assertThat(mobilePage.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Shop by Category")))
				.isVisible();

		System.out.println("Verified mobile layout for iPhone 13");
	}

	@AfterMethod
	public void cleanupMobileContext() {
		// Clean up the specific mobile context we created
		if (mobileContext != null) {
			mobileContext.close();
		}
	}

}
