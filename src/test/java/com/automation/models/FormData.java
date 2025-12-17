package com.automation.models;

public class FormData {
	
	private String name;
    private String email;
    private String password;
    private String company;
    private String website;
    private String country;
    private String city;
    private String address1;
    private String address2;
    private String state;
    private String zipCode;

    
    public FormData(String name, String email, String password, String company, String website, String country, String city, String address1, String address2, String state, String zipCode) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.company = company;
        this.website = website;
        this.country = country;
        this.city = city;
        this.address1 = address1;
        this.address2 = address2;
        this.state = state;
        this.zipCode = zipCode;
    }
    
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getCompany() { return company; }
    public String getWebsite() { return website; }
    public String getCountry() { return country; }
    public String getCity() { return city; }
    public String getAddress1() { return address1; }
    public String getAddress2() { return address2; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    
    @Override
    public String toString() {
        return "FormData for: " + name + " (" + email + ")";
    }
}
