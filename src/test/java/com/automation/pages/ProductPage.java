package com.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.assertions.LocatorAssertions; // For IsVisibleOptions

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * -------------------------------------------------------------------
 * Class Name:        	ProductPage
 * Description:       	Represents the Product Listing Page (PLP) where 
 * 						items are displayed.
 * Responsibilities:
 * 1. Filtering:      	Handles the Price Slider and "In Stock" 
 * 						checkbox filters.
 * 2. Selection:      	Selects specific products to view details.
 * 3. State Mgmt:     	Manages waits for filter results to update.
 * Author:            	[Your Name]
 * Date:              	[Current Date]
 * -------------------------------------------------------------------
 */


public class ProductPage {
    private final Page page;
    private final Locator minSlider;
    private final Locator inStockFilter;
    private final Locator resultsTextSlider;
    private final Locator resultsTextInStock;
    private final Locator macBookProLink;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductPage.class);

    public ProductPage(Page page) {
        this.page = page;
        // FIX: The robust technical locator we found via debugging for the slider handle
        this.minSlider = page.locator("#mz-filter-panel-0-0 .ui-slider-handle").first();
        
        // FIX: The robust technical locator for the dynamic "In Stock" filter ID
        this.inStockFilter = page.locator("[id^='mz-filter-panel-0-']").getByText("In stock").first();
        
        // Verification elements (Matching our script exactly)
        // We wait for specific text to ensure the filter has FINISHED updating.
        this.resultsTextSlider = page.getByText("Showing 1 to 5 of 5 (1 Pages)");
        this.resultsTextInStock = page.getByText("Showing 1 to 2 of 2 (1 Pages)");
        
        // Product link with exact match
        this.macBookProLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("MacBook Pro").setExact(true));
    }

    public void filterByPrice() {
        //System.out.println("Filtering price slider...");
    	LOGGER.info("Filtering price slider...");
        minSlider.focus();
        // Use the keyboard interaction loop we perfected
        for (int i = 0; i < 902; i++) {
            minSlider.press("ArrowRight");
        }
        // Assert: Wait for filter to apply using the specific text
        assertThat(resultsTextSlider).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));
    }

    public void filterByInStock() {
        //System.out.println("Filtering by 'In Stock'...");
    	LOGGER.info("Filtering by 'In Stock'...");
        // Ensure element is visible before interacting
        inStockFilter.evaluate("element => element.scrollIntoView({ behavior: 'smooth', block: 'center' })");
        page.waitForTimeout(500); 
        inStockFilter.click();
        
        // Assert: Wait for filter to apply using the specific text
        assertThat(resultsTextInStock).isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(10000));
    }

    public ProductDetailsPage selectProduct() {
        //System.out.println("Clicking product...");
    	LOGGER.info("Clicking product...");
        macBookProLink.click();
        // Return the next Page Object in the chain
        return new ProductDetailsPage(page);
    }
}
