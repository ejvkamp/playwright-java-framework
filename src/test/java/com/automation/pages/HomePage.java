package com.automation.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.automation.utils.ConfigReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Allure;


/**
 * -------------------------------------------------------------------
 * Class Name:        	HomePage
 * Description:      	Represents the landing page of the e-commerce 
 *						application.
 * Responsibilities:
 * 1. Navigation:     	Handles opening the base URL.
 * 2. Menu Actions:  	Interacts with the main "Mega Menu" to navigate to categories.
 * Author:            	[Your Name]
 * Date:              	[Current Date]
 * -------------------------------------------------------------------
 */


public class HomePage {
    private final Page page;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HomePage.class);

    // 1. Define Locators
    private final Locator megaMenuBtn;
    private final Locator desktopLink;
    
    // New Locators for Search
    private final Locator searchInput;
    private final Locator searchBtn;


    // 2. Constructor
    public HomePage(Page page) {
        this.page = page;
        this.megaMenuBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Mega Menu"));
        // We use .setExact(true) to avoid the strict mode violation we identified during the scripting phase
        this.desktopLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Desktop").setExact(true));
        
        // --- NEW Initializations ---
        // "Bullseye" locator: Using the Accessible Name (aria-label) via Role
        this.searchInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search For Products"));
        
        // "Bullseye" locator: Button role + name
        this.searchBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search"));

    }

    // 3. Methods define user actions
    public void navigate() {
    	// Wrap the logic in a step description
    	// Reads the URL from the config.properties file
    	Allure.step("Navigate to the Home Page", () -> {
    		page.navigate(ConfigReader.getProperty("baseUrl"));
    	});
    }
   
    
    public ProductPage navigateToDesktopPage() {
    	// You can return values from inside steps!
    	return Allure.step("Navigate to Desktop Category via Mega Menu", () -> {
    		LOGGER.info("Navigating to Desktops page...");
            megaMenuBtn.hover();
            desktopLink.click();
            // Return the next Page Object in the chain
            return new ProductPage(page);
    	});
    }
    
    // New action method
    public void search(String text) {
    	// Dynamic description using the variable
    	Allure.step("Search for product: " + text, () -> {
    		LOGGER.info("Searching for: {}", text);
        	searchInput.fill(text);
        	searchBtn.click();
    		});
    	}
    
    public LoginPage navigateToLoginPage() {
        return Allure.step("Navigate to Login Page", () -> {
            // Hover the "My Account" dropdown
        	page.getByRole(AriaRole.BUTTON, 
              new Page.GetByRoleOptions().setName("My account")).hover(); 
                
            // Click the "Login" link (Using Bullseye Locator)
            page.getByRole(AriaRole.LINK, 
              new Page.GetByRoleOptions().setName("Login")).click();
            return new LoginPage(page);
        });
    }
}
