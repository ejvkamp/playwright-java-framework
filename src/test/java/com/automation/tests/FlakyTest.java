package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.utils.RetryAnalyzer; // Import your new class
import org.testng.Assert;
import org.testng.annotations.Test;

public class FlakyTest extends BaseTest {

    private int attempt = 0;

    // We link the analyzer to this specific test method
    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void simulateFlakyNetwork() {
        attempt++;
        System.out.println("Attempt #" + attempt);

        // Simulate a failure on the first 2 attempts
        if (attempt < 3) {
            Assert.fail("Simulated network failure!");
        }

        // Pass on the 3rd attempt
        System.out.println("Network stabilized. Test Passed!");
    }
}