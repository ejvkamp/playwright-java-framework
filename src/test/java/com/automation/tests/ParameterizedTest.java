package com.automation.tests;

import com.microsoft.playwright.*;
import org.testng.annotations.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class ParameterizedTest {
	
	private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    //TODO: Revert to original
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "appURL"})
    public void setUp(String browserName, String url) {System.out.println("Setting up test on browser: " + browserName);

    playwright = Playwright.create();

    // Use the 'browserName' parameter to decide which engine to launch
    switch (browserName.toLowerCase()) {
     case "firefox": 
     browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
    break;
     case "webkit": 
     browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
    break;
    default: // Default to chromium
     browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    break;
    }

     context = browser.newContext();
     page = context.newPage();
     page.navigate(url); // Use the 'url' parameter to navigate
    } //end setUp
    
    // TODO: Delete the groups param
    @Test(groups= {"smoke"})
    public void verifyPageTitle() {
        // The page is already navigated to the correct URL
        assertThat(page).hasTitle("Your Store");
        System.out.println("Page title verified successfully.");
    }

    @AfterMethod
    public void tearDown() {
    	if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
} //end class
