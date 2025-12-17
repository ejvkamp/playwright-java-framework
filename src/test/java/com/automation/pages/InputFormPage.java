package com.automation.pages;

import com.automation.models.FormData;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import java.util.List; 

public class InputFormPage {
	private final Page page;
	
	private final Locator nameInput;
    private final Locator emailInput;
    private final Locator passwordInput;
    private final Locator companyInput;
    private final Locator websiteInput;
    private final Locator countrySelect;
    private final Locator cityInput;
    private final Locator address1Input;
    private final Locator address2Input;
    private final Locator stateInput;
    private final Locator zipInput;
    private final Locator submitButton;
    private final Locator successMessage;
    
    public InputFormPage(Page page) {
        this.page = page;
        this.nameInput = page.getByPlaceholder("Name", new Page.GetByPlaceholderOptions().setExact(true));
        this.emailInput = page.getByPlaceholder("Email", new Page.GetByPlaceholderOptions().setExact(true));
        this.passwordInput = page.getByPlaceholder("Password");
        this.companyInput = page.getByPlaceholder("Company");
        this.websiteInput = page.getByPlaceholder("Website");
        this.countrySelect = page.locator("select[name='country']");
        this.cityInput = page.getByPlaceholder("City");
        this.address1Input = page.getByPlaceholder("Address 1");
        this.address2Input = page.getByPlaceholder("Address 2");
        this.stateInput = page.getByPlaceholder("State");
        this.zipInput = page.getByPlaceholder("Zip Code");
        
        this.submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
        this.successMessage = page.locator(".success-msg");
    }
    
    public void navigate() {
    	 page.navigate("https://www.lambdatest.com/selenium-playground/input-form-demo");
    	}

    	// The Professional Method: Takes a POJO instead of a list of strings
    	public void submitForm(FormData data) {
    	 System.out.println("Submitting form for: " + data.getEmail());
    	        
    	 nameInput.fill(data.getName());
    	 emailInput.fill(data.getEmail());
    	 passwordInput.fill(data.getPassword());
    	 companyInput.fill(data.getCompany());
    	 websiteInput.fill(data.getWebsite());

    	 // Dynamically select the country from the data object
    	 // NOTE: This must match an option value or label exactly (e.g., "United States" or "US")
    	 countrySelect.selectOption(data.getCountry());
    	        
    	 cityInput.fill(data.getCity());
    	 address1Input.fill(data.getAddress1());
    	 address2Input.fill(data.getAddress2());
    	 stateInput.fill(data.getState());
    	 zipInput.fill(data.getZipCode());
    	 
    	 submitButton.click();
    	}
    	
    	// Returns all valid country options as a list of strings
    	public List<String> getCountryOptions() {
    		return countrySelect.locator("option").allInnerTexts();
    	}
    	    
    	public boolean isSuccessMessageVisible() {
    	 return successMessage.isVisible();
    	 }

}
