package com.example.deeploft.models;

import java.io.Serializable;

public class User implements Serializable {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String bankAccountNumber;
    private String bankName;
    private String role; // "STUDENT" or "INSTRUCTOR"
    private String profileImageUrl;

    public User(String name, String email, String password, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}