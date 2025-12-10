package com.automation.base;

import com.microsoft.playwright.*;
import com.automation.utils.ConfigReader; // Import our ConfigReader
import org.testng.annotations.*;

// Add slf4j logger imports
import org.slf4j.Logger; 
import org.slf4j.LoggerFactory; 

// For trace viewer
import com.microsoft.playwright.Tracing;
import org.testng.ITestResult;
import java.nio.file.Paths;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;



//Add Axe imports for Accessibility Testing (Shared capability)
import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.results.AxeResults;

//import org.testng.Assert;

/**
 * ------------------------------------------------------------------
 * Class Name:      	BaseTest
 * Description:     	The foundational class for all UI tests. 
 * 						It manages the lifecycle of the Playwright 
 *						objects (Browser, Context, Page) using 
 * 						TestNG annotations.
 * Responsibilities:
 * 1. Suite Setup:    	Initializes Playwright and the Browser 
 * 						(driven by config)
 * 2. Test Setup:     	Creates a fresh BrowserContext and Page 
 * 						for every @Test
 * 3. Teardown:       	Closes resources after tests/suite execution.
 * * Configuration:   	Reads 'browser' and 'headless' settings 
 * * 					from config.properties
 * Author:            	[Your Name]
 * Date:              	[Current Date]
 * -------------------------------------------------------------------
 */


public class BaseTest {
	// Suite-Level Objects (Static: shared once per run)
    protected static Playwright playwright;
    protected static Browser browser;

    // Test-Level Objects (Non-Static: fresh instance for every test)
    protected BrowserContext context;
    protected Page page;
    
	// ADD THIS LINE to create a logger instance for this class.
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    
    /**
     * Suite Setup
     * Runs once before any tests in this suite.
     * - Initializes the Playwright engine.
     * - Reads the 'browser' property from config.properties.
     * - Launches the appropriate browser (Chromium, Firefox, WebKit).
    */
    @BeforeSuite
    public void startPlaywrightAndBrowser() {
    
   	LOGGER.info("Setting up the Playwright and Browser for the suite...");
    
    playwright = Playwright.create();
    
    // Read configuration
    String browserName = ConfigReader.getProperty("browser");
    boolean isHeadless = Boolean.parseBoolean(ConfigReader.getProperty("headless"));

    switch (browserName.toLowerCase()) {
        case "chromium":
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(isHeadless));
    break;
        case "firefox":
    browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(isHeadless));
    break;
        case "webkit":
    browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(isHeadless));
    break;
        default:
    throw new IllegalArgumentException("Please provide a valid browser name in config.properties.");
    }
    
    // Update the browser launch log
    LOGGER.info("Launching Broswer: {} (Headless: {})", browserName, isHeadless);
    }
    
    /**
     * Test Setup
     * Runs before each @Test method.
     * Creates a new isolated BrowserContext and Page, ensuring a fresh state (incognito-like) for every test.
     */    
    @BeforeMethod
    public void createContextAndPage() {
    LOGGER.info("Setting up context for the method...");
    // We can add the viewport size here to ensure the responsive page loads correctly
    context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1920, 1080));
    // Start tracing
    LOGGER.info("Starting Trace recording...");
    context.tracing().start(new Tracing.StartOptions()
    		.setScreenshots(true)
    		.setSnapshots(true)
    		.setSources(true));
    
    page = context.newPage();
    }

    /**
     * Test Teardown
     * Runs after each @Test method.
     * Closes the BrowserContext and Page.
    */
    @AfterMethod
    public void closeContext(ITestResult result) {
     
      if (!result.isSuccess()) {
    	  // Create traces dir if needed
    	  File tracesDir = new File("traces");
    	  if (!tracesDir.exists()) {
    		  tracesDir.mkdirs();
    	  }
    	  
    	  // Generate timestamp and path
    	  String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    	  java.nio.file.Path tracePath = Paths.get("traces/" + result.getName() + "_" + timestamp + ".zip");
    	  
    	  // Stop tracing and save to file
    	  context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
    	  LOGGER.info("Trace saved to: {}", tracePath.toAbsolutePath());
      } else {
    	  // Even if it passed we need to stop tracing to free up memory
    	  context.tracing().stop();
      }
    	
      // Existing cleanup logic 
      if (context != null) {
      LOGGER.info("Closing context for the method...");
      context.close();
      }
     }
    
    /**
     * Suite Teardown
     * Runs once after all tests in the suite are complete.
     * Closes the Playwright engine and browser process to clean up resources.
    */
    @AfterSuite
    public void stopPlaywright() {
        if (playwright != null) {
        LOGGER.info("Tearing down the Playwright and Browser for the suite...");
        playwright.close();
      }
    }
    
    /**
     * Accessibility Check
     * Scans the current page for accessibility violations using Axe.
     * @param page The Playwright page to scan.
    */
	public static void checkAccessibility(Page page) {
	    //System.out.println("Running automated accessibility scan...");
		LOGGER.info("Running automated accessibility scan...");
	    
	    AxeBuilder axeBuilder = new AxeBuilder(page);
	    AxeResults accessibilityScanResults = axeBuilder.analyze();
	    
	    if (accessibilityScanResults.getViolations().isEmpty()) {
	        //System.out.println("✓ No accessibility violations found");
	        LOGGER.info("✓ No accessibility violations found");
	    } else {
	    	//System.out.println("!!! ACCESSIBILITY VIOLATIONS FOUND !!!");
	        //System.out.println("✗ Found " + accessibilityScanResults.getViolations().size() + " violations:");
	    	LOGGER.error("!!! ACCESSIBILITY VIOLATIONS FOUND !!!");
	    	LOGGER.error("Found {} violations:", accessibilityScanResults.getViolations().size());
	        
	        accessibilityScanResults.getViolations().forEach(violation -> {
	        	/*
	            System.out.println("[" + violation.getImpact().toUpperCase() + "] " + 
	                    violation.getDescription());
	                System.out.println("  Affects: " + violation.getNodes().size() + " element(s)");
	                System.out.println("  Learn more: " + violation.getHelpUrl() + "\n");
	                */
	        	LOGGER.error("[{}] {}", violation.getImpact().toUpperCase(), violation.getDescription());
                LOGGER.error("  Affects: {} element(s)", violation.getNodes().size());
                LOGGER.error("  Learn more: {}", violation.getHelpUrl());
	            });
	        /*
	     	// This connects the scan results to the TestNG reporting engine.
	        Assert.assertTrue(accessibilityScanResults.getViolations()
	       .isEmpty(),"Accessibility violations found on page: " + page.url());
	       */
	        
	        //System.out.println("!!! NOTE: Test continuing despite violations for learning purposes !!!");
	        LOGGER.info("!!! NOTE: Test continuing despite violations for learning purposes !!!");

	    }
	}

} // End of BaseTest class
