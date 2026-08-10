package com.automation.models;

public final class FormData {
	
	private final String name;
    private final String email;
    private final String password;
    private final String company;
    private final String website;
    private final String country;
    private final String city;
    private final String address1;
    private final String address2;
    private final String state;
    private final String zipCode;

    
    private FormData(Builder build) {
        this.name = build.name;
        this.email = build.email;
        this.password = build.password;
        this.company = build.company;
        this.website = build.website;
        this.country = build.country;
        this.city = build.city;
        this.address1 = build.address1;
        this.address2 = build.address2;
        this.state = build.state;
        this.zipCode = build.zipCode;
    }
    
    public static class Builder {
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

        public Builder withName(String name) { this.name = name; return this; }
        public Builder withEmail(String email) { this.email = email; return this; }
        public Builder withPassword(String password) { this.password = password; return this; }
        public Builder withCompany(String company) { this.company = company; return this; }
        public Builder withWebsite(String website) { this.website = website; return this; }
        public Builder withCountry(String country) { this.country = country; return this; }
        public Builder withCity(String city) { this.city = city; return this; }
        public Builder withAddress1(String address1) { this.address1 = address1; return this; }
        public Builder withAddress2(String address2) { this.address2 = address2; return this; }
        public Builder withState(String state) { this.state = state; return this; }
        public Builder withZipCode(String zipCode) { this.zipCode = zipCode; return this; }

        public FormData build() {
            return new FormData(this);
        }
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
