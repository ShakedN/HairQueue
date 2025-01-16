package com.example.hairqueue.Data;

public class Appointment {
    private String appointmentTime;
    private String customerName;

    // Default constructor for Firebase
    public Appointment() {}

    public Appointment(String appointmentTime, String customerName) {
        this.appointmentTime = appointmentTime;
        this.customerName = customerName;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
