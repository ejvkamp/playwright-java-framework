package com.automation.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.automation.models.FormData;

public class CsvUtil {

private static final String[] EXPECTED_HEADERS = {
        "Name", "Email", "Password", "Company", "Website", "Country", "City", "Address1", "Address2", "State", "Zip"
    };

public static Object[][] getFormDataFromCsv(String filePath) {
 List<Object[]> dataList = new ArrayList<>();
 
 // Use try-with-resources to ensure the file closes automatically
 try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	// 1. Validate the header row before reading any data
	 String headerLine = br.readLine();
	if (headerLine == null) {
	  throw new IllegalArgumentException("CSV file is empty: " + filePath);
}

  String[] headers = headerLine.split(",");
  	for (int i = 0; i < headers.length; i++) {
        headers[i] = headers[i].trim();
        if (i == 0 && headers[i].startsWith("\uFEFF")) {
          headers[i] = headers[i].substring(1);
      }
  }

  if (headers.length != EXPECTED_HEADERS.length
|| !java.util.Arrays.equals(headers, EXPECTED_HEADERS)) {
        throw new IllegalArgumentException(
          "CSV header mismatch in " + filePath + "\n" + "Expected: " + java.util.Arrays.toString(EXPECTED_HEADERS) + "\n" + "Found:    " + java.util.Arrays.toString(headers)
      );
  }

  String line;
  int rowNumber = 1; // Header was row 0

  while ((line = br.readLine()) != null) {
rowNumber++;
// Split by comma
String[] data = line.split(",");
                
// 1. Validate Column Count
if (data.length < EXPECTED_HEADERS.length) {
  throw new IllegalArgumentException("Invalid CSV row " + rowNumber + " - expected " + EXPECTED_HEADERS.length + " columns but found " + data.length + "\n" + "Raw line: " + line + "\nColumns parsed: " + java.util.Arrays.toString(data)
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

// 3. Validate required fields aren't blank
     // (per this CSV's own rules, every field is required)
     for (int i = 0; i < data.length; i++) {
       if (data[i].isEmpty()) {
    throw new IllegalArgumentException("Missing required value in CSV row " + rowNumber + ", column '" + EXPECTED_HEADERS[i] + "'" + "\nRaw line: " + line);
          }
      }
                
  // Map CSV columns to our POJO fields
  // Note: This relies on strict column ordering.
  FormData formData = new FormData.Builder()
    .withName(data[0])
    .withEmail(data[1])
    .withPassword(data[2])
    .withCompany(data[3])
    .withWebsite(data[4])
    .withCountry(data[5])
    .withCity(data[6])
    .withAddress1(data[7])
    .withAddress2(data[8])
    .withState(data[9])
    .withZipCode(data[10])
    .build();

    dataList.add(new Object[] { formData });
  }

} catch (IOException e) {
    throw new RuntimeException("Failed to read CSV file: " + filePath, e);
  }

  // Convert List to 2D Array for TestNG
  return dataList.toArray(new Object[0][0]);
    }
}
