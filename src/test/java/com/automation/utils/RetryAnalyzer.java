package com.automation.utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    
private int count = 0;
private static final int MAX_RETRY_COUNT = 2; // Retry up to 2 times

@Override
public boolean retry(ITestResult result) {
  if (!result.isSuccess()) {  
if (count < MAX_RETRY_COUNT) {              
  count++;                                
  // If test failed and we haven't maxed out retries increment count
System.out.println("Retrying test: " + result.getName() + " (Attempt " + count + ")");
return true;                            
  // Tell TestNG to rerun it
  }
     }
return false;                                   
// Stop retrying, mark as failed
  }
}
