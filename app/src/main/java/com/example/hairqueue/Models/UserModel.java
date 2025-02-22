package com.example.hairqueue.Models;


import java.util.List;

public class UserModel {
    private String email;
    private String phone;
    private String fullName;

    private List<AppointmentModel> appointments;

    public UserModel(String email, String phone, String fullName) {
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.appointments = null;
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

    public List<AppointmentModel> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentModel> appointments) {
        this.appointments = appointments;
    }
}
