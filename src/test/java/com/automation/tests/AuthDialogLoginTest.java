package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.options.HttpCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AuthDialogLoginTest extends BaseTest {
    
private static final Logger LOGGER = 
LoggerFactory.getLogger(AuthDialogLoginTest.class);

@Override
@BeforeMethod
public void createContextAndPage() {
  LOGGER.info("Creating context with credentials...");
  // 1. Define credentials (admin/admin for this test site)
  // Pro Tip: In a real framework never use hardcoded values!
  HttpCredentials credentials = new HttpCredentials("admin", "admin");
  
  // 2. Create a new context and inject these credentials
  context = browser.newContext(new Browser.NewContextOptions().setHttpCredentials(credentials));

  // 3. Create page and navigate
  page = context.newPage();
}

@Test
public void handleHttpAuth() {
 LOGGER.info("Starting HTTP Basic Auth test...");
 LOGGER.info("Navigating to protected site...");
 
 // The dialog will NEVER appear as we provided the keys in advance!
 page.navigate("https://the-internet.herokuapp.com/basic_auth");
        
 // 4. Verify we are logged in
 assertThat(page.locator("p")).containsText("Congratulations!");
 LOGGER.info("Authentication successful. Validated success message.");
 }
}
