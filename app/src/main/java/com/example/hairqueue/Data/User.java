package com.example.hairqueue.Data;


public class User {
    private String email;
    private String phone;

    // Default constructor required for Firestore
    public User() {}

    // Constructor to initialize User with email and phone
    public User(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters for email and phone
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
