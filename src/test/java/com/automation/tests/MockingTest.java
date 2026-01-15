package com.automation.tests;

import com.automation.base.BaseTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Route;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MockingTest extends BaseTest {

    @Test
    public void testMockProductSearch() {
        // 1. Define the Stub Data (The "Canned" Response)
        // Through Reconnaissance (Step 0), we discovered this specific endpoint expects HTML.
        // We construct a simple HTML list item to inject into the dropdown.
        String mockResponse = 
            "<li>" +
            "  <a href='#'>" +
            "    <div class='image'>" +
            "      <img src='https://placehold.co/150' alt='Super Secret Spy Gadget' />" +
            "    </div>" +
            "    <div class='name'>Super Secret Spy Gadget</div>" +
            "    <div class='price'>$1,000,000.00</div>" +
            "  </a>" +
            "</li>";

        // 2. Set up the Interception (Stubbing)
        page.route("**/index.php?route=extension/maza/product/product/autocomplete**", route -> {
            String url = route.request().url();
            
            // Strategic Mocking: Only intercept if it matches our specific test case input.
            // We use filter_name= to ensure we don't break empty-state calls.
            // using toLowerCase() ensures we catch 'Spy', 'spy', or 'SPY'.
            if (url.contains("filter_name=") && url.toLowerCase().contains("spy")) {
                System.out.println(">>> Intercepted 'Spy' Search Request! Serving Stubbed HTML.");
                
                route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setContentType("text/html; charset=UTF-8") // Exact match to server behavior
                    .setBody(mockResponse));
            } else {
                // Let real traffic through if it doesn't match our criteria
                route.resume();
            }
        });

        // 3. Perform the UI Action with Synchronization
        page.navigate("https://ecommerce-playground.lambdatest.io/");
        
        // Use fill() instead of the deprecated type()
        // Note: We use .first() because the page has two search bars (Desktop & Mobile)
        
        // Pro Tip: We wrap the action in waitForResponse to ensure the network call happens before we assert.
        // We strictly wait for the request that contains our specific filter.
        page.waitForResponse(response -> 
            response.url().contains("route=extension/maza/product/product/autocomplete") 
            && response.url().toLowerCase().contains("filter_name=spy")
            && response.status() == 200, 
            () -> {
                page.getByPlaceholder("Search For Products").first().fill("Spy");
            }
        );

        // 4. Validate the UI displays our Stubbed Data
        // We find the specific dropdown item that matches our mock name to avoid ambiguity.
        Locator resultItem = page.locator(".dropdown-menu li")
            .filter(new Locator.FilterOptions().setHasText("Super Secret Spy Gadget"))
            .first();
            
        assertThat(resultItem).isVisible();
        
        // 5. Verify structural fields to prove the UI used our Stub correctly
        // Instead of just checking text anywhere, we verify it landed in the correct CSS classes.
        assertThat(resultItem.locator(".name")).hasText("Super Secret Spy Gadget");
        assertThat(resultItem.locator(".price")).hasText("$1,000,000.00");
        
        // 6. Validate the Image Element Exists
        // We verify the structural existence of the image tag without asserting the fragile source URL.
        assertThat(resultItem.locator("img")).isVisible();
        
        System.out.println(">>> Mock Test Passed: UI displayed fake product structure correctly.");
    }
    
    @Test
    public void testHandle500Error() {
     String checkoutUrl = "https://ecommerce-playground.lambdatest.io/index.php?route=checkout/checkout";
     // Use a wildcard (*) at the end to match regardless 
     String routePattern = "**/index.php?route=checkout/checkout*";

     // 1. Set up the Interception
     page.route(routePattern, route -> {
      System.out.println(">>> Intercepting Checkout. Simulating 500 Error.");
      
      // Return a realistic 500 response with headers
      route.fulfill(new Route.FulfillOptions()
       .setStatus(500)
       .setContentType("text/html; charset=utf-8")
       .setBody("<h1>Internal Server Error</h1>"));
     });

     // 2. Trigger the Navigation
     page.navigate(checkoutUrl);
     
     // 3. Validate the UI behavior
     // We assert that our custom error message is visible.
     assertThat(page.locator("h1")).hasText("Internal Server Error");

     // 4. Cleanup (Good Practice)
     // Remove the route handler so it doesn't affect subsequent tests 
     page.unroute(routePattern);
    }
    
    
    @Test
    public void testSpyOnNetwork() {
     // Define a pattern to match ALL requests 
     // (use specific patterns like "**/api/**" in real suites)
     String pattern = "**/*";

     // Intercept to log, then continue
     page.route(pattern, route -> {
      // Log the method and URL
      System.out.println(">> Network Request: " + route.request().method() + " " + route.request().url());
      
      // Critical: Use fallback() to tell Playwright 
      // "I'm done, handle this request normally."
      // This is safer than resume() for simple spying.
      route.fallback(); 
     });
     
     // Navigate to the homepage to generate traffic
     page.navigate("https://ecommerce-playground.lambdatest.io/");
     
     // Simple assertion to ensure page loaded
     assertThat(page).hasTitle("Your Store");

     // Cleanup: Remove the route so it doesn't slow down 
     // subsequent tests
     page.unroute(pattern);
    }



    
    
}