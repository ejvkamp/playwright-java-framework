package com.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

import io.qameta.allure.Allure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * -------------------------------------------------------------------
 * Class Name:        	ProductDetailsPage
 * Description:       	Represents the Product Detail Page (PDP) 
 * 						for a single item.
 * Responsibilities:
 * 1. Quantity:       	Handles increasing item quantity 
 * 						(including network waits).
 * 2. Purchasing:     	Adds items to the cart.
 * 3. Navigation:     	Validates success messages and navigates 
 * 						to the Cart.
 * Author:            	[Your Name]
 * Date:              	[Current Date]
 * -------------------------------------------------------------------
 */

public class ProductDetailsPage {
    private final Page page;
    private final Locator increaseQuantityBtn;
    private final Locator addToCartBtn;
    private final Locator viewCartLink;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductDetailsPage.class);

    public ProductDetailsPage(Page page) {
        this.page = page;
        this.increaseQuantityBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Increase quantity"));
        this.addToCartBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to Cart"));
        this.viewCartLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View Cart"));
    }

    public void increaseQuantity(int times) {
    	Allure.step("Increasing quantity " + times + " times", ()->{
    		LOGGER.info("Increasing quantity...");
            // FIX: The network wait we added in Act II to prevent the click from failing
            // We encapsulate it here so the test script doesn't need to know about it.
            page.waitForLoadState(LoadState.NETWORKIDLE);
            
            for (int i = 0; i < times; i++) {
                increaseQuantityBtn.click();
            }
    	});
    }

     	// We pass the productName here to make the validation dynamic
    	public CartPage addToCartAndNavigate(String productName) {
    	Allure.step("Adding " + productName + " to cart and viewing cart", ()->{
    		LOGGER.info("Adding to cart...");
            addToCartBtn.click();
            
            // Assert success message using the dynamic product name
            Locator successMessage = page.getByText("Success: You have added " + productName + " to your shopping cart!");
            assertThat(successMessage).isVisible();
            
            // Navigate to cart
            viewCartLink.click();
    	});
    	
        // Return the next Page Object in the chain
        return new CartPage(page);
    }
}
