package com.automation.tests;

import com.automation.base.BaseTest;
import com.automation.models.FormData;
import com.automation.pages.InputFormPage;
import com.automation.utils.CsvUtil;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class InputFormCsvTest extends BaseTest {

// The Data Provider calls our CSV Utility
	@DataProvider(name = "csvData")
	public Object[][] getCsvData() {
		// Use our new utility to load the prop list
		return CsvUtil.getFormDataFromCsv("src/test/resources/data/input_form_data.csv");
	}

	// The Test accepts a FormData object
	// (automatically populated from the CSV row)
	@Test(dataProvider = "csvData")
	public void verifyFormWithCsvData(FormData data) {
		System.out.println("Testing with CSV Data: " + data);

		// 1. Arrange
		InputFormPage inputPage = new InputFormPage(page);
		inputPage.navigate();

		// 2. Act
		// The test logic uses the clean POJO data!
		inputPage.submitForm(data);

		// 3. Assert
		Assert.assertTrue(inputPage.isSuccessMessageVisible(), "Form submission failed for user: " + data.getEmail());
	}
}
