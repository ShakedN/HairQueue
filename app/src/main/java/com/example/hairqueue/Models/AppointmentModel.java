package com.example.hairqueue.Models;

public class AppointmentModel {
    String appointmentId;
    String email;
    String date;
    String service;
    String status;
    String startTime;
    String endTime;
    int duration;

    // No-argument constructor
    public AppointmentModel() {
    }

    // Parameterized constructor
    public AppointmentModel(String appointmentId, String email, String date, String service, String status, String startTime, String endTime, int duration) {
        this.appointmentId = appointmentId;
        this.email = email;
        this.date = date;
        this.service = service;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    // Getters and setters
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}