package com.automation.tests;

import com.automation.base.BaseTest;
import com.google.gson.JsonObject;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class NetworkTest extends BaseTest {

	@Test
	public void verifyOfflineHandling() {
		BrowserContext networkContext = browser.newContext();
		Page networkPage = networkContext.newPage();

		// 1. Load the page first (Online)
		networkPage.navigate("https://ecommerce-playground.lambdatest.io/");

		// 2. Simulate "Offline"
		// This cuts the connection at the browser level.
		networkContext.setOffline(true);
		System.out.println("Network is now OFFLINE.");

		// 3. Interact: Try to navigate or reload
		try {
			networkPage.reload();
		} catch (Exception e) {
			// Expected behavior: Browser throws a network error
			// because we are offline
			System.out.println("Caught expected network error: " + e.getMessage());
		}

		// 4. Restore "Online"
		networkContext.setOffline(false);
		networkPage.reload();

		// 5. Verify Recovery
		assertThat(networkPage).hasTitle("Your Store");

		System.out.println("Network recovered successfully.");

		networkContext.close();
	}

	@Test
	public void verifyNetworkRecoveryIndicators() {
		BrowserContext mobileContext = browser.newContext();
		Page page = mobileContext.newPage();

		// 1. Navigate to our local PWA simulation
		// We use getClassLoader to find the file in src/test/resources
		page.navigate(getClass().getClassLoader().getResource("offline-demo.html").toString());

		// 2. Go Offline
		mobileContext.setOffline(true);
		// ASSERT: The red "Offline" banner should appear immediately
		assertThat(page.locator("#offline-indicator")).isVisible();

		// 3. Go Online
		mobileContext.setOffline(false);
		// ASSERT: The red banner hides, green "Online" banner should appear
		assertThat(page.locator("#offline-indicator")).isHidden();
		assertThat(page.locator("#online-indicator")).isVisible();

		mobileContext.close();
	}

	@Test
	public void verifySlowNetworkHandling() {
		BrowserContext context = browser.newContext();
		Page page = context.newPage();

		// 1. Create a CDP Session (Direct link to browser engine)
		com.microsoft.playwright.CDPSession cdp = context.newCDPSession(page);

		// 2. Send the command to emulate network conditions
		// We use Gson JsonObject to create the parameters parameter safely
		JsonObject params = new JsonObject();
		params.addProperty("offline", false);
		params.addProperty("downloadThroughput", 50 * 1024); // 50kb/s
		params.addProperty("uploadThroughput", 20 * 1024); // 20kb/s
		params.addProperty("latency", 2000); // 2 second latency

		cdp.send("Network.emulateNetworkConditions", params);

		System.out.println("Network throttled to Slow 3G.");

		// 3. Verify loading behavior
		long startTime = System.currentTimeMillis();
		page.navigate("https://ecommerce-playground.lambdatest.io/");
		long duration = System.currentTimeMillis() - startTime;

		System.out.println("Page load took: " + duration + "ms");

		// Simple assertion: It should take longer than a normal
		// broadband load (e.g. > 2s)
		if (duration < 2000) {
			throw new RuntimeException("Network throttling failed! Page loaded too fast.");
		}

		context.close();
	}

}
