package com.learning.java;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


/**	
* Test Case ID:		TC-SHOPPING-001
* Description: 		Verifies that a guest user can search, filter, add an item to the cart, and remove it.
* Preconditions:	The practice website is available and the target product is in stock.
* User Journey:		1. Search 'iPhone'. 2. Filter 'In Stock'. 3. Add first iPhone. 4. Verify success msg. 5. View cart. 6. Remove item. 7. Verify empty msg. 8. Continue.
* Expected Result:	The item should be added successfully and then removed successfully.
* Author:			[Your Name]
* Date:				[Current Date]
*/	


public class MyFirstPlaywrightTest {

	public static void main(String[] args) {
		
		// TODO: Validate Steps output messages
				
		// ------------------- ARRANGE -------------------
		System.out.println("Step 1: Initializing browser and test data");
		// Create a Playwright instance.
		Playwright playwright = Playwright.create();
		
		// Launch a new Chromium browser instance in "headed" (visible) mode.
		Browser browser = playwright.chromium().launch(
		        new BrowserType.LaunchOptions().setHeadless(false)
		);
		
		// Create a new browser context.
		BrowserContext context = browser.newContext();

		// Create a new page (tab) within the context.
		Page page = context.newPage();

		// Define the URL of the practice website to be tested.
		String baseUrl = "https://ecommerce-playground.lambdatest.io/";

		// Define the value for our search item
		String searchItem = "iPhone";

		// ------------------- ACT (Search and Filter) -------------------
		System.out.println("Step 2: Open site and search for " + searchItem);
		page.navigate(baseUrl);
		
		Locator searchInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search For Products")); 

		searchInput.fill(searchItem);
		searchInput.press("Enter");
		
		System.out.println("Step 3: Applying the In Stock filter");
		page.locator("#mz-filter-panel-0-4").getByText("In stock").check(); // Use .check() for checkboxes
		
		// Define the locator for the text that only appears after the filter results are complete 
		Locator resultsText = page.getByText("Showing 1 to 2 of 2 (1 Pages)");

		// The assertThat function waits for that text to be visible before proceeding
		assertThat(resultsText).isVisible(
			    new LocatorAssertions.IsVisibleOptions().setTimeout(10000)
			);

		// ------------------- ACT (Add to Cart) -------------------
		System.out.println("Step 4a: Clicking on the first product link.");
		// Find the first link with the name "iPhone" and click it
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(searchItem)).first().click();

		System.out.println("Step 4b: Add item to cart");
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to Cart")).click();
		
		// ------------------- ASSERT (Verify Add Success) -------------------
		System.out.println("Step 5: Verifying the 'Add to Cart' success message");
		Locator successMessage = page.getByText("Success: You have added " + searchItem + " to your shopping cart!");
		assertThat(successMessage).isVisible();
		System.out.println("Assertion Passed: Success message is visible.");
		
		// ------------------- ACT (View Cart and Remove) -------------------
		System.out.println("Step 6a: Navigating to the shopping cart");
		page.getByText("View Cart").click();

		// Using getByTitle as identified in Phase 2
		System.out.println("Step 6b: Removing the item from the cart");
		page.getByTitle("Remove").click();

		// ------------------- ASSERT (Verify Cart Empty) -------------------
		System.out.println("Step 7: Verifying the cart is empty");
		
		Locator emptyCartMessage = page.locator("#content").getByText("Your shopping cart is empty!");
		assertThat(emptyCartMessage).isVisible();
		System.out.println("Assertion Passed: Cart is empty message is visible");

		// ------------------- ACT (Continue) -------------------
		System.out.println("Step 8: Clicking the 'Continue' button.");
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
						
		// --- Test Cleanup ---
		browser.close();
		playwright.close();
		System.out.println("Step 9: Test finished and resources cleaned up");
	}

}
