package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.HomePage;
import com.microsoft.playwright.Locator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


public class SearchTest extends BaseTest {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchTest.class);
	
	
	// 1. Define the Data Source
	@DataProvider(name = "searchData")
	public Object[][] getSearchData() {
	return new Object[][] {
	// { Search Term, Expected Result Header, Should Succeed? }
		{ "MacBook", "Search - MacBook", true },
		{ "iPhone",  "Search - iPhone",  true },
		{ "Unicorn", "Search - Unicorn", false } // Intentional Failure Scenario
	  };
	}
	
	// 2. The Test Method
	// TestNG injects the data from the provider into these arguments automatically.
	@Test(dataProvider = "searchData", description = "Verify search for multiple products")
	public void verifySearchFunctionality(String searchTerm, String expectedHeader, boolean shouldSucceed) {
	LOGGER.info("Starting search test for: {}", searchTerm);
	
	// --- ARRANGE ---
	HomePage home = new HomePage(page);
	home.navigate();

	// --- ACT ---
	// We use the new search() method we added to HomePage in Phase 2
	home.search(searchTerm);
	
	// --- ASSERT ---
	// 1. Verify the Header Text (This should always exist, even for 0 results)
	LOGGER.info("Verifying header contains: {}", expectedHeader);
	assertThat(page.locator("h1")).containsText(expectedHeader);

	// 2. Verify Product Visibility
	// We look for the product title in the results grid
	Locator productTitle = page.locator(".product-thumb h4 a").filter(new Locator.FilterOptions().setHasText(searchTerm)).first();

	if (shouldSucceed) {
	// For valid products, we expect to find them
	assertThat(productTitle).isVisible();
	LOGGER.info("Product found successfully.");
	} else {
	// For "Unicorn", this assertion will FAIL, triggering our Trace Viewer
	// This is intentional to demonstrate debugging a specific data row!
	LOGGER.info("Attempting to verify non-existent product to trigger Trace...");
	assertThat(productTitle).isVisible(); 
	}

	
	}

}
