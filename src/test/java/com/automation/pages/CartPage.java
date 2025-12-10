package com.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.lang.Math;

public class CartPage {
  private final Page page;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(CartPage.class);

  public CartPage(Page page) {
    this.page = page;
  }

  // 1. Helper Method (Private)
  private double parsePrice(String priceText) {
    return Double.parseDouble(priceText.replaceAll("[^\\d.]", ""));
  }

  // 2. Locator Helper (Private)
  // Finds the specific row for the product we are testing
  private Locator getProductRow(String productName) {
    return page.getByRole(AriaRole.ROW).filter(new Locator.FilterOptions().setHasText(productName)).last();
  }

  // 3. Public Methods to Get Data
  public double getUnitPrice(String productName) {
    String text = getProductRow(productName).locator("td").nth(4).textContent();
    return parsePrice(text);
  }

  public int getQuantity(String productName) {
    String text = getProductRow(productName).locator("input[type='text']").inputValue();
    return Integer.parseInt(text);
  }

  public double getActualTotal(String productName) {
    String text = getProductRow(productName).locator("td").nth(5).textContent();
    return parsePrice(text);
  }

  // 4. Business Logic Verification
  public void verifyPriceCalculation(String productName) {
    // System.out.println("Verifying price calculation for " + productName + "...");
    LOGGER.info("Verifying price calculation for {} ...", productName);
    
    double unitPrice = getUnitPrice(productName);
    int quantity = getQuantity(productName);
    double actualTotal = getActualTotal(productName);
    double expectedTotal = unitPrice * quantity;
    
    //System.out.println("Unit: " + unitPrice + " | Qty: " + quantity + " | Total: " + actualTotal);
    LOGGER.info("Unit: {} | Qty: {} | Total: {}", unitPrice, quantity, actualTotal);
    
    // Assertion with delta for floating point precision
    if (Math.abs(expectedTotal - actualTotal) > 0.01) {
      // Log the error first
      LOGGER.error("Price calculation failed! Expected: {} Actual: {}", expectedTotal, actualTotal);
      
      // Then throw the error and fail the test
      throw new RuntimeException("Price calculation failed! Expected: " + expectedTotal + " Actual: " + actualTotal);
    }
    //System.out.println("Price calculation verified.");
    LOGGER.info("Price calculation verified.");
  }

  // 5. Cleanup Action
  public void cleanupAndReturn(String productName) {
    //System.out.println("Cleaning up cart...");
	  LOGGER.info("Cleaning up cart...");
    
    // Click Remove on the specific product row
    // FIX: Use Locator.GetByRoleOptions because we are calling it on a Locator (productRow), not a Page.
    page.getByTitle("Remove").click();
        
    // Verify cart is empty
    assertThat(page.locator("#content").getByText("Your shopping cart is empty!")).isVisible();
    
    // Click Continue to go home
    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Continue")).click();
    
    // Verify we are back home
    assertThat(page).hasTitle("Your Store");
    //System.out.println("Cleanup complete. Test finished.");
    LOGGER.info("Cleanup complete. Test finished.");
  }
}
