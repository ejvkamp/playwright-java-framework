package com.automation.pages;

import com.automation.models.FormData;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
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
        
        //Scope locators to use main form to avoid any strict mode errors
        Locator mainForm = page.locator("#seleniumform");
        
        this.nameInput = mainForm.getByPlaceholder("Name");
        this.emailInput = mainForm.getByPlaceholder("Email");
        this.passwordInput = mainForm.getByPlaceholder("Password");
        this.companyInput = mainForm.getByPlaceholder("Company");
        this.websiteInput = mainForm.getByPlaceholder("Website");
        this.countrySelect = mainForm.locator("select[name='country']");
        this.cityInput = mainForm.getByPlaceholder("City");
        this.address1Input = mainForm.getByPlaceholder("Address 1");
        this.address2Input = mainForm.getByPlaceholder("Address 2");
        this.stateInput = mainForm.getByPlaceholder("State");
        this.zipInput = mainForm.getByPlaceholder("Zip Code");
        
        this.submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
        this.successMessage = page.locator(".success-msg");
    }
    
    public void navigate() {
    	 page.navigate("https://www.testmuai.com/selenium-playground/input-form-demo");
    	 //Add NETWORKIDLE to resolve errors due to timing 
    	 page.waitForLoadState(LoadState.NETWORKIDLE);    	 
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
