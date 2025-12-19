package com.automation.tests;

import com.automation.base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertFalse;

public class ResponsiveLayoutTest extends BaseTest {

// 1. Define Standard Breakpoints & Edge Cases
	@DataProvider(name = "screenResolutions")
	public Object[][] getScreenResolutions() {
		return new Object[][] {
				// { Width, Height, Expect Mobile Layout?, Description }

				// Mobile (Menu Hidden)
				{ 375, 667, true, "Mobile Portrait (iPhone SE)" }, { 767, 1024, true, "Boundary: Just before tablet" },

				// Tablet/Desktop (Menu Visible)
				{ 768, 1024, false, "Boundary: Exactly at tablet breakpoint" },
				{ 800, 360, false, "Mobile Landscape (Wide)" }, { 1920, 1080, false, "Desktop Standard" } };

	}

	@Test(dataProvider = "screenResolutions")
	public void verifyResponsiveLayout(int width, int height, boolean expectMobileLayout, String deviceName) {
		System.out.println("Testing Layout: " + deviceName + " [" + width + "x" + height + "]");

		// 2. Resize the existing page
		if (page != null) {
			page.setViewportSize(width, height);
		}

		// 3. Navigate
		page.navigate("https://ecommerce-playground.lambdatest.io/");

		// 4. Conditional Assertions (The "Responsive" Logic)
		// Inspect to identify these unique locators
		String desktopMenu = "ul.navbar-nav.horizontal";

		if (expectMobileLayout) {
			// ASSERT: Mobile Layout
			assertThat(page.locator(desktopMenu)).isAttached();
			assertThat(page.locator(desktopMenu)).isHidden();
		} else {
			// ASSERT: Desktop Layout
			assertThat(page.locator(desktopMenu)).isVisible();
		}

		// 5. Professional Check: Horizontal Scroll
		// A responsive page should NEVER have a horizontal scrollbar on the body.
		boolean hasHorizontalScroll = (boolean) page
				.evaluate("document.documentElement.scrollWidth > window.innerWidth");

		assertFalse(hasHorizontalScroll, "Page has horizontal scroll at " + width + "px (Responsive Failure)");

	}

	@Test
	public void discoverBreakpoints() {
		// Navigate once at the beginning
		page.navigate("https://ecommerce-playground.lambdatest.io/");

		int[] widths = { 320, 576, 768, 992, 1200, 1920 };
		System.out.println("--- Discovery Mode ---");
		for (int width : widths) {
			// Quick check without full context tear-down
			page.setViewportSize(width, 800);

			// Check visibility of the main menu
			boolean menuVisible = page.locator("ul.navbar-nav.horizontal").isVisible();
			System.out.println(width + "px: " + (menuVisible ? "Desktop" : "Mobile") + " layout");
		}

	}

	@Test
	public void testOrientationChange() {
		page.navigate("https://ecommerce-playground.lambdatest.io/");
		String desktopMenuSelector = "ul.navbar-nav.horizontal";

		// Portrait (iPad Mini width)
		page.setViewportSize(768, 1024);
		// Expect desktop menu (768px is Tablet/Desktop on this site)
		assertThat(page.locator(desktopMenuSelector)).isVisible();

		// Rotate to Landscape (1024px)
		page.setViewportSize(1024, 768);
		// Expect desktop menu
		assertThat(page.locator(desktopMenuSelector)).isVisible();

		System.out.println("Layout adapts correctly to orientation changes");
	}
}
