package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.models.FormData;
import com.automation.pages.InputFormPage;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

public class InputFormStaticTest extends BaseTest {

@Test
public void verifyFormWithStaticData() {
 // 1. Arrange: Create the "Prop" manually (Hardcoded for now)
 // Note: "United States" must match the dropdown text exactly!
 FormData staticData = new FormData(
	"John Doe", 
	"john.doe@example.com", 
	"password123", 
	"Acme Corp", 
	"https://www.google.com", 
	"United States", 
	"New York", 
	"123 Main St", 
	"Apt 4B", 
	"NY", 
	"10001"
  );
 
 InputFormPage inputPage = new InputFormPage(page);

 // 2. Act
 inputPage.navigate();
 
 //Optional: Verify country list is populated
 // This confirms our helper method works and the dropdown is readable
 List<String> countries = inputPage.getCountryOptions();
 System.out.println("Found " + countries.size() + " countries.");
 Assert.assertTrue(countries.contains("United States"), "Country list missing 'United States'");
 
 inputPage.submitForm(staticData);
 
 // 3. Assert
 Assert.assertTrue(inputPage.isSuccessMessageVisible(), 
   "Form submission failed!");
 System.out.println("Static data test passed!");
 }
}
