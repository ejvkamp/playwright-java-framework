package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.models.FormData;
import com.automation.pages.InputFormPage;
import com.automation.utils.TestDataFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Random;

public class InputFormDynamicTest extends BaseTest {

	@Test(invocationCount = 10)
	public void verifyFormSubmissionWithDynamicData() {
		InputFormPage inputPage = new InputFormPage(page);
		inputPage.navigate();

		// 1. Get valid countries from the page to avoid guessing
		List<String> countries = inputPage.getCountryOptions();

		// 2. Pick a random country
		String randomCountry = countries.get(new Random().nextInt(countries.size()));

		// 3. Create dynamic data using that specific country
		FormData dynamicData = TestDataFactory.createValidData(randomCountry);

		// Log it so we can debug if needed
		System.out.println("Testing with: " + dynamicData + " | Random Country: " + randomCountry);

		// 4. ACT: Submit
		inputPage.submitForm(dynamicData);

		// 5. ASSERT: Verify success message
		Assert.assertTrue(inputPage.isSuccessMessageVisible(), "Success message was not visible after submission!");

		System.out.println("Form submitted successfully with dynamic data!");
	}
}