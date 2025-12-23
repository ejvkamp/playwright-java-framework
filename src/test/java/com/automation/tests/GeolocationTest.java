package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.Geolocation;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class GeolocationTest extends BaseTest {

	private BrowserContext mobileContext;

	@DataProvider(name = "locations")
	public Object[][] locations() {
		return new Object[][] { 
			{ 40.7128, -74.0060, "New York" }, 
			{ 48.8566, 2.3522, "Paris" },
			{ 35.6762, 139.6503, "Tokyo" } };
	}

	@Test(dataProvider = "locations")
	public void verifyGlobalLocations(double lat, double lng, String city) {
		System.out.println("Testing from: " + city + " [" + lat + ", " + lng + "]");

		// 1. Configure Context with Location & Permissions
		// We assume we are on a mobile device (iPhone 13 dimensions)
		mobileContext = browser.newContext(
				new Browser.NewContextOptions().setViewportSize(390, 844).setGeolocation(new Geolocation(lat, lng))
						// Auto-accept the permission prompt
						.setPermissions(Arrays.asList("geolocation")).setLocale("en-US"));

		Page geoPage = mobileContext.newPage();

		// 2. Navigate to a map demo site
		geoPage.navigate("https://the-internet.herokuapp.com/geolocation");

		// 3. Act: Trigger the location check
		geoPage.click("button:has-text('Where am I?')");

		// 4. Assert: Verify the coordinates match our injected data
		// We use String.format to ensure the decimals match the format
		assertThat(geoPage.locator("#lat-value")).containsText(String.valueOf(lat));
        assertThat(geoPage.locator("#long-value")).containsText(String.valueOf(lng));

		System.out.println("Verified location set to " + city + "!");

		mobileContext.close();
	}
}
