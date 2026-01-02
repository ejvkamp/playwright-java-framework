package com.learning.java;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.results.AxeResults;

/**
 * Test Case ID:    TC-PRICE-001
 * Description:     Verifies that the total price is calculated 
 * 					correctly in the shopping cart.
 * Preconditions:   The practice website is available. 
 * 					The "MacBook Pro" product must be in stock.
 * User Journey:    Navigate to Product Category, Filter by Price Slider , 
 * 					Filter “In Stock”, Click “MacBook Pro”, Update 
 * 					Qty to 5, Add to cart, View Cart, Verify Total, 
 * 					Remove Item, Continue to Hompage 
 * Expected Result: The total price in the cart must match the 
 * 					unit price multiplied by the quantity.
 * Author: 			[Your Name] 
 * Date: 			[Current Date]
**/


public class MySecondPlaywrightTestOriginal {
	
	public static void main(String[] args) {
	
	// ------------------- ARRANGE -------------------
	System.out.println("Step 1: Initializing browser and test data");
	Playwright playwright = Playwright.create();
	Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
	BrowserContext context = browser.newContext();
	
	Page page = context.newPage();
	
	String searchItem = "MacBook Pro";
	
	// --- ACT (Navigate to Category) ---
    System.out.println("Step 2: Navigating to Desktops page");
    page.navigate("https://ecommerce-playground.lambdatest.io/");
    
    // --- ACT(Execute AXE A11y Test) ---
    checkAccessibility(page);
    
    //page.pause();
    
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Mega Menu")).hover();
    
    // Had to modify locator with .setExact(true) to resolve strict mode violation error
    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Desktop").setExact(true)).click();
    
    // --- ACT (Advanced Action: Price Slider Filter) ---
    System.out.println("Step 3: Filtering price slider");
    Locator minSlider = page.locator("#mz-filter-panel-0-0 .ui-slider-handle").first();
   
    // Use the more robust keyboard-based interaction
    minSlider.focus();
    // Since the default is 98 we'll press it 902 times to move it to 1000.
    for (int i = 0; i < 902; i++) {
        minSlider.press("ArrowRight");
    }
    
    // ------------------- ASSERT (Verify Search Results Refresh) -------------------
    // Define the locator for the text that only appears after the filter results are complete 
 	Locator resultsTextSlider = page.getByText("Showing 1 to 5 of 5 (1 Pages)");

 	// The assertThat function waits for that text to be visible before proceeding
 	assertThat(resultsTextSlider).isVisible(
 		    new LocatorAssertions.IsVisibleOptions().setTimeout(10000)
 	);

  	// --- ACT (Apply In Stock Filter) ---
  	System.out.println("Step 4: Filtering by In Stock");
 	page.locator("[id^='mz-filter-panel-0-']").getByText("In stock", new Locator.GetByTextOptions().setExact(true)).click();
 	
	// ------------------- ASSERT (Verify Search Results Refresh) -------------------
    // Define the locator for the text that only appears after the filter results are complete 
 	Locator resultsTextInStock = page.getByText("Showing 1 to 2 of 2 (1 Pages)");

 	// The assertThat function waits for that text to be visible before proceeding
 	assertThat(resultsTextInStock).isVisible(
 		    new LocatorAssertions.IsVisibleOptions().setTimeout(10000)
 	);
 	
 	// --- ACT (Add to Cart) ---
 	System.out.println("Step 5: Clicking product " + searchItem);
  	page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(searchItem).setExact(true)).click();
  	
  	// Wait for navigation / load
  	page.waitForLoadState(LoadState.NETWORKIDLE); 
  	
  	System.out.println("Step 6: Increasing product quantity");
  	Locator increaseQuantityBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Increase quantity"));
  	
 	// Our plan is for 5, but the default is 1. So we click 4 times.
 	for (int i = 0; i < 4; i++) { 				
 	   increaseQuantityBtn.click(); 	   
 	}
 	
 	// ------------------- ACT (Add to Cart) -------------------
 	System.out.println("Step 7a: Adding item to the cart");
 	page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to Cart")).click();

 	// ----------------- ASSERT (Verify Add Success) -----------
 	System.out.println("Step 7b: Verifying the 'Add to Cart' success message");
 	Locator successMessage = page.getByText("Success: You have added " + searchItem + " to your shopping cart!");
 	assertThat(successMessage).isVisible();
 	System.out.println("Assertion Passed: Success message is visible.");
 	
 	// ------------------- ACT (View Cart) -------------------
 	System.out.println("Step 7c: Navigating to the shopping cart.");
 	page.getByText("View Cart").click();
 	
 	// --- ASSERT (Final Price Calculation) ---
 	System.out.println("Step 9: Verifying the total price calculation");

 	// Define base locator for the MacBook Pro row
 	// We find the 'row' that has our searchItem text in it
 	System.out.println("Step 9a: Finding Anchor Row");
 	Locator productRow = page.getByRole(AriaRole.ROW).filter(new Locator.FilterOptions().setHasText(searchItem)).last();
 	
 	// Validate the row output
 	String rowText = productRow.textContent();
 	System.out.println("Row content: " + rowText);
 	
	//  Get the raw text/values from the cells in that row
 	System.out.println("Step 9b: Extract Row Data");
 	
	// We find the 5th table cell (td) in the row, which is at index 4
	String unitPriceText = productRow.locator("td").nth(4).textContent();
	System.out.println("unitPriceText: " + unitPriceText);
	
	// We find the <input> element inside the row and get its 'value'
	int quantity = Integer.parseInt(productRow.locator("input[type='text']").inputValue());
	System.out.println("quantity: " + Integer.toString(quantity));
	
	// We find the 6th table cell (td) in the row, which is at index 5
	String totalRowPriceText = productRow.locator("td").nth(5).textContent();
	System.out.println("totalRowPriceText: " + totalRowPriceText);
	
	// Use our helper method to clean the data
	double unitPrice = parsePrice(unitPriceText);
	double actualTotal = parsePrice(totalRowPriceText);
	
	System.out.println("Step 9c: Performing Calculations");

	// Perform the core business logic calculation
	double expectedTotal = unitPrice * quantity;

	// Print all values to the console for easy debugging
	System.out.println("Unit Price Found: " + unitPriceText + 
	" (as " + unitPrice + ")");
	System.out.println("Quantity Found: " + quantity);
	System.out.println("Actual Total Found: " + totalRowPriceText + 
	" (as " + actualTotal + ")");
	System.out.println("Calculated Expected Total: " + expectedTotal);
	
	System.out.println("Step 9d: Performing Verification");
	
	// We check if the difference is greater than 1 cent (0.01) to avoid floating-point errors.
	if (Math.abs(expectedTotal - actualTotal) > 0.01) {
	    // The calculation is wrong. Print an error and fail the test.
	    System.err.println("!!! ASSERTION FAILED !!!");
	    System.err.println("Expected total: " + expectedTotal);
	    System.err.println("Actual total: " + actualTotal);
	    throw new RuntimeException("Price calculation assertion failed.");
	} else {
	    // The calculation is correct.
	    System.out.println("Assertion Passed: Total price is correct.");
	}
 	
 	// ------------------- ACT (Remove Cart Item) -------------------
 	System.out.println("Step 10a: Removing the item from the cart");
 	page.getByTitle("Remove").click();

 	// ------------------- ASSERT (Verify Cart Empty) -------------------
 	System.out.println("Step 10b: Verifying the cart is empty");
 	Locator emptyCartMessage = page.locator("#content").getByText("Your shopping cart is empty!");
 	assertThat(emptyCartMessage).isVisible();
 	System.out.println("Assertion Passed: Cart is empty message is visible.");

 	// ------------------- ACT (Continue) -------------------
 	System.out.println("Step 10c: Clicking the 'Continue' button");
 	page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();

 	// --- Closing Up ---
 	browser.close();
 	playwright.close();
 	System.out.println("Test finished and resources cleaned up.");
	}
	
	// ... (This goes AFTER the 'main' method's closing brace '}' ) ...
	/**
	  * Helper method to parse price text (e.g., "$2,000.00")
	  * into a calculable double (e.g., 2000.00).
	  * @param priceText The price string from the website.
	  * @return A double representation of the price.
	*/
	private static double parsePrice(String priceText) {
	System.out.println("Step 8: Using helper method");
	// Removes $, ,, and whitespace, then converts to a double
	return Double.parseDouble(priceText.replaceAll("[^\\d.]", ""));
	}
	
	// This method demonstrates how to perform an accessibility scan
	// In practice, you would call this during your suitability analysis
	public static void checkAccessibility(Page page) {
	    System.out.println("Running automated accessibility scan...");
	    
	    AxeBuilder axeBuilder = new AxeBuilder(page);
	    AxeResults accessibilityScanResults = axeBuilder.analyze();
	    
	    if (accessibilityScanResults.getViolations().isEmpty()) {
	        System.out.println("✓ No accessibility violations found");
	    } else {
	        System.out.println("✗ Found " + accessibilityScanResults.getViolations().size() + " violations:");
	        
	        accessibilityScanResults.getViolations().forEach(violation -> {
	            System.out.println("[" + violation.getImpact().toUpperCase() + "] " + 
	                    violation.getDescription());
	                System.out.println("  Affects: " + violation.getNodes().size() + " element(s)");
	                System.out.println("  Learn more: " + violation.getHelpUrl() + "\n");
	            });
	    }
	}
	
}