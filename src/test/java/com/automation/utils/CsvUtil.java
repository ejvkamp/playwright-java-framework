package com.automation.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.automation.models.FormData;

public class CsvUtil {

	public static Object[][] getFormDataFromCsv(String filePath) {
		List<Object[]> dataList = new ArrayList<>();

		// Use try-with-resources to ensure the file closes automatically
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			// Skip the header row
			br.readLine();

			while ((line = br.readLine()) != null) {
				// Split by comma
				String[] data = line.split(",");

				// 1. Validate Column Count
				// We expect exactly 11 columns to match our FormData fields.
				if (data.length < 11) {
				    throw new IllegalArgumentException("Invalid CSV row – expected 11 columns but found " + data.length + "\n" +
				    		 "Raw line: " + line + "\n" +
				    		 "Columns parsed: " + java.util.Arrays.toString(data)
				    		);
				  }


				// 2. Trim Whitespace & Handle BOM
				for (int i = 0; i < data.length; i++) {
					data[i] = data[i].trim();
					// Strip UTF-8 BOM if present on the first column
					if (i == 0 && data[i].startsWith("\uFEFF")) {
						data[i] = data[i].substring(1);
					}
				}

				// Map CSV columns to our POJO fields
				// Note: This relies on strict column ordering.
				FormData formData = new FormData(data[0], // Name
						data[1], // Email
						data[2], // Password
						data[3], // Company
						data[4], // Website
						data[5], // Country
						data[6], // City
						data[7], // Address 1
						data[8], // Address 2 
						data[9], // State
						data[10] // Zip
				);

				// Wrap in Object array for TestNG DataProvider
				dataList.add(new Object[] { formData });
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read CSV file: " + filePath, e);
		}

		// Convert List to 2D Array for TestNG
		return dataList.toArray(new Object[0][0]);
	}
}
