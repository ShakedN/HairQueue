package com.example.hairqueue.Models;


public class UserModel {
    private String email;
    private String phone;
    private String fullName;

    public UserModel(String email, String phone, String fullName) {
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }




}
