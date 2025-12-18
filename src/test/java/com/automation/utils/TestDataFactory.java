package com.automation.utils;

import com.github.javafaker.Faker;
import com.automation.models.FormData;
import java.util.Locale;

public class TestDataFactory {
 // One Faker instance to rule them all
 // We use US locale to ensure phone/zip formats match 
 // the expected format
 private static final Faker faker = new Faker(Locale.US);

 // Generates a FormData object with random details 
 // and a default country.
 public static FormData createValidData() {
  return createValidData("United States");
 }

 // Overloaded method to generate data with a specific country.
 // Useful for testing random countries from the dropdown list.
public static FormData createValidData(String country) {
 String name = faker.name().fullName();
        
 // Use safeEmailAddress() to avoid domains that some apps reject
 String email = faker.internet().safeEmailAddress(); 
        
 String password = faker.internet().password(8, 16, true, true);
 String company = faker.company().name();
 String website = faker.internet().url();
        
 String city = faker.address().city();
 String address1 = faker.address().streetAddress();
 String address2 = faker.address().secondaryAddress();
 String state = faker.address().state();
 String zipCode = faker.address().zipCode();

 return new FormData(name, email, password, company, website, 
              country, city, address1, address2, state, zipCode);
    }
}
