package com.example.hairqueue.Models;

import java.util.List;
import com.example.hairqueue.Models.AppointmentModel;
public class DateModel {


    private String date;
    private  String dateStatus;
    List <AppointmentModel> appointments;


    public DateModel(String date, String dateStatus, List<AppointmentModel> appointments) {
        this.date = date;
        this.dateStatus = dateStatus;
        this.appointments = appointments;
    }
    public DateModel(String date, String dateStatus) {
        this.date = date;
        this.dateStatus = dateStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<AppointmentModel> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<AppointmentModel> appointments) {
        this.appointments = appointments;
    }
    public String getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(String dateStatus) {
        this.dateStatus = dateStatus;
    }

}
