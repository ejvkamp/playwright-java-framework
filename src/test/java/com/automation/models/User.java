package com.automation.models;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    private String password;

    // Constructor
    public User(String firstName, String lastName, String email, String telephone, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.telephone = telephone;
        this.password = password;
    }

    // Getters and Setters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; } 
    public String getTelephone() { return telephone; }
    public String getPassword() { return password; }
}
