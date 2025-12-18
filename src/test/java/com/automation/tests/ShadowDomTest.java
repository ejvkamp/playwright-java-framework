package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ShadowDomTest extends BaseTest {

private static final String SHADOW_DOM_URL = "https://www.lambdatest.com/selenium-playground/shadow-dom";

@BeforeMethod
public void navigateToShadowDemo() {
 page.navigate(SHADOW_DOM_URL);
}

@Test
public void interactWithShadowDom() {
System.out.println("Interacting with elements inside Shadow DOM...");
        
// 1. Interact with 'Name' (Inside Shadow Root)
// NOTE: We use setExact(true) because the page contains multiple 
// similar labels(like "First Name" vs "Name") and we want to 
// avoid partial matches.
Locator nameInput = page.getByRole(AriaRole.TEXTBOX, 
  new Page.GetByRoleOptions().setName("Name").setExact(true));
        
nameInput.fill("Playwright Tester");
        
assertThat(nameInput).hasValue("Playwright Tester");

// 2. Interact with 'Email' (Inside the SAME Shadow Root)
// Playwright handles the context seamlessly
Locator emailInput = page.getByRole(AriaRole.TEXTBOX, 
 new Page.GetByRoleOptions().setName("Email").setExact(true));
            
 emailInput.fill("tester@example.com");
 
 page.pause();
        
 assertThat(emailInput).hasValue("tester@example.com");
        
 System.out.println("Successfully pierced the Shadow DOM!");
}

// Helper method to help you visualize Shadow Hosts on a page
@Test
public void debugShadowDomStructure() {
 // Log all shadow hosts on the page to the console
 Object shadowHosts = page.evaluate("() => {" + 
  "  return Array.from(document.querySelectorAll('*'))" +
  "    .filter(el => el.shadowRoot)" +
  "    .map(el => el.tagName.toLowerCase());" +
  "}");
            
 System.out.println("Shadow hosts found on page: " + shadowHosts);
 }
}