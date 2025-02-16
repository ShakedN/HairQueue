package com.example.hairqueue.Models;

import java.util.List;
import com.example.hairqueue.Models.AppointmentModel;
public class DateModel {
    private String date;
    List <AppointmentModel> appointments;

    public DateModel(String date, List<AppointmentModel> appointments) {
        this.date = date;
        this.appointments = appointments;

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


}
