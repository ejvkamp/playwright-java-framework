package com.automation.utils;

import net.datafaker.Faker;
import com.automation.models.FormData;
import java.util.Locale;

public class TestDataFactory {
 // We use US locale to ensure phone/zip formats match the expected format
 private static final Faker faker = new Faker(Locale.US);

 // Generates a FormData object with random details and a default country.
 public static FormData createValidData() {
  return createValidData("United States");
 }

 // Overloaded method to generate data with a specific country.
 // Useful for testing random countries from the dropdown list.
 public static FormData createValidData(String country) {
	  return new FormData.Builder()
	.withName(faker.name().fullName())
	  	// Use safeEmailAddress() to avoid domains that some apps reject
	  	.withEmail(faker.internet().safeEmailAddress())
	  	.withPassword(faker.credentials().password(8, 16, true, true, true))
	  	.withCompany(faker.company().name())
	  	.withWebsite(faker.internet().url())
	  	.withCountry(country)
	  	.withCity(faker.address().city())
	  	.withAddress1(faker.address().streetAddress())
	  	.withAddress2(faker.address().secondaryAddress())
	  	.withState(faker.address().state())
	  	.withZipCode(faker.address().zipCode())
	  	.build();      
	    }
}
