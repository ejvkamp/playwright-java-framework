package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.pages.*;
import org.testng.annotations.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Test Case ID:   	TC-PRICE-001
 * Title:			Verify Cart Total Calculation
 * Description:    	Verifies that the total price is calculated 
 * 			    	correctly in the shopping cart.
 * Sub-System:		Shopping Cart
 * Priority:		P1 (Critical)
 * Traceability:	[Link to Ticket or REQ ID]
 * Preconditions:   The practice website is available. 
 * 			    	The "MacBook Pro" product must be in stock.
 * User Journey:    Navigate to Product Category, 
 *                  Filter by Price   Slider , 
 * 			    	Filter “In Stock”, Click “MacBook Pro”, 
 *                  Update Qty to 5, Add to cart, View Cart, 
 *                  Verify Total, Remove Item, Continue to Homepage 
 * Expected Result: The total price in the cart must match the 
 * 			    	unit price multiplied by the quantity.
 * Author: 	    	[Your Name] 
 * Date: 		    [Current Date]
**/


// Triggering CI build


public class MySecondPlaywrightTestPOM extends BaseTest {
	
  // ADD THIS LINE to create a logger instance for this test class
  private static final Logger LOGGER = LoggerFactory.getLogger(MySecondPlaywrightTestPOM.class);


  @Test(description = "Verify Cart Total Calculation for Multi-Quantity Item")
  public void advancedPriceCalculationTest() {
  String productName = "MacBook Pro";
  int quantityToClick = 4; // 1 default + 4 clicks = 5 total

  // --- ARRANGE ---
  //We initialize our "Actors" (Page Objects) and give them the "Stage" (page)
  HomePage home = new HomePage(page);
  ProductPage productPage = new ProductPage(page);
  ProductDetailsPage detailsPage = new ProductDetailsPage(page);
  CartPage cart = new CartPage(page);

  // --- ACT (The Test Flow - Pure Logic) ---
  
  // 1. Navigation
  // The HomePage gets the URL from config and opens the menu
  home.navigate();
  
  // 2. Accessibility Scan (Inherited from BaseTest)
  // Professional QA Practice: Check for violations immediately 
  checkAccessibility(page);

  // 3. Continue Navigation
  home.navigateToDesktopPage();

  // 4. Filtering
  // The ProductPage handles all filter logic
  productPage.filterByPrice();
  productPage.filterByInStock();
  
  // 5. Selection
  productPage.selectProduct();
  
  // 6. Interaction
  // Increase product quantity and add to cart
  detailsPage.increaseQuantity(quantityToClick);
  detailsPage.addToCartAndNavigate(productName);

  // --- ASSERT (Business Logic) ---
  // 7. Verify Price Calculation
  // Notice how clean this is: all the parsing and math is hidden in the Page Object
  cart.verifyPriceCalculation(productName);
  
  // --- CLEANUP (Final Assert/Act) ---
  // 8. Remove Item and Return Home
  cart.cleanupAndReturn(productName);
  
  //System.out.println("Test execution successful!");
  LOGGER.info("Test execution successful!");
    }
}
