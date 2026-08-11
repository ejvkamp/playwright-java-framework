package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MobileLayoutTest extends BaseTest {

@Override
@BeforeMethod
public void createContextAndPage() {
Browser.NewContextOptions options = 
  new Browser.NewContextOptions() 
    .setViewportSize(430, 932) 
   	.setDeviceScaleFactor(3.0) 
    .setIsMobile(true) 
    .setHasTouch(true) 
    .setUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 18_5 like Mac OS X) AppleWebKit/605.1.15 " + 
      "(KHTML, like Gecko) Version/18.5 Mobile/15E148 Safari/604.1");
  context = browser.newContext(options);
  page = context.newPage();
}

@Test
public void verifyMobileMenuOniPhone() {
 page.navigate("https://ecommerce-playground.lambdatest.io/");

 String userAgent = (String) page.evaluate("navigator.userAgent");
 int viewportWidth = page.viewportSize().width;
 int viewportHeight = page.viewportSize().height;

 // Debugging: Verify we are actually emulating
 System.out.println("User Agent: " + page.evaluate("navigator.userAgent"));
 System.out.println("Viewport: " + page.viewportSize().width + "x" + page.viewportSize().height);

 // Fail the test if emulation didn’t apply correctly
 Assert.assertTrue(userAgent.contains("iPhone"), "Expected iPhone in User Agent, but was: " + userAgent);
 Assert.assertEquals(viewportWidth, 430, "Unexpected viewport width");
 Assert.assertEquals(viewportHeight, 932, "Unexpected viewport height");
        
//Check for mobile elements
//The desktop menu exists but should be hidden
assertThat(page.locator("ul.navbar-nav.horizontal")).isAttached();
assertThat(page.locator("ul.navbar-nav.horizontal")).isHidden();

//Assertion: The mobile toggle button should be visible
assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
.setName("Shop by Category"))) 
.isVisible();
  
System.out.println("Verified mobile layout for iPhone 14 Pro Max ");

}


}
